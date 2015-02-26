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
* RestrictionTableModel.java
*
* The local model for the Restriction Table
*****************************************************************************/

package zeus.ontology.restrictions;

import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

import zeus.concepts.*;
import zeus.ontology.*;
import zeus.gui.editors.*;


public class RestrictionTableModel extends AbstractTableModel
             implements ChangeListener, ValidatingModel {

  protected String[]    columnNames = { "Name", "Type", "Restrictions" };
  protected String[][]  data = null;
  protected boolean[][] validityInfo = null;
  protected OntologyDb  model = null;

  static final int NAME        = 0;
  static final int TYPE        = 1;
  static final int RESTRICTION = 2;

  public RestrictionTableModel(OntologyDb model) {
    this.model = model;
    model.addChangeListener(this);
    refresh();
  }

  void refresh() {
    data = model.getAllRestrictions();
    validityInfo = model.getAllRestrictionValidityInfo();
    fireTableStructureChanged();
  }

  public int      getColumnCount()     { return columnNames.length; }
  public int      getRowCount()        { return (data != null) ? data.length : 0; }
  public String[] getRow(int row)      { return data[row]; }

  public boolean isCellEditable(int r, int c) { return true;}
  public String  getColumnName(int c)         { return columnNames[c]; }
  public Object  getValueAt(int r, int c)     { return data[r][c]; }
  public boolean isValidEntry(int r, int c)   { return validityInfo[r][c]; }

  public void setValueAt(Object aValue, int row, int col) {
    String value;
    if ( aValue == null )
       value = "";
    else
       value = ((String)aValue).trim();

    if ( data[row][col].equals(value) )
       return;

    String result = model.setRestrictionData(data[row][NAME],col,value);
    data[row][col] = result;
    validityInfo[row] = model.isRestrictionValid(data[row][NAME]);
    fireTableCellUpdated(row,col);
  }

  public void stateChanged(ChangeEvent e) {
    OntologyDbChangeEvent evt = (OntologyDbChangeEvent)e;
    if ( evt.getEventType() == OntologyDb.RELOAD )
       refresh();
  }

  void addNewRow() {
     model.addNewRestriction();
     refresh();
  }
  void deleteRows(int[] rows) {
     String[] names = new String[rows.length];
     for(int i = 0; i < rows.length; i++ )
        names[i] = data[rows[i]][NAME];
     model.deleteRestrictions(names);
     refresh();
  }
  void addRows(String[][] input) {
     model.addRestrictions(input);
     refresh();
  }
  String[][] getRows(int[] input) {
     String[][] result = new String[input.length][columnNames.length];
     for(int i = 0; i < result.length; i++ )
        for(int j = 0; j < result[i].length; j++)
           result[i][j] = data[input[i]][j];
     return result;
  }
}
