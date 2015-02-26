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
* TaskLinkBaseTreeModel.java
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
import zeus.generator.util.*;

public class TaskLinkBaseTreeModel extends DefaultTreeModel
                                implements ChangeListener, RenameListener {

  static final String PRECONDITIONS   = "PRECONDITIONS";
  static final String EFFECTS         = "EFFECTS";
  static final String BOTH            = "BOTH";
  static final String LEFT_SEPARATOR  = "[";
  static final String RIGHT_SEPARATOR = "] ";

  static final String[] INVISIBLE_ITEMS = {
     PRECONDITIONS, EFFECTS
  };

  protected GroupManager   leftGroupManager = null;
  protected GroupManager   rightGroupManager = null;
  protected String         mode = BOTH;
  protected String         nodeName = "--undefined--";

  public TaskLinkBaseTreeModel(GroupManager leftGroupManager,
                               GroupManager rightGroupManager) {

    super(new DefaultMutableTreeNode("--undefined--"));

    this.leftGroupManager = leftGroupManager;
    this.rightGroupManager = rightGroupManager;

    leftGroupManager.addChangeListener(this);
    rightGroupManager.addChangeListener(this);
    leftGroupManager.addRenameListener(this);
    rightGroupManager.addRenameListener(this);

    refresh();
  }

  void reset(String mode, String name) {
    this.nodeName = name;
    setMode(mode);
  }

  String getNodeName() {
    return nodeName;
  }

  void refresh(String mode) {
    setMode(mode);
  }

  void refresh() {
    DefaultMutableTreeNode base, prec, post, xnode, gnode;
    Vector items;
    String name, group;
    Enumeration enum;
    Fact f1;
    Hashtable input;

    root = new DefaultMutableTreeNode(nodeName);
    base = (DefaultMutableTreeNode)root;

    if ( mode.equals(BOTH) || mode.equals(PRECONDITIONS) ) {
       prec = new DefaultMutableTreeNode(PRECONDITIONS);
       base.add(prec);

       input = leftGroupManager.getManagerData();
       enum = input.keys();
       while( enum.hasMoreElements() ) {
          group = (String)enum.nextElement();

          gnode = new DefaultMutableTreeNode(group);
          prec.add(gnode);

          items = (Vector)input.get(group);
          for(int i = 0; i < items.size(); i++ ) {
             f1 = (Fact)items.elementAt(i);
             name = compoundName(f1.getType(),f1.getId());
             xnode = new DefaultMutableTreeNode(name);
             gnode.add(xnode);
          }
       }
    }
    if ( mode.equals(BOTH) || mode.equals(EFFECTS) ) {
       post = new DefaultMutableTreeNode(EFFECTS);
       base.add(post);

       input = rightGroupManager.getManagerData();
       enum = input.keys();
       while( enum.hasMoreElements() ) {
          group = (String)enum.nextElement();

          gnode = new DefaultMutableTreeNode(group);
          post.add(gnode);

          items = (Vector)input.get(group);
          for(int i = 0; i < items.size(); i++ ) {
             f1 = (Fact)items.elementAt(i);
             name = compoundName(f1.getType(),f1.getId());
             xnode = new DefaultMutableTreeNode(name);
             gnode.add(xnode);
          }
       }
    }

    reload();
  }

  public void setMode(String mode) {
     // if ( mode.equals(this.mode) ) return;
     Core.ERROR(mode.equals(BOTH) || mode.equals(PRECONDITIONS) ||
                mode.equals(EFFECTS), 1, this);
     this.mode = mode;
     refresh();
  }

  public static String getFactType(String name) {
     StringTokenizer st = new StringTokenizer(name,LEFT_SEPARATOR+RIGHT_SEPARATOR);
     return st.nextToken();
  }
  public static String getFactId(String name) {
     StringTokenizer st = new StringTokenizer(name,LEFT_SEPARATOR+RIGHT_SEPARATOR);
     st.nextToken(); // ignore fact type
     return st.nextToken();
  }

  public static String compoundName(String type, String id) {
     return LEFT_SEPARATOR + type + RIGHT_SEPARATOR + id;
  }

  public void stateChanged(ChangeEvent e) {
     refresh();
  }
  public void nameChanged(RenameEvent e) {
     // nodeName has changed
     String prev = (String)e.getOriginal();
     String curr = (String)e.getCurrent();
     if ( prev.equals(curr) ) return;
     
     if ( nodeName.equals(prev) ) {
        nodeName = curr;
        DefaultMutableTreeNode xnode = (DefaultMutableTreeNode)root;
        xnode.setUserObject(nodeName);
        nodeChanged(root);
     }
     else
        refresh();
  }
}
