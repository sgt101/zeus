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
 * @(#)b2.java 1.03b
 */


package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

/** 
    this is a node in the contract net protocol that is the default co-ordination 
    mechanism of zeus. 
    */
public class b2 extends Node {
   protected static final double DELTA_TIME = 0.25;
  
   
   // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/find protocols";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE


   public b2() {
      super("b2");
   }

   // memory useful for backtracking

   protected Graph[] local_graph = null;
   protected LocalDStruct[] local_dstruct = null;
   protected int reply_needed = 0;

   protected int exec() {
      Engine engine = context.Engine();
      MsgHandler handler = context.MsgHandler();
      ProtocolDb protocolDb = context.ProtocolDb();

      DStruct ds = (DStruct)input;
      output = input;

      Goal g = (Goal)ds.goal.elementAt(0);
      double ct = g.getConfirmTime().getTime();
      timeout = ct-DELTA_TIME;

      Core.DEBUG(3,getDescription() + " Pre-timeout = " + timeout);
      Core.DEBUG(3,getDescription() + " ds.gs.timeout = " + ds.gs.timeout);
      if ( !Misc.isZero(ds.gs.timeout) )
         timeout = Math.min(timeout,context.now() + ds.gs.timeout);

      Core.DEBUG(3,getDescription() + " Post-timeout = " + timeout);

      Time t = new Time(timeout);
      for(int i = 0; i < ds.goal.size(); i++ ) {
         g = (Goal) ds.goal.elementAt(i);
         g.setReplyTime(t);
      }

      msg_wait_key = context.newId("ProtocolInitiator");

      // Get applicable protocol & strategy for this fact/agent combination
      Fact fact = g.getFact();
      Vector info = protocolDb.getProtocols(fact,Misc.stringArray(ds.agents),
         ProtocolInfo.INITIATOR);

      if ( info.isEmpty() ) return FAIL;

      // Identify all independent agents in info
      // next, select the first ProtocolDbResult for each identified agent
      // next create local_graph for each agent then launch graphs

      HSet agents = new HSet();
      ProtocolDbResult result;
      for(int i = 0; i < info.size(); i++ ) {
         result = (ProtocolDbResult)info.elementAt(i);
         agents.add(result.agent);
      }

      String r_price = Double.toString(g.getCost());
      ProtocolDbResult[] protocols = new ProtocolDbResult[agents.size()];
      Enumeration enum = agents.elements();
      String agent;
      for(int j = 0; enum.hasMoreElements(); j++ ) {
         agent = (String)enum.nextElement();
         for(int i = 0; i < info.size(); i++ ) {
            result = (ProtocolDbResult)info.elementAt(i);
            if ( result.agent.equals(agent) ) {
               protocols[j] = result;
               protocols[j].parameters.put("reservation.price",r_price);
               break;
            }
         }
      }

      MessagePattern pattern = null;
      MessageAction action = null;

      local_graph = new Graph[protocols.length];
      local_dstruct = new LocalDStruct[protocols.length];

      for(int i = 0; i < local_graph.length; i++ ) {
         local_graph[i] = createGraph(protocols[i].protocol);
         if ( local_graph[i] != null ) {
            local_dstruct[i] = new LocalDStruct(protocols[i].agent,ds);
            local_dstruct[i].any = protocols[i];
            local_dstruct[i].key = context.newId("local_dstruct");

            pattern = new MessagePatternImpl();
            pattern.setConstraint("in-reply-to",local_dstruct[i].key);
            action = new MessageActionImpl(engine,"continue_dialogue");
            handler.addRule(new MessageRuleImpl(local_dstruct[i].key,pattern,action));

            local_graph[i].run(engine,this,local_dstruct[i],msg_wait_key);
            reply_needed++;
         }
         else
            local_dstruct[i] = null;
      }
      return (reply_needed > 0) ? WAIT : FAIL;
   }

   protected int continue_exec() {
      DStruct ds = (DStruct)input;
      output = input;

      int transaction_completed = 0;

      for(int i = 0; i < local_graph.length; i++ ) {
         if ( local_graph[i] != null ) {
            switch( local_graph[i].getState() ) {
            case Graph.DONE:
                 if ( !ds.results.contains(local_dstruct[i].result) )
                    ds.results.addElement(local_dstruct[i].result);
                 transaction_completed++;
  	         break;
            case Graph.FAILED:
                 transaction_completed++;
  	         break;
            }
         }
      }
      
      Core.DEBUG(3,getDescription() + "No Needed: " + reply_needed +
         "  completed: " + transaction_completed);

      if ( reply_needed - transaction_completed == 0 ) return OK;
      if ( timeout > context.now() )
         return WAIT;
      else
         return OK;
   }

   protected void reset() {
      // reset any state changed by exec()
      DStruct ds = (DStruct)input;
      ds.results.removeAllElements();
   }
}
