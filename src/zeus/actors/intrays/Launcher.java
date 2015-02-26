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
    /**
    *
    * @author  Roberto Avalos
    * @version 
    */
import java.io.*;
import java.net.*; 
import java.util.*; 
import org.apache.soap.*; // Body, Envelope, Fault, Header 
import org.apache.soap.rpc.*; // Call, Parameter, Response 
public class Launcher {

    /** Creates new Launcher */
    public Launcher() {
    }

    /*
     *  This Class contains only one method which makes a remote invokation using SOAP Protocol to launch the agent. This is done
     *  to create the agent in the same Virtual Machine as the SOAP Server is running on.
     */
    public static void main (String args[]) throws Exception
    {

     URL url = new URL( "http://localhost:8080/soap/servlet/rpcrouter" ); 
     String urn = "urn:agent:soap"; 
     Call call = new Call(); // prepare the service invocation 
     call.setTargetObjectURI( urn );
     call.setMethodName( "launchAgent" ); 
     call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC ); 
     Vector params = new Vector(); 
     params.addElement (new Parameter("numAgent", String.class, "", null));
     call.setParams( params ); 
     try 
       { 
       System.out.println( ". invoke service\n" + "  URL= " + url + "\n  URN =" + urn ); 
       Response response = call.invoke( url, "" ); // invoke the service 
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
       System.err.println( "SOAPException= " + e.getFaultCode() + ", " +  
         e.getMessage() ); 
       } 
    }

}
