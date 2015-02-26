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

public class Sv extends Node {
   public Sv() {
      super("Sv");
   }

   // memory useful for backtracking
   private Vector previous_external = null;
   private Vector previous_goals = null;
   private Vector previous_selection = null;

   protected int exec() {
      Engine engine = context.Engine();
      DelegationStruct ds;
      Goal g, g1;

      Object[] data = (Object[]) input;

      //first perform join
      GraphStruct gs = ((DStruct)data[0]).gs;
      gs.d_results =  new Vector();
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; j < ((DStruct)data[i]).results.size(); j++ )
            gs.d_results.addElement(((DStruct)data[i]).results.elementAt(j));
      }

      Core.DEBUG(2,"Sv Previous gs " + gs);

      previous_goals = Misc.copyVector(gs.goal);

      BindResults b = bind(gs.goal,gs.d_results);

      Core.DEBUG(2,"Sv BindResults " + b);

      if ( !b.ok ) {
         for(int i = 0; i < gs.d_results.size(); i++ ) {
            ds = (DelegationStruct) gs.d_results.elementAt(i);
            engine.continue_dialogue(ds.key,ds.agent,"reject-proposal",ds.goals);
         }
         return FAIL;
      }

      previous_external = gs.external;
      previous_selection = Misc.copyVector(gs.selection);

      gs.external = b.external;
      gs.selection = Misc.union(gs.selection,b.selection);

      // send-reject to unselected agents
      for(int i = 0; i < b.rejection.size(); i++ ) {
         ds = (DelegationStruct)b.rejection.elementAt(i);
         engine.continue_dialogue(ds.key,ds.agent,"reject-proposal",ds.goals);
      }

      Core.DEBUG(2,"Sv Current gs " + gs);

      output = gs;
      return OK;
   }
   protected void reset() {
      // reset any state changed by exec()
   }

   protected BindResults bind(Vector goals, Vector input) {
      Core.DEBUG(2,"Entering Sv-bind...");

      Goal g0, g1;
      Fact f0, f1;
      Vector[] reduced = new Vector[goals.size()];
      Bindings bindings = new Bindings(context.whoami());
      BindResults result = new BindResults();
      result.ok = false;

      for(int i = 0; i < goals.size(); i++ ) {
         g0 = (Goal)goals.elementAt(i);
         reduced[i] = sortFeasible(g0.getId(),input);
         if ( reduced[i].isEmpty() )
            result.unavailable.addElement(g0);
      }

      if ( !result.unavailable.isEmpty() )
         return result;

      Object[] data;
      PlanRecord rec;
      DelegationStruct[] ds = new DelegationStruct[goals.size()];
      Selector selector = new Selector(reduced);
      boolean found = false;

      while( !result.ok && selector.hasMoreElements() ) {
         data = (Object[]) selector.nextElement();
         bindings.clear();
         result.ok = true;
         for(int i = 0; i < data.length; i++ ) {
            ds[i] = (DelegationStruct)data[i];
            // assumes only one goal in request
            g1 = (Goal)ds[i].goals.elementAt(0);
            g0 = (Goal)goals.elementAt(i);
            f0 = g0.getFact();
            f1 = g1.getFact();
            result.ok = f1.unifiesWith(f0,bindings);
            if ( !result.ok )
               break;
         }

         Core.DEBUG(2,"Sv Current selection ... ");
         Core.DEBUG(2,ds);

         if ( result.ok ) {
            for( int i = 0; i < data.length; i++ ) {
               ds[i] = (DelegationStruct)data[i];

               result.selection.addElement(ds[i]);
               for(int j = 0; j < reduced[i].size(); j++ )
                  if ( reduced[i].elementAt(j) != ds[i] )
                     result.rejection.addElement(reduced[i].elementAt(j));
            }
         }
      }

      if ( !result.ok ) {
         // Undo all or just one?

         for( int i = 0; i < goals.size(); i++ ) {
            g0 = (Goal)goals.elementAt(i);
            result.unavailable.addElement(g0);
         }
      }

      Core.DEBUG(2,"++++++ Sv-Bind goals\n" + goals);
      Core.DEBUG(2,"++++++ Sv-Bind bindings\n" + bindings);
      Core.DEBUG(2,"++++++ Sv-Bind result\n" +  result + "\n" );

      return result;
   }

   protected Vector sortFeasible(String gid, Vector input) {
      Core.DEBUG(2,"Sv-sortFeasible input " + gid + "\n" + input);

      Goal g0, g1;
      Object obj;
      DelegationStruct ds;
      Vector reduced = new Vector();
      for( int i = 0; i < input.size(); i++ ) {
         ds = (DelegationStruct)input.elementAt(i);
         // assumes only one goal in goals field
         g0 = (Goal)ds.goals.elementAt(0);
         if ( gid.equals(g0.getId()) )
            reduced.addElement(ds);
      }
      Core.DEBUG(2,"Sv-sortFeasible reduced " + gid + "\n" + reduced);
      boolean changed = true;
      while( changed ) {
         changed = false;
         for( int i = 0; i < reduced.size()-1; i++ ) {
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
      Core.DEBUG(2,"Sv-sortFeasible results " + gid + "\n" + reduced);
      return reduced;
   }
}
