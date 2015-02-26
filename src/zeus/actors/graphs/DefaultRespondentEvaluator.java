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



package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.util.*;

public class DefaultRespondentEvaluator extends StrategyEvaluator {
   // parameters
   double percentage;
   double reserve_price = 0;

   double price = 0;
   double actual_cost = 0;

   public int evaluateFirst(Vector goals, ProtocolDbResult info) {
      this.goals = goals;
      this.protocolInfo = info;

      // set up parameters
      percentage = getDoubleParam("percent",100)/100.0;
      reserve_price = getDoubleParam("reservation.price",Double.MIN_VALUE);

      Goal g = (Goal)goals.elementAt(0);
      actual_cost = g.getCost();

      price = Math.max(percentage*actual_cost,reserve_price);

      g.setCost(price);
      return MESSAGE;
   }

   public int evaluateNext(DelegationStruct ds) {
      this.goals = ds.goals;
      return ds.msg_type.equals("accept-proposal") ? OK : FAIL;
   }
}
