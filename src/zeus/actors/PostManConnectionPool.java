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



package zeus.actors;

import java.net.*;
import java.io.*;

public class PostManConnectionPool { 
    static PrintWriter last = null; 
    static  String host  = null;
    static int port = 0; 
    
    public static PrintWriter getLast () {
           return (last); 
    }
    
 
       
    public static void setLast (PrintWriter current) { 
        last = current; 
    }
    
    
    public static boolean isSameAsAndSet(String chost, int cport) {
        if (host == null) {
            System.out.println("is null"); 
            host = chost; 
            port = cport; 
            return false;
        }
        if (host.equals(chost)&& port==cport) {
                System.out.println(port + "==" +cport + "," + host +"== "+ chost);
                
                return true; 
           }
           else {
            System.out.println("was not equal"); 
            host = chost; 
            port = cport; 
            return false; 
           }
    }
    
}
