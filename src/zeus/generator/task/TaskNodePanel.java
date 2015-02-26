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



/***************************************************************************
* TaskNodePanel.java
*
* Panel through which task attributes are entered
***************************************************************************/

package zeus.generator.task;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.generator.*;
import zeus.generator.event.*;
import zeus.generator.util.*;
import zeus.gui.fields.*;
import zeus.gui.help.*;

public class TaskNodePanel extends JPanel {
  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Invalid name"
  };
  protected EventListenerList nameListeners = new EventListenerList();
  protected boolean           isConditionalNode;
  protected NameField         namefield;
  protected FactPanel         preconditionsPanel, postconditionsPanel;
  protected JPanel            factPanel, leftPanel, rightPanel;
  protected GroupManager      leftGroupManager, rightGroupManager;
  protected JComboBox         leftCombo, rightCombo;
  protected GroupToolBar      toolbar;
  protected Hashtable         nameTable;
  protected String            previousName = null;
  protected AttributeModel    preAttrModel, postAttrModel;
  protected AttributeTable    preAttrTable, postAttrTable;

  public TaskNodePanel(OntologyDb ontologyDb, ChangeListener parent)  {
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    // Add the panel show that shows the node name to this panel.
    JPanel namePanel = new JPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(namePanel,gbc);
    add(namePanel);

    // Add the panel containing the task's facts to this panel.
    factPanel = new JPanel();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gridBagLayout.setConstraints(factPanel,gbc);
    add(factPanel);

    // Cost Panel information
    TitledBorder border =
       BorderFactory.createTitledBorder("Node Name");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    namePanel.setBorder(border);

    gridBagLayout = new GridBagLayout();
    namePanel.setLayout(gridBagLayout);
    namePanel.setBackground(Color.lightGray);

    JLabel label = new JLabel("Name:");
    label.setToolTipText("Name of this node");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(label,gbc);
    namePanel.add(label);

    namefield = new NameField(20);
    namefield.addChangeListener(parent);
    namefield.addFocusListener(new SymFocusAction());

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(0,8,8,8);
    gridBagLayout.setConstraints(namefield,gbc);
    namePanel.add(namefield);

    // Add factPanel info
    factPanel.setLayout(new GridLayout(1,2,5,5));
    factPanel.setBackground(Color.lightGray);

    leftGroupManager = new GroupManager(ontologyDb, new AttributeModel(),
       Fact.VARIABLE, FactPanel.PRECONDITION, new Fact[0]);
    rightGroupManager = new GroupManager(ontologyDb, new AttributeModel(),
       Fact.VARIABLE, FactPanel.POSTCONDITION, new Fact[0]);

    preconditionsPanel = new FactPanel(parent, FactPanel.PRECONDITION,
       "Node Preconditions", leftGroupManager);
    postconditionsPanel = new FactPanel(parent, FactPanel.POSTCONDITION,
       "Node Effects", rightGroupManager);

    preAttrModel = preconditionsPanel.getAttributeModel();
    postAttrModel = postconditionsPanel.getAttributeModel();

    preAttrTable = preconditionsPanel.getAttributeTable();
    postAttrTable = postconditionsPanel.getAttributeTable();

    // for pre/post attribute renaming
    leftGroupManager.addRenameListener(preAttrModel);
    leftGroupManager.addRenameListener(postAttrModel);

    rightGroupManager.addRenameListener(preAttrModel);
    rightGroupManager.addRenameListener(postAttrModel);

    // for pre/post conditions renaming
    SymRenameAction symRenameAction = new SymRenameAction();
    leftGroupManager.addRenameListener(symRenameAction);
    rightGroupManager.addRenameListener(symRenameAction);

    nameTable = new Hashtable();

    // left and right panels
    leftPanel = new JPanel();
    gridBagLayout = new GridBagLayout();
    leftPanel.setLayout(gridBagLayout);
    leftPanel.setBackground(Color.lightGray);

    label = new JLabel("Group:");
    label.setToolTipText("Name of this preconditions group");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(label,gbc);
    leftPanel.add(label);

    leftCombo = new JComboBox(leftGroupManager.getComboBoxModel()) {
       public void contentsChanged(ListDataEvent e) {
          selectedItemReminder = null;
          super.contentsChanged(e);
       }
    };

    leftCombo.setEnabled(false);
    leftCombo.addActionListener(leftGroupManager);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,0,8);
    gridBagLayout.setConstraints(leftCombo,gbc);
    leftPanel.add(leftCombo);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(preconditionsPanel,gbc);
    leftPanel.add(preconditionsPanel);

    rightPanel = new JPanel();
    gridBagLayout = new GridBagLayout();
    rightPanel.setLayout(gridBagLayout);
    rightPanel.setBackground(Color.lightGray);

    label = new JLabel("Group:");
    label.setToolTipText("Name of this postconditions group");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(label,gbc);
    rightPanel.add(label);

    rightCombo = new JComboBox(rightGroupManager.getComboBoxModel()) {
       public void contentsChanged(ListDataEvent e) {
          selectedItemReminder = null;
          super.contentsChanged(e);
       }
    };

    rightCombo.setEditable(true);
    rightCombo.addActionListener(rightGroupManager);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(rightCombo,gbc);
    rightPanel.add(rightCombo);

    toolbar = new GroupToolBar(rightGroupManager);
    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,0,8);
    gridBagLayout.setConstraints(toolbar,gbc);
    rightPanel.add(toolbar);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(postconditionsPanel,gbc);
    rightPanel.add(postconditionsPanel);
  }

  protected class GroupToolBar extends JToolBar
                    implements ActionListener {

     protected HelpWindow    helpWin;
     protected JToggleButton helpBtn;
     protected JButton       newBtn;
     protected JButton       deleteBtn;
     protected JButton       copyBtn;
     protected GroupManager  groupManager;

     public GroupToolBar(GroupManager groupManager) {
        this.groupManager = groupManager;

        setBackground(Color.lightGray);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setMargin(new Insets(0,0,0,0));
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        // New Button
        newBtn = new JButton(new ImageIcon(path + "new1.gif"));
	newBtn.setMargin(new Insets(0,0,0,0));
        add(newBtn);
        newBtn.setToolTipText("New group");
        newBtn.addActionListener(this);

        // Delete Button
        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	deleteBtn.setMargin(new Insets(0,0,0,0));
        add(deleteBtn);
        deleteBtn.setToolTipText("Delete group");
        deleteBtn.addActionListener(this);

        // Clone Button
        copyBtn = new JButton(new ImageIcon(path + "copy.gif"));
	copyBtn.setMargin(new Insets(0,0,0,0));
        add(copyBtn);
        copyBtn.setToolTipText("Copy group");
        copyBtn.addActionListener(this);

        addSeparator();

        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);
     }

     public void setEnabled(boolean set) {
        newBtn.setEnabled(set);
        deleteBtn.setEnabled(set);
        copyBtn.setEnabled(set);
        helpBtn.setEnabled(set);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == newBtn )
           groupManager.newGroup();
        else if ( src == deleteBtn )
           groupManager.deleteGroup();
        else if ( src == copyBtn )
           groupManager.copyGroup();
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point pt = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 pt, "generator", "Task Node Conditions Group");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }

  protected class SymRenameAction implements RenameListener {
     public void nameChanged(RenameEvent e) {
        Object src = e.getSource();
        if ( src == leftGroupManager || src == rightGroupManager ) {
           nameTable.put(e.getOriginal(),e.getCurrent());
        }
     }
  }
  protected class SymFocusAction implements FocusListener, ActionListener {
     public void focusGained(FocusEvent e) {
     }
     public void focusLost(FocusEvent e) {
        this.update(e);
     }
     public void actionPerformed(ActionEvent e) {
        this.update(e);
     }
     public void update(AWTEvent e) {
        Object src = e.getSource();
        if ( src == namefield ) {
           if ( previousName != null ) {
              String name = namefield.getText();
              if ( name == null ) {
                 errorMsg(0);
                 namefield.setText(previousName);
              }
              name = name.trim();
              if ( name.equals("") ) {
                 errorMsg(0);
                 namefield.setText(previousName);
              }
              if ( !previousName.equals(name) ) {
                 nameTable.put(previousName,name);
                 fireRenameAction(namefield,previousName,name);
              }
           }
        }
     }
  }

  protected void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  GroupManager getPostconditionsManager() { return rightGroupManager; }
  GroupManager getPreconditionsManager()  { return leftGroupManager; }

  TaskNode getNode() {
     TaskNode node;
     if ( isConditionalNode )
        node = new ConditionalNode(namefield.getText());
     else
        node = new TaskNode(namefield.getText());
     node.setPreconditions(leftGroupManager.getManagerData());
     node.setPostconditions(rightGroupManager.getManagerData());
     return node;
  }

  Hashtable getNameTable() {
     return nameTable;
  }

  void reset(TaskNode node) {
     isConditionalNode = false;
     previousName = node.getName();
     namefield.setText(previousName);

     factPanel.removeAll();
     nameTable.clear();

     leftGroupManager.removeRelatedModel(rightGroupManager);
     rightGroupManager.removeRelatedModel(leftGroupManager);
     leftGroupManager.removeFactModelListener(rightGroupManager);
     leftGroupManager.resetManager(node.getAllPreconditions());
     rightGroupManager.resetManager(node.getAllPostconditions());

     postconditionsPanel.setToolBarState(true);
     rightGroupManager.setEditable(true);

     if ( node.isBeginNode() ) {
        namefield.setEditable(false);
        factPanel.add(rightPanel);
        toolbar.setEnabled(false);
        rightCombo.setEnabled(false);
        postAttrTable.setFactModel(rightGroupManager);
     }
     else if ( node.isEndNode() ) {
        namefield.setEditable(false);
        factPanel.add(leftPanel);
        preAttrTable.setFactModel(leftGroupManager);
     }
     else if ( node.isConditionalNode() ) {
        isConditionalNode = true;

        namefield.setEditable(true);
        factPanel.add(leftPanel);
        factPanel.add(rightPanel);
        toolbar.setEnabled(true);
        rightCombo.setEnabled(true);
        postconditionsPanel.setToolBarState(false);
        rightGroupManager.setEditable(false);
	leftGroupManager.addFactModelListener(rightGroupManager);

        preAttrTable.setFactModel(leftGroupManager);
        postAttrTable.setFactModel(leftGroupManager);

        rightGroupManager.resetManager(node.getAllPostconditions(),
           node.getPreconditions());
     }
     else {
        namefield.setEditable(true);
        factPanel.add(leftPanel);
        factPanel.add(rightPanel);
        toolbar.setEnabled(false);
        rightCombo.setEnabled(false);

        leftGroupManager.addRelatedModel(rightGroupManager);
        rightGroupManager.addRelatedModel(leftGroupManager);

        preAttrTable.setFactModels(leftGroupManager,rightGroupManager);
        postAttrTable.setFactModels(leftGroupManager,rightGroupManager);
     }
  }

  public void addRenameListener(RenameListener x) {
     nameListeners.add(RenameListener.class, x);
     leftGroupManager.addRenameListener(x);
     rightGroupManager.addRenameListener(x);
  }
  public void removeRenameListener(RenameListener x) {
     nameListeners.remove(RenameListener.class, x);
     leftGroupManager.removeRenameListener(x);
     rightGroupManager.removeRenameListener(x);
  }

  protected void fireRenameAction(Object src, Object prev, Object curr) {
     RenameEvent e = new RenameEvent(this,src,prev,curr);
     Object[] listeners = nameListeners.getListenerList();
     for(int i = listeners.length-2; i >= 0; i -= 2) {
        if (listeners[i] == RenameListener.class) {
           RenameListener l = (RenameListener)listeners[i+1];
           l.nameChanged(e);
        }
     }
  }
}
