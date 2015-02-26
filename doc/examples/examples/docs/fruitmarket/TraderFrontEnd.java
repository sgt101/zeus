/*****************************************************************************
* TraderFrontEnd.java
* Jaron Collis (jaron@info.bt.co.uk), March 1999
* Implementation of the FruitMarket Trading GUI
*****************************************************************************/

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.fields.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.generator.util.*;


public class TraderFrontEnd extends JFrame 
       implements WindowListener, ConversationMonitor {
  // the root class of the GUI, contains the main display area

  protected JTabbedPane     tabbedPane;
  protected JPanel          sellPanel;
  protected JPanel          buyPanel;
  protected JPanel          stockPanel;
  public    JTextArea       infoArea;
  public    AgentContext    agent;
  public    JScrollPane scrollPane;


  public TraderFrontEnd(AgentContext ac)  {
    agent = ac;
    setTitle(agent.whoami() + " Trade Window");
    addWindowListener(this);

    // register GUI's interest in conversation events
    agent.Engine().addConversationMonitor(this,
       ConversationEvent.INITIATE_MASK | ConversationEvent.CONTINUE_MASK);

    getContentPane().setBackground(java.awt.Color.gray);
    String path = "gifs" + System.getProperty("file.separator");
    ImageIcon icon = new ImageIcon(path + "banana.gif");
    setIconImage(icon.getImage());

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    getContentPane().setLayout(gridBagLayout);

    JLabel header = new JLabel(new ImageIcon(path + "header.gif")) {
	    public void paint(Graphics g) {
	      Dimension size = getSize();
	      g.setColor(java.awt.Color.white);
	      g.fillRect(0, 0, size.width, size.height);
	      getIcon().paintIcon(this, g, 0, 0);
	    }
    };
    header.setBorder(new BevelBorder(BevelBorder.RAISED));

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(0,0,0,0);
    ((GridBagLayout)getContentPane().getLayout()).setConstraints(header, gbc);
    getContentPane().add(header);

    infoArea = new JTextArea(6,60);
    infoArea.setForeground(Color.black);
    infoArea.setBackground(Color.white);
    infoArea.setEditable(true);

    scrollPane = new JScrollPane(infoArea);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(300,100));

    infoArea.setText(agent.whoami() + " is awaiting your instructions...\n");
    infoArea.setCaretPosition(infoArea.getDocument().getLength());

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(8,8,0,8);
    ((GridBagLayout)getContentPane().getLayout()).setConstraints(scrollPane, gbc);
    getContentPane().add(scrollPane);

    tabbedPane = new JTabbedPane();
    sellPanel  = new TradePanel(false, this);
    buyPanel   = new TradePanel(true, this);
    stockPanel = new StockPanel(this);

    tabbedPane.addTab("Inventory", stockPanel);
    tabbedPane.addTab("Sell Fruit", sellPanel);
    tabbedPane.addTab("Buy Fruit", buyPanel);
    tabbedPane.setSelectedIndex(0);
    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,0,8);
    ((GridBagLayout)getContentPane().getLayout()).setConstraints(tabbedPane, gbc);
    getContentPane().add(tabbedPane);

    pack();
    setResizable(false);
    show();
  }

  public void windowClosed(WindowEvent event) {}
  public void windowDeiconified(WindowEvent event) {}
  public void windowIconified(WindowEvent event) {}
  public void windowActivated(WindowEvent event) {}
  public void windowDeactivated(WindowEvent event) {}
  public void windowOpened(WindowEvent event) {}
  public void windowClosing(WindowEvent event) {
     this.setVisible(false);
     this.dispose();
     System.exit(0);
  }


  public void display(String message)
  {
    infoArea.append(message + "\n");
    infoArea.setCaretPosition(infoArea.getDocument().getLength());
  }

  public void conversationInitiatedEvent(ConversationEvent event) {
    String correspondent = event.getReceiver();
    String mode = " to ";
    if (correspondent.equals(agent.whoami()))
    {
      correspondent = event.getSender();
      mode = " from ";
    }
    Goal g = (Goal)(event.getData().elementAt(0));
    double cost = g.getCost();
    String f = g.getFactType();
    display("Conversation started...");
		display("[" + event.getMessageType() + "]" + mode + correspondent +
		  " >> " + f + " @ " + Misc.decimalPlaces(cost,3));
	}

  public void conversationContinuedEvent(ConversationEvent event) {
    String correspondent = event.getReceiver();
    String mode = " to ";
    if (correspondent.equals(agent.whoami())) {
      correspondent = event.getSender();
      mode = " from ";
    }
    if ( event.isGoalEventType() ) {
       Goal g = (Goal)(event.getData().elementAt(0));
       double cost = g.getCost();
       String f = g.getFactType();
       display("[" + event.getMessageType() + "]" + mode + correspondent +
			   " >> " + f + " @ " + Misc.decimalPlaces(cost,3));
    }
    else {
       Fact f = (Fact)(event.getData().elementAt(0));
       display("[" + event.getDataType() + "]" + mode + correspondent);
     }
  }
}



class TradePanel extends JPanel implements ActionListener, FactSelector
{
  // the panel through which buying and selling preferences are entered

  protected FactDialog       factWin;
  protected NameField        fruitField;
  protected WholeNumberField askPriceField;
  protected WholeNumberField timeField;
  protected JButton          chooseBtn;
  protected JButton          tradeBtn;
  protected TraderFrontEnd   UI;

  protected boolean      buying;


  public TradePanel(boolean b, TraderFrontEnd frontend)
  {
    buying = b;
    UI = frontend;

    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gb);
    setBackground(Color.lightGray);
    setPreferredSize(new Dimension(360,220));
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    askPriceField = new WholeNumberField(0, 9999);
    askPriceField.setBackground(Color.white);
    askPriceField.setPreferredSize(new Dimension(60,20));
    askPriceField.setValue(10); // or get value from DB

    timeField = new WholeNumberField(0, 9999);
    timeField.setBackground(Color.white);
    timeField.setPreferredSize(new Dimension(60,20));
    timeField.setValue(3);

    fruitField = new NameField(7);
    fruitField.setBackground(Color.white);

    chooseBtn = new JButton("Choose");
    chooseBtn.addActionListener(this);

    JLabel label0 = new JLabel("Commodity to Trade: ");
    label0.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label0.setToolTipText("The type of fruit that will be traded");

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(label0,gbc);
    add(label0);

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gb.setConstraints(fruitField, gbc);
    add(fruitField);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(4,8,4,8);
    gb.setConstraints(chooseBtn, gbc);
    add(chooseBtn);

    String q = "Reserve price: ";
    if (buying) q = "Maximum offer price: ";
    JLabel label1 = new JLabel(q);
    label1.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label1.setToolTipText("The initial offer price");

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(label1,gbc);
    add(label1);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gb.setConstraints(askPriceField, gbc);
    add(askPriceField);

    JLabel label3 = new JLabel("Deadline (time-grains): ");
    label3.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label3.setToolTipText("The deadline by which trading should have concluded");

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,0);
    gb.setConstraints(label3, gbc);
    add(label3);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gbc.weightx = 1;
    gb.setConstraints(timeField, gbc);
    add(timeField);

    tradeBtn = new JButton("Trade");
    tradeBtn.addActionListener(this);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(16,8,8,8);
    gbc.weightx = 1;
    gb.setConstraints(tradeBtn, gbc);
    add(tradeBtn);

    factWin = new FactDialog((Frame)SwingUtilities.getRoot(this),
		                         UI.agent.OntologyDb());
  }

  public void factSelected(String[] names)
  {
    // get selected fact type and display name
    String str = names[0];
    if (str.equals("ZeusFact") || str.equals("Money")) return;
    fruitField.setText(str);
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (UI.agent == null ) return;
    if (evt.getSource() == chooseBtn)
    {
      factWin.setLocationRelativeTo(chooseBtn);
      factWin.display(this);
    }
    else if (evt.getSource() == tradeBtn)
    {
      // trade button pressed: do some error checking first
      String fruit = fruitField.getText();
      if (!UI.agent.OntologyDb().hasFact(fruit))
      {
        UI.display("** Error: " + fruit + " does not exist in the ontology.");
        return;
      }

      Fact[] tmp = UI.agent.ResourceDb().all(fruit);
      if (!buying && tmp[0].getNumber() == 0)
      {
        UI.display("Ooops, we're out of " + tmp[0].getType() + "s !");
        return;
      }

      // transaction is valid: commence trading
      Long askl  = askPriceField.getValue();
      Long timel = timeField.getValue();
      int ask  = (int)askl.longValue();
      int time = (int)timel.longValue();

      if (buying)
        beginBuy(fruit, ask, time);
      else
        beginSell(fruit, ask, time);
    }
  }


  public void beginBuy(String fruit, int ask, int time)
  {
    UI.display("\nAttempting to buy: " + fruit);
		UI.display("My preferences: price= " + ask + ", within " + time + " time-grains");

    Fact fact = UI.agent.OntologyDb().getFact(Fact.VARIABLE, fruit);
    fact.setNumber(1);
    int now = (int)UI.agent.now();
    Goal g = new Goal(UI.agent.newId("goal"), fact, now+time, ask,
                      UI.agent.whoami(), (double)(now+time-0.5));
	  UI.agent.Engine().buy(g);
  }


  public void beginSell(String fruit, int ask, int time)
  {
    UI.display("\nAttempting to sell: " + fruit);
    UI.display("My preferences: price= " + ask + ", within " + time + " time-grains");

    Fact fact = UI.agent.OntologyDb().getFact(Fact.VARIABLE, fruit);
    fact.setNumber(1);
    int now = (int)UI.agent.now();
    Goal g = new Goal(UI.agent.newId("goal"), fact, now+time, ask,
                      UI.agent.whoami(), (double)(0));
    UI.agent.Engine().sell(g);
	}
}







