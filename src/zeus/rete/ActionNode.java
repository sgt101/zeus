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


class ActionNode extends Node {
   String rule_name = null;
   int salience = 0;
   Vector actions = null;
   Vector constraints = new Vector();

   class Entry { int position; String attribute; ValueFunction value; }

   ActionNode(ReteEngine engine, String rule_name, int salience, Vector actions) {
      super(engine);
      this.rule_name = rule_name;
      this.actions = actions;
      this.salience = salience;
   }


   void evaluate(String path, int tag, int type, Vector input, Bindings b) {
      Assert.notFalse(type == ACTION);
      engine.getConfictSet().update(path,tag,input,b,this);

   }

   
   public String toString() {
      return "ActionNode(" + rule_name + "[" + salience + "]," + actions + ")";
   }
}
