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

public class ElseFn extends ValueFunction {
   protected ValueFunction[] args = new ValueFunction[3];

   public ElseFn(ValueFunction lhsArg, ValueFunction rhsArg) {
      super(ELSE,2);
      if  ( !(lhsArg instanceof ImplyFn) )
         throw new IllegalArgumentException("Illegal operand in \'" +
            lhsArg + " else " + rhsArg + "\'");

      args[0] = (ValueFunction)lhsArg.getArg(0);
      args[1] = (ValueFunction)lhsArg.getArg(1);
      args[2] = rhsArg;
   }
   
   
   public ElseFn(ValueFunction arg0, ValueFunction arg1, ValueFunction arg2) {
      super(ELSE,2);
      if  ( !(arg0 instanceof LogicalFn) )
         throw new IllegalArgumentException("Illegal operand \'" + arg0 +
            " in \' + (if " + args[0] + " then " + args[1] + " else " +
            args[2] + ")\'");
      args[0] = arg0;
      args[1] = arg1;
      args[2] = arg2;
   }


    /** 
        *resolve is here to handle unification for the postcondition to precondition maps - 
        *this will allow proper use of expressions when defining tasks. 
        *@since 1.2.1
        *@author Simon Thompson
        */
   public void resolve (String attrName, ValueFunction val) {
    debug ("in resolve"); 
    // recurse over lhs if this is a compound expression 
    if (args[0] instanceof ArithmeticFn) {
        debug ("recursing args[0]"); 
        ArithmeticFn lhsFn = (ArithmeticFn) args[0];
        lhsFn.resolve (attrName,val); 
    }
    // resolve when find a tangible
    else if (args[0] instanceof VarFn) { 
        debug ("resolving args[0]"); 
        debug (" args[0] = " + args[0].toString() + " val = " +val.toString() +" attrName = " + attrName); 
        if (args[0].toString().equals("?"+attrName)) {             
            args[0] = val; }
        debug (" args[0] = " + args[0].toString() + " val = " +val.toString() +" attrName = " + attrName); 
    }
    //recurse over rhs if this is a compound expression 
   if (args[1] instanceof ArithmeticFn) {
        debug ("recursing args[1]"); 
        ArithmeticFn thenFn = (ArithmeticFn) args[1];
        thenFn.resolve (attrName,val); 
    }
    // resolve when find a tangible
    else if (args[1] instanceof VarFn) { 
        debug ("resolving args[1]"); 
        debug (" args[1] = " + args[1].toString() + " val = " +val.toString() +" attrName = " + attrName); 
        if (args[1].toString().equals("?"+attrName)) {             
            args[1] = val; }
        debug (" args[1] = " + args[1].toString() + " val = " +val.toString() +" attrName = " + attrName); 
    }
   
    if (args[2] instanceof ArithmeticFn) {
        debug ("recursing args[2"); 
        ArithmeticFn elseFn = (ArithmeticFn) args[2];
        elseFn.resolve (attrName,val); 
    }
    // resolve when find a tangible
    else if (args[2] instanceof VarFn) { 
        debug ("resolving args[2]"); 
        debug (" args[2] = " + args[2].toString() + " val = " +val.toString() +" attrName = " + attrName); 
        if (args[2].toString().equals("?"+attrName)) {             
            args[2] = val; }
        debug (" args[2] = " + args[2].toString() + " val = " +val.toString() +" attrName = " + attrName); 
    }
}


   public String toString() {
      return "(if " + args[0] + " then " + args[1] + " else " + args[2] + ")";
   }
   
   
   ValueFunction simplify() {
      ValueFunction a, b, c;
      a = args[0].simplify();
      b = args[1].simplify();
      c = args[2].simplify();
      return (a != args[0] || b != args[1] || c != args[2] )
             ? new ElseFn(a,b,c) : this;
   }

   
   Object getArg(int position) {
      return args[position];
   }

   
   public boolean references(ValueFunction var) {
      return args[0].references(var) || args[1].references(var) ||
             args[2].references(var);
   }
   
   
   public Vector variables() {
      Vector temp = Misc.union(args[0].variables(),args[1].variables());
      return Misc.union(temp,args[2].variables());
   }
   
   
   public boolean isDeterminate() {
      return args[0].isDeterminate() && args[1].isDeterminate() &&
             args[2].isDeterminate();
   }
   
   
   ValueFunction normalize() {
      ValueFunction a, b, c;
      a = args[0].normalize();
      b = args[1].normalize();
      c = args[2].normalize();
      return (a != args[0] || b != args[1] || c != args[2] )
             ? new ElseFn(a,b,c) : this;
   }
   
   
   public ValueFunction mirror() {
      return new ElseFn(args[0].mirror(),args[1].mirror(),args[2].mirror());
   }
  
   
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      ValueFunction x = args[0].resolve(c,b);
      switch( ((LogicalFn)x).evaluate() ) {
         case LogicalFn.TRUE:
              return args[1].resolve(c,b);
         case LogicalFn.FALSE:
              return args[2].resolve(c,b);
         case LogicalFn.UNKNOWN:
              if ( x.equals(args[0]) ) 
                 return this;
              else
                 return new ElseFn(x,args[1],args[2]);
      }
      return null;
   }
   
   
   public ValueFunction evaluationFn() {
      debug (args[0].getClass().toString()); 
      switch( ((LogicalFn)args[0]).evaluate() ) {
         case LogicalFn.TRUE:
              return args[1];
         case LogicalFn.FALSE:
              return args[2];
         case LogicalFn.UNKNOWN:
              return this;
      }
      return this;
   }
   
   
   public ValueFunction duplicate(DuplicationTable table) {
      return new ElseFn(args[0].duplicate(table),args[1].duplicate(table),
                        args[2].duplicate(table));
   }
   
   
   public boolean equals(Object any) {
      if ( !(any instanceof ElseFn) ) return false;
      ElseFn fn = (ElseFn)any;
      ValueFunction a = this.simplify();
      ValueFunction b = fn.simplify();
      return ((ValueFunction)a.getArg(0)).equals((ValueFunction)b.getArg(0)) &&
             ((ValueFunction)a.getArg(1)).equals((ValueFunction)b.getArg(1)) &&
             ((ValueFunction)a.getArg(2)).equals((ValueFunction)b.getArg(2));
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

              x = args[1].unifiesWith(fn,b);
              y = args[2].unifiesWith(fn,b);

              if ( x == null && y == null )
                 return null;
              else if ( y == null )
                 return new ImplyFn(args[0],x);
              else if ( x == null )
                 return new ImplyFn(new LogicalNotFn(args[0]),y);
              else
                 return new ElseFn(args[0],x,y);

         case ELSE:
              if ( (y = ((ElseFn)fn).evaluationFn()) == null )
                 return null;
              else if ( y != fn )
                 return this.unifiesWith(y,b);

              x = args[1].unifiesWith(fn,b);
              y = args[2].unifiesWith(fn,b);

              if ( x == null && y == null )
                 return null;
              else if ( y == null )
                 return new ImplyFn(args[0],x);
              else if ( x == null ) 
                 return new ImplyFn(new LogicalNotFn(args[0]),y);
              else
                 return new ElseFn(args[0],x,y);

         default:
              x = args[1].unifiesWith(fn,b);
              y = args[2].unifiesWith(fn,b);
              if ( x == null && y == null )
                 return null;
              else if ( y == null )
                 return new ImplyFn(args[0],x);
              else if ( x == null )
                 return new ImplyFn(new LogicalNotFn(args[0]),y);
              else
                 return new ElseFn(args[0],x,y);
      }      
   }

   
   public int baseID() {
      // must be called when Fn is known to be determinate
      int x = args[1].baseID();
      int y = args[2].baseID();
      // perform type checking
      if ( x != y )
         throw new IllegalArgumentException("Incompatible types in " + this);
      return x;
   }
   
   
   public void debug (String str) { 
    //System.out.println("ArithmeticFn>>"+str);
   }

}
