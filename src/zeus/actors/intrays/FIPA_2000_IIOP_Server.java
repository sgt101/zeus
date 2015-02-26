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
import FIPA.*;
/**
    This is an extention of the Server class which is normally used in Zeus
    Instead of providing a sockets/TCPIP based transport this class provides a 
    IIOP based transport as per the FIPA 99/2000  specs<p> 
        
*/

public class FIPA_2000_IIOP_Server extends Server implements InTray {
        
    //protected Context initialNamingContext = null; 
// private String  connectionPoint   = new String ("iiop://127.0.0.1:900"); 
protected AgentContext context = null; 
private Queue  msgQ = new Queue("fipa2000In");    
private ZeusParser parser = new ZeusParser();
private FIPA_2000_Handler handler = null; 
 String fsep = System.getProperty("file.separator");
private String host = null;
private String port = null;
private String name = null; 
private String iorAddr;
String iorpath = SystemProps.getProperty("ior.dir");
private String orbIOR = null;
        
public FIPA_2000_IIOP_Server(FIPA_2000_Handler handler, String host, String port , String name, String threadId) {
    iorAddr = iorpath + name; 
	Hashtable env = new Hashtable();
	this.handler = handler; 
	this.host = host;
	this.port = port; 
	this.name = name; 
	env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, 
	                "com.sun.jndi.cosnaming.CNCtxFactory"); 
	try {
	    Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", port);
        org.omg.CORBA.ORB myORB = org.omg.CORBA.ORB.init(new String[0], props);  
     
        
	    MTS transport = new FIPA_2000_IIOP_Connection();
	    orbIOR = myORB.object_to_string(transport); 
	    java.io.File file = new java.io.File (iorAddr);
	    java.io.FileWriter write = new java.io.FileWriter (file); 
            write.write (orbIOR); 
	    write.flush(); 
	    write.close();

	    Thread corba_messageThread = new Thread((FIPA_2000_IIOP_Connection)transport); 
	    corba_messageThread.setPriority(Thread.MIN_PRIORITY);
	    corba_messageThread.start(); 
	    ((FIPA_2000_IIOP_Connection)transport).register(msgQ);
	  //  Context initialNamingContext = new InitialContext(env);
	    NamingContext context = NamingContextHelper.narrow(myORB.resolve_initial_references("NameService"));
        NameComponent nc1 = new NameComponent(name, "FIPA.MTS");

        NameComponent[] nameComp = {nc1};
        context.rebind(nameComp, transport);
	/*    NameComponet nc = new NameComponent
        initialNamingContext.rebind(name,transport); */
        } 
        catch (Exception e) {
            e.printStackTrace(); 
        }
this.start();
this.setName(threadId); 
}
      
      
/**
    This method should call an agnostic message handler....
    Right now, it just prints the message to System.out
         
    */
public void run() {
    processing = true;
    System.out.println("Listening for FIPA 2000 IIOP on port " + String.valueOf(port)); 
    while (processing) {     
        FIPA.FipaMessage message = this.pop();
	            try { 
	                handler.handle(message); 
	            }
	            catch (Exception e) { 
	                e.printStackTrace(); 
	                }
    	
	yield(); 
	}	    
    }    
        
           
public FIPA.FipaMessage pop () { 
    return (FIPA.FipaMessage) msgQ.dequeue(); 
}



 /** 
    used to return iiop://host:port/name
    now returns corbaname://host:port/NameService/name
    */
    public String getResponseAddress() { 
        return new String ("corbaname:iiop:" + host + ":" + port + "/" +"NameService/"+ name); 
    }
 
      
    public String getIORAddress () { 
        return new String (orbIOR); 
    }

}