/*
research product only
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
This class allows us to communicate agent to agent in a container
via method calls - not very agent, but high performance; so may be 
of use in some applications.        
*/

public class Container_Server extends Server implements InTray {
        
protected AgentContext context = null; 
private Queue  msgQ = new Queue("ContQueue");    
private ZeusParser parser = new ZeusParser();
private FIPA_2000_Handler handler = null; 
String fsep = System.getProperty("file.separator");
private String containerName = SystemProps.getProperty("container.name");  
private String name = null;      


public Container_Server(FIPA_2000_Handler handler, String host, String port , String name, String threadId) {
	Container_Connection conn = new Container_Connection(msgQ); 
        Thread containerThread = new Thread(conn);
        this.name = name;
	containerThread.start(); 
	this.handler = handler;
	Thread thr = new Thread (this);
	thr.start(); 
}
      
      
/**
    This method should call an agnostic message handler....

    */
public void run() {
    processing = true;
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
	makes an adderss of container@containerId/agentName
    */
    public String getResponseAddress() { 
        return new String ("container@" + containerName+"/" + name); 
    }
 
     
}