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


package zeus.concepts;

import java.util.*;
import zeus.actors.outtrays.*;
/**
    FIPA_AddressBook is an extention of the addressbook that can cope with fipa
    AID addresses, the intention is that this should be used to implement an 
    ACC agent that can act as a gateway from Zeus agencies to the FIPA world. <p> 
    The objective of this class is to provide something that can store agent aliases 
    so that when the ACC gets a message that is addressed to <i> testForwardACCiioptbtoledofuturesbtcom</i> 
    it can lookup the appropriate aid to construct a fipa performative which can be 
    sent to the target agent
    */
public class FIPA_AddressBook extends zeus.concepts.AddressBook {
    
    private Hashtable FIPA_Addresses= new Hashtable(); 

    
    /** 
        add an address to the book
        */
    public boolean add(FIPA_AID_Address fipa_address) { 
        FIPA_Addresses.put (fipa_address.getAlias(), fipa_address); // was getName
        return true; 
        }
        
    
    
   /**
        find a proper address from a name of an agent 
        */
    public FIPA_AID_Address lookupFIPA(String name) { 
        Enumeration allNames = FIPA_Addresses.keys(); 
        while (allNames.hasMoreElements()) { 
            String current = (String) allNames.nextElement();
            if (current.equals(name)) {
                return ((FIPA_AID_Address)FIPA_Addresses.get(current)); }
        }
        //improve this by raising an exception? 
        return (null); 
        }
        
    
    /**
        find a proper address from a name of an agent 
        */
    public FIPA_AID_Address lookupAlias(String alias) { 
        Enumeration allAddresses = FIPA_Addresses.elements(); 
        while (allAddresses.hasMoreElements()) { 
            try {
                FIPA_AID_Address current = (FIPA_AID_Address) allAddresses.nextElement(); 
                String currentAlias = current.getAlias();
          //      System.out.println("currentA =" + currentAlias + " alias = " +alias);
                if (currentAlias.equals(alias)) {
                    return current; }}
                catch (ClassCastException cce) {
                    // ignor this - if a class cast occurs it is because 
                    // a Address is being cast to a FIPA_AID_Address, which is expected 
                    // and irrelevant
                }
        }
        //improve this by raising an exception? 
        return (null); 
        }
        
    
    
    /**
    determine if the address for this agent  is in the address book, return 
    the correct address object if it is...
    */
    public FIPA_AID_Address checkAddress(Address addr ) { 
      String name = makeAlias(addr); // added getHost (could be a problem...)
      FIPA_AID_Address lookedUp = lookupFIPA(name); 
      if (lookedUp!= null) 
            return lookedUp;
        else
          try {  
            return (FIPA_AID_Address) addr; }
            catch (Exception e) { 
                e.printStackTrace (); 
                System.out.println("Probably class cast problem in checkAddress"); 
                System.out.println("You may have tried to check to see if a Zeus format"); 
                System.out.println("address is a FIPA_AID_Address - sorry, this causes an Exception "); 
                return null; 
            }
            
    }
    
    /**
     *utility method to be used to reconsitute local platform name from an 
     *external name/host combo
     */
    public static String makeAlias (Address addr) { 
        String host = addr.getHost(); 
        host.replace(':','X');
        host.replace('/','X'); 
        return (addr.getName() + host); 
    }
        
        
    
    
}