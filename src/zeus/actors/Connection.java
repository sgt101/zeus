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
import java.util.*;
import zeus.util.*;
import zeus.concepts.ZeusParser;
import zeus.concepts.Performative;
import zeus.concepts.PerformativeParser; 


class Connection {// extends Thread{
  protected static int BUF_SIZ = 1000;
  private static Integer token; 
  
  protected Socket	client;
  protected Server    	server;
  protected BufferedInputStream ins;
  String id = null; 
  // meaningless init  to allow rearch
  public Connection () {
  ;}
  


  // Initialize the streams and start the thread
  public Connection(Socket client, Server server, String id ) {
    this.id = id; 
    if (token == null) token = new Integer(0);  
    this.client = client;
    this.server = server;
 
         try {
            ins = new BufferedInputStream (client.getInputStream());
                    }
            catch (IOException e) {
                System.err.println("Exception while getting socket streams: " + e);
                e.printStackTrace();
                try {
	                client.close();
	                client = null;
                    }
                    catch (IOException e2) {
	                    System.err.println("Exception while closing client: " + e2);
	                    e2.printStackTrace(); }
                return;
            }
      this.run(); 
  }
  
  
    public void run() { 
      boolean done = false;
     
        while(!done) { 
            int x = 0;       
            int count = 1; 
            byte buf[] = new byte[BUF_SIZ];
            String text = new String("");
            while ( !done ) {
            try {
                    Thread.yield(); //Avoids starving socket buffer
	            x = ins.read(buf);
	           // System.out.println(x); 
            }
            catch (IOException e) {
	                Core.DEBUG(1,"IOException: " + e);
	                e.printStackTrace();
	                server.updateCount(-1);
	                try {
	                ins.close();
	                ins = null;
	                client.close();
	                client = null;
	                }
	                catch (IOException e1) {
	                System.err.println(e1);
	                e1.printStackTrace();
	                }
	                return;
            }

            if ( x == -1 ) {
	            done = true;
                    debug ("x was -1 (done)"); 
            }
            else if (x <BUF_SIZ)  {
                done = true; 
                text += new String(buf,0,x);
                debug (x + " bytes read (less than BUF_SIZ), " + 
                       "assuming end of buffer.");
            }
            else {
	            text += new String(buf,0,x);
                    debug (x + " bytes read (BUF_SIZ), continuing.");
            }
            }
            buf = null;
            if ( text.equals("")) {
                System.err.println("No data read from stream");
                return;
            }

    try {
     PerformativeParser parser = new PerformativeParser(new ByteArrayInputStream(text.getBytes()));
     //text = null;
     Performative msg = parser.Message();
     debug ("trying test"); 
        if ( !msg.isValid() ) {
            debug ("not valid"); 
             return;
        }
        debug("Sending message to mhandler"); 
        server.newMsg(msg);
    }
    catch (Exception e)  { 
         debug ("in exception handler"); 
        return;
    }
    catch (Error e) { 
        e.printStackTrace(); 
        debug ("in error handler"); 
         return;
    }
    debug ("after try catch block"); 
         }
  try {
    server = null; 
    if (ins != null) { 
           ins.close(); 
           ins=null; }
    if (client != null ) { 
            client.close(); 
            client = null; 
    }
    id = null; 
    }
    catch (Exception e) { 
        e.printStackTrace(); 
    }

  }


  /** 
    isEncrypted returns true if this message is encrypted.
   */
   public boolean isEncrypted (String text) {
    if (text.startsWith("encrypted")) 
        return (true); 
        else
            return false; 
   }

    /**
        decrypt returns the decrypted string which can then be converted into a performative and
        passed to the inbox*/
 /* public String decrypt (String text) { 
        try {
            String mess = text.substring (9); 
            String keyId = text.substring (0,10); 
            byte[] 
            if (server.getKey(keyId)) != null 
            
    
  }
*/
  protected void finalize() {
    try {
      if ( ins != null    ) ins.close();
      if ( client != null ) client.close();
    }
    catch (IOException e) {
    }
  }
  
  public String toString() {
   return " connected to: " + client.getInetAddress().getHostName() + ":" + client.getPort();
  }
  
  
  private void debug (String str) {
    System.out.println("Connection>> " + str);
  }
       
  
  }

