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
import zeus.actors.*;

/**
 * This class defines a negotation strategy, i.e. how the bidding strategy
 * changes with time and or bids received. Following initialisation,
 * negotiation proceeds via, first a call to the <code> evaluateFirst </code>
 * methods, the followed by zero or more calls to the <code> evaluateNext
 * </code> method. These two call return the following message types:
 * <table>
 * <tr> <td> <code> OK </code> </td> <td> the negotiation has
 * terminated successfully </td> </tr>
 * <tr> <td> <code> FAIL </code> </td> <td> the negotaition failed </td>
 * </tr> <tr> <td> <code> WAIT </code> </td> <td> wait until some timeout
 * period </td> </tr>
 * <tr> <td> <code> MESSAGE </code> </td> <td> a message needs to be sent
 * </td> </tr>
 * </table>
 *
 * @see StrategyEvaluatorList
 */
public abstract class StrategyEvaluator {
   public static final int OK      = 0;
   public static final int FAIL    = 1;
   public static final int WAIT    = 2;
   public static final int MESSAGE = 3;

   protected AgentContext context = null;
   protected StrategyEvaluatorList evaluators = null;
   protected boolean isActive = true;
   protected Vector goals = null;
   protected ProtocolDbResult protocolInfo = null;

   public void set(AgentContext context) {
      this.context = context;
   }
   public void set(StrategyEvaluatorList evaluators) {
      this.evaluators = evaluators;
   }

   public boolean          isActive()        { return isActive; }
   public Vector           getGoals()        { return goals;    }
   public ProtocolDbResult getProtocolInfo() { return protocolInfo; }

   public abstract int evaluateFirst(Vector goals, ProtocolDbResult info);
   public abstract int evaluateNext(DelegationStruct ds);

   protected String getParam(String param, String default_value) {
      if ( protocolInfo == null ) 
         return default_value;
      else {
         String obj = (String)protocolInfo.parameters.get(param);
         if ( obj == null ) return default_value;
         return obj;
      }
   }
   protected int getIntParam(String param, int default_value) {
      if ( protocolInfo == null ) 
         return default_value;
      else {
         String obj = (String)protocolInfo.parameters.get(param);
         if ( obj == null ) return default_value;
         try {
            return Integer.parseInt(obj);
         }
         catch(NumberFormatException e) {
            Core.USER_ERROR("NumberFormat error in parameter " + param +
                            "=" + obj + 
                            " of strategy " + protocolInfo.strategy + 
                            " of protocol " + protocolInfo.protocol + 
                            " -- integer value expected ");
            return default_value;
         }
      }
   }
   protected double getDoubleParam(String param, double default_value) {
      if ( protocolInfo == null ) 
         return default_value;
      else {
         String obj = (String)protocolInfo.parameters.get(param);
         if ( obj == null ) return default_value;
         try {
            return (Double.valueOf(obj)).doubleValue();
         }
         catch(NumberFormatException e) {
            Core.USER_ERROR("NumberFormat error in parameter " + param +
                            "=" + obj + 
                            " of strategy " + protocolInfo.strategy + 
                            " of protocol " + protocolInfo.protocol + 
                            " -- double value expected ");
            return default_value;
         }
      }
   }
   protected boolean getBooleanParam(String param, boolean default_value) {
      if ( protocolInfo == null ) 
         return default_value;
      else {
         String obj = (String)protocolInfo.parameters.get(param);
         if ( obj == null ) return default_value;
         try {
            return (Boolean.valueOf(obj)).booleanValue();
         }
         catch(NumberFormatException e) {
            Core.USER_ERROR("NumberFormat error in parameter " + param +
                            "=" + obj + 
                            " of strategy " + protocolInfo.strategy + 
                            " of protocol " + protocolInfo.protocol + 
                            " -- boolean value expected ");
            return default_value;
         }
      }
   }

}
