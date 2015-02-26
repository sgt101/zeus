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



/*
 * @(#)MsgHandler.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.util.DelegationStruct;
import zeus.actors.rtn.Engine;
import zeus.actors.event.*;
import zeus.agents.*;
import gnu.regexp.*;
import java.lang.reflect.*;

/**
 * The Message Handler component implements each Zeus agent's internal "mail
 * sorting office", continually checking the incoming-message-queue of the
 * {@link MailBox} for new messages, and forwarding them to the relevant
 * components within the agent. <p>
 *
 * The Message Handler's behaviour is controlled by two factors: first,
 * whether a new message represents the start of a new dialogue or it is part
 * of an existing dialogue; and second, on the message processing rules
 * registered with it by other components of the agent.<p>
 * Change Log <br>
 * ----------<p>
 * 22/08/00 Altered spelling of organization to correct version for about 6 lines - Simon
 *
 */

public class MsgHandler extends Thread {
    protected HSet[] eventMonitor = new HSet[4];
    
    protected static final int ADD    = 0;
    protected static final int FIRE   = 1;
    protected static final int DELETE = 2;
    protected static final int FAIL   = 3;
    
    protected Hashtable     addressQueryTable = new Hashtable();
    protected Hashtable     abilityQueryTable = new Hashtable();
    protected Hashtable     reportQueryTable = new Hashtable();
    
    protected ReportLogger  reportLogger = null;
    protected AbilityLogger abilityLogger = null;
    
    protected boolean       processing;
    protected AgentContext  context = null;
    protected RootNode      MessageRootNode = null;
    Vector                  attributeNodes = new Vector();
    static int cleanupInterval = 100;
    
    protected Queue handleQ = null;
    
    // meaningless init  to allow rearch
    public MsgHandler() {
        ;
    }
    
    
    public MsgHandler(AgentContext context) {
        super();
        handleQ = new Queue("msg handler processing Q");
        Assert.notNull(context);
        this.context = context;
        context.set(this);
        
        for(int i = 0; i < eventMonitor.length; i++ )
            eventMonitor[i] = new HSet();
        
        MessageRootNode = new RootNode(this);
        
        /* Initialize rule base */
        String[][] msg_pattern = {
            /* 00 */  { "type", "request", "content", "\\Aregister\\Z" },
            /* 01 */  { "type", "request", "content", "\\Aderegister\\Z" },
            
            /* 02 */  { "type", "subscribe", "content", "\\Alog_message\\Z"},
            /* 03 */  { "type", "subscribe", "content", "\\Alog_report\\Z"},
            /* 04 */  { "type", "subscribe", "content", "\\Alog_address\\Z"},
            
            /* 05 */  { "type", "cancel", "content", "\\Alog_message\\Z"},
            /* 06 */  { "type", "cancel", "content", "\\Alog_report\\Z"},
            /* 07 */  { "type", "cancel", "content", "\\Alog_address\\Z"},
            
            /* 08 */  { "type", "query-ref", "content", "\\Ayour_abilities\\Z"},
            /* 09 */  { "type", "query-ref", "content", "\\Aaddress_of(\\s+)(\\w)(.*)\\Z"},
            /* 10 */  { "type", "query-ref", "content", "\\Ayour_relations\\Z"},
            
            /* 11 */  { "type", "query-ref", "content", "\\Ahas_ability(\\s+)(.*)\\Z"},
            
            /* 12 */  { "type", "inform", "content", "\\Aisa_facilitator(\\s+)(\\w)(.*)\\Z"},
            
            /* 13 */  { "type", "request", "content", "\\Akill_yourself\\Z"},
            
            
            /* 14 */  { "type", "subscribe", "content", "\\Ahas_ability(\\s+)(.*)\\Z"},
            /* 15 */  { "type", "cancel", "content", "\\Ahas_ability(\\s+)(.*)\\Z"},
            /* 16 */  { "type", "inform", "content", "\\Amy_abilities(\\s+)(.*)\\Z"},
            /* 17 */  { "type", "inform", "content", "\\Adata(\\s+)(.*)\\Z"},
            
            // UNUSED
            /* 00 */  { "type", "request", "content", "resume_goals\\s*"},
            /* 00 */  { "type", "request", "content", "cancel_goals\\s*"},
            
            /* 00 */  { "type", "request", "content", "db_del\\s*"},
            /* 00 */  { "type", "request", "content", "optimize\\s"},
            /* 00 */  { "type", "request", "content", "add_task\\s*"},
            /* 00 */  { "type", "request", "content", "modify_task\\s*"},
            /* 00 */  { "type", "request", "content", "delete_task\\s*"},
            
            /* 00 */  { "type", "subscribe", "content", "log_goal"},
            /* 00 */  { "type", "subscribe", "content", "log_task"},
            /* 00 */  { "type", "subscribe", "content", "log_state"},
            
            /* 00 */  { "type", "cancel", "content", "log_goal"},
            /* 00 */  { "type", "cancel", "content", "log_task"},
            /* 00 */  { "type", "cancel", "content", "log_state"},
        };
        
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[0], this, "register"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[1], this, "deregister"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[2], this, "log_message"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[3], this, "log_report"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[4], this, "log_address"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[5], this, "cancel_message"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[6], this, "cancel_report"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[7], this, "cancel_address"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[8], this, "your_abilities"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[9], this, "address_of"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[10],this, "your_relations"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[11],this, "has_ability"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[12],this, "isa_facilitator"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[13],this, "kill_yourself"));
        
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[14],this, "log_specified_ability"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[15],this, "cancel_specified_ability"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[16],this, "add_agents_abilities"));
        addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern[17],this, "add_information"));
        this.setName("MsgHandler");
        this.start();
    }
    
    public AgentContext getAgentContext() {
        return context;
    }
    
    public void lowerStatus() {
        
        // this.setPriority(Thread.NORM_PRIORITY-2);
    }
    
    public void stopProcessing() {
        processing = false;
    }
    
    
    
    /**
     * refactored to help readability - this method loops until it gets
     * a valid MailBox from the agent, so that the system can initialise in
     * the right order
     */
    private MailBox waitForSetUp() {
        MailBox mbox = context.MailBox();
        while ( mbox == null ) {
            try {
                sleep(300); // sleep until mbox is built
                mbox = context.MailBox();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mbox;
    }
    
    
    /**
     * thread main method - try and read incomming messages until
     * the universe ends
     */
    public void run() {
        try {
            
            Performative msg;
            processing = true;
            MailBox mbox = waitForSetUp();
            int count = 0;
            while(processing) {
                synchronized (handleQ) {
                    msg = (Performative) handleQ.dequeue();
                    debug("processing:" + msg.toString());
                    Core.DEBUG(1,"MsgHandler processing msg from ...\n" + msg.getSender());
                    try {
                        MessageRootNode.evaluate(null, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 30/09/01 - don't fall out of loop if this node is incorrectly instantiated.
                    }
                }
                yield();
                count++;
                if (count > 100) {
                    
                    System.gc();
                    count = 0;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * in old versions of Zeus the msgHandler was responsible for ensuring that  incomming
     * messages were processed. However this approach meant that reengineering of the msgHandler would
     * be necessary whenever a mailbox was built that had more than one server - basically
     * this was a problem in terms of both coupling the mailbox,server and msgHandler
     * objects and reducing the coheasion of the msgHandler. <p>
     * solution: processMessage is a method that can be called by any
     * object to see if message rules have been set that apply to this message. The
     * servers (inboxes) are responsible for calling this method which will process the message
     * for them - or not as they like. <p>
     * This method puts the message on a queue, which is internal to the msgHandler. The thread
     * run by msgHandler will then process it. //synch
     */
    public synchronized void processMessage(Performative msg) {
        handleQ.enqueue(msg);
    }
    
    
    
    public Queue getMessageQueue() {
        return (handleQ);
    }
    
    
    //added sycn 4/6/03
    public synchronized void addRule(MessageRule r) {
        MsgNode node = null;
        MsgNode term;
        boolean found;
        Core.DEBUG(1,"Compiling msg processing rule ...\n\t" + r);
        MessagePattern token = r.getPattern();
        
        // link to RootNode
        term = MessageRootNode;
        Core.DEBUG(3,"=r");
        Core.DEBUG(4,"=r[" + term + "]");
        term.use_count++;
        node = term;
        
        // For each attribute value create attribute node
        // check if 'attributeNodes' already contains node
        RE[] value = token.listValues();
        String[] attribute = token.listAttributes();
        
        for(int k = 0; k < value.length; k++ ) {
            if ( value[k] != null  ) {
                term = new AttributeNode(this,attribute[k],value[k]);
                found = false;
                for(int j = 0; !found && j < attributeNodes.size(); j++ ) {
                    found = term.equals(attributeNodes.elementAt(j));
                    if (found)
                        term = (MsgNode)attributeNodes.elementAt(j);
                }
                if ( !found ) {
                    attributeNodes.addElement(term);
                    Core.DEBUG(3,"+m");
                    Core.DEBUG(4,"+m[" + term + "]");
                }
                else {
                    Core.DEBUG(3,"=m");
                    Core.DEBUG(4,"=m[" + term + "]");
                }
                term.use_count++;
                node.addSuccessor(r.getName(),term);
                node = term;
            }
        }
        
        // now, all that is left is the action node
        // first determine the last node: which should be 'term'
        ActionNode action = new ActionNode(this,r.getName(),r.getAction());
        action.use_count++;
        term.addSuccessor(r.getName(),action);
        Core.DEBUG(3,"+a");
        Core.DEBUG(4,"+a[" + action + "]");
        notifyMonitors(ADD,new MessageHandlerEvent(this,r,MessageHandlerEvent.ADD_MASK));
    }
    
    //sync??
    public void removeRule(String name) {
        MessageRootNode.remove(name);
        notifyMonitors(DELETE,new MessageHandlerEvent(this,name,MessageHandlerEvent.DELETE_MASK));
    }
    
    
    public synchronized Object execRule(String rule, Object object, String method, Performative input) {
        Class c = object.getClass();
        try {
            Class[] parameter_types = new Class[1];
            parameter_types[0] = input.getClass();
            Object[] arglist = new Object[1];
            arglist[0] = input;
            
            Core.DEBUG(2,"Invoking method " + method + " of class " +
            c.getName() + " with parameter " + input);
            
            Method m = c.getMethod(method,parameter_types);
            notifyMonitors(FIRE,new MessageHandlerEvent(this,rule,object,method,input,MessageHandlerEvent.FIRE_MASK));
            return m.invoke(object,arglist);
        }
        catch(Throwable err) {
            Core.USER_ERROR("MsgHandler - Error invoking target: [" + rule +
            "::" + object.getClass().getName() + "." + method +
            "()]\nwith args...\n" + input + "\n" + err);
            notifyMonitors(FAIL,new MessageHandlerEvent(this,rule,object,method,input,MessageHandlerEvent.FAIL_MASK));
        }
        return null;
    }
    
    protected void adviseAll(Address addr) {
        Enumeration enum = addressQueryTable.keys();
        String name, reply_with;
        Performative reply;
        MailBox mbox = context.MailBox();
        
        while ( enum.hasMoreElements() ) {
            name = (String) enum.nextElement();
            reply_with = (String) addressQueryTable.get(name);
            
            reply = new Performative("inform");
            reply.setReceiver(name);
            reply.setContent(addr.toString());
            reply.setInReplyTo(reply_with);
            mbox.sendMsg(reply);
        }
    }
    
    public void register(Performative msg) {
        try {
            if (context.whoami().equalsIgnoreCase("ANServer")) {
                Performative forwarder = new Performative(msg);
                forwarder.setReceiver("ams");
                forwarder.send(context);
            }
            String reply_with, content;
            if ( (reply_with = msg.getReplyWith()) == null ) {
                refuse(msg,"no reply-with tag");
                return;
            }
            
            if ( (content = context.Clock().initData()) != null ) {
                Performative reply = new Performative("inform");
                reply.setReceiver(msg.getSender());
                reply.setInReplyTo(reply_with);
                reply.setContent(content + " " + System.currentTimeMillis());
                context.MailBox().sendMsg(reply);
            }
            adviseAll(msg.getAddress());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deregister(Performative msg) {
        Address address;
        
        String name = msg.getSender();
        if ( (address = msg.getAddress()) != null )
            context.MailBox().del(address);
        
        if ( addressQueryTable.containsKey(name) )
            addressQueryTable.remove(name);
        
        String reply_with = msg.getReplyWith();
        if ( reply_with != null ) {
            Performative reply = new Performative("inform");
            reply.setReceiver(msg.getSender());
            reply.setInReplyTo(reply_with);
            reply.setContent("done deregister");
            context.MailBox().sendMsg(reply);
        }
        debug("Deregister message : " + msg.toString());
    }
    
    public void kill_yourself(Performative msg) {
        // inform done; send deregistration msg
        context.Agent().notifyMonitors(BasicAgent.DEATH);
        System.exit(0);
    }
    
    public void add_task(Performative msg) {
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        if ( context.TaskDb() != null ) {
            Vector List = ZeusParser.taskList(context.OntologyDb(),hd.data());
            context.TaskDb().add(List);
            inform(msg,"done add_task");
        }
        else
            refuse(msg,"no task database");
    }
    
    public void modify_task(Performative msg) {
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        if ( context.TaskDb() != null ) {
            Vector List = ZeusParser.taskList(context.OntologyDb(),hd.data());
            context.TaskDb().add(List);
            inform(msg,"done modify_task");
        }
        else
            refuse(msg,"no task database");
    }
    
    
    public void delete_task(Performative msg) {
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        if ( context.TaskDb() != null ) {
            context.TaskDb().deleteTask(hd.data());
            inform(msg,"done delete_task");
        }
        else
            refuse(msg,"no task database");
    }
    
    
    public void cancel_message(Performative msg) {
        context.MailBox().stopLoggingMessages(msg.getSender());
    }
    
    
    public void cancel_report(Performative msg) {
        reportQueryTable.remove(msg.getSender());
        if ( reportQueryTable.isEmpty() ) {
            reportLogger.stopLogging();
            reportLogger = null;
        }
    }
    
    public void cancel_specified_ability(Performative msg) {
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        if ( hd.data() == null ) {
            refuse(msg,"no key specified");
            return;
        }
        abilityQueryTable.remove(hd.data());
        if ( abilityQueryTable.isEmpty() ) {
            abilityLogger.stopLogging();
            abilityLogger = null;
        }
    }
    
    public void cancel_address(Performative msg) {
        addressQueryTable.remove(msg.getSender());
    }
    
    public void log_message(Performative msg) {
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        context.MailBox().logMessages(msg.getSender(),reply_with);
    }
    
    public void log_report(Performative msg) {
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        else if ( context.Planner() == null ) {
            refuse(msg,"no planner");
            return;
        }
        reportQueryTable.put(msg.getSender(),reply_with);
        if ( reportLogger == null ) reportLogger = new ReportLogger();
    }
    
    protected class ReportLogger extends PlanStepAdapter {
        public ReportLogger() {
            context.Planner().addPlanStepMonitor(this,PlanStepEvent.STATE_CHANGE_MASK);
        }
        public void stopLogging() {
            context.Planner().addPlanStepMonitor(this,PlanStepEvent.STATE_CHANGE_MASK);
        }
        public void planStepStateChangedEvent(PlanStepEvent event) {
            if ( reportQueryTable.isEmpty() ) return;
            
            PlanRecord rec = event.getPlanRecord();
            switch( rec.getState() ) {
                case PlanRecord.FREE:
                case PlanRecord.TEMP:
                case PlanRecord.TENTATIVE:
                    break;
                    
                case PlanRecord.FIRM:
                case PlanRecord.RUNNING:
                case PlanRecord.FAILED:
                case PlanRecord.COMPLETED:
                case PlanRecord.JEOPARDY:
                case PlanRecord.AGREEMENT:
                    ReportRec report = rec.report();
                    Enumeration enum = reportQueryTable.keys();
                    String agent, reply_with;
                    Performative reply;
                    while( enum.hasMoreElements() ) {
                        agent = (String)enum.nextElement();
                        reply_with = (String)reportQueryTable.get(agent);
                        
                        reply = new Performative("inform");
                        reply.setContent(report.toString());
                        reply.setReceiver(agent);
                        reply.setInReplyTo(reply_with);
                        context.MailBox().sendMsg(reply);
                    }
                    break;
            }
        }
    }
    
    public void log_address(Performative msg) {
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        addressQueryTable.put(msg.getSender(),reply_with);
        Vector List = context.MailBox().listAddresses();
        if ( !List.isEmpty() ) {
            Performative reply = new Performative("inform");
            reply.setReceiver(msg.getSender());
            reply.setInReplyTo(reply_with);
            reply.setContent(Misc.concat(List));
            context.MailBox().sendMsg(reply);
        }
    }
    
    public void log_specified_ability(Performative msg) {
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        if ( hd.data() == null ) {
            refuse(msg,"no ability specified");
            return;
        }
        else if ( context.OrganisationDb() == null ) {
            // 1.04 change of spelling by simon
            refuse(msg,"no organization database");
            return;
        }
        AbilitySpec a = ZeusParser.abilitySpec(context.OntologyDb(),hd.data());
        if ( a == null ) {
            refuse(msg,"no ability specified");
            return;
        }
        Vector List = context.OrganisationDb().findAll(a);
        if ( !List.isEmpty() ) {
            Performative reply = new Performative("inform");
            reply.setContent(Misc.concat(List));
            reply.setReceiver(msg.getSender());
            reply.setInReplyTo(reply_with);
            context.MailBox().sendMsg(reply);
        }
        
        Object[] data = new Object[2];
        data[0] = msg.getSender();
        data[1] = a;
        
        abilityQueryTable.put(reply_with,data);
        if ( abilityLogger == null ) abilityLogger = new AbilityLogger();
    }
    
    class AbilityLogger extends AbilityAdapter {
        public AbilityLogger() {
            context.OrganisationDb().addAbilityMonitor(this,AbilityEvent.ADD_MASK);
        }
        public void stopLogging() {
            context.OrganisationDb().removeAbilityMonitor(this,AbilityEvent.ADD_MASK);
        }
        public void abilityAddedEvent(AbilityEvent event) {
            if ( abilityQueryTable.isEmpty() ) return;
            
            Enumeration enum = abilityQueryTable.keys();
            String reply_with;
            Performative reply;
            Object[] data;
            AbilitySpec b, a = event.getAbility();
            
            Fact f2, f1 = a.getFact();
            int   t2, t1 = a.getTime();
            double  c2, c1 = a.getCost();
            
            Bindings bindings = new Bindings(context.whoami());
            
            while( enum.hasMoreElements() ) {
                bindings.clear();
                reply_with = (String)enum.nextElement();
                data = (Object[])abilityQueryTable.get(reply_with);
                
                b = (AbilitySpec)data[1];
                f2 = b.getFact();
                t2 = b.getTime();
                c2 = b.getCost();
                if ( (t1 == 0 || t2 <= t1) && (c1 == 0 || c2 <= c1) &&
                f2.unifiesWith(f1,bindings) ) {
                    reply = new Performative("inform");
                    reply.setContent(event.getAbilityDbItem().toString());
                    reply.setReceiver((String)data[0]);
                    reply.setInReplyTo(reply_with);
                    context.MailBox().sendMsg(reply);
                }
            }
        }
    }
    
    public void address_of(Performative msg) {
        String content = msg.getContent();
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        else if ( content == null ) {
            failure(msg,"no content");
            return;
        }
        MsgContentHandler hd = new MsgContentHandler(content);
        Address addr;
        if ( hd.data() != null &&
        (addr = context.MailBox().lookup(hd.data())) != null ) {
            Performative reply = new Performative("inform");
            reply.setReceiver(msg.getSender());
            reply.setInReplyTo(reply_with);
            reply.setContent(addr.toString());
            context.MailBox().sendMsg(reply);
        }
        else
            failure(msg,"address not known");
    }
    
    public void your_abilities(Performative msg) {
        try {
        String content = msg.getContent();
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        else if ( context.OrganisationDb() == null ) {
            // 1.04 change of spelling by simon
            refuse(msg,"no organization database");
            return;
        }
        Vector List = context.OrganisationDb().abilitiesOf(context.whoami());
        if ( List.isEmpty() ) {
            failure(msg,"no abilities");
            return;
        }
        Performative reply = new Performative("inform");
        reply.setContent(Misc.concat(List));
        reply.setReceiver(msg.getSender());
        reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
        sendServiceDescriptions(msg.getSender(), msg.getReplyWith());
        }
        catch (Throwable t) {
            t.printStackTrace(); 
        }
    }
    
    
    protected void sendServiceDescriptions(String target, String replyWith) {

       try {
        TaskDb tasks = context.getTaskDb();
        Enumeration keys = tasks.keys();
        while (keys.hasMoreElements()) {
            Hashtable ofType = (Hashtable) tasks.get(keys.nextElement());
            Enumeration allElements = ofType.elements();
            while (allElements.hasMoreElements()) { 

	      Task task = (Task) allElements.nextElement();

	      sendProfile(task, context, target, replyWith);
	      sendInstance(task, context, target, replyWith);
	      sendInstanceRange(task, context, target, replyWith);
	      sendProcess(task, context, target, replyWith);

            }//end while
        }// end while
       }
       catch (Exception e) { 
           e.printStackTrace(); 
       }
    }
    
  private void sendProfile(Task task, AgentContext context, String target,
			   String replyWith) {

    String description = task.getServiceDesc(context); 
    if(description != null && description.length() > 0) {
      Performative sendMsg = new Performative("inform");
      sendMsg.setReceiver(target);
      sendMsg.setInReplyTo(replyWith);
      sendMsg.setContent("( :serviceProfile (\"" + description + "\"))");
      sendMsg.send(context);
    }
  }
    
  private void sendInstance(Task task, AgentContext context, String target,
			    String replyWith) {

    String instanceDetails = task.getInstanceDetails(context);
    if(instanceDetails != null && instanceDetails.length() > 0) {
      Performative instanceDesc = new Performative("inform");
      instanceDesc.setReceiver(target);
      instanceDesc.setInReplyTo(replyWith);
      instanceDesc.setContent("( :serviceInstance (\"" +
			      instanceDetails + "\"))");
      instanceDesc.send(context);
    }
  }
    
  private void sendInstanceRange(Task task, AgentContext context,
				 String target, String replyWith) {

    String instanceRange = task.getInstanceRange(context);
    if(instanceRange != null && instanceRange.length() > 0) {
      Performative instanceDesc = new Performative("inform");
      instanceDesc.setReceiver(target);
      instanceDesc.setInReplyTo(replyWith);
      instanceDesc.setContent("( :serviceRange (:task " + task.getName() +
			      " :content \"" + instanceRange + "\"))");
      instanceDesc.send(context);
    }
  }

  private void sendProcess(Task task, AgentContext context, String target,
			   String replyWith) {

    String processModel = task.getProcessModel(context);
    if(processModel != null && processModel.length() > 0) {
      Performative process = new Performative("inform");
      process.setReceiver(target);
      process.setInReplyTo(replyWith);
      process.setContent("( :processModel (\"" + processModel + "\"))");
      process.send(context);
    }
  }
    
    public void your_relations(Performative msg) {
        String content = msg.getContent();
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        else if ( context.OrganisationDb() == null ) {
            // 1.04 change of spelling by simon
            refuse(msg,"no organization database");
            return;
        }
        Vector List = context.OrganisationDb().allRelations();
        if ( List.isEmpty() ) {
            failure(msg,"no relations");
            return;
        }
        Performative reply = new Performative("inform");
        reply.setContent(Misc.concat(List));
        reply.setReceiver(msg.getSender());
        reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
    }
    
    public void has_ability(Performative msg) {
        String content = msg.getContent();
        String reply_with = msg.getReplyWith();
        if ( reply_with == null ) {
            refuse(msg,"no reply-with key");
            return;
        }
        MsgContentHandler hd = new MsgContentHandler(content);
        if ( hd.data() == null ) {
            refuse(msg,"no ability specified");
            return;
        }
        else if ( context.OrganisationDb() == null ) {
            // 1.04 change of spelling by simon
            refuse(msg,"no organization database");
            return;
        }
        AbilitySpec a = ZeusParser.abilitySpec(context.OntologyDb(),hd.data());
        if ( a == null ) {
            refuse(msg,"no ability specified");
            return;
        }
        Vector List = context.OrganisationDb().findAll(a);
        if ( List.isEmpty() ) {
            failure(msg,"no abilities known");
            return;
        }
        Performative reply = new Performative("inform");
        reply.setContent(Misc.concat(List));
        reply.setReceiver(msg.getSender());
        reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
    }
    
    public void isa_facilitator(Performative msg) {
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        context.addFacilitator(hd.data());
    }
    
    public void add_agents_abilities(Performative msg) {
        MsgContentHandler hd = new MsgContentHandler(msg.getContent());
        Vector List = ZeusParser.abilitySpecList(context.OntologyDb(),hd.data());
        String agent = msg.getSender();
        context.OrganisationDb().add(agent,List);
    }
    
    public void add_information(Performative msg) {
        try {
            MsgContentHandler hd = new MsgContentHandler(msg.getContent());
            Vector List = ZeusParser.factList(context.OntologyDb(),hd.data());
            // **BUG**
            // should re-reference variables in factList before adding to my database
            context.ResourceDb().add(dereference(List));
        }
        catch(Exception e) {
            //e.printStackTrace();
        }
    }
    
    protected void refuse(Performative msg, String content) {
        Performative reply = new Performative("refuse");
        reply.setReceiver(msg.getSender());
        reply.setContent(content);
        String reply_with = msg.getReplyWith();
        if ( reply_with != null )
            reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
    }
    
    protected void failure(Performative msg, String content) {
        Performative reply = new Performative("failure");
        reply.setReceiver(msg.getSender());
        reply.setContent(content);
        String reply_with = msg.getReplyWith();
        if ( reply_with != null )
            reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
    }
    
    protected void not_understood(Performative msg, String content) {
        Performative reply = new Performative("failure");
        reply.setReceiver(msg.getSender());
        reply.setContent(content);
        String reply_with = msg.getReplyWith();
        if ( reply_with != null )
            reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
    }
    
    protected void inform(Performative msg, String content) {
        Performative reply = new Performative("inform");
        reply.setReceiver(msg.getSender());
        reply.setContent(content);
        String reply_with = msg.getReplyWith();
        if ( reply_with != null )
            reply.setInReplyTo(reply_with);
        context.MailBox().sendMsg(reply);
    }
    
    
    /**
     * If your code needs to react to changes in the MessageHandler use this
     * to add a MessageHandlerMonitor. This provides a programatic alternative to
     * writing reaction rules
     */
    public void addMessageHandlerMonitor(MessageHandlerMonitor monitor,
    long event_type) {
        if ( (event_type & MessageHandlerEvent.ADD_MASK) != 0 )
            eventMonitor[ADD].add(monitor);
        if ( (event_type & MessageHandlerEvent.FIRE_MASK) != 0 )
            eventMonitor[FIRE].add(monitor);
        if ( (event_type & MessageHandlerEvent.DELETE_MASK) != 0 )
            eventMonitor[DELETE].add(monitor);
        if ( (event_type & MessageHandlerEvent.FAIL_MASK) != 0 )
            eventMonitor[FAIL].add(monitor);
    }
    
    public void removeMessageHandlerMonitor(MessageHandlerMonitor monitor,
    long event_type) {
        if ( (event_type & MessageHandlerEvent.ADD_MASK) != 0 )
            eventMonitor[ADD].remove(monitor);
        if ( (event_type & MessageHandlerEvent.FIRE_MASK) != 0 )
            eventMonitor[FIRE].remove(monitor);
        if ( (event_type & MessageHandlerEvent.DELETE_MASK) != 0 )
            eventMonitor[DELETE].remove(monitor);
        if ( (event_type & MessageHandlerEvent.FAIL_MASK) != 0 )
            eventMonitor[FAIL].remove(monitor);
    }
    
    protected void notifyMonitors(int type, MessageHandlerEvent event) {
        if ( eventMonitor[type].isEmpty() ) return;
        
        MessageHandlerMonitor monitor;
        Enumeration enum = eventMonitor[type].elements();
        
        switch(type) {
            case ADD:
                while( enum.hasMoreElements() ) {
                    monitor = (MessageHandlerMonitor)enum.nextElement();
                    monitor.messageRuleAddedEvent(event);
                }
                break;
            case FIRE:
                while( enum.hasMoreElements() ) {
                    monitor = (MessageHandlerMonitor)enum.nextElement();
                    monitor.messageRuleFiredEvent(event);
                }
                break;
            case DELETE:
                while( enum.hasMoreElements() ) {
                    monitor = (MessageHandlerMonitor)enum.nextElement();
                    monitor.messageRuleDeletedEvent(event);
                }
                break;
            case FAIL:
                while( enum.hasMoreElements() ) {
                    monitor = (MessageHandlerMonitor)enum.nextElement();
                    monitor.messageRuleFailedEvent(event);
                }
                break;
        }
    }
    
    /**
     * Replication to handle the problem of variable clashes when
     * agents communicate with one another with variables in the
     * content data;
     */
    protected final Vector dereference(Vector input) {
        Fact f;
        Goal g;
        Vector result = new Vector();
        try {
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
        }catch (Exception e) {
            // e.printStackTrace();
        }
        
        return result;
    }
    
    private void debug(String str) {
     //   System.out.println("Free memory = " +Runtime.getRuntime().freeMemory());
    //    System.out.println("MsgHandler>> " + str);
    }
    
}
