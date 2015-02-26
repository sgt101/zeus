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
import javax.rmi.*;
import java.rmi.*;
import zeus.util.*;
import fipa97._FIPA_Agent_97ImplBase;
/**
    {@author Simon Thompson} FIPA_message is a concreat implementation of the 
    FIPA_Agent_97 interface that is designed to talk to zeus.actors.fipa.FIPA_Server.
    It runs in its own thread, when it recieves a message it calls the server and
    puts that message on a Queue. The server then dequeues the messages and deals with them 
    as it sees fit.
    */
public class FIPA_message extends _FIPA_Agent_97ImplBase implements Runnable 

{
 
    protected Queue queue;         
     
    public void run () { 
 /*       while (true) { 
            Thread.yield(); 
        }*/
        }

     
    public void register(Queue q)  { 
        this.queue = q;
    }
        
         
        
	public void message( String acl_message ) {
	    queue.enqueue(acl_message);
	}
	
	

	public FIPA_message () throws RemoteException {
	}
}
