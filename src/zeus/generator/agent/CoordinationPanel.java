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
* CoordinationPanel.java
*
* Panel through which coordination protocol are entered
* (In future it will also allow them to be defined)
***************************************************************************/

package zeus.generator.agent;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.generator.*;
import zeus.generator.util.*;
import zeus.ontology.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.help.*;
import zeus.gui.*;
/** 
    CoordinationPanel is the thing that you interact with in the 
    zeus.generator.AgentBuilder to arrange co-ordination methods for agents
    */
public class CoordinationPanel extends JPanel {
  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected ControlPanel     controlPane;
  protected AgentDescription agent;
  protected ProtocolModel    protocolModel;
  protected JTable           protocolTable;
  protected StrategyToolBar  strategyToolBar;
  protected AttributeModel   attributeModel;
  protected JTable           strategyTable;
  protected StrategyModel    strategyModel;
  protected Fact[]           clipboard = null;
  protected OntologyDb       ontologyDb;
  protected GeneratorModel   genmodel;
  protected AgentEditor      editor;

  public CoordinationPanel(AgentGenerator generator,
                           GeneratorModel genmodel,
                           OntologyDb ontologyDb,
                           AgentEditor editor,
                           AgentDescription agent)  {

     this.agent = agent;
     this.genmodel = genmodel;
     this.ontologyDb = ontologyDb;
     this.editor = editor;

     GridBagLayout gridBagLayout = new GridBagLayout();
     GridBagConstraints gbc = new GridBagConstraints();
     setLayout(gridBagLayout);
     setBackground(Color.lightGray);

     setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
     controlPane = new ControlPanel(editor,"Agent Coordination Panel",false,false);
	
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.HORIZONTAL;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(controlPane,gbc);
     add(controlPane);

     // Add the panel containing the agent's protocol to the panel.
     JPanel protocolPanel = new JPanel();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(8,8,8,8);
     gbc.weightx = gbc.weighty = 1;
     gridBagLayout.setConstraints(protocolPanel,gbc);
     add(protocolPanel);
	
     // create Strategies info
     JPanel strategyPanel = new JPanel();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(strategyPanel,gbc);
     add(strategyPanel);

     // Create Protocol info
     AttributeModel attributeModel = new AttributeModel();
     AttributeTable attributePanel = new AttributeTable(attributeModel);
     strategyModel = new StrategyModel(genmodel,ontologyDb,attributeModel);

     protocolModel = new ProtocolModel(ontologyDb,strategyModel,agent.getProtocols());

     protocolModel.addChangeListener(editor);
     attributeModel.addChangeListener(editor);
     strategyModel.addChangeListener(editor);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     column = new TableColumn(ProtocolModel.TYPE,24);
     column.setHeaderValue(protocolModel.getColumnName(ProtocolModel.TYPE));
     tm.addColumn(column);
     column = new TableColumn(ProtocolModel.PROTOCOL,24,
        new FriendlyRenderer(), new DefaultCellEditor(new JTextField()));
     column.setHeaderValue(protocolModel.getColumnName(ProtocolModel.PROTOCOL));
     tm.addColumn(column);
     JCheckBox checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     DefaultCellEditor cellEditor = new DefaultCellEditor(checkbox);
     checkbox.addItemListener(new SymItemAction());
     column = new TableColumn(ProtocolModel.STATE,8,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(protocolModel.getColumnName(ProtocolModel.STATE));
     tm.addColumn(column);

     protocolTable = new JTable(protocolModel,tm);
     protocolTable.getTableHeader().setReorderingAllowed(false);
     protocolTable.setColumnSelectionAllowed(false);

     ListSelectionModel selectionModel = protocolTable.getSelectionModel();
     selectionModel.addListSelectionListener(new SymListAction1());

     TitledBorder border = BorderFactory.createTitledBorder("Coordination Protocols");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     protocolPanel.setBorder(border);

     gridBagLayout = new GridBagLayout();
     protocolPanel.setLayout(gridBagLayout);
     protocolPanel.setBackground(Color.lightGray);

     JToolBar toolbar = new ProtocolToolBar();
     gbc = new GridBagConstraints();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     protocolPanel.add(toolbar);

     JScrollPane scrollPane = new JScrollPane(protocolTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setMinimumSize(new Dimension(160,80));
     scrollPane.setPreferredSize(new Dimension(200,80));
     protocolTable.setBackground(Color.white);

     gbc = new GridBagConstraints();
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     protocolPanel.add(scrollPane);

     // Create strategy panel info
     gridBagLayout = new GridBagLayout();
     strategyPanel.setLayout(gridBagLayout);
     strategyPanel.setBackground(Color.lightGray);

     tm = new DefaultTableColumnModel();
     JCheckBox checkbox1 = new JCheckBox();
     checkbox1.setHorizontalAlignment(JCheckBox.CENTER);
     column = new TableColumn(StrategyModel.MODE,4,
        new CheckBoxCellRenderer(), new DefaultCellEditor(checkbox1));
     column.setHeaderValue(strategyModel.getColumnName(StrategyModel.MODE));
     tm.addColumn(column);

     column = new TableColumn(StrategyModel.TYPE,12);
     column.setHeaderValue(strategyModel.getColumnName(StrategyModel.TYPE));
     tm.addColumn(column);

     column = new TableColumn(StrategyModel.AGENTS,24,
        new StringArrayCellRenderer(), new AgentCellEditor());
     column.setHeaderValue(strategyModel.getColumnName(StrategyModel.AGENTS));
     tm.addColumn(column);

     column = new TableColumn(StrategyModel.RELATIONS,24,
        new StringArrayCellRenderer(), new RelationsCellEditor());
     column.setHeaderValue(strategyModel.getColumnName(StrategyModel.RELATIONS));
     tm.addColumn(column);

     column = new TableColumn(StrategyModel.STRATEGY,24,
        new FriendlyRenderer(), new StrategyCellEditor());
     column.setHeaderValue(strategyModel.getColumnName(StrategyModel.STRATEGY));
     tm.addColumn(column);

     column = new TableColumn(StrategyModel.PARAMETERS,24,
        new HashtableCellRenderer(), new HashtableCellEditor());
     column.setHeaderValue(strategyModel.getColumnName(StrategyModel.PARAMETERS));
     tm.addColumn(column);

     strategyTable = new JTable(strategyModel,tm);
     strategyTable.getTableHeader().setReorderingAllowed(false);
     strategyTable.setColumnSelectionAllowed(false);

     selectionModel = strategyTable.getSelectionModel();
     selectionModel.addListSelectionListener(new SymListAction2());

     scrollPane = new JScrollPane(strategyTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(400,100));
     strategyTable.setBackground(Color.white);

     border = (BorderFactory.createTitledBorder("Coordination Strategies"));
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     strategyPanel.setBorder(border);

     gbc = new GridBagConstraints();
     strategyToolBar = new StrategyToolBar();

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.weightx = gbc.weighty = 0;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(strategyToolBar,gbc);
     strategyPanel.add(strategyToolBar);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     strategyPanel.add(scrollPane);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(attributePanel,gbc);
     strategyPanel.add(attributePanel);

     strategyToolBar.setEnabled(false);
  }

  class SymItemAction implements ItemListener {
     public void itemStateChanged(ItemEvent e) {
        strategyToolBar.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        repaint(); 
     }
  }

  class StrategyCellEditor extends DefaultCellEditor {
     public StrategyCellEditor() {
        super( new JComboBox() {
                      public void contentsChanged(ListDataEvent e) {
                         selectedItemReminder = null;
                         super.contentsChanged(e);
                      }
               }
        );
        JComboBox combo = (JComboBox)editorComponent;
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
           public Component getListCellRendererComponent(JList list,
              Object value, int index, boolean isSelected, boolean hasFocus)  {

              if ( value != null )
                 value = SystemProps.getProperty("friendly.name." + value,
                    (String)value);
              return super.getListCellRendererComponent(
                 list, value, index, isSelected, hasFocus);
           }
        };
        combo.setRenderer(renderer);
        setClickCountToStart(2);
     }
     public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        Vector List = null;
        JComboBox combo = (JComboBox)editorComponent;
        if ( combo.getModel().getSize() != 0 )
           combo.removeAllItems();
        String type = (String)protocolModel.getValueAt(
           protocolTable.getSelectedRow(),ProtocolModel.TYPE);
        if ( type.equals(ProtocolInfo.INITIATOR) )
           List = StrategyModel.INITIATOR_STRATEGY_LIST;
        else
           List = StrategyModel.RESPONDENT_STRATEGY_LIST;
        for(int i = 0; i < List.size(); i++ )
           combo.addItem(List.elementAt(i));
        return super.getTableCellEditorComponent(table, value, isSelected,
                                                 row, column);

     }
  }

  class AgentCellEditor extends DefaultCellEditor
                             implements ActionListener {

    protected JButton button = new JButton("");
    protected int row, column;
    protected EditableMultipleSelectionDialog dialog;
    protected Object value;

    public AgentCellEditor() {
      super(new JTextField());

      setClickCountToStart(2);

      dialog = new EditableMultipleSelectionDialog(
         (Frame)SwingUtilities.getRoot(editor),"Select Agents");

      button.setBackground(Color.white);
      button.setHorizontalAlignment(JButton.LEFT);
      button.setBorderPainted(false);
      button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ( src == button ) {
         String[] data = genmodel.getAgentNames();
         Vector items = Misc.stringVector(data);
         items.removeElement(editor.getObjectName());
         data = Misc.stringArray(items);
         dialog.setListData(data);
         dialog.setSelection((String[])value);
         dialog.setLocationRelativeTo(button);
         fireEditingCanceled();
         Object[] output = dialog.getSelection();
         strategyModel.setValueAt(Misc.stringArray(output),row,column);
      }
      repaint(); 
    }
    
    
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
      this.row = row;
      this.column = column;
      this.value = value;
      return button;
    }
  }

  class RelationsCellEditor extends DefaultCellEditor
                             implements ActionListener {

    protected JButton button = new JButton("");
    protected int row, column;
    protected MultipleSelectionDialog dialog;
    protected Object value;

    public RelationsCellEditor() {
      super(new JTextField());

      setClickCountToStart(2);

      dialog = new MultipleSelectionDialog(
         (Frame)SwingUtilities.getRoot(editor), "Select Relations");
      dialog.setListData(Misc.stringArray(AcquaintanceModel.RELATIONS_LIST));

      button.setBackground(Color.white);
      button.setHorizontalAlignment(JButton.LEFT);
      button.setBorderPainted(false);
      button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ( src == button ) {
         dialog.setSelection((String[])value);
         dialog.setLocationRelativeTo(button);
         fireEditingCanceled();
         Object[] items = dialog.getSelection();
         strategyModel.setValueAt(Misc.stringArray(items),row,column);
      }
      repaint(); 
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
      this.row = row;
      this.column = column;
      this.value = value;
      return button;
    }
  }

  class StringArrayCellRenderer extends DefaultTableCellRenderer {
     public void setValue(Object value) {
        String text = Misc.concat((String[])value);
        super.setValue(text);
     }
  }

  class FriendlyRenderer extends DefaultTableCellRenderer {
     public void setValue(Object value) {
        if ( value == null )
           super.setValue(value);
        else {
           String name = SystemProps.getProperty("friendly.name." + value,
              (String)value);
           super.setValue(name);
        }
     }
  }


  class HashtableCellRenderer extends DefaultTableCellRenderer {
     public void setValue(Object input) {
        if ( input == null )
           super.setValue(input);
        else {
           Hashtable table = (Hashtable)input;
           Enumeration enum = table.keys();
           String key, value;
           String result = "";
           while( enum.hasMoreElements() ) {
              key = (String)enum.nextElement();
              value = (String)table.get(key);
              result += key + "=" + value + " ";
           }
           super.setValue(result.trim());
        }
     }
  }

  class HashtableCellEditor extends DefaultCellEditor
                            implements ActionListener, ParameterChooser {

    protected JButton button = new JButton("");
    protected int row, column;
    protected ParameterDialog dialog;
    protected Hashtable value;

    public HashtableCellEditor() {
      super(new JTextField());
      setClickCountToStart(1);

      dialog = new ParameterDialog((Frame)SwingUtilities.getRoot(editor),
                                   "Set Parameters");

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
         dialog.display(this,value);
         repaint(); 
      }
      repaint(); 
    }

    public void parametersChanged(Hashtable input) {
         strategyModel.setValueAt(input,row,column);
         repaint(); 
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {

      this.row = row;
      this.column = column;
      this.value = (Hashtable)value;
      repaint(); 
      return button;
    }
  }


  class StrategyToolBar extends JToolBar
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
     protected JButton       allBtn;

     public StrategyToolBar() {
        setBackground(Color.lightGray);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        // New Button
        newBtn = new JButton(new ImageIcon(path + "new1.gif"));
	newBtn.setMargin(new Insets(0,0,0,0));
        add(newBtn);
        newBtn.setToolTipText("New");
        newBtn.addActionListener(this);

        // All Button
        allBtn = new JButton(new ImageIcon(path + "all.gif"));
	allBtn.setMargin(new Insets(0,0,0,0));
        add(allBtn);
        allBtn.setToolTipText("Set list of agents to any");
        allBtn.addActionListener(this);

        addSeparator();

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
        allBtn.setEnabled(set);
        deleteBtn.setEnabled(set);
        cutBtn.setEnabled(set);
        copyBtn.setEnabled(set);
        pasteBtn.setEnabled(set);
     }

     public void factSelected(String[] names)  {
        // Fact Selector callback to add new entries
        strategyModel.addNewRows(names);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == newBtn ) {
           factWin.setLocationRelativeTo(newBtn);
           factWin.display(this);
        }
        else if ( src == allBtn ) {
           allowAllAgents();
        }
        else if ( src == deleteBtn ) {
           deleteSelectedRow();
        }
        else if ( src == copyBtn ) {
           clipboard = getSelectedRows();
        }
        else if ( src == pasteBtn ) {
           strategyModel.addRows(clipboard);
           strategyTable.clearSelection();
        }
        else if ( src == cutBtn ) {
           clipboard = cutSelectedRows();
        }
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 dispos, "generator", "Activity Coord-2");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
        repaint();
     }
  }

  protected class CheckBoxCellRenderer extends JCheckBox
                                       implements TableCellRenderer,
                                       java.io.Serializable {

     public CheckBoxCellRenderer() {
        setHorizontalAlignment(JCheckBox.CENTER);
     }

     public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        if ( value != null )
           this.setSelected(((Boolean)value).booleanValue());
        return this;
     }
  }

  protected class ProtocolToolBar extends JToolBar
                                  implements ActionListener {

     protected JToggleButton helpBtn;
     protected JButton       clearBtn;
     protected JButton       allBtn;
     protected HelpWindow    helpWin;

     public ProtocolToolBar() {
        setBackground(Color.lightGray);
        setBorder( new BevelBorder(BevelBorder.LOWERED ) );
        setFloatable(false);
        String path = SystemProps.getProperty("gif.dir") + "generator" +
           System.getProperty("file.separator");

        // clear Button
        clearBtn = new JButton(new ImageIcon(path + "clear.gif" ));
	clearBtn.setMargin(new Insets(0,0,0,0));
        add(clearBtn);
        clearBtn.setToolTipText("Clear all protocols");
        clearBtn.addActionListener(this);

        // All Button
        allBtn = new JButton(new ImageIcon(path + "all.gif"));
	allBtn.setMargin(new Insets(0,0,0,0));
        add(allBtn);
        allBtn.setToolTipText("Select all protocols");
        allBtn.addActionListener(this);

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
        if ( src == clearBtn ) {
           for(int i = 0; i < protocolModel.getRowCount(); i++ )
              protocolModel.setValueAt(Boolean.FALSE,i,ProtocolModel.STATE);
        }
        else if ( src == allBtn ) {
           for(int i = 0; i < protocolModel.getRowCount(); i++ )
              protocolModel.setValueAt(Boolean.TRUE,i,ProtocolModel.STATE);
        }
        else if ( src == helpBtn ) {
           if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                                       dispos, "generator",
                                       "Activity Coord-1");
              helpWin.setSource(helpBtn);
           }
           else {
              helpWin.dispose();
           }
        }
     } 
  }

  protected void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  protected void allowAllAgents() {
     if ( !isRowSelected() ) return;
     int row = strategyTable.getSelectedRow();
     strategyModel.setValueAt(StrategyModel.ALL,row,StrategyModel.AGENTS);
  }
  protected Fact[] getSelectedRows()  {
    int[] rows = strategyTable.getSelectedRows();
    Fact[] data = new Fact[rows.length];
    for(int i = 0; i < rows.length; i++)
       data[i] = (Fact)strategyModel.getValueAt(rows[i],StrategyModel.FACT);
    return data;
  }

  protected Fact[] cutSelectedRows()  {
     Fact[] data = getSelectedRows();
     strategyModel.removeRows(strategyTable.getSelectedRows());
     return data;
  }

  protected void deleteSelectedRow()  {
     if ( !isRowSelected() ) return;
     cutSelectedRows();
     // strategyTable.clearSelection();
  }

  protected boolean isRowSelected() {
     int row = strategyTable.getSelectedRow();
     if ( row == -1) {
        errorMsg(0);
        return false;
     }
     return true;
  }

  class SymListAction1 implements ListSelectionListener {
     public void valueChanged(ListSelectionEvent e) {
        int row = protocolTable.getSelectedRow();
        protocolModel.selectRow(row);
        if ( row != -1 ) {
          Boolean s =(Boolean)protocolModel.getValueAt(row,ProtocolModel.STATE);
          strategyToolBar.setEnabled(s.equals(Boolean.TRUE));
        }
     }
  }

  class SymListAction2 implements ListSelectionListener {
     public void valueChanged(ListSelectionEvent e) {
        strategyModel.selectRow(strategyTable.getSelectedRow());
     }
 
  }

  void save() {
    agent.setProtocols(protocolModel.getData());
  }

}
