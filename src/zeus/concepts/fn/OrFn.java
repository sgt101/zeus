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

public class OrFn extends ValueFunction {
   protected ValueFunction[] args = new ValueFunction[2];

   public OrFn(ValueFunction lhsArg, ValueFunction rhsArg) {
      super(OR,6);
      args[0] = lhsArg;
      args[1] = rhsArg;
   }
   public String toString() {
      String out = "(" + args[0] + "|" + args[1] + ")";
      return out;
   }
   ValueFunction simplify() {
      Hashtable cache = new Hashtable();
      Vector data = new Vector();
      this.expand(data,cache);
      Misc.sort(data);
      return concatenate(data,cache);
   }
   protected ValueFunction concatenate(Vector data, Hashtable cache) {
      return concatenate(data,0,cache);
   }
   protected ValueFunction concatenate(Vector data, int i, Hashtable cache) {
      if ( i + 1 == data.size() )
         return (ValueFunction)cache.get(data.elementAt(i));
      else if ( i + 2 == data.size() )
         return new OrFn((ValueFunction)cache.get(data.elementAt(i)),
                         (ValueFunction)cache.get(data.elementAt(i+1)));
      else
         return new OrFn((ValueFunction)cache.get(data.elementAt(i)),
                         concatenate(data,i+1,cache));
   }
   protected void expand(Vector data, Hashtable cache) {
      for( int i = 0; i < args.length; i++ ) {
         if ( args[i].getID() == OR )
            ((OrFn)args[i]).expand(data,cache);
         else {
            ValueFunction fn = args[i].simplify();
            String s = fn.toString();
            if ( !data.contains(s) ) {
               data.addElement(s);
               cache.put(s,fn);
            }
         }
      }
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
   public ValueFunction duplicate(DuplicationTable table) {
      return new OrFn(args[0].duplicate(table),args[1].duplicate(table));
   }
   ValueFunction normalize() {
      return new OrFn(args[0].normalize(),args[1].normalize());
   }
   public ValueFunction mirror() {
      return new OrFn(args[0].mirror(),args[1].mirror());
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = args[0].resolve(c,b);
      ValueFunction y = args[1].resolve(c,b);
      return (new OrFn(x,y)).evaluationFn();
   }
   public boolean equals(Object any) {
      if ( !(any instanceof OrFn) ) return false;
      OrFn fn = (OrFn)any;
      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return ((ValueFunction)a.getArg(0)).equals((ValueFunction)b.getArg(0)) &&
             ((ValueFunction)a.getArg(1)).equals((ValueFunction)b.getArg(1));
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      ValueFunction x = null, y = null;

      if ( (x = args[0].unifiesWith(fn,b)) != null ) {
         if ( (y = args[1].unifiesWith(fn,b)) != null ) {
            if ( x.equals(y) ) return x;
            else               return new OrFn(x,y);
         }
         else
            return x;
      }
      else if ( (y = args[1].unifiesWith(fn,b)) != null ) {
         return y;
      }
      return null;
   }
   public int baseID() {
      // must be called when Fn is known to be determinate
      int x = args[0].baseID();
      int y = args[1].baseID();
      // perform type checking
      if ( x != y )
         throw new IllegalArgumentException("Incompatible types in " + this);
      return x;

   }
}
