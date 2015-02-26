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



package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class se2 extends Node {
   public se2() {
      super("se2");
   }

   // memory useful for backtracking
   Graph local_graph = null;
   GraphStruct gs = null, gs1 = null;
   DelegationStruct ds = null;

   protected int exec() {
      ProtocolDb protocolDb = context.ProtocolDb();
      Engine engine = context.Engine();
      MsgHandler handler = context.MsgHandler();

      Vector in = (Vector)input;
      gs = (GraphStruct)in.elementAt(0);
      ds = (DelegationStruct)in.elementAt(1);

      String[] agents = new String[1];
      agents[0] = ds.agent;

      // Get applicable protocol & strategy for this fact/agent combination
      Fact fact = ((DataRec)gs.any).getFact();
      Vector info = protocolDb.getProtocols(fact,agents,
         ProtocolInfo.RESPONDENT);

      if ( info.isEmpty() ) return FAIL;

      // Compute latest time when seller wants commodity sold
      Goal g0 = (Goal) gs.goal.elementAt(0);
      double t = (double)g0.getEndTime();

      // Compute latest time when buyer wants commodity bought
      Goal g = (Goal) ds.goals.elementAt(0);
      timeout = g.getReplyTime().getTime();

      // take minimum
      timeout = Math.min(t,timeout);

      msg_wait_key = context.newId("ProtocolRespondent");

      ProtocolDbResult result = (ProtocolDbResult)info.elementAt(0);

      // set cost of goal to seller's net cost
      g.setCost(((DataRec)gs.any).getCost());
      // set reservation price for item to seller's defined value
      result.parameters.put("reservation.price",Double.toString(g0.getCost()));

      gs1 = new GraphStruct(ds.agent,g,ds.key);
      gs1.any = result;

      local_graph = createGraph(result.protocol);
      if ( local_graph == null ) return FAIL;

      String[] pattern = { "in-reply-to", gs1.key };
      // added exectute once to stop the accumulation of rules
      handler.addRule(new MessageRuleImpl(gs1.key, pattern, engine,
                      "continue_dialogue"));

      local_graph.run(engine,this,gs1,msg_wait_key);
      return WAIT;
   }

   protected int continue_exec() {
      MsgHandler handler = context.MsgHandler();

      switch( local_graph.getState() ) {
         case Graph.DONE:
              gs1.goal = gs1.confirmed_goal;
              gs1.confirmed_goal = null;
              gs1.any = gs.any;
              output = gs1;
	      return OK;

	 case Graph.FAILED:
              handler.removeRule(gs1.key);
	      return FAIL;

	 default:
	      if ( timeout > context.now() )
	         return WAIT;
	      else {
                 handler.removeRule(gs1.key);
	         return FAIL;
              }
      }
   }

   protected void reset() {
   }
}
