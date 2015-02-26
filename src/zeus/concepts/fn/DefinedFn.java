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

public class DefinedFn extends ValueFunction {
   static final int ABS = 0;
   static final int ACOS = 2;
   static final int ASIN = 4;
   static final int ATAN = 6;
   static final int ATAN2 = 8;
   static final int CEIL = 10;
   static final int COS = 12;
   static final int SIN = 14;
   static final int TAN = 16;
   static final int EXP = 18;
   static final int FLOOR = 20;
   static final int IEEE_REMAINDER = 22;
   static final int LOG = 24;
   static final int MAX = 26;
   static final int MIN = 28;
   static final int POW = 30;
   static final int RANDOM = 32;
   static final int RINT = 34;
   static final int ROUND = 36;
   static final int SQRT = 38;
   static final int TO_DEGREES = 40;
   static final int TO_RADIANS = 42;
   static final int CONCAT = 44;
   static final int NONVAR = 46;
   static final int VAR = 48;

   static final String[] FUNCTIONS = {
      "abs", "1",
      "acos", "1",
      "asin", "1",
      "atan", "1",
      "atan2", "2",
      "ceil", "1",
      "cos", "1",
      "sin", "1",
      "tan", "1",
      "exp", "1",
      "floor", "1",
      "IEEEremainder", "2",
      "log", "1",
      "max", "2",
      "min", "2",
      "pow", "2",
      "random", "0",
      "rint", "1",
      "round", "1",
      "sqrt", "1",
      "toDegrees", "1",
      "toRadians", "1",
      "concat", "2",
      "nonvar", "1",
      "var", "1"
   };

   protected Object[] args = new Object[2];

   public DefinedFn(String lhs, Vector rhs) {
      super(FUNC,3);
      args[0] = lhs;
      args[1] = rhs;

      int position = -1;
      if ( (position = Misc.whichPosition(lhs,FUNCTIONS)) == -1 ||
           ((position % 2) != 0) ) {
         throw new IllegalArgumentException("Unknown function " + lhs +
            " in expression.");
      }

      int arity = Integer.parseInt(FUNCTIONS[position+1]);
      if ( rhs == null || rhs.size() != arity ) {
         throw new IllegalArgumentException(
            "Wrong number of arguments used in function " +
            lhs + "/" + arity + ".");
      }
   }
   public ValueFunction mirror() {
      Vector List = (Vector)args[1];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.mirror();
         otherList.addElement(fn);
      }
      return new DefinedFn((String)args[0],otherList);
   }

   public String toString() {
      String s = (String)args[0] + "(";
      Vector List = (Vector)args[1];
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         s += fn;
         if ( i + 1 < List.size() )
            s += ",";
      }
      s += ")";
      return s;
   }

   ValueFunction simplify() {
      Vector List = (Vector)args[1];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.simplify();
         otherList.addElement(fn);
      }
      return new DefinedFn((String)args[0],otherList);
   }
   Object getArg(int position) {
      return args[position];
   }
   public boolean references(ValueFunction var) {
      Vector List = (Vector)args[1];
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( fn.references(var) )
            return true;
      }
      return false;
   }
   public Vector variables() {
      Vector List = (Vector)args[1];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         otherList = Misc.union(otherList,fn.variables());
      }
      return otherList;
   }
   public boolean isDeterminate() {
      Vector List = (Vector)args[1];
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( !fn.isDeterminate() ) return false;
      }
      return true;
   }
   ValueFunction normalize() {
      Vector List = (Vector)args[1];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.normalize();
         otherList.addElement(fn);
      }
      return new DefinedFn((String)args[0],otherList);
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      Vector List = (Vector)args[1];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.resolve(c,b);
         otherList.addElement(fn);
      }
      return (new DefinedFn((String)args[0],otherList)).evaluationFn();
   }

   public ValueFunction evaluationFn() {
      if ( !args[0].equals("nonvar") && !args[0].equals("var") && !isDeterminate() ) return this;

      try {
         PrimitiveNumericFn a, b;
         IdFn c, d;
         ValueFunction x;

         switch( Misc.whichPosition((String)args[0],FUNCTIONS) ) {
            case ABS:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 switch( ((ValueFunction)a).getID() ) {
                    case INT:
                         return new IntFn(Math.abs(a.longValue()));
                    case REAL:
                         return new RealFn(Math.abs(a.doubleValue()));
                 }
                 break;

            case ACOS:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.acos(a.doubleValue()));

            case ASIN:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.asin(a.doubleValue()));

            case ATAN:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.atan(a.doubleValue()));

            case ATAN2:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 b = (PrimitiveNumericFn)((Vector)args[1]).elementAt(1);
                 return new RealFn(Math.atan2(a.doubleValue(),b.doubleValue()));

            case CEIL:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.ceil(a.doubleValue()));

            case COS:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.cos(a.doubleValue()));

            case EXP:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.exp(a.doubleValue()));

            case FLOOR:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.floor(a.doubleValue()));

            case IEEE_REMAINDER:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 b = (PrimitiveNumericFn)((Vector)args[1]).elementAt(1);
                 return new RealFn(Math.IEEEremainder(a.doubleValue(),b.doubleValue()));

            case LOG:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.log(a.doubleValue()));

            case MAX:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 b = (PrimitiveNumericFn)((Vector)args[1]).elementAt(1);
                 if ( ((ValueFunction)a).getID() == INT && ((ValueFunction)b).getID() == INT )
                    return new IntFn(Math.max(a.longValue(),b.longValue()));
                 else
                    return new RealFn(Math.max(a.doubleValue(),b.doubleValue()));

            case MIN:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 b = (PrimitiveNumericFn)((Vector)args[1]).elementAt(1);
                 if ( ((ValueFunction)a).getID() == INT && ((ValueFunction)b).getID() == INT )
                    return new IntFn(Math.min(a.longValue(),b.longValue()));
                 else
                    return new RealFn(Math.min(a.doubleValue(),b.doubleValue()));

            case POW:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 b = (PrimitiveNumericFn)((Vector)args[1]).elementAt(1);
                 return new RealFn(Math.pow(a.doubleValue(),b.doubleValue()));

            case RANDOM:
                 return new RealFn(Math.random());

            case RINT:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.rint(a.doubleValue()));

            case ROUND:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new IntFn(Math.round(a.doubleValue()));

            case SIN:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.sin(a.doubleValue()));

            case SQRT:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.sqrt(a.doubleValue()));

            case TAN:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.tan(a.doubleValue()));

            case TO_DEGREES:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.toDegrees(a.doubleValue()));

            case TO_RADIANS:
                 a = (PrimitiveNumericFn)((Vector)args[1]).elementAt(0);
                 return new RealFn(Math.toRadians(a.doubleValue()));

            case CONCAT:
                 c = (IdFn)((Vector)args[1]).elementAt(0);
                 d = (IdFn)((Vector)args[1]).elementAt(1);
                 return new IdFn(c.getValue() + d.getValue());

            case NONVAR:
                 x = (ValueFunction)((Vector)args[1]).elementAt(0);
                 return BoolFn.newBoolFn(x.isDeterminate());

            case VAR:
                 x = (ValueFunction)((Vector)args[1]).elementAt(0);
                 return BoolFn.newBoolFn(!(x.isDeterminate()));

            default:
                 throw new IllegalArgumentException(
                    "Unknown function: \'" + args[0] + "\'");
         }
      }
      catch(ClassCastException e) {
         throw new IllegalArgumentException(
            "Type mismatch in function \'" + this + "\'");
      }
      return null;
   }

   public ValueFunction duplicate(DuplicationTable table) {
      Vector List = (Vector)args[1];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.duplicate(table);
         otherList.addElement(fn);
      }
      return new DefinedFn((String)args[0],otherList);
   }
   public boolean equals(Object any) {
      if ( !(any instanceof DefinedFn) ) return false;
      DefinedFn fn = (DefinedFn)any;

      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return a.getArg(0).equals(b.getArg(0)) &&
             Misc.sameVector((Vector)a.getArg(1),(Vector)b.getArg(1));

   }
   ValueFunction unify(ValueFunction fn, Bindings b)  {
      ValueFunction x = null;
      if ( (x = evaluationFn()) == this )
         return new AndFn(this,fn);
      else
         return x.unifiesWith(fn,b);
   }
}
