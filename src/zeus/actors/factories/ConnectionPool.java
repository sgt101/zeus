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
package zeus.actors.factories; 
import zeus.actors.OutTray; 
import zeus.util.SystemProps;

public class ConnectionPool {
    
    public int MAX_CONNECTIONS = Integer.parseInt(SystemProps.getProperty("num.connections")); 
    private OutTray[] connections = new OutTray[MAX_CONNECTIONS]; 
    private String[] keys = new String [MAX_CONNECTIONS]; 
    private int write = 0; 
    
    
    /** 
        get a connection from the connection pool. Throw an exception if it is
        not present
        */
    public OutTray getConnection (String address) throws NotFoundException { 
       for (int i = 0; i < MAX_CONNECTIONS ; i++) {
        
        if (keys[i]== null) { ;}
        else { 
           // System.out.println("keys[i] == " + keys[i] + " address == " + address); 
            if (keys[i].equals(address)) { 
                return (connections[i]); 
            }
        }
        //System.out.println(i); 
        }
       throw new NotFoundException(); 
    
    }
    
    
    /** 
        store a connection in the pool. If more than MAX_CONNECTIONS then eliminate the 
        oldest - doesn't check to see if connection is in here!*/ 
    public void addConnection (String address, OutTray connection) { 
        if (write >= MAX_CONNECTIONS){
            write = 0; 
        }
     //   System.out.println("setting address = " + address); 
        connections[write] = connection; 
        keys[write] = address; 
        write ++; 
    }
            
    
}