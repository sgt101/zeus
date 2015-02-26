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
* FactTreePane.java
*
* The Container panel for the Fact Hierarchy
*****************************************************************************/

package zeus.ontology.facts;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import zeus.util.SystemProps;
import zeus.gui.help.*;
import zeus.concepts.*;
import zeus.ontology.*;
import zeus.ontology.attributes.*;


public class FactTreePane extends JSplitPane {
  protected FactTreeUI          treeView;
  protected AttributeTablePanel tablePanel;

  public FactTreePane(OntologyEditor editor, OntologyDb ontologyDb) {
    super(JSplitPane.VERTICAL_SPLIT,true);
    tablePanel = new AttributeTablePanel(editor,ontologyDb);
    treeView   = new FactTreeUI(ontologyDb,tablePanel);

    JPanel innerPanel = new JPanel();
    TitledBorder border = new TitledBorder(new EtchedBorder(),
                                            "The Fact Hierarchy");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    innerPanel.setBorder(border);

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    innerPanel.setLayout(gridBagLayout);
    //innerPanel.setBackground(Color.lightGray);

    JToolBar factsToolbar = new FactTreeToolBar();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets(0,16,0,0);
    gridBagLayout.setConstraints(factsToolbar, gbc);
    innerPanel.add(factsToolbar);

    //Create the scroll pane and add the tree to it.
    JScrollPane treePane = new JScrollPane();
    treePane.getViewport().add(treeView);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(16,16,16,16);
    gridBagLayout.setConstraints(treePane, gbc);
    innerPanel.add(treePane);

    innerPanel.setPreferredSize(new Dimension(540,250));
    innerPanel.setMinimumSize(new Dimension(0,0));
    tablePanel.setPreferredSize(new Dimension(540,350));
    tablePanel.setMinimumSize(new Dimension(0,0));
    setTopComponent(innerPanel);
    setBottomComponent(tablePanel);
    setDividerSize(7);
  }

class FactTreeToolBar extends JToolBar implements ActionListener {
  protected HelpWindow    helpWin;
  protected JButton       newSBtn;
  protected JButton       newPBtn;
  protected JButton       deleteBtn;
  protected JButton       cutBtn;
  protected JButton       copyBtn;
  protected JButton       pasteBtn;

  protected JToggleButton helpBtn;
  protected JToggleButton showBtn;

  public FactTreeToolBar() {
    setBorder( new BevelBorder(BevelBorder.LOWERED ) );
    setFloatable(false);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "ontology" + sep;

    // New Sub Button
    newSBtn = new JButton(new ImageIcon(path + "newsub.gif"));
    newSBtn.setMargin(new Insets(0,0,0,0));
    add(newSBtn);
    newSBtn.setToolTipText("Add new child fact");
    newSBtn.addActionListener(this);

    // New Peer Button
    newPBtn = new JButton(new ImageIcon(path + "newpeer.gif"));
    newPBtn.setMargin(new Insets(0,0,0,0));
    add(newPBtn);
    newPBtn.setToolTipText("Add new peer fact");
    newPBtn.addActionListener(this);

    // Delete Button
    deleteBtn = new JButton(new ImageIcon(path + "delete.gif"));
    deleteBtn.setMargin(new Insets(0,0,0,0));
    add(deleteBtn);
    deleteBtn.setToolTipText("Delete");
    deleteBtn.addActionListener(this);

    addSeparator();

    // Cut Button
    cutBtn = new JButton(new ImageIcon(path + "cut.gif"));
    cutBtn.setMargin(new Insets(0,0,0,0));
    add(cutBtn);
    cutBtn.setToolTipText("Cut");
    cutBtn.addActionListener(this);

    // Copy Button
    copyBtn = new JButton(new ImageIcon(path + "copy.gif"));
    copyBtn.setMargin(new Insets(0,0,0,0));
    add(copyBtn);
    copyBtn.setToolTipText("Copy");
    copyBtn.addActionListener(this);

    // Paste Button
    pasteBtn = new JButton(new ImageIcon(path + "paste.gif"));
    pasteBtn.setMargin(new Insets(0,0,0,0));
    add(pasteBtn);
    pasteBtn.setToolTipText("Paste");
    pasteBtn.addActionListener(this);

    addSeparator();

    // Attribute Toggle Button
    showBtn = new JToggleButton(new ImageIcon(path + "expand.gif"), false);
    showBtn.setSelectedIcon(new ImageIcon(path + "collapse.gif"));
    showBtn.setMargin(new Insets(0,0,0,0));
    add(showBtn);
    showBtn.setToolTipText("Expand/Collapse hierarchy");
    showBtn.addActionListener(this);

    addSeparator();

    // Help Button
    helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
    helpBtn.setMargin(new Insets(0,0,0,0));
    add(helpBtn);
    helpBtn.setToolTipText("Help");
    helpBtn.addActionListener(this);
  }
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if ( src == newPBtn )
       treeView.addPeerNode();
    else if ( src == newSBtn )
       treeView.addSubNode();
    else if ( src == deleteBtn )
       treeView.removeNode();
    else if ( src == showBtn ) {
       if ( showBtn.isSelected() )
          treeView.expandRow();
       else
          treeView.collapseRow();
    }
    else if ( src == cutBtn )
       treeView.cutNode();
    else if ( src == copyBtn )
       treeView.copyNode();
    else if ( src == pasteBtn )
       treeView.pasteNode();
    else if ( src == helpBtn ) {
      if ( helpBtn.isSelected() ) {
         Point dispos = getLocation();
         helpWin = new HelpWindow(SwingUtilities.getRoot(helpBtn),
	    dispos, "ontology", "Fact Hierarchy");
         helpWin.setSource(helpBtn);
      }
      else
        helpWin.dispose();
    }
  }
}
}
