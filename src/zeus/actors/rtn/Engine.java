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



package zeus.actors.rtn;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.actors.graphs.*;
import zeus.actors.rtn.util.*;


public class Engine extends Thread {
   static final int ARC_CREATE       = 0;
   static final int ARC_DISPOSE      = 1;
   static final int ARC_FAIL         = 2;
   static final int ARC_SUCCEED      = 3;

   static final int NODE_CREATE       = 0;
   static final int NODE_DISPOSE      = 1;
   static final int NODE_STATE_CHANGE = 2;

   static final int GRAPH_CREATE       = 0;
   static final int GRAPH_DISPOSE      = 1;
   static final int GRAPH_STATE_CHANGE = 2;

   static final int INITATE_CONVERSATION = 0;
   static final int CONTINUE_CONVERSATION = 1;

   public static String[] COORDINATION_MESSAGE_TYPES = {
      "accept-proposal",
      "reject-proposal",
      "cfp",
      "propose",
      "refuse",
      "cancel",
      "failure",
      "result",
      "payment",
      "invoice"
   };

   protected zeus.util.Queue queue = new zeus.util.Queue();
   protected Vector msgQueue = new Vector();
   protected NodeWaitTable msgWaitTable;
   protected boolean running = true;
   protected AuditTable auditTable = null;
   protected AgentContext context = null;
   protected Hashtable itemsForSale = new Hashtable();
   protected Hashtable keyTable = new Hashtable();

   protected HSet[] nodeMonitors = new HSet[3];
   protected HSet[] arcMonitors = new HSet[4];
   protected HSet[] graphMonitors = new HSet[3];
   protected HSet[] conversationMonitors = new HSet[2];

   public Engine () {
   super ();
   }

   public Engine(AgentContext context) {
      Assert.notNull(context);
      this.context = context;
      context.set(this);

      auditTable = new AuditTable(this);

      for(int i = 0; i < graphMonitors.length; i++ )
         graphMonitors[i] = new HSet();
      for(int i = 0; i < nodeMonitors.length; i++ )
         nodeMonitors[i] = new HSet();
      for(int i = 0; i < arcMonitors.length; i++ )
         arcMonitors[i] = new HSet();
      for(int i = 0; i < conversationMonitors.length; i++ )
         conversationMonitors[i] = new HSet();

      msgWaitTable = new NodeWaitTable(this,queue);
      this.setPriority(Thread.NORM_PRIORITY-1);

      // Initialize default message processing rules
      String[] pattern = { "type", "cfp",
                           "reply-with", "\\A(\\w)(.*)\\Z",
                           "in-reply-to", "" };
      context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
         pattern, this, "new_dialogue")
      );

      this.start();
      Thread waitTable = new Thread(msgWaitTable);
      waitTable.setPriority(Thread.NORM_PRIORITY-2);
      waitTable.start(); 
      
  
   }
   
   //synchronized
   /** 
    1.3 addition to stop planner deadlock
    */
   public synchronized void callCheckRecords() { 
      Planner planner = context.getPlanner();
      planner.checkRecords(); 
   }
   
   
   //synchronized 
   /** 
    1.3 addition to stop planner deadlock
    */
    public synchronized void callShuffle () { 
     Planner planner = context.getPlanner(); 
     planner.shuffle(); 
    }

   
   public AgentContext getAgentContext() {
      return context;
   }
   
   public AuditTable getAuditTable() {
      return auditTable;
   }
   
   
   public void run() {
     this.setName("Engine"); 
      Node node;
      while(running) {
         node = (Node)queue.dequeue();
         synchronized(this) {
            node.run(this);
            node = null; 
         }
        yield();
      }
   }

   public void stopProcessing() {
      running = false;
   }


 /**
      This routine is used by this agent to begin a negotiation dialogue
      with another (remote) agent
   */
   public synchronized void new_dialogue(String comms_key, String agent,
                            String msg_type, Vector goals) {
                                // sync
  

      String content = Misc.concat(goals);
      Core.DEBUG(1,"start_dialogue:\n\tAgent: " + agent +
         "\n\tCommsKey: " + comms_key + "\n\tMsgType: " + msg_type +
	 "\n\tContent: " + content);

      Performative msg = new Performative(msg_type);
      msg.setReplyWith(comms_key);
      msg.setReceiver(agent);
      msg.setContent(content);
      context.MailBox().sendMsg(msg);
      notifyConversationMonitors(comms_key,context.whoami(),agent,msg_type,
                                 goals,INITATE_CONVERSATION);
   }

 /**
      This routine is used by a remote agent to start a negotiation dialogue
      with this agent
   */
   public synchronized void new_dialogue(Performative msg) {
  
      String key = msg.getReplyWith();
      String agent = msg.getSender();
      String data = msg.getContent();

      Vector goals = ZeusParser.goalList(context.OntologyDb(),data);
      goals = dereference(goals);
      notifyConversationMonitors(key,agent,context.whoami(),msg.getType(),
                                 goals,INITATE_CONVERSATION);

      achieve(agent,key,goals);
   }


/**
      This routine is used by this agent to continue a negotiation dialogue
      with another (remote) agent
   */
   //synchronized
   public synchronized void continue_dialogue(String comms_key, String agent,
                                 String msg_type, Vector goals) {
      String content = Misc.concat(goals);
      Core.DEBUG(1,"continue_dialogue:\n\tAgent: " + agent +
         "\n\tCommsKey: " + comms_key + "\n\tMsgType: " + msg_type +
	 "\n\tContent: " + content);

      Performative msg = new Performative(msg_type);
      msg.setInReplyTo(comms_key);
      msg.setReceiver(agent);
      msg.setContent(content);
      context.MailBox().sendMsg(msg);
      notifyConversationMonitors(comms_key,context.whoami(),agent,msg_type,
                                 goals,CONTINUE_CONVERSATION);
   }


    // synchronized
   public synchronized void continue_dialogue(String comms_key, String agent,
                                 String msg_type, String data_type,
				 String data_key, Vector goals) {
  
      String content = Misc.concat(goals);
      Core.DEBUG(1,"continue_dialogue:\n\tAgent: " + agent +
         "\n\tCommsKey: " + comms_key + "\n\tMsgType: " + msg_type +
         "\n\tDataType: " + data_type + "\n\tDataKey: " + data_key +
	 "\n\tContent: " + content);

      Performative msg = new Performative(msg_type);
      Core.ERROR(msg_type.equals("inform"),2,this);
      msg.setInReplyTo(comms_key);
      msg.setReceiver(agent);
      msg.setContent(data_type + " " + data_key + " " + content);
      context.MailBox().sendMsg(msg);
      notifyConversationMonitors(comms_key,context.whoami(),agent,msg_type,
                                 data_type,data_key,goals,
                                 CONTINUE_CONVERSATION);
   
   }
   
   

   public synchronized void continue_dialogue(Performative msg) {
   /**
      This routine is used by a remote agent to continue a negotiation dialogue
      with this agent
   */
 //  synchronized (this) { 
 try { // bug hunting!
      String agent = msg.getSender();
      String content = msg.getContent();
      String msg_type = msg.getType();
      String msg_key = msg.getInReplyTo();
      Vector goals;
      if ( msg_type.equals("inform") ) {
         MsgContentHandler hd = new MsgContentHandler(content);
         String data_type = hd.tag();
         String data_key = hd.data(0);
         String data = hd.rest(0);
         goals = ZeusParser.factList(context.OntologyDb(),data);
         goals = dereference(goals);
         notifyConversationMonitors(msg_key,agent,context.whoami(),
                                    msg_type,data_type,data_key,goals,
                                    CONTINUE_CONVERSATION);
         add(new DelegationStruct(agent,data_type,data_key,goals));
      }
      else {
         goals = ZeusParser.goalList(context.OntologyDb(),content);
         goals = dereference(goals);
         notifyConversationMonitors(msg_key,agent,context.whoami(),msg_type,
                                    goals,CONTINUE_CONVERSATION);
         add(new DelegationStruct(agent,msg_type,msg_key,goals));
      }
 }catch (Exception e) { 
    e.printStackTrace(); 
 }

 // }
   }

   protected final Vector dereference(Vector input) {
      /**
          Replication to handle the problem of variable clashes when
          agents communicate with one another with variables in the
          content data;
      */

      Fact f;
      Goal g;
      Vector result = new Vector();
      Object x = input.elementAt(0);
      if ( x instanceof zeus.concepts.Fact ) {
         for(int i = 0; i < input.size(); i++ ) {
            f = (Fact)input.elementAt(i);
            f = f.duplicate(Fact.VAR,context.GenSym());
            result.addElement(f);
         }
      }
      else {
         for(int i = 0; i < input.size(); i++ ) {
            g = (Goal)input.elementAt(i);
            g = g.duplicate(Fact.VAR,context.GenSym());
            result.addElement(g);
         }
      }
      return result;
   }

    //synchronized
   public  void achieve(String agent, String buyers_key,
                                    Vector goals) {
      Core.ERROR(!agent.equals(context.whoami()), 2, this);
      // achieve received from a remote agent - first check if the item
      // required by the agent can be sold directly; i.e. we are waiting to
      // sell the item.

      // assume only one item in the goal list
      Goal g = (Goal)goals.elementAt(0);
      Fact f = g.getFact();
      String sellers_key;
      DelegationStruct ds = null;
      if ( (sellers_key = waitingToSell(f)) != null ) {
         itemsForSale.remove(sellers_key);
         ds = new DelegationStruct(agent,"cfp",buyers_key,goals);
         msgQueue.addElement(ds);
         Core.DEBUG(2,"Waking seller node with key = " + sellers_key);
         keyTable.put(sellers_key,buyers_key);
         msgWaitTable.wakeup(sellers_key);
         return;
      }

      GraphStruct gs = new GraphStruct(agent,buyers_key,goals);
      g0 graph = new g0();
      notifyGraphMonitors(graph,GRAPH_CREATE);
      graph.run(this,gs);
   }
   
   
   //synchronized
   public  void achieve(Goal goal) {
      // this is a locally called "achieve", hence we do not need to check
      // whether we have anything for sale - no point selling something back
      // to your self

      GraphStruct gs = new GraphStruct(context.whoami(),goal);
      g0 g = new g0();
      notifyGraphMonitors(g,GRAPH_CREATE);
      g.run(this,gs);
   }
   
   // synchronized 
   public void achieve(Goal goal, String key) {
      // this is also a locally called "achieve", hence we do not need to check
      // whether we have anything for sale

      Assert.notFalse( goal.whichType() == Goal.DISCRETE );
      DelegationStruct ds =
         new DelegationStruct(goal.getDesiredBy(),"enact",key,goal);
      auditTable.enact(ds);
   }
   
   
   //synchronized
   public  void buy(Goal goal) {
      buy g = new buy();
      notifyGraphMonitors(g,GRAPH_CREATE);
      GraphStruct gs = new GraphStruct(context.whoami(),goal);
      g.run(this,gs);
   }
   
   //synchronized
   public  void sell(Goal goal) {
      sell g = new sell();
      notifyGraphMonitors(g,GRAPH_CREATE);
      GraphStruct gs = new GraphStruct(context.whoami(),goal);
      g.run(this,gs);
   }

//synchronized
   public  void add(Node node) {
      queue.enqueue(node);
   }

 //synchronized
   public void add(DelegationStruct ds) {
      Core.DEBUG(2,"Engine: new msg received\n" + ds);

      if ( ds.msg_type.equals("result") ) {
         auditTable.goodsReceived(ds);
      }
      else if ( ds.msg_type.equals("payment") ) {
         auditTable.paymentReceived(ds);
      }
      else if ( ds.msg_type.equals("invoice") ) {
         auditTable.invoiceReceived(ds);
      }
      else if ( ds.msg_type.equals("failure") ) {
         int type = auditTable.exception(ds);

         GraphStruct gs = new GraphStruct();
         gs.goal = ds.goals;
         gs.internal = new Vector();
         gs.external = Misc.copyVector(gs.goal);
         gs.key = ds.key;
         gs.agent = context.whoami();
         gs.ignore_agents.addElement(ds.agent);

         double t = Double.MAX_VALUE;
         Goal g;
         for(int i = 0; i < gs.goal.size(); i++ ) {
            g = (Goal)gs.goal.elementAt(i);
            t = Math.min(t,g.getConfirmTime().getTime());
         }

         /* Timeout heuristic */
         gs.timeout = (t-context.now())/(gs.goal.size()+0.5);

         gs.any = new Integer(type);
         g2 graph = new g2();
         notifyGraphMonitors(graph,GRAPH_CREATE);
         graph.run(this,gs);
      }
      else if ( ds.msg_type.equals("cancel") ) {
         auditTable.cancel(ds);
      }
      else if ( ds.msg_type.equals("enact") ) {
         auditTable.enact(ds);
      }
      else {
         msgQueue.addElement(ds);
         msgWaitTable.wakeup(ds.key);
      }
   }


    //synchronized 
   public void addItemForSale(String sellers_key, Fact fact) {
      Core.ERROR(itemsForSale.put(sellers_key,fact) == null, 1, this);
   }
   
   
   //synchronized
   public  void removeItemForSale(String sellers_key) {
      itemsForSale.remove(sellers_key);
      keyTable.remove(sellers_key);
   }
   
   //synchronized 
   public String getBuyersKey(String sellers_key) {
      return (String)keyTable.get(sellers_key);
   }


   protected String waitingToSell(Fact fact) {
      Enumeration keys = itemsForSale.keys();
      String key;
      Fact f;
      Bindings b = new Bindings(context.whoami());
      while( keys.hasMoreElements() ) {
         b.clear();
         key = (String)keys.nextElement();
         f = (Fact)itemsForSale.get(key);
         if ( f.unifiesWith(fact,b) )
            return key;
      }
      return null;
   }


    //synchronized
   public synchronized void wakeup(String key) {
      msgWaitTable.wakeup(key);
   }


    //synchronized 
   public synchronized void replan(PlannerQueryStruct struct, String key) {
      GraphStruct gs = new GraphStruct();

      gs.goal = struct.goals;
      gs.internal = struct.internal;
      gs.external = struct.external;
      gs.agent = context.whoami();
      gs.key = key;

      Goal g;
      double t = Double.MAX_VALUE;
      for(int i = 0; i < gs.goal.size(); i++ ) {
         g = (Goal)gs.goal.elementAt(i);
         t = Math.min(t,g.getConfirmTime().getTime());
      }
      gs.timeout = (t-context.now())/(gs.goal.size()+0.5);
      g1 graph = new g1();
      notifyGraphMonitors(graph,GRAPH_CREATE);
      graph.run(this,gs);
   }
   
   //synchronized 
   public void agentWithAbilityFound(Performative msg) {
      String key = msg.getInReplyTo();
      String content = msg.getContent();
      if ( context.OrganisationDb() != null ) {
         Vector v = ZeusParser.abilityDbItemList(context.OntologyDb(),content);
         context.OrganisationDb().add(v);
         msgWaitTable.wakeup(key);
      }
   }
   
   // synchronized 
  synchronized void  waitForMsg(Node node) {
      msgWaitTable.add(node);
   }
   
   // synchronized
   public synchronized DelegationStruct replyReceived(String key) {
      DelegationStruct ds;
      for( int i = 0; i < msgQueue.size(); i++ ) {
         ds = (DelegationStruct)msgQueue.elementAt(i);
         if ( ds.key.equals(key) ) {
            msgQueue.removeElementAt(i--);
            return ds;
         }
      }
      return null;
   }
   
   //synchronized
   public synchronized DelegationStruct replyReceived(String key,
                                                      String type) {
      DelegationStruct ds;
      for( int i = 0; i < msgQueue.size(); i++ ) {
         ds = (DelegationStruct)msgQueue.elementAt(i);
         if ( ds.key.equals(key) && ds.msg_type.equals(type) ) {
            msgQueue.removeElementAt(i--);
            return ds;
         }
      }
      return null;
   }


    //synchronized 
   public synchronized void replaceReply(Vector replies) {
      if ( replies == null || replies.isEmpty() ) return;
      for(int i = 0; i < replies.size(); i++ )
         msgQueue.addElement(replies.elementAt(i));
   }


 //synchronized
   public Vector repliesReceived(String key) {
      Vector items = new Vector();
      DelegationStruct ds;
      for( int i = 0; i < msgQueue.size(); i++ ) {
         ds = (DelegationStruct)msgQueue.elementAt(i);
         if ( ds.key.equals(key) ) {
            items.addElement(ds);
            msgQueue.removeElementAt(i--);
         }
      }
      return items;
   }
   
   //synchronized
   public synchronized Vector repliesReceived(String key, String type) {
      Vector items = new Vector();
      DelegationStruct ds;
      for( int i = 0; i < msgQueue.size(); i++ ) {
         ds = (DelegationStruct)msgQueue.elementAt(i);
         if ( ds.key.equals(key) && ds.msg_type.equals(type) ) {
            items.addElement(ds);
            msgQueue.removeElementAt(i--);
         }
      }
      return items;
   }

   // ---- Event Monitoring ----
   public void addConversationMonitor(ConversationMonitor monitor,
                                      long event_mask) {
      if ( (event_mask & ConversationEvent.INITIATE_MASK) != 0 )
         conversationMonitors[INITATE_CONVERSATION].add(monitor);
      if ( (event_mask & ConversationEvent.CONTINUE_MASK) != 0 )
         conversationMonitors[CONTINUE_CONVERSATION].add(monitor);
   }
   public void removeConversationMonitor(ConversationMonitor monitor, 
                                         long event_mask) {
      if ( (event_mask & ConversationEvent.INITIATE_MASK) != 0 )
         conversationMonitors[INITATE_CONVERSATION].remove(monitor);
      if ( (event_mask & ConversationEvent.CONTINUE_MASK) != 0 )
         conversationMonitors[CONTINUE_CONVERSATION].remove(monitor);
   }

   public void addNodeMonitor(NodeMonitor monitor, long event_mask) {
      if ( (event_mask & NodeEvent.CREATE_MASK) != 0 )
         nodeMonitors[NODE_CREATE].add(monitor);
      if ( (event_mask & NodeEvent.STATE_CHANGE_MASK) != 0 )
         nodeMonitors[NODE_STATE_CHANGE].add(monitor);
      if ( (event_mask & NodeEvent.DISPOSE_MASK) != 0 )
         nodeMonitors[NODE_DISPOSE].add(monitor);
   }
   public void removeNodeMonitor(NodeMonitor monitor, long event_mask) {
      if ( (event_mask & NodeEvent.CREATE_MASK) != 0 )
         nodeMonitors[NODE_CREATE].remove(monitor);
      if ( (event_mask & NodeEvent.STATE_CHANGE_MASK) != 0 )
         nodeMonitors[NODE_STATE_CHANGE].remove(monitor);
      if ( (event_mask & NodeEvent.DISPOSE_MASK) != 0 )
         nodeMonitors[NODE_DISPOSE].remove(monitor);
   }

   public void addGraphMonitor(GraphMonitor monitor, long event_mask) {
      if ( (event_mask & GraphEvent.CREATE_MASK) != 0 )
         graphMonitors[GRAPH_CREATE].add(monitor);
      if ( (event_mask & GraphEvent.STATE_CHANGE_MASK) != 0 )
         graphMonitors[GRAPH_STATE_CHANGE].add(monitor);
      if ( (event_mask & GraphEvent.DISPOSE_MASK) != 0 )
         graphMonitors[GRAPH_DISPOSE].add(monitor);
   }
   public void removeGraphMonitor(GraphMonitor monitor, long event_mask) {
      if ( (event_mask & GraphEvent.CREATE_MASK) != 0 )
         graphMonitors[GRAPH_CREATE].remove(monitor);
      if ( (event_mask & GraphEvent.STATE_CHANGE_MASK) != 0 )
         graphMonitors[GRAPH_STATE_CHANGE].remove(monitor);
      if ( (event_mask & GraphEvent.DISPOSE_MASK) != 0 )
         graphMonitors[GRAPH_DISPOSE].remove(monitor);
   }

   public void addArcMonitor(ArcMonitor monitor, long event_mask) {
      if ( (event_mask & ArcEvent.CREATE_MASK) != 0 )
         arcMonitors[ARC_CREATE].add(monitor);
      if ( (event_mask & ArcEvent.SUCCEED_MASK) != 0 )
         arcMonitors[ARC_SUCCEED].add(monitor);
      if ( (event_mask & ArcEvent.FAIL_MASK) != 0 )
         arcMonitors[ARC_FAIL].add(monitor);
      if ( (event_mask & ArcEvent.DISPOSE_MASK) != 0 )
         arcMonitors[ARC_DISPOSE].add(monitor);
   }
   public void removeArcMonitor(ArcMonitor monitor, long event_mask) {
      if ( (event_mask & ArcEvent.CREATE_MASK) != 0 )
         arcMonitors[ARC_CREATE].remove(monitor);
      if ( (event_mask & ArcEvent.SUCCEED_MASK) != 0 )
         arcMonitors[ARC_SUCCEED].remove(monitor);
      if ( (event_mask & ArcEvent.FAIL_MASK) != 0 )
         arcMonitors[ARC_FAIL].remove(monitor);
      if ( (event_mask & ArcEvent.DISPOSE_MASK) != 0 )
         arcMonitors[ARC_DISPOSE].remove(monitor);
   }

   void notifyConversationMonitors(String comms_key, String sender, 
                                   String receiver, String msg_type, 
			           Vector data, int type) {
      if ( conversationMonitors[type].isEmpty() ) return;

      ConversationMonitor monitor;
      ConversationEvent event;
      Enumeration enum = conversationMonitors[type].elements();

      switch(type) {
         case INITATE_CONVERSATION:
              event = new ConversationEvent(this,this,comms_key,sender, 
                                            receiver,msg_type,data,
                                            ConversationEvent.INITIATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ConversationMonitor)enum.nextElement();
                 monitor.conversationInitiatedEvent(event);
              }
              break;
         case CONTINUE_CONVERSATION:
              event = new ConversationEvent(this,this,comms_key,sender, 
                                            receiver,msg_type,data,
                                            ConversationEvent.CONTINUE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ConversationMonitor)enum.nextElement();
                 monitor.conversationContinuedEvent(event);
              }
              break;
      }
   }

   void notifyConversationMonitors(String comms_key, String sender, 
                                   String receiver, String msg_type, 
			           String data_type, String data_key,
                                   Vector data, int type) {
      if ( conversationMonitors[type].isEmpty() ) return;

      ConversationMonitor monitor;
      ConversationEvent event;
      Enumeration enum = conversationMonitors[type].elements();

      switch(type) {
         case INITATE_CONVERSATION:
              event = new ConversationEvent(this,this,comms_key,sender, 
                                            receiver,msg_type,
                                            data_type, data_key, data,
                                            ConversationEvent.INITIATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ConversationMonitor)enum.nextElement();
                 monitor.conversationInitiatedEvent(event);
              }
              break;
         case CONTINUE_CONVERSATION:
              event = new ConversationEvent(this,this,comms_key,sender, 
                                            receiver,msg_type,
                                            data_type, data_key, data,
                                            ConversationEvent.CONTINUE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ConversationMonitor)enum.nextElement();
                 monitor.conversationContinuedEvent(event);
              }
              break;
      }
   }

   void notifyNodeMonitors(Node node, int type) {
      if ( nodeMonitors[type].isEmpty() ) return;

      NodeMonitor monitor;
      NodeEvent event;
      Enumeration enum = nodeMonitors[type].elements();

      switch(type) {
         case NODE_CREATE:
              event = new NodeEvent(this,node,NodeEvent.CREATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (NodeMonitor)enum.nextElement();
                 monitor.nodeCreatedEvent(event);
              }
              break;
         case NODE_DISPOSE:
              event = new NodeEvent(this,node,NodeEvent.DISPOSE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (NodeMonitor)enum.nextElement();
                 monitor.nodeDisposedEvent(event);
              }
              break;
         case NODE_STATE_CHANGE:
              event = new NodeEvent(this,node,NodeEvent.STATE_CHANGE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (NodeMonitor)enum.nextElement();
                 monitor.nodeStateChangedEvent(event);
              }
              break;
      }
   }

   void notifyGraphMonitors(Graph graph, int type) {
      if ( graphMonitors[type].isEmpty() ) return;

      GraphMonitor monitor;
      GraphEvent event;
      Enumeration enum = graphMonitors[type].elements();

      switch(type) {
         case GRAPH_CREATE:
              event = new GraphEvent(this,graph,GraphEvent.CREATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (GraphMonitor)enum.nextElement();
                 monitor.graphCreatedEvent(event);
              }
              break;
         case GRAPH_DISPOSE:
              event = new GraphEvent(this,graph,GraphEvent.DISPOSE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (GraphMonitor)enum.nextElement();
                 monitor.graphDisposedEvent(event);
              }
              break;
         case GRAPH_STATE_CHANGE:
              event = new GraphEvent(this,graph,GraphEvent.STATE_CHANGE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (GraphMonitor)enum.nextElement();
                 monitor.graphStateChangedEvent(event);
              }
              break;
      }
   }

   void notifyArcMonitors(Arc arc, Node node, int type) {
      if ( arcMonitors[type].isEmpty() ) return;

      ArcMonitor monitor;
      ArcEvent event;
      Enumeration enum = arcMonitors[type].elements();

      switch(type) {
         case ARC_CREATE:
              event = new ArcEvent(this,arc,node,ArcEvent.CREATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ArcMonitor)enum.nextElement();
                 monitor.arcCreatedEvent(event);
              }
              break;
         case ARC_DISPOSE:
              event = new ArcEvent(this,arc,node,ArcEvent.DISPOSE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ArcMonitor)enum.nextElement();
                 monitor.arcDisposedEvent(event);
              }
              break;
         case ARC_SUCCEED:
              event = new ArcEvent(this,arc,node,ArcEvent.SUCCEED_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ArcMonitor)enum.nextElement();
                 monitor.arcSucceededEvent(event);
              }
              break;
         case ARC_FAIL:
              event = new ArcEvent(this,arc,node,ArcEvent.FAIL_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ArcMonitor)enum.nextElement();
                 monitor.arcFailedEvent(event);
              }
              break;
      }
   }
}


