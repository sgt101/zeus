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



package zeus.rete;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;


class TypeNode extends PatternNode {
   String fact_type = null;

   TypeNode(ReteEngine engine, String fact_type) {
      super(engine);
      this.fact_type = fact_type;
   }
   void evaluate(String path, int tag, int type, Vector input, Bindings b) {
    //  System.out.println("evaluating Type " + path); 
      Assert.notFalse(path == null);
      Assert.notFalse(type == SINGLE);
      Assert.notFalse(input.size() == 1);
      Fact f = (Fact)input.elementAt(0);

  //    if ( fact_type.equals(f.getType()) )
     // System.out.println(f.getType()+ " == " + fact_type); 
      if ( f.isa (fact_type)) // change !
         propagate(path,tag,input,b);
   }
   
   
   void propagate(String path, int tag, Vector input, Bindings b) {
      Core.DEBUG(5,"\nPropagate: " + path + " " + this);
      Successor s;
      Enumeration keys = successors.keys();
      while( keys.hasMoreElements() ) {
         path = (String)keys.nextElement();
         s = (Successor)successors.get(path);
         s.node.evaluate(path,tag,s.type,input,new Bindings(b));
      }
   }
   
   
   public boolean equals(Object any) {
      if ( any instanceof TypeNode ) {
         TypeNode node = (TypeNode)any;
         return fact_type.equals(node.fact_type);
      }
      return false;
   }
   public String toString() {
      return "TypeNode(" + fact_type + ")";
   }
}
