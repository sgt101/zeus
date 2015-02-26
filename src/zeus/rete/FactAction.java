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

package zeus.rete; 
import zeus.concepts.*;

/**
    FactAction is a conveinience class so that a fact can be placed on the reteEngine 
    queue for later processing using the Zeus thread safe pattern 
    @see reteEngine
    @author Simon Thompson
    @since 1.1
    */
public class FactAction {
    
    
    /**
        defined these fields as int not boolean so that the class can be extended later
        */
    public static final int ADD = 0; 
    public static final int DELETE = 0; 
    
    
    private Fact fact = null;
    private int action = 0; 
    
    public void setFact (Fact fact){ 
        this.fact = fact; 
    }
    
    
    public Fact getFact () { 
        return this.fact; 
    }
    
    
    public void setAction (int action) { 
        this.action = action; }
        
        
   public boolean isAdd () { 
    if (action == this.ADD)
        return true; 
        else
        return false; 
   }
   
   
   public boolean isDelete () { 
    if (action == this.DELETE)
        return true;
        else
        return false; 
   }
   
}
        
          
        
    
    