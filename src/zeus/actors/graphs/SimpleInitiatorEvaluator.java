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

public class SimpleInitiatorEvaluator extends StrategyEvaluator {
   protected static final double round_time = 0.1;

   // The Strategy Parameters
   double min;
   double max;
   double noquibble;

   double cost = 0;
   double price = 0;
   double offer = 0;
   double start_time = 0;
   double end_time = 0;
   double rate = 0;

   public int evaluateFirst(Vector goals, ProtocolDbResult info) {
      this.goals = goals;
      this.protocolInfo = info;

      min       = getDoubleParam("min.percent",80)/100.0;
      max       = getDoubleParam("max.percent",120)/100.0;
      noquibble = getDoubleParam("noquibble.range",2);

      Goal g = (Goal)goals.elementAt(0);
      ExternalDb db = context.ExternalDb();

      end_time = g.getReplyTime().getTime() - round_time;
      start_time = context.now();

      // determine expected cost of goods
      cost = g.getCost();
      if ( Misc.isZero(cost) ) {
         Fact f = g.getFact();
         cost = f.getNetCost();
      }

      min *= cost;
      max *= cost;
      double r_price = getDoubleParam("reservation.price",Double.MAX_VALUE);
      max = Math.min(max,r_price);

      price = (int)min;
      rate = Math.log(max/min)/(end_time-start_time);

      g.setCost(price);
      // System.out.println(context.whoami() + " EvaluateFirst: " + start_time + ", " + end_time + ", " + price);
      return MESSAGE;
   }

   public int evaluateNext(DelegationStruct ds) {
      this.goals = ds.goals;
      if ( !ds.msg_type.equals("propose") ) {
	// System.out.println("FAIL 0");
         return FAIL;
      }

      Goal g = (Goal)ds.goals.elementAt(0);
    
      offer = g.getCost();

      if ( offer < price + noquibble ) {
	//         System.out.println("OK -1");
         return OK;
      }

      double dt = context.now() - start_time;
      // System.out.println("dt = " + dt + " rate = " + rate);

      price = (int)(min*Math.exp(rate*dt));

      // System.out.println(context.whoami() + " EvaluateNext " + g.getFact().getType() + ": " + offer + " " + price);

      if ( offer < price + noquibble ) {
	//         System.out.println("OK 0");
         return OK;
      }

      if ( context.now() + round_time >= end_time ) {
	 if ( offer < max ) {
	   //            System.out.println("OK 1");
            return OK;
         }
         else {
	   //            System.out.println("FAIl 1");
            return FAIL;
         }
      }

      g.setCost(price);      
      return MESSAGE;
   }
}
