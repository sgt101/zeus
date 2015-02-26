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

public class MethodCallFn extends ValueFunction {
   protected Object[] args = new Object[3];

   public MethodCallFn(String lhs, Vector rhs) {
      super(METH,3);

      int index = lhs.lastIndexOf(Fact.A_CHR);
      String method = lhs.substring(index+1);
      String object = lhs.substring(0,index);

      if ( object.indexOf(Fact.A_CHR) != -1 ) 
         args[0] = new FieldFn(object);
      else if ( object.charAt(0) == Fact.V_CHR )
         args[0] = new VarFn(object);
      else
         args[0] = new TypeFn(object);

      args[1] = method;
      args[2] = rhs;
   }

   protected MethodCallFn(ValueFunction arg0, String arg1, Vector arg2) {
      super(METH,3);
      args[0] = arg0;
      args[1] = arg1;
      args[2] = arg2;
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
      return new MethodCallFn(((ValueFunction)args[0]).mirror(),
                              (String)args[1],otherList);
   }

   public String toString() {
      String s = args[0].toString() + Fact.A_STR + args[1] + "(";
      Vector List = (Vector)args[2];
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
      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.simplify();
         otherList.addElement(fn);
      }
      return new MethodCallFn(((ValueFunction)args[0]).simplify(),
                              (String)args[1],otherList);
   }
   Object getArg(int position) {
      return args[position];
   }
   public boolean references(ValueFunction var) {
      if ( ((ValueFunction)args[0]).references(var) ) return true;
 
      Vector List = (Vector)args[2];
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( fn.references(var) )
            return true;
      }
      return false;
   }
   public Vector variables() {
      Vector firstList = ((ValueFunction)args[0]).variables();

      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         otherList = Misc.union(otherList,fn.variables());
      }
      return Misc.union(firstList,otherList);
   }
   public boolean isDeterminate() {
      if ( !((ValueFunction)args[0]).isDeterminate() ) return false;

      Vector List = (Vector)args[2];
      ValueFunction fn;
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
      return new MethodCallFn(((ValueFunction)args[0]).normalize(),
                              (String)args[1],otherList);
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.resolve(c,b);
         otherList.addElement(fn);
      }
      MethodCallFn m =  new MethodCallFn(((ValueFunction)args[0]).resolve(c,b),
                                         (String)args[1],otherList);
      return m.evaluationFn();
   }

   public ValueFunction evaluationFn() {
      if ( !args[1].equals("isDeterminate") && !isDeterminate() ) return this;

      return ((ValueFunction)args[0]).invokeMethod(
                (String)args[1],(Vector)args[2]);
   }

   public ValueFunction duplicate(DuplicationTable table) {
      Vector List = (Vector)args[2];
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.duplicate(table);
         otherList.addElement(fn);
      }
      return new MethodCallFn(((ValueFunction)args[0]).duplicate(table),
                              (String)args[1],otherList);
   }
   public boolean equals(Object any) {
      if ( !(any instanceof MethodCallFn) ) return false;
      MethodCallFn fn = (MethodCallFn)any;

      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return a.getArg(0).equals(b.getArg(0)) &&
             a.getArg(1).equals(b.getArg(1)) &&
             Misc.sameVector((Vector)a.getArg(2),(Vector)b.getArg(2));
   }
   ValueFunction unify(ValueFunction fn, Bindings b)  {
      ValueFunction x = null;
      if ( (x = evaluationFn()) == this )
         return new AndFn(this,fn);
      else
         return x.unifiesWith(fn,b);
   }
}
