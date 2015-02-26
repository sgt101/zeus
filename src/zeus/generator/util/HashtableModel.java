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
* HashtableModel.java
*
* The underlying model for the Task Table
*****************************************************************************/

package zeus.generator.util;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;


public class HashtableModel extends AbstractTableModel {
  protected static int keyCounter = 0;
  protected static int valueCounter = 0;

  static final int KEY   = 0;
  static final int VALUE = 1;

  protected static final String[] columnNames = {
     "Key", "Value"
  };

  protected Vector keys = new Vector();
  protected Vector values = new Vector();
  protected boolean changed = false;

  public HashtableModel() {
  }

  public void addNewRow() {
     String item = "key" + (keyCounter++);
     while( keys.contains(item) )
        item = "key" + (keyCounter++);
     keys.addElement(item);

     item = "value" + (valueCounter++);
     while( values.contains(item) )
        item = "value" + (valueCounter++);
     values.addElement(item);
     fireTableRowsInserted(keys.size()-1,keys.size()-1);
     changed = true;
  }
  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        keys.removeElementAt(rows[i]-i);
        values.removeElementAt(rows[i]-i);
        fireTableRowsDeleted(rows[i]-i,rows[i]-i);
     }
     changed = true;
  }

  public void reset(Hashtable input)  {
    keys.removeAllElements();
    values.removeAllElements();
    Enumeration enum = input.keys();
    Object key, value;
    while( enum.hasMoreElements() ) {
       key = enum.nextElement();
       value = input.get(key);
       keys.addElement(key);
       values.addElement(value);
    }
    fireTableDataChanged();
    changed = false;
  }

  public Hashtable getData() {
     Hashtable output = new Hashtable();
     for(int i = 0; i < keys.size(); i++ )
        output.put(keys.elementAt(i),values.elementAt(i));
     return output;
  }

  public boolean hasChanged() { return changed; }

  public int     getColumnCount()             { return columnNames.length; }
  public int     getRowCount()                { return keys.size(); }
  public String  getColumnName(int col)       { return columnNames[col]; }
  public boolean isCellEditable(int r, int c) { return true; }

  public Object getValueAt(int row, int column) {
     switch(column) {
        case KEY:
             return keys.elementAt(row);

	case VALUE:
             return values.elementAt(row);
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column) {
     switch(column) {
        case KEY:
             if ( aValue.equals(keys.elementAt(row)) ) return;
             keys.setElementAt(aValue,row);
             break;

	case VALUE:
             if ( aValue.equals(values.elementAt(row)) ) return;
             values.setElementAt(aValue,row);
             break;
     }
     changed = true;
  }
}
