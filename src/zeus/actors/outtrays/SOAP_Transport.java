/**
*             STANDARD DOCUMENTATION - EXPERIMENTAL SOFTWARE
*                       (QGN 13, Issue 4 refers)
*                          Copyright BTexact Technologies 2001
*  @author      : Roberto Avalos   
*  OUC code     : DVA5P
*  File name    : SOAP_Transport.java
*  @version     : 1.0  
*  Date         : 9/April/2002
*  Purpose      : This class is part of Outtrays and prepares the means of communication by setting up the soap 
*                  transport to send a messsage to other agent using the SOAP Server.
*  Testing      :   
*  Software req.: Java Virtual Machine
*  Hardware req.: 
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

package zeus.actors.outtrays;
import java.net.*; 
import java.util.*; 
import org.apache.soap.*; // Body, Envelope, Fault, Header 
import org.apache.soap.rpc.*; // Call, Parameter, Response 
import zeus.actors.*;
import zeus.actors.intrays.*; 
import javax.agent.service.*;
import javax.agent.*; 
import zeus.util.*;
import java.io.*;
import FIPA.*; 

/*
 *  This class is part of Outtrays and prepares the means of communication by setting up the soap transport to send a messsage to
 *  other agent using the SOAP Server.
 *  In order to communicate via SOAP to other remote agent there is some compulsory information to make the connection
 *  URN: Uniform Resource Name (the name given when the web service was deployed in the SOAP Server)
 *  URL: Uniform Resource Locator -> The URL, Port and Directory to the SOAP Server Listener
 *  Method Name: Method name to be invoked (The name of the class is not needed)
 *  Parameters (Names, Types and Data) -> Method's parameters names, data types are needed as well as the data values
 */
public class SOAP_Transport implements OutTray{

    private String URLAddress=null;
    Call target = new Call();
    
    /*
     *  This method sets up the nessary parameters to request a remote invocation through the soap server     
     */
    public SOAP_Transport (Call target2,String soapurn, String urlstring) {
        //System.out.println("SOAP_Transport has been called is being processed");
        URLAddress=urlstring;
        this.target = target2;
        this.target.setTargetObjectURI( soapurn ); 
        this.target.setMethodName( "message" ); 
        this.target.setEncodingStyleURI( Constants.NS_URI_SOAP_ENC );
        //System.out.println("SOAP_Transport has been successfully called");
        
    }
    
    public void send (javax.agent.Envelope envelope) {
        //**env To fperf
        //System.out.println("SOAP_Transport send has been invoked");
        zeus.concepts.FIPAPerformative fperf = (zeus.concepts.FIPAPerformative) envelope.getObject();
        String sendString = new String(); 
        sendString = fperf.toFIPAString();   
        //fperf to string
        try{
        send(sendString);}
        catch (Exception e) {
            System.out.println("SOAP Transport Error when sending the string");
            e.printStackTrace();
            }
    }
    
    /*
     *  This method receives the message and using the connection that was set up in SOAP_Transport method
     *  sends the request to the SOAP Server. Before the invocation is made, the parameters are put in a vector
     *  structure which includes parameter name, data type and value.
     */
    public void send (String message)  throws Exception { 
         System.out.println(message);
         URL url = new URL(URLAddress);
         Vector params = new Vector(); 
         params.addElement (new Parameter("acl_message", String.class, message , null));
         this.target.setParams( params );
         
         try 
           { 
               //System.out.println("invoke service\n" + "  URL= " + url + "\n  URN =" + urn ); 
               Response response = this.target.invoke(url, "" ); // invoke the service 
               //System.out.println("Invocation made");
               if( !response.generatedFault() ) 
                 { 
                 Parameter result = response.getReturnValue(); // response was OK 
                 System.out.println( "Result= " + result.getValue() ); 
                 } 
               else 
                 { 
                 Fault f = response.getFault(); // an error occurred 
                 System.err.println( "Fault= " + f.getFaultCode() + ", " + f.getFaultString() ); 
                 } 
           } 
         catch( SOAPException e ) // call could not be sent properly 
           { 
                System.err.println( "SOAPException= " + e.getFaultCode() + ", " +  e.getMessage() );
           }

    }
    
    public void send(Object obj) throws UnsuitableMessageException  
    {   
        Class cla = obj.getClass(); 
        System.out.println("class = " + cla.getName());
        try {
            //System.out.println("The send method will be invoked as an envolope");
             javax.agent.Envelope env = (javax.agent.Envelope) obj;
             send (env);
            }
            
        catch (Exception e) {
            e.printStackTrace(); 
            throw new UnsuitableMessageException ("Bad message"); 
        }
             
    }
        
}
