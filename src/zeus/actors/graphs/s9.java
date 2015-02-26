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
 * @(#)s9.java 1.03b
 */


package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s9 extends Node {
/**
This node rejects the proposals of some 'child' agents
during a negotiation dialogue
*/

   public s9() {
      super("s9");
   }
    // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/reject proposals ; do/refuse cfp";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   

   // memory useful for backtracking

   protected int exec() {
      MsgHandler handler = context.MsgHandler();
      Planner planner = context.Planner();
      Engine engine = context.Engine();

      GraphStruct gs = (GraphStruct)input;
      planner.reject(gs.goal,gs.internal);
      engine.continue_dialogue(gs.key,gs.agent,"refuse",gs.goal);
      handler.removeRule(gs.key);
      // send-reject to selected agents
      DelegationStruct ds;
      Goal g = (Goal) gs.goal.firstElement();
      double now = context.now();
      double ct = g.getConfirmTime().getTime();
      if ( ct > now ) {
         for( int i = 0; i < gs.selection.size(); i++ ) {
            ds = (DelegationStruct)gs.selection.elementAt(i);
            engine.continue_dialogue(ds.key,ds.agent,"reject-proposal",ds.goals);
            handler.removeRule(ds.key);
         }
      }
      return OK;
   }
   protected void reset() {
      // reset any state changed by exec()
   }
}
