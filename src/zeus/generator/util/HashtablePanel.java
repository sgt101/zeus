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
* HashtablePanel.java
*
*
***************************************************************************/

package zeus.generator.util;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.gui.help.*;

public class HashtablePanel extends JPanel {
  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected HashtableModel  model;
  protected JTable          table;

  public HashtablePanel(HashtableModel model) {
     this.model = model;

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;

     column = new TableColumn(HashtableModel.KEY,12);
     column.setHeaderValue(model.getColumnName(HashtableModel.KEY));
     tm.addColumn(column);

     column = new TableColumn(HashtableModel.VALUE,24);
     column.setHeaderValue(model.getColumnName(HashtableModel.VALUE));
     tm.addColumn(column);

     table = new JTable(model,tm);
     table.getTableHeader().setReorderingAllowed(false);
     table.setColumnSelectionAllowed(false);

     TitledBorder border = BorderFactory.createTitledBorder("Key-Value Table");
     border.setTitlePosition(TitledBorder.TOP);
     border.setTitleJustification(TitledBorder.RIGHT);
     border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
     border.setTitleColor(Color.blue);
     setBorder(border);

     JScrollPane scrollPane = new JScrollPane(table);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setMinimumSize(new Dimension(160,80));
     scrollPane.setPreferredSize(new Dimension(200,80));
     table.setBackground(Color.white);

     GridBagLayout gridBagLayout = new GridBagLayout();
     setLayout(gridBagLayout);
     GridBagConstraints gbc;
     setBackground(Color.lightGray);

     JToolBar toolbar = new HashtableToolBar();
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

  protected class HashtableToolBar extends JToolBar
                               implements ActionListener {

     protected JToggleButton helpBtn;
     protected HelpWindow    helpWin;
     protected JButton       newBtn;
     protected JButton       deleteBtn;

     public HashtableToolBar() {
        setBackground(Color.lightGray);
        setBorder( new BevelBorder(BevelBorder.LOWERED ) );
        setFloatable(false);
        String path = SystemProps.getProperty("gif.dir") + "generator" +
           System.getProperty("file.separator");

        newBtn = new JButton(new ImageIcon(path + "new1.gif"));
	newBtn.setMargin(new Insets(0,0,0,0));
        add(newBtn);
        newBtn.setToolTipText("New");
        newBtn.addActionListener(this);

        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	deleteBtn.setMargin(new Insets(0,0,0,0));
        add(deleteBtn);
        deleteBtn.setToolTipText("Delete");
        deleteBtn.addActionListener(this);

        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);
     }

     public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if ( src == newBtn ) {
           model.addNewRow();
           repaint();}
        else if ( src == deleteBtn ) {
           if ( isRowSelected() )
              model.removeRows(table.getSelectedRows());
              repaint();
        }
        else if ( src == helpBtn ) {
           if ( helpBtn.isSelected() ) {
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 getLocation(), "generator", "Generation Plan: Tasks");
              helpWin.setSource(helpBtn);
           }
           else {
              helpWin.dispose();
           }
           repaint();
        }
     }
  }

  protected boolean isRowSelected() {
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

  public void reset(Hashtable input) {
     model.reset(input);
  }

  public Hashtable getData() {
     return model.getData();
  }
}
