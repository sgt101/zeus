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
import zeus.actors.*;
import zeus.actors.rtn.util.*;
import zeus.concepts.*;

public class LinearRespondentEvaluator extends StrategyEvaluator {
   boolean first_response = true;

   double min;
   double max;
   double noquibble;

   // The Strategy Parameters
   double price = 0;
   double min_price = 0;
   double max_price = 0;
   double offer = 0;
   double start_time = 0;
   double end_time = 0;
   double actual_cost = 0;
   double reserve_price = 0;
   double default_step = 0;
   double step = 0;
   double dt = 0;

   public int evaluateFirst(Vector goals, ProtocolDbResult info) {
      this.goals = goals;
      this.protocolInfo = info;

      // set up parameters
      max       = getDoubleParam("max.percent",120)/100.0;
      min       = getDoubleParam("min.percent",103)/100.0;
      noquibble = getDoubleParam("noquibble.range",1);

      default_step  = getDoubleParam("step.default",0.2);
      reserve_price = getDoubleParam("reservation.price",Double.MIN_VALUE);

      Goal g = (Goal)goals.elementAt(0);
      actual_cost = g.getCost();

      start_time = context.now();
      end_time = g.getReplyTime().getTime();

      max_price = max*Math.max(reserve_price,actual_cost);
      min_price = Math.max(min*actual_cost,reserve_price);

      price = max_price;

      g.setCost(price);
      return MESSAGE;
   }

   public int evaluateNext(DelegationStruct ds) {
      this.goals = ds.goals;
      Goal g = (Goal)ds.goals.elementAt(0);

      if ( ds.msg_type.equals("accept-proposal") ) {
         return OK;
      }
      else if ( ds.msg_type.equals("reject-proposal") ) {
         return FAIL;
      }

      offer = g.getCost();
      double now = context.now();

      if ( first_response ) {
         first_response = false;

         dt = now - start_time;
         // modify end_time to conclude  before real end_time
         end_time = end_time - dt;

         step = (max_price-min_price)*dt/(end_time-start_time);
         step = Math.max(step,default_step);
      }

      price -= step;
      price = Math.max(price,0);

      if ( price < min_price )
       	 price = min_price;

      if ( offer >= price )
         price = offer + step;

      g.setCost(price);
      return MESSAGE;
   }
}
