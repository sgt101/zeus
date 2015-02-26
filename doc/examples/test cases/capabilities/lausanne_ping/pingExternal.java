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

        GridLayout layout = new GridLayout(13,1);
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
  	Object source = ae.getSource();
        if (source == sendLausButton) {
        	System.out.println("Trying Lausanne");
        	sendMessage("LausannePing");
		}
        if (source == sendMontButton) {
                System.out.println("Trying Montpellier");
                sendMessage ("MontpellierPing");
                }
        if (source == sendParisButton) {
                System.out.println("Trying Paris");
                sendMessage ("ParisPing");
                }
        if (source == sendSanButton) {
                System.out.println("Trying Sanfrancisco");
                sendMessage ("SanfranciscoPing");
                }
        if (source == sendParmaButton) {
                System.out.println("Trying Parma");
                sendMessage ("ParmaPing");
                }
        if (source == sendBarcaButton) {
                System.out.println("Trying Barcalona");
                sendMessage ("BarcalonaPing");
                }
        if (source == sendQMULButton) {
                System.out.println("Trying QMUL (London 1)");
                sendMessage ("QMULPing");
                }
        if (source == sendLisboaButton) {
                System.out.println("Trying Lisboa");
                sendMessage ("LisboaPing");
                }
        if (source == sendAgentScpButton) {
                System.out.println("Trying AgentScape (Berlin)");
                sendMessage ("BerlinPing");
                }
        if (source == sendSendiButton) {
                System.out.println("Trying Sendi");
                sendMessage ("SendiPing");
                }
        if (source == sendDublinButton){
                System.out.println("Trying Dublin");
                sendMessage ("DublinPing");
                }
         if (source == sendICSTMButton){
                System.out.println("Trying ICSTM");
                sendMessage ("ICSTMPing");
                }
         if (source == sendSaarButton){
                System.out.println("Trying Saarbrucken");
                sendMessage ("SaarbruckenPing");
                }

    }


    public void exec(AgentContext context) {
        this.context = context;
        setVisible (true);
        setResponse ();
        Performative perf = new Performative ("request"); 
        perf.setReceiver ("df"); 
        perf.setContent("register(df-agent-description :name (agent-identifier :name pingagent@adastralcity.agentcities.net) :services (set (service-description :name ping :type ping_acl_alpha_v1.0 ))  :protocol FIPA-Request  :ontology Agentcities   :language Ping   :ownership \\\"Simon\\\")   ");
        perf.send(context); 
	//setRedo();
    }


    public void sendMessage(String receiver) {
        Performative ping = new Performative ("query-ref") ;
        ping.setReceiver(receiver);
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
	   sendMessage(msg.getSender());
	}


    public void respond(Performative msg) {
        //System.out.println("msg = " +msg.toString());
      //  System.out.println("respond called");
        System.out.println("type = " + msg.getClass());
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