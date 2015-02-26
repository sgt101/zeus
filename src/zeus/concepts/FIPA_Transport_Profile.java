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

package zeus.concepts; 
import java.util.Vector;
import java.util.Enumeration;

/** 
    FIPA_Transport_Profile is used to store the results of parsing 
    transport profiles, and to generate a string (with toString()) which
    can be sent to FIPA agent platforms. 
    *@author Simon Thompson
    *@since 1.2
    */
public class FIPA_Transport_Profile { 
  
    private Vector mtps = new Vector();   
  
    /** 
        puts a MTP description into the transport profile
        */
    public void addMTPDescription (FIPA_MTP_Description mtp) { 
     mtps.addElement(mtp);    
    }
    
    
    /** 
        returns a formatted version of this data structure
        */
    public String toString() {
     String retVal = new String(); 
     retVal += ("(ap-transport-description :available-mtps (set "); 
     Enumeration allMTPs = mtps.elements();
     while (allMTPs.hasMoreElements()) { 
      FIPA_MTP_Description mtp = (FIPA_MTP_Description) allMTPs.nextElement(); 
      retVal += mtp.toString() +" "; 
     }
     retVal += "))"; 
     debug (retVal); 
     return retVal;
    }

        
    void debug (String str) { 
      // System.out.println("FIPA_Transport_Profile : " + str);  
      }
} 