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
import zeus.util.*;
import zeus.concepts.*;

public class DataRec implements Observer {
   protected int              nAvailable = 0;
   protected Fact             fact = null;
   protected PlanRecord       record = null;
   protected int              position = -1;
   protected Vector           available = new Vector();
   protected boolean          isNegative;


   // meaningless init  to allow rearch
   public DataRec() {
   ;
   }

   public DataRec(Fact fact, PlanRecord record, int precond_position) {
      Assert.notNull(fact);
      Assert.notNull(record);
      Assert.notFalse(precond_position >= 0);

      this.fact = fact;
      this.record = record;
      this.position = precond_position;
      this.isNegative = fact.isNegative();
   }

   public DataRec(Fact fact) {
      Core.ERROR(fact,1,this);

      this.fact = fact;
      this.isNegative = fact.isNegative();
      Core.ERROR(!isNegative,2,this);
   }

   public boolean add(ResourceItem item, int start, int num) {
      Core.ERROR(!isNegative,3,this);
      if ( item.reserve(this,start,!fact.isReadOnly(),num) ) {
         available.addElement(item);
         nAvailable += num;
         return true;
      }
      return false;
   }

   public void free() {
      nAvailable = 0;
      available.removeAllElements();
   }

   public void update(Observable o, Object arg) {
      Core.ERROR(!isNegative,4,this);
      ResourceItem item = (ResourceItem)o;
      String operation = (String)arg;
      Fact f1 = item.getFact();
      Core.DEBUG(3,"Notification in datarec from " + f1.pprint() +
         " of " + operation);

      if ( operation.equals("deleted") ) {
         // item has been deleted: remove item & alert
         // record to try to reallocate for item.fact
         int num = remove(item);
         if ( record != null )  {
            if ( !fact.isReadOnly() )
               record.updateCost(-1.0*num*f1.getUnitCost());
            record.reallocateResource(position,num);
         }
         else {
            System.err.println("Reserved item deleted - what do we do??");
         }
      }
   }

   public boolean executeNow(ResourceDb db, int now) {
      if ( isNegative ) {
         // make sure db does not contain fact
         return db.evalNegative(fact);
      }
      else {
         ResourceItem item;
         boolean execute_now = true;
         for(int i = 0; execute_now && i < available.size(); i++ ) {
            item = (ResourceItem) available.elementAt(i);
            execute_now &= item.executeNow(this,now);
         }
         return execute_now;
      }
   }
   public void newStartTime(int start) {
      if ( isNegative ) {
         return;
      }
      else {
         ResourceItem item;
         for(int i = 0; i < available.size(); i++ ) {
            item = (ResourceItem) available.elementAt(i);
            Assert.notFalse(item.newStartTime(this,start));
         }
      }
   }

   public DataRec subtract(PlanRecord rec, int position, int required) {
      Core.ERROR(!isNegative,5,this);
      Core.ERROR(record,6,this);
      DataRec datarec = rec.getDatarec(position);
      ResourceItem item;
      int start = rec.getStartTime();
      int no;
      for(int i = 0; required > 0 && i < available.size(); i++ ) {
         item = (ResourceItem) available.elementAt(i);
         no = item.getReservedAmount(this);
         if ( no > required ) {
            item.changeReservedAmount(this,no-required);
            datarec.add(item,start,required);
         }
         else { // no <= required
            item.cancelReservation(this);
            datarec.add(item,start,no);
            available.removeElementAt(i--);
         }
         nAvailable -= Math.min(no,required);
         required -= Math.min(no,required);
      }
      return datarec;
   }

   public boolean contains(ResourceItem item) {
      Core.ERROR(!isNegative,7,this);
      return available.contains(item);
   }

   protected int remove(ResourceItem item) {
      available.removeElement(item);
      int no = item.getReservedAmount(this);
      item.cancelReservation(this);
      nAvailable = nAvailable - no;
      return no;
   }

   public int getPosition() {
      Core.ERROR(record,8,this);
      return position;
   }
   public PlanRecord getRecord() {
      Core.ERROR(record,9,this);
      return record;
   }
   public String getId() {
      return fact.getId();
   }
   public Vector available() {
      return available;
   }
   public int nAvailable() {
      return isNegative ? fact.getNumber() : nAvailable;
   }
   public Fact getFact() {
      return fact;
   }
   public double getCost() {
      if ( isNegative )
         return fact.getNetCost();
      else {
         double cost = 0;
         ResourceItem item;
         Fact f;
         for(int i = 0; i < available.size(); i++ ) {
            item = (ResourceItem)available.elementAt(i);
            f = item.getFact();
            cost += f.getUnitCost() * item.getReservedAmount(this);
         }
         return cost;
      }
   }

   public Fact[] getData() {
      Fact[] out;
      if ( isNegative ) {
         out = new Fact[1];
         out[0] = fact;
      }
      else {
         Assert.notFalse(nAvailable > 0);
         ResourceItem item;
         out = new Fact[available.size()];
         for(int i = 0; i < available.size(); i++ ) {
           item = (ResourceItem)available.elementAt(i);
           out[i] = new Fact( item.getFact() );
           out[i].setNumber(item.getReservedAmount(this));
         }
      }
      return out;
   }

   public Fact mostGeneralDescriptor() {
      if ( isNegative ) return fact;

      if ( available.isEmpty() ) return fact;
      Fact f2;
      Fact f1 = new Fact( ((ResourceItem)available.elementAt(0)).getFact() );
      for(int i = 1; i < available.size(); i++) {
         f2 = ((ResourceItem)available.elementAt(i)).getFact();
         Assert.notFalse( f1.disjoin(f2) );
      }
      if ( f1.isa(OntologyDb.ENTITY) ) f1.setNumber(f1.newVar());
      return f1;
   }
}
