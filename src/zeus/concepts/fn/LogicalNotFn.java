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

public class LogicalNotFn extends LogicalFn {
   protected ValueFunction arg;

   static final String[] legal_operands = {
      "zeus.concepts.fn.DefinedFn",
      "zeus.concepts.fn.MethodCallFn"
   };

   public LogicalNotFn(ValueFunction arg) throws IllegalArgumentException {
      super(LNOT,4);
      String arg_type = arg.getClass().getName();

      if ( !(arg instanceof LogicalFn) && !Misc.member(arg_type,legal_operands) )
         throw new IllegalArgumentException("Illegal operand type in \'" +
            arg + "\'");
      this.arg = arg;
   }

   public ValueFunction mirror() {
      return new LogicalNotFn(arg);
   }
   public String toString() {
      return "!" + arg;
   }
   ValueFunction simplify() {
      ValueFunction a;
      a = arg.simplify();
      return (a != arg) ? new LogicalNotFn(a) : this;
   }
   Object getArg(int position) {
      if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return arg;
   }
   public boolean references(ValueFunction var) {
      return arg.references(var);
   }
   public Vector variables() {
      return arg.variables();
   }
   public boolean isDeterminate() {
      return arg.isDeterminate();
   }
   ValueFunction normalize() {
      ValueFunction a;
      a = arg.normalize();
      return (a != arg )? new LogicalNotFn(a) : this;
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = arg.resolve(c,b);
      return (new LogicalNotFn(x)).evaluationFn();
   }

   public int evaluate() {
      ValueFunction fn = evaluationFn();
      if ( fn == this ) return UNKNOWN;
      else if ( fn == BoolFn.trueFn ) return TRUE;
      else return FALSE;
   }

   public ValueFunction evaluationFn() {
      if ( !isDeterminate() ) return this;

      BoolFn a;
      a = (BoolFn) arg.evaluationFn();

      return a.equals(BoolFn.trueFn) 
             ? BoolFn.falseFn : BoolFn.trueFn;
   }

   public ValueFunction duplicate(DuplicationTable table) {
      return new LogicalNotFn(arg.duplicate(table));
   }
   public boolean equals(Object any) {
      if ( !(any instanceof LogicalNotFn) ) return false;
      LogicalNotFn fn = (LogicalNotFn)any;
      LogicalNotFn a = (LogicalNotFn)this.simplify();
      LogicalNotFn b = (LogicalNotFn)fn.simplify();
      return ((LogicalFn)a.getArg()).equals((LogicalFn)b.getArg());
   }
}
