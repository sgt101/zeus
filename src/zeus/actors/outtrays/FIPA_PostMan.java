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

// import rmi classes
import javax.naming.*;
import java.util.*;
import zeus.util.*;
//import java.rmi.RemoteException;
//import java.rmi.RMISecurityManager;
//import java.rmi.server.UnicastRemoteObject;
import javax.rmi.*;
import zeus.concepts.*;
import zeus.actors.*;
import java.io.*;
import fipa97.FIPA_Agent_97;
import fipa97.FIPA_Agent_97Helper;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.CosNaming.*;
import zeus.actors.factories.*;
import javax.agent.service.*;
import javax.agent.*;

/**
    This class is a FIPA complient PostMan that can be used to build an
    ACC agent which when working with the NameServer provides a link between
    the Zeus platform and other FIPA complient platforms
    */
public class FIPA_PostMan extends zeus.actors.PostMan {

  protected  javax.naming.Context  initialNamingContext = null;
  private FileWriter log;
  private File file = null; 
  public FIPA_PostMan(MailBox mbox, Queue outMail, Queue ccMail, Address myAddress) {
   //super (mbox,outMail,ccMail,myAddress);

    this.mbox      = mbox;
    this.outMail   = outMail;
    this.ccMail    = ccMail;
    this.myAddress = myAddress;

    if ( ccMail == null )
       this.setPriority(Thread.NORM_PRIORITY);

    try {
         initialNamingContext = new InitialContext();}
         catch (Exception e) {
            e.printStackTrace(); }
    this.setName ("FIPA_PostMan");
    this.start();

  }


  public void run() {
     FIPAPerformative msg;
     String       receiver;
     boolean done = false;
     dispatching = true;
     
     try {
      file = new File  (SystemProps.getProperty("http_root")+ SystemProps.getProperty("out_log")); 
      log = new FileWriter (file,true);    
         
     }
     catch (Exception e) {
         e.printStackTrace();
     }
     while( dispatching ) {
        try {
            msg = (FIPAPerformative) outMail.dequeue();
           /*        
            Enumeration test = msg.getFIPAReceivers();
            while (test.hasMoreElements()){
             FIPA_AID_Address addr = (FIPA_AID_Address) test.nextElement();
            }*/
            done = false;
            Enumeration recs = msg.getFIPAReceivers();
            while (recs.hasMoreElements () && !done) {
            try {
                FIPA_AID_Address addr = (FIPA_AID_Address) recs.nextElement();
                System.out.println(msg.toString());
                done = postFIPAMsg (msg, addr);
                file.setLastModified(java.lang.System.currentTimeMillis());
                }
                catch (Exception e) {
                        System.out.println("exception in address, probably proprietary/ unhandled transport");
                        System.out.println("zeus is trying next address");
              		 e.printStackTrace(); }
               	catch (Error er) {
	                System.out.println("error in address, probably proprietary/ unhandled transport");
                        System.out.println("zeus is trying next address");
                	er.printStackTrace();
                }
                System.out.println("recs = " + recs.toString());
                if (done) System.out.println("done = true");
                else System.out.println("done = false");
                }
              System.out.println("iterating");
            } catch (Exception e) {
               e.printStackTrace(); }
               catch (Error er) {
                er.printStackTrace();
                }
       // System.out.println("\n Message Transport error \n Zeus is recovering");
//     	 yield();

     }
  }


  /**
    posts the message to the appropriate transport, will set the return address to
    */
   public boolean postFIPAMsg( FIPAPerformative msg, FIPA_AID_Address addr ) {
   try {
    boolean isOk = false;
    int nTry = 0;
    //FIPA_AID_Address sender = new FIPA_AID_Address("agent-identifier\n:name " + myAddress.getName() + "@" + myAddress.getHost() + "\n:addresses (sequence iiop://" + myAddress.getHost() + "/ACC)");
  //  msg.setSender(sender);
  //  msg.setAddress(myAddress);
    Iterator addressIter = addr.iterator();
    String thisAddress = new String();
    while( addressIter.hasNext()) {

            thisAddress = (String) addressIter.next();
	    debug ("trying : " + thisAddress);
            TransportFactoryMethod tfm = new TransportFactoryMethod();
            TransportFactory tFactory = tfm.getTransportFactory();
            tFactory.setLog(file); 
            try {
                OutTray trans = tFactory.getTransport(thisAddress);
                javax.agent.Envelope env = msg.jasEnvelope(addr,thisAddress);
                trans.send (env);
                return (true);
                }
                catch (TransportUnsupportedException tue) {
                    tue.printStackTrace();
                   }
                catch (UnsuitableMessageException ume) {
                    ume.printStackTrace();
                }
                catch (Exception e) {
                        e.printStackTrace();
                        // continue
                        }
                catch (Error er) {
                        // continue
                        }
    }
    return false;    }
    catch (Exception e){
        e.printStackTrace();
        return false;
        }
  }

  private void debug(String str) {
   //     System.out.println("FIPA_PostMan>> " + str);
        }





}
