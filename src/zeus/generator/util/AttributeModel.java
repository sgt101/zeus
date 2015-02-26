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
* AttributeModel.java
*
* The underlying model for the Attribute Table
*****************************************************************************/

package zeus.generator.util;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.gui.editors.*;
import zeus.generator.event.*;

public class AttributeModel extends AbstractTableModel
                            implements ChangeListener,
			    ValidatingModel,
                            RenameListener  {

  public static final int ATTRIBUTE = 0;
  public static final int VALUE = 1;
  public static final int RESTRICTION = 2;

  protected EventListenerList  changeListeners = new EventListenerList();
  protected String[]           columnNames = { "Attribute", "Value",
					       "Restriction" };
  protected String[][]         data;
  protected Fact               fact;

  protected int                columns;
  protected boolean            restrictions;
  protected boolean[]          permissions;

  public AttributeModel() {
    columns = 2;
    data = new String[0][columns];
    permissions = new boolean[] {false, true, true};
  }

  public void reset(Fact fact) {
     this.fact = fact;
     if ( fact == null )
        data = new String[0][columns];
     else {
        String[] attributes = fact.listAttributes();
        ValueFunction[] values = fact.listValues();
        data = new String[values.length][columns];
        for(int i = 0; i < data.length; i++ ) {
           data[i][ATTRIBUTE] = attributes[i];
           // alteration for 1.2.2 suggested by Matthieu Gomez [mgomez@INTELOGICS.COM]
           // implemented by Simon Thompson
          /* if ( values[i].getID() == ValueFunction.LVAR )
              data[i][VALUE] = null;
           else*/
              data[i][VALUE] = values[i].toString();
        }
     }
     fireTableDataChanged();
  }

  public Fact getData()        { return fact; }
  public int  getColumnCount() { return columnNames.length; }
  public int  getRowCount()    { return data.length; }

  public boolean isCellEditable(int row, int col) {
    return permissions[col];
  }

  public String  getColumnName(int col)           { return columnNames[col]; }
  public Object  getValueAt(int row, int col)     { return data[row][col]; }

  public boolean isValidEntry(int row, int column) {
     switch(column) {
        case VALUE:
             if ( data[row][column] == null )
                return true;
             else if ( data[row][column].equals("") )
                return true;
             else
                return ZeusParser.Expression(data[row][column]) != null;
        case ATTRIBUTE:
             return true;
        case RESTRICTION:
	  return true;
     }
     return false; // sh never get here
  }

  public void setRestriction(String value, int row) {

    if(value != null) {
      data[row][RESTRICTION] = value;
    }
  }

  public void setValueAt(Object aValue, int row, int column)   {

     if(column == RESTRICTION) {
       if(aValue != null) {
	 data[row][RESTRICTION] = aValue.toString();
	 fireChanged();
	 fireTableCellUpdated(row,column);
       }
       return;
     }

     Core.ERROR(column == VALUE, 1, this);

     String value = (aValue == null) ? null : ((String)aValue).trim();
     if ( value.equals("") ) value = null;

     if ( value == null ) {
        if ( data[row][VALUE] == null )
           return;
        else {
           data[row][VALUE] = value;
           fact.setValue(data[row][ATTRIBUTE],fact.newVar());
        }
     }
     else {
        // always use this keyword for self reference!
        value = Misc.substitute(value,fact.getId(),Fact.THIS);
        if ( data[row][VALUE] != null && data[row][VALUE].equals(value) )
           return;
        else {
           data[row][VALUE] = value;
           ValueFunction fn = ZeusParser.Expression(data[row][VALUE]);
           if ( fn != null )
              fact.setValue(data[row][ATTRIBUTE],fn);
        }
     }
     fireChanged();
     fireTableCellUpdated(row,column);
  }

  public void nameChanged(RenameEvent e) {
     String prev = (String)e.getOriginal();
     String curr = (String)e.getCurrent();
     String s;
     for(int i = 0; i < data.length; i++ ) {
       if ( data[i][VALUE] != null ) {
          s = Misc.substitute(data[i][VALUE],prev,curr);
          if ( !s.equals(data[i][VALUE]) )
             setValueAt(s,i,VALUE);
       }
     }
  }
  public void stateChanged(ChangeEvent e) {
     reset(fact);
  }
  
  public void addChangeListener(ChangeListener x) {
     changeListeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     changeListeners.remove(ChangeListener.class, x);
  }
  
  protected void fireChanged() {
     ChangeEvent c = new ChangeEvent(this);
     Object[] listeners = changeListeners.getListenerList();
     for(int i= listeners.length-2; i >= 0; i -=2) {
        if (listeners[i] == ChangeListener.class) {
           ChangeListener cl = (ChangeListener)listeners[i+1];
           cl.stateChanged(c);
        }
     }
  }

  public void enableRestrictions() {
    if(restrictions == false) {
      columns++;
      restrictions = true;
      reset(fact);
    }
  }

  public void disableRestrictions() {
    if(restrictions == true) {
      columns--;
      restrictions = false;
      reset(fact);
    }
  }

  public void setReadOnly(int column) {
    permissions[column] = false;
  }

  public void setWriteable(int column) {
    permissions[column] = true;
  }

}
