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
import java.awt.event.*;
import javax.swing.*;
/**
    MessageSpeedTesterSource is the external for a test system that 
    test how fast a ZeusAgent on a particular machine can send messages to 
    another agent using a the specified messaging mechanism (set in the AgentBuilder in 
    Zeus 2.0 and beyond, but in 1.XX always sockets
    This tests the actual capacity of agents to receive and handle a message: the 
    agent that receives has to respond to the receieved message with a reply. 
    The reception of a reply on this side triggers a new messaging event - and so on. 
    The agents count the number of messages that they have received and sent and display them on
    a little GUI.
    They also calculate how many per second they are getting - assuming that your JVM 
    treats Thread.sleep(1000) as a second!!!!
    @author Simon Thompson 
    @version 1.0
    */
public class MessageSpeedTesterSource implements ZeusExternal,Runnable, MessageSpeedExternal, ActionListener{
boolean notResponded = false;
Performative newPerf = null;
int numberReceived = 0; 
int numberSent = 0; 
int currentnr = 0;
int currentns = 0;
boolean killed = false; 
AgentContext context = null; 
SpeedFrontEnd frontend = null; 

 
    /**
        method called when this agent is started
        */
    public void exec(AgentContext context) { 
        this.context = context;
       
        frontend = new SpeedFrontEnd(context.whoami()); 
        MsgHandler msg = context.MsgHandler(); 
        String messageReceivedPattern[] = 
                {"type", "inform", "content",  "\\A(\\s*)(.*)\\Z"}; 
        msg.addRule (new MessageRuleImpl(context.newId("Rule"), 
                                    messageReceivedPattern, this, "respond")); 
        try { 
            Thread.sleep (1000); }
            catch (Exception e) { 
                e.printStackTrace(); 
            }
        //this.sendFirstMessage();   
        JButton but = frontend.getButton();
        but.addActionListener (this);
        Thread messageMaker = new Thread(this);
	messageMaker.start();
    }

    
    public void sendFirstMessage(String target) { 
        
        newPerf = new Performative("inform"); 
        newPerf.setReceiver (target);   
        OntologyDb ont = context.OntologyDb(); 
        String content = context.whoami(); 
        Fact fact = ont.getFact(Fact.FACT,"Test"); 
        fact.setValue("name", content); 
        GenSym genId = context.GenSym();
        fact.setId (genId.newId(context.whoami()));
        newPerf.setContent (fact.toString()); 
        context.MailBox().sendMsg(newPerf); 
        System.out.println("Sent first message to " + target); 
    }


 


    public void respond (Performative perf) { 
        numberReceived++;
        currentnr++;
        newPerf = new Performative("inform"); 
        OntologyDb ont = context.OntologyDb(); 
        String content = context.whoami(); 
        Fact fact = ont.getFact(Fact.FACT,"Test"); 
        fact.setValue("name", content); 
        GenSym genId = context.GenSym();
        fact.setId (genId.newId(context.whoami()));
        newPerf.setContent (fact.toString()); 
        newPerf.setReceiver (perf.getSender());
        context.MailBox().sendMsg(newPerf); 
        numberSent++; 
        currentns++;
    }
    
    
    /**
        this is the thread that updates the GUI
        */
    public void run (){
        int count = 0; 
        while (!killed) { 
     //    System.out.println("count = " +String.valueOf(count));
         try {
                Thread.sleep(1000); }
                catch (Exception e) { 
                    e.printStackTrace(); }
                    
            count++;
            frontend.setNumberReceived (numberReceived); 
            frontend.setNumberSent (numberSent);
            frontend.setNumberReceivedPerSecond (currentnr/count); 
            frontend.setNumberSentPerSecond (currentns/count);
            if (count>10) {
                currentns = 0;
                currentnr = 0;
                count = 0;
                }


            
        }
        
    }
            

    public void actionPerformed (ActionEvent ae) {
        String target = frontend.getName();
        this.sendFirstMessage(target);
    }
    

    public void kill () {
        killed = true;
        System.exit(0); 
        }
        
        
    public void finalise () { 
        kill(); 
    } 

    
}