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
 * @(#)ConsumedDb.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.*;

/**
 * The Consumed Resources Database is an internal storage component used by
 * the {@link Planner} in the course of its activities. It is unlikely
 * that developers will need to call these methods directly.
 */

public class ConsumedDb 
{
   protected Vector[]   data = null;
   protected int[]      consumed = null;
   protected DataRec[]  records = null;
   protected PlanRecord owner = null;

   protected boolean resources_consumed = false;
   protected boolean resources_released = false;
   protected Vector  all_subgoals = new Vector();

   // meaningless init  to allow rearch
   public ConsumedDb () {
    ;
    }

   public ConsumedDb(PlanRecord owner, PrimitiveTask task) {
      consumed = task.numPreconditions();
      Assert.notNull(owner);
      this.owner = owner;
      data = new Vector[consumed.length];
      records = new DataRec[consumed.length];

      for(int i = 0; i < records.length; i++)
         records[i] = new DataRec(task.getPrecondition(i),owner,i);
   }
   
   
   public DataRec getDatarec(int precond_position) {
      Core.DEBUG(2,"Getting datarec for " + owner + " at position " +
                   precond_position);
      return records[precond_position];
   }
   
   
   public int amountUsed(int precond_position) {
      return consumed[precond_position];
   }


   public synchronized void add(int precond_position, PlanRecord child,
                                int effect_position, int amount) {
      PreconditionChain ch;
      Assert.notFalse(requiredItems(precond_position) >= amount);
      if ( data[precond_position] == null)
           data[precond_position] = new Vector();

      ch = new PreconditionChain(child,effect_position,amount);
      data[precond_position].addElement(ch);
   }
   
   
   public synchronized void add(int precond_position, String goal_id,
                                int amount) {

      Assert.notFalse(requiredItems(precond_position) >= amount);
      if ( data[precond_position] == null)
           data[precond_position] = new Vector();

      PreconditionChain ch = new PreconditionChain(goal_id,amount);
      data[precond_position].addElement(ch);
   }


   public synchronized void add(int precond_position, PreconditionChain ch) {
      Assert.notFalse(requiredItems(precond_position) >= ch.amount);
      if ( data[precond_position] == null)
           data[precond_position] = new Vector();
      data[precond_position].addElement(ch);
   }


   public int requiredItems(int position) {
      PreconditionChain ch;
      if ( records[position] == null ) return consumed[position];
      int available = records[position].nAvailable();
      if ( data[position] != null ) {
         for(int i = 0; i < data[position].size(); i++ ) {
            ch = (PreconditionChain)data[position].elementAt(i);
            available += ch.amount;
         }
      }
      return consumed[position] - available;
   }
   
   
   public boolean hasEnoughResources() {
      boolean has_enough = true;
      ResourceDb db = owner.getAgentContext().ResourceDb();
      int now = (int)owner.getAgentContext().now();
      for(int i = 0; has_enough && i < records.length; i++ )
         has_enough &= records[i].nAvailable() == consumed[i] &&
                       records[i].executeNow(db,now);
      return has_enough;
   }
   
   
   public void newStartTime(int start) {
      for(int i = 0; i < records.length; i++ )
         records[i].newStartTime(start);
   }
   
   
   public void consumeResources() {
      ResourceDb db = owner.getAgentContext().ResourceDb();
      for(int i = 0; i < records.length; i++ )
         db.consume(records[i]);
      resources_consumed = true;
   }
   
   
   public int getPosition(String goal_id) {
      PreconditionChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( ch.isExternal() && goal_id.equals(ch.key) )
               return i;
         }
      }
      Assert.notNull(null); // sh. never get here
      return -1;
   }
   
    // sync
   public void factExists(int precond_position, PlanRecord child,
                                       int effect_position, int amount) {
      ResourceDb db = owner.getAgentContext().ResourceDb();
      Vector List = data[precond_position];
      for(int j = 0; j < List.size(); j++ ) {
         PreconditionChain ch = (PreconditionChain)List.elementAt(j);
         if ( ch.record == child && ch.position == effect_position &&
              ch.amount == amount ) {
            List.removeElementAt(j--);
            Goal g = db.allocateResource(owner,precond_position,amount);
//            Assert.notFalse( g == null ); // FOR NOW
            if ( g != null ) {
               System.err.println("Resource Taken by someone?:\n" + g);
               try {
                   throw new Exception (); 
               }
               catch (Exception e) { 
                e.printStackTrace(); }
            }
            Core.DEBUG(2,"Calling Constrain: " + records[precond_position]);
            constrain(records[precond_position]); //PROBLEM? 
            return;
         }
      }
      Assert.notNull(null); // sh. never get here
   }
   
   // sync
   public void factExists(String goal_id) {
      ResourceDb db = owner.getAgentContext().ResourceDb();
      int precond_position = getPosition(goal_id);
      Vector List = data[precond_position];
      for(int j = 0; j < List.size(); j++ ) {
         PreconditionChain ch = (PreconditionChain)List.elementAt(j);
         if ( ch.isExternal() && ch.key.equals(goal_id) ) {
            List.removeElementAt(j--);
            Goal g = db.allocateResource(owner,precond_position,ch.amount);
//            Assert.notFalse( g == null ); // FOR NOW
            if ( g != null ) {
               System.err.println("Resource Taken by someone?:\n" + g);
            }
            constrain(records[precond_position]);
            Core.DEBUG(2,"Calling Constrain: " + records[precond_position]);
            return;
         }
      }
      Assert.notNull(null); // sh. never get here
   }
   
   
   public synchronized void replace(String goal_id, PlanRecord child,
                                    int effect_position, int amount) {

      int precond_position = getPosition(goal_id);
      Vector List = data[precond_position];
      for(int j = 0; j < List.size(); j++ ) {
         PreconditionChain ch = (PreconditionChain)List.elementAt(j);
         if (ch.isExternal() && ch.key.equals(goal_id) ) {
            if ( ch.amount == amount )
               List.removeElementAt(j--);
            else
               ch.amount -= amount;
            ch = new PreconditionChain(child,effect_position,amount);
            List.addElement(ch);
            return;
         }
      }
      Assert.notNull(null); // sh. never get here
   }


   protected boolean constrain(DataRec datarec) {
      Core.DEBUG(2,"Constrain: " + datarec);
      Fact f1 = datarec.mostGeneralDescriptor();
      Core.DEBUG(2,"Constrain: mgd " + f1);
      Bindings b = new Bindings(owner.getAgentContext().whoami());
      Fact f2 = datarec.getFact();
      Core.DEBUG(2,"Constrain: getFact " + f2);
      Assert.notFalse( f1.unifiesWith(f2,b) );
      return  owner.applyConstraints(b);
   }
   
   
   
   public Fact[][] getInputData() {
      Fact[][] data = new Fact[consumed.length][];
      for( int i = 0; i < data.length; i++ ) 
         data[i] = records[i].getData();
      return data;
   }
   
   
   public PlanRecord getOwner() { return owner; }
   
   
   public synchronized void share(ConsumedDb db) {
      DataRec datarec;
      int required;
      PreconditionChain ch;
      for(int i = 0; i < consumed.length; i++) {
         required = db.requiredItems(i);
         datarec = records[i].subtract(db.getOwner(),i,required);
         Assert.notNull(datarec);
         required = required - datarec.nAvailable();
         Core.DEBUG(2,"Share consumedb of " + owner + " at " + i +
                               " require " + required);
         for(int j = 0; required > 0 && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( ch.amount <= required ) {
               // remove ch from data[i]
               data[i].removeElementAt(j--);
               required = required - ch.amount;
               // now add ch to db at position i
            }
            else {
               ch.amount = ch.amount - required;
               // create new ch for db at position i
               ch = new PreconditionChain( ch );
               ch.amount = required;
               required = 0;
               // now add ch to db at position i
            }
            // now adding ch to db at position i
            db.add(i,ch);
         }
      }
   }
   
   
   public synchronized Hashtable getAllChildren() {
    System.out.println("getAllChildren is called"); 
      Hashtable output = new Hashtable();
      PreconditionChain ch;
      Fact f1;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            f1 = new Fact(records[i].getFact());
            f1.setNumber(ch.amount);
            if ( ch.isExternal() ) 
               output.put(f1,ch.key);
            else
               output.put(f1,ch.record);
         }
      }
      return output;
   }
   
   
   public synchronized Vector currentSubgoals() {
      Vector output = new Vector();
      PreconditionChain ch;
      StringTokenizer st;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( ch.isExternal() ) {
               st = new StringTokenizer(ch.key,"/");
               output.addElement(st.nextToken());
            }
            else
               output.addElement(ch.record.getGoal().getId());
         }
      }
      return output;
   }
   
   
   public synchronized Vector allSubgoals() {
      Vector output = currentSubgoals();
      all_subgoals = Misc.union(all_subgoals,output);
      return all_subgoals;
   }
   
   
   public synchronized PlanRecord[] getChildren() {
      Vector output = new Vector();
      PreconditionChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( !ch.isExternal() && !output.contains(ch.record) )
               output.addElement(ch.record);
         }
      }
      PlanRecord[] result = new PlanRecord[output.size()];
      for(int i = 0; i < result.length; i++)
         result[i] = (PlanRecord)output.elementAt(i);
      output = null;
      return result;
   }
   
   
   public synchronized boolean update(PlanRecord image, PlanRecord record) {
      PreconditionChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( !ch.isExternal() && ch.record == record ) {
               ch.record = image;
               return true;
            }
         }
      }
      return false;
   }
   
   
   public synchronized boolean update(String image, String key) {
      PreconditionChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( ch.isExternal() && ch.key.equals(key) ) {
               ch.key = image;
               return true;
            }
         }
      }
      return false;
   }
   
   
   public synchronized void releaseResources(SuppliedDb given) {
      if ( resources_consumed ) return;
      if ( resources_released ) return;

      ResourceDb db = owner.getAgentContext().ResourceDb();
      for(int i = 0; i < records.length; i++ )
         db.free(records[i]);

      PreconditionChain ch;
      Engine engine = owner.getAgentContext().Engine();
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (PreconditionChain)data[i].elementAt(j);
            if ( ch.isExternal() ) {
               if ( given == null || !given.cancelReservation(ch.key) )
                  engine.getAuditTable().cancel(ch.key);
            }
            else
               ch.record.breakEffectChain(ch.position,owner,i,ch.amount);
         }
      }

      resources_released = true;
   }
   
   
   public synchronized Fact remove(String goal_id) {
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            PreconditionChain ch = (PreconditionChain)data[i].elementAt(j);
            if ( ch.isExternal() && goal_id.equals(ch.key) ) {
               data[i].removeElementAt(j--);
               Fact f1 = new Fact(records[i].getFact());
               f1.setNumber(ch.amount);
               return f1;
            }
         }
      }
      Assert.notNull(null); // sh. never get here
      return null;
   }
   
   
   public synchronized Fact remove(int precond_position, PlanRecord child,
                                   int effect_position, int amount) {
      PreconditionChain ch;
      for(int j = 0; j < data[precond_position].size(); j++ ) {
         ch = (PreconditionChain)data[precond_position].elementAt(j);
         if ( ch.record == child && ch.position == effect_position
              && ch.amount == amount ) {
            data[precond_position].removeElementAt(j--);
            Fact f1 = new Fact(records[precond_position].getFact());
            f1.setNumber(ch.amount);
            return f1;
         }
      }
      Assert.notNull(null);
      return null;
   }
   
   
   public String toString() {
      String out = "ConsumedDb(" + "\n" + owner + "\n";
      for(int i = 0; i < data.length; i++ ) {
         if ( data[i] != null )
            out += "data[" + i + "]: " + data[i] + "\n";
      }
      out += ")";
      return out;
   }
}
