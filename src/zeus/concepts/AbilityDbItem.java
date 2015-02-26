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



package zeus.concepts;

import java.util.*;
import zeus.util.*;

/** 
  *  AbilityDbItem is a conveinience class for holding the items that will be 
  *  put in the agents AbilityDb
  *  Change Log
  *------------
  * Simon put a () init method in so that this could be extended gracefully.
  * @since 0.9  
    */ 

public class AbilityDbItem {
   protected String      agent;
   protected AbilitySpec ability;
   
   public AbilityDbItem() { 
   }

   public AbilityDbItem(String agent, AbilitySpec ab) {
      this.agent = agent;
      ability = new AbilitySpec(ab);
   }

   public AbilityDbItem(AbilityDbItem item) {
      agent   = item.getAgent();
      ability = new AbilitySpec(item.getAbility());
   }

   public AbilityDbItem duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   
   
   public AbilityDbItem duplicate(DuplicationTable table) {
      return new AbilityDbItem(agent,ability.duplicate(table));
   }

   public String         getAgent()    { return agent; }
   public AbilitySpec    getAbility()  { return ability; }
   public String         getType()     { return ability.getType(); }

   public boolean resolve(Bindings b) {
      return ability.resolve(b);
   }

   public boolean equals(AbilityDbItem item ) {
      return agent.equals(item.getAgent()) &&
             ability.equals(item.getAbility());
   }

   public String toString() {
      return( "(" +
               ":agent " + agent + " " +
               ":ability " + ability.toString() +
              ")"
            );
   }
}
