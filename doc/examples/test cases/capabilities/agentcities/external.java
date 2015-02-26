import zeus.actors.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.agents.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class pingExternal  extends JFrame implements ZeusExternal,ActionListener {
    JButton sendLausButton = new JButton("Ping Lausanne");
    JButton sendMontButton = new JButton ("Ping Montpellier");
    JButton sendSaarButton = new JButton ("Ping Saarbrucken");
    JButton sendSanButton = new JButton ("Ping SanFrancisco");
    JButton sendParisButton = new JButton("Ping Paris");
    JButton sendParmaButton = new JButton("Ping Parma");
    JButton sendBarcaButton = new JButton("Ping Barcelona");
    JButton sendQMULButton = new JButton("Ping QMLU");
    JButton sendLisboaButton = new JButton("Ping Lisboa");   
    JButton sendICSTMButton = new JButton("Ping ICSTM");    
    JButton sendAgentScpButton = new JButton("Ping Agentscape");
    JButton sendSendiButton = new JButton("Ping Sendi");
    JButton sendDublinButton = new JButton("Ping Dublin");
    
    
    
    AgentContext context = null;

    public pingExternal () {
        setSize (200,200);

        GridLayout layout = new GridLayout(1,13);
        getContentPane().setLayout(layout);
        
	getContentPane().add (sendLausButton);
	getContentPane().add (sendParisButton);
        getContentPane().add (sendMontButton);
        getContentPane().add (sendSaarButton);
	getContentPane().add (sendSanButton);
	getContentPane().add (sendParmaButton);
	getContentPane().add (sendBarcaButton);
	getContentPane().add (sendQMULButton);
	getContentPane().add (sendLisboaButton);
	getContentPane().add (sendICSTMButton);
        getContentPane().add (sendAgentScpButton);
        getContentPane().add (sendSendiButton);
        getContentPane().add (sendDublinButton);

	           
	sendLausButton.addActionListener(this);
        sendParisButton.addActionListener(this);
       sendMontButton.addActionListener(this);
       sendSaarButton.addActionListener(this);
       sendSanButton.addActionListener(this);
       sendParmaButton.addActionListener(this);
       sendBarcaButton.addActionListener(this);
       sendQMULButton.addActionListener(this);
       sendLisboaButton.addActionListener(this);
       sendICSTMButton.addActionListener(this);
       sendAgentScpButton.addActionListener(this);
       sendSendiButton.addActionListener(this);
       sendDublinButton.addActionListener(this);


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
	//setRedo();
    }


    public void sendMessage() {
        Performative ping = new Performative ("query-ref") ;
        ping.setReceiver("ping_agent");
        ping.setContent ("ping");
        ping.setLanguage ("PlainText");
        context.getMailBox().sendMsg(ping);
    }


   public void setResponse () {
        SimpleAPI api = new SimpleAPI(context);
        api.setHandler ("query-ref",this,"respond");
   }


public void setRedo () {
	SimpleAPI api = new SimpleAPI (context);
	api.setHandler ("inform", this, "redo");
}

	public void redo (Performative msg) {
	try {
           String content = msg.getContent();
           content = content.toLowerCase ();
           if (content == "alive")
	   Thread.sleep(6000) ; }
		catch (Exception e) {
			e.printStackTrace();
		}
	   sendMessage();
	}


    public void respond(Performative msg) {
        //System.out.println("msg = " +msg.toString());
      //  System.out.println("respond called");
        Performative resp = new Performative ("inform");
       // System.out.println("2");
        resp.setContent ("alive");
       // System.out.println("3");
        resp.setSender (msg.getReceiver());
       // System.out.println("4");
        resp.setReceiver(msg.getSender());
       // System.out.println("5");
        resp.send(context);
        }
}