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
package zeus.actors.outtrays;

import zeus.actors.intrays.*;
import javax.agent.service.*;
import javax.agent.*;
import zeus.actors.*;
import zeus.util.*;
import java.net.*;
import java.io.*;
import java.util.Date;

import FIPA.*;

/**
 * FIPA_2000_HTTP_Transport is an OutTray that is used to send a
 * message to a FIPA http agent.
 * @author Simon Thompson
 * @since 1.1
 */
public class FIPA_2000_HTTP_Transport implements OutTray {
    
    
    private FIPA_2000_HTTP_Accessor target = null;
    private FileWriter log = null;
    private File file;
    
    protected int BUFFER_SIZE = 1024; //was 4096
    
    /**
     * simple constructor that registers where this OutTray is looking to
     * send messages to.
     */
    public FIPA_2000_HTTP_Transport(FIPA_2000_HTTP_Accessor target,File file) {
        this.target = target;
        this.file = file;
        try {
            this.log = new FileWriter(file,true); }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    public void send  (Object obj) throws UnsuitableMessageException {
        try {
            javax.agent.Envelope env = (javax.agent.Envelope) obj;
            send(env);
        } catch (ClassCastException cce) {
            throw new UnsuitableMessageException("Must be javax.agent.envelope to work with this transport");
        }catch (Exception e) {
            e.printStackTrace();
            throw new UnsuitableMessageException("Bad message in send() - unknown problem, Excepiton printed to sout");
            
        }
    }
    
    /**
     * use this message to send an Envelope containing a FIPA_Performative to
     * an FIPA 2000 HTTP complient interface..
     */
    public void send(javax.agent.Envelope envelope) {
        try {
            zeus.concepts.FIPAPerformative fperf = (zeus.concepts.FIPAPerformative) envelope.getObject();
            String sendString = new String();
            sendString += "This is not part of the MIME multipart encoded message.";
            sendString += "\015\012--251D738450A171593A1583EB";
            sendString += "\015\012Content-Type: application/xml\015\012\015\012";
            
            sendString += "<?xml version=\"1.0\"?>\n";
            sendString += "\t<envelope>\n";
            sendString += "\t\t<params index=\"1\">\n";
            sendString += "\t\t<to>\n";
            sendString += fperf.getReceiversXML();
            sendString += "\t\t</to>\n";
            sendString += "\t\t<from> \n" ;
            sendString += fperf.getSenderXML();
            sendString += "\t\t</from>\n\n";
            sendString += "\t\t<intended-receiver>\n" + fperf.getReceiversXML() +"\n";
            sendString += "\t\t</intended-receiver>\n";
            
            sendString += "<acl-representation>fipa.acl.rep.string.std</acl-representation>\n\n";
            sendString += "<payload-encoding>US-ASCII</payload-encoding>\n\n";
            sendString += "<date>" + FIPA_Date.getDate() + "</date>\n\n";
            sendString += "</params>\n";
            
            sendString += "</envelope>\n\n";
            sendString += "\015\012--251D738450A171593A1583EB\015\012";
            sendString += "Content-Type: application/text\015\012\015\012";
            sendString += fperf.toFIPAString();
            sendString += "\015\012--251D738450A171593A1583EB--\n";
            //added
            
            sendString += "\r\n\015\012";
            
            
            try {
                
                URL url=new URL(target.getAddress());
                String host = url.getHost();
                int port = url.getPort();
                Socket sock = new Socket(host,port);
                BufferedOutputStream stream = new BufferedOutputStream(sock.getOutputStream(),BUFFER_SIZE);
                BufferedInputStream is=new BufferedInputStream(sock.getInputStream(),BUFFER_SIZE);
                String header = new String("POST http://" + host+":"+port+"/ACC HTTP/1.1\nCache-Control: no-cache\nHost: " +host+":"+String.valueOf(port)+"\nMime-Version: 1.0\nContent-type: multipart/mixed; \n\tboundary=\"251D738450A171593A1583EB\""+"\nContent-length: "+String.valueOf(sendString.length())+"\nConnection: close\n\n");
                String sendMessage = new String(header + sendString);
                // sending request
                
                PrintWriter os=new PrintWriter(stream);
                os.print(sendMessage);
                os.flush();
                System.out.println(" flushed out buffer"); 
                Date today = new Date();
                int month = today.getMonth() + 1;
                int year = today.getYear();
                String all = new String("Sent message at " + today.getDate() +"/" + String.valueOf(month) + "/" + String.valueOf(year) + " at " + today.getHours() +":" +today.getMinutes() +":" + today.getSeconds() + "\n");
                log = new FileWriter(file,true); 
                log.write(all);
                log.write(sendMessage);
                log.write("\n\n");
                log.flush();
                log.close(); 
                file.setLastModified(System.currentTimeMillis());
                
                debug(sendMessage);
                //os.flush();
                
                // http is synchronous so we need to listen for what comes back from it.
                ReplyListener rl = new ReplyListener(is,os,file);
                Thread tr = new Thread(rl);
                tr.start();
                
                //os.close();
                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //   System.out.println("Exception may be due to malformed messsage received");
            return;
        }
        
        
    }
    
    
    
    
    /**
     * debug was used to see what was going on when we built this
     */
    private  void debug(String message){
        // System.out.println("FIPA_2000_HTTP:>>" + message);
    }
    
}
