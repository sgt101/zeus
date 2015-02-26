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
import java.util.*;
import zeus.util.OrderedHashtable;

import zeus.actors.event.*;
import zeus.actors.*;
import zeus.concepts.*;




public class ProtocolModel  extends AbstractTableModel 
                            implements ProtocolMonitor{

      public final static int TYPE = 0;
      public final static int PROTOCOL = 1;
      public final static int STATE = 2;

      private String[]   columnNames = {"Type","Protocol", "State"};
      private Vector     data ;
      private ProtocolDb protocolDb;

//------------------------------------------------------------------------------
      public ProtocolModel(AgentContext context){
          data = new Vector();
          this.protocolDb = context.ProtocolDb();
          this.protocolDb.addProtocolMonitor(this,
             ProtocolEvent.ADD_MASK | ProtocolEvent.DELETE_MASK |
             ProtocolEvent.MODIFY_MASK, true );
      }
//------------------------------------------------------------------------------
       public int getRowCount() {
           return data.size();
       }
//------------------------------------------------------------------------------
       public int getColumnCount(){
           return columnNames.length;
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
            ProtocolInfo info = (ProtocolInfo) data.elementAt(row);
            switch(col) {
               case TYPE:
                    return info.getType();
	       case PROTOCOL:
                    return  info.getName();
               case STATE:
                    return Boolean.TRUE;
               default:
                    return null;
            }
       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  columnNames[col];
       }
//------------------------------------------------------------------------------
       public void protocolAddedEvent(ProtocolEvent event) {

          ProtocolInfo info = event.getProtocolInfo();
          if ( !data.contains(info)) {
             data.addElement(info);
             fireTableDataChanged();
          }
       }
//------------------------------------------------------------------------------
       public void protocolDeletedEvent(ProtocolEvent event) {
           ProtocolInfo info = event.getProtocolInfo();
           data.removeElement(info);
           fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public void protocolModifiedEvent(ProtocolEvent event) {
       }
//------------------------------------------------------------------------------
       public void protocolAccessedEvent(ProtocolEvent event) {}
//------------------------------------------------------------------------------
       public void removeZeusEventMonitors(){
         protocolDb.removeProtocolMonitor(this,
            ProtocolEvent.ADD_MASK | ProtocolEvent.DELETE_MASK |
            ProtocolEvent.MODIFY_MASK);
       }
//------------------------------------------------------------------------------
       public StrategyInfo[] getStrategyInfos(int row){
           ProtocolInfo protocol = (ProtocolInfo)data.elementAt(row);
           return protocol.getConstraints();
       }
}
