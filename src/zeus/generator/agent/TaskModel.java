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
* TaskModel.java
*
* The underlying model for the Task Table 
*****************************************************************************/

package zeus.generator.agent;

import java.util.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.generator.*;

public class TaskModel extends AbstractTableModel
                       implements ChangeListener {

  static final int TASK = 0;
  static final int TYPE = 1;
  static final int ID   = 2;

  protected String[]          columnNames = { "Task", "Type" };
  protected Vector            data;
  protected GeneratorModel    genmodel;
  protected EventListenerList changeListeners = new EventListenerList();

  protected boolean[]         permissions;

  public TaskModel(String[] taskId, GeneratorModel genmodel) {

    permissions = new boolean[] { true, false, false };
    this.genmodel = genmodel;
    genmodel.addChangeListener(this);

    data = new Vector();
    String[] element;
    for(int i = 0; i < taskId.length; i++ ) {
       element = new String[3];
       element[ID] = taskId[i];
       element[TASK] = genmodel.getTaskName(taskId[i]);
       element[TYPE] = genmodel.getTaskType(taskId[i]);
       data.addElement(element);
    }
    genmodel.addChangeListener(this);
  }

  public int     getColumnCount()                 { return columnNames.length; }
  public int     getRowCount()                    { return data.size(); }

  public boolean isCellEditable(int row, int col) {
    return permissions[col];
  }

  public String  getColumnName(int col)           { return columnNames[col]; }

  public Object  getValueAt(int row, int col) {
     return ((String[])data.elementAt(row))[col];
  }

  public String[] getData() {
     String[] output = new String[data.size()];
     String[] array;
     for(int i = 0; i < output.length; i++ ) {
        array = (String[])data.elementAt(i);
        output[i] = array[ID];
     }
     return output;
  }

  public void setValueAt(Object aValue, int row, int column) {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug
    if (aValue == null || aValue.toString().equals(""))
       return;

    String[] element = (String[])data.elementAt(row);
    switch(column) {
       case TASK:
            String task = (String)aValue;
            if ( element[TASK].equals(task) ) return;
            if ( contains(task) ) return;
            String newId = genmodel.reverseTaskNameLookup(task);
            if ( newId != null ) {
               element[ID] = newId;
               element[TASK] = task;
            }
            else {
               genmodel.renameTask(element[ID],task);
               element[TASK] = task;
            }
	    fireTableCellUpdated(row,column);
            fireChanged();
            break;
    }
  }

  public void addNewRow(String type) {
    String taskId = genmodel.createNewTaskId();
    genmodel.createNewTask(taskId,type);
    String[] element = new String[3];
    element[ID] = taskId;
    element[TASK] = genmodel.getTaskName(taskId);
    element[TYPE] = genmodel.getTaskType(taskId);
    data.addElement(element);
    int size = data.size();
    fireTableRowsInserted(size-2,size-1);
    fireChanged();
  }

  public void addNewRow(String type, String task) {
    if ( contains(task) ) return;

    String taskId = genmodel.reverseTaskNameLookup(task);
    String[] element = new String[3];
    element[ID] = taskId;
    element[TASK] = task;
    element[TYPE] = type;
    data.addElement(element);
    int size = data.size();
    fireTableRowsInserted(size-2,size-1);
    fireChanged();
  }

  public void removeRow(int row) {
     data.removeElementAt(row);
     fireTableRowsDeleted(row,row);
     fireChanged();
  }

  protected boolean contains(String task) {
    // check that model does not already contain task
    String[] element;
    for(int i = 0; i < data.size(); i++ ) {
       element = (String[])data.elementAt(i);
       if ( task.equals(element[TASK]) ) {
          JOptionPane.showMessageDialog(null,
             "Attempting to add an already\nexisting task",
             "Error", JOptionPane.ERROR_MESSAGE);
          return true;
       }
    }
    return false;
  }

  public void stateChanged(ChangeEvent e) {
    for(int i = 0; i < data.size(); i++ ) {
       String[] element = (String[])data.elementAt(i);
       if ( !genmodel.containsTask(element[ID]) ) {
          data.removeElementAt(i);
          fireTableRowsDeleted(i,i);
          i--;
       }
       else {
          element[TASK] = genmodel.getTaskName(element[ID]);
          element[TYPE] = genmodel.getTaskType(element[ID]);
       }
    }
    fireTableDataChanged();
  }

  public void setWriteable(int column) {
    permissions[column] = true;
  }

  public void setReadOnly(int column) {
    permissions[column] = false;
  }

  //------------------------------------------------------------------------
  // Event Methods
  //------------------------------------------------------------------------

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
