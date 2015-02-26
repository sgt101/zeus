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
* AttributeTableModel.java
*
* The underlying model for the Attribute Table
*****************************************************************************/

package zeus.ontology.attributes;

import java.util.Vector;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.ontology.*;
import zeus.gui.editors.*;


public class AttributeTableModel extends AbstractTableModel
                                 implements ChangeListener, ValidatingModel {

  static final int NAME        = 0;
  static final int TYPE        = 1;
  static final int RESTRICTION = 2;
  static final int DEFAULT     = 3;

  protected static final String[] columnNames = {
     "Name", "Type", "Restriction", "Default Value" };

  protected String[][]    data = null;
  protected boolean[][]   validityInfo = null;
  protected String        currentName = null;
  protected boolean       showAll = false;
  protected OntologyDb    model;


  public AttributeTableModel(OntologyDb model) {
    this.model = model;
  }

  void refreshAttributes(String fact) {
    currentName = fact;
    showAll = false;
    data = model.getAttributeEntriesFor(fact);
    validityInfo = model.getValidityInfoFor(fact);
    fireTableStructureChanged();
  }

  void refreshAllAttributes(String fact) {
    currentName = fact;
    showAll = true;
    data = model.getAllAttributeEntriesFor(fact);
    validityInfo = model.getAllValidityInfoFor(fact);
    fireTableStructureChanged();
  }

  void addNewRow() {
     model.addNewAttributeRow(currentName);
     refresh();
  }
  void deleteRows(int[] rows) {
     Vector attributes = new Vector(100);
     int limit = model.getEditableLimit();
     for(int i = 0; i < rows.length; i++ ) {
        if ( rows[i] < limit ) {
           JOptionPane.showMessageDialog(null,"Attribute " +
              data[rows[i]][NAME] + " cannot be deleted",
              "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
           attributes.addElement(data[rows[i]][NAME]);
     }
     model.deleteAttributes(currentName,Misc.stringArray(attributes));
     refresh();
  }
  void addRows(String[][] input) {
     model.addAttributeRows(currentName,input);
     refresh();
  }
  String[][] getRows(int[] input) {
     String[][] result = new String[input.length][columnNames.length];
     for(int i = 0; i < result.length; i++ )
        for(int j = 0; j < result[i].length; j++)
           result[i][j] = data[input[i]][j];
     return result;
  }

  public int getColumnCount() { return columnNames.length; }
  public int getRowCount()    { return (data != null) ? data.length : 0; }

  public String   getColumnName(int col)          { return columnNames[col]; }
  public Object   getValueAt(int row, int column) { return data[row][column]; }
  public String[] getRow(int row)                 { return data[row]; }
  public boolean  isValidEntry(int row, int col)  { return validityInfo[row][col]; }


  public boolean isCellEditable(int row, int col) {
     return isNodeEditable() && row >= model.getEditableLimit();
  }

  public boolean isNodeEditable() {
     return ( currentName != null && model.isFactEditable(currentName) );
  }

  public void setValueAt(Object aValue, int row, int column) {
    String value;
    if ( aValue == null )
       value = "";
    else
       value = (aValue.toString()).trim();

    if ( data[row][column].equals(value) ) return;

    data[row][column] = model.setAttribute(currentName,
       data[row][NAME],column,value);
    validityInfo[row] = model.isAttributeValid(currentName,data[row][NAME]);
    fireTableCellUpdated(row,column);
  }

  public void stateChanged(ChangeEvent e) {
    refresh();
  }

  protected void refresh() {
    if ( currentName == null ) return;

    if ( !model.hasFact(currentName) )  {
       currentName = null;
       data = new String[0][4];
       fireTableStructureChanged();
       return;
    }

    if ( showAll )
       refreshAllAttributes(currentName);
    else
       refreshAttributes(currentName);
  }
}
