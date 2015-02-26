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
* FactPanel.java
*
* The Container panel for the  Facts Table
*****************************************************************************/

package zeus.generator.util;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.help.*;
import zeus.gui.fields.*;

public class FactPanel extends JPanel {
  public static final int NONE          = 0;
  public static final int PRECONDITION  = 1;
  public static final int POSTCONDITION = 2;

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected FactToolBar    toolbar;
  protected AttributeTable attributePanel;
  protected AttributeModel aModel;
  protected JTable         table;
  protected FactModel      model;
  protected ChangeListener editor;
  protected Fact[]         clipboard = null;
  protected OntologyDb     ontologyDb;

  public FactPanel(OntologyDb ontologyDb, ChangeListener editor,
                   Fact[] facts, boolean isVariable, String label)  {
     this(ontologyDb,editor,facts,isVariable,NONE,label);
  }
  public FactPanel(OntologyDb ontologyDb, ChangeListener editor,
                   Fact[] facts, boolean isVariable, int modifierType,
		   String label)  {
     this(editor, modifierType, label,
        new FactModel(ontologyDb,new AttributeModel(),isVariable,modifierType,facts));

  }
  public FactPanel(ChangeListener editor, String label, FactModel model)  {
     this(editor,NONE,label,model);
  }
  
  public FactPanel(ChangeListener editor, int modifierType,
                   String label, FactModel model)  {

     this.ontologyDb = model.getOntologyDb();
     this.editor = editor;
     this.model = model;
     this.aModel = model.getAttributeModel();

     attributePanel = new AttributeTable(aModel);
     aModel.addChangeListener(editor);
     model.addChangeListener(editor);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     column = new TableColumn(FactModel.TYPE,12);
     column.setHeaderValue(model.getColumnName(FactModel.TYPE));
     tm.addColumn(column);
     column = new TableColumn(FactModel.INSTANCE,24,
        new DefaultTableCellRenderer(),
        new FactInstanceEditor());
     column.setHeaderValue(model.getColumnName(FactModel.INSTANCE));
     tm.addColumn(column);

     if ( modifierType != NONE ) {
        column = new TableColumn(FactModel.MODIFIERS,24,
           new FactModifiersCellRenderer(modifierType),
           new FactModifiersEditor(modifierType));
        column.setHeaderValue(model.getColumnName(FactModel.MODIFIERS));
        tm.addColumn(column);
     }

     table = new JTable(model,tm);
     table.getTableHeader().setReorderingAllowed(false);
     table.setColumnSelectionAllowed(false);

     ListSelectionModel selectionModel = table.getSelectionModel();
     selectionModel.addListSelectionListener(new SymListAction());

     JScrollPane scrollPane = new JScrollPane(table);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(400, 100));
     table.setBackground(Color.white);

     GridBagLayout gridBagLayout = new GridBagLayout();
     setLayout(gridBagLayout);
     setBackground(Color.lightGray);

     TitledBorder border = (BorderFactory.createTitledBorder(label));
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     setBorder(border);

     GridBagConstraints gbc = new GridBagConstraints();
     toolbar = new FactToolBar();

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar, gbc);
     add(toolbar);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane, gbc);
     add(scrollPane);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(attributePanel, gbc);
     add(attributePanel);
  }

  protected void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void           reset(Fact[] facts) { model.reset(facts);     }
  public Fact[]         getData()           { return model.getData(); }
  public FactModel      getFactModel()      { return model;           }
  public AttributeModel getAttributeModel() { return aModel;          }
  public AttributeTable getAttributeTable() { return attributePanel;  }

  protected Fact[] getSelectedRows()  {
    int[] srows = table.getSelectedRows();
    Fact[] data = new Fact[srows.length];
    for(int i = 0; i < srows.length; i++)
       data[i] = (Fact)model.getValueAt(srows[i],FactModel.FACT);
    return data;
  }

  protected Fact[] cutSelectedRows()  {
     Fact[] data = getSelectedRows();
     model.removeRows(table.getSelectedRows());
     return data;
  }
 
  protected void deleteSelectedRow()  {
     if ( !isRowSelected() ) return;
     cutSelectedRows();
  }

  protected boolean isRowSelected() {
     int row = table.getSelectedRow();
     if ( row == -1) {
        errorMsg(0);
        return false;
     }
     return true;
  }

  public void setToolBarState(boolean state) {
    toolbar.setEnabled(state);
  }

  class SymListAction implements ListSelectionListener {
     public void valueChanged(ListSelectionEvent e) {
        model.selectRow(table.getSelectedRow());
     }
  }

  public FactInstanceEditor newInstanceEditor() {
    return new FactInstanceEditor();
  }

  class FactInstanceEditor extends DefaultCellEditor {
     public FactInstanceEditor() {
        super(new NameField());
     }
     public Component getTableCellEditorComponent(JTable table, Object value,
                                                  boolean isSelected,
                                                  int row, int column) {
        String s = (String)value;
        s = s.substring(1);;
        return super.getTableCellEditorComponent(table,s,isSelected,row,column);
     }
  }

  class FactModifiersCellRenderer extends DefaultTableCellRenderer {
     protected int type;

     public FactModifiersCellRenderer(int type) {
        this.type = type;
     }
     public void setValue(Object value) {
        String s = "";
        int modifiers = ((Integer)value).intValue();
        if (type == PRECONDITION) {
           if ( Fact.isNegative(modifiers)   ) s += " NOT";
           if ( Fact.isReadOnly(modifiers)   ) s += " READ_ONLY";
           if ( Fact.isLocal(modifiers)      ) s += " LOCAL";
           if ( Fact.isReplaced(modifiers)   ) s += " REPLACED";
        }
        else {
           if ( Fact.isSideEffect(modifiers) ) s += "SIDE_EFFECT";
        }
        super.setValue(s);
     }
  }
  class FactModifiersEditor extends DefaultCellEditor
                            implements ActionListener, FactModifier {

    protected JButton button = new JButton("");
    protected int row, column;
    protected ModifierDialog dialog;
    protected int type;
    protected int modifier;
 
    public FactModifiersEditor(int modifierType) {
      super(new JTextField());
      setClickCountToStart(1);

      this.type = modifierType;

      dialog = new ModifierDialog((Frame)SwingUtilities.getRoot(table),
                                  "Set Modifiers");

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
         dialog.display(this,modifier,type);
      }
    }

    public void factModifiersChanged(int modifier) {
         model.setValueAt(new Integer(modifier),row,column);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
 
      this.row = row;
      this.column = column; 
      this.modifier = ((Integer)value).intValue();
      return button;
    }
  }

  class FactToolBar extends JToolBar
                    implements ActionListener,
                               FactSelector {

     protected FactDialog    factWin;
     protected HelpWindow    helpWin;
     protected JToggleButton helpBtn;
     protected JButton       newBtn;
     protected JButton       deleteBtn;
     protected JButton       cutBtn;
     protected JButton       copyBtn;
     protected JButton       pasteBtn;

     public FactToolBar() {
        setBackground(java.awt.Color.lightGray);
        setBorder( new BevelBorder(BevelBorder.LOWERED ) );
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        // New Button
        newBtn = new JButton(new ImageIcon(path + "new1.gif"));
	newBtn.setMargin(new Insets(0,0,0,0));
        add(newBtn);
        newBtn.setToolTipText("New");
        newBtn.addActionListener(this);

        // Delete Button
        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
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

        factWin = new FactDialog((Frame)SwingUtilities.getRoot(this),
                                 ontologyDb);
     }

     public void setEnabled(boolean set) {
        newBtn.setEnabled(set);
        deleteBtn.setEnabled(set);
        cutBtn.setEnabled(set);
        copyBtn.setEnabled(set);
        pasteBtn.setEnabled(set);
        helpBtn.setEnabled(set);
     }

     public void factSelected(String[] names)  {
        // Fact Selector callback to add new entries
        model.addNewRows(names);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == newBtn ) {
           factWin.setLocationRelativeTo(newBtn);
           factWin.display(this);
        }
        else if ( src == deleteBtn ) {
           deleteSelectedRow();
        }  
        else if ( src == copyBtn ) {
           clipboard = getSelectedRows();           
        }
        else if ( src == pasteBtn ) {
           model.addRows(clipboard);
           table.clearSelection();
        }
        else if ( src == cutBtn ) {
           clipboard = cutSelectedRows();
        }
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 dispos, "generator", "Fact Table");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }
}
