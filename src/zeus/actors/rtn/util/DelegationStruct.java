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



package zeus.actors.rtn.util;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

/** 
 * This structure holds contract related information being communicated
 * between two agents. During the contracting phase, the information will
 * mostly realate to the evolving stages of the contract (e.g. call,
 * proposal, counter, accept, etc.). Following the contracting phase, and
 * during the execution/monitoring phase the information will relate to
 * deliverables, payment, invoice, exceptions, etc.
 */

public class DelegationStruct {
   /** The agent sending the message */
   public String agent = null;

   /** The type of message, e.g. cfp, propose, accept, reject, etc. */
   public String msg_type = null;

   /** The delegation reference that uniquely identifies the goal/contract */
   public String key = null;

   /**
    * A vector containing: (a) the goals, during contracting phase, and (b)
    * facts, during the execution/monitoring phase.
    */
   public Vector goals = null;
   
   public DelegationStruct(String agent, String type,
                           String key, Vector goals) {
      this.agent = agent;
      this.msg_type = type;
      this.key = key;
      this.goals = goals;
   }
   public DelegationStruct(String agent, String type,
                           String key, Goal goal) {
      this.agent = agent;
      this.msg_type = type;
      this.key = key;
      this.goals = new Vector();
      this.goals.addElement(goal);
   }
   public DelegationStruct(String agent, String type,
                           String key, Fact goal) {
      this.agent = agent;
      this.msg_type = type;
      this.key = key;
      this.goals = new Vector();
      this.goals.addElement(goal);
   }

   public String toString() {
      String output = "(agent " + agent + "\n" +
                      " msg_type " + msg_type + "\n" +
                      " key " + key + "\n" +
                      " goals " + goals + "\n" + ")";
      return output;
   }
} 
