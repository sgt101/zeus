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
    import zeus.concepts.*;

    /**
        interface to allow the connection of a FIPA_2000_Server to an object that
        collates and manages message reception from it (the FIPA_97_Server) and 
        other sources (FIPA_97_Server, FIPA_2000_HTTP_Server). <p> 
        Alternatively this can be used to implement a single inbox service. 
        @author Simon Thompson 
        @since 1.1
        */
    public interface FIPA_2000_Handler {

        
        /** 
            handle is the method that must be implemented by the collation 
            object. I imagine that this method will process the string received
            in some way, perhaps by using it to instantiate a FIPAPerformative, or
            a Performative, and then call other methods that will decide what to do with it
            <p> Implemetations of this method is synchronized to preven simultaneous access from multiple 
            FIPA_2000 servers (for instance when the agent has two names in the CORBA name 
            space, or when it is using both IIOP and HTTP transports). <p> 
            @param message - the message received from the FIPA_2000_Server that this is 
            handling 
            */
        public  void handle (FIPA.FipaMessage aMessage);
        
        
        /** 
        Allow the registration of an agent's presence with 
        the handler. 
        */
        public void registerAgent(FIPA_AID_Address addr, String name);        
        
    }