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
* SummaryTaskModel.java
*
* The underlying model for the Summary Task Graph
*****************************************************************************/

package zeus.generator.task;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.graph.*;
import zeus.generator.event.*;

public class SummaryTaskModel extends AbstractGraphModel
                       implements ChangeListener {

  static final int PRECONDITION  = 0;
  static final int POSTCONDITION = 1;

  static final int BASIC_NODE = 0;
  static final int GUARD_NODE = 1;

  protected static final boolean ERROR = true;
  protected static final boolean NO_ERROR = false;

  protected static int count  = 0;

  protected OntologyDb        ontologyDb = null;
  protected Hashtable         nodeTable  = new Hashtable();
  protected Hashtable         linkTable  = new Hashtable();
  protected EventListenerList listeners  = new EventListenerList();

  public SummaryTaskModel(OntologyDb ontologyDb,
                          TaskNode[] node, TaskLink[] link) {
     this.ontologyDb = ontologyDb;
     ontologyDb.addChangeListener(this);
     reset(node,link);
  }

  public void reset(TaskNode[] node, TaskLink[] link) {
     nodeTable.clear();
     linkTable.clear();
     for(int i = 0; i < node.length; i++ )
        nodeTable.put(node[i].getName(),new GraphNode(node[i]));

     GraphNode node1, node2;
     for(int i = 0; i < link.length; i++ ) {
        linkTable.put(link[i].getId(),link[i]);
        // create parent-child relations between nodes
        node1 = (GraphNode)nodeTable.get(link[i].getLeftNode());
        node2 = (GraphNode)nodeTable.get(link[i].getRightNode());
        node1.addParent(node2);
        node2.addChild(node1);
     }
     fireGraphStructureChanged();
  }

  Fact[] getConditions(int type) {
     Fact[] out;
     GraphNode gnode;
     TaskNode node;

     switch(type) {
        case PRECONDITION:
             gnode = (GraphNode)nodeTable.get(TaskNode.BEGIN);
             node = (TaskNode)gnode.getUserObject();
             return node.getPostconditions();

        case POSTCONDITION:
             gnode = (GraphNode)nodeTable.get(TaskNode.END);
             node = (TaskNode)gnode.getUserObject();
             return node.getPreconditions();
     }
     Core.ERROR(null,1,this); // should never get here
     return null;
  }

  public TaskNode[] getNodes() {
     TaskNode[] output = new TaskNode[nodeTable.size()];
     Enumeration enum = nodeTable.elements();
     GraphNode node;
     for(int i = 0; enum.hasMoreElements(); i++ ) {
        node = (GraphNode)enum.nextElement();
        output[i] = (TaskNode)node.getUserObject();
     }
     return output;
  }
  public TaskLink[] getLinks() {
     TaskLink[] output = new TaskLink[linkTable.size()];
     Enumeration enum = linkTable.elements();
     for(int i = 0; enum.hasMoreElements(); i++ )
        output[i] = (TaskLink)enum.nextElement();
     return output;
  }

  public boolean isLinkVisible(GraphNode from, GraphNode to) {
     TaskNode node1 = (TaskNode)from.getUserObject();
     TaskNode node2 = (TaskNode)to.getUserObject();
     String name1 = node1.getName();
     String name2 = node2.getName();
     TaskLink link;
     Enumeration enum = linkTable.elements();
     while( enum.hasMoreElements() ) {
        link = (TaskLink)enum.nextElement();
        if ( link.getLeftNode().equals(name1) &&
             link.getRightNode().equals(name2) )
           return true;
     }
     return false;
  }


  public Enumeration nodes() {
     return nodeTable.elements();
  }
  public void setValue(GraphNode gnode, Object user_object) {
     Vector input = (Vector)user_object;
     TaskNode input_node = (TaskNode)input.elementAt(0);
     TaskLink[] input_links = (TaskLink[])input.elementAt(1);
     Hashtable nameTable = (Hashtable)input.elementAt(2);

     TaskNode node0 = (TaskNode)gnode.getUserObject();
     String name0 = node0.getName();
     nodeTable.remove(name0);

     gnode.setUserObject(input_node);
     nodeTable.put(input_node.getName(),gnode);

     // Remove all previous links relating to the original node (name0)
     Enumeration enum = linkTable.keys();
     String linkId;
     TaskLink link;
     while( enum.hasMoreElements() ) {
        linkId = (String)enum.nextElement();
        link = (TaskLink)linkTable.get(linkId);
        if ( link.referencesNode(name0) )
           linkTable.remove(linkId);
     }

     // initialise all graph  nodes
     enum = nodeTable.elements();
     while( enum.hasMoreElements() )
        ((GraphNode)enum.nextElement()).initialize();

     // add new links to linkTable
     for(int i = 0; i < input_links.length; i++ )
        linkTable.put(input_links[i].getId(),input_links[i]);

     GraphNode node1, node2;
     enum = linkTable.keys();
     while( enum.hasMoreElements() ) {
        linkId = (String)enum.nextElement();
        link = (TaskLink)linkTable.get(linkId);
        // create parent-child relations between nodes
        node1 = (GraphNode)nodeTable.get(link.getLeftNode());
        node2 = (GraphNode)nodeTable.get(link.getRightNode());
        node1.addParent(node2);
        node2.addChild(node1);
     }
     fireGraphStructureChanged();
     fireChanged();
  }
  public boolean isNodeEditable(GraphNode node) {
     return true;
  }

  public void removeNodes(GraphNode[] input) {
     TaskNode node;
     GraphNode node1, node2;
     TaskLink link;
     Enumeration enum;
     String name;
     for(int i = 0; i < input.length; i++ ) {
        node = (TaskNode)input[i].getUserObject();
        name = node.getName();
        if ( name.equals(TaskNode.BEGIN) || name.equals(TaskNode.END) ) {
           JOptionPane.showMessageDialog(null,
              "Cannot delete the BEGIN or END nodes",
              "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
           nodeTable.remove(node.getName());
           // remove links between node[i] and all other nodes in nodeTable
           enum = linkTable.elements();
           while( enum.hasMoreElements() ) {
              link = (TaskLink)enum.nextElement();
              if ( link.referencesNode(node.getName()) ) {
                 node1 = (GraphNode)nodeTable.get(link.getLeftNode());
                 node2 = (GraphNode)nodeTable.get(link.getRightNode());
                 if ( node1 != null && node2 != null ) {
                    node1.removeParent(node2);
                    node2.removeChild(node1);
                 }
              }
           }
           fireGraphNodeRemoved(input[i]);
        }
     }
     fireChanged();
  }

  public void addNewNode(int type)  {
     GraphNode[] node = new GraphNode[1];
     String name = "node" + (count++);
     while( contains(name,NO_ERROR) )
        name = "node" + (count++);
     switch(type) {
        case BASIC_NODE:
             node[0] = new GraphNode(new TaskNode(name));
             break;
        case GUARD_NODE:
             node[0] = new GraphNode(new ConditionalNode(name));
             break;
        default:
             Core.ERROR(null,1001,this);
             break;
     }
     addNodes(node);
  }

  public void addNodes(GraphNode[] input) {
     if ( input == null || input.length == 0 ) return;

     TaskNode node;
     String name;
     for(int i = 0; i < input.length; i++ ) {
        node = (TaskNode)input[i].getUserObject();
        if ( node.isConditionalNode() )
           node = new ConditionalNode((ConditionalNode)node);
        else
           node = new TaskNode(node);
        name = node.getName();
        while( contains(name,NO_ERROR) )
           name += "$" + (count++);
        node.setName(name);
        GraphNode gnode = new GraphNode(node);
        nodeTable.put(name,gnode);
        fireGraphNodeAdded(gnode);
     }
     fireChanged();
  }

  protected boolean contains(String name, boolean error) {
     if ( nodeTable.containsKey(name) ) {
        if ( error )
           JOptionPane.showMessageDialog(null,
              "Attempting to rename node to an already\nexisting name",
              "Error", JOptionPane.ERROR_MESSAGE);
        return true;
     }
     return false;
  }

  public void stateChanged(ChangeEvent e) {
     // Underlying ontology has changed!!
     // NEED to verify all facts!!
  }

  public void addChangeListener(ChangeListener x) {
     listeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     listeners.remove(ChangeListener.class, x);
  }
  public void addRenameListener(RenameListener x) {
     listeners.add(RenameListener.class, x);
  }
  public void removeRenameListener(RenameListener x) {
     listeners.remove(RenameListener.class, x);
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

  protected void fireNameChanged(Object object, Object previous,
                                 Object current) {
     RenameEvent c = new RenameEvent(this,object,previous,current);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == RenameListener.class) {
           RenameListener cl = (RenameListener)list[i+1];
           cl.nameChanged(c);
        }
     }
  }
}
