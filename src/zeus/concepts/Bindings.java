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

import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.fn.*;


public class Bindings extends Hashtable {
   protected IdFn  name = null;
   protected VarFn self = null;

   public Bindings() {
    super(); 
   }

   public Bindings(String agent) {
      this.name = new IdFn(agent);
      this.self = new VarFn(Fact.SELF);
      set(self,name);
   }

   public Bindings(Bindings List) {
      set(List);
   }

   public void clear() {
      super.clear();
      if ( self !=null && name != null )
         set(self,name);
   }

   public boolean add(Bindings List) {
      BindingsRecord rec;

      for(Enumeration enum = List.elements(); enum.hasMoreElements(); ) {
         rec = (BindingsRecord) enum.nextElement();
         if ( rec.lhs.unifiesWith(rec.rhs,this) == null )
            return false;
      }
      return true;
   }

   public void set(Bindings List) {
      Object key;
      BindingsRecord rec;

      super.clear();
      for(Enumeration enum = List.keys(); enum.hasMoreElements(); ) {
         key = enum.nextElement();
         rec = (BindingsRecord) List.get(key);
         put(key,rec);
      }
   }

   public void set(ValueFunction lhs, ValueFunction rhs) {
      ValueFunction temp;

      if ( lhs.equals(rhs) ) return; // Loop check

      if ( lhs.getPD() > rhs.getPD() ) {
         temp = lhs; lhs = rhs; rhs = temp;
      }

      if ( lhs.isDeterminate() && !rhs.isDeterminate() ) {
        temp = lhs; lhs = rhs; rhs = temp;
      }

      switch( lhs.getID() ) {
         case ValueFunction.LVAR:
         case ValueFunction.FIELD:
              put(lhs.toString(), new BindingsRecord(lhs,rhs));
              break;

         default:
              put(lhs, new BindingsRecord(lhs,rhs));
              break;
      }
   }

   public ValueFunction lookUp(ValueFunction lhs) {
      BindingsRecord rec;

      while( (rec = getBindingsRecord(lhs)) != null ) {
         if ( rec.rhs.equals(lhs) ) {
            System.err.println("Bindings -- Loop found\n" + this);
            System.exit(0);
         }
         lhs = rec.rhs;
      }
      return lhs;
   }

   protected BindingsRecord getBindingsRecord(ValueFunction lhs) {
      switch( lhs.getID() ) {
         case ValueFunction.LVAR:
         case ValueFunction.FIELD:
              return (BindingsRecord)this.get(lhs.toString());

         default:
              return (BindingsRecord)this.get(lhs);
      }
   }

   public String toString() {
      String results = new String("(");
      BindingsRecord rec;

      for(Enumeration enum = this.elements(); enum.hasMoreElements(); ) {
         rec = (BindingsRecord) enum.nextElement();
         results += rec.lhs + "=" + rec.rhs + " ";
      }
      return results.trim() + ")";
   }

/*
   // Does not work with negations/conjunctions/etc

   public boolean equals(Object obj) {
      if ( !(obj instanceof Bindings) ) return false;
      Bindings b = (Bindings)obj;

      if ( b.size() != this.size() ) return false;

      BindingsRecord r1, r2;
      Object key;
      Enumeration enum = this.keys();
      boolean status = true;

      while( status && enum.hasMoreElements() ) {
         key = enum.nextElement();
         r1 = (BindingsRecord)get(key);
         r2 = (BindingsRecord)b.get(key);
         status &= r2 != null && r1.lhs.equals(r2.lhs) && r1.rhs.equals(r2.rhs);
      }
      return status;
   }
*/

}

class BindingsRecord {
   public ValueFunction lhs, rhs;

   public BindingsRecord(ValueFunction left, ValueFunction right) {
      lhs = left;
      rhs = right;
   }
}
