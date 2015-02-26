/*
* The contents of this file are subject to the BT "ZEUS" Open Source 
* Licence (L77741), Version 1.0 (the "Licence"); you may not use this file 
* except in compliance with the Licence. You may obtain a copy of the Licence
* from $ZEUS_INSTALL/licence.html or alternatively from
* http://www.labs.bt.com/projects/agents/zeus/licence.htm
* 
* Except as stated in Clause 7 of the Licence, software distributed under the
* Licence is distributed WITHOUT WARRANTY OF ANY KIND, either express or 
* implied. See the Licence for the specific language governing rights and 
* limitations under the Licence.
* 
* The Original Code is within the package zeus.*.
* The Initial Developer of the Original Code is British Telecommunications
* public limited company, whose registered office is at 81 Newgate Street, 
* London, EC1A 7AJ, England. Portions created by British Telecommunications 
* public limited company are Copyright 1996-9. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



/*****************************************************************************
* SocietyModel.java
*
* The underlying model for the Summary Task Graph
*****************************************************************************/

package zeus.generator;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.graph.*;
import zeus.generator.event.*;
import zeus.generator.agent.AcquaintanceModel;

public class SocietyModel extends AbstractGraphModel
                          implements AgentListener {

  static final int SUPERIORS    = 0;
  static final int SUBORDINATES = 1;
  static final int PEERS        = 2;
  static final int COWORKERS    = 3;

  protected boolean           isNodeEditable  = false;

  protected GeneratorModel    genmodel   = null;
  protected Hashtable         nodeTable  = new Hashtable();
  protected EventListenerList listeners  = new EventListenerList();
  protected int               view       = 0;
  protected BitSet            links      =
     new BitSet(AcquaintanceModel.RELATIONS_LIST.size());

  public SocietyModel(GeneratorModel genmodel, boolean isNodeEditable) {
     this(genmodel);
     this.isNodeEditable = isNodeEditable;
  }
  public SocietyModel(GeneratorModel genmodel) {
     this.genmodel = genmodel;
     genmodel.addAgentListener(this);
     reset();
  }

  public void reset() {
     nodeTable.clear();
     for(int i = 0; i < links.size(); i++ )
        links.set(i);

     Acquaintance[] others;
     AgentDescription[] agents = genmodel.getAgents();
     for(int i = 0; i < agents.length; i++ )
        nodeTable.put(agents[i].getName(),new GraphNode(agents[i]));

     GraphNode node1, node2;
     String relation;
     for(int i = 0; i < agents.length; i++ ) {
        // create parent-child relations between nodes
        node1 = (GraphNode)nodeTable.get(agents[i].getName());
        others = agents[i].getAcquaintances();
        for(int j = 0; j < others.length; j++ ) {
           node2 = (GraphNode)nodeTable.get(others[j].getName());
           relation = others[j].getRelation();
           switch(Misc.whichPosition(relation,
              AcquaintanceModel.RELATIONS_LIST)) {
              case SUPERIORS:
                   node2.addChild(node1);
                   node1.addParent(node2);
                   break;
              case SUBORDINATES:
                   node1.addChild(node2);
                   node2.addParent(node1);
                   break;
              default:
                   node1.addSibling(node2);
                   node2.addSibling(node1);
                   break;
           }
        }
     }
     fireGraphStructureChanged();
  }

  public Enumeration nodes() {
     return nodeTable.elements();
  }

  public void setValue(GraphNode node, Object user_object) {
     AgentDescription agent = (AgentDescription)node.getUserObject();
     String agentId = agent.getName();
     genmodel.setAgentIcon(agentId,(String)user_object);
     fireChanged();
  }

  public boolean isNodeEditable(GraphNode node) {
     return isNodeEditable;
  }

  public Color getLinkColor(GraphNode from, GraphNode to) {
     int type;
     if ( (type = getLinkType(from,to)) != -1 )
        return getColor(type);
     return Color.black;
  }
  public boolean isLinkVisible(GraphNode from, GraphNode to) {
     int type;
     if ( (type = getLinkType(from,to)) != -1 )
        return links.get(type);
     return false;
  }
  public Vector getViewRelations(GraphNode node) {
     Vector output = new Vector();
     // first compute from me to others
     AgentDescription agent = (AgentDescription)node.getUserObject();
     String base = agent.getName();
     Acquaintance[] others = agent.getAcquaintances();
     for(int j = 0; j < others.length; j++ ) {
        if ( Misc.whichPosition(others[j].getRelation(),
                AcquaintanceModel.RELATIONS_LIST) == view )
           output.addElement(nodeTable.get(others[j].getName()));
     }

     // next compute from others to me
     Enumeration enum = nodeTable.elements();
     GraphNode node1;
     while( enum.hasMoreElements() ) {
        node1 = (GraphNode)enum.nextElement();
        if ( node1 != node && !output.contains(node1) ) {
           agent = (AgentDescription)node1.getUserObject();
           others = agent.getAcquaintances();
           for(int j = 0; j < others.length; j++ ) {
              if ( others[j].getName().equals(base) &&
	           Misc.whichPosition(others[j].getRelation(),
                      AcquaintanceModel.RELATIONS_LIST) == view )
              output.addElement(node1);
           }
        }
     }

     return output;
  }

  protected int getLinkType(GraphNode node1, GraphNode node2) {
     AgentDescription agent1 = (AgentDescription)node1.getUserObject();
     AgentDescription agent2 = (AgentDescription)node2.getUserObject();
     String agentId = agent2.getName();
     Acquaintance[] others = agent1.getAcquaintances();
     for(int j = 0; j < others.length; j++ ) {
        if ( others[j].getName().equals(agentId) ) {
           return Misc.whichPosition(others[j].getRelation(),
                                     AcquaintanceModel.RELATIONS_LIST);
        }
     }
     return -1;
  }

  public Color   getColor(int type)      { return ColorManager.getColor(type); }
  public boolean isLinkVisible(int type) { return links.get(type); }
  public void    setView(int type)       { this.view = type; }
  public int     getView()               { return view; }

  public void showLinks(int type, boolean state) {
     if ( state )
        links.set(type);
     else
        links.clear(type);
  }

  public void removeNodes(GraphNode[] input) {
     AgentDescription agent;
     String agentId;
     for(int i = 0; i < input.length; i++ ) {
        agent = (AgentDescription)input[i].getUserObject();
        agentId = agent.getName();
        genmodel.removeAgent(agentId);
        // fireGraphNodeRemoved(input[i]);
     }
     fireChanged();
  }

  public void addNewNode()  {
     genmodel.createNewAgent();
  }

  public void addNodes(GraphNode[] input) {
     if ( input == null || input.length == 0 ) return;

     AgentDescription agent;
     String agentId;
     for(int i = 0; i < input.length; i++ ) {
        agent = (AgentDescription)input[i].getUserObject();
        agentId = agent.getName();
        if ( genmodel.containsAgent(agentId) )
           genmodel.cloneAgent(agentId);
        else
           genmodel.addAgent(agent);
     }
     fireChanged();
  };

  public void agentStateChanged(AgentChangeEvent evt) {
     AgentDescription agent = evt.getAgent();
     String agentId = agent.getName();
     Acquaintance[] others;
     String relation;

     GraphNode node1, node2;
     switch(evt.getEventType()) {
        case AgentChangeEvent.ADD:
             node1 = new GraphNode(agent);
             nodeTable.put(agentId,node1);
             others = agent.getAcquaintances();
             for(int j = 0; j < others.length; j++ ) {
                node2 = (GraphNode)nodeTable.get(others[j].getName());
                if ( node2 != null ) {
		   relation = others[j].getRelation();
                   int position = Misc.whichPosition(relation,
                      AcquaintanceModel.RELATIONS_LIST);
                   switch( position ) {
                      case SUPERIORS:
                           node2.addChild(node1);
                           node1.addParent(node2);
                           fireGraphNodeStateChanged(node2);
                           break;
                      case SUBORDINATES:
                           node1.addChild(node2);
                           node2.addParent(node1);
                           fireGraphNodeStateChanged(node2);
                           break;
                      default:
                           node1.addSibling(node2);
                           node2.addSibling(node1);
                           fireGraphNodeStateChanged(node2);
                           break;
                   }
                }
             }
             fireGraphNodeAdded(node1);
             break;

        case AgentChangeEvent.DELETE:
             node1 = (GraphNode)nodeTable.remove(agentId);
             Enumeration enum = nodeTable.elements();
             while( enum.hasMoreElements() ) {
                node2 = (GraphNode)enum.nextElement();
                if ( node2.hasChild(node1) )  {
                   node2.removeChild(node1);
                   fireGraphNodeStateChanged(node2);
                }
                else if ( node2.hasParent(node1) )  {
                   node2.removeParent(node1);
                   fireGraphNodeStateChanged(node2);
                }
                else if ( node2.hasSibling(node1) )  {
                   node2.removeSibling(node1);
                   fireGraphNodeStateChanged(node2);
                }
             }
             fireGraphNodeRemoved(node1);
             break;

        case AgentChangeEvent.MODIFY:
             node1 = (GraphNode)nodeTable.get(agentId);
             // reset all of node1's internal state
             node1.initialize();
             node1.setUserObject(agent);
             others = agent.getAcquaintances();
             for(int j = 0; j < others.length; j++ ) {
                node2 = (GraphNode)nodeTable.get(others[j].getName());
                if ( node2 != null ) {
                   relation = others[j].getRelation();
                   int position = Misc.whichPosition(relation,
                      AcquaintanceModel.RELATIONS_LIST);
                   switch( position ) {
                      case SUPERIORS:
                           node2.addChild(node1);
                           node1.addParent(node2);
                           fireGraphNodeStateChanged(node2);
                           break;
                      case SUBORDINATES:
                           node1.addChild(node2);
                           node2.addParent(node1);
                           fireGraphNodeStateChanged(node2);
                           break;
                      default:
                           node1.addSibling(node2);
                           node2.addSibling(node1);
                           fireGraphNodeStateChanged(node2);
                           break;
                   }
                }
             }
             fireGraphNodeStateChanged(node1);
             break;
     }
  }

  public void addChangeListener(ChangeListener x) {
     listeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     listeners.remove(ChangeListener.class, x);
  }

  protected void fireChanged() {
     ChangeEvent c = new ChangeEvent(this);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == ChangeListener.class) {
           ChangeListener cl = (ChangeListener)list[i+1];
           cl.stateChanged(c);
        }
     }
  }
}
