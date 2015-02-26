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


import zeus.actors.*;
import zeus.util.*; 
import zeus.concepts.*; 
import zeus.agents.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class pingExternal  extends JFrame implements ZeusExternal,ActionListener, Runnable {
    JButton sendButton = new JButton("Send"); 
    AgentContext context = null; 
    Performative newPerf = null;
int numberReceived = 0; 
int numberSent = 0; 
boolean killed = false; 

SpeedFrontEnd frontend = null; 
    
    public pingExternal () { 
        setSize (200,200); 
        frontend = new SpeedFrontEnd();
        GridLayout layout = new GridLayout(1,1); 
        getContentPane().setLayout(layout); 
        getContentPane().add (sendButton); 
        sendButton.addActionListener(this); 
        repaint();
     
    }
        
        
    public void actionPerformed (ActionEvent ae) { 
        System.out.println("action"); 
        sendMessage(); 
    }
        
    
    public void exec(AgentContext context) { 
        this.context = context; 
        setVisible (true); 
        setResponse (); 
         try { 
            Thread.sleep (1000); }
            catch (Exception e) { 
                e.printStackTrace(); 
            }
        Thread messageMaker = new Thread(this); 
        messageMaker.start(); 
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
            frontend.setNumberReceivedPerSecond (numberReceived/count); 
            frontend.setNumberSentPerSecond (numberSent/count); 

            
        }
    }
    
    public void sendMessage() { 
        Performative ping = new Performative ("query-ref") ;
        ping.setReceiver("ping_agent"); 
        ping.setContent ("ping");
        context.getMailBox().sendMsg(ping); 
    }
    
 
   public void setResponse () { 
        SimpleAPI api = new SimpleAPI(context); 
        api.setHandler ("query-ref",this,"respondQueryRef"); 
        api.setHandler ("inform",this,"respondInform"); 
   }
    
     /** 
        performance will be degraded by the need to make a new performative 
        */
    public void respondQueryRef(Performative msg) {
        numberReceived ++; 
        Performative resp = new Performative ("inform");
        resp.setContent ("alive"); 
        resp.setSender (msg.getReceiver());
        resp.setReceiver(msg.getSender()); 
        resp.send(context);
        numberSent++; 
        }
    
    /** 
        performance will be degraded by the need to make a new performative 
        */
    public void respondInform(Performative msg) {
        numberReceived ++; 
        Performative resp = new Performative ("query-ref");
        resp.setContent ("ping"); 
        resp.setSender (msg.getReceiver());
        resp.setReceiver(msg.getSender()); 
        resp.send(context);
        numberSent++; 
        }
}