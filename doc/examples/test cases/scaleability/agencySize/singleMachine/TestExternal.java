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


import zeus.agents.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.util.*;
import java.util.Random;
import java.util.StringTokenizer;
/**
    TargetExternal is the external for a test system that 
    test how many agents can be deployed in Zeus. When one of these agents 
    is deployed it will sit and wait for a while and then will start 
    to message other agents with names like its own. It will send a 
    message every 0.1 seconds. 
    When it gets a message from another agent it will send a confirm response
    to simulate conversational activity. 
    @author Simon Thompson 
    @version 1.0
    */
public class TestExternal implements ZeusExternal,Runnable{
 
Random random = new Random(); 
Performative newPerf = null;
int numberReceived = 0; 
int numberSent = 0; 
boolean killed = false; 
AgentContext context = null; 
String typeName = new String(); 
int NUMBER_AGENTS = 30; 
 
    /**
        method called when this agent is started
        */
    public void exec(AgentContext context) { 
        this.context = context;
        MsgHandler msg = context.MsgHandler(); 
        
       // String messageReceivedPattern[] = 
              //  {"type", "inform", "content",  "\\A(\\s*)(.*)\\Z"}; 
   //     msg.addRule (new MessageRule(context.newId("Rule"), 
           //                         messageReceivedPattern, this, "respond")); 
        Thread messageMaker = new Thread(this); 
        String myName = context.whoami();
        StringTokenizer tokens = new StringTokenizer (myName,"_"); 
        typeName = (String) tokens.nextElement(); 
        messageMaker.start();                                   
    }

    
    public void sendMessage() { 
        newPerf = new Performative("inform"); 
        int number_of_agent = random.nextInt(NUMBER_AGENTS-1); 
        newPerf.setReceiver (typeName+"_"+String.valueOf(number_of_agent));   
        OntologyDb ont = context.OntologyDb(); 
        String content = context.whoami(); 
        Fact fact = ont.getFact(Fact.FACT,"Test"); 
        fact.setValue("name", content); 
        GenSym genId = context.GenSym();
        fact.setId (genId.newId(context.whoami()));
        newPerf.setContent (fact.toString()); 
        context.MailBox().sendMsg(newPerf); 
        numberSent++;
        }


    public void respond (Performative perf) { 
        numberReceived ++; 
        newPerf = new Performative("confirm"); 
        AddressBook agentBook = context.AddressBook(); 
        newPerf.setReceiver (perf.getSender());   
        OntologyDb ont = context.OntologyDb(); 
        String content = context.whoami(); 
        Fact fact = ont.getFact(Fact.FACT,"Test"); 
        fact.setValue("name", content); 
        GenSym genId = context.GenSym();
        fact.setId (genId.newId(context.whoami()));
        newPerf.setContent (fact.toString()); 
        context.MailBox().sendMsg(newPerf); 
        numberSent++; 
    }
    
    
    /**
        this is the thread that updates the GUI
        */
    public void run (){
        int count = 0; 
        while (!killed) { 
        try {
                Thread.sleep(100);  }
                catch (Exception e) { 
                    e.printStackTrace(); }
            count++;  
            sendMessage(); 
           
      
            
        }
        
    }
            
    
    public void kill () {
        killed = true;
        System.exit(0); 
        }
        
        
    public void finalise () { 
        kill(); 
    } 

    
}