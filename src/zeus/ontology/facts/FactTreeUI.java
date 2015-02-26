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
* FactTreeUI.java
*
* The Viewer/Controller for displaying the Fact Hierarchy
*****************************************************************************/

package zeus.ontology.facts;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;


import zeus.util.SystemProps;
import zeus.concepts.*;
import zeus.ontology.*;
import zeus.ontology.attributes.*;
import zeus.gui.fields.*;


public class FactTreeUI extends JTree {

  protected AttributeTablePanel     dataPanel;
  protected FactTreeModel           model;
  protected DefaultMutableTreeNode  clipboard;

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "You cannot add a peer node to the root fact",
     /* 1 */ "Please select a node before\ncalling this operation",
     /* 2 */ "You cannot delete this fact",
     /* 3 */ "Renaming fact to a name that already\nexists in fact hierarchy",
     /* 4 */ "You cannot rename this fact"
  };

  public FactTreeUI(OntologyDb ontologyDb) {
    super(new FactTreeModel(ontologyDb));
    model = (FactTreeModel)getModel();
    setEditable(false);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "ontology" + sep;
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(new ImageIcon(path + "cloud.gif"));
    setCellRenderer(renderer);

    putClientProperty( "JTree.lineStyle", "Angled" );
  }


  public FactTreeUI(OntologyDb ontologyDb, AttributeTablePanel panel) {
    this(ontologyDb);

    dataPanel = panel;
    setEditable(true);
    SymSelectAction listener = new SymSelectAction();

    FactCellEditor editor = new FactCellEditor();
    editor.addCellEditorListener(listener);

    setCellEditor(new DefaultTreeCellEditor(this,(DefaultTreeCellRenderer)
       this.getCellRenderer(),editor));

    TreeSelectionModel selectionModel = getSelectionModel();
    selectionModel.addTreeSelectionListener(listener);
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
  }

  class FactCellEditor extends DefaultCellEditor {
     public FactCellEditor() {
        super(new NameField());
     }
     public boolean isCellEditable(EventObject event) {
        if ( event instanceof MouseEvent ) {
           MouseEvent e = (MouseEvent)event;
           TreePath path = getPathForLocation(e.getX(),e.getY());
           DefaultMutableTreeNode node =
	      (DefaultMutableTreeNode)path.getLastPathComponent();
           if ( model.isEditable(node.getUserObject().toString()) )
              return super.isCellEditable(event);
           else {
              // cancelCellEditing();
              errorMsg(4);
              return false;
           }
        }
        return super.isCellEditable(event);
     }
  }

  class SymSelectAction implements TreeSelectionListener,
                                   CellEditorListener {
     public void valueChanged(TreeSelectionEvent e) {
       dataPanel.displayAttributes(getSelectedNodeName());
     }
     public void editingStopped(ChangeEvent e) {
       dataPanel.displayAttributes(getSelectedNodeName());
     }
     public void editingCanceled(ChangeEvent e) {
     }
  }

  public void refresh() { model.refresh(); }

  // -- UPDATE METHODS ----------------------------------------------

  void addPeerNode() {
    DefaultMutableTreeNode lastItem = getSelectedNode();
    if ( lastItem == null )  {
       errorMsg(1);
       return;
    }
    if ( lastItem.isRoot() ) {
       errorMsg(0);
       return;
    }

    DefaultMutableTreeNode   parent;

    // Determine where to create the new node
    if ( lastItem != null )
       parent = (DefaultMutableTreeNode)lastItem.getParent();
    else
       parent = (DefaultMutableTreeNode)model.getRoot();

    model.addNewChild(parent);
  }

  void addSubNode() {
    DefaultMutableTreeNode parent = getSelectedNode();
    if ( parent == null )  {
       errorMsg(1);
       return;
    }
    model.addNewChild(parent);
    expandPath(getSelectionPath());
  }

  void removeNode() {
    DefaultMutableTreeNode lastItem = getSelectedNode();
    if ( lastItem == null )  {
       errorMsg(1);
       return;
    }
    else if ( !model.isEditable(lastItem.getUserObject().toString()) )  {
       errorMsg(2);
       return;
    }
    model.removeNode(lastItem);
    dataPanel.clear();
  }
  void cutNode() {
    DefaultMutableTreeNode lastItem = getSelectedNode();
    if ( lastItem == null )  {
       errorMsg(1);
       return;
    }
    else if ( !model.isEditable(lastItem.getUserObject().toString()) )  {
       errorMsg(2);
       return;
    }
    clipboard = model.cutNode(lastItem);
    dataPanel.clear();
  }
  void copyNode() {
    DefaultMutableTreeNode lastItem = getSelectedNode();
    if ( lastItem == null )  {
       errorMsg(1);
       return;
    }
    clipboard = model.copyNode(lastItem);
  }
  void pasteNode() {
    DefaultMutableTreeNode lastItem = getSelectedNode();
    if ( lastItem == null )  {
       errorMsg(1);
       return;
    }
    model.pasteNode(lastItem,clipboard);
    expandPath(getSelectionPath());
  }

  void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  void expandRow() {
     expandRow(getLeadSelectionRow());
  }
  void collapseRow() {
     collapseRow(getLeadSelectionRow());
  }

  // -- RETURN VIEW'S PROPERTIES METHODS ----------------------------

  DefaultMutableTreeNode getSelectedNode() {
    TreePath path = getSelectionPath();
    if (path != null)
       return (DefaultMutableTreeNode)path.getLastPathComponent();
    return null;
  }

  public String getSelectedNodeName() {
    String name = null;
    DefaultMutableTreeNode node = getSelectedNode();
    if (node != null)
       name = node.getUserObject().toString();
    return name;
  }

  public String[] getSelectedNodeNames() {
    TreePath[] paths = getSelectionPaths();
    if (paths == null || paths.length == 0 ) return new String[0];

    String[] names = new String[paths.length];
    DefaultMutableTreeNode node;
    for(int i = 0; i < names.length; i++ ) {
       node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
       names[i] = node.getUserObject().toString();
    }
    return names;
  }

}
