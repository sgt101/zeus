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

public class ComparisonFn extends LogicalFn {
   static final int EQ = 0;
   static final int NE = 1;
   static final int LE = 2;
   static final int GE = 3;
   static final int LT = 4;
   static final int GT = 5;

   static final String[] comparators = {
      "==", "!=", "<=", ">=", "<", ">"
   };

   final static int[] illegal_operands = {
      AND, OR, ELSE, IMPLY
   };

   protected ValueFunction[] args = new ValueFunction[2];
   protected int op = -1;

   public ComparisonFn(ValueFunction lhs, ValueFunction rhs, String op) {
      super(COMP,4);
      if ( (this.op = Misc.whichPosition(op,comparators)) == -1) 
         throw new IllegalArgumentException("Unknown operator in \'" +
            lhs + " " + op + " " + rhs + "\'");
      int ltype = lhs.getID();
      int rtype = rhs.getID();
      if ( Misc.member(ltype,illegal_operands) ||
           Misc.member(rtype,illegal_operands) ) {
          throw new IllegalArgumentException("Illegal operands in " +
             " function \'" + lhs + " " + op + " " + rhs + "\'");
      }      
      if ( lhs.isDeterminate() && rhs.isDeterminate() ) {
         if ( lhs instanceof LogicalFn ) {
            if ( !(rhs instanceof LogicalFn) && !(rhs instanceof DefinedFn) )
               throw new IllegalArgumentException("Illegal operands in " +
                  " function \'" + lhs + " " + op + " " + rhs + "\'");
         }
         else if ( lhs instanceof NumericFn ) {
            if ( !(rhs instanceof NumericFn) && !(rhs instanceof DefinedFn) )
               throw new IllegalArgumentException("Illegal operands in " +
                  " function \'" + lhs + " " + op + " " + rhs + "\'");
         }
         else if ( ltype != rtype ) {
            throw new IllegalArgumentException("Illegal operands in " +
               " function \'" + lhs + " " + op + " " + rhs + "\'");
         }
      }

      args[0] = lhs;
      args[1] = rhs;
   }
   
   
   public ValueFunction mirror() {
      return new ComparisonFn(args[0].mirror(),args[1].mirror(),comparators[op]);
   }


   public String toString() {
      return "(" + args[0] + " " + comparators[op] + " " + args[1] + ")";
   }
   
   
   int getOperator() {
      return op;
   }
   
   
   ValueFunction simplify() {
      ValueFunction a, b;
      a = args[0].simplify();
      b = args[1].simplify();
      return (a != args[0] || b != args[1])
             ? new ComparisonFn(a,b,comparators[op]) : this;
   }
   
   
   Object getArg(int position) {
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
   
   
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = args[0].resolve(c,b);
      ValueFunction y = args[1].resolve(c,b);
      return (new ComparisonFn(x,y,comparators[op])).evaluationFn();
   }
   
   
   ValueFunction normalize() {
      ValueFunction a, b;
      a = args[0].normalize();
      b = args[1].normalize();
      return (a != args[0] || b != args[1] )
             ? new ComparisonFn(a,b,comparators[op]) : this;
   }


   public int evaluate() {
      ValueFunction fn = evaluationFn();
      if ( fn == this ) return UNKNOWN;
      else if ( fn == BoolFn.trueFn ) return TRUE;
      else return FALSE;
   }


   public ValueFunction evaluationFn() {
      if ( !isDeterminate() ) return this;

      ValueFunction a, b;
      a = args[0];
      b = args[1];

      if ( a instanceof LogicalFn )
         a = ((LogicalFn)a).evaluationFn();
      else if ( a instanceof ArithmeticFn)
         a = ((ArithmeticFn)a).evaluationFn();

      if ( b instanceof LogicalFn )
         b = ((LogicalFn)b).evaluationFn();
      else if ( b instanceof ArithmeticFn)
         b = ((ArithmeticFn)b).evaluationFn();

      if ( !(a instanceof PrimitiveFn) || !(b instanceof PrimitiveFn) )
         throw new IllegalArgumentException(this.toString());

      PrimitiveFn x = (PrimitiveFn)a;
      PrimitiveFn y = (PrimitiveFn)b;

      int xid = a.getID();
      int yid = b.getID();

      if ( xid != yid ) {
         if ( xid == INT  && yid == REAL )
            ; // do-nothing
         else if ( xid == REAL && yid == INT )
            ; // do-nothing
         else {
            throw new IllegalArgumentException(this.toString());
         }
      }


      switch( op ) {
         case EQ:
              return x.equals(y)
                     ? BoolFn.trueFn : BoolFn.falseFn;
         case LE:
              return ( x.less(y) || x.equals(y) ) 
                     ? BoolFn.trueFn : BoolFn.falseFn;

         case GE:
              return !x.less(y)
                     ? BoolFn.trueFn : BoolFn.falseFn;

         case NE:
              return !x.equals(y)
                     ? BoolFn.trueFn : BoolFn.falseFn;

         case GT:
              return ( !x.less(y) && !x.equals(y) )
                     ? BoolFn.trueFn : BoolFn.falseFn;
              
         case LT:
              return x.less(y)
                     ? BoolFn.trueFn : BoolFn.falseFn;
 
         default:
              throw new IllegalArgumentException("Unknown operator in \'" +
                 this + "\'");
      }
   }

   public ValueFunction duplicate(DuplicationTable table) {
      return new ComparisonFn(args[0].duplicate(table),args[1].duplicate(table),
                              comparators[op]);
   }
   public boolean equals(Object any) {
      if ( !(any instanceof ComparisonFn) ) return false;
      ComparisonFn fn = (ComparisonFn)any;
      if ( op != fn.getOperator() ) return false;

      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return ((ValueFunction)a.getArg(0)).equals((ValueFunction)b.getArg(0)) &&
             ((ValueFunction)a.getArg(1)).equals((ValueFunction)b.getArg(1));
   }
}
