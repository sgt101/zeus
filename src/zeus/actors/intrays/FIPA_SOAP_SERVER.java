 /**
 *             STANDARD DOCUMENTATION - EXPERIMENTAL SOFTWARE
 *                       (QGN 13, Issue 4 refers)
 *                          Copyright BTexact Technologies 2001
 *  @author      : Roberto Avalos   
 *  OUC code     : DVA5P
 *  File name    : FIPA_SOAP_SERVER.java
 *  @version     : 1.0  
 *  Date         : 9/April/2002
 *  Purpose      : Server that receives SOAP messages from other services or agents
 *  Testing      :   
 *  Software req.: Java Virtual Machine
 *  Hardware req.:
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

package zeus.actors.intrays;


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
import FIPA.*;
import org.apache.soap.*; // Body, Envelope, Fault, Header 
import org.apache.soap.rpc.*; // Call, Parameter, Response 
    /**
        This is an extention of the Server class which provides an "InTray" service for SOAP 
        transports. It will read messages from a SOAP connection and will then call the handle 
        method in the FIPA_2000_Handler object that was used to init it. 
        @author Roberto Avalos and Simon Thompson
        @since 1.1
        
    */
public class FIPA_SOAP_SERVER extends Server implements InTray{
    /** This variable was declared to retain the value of the reference of the FIPA_SOAP_Connection so as to be retrieved by 
    *   other classes
    *   Change made by Roberto Avalos
    */
    protected FIPA_SOAP_Connection transport=null;
    protected AgentContext context = null;
    private Queue  msgQ = new Queue("fipaSOAPIn");
    private ZeusParser parser = new ZeusParser();
    private FIPA_2000_Handler handler = null;
    private String host = null;
    private String port = null;
    private String name = null;
        
    /** Creates new Class */
    public FIPA_SOAP_SERVER(FIPA_2000_Handler handler, String host, String port,  String name, String threadId) {
        this.handler = handler;
        this.host = host;
        this.port = port;
        this.name = name;
               
        try {
            
            transport = new FIPA_SOAP_Connection();
            Thread soap_messageThread = new Thread(transport);
            //   http_messageThread.setPriority(Thread.MIN_PRIORITY);
            soap_messageThread.start();
            transport.register(msgQ);
        
            //These two lines are used to send a message to itself to prove the queue is working properly
            //String mensaje="(QUERY-REF :sender ( agent-identifier   :name PingAgent@132.146.209.244 :addresses (sequence SOAP::132.146.209.244:8080/soap/servlet/rpcrouter@@urn:agent:soap ) ) :receiver (set (agent-identifier   :name PingAgent@132.146.209.222 :addresses (sequence SOAP::132.146.209.222:8080/soap/servlet/rpcrouter::urn:agent:soap )) ) :content \"\"ping\"\")";       
            //transport.message(mensaje);
                             
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.start();
        this.setName(threadId);
        
    }
    
     /**
     * This method should call an agnostic message handler....
     * Right now, it just prints the message to System.out
     *
     */
    public void run() {
        processing = true;
        System.out.println("Listening for FIPA SOAP on port " + String.valueOf(port));
        while (processing) {
            //Changes by Roberto Avalos
            System.out.println("dequeuing");
            String msg = (String) msgQ.dequeue();
           // String message = messageFIPA.toFIPAString();
       //     String message=this.pop();
            //end of changes
            try {
                FIPAPerformative fperf = ZeusParser.fipaPerformative (msg); 
                FIPA.FipaMessage fmess = fperf.FipaMessage(); 
                System.out.println("sending to handler");
                handler.handle(fmess);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            yield();
        }
    }
    
    
    public FIPAPerformative pop() {
        System.out.println("poping out");
        return (FIPAPerformative) msgQ.dequeue();
    }
    
    /**
     * returns http://host:port/name
     */
    public String getResponseAddress() {
        return new String("SOAP::"+host+":"+port+"/soap/servlet/rpcrouter::urn:agent:soap");
    }
    
    /**
     * main method for testing only - not for user applications
     */
    public static void main(String argv[]) {
        //FIPA_SOAP_SERVER Server = new FIPA_SOAP_SERVER(null,"127.0.0.1","8002","acc","test");
    }
    
    /**
     *  It returns the reference of the FIPA_SOAP_Connection class that was instantiated in this class
     */
     public FIPA_SOAP_Connection getFIPA_SOAP_Connection(){
         System.out.println("Returning Soap Connection Reference");
         if (transport==null)
            {System.out.println("Transport is null");}
         return transport;
     }
}
