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

public class LogicalOrFn extends LogicalFn {
   protected ValueFunction[] args = new ValueFunction[2];

   static final String[] legal_operands = {
      "zeus.concepts.fn.DefinedFn",
      "zeus.concepts.fn.MethodCallFn"
   };

   public LogicalOrFn(ValueFunction lhs, ValueFunction rhs) 
      throws IllegalArgumentException {

      super(LOR,4);
      String lhs_type = lhs.getClass().getName();
      String rhs_type = rhs.getClass().getName();

      if ( (!(lhs instanceof LogicalFn) && !Misc.member(lhs_type,legal_operands)) ||
           (!(rhs instanceof LogicalFn) && !Misc.member(rhs_type,legal_operands)) )
         throw new IllegalArgumentException("Illegal operand type in function  \'" +
                                            lhs + " || " + rhs + "\'");
      args[0] = lhs;
      args[1] = rhs;
   }
   public ValueFunction mirror() {
      return new LogicalOrFn(args[0].mirror(),args[1].mirror());
   }

   public String toString() {
      return "(" + args[0] + " || " + args[1] + ")";
   }
   ValueFunction simplify() {
      ValueFunction a, b;
      a = args[0].simplify();
      b = args[1].simplify();
      return (a != args[0] || b != args[1])
             ? new LogicalOrFn(a,b) : this;
   }
   Object getArg(int position) {
      Assert.notFalse(position >= 0 && position <= args.length);
      return args[position];
   }
   public boolean references(ValueFunction var) {
      return args[0].references(var) || args[1].references(var);
   }
   public Vector variables() {
      return Misc.union(args[0].variables(),args[1].variables());
   }
   public boolean isDeterminate() {
      return args[0].isDeterminate() && args[1].isDeterminate();
   }
   ValueFunction normalize() {
      ValueFunction a, b;
      a = args[0].normalize();
      b = args[1].normalize();
      return (a != args[0] || b != args[1] )
             ? new LogicalOrFn(a,b) : this;
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = args[0].resolve(c,b);
      ValueFunction y = args[1].resolve(c,b);
      return (new LogicalOrFn(x,y)).evaluationFn();
   }

   public int evaluate() {
      ValueFunction fn = evaluationFn();
      if ( fn == this ) return UNKNOWN;
      else if ( fn == BoolFn.trueFn ) return TRUE;
      else return FALSE;
   }

   public ValueFunction evaluationFn() {
      if ( !isDeterminate() ) return this;

      BoolFn a, b;
      a = (BoolFn) args[0].evaluationFn();
      b = (BoolFn) args[1].evaluationFn();

      return a.equals(BoolFn.trueFn) || b.equals(BoolFn.trueFn)
             ? BoolFn.trueFn : BoolFn.falseFn;
   }

   public ValueFunction duplicate(DuplicationTable table) {
      return new LogicalOrFn(args[0].duplicate(table),args[1].duplicate(table));
   }
   public boolean equals(Object any) {
      if ( !(any instanceof LogicalOrFn) ) return false;
      LogicalOrFn fn = (LogicalOrFn)any;
      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return ((ValueFunction)a.getArg(0)).equals((ValueFunction)b.getArg(0)) &&
             ((ValueFunction)a.getArg(1)).equals((ValueFunction)b.getArg(1));
   }
}
