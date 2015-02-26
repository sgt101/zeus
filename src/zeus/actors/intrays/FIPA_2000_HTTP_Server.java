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

import javax.rmi.*;
import java.rmi.*;
import FIPA.*;
/**
 * This is an extention of the Server class which provides an "InTray" service for HTTP
 * transports. It will read messages from a http connection and will then call a the handle
 * method in the FIPA_2000_Handler object that was used to init it.
 * @author Simon Thompson
 * @since 1.1
 *
 */

public class FIPA_2000_HTTP_Server extends Server implements InTray {
    
    protected AgentContext context = null;
    private Queue  msgQ = new Queue("fipaHTTP2000In");
    private ZeusParser parser = new ZeusParser();
    private FIPA_2000_Handler handler = null;
    
    private String host = null;
    private String port = null;
    private String name = null;
    private File file = null;
    private FileWriter log;
    
    public FIPA_2000_HTTP_Server(FIPA_2000_Handler handler, String host, String port,  String name, String threadId) {
        this.handler = handler;
        this.host = host;
        this.port = port;
        this.name = name;
        try {
            
            
            FIPA_2000_HTTP_Connection transport = new FIPA_2000_HTTP_Connection(host,port,name);
            Thread http_messageThread = new Thread(transport);
            //   http_messageThread.setPriority(Thread.MIN_PRIORITY);
            http_messageThread.start();
            transport.register(msgQ);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.start();
        this.setName(threadId);
    }
    
    
    /**
     *   this is the method that gets stuff off the q and sends it to the handler for
     *processing
     */
    public void run() {
        processing = true;
        System.out.println("Listening for FIPA 2000 HTTP on port " + String.valueOf(port));
        
        try {
            file =  new File(SystemProps.getProperty("http_root") + SystemProps.getProperty("in_log"));
            log = new FileWriter(file,true);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        while (processing) {
            FIPAPerformative message = this.pop();
            file.setLastModified(java.lang.System.currentTimeMillis());
            try {
                debug(message.getContent());
                FIPA.FipaMessage fmess = message.FipaMessage();
                handler.handle(fmess);
                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            yield();
        }
    }
    
    
    public FIPAPerformative pop() {
        FIPAPerformative perf =  (FIPAPerformative) msgQ.dequeue();
        Date today = new Date();
        int year = today.getYear();
        int month = today.getMonth() +1;
        try {
            file.setLastModified(java.lang.System.currentTimeMillis());
            log = new FileWriter(file,true); 
            log.write("Message recieved at : " + today.getDate() +"/" + String.valueOf(month) +"/" + String.valueOf(year) + " at " + today.getHours() +":" +today.getMinutes() +":" + today.getSeconds()+"\n\n\n");
            System.out.println("UPDATING FILE TIMESTAMP!!!!!!!");
            log.write(perf.toFIPAString());
            log.write("\n\n");
            log.flush(); 
            file.setLastModified(java.lang.System.currentTimeMillis());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return(perf);
    }
    
    /**
     * returns http://host:port/name
     */
    public String getResponseAddress() {
        return new String("http://" + host + ":" + port + "/" + name);
    }
    
    
    /**
     * main method for testing only - not for user applications
     */
    public static void main(String argv[]) {
        FIPA_2000_HTTP_Server Server = new FIPA_2000_HTTP_Server(null,"127.0.0.1","8002","acc","test");
    }
    
    
    public void debug(String str) {
        System.out.println("HTTP_Server>>" + str);
    }
    
    
}