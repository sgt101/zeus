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
* ProtocolModel.java
*
* The underlying model for the Coordination Protocol Table
*****************************************************************************/

package zeus.generator.agent;

import java.util.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;

public class ProtocolModel extends AbstractTableModel {

  static final int TYPE     = 0;
  static final int PROTOCOL = 1;
  static final int STATE    = 2;

  protected String[]          columnNames = { "Type",  "Protocol", "State" };
  protected Object[][]        data;  
  protected EventListenerList changeListeners = new EventListenerList();
  protected Hashtable         store = new Hashtable();
  protected StrategyModel     strategyModel = null;
  protected OntologyDb        ontologyDb;
  protected int               selectedRow = -1;

  public ProtocolModel(OntologyDb ontologyDb,
                       StrategyModel strategyModel,
                       ProtocolInfo[] protocols) {

     this.strategyModel = strategyModel;
     this.ontologyDb = ontologyDb;

     String sep = SystemProps.getProperty("file.separator");
     String system1 = SystemProps.getProperty("system.protocols.initiator");
     String system2 = SystemProps.getProperty("system.protocols.respondent");
     String user1 = SystemProps.getProperty("user.protocols.initiator");
     String user2 = SystemProps.getProperty("user.protocols.respondent");

     StringTokenizer s1 = null, s2 = null, u1 = null, u2 = null;
     int count = 0;

     if ( system1 != null ) {
        s1 = new StringTokenizer(system1,sep);
        count += s1.countTokens();
     }
     if ( system2 != null ) {
        s2 = new StringTokenizer(system2,sep);
        count += s2.countTokens();
     }
     if ( user1 != null ) {
        u1 = new StringTokenizer(user1,sep);
        count += u1.countTokens();
     }
     if ( user2 != null ) {
        u2 = new StringTokenizer(user2,sep);
        count += u2.countTokens();
     }

     data = new Object[count][3];
     int i = 0;
     if ( system1 != null ) {
        while( s1.hasMoreTokens() ) {
           data[i][TYPE] = ProtocolInfo.INITIATOR;
           data[i][PROTOCOL] = s1.nextToken();
           data[i][STATE] = Boolean.FALSE;           
           i++;
        }        
     }
     if ( system2 != null ) {
        while( s2.hasMoreTokens() ) {
           data[i][TYPE] = ProtocolInfo.RESPONDENT;
           data[i][PROTOCOL] = s2.nextToken();
           data[i][STATE] = Boolean.FALSE;
           i++;
        }        
     }
     if ( user1 != null ) {
        while( u1.hasMoreTokens() ) {
           data[i][TYPE] = ProtocolInfo.INITIATOR;
           data[i][PROTOCOL] = u1.nextToken();
           data[i][STATE] = Boolean.FALSE;
           i++;
        }        
     }
     if ( user2 != null ) {
        while( u2.hasMoreTokens() ) {
           data[i][TYPE] = ProtocolInfo.RESPONDENT;
           data[i][PROTOCOL] = u2.nextToken();
           data[i][STATE] = Boolean.FALSE;
           i++;
        }        
     }

     for(int j = 0; j < data.length; j++ ) {
        for(int k = 0; k < protocols.length; k++ ) {
           if ( data[j][PROTOCOL].equals(protocols[k].getName()) ) {
              data[j][STATE] = Boolean.TRUE;
              store.put(protocols[k].getName(),protocols[k]);
              break;
           }
        }
     }
  }                     
  
  public int     getColumnCount()                 { return columnNames.length; }
  public int     getRowCount()                    { return data.length; }
  public boolean isCellEditable(int row, int col) { return col == STATE; }
  public String  getColumnName(int col)           { return columnNames[col]; }
  public Object  getValueAt(int row, int col)     { return data[row][col]; }

  public void setValueAt(Object aValue, int row, int column) {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug
    if (aValue == null || aValue.toString().equals(""))
       return;

    switch(column) {
       case STATE:
            if ( data[row][column].equals(aValue) ) return;
            data[row][column] = (Boolean)aValue;
            if ( aValue.equals(Boolean.TRUE) )
               store.put(data[row][PROTOCOL],
                  new ProtocolInfo((String)data[row][PROTOCOL],
                     (String)data[row][TYPE],
                     ontologyDb.getFact(Fact.VARIABLE,OntologyDb.ROOT)));
            else
               store.remove(data[row][PROTOCOL]);
            selectRow(-1); selectRow(row); // force row reselection
            fireTableCellUpdated(row,column);
            fireChanged();
            break;
    }
  }

  public ProtocolInfo[] getData() {
     int row = selectedRow;
     selectRow(-1); selectRow(row); // side effect save previous changes

     ProtocolInfo[] output = new ProtocolInfo[store.size()];
     Enumeration enum = store.elements();
     for(int i = 0; enum.hasMoreElements(); i++ )
           output[i] = (ProtocolInfo)enum.nextElement();
     return output;
  }

  public void selectRow(int row) {
     if ( selectedRow == row ) return;
     selectedRow = row;
     if ( strategyModel != null ) {
        if ( selectedRow != -1 && data[row][STATE].equals(Boolean.TRUE) )
           strategyModel.reset((ProtocolInfo)store.get(data[row][PROTOCOL]));
        else
           strategyModel.reset(null);
     }
  }


  //------------------------------------------------------------------------
  // Event Methods
  //------------------------------------------------------------------------

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
