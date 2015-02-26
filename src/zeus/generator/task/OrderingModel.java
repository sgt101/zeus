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
* OrderingModel.java
*
* The underlying model for the Preconditions Ordering Table 
*****************************************************************************/

package zeus.generator.task;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;
import zeus.generator.util.*;
import zeus.generator.util.*;
import zeus.generator.event.*;
import zeus.gui.editors.*;

public class OrderingModel extends AbstractTableModel
                           implements ChangeListener,
                                      ValidatingModel,
                                      RenameListener {

  static final int BEFORE = 0;
  static final int AFTER  = 1;

  protected static final String[] columnNames = { "Before", "After" };  

  protected EventListenerList changeListeners = new EventListenerList();
  protected Vector            data            = new Vector();
  protected Vector            validityInfo    = new Vector();
  protected BasicFactModel    preconditionsModel;

  public OrderingModel(BasicFactModel preconditionsModel,
                       Ordering[] input) {

     this.preconditionsModel = preconditionsModel;
     preconditionsModel.addChangeListener(this);
     preconditionsModel.addRenameListener(this);
     reset(input);
  }

  public void reset(Ordering[] input) {
     int r = data.size();
     data.removeAllElements();
     validityInfo.removeAllElements();

     if ( r != 0 ) fireTableRowsDeleted(0,r-1);

     Vector items = getPreconditionIds();
     for(int i = 0; i < input.length; i++ ) {
        data.addElement(input[i]);
        validityInfo.addElement(isValid(input[i],items));
     }
     fireTableRowsInserted(0,input.length-1);
  }
  
  public Ordering[] getData() {
     // Save only valid data
     Vector valid = new Vector();
     for(int i = 0; i < data.size(); i++ )
        if ( validityInfo.elementAt(i).equals(Boolean.TRUE) )
	   valid.addElement(data.elementAt(i));

     Ordering[] output = new Ordering[valid.size()];
     for(int i = 0; i < valid.size(); i++ )
        output[i] = (Ordering)valid.elementAt(i);
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

  public void addNewRows(String lhs, String[] rhs)  {
     Ordering c;
     int count = 0;
     int size = data.size();

     for(int i = 0; i < rhs.length; i++ ) {
        if ( additionOK(lhs,rhs[i],-1) ) {
           count++;
           c = new Ordering(lhs,rhs[i]);
           data.addElement(c);
           Vector items = getPreconditionIds();
           validityInfo.addElement(isValid(c,items));
        }
     }

     if ( count > 0 ) {
        fireTableRowsInserted(size-1,size-1+count);
        fireTableStructureChanged(); // swing bug? force redraw of table
        fireChanged();
     }
  }

  protected Vector getPreconditionIds() {
     Fact[] facts = preconditionsModel.getData();
     Vector items = new Vector();
     for(int i = 0; i < facts.length; i++ )
        items.addElement(facts[i].getId());
     return items;
  }

  protected Boolean isValid(Ordering c, Vector items) {
     boolean b = items.contains(c.getLHS()) && items.contains(c.getRHS());
     return new Boolean(b);
  }

  // ----------------------------------------------------------------------

  public int     getColumnCount()                 { return columnNames.length;}
  public boolean isCellEditable(int row, int col) { return true; }
  public int     getRowCount()                    { return data.size(); }
  public String  getColumnName(int col)           { return columnNames[col]; }
  public boolean isValidEntry(int row, int col)   { return validityInfo.elementAt(row).equals(Boolean.TRUE); }

  public Object getValueAt (int row, int column)  {
     Ordering c = (Ordering)data.elementAt(row);
     switch(column) {
        case BEFORE:
             return c.getLHS();
        case AFTER:
             return c.getRHS();
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column)  {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug 
    if (aValue.toString().equals(""))
      return;

     Ordering c = (Ordering)data.elementAt(row);
     String newId;
     Vector items;
     switch(column) {
        case BEFORE:
             newId = (String)aValue;          
             if ( newId.equals(c.getLHS()) )
                return;
             else if ( !additionOK(newId,c.getRHS(),row) )
                return;
             else {
                c.setLHS(newId);
                items = getPreconditionIds();
                validityInfo.setElementAt(isValid(c,items),row);		
                fireTableCellUpdated(row,column);
                fireChanged();
             }
             break;
        case AFTER:
             newId = (String)aValue;
             if ( newId.equals(c.getRHS()) )
                return;
             else if ( !additionOK(c.getLHS(),newId,row) )
                return;
             else {
                c.setRHS(newId);
                items = getPreconditionIds();
                validityInfo.setElementAt(isValid(c,items),row);		
                fireTableCellUpdated(row,column);
                fireChanged();
             }
             break;
     }
  }

  protected boolean additionOK(String lhs, String rhs, int row) {
    Ordering c;

    if ( lhs.equals(rhs) ) {
       JOptionPane.showMessageDialog(null,
          "Attempting to add ordering " + lhs + "  --> " + rhs,
          "Error", JOptionPane.ERROR_MESSAGE);
       return false;
    }

    for(int i = 0; i < data.size(); i++ ) {
       if ( i != row ) {
          c = (Ordering)data.elementAt(i);
          if ( c.getLHS().equals(lhs) && c.getRHS().equals(rhs) ) {
             JOptionPane.showMessageDialog(null,
                "Table already contains the ordering\n" + lhs + " < " + rhs,
                "Error", JOptionPane.ERROR_MESSAGE);
             return false;
          }
          else if ( c.getRHS().equals(lhs) && c.getLHS().equals(rhs) ) {
             JOptionPane.showMessageDialog(null,
                "Attempting to add ordering " + lhs + " < " + rhs + 
   	        "\nwhen table already contains ordering " + rhs + " < " + lhs,
                "Error", JOptionPane.ERROR_MESSAGE);
             return false;
          }
       }
    }
    return true;
  }

  public void stateChanged(ChangeEvent e) {
     // Preconditions have changed!
     // NEED to verify all ordering constraints!!
     Vector items = getPreconditionIds();
     for(int i = 0; i < data.size(); i++ ) {
        Ordering c = (Ordering)data.elementAt(i);
        validityInfo.setElementAt(isValid(c,items),i);
     }
     fireTableDataChanged();
  }
  public void nameChanged(RenameEvent e) {
     // Preconditions Ids have changed!
     // NEED to modify relevant ordering constraints!!
     String prev = (String)e.getOriginal();
     String curr = (String)e.getCurrent();
     for(int i = 0; i < data.size(); i++ ) {
        Ordering c = (Ordering)data.elementAt(i);
        if ( c.getLHS().equals(prev) ) c.setLHS(curr);
        if ( c.getRHS().equals(prev) ) c.setRHS(curr);
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