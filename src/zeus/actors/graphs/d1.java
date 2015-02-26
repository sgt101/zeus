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
 * @(#)d1.java 1.03b
 */

package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class d1 extends Node {

  protected static final int NOT_FOUND = 0;
  protected static final int SEARCHING = 1;
  protected static final int NOT_SOUGHT = 2;

/**
   Purpose: get all agents for given goal - if oragnisationDb
   does not contain applicable agents, then contact registered
   facilitators through the organisationDb
*/

// ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/find sellers; do/find service";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   
   public d1() {
      super("d1");
   }

   // memory useful for backtracking

   protected int abilitySought(String type) {
      /**
      First we should clear 'queryTable' of entries older the a predefined
      age so that our agent should query known facilitators for the ability.
      This way, our agent is periodically kept up-to-date
      */

      String name;
      KeyValue data;

      double now = context.now();
      Hashtable queryTable = context.queryTable();
      Enumeration enum = queryTable.keys();

      while( enum.hasMoreElements() ) {
         name = (String) enum.nextElement();
         data = (KeyValue)queryTable.get(name);
         if ( data.value-now >= context.getFacilitatorRefresh() ) {
            queryTable.remove(name);
            context.MsgHandler().removeRule(data.key);
         }
      }

      data = (KeyValue)queryTable.get(type);
      if ( data == null ) return NOT_SOUGHT;

      if ( data.value < context.now() )
         return NOT_FOUND;
      else
         return SEARCHING;
   }

   protected String findAgents(AbilitySpec a) {
      if ( context.facilitators().isEmpty() ) return null;
      AbilitySpec a1;
      Hashtable queryTable = context.queryTable();

      String type = a.getType();
      switch( abilitySought(type) ) {
         case NOT_FOUND:
              return null;

         case SEARCHING:
              KeyValue struct = (KeyValue)queryTable.get(type);
              return struct.key;

         case NOT_SOUGHT:
              // try contacting known facilitators to find agents with
              // required ability
              OntologyDb ontology = context.OntologyDb();
              a1 = new AbilitySpec(ontology.getFact(Fact.VARIABLE,type),0,0);

              Performative msg;
              MailBox mbox = context.MailBox();
              String key = context.newId();
              String[] pattern = { "type", "inform", "in-reply-to", key };

              context.MsgHandler().addRule(new MessageRuleImpl(key, pattern,
	         MessageActionImpl.EXECUTE_ONCE,context.Engine(),
		 "agentWithAbilityFound")
              );

              for(int i = 0; i < context.facilitators().size(); i++ ) {
                 msg = new Performative("query-ref");
                 msg.setReceiver((String)context.facilitators().elementAt(i));
                 msg.setContent("has_ability " + a1);
                 msg.setReplyWith(key);
                 mbox.sendMsg(msg);
              }
              // add ability to list of abilities which are being sought
              double t = context.now();
              if ( !context.facilitators().isEmpty() )
                 t += context.getFacilitatorTimeout();
              queryTable.put(type,new KeyValue(key,t));
              return key;
      }
      Assert.notNull(null);
      return null;
   }

   protected int exec() {
      OrganisationDb db = context.OrganisationDb();

      DStruct ds = (DStruct)input;
      // assume ds.goal has one element only
      Goal goal = (Goal)ds.goal.elementAt(0);
      ds.agents = db.anyAgent(goal,ds.ignore);

      Core.DEBUG(2, getDescription() + " anyAgent: " + ds.agents);

      if ( ds.agents.isEmpty() ) {
         timeout = context.now() + context.getFacilitatorTimeout();

         // Adjust for confirm & reply times
	 Time t = goal.getConfirmTime();
         if ( t != null )
         timeout = Math.min(timeout,t.getTime());
         t = goal.getReplyTime();
         if ( t != null )
         timeout = Math.min(timeout,t.getTime());

         msg_wait_key = findAgents(goal.getAbility());
         Core.DEBUG(2, getDescription() + " msg_wait_key: " + msg_wait_key);
         return (msg_wait_key != null) ? WAIT : FAIL;
      }
      output = ds;
      return ds.agents.isEmpty() ? FAIL : OK;
   }

   protected int continue_exec() {
      OrganisationDb db = context.OrganisationDb();

      DStruct ds = (DStruct)input;
      // assume ds.goal has one element only
      Goal goal = (Goal)ds.goal.elementAt(0);
      ds.agents = db.anyAgent(goal,ds.ignore);

      Core.DEBUG(2, getDescription() + " continue anyAgent: " + ds.agents);
      output = ds;
      return  ds.agents.isEmpty() ? FAIL : OK;
   }

   protected void reset() {
      // reset any state changed by exec()
      DStruct ds = (DStruct) input;
      ds.agents = null;
   }
}
