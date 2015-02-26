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
* AcquaintanceModel.java
*
* The underlying model for the Acquaintance Table
*****************************************************************************/

package zeus.generator.agent;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;

public class AcquaintanceModel extends AbstractTableModel
                              implements ChangeListener {
  static final int ALL    = 0;
  static final int PEERS  = 1;
  static final int OTHERS = 2;

  static final int AGENT    = 0;
  static final int RELATION = 1;
  static final int ID       = 2;

  public static Vector RELATIONS_LIST = null;
  public static final String PEER_RELATION =
     SystemProps.getProperty("system.organisation.relations.default");

  static {
     String system = SystemProps.getProperty("system.organisation.relations");
     String user = SystemProps.getProperty("user.organisation.relations");
     String sep = SystemProps.getProperty("file.separator");

     StringTokenizer st = new StringTokenizer(system,sep);
     RELATIONS_LIST = new Vector(100);

     String token;
     while( st.hasMoreTokens() ) {
        token = st.nextToken();
        if ( !RELATIONS_LIST.contains(token) )
           RELATIONS_LIST.addElement(token);
     }

     if ( user != null ) {
        st = new StringTokenizer(user,sep);
        while( st.hasMoreTokens() ) {
           token = st.nextToken();
           if ( !RELATIONS_LIST.contains(token) )
              RELATIONS_LIST.addElement(token);
        }
     }
     if ( !RELATIONS_LIST.contains(PEER_RELATION) )
        RELATIONS_LIST.addElement(PEER_RELATION);
  };



  protected String[]       columnNames  = { "Agent", "Relation" };  
  protected Vector         data         = new Vector(100);
  protected int            filter       = ALL;
  protected int            selectedRow  = -1;
  protected AbilityModel   abilityModel;
  protected GeneratorModel genmodel;
  protected OntologyDb     ontologyDb;
  
  public AcquaintanceModel(GeneratorModel genmodel,
                           OntologyDb ontologyDb,
                           AbilityModel abilityModel,
                           Acquaintance[] input) {

    this.abilityModel = abilityModel;
    this.genmodel = genmodel;
    reset(input);
    ontologyDb.addChangeListener(this);
    genmodel.addChangeListener(this);
  }

  public void reset(Acquaintance[] input) {
    int size = data.size();
    data.removeAllElements();
    if ( size != 0 )
       fireTableRowsDeleted(0,size-1);
    filter = ALL;
    selectRow(-1);
    if ( input == null || input.length == 0 ) return;
    for(int i = 0; i < input.length; i++ )
       data.addElement(input[i]);
    fireTableRowsInserted(0,input.length-1);
  }


  public void setFilter(int f) {
    if ( filter == f ) return;
    filter = f;
    selectRow(-1);
    fireTableDataChanged();
  }

  public void selectRow(int row) {
    selectedRow = row;
    if ( abilityModel != null ) {
       if ( selectedRow != -1 )
          abilityModel.reset(getAcquaintanceAt(row));
       else
          abilityModel.reset(null);
    }
  }

  public void addNewRow() {
    String agentId = genmodel.createNewAgentId();
    genmodel.createNewAgent(agentId);
    Acquaintance a = new Acquaintance(agentId,PEER_RELATION);
    data.addElement(a);
    selectRow(-1);
    int size = data.size();
    fireTableRowsInserted(size-2,size-1);
    fireChanged();
  }

  public void addNewRow(String agent) {
    if ( contains(agent) ) return;

    String agentId = genmodel.reverseAgentNameLookup(agent);
    Acquaintance a = new Acquaintance(agentId,PEER_RELATION);
    data.addElement(a);
    selectRow(-1);
    int size = data.size();
    fireTableRowsInserted(size-2,size-1);
    fireChanged();
  }

  public void removeRow(int row) {
     Acquaintance a = getAcquaintanceAt(row);
     data.removeElement(a);
     selectRow(-1);
     fireTableRowsDeleted(row,row);
     fireChanged();
  }

  public Acquaintance[] getData() {
     selectRow(selectedRow); // save last changed first as side-effect
     Acquaintance[] out = new Acquaintance[data.size()];
     for(int i = 0; i < out.length; i++ )
        out[i] = (Acquaintance)data.elementAt(i);
     return out;
  }
    
  // ---- AbstractTableModel Methods -----------------------------------

  public int     getColumnCount()             { return columnNames.length; }
  public boolean isCellEditable(int r, int c) { return true; }
  public String  getColumnName(int column)    { return columnNames[column]; }

  public int getRowCount() {
    int count = 0;
    Acquaintance a;

    switch(filter) {
       case ALL:
            return data.size();

       case PEERS:
            for(int i = 0; i < data.size(); i++ ) {
               a = (Acquaintance)data.elementAt(i);
               if ( a.getRelation().equals(PEER_RELATION) )
                  count++;
            }
            return count;
             
       case OTHERS:
            for(int i = 0; i < data.size(); i++ ) {
               a = (Acquaintance)data.elementAt(i);
               if ( !(a.getRelation().equals(PEER_RELATION)) )
                  count++;
            }
            return count;
    }
    return 0;
  }

  public void setValueAt(Object aValue, int row, int column) {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug
    if (aValue.toString().equals(""))
       return;

    Acquaintance a = getAcquaintanceAt(row);
    String id = a.getName();
    String name = genmodel.getAgentName(id);
    String relation = a.getRelation();

    switch(column) {
       case AGENT:
            String agent = (String)aValue;
            if ( name.equals(agent) ) return;
            if ( contains(agent) ) return;
            String newId = genmodel.reverseAgentNameLookup(agent);
            if ( newId != null )
	       a.setName(newId);
            else
               genmodel.renameAgent(id,agent);
            fireTableCellUpdated(row,column);
            fireChanged();
            break;
       case RELATION:
            if ( relation.equals(aValue) ) return;
            a.setRelation((String)aValue);
            fireTableCellUpdated(row,column);	    
            fireChanged();
            break;
    }
  }

  public Object getValueAt(int row, int column) {
    Acquaintance a = getAcquaintanceAt(row);
    switch(column) {
       case AGENT:
            return genmodel.getAgentName(a.getName());
       case ID:
            return a.getName();
       case RELATION:
            return a.getRelation();                     
    }
    return null; // sh never get here
  }

  protected Acquaintance getAcquaintanceAt(int row) {
    Acquaintance a = null;
    int position;

    switch(filter) {
       case ALL:
            return (Acquaintance)data.elementAt(row);

       case PEERS:
            position = -1;
            for(int i = 0; i < data.size(); i++ ) {
               a = (Acquaintance)data.elementAt(i);
               if ( a.getRelation().equals(PEER_RELATION) )
                  position++;
               if ( position == row )
                  return a;
            }
            break;

       case OTHERS:
            position = -1;
            for(int i = 0; i < data.size(); i++ ) {
               a = (Acquaintance)data.elementAt(i);
               if ( !(a.getRelation().equals(PEER_RELATION)) )
                  position++;
               if ( position == row )
                  return a;
            }
            break;            
    }
    Core.ERROR(false,1,this);
    return null; // sh never reach here
  }


  protected boolean contains(String agent) {
    // check that model does not already contain agent name
    Acquaintance a;
    for(int i = 0; i < data.size(); i++ ) {
       a = (Acquaintance)data.elementAt(i);
       if ( agent.equals(genmodel.getAgentName(a.getName())) ) {
          JOptionPane.showMessageDialog(null,
             "Attempting to add an already\nexisting agent",
             "Error", JOptionPane.ERROR_MESSAGE);
          return true;
       }
    }
    return false;
  }

  public void stateChanged(ChangeEvent e) {
     // Either underlying ontology has changed
     // or genmodel has changed

     Object src = e.getSource();
     if ( src == ontologyDb ) {
        // REM need to revalidate all abilities
     }
     else if ( src == genmodel ) {
        fireTableDataChanged();
     }
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
