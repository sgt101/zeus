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
    * public limited company are Copyright 1996-2001. All Rights Reserved.
    * 
    * THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
    */


package zeus.agents;

import zeus.concepts.*;
import zeus.actors.intrays.*; 
import zeus.actors.outtrays.*;
import zeus.actors.factories.*;
import zeus.actors.*;
import zeus.util.*;
import java.net.*; 
import java.util.*; 


/**
    SimpleAPI is intended to provide a simple way to program a 
    Zeus agent. The intention is that the methods that 
    are implemented here will allow a novice agent programmer to 
    easily perform a number of the most common task that they will 
    need to build a functional agent. <p>
    @author Simon Thompson
    @since 1.1
    */
public class SimpleAPI {
    
    private boolean messQ = false; 
    private Queue messageQueue = null; 
    private AgentContext agentContext = null; 


    /** 
        sendMessage can be used to send a message to another agent. 
        @param performative     the type of message ie. inform,confirm, cfp
        @param content      the content string in the message - what it is that 
        you are sending to the other agent
        @param target       the agent that you are sending this message to. The form 
        of this string should follow the traditional AID or tcp/ip format 
        ie. transport://host.place.domain:portNo/context1/context2/contextn/name
        <br>where <i>transport</i> might be iiop, iiopname, zeus or http, <br>
        <i>host</i> is the machine that this is on <br> 
        <i>place</i> is the company or institution's domain name <br>
        <i>domain</i> is the internet domaine - edu or com or org, or co.uk or fr <br>
        <i>portNo</i> is an integer which is the port that the agent is listening on <br>
        <i>contextx</i> is the naming context of the agent addressed <br>
        <i>name</i> is the name of the agent that is being addressed 
        @returns a boolean that indicates if the message was sent without an exception being 
        thrown. 
       */
    public boolean sendMessage (String performative, String content, String target) {
        FIPAPerformative perf = new FIPAPerformative (performative); 
        perf.setContent(content); 
        perf.setReceiver (target); 
        try {
            TransportFactory tf = agentContext.getTransportFactory(); 
            OutTray transport = tf.getTransport (target); 
            InetAddress ip = InetAddress.getLocalHost();
            String localHost = ip.getHostAddress();
            InTray myInTray = agentContext.getInTray(); 
            String sourceAddress = myInTray.getResponseAddress();
            javax.agent.Envelope env = perf.jasEnvelope(new FIPA_AID_Address(target),sourceAddress);  
            transport.send(env);} 
            catch (TransportUnsupportedException tue) { 
                return false; }
            catch (Exception e) { 
                e.printStackTrace(); 
                System.out.println("Unexpected exception in SimpleAPI.sendMessage() - attempting to recover"); 
                return false; 
            }
        return true;
    }
    
    
    /**
       achieve is used to make the agent attempt to satisfy the 
       postcondition formed by the parameter toGet[] 
       The array will be iterated over and a goal will be set for each of the 
       facts that are passed. 
       @param toGet[]   an array of facts that are to be the goals of the agent
       fail)
       @returns nothing
        */
    public void achieve (Fact toGet[]) {
        for (int count = 0; count < toGet.length; count++) { 
            Goal goal = new Goal(Goal.DISCRETE,
                agentContext.getGenSym().newId("goal"),
                toGet[count], 
                agentContext.whoami()); 
            agentContext.getEngine().achieve(goal);
                           } 
    }
        /** 
        setHandler method allows you to specify a method in an object that will 
        be called when a message of type messageType and from the agent agentFrom 
        is received by this agent.<p> 
        The method methodName will be called, it must have a single parameter of 
        type zeus.concepts.Performative - when this method is called by the 
        handling code this parameter will contain the message that has been 
        received. <p>
        @param messageType      the type of message (performative type) that is being watched for
         Examples of a performative type are <i> inform</i>, 
        <i>confirm </i>,<i> cfp </i>,<i> request </i> 
        @param agentFrom    the putative source of the messages in the form 
        <p>http://www.adastralCity.com/test <p> 
        This would mean that all messages from the agent called test on the 
        platform www.adastralCity.com that are passed via http will be 
        picked up and forwarded to the handleing method. 
        @param target   the object instance that is providing the handler 
        method for this
        @param methodName   the name of the method that is to be called 
        @returns nothing
        <p>
        If the parameters messageType and agentFrom are both null then the effect of 
       using these methods will be to make all the message that this agent receives
       be forwarded to target.methodName(Performative);
        
        */
    public void setHandler (String messageType, String agentFrom, 
                                    Object target, String methodName) { 
       MsgHandler msgh = agentContext.getMsgHandler();
       String [] msgPattern = { "type", messageType.toLowerCase(), "sender", "\\A"+agentFrom+"\\Z"};
       MessageRule messrule = new MessageRuleImpl(agentContext.newId("Rule"),msgPattern,target,methodName);
       String [] msgPattern2 = { "type", messageType.toUpperCase(), "sender", "\\A"+agentFrom+"\\Z"}; 
       messrule = new MessageRuleImpl(agentContext.newId("Rule"),msgPattern2,target,methodName);
       msgh.addRule(messrule);
    }
    

        /** 
        setHandler method allows you to specify a method in an object that will 
        be called when a message of type messageType <p> 
        The method methodName will be called, it must have a single parameter of 
        type zeus.concepts.Performative - when this method is called by the 
        handling code this parameter will contain the message that has been 
        received. <p>
        @param messageType      the type of message (performative type) that is being watched for
        Examples of a performative type are <i> inform</i>, 
        <i>confirm </i>,<i> cfp </i>,<i> request </i> 
        @param agentFrom    the putative source of the messages in the form 
        <p>http://www.adastralCity.com/test <p> 
        This would mean that all messages from the agent called test on the 
        platform www.adastralCity.com that are passed via http will be 
        picked up and forwarded to the handleing method.
        @param target   the object instance that is providing the handler 
        method for this
        @param methodName   the name of the method that is to be called 
        @returns nothing
        <p>
        If the parameters messageType and agentFrom are both null then the effect of 
       using these methods will be to make all the message that this agent receives
       be forwarded to target.methodName(Performative);
        
        */
    public void setHandler (String messageType, Object target, String methodName) { 
       MsgHandler msgh = agentContext.getMsgHandler();
       String [] msgPattern = { "type", messageType.toLowerCase()};
       MessageRule messrule = new MessageRuleImpl(agentContext.newId("Rule"),msgPattern,target,methodName);
       msgh.addRule (messrule); 
       String [] msgPattern2 = { "type", messageType.toUpperCase()};
       messrule = new MessageRuleImpl(agentContext.newId("Rule"),msgPattern2,target,methodName);
       msgh.addRule(messrule);
    }
    

    
    /** 
        setMessageQueue method is used to specify to zeus that it should queue messages 
        in this method so that they can be picked up one at a time in 
        a simple to understand way.<p>  
        This is a very primative way of handling messages and should only 
        be invoked for simple applications. Once the messageQueue has been tyrned on it cannot 
        be turned off, however, a repeat invokation of this method will cause the queue 
        to be reset, and all the messages currently stored on it will be discarded.
        @returns nothing
        */
    public void setMessageQueue () { 
        messQ = true;
        messageQueue = new Queue(); 
        // set message handler to call the queueing method
        this.setHandler (null,null,this,"addQueue");
    }
    
    
    /** 
        Simple method that is called to find out if the message enqueue 
        mechanism has been turned on or off. 
        @returns has setMessageQueue been called or not?
        */
    public boolean isQueueOn () { 
        return messQ; 
    }
    
    
    /**     
        if the messageQueue has been turned on this method will 
        return the next message on the queue. <P>
        If the messageQueue is not on then this call will return null. <p>
        If the messageQueue is empty this method will return null <p> 
        @returns the next message from the queue or null
        */
    public Performative getNextMessage() { 
     if (!messQ) return null; 
     // trying to be defensive
     synchronized (messageQueue) { 
        if (messageQueue.isEmpty()) return null;
        Object retObj = messageQueue.dequeue(); // defensive
        if (retObj == null)
            return null; 
        else 
        try {
            return (Performative) retObj;}
            catch (ClassCastException cce) {
                System.out.println("Bad message in SimpleAPI.getNextMessage()"); 
                return null;}
        }
    }
    
    
    /**
        waitNextMessage bocks until a message is received by the agent, it will 
        then return that message. However, this will only work if the setMessageQueue
        method has been called first, because if it hasn't this will block until you 
        call the addQueue(Performative) method.
        @returns the next message off the message queue (blocks if nothing is there)
        */      
    public Performative waitNextMessage() { 
     if (!messQ) return null; 
     // trying to be defensive
     synchronized (messageQueue) { 
        Object retObj = messageQueue.dequeue(); 
        if (retObj == null)
            return null; 
        else 
        try {
            return (Performative) retObj;}
            catch (ClassCastException cce) {
                System.out.println("Bad message in SimpleAPI.getNextMessage()"); 
                return null;}
     }                  
    }


    /** 
    	* addQueue is used to add a message to the internal queue maintained by this
        * agent. It is primarily intended for internal use, but if the message 
        * queue is turned on you could use this to send a message to yourself!
        *@param perf     the performative to add to the queue
        *@returns nothing
        */
    public void addQueue(Performative perf) { 
        messageQueue.enqueue(perf);
    }
    

        /**
          * addFact can be used to add a fact into the Zeus resourceDb (the agents beliefs)
          * This will trigger any rules that match the fact, and will allow agents to acheive
          * goals that can be met by exectuing tasks with preconditions that match the fact.
          *@since 1.3
          *@author Simon Thompson
          *@param toAdd the fact that you want to be added
          *@returns nothing
          */
    public void addFact (Fact toAdd) {
       ResourceDb rdb =  agentContext.getResourceDb();
       rdb.add(toAdd);
       }
    

    /** 
      * this init() method is used to set up a SimpleAPI for you to 
      *  use<p> 
      *  The suggested method of use is from your AgentExternal : <p> 
      *  <CODE> public class myExternal implements ZeusExternal {
      *      public void exec(AgentContext ac) { 
      *          SimpleAPI api = new SimpleAPI (ac); 
      *      </CODE> <br>
      *  You can now use the methods in this class via the api instance. 
      *@param agentContext     used by this class to access the internals
      *  of the zeus agent the agentContext object is the directory of 
      *  agent components that is passed to a Zeus AgentExternal when 
      *  it is instantiated. Sophisticated agent programmers will 
      *  want to utilize the agentContext object directly rather than via this 
      *  simple wrapper, which is designed for novice users.
      *@returns identity of this class
      *@see zeus.actors.AgentContext
      *@see zeus.actors.ZeusAgentContext
        */ 
    public SimpleAPI (AgentContext agentContext) { 
        this.agentContext = agentContext;    
    }    
}
