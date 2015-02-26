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

public abstract class LogicalFn extends ValueFunction {
   public static final int TRUE = 0;
   public static final int FALSE = 1;
   public static final int UNKNOWN = 2;

   public LogicalFn(int type, int precedence) {
      super(type,precedence);
   }
   public LogicalFn(int type) {
      super(type,4);
   }

   public abstract int evaluate();

   public int baseID() {
      return BOOL;
   }

   ValueFunction unify(ValueFunction fn, Bindings b) {
      int x;

      switch( fn.getID() ) {
         case ID:
         case TYPE:
         case INT:
         case REAL:
         case DATE:
         case TIME:
         case ARITH:
         case VECT:
              break;

         // These have higher unification precedence
         // case LVAR:
         // case IMPLY:
         // case ELSE:
         // case CONSB:
         // case FUNC:
         // case METH:

         case FIELD:
         case AND:
         case OR:
              return new AndFn(evaluationFn(),fn);

         case CONS:
              if ( fn.isDeterminate() ) {
                 if (fn.baseID() != BOOL)
                    break;

                 switch( evaluate() ) {
                    case TRUE:
                    case FALSE:
                         return ConstraintFn.compare((ConstraintFn)fn,
                                  (PrimitiveFn)evaluationFn());
                    case UNKNOWN:
                         return new AndFn(evaluationFn(),fn);
                 }
              }
              else
                 return new AndFn(evaluationFn(),fn);

         case LOR:
         case LAND:
         case LNOT:
         case COMP:
         case BOOL:
              x = ((LogicalFn)fn).evaluate();
              switch( evaluate() ) {
                 case TRUE:
                      switch( x ) {
                         case TRUE:
                              return evaluationFn();
                         case FALSE:
                              return null;
                         case UNKNOWN:
                              return new AndFn(evaluationFn(),fn);
                      }
                      break;

                 case FALSE:
                      switch( x ) {
                         case TRUE:
                              return null;
                         case FALSE:
                              return evaluationFn();
                         case UNKNOWN:
                              return new AndFn(evaluationFn(),fn);
                      }
                      break;

                 case UNKNOWN:
                      return new AndFn(this,((LogicalFn)fn).evaluationFn());
              }
              break;

         default:
              break;
      }
      throw new IllegalArgumentException("Unification type clash " +
         " attempting to unify \'" + fn + "\' with \'" + this + "\'");
   }
}
