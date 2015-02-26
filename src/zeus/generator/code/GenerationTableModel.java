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
* GenTableModel.java
*
* The underlying model for the Generator Table
*****************************************************************************/

package zeus.generator.code;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;


public class GenerationTableModel extends AbstractTableModel
                                  implements ChangeListener {

  static final int AGENT_FILTER   = 1;
  static final int TASK_FILTER    = 2;
  static final int UTILITY_FILTER = 3;

  protected static final String[] columnNames = {
     "Name", "Type", "Command Line"
  };

  protected Vector data = new Vector();

  protected boolean hideAgents = false;
  protected boolean hideTasks = false;
  protected boolean hideUtilities = false;

  protected GenerationPlan  genplan;

  public GenerationTableModel(GenerationPlan genplan) {
    this.genplan = genplan;
    genplan.addChangeListener(this);
    refresh();
  }

  public void setFilter(int f) {
    if      (f == AGENT_FILTER)    hideAgents = !hideAgents;
    else if (f == TASK_FILTER)     hideTasks = !hideTasks;
    else if (f == UTILITY_FILTER)  hideUtilities = !hideUtilities;
    refresh();
  }

  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        String[] entry = (String[])data.elementAt(rows[i]-i);
        genplan.removeEntry(entry[GenerationInfo.TYPE],
	                    entry[GenerationInfo.ID]);
     }
  }

  protected synchronized void refresh()  {
    int p_size = data.size();
    data.removeAllElements();
    String[][] entry;

    if ( !hideUtilities ) {
       entry = genplan.summarizeNameservers();
       for(int i = 0; i < entry.length; i++ )
          data.addElement(entry[i]);

       entry = genplan.summarizeFacilitators();
       for(int i = 0; i < entry.length; i++ )
          data.addElement(entry[i]);

       entry = genplan.summarizeVisualisers();
       for(int i = 0; i < entry.length; i++ )
          data.addElement(entry[i]);

       entry = genplan.summarizeDbProxys();
       for(int i = 0; i < entry.length; i++ )
          data.addElement(entry[i]);
    }

    if ( !hideAgents ) {
       entry = genplan.summarizeSelectedAgents();
       for(int i = 0; i < entry.length; i++ )
          data.addElement(entry[i]);
    }

    if ( !hideTasks ) {
       entry = genplan.summarizeSelectedTasks();
       for(int i = 0; i < entry.length; i++ )
          data.addElement(entry[i]);
    }
    if ( p_size > 0 ) fireTableRowsDeleted(0,p_size-1);
    int c_size = data.size();
    if ( c_size > 0 ) fireTableRowsInserted(0,c_size-1);
  }

  public int     getColumnCount()                 { return columnNames.length; }
  public int     getRowCount()                    { return data.size(); }
  public boolean isCellEditable(int row, int col) { return false;}
  public String  getColumnName(int col)           { return columnNames[col]; }

  public Object getValueAt(int row, int column) {
    try {
       String[] entry = (String[])data.elementAt(row);
       return entry[column];
    }
    catch(ArrayIndexOutOfBoundsException e) {
       return null;
    }
  }

  public void setValueAt(Object aValue, int row, int column) {
  }

  public void stateChanged(ChangeEvent e) {
    // generationPlan state changed recompute all entries;
    refresh();
  }
}
