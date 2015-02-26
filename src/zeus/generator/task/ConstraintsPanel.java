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
* ConstraintsPanel.java
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

public class ConstraintsPanel extends JPanel {
  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected Task             task;
  protected JTable           constraintsTable;
  protected JTable           orderingTable;
  protected OrderingModel    orderingModel;
  protected ConstraintsModel constraintsModel;
  protected BasicFactModel   precondsModel;
  protected BasicFactModel   postcondsModel;

  public ConstraintsPanel(AgentGenerator generator,
                          GeneratorModel genmodel,
                          OntologyDb ontologyDb,
                          TaskEditor editor,
                          Task task,
                          BasicFactModel precondsModel,
                          BasicFactModel postcondsModel)  {

    this.task = task;
    this.precondsModel = precondsModel;
    this.postcondsModel = postcondsModel;

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    // Add the control panel
    ControlPanel controlPane =
       new ControlPanel(editor,"Task Constraints",false,true);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(controlPane,gbc);
    add(controlPane);

    JPanel orderingPanel = null;
    if ( task.isPrimitive() ) {
       orderingPanel = new JPanel();
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.BOTH;
       gbc.insets = new Insets(8,8,8,8);
       gridBagLayout.setConstraints(orderingPanel,gbc);
       add(orderingPanel);
    }

    // Add the panel containing the task's applicability constraints
    // to this panel.
    JPanel constraintsPanel = new JPanel();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
       gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(constraintsPanel,gbc);
    add(constraintsPanel);

    TableColumnModel tm;
    TableColumn column;
    ValidatingCellRenderer renderer;
    TitledBorder border;
    JToolBar toolbar;
    JScrollPane scrollPane;

    // Ordering panel info

    if ( task.isPrimitive() ) {
       orderingModel = new OrderingModel(precondsModel,((PrimitiveTask)task).getOrdering());
       orderingModel.addChangeListener(editor);

       tm = new DefaultTableColumnModel();
       renderer = new ValidatingCellRenderer(orderingModel);
       OrderingCellEditor cellEditor = new OrderingCellEditor();
       column = new TableColumn(OrderingModel.BEFORE,12,renderer,cellEditor);
       column.setHeaderValue(orderingModel.getColumnName(OrderingModel.BEFORE));
       tm.addColumn(column);
       column = new TableColumn(OrderingModel.AFTER,12,renderer,cellEditor);
       column.setHeaderValue(orderingModel.getColumnName(OrderingModel.AFTER));
       tm.addColumn(column);

       orderingTable = new JTable(orderingModel,tm);
       orderingTable.getTableHeader().setReorderingAllowed(false);
       orderingTable.setColumnSelectionAllowed(false);

       toolbar = new OrderingToolBar();

       border = BorderFactory.createTitledBorder("Preconditions Ordering Constraints");
       border.setTitlePosition(TitledBorder.TOP);
       border.setTitleJustification(TitledBorder.RIGHT);
       border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
       border.setTitleColor(Color.blue);
       orderingPanel.setBorder(border);

       gridBagLayout = new GridBagLayout();
       orderingPanel.setLayout(gridBagLayout);
       orderingPanel.setBackground(Color.lightGray);

       scrollPane = new JScrollPane(orderingTable);
       scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
       scrollPane.setPreferredSize(new Dimension(340,150));
       orderingTable.setBackground(Color.white);

       orderingTable.setMinimumSize(new Dimension(400,200));

       gbc = new GridBagConstraints();
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.NONE;
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.insets = new Insets(0,8,0,0);
       gridBagLayout.setConstraints(toolbar,gbc);
       orderingPanel.add(toolbar);

       gbc = new GridBagConstraints();
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.BOTH;
       gbc.weightx = gbc.weighty = 1;
       gbc.insets = new Insets(8,8,8,8);
       gridBagLayout.setConstraints(scrollPane,gbc);
       orderingPanel.add(scrollPane);
    }

    // Add constraintsPanel info;

    constraintsModel = new ConstraintsModel(
       precondsModel,postcondsModel,task.getConstraints());
    constraintsModel.addChangeListener(editor);

    ExpressionCellEditor cellEditor1 = new ExpressionCellEditor(constraintsModel);
    cellEditor1.addMouseListener(new SymMouseAction());
    renderer = new ValidatingCellRenderer(constraintsModel);
    tm = new DefaultTableColumnModel();
    column = new TableColumn(ConstraintsModel.CONSTRAINT,12,
       renderer, cellEditor1);
    column.setHeaderValue(constraintsModel.getColumnName(ConstraintsModel.CONSTRAINT));
    tm.addColumn(column);

    constraintsTable = new JTable(constraintsModel,tm);
    constraintsTable.getTableHeader().setReorderingAllowed(false);
    constraintsTable.setColumnSelectionAllowed(false);

    toolbar = new ConstraintsToolBar();

    border = BorderFactory.createTitledBorder("Task Applicability Constraints");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    constraintsPanel.setBorder(border);

    gridBagLayout = new GridBagLayout();
    constraintsPanel.setLayout(gridBagLayout);
    constraintsPanel.setBackground(Color.lightGray);

    scrollPane = new JScrollPane(constraintsTable);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(340,150));
    constraintsTable.setBackground(Color.white);

    constraintsTable.setMinimumSize(new Dimension(400,200));
//    constraintsTable.setPreferredSize(new Dimension(400,200));

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(toolbar,gbc);
    constraintsPanel.add(toolbar);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(scrollPane,gbc);
    constraintsPanel.add(scrollPane);
  }

  class OrderingToolBar extends JToolBar
                        implements ActionListener, OrderingSelector {

     protected HelpWindow     helpWin;
     protected JToggleButton  helpBtn;
     protected JButton        newBtn;
     protected JButton        deleteBtn;
     protected OrderingDialog dialog;

     public OrderingToolBar() {
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
     
        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));	
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);

        dialog = new OrderingDialog((Frame)SwingUtilities.getRoot(this),
                                    "Select ordering", precondsModel);
     }
  
     public void setEnabled(boolean set) {
        newBtn.setEnabled(set);
        deleteBtn.setEnabled(set);
     }

     public void orderingSelected(String left, String[] right) {
        orderingModel.addNewRows(left,right);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == newBtn ) {
           dialog.setLocationRelativeTo(newBtn);	
           dialog.display(this);
        }
        else if ( src == deleteBtn ) {
           if ( !isRowSelected(orderingTable) ) return;
           orderingModel.removeRows(orderingTable.getSelectedRows());
        }  
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 dispos, "generator", "Preconditions Ordering Table");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }

  class ConstraintsToolBar extends JToolBar implements ActionListener {

     protected HelpWindow    helpWin;
     protected JToggleButton helpBtn;
     protected JButton       newBtn;
     protected JButton       deleteBtn;
    
     public ConstraintsToolBar() {
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
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == newBtn ) {
           constraintsModel.addNewRow();
        }
        else if ( src == deleteBtn ) {
           if ( !isRowSelected(constraintsTable) ) return;
           constraintsModel.removeRows(constraintsTable.getSelectedRows());
        }  
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 dispos, "generator", "Task Applicability Constraints Table");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }

  class OrderingCellEditor extends DefaultCellEditor {
     public OrderingCellEditor() {
        super( new JComboBox() {
                      public void contentsChanged(ListDataEvent e) {
                         selectedItemReminder = null; 
                         super.contentsChanged( e );
               }
           }
        );
	setClickCountToStart(2);
     }

     public Component getTableCellEditorComponent(JTable table, Object value,
                                                  boolean isSelected, 
                                                  int row, int column) {

        JComboBox combo = ((JComboBox)editorComponent);
        Fact[] facts = precondsModel.getData();
        String[] items = new String[facts.length];
        for(int i = 0; i < facts.length; i++ )
           items[i] = facts[i].getId();

        if ( combo.getItemCount() != 0 )
	   combo.removeAllItems();
        for(int i = 0; i < items.length; i++ ) {
           combo.addItem(items[i]);
        }
        combo.setSelectedItem(value);
        return super.getTableCellEditorComponent(table,value,isSelected,row,column);
     }
  }

  class SymMouseAction extends MouseAdapter implements AttributeSelector {
     protected JTextComponent field = null;
     protected AttributeDialog dialog = null;
     protected AttributeTreeModel attributeTreeModel = null;

     public SymMouseAction() {
        attributeTreeModel = new AttributeTreeModel();
        attributeTreeModel.setFactModels(precondsModel,postcondsModel);
     }
     public void mouseClicked(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
           field = (JTextComponent)e.getSource();
           if ( dialog == null )
              dialog = new AttributeDialog(
	         (Frame)SwingUtilities.getRoot(field),attributeTreeModel);
           dialog.setLocationRelativeTo(field);
           dialog.display(this);
        }
     }
     public void attributeSelected(String attribute) {
        try {
	   Document doc = field.getDocument();
           int length = doc.getLength();
           AttributeSet a = doc.getDefaultRootElement().getAttributes();
           doc.insertString(length,attribute,a);
	}
	catch(BadLocationException e) {
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

  void save() {
     task.setConstraints(constraintsModel.getData());
     if ( task.isPrimitive() )
        ((PrimitiveTask)task).setOrdering(orderingModel.getData());
  }
}
