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

public class ReservationEntry {
   public int start;
   public boolean consumed;
   public int amount;
   public String agent;
   public String id;
   public String goalId;
   public String comms_key;

   public ReservationEntry(String id, int start, boolean consumed,
                           int amount, String agent, String goalId,
                           String comms_key) {
      this.start = start;
      this.consumed = consumed;
      this.amount = amount;
      this.agent = agent;
      this.id = id;
      this.goalId = goalId;
      this.comms_key = comms_key;
   }
   public String toString() {
      return( "(" +
               ":id " + id + " " +
               ":start " + start + " " +
               ":consumed " + consumed + " " +
               ":amount " + amount + " " +
               ":agent " + agent + " " +
               ":goal_id " + goalId + " " +
               ":comms_key " + comms_key +
              ")"
            );
   }
}
