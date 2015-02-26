/**
 *             STANDARD DOCUMENTATION - EXPERIMENTAL SOFTWARE
 *                       (QGN 13, Issue 4 refers)
 *                          Copyright BTexact Technologies 2001
 *  @author      : Roberto Avalos   
 *  OUC code     : DVA5P
 *  File name    : FIPA_SOAP_SERVER.java
 *  @version     : 1.0  
 *  Date         : 9/April/2002
 *  Purpose      : FIPA_SOAP_Connection handles incoming connections on the SOAP Server and decides 
 *                  whether or not they are meant for it
 *  Testing      :   
 *  Software req.: Java Virtual Machine
 *  Hardware req.: 
 *  The contents of this file are subject to the BT "ZEUS" Open Source 
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
import zeus.concepts.*; 
import zeus.actors.*;
import zeus.util.*;
    /**
        FIPA_SOAP_Connection handles incoming connections on the SOAP Server
        and decides 
        whether or not they are meant for it (ie: is the name on the connection the 
        same as the name in the init of this class)<p>
        If the connection is relevant then the data will be read, a response (as per
        the spec ) will be sent and the message will be decoded into a FIPA.FipaMessage 
        and placed on the registered queue for processing by the relevant server <p> 
        The class constructor takes a host a port and a name : should this agent only listen 
        for connections for itself at this port and host? That is what is implemented here...
        comments on a postcard please<p>
        @author Roberto Avalos and Simon Thompson
        @since 1.1
        */
public class  FIPA_SOAP_Connection  implements Runnable
    {
        
    protected Queue queue;     
        
    /** Creates new FIPA_SOAP_Message */
    public FIPA_SOAP_Connection() {
            
    }
    public void register(Queue q)  { 
            this.queue = q;
            System.out.println("registering ="+this.queue.toString());
    }

    /*
     *  All messages received at FIPA_SOAP_MESSAGE will be forwarded to this method. In turn, this methos will queue the message
     */
    public void message( String acl_message )throws Exception {
       //  FIPAPerformative fperf = ZeusParser.fipaPerformative (acl_message); 
	/*System.out.println("");
        System.out.println("**********************************************************************");
        System.out.println("Message received at FIPA_SOAP_Connection: ");
        System.out.println(acl_message);*/
   
        try{
                System.out.println("Registered queue is : "+ this.queue.toString()); 
                queue.enqueue(acl_message);
            }
        catch (Exception e) 
            {
                System.err.println("here it is the fatal error");
                e.printStackTrace(); 
            }
        
	}
        
        public void run() {
        }
        
}
