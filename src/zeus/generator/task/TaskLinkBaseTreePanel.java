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
* TaskLinkBaseTreePanel.java
*
*
*****************************************************************************/

package zeus.generator.task;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import zeus.util.SystemProps;
import zeus.util.Misc;
import zeus.generator.event.*;

public class TaskLinkBaseTreePanel extends JPanel {
  protected EventListenerList selectionListeners = new EventListenerList();

  protected JTree                 tree;
  protected TaskLinkBaseTreeModel model;
  protected JTextField            field;

  public TaskLinkBaseTreePanel(TaskLinkBaseTreeModel model) {
    this.model = model;

    setBorder(new EmptyBorder(10,10,10,10));
    setBackground(Color.lightGray);
    setLayout(new BorderLayout());

    tree = new JTree(model);
    tree.setEditable(false);
    tree.setRootVisible(true);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "generator" + sep;
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(new ImageIcon(path + "cloud.gif"));
    tree.setCellRenderer(renderer);

    tree.putClientProperty( "JTree.lineStyle", "Angled" );

    TreeSelectionModel selectionModel = tree.getSelectionModel();
    selectionModel.addTreeSelectionListener(new SymSelectAction());
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    JScrollPane scrollpane = new JScrollPane();
    scrollpane.setPreferredSize(new Dimension(200,200));
    scrollpane.getViewport().add(tree);
    add(scrollpane,BorderLayout.CENTER);

    field = new JTextField(20);
    CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
      new EmptyBorder(5,5,5,5));
    field.setBorder(cbr);
    field.setEditable(false);

    add(field,BorderLayout.SOUTH);
  }

  class SymSelectAction implements TreeSelectionListener {
     public void valueChanged(TreeSelectionEvent e) {
       String type = getSelectedNodeType();
       String node = model.getNodeName();
       if ( type != null ) {
          fireRootSelectionAction(node,type);
          String name = getSelectedNodeName();
          if ( name != null ) {
             String group = getSelectedNodeGroup();
             field.setText(node + " " + group + " " + name);
             String fact = model.getFactType(name);
             String factId = model.getFactId(name);
             fireNodeSelectionAction(node,group,type,fact,factId);
          }
          else
             field.setText(null);
       }
     }
  }

  protected String getSelectedNodeName() {
     String name = null;
     int length;
     TreePath path = tree.getSelectionPath();
     if ( path != null && (length = path.getPathCount()) != 0 ) {
	for(int i = 1; i < length; i++ ) {
	   name = path.getPathComponent(i).toString();
           if ( !Misc.member(name,TaskLinkBaseTreeModel.INVISIBLE_ITEMS) ) {
	      // group found
	      return (i < length-1)
	             ? path.getPathComponent(length-1).toString() : null;
           }
        }
        return null;
     }
     return null;
  }
  protected String getSelectedNodeType() {
     String name = null;
     int length;
     TreePath path = tree.getSelectionPath();
     if ( path != null && (length = path.getPathCount()) != 0 ) {
	for(int i = 1; i < length; i++ ) {
	   name = path.getPathComponent(i).toString();
           if ( name.equals(TaskLinkBaseTreeModel.PRECONDITIONS) ||
                name.equals(TaskLinkBaseTreeModel.EFFECTS) )
              return name;
        }
        return null;
     }
     return null;
  }
  protected String getSelectedNodeGroup() {
     String name = null;
     int length;
     TreePath path = tree.getSelectionPath();
     if ( path != null && (length = path.getPathCount()) != 0 ) {
	for(int i = 1; i < length; i++ ) {
	   name = path.getPathComponent(i).toString();
           if ( !Misc.member(name,TaskLinkBaseTreeModel.INVISIBLE_ITEMS) )
              return name;
        }
        return null;
     }
     return null;
  }

  public void addLinkNodeSelectionListener(LinkNodeSelectionListener x) {
     selectionListeners.add(LinkNodeSelectionListener.class, x);
  }
  public void removeLinkNodeSelectionListener(LinkNodeSelectionListener x) {
     selectionListeners.remove(LinkNodeSelectionListener.class, x);
  }
  public void addLinkRootSelectionListener(LinkRootSelectionListener x) {
     selectionListeners.add(LinkRootSelectionListener.class, x);
  }
  public void removeLinkRootSelectionListener(LinkRootSelectionListener x) {
     selectionListeners.remove(LinkRootSelectionListener.class, x);
  }

  protected void fireNodeSelectionAction(String node, String group,
     String type, String fact, String factId) {

     LinkNodeSelectionEvent evt;
     evt = new LinkNodeSelectionEvent(this,node,group,type,fact,factId);
     Object[] listeners = selectionListeners.getListenerList();
     for(int i = listeners.length-2; i >= 0; i -= 2) {
        if (listeners[i] == LinkNodeSelectionListener.class) {
           ((LinkNodeSelectionListener)listeners[i+1]).linkNodeSelected(evt);
        }
     }
  }
  protected void fireRootSelectionAction(String node, String type) {
     LinkRootSelectionEvent evt;
     evt = new LinkRootSelectionEvent(this,node,type);
     Object[] listeners = selectionListeners.getListenerList();
     for(int i = listeners.length-2; i >= 0; i -= 2) {
        if (listeners[i] == LinkRootSelectionListener.class) {
           ((LinkRootSelectionListener)listeners[i+1]).linkRootSelected(evt);
        }
     }
  }
   
}
