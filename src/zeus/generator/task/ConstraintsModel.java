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
* ConstraintsModel.java
*
* The underlying model for the Applicability Constraints Table
*****************************************************************************/

package zeus.generator.task;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.generator.*;
import zeus.generator.event.*;
import zeus.generator.util.*;
import zeus.gui.editors.*;

public class ConstraintsModel extends AbstractTableModel
                              implements ChangeListener,
                                         ValidatingModel,
                                         RenameListener {

  static final int CONSTRAINT = 0;

  protected static final String[] columnNames = { "Constraint" };

  protected EventListenerList changeListeners = new EventListenerList();
  protected Vector            data            = new Vector();
  protected Vector            validityInfo    = new Vector();
  protected BasicFactModel    preconditionsModel;
  protected BasicFactModel    postconditionsModel;

  public ConstraintsModel(BasicFactModel preconditionsModel,
                          BasicFactModel postconditionsModel,
                          LogicalFn[] input) {

     this.preconditionsModel = preconditionsModel;
     this.postconditionsModel = postconditionsModel;
     preconditionsModel.addChangeListener(this);
     postconditionsModel.addChangeListener(this);
     preconditionsModel.addRenameListener(this);
     postconditionsModel.addRenameListener(this);
     reset(input);
  }

  public void reset(LogicalFn[] input) {
     int r = data.size();
     data.removeAllElements();
     validityInfo.removeAllElements();

     if ( r != 0 ) fireTableRowsDeleted(0,r-1);

     Vector items = getConditions();
     for(int i = 0; i < input.length; i++ ) {
        data.addElement(input[i].toString());
        validityInfo.addElement(isValid(input[i],items));
     }
     fireTableRowsInserted(0,input.length-1);
  }
  
  public LogicalFn[] getData() {
     // Save only valid data
     Vector valid = new Vector();
     for(int i = 0; i < data.size(); i++ )
        if ( validityInfo.elementAt(i).equals(Boolean.TRUE) )
	   valid.addElement(data.elementAt(i));

     LogicalFn[] output = new LogicalFn[valid.size()];
     for(int i = 0; i < valid.size(); i++ )
        output[i] = (LogicalFn)ZeusParser.Expression((String)valid.elementAt(i));
     return output; 
  }

  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        data.removeElementAt(rows[i]-i);     
        validityInfo.removeElementAt(rows[i]-i);     
        fireTableRowsDeleted(rows[i]-i,rows[i]-i);
     }
     fireTableStructureChanged(); // swing bug? force redraw of table
     fireChanged();
  }                             

  public void addNewRow()  {
     data.addElement("true");
     Vector items = getConditions();
     validityInfo.addElement(isValid("true",items));
     int size = data.size();
     fireTableRowsInserted(size-1,size-1);
     fireTableStructureChanged(); // swing bug? force redraw of table
     fireChanged();
  }

  protected Vector getConditions() {
     Vector items = new Vector();

     Fact[] facts = preconditionsModel.getData();
     for(int i = 0; i < facts.length; i++ )
        items.addElement(facts[i].getId());

     facts = postconditionsModel.getData();
     for(int i = 0; i < facts.length; i++ )
        items.addElement(facts[i].getId());	
     return items;
  }

  protected Boolean isValid(LogicalFn fn, Vector items) {
     // Note: only a surface check is performed
     // Thus for expressions of the form "?man.name.firstname"
     // only the fact that ?man is a valid reference will be checked
     // type checking of the ".name.firstname" is not performed

     Vector vars = fn.variables();
     for(int i = 0; i < vars.size(); i++ )
        if ( !items.contains(vars.elementAt(i).toString()) )
	   return Boolean.FALSE;
     return Boolean.TRUE;
  }
  protected Boolean isValid(String input, Vector items) {
     ValueFunction fn = ZeusParser.Expression(input);
     if ( (fn != null) && (fn instanceof LogicalFn) )
        return isValid((LogicalFn)fn,items);
     else
        return new Boolean(false);
  }

  
  // ----------------------------------------------------------------------

  public int     getColumnCount()                 { return columnNames.length;}
  public boolean isCellEditable(int row, int col) { return true; }
  public int     getRowCount()                    { return data.size(); }
  public String  getColumnName(int col)           { return columnNames[col]; }
  public boolean isValidEntry(int row, int col)   { return validityInfo.elementAt(row).equals(Boolean.TRUE); }

  public Object getValueAt (int row, int column)  {
     switch(column) {
        case CONSTRAINT:
             return data.elementAt(row);
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column)  {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug 
    if (aValue.toString().equals(""))
      return;

     String old = (String)data.elementAt(row);
     String input;
     Vector items;
     switch(column) {
        case CONSTRAINT:
             input = (String)aValue;          
             if ( input.equals(old) )
                return;
             else {
                data.setElementAt(input,row);
                items = getConditions();
                validityInfo.setElementAt(isValid(input,items),row);
                fireTableCellUpdated(row,column);
                fireChanged();
             }
             break;
     }
  }

  public void stateChanged(ChangeEvent e) {
     // Preconditions/Postconditions have changed!
     // NEED to verify all applicability constraints!!
     if ( e.getSource() == preconditionsModel ||
          e.getSource() == postconditionsModel ) {
        Vector items = getConditions();
        for(int i = 0; i < data.size(); i++ ) {
           String s = (String)data.elementAt(i);
           validityInfo.setElementAt(isValid(s,items),i);
        }
        fireTableDataChanged();
     }
  }
  public void nameChanged(RenameEvent e) {
     // Preconditions/Postconditions ids have changed!
     // NEED to update all ids in applicability constraints!!
     String prev = (String)e.getOriginal();
     String curr = (String)e.getCurrent();
     for(int i = 0; i < data.size(); i++ ) {
        String s = (String)data.elementAt(i);
        data.setElementAt(Misc.substitute(s,prev,curr),i);
     }
     fireTableDataChanged();
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
}
