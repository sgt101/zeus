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
import zeus.concepts.*;
import zeus.actors.*;

/** 
    implementation of the OutTray interface that can be used to message on a 
    Zeus (TCP/IP) communications channel 
    */
public class Zeus_Native_Transport implements OutTray { 
        
        
    private ZeusAgentContext context = null;    
        
    public Zeus_Native_Transport (ZeusAgentContext context) { 
         this.context = context;
    }
    
        
        
   public void send  (Object obj) throws UnsuitableMessageException { 
    try { 
        Performative  perf = (Performative) obj; 
        send (perf); 
        } catch (ClassCastException cce) { 
            throw new UnsuitableMessageException ("Must be Performative to work with this transport"); 
        }catch (Exception e) { 
            e.printStackTrace(); 
            throw new UnsuitableMessageException ("Bad message in send() - unknown problem, Excepiton printed to sout"); 
    
    }
    }
        
        
        
   public void send (Performative perf) { 
    context.getMailBox().sendMsg(perf); 
   }
   
        
   public void send (javax.agent.Envelope env) { 
    
    try {
        // very risky - is the object a performative, probably not.....
        Performative perf = (Performative) env.getObject(); 
        context.getMailBox().sendMsg(perf);
    }
       catch (ClassCastException cce) { 
        cce.printStackTrace(); 
        return; }
        
   }
   
   
    }