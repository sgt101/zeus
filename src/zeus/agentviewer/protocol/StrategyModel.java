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



package zeus.agentviewer.protocol;

import javax.swing.table.*;
import zeus.concepts.*;
import zeus.util.*;

public class StrategyModel  extends AbstractTableModel {

      static final int MODE       = 0;
      static final int FACT       = 1;
      static final int AGENTS     = 2;
      static final int RELATIONS  = 3;
      static final int STRATEGY   = 4;
      static final int PARAMETERS = 5;

      static final int      USE    = StrategyInfo.USE;
      static final int      NO_USE = StrategyInfo.NO_USE;

      protected String[]  columnNames     = {
           "Mode", "Fact", "Agents", "Relations", "Strategy", "Parameters"
      };

      private StrategyInfo[]   data;


//------------------------------------------------------------------------------
      public StrategyModel() {
         data = null;
      }
//------------------------------------------------------------------------------
      public int getRowCount() {
         return (data == null) ? 0 : data.length;
      }
//------------------------------------------------------------------------------
      public int getColumnCount() {
         return columnNames.length;
      }
//------------------------------------------------------------------------------
      public Object getValueAt(int row, int column) {
         StrategyInfo info = data[row];
         switch(column) {
            case MODE:
                 return new Boolean(info.getType() == USE);

            case AGENTS:
	         return info.getAgents();

	    case RELATIONS:
                 return info.getRelations();

            case STRATEGY:
                 return (info.getType() == USE) ? info.getStrategy() : null;

            case FACT:
                 return (info.getFact()).getType();

            case PARAMETERS:
                 return info.getParameters();
         }
         return null;
     }
//------------------------------------------------------------------------------
     public String getColumnName(int col) {
        return  columnNames[col];
     }
//------------------------------------------------------------------------------
     public Fact getFact(int row) {
        StrategyInfo info = data[row];
        return info.getFact();
     }
//------------------------------------------------------------------------------
     public void setStrategies(StrategyInfo[] data) {
        this.data = data;
        fireTableDataChanged();
     }
}
