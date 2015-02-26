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



package zeus.agentviewer.resources;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import zeus.concepts.*;
import zeus.actors.event.*;
import zeus.concepts.*;
import zeus.actors.*;


public class ResourceTableModel  extends AbstractTableModel
             implements FactMonitor {

      static final int TYPE   = 0;
      static final int ID     = 1;
      static final int STATUS = 2;

      private String[] header = { "Type", "Id", "Status" };
      private Vector   data ;
      public ResourceDb resDB;
      public OntologyDb ontologyDb;
      protected EventListenerList changeListeners = new EventListenerList();


//---------------------------------------------------------------------------
      public ResourceTableModel(AgentContext context){
        data = new Vector();
        resDB = context.ResourceDb();
        ontologyDb = context.OntologyDb();
        resDB.addFactMonitor(this, FactEvent.ADD_MASK | FactEvent.DELETE_MASK
                                   | FactEvent.MODIFY_MASK,true);
      }


//---------------------------------------------------------------------------
       public int getRowCount() { return data.size(); }
//---------------------------------------------------------------------------
       public int getColumnCount() { return header.length; }
//---------------------------------------------------------------------------
       public String getColumnName(int col) { return header[col]; }
//---------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
           ResourceItem item = (ResourceItem)data.elementAt(row);
           switch(col) {
              case TYPE:
                   return  item.getFact().getType();
              case ID:
                   return item.getFact().getId();
              case STATUS:
                   return (item.isReserved() ? "RESERVED" : "UNRESERVED");
           }
           return null;
       }
//---------------------------------------------------------------------------
       public boolean isCellEditable(int row, int col) {
          return false;
       }
//---------------------------------------------------------------------------
        public Fact  getAttributesOf(int row){
          return ((ResourceItem)data.elementAt(row)).getFact();
       }
//---------------------------------------------------------------------------
       public void factAddedEvent(FactEvent event) {
         if ( !data.contains(event.getObject()) )
	    data.addElement(event.getObject());
         fireTableDataChanged();
         fireChanged();
       }
//---------------------------------------------------------------------------
       public void factModifiedEvent(FactEvent event) {
         fireTableDataChanged();
         fireChanged();
      }
//---------------------------------------------------------------------------
      public void factDeletedEvent(FactEvent event) {
         data.removeElement(event.getObject());
         fireTableDataChanged();
         fireChanged();
      }
//---------------------------------------------------------------------------
      public void factAccessedEvent(FactEvent event) {}
//---------------------------------------------------------------------------

// -- EVENTLISTENER METHODS ---------------------------------------

    public void addChangeListener(ChangeListener x) {
     changeListeners.add(ChangeListener.class, x);
    }
//---------------------------------------------------------------------------
    public void removeChangeListener(ChangeListener x) {
     changeListeners.remove(ChangeListener.class, x);
    }
//---------------------------------------------------------------------------
    protected void fireChanged() {
      ChangeEvent c = new ChangeEvent(this);
      Object[] listeners = changeListeners.getListenerList();
      for (int i= listeners.length-2; i >= 0; i -=2) {
        if (listeners[i] == ChangeListener.class) {
          ChangeListener cl = (ChangeListener)listeners[i+1];
          cl.stateChanged(c);
        }
      }
    }

//---------------------------------------------------------------------------
    public void removeZeusEventMonitors(){
       resDB.removeFactMonitor(this, FactEvent.ADD_MASK | FactEvent.DELETE_MASK
                                     | FactEvent.MODIFY_MASK);

    }
}
