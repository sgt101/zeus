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

import java.io.*;
import java.util.*;
import zeus.util.*;

/**
    Address is a pure virtual class because we need to deal with Zeus based 
    port and socket addressing, and FIPA GUI addressing, and any bizzar LEAP or 
    Biz talk addressing that is implemented in the future 
    */
public interface Address {
    /**
        getName returns the name of the agent that has this address
        */
   public abstract String getName(); 
   
      
   /**
    set the name field
    */
   public abstract void setName(String val); 
   
   /**
    getHost returns the name of the host that the agent that has this address is 
    currently present on
    */
   public abstract String getHost();
   
   /**
    getType is a Zeus specific method, in Zeus it is used to denote "ANS" or "FACILITATOR".
    Non Zeus agents should be typed as "FIPA-IIOP" or 
    "LEAP" for example. Other types of agent may be implemented if other addressing 
    is used i.e. "LDAP" 
    */
   public abstract String getType();
   
   /**
    getPort returns the port that is listened to by the transport mechanism for the agent 
    in the case of FIPA-IIOP agents this will generally be "900", as this is the default IIOP 
    port number, FIPA-HTTP agents the port number might be "8080" for instance
    */
   public abstract int    getPort();
   
   /**
     is this the same as another address? 
     The behaviour should be that if a FIPA-IIOP agent of name X is present on host Y and 
     also there is a FIPA-HTTP X,Y then FIPA-IIOP == FIPA-HTTP  (if you follow my thread)
     
     However, since the implementation is left to others don't be surprised if this always returns
     false or something dumb like that...
     */
   public abstract boolean equals(Address addr );
   
   /**
    returns true if these two agents share a machine and port - ie. they both live at the 
    same address.... ahhh sweeeet.
    */
   public abstract boolean sameAddress (Address addr);
   
   /**
    produce a nice string version of the address. 
    The string produced MUST be in a format that can be parsed.
    */
   public abstract String toString();
   
   /** 
    *allow the type to be manipulated
    */
   public abstract void setType (String type);
}
