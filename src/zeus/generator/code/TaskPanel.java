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

/**
    Change Log
    ----------
    Simon 28/08/00 - implemented code for Task Externals. 
    
    */

/****************************************************************************
* TaskPanel.java
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

public class TaskPanel extends JPanel {
  protected GenerationPlan  genplan;
  protected TaskModel       taskModel;
  protected JTable          taskTable;

  public TaskPanel(GenerationPlan genplan) {
     this.genplan = genplan;

     taskModel = new TaskModel(genplan);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     JCheckBox checkbox = new JCheckBox();
     checkbox.setHorizontalAlignment(JCheckBox.CENTER);
     DefaultCellEditor cellEditor = new DefaultCellEditor(checkbox);

     column = new TableColumn(TaskModel.GENERATE,8,
        new CheckBoxCellRenderer(), cellEditor);
     column.setHeaderValue(taskModel.getColumnName(TaskModel.GENERATE));
     tm.addColumn(column);

     column = new TableColumn(TaskModel.STATUS,24);
     column.setHeaderValue(taskModel.getColumnName(TaskModel.STATUS));
     tm.addColumn(column);

     column = new TableColumn(TaskModel.NAME,24);
     column.setHeaderValue(taskModel.getColumnName(TaskModel.NAME));
     tm.addColumn(column);
        // 25/08/00 addition by simon
     column = new TableColumn(TaskModel.EXTERNAL,24,
        new DefaultTableCellRenderer(),new DefaultCellEditor(new NameField()));
     column.setHeaderValue(taskModel.getColumnName(TaskModel.EXTERNAL));
     tm.addColumn(column);

     taskTable = new JTable(taskModel,tm);
     taskTable.getTableHeader().setReorderingAllowed(false);
     taskTable.setColumnSelectionAllowed(false);

     TitledBorder border = BorderFactory.createTitledBorder("Tasks");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     setBorder(border);

     JScrollPane scrollPane = new JScrollPane(taskTable);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setMinimumSize(new Dimension(160,80));
     scrollPane.setPreferredSize(new Dimension(200,80));
     taskTable.setBackground(Color.white);

     GridBagLayout gridBagLayout = new GridBagLayout();
     setLayout(gridBagLayout);
     GridBagConstraints gbc;
     setBackground(Color.lightGray);

     JToolBar toolbar = new TaskToolBar();
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

  protected class TaskToolBar extends JToolBar
                               implements ActionListener {

     protected JToggleButton helpBtn;
     protected HelpWindow    helpWin;

     public TaskToolBar() {
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
                 getLocation(), "generator", "Generation Plan: Tasks");
              helpWin.setSource(helpBtn);
           }
           else {
              helpWin.dispose();
           }
        }
     }
  }
}