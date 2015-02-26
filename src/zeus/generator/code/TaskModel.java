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
/*
Change Log.
----------
28/08/00 alterations to include a task external
*/

/*****************************************************************************
* TaskModel.java
*
* The underlying model for the Task Table
*****************************************************************************/

package zeus.generator.code;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;


public class TaskModel extends UtilityModel {

  static final int GENERATE    = 0;
  static final int STATUS      = 1;
  static final int NAME        = 2;
     // 25/08/00 addition by simon
  static final int EXTERNAL    = 3;
  protected static final String[] columnNames = {
       // 25/08/00 addition by simon
     "Generate", "Status", "Name","External Program"
  };

  protected Vector data = new Vector();
  protected GenerationPlan  genplan;

  public TaskModel(GenerationPlan genplan) {
    this.genplan = genplan;
    genplan.addChangeListener(this);
    refresh();
  }

  public void addNewRow() {
  }
  public void removeRows(int[] rows) {
  }
  protected void refresh()  {
    data.removeAllElements();
   TaskInfo[] info = genplan.getTasks();
    for(int i = 0; i < info.length; i++ )
       data.addElement(info[i]);
    fireTableDataChanged();
  }

  public int     getColumnCount()                 { return columnNames.length; }
  public int     getRowCount()                    { return data.size(); }
  public String  getColumnName(int col)           { return columnNames[col]; }

  public boolean isCellEditable(int row, int col) {
     switch(col) {
        case GENERATE:
        {
             return true; 
        }
        case EXTERNAL:{         
             return true; 
        }
	    case NAME:
        case STATUS:
             return false;
     }
     return false;
  }

  public Object getValueAt(int row, int column) {
     TaskInfo info = (TaskInfo)data.elementAt(row);
     switch(column) {
        case GENERATE:
             return new Boolean(info.generate);

	    case STATUS:
             return info.status;

        case NAME:
             return info.name;
       
        case EXTERNAL: 
            return info.task_external; 
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column) {
     TaskInfo info = (TaskInfo)data.elementAt(row);
     switch(column) {
        case GENERATE:{
             info.generate = updateBoolean(info.generate,aValue);
             break;}

	    case STATUS:{
             Core.ERROR(null,1,this);
             break;}

        case NAME:{
             Core.ERROR(null,2,this);
             break;}
             
        case EXTERNAL:{
             System.out.println ("Setting external value"); 
             info.task_external = updateString(info.task_external,aValue);
             if ( changed ) genplan.setTask(info);
             break;}
     }
     if ( changed ) genplan.setTask(info);
  }
}