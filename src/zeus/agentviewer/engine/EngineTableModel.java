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



package zeus.agentviewer.engine;

import javax.swing.table.*;
import javax.swing.*;
import java.util.*;
import zeus.util.OrderedHashtable;
import zeus.actors.rtn.Node;
import zeus.actors.event.*;
import zeus.actors.*;
import zeus.actors.rtn.Engine;
import zeus.gui.graph.*;
import zeus.util.Core;
import zeus.util.Misc;


public class EngineTableModel  extends AbstractTableModel implements NodeMonitor {
      private String[] header = { "Graphs" };
      private OrderedHashtable   data ;
      private GraphsModel graphsModel;
      private Engine engine;
      
      private int      BUFFER_CAPACITY = 25;
      private int      REMOVE_INDEX = 0;
//--------------------------------------------------------------------------
      public EngineTableModel(AgentContext context){
          data = new OrderedHashtable();
          graphsModel = new GraphsModel();
          engine = context.Engine();
          engine.addNodeMonitor(this, NodeEvent.CREATE_MASK |
	     NodeEvent.DISPOSE_MASK | NodeEvent.STATE_CHANGE_MASK);
      }
//--------------------------------------------------------------------------
       public int getRowCount() {
           return data.size();
       }
//--------------------------------------------------------------------------
       public int getColumnCount() {
           return 1;
       }
//--------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
        try { 
            return (String) data.getKeyAt(row);
        } catch (Exception e) { 
            return ("removed");  // this occurs when a graph has been sweeped off the 
            // table and the table length has not been updated yet - catch it and 
            // put it away
        }
        }
//--------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  header[col];
       }
//--------------------------------------------------------------------------

       EngineGraphModel markedForDeletion = null;
       
       protected void markForDeletion(EngineGraphModel eRoot) {
          if ( markedForDeletion != null ) {
             Node root_node = (Node)(((markedForDeletion).getRoot()).getUserObject());
             String name = root_node.getDescription();
             data.remove(name);
             fireTableDataChanged();
             markedForDeletion.deleteTree(graphsModel);
          }
          markedForDeletion = eRoot;
       }


       public  void nodeCreatedEvent(NodeEvent event){
           Core.DEBUG(1,"EngineTableModel addNode(): " + event.getNode().getDescription());
          if (event.getParents() == null) {
              Core.DEBUG(1,"\tNode has no parents");
              EngineGraphModel newRoot = new EngineGraphModel(new GraphNode(event.getNode()));
              try {
                System.out.println("ETM >> " + data.size()); 
	          while (data.size() > BUFFER_CAPACITY ) {
	            System.out.println("ETM>> " + data.getKeyAt(REMOVE_INDEX)); 
	            EngineGraphModel eRoot = (EngineGraphModel)data.get(data.getKeyAt(REMOVE_INDEX)); 
	            markForDeletion(eRoot);
	            Thread.yield(); 
	            }
	          } catch (Exception e) { 
	                e.printStackTrace(); 
	          }

               // data.removeElementAt(REMOVE_INDEX); 
	          data.put(event.getNodeName(),newRoot);
              graphsModel.addRoot(event.getNodeName(),newRoot.getRoot());
              fireTableDataChanged();
          }
          else {
             Core.DEBUG(1,"\tNode has parents: " + Misc.concat(event.getParentNames()));
             GraphNode gNode = graphsModel.addToGraph(event);
             EngineGraphModel eRoot = updateGraph(event.getNodeName());
             if (eRoot != null && gNode != null) {
                eRoot.refresh(EngineGraphModel.ADD,gNode);
             }
             else
             {
                debug ("eRoot or gNode null"); 
                Core.DEBUG(1,"eRoot or gNode is null");}
          }
       }
//--------------------------------------------------------------------------
       private EngineGraphModel updateGraph(String name){
        try {
          GraphNode aRoot = graphsModel.getRoot(name);
          Node node = (Node)aRoot.getUserObject();
          EngineGraphModel eRoot = (EngineGraphModel)data.get(node.getDescription());
          return eRoot;}
          catch (Exception e) { // added 30/9/01
            return (null); 
          }

       }
//--------------------------------------------------------------------------
       public  void nodeStateChangedEvent(NodeEvent event){
           GraphNode gNode = graphsModel.changeNode(event);
//           fireTableDataChanged();
           EngineGraphModel eRoot = updateGraph(event.getNodeName());

          if (eRoot != null && gNode != null) {
             eRoot.refresh(EngineGraphModel.CHANGE,gNode);
/*
             Node last_node = (Node)gNode.getUserObject();
             if ( last_node.getState() == Node.DONE ||
	          last_node.getState() == Node.FAILED ) {
                Node root_node = (Node)(((eRoot).getRoot()).getUserObject());
                if ( root_node.getGraph() == last_node.getGraph() )
                   markForDeletion(eRoot);
             }
*/
          }
       }
//--------------------------------------------------------------------------
       public void removeAll(){
         data.clear();
         fireTableDataChanged();
       }
//--------------------------------------------------------------------------
       public void removeGraph(EngineGraphModel root){
          data.remove(root);
          fireTableDataChanged();
       }
//--------------------------------------------------------------------------
       public EngineGraphModel getGraph(int row){
          String key = (String) data.getKeyAt(row);
          return  (EngineGraphModel) data.get(key);
       }

//--------------------------------------------------------------------------
       public  void nodeDisposedEvent(NodeEvent event){
       }
//--------------------------------------------------------------------------
       public void removeZeusEventMonitors(){
         engine.removeNodeMonitor(this, NodeEvent.CREATE_MASK |
	    NodeEvent.DISPOSE_MASK | NodeEvent.STATE_CHANGE_MASK);
       }
       
       private void debug (String str) { 
            System.out.println("EngineTableModel>>" + str); 
       }
}