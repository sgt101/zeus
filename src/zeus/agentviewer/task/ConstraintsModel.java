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



package zeus.agentviewer.task;

import javax.swing.table.*;
import java.util.*;
import zeus.concepts.fn.*;


public class ConstraintsModel  extends AbstractTableModel {

      private final int CONSTRAINT = 0;

      private String columnName = "Constraint";
      private LogicalFn[]   data ;
//------------------------------------------------------------------------------
      public ConstraintsModel(){
          this.data = null;
      }
//------------------------------------------------------------------------------
       public int getRowCount() {
          if (data == null)
           return 0;
          else
           return data.length;
       }
//------------------------------------------------------------------------------
       public int getColumnCount(){
           return 1;
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
             LogicalFn aValue = data[row];

             if (col == CONSTRAINT)
              return aValue.toString();
             else
              return new String("Error in Constraint Model");

       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  columnName;
       }
//------------------------------------------------------------------------------
       public void setConstraints(LogicalFn[] data){
            this.data = data;
            fireTableDataChanged();
       }
//------------------------------------------------------------------------------

}