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
package zeus.actors.outtrays;

import javax.agent.service.*; 
import javax.agent.*; 
import FIPA.*; 
import fipa97.FIPA_Agent_97;
import zeus.concepts.*;
import zeus.actors.*;

/** 
    implementation of the OutTray interface that wraps a FIPA_97_IIOP transport 
    ie. a FIPA_Agent_97 interface 
    */
public class FIPA_97_IIOP_Transport implements OutTray { 
        
        
    private FIPA_Agent_97 target = null; 
    
    
    public FIPA_97_IIOP_Transport (FIPA_Agent_97 target) { 
        this.target = target; 
    }
    
    
    public void send  (Object obj) throws UnsuitableMessageException { 
    try { 
        javax.agent.Envelope env = (javax.agent.Envelope) obj; 
        send (env); 
        } catch (ClassCastException cce) { 
            throw new UnsuitableMessageException ("Must be javax.agent.envelope to work with this transport"); 
        }catch (Exception e) { 
            e.printStackTrace(); 
            throw new UnsuitableMessageException ("Bad message in send() - unknown problem, Excepiton printed to sout"); 
     
    }
    }
    
    /** 
        send takes the Envelope, pops out the "object" and then tries 
        to cast it to a zeus.concepts.FIPAPerformative. If it can't do that 
        it will fail, and print a stack trace. If it can cast it, it will call 
        the FIPA_Agent_97.message(FIPAPerformative.toString()) and send it 
        down the wire
        */
    public void send (javax.agent.Envelope envelope) { 
        // must convert the javax.agent.Envelope to a FIPAPerformative, toString it and 
        // send it 
        try {
            FIPAPerformative fperf = (FIPAPerformative) envelope.getObject (); 
            target.message(fperf.toFIPAString()); 
        }
        catch (ClassCastException cce) { 
                    // I know that this is a bit grim.... 
                    // let's not bring everything down for one mistake...
                    cce.printStackTrace(); 
                    return; 
                } 
            
        
    }
   

}
