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
* AbilityModel.java
*
* The underlying model for the Ability Table 
*****************************************************************************/

package zeus.generator.agent;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;
import zeus.generator.util.*;

public class AbilityModel extends AbstractTableModel
                       implements ChangeListener {

  static final int TYPE    = 0;
  static final int TIME    = 1;
  static final int COST    = 2;
  static final int ABILITY = 3;

  static int count  = 0;

  protected String[]        columnNames = { "Ability Type", "Time", "Cost" };  
  protected int             selectedRow = -1;
  protected AttributeModel  attributeModel;
  protected OntologyDb      ontologyDb;  
  protected Vector          data = new Vector(100);
  protected Acquaintance    acquaintance = null;

	
  public AbilityModel(OntologyDb ontologyDb,
                      AttributeModel attributeModel) {

     this.ontologyDb = ontologyDb;
     this.attributeModel = attributeModel;
     ontologyDb.addChangeListener(this);
  }

  public void reset(Acquaintance input) {
     if ( acquaintance != null )
        acquaintance.setAbilities(data);

     int size = data.size();

     this.acquaintance = input;
     data.removeAllElements();
     selectRow(-1);

     if ( size != 0 )
        fireTableRowsDeleted(0,size-1);

     if ( input != null ) {
        AbilitySpec[] in = input.getAbilities();
        for(int i = 0; i < in.length; i++ ) 
           data.addElement(in[i]);
        fireTableRowsInserted(0,in.length-1);
     }
  }
  
  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        data.removeElementAt(rows[i]-i);
        fireTableRowsDeleted(rows[i]-i,rows[i]-i);
     }
     selectRow(-1);
     fireChanged();
  }

  public void selectRow(int row) {
     selectedRow = row;
     if ( attributeModel != null ) {
        if ( selectedRow >= 0 ) {
           AbilitySpec a = (AbilitySpec)data.elementAt(selectedRow);
           attributeModel.reset(a.getFact());
        }
        else
           attributeModel.reset(null);
     }
  }

  public void addNewRows(String[] names)  {
     if ( names == null || names.length == 0 ) return;
     Fact f;
     AbilitySpec[] input = new AbilitySpec[names.length];
     for(int i = 0; i < names.length; i++ ) {
        f = ontologyDb.getFact(Fact.VARIABLE,names[i]);
        input[i] = new AbilitySpec(f,0,0);
     }
     addRows(input);
  }

  public void addRows(AbilitySpec[] input) {
     if ( input == null     ) return;
     if ( input.length == 0 ) return;

     AbilitySpec a1;
     String id;
     int size = data.size();
     for(int i = 0; i < input.length; i++ ) {
        a1 = new AbilitySpec(input[i]);
        id = a1.getFact().ID();
        while( contains(id) )
           id += (count++);
        a1.getFact().setId(id);
        data.addElement(a1);
     }
     selectRow(-1);
     fireTableRowsInserted(size-1,size+input.length-1);
     fireChanged();
  }
  protected boolean contains(String id) {
    // check that model does not already contain task
    AbilitySpec a;
    for(int i = 0; i < data.size(); i++ ) {
       a = (AbilitySpec)data.elementAt(i);
       if ( id.equals(a.getFact().ID()) )
          return true;
    }
    return false;
  }

  
  // ----------------------------------------------------------------------

  public int     getColumnCount()                 { return columnNames.length; }
  public int     getRowCount()                    { return data.size(); }
  public boolean isCellEditable(int row, int col) { return col != TYPE; }
  public String  getColumnName(int col)           { return columnNames[col]; }

  public Object getValueAt(int row, int column)  {
     AbilitySpec a = (AbilitySpec)data.elementAt(row);
     switch(column) {
        case TYPE:
             return a.getType();
        case COST:
             return new Double(a.getCost());
        case TIME:
             return new Integer(a.getTime());
        case ABILITY:
             return a;
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column)  {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug 
    if (aValue.toString().equals(""))
       return;

     AbilitySpec a = (AbilitySpec)data.elementAt(row);
     switch(column) {
        case TYPE:
             Core.ERROR(null,1,this);
             break;
        case TIME:
             int time = Integer.parseInt((String)aValue);
             if ( time == a.getTime() ) return;
             a.setTime(time);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;
        case COST:
             double cost = (Double.valueOf((String)aValue)).doubleValue();
             if ( Math.abs(cost - a.getCost()) < 1.0e-12 ) return;
             a.setCost(cost);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;
     }
  }
  
  public void stateChanged(ChangeEvent e) {
     // Underlying ontology has changed!!
     // NEED to verify all facts!!
  }

  protected EventListenerList  changeListeners = new EventListenerList();
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
