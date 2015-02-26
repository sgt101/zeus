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



package zeus.actors.event;

import zeus.util.*;

public abstract class Event {
   protected static final int FACT_FIRST = 0;
   protected static final int FACT_LAST = 3;

   protected static final int TASK_FIRST = 4;
   protected static final int TASK_LAST = 7;

   protected static final int ABILITY_FIRST = 8;
   protected static final int ABILITY_LAST = 11;

   protected static final int RELATION_FIRST = 12;
   protected static final int RELATION_LAST = 15;

   protected static final int MESSAGE_FIRST = 16;
   protected static final int MESSAGE_LAST = 19;

   protected static final int AGENT_FIRST = 20;
   protected static final int AGENT_LAST = 23;

   protected static final int PLANNING_FIRST = 24;
   protected static final int PLANNING_LAST = 26;

   protected static final int PLANSTEP_FIRST = 27;
   protected static final int PLANSTEP_LAST = 29;

   protected static final int CLOCK_FIRST = 30;
   protected static final int CLOCK_LAST = 30;

   protected static final int HANDLER_FIRST = 31;
   protected static final int HANDLER_LAST = 34;

   protected static final int NODE_FIRST = 35;
   protected static final int NODE_LAST = 38;

   protected static final int ARC_FIRST = 39;
   protected static final int ARC_LAST = 42;

   protected static final int GRAPH_FIRST = 43;
   protected static final int GRAPH_LAST = 46;

   protected static final int PROTOCOL_FIRST = 47;
   protected static final int PROTOCOL_LAST = 50;

   protected static final int CONVERSATION_FIRST = 51;
   protected static final int CONVERSATION_LAST = 52;

   protected static final int RETE_FIRST = 53;
   protected static final int RETE_LAST = 57;

   public static final int  MAX_ID    = 57;
   public static final long NULL_MASK = 0L;
   public static final long ALL_MASK  = ~0L;

   protected static final String[] event_type = {
      "FactAddedEvent",
      "FactModifiedEvent",
      "FactDeletedEvent",
      "FactAccessedEvent",

      "TaskAddedEvent",
      "TaskModifiedEvent",
      "TaskDeletedEvent",
      "TaskAccessedEvent",

      "AbilityAddedEvent",
      "AbilityModifiedEvent",
      "AbilityDeletedEvent",
      "AbilityAccessedEvent",

      "RelationAddedEvent",
      "RelationModifiedEvent",
      "RelationDeletedEvent",
      "RelationAccessedEvent",

      "MessageReceivedEvent",
      "MessageQueuedEvent",
      "MessageDispatchedEvent",
      "MessageNotDispatchedEvent",

      "AgentCreatedEvent",
      "AgentDeathEvent",
      "AgentSuspendedEvent",
      "AgentResumedEvent",

      "PlanningStartedEvent",
      "PlanningFailedEvent",
      "PlanningSucceededEvent",

      "PlanStepCreatedEvent",
      "PlanStepDisposedEvent",
      "PlanStepStateChangedEvent",

      "ClockTickEvent",

      "MessageRuleAddedEvent",
      "MessageRuleDeletedEvent",
      "MessageRuleFiredEvent",
      "MessageRuleFailedEvent",

      "NodeCreatedEvent",
      "NodeDisposedEvent",
      "NodeStateChangedEvent",

      "ArcCreatedEvent",
      "ArcDisposedEvent",
      "ArcFailedEvent",
      "ArcSucceededEvent",

      "GraphCreatedEvent",
      "GraphDisposedEvent",
      "GraphStateChangedEvent",

      "ProtocolAddedEvent",
      "ProtocolModifiedEvent",
      "ProtocolDeletedEvent",
      "ProtocolAccessedEvent",

      "ConversationInitiatedEvent",
      "ConversationContinuedEvent",

      "ReteRuleAddedEvent",
      "ReteRuleDeletedEvent",
      "ReteRuleActivatedEvent",
      "ReteRuleDeactivatedEvent",
      "ReteRuleFiredEvent"
   };

   protected long   time = 0;
   protected int    id = -1;
   protected Object source = null;
   protected Object object = null;

   public long   getTime()        { return time; }
   public int    getID()          { return id; }
   public String getDescription() { return event_type[id]; }
   public Object getSource()      { return source; }
   public Object getObject()      { return object; }

   protected Event(Object source, Object object, int first,
                   int last, long mask) {
      id = (int)(Math.log((double)mask)/Math.log(2.0)) + first;
      Assert.notNull(source);
      Assert.notNull(object);
      Assert.notFalse(0 <= first && first <= id &&
                      id <= last && last <= MAX_ID);
      this.time = System.currentTimeMillis();
      this.source = source;
      this.object = object;
   }
   public String toString() {
      return getDescription() + ":\n\tTime: " + time +
                                 "\n\tSource: " + source +
                                 "\n\tObject: " + object;
   }
}
