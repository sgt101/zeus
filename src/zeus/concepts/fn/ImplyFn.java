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

public class ImplyFn extends ValueFunction {
   protected ValueFunction[] args = new ValueFunction[2];

   public ImplyFn(ValueFunction lhsArg, ValueFunction rhsArg) {
      super(IMPLY,2);
      if ( !(lhsArg instanceof LogicalFn) )
         throw new IllegalArgumentException("Illegal argument \'" + lhsArg +
            "\' in function \'(if " + lhsArg + " then " + rhsArg + ")\'");

      args[0] = lhsArg;
      args[1] = rhsArg;
   }
   public String toString() {
      String out = "(if " + args[0] + " then " + args[1] + ")";
      return out;
   }
   ValueFunction simplify() {
      ValueFunction a, b;
      a = args[0].simplify();
      b = args[1].simplify();
      return (a != args[0] || b != args[1]) ? new ImplyFn(a,b) : this;
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
      return new ImplyFn(args[0].normalize(),args[1].normalize());
   }
   public ValueFunction mirror() {
      return new ImplyFn(args[0].mirror(),args[1].mirror());
   }
   public ValueFunction resolve(ResolutionContext c, Bindings bb) {
      ValueFunction a = args[0].resolve(c,bb);
      ValueFunction b = args[1].resolve(c,bb);
      return (new ImplyFn(a,b)).evaluationFn();
   }
   public ValueFunction evaluationFn() {
      switch( ((LogicalFn)args[0]).evaluate() ) {
         case LogicalFn.TRUE:
              return args[1];
         case LogicalFn.FALSE:
              return null;
         case LogicalFn.UNKNOWN:
              return this;
      }
      return this;
   }
   public ValueFunction duplicate(DuplicationTable table) {
      return new ImplyFn(args[0].duplicate(table),args[1].duplicate(table));
   }
   public boolean equals(Object any) {
      if ( !(any instanceof ImplyFn) ) return false;
      ImplyFn fn = (ImplyFn)any;
      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return ((ValueFunction)a.getArg(0)).equals((ValueFunction)b.getArg(0)) &&
             ((ValueFunction)a.getArg(1)).equals((ValueFunction)b.getArg(1));
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      ValueFunction x = null, y = null;

      if ( (x = evaluationFn()) == null )
         return null;
      else if ( x != this )
         return x.unifiesWith(fn,b);

      switch( fn.getID() ) {
         case IMPLY:
              if ( (y = ((ImplyFn)fn).evaluationFn()) == null )
                 return null;
              else if ( y != fn )
                 return this.unifiesWith(y,b);

              if ( (x = args[1].unifiesWith(fn,b)) == null )
                 return null;
              else
                 return new ImplyFn(args[0],x);

         case ELSE:
              if ( (y = ((ElseFn)fn).evaluationFn()) == null )
                 return null;
              else if ( y != fn )
                 return this.unifiesWith(y,b);

              if ( (x = args[1].unifiesWith(fn,b)) == null )
                 return null;
              else
                 return new ImplyFn(args[0],x);

         default:
              if ( (x = args[1].unifiesWith(fn,b)) == null )
                 return null;
              else
                 return new ImplyFn(args[0],x);
      }
      
   }
   public int baseID() {
      // must be called when Fn is known to be determinate
      return args[1].baseID();
   }
}
