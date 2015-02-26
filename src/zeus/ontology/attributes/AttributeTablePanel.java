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
* AttributeTablePanel.java
*
* The Container panel for the Attribute Table
*****************************************************************************/

package zeus.ontology.attributes;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.event.*;

import zeus.util.*;
import zeus.gui.help.*;
import zeus.gui.fields.*;
import zeus.gui.editors.*;
import zeus.concepts.*;
import zeus.ontology.*;


public class AttributeTablePanel extends JPanel {
  protected JTable              table;
  protected TitledBorder        border;
  protected AttributeToolBar    toolbar;
  protected OntologyEditor      parent;

  protected String              currentName = null;
  protected boolean             showState   = false;
  protected AttributeTableModel model;
  protected String[][]          clipboard = null;

  static final String[] ERROR_MESSAGE = {
     "Please select a fact before\ncalling this operation",
     "Please select a row before\ncalling this operation"
  };

  public AttributeTablePanel(OntologyEditor editor, OntologyDb ontologyDb) {
    parent = editor;
    model = new AttributeTableModel(ontologyDb);

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;

    column = new TableColumn(AttributeTableModel.NAME,12,
       new NameCellRenderer(),
       new DefaultCellEditor(new NameField()));
    column.setHeaderValue(model.getColumnName(AttributeTableModel.NAME));
    tm.addColumn(column);
    column = new TableColumn(AttributeTableModel.TYPE,12,
       new ValidatingCellRenderer(model),
       new TypeCellEditor(ontologyDb));
    column.setHeaderValue(model.getColumnName(AttributeTableModel.TYPE));
    tm.addColumn(column);
    column = new TableColumn(AttributeTableModel.RESTRICTION,24,
       new ValidatingCellRenderer(model),
       new ExpressionCellEditor(model));
    column.setHeaderValue(model.getColumnName(AttributeTableModel.RESTRICTION));
    tm.addColumn(column);
    column = new TableColumn(AttributeTableModel.DEFAULT,24,
       new ValidatingCellRenderer(model),
       new ExpressionCellEditor(model));
    column.setHeaderValue(model.getColumnName(AttributeTableModel.DEFAULT));
    tm.addColumn(column);

    table = new JTable(model,tm);

    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(420, 100));
    table.setBackground(Color.white);

    setLayout(new BorderLayout());
    //setBackground(Color.lightGray);
    setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),
                                 new EmptyBorder(15,15,15,15)));

    JPanel innerPanel = new JPanel();
    border = (BorderFactory.createTitledBorder("Fact Attributes"));
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    innerPanel.setBorder(border);

    GridBagLayout gridBagLayout = new GridBagLayout();
    innerPanel.setLayout(gridBagLayout);
    GridBagConstraints gbc = new GridBagConstraints();

    toolbar = new AttributeToolBar();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,16,0,0);
    gridBagLayout.setConstraints(toolbar, gbc);
    innerPanel.add(toolbar);


    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(16,16,16,16);
    gridBagLayout.setConstraints(scrollPane, gbc);
    innerPanel.add(scrollPane);

    add("Center", innerPanel);
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
         ontologyDb,TypeTreeModel.FACT);

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

  class NameCellRenderer extends DefaultTableCellRenderer {
     public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

        // show attributes not specific to this class in red
        if ( model.isCellEditable(row,column) )
           setForeground(Color.black);
        else
           setForeground(Color.green);

        return super.getTableCellRendererComponent(table,value,isSelected,
           hasFocus, row, column);
     }
  }

class AttributeToolBar extends JToolBar implements ActionListener {
  protected HelpWindow    helpWin = null;
  protected JButton       newBtn;
  protected JButton       deleteBtn;
  protected JButton       cutBtn;
  protected JButton       copyBtn;
  protected JButton       pasteBtn;
  protected JToggleButton helpBtn;
  protected JToggleButton showBtn;

  public AttributeToolBar() {
    setBorder( new BevelBorder(BevelBorder.LOWERED ) );
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

    // Attribute Toggle Button
    showBtn = new JToggleButton(new ImageIcon(path + "row1.gif"), false);
    showBtn.setMargin(new Insets(0,0,0,0));
    showBtn.setSelectedIcon(new ImageIcon(path + "row2.gif"));
    add(showBtn);
    showBtn.setToolTipText("Toggle shown attributes");
    showBtn.addActionListener(this);

    addSeparator();

    // Help Button
    helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
    helpBtn.setMargin(new Insets(0,0,0,0));
    add(helpBtn);
    helpBtn.setToolTipText("Help");
    helpBtn.addActionListener(this);

    addSeparator();

  }
  public void activate(boolean state) {
     newBtn.setEnabled(state);
     deleteBtn.setEnabled(state);
     cutBtn.setEnabled(state);
     pasteBtn.setEnabled(state);
  }
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();

    if ( src == newBtn )
       addNewRow();
    else if ( src == deleteBtn )
       deleteSelected();
    else if ( src == showBtn )
       updateDisplay(showBtn.isSelected());
    else if ( src == copyBtn )
       clipboard = getSelectedRows();
    else if ( src == pasteBtn ) {
       if ( clipboard != null )
          model.addRows(clipboard);
    }
    else if ( src == cutBtn )
       clipboard = cutSelectedRows();
    else if ( src == helpBtn ) {
       if ( helpBtn.isSelected() ) {
          Point dispos = getLocation();
          helpWin = new HelpWindow(SwingUtilities.getRoot(helpBtn),
             dispos, "ontology", "Attribute Table");
          helpWin.setSource(helpBtn);
       }
       else {
          helpWin.dispose();
       }
    }
  }
}


  public void displayAttributes(String name) {
    currentName = name;
    updateDisplay(showState);
  }

  void updateDisplay(boolean state) {
    showState = state;
    if (showState) {
      border.setTitle("All Attributes of '" + currentName + "'");
      model.refreshAllAttributes(currentName);
    }
    else {
      border.setTitle("The Attributes specific to '" + currentName + "'");
      model.refreshAttributes(currentName);
    }
    toolbar.activate(model.isNodeEditable());
    invalidate();
    validate();
    repaint();
  }

  public void clear() {
    currentName = null;
    border.setTitle("No Attributes Shown");
    displayAttributes(currentName);
    validate();
    repaint();
  }

  AttributeTableModel getModel() { return model; }

  private void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  void addNewRow() {
    if ( currentName == null )  {
       errorMsg(0);
       return;
    }
    model.addNewRow();
  }

  void addRows(String[][] rows) {
    if ( currentName == null )  {
       errorMsg(0);
       return;
    }
    model.addRows(rows);
  }

  void deleteSelected() {
    if ( currentName == null )  {
       errorMsg(0);
       return;
    }
    else if ( table.getSelectedRow() == -1 ) {
       errorMsg(1);
       return;
    }
    int[] srows = table.getSelectedRows();
    model.deleteRows(srows);
  }

  String[][] getSelectedRows() {
    if ( currentName == null )  {
       errorMsg(0);
       return new String[0][0];
    }
    else if ( table.getSelectedRow() == -1 ) {
       errorMsg(1);
       return new String[0][0];
    }
    int[] srows = table.getSelectedRows();
    return model.getRows(srows);
  }

  String[][] cutSelectedRows() {
    if ( currentName == null )  {
       errorMsg(0);
       return new String[0][0];
    }
    else if ( table.getSelectedRow() == -1 ) {
       errorMsg(1);
       return new String[0][0];
    }
    String[][] data = getSelectedRows();
    deleteSelected();
    return data;
  }

}
