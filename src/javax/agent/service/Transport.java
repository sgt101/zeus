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

package javax.agent.service; 
import javax.agent.*;


/**
    Zeus version of the Java Agent Services Transport interface.
    
    See www.java-agent.org for the original spec (such as it is in 25/1/01). 
    @author Simon Thompson
    @version 1.1
    */
public interface Transport {
    
    
    /** 
        I anticipate that the general use case for this method will be to use an implementation 
        of zeus.actors.services.TransportFactory to generate an implentation of this interface, 
        and then call this send method in order to send a message. 
        */
    public void send (Envelope envelope);
    
   
    /** 
        I am not sure how receive() and receive (long) are going to be relevant 
        to the Zeus model of active mailboxes... Still, we shall but try!
        */
    public Envelope receive(); 
   
   
     /** 
        I am not sure how receive() and receive (long) are going to be relevant 
        to the Zeus model of active mailboxes... 
        */
    public Envelope receive(long millis) throws TimeoutException; 
    
    
    /** 
        this should return the iiop://iiop.zeus.bt.com/agentcities/adastral part of the 
        agent aid that was used to find this transport. 
        */
    public String getLocation (); 
    
    
}