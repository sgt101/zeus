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


public class ZeusAddress implements Address {
   private String name;
   private String host;
   private int    port;
   private String type;

   public ZeusAddress( String name, String host, int port, String type ) {
      this.name = name;
      this.host = host;
      this.port = port;
      this.type = type;
   }

   public ZeusAddress( Address addr ) {
      name = addr.getName();
      host = addr.getHost();
      port = addr.getPort();
      type = addr.getType();
   }

   public String getName() { 
        return name; }
   public String getHost() { return host; }
   public String getType() { return type; }
   public int    getPort() { return port; }

   public boolean equals(Address addr ) {
      return name.equals(addr.getName()) &&
             host.equals(addr.getHost()) &&
             port == addr.getPort()      &&
             type.equals(addr.getType());
   }

   public boolean sameAddress(Address addr ) {
      return host.equals(addr.getHost()) &&
             port == addr.getPort();
   }

   public String toString() {
      return( "(" +
               ":name " + name + " " +
               ":host \"" + host + "\" " +
               ":port " + port + " " +
               ":type " + type +
              ")"
            );
   }

    public void setName (String name ) { 
     this.name = name;    
    }
    
    /**
     * allow the type to be manipulated
     */
    public void setType(String type) {
        this.type = type; 
    }    
    
}


