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

public class d3I extends Node {
   protected static final double DELTA_TIME = 0.25;

   public d3I() {
      super("d3I");
   }

   // memory useful for backtracking
   private StrategyEvaluator evaluator = null;

   protected int exec() {
      Engine engine = context.Engine();

      LocalDStruct ls = (LocalDStruct)input;
      ProtocolDbResult info = (ProtocolDbResult)ls.any;

      Goal g = (Goal) ls.goal.elementAt(0);
      double ct = g.getConfirmTime().getTime();
      timeout = ct-1.5*DELTA_TIME;

      Core.DEBUG(3,getDescription() + " Pre-timeout = " + timeout);
      Core.DEBUG(3,getDescription() + " ls.gs.timeout = " + ls.gs.timeout);

      if ( !Misc.isZero(ls.gs.timeout) )
         timeout = Math.min(timeout,context.now() + ls.gs.timeout);

      Core.DEBUG(3,getDescription() + " Post-timeout = " + timeout);

      Time t = new Time(timeout);
      for(int i = 0; i < ls.goal.size(); i++ ) {
         g = (Goal)ls.goal.elementAt(i);
         g.setReplyTime(t);
      }
      msg_wait_key = ls.key;

      evaluator = (StrategyEvaluator)createObject(info.strategy);
      if ( evaluator == null ) return FAIL;
      evaluator.set(context);
      ls.gs.evaluators.add(evaluator);
      evaluator.set(ls.gs.evaluators);

      switch( evaluator.evaluateFirst(ls.goal,info) ) {
         case StrategyEvaluator.MESSAGE:
              engine.new_dialogue(ls.key,ls.agent,"cfp",evaluator.getGoals());
              return WAIT;

         default:
              return FAIL;
      }
   }

   protected int continue_exec() {

      if (context.now() > timeout) return FAIL;

      Engine engine = context.Engine();

      LocalDStruct ls = (LocalDStruct)input;
      ProtocolDbResult info = (ProtocolDbResult)ls.any;
      DelegationStruct ds;

      if ( (ds = engine.replyReceived(ls.key)) != null ) {
          Core.DEBUG(2,"d3I replyReceived: " + ds);
         switch( evaluator.evaluateNext(ds) ) {
            case StrategyEvaluator.MESSAGE:
                 engine.continue_dialogue(ls.key,ls.agent,"cfp",
	            evaluator.getGoals());
                 return WAIT;

            case StrategyEvaluator.FAIL:
                 return FAIL;

            case StrategyEvaluator.OK:
                 ls.result = ds;
                 output = ls;
                 return OK;

            case StrategyEvaluator.WAIT:
                 return WAIT;

         }
      }
      return FAIL; // should not get here
   }

   protected void reset() {
      // reset any state changed by exec()
      LocalDStruct ls = (LocalDStruct)input;
      ls.result = null;
   }
}
