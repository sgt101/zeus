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
import org.omg.CosNaming.*;
import org.omg.CORBA.*;

import javax.rmi.*;
import java.rmi.*; 
import fipa97.FIPA_Agent_97;
/**
    This is an extention of the Server class which is normally used in Zeus
    Instead of providing a sockets/TCPIP based transport this class provides a 
    IIOP based transport as per the FIPA specs<p> 
    Perhaps it should be called IIOP_Server? 
    
 */

public class FIPA_97_Server extends Server implements InTray {
    
  protected AgentContext context = null; 
  private Queue  msgQ = new Queue("fipa97In");    
  private ZeusParser parser = new ZeusParser();
  private String host = null;
  private String port = null;
  private String name = null; 
  
  private FIPA_97_Handler handler = null; 
  
  
  public FIPA_97_Server(FIPA_97_Handler handler, String host, String port, String name, String threadId) {
	Hashtable env = new Hashtable();
	this.handler = handler; 
	this.host = host;
	this.port = port; 
	this.name = name; 
	env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.cosnaming.CNCtxFactory"); 
    //env.put(Context.PROVIDER_URL,host+":" +port);
	try {
	    Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", port);
        org.omg.CORBA.ORB myORB = org.omg.CORBA.ORB.init(new String[0], props);        
	    FIPA_Agent_97 fipa = new FIPA_97_Connection();
	    Thread corba_messageThread = new Thread((FIPA_97_Connection)fipa); 
	    corba_messageThread.setPriority(Thread.MIN_PRIORITY);
	    corba_messageThread.start(); 
	    ((FIPA_97_Connection)fipa).register(msgQ);
	    //org.omg.CORBA.Context initialNamingContext = new InitialContext(env);
	    
	    NamingContext context = NamingContextHelper.narrow(myORB.resolve_initial_references("NameService"));
        NameComponent nc1 = new NameComponent(name, "FIPA_Agent_97");
      //  NameComponent nc2 = new NameComponent(name, "agent");
        NameComponent[] nameComp = {nc1};
        context.rebind(nameComp, fipa);
        

        } 
        catch (Exception e) {
            e.printStackTrace(); 
        }
   this.start();
   this.setName(threadId); 
  }
  
  
  /**
        run loop that pops a message off the Q (it will wait () if there is no message) 
         and then sends it to the handler that was used to construct this object. 
    */
  public void run() {
    processing = true;
    System.out.println("Listening for FIPA 97 IIOP on port " + String.valueOf(port)); 
    while (processing) {     
        String text = this.pop();
	            try { 
	                System.out.println("text in f97 = " + text); 
	               handler.handle(text); 
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
  
  /** 
    returns iiop://host:port/name
    */
  public String getResponseAddress() { 
    return (new String ("iiop://" +host + ":" +port + "/" + name)); 
  }
  
}
