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



package zeus.agentviewer;

import javax.swing.table.*;
import java.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.actors.*;
import zeus.gui.editors.*;
import zeus.util.Misc;

public class FactTableModel  extends AbstractTableModel implements
                                                            ValidatingModel{
      static final int ATTRIBUTE = 0;
      static final int VALUE = 1;

      protected String[]  columnNames = { "Attribute", "Value" };
      private Fact   fact ;
      OntologyDb ontologyDb;

//------------------------------------------------------------------------------
      public FactTableModel(OntologyDb ontology){
          ontologyDb = ontology;
          this.fact = null;

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
           return columnNames.length;
       }
//------------------------------------------------------------------------------
       public boolean isCellEditable(int row, int col) {
          return  (col == VALUE);
       }


//--------------------------------------------------------------------------
  public void setValueAt(Object aValue, int row, int column)   {

     String fValue = (fact.listValues())[row].toString();
     String fAttr =  (fact.listAttributes())[row];


     String value = (aValue == null) ? null : ((String)aValue).trim();
     if ( value.equals("") ) value = null;

     if ( value == null ) {
        if ( fValue == null )
           return;
        else {
           fValue = value;
           fact.setValue(fAttr,fact.newVar());
        }
     }
     else {
        // always use this keyword for ?this reference!
        value = Misc.substitute(value,fact.getId(),Fact.THIS);
        if (fValue != null && fValue.equals(value) )
           return;
        else {
          fValue = value;
           ValueFunction fn = ZeusParser.Expression(fValue);
           if ( fn != null )
              fact.setValue(fAttr,fn);
        }
     }
     fireTableCellUpdated(row,column);
  }



//--------------------------------------------------------------------------
      public boolean isValidEntry(int row, int column) {
          switch(column) {
            case VALUE:
             if ( (fact.listValues())[row] == null )
                return true;
             else if ( ((fact.listValues())[row]).equals("") )
                return true;
             else
                return ZeusParser.Expression((fact.listValues())[row].toString()) != null;
           case ATTRIBUTE:
             return true;
          }
          return false; // sh never get here
      }

//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {

             if (col == ATTRIBUTE)
              return (fact.listAttributes())[row];
             else if (col == VALUE )
              return (fact.listValues())[row].toString();
             else
              return  new String("Error in AttributesTableModel at getValueAt");
       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  columnNames[col];
       }
//------------------------------------------------------------------------------
       public void setFact(String factName){
              if ( factName == null ) return;
              fact = ontologyDb.getFact(Fact.VARIABLE,factName);
              System.out.println(fact);
              fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public Fact getFact(){
           return fact;
       }
//------------------------------------------------------------------------------

}
