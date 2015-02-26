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
* TypeTreeModel.java
*
* The underlying model for the Fact Hierarchy tree
*****************************************************************************/

package zeus.ontology;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import zeus.concepts.*;
import zeus.ontology.*;


public class TypeTreeModel extends DefaultTreeModel {
  public static final int FACT        = 0;
  public static final int RESTRICTION = 1;

  protected static final String DEFAULT_ROOT = "#DefaultRoot#";
  protected static final String BASIC_TYPES  = "Basic Types";
  protected static final String RESTRICTIONS = "Restrictions";
  protected static final String FACTS        = "Facts";
  protected static final String SETS         = "Set of ...";

  protected OntologyDb model;
  protected int mode;

  public TypeTreeModel(OntologyDb model, int mode) {
    super(new DefaultMutableTreeNode(DEFAULT_ROOT));
    this.model = model;
    this.mode = mode;
    refresh();
  }

  void refresh() {
    root = new DefaultMutableTreeNode(DEFAULT_ROOT);

    DefaultMutableTreeNode basic, restrictions, facts, base, sets;
    base = (DefaultMutableTreeNode)root;
    basic = new DefaultMutableTreeNode(BASIC_TYPES);
    restrictions = new DefaultMutableTreeNode(RESTRICTIONS);
    facts = new DefaultMutableTreeNode(FACTS);
//    sets = new DefaultMutableTreeNode(SETS);
//    need to add sets to both restrictions and facts

    base.add(basic);
    base.add(restrictions);

    for(int i = 0; i < OntologyDb.BASIC_TYPES.length; i++ )
       basic.add(new DefaultMutableTreeNode(OntologyDb.BASIC_TYPES[i]));

    String[] names = model.getAllRestrictionNames();
    for(int i = 0; i < names.length; i++ )
       restrictions.add(new DefaultMutableTreeNode(names[i]));

    if ( mode == FACT ) {
       base.add(facts);
       basic.add(new DefaultMutableTreeNode(OntologyDb.OBJECT_TYPE));

       DefaultMutableTreeNode node;
       node = new DefaultMutableTreeNode(model.getRoot());
       facts.add(node);
       createTree(node,model.getRoot());
    }
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
}
