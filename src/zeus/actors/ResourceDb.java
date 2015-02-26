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
 * @(#)ResourceDb.java 1.00
 */
package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.actors.event.*;


/**
 * This class implements the Resource Database component, whose role is to
 * store the resources owned by the agent, (which are {@link Fact} objects).
 * The Resource Database is simply an extended {@link Hashtable} with the fact
 * objects held indexed by their types. <p>
 *
 * Developers will probably interact the Resource Database on a regular basis,
 * as it both contains the agent's knowledge and provides a means of altering it. 
 * A reference to the Resource Database can be obtained from the agent's 
 * {@link AgentContext} object, allowing access to the methods described below; 
 * (The methods without descriptions are primarily used by other agent components
 * and so are unlikely to be useful to developers).
 
 <p> <i> Change log </i> <p> 
 19/08/00 - added some code to the any(Fact) method at about line 580 - 590
            this is to stop an unnecessary exception being thrown, the new 
            behavior is that if the method is called and there are no facts of that type
            in the resourcedb then a new fact of that type will be returned instead. 
 <P> 
 Change log 
 ----------
 13/06/01 added a number of fields (name,planner,ontologyDb, gensym,externaldb) which are 
 used to store references that would be obtained from the AgentContext object, but 
 cannot be if the context is not available at initialisation.
 I hope that this will enable better extensibility, but it might cause problems if the agent 
 architecture is altered at run time. 
 Added isSet method to allow programmers to check to see if 
 Added setPlanner to permit this reference to be updated easily 
 Added setName 
 Added setOntologyDb
 Added setGenSym 
 */

public class ResourceDb extends Hashtable
{
   private HSet[] eventMonitor = new HSet[4];

   private static final int ADD    = 0;
   private static final int MODIFY = 1;
   private static final int DELETE = 2;
   private static final int ACCESS = 3;

   protected AgentContext context = null;
   protected String name = null;
   protected Planner planner = null; 
   protected OntologyDb ontologyDb = null; 
   protected GenSym gensym = null; 
   

   public ResourceDb () {
     super(); 
      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();
   }

   public ResourceDb(AgentContext context) {
      super(); 
      this.context = context;
      this.name = context.whoami(); 
      this.gensym = context.GenSym(); 
      this.planner = context.getPlanner(); 
      this.ontologyDb = context.getOntologyDb(); 
      context.set(this);

      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();
   }


    /** 
       * this allows you to set the planner reference 
       */
    public void setPlanner(Planner planner) { 
        this.planner = planner; 
    }
    
    
    /** 
        this allows you to set the name reference
        */
    public void setName (String name) { 
        this.name = name; 
    }
    
    
    /** 
        this allows you to set the gensym referencce
        */
    public void setGenSym(GenSym gensym) { 
        this.gensym = gensym; 
    }
    
    
    /**
       this allows you to set the ontolgyDb reference
       */
    public void setOntologyDb(OntologyDb ontologyDb) { 
        this.ontologyDb = ontologyDb; 
    }
    
    /** 
        check that the name refernce is set
        */
    public boolean isNameSet() { 
        if (name == null) 
            return false;
            else 
                return true;
    }
    
    /** 
        check that the gensym refernece is set
        */
    public boolean isGenSymSet() { 
        if (gensym == null) 
            return false; 
            else
               return true;
    }
    

    /** 
        check that the planner reference is set 
        */
    public boolean isPlannerSet() { 
        if (planner == null) 
            return false; 
            else 
               return true; 
         }
    
        
    /** 
        check that the ontologyDb reference is set
        */
    public boolean isOntologyDbSet (){ 
        if (ontologyDb == null) 
            return false; 
            else
                return true; 
    }
    
    
    /** 
        check that the AgentContext context field is set. 
        Note: this is not necessary for the rdb to function, but 
        is included so that 
        */
    public boolean isContextSet() { 
        if (context == null) 
            return false; 
            else 
                return true; 
    }   
    
    
    /** 
        check to see if all necessary fields are set 
        */ 
    public boolean isSet () { 
        if (isContextSet ()) return true; 
        else {
         if (!isGenSymSet () ) {
                System.out.println("gensym not set"); 
                return false; 
         }
         if (!isPlannerSet () ) {
            System.out.println("planner not set"); 
            return false; 
            }
         if (!isOntologyDbSet () ) {
            System.out.println("ontologydb not set"); 
            return false; 
         }
         if (!isNameSet () ) {
             System.out.println("name not set"); 
             return false; 
         }
                
        }
         return true; 
    }
        
        
     
        

   /** Use this to obtain handles to the other internal components of agent */
   public AgentContext getAgentContext() { return context; }

    
   /** 
    was synchronized
    */ 
   public synchronized ResourceItem add(Vector List) {
      for(int i = 0; i < List.size(); i++ )
         add( (Fact)List.elementAt(i) );
      return null;
   }
   
   
   /** 
    was synchronized
    */
   public synchronized ResourceItem add(Fact[] List) {
      for(int i = 0; i < List.length; i++ )
         add( List[i] );
      return null;
   }

   /** The primary method for adding new facts to the Resource Database 
       was synchronized
   */
   public synchronized ResourceItem add(Fact fact) {
      String type = fact.getType();
      ResourceItem item;
      Vector List;
      PrimitiveNumericFn numericFn;

      if ( (List = (Vector)this.get(type)) == null ) {
         List = new Vector();
         this.put(type,List);
      }

      if ( type.equals(OntologyDb.MONEY) && !List.isEmpty() ) {
         for(int i = 0; i < List.size(); i++ ) {
            item = (ResourceItem) List.elementAt(i);
            notifyMonitors(item,ACCESS);
            if ( !item.isReserved() ) {
               Fact f1 = item.getFact();
               f1.resolve(new Bindings(name));
               numericFn = (PrimitiveNumericFn)f1.getFn(OntologyDb.AMOUNT);
               double db_amount = numericFn.doubleValue();

               fact.resolve(new Bindings(name));
               numericFn = (PrimitiveNumericFn)fact.getFn(OntologyDb.AMOUNT);
               double input = numericFn.doubleValue();

               double total = db_amount + input;
               if ( total == 0 ) {
                  // remove item
                  List.removeElementAt(i--);
                  item.deleted();
                  notifyMonitors(item,DELETE);
                  if ( List.isEmpty() ) this.remove(type);
                  return null;
               }
               else {
                  List.removeElementAt(i--);
                  item.deleted();
                  notifyMonitors(item,DELETE);
                  f1.setValue(OntologyDb.AMOUNT,total);
                  f1.resolve(new Bindings(name));
                  List.addElement(item);
                  notifyMonitors(item,ADD);
                  return item;
               }
            }
         }
      }
      item = new ResourceItem(fact);
      List.addElement(item);
      notifyMonitors(item,ADD);
      return item;
   }

    /** 
        was synchronized
        */
   synchronized void  replaceOrAdd(Fact fact) {
      ExternalDb externalDb = context.ExternalDb();
      if ( externalDb != null && externalDb.put(fact) ) {
         // ResourceItem item = new ResourceItem(fact);
         // notifyMonitors(item,ADD);
      }
      else
         add(fact);
   }

   public synchronized void del(Vector List) {
      for(int i = 0; i < List.size(); i++ )
         del((Fact)List.elementAt(i));
   }

   public synchronized void del(Fact[] List) {
      for(int i = 0; i < List.length; i++ )
         del(List[i]);
   }


   /** The primary method for permanently removing facts from the Resource Database */
   public synchronized void del(Fact fact) {
      String type = fact.getType();
      Vector List;

      if ( (List = (Vector)this.get(type)) == null )
         return;

      ResourceItem item;
      Fact f1;
      for(int i = 0; i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         if ( f1.equals(fact) ) {
            List.removeElementAt(i--);
            item.deleted();
            notifyMonitors(item,DELETE);
         }
      }
      if ( List.isEmpty() ) this.remove(type);
   }

   /** Use this if facts have changed and you need to update the Resource Database */
   public synchronized void modify(Fact f1, Fact f2) {
      del(f1);
      add(f2);
   }

    // synchronized 
   public void free(DataRec rec) {
      if ( rec == null ) return;
      Vector List = rec.available();
      ResourceItem item;
      ExternalDb externalDb = context.ExternalDb();

      Fact f1;
      for(int i = 0; i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         item.cancelReservation(rec);
         f1 = item.getFact();
         if ( externalDb != null && !item.isReserved() &&
              externalDb.put(f1) ) {
            List.removeElementAt(i--);
            item.deleted();
            notifyMonitors(item,DELETE);
         }
      }
      rec.free();
   }

   public void consume(DataRec rec) {
      if ( rec == null ) return;
      Vector List = rec.available();
      Vector data;
      Fact f1;
      for(int i = 0; i < List.size(); i++ ) {
         ResourceItem item = (ResourceItem) List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         notifyMonitors(item,DELETE);
         data = (Vector)this.get(f1.getType());
         switch( item.consumed(rec) ) {
            case ResourceItem.UNCHANGED:
	         notifyMonitors(item,ADD); // replace unchanged
                 break;
            case ResourceItem.MODIFY:
	         notifyMonitors(item,ADD); // replace changed
                 break;
	          case ResourceItem.DELETE: // no replacement
                 data.removeElement(item);
                 if ( data.isEmpty() )
                 this.remove(item.getFact().getType());
                 break;
         }
      }
   }

   public int findAll(PlanRecord rec, int precond_position,
                                   int required) {
      Core.ERROR(required > 0,1001,this);
      DataRec datarec = rec.getDatarec(precond_position);
      int start = rec.getStartTime();
      return reserve(datarec,rec,start,required);
   }

   /** Enables a resource to be secured at a certain time period, (this is
       provided primarily for internal use) */
   public int reserve(DataRec datarec, PlanRecord rec,
                                   int start, int required) {
      Vector List;
      ResourceItem item;
      Fact f1, f2, f3;
      ExternalDb externalDb = context.ExternalDb();

      Fact fact = datarec.getFact();
      boolean consumed = !fact.isReadOnly();

      if ( (List = (Vector)this.get(fact.getType())) == null )
         List = new Vector();

      int amt;
      Bindings b = new Bindings(name);
      for(int i = 0; required > 0 && i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         int available;
         Core.DEBUG(2,"Fact before test \n" + f1); 
         b.clear();
         if ( (available = item.unreservedAmount(start,consumed)) > 0 &&
              f1.unifiesWith(fact,b) && rec.applyConstraints(b) ) {
            amt = Math.min(available,required);
            Core.ERROR(datarec.add(item,start,amt),1002,this);
            if ( consumed )
               rec.updateCost(amt*f1.getUnitCost());
            required -= amt;

            Core.DEBUG(2,"Required to find Fact:\n" + fact);
            Core.DEBUG(2,"Fact:\n" + f1 + "\nassigned to datarec " +
                         datarec.getId());
         }
      }

      if ( required > 0 && externalDb != null ) {
         Enumeration enum = externalDb.all(fact);
         while( enum != null && enum.hasMoreElements() && required > 0 ) {
            f1 = (Fact)enum.nextElement();
            b.clear();
            if ( f1.unifiesWith(fact,b) && rec.applyConstraints(b) ) {
               int available = f1.getNumber();
               f3 = externalDb.remove(f1);
               if ( f3 != null ) {
                  item = add(f3);
                  amt = Math.min(available,required);
                  Core.ERROR(datarec.add(item,start,amt),1003,this);
                  if ( consumed )
                     rec.updateCost(amt*f3.getUnitCost());
                  required -= amt;
   
                  Core.DEBUG(2,"Required to find Fact:\n" + fact);
                  Core.DEBUG(2,"Fact:\n" + f3 + "\nassigned to datarec " +
                            datarec.getId());
               }
               else {
                  Core.USER_ERROR("Improperly defined externalDb: fact \"" +
                     f1 + "\" should be in externaldb, but not found");
               }
            }
         }
      }
      return required;
   }

   public synchronized int reserve(DataRec datarec, int start, int required) {
      Vector List;
      ResourceItem item;
      Fact f1, f2;
      ExternalDb externalDb = context.ExternalDb();

      Fact fact = datarec.getFact();
      boolean consumed = !fact.isReadOnly();

      if ( (List = (Vector)this.get(fact.getType())) == null )
         List = new Vector();

      int amt;
      for(int i = 0; required > 0 && i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         Bindings b = new Bindings(name);
         int available;
         if ( (available = item.unreservedAmount(start,consumed)) > 0 &&
              f1.unifiesWith(fact,b) ) {
            amt = Math.min(available,required);
            Core.ERROR(datarec.add(item,start,amt),1005,this);
            required -= amt;

            Core.DEBUG(2,"Required to find Fact:\n" + fact);
            Core.DEBUG(2,"Fact:\n" + f1 + "\nassigned to datarec " +
                         datarec.getId());
         }
      }

      if ( required > 0 && externalDb != null ) {
         Enumeration enum = externalDb.all(fact);
         while( enum.hasMoreElements() && required > 0 ) {
            f1 = (Fact)enum.nextElement();
            Bindings b = new Bindings(name);
            if ( f1.unifiesWith(fact,b) ) {
               int available = f1.getNumber();
               f1 = externalDb.remove(f1);
               item = add(f1);
               amt = Math.min(available,required);
               Core.ERROR(datarec.add(item,start,amt),1006,this);
               required -= amt;

               Core.DEBUG(2,"Required to find Fact:\n" + fact);
               Core.DEBUG(2,"Fact:\n" + f1 + "\nassigned to datarec " +
                         datarec.getId());
            }
         }
      }
      return required;
   }

    // was sychronized - but caused deadlock!
   public Vector allocateResources(PlanRecord rec) {
     // zeus.actors.rtn.Engine engine = context.getEngine(); 
     // synchronized (engine) { 
      Vector subgoals = new Vector();
      Goal g;
      int position, required;

      PrimitiveTask task = rec.getTask();
      Fact[][] consumed = task.orderPreconditions();

      for(int i = 0; i < consumed.length; i++) {
         for(int j = 0; j < consumed[i].length; j++) {
            if ( !consumed[i][j].isNegative() ) {
               position = task.getConsumedPos(consumed[i][j]);
               required = rec.noRequiredItems(position);
               if ( required > 0) {
                  g = allocateResource(rec,position,required);
                  if ( g != null ) subgoals.addElement(g);
               }
            }
         }
         if ( !subgoals.isEmpty() ) break;
      }
      return subgoals;
    //  }
   }


    // synchronized
   public Goal allocateResource(PlanRecord rec, int position,
                                             int required) {
      int s;
      required = findAll(rec,position,required);

      if ( required > 0 ) {
        // since 1.3 - check that the planner is extant.
         if (!isPlannerSet())  
            if (!isContextSet()) { 
                    try {
                        throw new Exception ("Planner and AgentContext not set in ResourceDb.\n Agent is improperly initialised, sorry"); 
                        } 
                        catch (Exception e) { 
                                e.printStackTrace(); 
                            } }
                else { 
                    this.planner = context.getPlanner(); } 
                    // ends addition
         Planner planner = this.planner;
         debug("planner = " + planner); 
         // check planner for seredipituous side effects
         DataRec datarec = rec.getDatarec(position);
         Fact desc = datarec.getFact();
         if ( (s = planner.anySideEffect(desc,rec,position,required)) != 0 ) {
            Fact g = new Fact(desc);
            g.setNumber(s);
            return rec.createSubgoal(g,position);
         }
      }
      return null;
   }

   /** Special purpose method to decrease the value of the amount attribute of MONEY facts */
   public synchronized Fact debit(double amount) {
      Vector List;
      ResourceItem item;
      Fact f1, f2, debit;
      PrimitiveNumericFn numericFn;

      Core.DEBUG(3,"About to debit: " + amount);
      Core.ERROR(amount>=0,1006,this);

      OntologyDb ontology = ontologyDb;
      debit = ontology.getFact(Fact.FACT,OntologyDb.MONEY);
      debit.setValue(OntologyDb.AMOUNT,amount);

      List = (Vector)this.get(OntologyDb.MONEY);
      for(int i = 0; List != null && amount > 0 && i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         notifyMonitors(item,ACCESS);
         if ( !item.isReserved() ) {
            f1 = item.getFact();
            f1.resolve(new Bindings(name));
            numericFn = (PrimitiveNumericFn)f1.getFn(OntologyDb.AMOUNT);
            double available = numericFn.doubleValue();
            if ( available > amount ) {
               f2 = f1.duplicate(Fact.VAR,gensym);
               f2.setValue(OntologyDb.AMOUNT,available-amount);
               f2.resolve(new Bindings(name));

               // delete f1 from db
               List.removeElementAt(i--);
               item.deleted();
               notifyMonitors(item,DELETE);
               f1.setValue(OntologyDb.AMOUNT,amount);

               ResourceItem new_item = new ResourceItem(f2);
               List.addElement(new_item);
               notifyMonitors(new_item,ADD);
               Core.DEBUG(3,"Debit completed: " + debit);
               return debit;
            }
            else if ( available >= 0 ) {
               // delete f1 from db
               List.removeElementAt(i--);
	       item.deleted();
               notifyMonitors(item,DELETE);
               amount = amount - available;
            }
         }
      }

      // if amount > 0 then add -ve amount to db and return
      if ( amount > 0 ) {
         boolean done = false;
         for(int i = 0; List != null && !done && i < List.size(); i++ ) {
            item = (ResourceItem)List.elementAt(i);
            notifyMonitors(item,ACCESS);
            if ( !item.isReserved() ) {
               f1 = item.getFact();
               f1.resolve(new Bindings(name));
               numericFn = (PrimitiveNumericFn)f1.getFn(OntologyDb.AMOUNT);
               double available = numericFn.doubleValue();

               // delete f1 from db
               List.removeElementAt(i--);
               item.deleted();
               notifyMonitors(item,DELETE);
               f1.setValue(OntologyDb.AMOUNT,available-amount);
               f1.resolve(new Bindings(name));
               List.addElement(item);
               notifyMonitors(item,ADD);
               done = true;
            }
         }

         if ( !done ) {
            f1 = ontology.getFact(Fact.FACT,OntologyDb.MONEY);
            f1.setValue(OntologyDb.AMOUNT,0 - amount);
            f1.resolve(new Bindings(name));
            add(f1);
         }
      }
      Core.DEBUG(3,"Debit completed: " + debit);
      return debit;
   }

   public synchronized Fact evalLocal(Fact fact) {
      Fact[] answer = all(fact);
      if ( answer.length == 0 ) return null;
      OntologyDb ontology = ontologyDb;
      Fact result = new Fact(answer[0]);
      for( int i = 1; i < answer.length; i++ )
         Core.ERROR(result.disjoin(answer[i]),1008,this);
      return result;
   }

   public synchronized boolean evalNegative(Fact fact) {
      Fact[] answer = all(fact);
      return ( answer.length == 0 );
   }

   public synchronized Fact[] all(String type) {
      OntologyDb ontology = ontologyDb;
      Fact f = ontology.getFact(Fact.VARIABLE,type);
      return all(f);
   }

   /** Use this to retrieve all the facts in the database that match the parameter */
   public synchronized Fact[] all(Fact fact) {
      Vector List;
      ResourceItem item;
      Fact f1;
      ExternalDb externalDb = context.ExternalDb();

      if ( (List = (Vector)this.get(fact.getType())) == null )
         List = new Vector();

      Vector answer = new Vector();
      Bindings b = new Bindings(name);
      for(int i = 0; i < List.size(); i++, b.clear() ) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         if ( f1.unifiesWith(fact,b) )
            answer.addElement(f1);
      }
      if ( externalDb != null ) {
         Enumeration enum = externalDb.all(fact);
         while( enum.hasMoreElements() )
            answer.addElement((Fact)enum.nextElement());
      }

      Fact[] results = new Fact[answer.size()];
      for(int i = 0; i < answer.size(); i++ )
         results[i] = (Fact) answer.elementAt(i);
      return results;
   }

   /** Deletes all facts matching the parameter type, should obviously
       be used with caution */
   public synchronized void deleteAll(String type) {
      Vector List;
      ResourceItem item;
      Fact f1;

      if ( (List = (Vector)this.get(type)) == null )
         return;

      // delete all unallocated first then delete the allocated
      for(int i = 0; i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         if ( !item.isReserved() ) {
            List.removeElementAt(i--);
            item.deleted();
            notifyMonitors(item,DELETE);
         }
      }
      for(int i = 0; i < List.size(); i++ ) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         List.removeElementAt(i--);
         item.deleted();
         notifyMonitors(item,DELETE);
      }
   }

   /** Randomly retrieves a fact with the same type as the parameter */
   public synchronized Fact any(String type) {
      OntologyDb ontology = ontologyDb;
      Fact f = ontology.getFact(Fact.VARIABLE,type);
      return any(f);
   }

   /** Use this to randomly retrieve a fact that matches the parameter */
   public synchronized Fact any(Fact fact) {
      Fact[] answer = all(fact);
      if ( answer == null ) return null;
      int pos = (int) (Math.random()*answer.length);
      // added by simon to stop an exception being thrown. 
      //19/8/00
      if (pos>answer.length-1) {
        // in this case the array is out of bounds
        return fact; }
        // end addition
      else 
            return answer[pos];
   }

   /** Use this to test whether a particular fact exists in the database */
   public synchronized boolean contains(Fact fact, int start) {
      Vector List;
      ResourceItem item;
      Fact f1;
      ExternalDb externalDb = context.ExternalDb();

      if ( (List = (Vector)this.get(fact.getType())) == null )
         List = new Vector();

      Bindings b = new Bindings(name);
      int required = fact.getNumber();
      boolean consumed = !fact.isReadOnly();
      int available;
      for(int i = 0; required > 0 && i < List.size(); i++, b.clear()) {
         item = (ResourceItem)List.elementAt(i);
         f1 = item.getFact();
         notifyMonitors(item,ACCESS);
         if ( (available = item.unreservedAmount(start,consumed)) > 0 &&
              f1.unifiesWith(fact,b) ) {
            required = required - Math.min(available,required);
         }
      }

      if ( required > 0 && externalDb != null ) {
         Enumeration enum = externalDb.all(fact);
         while( enum.hasMoreElements() && required > 0 ) {
            f1 = (Fact)enum.nextElement();
            available = f1.getNumber();
            required = required - Math.min(available,required);
         }
      }

      return required <= 0;
   }


   /**
    * Use this if your code needs to react to changes in the Resource Database.
    * This provides a programatic alternative to writing reaction rules
    */

   public void addFactMonitor(FactMonitor monitor, long event_type,
                              boolean notify_previous)  {
      addFactMonitor(monitor,event_type);
      if ( !notify_previous ) return;

      Enumeration enum = elements();
      Vector List;
      ResourceItem item;
      FactEvent event;

      while( enum.hasMoreElements() ) {
         List = (Vector)enum.nextElement();
         for(int i = 0; i < List.size(); i++ ) {
            item = (ResourceItem) List.elementAt(i);
            event = new FactEvent(this,item,FactEvent.ACCESS_MASK);
            monitor.factAccessedEvent(event);
            event = new FactEvent(this,item,FactEvent.ADD_MASK);
            monitor.factAddedEvent(event);
         }
      }
   }

   public void addFactMonitor(FactMonitor monitor, long event_type) {
      if ( (event_type & FactEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].add(monitor);
      if ( (event_type & FactEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].add(monitor);
      if ( (event_type & FactEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].add(monitor);
      if ( (event_type & FactEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].add(monitor);
   }

   public void removeFactMonitor(FactMonitor monitor, long event_type) {
      if ( (event_type & FactEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].remove(monitor);
      if ( (event_type & FactEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].remove(monitor);
      if ( (event_type & FactEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].remove(monitor);
      if ( (event_type & FactEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].remove(monitor);
   }

   private void notifyMonitors(ResourceItem item, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      FactMonitor monitor;
      FactEvent event;
      Enumeration enum = eventMonitor[type].elements();

      switch(type) {
         case ADD:
              event = new FactEvent(this,item,FactEvent.ADD_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (FactMonitor)enum.nextElement();
                 monitor.factAddedEvent(event);
              }
              break;
         case MODIFY:
              event = new FactEvent(this,item,FactEvent.MODIFY_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (FactMonitor)enum.nextElement();
                 monitor.factModifiedEvent(event);
              }
              break;
         case DELETE:
              event = new FactEvent(this,item,FactEvent.DELETE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (FactMonitor)enum.nextElement();
                 monitor.factDeletedEvent(event);
              }
              break;
         case ACCESS:
              event = new FactEvent(this,item,FactEvent.ACCESS_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (FactMonitor)enum.nextElement();
                 monitor.factAccessedEvent(event);
              }
              break;
      }
   }
   
   private void debug(String str) { 
  //  System.out.println(str); 
   }

}
