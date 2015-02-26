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
* TaskTableModel.java
*
* The underlying model for the Task Table
*****************************************************************************/

package zeus.generator;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;

public class TaskTableModel extends AbstractTableModel
                             implements ChangeListener {

  static final int TASK = 0;
  static final int TYPE = 1;
  static final int ID   = 2;

  protected String[]   columnNames = { "Task", "Type" };
  protected Object[][] data        = new Object[0][2];
  protected boolean    isEditable  = false;

  public void setEditable(boolean isEditable) {
     this.isEditable = isEditable;
  }

  protected GeneratorModel genmodel;

  public TaskTableModel(GeneratorModel genmodel) {
    this.genmodel = genmodel;
    genmodel.addChangeListener(this);
    data = genmodel.getTaskData();
  }

  public int     getColumnCount()                 { return columnNames.length; }
  public int     getRowCount()                    { return data.length; }
  public boolean isCellEditable(int row, int col) { return isEditable && col == TASK; }
  public String  getColumnName(int col)           { return columnNames[col]; }
  public Object  getValueAt(int row, int col)     { return data[row][col]; }

  public void setValueAt(Object aValue, int row, int column) {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug
    if (aValue == null || aValue.toString().equals(""))
       return;

    switch(column) {
       case TASK:
            if ( data[row][TASK].equals((String)aValue) ) return;
            genmodel.renameTask((String)data[row][ID],(String)aValue);
            break;
    }
  }

  public void addNewRow(int type) {
    genmodel.createNewTask(AbstractTask.getTypeName(type));
  }

  public void stateChanged(ChangeEvent e) {
    int size = data.length;
    data = genmodel.getTaskData();
    if ( size != 0 )
       fireTableRowsDeleted(0,size-1);

    fireTableDataChanged();
  }
}
