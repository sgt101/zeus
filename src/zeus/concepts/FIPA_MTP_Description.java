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
    FIPA_MTP_Description is used to store the result of parsing a mtp-description 
    fragment. The toString method can be used to generate a FIPA format string for 
    sending message transport profiles to agents on other platforms. 
    *@author Simon Thompson
    *@since 1.2
    */
public class FIPA_MTP_Description {
 
    private String profile; 
    private String mtpName; 
    private Vector addresses = null; 
 
    public void setProfile (String prof) { 
        this.profile = prof;
    }
    
    
    public void setMTPName (String mtp) { 
        this.mtpName = mtp; 
    }
    
    
    public void addAddress (String address) { 
        if (addresses == null) addresses = new Vector(); 
        this.addresses.addElement(address);
    }
        
    
    public String getProfile () { 
        return this.profile; 
    }
    
    
    public String getMTPName () { 
        return this.mtpName; 
    }
    
    
    public String getAddresses () { 
        if (addresses == null) return null; 
        Enumeration allAddresses = addresses.elements(); 
        String retVal = new String(); 
        while (allAddresses.hasMoreElements()) {
            String thisAddress = (String) allAddresses.nextElement(); 
            retVal +=thisAddress + " ";
        }
        return retVal; 
    }
    
    
    /** 
        return a string that is formatted to FIPA spec. 
        **/ 
    public String toString() { 
        String retVal = new String("(mtp-description"); 
        if (profile != null) 
            retVal += " :profile " + getProfile(); 
        if (mtpName != null) 
            retVal += " :mtp-name " + getMTPName(); 
        if (addresses!= null) 
            retVal += " :addresses (sequence " + getAddresses() +") " ; 
        retVal += ") ";         
        return (retVal); 
    }
}