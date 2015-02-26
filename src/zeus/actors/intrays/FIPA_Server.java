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
import fipa97.FIPA_Agent_97;
/**
    This is an extention of the Server class which is normally used in Zeus
    Instead of providing a sockets/TCPIP based transport this class provides a 
    IIOP based transport as per the FIPA specs<p> 
    Perhaps it should be called IIOP_Server? 
    This is not well implemented yet, and Zeus_ACC_Server is the one to use instead.
    
 */

public class FIPA_Server extends Server implements InTray {
    
    //protected Context initialNamingContext = null; 
 // private String  connectionPoint   = new String ("iiop://127.0.0.1:900"); 
  protected AgentContext context = null; 
  private Queue  msgQ = new Queue("fipaIn");    
  private ZeusParser parser = new ZeusParser();
  
  
  public FIPA_Server() { 
    super(); 
    
  }
  
  public FIPA_Server(String hostAddress, String name) {
	Hashtable env = new Hashtable();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.cosnaming.CNCtxFactory"); 
	env.put(Context.PROVIDER_URL,hostAddress);
	try {
        org.omg.CORBA.ORB myORB = org.omg.CORBA.ORB.init(new String[0], null);      
	    FIPA_Agent_97 fipa = new FIPA_message();
	    Thread corba_messageThread = new Thread((FIPA_message)fipa); 
	    corba_messageThread.setPriority(Thread.MIN_PRIORITY);
	    corba_messageThread.start(); 
	    ((FIPA_message)fipa).register(msgQ);
	    Context initialNamingContext = new InitialContext(env);
        initialNamingContext.rebind(name,fipa); 
        } 
        catch (Exception e) {
            e.printStackTrace(); 
        }
   this.start();
   this.setName("FIPA_Server"); 
  }
  
  
  /**
     This method should call an agnostic message handler....
     Right now, it just prints the message to System.out
     
    */
  public void run() {
    processing = true;

    while (processing) {     
        String text = this.pop();
	            try { 
	                System.out.println(text); 
	            }
	            catch (Exception e) { 
	                e.printStackTrace(); 
	                }
	
	   yield(); 
	}	    
    }    
    
       
  public String pop () { 
    return (String) msgQ.dequeue(); 
  }
  
}
