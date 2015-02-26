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
 * @(#)s2.java 1.03b
 */

package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s2 extends Node {
   public s2() {
      super("s2");
   }
  
// ST 050500 1.03bB node description due to CVB
   private String node_desc = "entry/COMMIT scheduled task";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE

   // memory for backtracking
   protected PlannerQueryStruct bind_data = null;

   protected int exec() {
    try {
      Planner table = context.Planner();
      Engine engine = context.Engine();
      AuditTable audit = engine.getAuditTable();

      GraphStruct gs = (GraphStruct) input;

      Vector bound_goals = table.bind(gs.goal);
      bind_data = table.clear_bind(gs.goal);

      // for now assume gs.goal & ds.goals have one element only
      Goal g0, g1;
      Vector records;
      DelegationStruct ds;

      for(int i = 0; i < gs.goal.size(); i++ ) {
         // Original goal statement
         g0 = (Goal)bind_data.goals.elementAt(i);
         // Goal statement with new info, e.g. cost, routing, etc.
         g1 = (Goal)bound_goals.elementAt(i);

         audit.addProducerRecords(g1.getProducerRecords());
         audit.addConsumerRecords(g1.getConsumerRecords());

         audit.add(g0,gs.key,g1.getCost(),false,false,context.whoami(),
                   gs.agent,g0.getEndTime());
      }
      for(int i = 0; i < gs.selection.size(); i++ ) {
         ds = (DelegationStruct)gs.selection.elementAt(i);
         g0 = (Goal)ds.goals.elementAt(0);

         audit.addProducerRecords(g0.getProducerRecords());
         audit.addConsumerRecords(g0.getConsumerRecords());

         audit.add(g0,ds.key,g0.getCost(),false,false,ds.agent,
                   context.whoami(),g0.getEndTime());
      }

      if ( !gs.internal.isEmpty() ){
         table.book(PlanRecord.FIRM,gs.goal,gs.internal);
       }
      
      Core.DEBUG(2,"*** S2 AuditTable State ***");
      Core.DEBUG(2,audit);

      output = gs;
      
      return OK;} 
      catch (Exception e) { 
        return FAIL; 
      }
   }

   protected void reset() {
      // reset any state changed by exec()
      Planner table = context.Planner();
      Engine engine = context.Engine();
      AuditTable audit = engine.getAuditTable();

      GraphStruct gs = (GraphStruct) input;
      if ( !gs.internal.isEmpty() )
         table.book(PlanRecord.TENTATIVE,gs.goal,gs.internal);

      table.reset_bind(gs.goal,bind_data);

      bind_data = null; // gc

      // for now assume gs.goal & ds.goals have one element only
      Goal g;

      for(int i = 0; i < gs.goal.size(); i++ ) {
         g = (Goal)gs.goal.elementAt(i);
         audit.del(g);
      }
      DelegationStruct ds;
      for(int i = 0; i < gs.selection.size(); i++ ) {
         ds = (DelegationStruct)gs.selection.elementAt(i);
         g = (Goal)ds.goals.elementAt(0);
         audit.del(g);
      }
      this.finals();
   }
}
