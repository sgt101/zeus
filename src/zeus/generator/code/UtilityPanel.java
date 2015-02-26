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
* UtilityPanel.java
*
*
***************************************************************************/

package zeus.generator.code;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.gui.help.*;
import zeus.gui.editors.*;
import zeus.gui.fields.*;

public class UtilityPanel extends JPanel {
  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected GenerationPlan   genplan;
  protected NameserverModel  nameserverModel;
  protected JTable           nameserverTable;
  protected FacilitatorModel facilitatorModel;
  protected JTable           facilitatorTable;
  protected VisualiserModel  visualiserModel;
  protected JTable           visualiserTable;
  protected DbProxyModel     dbProxyModel;
  protected JTable           dbProxyTable;

  public UtilityPanel(GenerationPlan genplan) {
     this.genplan = genplan;

     GridBagLayout gridBagLayout = new GridBagLayout();
     GridBagConstraints gbc = new GridBagConstraints();
     setLayout(gridBagLayout);
     setBackground(Color.lightGray);

     setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

     // Add the panel containing the nameservers to this panel.
     JPanel nameserverPanel = new JPanel();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(nameserverPanel,gbc);
     add(nameserverPanel);

     // Add the panel containing the facilitators to this panel.
     JPanel facilitatorPanel = new JPanel();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(facilitatorPanel,gbc);
     add(facilitatorPanel);

     // Add the panel containing the visualisers to this panel.
     JPanel visualiserPanel = new JPanel();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(visualiserPanel,gbc);
     add(visualiserPanel);

     // Add the panel containing the dbProxysto this panel.
     JPanel dbProxyPanel = new JPanel();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(dbProxyPanel,gbc);
     add(dbProxyPanel);


     // Create Nameserver info
     nameserverModel = new NameserverModel(genplan);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     JCheckBox checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     DefaultCellEditor cellEditor = new DefaultCellEditor(checkbox);

     column = new TableColumn(NameserverModel.NAME,24,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new NameField()));
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.NAME));
     tm.addColumn(column);

     column = new TableColumn(NameserverModel.HOST,24);
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.HOST));
     tm.addColumn(column);

     column = new TableColumn(NameserverModel.IS_ROOT,8,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.IS_ROOT));
     tm.addColumn(column);

     column = new TableColumn(NameserverModel.TIME,24,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new RealNumberField(0,1000)));
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.TIME));
     tm.addColumn(column);

     column = new TableColumn(NameserverModel.SERVER_FILE,24);
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.SERVER_FILE));
     tm.addColumn(column);

     column = new TableColumn(NameserverModel.OUT_FILE,24);
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.OUT_FILE));
     tm.addColumn(column);
/*
     column = new TableColumn(NameserverModel.HAS_GUI,24,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.HAS_GUI));
     tm.addColumn(column);

     column = new TableColumn(NameserverModel.EXTERNAL,24);
     column.setHeaderValue(nameserverModel.getColumnName(NameserverModel.EXTERNAL));
     tm.addColumn(column);
*/
     nameserverTable = new JTable(nameserverModel,tm);
     nameserverTable.getTableHeader().setReorderingAllowed(false);
     nameserverTable.setColumnSelectionAllowed(false);

     TitledBorder border = BorderFactory.createTitledBorder("Nameservers");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     nameserverPanel.setBorder(border);

     gridBagLayout = new GridBagLayout();
     nameserverPanel.setLayout(gridBagLayout);
     nameserverPanel.setBackground(Color.lightGray);

     JToolBar toolbar = new UtilityToolBar(nameserverTable,nameserverModel,
        "Generation Plan: Nameservers");
     gbc = new GridBagConstraints();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     nameserverPanel.add(toolbar);

     JScrollPane scrollPane = new JScrollPane(nameserverTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(200,80));
     nameserverTable.setBackground(Color.white);

     gbc = new GridBagConstraints();
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     nameserverPanel.add(scrollPane);

     // Create facilitator panel info
     facilitatorModel = new FacilitatorModel(genplan);

     gridBagLayout = new GridBagLayout();
     facilitatorPanel.setLayout(gridBagLayout);
     facilitatorPanel.setBackground(Color.lightGray);

     tm = new DefaultTableColumnModel();
     checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     cellEditor = new DefaultCellEditor(checkbox);

     column = new TableColumn(FacilitatorModel.NAME,24,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new NameField()));
     column.setHeaderValue(facilitatorModel.getColumnName(FacilitatorModel.NAME));
     tm.addColumn(column);

     column = new TableColumn(FacilitatorModel.HOST,24);
     column.setHeaderValue(facilitatorModel.getColumnName(FacilitatorModel.HOST));
     tm.addColumn(column);

     column = new TableColumn(FacilitatorModel.SERVER_FILE,24);
     column.setHeaderValue(facilitatorModel.getColumnName(FacilitatorModel.SERVER_FILE));
     tm.addColumn(column);
/*
     column = new TableColumn(FacilitatorModel.HAS_GUI,24,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(facilitatorModel.getColumnName(FacilitatorModel.HAS_GUI));
     tm.addColumn(column);

     column = new TableColumn(FacilitatorModel.EXTERNAL,24);
     column.setHeaderValue(facilitatorModel.getColumnName(FacilitatorModel.EXTERNAL));
     tm.addColumn(column);
*/
     column = new TableColumn(FacilitatorModel.PERIOD,24,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new RealNumberField(0,1000)));
     column.setHeaderValue(facilitatorModel.getColumnName(FacilitatorModel.PERIOD));
     tm.addColumn(column);


     facilitatorTable = new JTable(facilitatorModel,tm);
     facilitatorTable.getTableHeader().setReorderingAllowed(false);
     facilitatorTable.setColumnSelectionAllowed(false);

     scrollPane = new JScrollPane(facilitatorTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(200,80));
     facilitatorTable.setBackground(Color.white);

     border = (BorderFactory.createTitledBorder("Facilitators"));
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     facilitatorPanel.setBorder(border);

     gbc = new GridBagConstraints();
     toolbar = new UtilityToolBar(facilitatorTable,facilitatorModel,
        "Generation Plan: Facilitators");

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.weightx = gbc.weighty = 0;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     facilitatorPanel.add(toolbar);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     facilitatorPanel.add(scrollPane);

     // Create visualisers panel info
     visualiserModel = new VisualiserModel(genplan);

     gridBagLayout = new GridBagLayout();
     visualiserPanel.setLayout(gridBagLayout);
     visualiserPanel.setBackground(Color.lightGray);

     tm = new DefaultTableColumnModel();
     checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     cellEditor = new DefaultCellEditor(checkbox);

     column = new TableColumn(VisualiserModel.NAME,24,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new NameField()));
     column.setHeaderValue(visualiserModel.getColumnName(VisualiserModel.NAME));
     tm.addColumn(column);

     column = new TableColumn(VisualiserModel.HOST,24);
     column.setHeaderValue(visualiserModel.getColumnName(VisualiserModel.HOST));
     tm.addColumn(column);

     column = new TableColumn(VisualiserModel.SERVER_FILE,24);
     column.setHeaderValue(visualiserModel.getColumnName(VisualiserModel.SERVER_FILE));
     tm.addColumn(column);
/*
     column = new TableColumn(VisualiserModel.HAS_GUI,24,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(visualiserModel.getColumnName(VisualiserModel.HAS_GUI));
     tm.addColumn(column);

     column = new TableColumn(VisualiserModel.EXTERNAL,24);
     column.setHeaderValue(visualiserModel.getColumnName(VisualiserModel.EXTERNAL));
     tm.addColumn(column);
*/
     visualiserTable = new JTable(visualiserModel,tm);
     visualiserTable.getTableHeader().setReorderingAllowed(false);
     visualiserTable.setColumnSelectionAllowed(false);

     scrollPane = new JScrollPane(visualiserTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(200,80));
     visualiserTable.setBackground(Color.white);

     border = (BorderFactory.createTitledBorder("Visualisers"));
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     visualiserPanel.setBorder(border);

     gbc = new GridBagConstraints();
     toolbar = new UtilityToolBar(visualiserTable,visualiserModel,
        "Generation Plan: Visualisers");

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.weightx = gbc.weighty = 0;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     visualiserPanel.add(toolbar);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     visualiserPanel.add(scrollPane);

     // Create dbproxyspanel info
     dbProxyModel = new DbProxyModel(genplan);

     gridBagLayout = new GridBagLayout();
     dbProxyPanel.setLayout(gridBagLayout);
     dbProxyPanel.setBackground(Color.lightGray);

     tm = new DefaultTableColumnModel();
     checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     cellEditor = new DefaultCellEditor(checkbox);

     column = new TableColumn(DbProxyModel.NAME,24,
        new DefaultTableCellRenderer(),
	new DefaultCellEditor(new NameField()));
     column.setHeaderValue(dbProxyModel.getColumnName(DbProxyModel.NAME));
     tm.addColumn(column);

     column = new TableColumn(DbProxyModel.HOST,24);
     column.setHeaderValue(dbProxyModel.getColumnName(DbProxyModel.HOST));
     tm.addColumn(column);

     column = new TableColumn(DbProxyModel.PATH,24);
     column.setHeaderValue(dbProxyModel.getColumnName(DbProxyModel.PATH));
     tm.addColumn(column);

     column = new TableColumn(DbProxyModel.SERVER_FILE,24);
     column.setHeaderValue(dbProxyModel.getColumnName(DbProxyModel.SERVER_FILE));
     tm.addColumn(column);
/*
     column = new TableColumn(DbProxyModel.HAS_GUI,24,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(dbProxyModel.getColumnName(DbProxyModel.HAS_GUI));
     tm.addColumn(column);

     column = new TableColumn(DbProxyModel.EXTERNAL,24);
     column.setHeaderValue(dbProxyModel.getColumnName(DbProxyModel.EXTERNAL));
     tm.addColumn(column);
*/
     dbProxyTable = new JTable(dbProxyModel,tm);
     dbProxyTable.getTableHeader().setReorderingAllowed(false);
     dbProxyTable.setColumnSelectionAllowed(false);

     scrollPane = new JScrollPane(dbProxyTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(200,80));
     dbProxyTable.setBackground(Color.white);

     border = (BorderFactory.createTitledBorder("Database Proxys"));
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     dbProxyPanel.setBorder(border);

     gbc = new GridBagConstraints();
     toolbar = new UtilityToolBar(dbProxyTable,dbProxyModel,
        "Generation Plan: Database Proxys");

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.weightx = gbc.weighty = 0;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     dbProxyPanel.add(toolbar);

     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     dbProxyPanel.add(scrollPane);

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

  protected class UtilityToolBar extends JToolBar
                                 implements ActionListener {

     protected JToggleButton helpBtn;
     protected JButton       newBtn;
     protected JButton       deleteBtn;
     protected HelpWindow    helpWin;
     protected JTable        table;
     protected String        help_page;
     protected UtilityModel  model;

     public UtilityToolBar(JTable table, UtilityModel model, String help_page) {
        this.model = model;
        this.table = table;
        this.help_page = help_page;

        setBackground(Color.lightGray);
        setBorder( new BevelBorder(BevelBorder.LOWERED ) );
        setFloatable(false);
        String path = SystemProps.getProperty("gif.dir") + "generator" +
           System.getProperty("file.separator");

        // clear Button
        newBtn = new JButton(new ImageIcon(path + "new1.gif" ));
	newBtn.setMargin(new Insets(0,0,0,0));
        add(newBtn);
        newBtn.setToolTipText("New entry");
        newBtn.addActionListener(this);

        // All Button
        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	deleteBtn.setMargin(new Insets(0,0,0,0));
        add(deleteBtn);
        deleteBtn.setToolTipText("Delete selected entries");
        deleteBtn.addActionListener(this);

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
        else if ( src == deleteBtn ) {
           if ( table.getSelectedRow() == -1 ) {
              errorMsg(0);
              return;
           }
           else {
              model.removeRows(table.getSelectedRows());
              table.clearSelection();
           }
        }
        else if ( src == helpBtn ) {
           if ( helpBtn.isSelected() ) {
              Point dispos = getLocation();
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                                       dispos, "generator", help_page);
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
}
