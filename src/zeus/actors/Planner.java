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
 * @(#)Planner.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.util.DelegationStruct;
import zeus.actors.rtn.Engine;
import zeus.actors.event.*;

/**
 * This component implements the agent's Planning and Scheduling mechanism. 
 * Its role is to construct action sequences that will achieve desired input goals.
 * Hence the Planner is under the control of the {@link Engine} component, which 
 * initiates planning and manages the contracting of any subgoals that the agent 
 * cannot achieve. <p>
 *
 * Planning operators (actions or tasks) are represented in the classical fashion 
 * as primitive or summary operators.  Primitive operators are defined in terms 
 * of their preconditions, effects, cost, duration and constraints and precondition 
 * order, while summary operators are defined in terms of a graph of existing 
 * primitive tasks. <p>
 *
 * The Planner utilises classical partial order means end planning in its plan 
 * construction process. So when given a goal the Planner searches its {@link PlanDb}
 * for an operator with a public effect that unifies (with unification bindings q) 
 * with the desired_effect of the goal.  If multiple operators are found, they are 
 * ranked by cost, and then by duration. Next, the Planner selects the first 
 * ranked operator, constrains its preconditions and effects with q, and then 
 * attempts to schedule the operator into its diary.  If the operator cannot be 
 * scheduled, the Planner backtracks and re-peats the process with the next 
 * applicable operator. <p>
 *
 * Details on how the Planner functions are provided in the Zeus Technical Manual.
 */


public class Planner extends PlanDb 
{
   private HSet[]  eventMonitor = new HSet[6];

   public static final int START   = 0;
   public static final int FAIL    = 1;
   public static final int SUCCEED = 2;

   public static final int CREATE       = 3;
   public static final int DISPOSE      = 4;
   public static final int STATE_CHANGE = 5;

   public static final boolean EXPAND = true;
   public static final int     REPLAN = 0;
   public static final int     PLAN   = 1;


 

   /** This object stores the Planner's diary as is a two-dimensional array, with
       time on one dimension and processors on another. */
   protected PlanRecord[][] table;

   protected int            plannerWidth;
   protected int            plannerLength;
   protected int            now;
   protected Hashtable      BindTable;

   protected boolean user_responded = false;
   protected long USER_TIME_OUT = 0;
   protected AgentContext context = null;


   public Planner () {
    for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();
   }


   public Planner(AgentContext context, int plannerWidth,
                  int plannerLength ) 
   {
      Assert.notNull(context);
      this.context = context;
      context.set(this);

      Core.ERROR(plannerLength > 0 && plannerWidth > 0, 1005, this);

      table = new PlanRecord[plannerWidth][plannerLength];
      this.plannerLength = plannerLength;
      this.plannerWidth = plannerWidth;
      now = (int) now();
      BindTable = new Hashtable();

      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();

      USER_TIME_OUT = (long)(0.5 * context.getClockStep());
   }

   public AgentContext getAgentContext() {
      return context;
   }


   public int getPlannerWidth() {
      return   plannerWidth;
   }
   public int getPlannerLength() {
     return plannerLength;
   }

    //synchronized
  int  anySideEffect(Fact desc, PlanRecord rec,
                                 int position, int required) {
      return anySideEffect(desc,rec,new Integer(position),required);
   }
   
   
    // synchronized 
  int  anySideEffect(Fact desc, PlanRecord rec,
                                  Object precond, int required) 
   {
      if ( !context.getSharePlan() ) return required;
      Core.DEBUG(2,"checking for serendipitous side-effects for " + desc);

      int no = desc.getNumber();
      PlanRecord crec;
      int st = rec.getStartTime();
      if ( desc.isa(OntologyDb.ENTITY) ) desc.setNumber(desc.newVar());
      Enumeration enum = this.elements();
      while( required > 0 && enum.hasMoreElements() ) {
         crec = (PlanRecord)enum.nextElement();
         if ( crec != rec && crec.getEndTime() <= st )
            required = crec.anySideEffect(desc,rec,precond,required);
      }
      desc.setNumber(no);
      return required;
   }
   
   
// synchronized 
   public PlannerQueryStruct canAchieve(Vector goals, String key) 
   {
      Core.DEBUG(3,"\nCanAchieve:\n" + goals + "\n");

      Goal g;
      PlannerQueryStruct struct = new PlannerQueryStruct(goals);

      for(int i = 0; i < goals.size(); i++ ) {
         g = (Goal)goals.elementAt(i);
         notifyMonitors(g,START,PlanningEvent.PLANNING);
      }

      struct.timeout = context.getAcceptTimeout();

      // check for Looping i.e. A --> B --> C --> A and
      // the goal.fact at the two A's are identical
      if ( loopFound(goals) ) {
         Core.DEBUG(0,"CanAchieve loop found = " + struct);
         index(struct);
         return struct;
      }

      // check confirm & reply times
      double ct = Double.MAX_VALUE;
      double rt = Double.MAX_VALUE;
      for(int i = 0; i < goals.size(); i++ ) {
         g = (Goal)goals.elementAt(i);
         ct = Math.min(ct,g.getConfirmTime().getTime());
         if ( g.getReplyTime() != null )
            rt = Math.min(rt,g.getReplyTime().getTime());
      }
      double now = now();
      if ( now >= Math.min(ct,rt) ) {
         Core.DEBUG(0,"CanAchieve rt/ct problem: " + now + "\n" + struct);
         index(struct);
         return struct;
      }

      struct.internal = schedule(key,null,new Vector(),goals,struct,EXPAND);
      if ( struct.internal.isEmpty() ) {
         Core.DEBUG(0,"CanAchieve = " + struct);
         index(struct);
         return struct;
      }

      double lct = (double) latestConfirmTime(goals);
      if ( lct < ct ) {
         Core.DEBUG(0,"CanAchieve lct < ct " + now + "\n" + struct);
         reject(struct.goals,struct.internal);
         struct.internal = new Vector();
         index(struct);
         return struct;
      }

      if ( !struct.internal.isEmpty() && !struct.external.isEmpty() ) {
         // timeout heuristic
         double t;
         t = Math.min(ct,rt);
         t = t - now();
         t = t/2*struct.external.size();
         struct.timeout = t;
      }
      index(struct);
      Core.DEBUG(0,"canAchieve = " + struct);
      return struct;
   }

   protected boolean loopFound(Vector goals) {
      Enumeration enum = BindTable.elements();
      PlannerQueryStruct struct;
      Vector stored;
      Goal g1, g2;
      Fact f1, f2;
      String r1, r2;
      Bindings b = new Bindings(context.whoami());

      while( enum.hasMoreElements() ) {
         struct = (PlannerQueryStruct) enum.nextElement();
         stored = struct.goals;
         for( int i = 0; i < goals.size(); i++ ) {
            g1 = (Goal)goals.elementAt(i);
            f1 = g1.getFact();
            r1 = g1.getRootId();
            for( int j = 0; j < stored.size(); j++, b.clear() ) {
               g2 = (Goal)stored.elementAt(j);
               if ( g2.getDesiredBy().equals(context.whoami()) ) {
                  f2 = g2.getFact();
                  r2 = g2.getRootId();
                  if ( r1.equals(r2) && f1.unifiesWith(f2,b) ) {
                     Core.DEBUG(0,"Loop found: " + g1 + "\n" + g2);
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

   public PlannerQueryStruct clear_bind(Vector goals) {
      if ( goals.isEmpty() ) return null;
      Core.DEBUG(3,"Entering Clear Bind goals\n" +  goals + "\n");

      String index = makeIndex(goals);
      return (PlannerQueryStruct) BindTable.remove(index);
   }

   public void reset_bind(Vector goals, PlannerQueryStruct data) {
      Core.DEBUG(3,"Entering Reset Bind goals\n" +  goals + "\n");

      String index = makeIndex(goals);
      Core.ERROR(BindTable.put(index,data) == null,1009,this);
   }

   public Vector bind(Vector goals) {
      Core.DEBUG(3,"Entering Final Bind goals\n" +  goals + "\n");

      String index = makeIndex(goals);
      PlannerQueryStruct struct = (PlannerQueryStruct) BindTable.get(index);
      PlanRecord rec;
      Bindings b;
      Core.DEBUG(3,"Final Bind checking record" );
      for(int i = 0; i < struct.internal.size(); i++ ) {
         rec = (PlanRecord)struct.internal.elementAt(i);
         b = rec.getBindings();
         Core.ERROR(struct.bindings.add(b),1010,this);
      }
      Goal g;
      Vector data = new Vector();
      Core.DEBUG(3,"Final Bind checking costs" );
      for(int i = 0; i < goals.size(); i++ ) {
         g = new Goal( (Goal)goals.elementAt(i) );
         g.constrain(struct.bindings);
         rec = lookUp(g);
         g.setCost(rec.getCost());
         data.addElement(g);
      }
      Core.DEBUG(3,"Exiting Final Bind goals\n" +  data + "\n" );
      return data;
   }


//synchronized
   public synchronized void userResponded() {
      this.user_responded = true;
   }

   public BindResults bind(Vector goals, Vector input, int mode) {
      Core.DEBUG(3,"Entering bind...");
      String index = makeIndex(goals);
      PlannerQueryStruct struct = (PlannerQueryStruct) BindTable.get(index);

      Goal g0, g1;
      Fact f0, f1;
      PlanRecord rec;
      Vector[] reduced = new Vector[struct.external.size()];
      Bindings bindings = new Bindings(context.whoami());
      BindResults result = new BindResults();
      boolean found = false;

      Vector absent = new Vector();
      Vector present = new Vector();

      for(int i = 0; i < struct.external.size(); i++ ) {
         g0 = (Goal)struct.external.elementAt(i);
         reduced[i] = sortFeasible(g0.getId(),input);
         if ( reduced[i].isEmpty() )
            absent.addElement(g0);
         else
            present.addElement(g0);
      }

      if ( !absent.isEmpty() ) {
         Core.DEBUG(3,"From bind...");
         Core.DEBUG(3,"PlannerQueryStruct = " + struct);
         Core.DEBUG(3,"absent = " + absent);
         for( int x = 0; x < struct.internal.size(); x++ ) {
            rec = (PlanRecord)struct.internal.elementAt(x);
            Core.DEBUG(3,rec.getConsumedDb());
         }

         softFailParentOf(absent,struct,mode);
         for(int j = 0; j < present.size(); j++ ) {
            g1 = (Goal)present.elementAt(j);
            found = false;
            for(int i = 0; !found && i < struct.external.size(); i++ ) {
               g0 = (Goal)struct.external.elementAt(i);
               found = found || g0.getId().equals(g1.getId());
               if ( found ) struct.external.removeElementAt(i--);
            }
            if ( !found ) present.removeElementAt(j--);
         }
      }

      Core.DEBUG(3,"Present = " + present);
      Core.DEBUG(3,"Struct = " + struct);

      if ( present.isEmpty() ) {
         result.rejection = input;
         result.ok = !struct.internal.isEmpty() ||
                     !struct.decompositions.isEmpty();
         result.external = Misc.copyVector(struct.external);
         return result;
      }
     

      if ( present.size() != reduced.length ) {
         reduced = new Vector[present.size()];
         for(int i = 0; i < present.size(); i++ ) {
            g0 = (Goal)present.elementAt(i);
            reduced[i] = sortFeasible(g0.getId(),input);
         }
      }

/*
      AskUser ask_user;
      user_responded = false;
      long user_timeout = System.currentTimeMillis() + USER_TIME_OUT;
      synchronized(this) {
         ask_user = new AskUser(reduced,this);
         while( !user_responded && 
                System.currentTimeMillis() < user_timeout ) {
            try {
               wait(USER_TIME_OUT);
            }
            catch(InterruptedException e) {
            }
         }
      }
      if ( !user_responded && ask_user != null && ask_user.isShowing() )
         ask_user.okBtnFn();
*/

      Object[] data;
      DelegationStruct[] ds = new DelegationStruct[present.size()];
      Selector selector = new Selector(reduced);
      result.ok = false;
      while( !result.ok && selector.hasMoreElements() ) {
         data = (Object[]) selector.nextElement();
         bindings.clear();
         result.ok = true;
         for(int i = 0; i < data.length; i++ ) {
            ds[i] = (DelegationStruct)data[i];
            // assumes only one goal in request
            g1 = (Goal)ds[i].goals.elementAt(0);
            g0 = (Goal)present.elementAt(i);
            f0 = g0.getFact();
            f1 = g1.getFact();
            result.ok = f1.unifiesWith(f0,bindings);
            if ( !result.ok )
               break;
         }

         Core.DEBUG(3,"Current selection ... ");
         Core.DEBUG(3,ds);

         result.ok = result.ok && bindings.add(struct.bindings);
         if ( result.ok ) {
            for(int j = 0; j < struct.internal.size(); j++ ) {
               rec = (PlanRecord) struct.internal.elementAt(j);
               Core.ERROR(rec.applyConstraints(bindings),1011,this);
            }
            struct.bindings = bindings;

            for( int i = 0; i < data.length; i++ ) {
               ds[i] = (DelegationStruct)data[i];
               result.selection.addElement(ds[i]);
            }
         }
      }

      if ( result.ok ) {
         result.rejection = Misc.difference(input,result.selection);
         resume_planning(ds,struct);
      }
      else {
         result.rejection = input;
         softFailParentOf(struct.external,struct,mode);
         result.ok = !struct.internal.isEmpty() ||
                     !struct.decompositions.isEmpty();
      }

      result.external = Misc.copyVector(struct.external);

      Core.DEBUG(3," Bind struct\n" + struct);
      Core.DEBUG(3," Bind goals\n" + goals);
      Core.DEBUG(3," Bind bindings\n" + struct.bindings);
      Core.DEBUG(3," Bind result\n" +  result + "\n" );

      return result;
   }

   protected Vector sortFeasible(String gid, Vector input) {
      Core.DEBUG(3,"sortFeasible input " + gid + "\n" + input);
      Goal g0, g1;
      Object obj;
      DelegationStruct ds;
      Vector reduced = new Vector();
      for(int i = 0; i < input.size(); i++ ) {
         ds = (DelegationStruct)input.elementAt(i);
         // assumes only one goal in goals field
         g0 = (Goal)ds.goals.elementAt(0);
         if ( gid.equals(g0.getId()) )
            reduced.addElement(ds);
      }
      Core.DEBUG(3,"sortFeasible reduced " + gid + "\n" + reduced);
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
      Core.DEBUG(3,"sortFeasible results " + gid + "\n" + reduced);
      return reduced;
   }

   // Assess feasilibility of enactment of a prearranged SLA
   public PlannerEnactStruct enact(Goal goal, Goal sla) {
      PlannerEnactStruct es = new PlannerEnactStruct();
      PlanRecord rec, image;

      notifyMonitors(goal,START,PlanningEvent.ENACTMENT);

      rec = lookUp(sla);
      if ( rec == null ) {
         es.ok = false;
         notifyMonitors(goal,FAIL,PlanningEvent.ENACTMENT);
         Core.DEBUG(3,"PlannerEnactStruct no sla found for\n" +  sla);
         return es;
      }

      rec.enact(es,goal,null,goal.getId(),new Hashtable());

      if ( es.ok  ) {
         for( int i = 0; i < es.images.size(); i++ ) {
            image = (PlanRecord)es.images.elementAt(i);
            image.setState(PlanRecord.FIRM);
         }
         if ( !rec.hasMoreEnactments() )
            rec.dispose();
         notifyMonitors(goal,SUCCEED,PlanningEvent.ENACTMENT);
      }
      else {
         notifyMonitors(goal,FAIL,PlanningEvent.ENACTMENT);
      }
      
      Core.DEBUG(3,"Final PlannerEnactStruct\n" +  es);
      return es;
   }

   int latestConfirmTime(Vector goals) {
      Core.ERROR(goals,1012,this);
      Core.ERROR(!goals.isEmpty(),1013,this);
      int  lct = now + plannerLength;
      for( int i = 0; i < goals.size(); i++ )
         lct = Math.min(lct,latestConfirmTime((Goal)goals.elementAt(i)));
      return lct;
   }
   int latestConfirmTime(Goal goal) {
      PlanRecord rec;
      rec = lookUp(goal);
      return rec.latestConfirmTime();
   }

   protected String makeIndex(Vector goals) {
      Core.ERROR(goals,1014,this);
      if ( goals.isEmpty() ) return null;

      String[] Items = new String[goals.size()];
      for( int i = 0; i < goals.size(); i++ )
         Items[i] = ((Goal)goals.elementAt(i)).getId();

      Misc.sort(Items);

      String index = new String();
      for(int i = 0; i < Items.length-1; i++ )
         index += Items[i] + "/";
      index += Items[Items.length-1];

      return index;
   }

   protected void index(PlannerQueryStruct Results) {
      String index = makeIndex(Results.goals);
      Core.ERROR(BindTable.put(index,Results) == null,1015,this);
   }

   protected void removeFromIndexTable(Vector goals) {
     Core.DEBUG(3,"Planner removeFromIndexTable");
      String index = makeIndex(goals);
      BindTable.remove(index);
   }

   public void book(int type, Vector goals, Vector records) {
      Vector rootSet = new Vector();
      PlanRecord root;

      for(int i = 0; i < records.size(); i++ ) {
         root = ((PlanRecord)records.elementAt(i)).getRoot();
         if ( !rootSet.contains(root) ) rootSet.addElement(root);
      }
      Goal g;
      for(int i = 0; i < goals.size(); i++ ) {
         g = (Goal)goals.elementAt(i);
         book(type,g,rootSet);
         if ( type == PlanRecord.FIRM )
            notifyMonitors(g,SUCCEED,PlanningEvent.PLANNING);
      }
      for(int i = 0; i < rootSet.size(); i++ ) {
         root = (PlanRecord)rootSet.elementAt(i);
         root.setState(type);
      }
   }

   protected void book(int type, Goal goal, Vector rootSet) {
      PlanRecord rec = lookUp(goal);
      if ( rec != null ) {
         rec.setState(type);
         rootSet.removeElement(rec);
      }
   }

   public void reject(Vector goals, Vector records) {

      Core.DEBUG(3,"Planner Reject goals called " + goals);
      removeFromIndexTable(goals);
      Vector rootSet = new Vector();
      PlanRecord root;

      for(int i = 0; i < records.size(); i++ ) {
         root = ((PlanRecord)records.elementAt(i)).getRoot();
         if ( !rootSet.contains(root) ) rootSet.addElement(root);
      }
      Goal g;
      for(int i = 0; i < goals.size(); i++ ) {
         g = (Goal)goals.elementAt(i);
         reject(g,rootSet);
         notifyMonitors(g,FAIL,PlanningEvent.PLANNING);
      }
      for(int i = 0; i < rootSet.size(); i++ ) {
         root = (PlanRecord)rootSet.elementAt(i);
         root.dispose();
      }
   }

   protected void reject(Goal goal, Vector rootSet) {
    // Core.DEBUG(3,"Planner Reject goals called " + goals);
      PlanRecord rec = lookUp(goal);
      if ( rec != null ) {
         rec.dispose();
         rootSet.removeElement(rec);
      }
   }

   Vector schedule(String key, PlanRecord parent, Vector path,
                   Vector goals, PlannerQueryStruct struct, boolean mode) {
      Core.DEBUG(3,"schedule: 0");
      Vector lpath;
      Goal goal;
      Vector sub_records, records = new Vector();
      for(int i = 0; i < goals.size(); i++ ) {
         goal = (Goal)goals.elementAt(i);
         lpath = Misc.copyVector(path);
         sub_records = schedule(key,parent,lpath,goal,struct,mode);
         records = Misc.union(records,sub_records);
      }
      return records;
   }

   Vector schedule(String key, PlanRecord parent, Vector path,
                   Goal goal, PlannerQueryStruct struct, boolean mode) {

      Core.DEBUG(3,"schedule: 1");
      // Attempt to exploit side-effects of children of sister goals
      if ( parent != null ) {
         goal = new Goal(goal);
         Fact desc = goal.getFact();
         int required = desc.getNumber();
         required = anySideEffect(desc,parent,goal.getId(),required);
         if ( required == 0 )
            return new Vector();
         else {
            desc.setNumber(required);
            goal.setFact(desc);
         }
      }

      if ( !validTime(goal.getEndTime()) ) {
         addToExternal(struct.external,goal);
         return new Vector();
      }

      Vector tasks = context.TaskDb().findAll(goal.getFact(),path);

      Core.DEBUG(3,"Tasks for: " + goal.getFactType());
      Core.DEBUG(3,tasks);

      return schedule(key,parent,path,goal,tasks,struct,mode);
   }

   protected void addToExternal(Vector List, Goal goal) {
      Core.DEBUG(3,"Adding to external ... attempt");
      String id = goal.getId();
      Goal g;
      for(int i = 0; i < List.size(); i++ ) {
         g = (Goal)List.elementAt(i);
         if ( id.equals(g.getId()) ) return;
      }
      Core.DEBUG(3,"Adding to external ... done");
      List.addElement(goal);
   }


   Vector schedule(String key, PlanRecord parent, Vector path, Goal goal,
                   Vector tasks, PlannerQueryStruct struct, boolean mode) {

      Core.DEBUG(3,"schedule: 2");
      Core.DEBUG(3,"schedule: 2 Path0 = " + path);
      Vector records;
      Task t;

      if ( tasks == null )
         tasks = context.TaskDb().findAll(goal.getFact(),path);

      Core.DEBUG(3,"schedule: 2 Path1 = " + path);

      while( !tasks.isEmpty() ) {
         t = (Task)tasks.firstElement();
         records = (t.isPrimitive())
                   ? schedule_primitive(key,parent,path,goal,tasks,struct,mode)
                   : schedule_summary(key,parent,path,goal,tasks,struct,mode);

         if ( !records.isEmpty() )
            return records;
      }
      addToExternal(struct.external,goal);
      return new Vector();
   }


   Vector schedule_summary(String key, PlanRecord parent, Vector path,
                           Goal goal, Vector tasks, PlannerQueryStruct struct,
                           boolean mode) {

      Core.DEBUG(3,"schedule_summary");
      Core.DEBUG(3,"schedule_summary: Path = " + path);
      SummaryTask task = (SummaryTask)tasks.firstElement();
      tasks.removeElementAt(0);

      Vector records;
      String node;
      Decomposition decomposition;

      decomposition = new Decomposition(this,key,parent,path,goal,task);
      records = expand_summary(decomposition,struct,mode);
      Core.DEBUG(3,"schedule_summary end:");
      Core.DEBUG(3,struct);
      return records;
   }

   Vector expand_summary(Decomposition decomposition, PlannerQueryStruct struct,
                         boolean mode) {

      Core.DEBUG(3,"expand_summary");

      Vector records, path, sub_tasks;
      Fact[] consumed, produced;
      String node, key;
      Vector all_records = new Vector();
      PlanRecord rec, parent;
      SuppliedDb given;
      TaskDb db = context.TaskDb();
      Goal goal;

      decomposition.reset();
      while( (node = decomposition.nextNode()) != null ) {
         Core.DEBUG(3,"expand_summary node ... " + node);
         if ( !decomposition.isScheduled(node) &&
              !decomposition.isQueued(node) ) {
            consumed = decomposition.getPreconditions(node);
            produced = decomposition.getPostconditions(node);
            path = decomposition.getPath(node);
            goal = decomposition.getGoal(node);
            Core.DEBUG(3,"expand_summary: goal = " + goal);
            sub_tasks = db.findAll(consumed,produced,path);
            key = decomposition.getKey(node);
            parent = decomposition.getParentRecord(node);
            records = schedule(key,parent,path,goal,sub_tasks,struct,!EXPAND);
            Core.DEBUG(3,"expand_summary record:");
            Core.DEBUG(3,records);
            if ( !records.isEmpty() ) {
               decomposition.setRecords(node,records);
               all_records = Misc.union(all_records,records);
            }
            else {
               struct.decompositions.put(goal.getId(),decomposition);
               given = decomposition.getSuppliedDb(node);
               goal.setSuppliedDb(given);
               decomposition.setQueued(node,true);
            }
         }
      }
      if ( decomposition.allNodesScheduled() ) {
         decomposition.enforceLinks();
         decomposition.reset();
         while( (node = decomposition.nextNode()) != null ) {
            Core.DEBUG(3,"expand_summary node: " + node);
            rec = decomposition.getRecord(node); // REM ALL RECS
            Core.DEBUG(3,"expand_summary getRecord: " + rec);
            if ( rec != null ) {
               key = decomposition.getKey(node);
               path = decomposition.getPath(node);
               records = schedule_children(key,rec,path,struct,mode);
               all_records = Misc.union(all_records,records);
            }
         }
      }
      Core.DEBUG(3,"expand_summary end");
      Core.DEBUG(3,struct);
      return all_records;
   }

   Vector schedule_primitive(String key, PlanRecord parent, Vector path,
                             Goal goal, Vector tasks, PlannerQueryStruct struct,
                             boolean mode) {

      Core.DEBUG(3,"schedule_primitive");
      PrimitiveTask task = (PrimitiveTask)tasks.firstElement();
      tasks.removeElementAt(0);

      PlanRecord rec;
      int stime, etime, duration, top, lstime, ttime;
      boolean space_found;
      Fact consumed;
      Vector sub_records;
      Vector records = new Vector();

      ResourceDb db = context.ResourceDb();
      SuppliedDb given = goal.getSuppliedDb();

      etime = goal.getEndTime();
      ttime = task.getTime();
      if ( goal.isContinuous() ) {
         stime = goal.getStartTime();
         lstime = stime-ttime;
         duration = etime - lstime;
         if ( duration/ttime < goal.getInvocations() )
            return records;
      }
      else {
         lstime = etime-ttime;
         duration = ttime;
      }

      if ( !validTime(etime)  ) return records;
      if ( !validTime(lstime) ) return records;

      // check local & negation
      for(int i = 0; i < task.countPreconditions(); i++ ) {
         consumed = task.getPrecondition(i);
         if ( consumed.isLocal() ) {
            // Locals can be obtained from either the ResourceDb
	    // or the SuppliedDb
            Core.DEBUG(3,"Checking consumed:\n" + consumed.pprint());
            Core.DEBUG(3,"IsLocal: true");

            Fact x1 = null, x2 = null, x = null;
            if ( given != null ) {
               x1 = given.evalLocal(consumed);
               Core.DEBUG(3,"SuppliedDb contains fact:\n" + x1.pprint());
            }
            x2 = db.evalLocal(consumed);
            if ( x1 == null && x2 == null )
               return records;

            if ( x1 != null && x2 != null ) {
               Core.ERROR(x1.disjoin(x2),1016,this);
               x = x1;
            }
            else if ( x1 == null )
               x = x2;
            else
               x = x1;

            Core.DEBUG(3,"Db contains fact:\n" + x.pprint());
            Bindings b = new Bindings(context.whoami());
            consumed.unifiesWith(x,b);
            task.resolve(b);
            Core.DEBUG(3,"IsLocal bindings: " + b);
         }
         else if ( consumed.isNegative() ) {
            Core.DEBUG(3,"Checking negative:\n" + consumed.pprint());
            Core.DEBUG(3,"IsNegative: true");

            if ( !db.evalNegative(consumed) )
               return records;

            Core.DEBUG(3,"Db does not contain fact:\n" + consumed.pprint());
         }
      }

      for(top = etime-1; validTime(top-duration); top-- ) {
         for(int proc = 0; proc < plannerWidth; proc++ ) {
            space_found = true;
            for(int i = top; space_found && i > top-duration; i-- )
               space_found = isFreeCell(proc,i) & space_found;
            if ( space_found ) {
               rec = new PlanRecord(this,key,parent,goal,task,proc,
                                    top-duration+1,top+1);
               rec.setPath(Misc.copyVector(path));
               rec.setAlternativeTasks(tasks);

               for(int i = top; i > top-duration; i-- )
                  assignCell(proc,i,rec);

               struct.internal.addElement(rec);
               records.addElement(rec);
               path.addElement(goal.getFact());

               sub_records = schedule_children(key,rec,path,struct,mode);
               records = Misc.union(records,sub_records);
               return records;
            }
         }
      }

      // !space_found

      Core.DEBUG(3,"Space not found for: " + goal.getFactType());
      Core.DEBUG(3,"tduration = " + duration);
      Core.DEBUG(3,"tetime = " + etime);
      Core.DEBUG(3,"tlstime = " + lstime);

      return records;
   }

   protected Vector schedule_children(String key, PlanRecord rec, Vector path,
                                      PlannerQueryStruct struct, boolean mode) {

      Vector external, subgoals, sub_records, lpath;
      Vector records = new Vector();
      ResourceDb db = context.ResourceDb();
      SuppliedDb given = rec.getSuppliedDb();

      if ( mode == EXPAND ) {
         external = struct.external;
         struct.external = new Vector();
         do {
            if ( given != null )
               given.allocateResources(rec);
            subgoals = db.allocateResources(rec);
            if ( !subgoals.isEmpty() ) {
               lpath = Misc.copyVector(path);
               sub_records = schedule(key,rec,lpath,subgoals,struct,mode);
               records = Misc.union(records,sub_records);
            }
         } while ( struct.external.isEmpty() && !subgoals.isEmpty() );
         struct.external = Misc.union(struct.external,external);
      }
      return records;
   }


   protected void resume_planning(DelegationStruct[] ds,
                                  PlannerQueryStruct struct) {
      Goal g;
      boolean found;
      Decomposition decomposition = null;
      String key;
      Vector path, records;
      PlanRecord rec = null;

      Core.DEBUG(3,"resume_planning ... ds/struct");
      Core.DEBUG(3,ds);
      Core.DEBUG(3,struct);

      String consumer, consumer_id;
      String producer, producer_id;
      String use_ref;
      int amount, start;
      boolean consumed;

      struct.external.removeAllElements();
      for(int i = 0; i < ds.length; i++ ) {
         // assume only one goal in request
         g = (Goal)ds[i].goals.elementAt(0);

         decomposition = (Decomposition)struct.decompositions.remove(g.getId());
         if ( decomposition == null ) {
            found = false;
            for(int j = 0; !found && j < struct.internal.size(); j++ ) {
               rec = (PlanRecord)struct.internal.elementAt(j);
               found = rec.hasSubgoal(g.getId());
            }
            Core.ERROR(found,1017,this);

            producer = ds[i].agent;
            producer_id = g.getId();
            consumer = context.whoami();
            consumer_id = rec.getGoal().getId();
            use_ref = context.newId("used");
            start = rec.getStartTime();
            int precond_position = rec.getConsumedPosition(producer_id);
            consumed = rec.isPreconditionConsumed(precond_position);
            amount = rec.getAmountUsed(precond_position);
            Core.ERROR(amount > 0,1031,this);

            g.addConsumer(producer, producer_id, consumer, consumer_id,
                          use_ref, ds[i].key, start, amount, consumed);

            rec.getConsumedDb().update(producer_id+"/"+use_ref, producer_id);

            path = rec.getChildPath();
            key = rec.getKey();
            schedule_children(key,rec,path,struct,EXPAND);
         }
         else {
            String node = decomposition.getNodeWithGoalId(g.getId());
            Core.DEBUG(3,"getNodeWithGoalId " + g.getId() + " " + node);
            decomposition.setImage(node,g,ds[i].agent,ds[i].key);
            expand_summary(decomposition,struct,EXPAND);
         }
      }
   }

   public void goalConfirmed(Vector original_goals, Vector confirmed_goals,
                             Vector selection) {
      Goal g, g1;
      Vector records;
      DelegationStruct ds;
      ProducerRecord pr;
      ConsumerRecord cr;
      PlanRecord rec;
      ConsumedDb cdb;
      ProducedDb pdb;
      boolean test;
      SuppliedDb given;
/*
   REM: some check may be made with original_goals
*/
      for(int i = 0; i < confirmed_goals.size(); i++ ) {
         g = (Goal)confirmed_goals.elementAt(i);

         records = g.getProducerRecords();
         for(int j = 0; records != null && j < records.size(); j++ ) {
            pr = (ProducerRecord)records.elementAt(j);
            if ( pr.consumer.equals(context.whoami()) ) {
               rec = (PlanRecord)this.get(pr.consumer_id);
               Core.ERROR(rec,1001,this);
               cdb = rec.getConsumedDb();
               test = cdb.update(pr.producer_id+"/"+pr.use_ref,pr.use_ref);
               Core.ERROR(test,1002,this);
            }
            else {
               for(int k = 0; k < selection.size(); k++ ) {
                  ds = (DelegationStruct)selection.elementAt(k);
                  // Assumes one element only in ds.goals
                  g1 = (Goal)ds.goals.elementAt(0);
                  given = g1.getSuppliedDb();
                  if ( given != null && given.isReserved(pr.supply_ref) )
                     g1.addProducer(pr);
               }
            }
         }

         records = g.getConsumerRecords();
         for(int j = 0; records != null && j < records.size(); j++ ) {
            cr = (ConsumerRecord)records.elementAt(j);
            if ( cr.producer.equals(context.whoami()) ) {
               rec = (PlanRecord)this.get(cr.producer_id);
               Core.ERROR(rec,1003,this);
               pdb = rec.getProducedDb();
               Core.DEBUG(3,"CdB replacing " + cr.producer_id + " with " +
                                 cr.consumer_id+"/"+cr.use_ref);
               Core.DEBUG(3,records);
               Core.DEBUG(3,rec);
               
               test = pdb.replaceOrAdd(cr.producer_id,
                                       cr.consumer_id + "/" + cr.use_ref,
                                       cr.start, cr.amount, cr.consumed );
               Core.DEBUG(3,"Replacement is " + test);
               Core.ERROR(test,1004,this);
            }
            else {
               for(int k = 0; k < selection.size(); k++ ) {
                  ds = (DelegationStruct)selection.elementAt(k);
                  // Assumes one element only in ds.goals
                  g1 = (Goal)ds.goals.elementAt(0);
                  g1.addConsumer(cr);
               }
            }
         }
      }
   }

   protected boolean validTime(int t) {  
      return (now + plannerLength >= t && t >= now);
   }

   protected boolean validProc(int proc) {
      return ( proc >= 0 && proc < plannerWidth);
   }

   protected boolean isFreeCell(int proc, int t) {
      Core.ERROR(proc >= 0 && proc < plannerWidth, 1018,this);
      Core.ERROR(now + plannerLength > t && t >= now, 1019,this);
      return (table[proc][t-now] == null);
   }

    /** 
        1.3 promoted to public - used to decide which job to execute
    */
    //synchronized
  public void shuffle() {
      checkRecords();
      for(int i = 0; i < plannerWidth; i++ ) {
         for(int j = 0; j < plannerLength-1; j++ )
            assignCell(i,j+now,table[i][j+1]);
         assignCell(i,plannerLength-1+now,null);
      }
      now++;
   }

//synchronized
    synchronized void  executeEarliest() {
      PlanRecord rec;
      Enumeration enum  = this.elements();
      while( enum.hasMoreElements() ) {
         rec = (PlanRecord)enum.nextElement();
         if ( rec.getState() == PlanRecord.FIRM &&
              rec.isDiscrete() &&
              rec.getStartTime() > now &&
              rec.hasEnoughResources() )
            executeEarliest(rec);
      }
   }
   
   
   //synchronized           
    synchronized void executeEarliest(PlanRecord rec) {
      int stime, etime, duration, top;
      boolean space_found;

      etime = rec.getEndTime();
      stime = rec.getStartTime();
      duration = etime - stime;

      if ( !validTime(etime) ) return;
      if ( !validTime(stime) ) return;

      for( top = now+1; top < stime; top++ ) {
         for( int proc = 0; proc < plannerWidth; proc++ ) {
            space_found = true;
            for( int i = top; i < top+duration; i++ ) {
               space_found = space_found && (isFreeCell(proc,i) ||
                             rec.isOnCell(proc,i));
               if ( !space_found ) break;
            }
            if ( space_found ) {
               Core.DEBUG(3,"Reassigning rec " + rec.getId() +
                                 " from [" + stime + "," + etime + "] to [" +
                                 top + "," + (top+duration) +"]");
               rec.reassign(proc,top);
               for(int i = top; i < top+duration; i++ )
                  assignCell(proc,i,rec);
               return;
            }
         }
      }
   }


    // synchronized
   synchronized boolean incrementProcessorTime(PlanRecord rec,int time) {
      if ( !validTime(time) ) return false;
      if ( isFreeCell(rec.getProc(),time) ) {
         rec.incrementTime(time);
         assignCell(rec.getProc(),time,rec);
         return true;
      }
      else {
         // shift other records to different processors if possible
         // then rec.incrementTime()
      }
      return false;
   }


    /** 
        start Firmly booked tasks & cancel Tentatively booked ones
        1.3 - promoted to public 
        // synched ?
        */
   synchronized public void checkRecords() { 
      PlanRecord rec, root;
      ExecutionMonitor monitor = context.ExecutionMonitor();
      for( int i = 0; i < plannerWidth; i++ ) {
         if ( (rec = table[i][0]) != null ) {
            switch( rec.getState() ) {
               case PlanRecord.JEOPARDY:
               case PlanRecord.AGREEMENT:
                  break;
               case PlanRecord.TEMP:
               case PlanRecord.TENTATIVE:
                  // NOTIFY MONITORS FREE CELL
                  root = rec.getRoot();
                  root.dispose();
                  break;
               case PlanRecord.FIRM:
                  if ( rec.exec() )
                     ; // NOTIFY MONITORS START CELL
                  break;
               case PlanRecord.RUNNING:
                  if ( rec.overRun() )
                     ; // NOTIFY MONITORS STOP CELL
                  else 
                     break;
               case PlanRecord.FAILED:
                  // NOTIFY MONITORS CELL FAILED
                  root = rec.getRoot();
                  root.dispose();
                  break;
               case PlanRecord.COMPLETED:
                  // NOTIFY MONITORS CELL COMPLETED
                  rec.dispose();
                  break;
            }
         }
      }
      if ( context.getExecuteEarliest() ) executeEarliest();
   }
 
 
   public void notifyReceived(Fact f1, String goalId, String subgoalId) {
    try {
      Core.DEBUG(3,"NotifyReceived...\n" + f1.pprint());
      PlanRecord rec = (PlanRecord)this.get(goalId);
      Core.ERROR(rec,1020,this);
      context.ResourceDb().add(f1);
      rec.preconditionExists(subgoalId);}
      catch (Exception e) { 
        e.printStackTrace(); 
      }
      
   }

   protected void assignCell(int proc, int t, PlanRecord rec) {
      Core.ERROR(proc >= 0 && proc < plannerWidth,1021,this);
      Core.ERROR(now + plannerLength > t && t >= now,1022,this);
      table[proc][t-now] = rec;
   }

   void freeCell( int proc, int t) {
      Core.ERROR(proc >= 0 && proc < plannerWidth,1023,this);
      if ( now + plannerLength > t && t >= now ) {
         table[proc][t-now] = null;
      }
   }

   void freeCells(int proc, int s, int e) {
      for(int i = s; i < e; i++ ) freeCell(proc,i);
   }

   public Goal recreateSubgoal(Goal goal) {
      PlanRecord rec;
      String goalId = goal.getId();
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         rec = (PlanRecord) enum.nextElement();
         if ( rec.hasSubgoal(goalId) )
            return rec.recreateSubgoal(goal);
      }
      Goal g = new Goal(goal);
      g.setId(context.newId("subgoal"));
      g.setImage(goal.getId());
      g.setConfirmTime(new Time(now() +
                       context.getReplanPeriod()));
      return g;
   }


   public void reconfirmParentOf(Vector goals) {
      for( int i = 0; i < goals.size(); i++ )
         reconfirmParentOf( (Goal)goals.elementAt(i) );
   }

   
   public void reconfirmParentOf(Goal g) {
      PlanRecord rec;
      String goalId = g.getId();
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         rec = (PlanRecord) enum.nextElement();
         if ( rec.hasSubgoal(goalId) ) {
            rec.reconfirm();
            return;
         }
         else if ( rec.getGoal().getId().equals(goalId) ) {
            if ( rec.getParent() != null ) {
               rec.getParent().reconfirm();
               return;
            }
         }
      }
      Core.DEBUG(3,"reconfirmParentOf error ...  " + g);
      Core.ERROR(null,1024,this);
   }
   
   
   public void failParentOf(Vector goals) {
      for( int i = 0; i < goals.size(); i++ )
         failParentOf( (Goal)goals.elementAt(i) );
   }
   
   
   public void failParentOf(Goal g) {
      PlanRecord rec;
      String goalId = g.getId();
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         rec = (PlanRecord) enum.nextElement();
         if ( rec.hasSubgoal(goalId) ) {
            rec.setState(PlanRecord.FAILED);
            return;
         }
      }
      Core.DEBUG(3,"failParentOf error ...  " + g);
//      Core.ERROR(null,1025,this);
   }
   
   
   void softFailParentOf(Vector goals, PlannerQueryStruct struct, int mode) {
      Core.DEBUG(3,"softFailParentOf Goal = " + goals);
      Core.DEBUG(3,"softFailParentOf PlannerQueryStruct = " + struct);

      PlanRecord rec = null;
      Goal g, goal = null;
      String goalId;
      boolean found;
      Decomposition decomposition;

      for(int i = 0; i < goals.size(); i++ ) {
         g = (Goal)goals.elementAt(i);
         goalId = g.getId();

         // remove unachievable goal (g) from external list
         for(int j = 0; j < struct.external.size(); j++ ) {
            goal = (Goal)struct.external.elementAt(j);
            if ( goalId.equals(goal.getId()) ) {
               struct.external.removeElementAt(j--);
               break;
            }
         }
               
         decomposition = (Decomposition)struct.decompositions.remove(g.getId());
         if ( decomposition != null ) {
            // decomposition.softFail(struct,mode);
         }
         else {
            found = false;
            for(int j = 0; !found && j < struct.internal.size(); j++ ) {
               rec = (PlanRecord) struct.internal.elementAt(j);
               found = rec.hasSubgoal(goalId);
            }
            if ( found ) // swapped !found to found
               rec.softFail(struct,mode);
         }
      }

      // check records for validity
      for(int i = 0; i < struct.internal.size(); i++ ) {
         rec = (PlanRecord)struct.internal.elementAt(i);
         if ( !containsRecord(rec) )
            struct.internal.removeElementAt(i--);
      }
      // check decompositions for validity
      Enumeration enum = struct.decompositions.keys();
      while( enum.hasMoreElements() ) {
         goalId = (String)enum.nextElement();
         found = false;
         for(int i = 0; !found && i < struct.external.size(); i++ ) {
            goal = (Goal)struct.external.elementAt(i);
            found = goalId.equals(goal.getId());
         }
         if ( !found ) struct.decompositions.remove(goalId);
      }
   }

   /**
    * Use a PlanningMonitor if your code needs to react to changes in the Planner
    */
   public void addPlanningMonitor(PlanningMonitor monitor, long type) {
      if ( (type & PlanningEvent.START_MASK) != 0 )
         eventMonitor[START].add(monitor);
      if ( (type & PlanningEvent.FAIL_MASK) != 0 )
         eventMonitor[FAIL].add(monitor);
      if ( (type & PlanningEvent.SUCCEED_MASK) != 0 )
         eventMonitor[SUCCEED].add(monitor);
   }

   public void removePlanningMonitor(PlanningMonitor monitor, long type) {
      if ( (type & PlanningEvent.START_MASK) != 0 )
         eventMonitor[START].remove(monitor);
      if ( (type & PlanningEvent.FAIL_MASK) != 0 )
         eventMonitor[FAIL].remove(monitor);
      if ( (type & PlanningEvent.SUCCEED_MASK) != 0 )
         eventMonitor[SUCCEED].remove(monitor);
   }

   /**
    * Use a PlanStepMonitor if your code needs to react to state changes in a
    * particular plan
    */
   public void addPlanStepMonitor(PlanStepMonitor monitor, long type,
                                  boolean notify_previous) {
      addPlanStepMonitor(monitor,type);
      if ( !notify_previous ) return;

      Enumeration enum = elements();
      PlanRecord record;
      PlanStepEvent event;

      while( enum.hasMoreElements() ) {
         record = (PlanRecord)enum.nextElement();
         event = new PlanStepEvent(this,record,PlanStepEvent.CREATE_MASK);
         monitor.planStepCreatedEvent(event);
      }
   }

   public void addPlanStepMonitor(PlanStepMonitor monitor, long type) {
      if ( (type & PlanStepEvent.CREATE_MASK) != 0 )
         eventMonitor[CREATE].add(monitor);
      if ( (type & PlanStepEvent.DISPOSE_MASK) != 0 )
         eventMonitor[DISPOSE].add(monitor);
      if ( (type & PlanStepEvent.STATE_CHANGE_MASK) != 0 )
         eventMonitor[STATE_CHANGE].add(monitor);
   }

   public void removePlanStepMonitor(PlanStepMonitor monitor, long type) {
      if ( (type & PlanStepEvent.CREATE_MASK) != 0 )
         eventMonitor[CREATE].remove(monitor);
      if ( (type & PlanStepEvent.DISPOSE_MASK) != 0 )
         eventMonitor[DISPOSE].remove(monitor);
      if ( (type & PlanStepEvent.STATE_CHANGE_MASK) != 0 )
         eventMonitor[STATE_CHANGE].remove(monitor);
   }

   void notifyMonitors(Goal goal, int event_type, int sub_type) {
      if ( eventMonitor[event_type].isEmpty() ) return;

      Enumeration enum = eventMonitor[event_type].elements();
      PlanningMonitor monitor;
      PlanningEvent event;
      switch(event_type) {
         case START:
              event = new PlanningEvent(this,goal,PlanningEvent.START_MASK,sub_type);
              while( enum.hasMoreElements() ) {
                 monitor = (PlanningMonitor)enum.nextElement();
                 monitor.planningStartedEvent(event);
              }
              break;
         case FAIL:
              event = new PlanningEvent(this,goal,PlanningEvent.FAIL_MASK,sub_type);
              while( enum.hasMoreElements() ) {
                 monitor = (PlanningMonitor)enum.nextElement();
                 monitor.planningFailedEvent(event);
              }
              break;
         case SUCCEED:
              event = new PlanningEvent(this,goal,PlanningEvent.SUCCEED_MASK,sub_type);
              while( enum.hasMoreElements() ) {
                 monitor = (PlanningMonitor)enum.nextElement();
                 monitor.planningSucceededEvent(event);
              }
              break;
      }
   }

   void notifyMonitors(PlanRecord record, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      Enumeration enum = eventMonitor[type].elements();
      Core.ERROR(enum,2001,this);
      PlanStepMonitor monitor;
      PlanStepEvent event;
      switch(type) {
         case CREATE:
              event = new PlanStepEvent(this,record,PlanStepEvent.CREATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (PlanStepMonitor)enum.nextElement();
                 monitor.planStepCreatedEvent(event);
              }
              break;
         case DISPOSE:
              event = new PlanStepEvent(this,record,PlanStepEvent.DISPOSE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (PlanStepMonitor)enum.nextElement();
                 monitor.planStepDisposedEvent(event);
              }
              break;
         case STATE_CHANGE:
              event = new PlanStepEvent(this,record,PlanStepEvent.STATE_CHANGE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (PlanStepMonitor)enum.nextElement();
                 monitor.planStepStateChangedEvent(event);
              }
              break;
      }
   }
   
   
   /** 
    now introduced to prevent the need for a call back to the context object 
    to get timeing - decouples the Planner a bit..
    */
   protected double now () { 
    return context.now(); 
   }
   
}
