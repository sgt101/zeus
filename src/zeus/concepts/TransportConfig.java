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
    this class is used to load the configuration info for the various 
    FIPA transports that could be set for Zeus. You may want to alter and 
    extend it so that you can use more sophisticated config info, but you will have 
    to have a look at the method Transports in FIPAParser.jj as well. 
    *@author Simon Thompson
    *@since 1.2
    */
public class TransportConfig {
    
    private String name = null; 
    private String port = null; 
    
    public void setName (String name) { 
        this.name = name ; 
    }
    
    
    public void setPort (String port) { 
        this.port = port; 
    }
    
    
    public String getPort () { 
        return this.port; 
    }
    
    
    public String getName () { 
        return this.name; 
    }
    
    
       
}