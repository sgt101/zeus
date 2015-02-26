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

public class ConstraintBFn extends ValueFunction {
   static final int NONVAR = 0;
   static final int VAR    = 3;

   static final String[] FUNCTIONS = {
      "nonvar", "0", "false",
      "var"   , "0", "false"
   };

   protected Object[] args = new Object[3];
   protected int position = -1;

   public ConstraintBFn(ValueFunction fn, String lhs, Vector rhs) {
      super(CONSB,0);
      args[0] = fn;
      args[1] = lhs;
      args[2] = rhs;

      if ( (position = Misc.whichPosition(lhs,FUNCTIONS)) == -1 ||
           ((position % 3) != 0) ) {
         throw new IllegalArgumentException("Unknown operator " + lhs +
            " in constraint expression.");
      }

      int arity = Integer.parseInt(FUNCTIONS[position+1]);
      if ( rhs == null || rhs.size() != arity ) {
         throw new IllegalArgumentException(
            "Wrong number of arguments used in constraint expression " +
            lhs + "/" + arity + ".");
      }
   }
   public ValueFunction mirror() {
      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.mirror();
         otherList.addElement(fn);
      }
      fn = ((ValueFunction)args[0]).mirror();
      return new ConstraintBFn(fn,(String)args[1],otherList);
   }

   public String toString() {
      String s = args[0].toString() + "::" + (String)args[1] + "(";
      Vector List = (Vector)args[2];
      for(int i = 0; i < List.size(); i++ ) {
         s += List.elementAt(i);
         if ( i + 1 < List.size() )
            s += ",";
      }
      s += ")";
      return s;
   }

   ValueFunction simplify() {
      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.simplify();
         otherList.addElement(fn);
      }
      fn = ((ValueFunction)args[0]).simplify();
      return new ConstraintBFn(fn,(String)args[1],otherList);
   }
   Object getArg(int position) {
      return args[position];
   }
   public boolean references(ValueFunction var) {
      ValueFunction fn = (ValueFunction)args[0];
      if ( fn.references(var) ) return true;

      Vector List = (Vector)args[2];
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( fn.references(var) )
            return true;
      }
      return false;
   }
   public Vector variables() {
      ValueFunction fn = (ValueFunction)args[0];
      Vector otherList = fn.variables();

      Vector List = (Vector)args[2];
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         otherList = Misc.union(otherList,fn.variables());
      }
      return otherList;
   }
   public boolean isDeterminate() {
      ValueFunction fn = (ValueFunction)args[0];
      if ( !fn.isDeterminate() ) return false;

      Vector List = (Vector)args[2];
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( !fn.isDeterminate() ) return false;
      }
      return true;
   }
   ValueFunction normalize() {
      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.normalize();
         otherList.addElement(fn);
      }
      fn = ((ValueFunction)args[0]).normalize();
      return new ConstraintBFn(fn,(String)args[1],otherList);
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction a = ((ValueFunction)args[0]).resolve(c,b);

      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.resolve(c,b);
         otherList.addElement(fn);
      }
      return (new ConstraintBFn(a,(String)args[1],otherList)).evaluationFn();
   }

   protected final boolean onlyEvaluateIfDeterminate() {
      return (Boolean.valueOf(FUNCTIONS[position+2])).booleanValue();
   }

   ValueFunction unify(ValueFunction fn, Bindings b) {
      if ( onlyEvaluateIfDeterminate() &&
           (!isDeterminate() || !fn.isDeterminate()) )
         return new AndFn(this,fn);

      switch(position) {
         case NONVAR:
              if ( !fn.isDeterminate() ) return null;
              break;

         case VAR:
              if ( fn.isDeterminate() ) return null;
              break;

         default:
              throw new IllegalArgumentException(
                 "Unknown operator in: \'" + this + "\'");
      }

      ValueFunction x = (ValueFunction)args[0];
      return x.unifiesWith(fn,b);
   }
   public ValueFunction duplicate(DuplicationTable table) {
      ValueFunction a = ((ValueFunction)args[0]).duplicate(table);

      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.duplicate(table);
         otherList.addElement(fn);
      }
      return new ConstraintBFn(a,(String)args[1],otherList);
   }
   public boolean equals(Object any) {
      if ( !(any instanceof ConstraintBFn) ) return false;
      ConstraintBFn fn = (ConstraintBFn)any;

      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return a.getArg(0).equals(b.getArg(0)) &&
             a.getArg(1).equals(b.getArg(1)) &&
             Misc.sameVector((Vector)a.getArg(2),(Vector)b.getArg(2));

   }
}
