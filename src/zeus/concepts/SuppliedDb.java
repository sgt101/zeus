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



package zeus.concepts;

import java.util.*;
import zeus.util.*;

public class SuppliedDb extends Hashtable {
   protected OntologyDb ontology;

   public SuppliedDb(OntologyDb ontology) {
      Assert.notNull(ontology);
      this.ontology = ontology;
   }
   public SuppliedDb(SuppliedDb db) {
      this.ontology = db.getOntology();
      add(db);
   }
   OntologyDb getOntology() {
      return ontology;
   }

   public synchronized boolean add(SuppliedDb db) {
      SuppliedItem item;
      Enumeration e;
      Hashtable tb;
      boolean status = true;
      Enumeration enum = db.elements();

      while( status && enum.hasMoreElements() ) {
         tb = (Hashtable)enum.nextElement();
         e = tb.elements();
         while( status && e.hasMoreElements() ) {
            item = (SuppliedItem)e.nextElement();
            status &= add(item);
         }
      }
      return status;
   }
   public synchronized boolean add(Vector List) {
      boolean status = true;
      for(int i = 0; status && i < List.size(); i++ )
         status &= add((SuppliedItem)List.elementAt(i));
      return status;
   }
   public synchronized boolean add(SuppliedItem[] List) {
      boolean status = true;
      for(int i = 0; status && i < List.length; i++ )
         status &= add(List[i]);
      return status;
   }
   public synchronized boolean add(SuppliedItem item) {
      Assert.notNull(item);

      String type = item.getFact().getType();
      Hashtable table;

      if ( (table = (Hashtable)this.get(type)) == null ) {
         table = new Hashtable();
         this.put(type,table);
      }
      SuppliedItem db_item;
      db_item = (SuppliedItem)table.get(item.getId());
      if ( db_item == null ) {
         table.put(item.getId(),item);
         return true;
      }
      else {
         ReservationEntry[] entry = item.getReservations();
         boolean status = true;
         for(int i = 0; status && i < entry.length; i++ )
            status &= db_item.reserve(entry[i].id,entry[i].start,
                                      entry[i].consumed,entry[i].amount,
                                      entry[i].agent,entry[i].goalId,
                                      entry[i].comms_key);
         return status;
      }
   }

   public synchronized void del(Vector List) {
      for(int i = 0; i < List.size(); i++ )
         del( (SuppliedItem)List.elementAt(i));
   }
   public synchronized void del(SuppliedItem[] List) {
      for(int i = 0; i < List.length; i++ )
         del(List[i]);
   }
   public synchronized void del(SuppliedItem item) {
      Assert.notNull(item);

      String type = item.getFact().getType();
      Hashtable table;

      if ( (table = (Hashtable)this.get(type)) == null )
         return;

      SuppliedItem db_item;
      Enumeration enum = table.elements();
      while( enum.hasMoreElements() ) {
         db_item = (SuppliedItem)enum.nextElement();
         if ( db_item.equals(item) ) {
            table.remove(db_item.getId());
            break;
         }
      }
      if ( table.isEmpty() ) this.remove(type);
   }

   public synchronized int findAll(SuppliedRequester rec, Fact fact,
                                   int precond_position, int required) {
      Hashtable table;
      SuppliedItem item;
      Fact f1, f2;

      Assert.notFalse(required > 0);

      if ( (table = (Hashtable)this.get(fact.getType())) == null )
         return required;

      int start = rec.getStartTime();
      boolean consumed = !fact.isReadOnly();

      Enumeration enum = table.elements();
      Bindings b = new Bindings();
      while(required > 0 && enum.hasMoreElements() ) {
         item = (SuppliedItem)enum.nextElement();
         f1 = item.getFact();
         b.clear();
         int available;
         if ( (available = item.unreservedAmount(start,consumed)) > 0 &&
              f1.unifiesWith(fact,b) && rec.applyConstraints(b) ) {
            Assert.notFalse( rec.setSupplier(precond_position,
                                             Math.min(available,required),
                                             item) );
            required = required - Math.min(available,required);

            Core.DEBUG(3,"SRDb Required to find Fact:\n" + fact);
            Core.DEBUG(3,"SRDb Supplied Item:\n" + item +
                         "\nassigned to rec " + rec);
         }
      }
      return required;
   }


   public synchronized void allocateResources(SuppliedRequester rec) {
      int position, required;

      PrimitiveTask task = rec.getTask();
      Fact[][] consumed = task.orderPreconditions();
      boolean status = true;

      for(int i = 0; status && i < consumed.length; i++) {
         for(int j = 0; j < consumed[i].length; j++) {
            if ( !consumed[i][j].isNegative() ) {
               position = task.getConsumedPos(consumed[i][j]);
               required = rec.noRequiredItems(position);
               if ( required > 0)
                  status &= (findAll(rec,consumed[i][j],position,required) == 0);
            }
         }
      }
   }
   public synchronized int allocateResource(SuppliedRequester rec,
                                            int position, int amount) {
      PrimitiveTask task = rec.getTask();
      Fact fact = task.getPrecondition(position);
      return findAll(rec,fact,position,amount);
   }

   public synchronized Fact evalLocal(Fact fact) {
      Fact[] answer = all(fact);
      if ( answer == null || answer.length == 0 ) return null;
      Fact result = new Fact(answer[0]);
      for(int i = 1; i < answer.length; i++ )
         Assert.notFalse( result.disjoin(answer[i]) );
      return result;
   }

   public synchronized Fact[] all(Fact fact) {
      Hashtable table;
      SuppliedItem item;
      Fact f1;

      if ( (table = (Hashtable)this.get(fact.getType())) == null )
         return null;

      Vector answer = new Vector();
      Bindings b = new Bindings();
      Enumeration enum = table.elements();
      while( enum.hasMoreElements() ) {
         item = (SuppliedItem)enum.nextElement();
         f1 = item.getFact();
         b.clear();
         if ( f1.unifiesWith(fact,b) )
            answer.addElement(f1);
      }
      Fact[] results = new Fact[answer.size()];
      for(int i = 0; i < answer.size(); i++ )
         results[i] = (Fact) answer.elementAt(i);
      return results;
   }

   public synchronized Fact any(Fact fact) {
      Fact[] answer = all(fact);
      if ( answer == null ) return null;
      int pos = (int) (Math.random()*answer.length);
      return answer[pos];
   }

   public synchronized boolean contains(Fact fact, int start) {
      Hashtable table;
      SuppliedItem item;
      Fact f1;

      if ( (table = (Hashtable)this.get(fact.getType())) == null )
         return false;

      Bindings b = new Bindings();
      int required = fact.getNumber();
      boolean consumed = !fact.isReadOnly();
      int available;
      Enumeration enum = table.elements();
      while( enum.hasMoreElements() && required > 0 ) {
         item = (SuppliedItem)enum.nextElement();
         b.clear();
         f1 = item.getFact();
         if ( (available = item.unreservedAmount(start,consumed)) > 0 &&
              f1.unifiesWith(fact,b) ) {
            required = required - Math.min(available,required);
         }
      }
      return required <= 0;
   }

   public boolean cancelReservation(String resrvId) {
      SuppliedItem item;
      boolean status = false;
      Enumeration enum = this.elements();
      Enumeration e;
      Hashtable tb;
      while( !status && enum.hasMoreElements() ) {
         tb = (Hashtable)enum.nextElement();
         e = tb.elements();
         while( !status && e.hasMoreElements() ) {
            item = (SuppliedItem) e.nextElement();
            status = item.cancelReservation(resrvId);
         }
      }
      return status;
   }

   public synchronized SuppliedItem getSuppliedItem(String itemId) {
      SuppliedItem item;
      Enumeration enum = this.elements();
      Enumeration e;
      Hashtable tb;
      while( enum.hasMoreElements() ) {
         tb = (Hashtable)enum.nextElement();
         e = tb.elements();
         while( e.hasMoreElements() ) {
            item = (SuppliedItem) e.nextElement();
            if ( item.getId().equals(itemId) )
               return item;
         }
      }
      return null;
   }

   public synchronized boolean isReserved(String itemId) {
      SuppliedItem item  = getSuppliedItem(itemId);
      return item.isReserved();
   }

   public synchronized ReservationEntry[] getReservations(String producer,
                                                          String consumer) {
      Vector data = new Vector();
      SuppliedItem item;
      Enumeration enum = this.elements();
      Enumeration e;
      Hashtable tb;
      ReservationEntry[] out;
      while( enum.hasMoreElements() ) {
         tb = (Hashtable)enum.nextElement();
         e = tb.elements();
         while( e.hasMoreElements() ) {
            item = (SuppliedItem) e.nextElement();
            if ( item.getSupplier().equals(producer) ) {
               out = item.getReservations(consumer);
               for(int i = 0; i < out.length; i++ )
                  data.addElement(out[i]);
            }
         }
      }
      ReservationEntry[] output = new ReservationEntry[data.size()];
      for(int i = 0; i < data.size(); i++ )
         output[i] = (ReservationEntry)data.elementAt(i);
      return output;
   }

   public String toString() {
      String out = "";
      Enumeration enum = this.elements();
      Enumeration e;
      Hashtable tb;
      while( enum.hasMoreElements() ) {
         tb = (Hashtable)enum.nextElement();
         e = tb.elements();
         while( e.hasMoreElements() ) {
            out += e.nextElement();
         }
      }
      return out;
   }
   public SuppliedDb duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public SuppliedDb duplicate(DuplicationTable table) {
      SuppliedItem item;
      SuppliedDb db = new SuppliedDb(ontology);

      Enumeration enum = this.elements();
      Enumeration e;
      Hashtable tb;
      while( enum.hasMoreElements() ) {
         tb = (Hashtable)enum.nextElement();
         e = tb.elements();
         while( e.hasMoreElements() ) {
            item = (SuppliedItem)e.nextElement();
            db.add(item.duplicate(table));
         }
      }
      return db;
   }
}
