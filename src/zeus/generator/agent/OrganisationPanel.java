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
* OrganisationPanel.java
*
* Panel through which agent relationships are specified
***************************************************************************/

package zeus.generator.agent;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;
import zeus.generator.util.*;
import zeus.ontology.*;
import zeus.gui.fields.*;
import zeus.gui.help.*;

public class OrganisationPanel extends JPanel {

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected ControlPanel       controlPane;
  protected AgentDescription   agent;
  protected AcquaintanceModel  acquaintanceModel;
  protected JTable             acquaintanceTable;
  protected AbilityModel       abilityModel;
  protected JTable             abilityTable;
  protected AgentGenerator     generator;
  protected GeneratorModel     genmodel;
  protected OntologyDb         ontologyDb;
  protected AgentEditor        editor;
  protected AbilitySpec[]      clipboard = null;
  protected FactToolBar        factToolBar;

  public OrganisationPanel(AgentGenerator generator,
                           GeneratorModel genmodel,
                           OntologyDb ontologyDb,
                           AgentEditor editor,
                           AgentDescription agent)  {

     this.agent = agent;
     this.generator = generator;
     this.genmodel = genmodel;
     this.editor = editor;
     this.ontologyDb = ontologyDb;

     GridBagLayout gridBagLayout = new GridBagLayout();
     GridBagConstraints gbc = new GridBagConstraints();
     setLayout(gridBagLayout);
     setBackground(Color.lightGray);
	
     setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
     controlPane = new ControlPanel(editor,"Agent Organisation Panel",false,false);
	
     JPanel acquaintancePanel = new JPanel();	
     JPanel dataPanel = new JPanel();
 
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.HORIZONTAL;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(controlPane,gbc);
     add(controlPane);

     // Add the panel containing the other agents table to the panel.
     gbc.anchor = GridBagConstraints.NORTHEAST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(8,8,8,8);
     gbc.weightx = gbc.weighty = 1;
     gridBagLayout.setConstraints(acquaintancePanel,gbc);
     add(acquaintancePanel);

     // Add the panel containing the known suppliers
     gbc.anchor = GridBagConstraints.SOUTH;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.insets = new Insets(8,8,8,8);
     gbc.weightx = gbc.weighty = 1;
     gridBagLayout.setConstraints(dataPanel,gbc);
     add(dataPanel);

     // Create acquaintance info area
     AttributeModel attributeModel = new AttributeModel();
     AttributeTable attributeTable = new AttributeTable(attributeModel);
     abilityModel = new AbilityModel(ontologyDb,attributeModel);
     acquaintanceModel = new AcquaintanceModel(genmodel,ontologyDb,
        abilityModel,agent.getAcquaintances());

     attributeModel.addChangeListener(editor);
     abilityModel.addChangeListener(editor);
     acquaintanceModel.addChangeListener(editor);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     column = new TableColumn(AcquaintanceModel.AGENT,12,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new NameField()));
     column.setHeaderValue(acquaintanceModel.getColumnName(AcquaintanceModel.AGENT));
     tm.addColumn(column);
     column = new TableColumn(AcquaintanceModel.RELATION,12,
        new DefaultTableCellRenderer(), new RelationEditor());
     column.setHeaderValue(acquaintanceModel.getColumnName(AcquaintanceModel.RELATION));
     tm.addColumn(column);

     acquaintanceTable = new JTable(acquaintanceModel,tm);
     acquaintanceTable.getTableHeader().setReorderingAllowed(false);
     acquaintanceTable.setColumnSelectionAllowed(false);

     ListSelectionModel selectionModel = acquaintanceTable.getSelectionModel();
     selectionModel.addListSelectionListener(new SymListAction1());

     JScrollPane scrollPane = new JScrollPane(acquaintanceTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(340,120));
     acquaintanceTable.setBackground(Color.white);

     gridBagLayout = new GridBagLayout();
     acquaintancePanel.setLayout(gridBagLayout);
     acquaintancePanel.setBackground(Color.lightGray);

     acquaintanceTable.setMinimumSize(new Dimension(400,140));

     TitledBorder border = BorderFactory.createTitledBorder("Acquaintances");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(java.awt.Color.blue);
     acquaintancePanel.setBorder(border);

     JToolBar toolbar = new AcquaintanceToolBar();
     gbc = new GridBagConstraints();
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     acquaintancePanel.add(toolbar);

     gbc = new GridBagConstraints();
     gbc.gridwidth = GridBagConstraints.REMAINDER;     
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     acquaintancePanel.add(scrollPane);

     // Create data info area     
     tm = new DefaultTableColumnModel();     
     column = new TableColumn(AbilityModel.TYPE,12);
     column.setHeaderValue(abilityModel.getColumnName(AbilityModel.TYPE));
     tm.addColumn(column);
     column = new TableColumn(AbilityModel.COST,12,     
        new DefaultTableCellRenderer(),
        new DefaultCellEditor(new RealNumberField()));
     column.setHeaderValue(abilityModel.getColumnName(AbilityModel.COST));
     tm.addColumn(column);
     column = new TableColumn(AbilityModel.TIME,12,     
        new DefaultTableCellRenderer(),
        new DefaultCellEditor(new WholeNumberField()));
     column.setHeaderValue(abilityModel.getColumnName(AbilityModel.TIME));
     tm.addColumn(column);

     abilityTable = new JTable(abilityModel,tm);
     abilityTable.getTableHeader().setReorderingAllowed(false);
     abilityTable.setColumnSelectionAllowed(false);

     selectionModel = abilityTable.getSelectionModel();
     selectionModel.addListSelectionListener(new SymListAction2());

     scrollPane = new JScrollPane(abilityTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(400,100));
     abilityTable.setBackground(Color.white);

     gridBagLayout = new GridBagLayout();
     dataPanel.setLayout(gridBagLayout);
     dataPanel.setBackground(Color.lightGray);

     abilityTable.setMinimumSize(new Dimension(400,120));
     attributeTable.setMinimumSize(new Dimension(400,140));

     border = BorderFactory.createTitledBorder("Acquaintance Abilities");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     dataPanel.setBorder(border);

     factToolBar = new FactToolBar();
     factToolBar.setEnabled(false);

     gbc = new GridBagConstraints();
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(factToolBar,gbc);
     dataPanel.add(factToolBar);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     dataPanel.add(scrollPane);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(attributeTable,gbc);
     dataPanel.add(attributeTable);
  }

  // Begin inner classes

  class AcquaintanceToolBar extends JToolBar implements ActionListener {
     protected JToggleButton  helpBtn, allBtn, peerBtn, nonpeerBtn;
     protected HelpWindow     helpWin;
     protected JButton        resetBtn;
     protected JButton        newBtn;
     protected JButton        editBtn;
     protected JButton        deleteBtn;
     protected JPopupMenu     popup;
     protected JMenuItem      addMenuItem;

     public AcquaintanceToolBar() {
        setBackground(Color.lightGray);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setFloatable(false);
        String path = SystemProps.getProperty("gif.dir") + "generator" +
           System.getProperty("file.separator");

        // new Button
        newBtn = new JButton(new ImageIcon(path + "new1.gif"));
	newBtn.setMargin(new Insets(0,0,0,0));		
        add(newBtn);
        newBtn.setToolTipText("New acquaintance");    
        newBtn.addActionListener(this);
        addSeparator();

        editBtn = new JButton(new ImageIcon(path + "edit1.gif"));
	editBtn.setMargin(new Insets(0,0,0,0));	
        add(editBtn);
        editBtn.setToolTipText("Edit this acquaintance");
        editBtn.addActionListener(this);
   
        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	deleteBtn.setMargin(new Insets(0,0,0,0));
        add(deleteBtn);
        deleteBtn.setToolTipText("Delete this acquaintance");
        deleteBtn.addActionListener(this);
   
        addSeparator();
       
        // Reset Button
        resetBtn = new JButton(new ImageIcon(path + "reset.gif" ));
	resetBtn.setMargin(new Insets(0,0,0,0));	
        add(resetBtn);
        resetBtn.setToolTipText("Reset acquaintance to peer");    
        resetBtn.addActionListener(this);
        addSeparator();
    
        ButtonGroup group = new ButtonGroup();
    
        // All Button
        allBtn = new JToggleButton(new ImageIcon(path + "all.gif"), true);
	allBtn.setMargin(new Insets(0,0,0,0));
        add(allBtn);
        allBtn.setToolTipText("Show all acquaintances");
        allBtn.addActionListener(this);
        group.add(allBtn);
    
        // Peers Button
        peerBtn = new JToggleButton(new ImageIcon(path + "peers.gif"));
	peerBtn.setMargin(new Insets(0,0,0,0));	
        add(peerBtn);
        peerBtn.setToolTipText("Show peers only");
        peerBtn.addActionListener(this);
        group.add(peerBtn);
    
        // Non-Peers Button
        nonpeerBtn = new JToggleButton(new ImageIcon(path + "others.gif"));
	nonpeerBtn.setMargin(new Insets(0,0,0,0));	
        add(nonpeerBtn);
        nonpeerBtn.setToolTipText("Show non-peers only");
        nonpeerBtn.addActionListener(this);
        group.add(nonpeerBtn);
    
        addSeparator();

        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));	
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);

        // ---- Popup Menu for Acquaintances ----
        popup = new JPopupMenu("Add new acquaintance");
        addMenuItem = new JMenuItem("Create new acquaintance");
        addMenuItem.addActionListener(this);

        CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
                                                new EmptyBorder(5,5,5,5));
        popup.setBorder(cbr);
     }

     public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if ( src == newBtn ) {
           popup.removeAll();
           popup.add(addMenuItem);
           popup.addSeparator();

           String[] items = genmodel.getAgentNames();
           Vector names = new Vector();
           names.addElement(editor.getObjectName());
           for(int i = 0; i < acquaintanceModel.getRowCount(); i++ ) 
              names.addElement(acquaintanceModel.getValueAt(i,AcquaintanceModel.AGENT));
           Vector allNames = Misc.stringVector(items);
           Vector validNames = Misc.difference(allNames,names);

           JMenuItem mi;
           for(int i = 0; i < validNames.size(); i++ ) {
              mi = new JMenuItem((String)validNames.elementAt(i));
              mi.addActionListener(this);
              mi.setActionCommand("NEW_ACQUAINTANCE");
              popup.add(mi);
           }
           popup.pack();
           popup.show(newBtn,0,0);
        }
        else if ( src == addMenuItem )
           addNewAcquaintance();
        else if ( e.getActionCommand().equals("NEW_ACQUAINTANCE") )
           addNewAcquaintance(((JMenuItem)src).getText());         
        else if ( src == editBtn )
           editAcquaintance();
        else if ( src == deleteBtn )
           removeAcquaintance();
        if ( src == resetBtn )
           resetAcquaintance();
        else if ( src == allBtn )
           acquaintanceModel.setFilter(AcquaintanceModel.ALL);
        else if ( src == peerBtn )
           acquaintanceModel.setFilter(AcquaintanceModel.PEERS);
        else if ( src == nonpeerBtn )
           acquaintanceModel.setFilter(AcquaintanceModel.OTHERS);
        else if ( src == helpBtn ) {
           if (helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 dispos, "generator", "Activity Org-1");
              helpWin.setSource(helpBtn);
           }
           else {
              helpWin.dispose();
           }
        }
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

        factWin = new FactDialog(editor, ontologyDb); 
     }
  
     public void setEnabled(boolean set) {
        newBtn.setEnabled(set);
        deleteBtn.setEnabled(set);
        cutBtn.setEnabled(set);
        copyBtn.setEnabled(set);
        pasteBtn.setEnabled(set);
     }

     public void factSelected(String[] names)  {
        // Fact Selector callback to add new entries
        abilityModel.addNewRows(names);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == newBtn ) {
           factWin.setLocationRelativeTo(newBtn);
           factWin.display(this);
        }
        else if ( src == deleteBtn ) {
           deleteSelectedAbilities();
        }  
        else if ( src == copyBtn ) {
           clipboard = getSelectedAbilities();
        }
        else if ( src == pasteBtn ) {
           abilityModel.addRows(clipboard);
           abilityTable.clearSelection();
        }
        else if ( src == cutBtn ) {
           clipboard = cutSelectedAbilities();
        }
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(editor, dispos, "generator", "Activity Org-2");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }

  class RelationEditor extends DefaultCellEditor {
     public RelationEditor() {
        super( new JComboBox(AcquaintanceModel.RELATIONS_LIST) {
                      public void contentsChanged(ListDataEvent e) {
                         selectedItemReminder = null; 
                         super.contentsChanged(e);
                      }
               }
        );
        setClickCountToStart(2);
     }
  }

  class SymListAction1 implements ListSelectionListener {
     public void valueChanged(ListSelectionEvent e) {
        int row = acquaintanceTable.getSelectedRow();
        acquaintanceModel.selectRow(row);
        factToolBar.setEnabled(row != -1);
        repaint(); // swing bug?
     }
  }

  class SymListAction2 implements ListSelectionListener {
     public void valueChanged(ListSelectionEvent e) {
        abilityModel.selectRow(abilityTable.getSelectedRow());
        repaint(); // swing bug?
     }
  }

  // End of inner classes

  void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  protected void resetAcquaintance() {
     int row = acquaintanceTable.getSelectedRow();
     if (row == -1) {
        errorMsg(0);
        return;
     }
     acquaintanceModel.setValueAt(
        SystemProps.getProperty("organisation.relations.default"),
        row,AcquaintanceModel.RELATION);
  } 

  protected String getSelectedAcquaintanceName() {
    int row = acquaintanceTable.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)acquaintanceModel.getValueAt(row,AcquaintanceModel.AGENT);
  }

  protected String getSelectedAcquaintanceId() {
    int row = acquaintanceTable.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return null;
    }
    return (String)acquaintanceModel.getValueAt(row,AcquaintanceModel.ID);
  }
  
  protected void addNewAcquaintance() {
    acquaintanceModel.addNewRow();
    factToolBar.setEnabled(false);
  }

  protected void addNewAcquaintance(String name) {
    acquaintanceModel.addNewRow(name);
    factToolBar.setEnabled(false);
  }

  protected void editAcquaintance() {
    String id = getSelectedAcquaintanceId();
    if ( id == null ) return;
    generator.editAgent(id);
  }

  protected void removeAcquaintance() {
    int row = acquaintanceTable.getSelectedRow();
    if ( row == -1 ) {
       errorMsg(0);
       return;
    }
    acquaintanceModel.removeRow(row);
    factToolBar.setEnabled(false);
    repaint(); // swing bug?
  }

  protected void reset(Acquaintance[] data) {
     acquaintanceModel.reset(data);
     factToolBar.setEnabled(false);
  }
  
  protected Acquaintance[] getData() {
     return acquaintanceModel.getData();
  }
  
  protected AbilitySpec[] getSelectedAbilities()  {
    int[] srows = abilityTable.getSelectedRows();
    AbilitySpec[] data = new AbilitySpec[srows.length];
    for(int i = 0; i < srows.length; i++)
       data[i] = (AbilitySpec)abilityModel.getValueAt(srows[i],AbilityModel.ABILITY);
    return data;
  }
  
  protected AbilitySpec[] cutSelectedAbilities()  {
     AbilitySpec[] data = getSelectedAbilities();
     abilityModel.removeRows(abilityTable.getSelectedRows());
     repaint(); // swing bug?
     return data;
  }
 
  protected void deleteSelectedAbilities()  {
     int row = abilityTable.getSelectedRow();
     if ( row == -1 ) {
        errorMsg(0);
        return;
     }
     cutSelectedAbilities();
  }

  public void save() {
     agent.setAcquaintances(acquaintanceModel.getData());
  }
}
