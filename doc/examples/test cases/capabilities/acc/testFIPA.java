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

import zeus.actors.fipa.*;
import java.net.*;
import javax.naming.*;
import java.util.*;
import javax.rmi.*;
import java.io.*;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.CosNaming.*;
import FIPA_Agent_97;
import zeus.concepts.*;

/**
    testFIPA is meant to test the response of the 
    Zeus acc agent to some fipa messages sent via iiop
*/
public class testFIPA {
    // this is a default accId: needs to be updated to be the 
    // machine, port and directory in which your acc agent is running
/*    private String accId = new String ("acc@tb-toledo.futures.bt.co.uk:900/acc"); 
    private String accAddress = new String ("iiop://tb-toledo.futures.bt.co.uk:900/acc");
    private String senderId = new String ("sender@tb-toledo.futures.bt.co.uk:900/sender"); */
    
    public String fipaMachName = new String ("tb-toledo.futures.bt.co.uk:1050");
    public String zeusMachName = new String ("tb-toledo.futures.bt.co.uk:900"); 
    
    
    // following string lifted from the FIPA'98 Part 1:Version 1.0 spec (page 18)
    // sub
    //public String request_forward = new String ("(letter :envelope ( :destination ( (:name " + accId +" (:address (" + accAddress +" :sender-details ( (:name " +senderId +" ))) :message (request : sender " +senderId +" :receiver  "+ accId +" :ontology fipa-agent-management :language SL0 :protocol fipa-request :content (action "+accId +" (forward (:letter :envelope ():message ()))))))))"); 
    public String request_forward = new String ("(request :sender (agent-identifier\n" +
                                                        ":name iotestagent@"+ zeusMachName +"\n" +
                                                        ":addresses (sequence iiop://"+fipaMachName +"/acc))\n"+
                                                        ":receiver (set\n" +
                                                        "(agent-identifier\n" +
                                                        ":name testForward@"+fipaMachName+"\n"+
                                                        ":addresses (sequence iiop://"+ zeusMachName+"/acc)))\n" +
                                                        ":content (retrieve_fact :type information)\n" +
                                                        ":reply-with ping-test \n" +
                                                        ":language zeus\n" +
                                                        ":ontology ping  )");    
    private FIPA_Agent_97 target = null; 
    private javax.naming.Context initialNamingContext  = null; 
    private ORB orb = null;
    private FIPA_Server server = null;
    
    private FIPA_Agent_97 createLink () { 
        try {
            InetAddress ip = InetAddress.getLocalHost();
            Hashtable env = new Hashtable();
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, 
                            "com.sun.jndi.cosnaming.CNCtxFactory"); 
	        initialNamingContext = new InitialContext(env);
	        String args [] = new String[0];	        
	        orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef =
                 orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent("acc", "");
            NameComponent path[] = {nc};
            java.lang.Object obj = ncRef.resolve(path);	
            Class fipaClass = Class.forName("FIPA_Agent_97"); 
            FIPA_Agent_97 target = (FIPA_Agent_97)PortableRemoteObject.narrow(obj, 
                                                fipaClass);    
            return (target); 
        }
        catch (Exception e) { 
            e.printStackTrace (); 
            System.out.println("failure to initialise in createLink"); 
            return (null);
            }
    }
        
        private void sendMessages (int numberToSend, FIPA_Agent_97 target) {     
            Performative perf = new Performative ("request"); 
            perf.setSender("tester"); 
            System.out.println(request_forward); 
            for (int count = 0; count<numberToSend; count++) { 
                    System.out.println("sending message"); 
                    target.message(request_forward);
                    }
    
        
       }

    
        

    
    public void test(int numberMessages) {
        FIPA_Agent_97 target = createLink(); 
        sendMessages (numberMessages, target); 
        orb.shutdown(false);
        try {
        initialNamingContext.close(); }
        catch (Exception e) { 
            e.printStackTrace();}

        
    }
                
                
    public static void main (String argv[]) { 
        testFIPA tf = new testFIPA(); 
        FIPA_Server fipaServer = new FIPA_Server("iiop://" +tf.fipaMachName, "acc"); 
        int numberMessages = Integer.parseInt(argv[0]);
        tf.test(numberMessages); 
     
        
    }
                    
                    
}
            
            

