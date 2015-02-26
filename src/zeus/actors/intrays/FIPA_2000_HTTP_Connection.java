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
import zeus.util.*;
import FIPA.*;
import zeus.concepts.*;
import zeus.actors.outtrays.*;
import zeus.actors.*;
import zeus.actors.factories.*;


/**
 * FIPA_2000_HTTP_Connection handles incomming connections on this host/port socket
 * and decides
 * whether or not they are meant for it (ie: is the name on the connection the
 * same as the name in the init of this class)<p>
 * If the connection is relevant then the data will be read, a response (as per
 * the spec ) will be sent and the message will be decoded into a FIPA.FipaMessage
 * and placed on the registered queue for processing by the relevant server <p>
 * The class constructor takes a host a port and a name : should this agent only listen
 * for connections for itself at this port and host? That is what is implemented here...
 * comments on a postcard please<p>
 * @author Simon Thompson
 * @since 1.1
 */
public class FIPA_2000_HTTP_Connection  implements Runnable {
    protected Queue queue;
    private String response_ok = new String("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nCache-Control: no-cache\r\nConnection: close\n");
    private String response_not_ok = new String("HTTP/1.1 401 NOT_OK \nContent-Type: text/plain\nCache-Control: no-cache\nConnection: close\n");
    private String host = null;
    private String name = null;
    private String port = null;
    private int MAX_HTTP_QUEUE = 10;
    /**
     * MAX_LINES_TO_READ_WITHOUT_YEILDING is used to control how
     * many lines will be read in from a connection in one lump. Without this I
     * think that a DOS attack could be mounted via a http connection just
     * by sending it an infinitely long message: perhaps another agent could
     * use this to prevent this agent from bidding in a round of contracts, or
     * from sending some alert information to another agent! <p>
     * This is protected to prevent it being reset in Agent specific code
     */
    protected int MAX_LINES_TO_READ_WITHOUT_YEILDING = 5000;
    protected int MAX_LINES_TO_READ_WITHOUT_SHUTTING = 5000;
    /**
     * if MAX_CONTENT_LENGHT is greater than 200001 it will be ignored and
     * max_int length content will be accepted
     */
    protected int MAX_CONTENT_LENGTH = 100000;
    private ServerSocket serverSocket = null;
    
    
    /**
     * run method that makes sure a thread is trying to pick up inputs from a
     * connection <p>
     * The loop blocks on the ServerSocket.accept() method. A count variable
     * is used to prevent an DOS on this agent by yeilding after a certain number
     * of lines have been read.
     */
    public void run() {
        while (true) {
            try {
                String currentMessage = new String();
                debug("Listening in http");
                Socket connection = serverSocket.accept();
                //     InetAddress inet = connection.getInetAddress();
                //    debug("name " + inet.getHostName() + "  address " + inet.getHostAddress());
                
                PrintWriter out = new PrintWriter(connection.getOutputStream(), true );
                InputStreamReader ins = new InputStreamReader(connection.getInputStream());
                BufferedReader in = new BufferedReader(ins);
                int count = 0;
                String ln;
                boolean done = false;
                String contentStr = null;
                String boundaryVal = null;
                int contentLength = 0;
                int bigCount = 0;
                while(!done) {
                    ln=in.readLine();
                    debug(ln);
                    currentMessage += ln +"\n";
                    if (boundaryVal == null)
                        boundaryVal = testAndSetBoundary(ln);
                    if (contentLength == 0)
                        contentLength = testAndSetContentLength(ln);
                    
                    if (contentLength > MAX_CONTENT_LENGTH) {
                        respondNotOK(out);
                        in.close();
                        out.close();
                        connection.close(); // reset peer, no DOS!
                    }
                    debug("boundary=" + boundaryVal);
                    // debug (String.valueOf(contentLength));
                    if ((boundaryVal != null) && (contentLength > 0)) { // start watching for a header end
                        //  debug("finding boundary");
                        if (ln.equals("--" + boundaryVal)) {
                            debug("found boundary");
                            contentStr = new String();
                            ln = in.readLine();
                            String ender = "--" + boundaryVal + "--";
                            
                            while (!ender.equals(ln.trim())) {
                                debug("content:" + ln);
                                contentStr += ln +" "; // added " "
                                ln = in.readLine();
                                
                                
                            }
                            contentStr += ender;
                           /* char [] content = new char [contentLength];
                            in.read(content,0,contentLength);
                            contentStr = new String (content); */
                            //  debug ("content length = " + contentStr.length());
                            // debug ("\n\ncontent = " + contentStr);
                            done = true;
                        }
                    }
                    
                    debug("readIn >> " + ln);
                    bigCount++;
                    count++;
                    //debug("before maxlines");
                    if (count > MAX_LINES_TO_READ_WITHOUT_YEILDING) {
                        Thread.yield();
                        count = 0; }
                    //debug("before connection clsoser");
                    if (bigCount > MAX_LINES_TO_READ_WITHOUT_SHUTTING) {
                        //respondNotOK(out);
                        in.close();
                        out.close();
                        connection.close(); // reset peer, no DOS!
                    }
                    //debug("after connection closer");
                    /*if (done) {
                        debug ("DONE!!");
                        }
                        else {
                                debug ("NOT DONE!!!");
                                }  */
                }
                // in.close();
                //debug ("1");
                debug(contentStr);
                FIPAPerformative fmess = process(contentStr,boundaryVal);
                //debug("2");
                if (fmess!= null) {
                    respondOK(out);
                    message(fmess); }
                else {
                    respondNotOK(out);
                }
                //  debug("3");
                //       Thread.yield();
                connection.close();
                //debug("4");
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("IO error opening http connection from another agent: recovering");
            }
            catch (Exception e) {
                e.printStackTrace();}
            catch (Error er) {
                System.out.println("Error trapped and handled in FIPA_2000_HTTP_Connection: recovering");
                er.printStackTrace(); }
            catch (Throwable tr) {
                System.out.println("*** Throwable trapped and handled in FIPA_2000_HTTP_Connection: recovering");
            }
            
        }
        
    }
    
    
    /**
     * hitch this connection to a message queue
     */
    public void register(Queue q)  {
        this.queue = q;
    }
    
    
    /**
     * pull the boundary val from the header
     */
    private String testAndSetBoundary(String ln) {
        
        String tmp = ln.toLowerCase();
        debug(">> test boundary:" + tmp);
        // works for jade
        if (tmp.startsWith("content-type: multipart/mixed; boundary=\"")) {
            //     debug (ln.substring (41,ln.length()-1));
            debug(">> returning boundary!");
            return (ln.substring(41,ln.length()-1));
        }
        if (tmp.startsWith("content-type: multipart/mixed;  boundary=\"")) {
            //     debug (ln.substring (41,ln.length()-1));
            debug(">> returning boundary!");
            return (ln.substring(42,ln.length()-1));
        }
        if (tmp.startsWith("content-type: multipart/mixed ; boundary=\""))    {
            return (ln.substring(42,ln.length()-1));
        }
        else if (tmp.startsWith("\tboundary=\"")) {
            //         debug (ln.substring (11,ln.length()-1));
            debug(">> returning boundary!");
            return (ln.substring(11, ln.length()-1));
        }
        else {
            debug(">> returning null!");
            return null;
        }
    }
    
    
    /**
     * pull the content length from the header
     */
    private int testAndSetContentLength(String ln) {
        if (ln.startsWith("Content-length") || ln.startsWith("Content-Length")) {
            String lengthContent = ln.substring(15,ln.length()).trim();
            int contentLength = Integer.parseInt(lengthContent);
            //       debug (String.valueOf(contentLength));
            return contentLength;
        }
        else {
            return 0;
        }
    }
    
    
    
    /**
     * send the mandated response to a successful message reception
     * episode (see XC00084C)
     */
    public void respondOK(PrintWriter out) {
        out.println(response_ok);
        out.flush();
        out.close();
    }
    
    
    /**
     * send a not OK response (hopefully we won't have to do this!)<p>
     * This is not something that I could see mandated in the FIPA-spec,
     * but I think that it is a good idea - otherwise the connection remains open
     * until a timeout. <p>
     * Perhaps sufficient open connections could be used as some sort of attack
     * on the agent.
     */
    public void respondNotOK(PrintWriter out) {
        out.println(response_not_ok);
        out.flush();
        out.close();
    }
    
    
    /**
     * message is used to handle the completely read and parsed message when
     * it has come off the message queue
     */
    public void message(FIPAPerformative aFipaMessage) {
        //  FIPAPerformative perf = new FIPAPerformative (aFIPAMessage);
        queue.enqueue(aFipaMessage);
    }
    
    
    public FIPA_2000_HTTP_Connection(String host, String port, String name) {
        this.host = host;
        this.name = name;
        this.port = port;
        try {
            
            java.io.File file = new java.io.File("http.out");
            java.io.FileOutputStream outStream = new java.io.FileOutputStream(file);
            java.io.PrintStream err = new java.io.PrintStream(outStream);
            // System.setErr(err);
            
            serverSocket = new ServerSocket(Integer.parseInt(port),MAX_HTTP_QUEUE); }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("cannot get a socket to listen for http connections on: recovering (without HTTP connection)");
        }
        
        
    }
    
    
    /**
     * used to test the code here!
     */
    protected boolean test(String myPort, String testHost, String testPort) {
        try {
            TransportFactory tf = new IIOP_Z_HTTP_TransportFactory();
            InetAddress ip = InetAddress.getLocalHost();
            String localHost = ip.getHostAddress();
            String targetAddress = new String("http://"+testHost+":"+testPort+"/test");
            String sourceAddress = new String("http://"+localHost+":"+myPort+"/test");
            OutTray transport = tf.getTransport(targetAddress);
            FIPAPerformative fperf = new FIPAPerformative("inform");
            javax.agent.Envelope env = fperf.jasEnvelope(new FIPA_AID_Address(sourceAddress),targetAddress);
            transport.send(env);
            return true; }
        catch (Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * main method for testing this module<p>
     * <p> parameters are <port number for this> <host to test connection to> <port on test host>
     */
    public static void main(String argv[]) {
        String myPort = argv[0];
        String testHost = argv[1];
        String testPort = argv[2];
        FIPA_2000_HTTP_Connection test = new FIPA_2000_HTTP_Connection("tb-toledo.futures.bt.co.uk",myPort,"test");
        boolean done = false;
        while (!done) {
            try {
                Thread.sleep(1000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //  done = test.test(myPort,testHost,testPort);
        }
        
    }
    
    
    private FIPAPerformative process(String inMess,String bound) {
        debug("inMess == \n" + inMess + "\n end inMess");
        String message = stripEnvelope(inMess, bound);
        debug ("......\n " + message + " -------"); 
        FIPAPerformative fperf = ZeusParser.fipaPerformative(message);
        String content = fperf.getContent();
        try {
            fperf.setContent (addEscape(content));}
        catch (Exception e) { 
            System.out.println("EXCEPTION"); 
            e.printStackTrace(); 
        }
        debug("outMess == \n" + fperf.getContent() + "\n end inMess");
        return (fperf);
        
    }
    
    private String addEscape (String in) { 
        try { // content can be null 
     int counter = 0; 
     int pointer = 0; 
     StringBuffer buf = new StringBuffer (in);
     StringBuffer newOne = new StringBuffer(); 
     while (counter<in.length()-1) {
         char thisOne = buf.charAt(counter); 
         if (thisOne != '"') {
             newOne.append(thisOne); 
         }
         else {
             System.out.println("replacing"); 
             newOne.append('\\'); 
             newOne.append('\\'); 
             newOne.append('\\'); 
             newOne.append(thisOne); 
         }
         counter++; 
     }
     String ret = newOne.toString(); 
     return ret; }
        catch (Exception e) { 
            return (new String()); 
        } 
    }
    
    /**
     * crude for the moment - this needs redoing so it is less fragile.
     */
    private String stripEnvelope(String HTTPmessage,String bound) {
        //         debug ("boundary == " + bound);
        String remLastBoundary = HTTPmessage.substring(0, HTTPmessage.lastIndexOf(bound)-2);
        //  debug (remLastBoundary);
        //add
        String message = remLastBoundary.substring(HTTPmessage.indexOf(bound)+bound.length(),remLastBoundary.length());
        message =  remLastBoundary.substring(remLastBoundary.indexOf('('),remLastBoundary.length());
        //endadd
        //           String message = remLastBoundary.substring (HTTPmessage.indexOf(bound)+bound.length()+30, remLastBoundary.length());
        //      debug ("");
        //    debug ("");
        //    debug ("stripped content = "+message);
        return message.trim();
    }
    
    
    private void debug(String val) {
        System.out.println("\nFIPA_2000_HTTP_Connection>>"+val+"\n");
    }
}