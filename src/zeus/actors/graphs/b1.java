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
 * @(#)b1.java 1.03b
 */

package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class b1 extends Node {
/**
   Purpose: for a predefined fraction of the period (confirm_time - now)
   subscribe to the facilitator to stream list of all agents with required
   ability: collect set of agents received
*/

// ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/find sellers; do\find service";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   

   public b1() {
      super("b1");
   }

   // local memory
   static final double FRACTION = 0.50;
   boolean proceed_immediately;

   protected int exec() {
     
      OrganisationDb db = context.OrganisationDb();
      
      OntologyDb ontology = context.OntologyDb();
      Engine engine = context.Engine();
      MailBox mbox = context.MailBox();

      DStruct ds = (DStruct)input;
      // assume ds.goal has one element only
       debug ("1"); 
      Goal goal = (Goal)ds.goal.elementAt(0);
      String type = goal.getFactType();
      double t = goal.getConfirmTime().getTime();
      double now = context.now();
      debug ("2"); 
      if ( now >= t ) return FAIL;

      // if no facilitators are listed, then directly query organisationDb
      // and return

      if ( context.facilitators().isEmpty() ) {
         if ( !ds.ignore.contains(context.whoami()) )
            ds.ignore.addElement(context.whoami());
         ds.agents = db.anyAgent(goal,ds.ignore);
         ds.ignore = Misc.union(ds.agents,ds.ignore);

         Core.DEBUG(2,"b1 ds.agents = " + ds.agents);

         output = input;
         return ds.agents.isEmpty() ? FAIL : OK;
      }
      debug ("3"); 
      // otherwise
      // subscribe to facilitators send list of agents with required ability
      timeout = now + FRACTION*(t-now);
      AbilitySpec a = new AbilitySpec(ontology.getFact(Fact.VARIABLE,type),0,0);

      Performative msg;
      msg_wait_key = context.newId();
      String[] pattern = { "type", "inform", "in-reply-to", msg_wait_key };

      proceed_immediately = ds.gs.any != null && ((Boolean)ds.gs.any).booleanValue();
      debug("4"); 
      String msg_type;
      if ( proceed_immediately ) {
          context.MsgHandler().addRule(new MessageRuleImpl(msg_wait_key, pattern,
            MessageActionImpl.EXECUTE_ONCE,engine,"agentWithAbilityFound")
          );
          msg_type = "query-ref";
      }
      else {
         context.MsgHandler().addRule(new MessageRuleImpl(msg_wait_key, pattern,
            engine,"agentWithAbilityFound")
         );
          msg_type = "subscribe";
      }
      debug ("5"); 
      for(int i = 0; i < context.facilitators().size(); i++ ) {
         msg = new Performative(msg_type);
         msg.setReceiver((String)context.facilitators().elementAt(i));
         msg.setContent("has_ability " + a);
         msg.setReplyWith(msg_wait_key);
         mbox.sendMsg(msg);
      }
      Thread.yield(); 
      return WAIT;
   }

   protected int continue_exec() {
      MailBox mbox = context.MailBox();
      OrganisationDb db = context.OrganisationDb();
      debug ("6"); 
      if ( proceed_immediately || context.now() > timeout ) {

         if ( !proceed_immediately ) {
            debug ("6a"); 
            context.MsgHandler().removeRule(msg_wait_key);
            debug("7"); 
            Performative msg;
            for(int i = 0; i < context.facilitators().size(); i++ ) {
               msg = new Performative("cancel");
               msg.setReceiver((String)context.facilitators().elementAt(i));
               msg.setContent("has_ability " + msg_wait_key);
               mbox.sendMsg(msg);
            }
         }
         debug ("8a"); 
         DStruct ds = (DStruct)input;
         // assume ds.goal has one element only
         Goal goal = (Goal)ds.goal.elementAt(0);
         ds.agents = db.anyAgent(goal,ds.ignore);
         ds.ignore = Misc.union(ds.agents,ds.ignore);
         debug ("8b"); 
         Core.DEBUG(2,"b1 ds.agents = " + ds.agents);

         output = input;
         if (ds.agents.isEmpty () ) { 
                debug ("9"); 
                Thread.yield(); 
                return FAIL; 
         }
         else {
            debug ("10");
            return OK; 
         }
            
         //return ds.agents.isEmpty() ? FAIL : OK;
      }
      return WAIT;
   }

   protected void reset() {
   }
   
   private void debug (String str) { 
        System.out.println("b1>> " + str); 
   }
}
