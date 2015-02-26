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



package zeus.actors.rtn;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.util.*;

public class Contract {
   public Goal    goal = null;
   public String  key = null;
   public double  cost = 0;
   public boolean delivered = false;
   public boolean paid = false;
   public String  agent = null;
   public String  owner = null;
   public double  timeout = 0;
   
   public Contract(Goal goal, String key, double cost,
                   boolean delivered, boolean paid,
                   String agent, String owner, double timeout) {

      Assert.notNull(goal);
      Assert.notNull(key);
      Assert.notNull(agent);
      Assert.notNull(owner);

      this.goal = goal;
      this.key = key;
      this.cost = cost;
      this.delivered = delivered;
      this.paid = paid;
      this.agent = agent;
      this.owner = owner;
      this.timeout = timeout;
   }
   public String toString() {
      String out;
      out = "(goal " + goal.getId() + "\n" +
            " key " + key + "\n" +
            " cost " + cost + "\n" +
            " delivered " + delivered + "\n" +
            " paid " + paid + "\n" +
            " agent " + agent + "\n" +
            " owner " + owner + "\n" + 
            " timeout " + timeout + ")";
      return out;
   }
} 
