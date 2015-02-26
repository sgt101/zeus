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

import zeus.util.*;
import zeus.concepts.*;

public class MessageHandlerEvent extends Event {
   public static final long ADD_MASK    = 1;
   public static final long DELETE_MASK = 2;
   public static final long FIRE_MASK   = 4;
   public static final long FAIL_MASK   = 8;

   protected Object destination = null;
   protected MessageRule rule = null;
   protected String ruleName = null;
   protected Performative msg = null;
   protected String method = null;

   public MessageHandlerEvent(Object source, MessageRule rule, long event_mask) {
      super(source,source,HANDLER_FIRST,HANDLER_LAST,event_mask);
      this.rule = rule;
      this.ruleName = rule.getName();
   }

   public MessageHandlerEvent(Object source, String rule, long event_mask) {
      super(source,source,HANDLER_FIRST,HANDLER_LAST,event_mask);
      this.ruleName = rule;
   }

   public MessageHandlerEvent(Object source, String rule,
                              Object destination, String method,
			      Performative msg, long event_mask) {
      super(source,source,HANDLER_FIRST,HANDLER_LAST,event_mask);
      this.ruleName = rule;
      this.destination = destination;
      this.method = method;
      this.msg = msg;
   }

   public Performative getMessage() {
      return msg;
   }
   public String getSender() {
      return msg.getSender();
   }
   public String getReceiver() {
      return msg.getReceiver();
   }
   public String getMessageType() {
      return msg.getType();
   }
   public String getDestination() {
      if ( destination == null ) return null;
      return destination.getClass().getName();
   }
   public String getMethod() {
      return method;
   }
   public String getRuleName() {
      return ruleName;
   }

   public MessageRule getRule() {
      return rule;
   }
}
