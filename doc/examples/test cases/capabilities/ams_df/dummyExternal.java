
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
import java.util.*;
import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import zeus.concepts.*;
/** 
this class is the external of the dummy agent in the FIPA example for 
AgentManagement (Annex A p 22 XC000023G @ www.fipa.org). Basically it provides a GUI with 
some buttons, which you click to send the messages in the example
The first 5 buttons implement the example, but any buttons after that have been added in order to 
test other FIPA AMS and DF features (like platform description and AMS search) that 
are not covered in the example. 
*/
public class dummyExternal extends JFrame implements ZeusExternal, ActionListener {
    
    private AgentContext context = null; 
    private JButton sendReqRegAMS = new JButton("Send Request Register AMS"); 
    private JButton sendReqRegDF = new JButton ("Send Request Register DF"); 
    private JButton sendSearchDF = new JButton ("Send Search Scheduler"); 
    private JButton sendModify = new JButton ("Send Modify Scheduler"); 
    private JButton sendGetDescription = new JButton ("Send Get Description AMS"); 
    private JButton sendSearchAMS = new JButton ("Send Search AMS"); 
  
    
    /** this button has the note (fails) because in the example 
    given by FIPA a propose is sent, and the DF should respond 
    not understood*/
    private JButton sendDeregisterPropose  = new JButton ("Send Deregister (fails)"); 
    
    
    private String regAMSStr = null; 
    private String regDFStr = null; 
    private String searchDFStr = null; 
    private String modifyStr = null; 
    private String deregisterStr = null;  
    private String searchAMSStr = null;
    private String getAPDescAMS = null; 
    
    public void exec(AgentContext context) { 
        this.context = context;  
        setSize (250,600); 

        GridLayout layout = new GridLayout(7,1); 
        getContentPane().setLayout(layout); 
        getContentPane().add (sendReqRegAMS); 
        getContentPane().add (sendReqRegDF); 
        getContentPane().add (sendSearchDF); 
        getContentPane().add (sendModify); 
        getContentPane().add (sendDeregisterPropose); 
        getContentPane().add (sendGetDescription);
        getContentPane().add (sendSearchAMS); 
        sendReqRegAMS.addActionListener(this); 
        sendReqRegDF.addActionListener(this); 
        sendSearchDF.addActionListener(this); 
        sendModify.addActionListener(this); 
        sendDeregisterPropose.addActionListener(this); 
        sendGetDescription.addActionListener(this); 
        sendSearchAMS.addActionListener(this);
        
        regAMSStr = readFromFile ("regAMS.txt"); 
        regDFStr = readFromFile ("regDF.txt"); 
        searchDFStr = readFromFile ("searchDF.txt"); 
        modifyStr = readFromFile ("modifyDF.txt"); 
        deregisterStr = readFromFile ("deregister.txt"); 
        searchAMSStr = readFromFile ("searchAMS.txt"); 
        getAPDescAMS = readFromFile ("getDescription.txt"); 
        
        this.setVisible(true); 
        repaint();
   
    }
    
 
 
 public void actionPerformed (ActionEvent ae) { 
   
    String param = ae.getActionCommand().trim(); 
    Object source = ae.getSource(); 
    try {    
    if (source==sendReqRegAMS) {
        msendReqRegAMS(); }
    else if (source==sendReqRegDF) { 
        msendReqRegDF(); }
    else if (source==sendSearchDF) {
        msendSearchDF(); }
    else if (source==sendModify) { 
        msendModify(); }
    else if (source==sendDeregisterPropose) { 
        msendDeregister(); }
    else if (source ==sendGetDescription) { 
        msendGetDescription(); }
    else if (source == sendSearchAMS ) {
        msendSearchAMS(); }
    else { 
        System.out.println("Command not recognised"); 
    }}
    catch (Exception e) {
        e.printStackTrace(); 
    }

 }

    public void msendReqRegAMS() { 
      Performative perf = new Performative ("request"); 
      perf.setSender(context.whoami()); 
      perf.setReceiver("remote_ams");
      perf.setLanguage("FIPA-SL0"); 
      perf.setProtocol("FIPA-Request"); 
      perf.setOntology("FIPA-Agent-Management"); 
      perf.setContent(regAMSStr);   
      send (perf); 
      }
    
    
    public void msendReqRegDF() { 
      Performative perf = new Performative ("request"); 
      perf.setSender(context.whoami()); 
      perf.setReceiver("remote_df");
      perf.setLanguage("FIPA-SL0"); 
      perf.setProtocol("FIPA-Request"); 
      perf.setOntology("FIPA-Agent-Management"); 
      perf.setContent(regDFStr);
      send (perf); 
    }
    
    
    public void msendSearchDF() { 
      Performative perf = new Performative ("request"); 
      perf.setSender(context.whoami()); 
      perf.setReceiver("remote_df");
      perf.setLanguage("FIPA-SL0"); 
      perf.setProtocol("FIPA-Request"); 
      perf.setOntology("FIPA-Agent-Management"); 
      perf.setContent(searchDFStr);
      send (perf); 
    }
    
    
    public void msendSearchAMS () { 
      Performative perf = new Performative ("request"); 
      perf.setSender(context.whoami()); 
      perf.setReceiver("remote_ams");
      perf.setLanguage("FIPA-SL0"); 
      perf.setProtocol("FIPA-Request"); 
      perf.setOntology("FIPA-Agent-Management"); 
      perf.setContent(searchAMSStr);
      perf.send(context); 
    }
    
    
    public void msendModify() { 
      Performative perf = new Performative ("request"); 
      perf.setSender(context.whoami()); 
      perf.setReceiver("remote_df");
      perf.setLanguage("FIPA-SL0"); 
      perf.setProtocol("FIPA-Request"); 
      perf.setOntology("FIPA-Agent-Management"); 
      perf.setContent(modifyStr);
      send (perf); 
    }
    
    
    public void msendDeregister() { 
      Performative perf = new Performative ("propose"); 
      perf.setSender(context.whoami()); 
      perf.setReceiver("remote_df");
      perf.setLanguage("FIPA-SL0"); 
      perf.setProtocol("FIPA-Request"); 
      perf.setOntology("FIPA-Agent-Management"); 
      perf.setContent(deregisterStr);
      send (perf);  
    }
    
    public void msendGetDescription () { 
      Performative perf = new Performative ("request"); 
      perf.setSender (context.whoami()); 
      perf.setReceiver ("remote_ams"); 
      perf.setLanguage ("FIPA-SLO"); 
      perf.setProtocol ("FIPA-Request"); 
      perf.setOntology ("FIPA-Agent-Management"); 
      perf.setContent (getAPDescAMS); 
      perf.send(context); 
    }
    
    
    private String readFromFile (String name) { 
        try {
            RandomAccessFile raf = new RandomAccessFile (name,"r"); 
            String retVal = new String();//"\""); 
            String temp = new String (); 
            while (temp !=null) { 
                try { 
                    temp = raf.readLine(); 
                    if (temp != null) {
                        retVal += temp;
                        retVal += "\n";
                    }}
                    catch (Exception e) { 
                        temp = null; 
                    }
            }
            System.out.println(name + " = " + retVal); 
            return retVal;//+"\""; 
        } catch (Exception e) {
            e.printStackTrace(); 
        }         
        return (null); 
    }
     
     
     
     public void send (Performative perf) {
        MailBox mbox = context.getMailBox(); 
        mbox.sendMsg(perf); 
     }
        
        
        
 }
    

    
    
