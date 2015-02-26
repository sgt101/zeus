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
    * public limited company are Copyright 1996-2001. All Rights Reserved.
    * 
    * THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
    */

    package zeus.actors.intrays;
    import javax.rmi.*;
    import java.rmi.*;
    import zeus.util.*;
    import FIPA.*;
    /**
         FIPA_2000_IIOP_Connection is a concreat implementation of the 
        FIPA.MTS interface that is designed to talk to zeus.actors.fipa.FIPA_2000_Server implementors.
        It runs in its own thread, when it recieves a message it calls the server and
        puts that message on a Queue. The server then dequeues the messages and deals with them 
        as it sees fit.
        @author Simon Thompson
        @since 1.1
        */
    public class FIPA_2000_IIOP_Connection extends _MTSImplBase implements Runnable 

    {
     
        protected Queue queue;         
         
        public void run () { 
      /*      while (true) { 
                wait();
                Thread.yield(); 
            }*/
            }

         
        public void register(Queue q)  { 
            this.queue = q;
        }
            
             
            
	    public void message (FIPA.FipaMessage aFipaMessage) {
	    //  FIPAPerformative perf = new FIPAPerformative (aFIPAMessage); 
	        debug("Message received in F2000"); 
	        debug (aFipaMessage.toString()); 
	        queue.enqueue(aFipaMessage);
	    }
    	
    	

	    public FIPA_2000_IIOP_Connection () throws RemoteException {
	    }
	    
	    
	    public void debug (String str) {
	     System.out.println("FIPA_2000_IIOP_Connection" + str);    
	    }
    }