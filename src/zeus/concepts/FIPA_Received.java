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

/** 
    FIPA_Received is used to store the stamps that FIPA says you should be 
    able to attach to the various messages that they allow
    */
public class FIPA_Received { 
    
    
    private String receivedBy = null ;
    private String receivedDate = null; 
    private String receivedId = null; 
    
    
    public void setReceivedBy (String rec) { 
        this.receivedBy = rec; 
    }
    
    
    public void setReceivedDate (String date) { 
        this.receivedDate = date; 
    }
    
    
    public void setReceivedId (String id) { 
        this.receivedId = id; 
    }

    
    public String toXML () { 
        String retVal = new String("<received>\n"); 
        retVal += "<received-by value = \"" + receivedBy +"\" />\n";
        retVal += "<received-date value = \"" + receivedDate + "\" />\n";
        /*if (receivedId != null) { 
            retVal += "<received-id value = \"" + receivedId +"\" />"; }*/
        retVal += "</received>"; 
        return retVal;
    }


    
    
    
}