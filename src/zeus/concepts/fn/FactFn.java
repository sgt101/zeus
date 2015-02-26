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

public class FactFn extends ValueFunction {
   protected Fact arg = null;

   public FactFn(Fact arg) {
      super(FACT,7);
      this.arg = arg;
   }
   public String toString() {
      return arg.toString();
   }
   Object getArg(int position) {
      if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return arg;
   }
   public Fact getFact() {
      return arg;
   }
   public boolean references(ValueFunction var) {
      ValueFunction[] variables = arg.variables();
      for(int i = 0; i < variables.length; i++ )
         if ( variables[i].references(var) )
            return true;
      return false;
   }
   public Vector variables() {
      Vector List = new Vector();
      ValueFunction[] variables = arg.variables();
      for(int i = 0; i < variables.length; i++ )
         List.addElement(variables[i]);
      return List;
   }
   public boolean isDeterminate() {
      return arg.isDeterminate();
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      if ( arg.resolve(c,b) ) return this;
      return null;
   }
   public ValueFunction duplicate(DuplicationTable table) {
      Fact f1 = arg.duplicate(table);
      return new FactFn(f1);
   }
   public ValueFunction mirror() {
      return new FactFn(new Fact(arg));
   }
   public boolean equals(Object any) {
      if ( !(any instanceof FactFn) ) return false;
      FactFn fn = (FactFn)any;
      return arg.equals(fn.getArg());
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      switch(fn.getID()) {
         case FACT:
              Fact f1 = (Fact)fn.getArg(0);
              if ( arg.unifiesWith(f1,b) ) {
                 arg.resolve(b);
                 return this;
              }
              return null;

         default:
              return null;
      }
   }
   public int baseID() {
      return getID();
   }

}
