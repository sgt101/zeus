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



package zeus.actors.event;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

public class ConversationEvent extends Event {
   public static final long INITIATE_MASK = 1;
   public static final long CONTINUE_MASK = 2;

   public static final String GOAL = "goal";

   protected String comms_key = null;
   protected String sender = null;
   protected String receiver = null;
   protected String msg_type = null;
   protected String data_type = GOAL;
   protected String data_key = null;
   protected Vector data = null;

   public ConversationEvent(Object source, Object object,
                            String comms_key, String sender,
                            String receiver, String msg_type, String data_type,
			    String data_key, Vector data, long event_mask) {
      super(source,object,CONVERSATION_FIRST,CONVERSATION_LAST,event_mask);
      this.comms_key = comms_key;
      this.sender = sender;
      this.receiver = receiver;
      this.msg_type = msg_type;
      this.data_type = data_type;
      this.data_key = data_key;
      this.data = data;
   }
   public ConversationEvent(Object source, Object object, String comms_key,
                            String sender, String receiver, String msg_type,
			    Vector data, long event_mask) {
      super(source,object,CONVERSATION_FIRST,CONVERSATION_LAST,event_mask);
      this.comms_key = comms_key;
      this.sender = sender;
      this.receiver = receiver;
      this.msg_type = msg_type;
      this.data = data;
   }
   public String  getConversationId() { return comms_key; }
   public String  getSender()         { return sender; }
   public String  getReceiver()       { return receiver; }
   public String  getMessageType()    { return msg_type; }
   public String  getDataType()       { return data_type; }
   public String  getDataKey()        { return data_key; }
   public Vector  getData()           { return data; }
   public boolean isGoalEventType()   { return data_type.equals(GOAL); }
   public boolean isFactEventType()   { return !data_type.equals(GOAL); }
}
