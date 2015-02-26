 /**
 *             STANDARD DOCUMENTATION - EXPERIMENTAL SOFTWARE
 *                       (QGN 13, Issue 4 refers)
 *                          Copyright BTexact Technologies 2001
 *  @author      : Roberto Avalos   
 *  OUC code     : DVA5P
 *  File name    : FIPA_SOAP_MESSAGE.java
 *  @version     : 1.0  
 *  Date         : 9/April/2002
 *  Purpose      : This is the actual interface that describes the web service for receiving messages
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
import zeus.agents.*; 
import zeus.concepts.*; 
import zeus.actors.*;
import zeus.util.*;
import java.io.*;

/**
 *      This Class is utilised when SOAP protocol is determined to be the communication protocol for agent interoperation.
 *      It is invoked by the SOAP Server on every incoming message from other agents and is deployed as the Web Service of this 
 *      agent platform. In other words, the method "message" is exposed to other agents to receive all messages from them. 
 *      As messages arrive to method "message", this one forwards the message to class FIPA_SOAP_Connection, which in turn stores the
 *      message in a queue for further processing by the agent.
 */     

public  class   FIPA_SOAP_MESSAGE {
    private int iSOAPInvokationCounter=0;
    private FIPA_SOAP_Connection SOAP_Conn=null;

    /** Creates new FIPA_SOAP_MESSAGE */
    public  FIPA_SOAP_MESSAGE() {
    }
    
    /**
     *  This method receives a string containing a message from an external agent for processing. This method forwards this message to
     *  FIPA_SOAP_Connection. The reference of the instance of this class is obtained from "launchAgent" method, which runs the agent
     *  and returns the reference of the instantiation of this class.
     */
    public  boolean message(String acl_message) throws Exception
    {   
        iSOAPInvokationCounter++;
        System.out.println(acl_message);
        
        try{
      
            SOAP_Conn.message(acl_message); /*.queue*/

        }
        catch (Exception e) 
            {
             System.err.println("but with an error");
             e.printStackTrace(); 
             return false;
         }
         return true; 
      
    }
    
  
    
}
