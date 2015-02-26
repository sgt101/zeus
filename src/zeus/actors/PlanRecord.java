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



package zeus.actors;

import java.util.*;
import java.awt.Color;
import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.DelegationStruct;

/**
    * PlanRecord is used to manage the execution of a (primitive, mutable or infered) task. 
    * The field task is the primitive task being executed, the field thread is the ZeusTask 
    * that is actually being run
    */
public class PlanRecord implements SuppliedRequester {
   public static final int FREE      = 0;
   public static final int TEMP      = 1;
   public static final int TENTATIVE = 2;
   public static final int FIRM      = 3;
   public static final int RUNNING   = 4;
   public static final int FAILED    = 5;
   public static final int COMPLETED = 6;
   public static final int JEOPARDY  = 7;
   public static final int AGREEMENT = 8;

   protected static final boolean BOOKED = true;

   public static final Color[] color = {
      Color.lightGray,
      Color.orange,
      Color.yellow,
      Color.cyan,
      Color.green,
      Color.red,
      Color.white,
      Color.magenta,
      Color.blue
   };

   public static final String[] state_string = {
      "Free",
      "Temporary",
      "Tentative",
      "Firm",
      "Running",
      "Failed",
      "Completed",
      "Jeopardy",
      "Service-Agreement"
   };

   protected Planner planner;
   protected int state = TEMP;
   protected PlanRecord parent;
   protected String id;
   protected Goal goal;
   protected PrimitiveTask task;
   protected int proc;
   protected int start_time;
   protected int lstart_time;
   protected int end_time;
   protected double cost;
   protected ZeusTask thread = null;
   protected String diagnostic = null;
   protected String key = null;
   protected boolean is_disposed = false;

   protected ConsumedDb consumedDb = null;
   protected ProducedDb producedDb = null;
   protected int reconfirm_count = 0;

   protected Vector other_tasks = null;
   protected Vector path = null;

   // additional info for handling continuous goals and their enactments
   protected PlanRecord original = null;
   protected int noAllowedInvocations = 1;
   protected int noAvailableItems = 0;
   protected boolean[] slots = null;
   protected Vector    images = null;


   public PlanRecord () {
    ;
    }


   protected void init(Planner planner, String key, PlanRecord parent,
                       Goal goal, PrimitiveTask task, int proc, int start,
                       int end) {

      Assert.notNull(key);
      Assert.notNull(goal);
      Assert.notNull(task);
      Assert.notFalse(start >= 0);
      Assert.notFalse(end >= 0);
      Assert.notFalse(proc >= 0);

      this.planner = planner;
      this.key = key;
      this.parent = parent;
      this.id = planner.getAgentContext().newId("PlanRecord");
      this.goal = goal;
      this.task = task;
      this.start_time = start;
      this.lstart_time = start;
      this.end_time = end;
      this.proc = proc;
      this.cost = task.getCost();

      if ( (noAvailableItems = goal.getFact().getNumber()) == 0 ) {
         Core.USER_ERROR("Integer expected in goal.fact.no field." +
         "\nEnsure \"no\" constraints are defined in all task specifications");

         // ERROR FIX
         noAvailableItems = 1;
      }
      task.preprocess();
      consumedDb = new ConsumedDb(this,task);
      producedDb = new ProducedDb(this,task);

      Core.DEBUG(3,"PlanRecord created: " + this);
   }

   public PlanRecord(Planner planner, String key, PlanRecord parent,
                     Goal goal, PrimitiveTask task, int proc, int start,
                     int end) {

      init(planner, key, parent, goal, task, proc, start, end);
      producedDb.add(task.getActiveEffectPos(), parent, noAvailableItems,
                     goal.getId(),!(goal.getFact().isReadOnly()));

      if ( parent != null )
         parent.updateCost(cost);

      planner.add(this);
      planner.notifyMonitors(this,Planner.CREATE);
   }

   protected PlanRecord(Planner planner, PlanRecord original, String key,
                        PlanRecord parent, Goal goal, PrimitiveTask task,
                        int proc, int start, int end) {

      Assert.notNull(original);
      Assert.notFalse(goal.whichType() == Goal.DISCRETE);
      init(planner, key, parent, goal, task, proc, start, end);
      this.original = original;

      planner.add(this);
      planner.notifyMonitors(this,Planner.CREATE);
   }

   public String        getId()              { return id; }
   public int           getProc()            { return proc; }
   public int           getState()           { return state; }
   public int           getStartTime()       { return start_time; }
   public int           getLatestStartTime() { return lstart_time; }
   public int           getEndTime()         { return end_time; }
   public PrimitiveTask getTask()            { return task; }
   public Goal          getGoal()            { return goal; }
   public PlanRecord    getParent()          { return parent; }
   public String        getKey()             { return key; }
   public double        getCost()            { return cost; }
   public ConsumedDb    getConsumedDb()      { return consumedDb; }
   public ProducedDb    getProducedDb()      { return producedDb; }
   public AgentContext  getAgentContext()    { return planner.getAgentContext(); }
   public SuppliedDb    getSuppliedDb()      { return goal.getSuppliedDb(); }

   public int anySideEffect(Fact desc, PlanRecord rec,
                            Object precond, int required) {

      boolean status = isDiscrete() && (state == FIRM || state == RUNNING);
      status |= (getRoot() == rec.getRoot());

      if ( !status ) return required;

      Fact[] produced = task.getPostconditions();
      Bindings b = new Bindings(planner.getAgentContext().whoami());
      for(int i = 0; required > 0 && i < produced.length; i++, b.clear()) {
         if ( produced[i].unifiesWith(desc,b) )
            required = producedDb.anySideEffect(i,rec,precond,required);
      }
      return required;
   }

   public void setAlternativeTasks(Vector Tasks) {
      this.other_tasks = Tasks;
   }
   
   
   public void setPath(Vector path) {
      this.path = path;
   }
   
   
   public Vector getChildPath() {
      Vector child_path = Misc.copyVector(path);
      child_path.addElement(goal.getFact());
      return child_path;
   }


   public PlanRecord[] getChildren() { 
      return consumedDb.getChildren();
   }
   
   
   public boolean hasAtMostOneParent(PlanRecord parent, String key) {
      return producedDb.hasAtMostOneParent(parent,key);
   }
   public DataRec getDatarec(int precond_position) {
      return consumedDb.getDatarec(precond_position);
   }
   public int getConsumedPosition(String goal_id) {
      return consumedDb.getPosition(goal_id);
   }
   
   
   public boolean isPreconditionConsumed(int precond_position) {
      Fact precond = task.getPrecondition(precond_position);
      return !precond.isReadOnly();
   }
   
   
   public int noRequiredItems(int precond_position) {
      return consumedDb.requiredItems(precond_position);
   }
   
   
   public int getAmountUsed(int precond_position) {
      return consumedDb.amountUsed(precond_position);
   }
   
   
   public boolean setSupplier(int precond_position, int amount,
                              SuppliedItem item) {

      String Id = planner.getAgentContext().newId("used");
      boolean consumed = isPreconditionConsumed(precond_position);
      boolean b = item.reserve(Id,start_time,consumed,
         amount,planner.getAgentContext().whoami(), goal.getId(),key);
      if ( b ) consumedDb.add(precond_position,Id,amount);
      return b;
   }

   public void chainPrecondition(PlanRecord child, int effect_position,
                                 int amount, int precond_position) {
      consumedDb.add(precond_position,child,effect_position,amount);
   }
   
   
   public void preconditionExists(PlanRecord child, int effect_position,
                                  int amount, int precond_position) {
      consumedDb.factExists(precond_position,child,effect_position,amount);
   }
   
   
   public void preconditionExists(String goal_id) {
      consumedDb.factExists(goal_id);
   }
   
   
   public void replacePrecondition(String goal_id, PlanRecord child,
                                   int effect_position, int amount) {
      consumedDb.replace(goal_id,child,effect_position,amount);
   }
   
   
   public void breakEffectChain(int effect_position, PlanRecord rec,
                                int precond_position, int amount) {
      producedDb.remove(effect_position,rec,precond_position,amount);
      if ( parent == rec && !producedDb.references(parent) ) {
         parent = producedDb.firstParent();
         if ( parent != null ) {
            int first_position = producedDb.firstPosition(parent);
            task.setActiveEffect(first_position);
            goal.setFact(task.getActiveEffect());
            setKey(parent.getKey());
         }
         else {
//REM: need to check this later
            String other_key = producedDb.firstKey();
            if ( other_key != null )
               setKey(other_key);
         }
      }
   }

   public Goal recreateSubgoal(Goal g) {
      int position = consumedDb.getPosition(g.getId());
      Fact x = consumedDb.remove(g.getId());
      Goal g1 = createSubgoal(x,position);
      double t = planner.getAgentContext().now() +
                 planner.getAgentContext().getReplanPeriod();
      g1.setConfirmTime(new Time(t));

      g1.setSuppliedDb(this.goal.getSuppliedDb());

      setState(JEOPARDY);
      return g1;
   }

   public void reconfirm() {
      Assert.notFalse(state == JEOPARDY);
      Core.DEBUG(2,"Decrementing reconfirm from: " + reconfirm_count);
      if ( --reconfirm_count == 0 )
         setState(FIRM);
   }

   public void setKey(String nkey) {
      Core.DEBUG(3,"PlanRecord: " + this + "\n\tsetKey: " + nkey);
      if ( parent == null || nkey.equals(parent.getKey()) ) {
         consumedDb.update(nkey,this.key);
         this.key = nkey;

         PlanRecord[] children = consumedDb.getChildren();
         for(int i = 0; i < children.length; i++ )
            children[i].setKey(nkey);
      }
   }

   public boolean isDiscrete() {
      return goal.whichType() == Goal.DISCRETE;
   }
   public boolean isContinuous() {
      return goal.whichType() == Goal.CONTINUOUS;
   }
   public boolean hasMoreEnactments() {
      Assert.notFalse(goal.whichType() == Goal.CONTINUOUS);
      return noAvailableItems > 0;
   }
   public boolean hasEnoughResources() {
      return consumedDb.hasEnoughResources();
   }

   public void updateCost(double value) {
// System.err.print(this + "\n\tUpdating cost from " + cost);
      this.cost += value;
// System.err.println(" to " + cost);
      if ( parent != null ) parent.updateCost(value);
   }

   public boolean applyConstraints(Bindings bindings) {
      if ( !task.applyConstraints(bindings) ) return false;
      return producedDb.constrain(bindings);
   }

   public Bindings getBindings() {
      Bindings b = new Bindings(planner.getAgentContext().whoami());
      Fact f1 = goal.getFact();
      Fact f2 = task.getActiveEffect();
      Assert.notFalse( f2.unifiesWith(f1,b) );
      return b;
   }

   public boolean isOnCell(int proc,int t) {
      return this.proc == proc && t >= start_time && t < end_time;
   }

   protected void vacatingPosition(int proc, int start, int end) {
      Assert.notFalse(goal.whichType() == Goal.CONTINUOUS);
      Assert.notFalse(proc == this.proc);

      for(int i = start - start_time; i < end - start_time; i++ )
         slots[i] = !BOOKED;

      for(int i = start; i < end; i++ )
         planner.assignCell(proc,i,this);
   }
   
   
   protected void originalDisposed() {
      Assert.notNull(original);
      original = null;
   }
   
   
   public void reassign(int pc, int st) {
      if ( original != null ) 
         original.vacatingPosition(proc,start_time,end_time);
      else 
         planner.freeCells(proc,start_time,end_time);

      proc = pc;
      start_time = st;
      end_time = st + task.getTime();
      consumedDb.newStartTime(start_time);
   }
   
   
   public void incrementTime(int t) {
      // we want to shift from the [now] to the [now+1] position
      planner.freeCell(proc,end_time-1);
      end_time = t+1;
   }
      
      
   public String diagnostic() {
      return diagnostic;
   }


    /** 
       * mapPreToPost is used to map precondition variables that are 
       * supposed to be copied to post conditions
       * synchronised?
       *@author Simon Thompson
       *@since 1.2
        */
  public  Fact [][] mapPreToPost(Fact [][] input, Fact[] exp_out) { 
    // assume one out for now. 
    Core.DEBUG (1, "in mapPreToPost"); 
    AgentContext cont = this.getAgentContext(); 
    TaskDb tdb = cont.getTaskDb(); 
    String taskName = task.getName(); 
    Task retTask = tdb.getTask (taskName); 
    
    Fact [] retPre = retTask.getPreconditions(); 
    Fact [] retPost = retTask.getPostconditions(); 
      
    debug("\nTask from DB = " + retTask.toString()); 
    
    debug("\nThis task = " + task.toString()); 
    
    Fact [][] output = new Fact [exp_out.length][1];
    debug(String.valueOf(input.length));
    for (int count = 0; count<input.length; count++) { 
        // for all input facts 
        debug(String.valueOf(exp_out.length)); 
        for (int count2 = 0; count2 < exp_out.length; count2++) { 
            // for all output facts
            // primitiveTask post conditions... effects and preconditions = 
            output[count2][0] = exp_out[count2];
            // build a map of copy instructions from the task description
            String [] map = retPost[count2].buildMap(retPre[count]);
            // apply that map to the pre and post conditions for this instantiation
            output[count2][0].doMap(input[count][0],map); 
            debug("in = " + input[count][0] + " out = " +output[count2][0]); 
                    
                }
        }
    return output; 
    
  }
  
  /** 
    bind the any variables in the task cost and time to their pre & postcondition 
    values at run time 
    
    */
    public void mapCostAndTime() {
        AgentContext cont = this.getAgentContext(); 
        TaskDb tdb = cont.getTaskDb(); 
        String taskName = task.getName();
        Task retTask = tdb.getTask (taskName); 
        Fact [] retPost = retTask.getPostconditions(); 
        Fact [] post = task.getPostconditions(); 
        Fact [] retPre = retTask.getPreconditions(); 
        Fact [] pre = task.getPreconditions(); 
        ValueFunction time = task.getTimeFn(); 
        ValueFunction cost = task.getCostFn(); 
        for (int i = 0; i<retPost.length; i++) { 
            map (time, retPost[i], post[i]);
            map  (cost, retPost[i], post[i]); 
        }
        for (int i = 0; i<retPre.length; i++) { 
            map (time, retPre[i], pre[i]); 
            map (cost,retPre[i], pre[i]);
        }
            

    }


    public void map (ValueFunction val, Fact lhs, Fact rhs) { 
        debug ("val = " + val.toString() + "\nlhs = " + lhs.toString() + "\nrhs = " + rhs.toString()); 
    }
    
    
 public boolean exec() {
     diagnostic = null;
     PrimitiveTask task_image = new PrimitiveTask(task);
  
/**
      Fact fact = goal_image.getFact();
      Fact f2 = task_image.getActiveEffect();
      es.ok = es.ok && f2.unifiesWith(fact,b)
*/
        
      if ( goal.whichType() != Goal.DISCRETE || thread != null )
         return false;

      if ( !consumedDb.hasEnoughResources() ) {
         setState(FAILED);
         diagnostic = "1: Cannot exec - inadequate resources";
         Core.DEBUG(2,diagnostic);
         return true;
      }
      try {
         String name = task.getName();

         /* For experiment only */
         if ( name.startsWith("GenTask") )
            name = "GenTask";
         /* Experiment ends */
       //  task.relaxNumberFields();   
         
         Class c = Class.forName(name);
         thread = (ZeusTask) c.newInstance();
         Fact[][] input = consumedDb.getInputData();
         consumedDb.consumeResources();
         System.out.println(task.pprint());
         thread.setInputArgs(input);
         /** 
            everything else is being set from the primitiveTask, but
            this is not right... I think that the output arguments need to
            be mapped from the inputArgs at this point
            */
            
         thread.setExpectedOutputArgs(task.getOriginalPostconditions());
         
         Fact[]  exp_input =  task.getPreconditions();
         thread.setExpectedInputArgs(exp_input);

         thread.setMedia(goal.getTargetMedia());
         thread.setContext(getAgentContext());
         Fact [] exp_out = task.getOriginalPostconditions(); 
         Fact [][] act_out = null; 
         // deal with 0 preconditions
         if (input.length>0) { 
            act_out = mapPreToPost (input,exp_out);// added 1.2.1
         }
            else {
                act_out = new Fact [exp_out.length][1];
                for (int i = 0; i<exp_out.length;i++) { 
                    act_out[i][0] = exp_out[i]; }
            }
         mapCostAndTime(); // addesd 1.2.1
         Bindings b = new Bindings(planner.getAgentContext().whoami());
         task.resolve(b);
        
         thread.setOutputArgs (act_out); 
         for (int count = 0;  count<act_out.length; count ++ ) { 
            exp_out[count]= act_out[count][0]; 
         }
         thread.setExpectedOutputArgs(exp_out); 
       // was commented out         
            Fact [][] user_out = new Fact [task.getOriginalPostconditions().length][1];
            Fact[][] output = new Fact[task.getOriginalPostconditions().length][1];
            
            for (int count = 0; count<user_out.length; count++ ) { 
                user_out[count][0] = exp_out[count];
            }
 
            for(int i = 0; i < user_out.length; i++ ) {
                debug ("user_out[" + String.valueOf(i) + "] = " + user_out[i].toString()); 
               output[i] = user_out[i];
         
               }

         /**   int j = user_out.length;
            for(int i = 0; i < exp_input.length; i++ )
               if ( exp_input[i].isReplaced() )
                  output[j++] = input[i]; */
       
         task.relaxNumberFields();
         thread.start(); // 21/06/01
     //    producedDb.allocatePostconditions(output);

       //thread.start();
         setState(RUNNING);
         diagnostic = "Running started ... ";
         Core.DEBUG(2,diagnostic);
      }
      
      catch(ClassNotFoundException e) {
         setState(FAILED);
         diagnostic = "2: Cannot exec - ClassNotFoundException";
         Core.DEBUG(2,diagnostic);
      }
      catch(IllegalAccessException e) {
         setState(FAILED);
         diagnostic = "3: Cannot exec - IllegalAccessException";
         Core.DEBUG(2,diagnostic);
      }
      catch(InstantiationException e) {
         setState(FAILED);
         diagnostic = "4: Cannot exec - InstantiationException";
         Core.DEBUG(2,diagnostic);
      }
      return true;
   }

 

    
  
   public boolean overRun() {
      ResourceDb resDb = getAgentContext().getResourceDb(); 
      if ( thread.isFinished() ) {
         Fact[][] user_out = thread.getOutputArgs();

         if ( user_out != null && user_out.length > 0 ) {
            Fact[][] output = thread.outputArgs; 
	    //    Fact[][] output = new Fact[task.countPostconditions()][];
            Fact[][] input = thread.getInputArgs();
            Fact[]   exp_input = thread.getExpectedInputArgs();

           // for(int i = 0; i < user_out.length; i++ )
             // output[i] = user_out[i];

              int j = user_out.length;
              for(int i = 0; i < exp_input.length; i++ )
              if ( exp_input[i].isReplaced() )
                      resDb.add(input[i]); 
                      //output[j++] = input[i]; // surely added to resdb!

            // readded 21/6/01
            if (output!= null) 
                producedDb.allocatePostconditions(output);
            
            setState(COMPLETED);
            return false;
         }
         else {
            setState(FAILED);
            diagnostic = "5: Failed - task execution did not produce output";
            return false;
         }
      }
      double t = planner.getAgentContext().now();
      // 1.3 change >= 
      if ( t >= end_time ) {
         if ( t < goal.getEndTime() &&
              planner.incrementProcessorTime(this,(int)t) ) {
            // we have some time to spare and a free proc to use
            return false;
         }
         setState(FAILED);
         diagnostic = "5: Failed - overrun allocated time: " +
                      t + " > " + end_time;
         thread.abort();
         return true;
      }
      return false;
   }


   protected void setState(int newState, PlanRecord parent) {
      /*
      This guards against cases where a foster parent tries to
      change the state of a child (cf. side-effects).
      */
      if ( parent == this.parent )
         setState(newState);
   }
   
   
   public void setState(int newState) {
      Assert.notFalse(newState >= TEMP && newState <= AGREEMENT);

      Core.DEBUG(3,"Setting state for rec " + id + " from " +
                   state_string[state] + " to " + state_string[newState]);
      state = newState;
      planner.notifyMonitors(this,Planner.STATE_CHANGE);

      PlanRecord[] children;
      switch( state ) {
         case TEMP:
         case TENTATIVE:
              children = getChildren();
              for(int i = 0; i < children.length; i++ )
                 children[i].setState(newState,this);
              break;

         case FIRM:
              Core.DEBUG(3,this);
              Core.DEBUG(3,producedDb);
              Core.DEBUG(3,consumedDb);

              goal.setSuppliedDb(null);
              if ( goal.whichType() == Goal.CONTINUOUS ) {
                 setState(AGREEMENT);
                 return;
              }
              children = getChildren();
              for(int i = 0; i < children.length; i++ ) 
                 children[i].setState(newState,this);
              break;

         case AGREEMENT:
              goal.setSuppliedDb(null);
              Assert.notFalse(goal.whichType() == Goal.CONTINUOUS);
              if ( slots == null ) {
                 noAllowedInvocations = goal.getInvocations();
                 slots = new boolean[end_time-start_time];
                 for(int i = 0; i < slots.length; i++ )
                    slots[i] = !BOOKED;
                 images = new Vector();
              }
              children = getChildren();
              for(int i = 0; i < children.length; i++ )
                 children[i].setState(newState,this);
              break;

         case RUNNING:
              break;

         case COMPLETED:
              break;

         case FAILED:
              dispose(parent,key);
              producedDb.notifyFailed(other_tasks,path); 
              break;

         case JEOPARDY:
              reconfirm_count++;
              break;

         default:
              break;
      }
   }
   
   
   public void softFail(PlannerQueryStruct struct, int mode) {
      Core.DEBUG(2,"softFail..." + struct);

      if ( mode == Planner.REPLAN ) {
         state = FAILED;
         planner.notifyMonitors(this,Planner.STATE_CHANGE);
      }

      Goal g;
      Vector subgoals = descendantSubgoals();
      subgoals.addElement(this.goal.getId());
      for(int i = 0; i < struct.external.size(); i++ ) {
         g = (Goal)struct.external.elementAt(i);
         if ( subgoals.contains(g.getId()) )
            struct.external.removeElementAt(i--);
      }
      dispose(parent,key);
      if ( mode == Planner.REPLAN ) {
         planner.clear_bind(struct.goals);
         for(int i = 0; i < struct.goals.size(); i++ ) {
            g = (Goal)struct.goals.elementAt(i);
            if ( subgoals.contains(g.getId()) )
               struct.goals.removeElementAt(i--);
         }
      }
      producedDb.softNotifyFailed(other_tasks,path,struct,mode);
   }


   public Vector descendantSubgoals() {
      Vector subgoals = consumedDb.currentSubgoals();
      PlanRecord[] children = consumedDb.getChildren();
      Vector sub;
      for(int i = 0; i < children.length; i++ ) {
         sub = children[i].descendantSubgoals();
         subgoals = Misc.union(subgoals,sub);
      }
      return subgoals;
   }


   public int latestConfirmTime() {
      int lct = start_time;

      PlanRecord[] children = consumedDb.getChildren();
      for(int i = 0; i < children.length; i++ )
         lct = Math.min(lct,children[i].latestConfirmTime());
      return lct;
   }


   public void dispose() {
      dispose(parent,key,goal.getSuppliedDb());
   }
   
   
   public void dispose(PlanRecord parent, String key) {
      dispose(parent,key,goal.getSuppliedDb());
   }

   protected void dispose(PlanRecord aParent, String aKey,
                          SuppliedDb db) {
      if ( is_disposed ) return;

      Core.DEBUG(3,"Dispose rec " + id + "-" + goal.getFactType() +
                   ":  " + state_string[state]);

      PlanRecord rec;
      PlanRecord[] children;
      switch(state) {
         case TEMP:
         case TENTATIVE:
         case FIRM:
         case RUNNING:
         case JEOPARDY:
/*
         NOTE: for sla enactments, when disposing a record which is
         FIRM,RUNNING,JEOPARDY  we need to return the consumed_db data back
         to the original
*/
              if ( producedDb.hasAtMostOneParent(aParent,aKey) ) {
                 Core.DEBUG(3,"rec " + id + " hasAtMostOneParent");
                 // we can safely dispose this record
                 planner.freeCells(proc,start_time,end_time);
                 if ( thread != null ) thread.abort();
                 thread = null;
                 children = consumedDb.getChildren();
                 for(int i = 0; i < children.length; i++ )
                    children[i].dispose(this,key);
                 consumedDb.releaseResources(db);
                 Core.DEBUG(3,"Calling planner.del: " + this);
                 planner.del(this);
                 is_disposed = true;
                 planner.notifyMonitors(this,Planner.DISPOSE);
              }
              break;

         case AGREEMENT:
              for(int i = 0; images != null && i < images.size(); i++ ) {
                 rec = (PlanRecord)images.elementAt(i);
                 rec.originalDisposed();
              }

         case FAILED:
         case COMPLETED:
              // we must dispose this record
              if ( thread != null ) thread.abort();
              thread = null;

              if ( goal.whichType() == Goal.CONTINUOUS ) {
                 for(int i = 0; i < slots.length; i++ )
                    if ( slots[i] != BOOKED )
                       planner.freeCell(proc,i+start_time);
              }
              else {
                 planner.freeCells(proc,start_time,end_time);
              }

              children = consumedDb.getChildren();
              for(int i = 0; i < children.length; i++ )
                 children[i].dispose(this,key);
              consumedDb.releaseResources(db);
              planner.del(this);
              is_disposed = true;
              planner.notifyMonitors(this,Planner.DISPOSE);
              break;

         default:
              Assert.notNull(null);
      }
   }


   public boolean hasAncestor(PlanRecord ancestor) {
      PlanRecord father = this;
      while( father != null ) {
         if ( father == ancestor )
            return true;
         father = father.getParent();
      }
      return false;
   }
   
   
   public PlanRecord getRoot() {
      PlanRecord root = this;
      while( root.getParent() != null )
         root = root.getParent();
      return root;
   }

   public void reallocateResource(int precond_position, int amount) {
      reallocateResource(precond_position,amount,null,null);
   }
   
   
   public void reallocateResource(int precond_position, PlanRecord child,
                                  int effect_position, int amount,
                                  Vector Tasks, Vector path) {
      consumedDb.remove(precond_position,child,effect_position,amount);
      reallocateResource(precond_position,amount,Tasks,path);
   }
   
   
   public void reallocateResource(int precond_position, int amount,
                                  Vector Tasks, Vector Path) {
      ResourceDb db = planner.getAgentContext().ResourceDb();
      Goal g;

      Core.DEBUG(3,"Attempting reallocation...");

      SuppliedDb given = this.goal.getSuppliedDb();
      if ( given != null )
         amount = given.allocateResource(this,precond_position,amount);

      if ( amount == 0 ) return;

      if ( (g = db.allocateResource(this,precond_position,amount)) == null )
         return;

      diagnostic = "Required resource unavailable - replanning";
      setState(JEOPARDY);

      double t = planner.getAgentContext().now() +
                 planner.getAgentContext().getReplanPeriod();
      g.setConfirmTime(new Time(t));

      Core.DEBUG(3,"Reallocation failed... internal replanning");

      if ( path == null )
         path = Misc.copyVector(this.path);

      PlannerQueryStruct struct = new PlannerQueryStruct(g);
      struct.internal.addElement(this);
      planner.schedule(key,this,path,g,Tasks,struct,Planner.EXPAND);

      if ( !struct.external.isEmpty() ) {
         Core.DEBUG(3,"Reallocation failed... external replanning");
         planner.index(struct);
         planner.getAgentContext().Engine().replan(struct,key);
      }
      else if ( !struct.internal.isEmpty() )
         reconfirm();
                                  }

   public void raiseException(int effect_position, String exception_key,
                              int amount) {

      // Assert.notFalse(exception_key.equals(key));
      // g must be the same as this.goal

      Goal g = new Goal(goal);

      g.setId(planner.getAgentContext().newId("subgoal"));
      g.setImage(goal.getId());

      Fact needed = task.getPostcondition(effect_position);
      needed.setNumber(amount);
      g.setFact(needed);
      g.setDesiredBy(planner.getAgentContext().whoami());
      double t = planner.getAgentContext().now() +
                 planner.getAgentContext().getReplanPeriod();
      g.setConfirmTime(new Time(t));

      Core.DEBUG(3,"Top goal... internal replanning");

      PlannerQueryStruct struct = new PlannerQueryStruct(g);
      planner.schedule(key,null,path,g,other_tasks,struct,Planner.EXPAND);
      if ( !struct.internal.isEmpty() ) {
         if ( !struct.external.isEmpty() ) {
            Core.DEBUG(3,"Top goal... external replanning");
            planner.index(struct);
            planner.getAgentContext().Engine().replan(struct,key);
         }
         else 
            planner.book(FIRM,g,struct.internal);
      }
      else {
         Vector goals = new Vector();
         goals.addElement(goal);
         DelegationStruct ds = new DelegationStruct(planner.getAgentContext().whoami(),
                                   "failure",exception_key,goals);
         planner.getAgentContext().Engine().add(ds);
      }
   }


   public void softReallocateResource(int precond_position, PlanRecord child,
                                      int effect_position, int amount,
                                      Vector Tasks, Vector path,
                                      PlannerQueryStruct struct, int mode) {
      consumedDb.remove(precond_position,child,effect_position,amount);
      softReallocateResource(precond_position,amount,Tasks,path,struct,mode);
   }
   
   
   public void softReallocateResource(int precond_position, int amount,
                                      Vector Tasks, Vector Path,
                                      PlannerQueryStruct struct, int mode) {

      ResourceDb db = planner.getAgentContext().ResourceDb();
      Goal g;

      if ( path == null )
         path = Misc.copyVector(this.path);

      Core.DEBUG(3,"Attempting soft reallocation...");

      SuppliedDb given = this.goal.getSuppliedDb();
      if ( given != null )
         amount = given.allocateResource(this,precond_position,amount);

      if ( amount == 0 ) return;

      if ( (g = db.allocateResource(this,precond_position,amount)) == null )
         return;

      diagnostic = "Required resource unavailable - replanning";
      if ( mode == Planner.REPLAN ) { 
         struct.goals.addElement(g);
         planner.index(struct);
         setState(JEOPARDY);
         double t = planner.getAgentContext().now() +
                    planner.getAgentContext().getReplanPeriod();
         g.setConfirmTime(new Time(t));
      }
      else {
        g.setConfirmTime(this.goal.getConfirmTime());
      }

      Core.DEBUG(3,"Soft reallocation failed... internal replanning");

      if ( !struct.internal.contains(this) )
         struct.internal.addElement(this);

      planner.schedule(key,this,path,g,Tasks,struct,Planner.EXPAND);
   }


   public void softRaiseException(int effect_position, String exception_key,
                                  int amount, PlannerQueryStruct struct,
                                  int mode) {

      // Assert.notFalse(exception_key.equals(key));
      // g must be the same as this.goal

      Fact needed = task.getPostcondition(effect_position);
      needed.setNumber(amount);
  
      Goal g;
      if ( mode == Planner.REPLAN ) {
         g = new Goal(goal);

         g.setId(planner.getAgentContext().newId("subgoal"));
         g.setImage(goal.getId());
         g.setDesiredBy(planner.getAgentContext().whoami());

         g.setFact(needed);
         double t = planner.getAgentContext().now() +
                    planner.getAgentContext().getReplanPeriod();
         g.setConfirmTime(new Time(t));
         g.setSuppliedDb(null);
      }
      else {
         g = goal;
      }

      Core.DEBUG(3,"Top goal... soft internal replanning");

      this.removeRecordTree(struct.internal);
      planner.schedule(key,null,path,g,other_tasks,struct,Planner.EXPAND);

      if ( struct.internal.isEmpty() && mode == Planner.REPLAN ) {
         Vector goals = new Vector();
         goals.addElement(goal);
         DelegationStruct ds = new DelegationStruct(
            planner.getAgentContext().whoami(),"failure",
            exception_key,goals);
         planner.getAgentContext().Engine().add(ds);
      }
   }
   

   public void removeRecordTree(Vector data) {
      data.removeElement(this);
      PlanRecord[] children = getChildren();
      for(int i = 0; i < children.length; i++ )
         children[i].removeRecordTree(data);
   }
   

   public ReportRec report() {
      String parent_id = parent != null ? parent.getGoal().getId() : null;

      Vector siblings = new Vector();
      if ( goal.getImage() != null )
         siblings.addElement(goal.getImage());

      Vector parents = producedDb.getAllParents();
      parents.removeElement(goal.getId());

      ReportRec report = new ReportRec(goal.getId(), goal.getFactType(),
         task.getName(), planner.getAgentContext().whoami(),
         state, goal.getDesiredBy(),
         goal.getRootId(), parent_id,
         start_time, end_time, cost,
         consumedDb.allSubgoals(), siblings, parents,
         task.getPreconditions(), task.getPostconditions()
      );
      return report;
   }
   

   public Goal createSubgoal(Fact fact, int precon_position) {
      String goalId = planner.getAgentContext().newId("subgoal");
      String myself = planner.getAgentContext().whoami();

      Goal g = new Goal(goal.whichType(),goalId,fact,myself);

      if ( goal.whichType() == Goal.CONTINUOUS ) {
         g.setStartTime(start_time);
         g.setEndTime(end_time - task.getTime());
         g.setInvocations(goal.getInvocations());
      }
      else {
         g.setEndTime(start_time);
      }
      g.setConfirmTime(goal.getConfirmTime());
      g.setPriority(goal.getPriority());
      g.setCost(0);
      g.setRootId(goal.getRootId());

      g.setSuppliedDb(this.goal.getSuppliedDb());

      consumedDb.add(precon_position, g.getId(), fact.getNumber());

      return g;
   }
      
      
   public boolean hasSubgoal(String goalId) {
      return consumedDb.currentSubgoals().contains(goalId);
   }
   
   
   public boolean hasSubgoal(Goal goal) {
      return consumedDb.currentSubgoals().contains(goal.getId());
   }


   public boolean equals(PlanRecord rec)  {
      return id.equals(rec.getId()) &&
             goal.equals(rec.getGoal()) &&
             task.equals(rec.getTask());
   }


   public PlanRecord enact(PlannerEnactStruct es, Goal goal_image,
                           PlanRecord parent_image, String key_image,
                           Hashtable substitution_table) {
      System.out.println("ENACTING...."); 
      Fact fact = goal_image.getFact();
      int required = fact.getNumber();
      int etime = goal_image.getEndTime();
      int duration = task.getTime();

      etime = (etime <= 0) ? end_time + etime : etime;
      goal_image.setEndTime(etime);

      Core.DEBUG(4,"required = " + required);
      Core.DEBUG(4,"noAllowedInvocations = " + noAllowedInvocations);
      Core.DEBUG(4,"noAvailableItems = " + noAvailableItems);
      Core.DEBUG(4,"etime = " + etime);
      Core.DEBUG(4,"end_time = " + end_time);
      Core.DEBUG(4,"start_time = " + start_time);
      Core.DEBUG(4,"duration = " + duration);

      es.ok = es.ok &&
              goal.whichType() == Goal.CONTINUOUS && // prior SLA
              goal_image.whichType() == Goal.DISCRETE  && // enactment of SLA
              etime <= end_time && // time check
              etime >= start_time + duration &&
              noAllowedInvocations > 0 && // within allowed no. of enactments
              required <= noAvailableItems; // within allowed no of items

      if ( !es.ok ) {
         Core.DEBUG(4,"Failed here");
         return null;
      }

      boolean found = false;
      int top = 0, bottom = 0;
      for(int i = etime-start_time-1; !found && i >= duration-1; i-- ) {
         found = true;
         top = i; bottom = top-duration+1;
         for(int j = top; j >= bottom; j-- )
            found = found && slots[j] != BOOKED;
      }
      if ( !found ) {
         es.ok = false;
         return null;
      }
       System.out.println("ENACTING"); 
      PrimitiveTask task_image = new PrimitiveTask(task);
      
      // maintain everything except no. of items being produced
      // and consumed -- this should be taken care of by unifying
      // with the goal_image and applyConstraints

      task_image.relaxNumberFields();

      Core.DEBUG(4,"TaskImage Before = \n" + task_image.pprint());
     
      Bindings b = new Bindings(planner.getAgentContext().whoami());
      Fact f2 = task_image.getActiveEffect();
      es.ok = es.ok && f2.unifiesWith(fact,b) &&
              b.add(es.bindings) &&
              task_image.applyConstraints(b);

      Core.DEBUG(4,"TaskImage After = \n" + task_image.pprint());

      if ( !es.ok )
         return null;


      PlanRecord image = new PlanRecord(planner, this, key_image, parent_image,
         goal_image, task_image, proc, start_time+bottom, start_time+top+1);

      images.addElement(image);
      image.setAlternativeTasks(Misc.copyVector(other_tasks)); // poor copy
      image.setPath(Misc.copyVector(path)); // poor copy
      for(int i = start_time+top; i > start_time+top-duration; i-- ) {
         Core.DEBUG(4,"Assigning cell[" + proc + "][" + i + "] to "+image);
         planner.assignCell(proc,i,image);
      }

      substitution_table.put(this,image);
      es.images.addElement(image);
      producedDb.share(image.getProducedDb(),
                       parent_image, key_image, parent, key);
      consumedDb.share(image.getConsumedDb());

      // Note: negative s_etime. Subgoal will use this as offset to compute
      // their required etime;
      int s_etime = etime - end_time; 

      Goal child_goal_image;
      PlanRecord child;
      PlanRecord child_image;
      Object any;
      String goal_id;
      Fact f1;

      Hashtable children = image.getConsumedDb().getAllChildren();
      Enumeration enum = children.keys();
      while( enum.hasMoreElements() ) {

         f1 = (Fact) enum.nextElement();
         any = children.get(f1);

         String goalId = planner.getAgentContext().newId("subgoal");
         String myself = planner.getAgentContext().whoami();

         child_goal_image = new Goal(Goal.DISCRETE,goalId,f1,myself);
         child_goal_image.setEndTime(s_etime);
         child_goal_image.setPriority(goal.getPriority());
         child_goal_image.setCost(0);
         child_goal_image.setRootId(goal_image.getRootId());

         if ( any instanceof String ) {
            goal_id = (String)any;
            image.getConsumedDb().update(child_goal_image.getId(),goal_id);
            es.external.addElement(child_goal_image);
            es.table.put(child_goal_image.getId(),goal_id);
         }
         else {
            child = (PlanRecord)any;
            child_image = (PlanRecord)substitution_table.get(child);
            if ( child_image == null )
               child_image = child.enact(es,child_goal_image,image,key_image,
                                         substitution_table);
            Assert.notNull(child_image);
            image.getConsumedDb().update(child_image,child);
         }
      }

      for(int j = top; j >= bottom; j-- )
         slots[j] = BOOKED;

      noAllowedInvocations--;
      noAvailableItems = noAvailableItems - required;
      return image;
   }

   public String toString() {
      return id + "/" + goal.getFactType() + "/" + goal.getFactId() + "/" + key;
   }

    public void debug (String str) { 
      //  System.out.println("PlanRecord>> " +str); 
    }

}
