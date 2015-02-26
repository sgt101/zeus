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
* TaskLinkMainTreePanel.java
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

public class TaskLinkMainTreePanel extends JPanel 
                                   implements LinkRootSelectionListener {

  protected EventListenerList selectionListeners = new EventListenerList();

  protected JTree                 tree;
  protected TaskLinkMainTreeModel model;
  protected JTextField            field;


  public TaskLinkMainTreePanel(TaskLinkMainTreeModel model) {
    this.model = model;

    setBorder(new EmptyBorder(10,10,10,10));
    setBackground(Color.lightGray);
    setLayout(new BorderLayout());

    tree = new JTree(model);
    tree.setEditable(false);
    tree.setRootVisible(false);

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
       String name = getSelectedItem();
       if ( name != null ) {
          String node = getSelectedNode();
          String group = getSelectedItemGroup();
          field.setText(node + " " + group + " " + name);
          String type = model.getMode();
          String fact = model.getFactType(name);
          String factId = model.getFactId(name);
          fireSelectionAction(node,group,type,fact,factId);
       }
       else
          field.setText(null);
     }
  }

  public void linkRootSelected(LinkRootSelectionEvent e) {
     field.setText(null);
     String mode = e.getType();
     model.setInverseMode(mode);
  }
  protected String getSelectedNode() {
     TreePath path = tree.getSelectionPath();
     if ( path != null && path.getPathCount() == 4 )
	return path.getPathComponent(1).toString();
     return null;
  }
  protected String getSelectedItem() {
     TreePath path = tree.getSelectionPath();
     if ( path != null && path.getPathCount() == 4 )
        return path.getPathComponent(3).toString();
     return null;
  }
  protected String getSelectedItemGroup() {
     TreePath path = tree.getSelectionPath();
     if ( path != null && path.getPathCount() == 4 )
        return path.getPathComponent(2).toString();
     return null;
  }

   public void addLinkNodeSelectionListener(LinkNodeSelectionListener x) {
      selectionListeners.add(LinkNodeSelectionListener.class, x);
   }
   public void removeLinkNodeSelectionListener(LinkNodeSelectionListener x) {
      selectionListeners.remove(LinkNodeSelectionListener.class, x);
   }

   protected void fireSelectionAction(String node, String group,
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
}
