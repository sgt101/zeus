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



package zeus.agentviewer.msghandler;

import javax.swing.table.*;
import java.util.*;
import zeus.concepts.Performative;
import zeus.actors.event.*;
import zeus.actors.*;


public class MsgHandlerTableModel extends AbstractTableModel
                                  implements MessageHandlerMonitor {

      static final int SENDER = 0;
      static final int TYPE   = 1;
      static final int OBJECT = 2;
      static final int METHOD = 3;

       protected int messageCount = 0; 
      
      private int BUFFER_CAPACITY = 50;
      private int REMOVE_INDEX = 0;

      private String[] header = { "Sender", "Type", "Object", "Method" };
      private Vector   data;
      private AgentContext context;


      public MsgHandlerTableModel(AgentContext context){
          this.context = context;
          data = new Vector();
          context.MsgHandler().addMessageHandlerMonitor(this,MessageHandlerEvent.FIRE_MASK);
      }

      
      /** 
        *return the number of messages that you have processed 
        *@author Simon Thompson
        *@since 1.3
        */ 
      public int getNumberMessages() { 
            return (messageCount);
      }
            
      
      public int getRowCount() { return data.size(); }
//---------------------------------------------------------------------------
       public int getColumnCount() { return header.length; }
//---------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
           MessageHandlerEvent evt = (MessageHandlerEvent) data.elementAt(row);
           switch(col) {
              case SENDER:
                   return evt.getSender();

              case TYPE:
                   return evt.getMessageType();

	      case OBJECT:
                   return evt.getDestination();

              case METHOD:
                   return evt.getMethod();
           }
           return null;
       }
//---------------------------------------------------------------------------
       Performative getMessage(int row){
          return ((MessageHandlerEvent)data.elementAt(row)).getMessage();
       }
       public String getColumnName(int col) { return  header[col]; }

       public void messageRuleFailedEvent(MessageHandlerEvent event)  {}
       public void messageRuleAddedEvent(MessageHandlerEvent event)   {}
       public void messageRuleDeletedEvent(MessageHandlerEvent event) {}

       public void messageRuleFiredEvent(MessageHandlerEvent event) {
           if ( data.contains(event) )
             return;

           if (data.size() > BUFFER_CAPACITY )
              data.removeElementAt(REMOVE_INDEX);
           data.addElement(event);
           messageCount++; 
           fireTableDataChanged();
       }
//---------------------------------------------------------------------------
       public void removeZeusEventMonitors(){
          context.MsgHandler().removeMessageHandlerMonitor(this,MessageHandlerEvent.FIRE_MASK);
       }
}
