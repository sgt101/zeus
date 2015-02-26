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

public class VarFn extends ValueFunction {
   public String arg = null;

   public VarFn(String arg) {
      super(LVAR,1);
      this.arg = arg;
   }
   
   
   public ValueFunction mirror() {
      return new VarFn(arg);
   }
   
   
   public String toString() {
      return arg;
   }
   
   
   Object getArg(int position) {
      if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return arg;
   }
   
   
   public ValueFunction duplicate(DuplicationTable table) {
      String base = arg.substring(1);
      base = table.getRef(base);
      String result = "" + arg.charAt(0) + base;
      return new VarFn(result);
   }
   
   
   public boolean references(ValueFunction var) {
      return equals(var);
   }
   
   
   public Vector variables() {
      Vector out = new Vector();
      out.addElement(this);
      return out;
   }
   
   
   public boolean isDeterminate() {
      return false;
   }
   
   
   public boolean equals(Object any) {
      if ( !(any instanceof VarFn) ) return false;
      VarFn fn = (VarFn)any;
      return arg.equals((String)fn.getArg());
   }
   
   
   ValueFunction unify(ValueFunction fn, Bindings b) {
      return fn;
   }

}
