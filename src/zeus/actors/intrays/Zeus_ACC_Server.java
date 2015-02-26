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

package zeus.actors.intrays;

import zeus.actors.outtrays.*;
import java.net.*;
import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.agents.*;
import javax.naming.*;
import java.net.*;
import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

import javax.rmi.*;
import java.rmi.*;


/**
 * Zeus_ACC_Server uses the FIPA_97_Server and FIPA_2000_Server classes to provide
 * a unified message reception and handleing service.On init this class will create
 * a FIPA_97_Server and a FIPA_2000_Server, using itself as a parameter.
 * When either of these intrays receives a message it will call back the appropiate
 * handle method in this class. The message will then be processed and placed on the
 * message handleing q in this instance. Finally this message will be handled and processed.
 * <p>
 * @author Simon Thompson
 * @since 1.1
 */
public class Zeus_ACC_Server extends Server implements InTray, FIPA_97_Handler, FIPA_2000_Handler {  // the ACC agent should include code to set this.
    
    public  String hostname = zeus.util.SystemProps.getProperty("HAP_Address");
    
    /**
    iiop2000port is used to store the number of the port that the FIPA_2000 IIOP
    listening service (the server) will use.
    This is package protected so that subclasses can do as they will!
    value is set to "2000" by default
     */
    protected String iiop2000Port = "2000";
    
    
    /**
     * iiop97port is used to store the number of the port that the FIPA_97 IIOP
     * listening service (the server) will use.
     * This is package protected so that subclasses can do as they will!
     * value is set to "1097" by default.
     */
    protected String iiop97Port = "1097";
    
    
    /**
    httpPort is the number of the port that the FIPA_2000 HTTP service
    will listenon.
    This is package protected so that you can fiddle with it in subclasses.
    value is set to "61000" by default
     */
    protected String httpPort = "61000";
    
    
    /**
    access to the context object is provided as protected as a convienience for
    API programmers and future extentions, it is passed to the init() of this class
     */
    protected AgentContext context = null;
    
    private Queue  msgQ = new Queue("Zeus_ACC_In");
    private ZeusParser parser = new ZeusParser();
    
    
    /**
    name is set to "ACC" by default
     */
    protected String name = "ACC";
    
    /**
    twoThousand_Server is the reference to the FIPA_2000_IIOP_Server that
    this class sets up and runs in it's init().
     */
    protected FIPA_2000_IIOP_Server twoThousand_Server = null ;
    
    
    /**
    ninetySeven_Server is the reference to the FIPA_97_Server that
    this class sets up and runs in it's init()
     */
    protected FIPA_97_Server ninetySeven_Server = null;
    
    
    /**
    httpServer is the reference to the FIPA_2000_HTTP_Server that
    this class sets up and runs in it's init()
     */
    protected FIPA_2000_HTTP_Server httpServer = null;
    
    
    /**
     host is protected to allow for interference by putative sub-classes
     */
    protected String host = null;
    
    
    /**
    class constructor that takes the AgentContext and registers this instance with it,
    and the MailBox and grabs the reference so that it can be used later. This also sets
    up the ServerThreads for the message transports that this Server is utilizing.
     */
    public Zeus_ACC_Server(AgentContext context, Zeus_ACC_MailBox mbox) {
        this.mbox = (MailBox) mbox;
        timeout = 10;
        Assert.notNull(context);
        this.context = context;
        TransportConfig twoIIOPConf = SystemProps.getTransport("FIPA_IIOP_2000");
        if (twoIIOPConf!=null) {
            debug("setting iiop 2000 port to : " + twoIIOPConf.getPort());
            iiop2000Port = twoIIOPConf.getPort(); }
        TransportConfig twoHTTPConf = SystemProps.getTransport("FIPA_HTTP_2000");
        if (twoHTTPConf != null) {
            debug("setting http 2000 port to : " + twoHTTPConf.getPort());
            httpPort = twoHTTPConf.getPort(); }
        TransportConfig nineIIOPConf = SystemProps.getTransport("FIPA_IIOP_1997");
        if (nineIIOPConf != null) {
            debug("setting iiop 1997 port to : " + nineIIOPConf.getPort());
            iiop97Port = nineIIOPConf.getPort(); }
        
        try {
            
            
            InetAddress ip = InetAddress.getLocalHost();
            host = ip.getHostAddress();
            if (hostname == null ) {
                hostname = host; }
            twoThousand_Server = new FIPA_2000_IIOP_Server(this,host, iiop2000Port,name,"2000Connection");
            ninetySeven_Server = new FIPA_97_Server(this,host, iiop97Port,name,"97Connection");
            httpServer = new FIPA_2000_HTTP_Server(this,host,httpPort,name, "HTTPConnection");
            // note to self - do http server here
            this.start();
            this.setName("Zeus_ACC_Server");}
        catch (Exception e ) {
            // will only be called if local host is not set
            // very unlikely...
            e.printStackTrace();
            System.out.println("ERROR - probably localhost cannot be looked up!\n WARNING: attempting to continue, but likely fatal");
        }
    }
    
    
    
    /**
    stopProcessing will cause the run loop of this class to stop
     */
    public void stopProcessing() {
        processing = false;
    }
    
    
    /**
    This is where the changes were needed. Instead of listening on a stupid socket ;-)
    we simply look in our message queue to see if any goodies have been put there by nice
    mr FIPA_97_Connection.
     */
    public void run() {
        processing = true;
        while (processing) {
            try {// robust!?
                Object obj = this.pop();
                debug ("popped message off queue"); 
                if (obj instanceof String) {
                    String text =(String) obj;
                    debug("message popped is: " + text);
                    //       try {
                    text = Misc.unescape(text);// swapped from underneath
                    //    text = text.replace('\n',' ');
                    //   text = text.replace('\r',' ');
                    //   text = text.replace('\t',' ');
                    
                    forwardFIPAMessage(text);
                    
                }
      
                yield();}
            catch (Exception e ) {
                e.printStackTrace();
                System.out.println("exception in ACC server - attempting to recover");
            }
            catch (Error er) {  
                er.printStackTrace(); 
                System.out.println("error - attempting to recover"); 
            }
            catch (Throwable tr) { 
                tr.printStackTrace();
                System.out.println("throwable - attempting to recover"); 
            }
        }
    }
    
    
    /**
     * for now the behaviour is :-
     * get message, <br>
     * act surprised, <br>
     * then parse it into a FIPAPerformative, <br>
     * map that to a Performative, <br>
     * register self as alias in nameserver,<br>
     * add alias to FIPAAddressBook, <br>
     * stamp message with said alias,<br>
     * assert a messageHandleing rule to invoke forwarding behaviour when
     * a response is received
     * send message to Zeus agent, <br
     * pray.<p>
     */
    public void forwardFIPAMessage(String text) {
        try {
            debug ("forwarding"); 
            FIPA_AddressBook addresses = ((FIPA_AddressBook)context.getAddressBook());
            FIPAPerformative fmsg = parser.fipaPerformative(text);
            FIPA_AID_Address fAddress = fmsg.getSender_As_FIPA_AID();
            fAddress = addresses.checkAddress(fAddress);
            if (fAddress.getAlias() == null) {
                registerAlias(fAddress); }
            Performative msg = fmsg.performative();
            String alias = fAddress.getAlias();
            msg.setSender(alias);
            debug ("setting send address to " + alias); 
            msg.setReplyTo(alias);
            addresses.add(fAddress);
            setHandleMessage(fAddress, alias);
     
            debug("Zeus content = " + msg.getContent());
            mbox.sendMsg(msg); }
        catch (Exception e) {
            try {
                java.io.File file = new java.io.File("debug.out");
                java.io.FileOutputStream fileout = new java.io.FileOutputStream(file);
                java.io.PrintWriter fw= new java.io.PrintWriter(fileout);
                e.printStackTrace(fw);
                fw.flush();
                fw.close();} catch (Exception ne) { ne.printStackTrace(); }
            
        }
        
    }
    
    
    /**
     * set up a forwarding rule, if one is not already present.
     */
    private void setHandleMessage(FIPA_AID_Address faddress, String alias) {
        if (!faddress.getForwardingRuleSet()) {
            MsgHandler handler = context.getMsgHandler();
            String msg_pattern[] = {"receiver",alias};
            handler.addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern, this, "forward"));
            faddress.setForwardingRuleSet(true);
        }
    }
    
    
    
    /**
     * provides a basic mechanism for getting aliases for agents on other platforms
     * into the Zeus address space. If this method is called from the ACC agent
     * stub it can be used to setup zeus names and fipa forwarding in the ANServer and
     * in the ACC
     */
    public void setFIPAAlias(String name, String address) {
        FIPA_AddressBook addresses = ((FIPA_AddressBook)context.getAddressBook());
        FIPAPerformative fmsg = parser.fipaPerformative(address);
        FIPA_AID_Address fAddress = fmsg.getSender_As_FIPA_AID();
        fAddress = addresses.checkAddress(fAddress);
        if (fAddress.getAlias() == null){
            fAddress.setAlias(name);
            registerAlias(fAddress,name);
            setHandleMessage(fAddress,name);
            addresses.add(fAddress);
        }
        
        
    }
    
    
    /**
     * provides a basic mechanism for getting aliases for agents on other platforms
     * into the Zeus address space. If this method is called from the ACC agent
     * stub it can be used to setup zeus names and fipa forwarding in the ANServer and
     * in the ACC
     */
    public  void setFIPAAlias(String name, FIPA_AID_Address fAddress) {
        FIPA_AddressBook addresses = ((FIPA_AddressBook)context.getAddressBook());
        fAddress = addresses.checkAddress(fAddress);
        if (fAddress.getAlias() == null) {
        fAddress.setAlias(name);   
            registerAlias(fAddress,name);
            setHandleMessage(fAddress,name);
            addresses.add(fAddress);
        }
    }
    
    
    /**
     * forward is the method called when the MsgHandler fires a MessageRule that is setup
     * by the setHandleMessage method.
     * It converts the Zeus Performative into a FIPA performative, does any address mapping
     * that is needed and sends the message to the FIPA_postman.
     */
    public void forward(Performative perf) {
        if (perf.getSender().startsWith("Nameserver")) return;
        if (perf.getSender().startsWith("Facilitator")) return; // god knows
        try {
            FIPAPerformative fPerf = new FIPAPerformative(perf);
            FIPA_AddressBook addresses = ((FIPA_AddressBook)context.getAddressBook());
            String raddr =  perf.getReceiver();
            FIPA_AID_Address faddr = addresses.lookupAlias(raddr);
            Vector recs = new Vector();
            recs.addElement(faddr);
            fPerf.setReceivers(recs);
            String senderName = perf.getSender();
            //  Envelope perf.getEnvelope();
            InetAddress ip = InetAddress.getLocalHost();
            String localhost = hostname;
            FIPA_AID_Address sender = new FIPA_AID_Address("(agent-identifier\n:name " + senderName + "@" + localhost + "\n:addresses (sequence " + getResponseAddress()+"))");
            //  sender.setName(perf.getSender());
            fPerf.setSender(sender);
         
            FIPA_PostMan postey =((Zeus_ACC_MailBox) mbox).getFIPA_PostMan();
            postey.push(fPerf);
            // set sender?}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    /**
     * send a registration to the nameservers that we are using. <br>
     * @param sender is the name of the alias to use
     */
    protected void registerAlias(FIPA_AID_Address sender) {
        System.out.println("In register 1");
        String name = sender.getName()+sender.getHost();
        AddressBook addressBook = context.getAddressBook();
        for(int i = 0; i < context.nameservers().size(); i++ ) {
            String key = sender.setAlias(name);
            Address addr = (Address)context.nameservers().elementAt(i);
            addressBook.add(addr);
            Performative msg = new Performative("request");
            msg.setReceiver(addr.getName());
            msg.setReplyWith(key);
            msg.setContent("register");
            msg.setSender(key);
            System.out.println("send message to nameserver");
            mbox.sendMsg(msg);
            System.out.println("done message");
        }
    }
    
    
    /**
        send a registration to the nameservers that we are using. <br>
        @param sender is the name of the alias to use
     */
    protected void registerAlias(FIPA_AID_Address address, String name) {
        AddressBook addressBook = context.getAddressBook();
        for(int i = 0; i < context.nameservers().size(); i++ ) {
            String key = address.setAlias(name);
            Address addr = (Address)context.nameservers().elementAt(i);
            addressBook.add(addr);
            Performative msg = new Performative("request");
            msg.setReceiver(addr.getName());
            msg.setReplyWith(key);
            msg.setContent("register");
            msg.setSender(key);
            mbox.sendMsg(msg);
        }
    }
    
    
    /**
        send a registration to the nameservers that we are using. <br>
        @param sender is the name of the alias to use
     */
    protected void registerAlias(String name) {
        AddressBook addressBook = context.getAddressBook();
        for(int i = 0; i < context.nameservers().size(); i++ ) {
            String key = name;
            Address addr = (Address)context.nameservers().elementAt(i);
            addressBook.add(addr);
            Performative msg = new Performative("request");
            msg.setReceiver(addr.getName());
            msg.setReplyWith(key);
            msg.setContent("register");
            msg.setSender(key);
            mbox.sendMsg(msg);
        }
    }
    
    
    /**
     * registerAgent is functionally identical to registerAlias, but
     * is semantically slightly different because in the one we are using
     * the agent name directly, while in the other we are trying to decouple
     */
    public void registerAgent(FIPA_AID_Address address, String name) {
        this.registerAlias(address,name);
    }
    
    
    protected void finalize() {
        try {
            if ( listenSocket != null ) listenSocket.close();
            timeout = -1;
        }
        catch(IOException e) {
        }
    }
    
    
    public Address getAddress() {
        return address;
    }
    
    
    /**
    pull something off the ACC message processing queue
     */
    public String pop() {
        return (String) msgQ.dequeue();
    }
    
    
    /**
     * put something onto the ACC message processing queue
     */
    public void push(String target) {
        debug ("enqueueing message "); 
        msgQ.enqueue(target);
        
    }
    
    
    /**
     * handle(String message) is called by the FIPA_97_Server which the init method of the Zeus_ACC_Server
     * class creates it is used as an interface to unify and collate the information
     * reception and processing for the ACC agent. <p>
     * implements the FIPA_97_Handler interface <p>
     * @param message - the message picked up from the fipa_97 transport
     */
    public void handle(String message) {
        // simply push this onto the message handling q.
        push(message);
    }
    
    
    
    
    /**
     * handle (FIPA.FipaMessage aMessage) is called by the FIPA_2000_server that the
     * init method of this class creates.
     * implements that FIPA_2000_Handler interface<p>
     * ISSUES <br>
     * ------ <br>
     * Envelopes - what should we do???
     *
     * @param aMessage - the message in FIPA 2000 object (java/idl) format
     * @see FIPA.FipaMessage
     */
    public synchronized void handle(FIPA.FipaMessage aMessage) {
        byte body [] = aMessage.messageBody;
        String messageStr = new String(body);
        
        push(messageStr);
    }
    
    /**
    return a string of addresses that this server is listening on
     */
    public String getResponseAddress() {
        String httpAddress = httpServer.getResponseAddress();//"http://" + host + ":" + http2000Port  + name
        String iiop2000Address = twoThousand_Server.getResponseAddress(); //"corbaname::" + host + ":" +iiop2000Port + "/NameService/" name
        String iiop97Address = ninetySeven_Server.getResponseAddress();//"iiop" + host + ":" + iiop97Port + name
        String ior2000Address = twoThousand_Server.getIORAddress();
        // removed corba addresses 
        String addressStr = new String(iiop2000Address +" " + httpAddress );
        
        return (addressStr);
        
    }
    
    
    public void debug(String str) {
          System.out.println ("Zeus ACC server>> " + str);
    }
    
    
}
