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
 * @(#)ProducedDb.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.Engine;
import zeus.actors.rtn.util.DelegationStruct;


/**
 * The Produced Resources Database is an internal storage component used by
 * the {@link Planner} in the course of its activities. It is unlikely
 * that developers will need to call these methods directly.
 */

public class ProducedDb
{
   protected Vector[] data = null;
   protected int[] produced = null;
   protected PlanRecord owner = null;


   public ProducedDb () {
   ;
   }
   

   public ProducedDb(PlanRecord owner, PrimitiveTask task) {
      Assert.notNull(owner);
      produced = task.numPostconditions();
      this.owner = owner;
      data = new Vector[produced.length];
   }
   
   
   public synchronized void add(int effect_position, PlanRecord parent,
                                int amount, String goal_id, boolean consumed) {
      Assert.notFalse(effect_position >= 0 && effect_position < data.length);

      if ( data[effect_position] == null)
           data[effect_position] = new Vector();

      EffectChain ch;
      int start;
      if ( parent != null ) {
         int precond_position = parent.getConsumedPosition(goal_id);
         start = parent.getStartTime();
         Assert.notFalse(availableItems(effect_position,start,consumed) >=
                         amount);
         ch = new EffectChain(parent,precond_position,amount,start,consumed);
         parent.replacePrecondition(goal_id,owner,effect_position,amount);
      }
      else {
         start = owner.getEndTime();
         Assert.notFalse(availableItems(effect_position,start,consumed) >= 
                         amount);
         ch = new EffectChain(goal_id,amount,start,consumed);
      }
      data[effect_position].addElement(ch);
   }
   
   
   public synchronized void add(int effect_position, EffectChain ch) {
      Assert.notFalse(effect_position >= 0 && effect_position < data.length);
      Assert.notFalse(availableItems(effect_position,ch.start,ch.consumed) >=
                      ch.amount);

      if ( data[effect_position] == null)
           data[effect_position] = new Vector();
      data[effect_position].addElement(ch);
   }
   
   
   public int noItemsProduced(int position) {
      return produced[position];
   }
   
   
   protected synchronized int availableItems(int position, int start,
                                             boolean consumed) {
      if ( data[position] == null ) return produced[position];
      EffectChain ch;
      int amount = 0;
      for(int i = 0; i < data[position].size(); i++ ) {
         ch = (EffectChain)data[position].elementAt(i);
         if ( consumed ) {
            if ( ch.consumed || ch.start > start ) 
               amount += ch.amount;
         }
         else {
            if ( ch.consumed && ch.start <= start ) 
               amount += ch.amount;
         }
      }
      return produced[position] - amount;
   }
   
   
   public synchronized int anySideEffect(int effect_position, PlanRecord parent,
                                         Object precond, int required) {
      int precond_position = -1;
      String goal_id = null;
      boolean is_string = false;
      if ( precond instanceof String ) {
         goal_id = (String)precond;
         precond_position = parent.getConsumedPosition(goal_id);
         is_string = true;
      }
      else { // must be an int
         precond_position = ((Integer)precond).intValue();
      }
          
      int start = parent.getStartTime();
      boolean consumed = parent.isPreconditionConsumed(precond_position);
      int available = availableItems(effect_position,start,consumed);
      if ( available == 0 )
         return required;

      Core.DEBUG(2,"ProducedDb checking for serendipitous effects");

      if ( data[effect_position] == null )
         data[effect_position] = new Vector();

      EffectChain ch;
      int amount = (available >= required) ? required : available;
      ch = new EffectChain(parent,precond_position,amount,start,consumed);
      data[effect_position].addElement(ch);
      if ( is_string )
        parent.replacePrecondition(goal_id,owner,effect_position,amount);
      else
        parent.chainPrecondition(owner,effect_position,amount,precond_position);

      return required - amount;
   }


   public synchronized void hardChain(int effect_position, int required,
                                      PlanRecord parent, int precond_position) {

      Core.DEBUG(2,"ProducedDb hardChain effects ...1");
      Core.DEBUG(2,owner.toString() + "::" + effect_position);
      Core.DEBUG(2," ==");
      Core.DEBUG(2,parent.toString() + "::" + precond_position);

      int start = parent.getStartTime();
      boolean consumed = parent.isPreconditionConsumed(precond_position);
      int available = availableItems(effect_position,start,consumed);

      if ( available == 0 ) {
         System.err.println("ProducedDb hardChain effects: available == 0");
         return;
      }

      if ( data[effect_position] == null )
         data[effect_position] = new Vector();

      EffectChain ch;
      int amount = (available >= required) ? required : available;
      ch = new EffectChain(parent,precond_position,amount,start,consumed);
      data[effect_position].addElement(ch);
      parent.chainPrecondition(owner,effect_position,amount,precond_position);
   }
   
   
   public synchronized void hardChain(int effect_position, String key,
                                      int amount, int start, boolean consumed) {

      Core.DEBUG(2,"ProducedDb hardChain effects ...2");
      Core.DEBUG(2,"effect_position: " + effect_position);
      Core.DEBUG(2,"key: " + key);
      Core.DEBUG(2,"amount: " + amount);
      Core.DEBUG(2,"start: " + start);
      Core.DEBUG(2,"consumed: " + consumed);

      Assert.notFalse(effect_position >= 0 && effect_position < data.length);

      if ( data[effect_position] == null )
           data[effect_position] = new Vector();

      Core.DEBUG(2,"Current chains...");
      Core.DEBUG(2,data[effect_position]);

      EffectChain ch;
      Assert.notFalse(availableItems(effect_position,start,consumed)>=amount);
      ch = new EffectChain(key,amount,start,consumed);
      data[effect_position].addElement(ch);
   }
   
   
   public void allocatePostconditions(Fact[][] fact) {
    debug (fact.toString()); 
      for(int i = 0; i < fact.length; i++ ) {
        debug (fact[i].toString()); 
         allocatePostcondition(i,fact[i]);}
   }
   
   
   protected void allocatePostcondition(int position, Fact[] input) {
      // REM: for now assume only one item per condition
      Fact fact = input[0];

      int available;
      debug ("in producedDB " + String.valueOf(position)); 
      debug ("in producedDB "  + fact.toString()); 
      if ( (available = fact.getNumber()) == 0 ) {
         Core.USER_ERROR("Warning: integer expected in .fact.no field." +
         "\nEnsure \'" + OntologyDb.NUMBER + "\' constraints are defined " +
         "in all task specifications");
         return;
      }
      if ( available < produced[position] ) {
         Core.USER_ERROR("\nWarning: fewer items produced: " + available +
                         " than expected " + produced[position]);
         Core.USER_ERROR(fact.pprint());
      }

      ResourceDb db = owner.getAgentContext().ResourceDb();
      if ( data[position] == null ) {
         db.replaceOrAdd(fact);
         return;
      }

      Fact f1;
      Engine engine = owner.getAgentContext().Engine();
      DelegationStruct ds;
      EffectChain ch;

      for(int i = 0; available > 0 && i < data[position].size(); i++) {
         ch = (EffectChain)data[position].elementAt(i);
         if ( ch.isExternal() && available >= ch.amount ) {
            if ( ch.consumed ) available = available - ch.amount;
            f1 = new Fact(Fact.FACT, fact);
            f1.setNumber(ch.amount);
            ds = new DelegationStruct(owner.getAgentContext().whoami(),
                                      "result", ch.key, f1);
            debug (ds.toString());                           
            engine.add(ds);
         }
      }
      if ( available > 0 ) {
         fact.setNumber(available);
         db.replaceOrAdd(fact);
      }
      for(int i = 0; i < data[position].size(); i++) {
         ch = (EffectChain)data[position].elementAt(i);
         if ( !ch.isExternal() ) {
            f1 = new Fact(Fact.VARIABLE, fact);
            f1.setNumber(ch.amount);
            ch.record.preconditionExists(owner,position,ch.amount,ch.position);
         }
      }
   }
   
   /// i think that this may be responsible for ye oldy precondition unification. 
   public boolean constrain(Bindings bindings) {
      boolean status = true;
      Vector List;
      EffectChain ch;
      for(int i = 0; status && i < data.length; i++ ) {
         List = data[i];
         for(int j = 0; status && List != null && j < List.size(); j++ ) {
            ch = (EffectChain)List.elementAt(j);
            if ( !ch.isExternal() )
            // &= !!! i ask you..... bitwise and assignment - if you 
            // find a true ever then make status true...
               status &= ch.record.applyConstraints(bindings);
         }
      }
      return status;
   }


   public PlanRecord getOwner() { return owner; }



   public synchronized void share(ProducedDb db,
      PlanRecord parent_image, String key_image, 
      PlanRecord parent, String key) {

      int required;
      EffectChain ch;
      for(int i = 0; i < produced.length; i++) {
         required = db.noItemsProduced(i);
         for(int j = 0; required > 0 && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( ch.amount <= required ) {
               // remove ch from data[i]
               data[i].removeElementAt(j--);
               required = required - ch.amount;
               // now add ch to db at position i
            }
            else {
               ch.amount = ch.amount - required;
               // create new ch for db at position i
               ch = new EffectChain(ch);
               ch.amount = required;
               required = 0;
               // now add ch to db at position i
            }

            if ( ch.isExternal() ) {
               if ( ch.key.equals(key) )
                  ch.key = key_image;
            }
            else {
               if ( ch.record == parent ) 
                  ch.record = parent_image;
            }
            // now adding ch to db at position i
            db.add(i,ch);
         } 
      }
   }
   
   
   public synchronized boolean update(PlanRecord image, PlanRecord record) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( !ch.isExternal() && ch.record == record ) {
               ch.record = image;
               return true;
            }
         }
      }
      return false;
   }
   
   
   public synchronized boolean update(String image, String key) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( ch.isExternal() && ch.key.equals(key) ) {
               ch.key = image;
               return true;
            }
         }
      }
      return false;
   }
   
   
   public synchronized boolean replaceOrAdd(String key, String newKey,
                                            int start, int amount,
                                            boolean consumed) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( ch.isExternal() && ch.key.equals(key) ) {
               Core.DEBUG(2,ch);
               if ( ch.start == start && ch.amount == amount &&
                    ch.consumed == consumed ) {
                  // replacement valid
                  ch.key = newKey;
                  return true;
               }
/*
   Special case:
*/
               else if ( ch.start <= start && ch.amount == amount &&
                    ch.consumed == true && consumed == true ) {
                  // replacement valid
                  ch.key = newKey;
                  ch.start = start;
                  return true;
               }
               else {
                  ch = new EffectChain(newKey,amount,start,consumed);
                  add(i,ch);
                  return true;
               }
            }
         }
      }
      ch = new EffectChain(newKey,amount,start,consumed);
      add(owner.getTask().getActiveEffectPos(),ch);
      return true;
   }
   
   
   public synchronized boolean hasAtMostOneParent(PlanRecord parent, String key) {
      Vector records = new Vector();
      Vector keys = new Vector();
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( ch.isExternal() ) {
               if ( !keys.contains(ch.key) )
                  keys.addElement(ch.key);
            }
            else {
               if ( !records.contains(ch.record) )
                  records.addElement(ch.record);
            }
         }
      }
      Core.DEBUG(3,"ProducedDb hasAtMostOneParent: parent = " + parent + " key = " + key);
      Core.DEBUG(3,"ProducedDb hasAtMostOneParent: records = " + records);
      Core.DEBUG(3,"ProducedDb hasAtMostOneParent: keys = " + keys);

      if ( parent != null ) {
         return keys.isEmpty() && records.size() == 1 &&
                records.contains(parent);
      }
      else {
         return records.isEmpty() && keys.size() == 1 && 
	        (keys.contains(key) || keys.contains(owner.getGoal().getId()));
      }
   }
   
   
   public synchronized void remove(int effect_position,
                                   PlanRecord parent,
                                   int precond_position, int amount) {
      EffectChain ch;
      for(int j = 0; j < data[effect_position].size(); j++ ) {
         ch = (EffectChain)data[effect_position].elementAt(j);
         if ( ch.record == parent && ch.position == precond_position &&
              ch.amount == amount ) {
            data[effect_position].removeElementAt(j--);
            return;
         }
      }
      Assert.notNull(null);
   }
   
   
   public synchronized boolean references(PlanRecord parent) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( !ch.isExternal() && ch.record == parent )
               return true;
         }
      }
      return false;
   }
   
   
   public synchronized PlanRecord firstParent() {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( !ch.isExternal() )
               return ch.record;
         }
      }
      return null;
   }
   
   
   public synchronized String firstKey() {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( ch.isExternal() )
               return ch.key;
         }
      }
      return null;
   }
   public synchronized int firstPosition(PlanRecord parent) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( !ch.isExternal() && ch.record == parent )
               return i;
         }
      }
      Assert.notNull(null);
      return -1;
   }
   
   
 
   public synchronized Vector getAllParents() {
      Vector output = new Vector();
      EffectChain ch;
      StringTokenizer st;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( !ch.isExternal() )
               output.addElement(ch.record.getGoal().getId());
            else {
               st = new StringTokenizer(ch.key,"/");
               output.addElement(st.nextToken());
            }
         }
      }
      return output;
   }
   
   
   public synchronized void notifyFailed(Vector Tasks, Vector path) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            data[i].removeElementAt(j--);
            if ( ch.isExternal() )
               owner.raiseException(i,ch.key,ch.amount);
            else {
               if ( ch.record == owner.getParent() )
                  ch.record.reallocateResource(ch.position,owner,i,
                                               ch.amount,Tasks,path);
               else 
                  ch.record.reallocateResource(ch.position,owner,i,
                                               ch.amount,null,null);
            }
         }
      }
   }
   
   
   public synchronized void softNotifyFailed(Vector Tasks, Vector path,
                                             PlannerQueryStruct struct,
                                             int mode) {
      EffectChain ch;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            data[i].removeElementAt(j--);
            if ( ch.isExternal() )
               owner.softRaiseException(i,ch.key,ch.amount,struct,mode);
            else {
               if ( ch.record == owner.getParent() )
                  ch.record.softReallocateResource(ch.position,owner,i,
                     ch.amount,Tasks,path,struct,mode);
               else 
                  ch.record.softReallocateResource(ch.position,owner,i,
                     ch.amount,null,null,struct,mode);
            }
         }
      }
   }
   
   

   public String toString() {
      String out = "ProducedDb(" + "\n" + owner + "\n";
      for(int i = 0; i < data.length; i++ ) {
         if ( data[i] != null )
            out += "data[" + i + "]: " + data[i] + "\n";
      }
      out += ")";
      return out;
   }
   

   public synchronized boolean setConsumers(String key, Vector input) {
      EffectChain ch;
      ConsumerRecord e;
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; data[i] != null && j < data[i].size(); j++ ) {
            ch = (EffectChain)data[i].elementAt(j);
            if ( ch.isExternal() && ch.key.equals(key) ) {
               if ( !checkConsumers(ch.start,ch.amount,ch.consumed,input) )
                  return false;
               data[i].removeElementAt(j--);
               for(int k = 0; k < input.size(); k++ ) {
                  e = (ConsumerRecord)input.elementAt(k);
                  ch = new EffectChain(e.consumer_id,e.amount,
                                       e.start,e.consumed);
                  data[i].addElement(ch);
               }
               return true;
            }
         }
      }
      return false;
   }


   protected synchronized boolean checkConsumers(int start, int amount,
      boolean consumed, Vector temp) {

      ConsumerRecord e;
      Vector current = new Vector();
      for(int i = 0; i < temp.size(); i++ ) {
         e = (ConsumerRecord)temp.elementAt(i);
         if ( e.start < start ||
              !checkCurrent(amount,current,e.start,e.consumed,e.amount) )
            return false;
         current.addElement(e);
      }
      current = null; // GC
      return true;
   }


   protected synchronized boolean checkCurrent(int available, Vector current,
      int start, boolean consumed, int amount) {

      int reserved = 0;
      ConsumerRecord e;
      for(int i = 0; i < current.size(); i++ ) {
         e = (ConsumerRecord)current.elementAt(i);
         if ( consumed ) {
            if ( e.consumed || e.start > start )
               reserved += e.amount;
         }
         else {
            if ( e.consumed && e.start <= start )
               reserved += e.amount;
         }
      }
      return available - reserved >= amount;
   }


public void debug(String str) { 
 //  System.out.println("ProducedDb>> " + str); 
}
}
