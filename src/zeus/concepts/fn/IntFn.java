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



package zeus.concepts.fn;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

public class IntFn extends ValueFunction 
                   implements PrimitiveFn, PrimitiveNumericFn {

   protected long arg;

   public IntFn(long arg) {
      super(INT,10);
      this.arg = arg;
   }
   public IntFn(int arg) {
      super(INT,10);
      this.arg = (long)arg;
   }
   public IntFn(String arg) {
      super(INT,10);
      this.arg = (Long.valueOf(arg)).longValue();
   }
   public ValueFunction mirror() {
      return new IntFn(arg);
   }
   public String toString() {
      return Long.toString(arg);
   }
   Object getArg(int position) {
      if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return new Long(arg);
   }
   public int getValue() {
      return (int)arg;
   }
   public long longValue() {
      return arg;
   }
   public double doubleValue() {
      return (double)arg;
   }
   public int intValue() {
      return (int)arg;
   }
   public boolean references(ValueFunction var) {
      return false;
   }
   public boolean isDeterminate() {
      return true;
   }
   public boolean equals(Object any) {
      if ( !(any instanceof ValueFunction) ) return false;
      ValueFunction fn = (ValueFunction)any;
      switch( fn.getID() ) {
         case INT:
              return arg == ((IntFn)fn).getValue();
         case REAL:
              return arg == ((RealFn)fn).getValue();
       }
       return false;
   }
   public boolean less(Object any) {
      if ( !(any instanceof ValueFunction) ) return false;
      ValueFunction fn = (ValueFunction)any;
      switch( fn.getID() ) {
         case INT:
              return arg < ((IntFn)fn).getValue();
         case REAL:
              return arg < ((RealFn)fn).getValue();
       }
       return false;
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      return null;
   }
}
