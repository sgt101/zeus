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

public abstract class ValueFunction {
/*
	Precedence values

	0	CONSB
	1	LVAR
	2	ELSE, IMPLY
	3	ARITH, FUNC, METH
        4       COMP, LAND, LNOT, LOR
	5	AND
	6	OR
	7	CONS, FACT?
	8	FIELD
        9
	10	VECT, BOOL, DATE, ID, INT, REAL, TYPE, TIME
*/

   static final int ID_MIN = 0;
   static final int ID_MAX = 23;


   public static final int CONSB = 0;
   public static final int LVAR  = 1;
   public static final int ID    = 2;
   public static final int TYPE  = 3;
   public static final int FIELD = 4;
   public static final int AND   = 5;
   public static final int OR    = 6;
   public static final int CONS  = 7;
   public static final int IMPLY = 8;
   public static final int ELSE  = 9;
   public static final int LOR   = 10;
   public static final int LAND  = 11;
   public static final int LNOT  = 12;
   public static final int COMP  = 13;
   public static final int INT   = 14;
   public static final int REAL  = 15;
   public static final int DATE  = 16;
   public static final int TIME  = 17;
   public static final int BOOL  = 18;
   public static final int ARITH = 19;
   public static final int FACT  = 20;
   public static final int FUNC  = 21;
   public static final int METH  = 22;
   public static final int VECT  = 23;

   static final int PD_MIN = 0;
   static final int PD_MAX = 10;

   protected int type = -1;
   protected int precedence = -1;

   public ValueFunction(int type, int precedence) {
      Assert.notFalse(type >= ID_MIN && type <= ID_MAX);
      Assert.notFalse(precedence >= PD_MIN && precedence <= PD_MAX);
      this.type = type;
      this.precedence = precedence;
   }

   public final int getID() { return type; }
   public final int getPD() { return precedence; }

   public abstract boolean isDeterminate();
   public abstract boolean references(ValueFunction var);

   public final ValueFunction unifiesWith(ValueFunction fn, Bindings b) {
      ValueFunction x, y, z;
      
      x = this.resolve(b);
      y = fn.resolve(b);

      if ( x == null || y == null ) return null;

      x = x.normalize();
      y = y.normalize();

      if ( x.equals(y) ) return y;

      z = (y.getPD() < x.getPD() ?  y.unify(x,b) : x.unify(y,b));

      if ( z == null )
         return null;

      z = z.simplify();
      b.set(x,z);
      b.set(y,z);
      return z;
   }

   public ValueFunction resolve(Bindings b) {
      return resolve(new ResolutionContext(), b);
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      return b.lookUp(this);
   }
   public Vector variables() {
      return new Vector();
   }
   public ValueFunction duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public ValueFunction duplicate(DuplicationTable table) {
      return this;
   }
   public ValueFunction evaluationFn() {
      return this;
   }
   public int baseID() {
      return type;
   }

   abstract Object        getArg(int position);
   abstract ValueFunction unify(ValueFunction fn, Bindings b);

   public abstract ValueFunction mirror();

   ValueFunction normalize()    { return this; }
   ValueFunction simplify()     { return this; }

   final Object getArg()        { return getArg(0); }

   /*
      List of methods associated with this ValueFunction type
   */

   static final String[] METHOD_LIST = {
      "isDeterminate", "0",
      "toString", "0"
   };

   static final int IS_DETERMINATE = 0;
   static final int TO_STRING      = 2;

   ValueFunction invokeMethod(String method, Vector args) {
      int arity;
      int position = Misc.whichPosition(method,METHOD_LIST);

      if ( position != -1 ) {
         arity = Integer.parseInt(METHOD_LIST[position+1]);
         if ( args.size() != arity )
            throw new IllegalArgumentException(
               "Wrong number of arguments in method \'" + method + "/" +
                arity + "\'.");
      }
      else {
         throw new IllegalArgumentException(
            "Unknown method \'" + method + "\' invoked on \'" + this +"\'");
      }

      switch( position ) {
         case IS_DETERMINATE:
              return isDeterminate() ? BoolFn.trueFn : BoolFn.falseFn;

         case TO_STRING:
              return new IdFn(toString());

         default:
              throw new IllegalArgumentException(
                 "Unknown method \'" + method + "\' invoked on \'" + this +"\'");
      }
   }
}
