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



/****************************************************************************
* TaskLinkPanel.java
*
* Panel through which task constraints are entered
***************************************************************************/

package zeus.generator.task;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;
import zeus.generator.util.*;
import zeus.gui.fields.*;
import zeus.gui.editors.*;
import zeus.gui.help.*;

public class TaskLinkPanel extends JPanel {
  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected JTable                linkTable;
  protected TaskLinkModel         linkModel;
  protected TaskLinkBaseTreeModel baseModel;
  protected TaskLinkMainTreeModel mainModel;


  public TaskLinkPanel(ChangeListener changeListener, TaskNodePanel nodePanel,
                       GroupManager leftGroupManager,
                       GroupManager rightGroupManager)  {

    baseModel = new TaskLinkBaseTreeModel(leftGroupManager,rightGroupManager);
    mainModel = new TaskLinkMainTreeModel();
    nodePanel.addRenameListener(mainModel);
    nodePanel.addRenameListener(baseModel);
    TaskLinkBaseTreePanel basePanel = new TaskLinkBaseTreePanel(baseModel);
    TaskLinkMainTreePanel mainPanel = new TaskLinkMainTreePanel(mainModel);
    basePanel.addLinkRootSelectionListener(mainPanel);

    linkModel = new TaskLinkModel(leftGroupManager,rightGroupManager);
    nodePanel.addRenameListener(linkModel);
    linkModel.addChangeListener(changeListener);
    basePanel.addLinkNodeSelectionListener(linkModel);
    mainPanel.addLinkNodeSelectionListener(linkModel);

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    JPanel graphPanel = new JPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(graphPanel,gbc);
    add(graphPanel);

    // Add the panel containing the task's applicability constraints
    // to this panel.
    JPanel linkPanel = new JPanel();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(linkPanel,gbc);
    add(linkPanel);

    // create graph panel info
    TitledBorder border;
    border = BorderFactory.createTitledBorder("Effect-Precondition Links");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    graphPanel.setBorder(border);

    graphPanel.setLayout(new GridLayout(1,2,5,5));
    graphPanel.setBackground(Color.lightGray);
    graphPanel.add(basePanel);
    graphPanel.add(mainPanel);

    // Add linkPanel info;
    TableColumnModel tm;
    TableColumn column;
    JToolBar toolbar;
    JScrollPane scrollPane;

    tm = new DefaultTableColumnModel();
    column = new TableColumn(TaskLinkModel.LEFT,12);
    column.setHeaderValue(linkModel.getColumnName(TaskLinkModel.LEFT));
    tm.addColumn(column);
    column = new TableColumn(TaskLinkModel.RIGHT,12);
    column.setHeaderValue(linkModel.getColumnName(TaskLinkModel.RIGHT));
    tm.addColumn(column);

    linkTable = new JTable(linkModel,tm);
    linkTable.getTableHeader().setReorderingAllowed(false);
    linkTable.setColumnSelectionAllowed(false);

    toolbar = new TaskLinkToolBar();

    border = BorderFactory.createTitledBorder("Links");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    linkPanel.setBorder(border);

    gridBagLayout = new GridBagLayout();
    linkPanel.setLayout(gridBagLayout);
    linkPanel.setBackground(Color.lightGray);

    scrollPane = new JScrollPane(linkTable);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(340,150));
    linkTable.setBackground(Color.white);

    linkTable.setMinimumSize(new Dimension(400,200));

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(toolbar,gbc);
    linkPanel.add(toolbar);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(scrollPane,gbc);
    linkPanel.add(scrollPane);
  }

  class TaskLinkToolBar extends JToolBar implements ActionListener {

     protected HelpWindow    helpWin;
     protected JToggleButton helpBtn;
     protected JButton       addBtn;
     protected JButton       deleteBtn;

     public TaskLinkToolBar() {
        setBackground(Color.lightGray);
        setBorder( new BevelBorder(BevelBorder.LOWERED ) );
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        // New Button
        addBtn = new JButton(new ImageIcon(path + "add.gif"));
	addBtn.setMargin(new Insets(0,0,0,0));
        add(addBtn);
        addBtn.setToolTipText("New");
        addBtn.addActionListener(this);

        // Delete Button
        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	deleteBtn.setMargin(new Insets(0,0,0,0));	
        add(deleteBtn);
        deleteBtn.setToolTipText("Delete");
        deleteBtn.addActionListener(this);

        addSeparator();
     
        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));	
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);
     }
  
     public void setEnabled(boolean set) {
        addBtn.setEnabled(set);
        deleteBtn.setEnabled(set);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == addBtn ) {
           linkModel.addNewRow();
        }
        else if ( src == deleteBtn ) {
           if ( !isRowSelected(linkTable) ) return;
           linkModel.removeRows(linkTable.getSelectedRows());
        }  
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 dispos, "generator", "Summary Task Link Table");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }

  protected boolean isRowSelected(JTable table) {
     int row = table.getSelectedRow();
     if ( row == -1) {
        errorMsg(0);
        return false;
     }
     return true;
  }

  protected void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  TaskLink[] getLinks() {
     return linkModel.getData();
  }

  void reset(TaskNode node, TaskNode[] others, TaskLink[] links) {
     String mode = null;
     if      ( node.isBeginNode() ) mode = TaskLinkBaseTreeModel.EFFECTS;
     else if ( node.isEndNode()   ) mode = TaskLinkBaseTreeModel.PRECONDITIONS;
     else                           mode = TaskLinkBaseTreeModel.BOTH;

     baseModel.reset(mode,node.getName());
     mainModel.reset(others,node.getName());
     linkModel.reset(node.getName(),links);
  }
}
