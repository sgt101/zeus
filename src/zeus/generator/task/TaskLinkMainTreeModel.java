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
* TaskLinkMainTreeModel.java
*
* The underlying model for the Fact Hierarchy tree
*****************************************************************************/

package zeus.generator.task;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import zeus.concepts.*;
import zeus.util.HSet;
import zeus.util.Core;
import zeus.generator.event.*;

public class TaskLinkMainTreeModel extends DefaultTreeModel
                                   implements RenameListener {

  static final String DEFAULT_ROOT  = "#Root#";
  static final String PRECONDITIONS = TaskLinkBaseTreeModel.PRECONDITIONS;
  static final String EFFECTS       = TaskLinkBaseTreeModel.EFFECTS;

  protected TaskNode[] nodes = new TaskNode[0];
  protected String     ignoreNode;
  protected String     mode = PRECONDITIONS;

  public TaskLinkMainTreeModel() {
    super(new DefaultMutableTreeNode(DEFAULT_ROOT));
  }

  public TaskLinkMainTreeModel(TaskNode[] nodes, String ignoreNode) {
    this();
    reset(nodes,ignoreNode);
  }

  void reset(TaskNode[] nodes, String ignoreNode) {
    this.nodes = nodes;
    this.ignoreNode = ignoreNode;
    mode = PRECONDITIONS;
    refresh();
  }

  void refresh(String mode) {
    setMode(mode);
  }

  void refresh() {
    DefaultMutableTreeNode base, xnode, gnode, leaf;
    Vector items;
    Hashtable input;
    String name, group;
    Enumeration enum;
    Fact f1;

    root = new DefaultMutableTreeNode(DEFAULT_ROOT);
    base = (DefaultMutableTreeNode)root;

    for(int k = 0; k < nodes.length; k++ ) {
       if ( !((nodes[k].getName()).equals(ignoreNode)) ) {
          xnode = new DefaultMutableTreeNode(nodes[k].getName());
          base.add(xnode);

          input = null;
          if ( mode.equals(PRECONDITIONS) )
             input = nodes[k].getAllPreconditions();
          else if ( mode.equals(EFFECTS) )
             input = nodes[k].getAllPostconditions();
          else
             Core.ERROR(null,2,this);

          enum = input.keys();
          while( enum.hasMoreElements() ) {
             group = (String)enum.nextElement();

             gnode = new DefaultMutableTreeNode(group);
             xnode.add(gnode);

             items = (Vector)input.get(group);
             for(int i = 0; i < items.size(); i++ ) {
                f1 = (Fact)items.elementAt(i);
                name = TaskLinkBaseTreeModel.compoundName(f1.getType(),f1.getId());
                leaf = new DefaultMutableTreeNode(name);
                gnode.add(leaf);
             }
          }
       }
    }
    reload();
  }

  public void setMode(String mode) {
     if ( mode.equals(this.mode) ) return;
     Core.ERROR(mode.equals(PRECONDITIONS) || mode.equals(EFFECTS), 1, this);
     this.mode = mode;
     refresh();
  }

  public void setInverseMode(String inverse) {
     String new_mode = null;

     if ( inverse.equals(PRECONDITIONS) )
        new_mode = EFFECTS;
     else if ( inverse.equals(EFFECTS) )
        new_mode = PRECONDITIONS;
     else
        Core.ERROR(null, 1, this);

     if ( mode != null && new_mode.equals(mode) ) return;
     this.mode = new_mode;
     refresh();
  }

  public String getMode() { return mode; }

  public static String getFactType(String name) {
     return TaskLinkBaseTreeModel.getFactType(name);
  }

  public static String getFactId(String name) {
     return TaskLinkBaseTreeModel.getFactId(name);
  }
  public void nameChanged(RenameEvent e) {
     // ignoreNode has changed
     String prev = (String)e.getOriginal();
     String curr = (String)e.getCurrent();
     if ( prev.equals(curr) ) return;
     if ( ignoreNode == null || ignoreNode.equals(prev) ) ignoreNode = curr;
  }
}
