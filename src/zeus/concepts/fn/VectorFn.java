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

public class VectorFn extends ValueFunction {
   protected Object args = null;

   public VectorFn(Vector items) {
      super(VECT,10);
      args = items;
   }

   public ValueFunction mirror() {
      Vector List = (Vector)args;
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.mirror();
         otherList.addElement(fn);
      }
      return new VectorFn(otherList);
   }

   public String toString() {
      String s = "{";
      Vector List = (Vector)args;
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         s += fn;
         if ( i + 1 < List.size() )
            s += ",";
      }
      s += "}";
      return s;
   }

   ValueFunction simplify() {
      Vector List = (Vector)args;
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.simplify();
         otherList.addElement(fn);
      }
      return new VectorFn(otherList);
   }
   Object getArg(int position) {
      if ( position != 0 ) 
         throw new ArrayIndexOutOfBoundsException(position);
      return args;
   }
   public boolean references(ValueFunction var) {
      Vector List = (Vector)args;
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( fn.references(var) )
            return true;
      }
      return false;
   }
   public Vector variables() {
      Vector List = (Vector)args;
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         otherList = Misc.union(otherList,fn.variables());
      }
      return otherList;
   }
   public boolean isDeterminate() {
      Vector List = (Vector)args;
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         if ( !fn.isDeterminate() ) return false;
      }
      return true;
   }
   ValueFunction normalize() {
      Vector List = (Vector)args;
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.normalize();
         otherList.addElement(fn);
      }
      return new VectorFn(otherList);
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      Vector List = (Vector)args;
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.resolve(c,b);
         otherList.addElement(fn);
      }
      VectorFn m =  new VectorFn(otherList);
      return m.evaluationFn();
   }

   public ValueFunction duplicate(DuplicationTable table) {
      Vector List = (Vector)args;
      Vector otherList = new Vector();
      ValueFunction fn;
      for(int i = 0; i < List.size(); i++ ) {
         fn = (ValueFunction)List.elementAt(i);
         fn = fn.duplicate(table);
         otherList.addElement(fn);
      }
      return new VectorFn(otherList);
   }
   public boolean equals(Object any) {
      if ( !(any instanceof VectorFn) ) return false;
      VectorFn fn = (VectorFn)any;

      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return Misc.sameVector((Vector)a.getArg(0),(Vector)b.getArg(0));
   }

   ValueFunction unify(ValueFunction fn, Bindings b) {
      Vector thisVector, otherVector, resultVector;
      ValueFunction x, y, z;

      switch( fn.getID() ) {
         case VECT:
              thisVector = (Vector)getArg();
              otherVector = (Vector)fn.getArg();
              if ( thisVector.size() != otherVector.size() ) return null;

              Bindings b1 = new Bindings(b);
              resultVector = new Vector();

              for(int i = 0; i < thisVector.size(); i++ ) {
                 x = (ValueFunction)thisVector.elementAt(i);
                 y = (ValueFunction)otherVector.elementAt(i);
                 if ( (z = x.unifiesWith(y,b1)) == null )
                    return null;
                 resultVector.addElement(z);
              }
              b.add(b1);
              return new VectorFn(resultVector);

         default:
              throw new IllegalArgumentException("Unification type clash" +
                 " between \'" + this + "\' and \'" + fn + "\'");
      }
   }


   /*
      List of methods associated with this ValueFunction type
   */

   static final String[] METHOD_LIST = {
      /* ValueFunction */ "elementAt",            "1",   /* (int index) */
      /* BoolFn        */ "setElementAt",         "2",   /* (ValueFunction obj, int index) */
      /* BoolFn        */ "insertElementAt",      "2",   /* (ValueFunction obj, int index) */
      /* BoolFn        */ "addElement",           "1",   /* (ValueFunction obj) */
      /* BoolFn        */ "removeElement",        "1",   /* (ValueFunction obj) */
      /* BoolFn        */ "removeElementAt",      "1",   /* (int index) */
      /* BoolFn        */ "removeAllElements",    "0",   /* () */
      /* BoolFn        */ "containsElement",      "1",   /* (ValueFunction obj) */
      /* BoolFn        */ "isEmpty",              "0",   /* () */
      /* IntFn         */ "size",                 "0",   /* () */
      /* ValueFunction */ "firstElement",         "0",   /* () */
      /* ValueFunction */ "lastElement",          "0",   /* () */
      /* VectorFn      */ "union",                "1",   /* (VectorFn obj) */
      /* VectorFn      */ "intersection",         "1",   /* (VectorFn obj) */
      /* VectorFn      */ "difference",           "1",   /* (VectorFn obj) */
   };

   static final int ELEMENT_AT 		= 0;
   static final int SET_ELEMENT_AT 	= 2;
   static final int INSERT_ELEMENT_AT	= 4;
   static final int ADD_ELEMENT 	= 6;
   static final int REMOVE_ELEMENT 	= 8;
   static final int REMOVE_ELEMENT_AT 	= 10;
   static final int REMOVE_ALL_ELEMENTS = 12;
   static final int CONTAINS_ELEMENT 	= 14;
   static final int IS_EMPTY 		= 16;
   static final int SIZE 		= 18;
   static final int FIRST_ELEMENT 	= 20;
   static final int LAST_ELEMENT 	= 22;
   static final int UNION 		= 24;
   static final int INTERSECTION 	= 26;
   static final int DIFFERENCE 		= 28;

   ValueFunction invokeMethod(String method, Vector arguments) {
      int position = Misc.whichPosition(method,METHOD_LIST);

      if ( position == -1 )
         return super.invokeMethod(method,arguments);

      StringTokenizer st = new StringTokenizer(METHOD_LIST[position+1],",");
      boolean num_args_ok = false;
      int arity = -1;
      while( !num_args_ok && st.hasMoreTokens() ) {
         arity = Integer.parseInt(st.nextToken());
         num_args_ok = (arguments.size() == arity);
      }

      if ( !num_args_ok )
         throw new IllegalArgumentException(
            "Wrong number of arguments in method VectorFn.\'" + method + "/" +
             arity + "\' --> " + this);

       try {

         IntFn index;
         ValueFunction value;
         VectorFn vn;
         Vector otherData;

         Vector data = (Vector)args;

         switch( position ) {
            case ELEMENT_AT:
                 index = (IntFn)arguments.elementAt(0);
                 return (ValueFunction)data.elementAt(index.intValue());

            case SET_ELEMENT_AT:
                 value = (ValueFunction)arguments.elementAt(0);
                 index = (IntFn)arguments.elementAt(1);
                 data.setElementAt(value,index.intValue());
                 return BoolFn.trueFn;

            case INSERT_ELEMENT_AT:
                 value = (ValueFunction)arguments.elementAt(0);
                 index = (IntFn)arguments.elementAt(1);
                 data.insertElementAt(value,index.intValue());
                 return BoolFn.trueFn;

            case ADD_ELEMENT:
                 value = (ValueFunction)arguments.elementAt(0);
                 data.addElement(value);
                 return BoolFn.trueFn;

            case REMOVE_ELEMENT:
                 value = (ValueFunction)arguments.elementAt(0);
                 return BoolFn.newBoolFn(data.removeElement(value));

            case REMOVE_ELEMENT_AT:
                 index = (IntFn)arguments.elementAt(0);
                 value = (ValueFunction)data.elementAt(index.intValue());
                 data.removeElementAt(index.intValue());
                 return value;

            case REMOVE_ALL_ELEMENTS:
                 data.removeAllElements();
                 return BoolFn.trueFn;

            case CONTAINS_ELEMENT:
                 value = (ValueFunction)arguments.elementAt(0);
                 return BoolFn.newBoolFn(data.contains(value));

            case IS_EMPTY:
                 return BoolFn.newBoolFn(data.isEmpty());

	    case SIZE:
                 return new IntFn(data.size());

            case FIRST_ELEMENT:
                 return (ValueFunction)data.firstElement();

            case LAST_ELEMENT:
                 return (ValueFunction)data.lastElement();

	    case UNION:
                 vn = (VectorFn)arguments.elementAt(0);
                 otherData = (Vector)vn.getArg();
                 return new VectorFn(Misc.union(data,otherData));

	    case INTERSECTION:
                 vn = (VectorFn)arguments.elementAt(0);
                 otherData = (Vector)vn.getArg();
                 return new VectorFn(Misc.intersection(data,otherData));

	    case DIFFERENCE:
                 vn = (VectorFn)arguments.elementAt(0);
                 otherData = (Vector)vn.getArg();
                 return new VectorFn(Misc.difference(data,otherData));
         }
      }
      catch(ClassCastException e) {
         throw new IllegalArgumentException(
            "Type mismatch in method \'" + this + Fact.A_CHR + method +
            "(...)\'");

      }
      Core.ERROR(null,1,this); // should never get here
      return null;
   }
}
