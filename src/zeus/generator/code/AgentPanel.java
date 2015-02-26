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
* AgentPanel.java
*
*
***************************************************************************/

package zeus.generator.code;

import java.io.*;
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

public class AgentPanel extends JPanel {
  protected GenerationPlan   genplan;
  protected AgentModel       agentModel;
  protected JTable           agentTable;

  public AgentPanel(GenerationPlan genplan) {
     this.genplan = genplan;

     agentModel = new AgentModel(genplan);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     JCheckBox checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     DefaultCellEditor cellEditor = new DefaultCellEditor(checkbox);

     column = new TableColumn(AgentModel.GENERATE,8,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.GENERATE));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.STATUS,24);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.STATUS));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.NAME,24);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.NAME));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.HOST,24);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.HOST));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.SERVER_FILE,24);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.SERVER_FILE));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.DATABASE,24);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.DATABASE));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.HAS_GUI,24,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.HAS_GUI));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.EXTERNAL,24);
     column.setHeaderValue(agentModel.getColumnName(AgentModel.EXTERNAL));
     tm.addColumn(column);

     column = new TableColumn(AgentModel.ICON,24,
        new IconRenderer(), new IconEditor());
     column.setHeaderValue(agentModel.getColumnName(AgentModel.ICON));
     tm.addColumn(column);

     agentTable = new JTable(agentModel,tm);
     agentTable.getTableHeader().setReorderingAllowed(false);
     agentTable.setColumnSelectionAllowed(false);

     TitledBorder border = BorderFactory.createTitledBorder("Task Agents");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     setBorder(border);

     JScrollPane scrollPane = new JScrollPane(agentTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setMinimumSize(new Dimension(160,80));
     scrollPane.setPreferredSize(new Dimension(200,80));
     agentTable.setBackground(Color.white);

     GridBagLayout gridBagLayout = new GridBagLayout();
     setLayout(gridBagLayout);
     GridBagConstraints gbc;
     setBackground(Color.lightGray);

     JToolBar toolbar = new AgentToolBar();
     gbc = new GridBagConstraints();
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.NONE;
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.insets = new Insets(0,8,0,0);
     gridBagLayout.setConstraints(toolbar,gbc);
     add(toolbar);

     gbc = new GridBagConstraints();
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.NORTHWEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(8,8,8,8);
     gridBagLayout.setConstraints(scrollPane,gbc);
     add(scrollPane);
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

  protected class IconRenderer extends DefaultTableCellRenderer {
     public IconRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
     }
     public void setValue(Object value) {
        if ( value == null ) return;

        String path = ((String)value).trim();
        if ( path.equals("") ) return;

        super.setIcon(new ImageIcon(path));
     }
  }

  class IconEditor extends DefaultCellEditor
                           implements ActionListener {

    protected JButton button = new JButton("");
    protected int row, column;
    protected Object value;
    protected FileDialog dialog = null;

    public IconEditor() {
      super(new JTextField());
      setClickCountToStart(1);

      button.setBackground(Color.white);
      button.setHorizontalAlignment(JButton.LEFT);
      button.setBorderPainted(false);
      button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ( src == button ) {
         // Note: create dialog (using button) before stopping editing
         // which will remove the button from the visible component hierachy
         if ( dialog == null )
            dialog = new FileDialog((Frame)SwingUtilities.getRoot(button),
	       "Select (a file within) Target Directory",FileDialog.LOAD);

	 fireEditingCanceled();

         if ( value != null && !value.equals("") ) {
	    File f1 = new File((String)value);
            dialog.setFile(f1.getName());
            dialog.setDirectory(f1.getParent());
         }
         else
            dialog.setFile("*.gif");

         dialog.pack();
         dialog.setVisible(true);

         String path = (dialog.getFile() == null) ? null : dialog.getDirectory() +
	    System.getProperty("file.separator") + dialog.getFile();
         agentModel.setValueAt(path,row,column);
      }
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

  protected class AgentToolBar extends JToolBar
                               implements ActionListener {

     protected JToggleButton helpBtn;
     protected HelpWindow    helpWin;

     public AgentToolBar() {
        setBackground(Color.lightGray);
        setBorder( new BevelBorder(BevelBorder.LOWERED ) );
        setFloatable(false);
        String path = SystemProps.getProperty("gif.dir") + "generator" +
           System.getProperty("file.separator");

        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);
     }

     public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if ( src == helpBtn ) {
           if ( helpBtn.isSelected() ) {
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 getLocation(), "generator", "Generation Plan: Agents");
              helpWin.setSource(helpBtn);
           }
           else {
              helpWin.dispose();
           }
        }
     }
  }
}
