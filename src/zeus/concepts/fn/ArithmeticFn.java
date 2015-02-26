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

/** 
    ArithmeticFn is used to store arthimetic over some condition 
    - made some alterations for 1.2.1  so that the precondition unification 
    bugs could be properly sorted (well, for the time being at least!) 
        Si.
   */
public class ArithmeticFn extends ValueFunction implements NumericFn {
   public static final int PLUS = 0;
   public static final int MINUS = 1;
   public static final int TIMES = 2;
   public static final int DIVIDE = 3;
   public static final int REM = 4;
   public static final String[] operators = {
      "+", "-", "*", "/", "%" };

   public final static String[] legal_operands = {
      "zeus.concepts.fn.MethodCallFn",
      "zeus.concepts.fn.DefinedFn",
      "zeus.concepts.fn.ArithmeticFn",
      "zeus.concepts.fn.IntFn",
      "zeus.concepts.fn.RealFn",
      "zeus.concepts.fn.VarFn",
      "zeus.concepts.fn.FieldFn",
      "zeus.concepts.fn.ElseFn"
   };
   public final static String[] legal_unifiers = {
      "zeus.concepts.fn.MethodCallFn",
      "zeus.concepts.fn.DefinedFn",
      "zeus.concepts.fn.ArithmeticFn",
      "zeus.concepts.fn.IntFn",
      "zeus.concepts.fn.RealFn",
      "zeus.concepts.fn.VarFn",
      "zeus.concepts.fn.FieldFn",

      "zeus.concepts.fn.AndFn",
      "zeus.concepts.fn.OrFn",
      "zeus.concepts.fn.NotFn",
      "zeus.concepts.fn.ImplyFn",
      "zeus.concepts.fn.ElseFn"
   };


   protected ValueFunction[] args = new ValueFunction[2];
   protected int op = -1;


    /** 
        *resolve is here to handle unification for the postcondition to precondition maps - 
        *this will allow proper use of expressions when defining tasks. 
        *@since 1.2.1
        *@author Simon Thompson
        */
   public void resolve (String attrName, ValueFunction val) { 
    ValueFunction lhs = args[0]; 
    ValueFunction rhs = args[1]; 
    debug ("in resolve"); 
    // recurse over lhs if this is a compound expression 
    if (lhs instanceof ArithmeticFn) {
        debug ("recursing lhs"); 
        ArithmeticFn lhsFn = (ArithmeticFn) lhs;
        lhsFn.resolve (attrName,val); 
    }
    // resolve when find a tangible
    else if (lhs instanceof VarFn) { 
        debug ("resolving lhs"); 
        debug (" lhs = " + lhs.toString() + " val = " +val.toString() +" attrName = " + attrName); 
        if (lhs.toString().equals("?"+attrName)) {             
            lhs = val;
            args[0] = lhs; }
        debug (" lhs = " + lhs.toString() + " val = " +val.toString() +" attrName = " + attrName); 
    }
    //recurse over rhs if this is a compound expression 
    if (rhs instanceof ArithmeticFn) { 
        debug ("recursing rhs"); 
        ArithmeticFn rhsFn = (ArithmeticFn) rhs; 
        rhsFn.resolve (attrName,val); 
    }    
    // resolve when find rhs tangible.
    else if (rhs instanceof VarFn) { 
       debug ("resolving rhs"); 
       if (rhs.toString().equals("?"+attrName)){
              rhs = val;
              args[1] = rhs; 
       }
    }
   }
   

   public ArithmeticFn(ValueFunction lhs, ValueFunction rhs, String op) {
      super(ARITH,3);
      args[0] = lhs;
      args[1] = rhs;

      if ( (this.op = Misc.whichPosition(op,operators)) == -1 )
         throw new IllegalArgumentException("Illegal operator " + 
            " in arithmetic function \'" + lhs + op + rhs + "\'");

      String lname = lhs.getClass().getName();
      String rname = rhs.getClass().getName();
      if ( !Misc.member(lname,legal_operands) ||
           !Misc.member(rname,legal_operands) )
         throw new IllegalArgumentException("Illegal operands "  +
            " in arithmetic function \'" + lhs + op + rhs + "\'");
   }
   
   
   public ValueFunction getLHS () { 
    return args[0]; 
   }
   
   public ValueFunction getRHS () { 
    return args[1]; 
   }
   
   
  
   
   public ValueFunction mirror() {
      return new ArithmeticFn(args[0].mirror(),args[1].mirror(),operators[op]);
   }

   public String toString() {
      return "(" + args[0] + " " + operators[op] + " " + args[1] + ")";
   }
   
   public int getOperator() {
      return op;
   }
   
   
   ValueFunction simplify() {
      ValueFunction a, b;
      a = args[0].simplify();
      b = args[1].simplify();
      return (a != args[0] || b != args[1])
             ? new ArithmeticFn(a,b,operators[op]) : this;
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
   
   
   ValueFunction normalize() {
      ValueFunction a, b;
      a = args[0].normalize();
      b = args[1].normalize();
      return (a != args[0] || b != args[1] )
             ? new ArithmeticFn(a,b,operators[op]) : this;
   }
   
   
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = args[0].resolve(c,b);
      ValueFunction y = args[1].resolve(c,b);
      return (new ArithmeticFn(x,y,operators[op])).evaluationFn();
   }


   public ValueFunction evaluationFn() {
      if ( !isDeterminate() ) return this;
      ValueFunction a, b;
      double x = 0, y = 0, z = 0;
      int left_type = REAL;
      int right_type = REAL;
      int return_type = REAL;
      try {
         a = args[0].evaluationFn();
         b = args[1].evaluationFn();

         if ( a instanceof IntFn ) {
            x = (double) ((IntFn)a).getValue();
            left_type = INT;
         }
         else
            x = ((RealFn)a).getValue();

         if ( b instanceof IntFn ) {
            y = (double) ((IntFn)b).getValue();
            right_type = INT;
         }
         else
            y = ((RealFn)b).getValue();
       
         if ( left_type == REAL || right_type == REAL )
            return_type = REAL;
         else
            return_type = INT;
      }
      catch(Exception e) {
         throw new IllegalArgumentException("Unknown operand type in " +
            " arithmetic function \'" + this + "\'");
      }

      switch( op ) {
         case PLUS:
              z = x+y;
              break;
         case MINUS:
              z = x-y;
              break; 
         case TIMES:
              z = x*y;
              break;
         case DIVIDE:
              z = x/y;
              break;
         case REM:
              z = (double) (((int)x) % ((int)y));
              break;              
         default:
              throw new IllegalArgumentException("Unknown operator in " +
                 " arithmetic function \'" + this + "\'");
      }
      if (op == REM || return_type == INT ) 
         return new IntFn((int)z);
      else
         return new RealFn(z);
   }


   public ValueFunction duplicate(DuplicationTable table) {
      return new ArithmeticFn(args[0].duplicate(table),args[1].duplicate(table),
                              operators[op]);
   }
   
   
   public boolean equals(Object any) {
      if ( !(any instanceof ArithmeticFn) ) return false;
      ArithmeticFn fn = (ArithmeticFn)any;

      if ( op != ((ArithmeticFn)fn).getOperator() ) return false;

      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return ((ValueFunction)a.getArg(0)).equals((ValueFunction)b.getArg(0)) &&
             ((ValueFunction)a.getArg(1)).equals((ValueFunction)b.getArg(1));
   }
   
   
   ValueFunction unify(ValueFunction fn, Bindings b)  {
      String name = fn.getClass().getName();
      if ( !Misc.member(name,legal_unifiers) ) {
         throw new IllegalArgumentException("Illegal unification attempted: " +
            this + " and " + fn );
      }      

      ValueFunction x = null;

      if ( (x = evaluationFn()) == this )
         return new AndFn(this,fn);
      else
         return x.unifiesWith(fn,b);
   }
   
   
   public void debug (String str) { 
   // System.out.println("ArithmeticFn>>"+str);
   }


}
