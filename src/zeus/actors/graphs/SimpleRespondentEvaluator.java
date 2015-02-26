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

public class SimpleRespondentEvaluator extends StrategyEvaluator {
   protected static final double round_time = 0.1;

   // Strategy parameters
   double max;
   double min;
   double noquibble;

   double cost = 0;
   double price = 0;
   double offer = 0;
   double rate;
   double start_time = 0;
   double end_time = 0;

   public int evaluateFirst(Vector goals, ProtocolDbResult info) {
      Core.DEBUG(3,"SimpleRespondentEvaluator:EvaluateFirst entry\n" +
         goals + "\n" + info);
      this.goals = goals;
      this.protocolInfo = info;

      // set up parameters
      max       = getDoubleParam("max.percent",120)/100.0;
      min       = getDoubleParam("min.percent",103)/100.0;
      noquibble = getDoubleParam("noquibble.range",1);
      
      Goal g = (Goal)goals.elementAt(0);
      Core.DEBUG(3,"SimpleRespondentEvaluator:EvaluateFirst g = " + g);

      end_time = g.getReplyTime().getTime() - round_time;
      start_time = context.now();
      cost = g.getCost();

      max *= cost;
      min *= cost;
      double r_price = getDoubleParam("reservation.price",Double.MIN_VALUE);
      min = Math.max(min,r_price);

      price = (int)max;
      rate = Math.log(max/min)/(end_time-start_time);

      price = Math.max(price,0);
      g.setCost(price);
      Core.DEBUG(3,context.whoami() + " EvaluateFirst: " + start_time +", " + end_time + ", " + price);

      return MESSAGE;
   }

   public int evaluateNext(DelegationStruct ds) {
      Core.DEBUG(3,"SimpleRespondentEvaluator:EvaluateNext: " + ds);
      this.goals = ds.goals;
      if ( ds.msg_type.equals("accept-proposal") ) {
	 System.out.println("OK 0");
         return OK;
      }
      if ( ds.msg_type.equals("reject-proposal") ) {
	 System.out.println("Fail 0");
         return FAIL;
      }

      Goal g = (Goal)ds.goals.elementAt(0);
    
      offer = g.getCost();

      double dt = context.now() - start_time;
      System.out.println("dt = " + dt + " rate = " + rate);

      price = (int)(max*Math.exp(-1.0*rate*dt));

      Core.DEBUG(3,context.whoami() + " EvaluateNext: " + offer + " " + price);

      if ( price < min ) {
	 System.out.println("Fail 1");
         return FAIL;
      }

      if ( offer >= price )
         price = offer + 1;

      price = Math.max(price,0);
      g.setCost(price);      

      return MESSAGE;
   }
}
