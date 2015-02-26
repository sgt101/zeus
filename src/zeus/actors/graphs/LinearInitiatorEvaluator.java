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

public class LinearInitiatorEvaluator extends StrategyEvaluator {
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
   double expected_cost = 0;
   double reserve_price = 0;
   double default_step = 0;
   double step = 0;
   double dt = 0;

   public int evaluateFirst(Vector goals, ProtocolDbResult info) {
      this.goals = goals;
      this.protocolInfo = info;

      min       = getDoubleParam("min.percent",80)/100.0;
      max       = getDoubleParam("max.percent",120)/100.0;
      noquibble = getDoubleParam("noquibble.range",2);

      default_step  = getDoubleParam("step.default",0.2);
      reserve_price = getDoubleParam("reservation.price",Double.MAX_VALUE);

      Goal g = (Goal)goals.elementAt(0);
      expected_cost = g.getCost();

      start_time = context.now();
      end_time = g.getReplyTime().getTime();

      g.setCost(0);

      return MESSAGE;
   }

   public int evaluateNext(DelegationStruct ds) {
      this.goals = ds.goals;
      if ( !ds.msg_type.equals("propose") )
         return FAIL;

      Goal g = (Goal)ds.goals.elementAt(0);
      offer = g.getCost();
      double now = context.now();

      if ( first_response ) {
         first_response = false;

         dt = now - start_time;
         // modify end_time to conclude  before real end_time
         end_time = end_time - dt;

         expected_cost = Math.max(offer,expected_cost);

         min_price = min*Math.min(reserve_price,expected_cost);
         max_price = Math.min(max*expected_cost,reserve_price);

         price = min_price;
         step = (max_price-min_price)*dt/(end_time-start_time);
         step = Math.max(step,default_step);
      }

      if ( offer < price + noquibble )
         return OK;

      if ( !first_response ) price += step;

      if ( price < min_price )
         return FAIL;
      else if ( offer < price + noquibble )
         return OK;

      if ( now + dt >= end_time )
			{
         // running out of time: terminate
	       if ( offer < max_price )
            return OK;
         else
            return FAIL;
      }

      g.setCost(price);
      return MESSAGE;
   }
}
