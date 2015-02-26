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



package zeus.actors.rtn;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.util.*;
import zeus.actors.*;

public class AuditTable {
   public static final int CHILD = 0;
   public static final int ROOT = 1;

   protected static final int GOAL = 0;
   protected static final int KEY = 1;

   protected Hashtable contracts = new Hashtable();
   protected Hashtable auxiliary = new Hashtable();
   protected Hashtable router    = new Hashtable();
   protected Engine    engine    = null;


   AuditTable(Engine engine) {
      Assert.notNull(engine);
      this.engine = engine;
   }

/**
  ProducerRecord/ConsumerRecord
  -----------------------------

  says to agent <c>
       expect data from agent <p>
       with comms ref <comms_key> and internal ref <cid/uid>
       do action
          allocate data to record <cid>
          at precondition slot <pid/uid>


  says to agent <p>
       expect data from yourself record <pid>
       with internal ref <cid/uid> [at time <s>, amount <a>, consumed <b>]
       do action
          1. send data to agent <c>
             with ref <comms_key>
          2. Request payment
*/

   public void addProducerRecords(Vector records) {
      Core.DEBUG(2,"AddProducerRecords " + records);
      if ( records == null ) return;
      for(int i = 0; i < records.size(); i++ )
         addRoutingRecord((RoutingRecord)records.elementAt(i));
   }
   
   
   public void addConsumerRecords(Vector records) {
      Core.DEBUG(2,"AddConsumerRecords " + records);
      if ( records == null ) return;
      for(int i = 0; i < records.size(); i++ )
         addRoutingRecord((RoutingRecord)records.elementAt(i));
   }


   protected void addRoutingRecord(RoutingRecord rec) {
      String myself = engine.getAgentContext().whoami();
      if ( rec.producer.equals(myself) || rec.consumer.equals(myself) ){
         String key = rec.consumer_id + "/" + rec.use_ref;
         Object ob = router.put(key,rec);
         if ( ob != null )
            Core.ERROR(rec.equals(ob),2001,this);
      }
   }
   

   public synchronized void add(Contract entry) {
      Core.DEBUG(2,"AddContract " + entry);
      Object ob;
      ob = contracts.put(entry.goal.getId(),entry);
      Core.ERROR(ob == null,2002,this);
   }
   
   
   public synchronized void add(Goal goal, String key, double cost,
                                boolean delivered, boolean paid, String agent,
                                String owner, double timeout) {
      add(new Contract(goal,key,cost,delivered,paid,agent,owner,timeout));
   }
   
   
   public synchronized void del(Goal goal) {
      Core.DEBUG(2,"Del goal " + goal);
/*
      String goalId = goal.getId();
      this.remove(goalId);
      Contract aux = (Contract)auxiliary.remove(goalId);
      String myself = engine.getAgentContext().whoami();
      if ( aux != null && !aux.owner.equals(myself) ) {
         MsgHandler hd = engine.getAgentContext().MsgHandler();
         Vector goals = new Vector();
         goals.addElement(aux.goal);
         hd.send_message(aux.owner,aux.key,"exception",goals);
      }
*/
   }
   
   
   public synchronized void goodsReceived(DelegationStruct ds) {
      Core.DEBUG(2,"GoodsReceived\n" + ds);
      Core.DEBUG(2,this);

      Planner table = engine.getAgentContext().Planner();
      MsgHandler handler = engine.getAgentContext().MsgHandler();
      ResourceDb db = engine.getAgentContext().ResourceDb();
      OntologyDb ontology = engine.getAgentContext().OntologyDb();
      String myself = engine.getAgentContext().whoami();

      String agent;
      RoutingRecord rec = null;
      Contract entry;
      Fact item, money;

      if ( (rec = (RoutingRecord)router.remove(ds.key)) != null ) {
         if ( ds.agent.equals(myself) ) {
            Core.ERROR(rec.producer.equals(myself),1001,this);
            // send result to customer
            engine.continue_dialogue(rec.comms_key,rec.consumer,"inform",
				     "result", ds.key, ds.goals);

	        // mark as delivered
            entry = (Contract)contracts.get(rec.producer_id);
            entry.delivered = true;
            // INVOICE customer
            money = ontology.getFact(Fact.VARIABLE,OntologyDb.MONEY);
            money.setValue(OntologyDb.AMOUNT,Double.toString(entry.cost));
            Vector data = new Vector();
            data.addElement(money);
            engine.continue_dialogue(entry.key,entry.owner,"inform",
				     "invoice", rec.producer_id, data);
         }
         else {
            Core.ERROR(rec.consumer.equals(myself),1002,this);
            entry = (Contract)contracts.get(rec.producer_id);
            item = (Fact)ds.goals.elementAt(0);
	        // set unit cost
	        if ( item.isa(OntologyDb.ENTITY) ) {
               double unit_cost = entry.cost/item.getNumber();
               item.setValue(OntologyDb.COST,Double.toString(unit_cost));
            }
            String goal_id = rec.consumer_id;
            String subgoal_id = rec.producer_id + "/" + rec.use_ref;
            table.notifyReceived(item,goal_id,subgoal_id);
            handler.removeRule(ds.key);
            // mark DELIVERED
            if ( entry != null )
               entry.delivered = true;
            }
        }
      else {
        debug ("toString" + ds.toString()); 
        debug ("key " + ds.key); 
        debug ("contracts " + contracts.toString()); 
         entry = (Contract)contracts.get(ds.key);
         if ( ds.agent.equals(myself) ) {
            if ( entry.owner.equals(myself) ) {
               // goods for myself by myself
   	       // set unit cost
               contracts.remove(ds.key);
               item = (Fact)ds.goals.elementAt(0);
   	       if ( item.isa(OntologyDb.ENTITY) ) {
                  double unit_cost = entry.cost/item.getNumber();
                  item.setValue(OntologyDb.COST,Double.toString(unit_cost));
               }
	       db.add(ds.goals);
            }
            else {
	       // goods by myself for someone else; selling my service
               // send result to customer
               engine.continue_dialogue(entry.key,entry.owner,"inform",
	          "result", ds.key, ds.goals);

	       // mark as delivered
               entry.delivered = true;
               // INVOICE customer
               money = ontology.getFact(Fact.VARIABLE,OntologyDb.MONEY);
               money.setValue(OntologyDb.AMOUNT,Double.toString(entry.cost));
               Vector data = new Vector();
               data.addElement(money);
               engine.continue_dialogue(entry.key,entry.owner,"inform",
	          "invoice", ds.key, data);
            }
         }
         else {
            // goods from someone for me
            Core.DEBUG (3, entry.toString()); 
            Core.DEBUG (3, entry.owner); 
            
            Core.ERROR(entry.owner.equals(myself),1075,this);
   	    // set unit cost
            entry.delivered = true;
            item = (Fact)ds.goals.elementAt(0);
   	    if ( item.isa(OntologyDb.ENTITY) ) {
               double unit_cost = entry.cost/item.getNumber();
               item.setValue(OntologyDb.COST,Double.toString(unit_cost));
            }
	    db.add(ds.goals);
         }
      }
      Core.DEBUG(2,"GoodsReceived - post\n" + this);
   }


    //synchronized 
   public  void invoiceReceived(DelegationStruct ds) {
      Core.DEBUG(2,"AuditTable invoiceReceived:\n" + ds);
      Core.DEBUG(2,this);

      ResourceDb db = engine.getAgentContext().ResourceDb();
      MsgHandler handler = engine.getAgentContext().MsgHandler();
      String myself = engine.getAgentContext().whoami();

      Contract entry = (Contract)contracts.remove(ds.key);
      Core.DEBUG(3,"InvoiceReceived: contract\n\t" + entry);
      Assert.notFalse(entry.agent.equals(ds.agent));
      Assert.notFalse(entry.owner.equals(myself));
      entry.paid = true;

      Core.DEBUG(3,"InvoiceReceived: about to debit");
      Fact money = db.debit(entry.cost);
      Core.DEBUG(3,"InvoiceReceived: debit = " + money);

      Vector data = new Vector();
      data.addElement(money);

      engine.continue_dialogue(entry.key,entry.agent,"inform",
         "payment", ds.key, data);
      handler.removeRule(entry.key);
      Core.DEBUG(2,"AuditTable invoiceReceived - post:\n" + this);
   }

   public synchronized void paymentReceived(DelegationStruct ds) {
      Core.DEBUG(2,"AuditTable paymentReceived:\n" + ds);
      Core.DEBUG(2,this);

      ResourceDb db = engine.getAgentContext().ResourceDb();
      String myself = engine.getAgentContext().whoami();

      Contract entry = (Contract)contracts.remove(ds.key);
      Assert.notFalse(entry.owner.equals(ds.agent));
      Assert.notFalse(entry.agent.equals(myself));
      entry.paid = true;

      // Payment for a job I did
      Fact money = (Fact)ds.goals.elementAt(0);
      db.add(money);
      Core.DEBUG(2,"AuditTable paymentReceived - post\n" + this);
   }


   public synchronized int exception(DelegationStruct ds) {
      Core.DEBUG(2,"AuditTable exception:\n" + ds);
/*
      Core.DEBUG(2,this);

      Contract entry = getEntry(KEY,ds.key);
      this.remove(entry.goal.getId());

      Planner table = engine.getAgentContext().Planner();
      Goal g = table.recreateSubgoal(entry.goal);

      ds.goals.removeAllElements();
      ds.goals.addElement(g);
      String myself = engine.getAgentContext().whoami();

      if ( ds.agent.equals(myself) ) {
         auxiliary.put(g.getId(),entry);
         if ( entry.owner.equals(myself) )
            entry.cost = 0;
         return ROOT;
      }
      else {
         Contract aux;
         aux = (Contract)auxiliary.get(entry.goal.getId());
         if ( aux != null ) {
            auxiliary.remove(entry.goal.getId());
            Assert.notFalse(auxiliary.put(g.getId(),aux) == null);
            return ROOT;
         }
         return CHILD;
      }
*/
         return CHILD;
   }


   public synchronized void cancel(DelegationStruct ds) {
      Core.DEBUG(2,"AuditTable cancel:\n" + ds);
/*
      Core.DEBUG(2,this);

      String myself = engine.getAgentContext().whoami();
      Contract entry = getEntry(KEY,ds.key);

      Assert.notFalse(entry.agent.equals(myself));
      Assert.notFalse(ds.agent.equals(entry.owner));

      this.remove(entry.goal.getId()); // remove entry
      auxiliary.remove(entry.goal.getId()); // remove entry
      redirection.remove(entry.goal.getId()); // remove entry

      engine.getAgentContext().Planner().reject(ds.goals, new Vector());
*/
   }
   public synchronized void cancel(String goal_id) {
      Core.DEBUG(2,"AuditTable cancel: " + goal_id);
/*
      MsgHandler handler = engine.getAgentContext().MsgHandler();
      String myself = engine.getAgentContext().whoami();
      Core.DEBUG(2,this);

      if ( entry == null ) return;

      this.remove(goal_id);
      redirection.remove(goal_id);
      if ( !entry.agent.equals(myself) ) {
         Vector goals = new Vector();
         goals.addElement(entry.goal);
         handler.send_message(entry.agent,entry.key,"cancel",goals);
      }
*/


   }
   public synchronized void enact(DelegationStruct ds) {
      Core.DEBUG(2,"AuditTable enact:\n" + ds);
      
      /*
      Planner table = engine.getAgentContext().Planner();
      MsgHandler handler = engine.getAgentContext().MsgHandler();
      String myself = engine.getAgentContext().whoami();

      Core.DEBUG(2,this);

      Contract entry = contracts.get(KEY,ds.key);
      Assert.notFalse(entry.owner.equals(ds.agent));
      Assert.notFalse(entry.agent.equals(myself));

      Goal g = (Goal)ds.goals.elementAt(0);
      PlannerEnactStruct es = table.enact(g,entry.goal);
      if ( !es.ok ) return;

      // add entries referring to self
      add(g, g.getId(), entry.cost, false, false, entry.agent,
          entry.owner, g.getEndTime());
      if ( !entry.owner.equals(entry.agent) )
         router.add(g.getId(),"coordination_dialog");

      // add entries for children
      String goal_id;
      Vector goals;
      for(int i = 0; i < es.external.size(); i++ ) {
         g = (Goal)es.external.elementAt(i);
         goal_id = (String)es.table.get(g.getId());
         entry = getEntry(GOAL,goal_id);
         Assert.notFalse(entry.owner.equals(myself));
         add(g, g.getId(), entry.cost, false, false, entry.agent,
             entry.owner, g.getEndTime());
         router.add(g.getId(),"coordination_dialog");

         goals = new Vector();
         goals.addElement(g);
         handler.send_message(entry.agent,entry.key,"enact",goals);
      }
      */
   }
   
   
   protected synchronized boolean cleanup(Contract entry) {
/*
      if ( entry.delivered && entry.paid ) {
         if ( entry.goal != null )
            this.remove(entry.goal.getId());
         else
            this.remove(entry.key);

         return true;
      }
      else
         return false;
*/
      return false;
   }
   public Object remove(Object key) {
/*
      Contract entry = (Contract)super.remove(key);
      if ( entry != null ) {
         MContext msgContext = engine.getAgentContext().MsgContext();
         msgContext.del(entry.key);
      }
      return entry;
*/
      return null;
   }

   public String toString() {
      String output = "Audit Table Entries\n";
      String key;
      Enumeration keys = contracts.keys();
      Contract entry;
      RoutingRecord record;

      output += "...Contracts...\n";
      while( keys.hasMoreElements() ) {
         key = (String)keys.nextElement();
         entry = (Contract)contracts.get(key);
         output += "Table-key = " + key + "\n" + entry + "\n";
      }

      output += "...Routing...\n";
      keys = router.keys();
      while( keys.hasMoreElements() ) {
         key = (String)keys.nextElement();
         record = (RoutingRecord)router.get(key);
         output += "Table-key = " + key + "\n" + record + "\n";
      }

      output += "\n**************\n";
      return output;
   }
   
   
   public void debug (String str) { 
    //System.out.println("audittable >> " + str);
    }

}
