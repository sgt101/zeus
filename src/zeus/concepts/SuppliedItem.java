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

public class SuppliedItem {
   public static final int UNCHANGED = 0;
   public static final int MODIFY    = 1;
   public static final int DELETE    = 2;

   protected Fact fact = null;
   protected String supplier = null;
   protected String id = null;
   protected String link = null;
   protected Hashtable reservations = new Hashtable();

   public SuppliedItem(String id, String link, String supplier, Fact fact) {
      Assert.notNull(id);
      Assert.notNull(link);
      Assert.notNull(fact);
      Assert.notNull(supplier);
      this.id = id;
      this.link = link;
      this.fact = fact;
      this.supplier = supplier;
   }

   public Fact    getFact()     { return fact; }
   public String  getSupplier() { return supplier; }
   public String  getId()       { return id; }
   public String  getLink()     { return link; }

   public boolean equals(SuppliedItem item) {
      return id.equals(item.getId());
   }

   public synchronized ReservationEntry[] getReservations() {
      ReservationEntry[] out = new ReservationEntry[reservations.size()];
      Enumeration enum = reservations.elements();
      for(int i = 0; enum.hasMoreElements(); i++ )
         out[i] = (ReservationEntry)enum.nextElement();
      return out;
   }
   public synchronized ReservationEntry[] getReservations(String consumer) {
      Vector out = new Vector();
      ReservationEntry entry;
      Enumeration enum = reservations.elements();
      for(int i = 0; enum.hasMoreElements(); i++ ) {
         entry = (ReservationEntry)enum.nextElement();
         if ( entry.agent.equals(consumer) )
            out.addElement(entry);
      }
      ReservationEntry[] data = new ReservationEntry[out.size()];
      for(int i = 0; i < out.size(); i++ )
         data[i] = (ReservationEntry)out.elementAt(i);
      return data;
   }

   public synchronized boolean isReserved() {
      return !reservations.isEmpty();
   }

   public synchronized boolean reservationOK(int start, boolean consumed,
                                             int amount) {
      return unreservedAmount(start,consumed) >= amount;
   }
   public synchronized int unreservedAmount(int start, boolean consumed) {
      return fact.getNumber() - reservedAmount(start,consumed);
   }
   public synchronized int reservedAmount(int start, boolean consumed) {
      Enumeration enum = reservations.elements();
      int reserved = 0;
      ReservationEntry e;
      while( enum.hasMoreElements() ) {
         e = (ReservationEntry)enum.nextElement();
         if ( consumed ) {
            if ( e.consumed || e.start > start )
               reserved += e.amount;
         }
         else {
            if ( e.consumed && e.start <= start )
               reserved += e.amount;
         }
      }
      return reserved;
   }

   public synchronized String[] getReservationId() {
      String[] data = new String[reservations.size()];
      Enumeration enum = reservations.elements();
      ReservationEntry e;
      for(int i = 0; enum.hasMoreElements(); i++ ) {
         e = (ReservationEntry)enum.nextElement();
         data[i] = e.id;
      }
      return data;
   }
   public synchronized int getEarliestReservationTime() {
      Enumeration enum = reservations.elements();
      int time = Integer.MAX_VALUE;
      ReservationEntry e;
      while( enum.hasMoreElements() ) {
         e = (ReservationEntry)enum.nextElement();
         time = Math.min(time,e.start);
      }
      return time;
   }
   public boolean containsReservationId(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return e != null;
   }
   public int getReservationTime(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return e.start;
   }
   public boolean isReservationConsumed(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return e.consumed;
   }
   public int getReservedAmount(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return (e == null) ? 0 : e.amount;
   }
   public String getReservingAgent(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return (e == null) ? null : e.agent;
   }
   public String getReservationGoalId(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return (e == null) ? null : e.goalId;
   }
   public String getReservationCommsKey(String reservationId) {
      ReservationEntry e = (ReservationEntry) reservations.get(reservationId);
      return (e == null) ? null : e.comms_key;
   }
   public int getAmountReservedByAgent(String agentId) {
      Enumeration enum = reservations.elements();
      int reserved = 0;
      ReservationEntry e;
      while( enum.hasMoreElements() ) {
         e = (ReservationEntry)enum.nextElement();
         if ( e.agent.equals(agentId) )
            reserved += e.amount;
      }
      return reserved;
   }
   public synchronized void changeReservedAmount(String resrvId, int amount) {
      ReservationEntry e = (ReservationEntry) reservations.get(resrvId);
      Assert.notNull(e);
      Assert.notFalse(e.amount >= amount);
      e.amount = amount;
   }
   public synchronized boolean cancelReservation(String resrvId) {
      ReservationEntry e = (ReservationEntry) reservations.remove(resrvId);
      return e != null;
   }
   public synchronized boolean executeNow(String resrvId, int now) {
      boolean execute_now = true;
      ReservationEntry e = (ReservationEntry) reservations.get(resrvId);
      if ( !e.consumed )
         return true;
      else {
         reservations.remove(resrvId);
         boolean status = reservationOK(now,e.consumed,e.amount);
         reservations.put(resrvId,e);
         return status;
      }
   }
   public synchronized boolean newStartTime(String resrvId, int start) {
      ReservationEntry e = (ReservationEntry) reservations.remove(resrvId);
      if ( reserve(resrvId,start,e.consumed,e.amount,
                   e.agent,e.goalId,e.comms_key) )
         return true;
      else {
         reservations.put(resrvId,e);
         return false;
      }
   }
   public synchronized boolean reserve(ReservationEntry e) {
      return reserve(e.id,e.start,e.consumed,e.amount,e.agent,
         e.goalId,e.comms_key);
   }

   public synchronized boolean reserve(String resrvId, int start,
                                       boolean consumed, int amount,
                                       String agent, String goalId,
                                       String comms_key) {

      ReservationEntry e = (ReservationEntry)reservations.get(resrvId);
      if ( e != null ) {
         Assert.notFalse(e.start == start   && e.consumed == consumed &&
                         e.amount == amount && e.agent.equals(agent) &&
                         e.goalId.equals(goalId) &&
                         e.comms_key.equals(comms_key));
         return true;
      }

      if ( !reservationOK(start,consumed,amount) ) return false;

      e = new ReservationEntry(resrvId,start,consumed,
                               amount,agent,goalId,comms_key);
      // Assert.notFalse(reservations.put(observer,e) == null);
      ReservationEntry xe = (ReservationEntry) reservations.put(resrvId,e);
      if ( xe != null ) {
         System.err.println("SuppliedItem reserve: multiple entries " +
                            "for same observer " + resrvId);
         System.err.println("Previous = " + xe);
         System.err.println("Current = " + e);
      }
      return true;
   }

   public int consumed(String resrvId) {
      int no = fact.getNumber();
      ReservationEntry e = (ReservationEntry) reservations.remove(resrvId);
      int status;
      if ( !e.consumed )         status = UNCHANGED;
      else if ( e.amount < no )  status = MODIFY;
      else                       status = DELETE;

      if ( status == MODIFY ) fact.setNumber(no-e.amount);
      return status;
   }
   public String toString() {
      String out = "(";
      out += ":id " + id + " " +
             ":link \"" + link + "\" " +
             ":fact " + fact + " " +
             ":supplier " + supplier;

      Enumeration enum = reservations.elements();
      if ( enum.hasMoreElements() ) {
         ReservationEntry  entry;
         out += " :reservations (";
         while( enum.hasMoreElements() ) {
            entry = (ReservationEntry)enum.nextElement();
            out += entry;
         }
         out += ")";
      }
      out += ")";
      return out;
   }
   public SuppliedItem duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public SuppliedItem duplicate(DuplicationTable table) {
      Fact f1 = fact.duplicate(table);
      SuppliedItem item = new SuppliedItem(id,link,supplier,f1);

      Enumeration enum = reservations.elements();
      ReservationEntry  e;
      while( enum.hasMoreElements() ) {
         e = (ReservationEntry)enum.nextElement();
         item.reserve(e.id,e.start,e.consumed,
                      e.amount,e.agent,e.goalId,e.comms_key);
      }
      return item;
   }
}
