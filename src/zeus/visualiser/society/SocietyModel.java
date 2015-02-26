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
*****************************************************************************/

package zeus.visualiser.society;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.Relationship;
import zeus.gui.*;
import zeus.gui.graph.*;
import zeus.generator.agent.AcquaintanceModel;

public class SocietyModel extends AbstractGraphModel {
  static final int SUPERIORS    = 0;
  static final int SUBORDINATES = 1;
  static final int PEERS        = 2;
  static final int COWORKERS    = 3;

  protected boolean           isNodeEditable = true;
  protected Hashtable         nodeTable      = new Hashtable();
  protected Properties        imageTable     = new Properties();
  protected EventListenerList listeners      = new EventListenerList();
  protected int               view           = 0;
  protected BitSet            links          =
     new BitSet(AcquaintanceModel.RELATIONS_LIST.size());

  public SocietyModel() {
     try {
        imageTable.load(
           new FileInputStream(SystemProps.getProperty("application.gif"))
        );
     }
     catch(Exception e) {
     }
     reset();
  }

  public void reset() {
     nodeTable.clear();
     for(int i = 0; i < links.size(); i++ )
        links.set(i);
     fireGraphStructureChanged();
  }

  public Enumeration nodes() {
     return nodeTable.elements();
  }

  public void setValue(GraphNode node, Object user_object) {
     SocietyModelEntry agent = (SocietyModelEntry)node.getUserObject();
     if ( user_object == null )
        agent.icon = null;
     else
        agent.setIcon((String)user_object);
     fireGraphNodeStateChanged(node);
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
     SocietyModelEntry agent = (SocietyModelEntry)node.getUserObject();
     String base = agent.getName();
     Relationship[] others = agent.getRelations();
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
           agent = (SocietyModelEntry)node1.getUserObject();
           others = agent.getRelations();
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
     SocietyModelEntry agent1 = (SocietyModelEntry)node1.getUserObject();
     SocietyModelEntry agent2 = (SocietyModelEntry)node2.getUserObject();
     String agentId = agent2.getName();
     Relationship[] others = agent1.getRelations();
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
     SocietyModelEntry agent;
     String agentId;
     for(int i = 0; i < input.length; i++ ) {
        agent = (SocietyModelEntry)input[i].getUserObject();
        agentId = agent.getName();
        nodeTable.remove(agentId);
        fireGraphNodeRemoved(input[i]);
     }
     fireChanged();
  }

    // 1.3 try all case variations to see if we can get a result. 
   public GraphNode getNode(String name)  {
     GraphNode result = (GraphNode)nodeTable.get(name);
     if (result == null) {
        result = (GraphNode) nodeTable.get(name.toLowerCase()); 
     }
     if (result == null) { 
        result = (GraphNode) nodeTable.get(name.toUpperCase()); 
     }
     
        return result;
     }


  public GraphNode addAgent(String name, String type)  {
     SocietyModelEntry agent;
     GraphNode node;

     String icon = imageTable.getProperty(name);
     if ( icon == null ) {
        icon = SystemProps.getProperty("gif.dir");
        icon += File.separator + "visualiser" + File.separator +
                type.toLowerCase() + ".gif";
     }
     else {
        char sys_char = File.separatorChar;
        char zeus_char = SystemProps.getProperty("file.separator").charAt(0);
        icon = icon.replace(zeus_char,sys_char);
     }

     if ( nodeTable.containsKey(name) )  {
        node = (GraphNode)nodeTable.get(name);
        agent = (SocietyModelEntry)node.getUserObject();
        if ( agent.getIcon().equals(icon) ) {
           agent.setIcon(icon);
           fireGraphNodeStateChanged(node);
        }
     }
     else {
        agent = new SocietyModelEntry(name,icon);
        node = new GraphNode(agent);
        nodeTable.put(name.toLowerCase(),node); // 1.3 si, tlc ()
        fireGraphNodeAdded(node);
     }
     return node;
  }

  public void addAgents(Vector input) {
     String type = SystemProps.getProperty("agent.names.agent");
     for(int i = 0; i < input.size(); i++ ) 
        addAgent((String)input.elementAt(i),type);
  }

  public GraphNode addAgent(String name) {
     String type = SystemProps.getProperty("agent.names.agent");
     return addAgent(name,type);
  }

  public void addRelations(String name, Vector List) {
     GraphNode node1 = (GraphNode)nodeTable.get(name);
     if ( node1 == null ) node1 = addAgent(name);

     SocietyModelEntry agent = (SocietyModelEntry)node1.getUserObject();
     agent.addRelations(List);

     GraphNode node2;
     Relationship relation;
     for(int j = 0; j < List.size(); j++ ) {
        relation = (Relationship)List.elementAt(j);
        node2 = (GraphNode)nodeTable.get(relation.getName());
        if ( node2 != null ) {
           int position = Misc.whichPosition(relation.getRelation(),
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
