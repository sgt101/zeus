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
 * @(#)b4.java 1.03b
 */

package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class b4 extends Node {
    
    // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do/accept & reject";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   
   
   public b4() {
      super("b4");
   }

   // memory useful for backtracking

   protected Vector select(Vector goals, Vector input) {
      Core.DEBUG(3,"select input " + goals + "\n" + input);
      Goal g, g0, g1;
      Object obj;
      DelegationStruct ds;
      String gid;
      Vector reduced = new Vector();
      Vector selection = new Vector();

      for(int j = 0; j < goals.size(); j++ ) {
         g = (Goal)goals.elementAt(j);
         gid = g.getId();
         reduced.removeAllElements();

         for(int i = 0; i < input.size(); i++ ) {
            ds = (DelegationStruct)input.elementAt(i);
            // assumes only one goal in goals field
            g0 = (Goal)ds.goals.elementAt(0);
            if ( gid.equals(g0.getId()) )
               reduced.addElement(ds);
         }
         Core.DEBUG(3,"select reduced " + gid + "\n" + reduced);
         boolean changed = true;
         while( changed ) {
            changed = false;
            for(int i = 0; i < reduced.size()-1; i++ ) {
               // assumes only one goal in goals field
               ds = (DelegationStruct)reduced.elementAt(i);
               g0 = (Goal)ds.goals.elementAt(0);
               ds = (DelegationStruct)reduced.elementAt(i+1);
               g1 = (Goal)ds.goals.elementAt(0);
               if ( g0.getCost() > g1.getCost() ) {
                  obj = reduced.elementAt(i);
                  reduced.setElementAt(reduced.elementAt(i+1),i);
                  reduced.setElementAt(obj,i+1);
                  changed = true;
               }
            }
         }
         if ( !reduced.isEmpty() )
	    selection.addElement(reduced.elementAt(0));
      }
      Core.DEBUG(3,"select results\n" + selection);
      return selection;
   }


   protected int exec() {
      Engine engine = context.Engine();

      // first perform join
      Object[] data = (Object[]) input;
      GraphStruct gs = ((DStruct)data[0]).gs;
      gs.d_results = new Vector();
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; j < ((DStruct)data[i]).results.size(); j++ )
            gs.d_results.addElement(((DStruct)data[i]).results.elementAt(j));
      }

      Core.DEBUG(2,"b4 Input gs " + gs);

      Vector selection = select(gs.goal,gs.d_results);
      Vector rejection = Misc.difference(gs.d_results,selection);
      gs.selection = Misc.union(gs.selection,selection);

      Core.DEBUG(2,"b4 Results " + selection);

      DelegationStruct ds;
      // send reject message to rejected children
      for(int i = 0; i < rejection.size(); i++ ) {
         ds = (DelegationStruct) rejection.elementAt(i);
         engine.continue_dialogue(ds.key,ds.agent,"reject-proposal",ds.goals);
      }

      Core.DEBUG(2,"b4 Current gs " + gs);

      if ( gs.selection.isEmpty() ) return FAIL;

      // send confirm message to selected children
      for(int i = 0; i < gs.selection.size(); i++ ) {
         ds = (DelegationStruct)gs.selection.elementAt(i);
         engine.continue_dialogue(ds.key,ds.agent,"accept-proposal",ds.goals);
      }

      // Now prepare for post-contract phase
      // for now assume gs.goal & ds.goals have one element only
      Goal g;
      AuditTable audit = engine.getAuditTable();

      for(int i = 0; i < gs.selection.size(); i++ ) {
         ds = (DelegationStruct)gs.selection.elementAt(i);
         g = (Goal)ds.goals.elementAt(0);

         audit.add(g,ds.key,g.getCost(),false,false,ds.agent,
                   context.whoami(),g.getEndTime());
      }

      Core.DEBUG(2,"*** B4 AuditTable State ***");
      Core.DEBUG(2,audit);

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
