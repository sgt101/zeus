
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
    AMS_Description holds a description of an AMS as per fipa
    */

public class AMS_Description { 
 
    private FIPA_AID_Address name = null; 
    private String ownership = null; 
    private String state = null; 
    
    
    public void setName (FIPA_AID_Address aid) { 
      this.name = aid;
    }


    public FIPA_AID_Address getName () { 
        return this.name; 
    }

    
    public void setOwnership (String ownership) {
     this.ownership = ownership;   
    }
    
    
    public String getOwnership () { 
        return ownership; 
    }
    
    public void setState (String state) { 
     this.state = state;    
    }
    
    
    public String getState () { 
        return state; 
    }
    
    
    public String toString () { 
        String retVal = new String ("(ams-agent-description ");  
        
        if (name!=null) { 
            retVal += ":name " + "(" + name.toFIPAString() + ") ";
        }
       if (ownership != null) {
            retVal += ":ownership " + ownership +" "; 
       }
       
       if (state != null ) { 
            retVal += ":state " + state + " "; 
       }
       retVal += ")\n"; 
       return retVal; 
       
    }
    
    
        
    public boolean match (AMS_Description desc) { 
        boolean match = true; 
        debug ("in match");
        if (name != null ) {
                if (!desc.getName().equals (this.getName())) {
                    debug ("name doesnt match " + desc.getName().toString() +" " + name.toString() ); 
                    return false;
                }
            } 
       if (ownership!= null ) { 
            if (!desc.getOwnership().equals (this.getOwnership())) {
                debug ("ownership doesn't match " + desc.getOwnership() +" " + ownership); 
                return false; 
            }
          }
        if (state != null ) { 
            if (!desc.getState().equals (this.getState()))  {
                debug ("state doesn't match " + desc.getState() + " " + state); 
                return false; 
            }
        }
        return (match); 
    }
    
    
 
    
    void debug (String str) { 
        //System.out.println(str); 
        
    }
    
}