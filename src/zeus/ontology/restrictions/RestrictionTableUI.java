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
* RestrictionTableUI.java
*
* The Viewer/Controller for displaying and editing restrictions
*****************************************************************************/

package zeus.ontology.restrictions;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.ontology.*;
import zeus.gui.help.*;
import zeus.gui.editors.*;
import zeus.gui.fields.*;


public class RestrictionTableUI extends JPanel {
  static final String[] ERROR_MESSAGE = {
     "Please select a row before\ncalling this operation"
  };

  protected JTable                table;
  protected RestrictionTableModel model;
  protected String[][]            clipboard = null;

  public RestrictionTableUI(OntologyDb ontologyDb) {

    model = new RestrictionTableModel(ontologyDb);

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;

    column = new TableColumn(RestrictionTableModel.NAME,12,
       new DefaultTableCellRenderer(),
       new DefaultCellEditor(new NameField()));
    column.setHeaderValue(model.getColumnName(RestrictionTableModel.NAME));
    tm.addColumn(column);
    column = new TableColumn(RestrictionTableModel.TYPE,12,
       new ValidatingCellRenderer(model),
       new TypeCellEditor(ontologyDb));
    column.setHeaderValue(model.getColumnName(RestrictionTableModel.TYPE));
    tm.addColumn(column);
    column = new TableColumn(RestrictionTableModel.RESTRICTION,24,
       new ValidatingCellRenderer(model),
       new ExpressionCellEditor(model));
    column.setHeaderValue(model.getColumnName(RestrictionTableModel.RESTRICTION));
    tm.addColumn(column);

    table = new JTable(model,tm);

    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(420, 100));
    table.setBackground(Color.white);

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
   // setBackground(Color.lightGray);

    TitledBorder border = (BorderFactory.createTitledBorder("Known Restrictions"));
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    setBorder(border);

    GridBagConstraints gbc = new GridBagConstraints();

    RestrictionToolBar resToolbar = new RestrictionToolBar();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,16,0,0);
    gridBagLayout.setConstraints(resToolbar, gbc);
    add(resToolbar);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(16,16,16,16);
    gridBagLayout.setConstraints(scrollPane, gbc);
    add(scrollPane);
  }

  class TypeCellEditor extends DefaultCellEditor
                       implements ActionListener, TypeSelector {

    protected JButton button = new JButton("");
    protected int row, column;
    protected TypeDialog dialog;

    public TypeCellEditor(OntologyDb ontologyDb) {
      super(new JTextField());
      setClickCountToStart(2);
      dialog = new TypeDialog((Frame)SwingUtilities.getRoot(table),
         ontologyDb, TypeTreeModel.RESTRICTION);

      button.setBackground(Color.white);
      button.setHorizontalAlignment(JButton.LEFT);
      button.setBorderPainted(false);
      button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ( src == button ) {
         dialog.setLocationRelativeTo(button);
         fireEditingCanceled();
         dialog.display(this);
      }
    }
    public void typeSelected(String value) {
       model.setValueAt(value,row,column);
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {

      this.row = row;
      this.column = column;
      button.setText(value.toString());
      return button;
    }
  }

public class RestrictionToolBar extends JToolBar implements ActionListener {
  protected HelpWindow         helpWin;
  protected JButton            newBtn;
  protected JButton            deleteBtn;
  protected JButton            cutBtn;
  protected JButton            copyBtn;
  protected JButton            pasteBtn;
  protected JToggleButton      helpBtn;

  public RestrictionToolBar() {
    setBorder(new BevelBorder(BevelBorder.LOWERED));
    setFloatable(false);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "ontology" + sep;

    // New Button
    newBtn = new JButton(new ImageIcon(path + "new.gif"));
    newBtn.setMargin(new Insets(0,0,0,0));
    add(newBtn);
    newBtn.setToolTipText("New");
    newBtn.addActionListener(this);

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

    // Help Button
    helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
    helpBtn.setMargin(new Insets(0,0,0,0));
    add(helpBtn);
    helpBtn.setToolTipText("Help");
    helpBtn.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if ( src == newBtn )
       model.addNewRow();
    else if ( src == deleteBtn )
       deleteSelected();
    else if ( src == copyBtn )
       clipboard = getSelectedRows();
    else if ( src == pasteBtn )
       addRows(clipboard);
    else if ( src == cutBtn )
       clipboard = cutSelectedRows();
    else if ( src == helpBtn ) {
       if ( helpBtn.isSelected() ) {
          helpWin = new HelpWindow(SwingUtilities.getRoot(this),
	     getLocation(),"ontology","Restriction Table");
          helpWin.setSource(helpBtn);
       }
       else {
          helpWin.dispose();
       }
    }
  }
}
  private void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  void addRows(String[][] rows) {
    model.addRows(rows);
  }

  void deleteSelected() {
    if ( table.getSelectedRow() == -1 ) {
       errorMsg(1);
       return;
    }
    int[] srows = table.getSelectedRows();
    model.deleteRows(srows);
  }

  String[][] getSelectedRows() {
    if ( table.getSelectedRow() == -1 ) {
       errorMsg(1);
       return new String[0][0];
    }
    int[] srows = table.getSelectedRows();
    return model.getRows(srows);
  }

  String[][] cutSelectedRows() {
    String[][] result = getSelectedRows();
    if ( result.length > 0 ) deleteSelected();
    return result;
  }
}
