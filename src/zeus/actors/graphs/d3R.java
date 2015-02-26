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

public class d3R extends Node {
   public d3R() {
      super("d3R");
   }

   // memory useful for backtracking
   private StrategyEvaluator evaluator = null;

 //  private TimeOutActions timeOutActions = new TimeOutActions(this); 
//   private Timer timer = null; 
    
   protected int exec() {
      Engine engine = context.Engine();

      GraphStruct gs = (GraphStruct)input;
      ProtocolDbResult info = (ProtocolDbResult)gs.any;
      Goal g = (Goal)gs.goal.elementAt(0);
      debug ("1"); 
      evaluator = (StrategyEvaluator)createObject(info.strategy);
      if ( evaluator == null ) return FAIL;
      evaluator.set(context);
      gs.evaluators.add(evaluator);
      evaluator.set(gs.evaluators);
      debug ("2"); 
      switch( evaluator.evaluateFirst(gs.goal,info) ) {
         case StrategyEvaluator.MESSAGE:
              engine.continue_dialogue(gs.key,gs.agent,"propose", evaluator.getGoals());
              break;

         default:
              //timer.cancel(); 
              return FAIL;
      }
      debug ("3");
      timeout = g.getConfirmTime().getTime();
      //timer = new Timer (); 
      //timer.schedule (timeOutActions,(long)timeout+(long)100);// wait 1/10th sec to sort it. 
      msg_wait_key = gs.key;
      return WAIT;
   }


    public boolean timeOut(){ 
        System.out.println("timeing out..."); 
        return (false); 
    }


   protected int continue_exec() {
      Core.DEBUG(2,"d3R continue_exec");

      if (context.now() > timeout)  {
         Core.DEBUG(2,"d3R Fail: " + context.now() + " > " + timeout);
         debug ("4 :d3R Fail: " + context.now() + " > " + timeout); 
         return FAIL;
      }

      Engine engine = context.Engine();

      GraphStruct gs = (GraphStruct)input;
      DelegationStruct ds;

      if ( (ds = engine.replyReceived(gs.key)) != null ) {
         Core.DEBUG(2,"d3R replyReceived: " + ds);
         debug ("5"); 
         switch( evaluator.evaluateNext(ds) ) {
            case StrategyEvaluator.MESSAGE:
                 engine.continue_dialogue(gs.key,gs.agent,"propose",
	            evaluator.getGoals());
	            debug ("6a");
                 return WAIT;

            case StrategyEvaluator.FAIL:
               //  timer.cancel(); 
               //evaluator = null; 
                debug ("6b"); 
                 return FAIL;

            case StrategyEvaluator.OK:
                 gs.confirmed = true;
                 gs.confirmed_goal = ds.goals;
                 output = gs;
             //    timer.cancel(); 
             //    evaluator = null; 
                 debug ("6c"); 
                 return OK;

            case StrategyEvaluator.WAIT:
                 debug ("6d"); 
                 return WAIT;
         }
      }
      
      debug ("7"); 
      Core.DEBUG(2,"d3R - end point?");
 //     timer.cancel(); 
     //   evaluator = null; 
      return FAIL;
   }

   protected void reset() {
      // reset any state changed by exec()
   }
   
   private void debug (String str) {    
        System.out.println("d3R>> " + str); 
   }

}
