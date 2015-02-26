/**
 * mazeControl.java
 * Implements the Controller role of the maze Environment agent
 * Notice that Zeus code is imported, as this is an agent-level component
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.agents.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.gui.*;


public class mazeControl extends JFrame implements ZeusExternal, FactMonitor
{
  boolean frameSizeAdjusted = false;
  JButton startButton = new JButton();
  AgentContext context = null;
  mazeView mazeGui = null;
  mazeModel mazeInUse = null;


  public mazeControl()
  {
    setTitle("Maze Display");
    getContentPane().setLayout(new BorderLayout(0,0));
    setSize(400,450);
    // mainPanel.setLayout(null);
    // getContentPane().add(mainPanel);

    startButton.setText("Start");
    startButton.setActionCommand("Start");
    startButton.setEnabled(false);
    getContentPane().add("South", startButton);

    // REGISTER LISTENERS
    SymWindow aSymWindow = new SymWindow();
    this.addWindowListener(aSymWindow);
    SymAction lSymAction = new SymAction();
    startButton.addActionListener(lSymAction);
  }


  public void exec(AgentContext context)
  {
    this.setVisible(true);
    try
    {
      mazeInUse = new mazeModel(10,10);
      mazeGui = new mazeView(mazeInUse);
      // bad code follows... don't like it, but it is bad karma to add (self) in self.construct()
      mazeGui.addMouseListener (mazeGui);
      mazeGui.addMouseMotionListener(mazeGui);
      // because the result is dependend on the JVM implementation.
      this.context = context;
      ZeusAgent zAgent = (ZeusAgent) context.Agent();
      zAgent.addFactMonitor(this,1);
      ResourceDb resdb = this.context.ResourceDb();
      mazeGui.setBackground(Color.white);
      mazeGui.setForeground(Color.white);
      getContentPane().add("Center", mazeGui);
      validate();
      show();
      repaint();
    }
    catch (Exception e) { e.printStackTrace(); }
  }


  public void startAction ()
  {
    System.out.println("Called startAction");
    try
    {
      System.out.println(context.toString());
      OntologyDb ont = context.OntologyDb();
      // if the button is pushed then add a fact to the database that
      // triggers a rule initiating movement from the navigator agent

      Fact fact = ont.getFact(Fact.FACT,"moveMade");
      fact.setValue("moved","true");
      int now = (int) context.now();
      Integer thisInt = new Integer (now);
      ResourceDb ResDb = context.ResourceDb();
      ResDb.add(fact);
    }
    catch (Exception e ) { e.printStackTrace(); }
    System.out.println("Set initial move");
  }

  // factAccessed is left unimplemented
  public void factAccessedEvent(FactEvent fe) {;}

  // but we implement factAdded event to get some behavior in terms
  // of referencing the model (which is external to zeus)
  // and telling the zeus agent about it
  public void factAddedEvent (FactEvent fe)
  {
    Fact currentFact = fe.getFact();
    String agentName = context.whoami();
    String factType = currentFact.getType();
    System.out.println("Fact = " + factType);
    if (factType.equals("thisMove"))
    {
      // the navigator agent connected to this proxy asks if it can move in
      // the environment. This agent (environment) must decide what to do.

      // first step is to determine the contents of the move
      String north = currentFact.getValue ("north");
      String south = currentFact.getValue ("south");
      String east = currentFact.getValue ("east");
      String west = currentFact.getValue ("west");
      String id = currentFact.getValue ("id");
      boolean nBool = north.equals("true");
      boolean sBool = south.equals("true");
      boolean eBool = east.equals("true");
      boolean wBool = west.equals("true");
      try
      {
        Thread.sleep(500); // pause for a second, prevent everything happening too quickly
      }
      catch (Exception e) {;}

      // now invoke a function that tests whether it is a legal move, and enacts it if it is
      // as a side effect the obstacles around the agent are asserted in the
      // fact database, as is a fact indicating if the move made was legal or not
      // this will trigger more behaviour in the agent.
      move (id, nBool, eBool, sBool, wBool);
      // a message should now have been sent to the originating agent
      // telling it what new obstacles lie in it's path!
    }
    else if (factType.equals ("agentsName"))
    {
      startButton.setEnabled(true);
      String id = currentFact.getValue("name");
      mazeInUse.registerAgent(id, Math.Random(10),9); // set initial agent position

      mazeGui.repaint();
      OntologyDb ont = context.OntologyDb();
      Fact obst = ont.getFact(Fact.FACT, "obstacle");
      obst.setValue("north",mazeInUse.northVal(id));
      obst.setValue("east",mazeInUse.eastVal(id));
      obst.setValue("south",mazeInUse.southVal(id));
      obst.setValue("west",mazeInUse.westVal(id));

      // add it to the fact db
      ResourceDb rdb = context.ResourceDb();
      rdb.add(obst);
      System.out.println("Added an obstacle");
    }
    else System.out.println("fact added and detected");
  }

  public void factDeletedEvent (FactEvent fe) {;}
  public void factModifiedEvent (FactEvent fe) {;}

 /** move (String id ,bool,bool,bool,bool) moves the agent(id) in the direction(s)
     indicated by the boolean values (north, east, south, west)
     As a side effect the old obstacles are retracted from the resourceDb
     and new obstacles are looked up and asserted.
     The notional position of the agent is changed.
     A fact (moveMade) indicating if the move was legal or illegal
     (the agent is not allowed to go through obstacles) is asserted
     These changes should fire one of the rules in the environment
     agents rulebase and trigger another move from the Navigator agent **/
  public void move (String id, boolean north, boolean east, boolean south, boolean west)
  {
    // first acquire the resources that are required from the agent context
    ResourceDb rdb = context.ResourceDb();
    OntologyDb ont = context.OntologyDb();

    if (mazeInUse.isLegalMove(id,north,east,south,west))
    {
      // legal move - so move the agent
      mazeInUse.moveAgent(id,north,east,south,west);
      // set up a new obstacle and add to ResourceDb
      Fact obst = ont.getFact(Fact.FACT, "obstacle");
      obst.setValue("north",mazeInUse.northVal(id));
      obst.setValue("east",mazeInUse.eastVal(id));
      obst.setValue("south",mazeInUse.southVal(id));
      obst.setValue("west",mazeInUse.westVal(id));
      rdb.add(obst);
      mazeGui.repaint();
      if (mazeInUse.exited(id))
      {
        Fact exMaz = ont.getFact (Fact.FACT, "mazeExited");
        exMaz.setValue("id", id);
        rdb.add (exMaz);
        System.out.println(id +" has exited the maze");
        startButton.setEnabled(false);
      }
    }
    else
    {
      // illegal move - return the old obstacle and do not move the agent
      // although in this implementation the navigator never tries
      // an illegal move so this code in unused (and untested!!)
      Fact obst = ont.getFact(Fact.FACT, "obstacle");
      obst.setValue("north",mazeInUse.northVal(id));
      obst.setValue("east",mazeInUse.eastVal(id));
      obst.setValue("south",mazeInUse.southVal(id));
      obst.setValue("west",mazeInUse.westVal(id));
      rdb.add(obst);
    }

    // the move was legal so set up a moveMade fact
    Fact fact = ont.getFact(Fact.FACT,"moveMade");
    fact.setValue("moved","true");
    // add it to the agents factdatabase to trigger some more behavior
    rdb.add(fact);
  }

  public mazeControl(String sTitle)
  {
    this();
    setTitle(sTitle);
  }

  public void addNotify()
  {
    // Record the size of the window prior to calling parents addNotify.
    Dimension size = getSize();
    super.addNotify();

    if (frameSizeAdjusted)
      return;
    frameSizeAdjusted = true;

    // Adjust size of frame according to the insets and menu bar
    JMenuBar menuBar = getRootPane().getJMenuBar();
    int menuBarHeight = 0;
    if (menuBar != null)
        menuBarHeight = menuBar.getPreferredSize().height;
    Insets insets = getInsets();
    setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight);
  }



  void exitApplication()
  {
    try
    {
      int reply = JOptionPane.showConfirmDialog(this, "Do you want to terminate this Agent?",
                                                "Kill Environment Agent?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE);
      if (reply == JOptionPane.YES_OPTION)
      {
        this.setVisible(false);    // hide the Frame
        this.dispose();            // free the system resources
        System.exit(0);            // close the application
      }
    } catch (Exception e) {}
  }

  class SymWindow extends java.awt.event.WindowAdapter
  {
    public void windowClosing(java.awt.event.WindowEvent event)
    {
      Object object = event.getSource();
      if (object == mazeControl.this)
        mazeControl_windowClosing_a(event);
    }
  }

  void mazeControl_windowClosing_a(java.awt.event.WindowEvent event) {
    mazeControl_windowClosing(event);
  }

  void mazeControl_windowClosing(java.awt.event.WindowEvent event) {
    try {
      this.exitApplication();
    }
    catch (Exception e) {}
  }

  class SymAction implements java.awt.event.ActionListener
  {
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
      Object object = event.getSource();
      if (object == startButton)
        startButton_actionPerformed_a(event);
    }
  }

  void startButton_actionPerformed_a(java.awt.event.ActionEvent event) {
    startButton_actionPerformed(event);
  }

  void startButton_actionPerformed(java.awt.event.ActionEvent event)
  {
    try {
      this.startAction();
    } catch (Exception e) {}
  }
}
