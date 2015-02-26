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
 * @(#)s0.java 1.03b
 */


package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s0 extends Node {
    
   // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/analyse goal";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   
   public s0() {
      super("s0");
   }
   
   protected int exec() {
      // prepare output
      Planner table = context.Planner();
      Engine engine = context.getEngine(); 
      GraphStruct gs = (GraphStruct) input;
      //1.3
   //   debug ("before sync planner"); 
    //  Core.DEBUG(3, "entering sync -exec"); 
    //synchronized (table) {//1.3
    //  debug ("before sync engine"); 
      synchronized (engine) {

        PlannerQueryStruct struct = table.canAchieve(gs.goal,gs.key);
        gs.internal = struct.internal;
        gs.external = struct.external;
        gs.decompositions = struct.decompositions;
        gs.timeout = struct.timeout;
     }
      //}
           Core.DEBUG(3, "out of sync -exec"); 
      output = input;
      return OK;
   }
   //sync table?
   
   protected void reset() {
      // reset any state changed by exec()
      Planner table = context.Planner();
      Engine engine = context.getEngine(); 
      Core.DEBUG(3, "entering sync -reset"); 
   //   synchronized (table) { //1.3
     synchronized (engine) { 

        GraphStruct gs = (GraphStruct) input;
        table.reject(gs.goal,gs.internal);
        gs.internal = null;
        gs.external = null;
        gs.decompositions = null;
      }
        //}
             Core.DEBUG(3, "out of sync - reset"); 
   }
   
   
   public void debug (String str) { 
    System.out.println("S0>> " + str); 
   }

   
}
