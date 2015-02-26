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


package zeus.util;
import java.util.*; 

/** 
    StringHashtable is a very primitive class that allows you to have a hashtable
    that is keyed on string values rather than string object identities. 
    */
public class StringHashtable extends Hashtable { 
    
    
    public StringHashtable () { 
        super(); }
        
   
   
    public int noElements() { 
        Enumeration keys = this.keys(); 
        int counter = 0;
        while (keys.hasMoreElements()) { 
            counter++;
            keys.nextElement(); }
            return (counter); 
    }
    
    
    public Object getForString(String key) { 
        Enumeration allKeys = this.keys();
        String currentKey = null; 
        while (allKeys.hasMoreElements()) { 
            currentKey = (String) allKeys.nextElement(); 
            if (currentKey.equals(key)) { 
                return (this.get(currentKey));
            }
        }
        return(null); }
        
        
        
    public void removeForString(String key) {
        Enumeration allKeys = this.keys();
        String currentKey = null; 
        while (allKeys.hasMoreElements()) { 
            currentKey = (String) allKeys.nextElement(); 
            if (currentKey.equals(key)) { 
                remove(currentKey); 
                return;
            }
        }
        return; }
    
        
}