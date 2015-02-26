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


public class FIPA_AP_Description {
    
    
        private String name = null;
        private String dynamic = null;
        private String mobility = null; 
        private FIPA_Transport_Profile transportProfile = null; 
        
        
        public void setName (String name) { 
            this.name = name; 
        }
        
        
        public String getName () { 
            return this.name; 
        }
        
        
        /** 
            it is possible that this should be only boolean, but I am not sure...
            */
        public void setDynamic (String dynamic) { 
            this.dynamic = dynamic;
        }
        
        
        public void setDynamic (boolean dyn) { 
            if (dyn) 
                this.dynamic = new String ("true"); 
            else 
                this.dynamic = new String ("false"); 
        }
        
        
        public String getDynamic () { 
            return this.dynamic; 
        }

        
        /** 
            it is possible that this should be only boolean, but I am not sure...
            */
        public void setMobility (String mobility) { 
            this.mobility = mobility; 
        }

        
        public void setMobility (boolean mob) { 
            if (mob) 
                this.mobility = new String ("true"); 
                else
                this.mobility = new String ("false"); 
        }
        
        
        public String getMobility () { 
            return this.mobility;
        }
          
    
        public void setTransportProfile (FIPA_Transport_Profile transportProfile) { 
            this.transportProfile = transportProfile; 
        }
        
        
        /** 
            returns a formatted String suitable for transmission to other 
            FIPA agents. 
            */
        public String toString () { 
             String retVal = new String ("(ap-description "); 
             if (name != null) 
		 retVal += " :name \\\"" + this.name +"\\\""; //HAP NAME - CHECK PARSER                
             if (dynamic != null) 
                retVal += " :dynamic " + this.dynamic; 
             if (mobility != null) 
                retVal += " :mobility " + this.mobility; 
             if (transportProfile != null) 
                retVal += " :transport-profile " + transportProfile.toString(); 
             retVal += ") "; 
             return retVal;
        }  
}
