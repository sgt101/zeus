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

public class BoolFn extends LogicalFn implements PrimitiveFn {
   protected boolean arg;
   public static final BoolFn trueFn = new BoolFn("true");
   public static final BoolFn falseFn = new BoolFn("false");

   public static final BoolFn newBoolFn(boolean arg) {
      return ( arg ) ? trueFn : falseFn;
   }
   public static final BoolFn newBoolFn(String arg) {
      if ( arg.equals("true") ) return trueFn;
      else if ( arg.equals("false") ) return falseFn;
      else 
         throw new IllegalArgumentException("Unknown operand \'" + arg +
            "\' in boolean function");
   }

   public ValueFunction mirror() {
      return new BoolFn(toString());
   }
   
   private BoolFn(String arg) {
      super(BOOL,10);
      this.arg = (Boolean.valueOf(arg)).booleanValue();
   }
   public String toString() {
      return (new Boolean(arg)).toString();
   }
   Object getArg(int position) {
      if ( position != 0 )
         throw new ArrayIndexOutOfBoundsException(position);
      return new Boolean(arg);
   }
   public boolean getValue() {
      return arg;
   }
   public boolean isDeterminate() {
      return true;
   }
   public int evaluate() {
      return  arg ? TRUE : FALSE;
   }      
   public boolean equals(Object any) {
      if ( !(any instanceof BoolFn) ) return false;
      BoolFn fn = (BoolFn)any;
      return arg == fn.getValue();
   }
   public boolean less(Object fn) {
      return false;
   }
   public boolean references(ValueFunction var) {
      return false;
   }
}
