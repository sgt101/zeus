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



package zeus.actors;

import zeus.actors.event.*; 
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import java.lang.reflect.*; 

public class ActionNode extends MsgNode {
   String rule = null;
   MessageAction action = null;

// meaningless init  to allow rearch
   public ActionNode() {
   ;
   }


   ActionNode(MsgHandler engine, String rule, MessageAction action) {
      super(engine);
      this.rule = rule;
      this.action = action;
   }
   
   void evaluate(String rule, Performative input) {
       if ( action.getType() == MessageActionImpl.EXECUTE_ONCE )
          engine.removeRule(rule);
   //   System.out.println("in evalate in exec " + rule +" " + action.getMethod()); 
       execRule(rule,action.getObject(),action.getMethod(),input);
   }
   
   
   public String toString() {
      return "ActionNode(" + rule + "," + action + ")";
   }
   
   
   public final synchronized Object execRule(String rule, Object object, String method, Performative input) {

      Class c = object.getClass();
      try {
         Class[] parameter_types = new Class[1];
         parameter_types[0] = input.getClass();
	     Object[] arglist = new Object[1];
         arglist[0] = input;

         Core.DEBUG(2,"Invoking method " + method + " of class " +
                    c.getName() + " with parameter " + input);

         Method m = c.getMethod(method,parameter_types);
         engine.notifyMonitors(MsgHandler.FIRE,new MessageHandlerEvent(this,rule,object,method,input,MessageHandlerEvent.FIRE_MASK));
         return m.invoke(object,arglist);
      }
      catch(Throwable err) {
         Core.USER_ERROR("MsgHandler - Error invoking target: [" + rule +
            "::" + object.getClass().getName() + "." + method +
            "()]\nwith args...\n" + input + "\n" + err);
         engine.notifyMonitors(MsgHandler.FAIL,new MessageHandlerEvent(this,rule,object,method,input,MessageHandlerEvent.FAIL_MASK));
      }
      return null;
   }
}
