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


class AttributeNode extends PatternNode {
   ValueFunction value = null;
   String attribute = null;

   AttributeNode(ReteEngine engine, String attribute, ValueFunction value) {
      super(engine);
      this.attribute = attribute;
      this.value = value;
   }
   void evaluate(String path, int tag, int type, Vector input, Bindings b) {
     // System.out.println("evaluating attribute node" + toString()); 
      Assert.notFalse(type == SINGLE);
      Assert.notFalse(input.size() == 1);
      Fact f = (Fact)input.elementAt(0);
      ValueFunction v = f.getFn(attribute);
      if ( v != null && value.unifiesWith(v,b) != null )
         propagate(path,tag,input,b);
   }
   public boolean equals(Object any) {
      if ( any instanceof AttributeNode ) {
         AttributeNode node = (AttributeNode)any;
         return attribute.equals(node.attribute) && value.equals(node.value);
      }
      return false;
   }
   public String toString() {
      return "AttributeNode(" + attribute + "," + value + ")";
   }
}
