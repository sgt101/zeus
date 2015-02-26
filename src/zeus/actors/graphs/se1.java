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


/*
 * @(#)se1.java 1.03b
 */

package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class se1 extends Node {
/**
   Purpose: for a predefined fraction of the period (end_time - now)
   advertise good for sale to facilitator
   Wait until contacted by buyer, then proceed with negotiation
*/


   // LL 040500 1.03bB
      private String node_desc = "continue buying";
      public  final String getDesc()                 {return node_desc;};
      public  final void   setDesc(String node_desc) {this.node_desc = node_desc;};
   // LL 040500 1.03bE

   public se1() {
      super("se1");
   }

   // local memory
   static final double FRACTION = 0.90;

   protected int exec() {
      // if no facilitators are listed, then FAIL
      // (alternatively, could be made to utilise the nameserver and directly
      // contact agents, i.e. proactive selling)
      debug ("1"); 
      if ( context.facilitators().isEmpty() ) return FAIL;
      debug ("2");  
      Engine engine = context.Engine();
      debug ("3"); 
      MailBox mbox = context.MailBox();
      debug ("4"); 
      GraphStruct gs = (GraphStruct)input;
      debug ("5"); 
      // assume gs.goal has one element only
      Goal goal = (Goal)gs.goal.elementAt(0);
      debug ("6"); 
      Fact fact = ((DataRec)gs.any).getFact();
      debug ("7"); 
      double t = (double)goal.getEndTime();
      debug ("8");
      double now = context.now();
      debug ("9");
      if ( t <= now ) return FAIL;
      debug ("10");   
      // advertise to facilitators
      timeout = now + FRACTION*(t-now);
      debug ("11"); 
      AbilitySpec a = new AbilitySpec(fact,0,0);
      debug ("12"); 
      Performative msg;
      msg_wait_key = context.newId();
debug ("13"); 
      for(int i = 0; i < context.facilitators().size(); i++ ) {
         msg = new Performative("inform");
         msg.setReceiver((String)context.facilitators().elementAt(i));
         msg.setContent("my_abilities " + a);
         mbox.sendMsg(msg);
      }
      debug ("14"); 
      engine.addItemForSale(msg_wait_key,fact);
      debug ("15a"); 
      return WAIT;
   }

   protected int continue_exec() {
      Engine engine = context.Engine();
      Core.DEBUG(2,"se1 continue_exec called");
    debug ("15"); 
      if ( context.now() > timeout ) {
        debug ("16"); 
         engine.removeItemForSale(msg_wait_key);
         debug("17"); 
         Core.DEBUG(2,"se1 failing now > timeout");
         debug ("18"); 
         return FAIL;
      }
    debug ("19"); 
      GraphStruct gs = (GraphStruct)input;
    debug ("20"); 
      DelegationStruct ds;
      debug ("21"); 
      String buyers_key = engine.getBuyersKey(msg_wait_key);
      if ( buyers_key != null && 
           (ds = engine.replyReceived(buyers_key)) != null ) {
         output = new Vector();
         ((Vector)output).addElement(gs);
         ((Vector)output).addElement(ds);
         Core.DEBUG(2,"se1 returning success");
         return OK;
      }
      return WAIT;
   }

   protected void reset() {
   }
   
   
   public void debug (String str) { 
    System.out.println("se1>>" + str);
   }
}
