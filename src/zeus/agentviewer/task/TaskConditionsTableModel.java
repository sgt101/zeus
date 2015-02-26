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
import zeus.concepts.Fact;



public class TaskConditionsTableModel  extends AbstractTableModel {

      static final int TYPE      = 0;
      static final int INSTANCE  = 1;
      static final int MODIFIERS = 2;

      protected static final String[] columnNames     = {
        "Fact Type", "Instance", "Modifiers"
      };
      private Fact[] data = null;
//------------------------------------------------------------------------------
      public TaskConditionsTableModel() {
          data = null;
      }
//------------------------------------------------------------------------------
       public int getRowCount() {
             if (data == null)
              return 0;
             else
              return data.length;
       }
//------------------------------------------------------------------------------
       public int getColumnCount() {
           return columnNames.length;
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
            Fact f =  data[row];
            switch(col) {
                 case TYPE:
                  return f.getType();
                 case INSTANCE:
                  return f.getId();
                 case MODIFIERS:
                  // Note: This should really be done by a cell renderer
                  String s = "";
                  int x = f.getModifiers();

                  if ( Fact.isNegative(x)   ) s += "NOT ";
                  if ( Fact.isReadOnly(x)   ) s += "READ_ONLY";
                  if ( Fact.isLocal(x)      ) s += "LOCAL";
                  if ( Fact.isReplaced(x)   ) s += "REPLACED";
                  if ( Fact.isSideEffect(x) ) s += "SIDE_EFFECT";
                  return s;
            }
            return null;
       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
           return columnNames[col];
       }

//------------------------------------------------------------------------------
       public void setFacts(Fact[] data){
            this.data = data;
            fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public Fact getFact(int row){
           return data[row];
       }
}
