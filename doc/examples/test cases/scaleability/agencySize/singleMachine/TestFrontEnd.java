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
import javax.swing.*;
import java.awt.*;
/**
    TestFrontEnd is a simple gui that is used to display some statistics on agent 
    messaging 
    */
public class TestFrontEnd extends JFrame {
   
   private JTextField numberReceived = new JTextField (); 
   private JTextField numberSent = new JTextField(); 
   private JTextField sentPerSec = new JTextField (); 
   private JTextField receivedPerSec = new JTextField ();   
   
   
   public TestFrontEnd ()  { 
        super (); 
        this.setTitle ("Message Speed Test"); 
        Container pane = this.getContentPane(); 
        this.setSize(200,150); 
        pane.setLayout(new GridLayout (4,2));   
        pane.add (new JLabel ("Number Sent")); 
        pane.add (numberSent); 
        pane.add (new JLabel ("Number Received")); 
        pane.add (numberReceived); 
        pane.add (new JLabel ("Received /Sec")); 
        pane.add (sentPerSec); 
        pane.add (new JLabel ("Sent /Sec"));
        pane.add (receivedPerSec); 
        
        setVisible (true); 
    }
       
       
    public void setNumberReceived (int received) { 
        numberReceived.setText(String.valueOf(received)); 
    }
    
       
    public void setNumberSent (int sent) { 
        numberSent.setText (String.valueOf (sent)); 
    }
    
    
    public void setNumberReceivedPerSecond (int recPS) { 
        receivedPerSec.setText (String.valueOf(recPS)); 
    } 
    
    
    public void setNumberSentPerSecond (int sentPS) { 
        sentPerSec.setText (String.valueOf(sentPS)); 
    }
       
       
    public static void main (String argv[]) { 
        TestFrontEnd test = new TestFrontEnd(); 

    }
    }
   
