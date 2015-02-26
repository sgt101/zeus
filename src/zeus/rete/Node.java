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


public abstract class Node {
   public static final int ADD = 0;
   public static final int REMOVE = 1;

   static final int SINGLE = 0;
   static final int ACTION = 1;
   static final int LEFT   = 2;
   static final int RIGHT  = 3;

   Hashtable  successors = new Hashtable();
   ReteEngine engine = null;
   int use_count = 0;

   Node(ReteEngine engine) {
      this.engine = engine;
   }

   void addSuccessor(String path, Node node, int type) {
      Assert.notFalse(successors.put(path,new Successor(node,type)) == null);
   }

   void propagate(String path, int tag, Vector input, Bindings b) {
      Core.DEBUG(5,"\nPropagate: " + path + " " + this);
      Successor s = (Successor)successors.get(path);
      s.node.evaluate(path,tag,s.type,input,b);
   }

   abstract void evaluate(String path, int tag, int type,
                          Vector input, Bindings b);
}
