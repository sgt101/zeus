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
 * @(#)TaskDb.java 1.00
 * Change log
 * ----------
 * 12-06-01 Added agentName and genSym to facilitate intialisation from extended types. 
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.actors.event.*;

/**
 * The Task Database is a simple storage component that holds the {@link Task}
 * descriptions known by the owning agent.  This information is used by
 * the agent's {@link Planner} component. <p>
 *
 * The methods of this component are only likely to be of interest to
 * developers who write code to monitor or change the abilities of agents.
 * Change log
 * ----------
 * 12-06-01 Added agentName and orgDb to facilitate intialisation from extended types. 
 * 
 */

public class TaskDb extends Hashtable {
  protected HSet[] eventMonitor = new HSet[4];

  public static final int MODIFY = 0;
  public static final int ADD    = 1;
  public static final int DELETE = 2;
  public static final int ACCESS = 3;
  public static final int NONE   = 4;

  protected static Random rand = new Random(System.currentTimeMillis());
  protected static int CMIN = 10;
  protected static int CMAX = 50;
  protected boolean demo_version = false;
  
  
  protected String agentName = null; 
  protected OrganisationDb orgDb = null; 

  protected AgentContext context = null;
  
  protected GenSym gensym = null; 



  public TaskDb () {
    for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();
    String version = SystemProps.getProperty("demo.version");
    demo_version = (Boolean.valueOf(version)).booleanValue();
        }
  

  public TaskDb(AgentContext context) {
     Assert.notNull(context);
     this.context = context;
     this.agentName = context.whoami(); 
     this.orgDb = context.getOrganisationDb(); 
     this.gensym = context.getGenSym(); 
     context.set(this);
     for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();
     String version = SystemProps.getProperty("demo.version");
     demo_version = (Boolean.valueOf(version)).booleanValue();
  }

    /** 
        returns a reference to the agent context pointer
        */
  public AgentContext getAgentContext() {
     return context;
  }

  
  /** 
    The primary method for adding new task abilities to the Task Database */
  public void add(Task task) {
    Assert.notNull(task);
    debug ("LOADED TASK NAME " + task.getName()); 
    
    Fact[] produced = task.getPostconditions();
    Fact[] consumed = task.getPreconditions(); // 1.2.1 Simon 
    resolvePreconditions (task, consumed, produced); // 1.2.1 Simon 
    resolveCostAndTime (task,consumed,produced);// 1.2.1 Simon
    int operation = addItem(produced,task);
    if ( operation != NONE )
       notifyMonitors(task,operation);
  }
  
  
  /** 
    resolveCostAndTime is used to check that the cost and timing of the task in terms of 
    the preconditions and postconditions of the task is properly resolved
    . Works like this: get the cost out, check to see if it matches any of the consumed 
    (postconditions), then check to see if it matches any of the produced (preconditions)
    If it does, copy the values for evaluation. 
    * ISSUES
    *--------
    *My main concern is that I have really foobared, and this used to be done somewhere else, but I have 
    *broken it : suggestions to me please!
    *@author Simon Thompson
    *@since 1.2.1 
  */
  public void resolveCostAndTime (Task task, Fact[] consumed, Fact[] produced) { 
    ValueFunction time = task.getTimeFn(); 
    ValueFunction cost = task.getCostFn(); 
    
    debug("time = " + time.getClass().toString());
    debug ("cost = " + cost.getClass().toString()); 
     
    if (time instanceof ArithmeticFn){
        resolveall((ArithmeticFn)time,task,consumed);
        resolveall((ArithmeticFn)time,task,produced); 
    }
    if (cost instanceof ArithmeticFn) { 
        resolveall((ArithmeticFn)cost,task,consumed); 
        resolveall((ArithmeticFn)cost,task,produced); 
    }
    
  }
  
  
  public void resolveall (ArithmeticFn target, Task task, Fact [] resolvers) { 
    debug("in resolveall"); 
    for (int i = 0; i<resolvers.length; i++) { 
      Fact currentFact = resolvers[i];
      debug ("doing " + currentFact.toString()); 
      AttributeList attrs = currentFact.getAttributeList(); 
      String [] attrNames = attrs.getNames(); 
      for (int attrCount = 0; attrCount < attrNames.length; attrCount++) { 
       target.resolve(currentFact.ID()+"." + attrNames[attrCount],currentFact.getFn(attrNames[attrCount])); 
       debug (target.toString()); 
      }
        
        
    }
  }
  
  
  /** 
    resolvePreconditions is used to check through all the precondition attributes in the task as
    they are loaded to see if there are any references to postcondition attributes, and then 
    resolve those references so that when task chaining and execution occur the 
    correct values will be used
    *@since 1.2.1 
    *@author Simon Thompson
    */
    public void resolvePreconditions(Task task, Fact[] consumed, Fact[] produced) { 
        for (int count = 0; count<consumed.length; count++) {
            Fact currentFact = consumed[count];
            AttributeList attrs = currentFact.getAttributeList(); 
            String [] attrName = attrs.getNames(); 
            for (int attrCount = 0; attrCount < attrName.length; attrCount++) {
             debug("before checknmap" + attrs.toString()); 
             checkAndMapValue(attrName[attrCount],attrs,produced);
             debug ("after checknmap " + attrs.toString()); 
            }
            
        }
        if (task instanceof zeus.concepts.PrimitiveTask) { 
            ((PrimitiveTask) task).setPreconditions(consumed); 
            ((PrimitiveTask) task).setPostconditions(produced); }
       
    }
  
  
  /** 
    checkAndMapValue takes a value from an attribute and checks to see if it 
    is a reference to a post condition. If it is, then it will map the value of the 
    post condition reference to the precondition: if we have a precondition x.name = ?y.name 
    where the post condition is ?y and ?y.name is ?var123 then we should map the precondition 
    to : 
        x.name = ?var123
    if this is made so, all will be well, and good, good, will have triumphed. 
    
    @since 1.2.1
    @author Simon Thompson
        */
  public void checkAndMapValue (String attrName, AttributeList attrs, Fact [] produced) { 
    String value = attrs.getValue(attrName); 
    if (value.startsWith ("?var")) return; 
    for (int count = 0; count< produced.length; count ++) { 
     Fact currentPost = produced[count];
     String currentPostName = currentPost.getId(); 
     debug (currentPostName + " - " + value);
     for (int i = 0; i < value.length() - currentPostName.length();i++) { 
        if (value.regionMatches (i, currentPostName, 0, currentPostName.length())) {
            debug ("match"); 
            String postAttr = value.substring(i+currentPostName.length()+1, value.length());
            postAttr = postAttr.replace(')',' '); // get rid of trailing brackets
            postAttr = postAttr.trim(); 
            String attrExp = value.substring (0, i); 
            String postVal = currentPost.getValue(postAttr); 
            ValueFunction postFn = currentPost.getFn(postAttr); 
            debug ("postVal = " + postVal); 
            if (postVal != null) {
                ValueFunction currentVf = attrs.getFn(attrName); 
                if (currentVf instanceof VarFn) { 
                    ((VarFn) currentVf).arg = postVal;
                    attrs.remove (attrName); 
                    attrs.setValue(attrName,currentVf); 
                    }
                else if (currentVf instanceof ConstraintFn) { 
                    ConstraintFn consVf= (ConstraintFn) currentVf; 
                    consVf.arg = postFn;
                    int operator = consVf.getOperator(); 
                    String operand = ConstraintFn.operators[operator]; 
                    ConstraintFn newFn = new ConstraintFn(operand,consVf.arg); 
                    debug (newFn.toString()); 
                    attrs.remove (attrName); 
                    attrs.setValue(attrName,newFn); 
                }// end elseif
                else if (currentVf instanceof ArithmeticFn) { 
                    ArithmeticFn arithVf = (ArithmeticFn) currentVf; 
                    arithVf.resolve(postVal,postFn); 
                    ValueFunction lhs = arithVf.getLHS(); 
                    debug ("LHS = " + lhs.toString() + " class " + lhs.getClass()); 
                    ValueFunction rhs = arithVf.getRHS(); 
                     debug ("RHS = " + rhs.toString() + " class " + rhs.getClass()); 
                    int operator = arithVf.getOperator(); 
                    String operand = ArithmeticFn.operators[operator]; 
                    ArithmeticFn newFn = new ArithmeticFn(lhs,rhs,operand);
                    attrs.remove(attrName);
                    attrs.setValue(attrName,newFn); 
                    
                    
                }
                else { 
                    debug ("NOT recognised >>>" + currentVf.getClass().toString()); 
                }
                
                debug ("attrs in checknmap " + attrs.toString()); 
                } // end if
        }//end if
     }// end for
     }// end for
  }
     
  
  public void add(Vector List) {
    for(int i = 0; List != null && i < List.size(); i++ )
      add((Task)List.elementAt(i));
  }
  
  
  public void add(Task[] List) {
    for(int i = 0; List != null && i < List.length; i++ )
      add(List[i]);
  }

  protected int addItem(Vector List, Task task) {
    int op = NONE;
    for(int i = 0; i < List.size(); i++ )
      op = Math.min(op, addItem((Fact)List.elementAt(i),task));
    return op;
  }
  protected int addItem(Fact[] List, Task task) {
    int op = NONE;
    for(int i = 0; i < List.length; i++ )
      op = Math.min(op, addItem(List[i],task));
    return op;
  }

  
  protected int addItem(Fact fact, Task task) {
    int op = NONE;
    if ( fact.isLocal() ) return op;

    String type = fact.getType();
    Hashtable obj;

    if ( (obj = (Hashtable)this.get(type)) == null ) {
       obj = new Hashtable();
       this.put(type,obj);
    }
    op =  obj.put(task.getName(),task) != null ? MODIFY : ADD;

    if ( orgDb != null ) {
       AbilitySpec a = new AbilitySpec(task.getName(),fact,0,0);
       OrganisationDb org = orgDb;
       org.add(agentName,a);
    }
    return op;
  }

  /** The primary method for deleting task abilities from the Task Database */
  public void del(Task task) {
    Assert.notNull(task);
    Fact[] produced = task.getPostconditions();
    int operation = deleteItem(produced,task);
    if ( operation != NONE )
       notifyMonitors(task,operation);
  }
  
  public void del(Vector List) {
    for(int i = 0; List != null && i < List.size(); i++ )
      del((Task)List.elementAt(i));
  }
  
  
  public void del(Task[] List) {
    for(int i = 0; List != null && i < List.length; i++ )
      del(List[i]);
  }

  protected int deleteItem(Vector List, Task task) {
    int op = NONE;
    for(int i = 0; i < List.size(); i++ )
      op = Math.min(op, deleteItem((Fact)List.elementAt(i),task));
    return op;
  }
  
  
  protected int deleteItem(Fact[] List, Task task) {
    int op = NONE;
    for( int i = 0; i < List.length; i++ )
      op = Math.min(op, deleteItem(List[i],task));
    return op;
  }
  
  
  protected int deleteItem(Fact fact, Task task) {
    int op = NONE;

    String type = fact.getType();
    Hashtable List;
    Task t;

    if ( (List = (Hashtable)this.get(type)) == null ) return op;

    op = List.remove(task.getName()) != null ? DELETE : NONE;

    if ( List.isEmpty() ) this.remove(type);

    if ( orgDb != null ) {
        // might need to add the name param here..
      AbilitySpec a = new AbilitySpec(task.getName(),fact,0,0);
      OrganisationDb org = orgDb;
      org.del(agentName,a);
    }
    return op;
  }

  /** Returns a duplicate of the task object with the given name */
  public Task getTask(String name) {
    Task task;
    Hashtable data;
    Enumeration enum = this.elements();
    while( enum.hasMoreElements() ) {
       data = (Hashtable) enum.nextElement();
       task = (Task)data.get(name);
       notifyMonitors(task,ACCESS);
       if ( task != null ) {
         Task retTask = (Task) task.duplicate (Fact.VAR, gensym); 
         debug("Duplicate = " + retTask.toString()); 
         debug("Original = " + task.toString());
        // return (Task) task.duplicate(Fact.VAR,gensym);}
         return (task);}
    }
    return null;
  }

  /** Deletes the task object with the given name */
  public void deleteTask(String name) {
    Task task;
    Hashtable data;
    Enumeration enum = this.elements();
    while( enum.hasMoreElements() ) {
       data = (Hashtable) enum.nextElement();
       task = (Task)data.remove(name);
       if ( task != null )
          notifyMonitors(task,DELETE);
    }
  }

  /** Randomly chooses a task able to produces a fact matching the parameter */
  public Task findOne(Fact fact) {
    Task task = null;
    Vector reduced = findAll(fact);
    if ( !reduced.isEmpty() ) {
       int pos = (int) (Math.random()*reduced.size());
       task = (Task)reduced.elementAt(pos);
       notifyMonitors(task,ACCESS);
    }
    reduced = null; // gc
    return task;
  }

  /** Retrieves the tasks that produce a fact matching the parameter */
  public Vector findAll(Fact fact) {
    Hashtable obj;

    if ( (obj = (Hashtable)this.get(fact.getType())) == null )
       return new Vector();

    return this.reduce(obj,fact);
  }

  public Vector findAll(Fact fact, Vector path) {
     Core.DEBUG(3,"TaskDb findAll(2)");
     Core.DEBUG(3,"tFact = " + fact);
     Core.DEBUG(3,"tPath = " + path);

     Vector prior = findAll(fact);
     return checkPath(prior,path);
  }

  /** Retrieves the tasks that produce and consume the same facts as the parameters */
  public Vector findAll(Fact[] consumed, Fact[] produced, Vector path) {
     Core.DEBUG(3,"TaskDb findAll(3)");

     Assert.notFalse(produced.length > 0);

     Core.DEBUG(3,consumed);
     Core.DEBUG(3,produced);
     Core.DEBUG(3,path);

     int index = -1;
     for(int i = 0; index == -1 && i < produced.length; i++ )
        if ( !produced[i].isSideEffect() ) index = i;

     Assert.notFalse(index != -1);
     Vector prior = findAll(produced[index]);
     Vector List = new Vector();
     Fact[] preconds, effects;
     Task task;
     for(int i = 0; i < prior.size(); i++ ) {
        task = (Task)prior.elementAt(i);
        preconds = task.getPreconditions();
        effects = task.getPostconditions();
        if ( hasCondition(consumed,preconds) && hasCondition(produced,effects) )
           List.addElement(task);
     }
     return checkPath(List,path);
  }

  protected boolean hasCondition(Fact[] test, Fact[] data) {
     boolean state = true;
     for(int i = 0; state && i < test.length; i++ )
        state &= hasCondition(test[i],data);
     return state;
  }

  protected boolean hasCondition(Fact test, Fact[] data) {
     Bindings b = new Bindings(agentName);
     for(int i = 0; i < data.length; i++, b.clear() )
        if ( data[i].unifiesWith(test,b) ) return true;
     return false;
  }

  protected Vector checkPath(Vector prior, Vector path) {
     Core.DEBUG(3,"Checking path...");
     Core.DEBUG(3,"tprior = " + prior);

     if ( prior.isEmpty() ) return prior;

     Vector posterior = new Vector();
     Bindings b = new Bindings(agentName);
     Task task;
     boolean ok;
     Fact f1;
     Fact[] consumed;
     for(int i = 0; i < prior.size(); i++ ) {
        task = (Task)prior.elementAt(i);
        notifyMonitors(task,ACCESS);
        ok = true;
        consumed = task.getPreconditions();
        for(int j = 0; ok && j < consumed.length; j++ ) {
           for(int k = 0; ok && k < path.size(); k++, b.clear()) {
              f1 = (Fact)path.elementAt(k);
              ok = ok && !consumed[j].unifiesWith(f1,b);
           }
        }
        if ( ok ) posterior.addElement(task);
     }
     prior = null; // gc

     Core.DEBUG(3,"posterior = " + posterior);

     return posterior;
  }

  protected Vector reduce(Hashtable List, Fact fact) {
    Vector ReducedList = new Vector();
    Task task, t;
    Bindings b = new Bindings(agentName);

    Enumeration enum = List.elements();
    while( enum.hasMoreElements() ) {
       task = (Task)enum.nextElement();
       notifyMonitors(task,ACCESS);
       t = (Task) task.duplicate(Fact.VAR,gensym);
       Fact[] produced = t.getPostconditions();
       Core.DEBUG(3,"TASK_IS\n" + t.pprint());
       for(int j = 0; j < produced.length; j++, b.clear() ) {

          Core.DEBUG(3,"TaskDb reduce(): unifying\n\trequired =\n" +
             fact.pprint() + "\n\tproduced =\n" + produced[j].pprint());

          if ( produced[j].unifiesWith(fact,b) && t.applyConstraints(b) ) {
             Core.DEBUG(3,"After apply Constraints:\n" + t);
             Core.DEBUG(3,"Bindings is\n" + b);

             if ( t.isPrimitive() )
                ((PrimitiveTask)t).setActiveEffect(j);
             ReducedList.addElement(t);

             // begin: demonstration version only
             if ( demo_version && t.isPrimitive() )
                t.setCostFn(Integer.toString(CMIN + Math.abs(rand.nextInt()%(CMAX-CMIN))));
             // end: demonstration version only

             break;
          }
       }
    }
    sort(ReducedList);
    return ReducedList;
  }

  /** Sorts a vector of tasks according to their time and cost functions */
  protected void sort(Vector List) {
     Vector primitive = new Vector();
     Task t;
     for(int i = 0; i < List.size(); i++ ) {
        t = (Task)List.elementAt(i);
        notifyMonitors(t,ACCESS);
        if ( t.isPrimitive() ) {
           primitive.addElement(t);
           List.removeElementAt(i--);
        }
     }
     sort_basic(List);
     sort_basic(primitive);
     for(int i = 0; i < primitive.size(); i++ )
        List.addElement(primitive.elementAt(i));
     primitive = null; // GC
  }

  protected void sort_basic(Vector List) {
     boolean changed = true;
     Task t1, t2;

     while( changed ) {
        changed = false;
        for( int i = 0; i < List.size()-1; i++ ) {
           t1 = (Task)List.elementAt(i);
           notifyMonitors(t1,ACCESS);
           t2 = (Task)List.elementAt(i+1);
           notifyMonitors(t2,ACCESS);
           if ( (t1.getCost() > t2.getCost()) ||
                (t1.getCost() == t2.getCost() && t1.getTime() > t2.getTime()) ) {
              List.setElementAt(t2,i);
              List.setElementAt(t1,i+1);
              changed = true;
           }
        }
     }
  }


  /**
    * Use this if your code needs to react to changes in the Task Database
    */
  public void addTaskMonitor(TaskMonitor monitor, long event_type,
                              boolean notify_previous) {
      addTaskMonitor(monitor,event_type);
      if ( !notify_previous ) return;

      Enumeration enum1 = elements();
      Task task;
      Hashtable data;
      TaskEvent event;

      while( enum1.hasMoreElements() ) {
         data = (Hashtable) enum1.nextElement();
         Enumeration enum2 = data.elements();
         while( enum2.hasMoreElements() ) {
            task = (Task)enum2.nextElement();
            event = new TaskEvent(this,task,TaskEvent.ACCESS_MASK);
            monitor.taskAccessedEvent(event);
            event = new TaskEvent(this,task,TaskEvent.ADD_MASK);
            monitor.taskAddedEvent(event);
         }
      }
   }

   public void addTaskMonitor(TaskMonitor monitor, long event_type) {
      if ( (event_type & TaskEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].add(monitor);
      if ( (event_type & TaskEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].add(monitor);
      if ( (event_type & TaskEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].add(monitor);
      if ( (event_type & TaskEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].add(monitor);
   }

   public void removeTaskMonitor(TaskMonitor monitor, long event_type) {
      if ( (event_type & TaskEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].remove(monitor);
      if ( (event_type & TaskEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].remove(monitor);
      if ( (event_type & TaskEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].remove(monitor);
      if ( (event_type & TaskEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].remove(monitor);
   }

   protected void notifyMonitors(Task task, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      Enumeration enum = eventMonitor[type].elements();
      TaskMonitor monitor;
      TaskEvent event;
      switch(type) {
         case ADD:
              event = new TaskEvent(this,task,TaskEvent.ADD_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (TaskMonitor)enum.nextElement();
                 monitor.taskAddedEvent(event);
              }
              break;
         case MODIFY:
              event = new TaskEvent(this,task,TaskEvent.MODIFY_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (TaskMonitor)enum.nextElement();
                 monitor.taskModifiedEvent(event);
              }
              break;
         case DELETE:
              event = new TaskEvent(this,task,TaskEvent.DELETE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (TaskMonitor)enum.nextElement();
                 monitor.taskDeletedEvent(event);
              }
              break;
         case ACCESS:
              event = new TaskEvent(this,task,TaskEvent.ACCESS_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (TaskMonitor)enum.nextElement();
                 monitor.taskAccessedEvent(event);
              }
              break;
      }
   }
   
   private void debug (String out) { 
    //  System.out.println("taskDb>> " + out); 
   }
   
}
