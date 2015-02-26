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
 * @(#)s7.java 1.03b
 */


package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s7 extends Node {
/**
This node rejects (i.e. refuses) the cfp of a 'parent' agent
during a negotiation dialogue
*/

// ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/refuse cfp";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   
   public s7() {
      super("s7");
   }

   // memory useful for backtracking

   protected int exec() {
      Planner table = context.Planner();
      Engine engine = context.Engine();

      GraphStruct gs = (GraphStruct)input;
      Goal g = (Goal) gs.goal.firstElement();
      double now = context.now();
      double rt = g.getReplyTime().getTime();
      if ( rt > now )
         engine.continue_dialogue(gs.key,gs.agent,"refuse",gs.goal);

      table.reject(gs.goal,gs.internal);
      return OK;
   }
   protected void reset() {
      // reset any state changed by exec()
   }
}
