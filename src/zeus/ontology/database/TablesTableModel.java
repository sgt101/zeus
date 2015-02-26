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
* TablesTableModel.java
*
* The local model for the Tables Table
*****************************************************************************/

package zeus.ontology.database;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

import zeus.concepts.*;
import zeus.ontology.*;


public class TablesTableModel extends AbstractTableModel
{
  protected String[]    columnNames = { "Table Name" };
  protected String[][]  data = null;
  protected boolean[][] validityInfo = null;
  //protected OntologyDb  model = null;

  public TablesTableModel() 
  {
    // addChangeListener(parent frame);
    // refresh();
  }

  public void setValues(Vector tablesList)
  {
    int rows = tablesList.size();
    data = new String[rows][1];
    int r = 0;
    Enumeration enum = tablesList.elements();
    while (enum.hasMoreElements())
    {
      String s = (String)enum.nextElement();
      data[r][0] = s;
      r++;
    }
    refresh();
  }

  void refresh() {
    // refresh data from source
    fireTableStructureChanged();
  }

  public int      getColumnCount()     { return columnNames.length; }
  public int      getRowCount()        { return (data != null) ? data.length : 0; }
  public String[] getRow(int row)      { return data[row]; }

  public boolean isCellEditable(int r, int c) { return false;}
  public String  getColumnName(int c)         { return columnNames[c]; }
  public Object  getValueAt(int r, int c)     { return data[r][c]; }
  public boolean isValidEntry(int r, int c)   { return validityInfo[r][c]; }

  String[][] getRows(int[] input) {
     String[][] result = new String[input.length][columnNames.length];
     for(int i = 0; i < result.length; i++ )
        for(int j = 0; j < result[i].length; j++)
           result[i][j] = data[input[i]][j];
     return result;
  }
}
