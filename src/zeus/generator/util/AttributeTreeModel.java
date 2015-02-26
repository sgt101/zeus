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
* AttributeTreeModel.java
*
* The underlying model for the Fact Hierarchy tree
*****************************************************************************/

package zeus.generator.util;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import zeus.concepts.*;
import zeus.util.HSet;


public class AttributeTreeModel extends DefaultTreeModel
                                implements ChangeListener {

  static final String PRECONDITIONS = "PRECONDITIONS";
  static final String EFFECTS       = "EFFECTS";
  static final String DEFAULT_ROOT  = "#Root";

  static final String[] INVISIBLE_ITEMS = {
     DEFAULT_ROOT, PRECONDITIONS, EFFECTS
  };

  protected AttributeModel model = null;
  protected BasicFactModel preconditionsModel = null;
  protected BasicFactModel postconditionsModel = null;
  protected BasicFactModel singletonModel = null;
  protected boolean        changed;

  public AttributeTreeModel(AttributeModel model) {
    this();
    this.model = model;
  }
  public AttributeTreeModel() {
    super(new DefaultMutableTreeNode(DEFAULT_ROOT));
    changed = true;
  }

  public void setFactModels(BasicFactModel preconditionsModel,
                            BasicFactModel postconditionsModel) {

    if ( this.preconditionsModel != null ) {
       this.preconditionsModel.removeChangeListener(this);
       this.postconditionsModel.removeChangeListener(this);
    }

    this.preconditionsModel = preconditionsModel;
    this.postconditionsModel = postconditionsModel;

    preconditionsModel.addChangeListener(this);
    postconditionsModel.addChangeListener(this);

    if ( singletonModel != null ) {
       singletonModel.removeChangeListener(this);
       singletonModel = null;
    }
    changed = true;
  }

  public void setFactModel(BasicFactModel singletonModel) {
    if ( this.preconditionsModel != null ) {
       this.preconditionsModel.removeChangeListener(this);
       this.postconditionsModel.removeChangeListener(this);
       this.preconditionsModel = null;
       this.postconditionsModel = null;
    }

    if ( this.singletonModel != null )
       this.singletonModel.removeChangeListener(this);

    this.singletonModel = singletonModel;
    this.singletonModel.addChangeListener(this);
    changed = true;
  }

  void refresh() {
    if ( !changed ) return;

    DefaultMutableTreeNode base, prec, post, xnode;
    Fact[] items;

    root = new DefaultMutableTreeNode(DEFAULT_ROOT);
    base = (DefaultMutableTreeNode)root;

    zeus.util.TreeNode node;
    if ( preconditionsModel != null ) {
       String this_id = ( model != null ) ? model.getData().getId() : "";
       prec = new DefaultMutableTreeNode(PRECONDITIONS);
       post = new DefaultMutableTreeNode(EFFECTS);
       base.add(prec);
       base.add(post);

       items = preconditionsModel.getData();
       for(int i = 0; i < items.length; i++ ) {
          node = items[i].createAttributeTree(items[i].getId().equals(this_id));
          xnode = new DefaultMutableTreeNode(node);
          createTree(xnode,node);
          prec.add(xnode);
       }

       items = postconditionsModel.getData();
       for(int i = 0; i < items.length; i++ ) {
          node = items[i].createAttributeTree(items[i].getId().equals(this_id));
          xnode = new DefaultMutableTreeNode(node);
          createTree(xnode,node);
          post.add(xnode);
       }
    }
    else if ( singletonModel != null ) {
       String this_id = ( model != null ) ? model.getData().getId() : "";
       items = singletonModel.getData();
       for(int i = 0; i < items.length; i++ ) {
          node = items[i].createAttributeTree(items[i].getId().equals(this_id));
          xnode = new DefaultMutableTreeNode(node);
          createTree(xnode,node);
          base.add(xnode);
       }
    }
    else if ( model != null ) {
       node = model.getData().createAttributeTree(true);
       xnode = new DefaultMutableTreeNode(node);
       createTree(xnode,node);
       base.add(xnode);
    }

    changed = false;
    reload();
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

  public void stateChanged(ChangeEvent e) {
     changed = true;
  }
}
