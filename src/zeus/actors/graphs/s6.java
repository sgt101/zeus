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
 * @(#)s6.java 1.03b
 */


package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s6 extends Node {
// NOTE: for housekeeping reasons, this node always returns true;

   // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/find protocol";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   
   
   public s6() {
      super("s6");
   }




   // memory useful for backtracking
   protected static final double DELTA_TIME = 0.25;
   private Graph local_graph = null;

   protected int exec() {
      Planner planner = context.Planner();
      ProtocolDb protocolDb = context.ProtocolDb();
      Engine engine = context.Engine();
      MsgHandler handler = context.MsgHandler();

      GraphStruct gs = (GraphStruct)input;
      output = input;
      gs.goal = planner.bind(gs.goal);
      debug("1"); 
      Goal g = (Goal) gs.goal.elementAt(0);   
      debug("2"); 
      timeout = g.getConfirmTime().getTime() + DELTA_TIME;
      debug("3"); 
      msg_wait_key = context.newId("ProtocolRespondent");
      debug("4"); 
      String[] agents = new String[1];
      agents[0] = gs.agent;
    
      // Get applicable protocol & strategy for this fact/agent combination
      Fact fact = g.getFact();
       debug("5"); 
      Vector info = protocolDb.getProtocols(fact,agents,ProtocolInfo.RESPONDENT);
        debug("6"); 
      if ( info.isEmpty() ) return OK; // really fail
        debug("7"); 
      ProtocolDbResult result = (ProtocolDbResult)info.elementAt(0);
      gs.any = result;
        debug("8"); 
      local_graph = createGraph(result.protocol);
      if ( local_graph == null ) return OK; // really fail
        debug("9"); 
      String[] pattern = { "in-reply-to", gs.key };
      // added execute_once to stop the accumulation of rules
      handler.addRule(new MessageRuleImpl(gs.key, pattern, engine, "continue_dialogue"));
        debug("10"); 
      local_graph.run(engine,this,gs,msg_wait_key);
        debug("11"); 
      return WAIT;
   }
   

   protected int continue_exec() {
      Core.DEBUG(2,getDescription() + " continue_exec");
      Planner planner = context.Planner();
         debug("12"); 
      MsgHandler handler = context.MsgHandler();
       debug("13"); 
      GraphStruct gs = (GraphStruct)input;
        debug("14"); 
      output = input;
        debug("15"); 
      switch( local_graph.getState() ) {
         case Graph.DONE:
               debug("16"); 
              planner.goalConfirmed(gs.goal,gs.confirmed_goal,gs.selection);
              gs.goal = gs.confirmed_goal;
               debug("17"); 
              gs.confirmed_goal = null;
              Core.DEBUG(2,getDescription() + " continue_exec OK");
	      return OK; 
	 case Graph.FAILED:
              Core.DEBUG(2,getDescription() + " continue_exec FAILED 1");
	      return OK; // really fail

	 default:
	      if ( timeout > context.now() ) {
                 Core.DEBUG(2,getDescription() + " continue_exec WAIT");
	         return WAIT;
              }
	      else {
                 Core.DEBUG(2,getDescription() + " continue_exec FAILED 2");
	         return OK; // really fail
              }
      }
   }

    
    private void debug (String str) { 
           System.out.println ("s6>>" + str); 
    }
    
    
   protected void reset() {
   }
}
