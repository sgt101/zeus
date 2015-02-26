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
 * @(#)s4.java 1.03b
 */

package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s4 extends Node {
    
   // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/reject unwanted";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE 
   
   public s4() {
      super("s4");
   }

   // memory useful for backtracking

   protected int exec() {
      // prepare output
      Planner table = context.Planner();
      Engine engine = context.Engine();

      //first perform join
      Object[] data = (Object[]) input;
      GraphStruct gs = ((DStruct)data[0]).gs;
      gs.d_results = new Vector();
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; j < ((DStruct)data[i]).results.size(); j++ )
            gs.d_results.addElement(((DStruct)data[i]).results.elementAt(j));
      }

      Core.DEBUG(2,"s4 Input gs " + gs);

      BindResults b = table.bind(gs.goal,gs.d_results,Planner.PLAN);

      Core.DEBUG(2,"s4 BindResults " + b);

      DelegationStruct ds;
      if ( !b.ok ) { // send-reject to all children: they are all unselected
         for( int i = 0; i < gs.d_results.size(); i++ ) {
            ds = (DelegationStruct) gs.d_results.elementAt(i);
            engine.continue_dialogue(ds.key,ds.agent,"reject-proposal",ds.goals);
         }
         return FAIL;
      }

      gs.external = b.external;
      gs.selection = Misc.union(gs.selection,b.selection);

      // send-reject to unselected children
      for(int i = 0; i < b.rejection.size(); i++ ) {
         ds = (DelegationStruct) b.rejection.elementAt(i);
         engine.continue_dialogue(ds.key,ds.agent,"reject-proposal",ds.goals);
      }

      Core.DEBUG(2,"s4 Current gs " + gs);
      output = gs;
      return OK;
   }
   protected void reset() {
      // reset any state changed by exec()
      Object[] data = (Object[]) input;
      GraphStruct gs = ((DStruct)data[0]).gs;
      gs.d_results = null;
   }
}
