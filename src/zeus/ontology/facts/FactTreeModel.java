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
* FactTreeModel.java
*
* The underlying model for the Fact Hierarchy tree
*****************************************************************************/

package zeus.ontology.facts;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import zeus.concepts.*;
import zeus.ontology.*;


public class FactTreeModel extends DefaultTreeModel
                           implements ChangeListener {

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Rename failed"
  };

  protected OntologyDb model;

  public FactTreeModel(OntologyDb model) {
    super(new DefaultMutableTreeNode(model.getRoot()));
    this.model = model;
    createTree((DefaultMutableTreeNode)getRoot(),model.getRoot());
    model.addChangeListener(this);
  }

  protected void createTree(DefaultMutableTreeNode m_node,
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

  public void valueForPathChanged(TreePath path, Object aValue) {
    String newname = (String)aValue;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
      path.getLastPathComponent();
    String name = node.getUserObject().toString();
    if ( name.equals(newname) ) return;

    Object object;
    if ( (object = model.renameFact(name,newname)) != null ) {
       node.setUserObject(object);
       nodeChanged(node);
    }
    else
       errorMsg(0);
  }

  void refresh() {
     root = new DefaultMutableTreeNode(model.getRoot());
     createTree((DefaultMutableTreeNode)root,model.getRoot());
     reload();
  }

  void errorMsg(int tag) {
     JOptionPane.showMessageDialog(null,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  // -- MANIPULATE INTERNAL STATE METHODS ---------------------------

  public boolean isEditable(String name) {
     return model.isFactEditable(name);
  }

  void addNewChild(DefaultMutableTreeNode parent) {
     DefaultMutableTreeNode node;
     zeus.util.TreeNode p_node = (zeus.util.TreeNode)parent.getUserObject();
     zeus.util.TreeNode c_node = model.addChildFact(p_node);
     node = new DefaultMutableTreeNode(c_node);
     insertNodeInto(node,parent,parent.getChildCount());
  }

  Object renameFact(String old_name, String new_name) {
     return model.renameFact(old_name,new_name);
  }

  void removeNode(DefaultMutableTreeNode node) {
    if ( node != null && node != (DefaultMutableTreeNode)getRoot() ) {
       model.removeFact((zeus.util.TreeNode)node.getUserObject());
       DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
       removeNodeFromParent(node);
       nodeStructureChanged(parent);
    }
  }

  DefaultMutableTreeNode cutNode(DefaultMutableTreeNode node) {
    if ( node != null && node != (DefaultMutableTreeNode)getRoot() ) {
       model.removeFact((zeus.util.TreeNode)node.getUserObject());
       DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
       removeNodeFromParent(node);
       nodeStructureChanged(parent);
       return node;
    }
    return null;
  }
  DefaultMutableTreeNode copyNode(DefaultMutableTreeNode node) {
     if ( node != null ) {
        zeus.util.TreeNode o_node = (zeus.util.TreeNode)node.getUserObject();
        zeus.util.TreeNode c_node = model.copyFactTree(o_node);
        DefaultMutableTreeNode m_node = new DefaultMutableTreeNode(c_node);
        createTree(m_node,c_node);
        return m_node;
     }
     return null;
  }
  void pasteNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode node) {
     if ( node != null ) {
        zeus.util.TreeNode p_node = (zeus.util.TreeNode)parent.getUserObject();
        zeus.util.TreeNode c_node = (zeus.util.TreeNode)node.getUserObject();
        zeus.util.TreeNode x_node = model.pasteFactTree(p_node,c_node);
        DefaultMutableTreeNode m_node = new DefaultMutableTreeNode(x_node);
        parent.add(m_node);
        createTree(m_node,x_node);
        nodeStructureChanged(parent);
     }
     return;
  }

  // -- CHANGE LISTENER METHODS -------------------------------------

  public void stateChanged(ChangeEvent e) {
    OntologyDbChangeEvent evt = (OntologyDbChangeEvent)e;
    if ( evt.getEventType() == OntologyDb.RELOAD )
       refresh();
  }

}
