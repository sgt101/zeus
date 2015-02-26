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

public class ResourceItem extends Observable {
   public static final String ALLOCATED = "Reserved";
   public static final String UNALLOCATED = "Free";

   public static final int UNCHANGED = 0;
   public static final int MODIFY    = 1;
   public static final int DELETE    = 2;

   protected Fact fact = null;
   protected Hashtable reservations = new Hashtable();

   public ResourceItem(Fact fact) {
      Assert.notNull(fact);
      this.fact = fact;
   }

   public Fact getFact() { return fact; }

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
      Entry e;
      while( enum.hasMoreElements() ) {
         e = (Entry)enum.nextElement();
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

   public int getReservedAmount(Observer observer) {
      Entry e = (Entry) reservations.get(observer);
      return (e == null) ? 0 : e.amount;
   }
   public synchronized void changeReservedAmount(Observer o, int amount) {
      Entry e = (Entry) reservations.get(o);
      Assert.notNull(e);
      Assert.notFalse(e.amount >= amount);
      e.amount = amount;
   }
   public synchronized void cancelReservation(Observer observer) {
      deleteObserver(observer);
      Entry e = (Entry) reservations.remove(observer);
   }
   public synchronized boolean executeNow(Observer observer, int now) {
      boolean execute_now = true;
      Entry e = (Entry) reservations.get(observer);
      if ( !e.consumed )
         return true;
      else {
         reservations.remove(observer);
         boolean status = reservationOK(now,e.consumed,e.amount);
         reservations.put(observer,e);
         return status;
      }
   }
   public synchronized boolean newStartTime(Observer observer, int start) {
      Entry e = (Entry) reservations.remove(observer);
      if ( reserve(observer,start,e.consumed,e.amount) )
         return true;
      else {
         reservations.put(observer,e);
         return false;
      }
   }

   public synchronized boolean reserve(Observer observer, int start,
                                       boolean consumed, int amount) {

      if ( !reservationOK(start,consumed,amount) ) return false;

      Entry e = new Entry(start,consumed,amount);
      // Assert.notFalse(reservations.put(observer,e) == null);
      Entry xe = (Entry) reservations.put(observer,e);
      if ( xe != null ) {
         System.err.println("ResourceItem reserve: multiple entries " +
                            "for same observer " + observer);
         System.err.println("Previous = " + xe);
         System.err.println("Current = " + e);
      }

      addObserver(observer);
      return true;
   }

   public void deleted() {
      Core.DEBUG(3,"Notifying observers of deletion...\n" + fact.pprint());
      setChanged();
      notifyObservers("deleted");
   }

   public int consumed(Observer observer) {
      deleteObserver(observer);
      int no = fact.getNumber();
      Entry e = (Entry) reservations.remove(observer);
      int status;
      if ( !e.consumed )         status = UNCHANGED;
      else if ( e.amount < no )  status = MODIFY;
      else                       status = DELETE;

      if ( status == MODIFY ) fact.setNumber(no-e.amount);

      return status;
   }
   protected class Entry {
      public int start;
      public boolean consumed;
      public int amount;

      public Entry(int start, boolean consumed, int amount) {
         this.start = start;
         this.consumed = consumed;
         this.amount = amount;
      }
      public String toString() {
         return( "(" +
                  ":start " + start + " " +
                  ":consumed " + consumed + " " +
                  ":amount " + amount +
                 ")"
               );
      }
   }
}
