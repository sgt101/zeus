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
* AgentTableUI.java
*
* The Viewer/Controller for displaying and editing the list of Known Agents
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

public class AgentTableUI extends JPanel {
  protected JTable          table;
  protected AgentTableModel model;
  protected AgentGenerator  generator;

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  public AgentTableUI(AgentGenerator generator, GeneratorModel genmodel) {
    this.generator = generator;

    model = new AgentTableModel(genmodel);

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;
    column = new TableColumn(AgentTableModel.AGENT,12,
       new DefaultTableCellRenderer(),new DefaultCellEditor(new NameField()));
    column.setHeaderValue(model.getColumnName(AgentTableModel.AGENT));
    tm.addColumn(column);
    column = new TableColumn(AgentTableModel.TASK,24,
       new AgentTableCellRenderer(),
       new AgentTableCellEditor(genmodel));
    column.setHeaderValue(model.getColumnName(AgentTableModel.TASK));
    tm.addColumn(column);

    table = new JTable(model,tm);
    setPreferredSize(new Dimension(240,120));

    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    table.setBackground(Color.white);

    // Add the scroll pane to this panel.
    setLayout(new BorderLayout());
    add(scrollPane,BorderLayout.CENTER);

    MouseListener ml = new MouseAdapter() {
       public void mouseClicked(MouseEvent e) {
          int row = table.rowAtPoint(e.getPoint());
          if ( row != -1 && table.isRowSelected(row) ) {
             if ( e.getClickCount() == 2 )
                editAgent();
          }
       }
    };
    table.addMouseListener(ml);
  }

  void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  protected String getSelectedAgentName() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)model.getValueAt(row,AgentTableModel.AGENT);
  }

  protected String getSelectedAgentId() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)model.getValueAt(row,AgentTableModel.ID);
  }

  public void addNewAgent() {
    model.addNewRow();
  }

  public void editAgent() {
    String id = getSelectedAgentId();
    if ( id == null ) return;
    generator.editAgent(id);
  }

  public void removeAgent() {
    String id = getSelectedAgentId();
    if ( id == null ) return;
    generator.removeAgent(id);
  }

  public void cloneAgent() {
    String id = getSelectedAgentId();
    if ( id == null ) return;
    generator.cloneAgent(id);
  }

  public void renameAgent() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return;
    }
    model.setEditable(true);
    table.editCellAt(row,AgentTableModel.AGENT);
    model.setEditable(false);
  }

  public void modifyTaskList() {
    int row = table.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return;
    }
    model.setEditable(true);
    table.editCellAt(row,AgentTableModel.TASK);
    model.setEditable(false);
  }

  class AgentTableCellEditor extends DefaultCellEditor
                             implements ActionListener {

    protected JButton button = new JButton("");
    protected GeneratorModel genmodel;
    protected int row, column;
    protected MultipleSelectionDialog dialog;

    public AgentTableCellEditor(GeneratorModel genmodel) {
      super(new JTextField());
      this.genmodel = genmodel;

      setClickCountToStart(1);

      dialog = new MultipleSelectionDialog(
         (Frame)SwingUtilities.getRoot(table),"Select Tasks");

      button.setBackground(Color.white);
      button.setBorderPainted(false);
      button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ( src == button ) {
         dialog.setLocationRelativeTo(button);
         fireEditingCanceled();
         Object[] items = dialog.getSelection();
         model.setValueAt(Misc.stringArray(items),row,column);
      }
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {

      this.row = row;
      this.column = column;
      String[] data = genmodel.getTaskNames();
      dialog.setListData(data);
      dialog.setSelection((String[])value);
      button.setText("Click to edit");
      return button;
    }
  }

  class AgentTableCellRenderer extends DefaultTableCellRenderer {
     public void setValue(Object value) {
        String[] items = (String[])value;
        String text = Misc.concat(items);
        super.setValue(text);
     }
  }
}
