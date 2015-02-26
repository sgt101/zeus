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

package zeus.ontology.database;

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


public class TablesTableUI extends JPanel 
{
  static final String[] ERROR_MESSAGE = {
     "Please select a row before\ncalling this operation"
  };

  protected JTable         table;

  protected TablesTableModel  model;
  protected ColumnsTableModel cmodel;

  protected int selRow = -1;


  public TablesTableUI(ColumnsTableModel cm)
  {
    cmodel = cm;
    model = new TablesTableModel();

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;
    column = new TableColumn(0);
    column.setHeaderValue(model.getColumnName(0));
    tm.addColumn(column);
    table = new JTable(model,tm);

    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(140, 180));
    table.setBackground(Color.white);

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
   // setBackground(Color.lightGray);

    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(16,16,16,16);
    gridBagLayout.setConstraints(scrollPane, gbc);
    add(scrollPane);

    ListSelectionModel selectionModel = table.getSelectionModel();
    selectionModel.addListSelectionListener(new SymListAction());
  }

  public TablesTableModel getModel() { return model; }


  private void errorMsg(int tag) {
     JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                   "Error", JOptionPane.ERROR_MESSAGE);
  }

  public String getSelectedRow()
  {
    if (selRow == -1) {
      return null;
    }
    return (String)model.getValueAt(selRow, 0);
  }

  class SymListAction implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e)
    {
      int row = table.getSelectedRow();
      if (row != selRow)
      {
        // avoids opening unnecessary DB connections
        selRow = row;
        String name = (String)model.getValueAt(row, 0);
        //System.out.println("Select: " + name);
        cmodel.refreshColumns(name);
      }
    }
  }
}
