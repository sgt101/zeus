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
 * @(#)PostMan.java 1.00
 */

package zeus.actors;

import java.net.*;
import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

/**
 * This component is part of the {@link MailBox}, and is responsible for
 * dispatching messages on demand to their recipients. This component
 * operates within its own thread enabling the MailBox to send and receive
 * multiple messages simultaneously. <p>
 *
 * It is unlikely that developers will need to call these methods directly.
 * Although if the user wants to replace the default TCP/IP messaging mechanism
 * this could be cleanly achieved by reimplementing the methods of this class.
 */

public class PostMan extends Thread 
{
  protected final int   MAX_RETRY = 10;

  /** Data structure holding messages pending dispatch */
  protected Queue       outMail = null;

  /** Data structure holding CC'ed messages pending dispatch to Visualisers */
  protected Queue       ccMail = null;

  /** Reference to MailBox of which this is a sub-component */
  protected MailBox     mbox = null;

  protected Address     myAddress = null;
  protected boolean     dispatching;
  protected Hashtable   waitQueue = new Hashtable();
  private static int id = 0; 
  private boolean fastAgent = false; 
  int queueLength = 5;
  int sleepTime = 100; 
  
  public PostMan() { 
    ;
  }

  public PostMan(MailBox mbox, Queue outMail, Address myAddress) {
    this(mbox,outMail,null,myAddress);

  }

  public PostMan(MailBox mbox, Queue outMail, Queue ccMail, Address myAddress) {
    
    this.mbox      = mbox;
    this.outMail   = outMail;
    this.ccMail    = ccMail;
    this.myAddress = myAddress;
 
    // ( ccMail == null )
     //this.setPriority(Thread.NORM_PRIORITY-2);
    this.setName("Normal postman"+id); 
    id ++;
    //this.lowerStatus();
    this.start();
  }

  public void stopDispatching() {
    dispatching = false;
  }

  public void lowerStatus() {
    this.setPriority(Thread.NORM_PRIORITY-2);
  }



  /**
    this run method is the business end of the agent's communication infrastructure. 
    It works in the following way. <p>
    for every receiver of the message<br>
    <t> if lookupAddress good then <br> 
    <t><t> if can send to that address 
     <t><t><t> tell the visualisers about the message
     <t><t> else 
     <t><t><t> post an error and delete that address
     <t>else 
     <t><t> ask the nameserver for the address and put this message on a todo list until the nameserver responds
     then service it.  
    */
  public void run() {


     dispatching = true;
     while( dispatching ) {
        doPost();
     }
  }


  public void doPost () {
     Performative msg, query;
     String       receiver;
     Address      addr;
      msg = (Performative) outMail.dequeue();
      System.out.println("Zeus native msg = " + msg.toString()); 
        Enumeration allRec = msg.getReceivers();
        if (allRec == null) {
            mbox.postErrorMsg(msg,"No reveivers specified"); }
        else {
            while (allRec.hasMoreElements()) { //1
                receiver = (String) allRec.nextElement();
                if (( addr = mbox.lookup(receiver)) != null) { //2
                    System.out.println("send to addr = " + addr.toString()); 
                    if ( postMsg(msg,addr) ) {     
	                    if ( ccMail != null )
	                        mbox.informVisualisers(msg);
	                        }
	                        else {//2
	                        // The receiver cannot be contacted at the given
	                        // address - we assume the address is wrong, and
	                        // delete it from the address book.
	                        mbox.postErrorMsg(msg,"Cannot contact reciever");
	                        mbox.del(addr);
	                        }
	                    }
	                    else {//3
	                        String key = mbox.addressSought(receiver);
	                        if ( key == null ){
	                            mbox.postErrorMsg(msg,"Cannot find address of receiver");
	                            }
	                        else {//4
	                             Vector list = (Vector)waitQueue.get(key);
                                 if ( list == null ) {
                                        list = new Vector(20);
                                        waitQueue.put( key,list);
                                        }
                                    list.addElement(msg);
                                  }// end else 4
                         }// end else 3
                 }//end if 2

            }
        yield();
        }


  public void addressReceived(String key) {
     synchronized( waitQueue ) {
        Vector list =  (Vector)waitQueue.remove(key);
        for(int i = 0; list != null && i < list.size(); i++ )
           outMail.enqueue(list.elementAt(i));
        list = null; // GC
     }
  }

 /**
    Some agents need to message quicky, some don't - they need to be sure that
    there reasoning components get a go at the processor.
    Call this method on your postman to turn it into a messaging deamon.
    */
 public void setFastAgent() { 
    fastAgent = true;
    
 }
 

  /**
    postMsg sends the performative out to the other Zeus agent
    a socket is opened and the message is written as a string down it.
    
    <p> 
    The behaviour of this method has been changed for 1.1/2.0 to allow some 
    flexibility on the sender field. This is mostly to allow agents to 
    send messages and stamp them as comming from someone else - in the case of the
    ACC agent this allows an alias to be set up in the name server for each exterior 
    IIOP/HTTP/WAP/UMTS agent or address of agent that is to be contacted. When the 
    ACC is contacted under this alias it can use it's FIPAAddresBook to lookup an 
    aid and construct a new message for forwarding<p> 
    To summerise : if the sender name in the msg parameter is set then the sent message 
    will have that sender name, and the address that is returned will also have that name. 
    
    */
  public synchronized boolean postMsg( Performative msg, Address addr ) {
    PrintWriter out = null;
    boolean isOk = false;
    int nTry = 0;
    // if statements added by simon
    if (msg.getSender()==null) {
        msg.setSender(myAddress.getName());
        }
    else if (msg.getSender().equals("") || msg.getSender().equals("null")) {
        msg.setSender(myAddress.getName());
        }
    // set the address in the envelope
    Address mailAddress = new ZeusAddress (myAddress);
    //changes for forwarding... 
    if (!msg.getSender().equals(mailAddress.getName())){
        mailAddress.setName (msg.getSender()); 
        if (myAddress.getType().equalsIgnoreCase("facilitator")) {
            mailAddress.setType ("FORWARD"); }
    }
    msg.setAddress(mailAddress);
    while( !isOk && nTry++ < MAX_RETRY ) {
      try {
            System.out.println("HOST = " + addr.getHost() + " PORT = " + addr.getPort()); 
            Socket socket = new Socket( addr.getHost(), addr.getPort() );
	        out = new PrintWriter( socket.getOutputStream(), true );
	        Time time;
            if ( (time = mbox.getAgentContext().currentTime()) != null )
                 msg.setSendTime(time);
            out.println( msg );  // Send msg
            out.flush();         // flush out-stream
            isOk = true;
            if ( (time = mbox.getAgentContext().currentTime()) != null )
                msg.setReceiveTime(time);
                mbox.notifyMonitors(msg,MailBox.DISPATCH);
            System.out.println("MESSAGE SENT"); 
      }
      catch (IOException e) {
         Core.DEBUG(3,"IOException: " + e);
            yield();
      }
    finally {
	    if (out != null) out.close();
        }
    }
  //  System.out.println("leaving postman"); 
    return isOk;
    
  }


  /**
    use in preference to postMsg. Whereas the postMsg method invokes the messaging behaviour 
    of the agent directly this method places the message on a Q to be dealt with as and when.<br>
    This is a good thing, as it ensures that the agent is not forced into spending all its 
    time just sending messages.....
    */
  public void push (Performative msg) {
 // System.out.println("mailq = " + outMail.size()); 
  while (outMail.size() > queueLength) {
      doPost(); 
          }
    outMail.enqueue(msg);
  }

 
  
}
