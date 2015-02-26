
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


import zeus.actors.*; 
import zeus.agents.*;
import zeus.concepts.*;
import java.util.*; 

public class schedulerExternal implements ZeusExternal  { 
    
 
    AgentContext context = null; 
    String host = "132.146.209.68"; 
    String addressstr = "iiop://132.146.209.68:2000/acc";
 
 
    /** 
        this is meant to make the scheduler agent register it's services 
        with the FIPA df so that the dummy agent on another platform can 
        search it 
        */
    public void exec (AgentContext context) { 
        try {
            Thread.sleep (250); 
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
        this.context = null; 
        Performative perf = new Performative ("request"); 
        perf.setSender (context.whoami()); 
        perf.setReceiver ("df"); 
        DF_Description desc = new DF_Description (); 
        FIPA_AID_Address faddr = new FIPA_AID_Address (context.whoami(), host); 
        faddr.addAddress (addressstr); 
        desc.setName (faddr); 
        Vector services = new Vector(); 
        FIPA_Service_Description service = new FIPA_Service_Description(); 
        service.setName("profiling"); 
        service.setType ("meeting-scheduler-service"); 
        services.addElement(service); 
        FIPA_Service_Description service2 = new FIPA_Service_Description(); 
        service2.setName("profiling"); 
        service2.setType ("user-profiling-service"); 
        services.addElement(service2); 
        desc.setServices (services); 
        Vector onts = new Vector(); 
        onts.addElement(new String ("meeting-scheduler")); 
        onts.addElement(new String ("FIPA-Agent-Management")); 
        desc.setOntology(onts); 
        Vector langs = new Vector (); 
        langs.addElement(new String ("FIPA-SL0")); 
        langs.addElement (new String ("FIPA-SL1")); 
        langs.addElement(new String ("KIF")); 
        desc.setLanguage (langs); 
   
        FIPA_DF_Management_Content man = new FIPA_DF_Management_Content(); 
        man.setSubject (desc); 
        man.setAction("register"); 
        FIPA_AID_Address dfaddr = new FIPA_AID_Address ("df",host); 
        dfaddr.addAddress (addressstr); 
        man.setImplementor (dfaddr); 
        perf.setContent(man.toString()); 
        System.out.println(man.toString()); 
        perf.send(context);
        
        
    }
    
}