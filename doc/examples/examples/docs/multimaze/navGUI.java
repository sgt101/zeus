/**
 * NavGUI.java
 * A basic front-end display for the Navigator agent
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



public class navGUI extends JFrame implements ZeusExternal, FactMonitor,
                                              ActionListener, WindowListener
{
  AgentContext context = null;
  JLabel  status      = new JLabel("Agent not yet registered with a maze environment");
  JButton startButton = new JButton();


  public navGUI()
  {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    getContentPane().setLayout(new GridLayout(2,1));
    getContentPane().add(status);
    startButton.setText("Register");
    startButton.setActionCommand("Start");
    startButton.setEnabled(true);
    getContentPane().add(startButton);
    validate();
    pack();
    show();

    // REGISTER LISTENERS
    addWindowListener(this);
    startButton.addActionListener(this);
  }

  public void exec(AgentContext context)
  {
    this.context = context;
    ((ZeusAgent)context.Agent()).addFactMonitor(this,1);
    this.setTitle(context.whoami() + " Display");
    this.setVisible(true);
  }


  public void factAccessedEvent(FactEvent fe) {;}
  public void factDeletedEvent (FactEvent fe) {;}
  public void factModifiedEvent(FactEvent fe) {;}

  public void factAddedEvent (FactEvent fe)
  {
    Fact currentFact = fe.getFact();
    String factType = currentFact.getType();
    System.out.println("Fact = " + factType);
    if (factType.equals("agentRegistered"))
    {
      status.setText("Registered with Maze");
      status.repaint();
      startButton.setEnabled(false);
    }
    if (factType.equals("thisMove"))
    {
      String dir = "nowhere";
      if ((currentFact.getValue("north")).equals("true")) dir = "North";
      if ((currentFact.getValue("south")).equals("true")) dir = "South";
      if ((currentFact.getValue("east")).equals("true")) dir = "East";
      if ((currentFact.getValue("west")).equals("true")) dir = "West";
      status.setText("Moving: " + dir);
      status.repaint();
    }
    if (factType.equals("mazeExited"))
    {
      status.setText("I have reached the Maze exit.");
      startButton.setEnabled(true);
      ResourceDb resdb = context.ResourceDb();
      try {
      resdb.del(resdb.all("inMaze"));} catch (Exception e){

        e.printStackTrace();}
      try {

      resdb.del(currentFact);} catch (Exception e) {
            e.printStackTrace();}
      
      status.repaint();
    }
  }


  public void actionPerformed(java.awt.event.ActionEvent event)
  {
    Object object = event.getSource();
    if (object == startButton)
      startAction();
  }

  public void startAction ()
  {
    System.out.println("Called startAction");
    try
    {
      OntologyDb ont = context.OntologyDb();
      // if the button is pushed then add a fact to the database triggering
      // the registration rule

      Fact fact = ont.getFact(Fact.FACT, "agentsName");
      fact.setValue("name",context.whoami());

      ResourceDb ResDb = context.ResourceDb();
      ResDb.add(fact);
    }
    catch (Exception e ) {e.printStackTrace();}
    status.setText("Registering with Maze");
    status.repaint();
  }

  public void windowClosed(WindowEvent event) {}
  public void windowDeiconified(WindowEvent event) {}
  public void windowIconified(WindowEvent event) {}
  public void windowActivated(WindowEvent event) {}
  public void windowDeactivated(WindowEvent event) {}
  public void windowOpened(WindowEvent event) {}
  public void windowClosing(WindowEvent event)
  {
    this.setVisible(false);
    this.dispose();
    System.exit(0);
  }
}
