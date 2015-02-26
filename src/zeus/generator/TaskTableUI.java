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
* TaskTableUI.java
*
* The Viewer/Controller for displaying and editing the list of Known Tasks
*****************************************************************************/

package zeus.generator;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import zeus.gui.*;
import zeus.gui.fields.*;
import zeus.util.*;

public class TaskTableUI extends JPanel {
  protected JTable         table;
  protected TaskTableModel model;
  protected AgentGenerator generator;

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  public TaskTableUI(AgentGenerator generator, GeneratorModel genmodel) {
    this.generator = generator;

    model = new TaskTableModel(genmodel);
    setPreferredSize(new Dimension(240,120));

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;
    column = new TableColumn(TaskTableModel.TASK,12,
       new DefaultTableCellRenderer(),new DefaultCellEditor(new NameField()));
    column.setHeaderValue(model.getColumnName(TaskTableModel.TASK));
    tm.addColumn(column);
    column = new TableColumn(TaskTableModel.TYPE,24);
    column.setHeaderValue(model.getColumnName(TaskTableModel.TYPE));
    tm.addColumn(column);
   
    table = new JTable(model,tm);

    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    table.setBackground(java.awt.Color.white);
    
    //Add the scroll pane to this panel.
    setLayout(new BorderLayout());
    add(scrollPane,BorderLayout.CENTER);

    MouseListener ml = new MouseAdapter() {
       public void mouseClicked(MouseEvent e) {
          int row = table.rowAtPoint(e.getPoint());
          if ( row != -1 && table.isRowSelected(row) ) {
             if ( e.getClickCount() == 2 )
                editTask();
          }
       }
    };
    table.addMouseListener(ml);
  }

  void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }
  
  protected String getSelectedTaskName() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)model.getValueAt(row,TaskTableModel.TASK);
  }

  protected String getSelectedTaskId() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)model.getValueAt(row,TaskTableModel.ID);
  }
  
  public void addNewTask(int type) {
    model.addNewRow(type);
  }

  public void editTask() {
    String id = getSelectedTaskId();
    if ( id == null ) return;
    generator.editTask(id);
  }

  public void removeTask() {
    String id = getSelectedTaskId();
    if ( id == null ) return;
    generator.removeTask(id);
  }

  public void cloneTask() {
    String id = getSelectedTaskId();
    if ( id == null ) return;
    generator.cloneTask(id);
  }
  
  public void renameTask() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return;
    }
    model.setEditable(true);
    table.editCellAt(row,TaskTableModel.TASK);
    model.setEditable(false);
  }
}
