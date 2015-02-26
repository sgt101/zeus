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

import zeus.util.OrderedHashtable;
import  zeus.actors.rtn.Node;
import zeus.actors.event.NodeEvent;
import zeus.gui.graph.*;


public class GraphsModel   {

      private OrderedHashtable data;
//--------------------------------------------------------------------------
      public GraphsModel() {
          data = new OrderedHashtable();
      }
//--------------------------------------------------------------------------
       public GraphNode addToGraph(NodeEvent event){
          GraphNode parent;
          Node node = event.getNode();
          GraphNode gNode = new GraphNode(node);
          String nodeName = event.getNodeName();
	  String[] parentNames =  event.getParentNames();

          for(int i = 0; i< parentNames.length; i++) {
	     if ( data.containsKey(parentNames[i]) ) {
                parent = (GraphNode) data.get(parentNames[i]);
                parent.addChild(gNode);
                gNode.addParent(parent);
             }
          }
          data.put(nodeName,gNode);
          return gNode;
       }
//--------------------------------------------------------------------------
       public GraphNode changeNode(NodeEvent event){
          GraphNode aGraphNode = null;

          if (data.containsKey(event.getNodeName())) {
             aGraphNode = (GraphNode) data.get(event.getNodeName());
             aGraphNode.setUserObject(event.getNode());
          }
          return aGraphNode;
       }
//--------------------------------------------------------------------------
       public void addRoot(String rootName, GraphNode root) {
         data.put(rootName,root);
       }
//--------------------------------------------------------------------------
       public void removeAll(){
         data.clear();
       }
//--------------------------------------------------------------------------
       public void removeNode(NodeEvent event){
          data.remove(event.getNodeName());
       }
       public void removeNode(GraphNode gnode){
          Node node = (Node)gnode.getUserObject();
          data.remove(node.getDescription());
       }

//--------------------------------------------------------------------------
       public GraphNode getRoot(String name) {
          GraphNode root = null;
	  GraphNode node = (GraphNode)data.get(name);
          GraphNode[] parents = node.getParents();
          if ( parents.length == 0 ) return node;

          while( parents.length != 0 ) {
            root = parents[0];
	    parents = parents[0].getParents();
          }
          return root;
       }
}