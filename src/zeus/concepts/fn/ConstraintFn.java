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
    note: changed some accesses for 1.2.1 so that I could infer contents for 
    precondition unification bug fixes
   */
public class ConstraintFn extends ValueFunction {
   static final int LE = 0;
   static final int LT = 1;
   static final int GE = 2;
   static final int GT = 3;
   static final int UN = 4;
   static final int NE = 5;

   public static final String[] operators = {
      "<=", "<", ">=", ">", "~", "!="
   };

   public ValueFunction arg = null;
   protected int op = -1;

   public ConstraintFn(String operator, ValueFunction arg) {
      super(CONS,7);
      op = Misc.whichPosition(operator,operators);
      if ( op == -1 )
         throw new IllegalArgumentException("Unknown operator \'" + operator +
            "\' in constraint expression");

      this.arg = arg;
      switch(op) {
         case LE:
         case GE:
         case LT:
         case GT:
              switch(arg.getID()) {
                 case ID:
                 case LVAR:
                 case FIELD:
                 case ARITH:
                 case FUNC:
                 case INT:
                 case REAL:
                 case DATE:
                 case TIME:
                 case METH:
                      break;

                 default:
                    throw new IllegalArgumentException("Illegal operand in \'" +
                       this + "\'");
              }
              break;

         case UN:
         case NE:
              break;

         default:
              throw new IllegalArgumentException("Illegal operand in \'" +
                 this + "\'");
      }
   }
   
   
   public String toString() {
      String out = operators[op] + arg;
      return out;
   }
   
   
   public int getOperator() {
      return op;
   }
   
   
   Object getArg(int position) {
      if ( position != 0 )
         throw new ArrayIndexOutOfBoundsException(position);
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
   
   
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = arg.resolve(c,b);
      return (new ConstraintFn(operators[op],x)).evaluationFn();
   }
   
   
   public ValueFunction duplicate(DuplicationTable table) {
      return new ConstraintFn(operators[op],arg.duplicate(table));
   }
   
   
   public ValueFunction mirror() {
      return new ConstraintFn(operators[op],arg.mirror());
   }
   
   
   ValueFunction normalize() {
      ValueFunction a = arg.normalize();
      switch(op) {
         case LE:
         case GE:
         case LT:
         case GT:
              switch(a.getID()) {
                 case ID:
                 case LVAR:
                 case FIELD:
                 case ARITH:
                 case FUNC:
                 case INT:
                 case REAL:
                 case DATE:
                 case TIME:
                 case METH:
                      return new ConstraintFn(operators[op],a);

                 default:
                      throw new IllegalArgumentException("Illegal operand in \'" + this + "\'");
              }

         case UN:
         case NE:
              switch(a.getID()) {
                 case ID:
                 case TYPE:
                 case FIELD:
                 case FUNC:
                 case IMPLY:
                 case ELSE:
                 case LOR:
                 case LAND:
                 case COMP:
                 case BOOL:
                 case INT:
                 case REAL:
                 case DATE:
                 case TIME:
                 case ARITH:
                 case METH:
                      return new ConstraintFn(operators[op],a);

                 case LNOT:
                      return (ValueFunction)a.getArg();

                 case CONS:
                      switch(((ConstraintFn)a).getOperator()) {
                         case LE:
                              return new ConstraintFn(operators[GT],
                                 (ValueFunction)a.getArg());
                         case GE:
                              return new ConstraintFn(operators[LT],
                                 (ValueFunction)a.getArg());
                         case LT:
                              return new ConstraintFn(operators[GE],
                                 (ValueFunction)a.getArg());
                         case GT:
                              return new ConstraintFn(operators[LE],
                                 (ValueFunction)a.getArg());

                         case NE:
                         case UN:
                              return (ValueFunction)a.getArg();

                         default:
                              throw new IllegalArgumentException("Illegal operand in \'" + this + "\'");

                      }

                 case OR:
                      ValueFunction x = (ValueFunction)a.getArg(0);
                      ValueFunction y = (ValueFunction)a.getArg(1);
                      x = (new ConstraintFn(operators[NE],x)).normalize();
                      y = (new ConstraintFn(operators[NE],y)).normalize();
                      return new AndFn(x,y);
                 case AND:
                      return new ConstraintFn(operators[op],a);

                 case LVAR:
                      return new ConstraintFn(operators[op],a);

                 default:
                      throw new IllegalArgumentException("Illegal operand in \'" + this + "\'");

              }

         default:
              throw new IllegalArgumentException("Illegal operand in \'" + this + "\'");

      }
   }
   
   
   public boolean equals(Object any) {
      if ( !(any instanceof ConstraintFn) ) return false;
      ConstraintFn fn = (ConstraintFn)any;
      return arg.equals(fn.getArg());
   }
   
   
   ValueFunction unify(ValueFunction fn, Bindings b) {

      if ( !isDeterminate() || !fn.isDeterminate() )
         return new AndFn(this,fn);

      ValueFunction u = evaluationFn();
      ValueFunction v = fn.evaluationFn();
      int x = u.baseID();
      int y = v.baseID();

      if ( x == INT || x == REAL ) {
         if ( y != INT && y != REAL )
            throw new IllegalArgumentException("Illegal operand in \'" +
               this + "\'");
      }
      else if ( x != y ) {
         throw new IllegalArgumentException("Illegal operand in \'" +
            this + "\'");
      }

      switch(op) {
         case LE:
         case GE:
         case LT:
         case GT:
              switch(v.getID()) {
                 case ID:
                 case DATE:
                 case TIME:
                 case INT:
                 case REAL:
                      return compare((ConstraintFn)u,(PrimitiveFn)v);

                 case CONS:
                      return checkRange((ConstraintFn)u,(ConstraintFn)v);

                 default:
                    throw new IllegalArgumentException("Illegal operand in \'" +
                       this + "\'");
              }

         case UN:
         case NE:
              switch(v.getID()) {
                 // The commented types should all resolve to primitives
                 // case LVAR:
                 // case FIELD:
                 // case ARITH:
                 // case IMPLY:
                 // case ELSE:
                 // case LOR:
                 // case LAND:
                 // case LNOT:
                 // case COMP:
                 // case FUNC:
                 // case METH:
                 // case CONSB:

                 case ID:
                 case TYPE:
                 case AND:
                 case OR:
                 case DATE:
                 case TIME:
                 case BOOL:
                 case INT:
                 case REAL:
                 case VECT:
                      if (((ValueFunction)u.getArg()).unifiesWith(v,b) == null)
                         return v;
                      else
                         return null;

                 case CONS:
                    switch(((ConstraintFn)v).getOperator()) {
                       case UN:
                       case NE:
                            if ( u.equals(v) )
                               return u;
                            else
                               return new AndFn(u,v);

                       default:
                          return checkRange((ConstraintFn)u,(ConstraintFn)v);
                    }
              }
              break;

         default:
              throw new IllegalArgumentException("Illegal operand in \'" +
                 this + "\'");
      }
      return null;
   }
   
   
   public int baseID() {
      // must be called when Fn is known to be determinate
      return arg.baseID();
   }



   static ValueFunction compare(ConstraintFn a, PrimitiveFn b) {
      PrimitiveFn x = (PrimitiveFn)a.getArg();
      switch(a.getOperator()) {
         case LE:
// System.err.println("Compare LE " + a + " and " + b);
              if ( b.less(x) || b.equals(x) )
                 return (ValueFunction)b;
              else
                 return null;

         case GE:
// System.err.println("Compare GE " + a + " and " + b);
              if ( x.less(b) || x.equals(b) )
                 return (ValueFunction)b;
              else
                 return null;

         case LT:
// System.err.println("Compare LT " + a + " and " + b);
              if ( b.less(x) )
                 return (ValueFunction)b;
              else
                 return null;
         case GT:
// System.err.println("Compare GT " + a + " and " + b);
              if ( x.less(b) )
                 return (ValueFunction)b;
              else
                 return null;
         default:
// System.err.println("Compare DEFAULT " + a + " and " + b);
              Assert.notNull(null);
              return null;
      }
   }


   static ValueFunction checkRange(ConstraintFn a, ConstraintFn b) {
// System.err.println("CheckRange " + a + " and " + b);
      if ( a.getOperator() > b.getOperator() )
         return checkRange(b,a);

      PrimitiveFn x = (PrimitiveFn) a.getArg();
      PrimitiveFn y = (PrimitiveFn) b.getArg();

      switch( a.getOperator() ) {
         case LE:
// System.err.println("CheckRange LE " + a + " and " + b);
              switch( b.getOperator() ) {
                 case LE:
                      return ( x.less(y) ? a : b );

                 case LT:
                      if ( x.equals(y) )
                         return null;
                      else if ( x.less(y) )
                         return a;
                      else
                         return b;

                 case GE:
                      if ( x.equals(y) )
                         return (ValueFunction)x;
                      else if ( y.less(x) )
                         return new AndFn(a,b);
                      else
                         return null;

                 case GT:
                      if ( y.less(x) )
                         return new AndFn(a,b);
                      else
                         return null;

                 case UN:
                 case NE:
                      if ( x.equals(y) )
                         return new ConstraintFn(operators[LT],(ValueFunction)x);
                      else if ( x.less(y) )
                         return a;
                      else
                         return new AndFn(a,b);
              }
              break;

         case LT:
// System.err.println("CheckRange LT " + a + " and " + b);
              switch( b.getOperator() ) {
                 // case LE: already done
                 case LT:
                      return ( x.less(y) ? a : b );

                 case GE:
                 case GT:
                      if ( y.less(x) )
                         return new AndFn(a,b);
                      else
                         return null;

                 case UN:
                 case NE:
                      if ( y.less(x) )
                         return new AndFn(a,b);
                      else
                         return a;
              }
              break;

         case GE:
// System.err.println("CheckRange GE " + a + " and " + b);
              switch( b.getOperator() ) {
                 // case LE: already done
                 // case LT: already done
                 case GE:
                      return (x.less(y) ? (ValueFunction)y : (ValueFunction)x);

                 case GT:
                      if ( x.equals(y) )
                         return null;
                      else if ( x.less(y) )
                         return b;
                      else
                         return a;

                 case UN:
                 case NE:
                      if ( x.equals(y) )
                         return new ConstraintFn(operators[GT],(ValueFunction)x);
                      else if ( x.less(y) )
                         return new AndFn(a,b);
                      else
                         return a;
              }
              break;

         case GT:
// System.err.println("CheckRange GT " + a + " and " + b);
              switch( b.getOperator() ) {
                 // case LE:
                 // case LT:
                 // case GE:
                 case GT:
                      return ( x.less(y) ? b : a );

                 case UN:
                 case NE:
                      if ( x.less(y) )
                         return new AndFn(a,b);
                      else
                         return a;
              }
              break;

         case UN:
         case NE:
// System.err.println("CheckRange NE " + a + " and " + b);
              switch( b.getOperator() ) {
                 // case LE:
                 // case LT:
                 // case GE:
                 // case GT:
                 case UN:
                 case NE:
                      return ( x.equals(y) ? (ValueFunction)a
                                           : (ValueFunction)(new AndFn(a,b)) );
              }
              break;
      }
      Assert.notNull(null); // should never get here
      return null;
   }

}
