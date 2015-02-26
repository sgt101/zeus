
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
import java.util.*; 

public class FIPA_Search_Constraints { 
    
    
    private int max_depth = 0; 
    private int max_results = 0; 

    
    
    public void setMaxDepth (int max_depth) { 
        this.max_depth = max_depth; 
    }
    
      
    public void setMaxResults (int max_results) { 
        this.max_results = max_results; 
    }
    
   
    public int getMaxDepth () { 
        return this.max_depth; 
    }
    
       
    public int getMaxResults () { 
        return this.max_results; 
    }
    
    
  
    public String toString () { 
        String retVal = new String("(search-constraints "); 
        if (max_depth >0) { 
            retVal += ":max-depth " + String.valueOf(max_depth) +" ";
        }
        if (max_results >0) { 
            retVal += ":max-results " +  String.valueOf(max_results) + " "; 
        }
        retVal += ")"; 
        return retVal; 
      }

    
}