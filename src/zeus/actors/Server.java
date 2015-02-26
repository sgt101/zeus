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
 * @(#)Server.java 1.3
 */

package zeus.actors;

import java.net.*;
import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;


/**
 * This component is part of the {@link MailBox}, and is responsible for
 * reading incoming messages. This component operates within its own thread
 * enabling the MailBox to send and receive multiple messages simultaneously. <p>
 *
 * It is unlikely that developers will need to call these methods directly.
 * Although if the user wants to replace the default TCP/IP messaging mechanism
 * this could be cleanly achieved by reimplementing the methods of this class.
 */

public class Server extends Thread implements InTray {
    public static final int MAX_CONNECTIONS = 1;
    public static final int MAX_QUEUE_LENGTH = 100; // 50
    public static final int DEFAULT_PORT_MIN = 6700;//6700
    public static final int DEFAULT_PORT_MAX = 7800;//6800
    
    protected ServerSocket listenSocket;
    protected Address      address;
    protected int          connection_count = 0;
    protected boolean      processing;
    private AgentContext context = null;
    
    /**
    timeout was originally private, but subclasses need to
    use it to construct there own treads of control, so I changed it to protected
    (ST -14/8/00)
     */
    protected   long         timeout = -1;
    
    /** Data structure holding messages as they are read in */
    protected Queue        inMail;
    
    /** Reference to MailBox of which this is a sub-component */
    protected MailBox      mbox;
    
    protected MsgHandler msgHandler = null;
    
    
    
    
    // added so that the class can be extended without starting the threads.
    public Server() { ; }
    
    
    
    public Server(AgentContext context, MailBox mbox, Queue inMail) {
        Assert.notNull(context);
        this.context = context;
        // Get localhost details
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String localhost = ip.getHostAddress();
            Core.DEBUG(4,"Ip Address is: " + ip);
            
            // Select port for listening
            boolean port_found = false;
            for(int port = DEFAULT_PORT_MIN;
            !port_found && port < DEFAULT_PORT_MAX; port++ ) {
                try {
                    listenSocket = new ServerSocket(port,MAX_QUEUE_LENGTH);
                    port_found = true;
                    address = new ZeusAddress(context.whoami(),localhost,
                    port,context.whatami());
                    context.AddressBook().add(address);
                }
                catch (IOException e) {
                    // e.printStackTrace();
                }
            }
            if ( !port_found ) {
                System.err.println("Cannot get a port for listening");
                //  System.exit(0);
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Cannot get local host IP address");
            //System.exit(0);
        }
        catch (IOException e) {
            System.err.println("Cannot get a port for listening");
            e.printStackTrace();
            // System.exit(0);
        }
        
        // Store variables
        //  this.inMail = inMail;
        this.mbox = mbox;
        
        // LL 030500 1.03b
        // lowerStatus();
        
        // Start the server listening for connections
        this.setName("Normal server");
        this.start();
    }
    
    public AgentContext getAgentContext() {
        return mbox.getAgentContext();
    }
    
    
    public synchronized void updateCount(int x) {
        debug("update count");
        connection_count += x;
        if ( x < 0 ) {
            debug("notifying");
            notify();
        }
    }
    
    public void stopProcessing() {
        processing = false;
    }
    
    public void lowerStatus() {
        this.setPriority(Thread.NORM_PRIORITY-2);
        timeout = 1000;
    }
    
    // LL 030500 1.03bB
    public void normalStatus() {
        this.setPriority(Thread.NORM_PRIORITY);
        timeout = 1000;//1000?
    }
    // LL 030500 1.03bE
    
    // The body of the server thread.  Loop forever, listening for and
    // accepting connections from clients.  For each connection,
    // create a Connection object to handle communication through the
    // new Socket.
    
    public void run() {
        processing = true;
        while(processing) {
            debug("alive");
            try {
                Socket client = null;
                client = listenSocket.accept();
                new Connection(client, this,context.GenSym().newId());
                yield();
            }
            catch (Exception e) {
                e.printStackTrace();
                Core.DEBUG(3,"Exception listening for connections: " + e);
            }
            catch (Error er ) { 
                er.printStackTrace(); 
            }
            catch (Throwable tr) { 
               tr.printStackTrace(); 
            }
            // extra catches inserted by Simon on 4/6/03 to give enhanced confidence. 
       }
    }
    
    
    protected void finalize() {
        try {
            if ( listenSocket != null ) listenSocket.close();
        }
        catch(IOException e) {
        }
    }
    
    
    public Address getAddress() {
        return address;
    }
    
    
    /**
    ensures that messages are processed
    // synchronized
     */
    public void newMsg( Performative msg ) {
        Address addr;
        Time time;
        AgentContext context = this.getAgentContext();
        debug("1");
        if ( (time = context.currentTime()) != null )
            msg.setReceiveTime(time);
        // add this agent to the addressbook
        debug("2");
        if ( (addr = msg.getAddress()) != null )
            if (!addr.getType().equalsIgnoreCase("FORWARD"))
                context.AddressBook().add(addr);
        
        // if (inMail == null){
        debug("3");
        if (msgHandler==null)
            msgHandler = context.getMsgHandler();
        //	     inMail = msgHandler.getMessageQueue();
        //}
        
        Core.DEBUG(3,"putting message from " + msg.getSender() + " on handler queue");
        //inMail.enqueue(msg);
        debug("4");
        msgHandler.processMessage(msg);
        debug("5");
        mbox.notifyMonitors(msg,MailBox.RECEIVE);
        debug("6");
    }
    
    
    /**
    implement in haste, repent at leisure
     */
    public String getResponseAddress() {
        return (address.toString());
    }
    
    
    public void debug(String str) {
          System.out.println("Server>> " + str);
    }
    
}
