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
* AttributeDialog.java
*
* A pop-up dialog that prompts selection of a fact attribute
*****************************************************************************/

package zeus.generator.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

import zeus.concepts.*;
import zeus.util.SystemProps;
import zeus.util.Misc;

public class AttributeDialog extends JDialog implements ActionListener {
  protected JTree               tree;
  protected AttributeTreeModel  model;
  protected JButton             okBtn, cancelBtn;
  protected AttributeSelector   caller = null;

  public AttributeDialog(Frame parent, AttributeTreeModel model) {
    super(parent,"Select Attribute");
    initialize(model);
  }

  public AttributeDialog(Dialog parent, AttributeTreeModel model) {
    super(parent,"Select Attribute");
    initialize(model);
  }

  protected void initialize(AttributeTreeModel model) {
    this.model = model;

    JPanel pane = (JPanel)getContentPane();
    pane.setBorder(new EmptyBorder(10,10,10,10));
    pane.setBackground(Color.lightGray);
    pane.setLayout(new BorderLayout());

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
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    JScrollPane scrollpane = new JScrollPane();
    scrollpane.setPreferredSize(new Dimension(200,200));
    scrollpane.getViewport().add(tree);
    pane.add(scrollpane,BorderLayout.CENTER);

    JPanel controlpane = new JPanel();
    controlpane.setLayout(new GridLayout(1,2,10,10));
    pane.add(controlpane,BorderLayout.SOUTH);

    okBtn = new JButton("OK");
    okBtn.addActionListener(this);
    cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(this);
    controlpane.add(okBtn);
    controlpane.add(cancelBtn);

    setModal(true);
    setVisible(false);
  }

  public void actionPerformed(ActionEvent evt) {
    if ( evt.getSource() == okBtn ) {
       setVisible(false);
       String name = null;
       int length;
       TreePath path = tree.getSelectionPath();
       if ( path != null && (length = path.getPathCount()) != 0 ) {
          int i = 0;
	  do {
	     name = path.getPathComponent(i++).toString();
          }
          while( Misc.member(name,AttributeTreeModel.INVISIBLE_ITEMS) &&
	         i < length );

	  if ( Misc.member(name,AttributeTreeModel.INVISIBLE_ITEMS) )
             return;

	  for(; i < length; i++ )
             name += Fact.A_STR + path.getPathComponent(i);
          caller.attributeSelected(name);
       }
    }
    else if ( evt.getSource() == cancelBtn ) {
       setVisible(false);
    }
  }

  public void display(AttributeSelector caller) {
    this.caller = caller;
    model.refresh();
    pack();
    setVisible(true);
  }
}
