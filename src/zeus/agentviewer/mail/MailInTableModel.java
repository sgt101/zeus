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



package zeus.agentviewer.mail;

import javax.swing.table.*;
import java.util.*;
import zeus.concepts.Performative;
import zeus.actors.event.*;
import zeus.actors.*;


public class MailInTableModel  extends AbstractTableModel implements MessageMonitor {

      private String   SENDER  = "From";
      private String   TYPE    = "Type";
      private String   SUBJECT = "Subject";
      private String   CONTENT = "Content";
      private int      BUFFER_CAPACITY = 50;
      private int      REMOVE_INDEX = 0;
      
      protected int messageCount = 0; 


      private String[] header = {SENDER,SUBJECT,TYPE};
      private Vector   data ;
      private MailBox mb;
//---------------------------------------------------------------------------
      public MailInTableModel(AgentContext context){
          data = new Vector();
          mb = context.MailBox();
          mb.addMessageMonitor(this, MessageEvent.RECEIVE_MASK );
      }


//---------------------------------------------------------------------------
       public int getRowCount() {
             return data.size();
       }
//---------------------------------------------------------------------------
       public int getColumnCount(){
           return header.length;
       }
//---------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
           Performative msg = (Performative) data.elementAt(row);
           if (getColumnName(col).equals(SENDER))  {
             return  msg.getSender();
           }
           else if (getColumnName(col).equals(SUBJECT))  {
             return null; // MORE 
           }
           else if (getColumnName(col).equals(TYPE)) {
             return msg.getType();
           }
           else{
             return new String("Error in MailInTableModel at getValueAt");
           }
       }
//---------------------------------------------------------------------------
       Performative getMessage(int row){
          Performative msg = (Performative) data.elementAt(row);
          return (msg);
       }       
//---------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  header[col];
       }
//---------------------------------------------------------------------------
       public void addMail(Performative msg){
           if ( data.contains(msg) )
             return;

           if (data.size() > BUFFER_CAPACITY )
             data.removeElementAt(REMOVE_INDEX);
           data.addElement(msg);
           messageCount++; 
           fireTableDataChanged();
       }
//---------------------------------------------------------------------------
       public String getMailContent(int row){
          Performative msg = (Performative) data.elementAt(row);
          return (msg.getContent());

       }
//---------------------------------------------------------------------------
   public  void messageReceivedEvent(MessageEvent event){
         addMail((Performative) event.getObject());
   }
//---------------------------------------------------------------------------
       public  void messageQueuedEvent(MessageEvent event){
        
       }
//---------------------------------------------------------------------------
       public  void messageDispatchedEvent(MessageEvent event){}
//---------------------------------------------------------------------------
       public void messageNotDispatchedEvent(MessageEvent event){}
//---------------------------------------------------------------------------
       public void removeZeusEventMonitors(){
          mb.removeMessageMonitor(this, MessageEvent.RECEIVE_MASK );

       }
}
