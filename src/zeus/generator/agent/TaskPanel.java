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
* TaskTablePanel.java
*
* The Container panel for the Task Table
*****************************************************************************/

package zeus.generator.agent;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.help.*;
import zeus.gui.fields.*;
import zeus.generator.*;

public class TaskPanel extends JPanel {

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected JTable            table;
  protected TaskModel         model;
  protected HelpWindow        helpWin;
  protected AgentEditor       editor;
  protected TaskToolBar       toolbar;
  protected GeneratorModel    genmodel;
  protected AgentGenerator    generator;

  public TaskPanel(AgentGenerator generator,
                   GeneratorModel genmodel,
                   AgentEditor editor,
                   String[] tasks,
                   String label) {

    this.editor = editor;
    this.genmodel = genmodel;
    this.generator = generator;

    model = new TaskModel(tasks,genmodel);
    model.addChangeListener(editor);

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;
    column = new TableColumn(TaskModel.TASK,12,
       new DefaultTableCellRenderer(),
       new DefaultCellEditor(new NameField()));
    column.setHeaderValue(model.getColumnName(TaskModel.TASK));
    tm.addColumn(column);
    column = new TableColumn(TaskModel.TYPE,24);
    column.setHeaderValue(model.getColumnName(TaskModel.TYPE));
    tm.addColumn(column);

    table = new JTable(model,tm);
    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);

    toolbar = new TaskToolBar();

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    setBackground(java.awt.Color.lightGray);

    TitledBorder border = (BorderFactory.createTitledBorder(label));
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(java.awt.Color.blue);
    setBorder(border);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setMinimumSize(new Dimension(160, 80));
    scrollPane.setPreferredSize(new Dimension(200, 80));
    table.setBackground(Color.white);
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(toolbar, gbc);
    add(toolbar);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(scrollPane, gbc);
    add(scrollPane);
  }

  protected void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  public String[] getData() {
     return model.getData();
  }

  protected String getSelectedTaskId() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)model.getValueAt(row,TaskModel.ID);
  }

  class TaskToolBar extends JToolBar implements ActionListener {
     protected JButton        newBtn;
     protected JButton        editBtn;
     protected JButton        deleteBtn;
     protected JToggleButton  helpBtn;
     protected HelpWindow     helpWin;
     protected JPopupMenu     popup;
     protected JMenu          primitiveMenu;
     protected JMenu          summaryMenu;
     protected JMenu          behaviourMenu;
     protected JMenu          scriptMenu;
     protected JMenuItem      primitiveMenuItem;
     protected JMenuItem      summaryMenuItem;
     protected JMenuItem      behaviourMenuItem;
     protected JMenuItem      scriptMenuItem;

     public TaskToolBar() {
       setBackground(java.awt.Color.lightGray);
       setBorder( new BevelBorder(BevelBorder.LOWERED ) );
       setFloatable(false);

       String sep = System.getProperty("file.separator");
       String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

       newBtn = new JButton(new ImageIcon(path + "new1.gif"));
       newBtn.setMargin(new Insets(0,0,0,0));
       add(newBtn);
       newBtn.setToolTipText("Create New Task");
       newBtn.addActionListener(this);

       editBtn = new JButton(new ImageIcon(path + "edit1.gif"));
       editBtn.setMargin(new Insets(0,0,0,0));
       add(editBtn);
       editBtn.setToolTipText("Edit this Task");
       editBtn.addActionListener(this);

       deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
       deleteBtn.setMargin(new Insets(0,0,0,0));
       add(deleteBtn);
       deleteBtn.setToolTipText("Delete this Task");
       deleteBtn.addActionListener(this);

       addSeparator();

       helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
       helpBtn.setMargin(new Insets(0,0,0,0));
       add(helpBtn);
       helpBtn.setToolTipText("Help");
       helpBtn.addActionListener(this);

       // ---- Popup Menu for Task Types ----
       popup = new JPopupMenu();
       popup.add(new JLabel ("Tasks"));
       popup.addSeparator();

       String name = AbstractTask.getTypeName(AbstractTask.PRIMITIVE);
       primitiveMenu = new JMenu(name,false);
       primitiveMenuItem = new JMenuItem("New " + name + " task");
       primitiveMenuItem.addActionListener(this);

       name = AbstractTask.getTypeName(AbstractTask.SUMMARY);
       summaryMenu = new JMenu(name,false);
       summaryMenuItem = new JMenuItem("New " + name + " task");
       summaryMenuItem.addActionListener(this);

       name = AbstractTask.getTypeName(AbstractTask.BEHAVIOUR);
       behaviourMenu = new JMenu(name,false);
       behaviourMenuItem = new JMenuItem("New " + name);
       behaviourMenuItem.addActionListener(this);

       name = AbstractTask.getTypeName(AbstractTask.SCRIPT);
       scriptMenu = new JMenu(name,false);
       scriptMenuItem = new JMenuItem("New " + name);
       scriptMenuItem.addActionListener(this);

       popup.add(primitiveMenu);
       popup.add(summaryMenu);
       popup.add(behaviourMenu);
       popup.add(scriptMenu);

       CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
                                               new EmptyBorder(5,5,5,5));
       popup.setBorder(cbr);
     }

     public void actionPerformed(ActionEvent e) {
       Object src = e.getSource();
       String primitive = AbstractTask.getTypeName(AbstractTask.PRIMITIVE);
       String summary   = AbstractTask.getTypeName(AbstractTask.SUMMARY);
       String behaviour = AbstractTask.getTypeName(AbstractTask.BEHAVIOUR);
       String script    = AbstractTask.getTypeName(AbstractTask.SCRIPT);


       if ( src == newBtn ) {
          String[] taskId = model.getData();
          Vector names = new Vector();
          for(int i = 0; i < taskId.length; i++ )
             names.addElement(genmodel.getTaskName(taskId[i]));

          primitiveMenu.removeAll();
          primitiveMenu.add(primitiveMenuItem);
          primitiveMenu.addSeparator();

          String[] items = genmodel.getTaskNames(AbstractTask.PRIMITIVE);
          Vector allNames = Misc.stringVector(items);
          Vector validNames = Misc.difference(allNames,names);

          JMenuItem mi;
          for(int i = 0; i < validNames.size(); i++ ) {
             mi = new JMenuItem((String)validNames.elementAt(i));
             mi.addActionListener(this);
             mi.setActionCommand(primitive);
             primitiveMenu.add(mi);
          }

          summaryMenu.removeAll();
          summaryMenu.add(summaryMenuItem);
          summaryMenu.addSeparator();

          items = genmodel.getTaskNames(AbstractTask.SUMMARY);
          allNames = Misc.stringVector(items);
          validNames = Misc.difference(allNames,names);

          for(int i = 0; i < validNames.size(); i++ ) {
             mi = new JMenuItem((String)validNames.elementAt(i));
             mi.addActionListener(this);
             mi.setActionCommand(summary);
             summaryMenu.add(mi);
          }

          behaviourMenu.removeAll();
          behaviourMenu.add(behaviourMenuItem);
          behaviourMenu.addSeparator();

          items = genmodel.getTaskNames(AbstractTask.BEHAVIOUR);
          allNames = Misc.stringVector(items);
          validNames = Misc.difference(allNames,names);

          for(int i = 0; i < validNames.size(); i++ ) {
             mi = new JMenuItem((String)validNames.elementAt(i));
             mi.addActionListener(this);
             mi.setActionCommand(behaviour);
             behaviourMenu.add(mi);
          }

          scriptMenu.removeAll();
          scriptMenu.add(scriptMenuItem);
          scriptMenu.addSeparator();

          items = genmodel.getTaskNames(AbstractTask.SCRIPT);
          allNames = Misc.stringVector(items);
          validNames = Misc.difference(allNames,names);

          for(int i = 0; i < validNames.size(); i++ ) {
             mi = new JMenuItem((String)validNames.elementAt(i));
             mi.addActionListener(this);
             mi.setActionCommand(script);
             scriptMenu.add(mi);
          }

          popup.pack();
          popup.show(newBtn,0,0);
       }
       else if ( src == primitiveMenuItem )
          model.addNewRow(primitive);
       else if ( src == summaryMenuItem )
          model.addNewRow(summary);
       else if ( src == behaviourMenuItem )
          model.addNewRow(behaviour);
       else if ( src == scriptMenuItem )
          model.addNewRow(script);

       else if ( e.getActionCommand().equals(primitive) )
          model.addNewRow(primitive,((JMenuItem)src).getText());
       else if ( e.getActionCommand().equals(summary) )
          model.addNewRow(summary,((JMenuItem)src).getText());
       else if ( e.getActionCommand().equals(behaviour) )
          model.addNewRow(behaviour,((JMenuItem)src).getText());
       else if ( e.getActionCommand().equals(script) )
          model.addNewRow(script,((JMenuItem)src).getText());

       else if ( src == editBtn ) {
          String id = getSelectedTaskId();
          if ( id == null ) return;
             generator.editTask(id);
       }
       else if ( src == deleteBtn ) {
          int row = table.getSelectedRow();
          if ( row == -1 ) {
             errorMsg(0);
             return;
          }
          model.removeRow(row);
       }
       
       else if ( src == helpBtn ) {
         if ( helpBtn.isSelected() ) {
            Point dispos = getLocation();
            helpWin = new HelpWindow(SwingUtilities.getRoot(this),dispos,
                                     "generator", "Task Declaration");
            helpWin.setSource(helpBtn);
         }
         else
            helpWin.dispose();
       }
     }
   }
}
