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
 * public limited company are Copyright 1996-2002. All Rights Reserved.
 *
 * THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
 */

/*
 * NIOServer.java
 *
 * Created on 09 April 2002, 14:29
 */

package zeus.actors.intrays;


import java.nio.channels.*;
import java.net.*;
import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;


/**
 *this is a souped up version of the native socket transport to take advantage
 *of the nio libraries.
 */

public class NIOServer extends Thread implements InTray {
    public static final int MAX_CONNECTIONS = 1;
    public static final int MAX_QUEUE_LENGTH = 100; // 50
    public static final int DEFAULT_PORT_MIN = 6700;//6700
    public static final int DEFAULT_PORT_MAX = 7800;//6800
    public int maxMessageSize = 100000;
    protected ServerSocket listenSocket;
    protected Address      address;
    protected int          connection_count = 0;
    protected boolean      processing;
    private AgentContext context = null;
    private java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(maxMessageSize);
    /**
     * timeout was originally private, but subclasses need to
     * use it to construct there own treads of control, so I changed it to protected
     * (ST -14/8/00)
     */
    protected   long         timeout = -1;
    
    /** Data structure holding messages as they are read in */
    protected Queue        inMail;
    
    /** Reference to MailBox of which this is a sub-component */
    protected MailBox      mbox;
    
    protected MsgHandler msgHandler = null;
    
    
    // java.nio extentions
    java.nio.channels.Selector selector;
    ServerSocketChannel server ;
    SelectionKey key;
    SelectionKey serverKey;
    private LinkedList clients;
    private java.nio.channels.Selector readSelector;
    ServerSocket socket;
    
    // added so that the class can be extended without starting the threads.
    public NIOServer() { ; }
    
    
    public NIOServer(AgentContext context, MailBox mbox, Queue inMail) {
        Assert.notNull(context);
        this.context = context;
        // Get localhost details
        try {
        InetAddress ip = InetAddress.getLocalHost();
        String localhost = ip.getHostAddress();
        Core.DEBUG(4,"Ip Address is: " + ip);
  
        // Select port for listening
        boolean port_found = false;
        for(int port = DEFAULT_PORT_MIN; !port_found && port < DEFAULT_PORT_MAX; port++ ) {
            clients = new LinkedList();
            readSelector = java.nio.channels.Selector.open();
            server = ServerSocketChannel.open(); 
            socket = server.socket(); 
            socket.bind(new InetSocketAddress(port), 100); 
            server.configureBlocking(false); 
            //  listenSocket = new ServerSocket(port,MAX_QUEUE_LENGTH);
            port_found = true;
            address = new ZeusAddress(context.whoami(),localhost,port,context.whatami());
            context.AddressBook().add(address);
             }
            if ( !port_found ) {
                System.err.println("Cannot get a port for listening");
                //  System.exit(0);
            }
        
        
        } catch (Exception e) { 
            e.printStackTrace(); 
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
        SocketChannel client;
        while(processing) {
            debug("alive");
            try {
                while (true) {
                    client = server.accept();
                    if (client!=null)
                        registerClient(client);
                    serviceClients();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Core.DEBUG(3,"Exception listening for connections: " + e);
            }
            finally {
                  try {
                server.socket().close();}
                catch (Exception e) {
                    ;
                }
            }
            
        }
    }
    
    public void serviceClients()
    throws IOException {
        Set keys;
        Iterator it;
        SelectionKey key;
        SocketChannel client;
        try {
        if(readSelector.select(1) > 0) {
            keys = readSelector.selectedKeys();
            it = keys.iterator();
            while(it.hasNext()) {
                key = (SelectionKey)it.next();
                if(key.isReadable()) {
                    int bytes;
                    client = (SocketChannel)key.channel();
                    buffer.clear();
                    bytes = client.read(buffer);
                    if(bytes >= 0) {
                        PerformativeParser parser = new PerformativeParser(new ByteArrayInputStream(buffer.array()));
                        Performative msg = parser.Message();
                        this.newMsg(msg);
                    } else if(bytes < 0) {
                        clients.remove(client);
                        client.close();
                    }
                }
            }
        }}catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void registerClient(SocketChannel client) throws IOException {
        client.configureBlocking(false);
        client.register(readSelector, SelectionKey.OP_READ);
        clients.add(client);
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
     * ensures that messages are processed
     * // synchronized
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
     * implement in haste, repent at leisure
     */
    public String getResponseAddress() {
        return (address.toString());
    }
    
    
    public void debug(String str) {
        System.out.println("Server>> " + str);
    }
    
}

