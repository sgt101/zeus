 
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
/** 
    A message rule is set in the zeus.actors.MsgHandler of an agent (look for the set method!)
    basically you define a pattern, which the rule will match to any incomming messages, and an action
    which is a call to a method. 
    Use this to get the agent to act in a particular way when it receives a message from a source
    Change Log
    ----------
    06/06/01 - MessageRule() init added to allow graceful subclassing
    06/06/01 - changed to MessageRuleImpl to allow returned types to be ok. 
    */
    
    
    
public class MessageRuleImpl implements MessageRule{
   protected MessageAction  action = null;
   protected MessagePattern pattern = null;
   protected String         name = null;

   public MessageRuleImpl () { 
   }


   public MessageRuleImpl(String name) {
      Assert.notNull(name);
      this.name = name;
   }


   public MessageRuleImpl(String name, String[] patterns,
                      int type, Object object, String method) {
      this(name);
      setPattern(new MessagePatternImpl(patterns));
      setAction(new MessageActionImpl(type,object,method));
   }
   
   
   public MessageRuleImpl(String name, String[] patterns,
                      Object object, String method) {
      this(name);
      setPattern(new MessagePatternImpl(patterns));
      setAction(new MessageActionImpl(object,method));
   }
   
   
   public MessageRuleImpl(String name, MessagePattern pattern,
                      MessageAction action) {
      this(name);
      setPattern(pattern);
      setAction(action);
   }


   public String         getName()    { return name; }
   public MessagePattern getPattern() { return pattern; }
   public MessageAction  getAction()  { return action; }


   public void setPattern(MessagePattern pattern) {
      this.pattern = pattern;
   }
   
   
   public void setAction(MessageAction action) {
      this.action = action;
   }
   
   
   public String toString() {
      return "(:message_rule " + name + " :pattern " + pattern +
             " :action " + action + ")";
   }
}
