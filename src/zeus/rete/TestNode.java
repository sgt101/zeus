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
import zeus.concepts.fn.*;


class TestNode extends PatternNode {
   ValueFunction value = null;

   TestNode(ReteEngine engine, ValueFunction value) {
      super(engine);
      this.value = value;
   }
   void evaluate(String path, int tag, int type, Vector input, Bindings b) {
     // System.out.println("evaluating test " + path); 
      Assert.notFalse(type == SINGLE);
      ValueFunction fn = value.resolve(b);
      if ( !fn.isDeterminate() ) {
         Core.USER_ERROR("Non-ground argument in Rete test expression \"" +
            "(test " + value + ")\"");
         return;
      }

      if ( !(fn instanceof LogicalFn) ) {
         Core.USER_ERROR("Non-logical argument in Rete test expression \"" +
            "(test " + value + ")\"");
         return;
      }
      if ( ((LogicalFn)fn).evaluate() == LogicalFn.TRUE )
         propagate(path,tag,input,b);
   }
   public boolean equals(Object any) {
      if ( !(any instanceof TestNode) ) return false;
      TestNode node = (TestNode)any;
      return value.equals(node.value);
   }
   public String toString() {
      return "TestNode(" + value + ")";
   }
}
