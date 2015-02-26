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



package zeus.agentviewer.acquaintances;

import javax.swing.table.*;
import java.util.*;
import zeus.concepts.*;


public class AttributesTableModel  extends AbstractTableModel {

      private String   ATTRIBUTE = "Attribute";
      private String   VALUE     = "Value";

      private String[] header = {ATTRIBUTE,VALUE};
      private Fact   fact ;
      private AbilitiesTableModel abilitiesTableModel;
//------------------------------------------------------------------------------
      public AttributesTableModel(AbilitiesTableModel abilitiesTableModel){
          this.fact = null;
          this.abilitiesTableModel = abilitiesTableModel;
      }
//------------------------------------------------------------------------------
       public int getRowCount() {
          if (fact == null)
           return 0;
          else
           return fact.listAttributes().length;
       }
//------------------------------------------------------------------------------
       public int getColumnCount(){
           return header.length;
       }
//------------------------------------------------------------------------------
       public boolean isCellEditable(int row, int col) {
          return false;
       }
//------------------------------------------------------------------------------
       public void setValueAt(Object value, int row, int col) {
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {

             if (getColumnName(col).equals(ATTRIBUTE))
              return (fact.listAttributes())[row];
             else if (getColumnName(col).equals(VALUE))
              return (fact.listValues())[row];
             else
              return  new String("Error in AgentAbilityAttributesTableModel at getValueAt");
       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  header[col];
       }
//------------------------------------------------------------------------------
       public void setFact(Fact fact){
            this.fact = fact;
            fireTableDataChanged();
       }
//------------------------------------------------------------------------------

}