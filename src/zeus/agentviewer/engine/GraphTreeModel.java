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
import javax.swing.tree.*;
import zeus.util.Tree;


public class GraphTreeModel extends DefaultTreeModel {

  private Tree graph;
//----------------------------------------------------------------------------
  public GraphTreeModel(Tree graph) {
    super(new DefaultMutableTreeNode(graph.getRoot()));
    this.graph = graph;
    createTree((DefaultMutableTreeNode)getRoot(),graph.getRoot());
    reload();
  }
//----------------------------------------------------------------------------
  private void createTree(DefaultMutableTreeNode m_node,
                          zeus.util.TreeNode a_node) {

    // takes contents of model and inserts them into the tree

    Vector children = a_node.getChildren();
    zeus.util.TreeNode b_node;
    DefaultMutableTreeNode n_node;

    for(int i = 0; i < children.size(); i++ ) {
       b_node = (zeus.util.TreeNode)children.elementAt(i);
       n_node = new DefaultMutableTreeNode(b_node);
       m_node.add(n_node);
       createTree(n_node,b_node);
    }
  }
}
