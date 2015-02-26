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
 * @(#)MailBox.java 1.03b
 */

package zeus.actors;

import java.net.*;
import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.event.*;
import zeus.actors.intrays.*;

/**
 * Each agent has a Mailbox component that implements its communication
 * mechanism. The sub-components of the MailBox are together responsible for 
 * creating and reading the TCP/IP sockets that send and receive messages. <p>
 *
 * The MailBox maintains two independent threads of activity, one is a reader 
 * thread, which continually listens for incoming socket connections, whereupon
 * a new transient thread is created to read the message and deliver it to the 
 * {@link MsgHandler}, which processes it. This approach delegates
 * responsibility for reading messages to the new connection thread, leaving the
 * main {@link Server} thread free to continue listening for incoming messages, (thus
 * enabling several messages to be received simultaneously).  When the incoming
 * message is read, the connection thread terminates. <p>
 *
 * The other Mailbox thread is a {@link PostMan} object, which creates transient
 * threads that open sockets to the message recipients. If the connection is made
 * the message is then streamed down the socket, this allows the agent to
 * dispatch more than one message at a time. <p>
 *
 * More details on the workings of the communication mechanism are provided
 * in the Zeus Technical Manual.
 */
public class MailBox 
{
    /**
        eventMonitor used to be private, but I needed to alter this so 
        that I could build an effective sub-class
            */
  protected HSet[]  eventMonitor = new HSet[4];

  public static final int RECEIVE      = 0;
  public static final int QUEUE        = 1;
  public static final int DISPATCH     = 2;
  public static final int NOT_DISPATCH = 3;

  /** A data structure holding the agent's incoming mail messages */
  protected Queue       inMail = new Queue("Zeus inMail");

  /** A data structure holding the agent's outgoing mail messages */
  protected Queue       outMail = new Queue("Zeus outMail");

  /** Holds mail messages that need to be CC'ed to Visualiser agents */
  protected Queue       ccMail = new Queue("Zeus ccMail");

  protected Hashtable   asTable = new Hashtable();
  protected Hashtable   visualisers = new Hashtable();

  /** The sub-component responsible for reading incoming mail */
  protected Server       server;

  /** The sub-component responsible for dispatching outgoing mail */
  protected PostMan[]    postman;

  protected Address      myAddress;
  protected AgentContext context;

  public MailBox () {;} 

  public MailBox(AgentContext context) {
    Assert.notNull(context);
    this.context = context;
    context.set(this);

    Address addr;
    Performative msg;

    // setup event-monitor db
    for(int i = 0; i < eventMonitor.length; i++ )
       eventMonitor[i] = new HSet();

    context.set(new AddressBook());
    server = new Server(context,this,inMail);
    myAddress = server.getAddress();

    postman = new PostMan[2];
    postman[0] = new PostMan(this,outMail,ccMail,myAddress);
    postman[1] = new PostMan(this,ccMail,myAddress);

    // Register with Name Servers
    String key = context.newId();
    String[] pattern = { "type", "inform", "in-reply-to", key };

    context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
       pattern,MessageActionImpl.EXECUTE_ONCE,this,"register")
    );

    for(int i = 0; i < context.nameservers().size(); i++ ) {
       addr = (Address)context.nameservers().elementAt(i);
       context.AddressBook().add(addr);

       msg = new Performative("request");
       msg.setReceiver(addr.getName());
       msg.setReplyWith(key);
       msg.setContent("register");
       sendMsg(msg);
    }
  }

  public void register(Performative msg) {
    String content = msg.getContent();
    if ( context.Clock() == null && content != null ) {
       StringTokenizer st = new StringTokenizer(content);
       long prev, incr, now0, now1;
       long deltaX, deltaT = 160;

       prev = Long.parseLong(st.nextToken());
       incr = Long.parseLong(st.nextToken());
       now0 = Long.parseLong(st.nextToken());
       now1 = System.currentTimeMillis();
       deltaX = now1 - (now0+deltaT);
       context.set(new Clock(prev+deltaX,incr));
    }
  }

  public AgentContext getAgentContext() { return  context; }

  public void del( Address addr ) {
    Assert.notNull(addr);
    context.AddressBook().del( addr );


    // LL 040500 1.03B
    // after deregister, remove all records of address query
    // and explicitly extract msg from waitQueue and put to outMail (which
    // will then fail when do a mbox.lookup).
    KeyValue data = (KeyValue) asTable.remove(addr.getName()); // LL 040500 1.03b
    if ( data != null) 
        for(int i = 0; i < postman.length; i++ )
            postman[i].addressReceived(data.key);
    // LL 040500 1.03bE

/* LL this would only solve the type cast problem. However, the logic doesn't
      make sense. As an agent deregisters itself, all the pending out messages
      to it should not be left in the waitQueue. 
    Time t = context.currentTime();
    KeyValue data = (KeyValue) asTable.get(addr.getName());

    if ( t != null  ) 
        if ( data != null )
            asTable.put(addr.getName(), new KeyValue(data.key,t));
        else {
            String key = context.newId(); 
            asTable.put(agent,new KeyValue(key,now));
        }
*/

  }

  public void del( Vector v ) {
    Assert.notNull(v);
    for(int i = 0; i < v.size(); i++ )
      context.AddressBook().del( (Address)v.elementAt(i) );
  }

  public void add( Address addr ) {
    Assert.notNull(addr);
    context.AddressBook().add( addr );
  }

  public void add( Vector v ) {
    Assert.notNull(v);
    for(int i = 0; i < v.size(); i++ )
       context.AddressBook().add( (Address)v.elementAt(i) );
  }

  public Address lookup( String name ) {
    Assert.notNull(name);
    return context.AddressBook().lookup( name );
  }

  public void stopDispatching() {
    for( int i = 0; i < postman.length; i++ )
      postman[i].stopDispatching();
  }

  public void stopProcessing() {
    server.stopProcessing();
  }

  public void lowerStatus() {
    for( int i = 0; i < postman.length; i++ )
      postman[i].lowerStatus();
    server.lowerStatus();
  }

    /**
    changed to public so that subclasses of the classes in this package can have access
    (Simon) 
    */
  public String addressSought(String agent) {
    // First we should clear 'asTable' of entries older than a predefined
    // age so that our agent should query known nameservers for the
    // receiver's address. This way, a receiver that went off-line and
    // later comes online would be found.

    String name;
    KeyValue data;
    double now = context.now();
    Enumeration enum = asTable.keys();
    while( enum.hasMoreElements() ) {
      name = (String) enum.nextElement();
      data = (KeyValue)asTable.get(name);
      if ( now-data.value >= context.getAddressBookRefresh() ) { // LL 040500 1.03b
        asTable.remove(name);
         context.MsgHandler().removeRule(data.key);
      }
    }
    data = (KeyValue)asTable.get(agent);
    if (data == null) {
      // try contacting known nameservers to find agent's address
      Performative query;
      Address addr;
      String key = context.newId();
      String[] pattern = { "type", "inform", "in-reply-to", key };
      context.MsgHandler().addRule(new MessageRuleImpl(key,pattern,
         MessageActionImpl.EXECUTE_ONCE,this,"addressReceived")
      );
      for(int i = 0; i < context.nameservers().size(); i++ ) {
         addr = (Address)context.nameservers().elementAt(i);
         query = new Performative("query-ref");
         query.setReceiver(addr.getName());
         query.setReplyWith(key);
         query.setContent("address_of " + agent);
         sendMsg(query);
      }

      // add receiver to list of agents whose addresses
      // are being looked for
      now = context.now();
      if ( !context.nameservers().isEmpty() ) {
         now += context.getAddressTimeout();
         asTable.put(agent,new KeyValue(key,now));
         return key;
      }
      else {
         return null;
      }
    }

    else if ( data.value > context.now()){
      return data.key;}
    else{
      return null;}
  }

  public void addressReceived(Performative msg) {
    String key = msg.getInReplyTo();
    Address  address = ZeusParser.address(msg.getContent());
    add(address);
    asTable.remove(address.getName());
    for(int i = 0; i < postman.length; i++ )
       postman[i].addressReceived(key);
  }

  public void logMessages(String agent, String tag) {
    Assert.notNull(agent);
    Assert.notNull(tag);
    visualisers.put(agent,tag);
  }


  public void stopLoggingMessages(String agent) {
    Assert.notNull(agent);
    visualisers.remove(agent);
  }


  public void informVisualisers(Performative msg) {
    Enumeration keys = visualisers.keys();
    String agent, replyTag;
    Performative inform;
    while( keys.hasMoreElements() ) {
      agent = (String) keys.nextElement();
      replyTag = (String)visualisers.get(agent);
      inform = new Performative("inform");
      inform.setReceiver(agent);
      inform.setInReplyTo(replyTag);
      inform.setContent((msg.toString()).trim());
      ccMail.enqueue(inform);
      notifyMonitors(inform,QUEUE);
    }
  }


  public Address getAddress() {
    return myAddress;
  }


  public void shutdown() {
    Address addr;
    Performative msg;
    // Deregister from Name Servers
    for(int i = 0; i < context.nameservers().size(); i++ ) {
      addr = (Address)context.nameservers().elementAt(i);
      msg = new Performative("request");
      msg.setContent("deregister");
      msg.setReceiver(addr.getName());
      sendMsg(msg);
    }
  }


  public void postErrorMsg(Performative msg, String content) {
    notifyMonitors(msg,NOT_DISPATCH);
    String reply_with;
    Time time;
    Performative errorMsg = new Performative("failure");
    errorMsg.setSender(myAddress.getName());
    errorMsg.setReceiver(myAddress.getName());
    errorMsg.setAddress(myAddress);
    if ( (reply_with = msg.getReplyWith()) != null )
       errorMsg.setInReplyTo(reply_with);
    errorMsg.setContent(content + " " + msg);
    if ( (time = context.currentTime()) != null )
      errorMsg.setSendTime(time);
    server.newMsg(errorMsg);
  }
    

  public void sendMsg(Performative msg) {
    postman[0].push(msg);
    notifyMonitors(msg,QUEUE);
  }

/**
    redundant now
    */
  public  Performative nextMsg() {
    Object obj = inMail.dequeue();
    try { 
        Performative perf = (Performative) obj;
        return (perf);
    } catch (Exception e) { 
        e.printStackTrace(); 
        return new Performative (obj.toString()); 
        }
        
 
  }

  public Vector listAddresses() {
    Vector result = new Vector(10);
    Enumeration enum = context.AddressBook().elements();
    Address addr;
    
    while( enum.hasMoreElements() ) {
      addr = (Address) enum.nextElement();
      result.addElement(new ZeusAddress(addr));
    }
    return result;
  }

  /**
   * Use this method to add a MessageMonitor if your code needs to react to
   * changes in the state of the mailbox. This is programatic alternative to
   * writing reaction rules
   */
  public void addMessageMonitor(MessageMonitor monitor, long event_type) {
      if ( (event_type & MessageEvent.RECEIVE_MASK) != 0 )
         eventMonitor[RECEIVE].add(monitor);
      if ( (event_type & MessageEvent.QUEUE_MASK) != 0 )
         eventMonitor[QUEUE].add(monitor);
      if ( (event_type & MessageEvent.DISPATCH_MASK) != 0 )
         eventMonitor[DISPATCH].add(monitor);
      if ( (event_type & MessageEvent.NOT_DISPATCH_MASK) != 0 )
         eventMonitor[NOT_DISPATCH].add(monitor);
  }

  public void removeMessageMonitor(MessageMonitor monitor, long event_type) {
      if ( (event_type & MessageEvent.RECEIVE_MASK) != 0 )
         eventMonitor[RECEIVE].remove(monitor);
      if ( (event_type & MessageEvent.QUEUE_MASK) != 0 )
         eventMonitor[QUEUE].remove(monitor);
      if ( (event_type & MessageEvent.DISPATCH_MASK) != 0 )
         eventMonitor[DISPATCH].remove(monitor);
      if ( (event_type & MessageEvent.NOT_DISPATCH_MASK) != 0 )
         eventMonitor[NOT_DISPATCH].remove(monitor);
   }
   
   
   public void notifyMonitors(Performative message, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      MessageMonitor monitor;
      MessageEvent event;
      Enumeration enum = eventMonitor[type].elements();
      switch(type) {
         case RECEIVE:
              event = new MessageEvent(this,message,MessageEvent.RECEIVE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (MessageMonitor)enum.nextElement();
                 monitor.messageReceivedEvent(event);
              }
              break;
         case QUEUE:
              event = new MessageEvent(this,message,MessageEvent.QUEUE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (MessageMonitor)enum.nextElement();
                 monitor.messageQueuedEvent(event);
              }
              break;
         case DISPATCH:
              event = new MessageEvent(this,message,MessageEvent.DISPATCH_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (MessageMonitor)enum.nextElement();
                 monitor.messageDispatchedEvent(event);
              }
              break;
         case NOT_DISPATCH:
              event = new MessageEvent(this,message,MessageEvent.NOT_DISPATCH_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (MessageMonitor)enum.nextElement();
                 monitor.messageNotDispatchedEvent(event);
              }
              break;
      }
   }
   
   
   
   /** 
    added so that transports can be accessed 
    @since 1.1
    @author Simon "guilty party" Thompson
    */ 
   public InTray getInTray () { 
    return server; 
   }
}
