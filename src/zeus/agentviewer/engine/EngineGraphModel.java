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

import java.util.*;
import zeus.util.*;
import zeus.gui.graph.*;

public class EngineGraphModel extends AbstractGraphModel{
      final static int ADD = 0;
      final static int DELETE = 1;
      final static int CHANGE = 2;
      private int      BUFFER_CAPACITY = 50;
      private int      REMOVE_INDEX = 0;


      GraphNode root;
      Hashtable allNodes = new Hashtable();

      public EngineGraphModel(GraphNode root){
        this.root = root;
      }
//--------------------------------------------------------------------------
      public  Enumeration nodes() {
          addNode(root);
	  getAllNodesOf(root);
          return allNodes.elements();
      }
//--------------------------------------------------------------------------
     public boolean isLinkVisible(GraphNode from, GraphNode to) {
        return to.hasParent(from) ;
     }
//--------------------------------------------------------------------------
     public  void setValue(GraphNode node, Object user_object) {}
//--------------------------------------------------------------------------
     public  boolean isNodeEditable(GraphNode node) { return false; }
//--------------------------------------------------------------------------
     private void getAllNodesOf(GraphNode a_node) {
       GraphNode[] children = a_node.getChildren();

       for(int i = 0; i < children.length; i++ ) {
          addNode(children[i]);
          getAllNodesOf(children[i]);
       }
     }
//--------------------------------------------------------------------------
    public GraphNode getRoot() {
        return root;
    }
//--------------------------------------------------------------------------
    private void addNode(GraphNode gNode){
             
       zeus.actors.rtn.Node  node = (zeus.actors.rtn.Node)gNode.getUserObject();
       allNodes.put(node.getDescription(),gNode);
    }
//--------------------------------------------------------------------------
    public void refresh(int val, GraphNode aNode) {
      if ( val == CHANGE )
         fireGraphNodeStateChanged(aNode);
      else
         fireGraphStructureChanged();
    }
//--------------------------------------------------------------------------
    void deleteTree(GraphsModel graphsModel) {
       deleteTreeNode(root,graphsModel);
    }

    protected void deleteTreeNode(GraphNode a_node, GraphsModel graphsModel) {
       GraphNode[] children = a_node.getChildren();

       for(int i = 0; i < children.length; i++ )
          deleteTreeNode(children[i],graphsModel);

       graphsModel.removeNode(a_node);
    }

}