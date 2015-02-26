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



package zeus.visualiser.control;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.util.*;
import zeus.concepts.*;
// import zeus.concepts.rtn.IEngine;
import zeus.gui.*;
// import zeus.gui.visual.*;
import zeus.gui.dialog.*;
import zeus.gui.help.*;
import zeus.actors.*;
import zeus.visualiser.VisualiserModel;
import zeus.visualiser.basic.BasicTool;



public class ControlTool extends BasicTool implements ActionListener /*,
                                                      IBrowseGoal, IDoCommandUI, IEngine */
{
  private static int count = 0;

  protected ControlMenuBar menubar;

  protected SingleSelectionDialog sa_goal = null;
  //protected DoCommandUI cmdUI     = null;

  protected ControlToolBar toolbar;
  protected File           file;
  //protected GoalUI       goalUI = null;
  //protected BrowseGoalUI   browseGoalUI = null;
  protected SummaryTable   goalTable = new SummaryTable();
  protected String         myName;
  protected AgentContext   context;

  public ControlTool(AgentContext context, VisualiserModel model)
	{
    super(context, model);
    myName = context.whoami();
    this.setTitle(myName + " -  Control Tool:" + (count++));
    ImageIcon icon = new ImageIcon(SystemProps.getProperty("gif.dir") +
		                               File.separator + "visualiser" +
																	 File.separator + "control-icon.gif");
    setIconImage(icon.getImage());
    this.setBackground(Color.lightGray);
    getContentPane().setLayout(new BorderLayout());

    menubar = new ControlMenuBar(this);
    setJMenuBar(menubar);

    // Create and Add Toolbars
    toolbar = new ControlToolBar(this);
    ImageIcon im = new ImageIcon(SystemProps.getProperty("gif.dir") + File.separator + "visualiser" + File.separator + "bt.gif");
    JLabel logo = new JLabel(im);
    JPanel toolpane = new JPanel();
    toolpane.setLayout(new BorderLayout());
    toolbar.setBorder(new EmptyBorder(0,16,0,16));
    toolpane.add(toolbar, BorderLayout.WEST);
    logo.setBorder(new EmptyBorder(4,8,4,8));
    toolpane.add(logo, BorderLayout.EAST);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(toolpane, "North");

    pack();
    this.setVisible(true);
  }

  public void addNotify()
	{
    super.addNotify();
    Dimension d = this.getPreferredSize();
    this.setSize(d.width+150,d.height);
  }

  public void actionPerformed(ActionEvent evt)
	{
    // Object src = evt.getSource();
    String cmd = evt.getActionCommand();

    if      (cmd.equals("Kill"))        KillAgents();
    else if (cmd.equals("Help"))        showHelp();
    else
      JOptionPane.showMessageDialog(this, "Option not functional yet");

    /* if      (cmd.equals("AddGoal"))     AddGoal();
    else if (cmd.equals("TuneAgent"))   TuneAgent();
    else if (cmd.equals("AddFact"))     AddFact();
    else if (cmd.equals("AddTask"))     AddTask();
    else if (cmd.equals("AddRelation")) AddRelation();
    else if (cmd.equals("AddStrategy")) AddStrategy(); */
  }

  public void SendCommand()
	{
    if ( !hubOK() ) return;

    String[] agents = model.getAgents();
    /* if ( cmdUI == null )
      cmdUI = new DoCommandUI(Util.getJFrame(this),"Command Interface",
                              "Add Agent:", agents, this);
    else
		{
      String[] chosen = cmdUI.getData();
      cmdUI.setListData(agents);
      cmdUI.setData(chosen);
    }
    cmdUI.setVisible(true); */
  }

  public void executeCommand(String cmd, String[] agents)
	{
    /* if ( cmd != null ) cmd = cmd.trim();
    if ( cmd.equals("") ) cmd = null;
    model.addAgents(agents);

    if ( agents != null && agents.length > 0 && cmd != null )
      achieve(cmd, agents, "?????"); */
  }

  public void LoadOntology()
	{
    File f = getFile(FileDialog.LOAD);
    if ( f == null ) return;

    if ( !f.exists() )
    {
		  JOptionPane.showMessageDialog(this, "File does not exist!");
			return;
    }

    OntologyDb db = context.OntologyDb();
    int status = db.openFile(f);
    if ( (status & OntologyDb.ERROR_MASK) != 0 )
    {
      JOptionPane.showMessageDialog(this, "Ontology File I/O Error: " + db.getError());
      return;
    }
    else if ( (status & OntologyDb.WARNING_MASK) != 0 )
      JOptionPane.showMessageDialog(this, "Warning: " + db.getWarning());
    else if ( (status & OntologyDb.WARNING_MASK) == 0 )
      JOptionPane.showMessageDialog(this, "Now using ontology from: " + f.getName());
  }

  protected File getFile(int type)
	{
    FileDialog f = new FileDialog(this, "Select File", type);

    if ( file != null ) {
      f.setFile(file.getName());
      f.setDirectory(file.getParent());
    }

    f.pack();
    f.setVisible(true);

    return  f.getFile()==null ? null : new File(f.getDirectory(),f.getFile());
  }

  public void AddGoal()
	{
    if ( !hubOK() ) return;
    /* if ( goalUI == null )
         goalUI = new GoalUI(Util.getFrame(this),"Goal Editor",GoalUI.EXTENDED,
                             this,ontology);

      if ( goalUI.isShowing() )
         goalUI.toFront();
      else {
         String storeKey = AllNames.AGENT_TYPE_PREFIX + AllNames.AGENT;
         String[] agents = model.getData(storeKey);
         if ( agents == null ) {
            JOptionPane.showMessageDialog(Util.getJFrame(this),
                          "No task agents currently online");
         }
         else {
            goalUI.display(agents);
         }
      } */
   }

   public void BrowseGoals()
	 {
     if ( !hubOK() ) return;

     JOptionPane.showMessageDialog(this, "This is Incomplete");

     /* if ( browseGoalUI == null )
          browseGoalUI = new BrowseGoalUI(Util.getFrame(this), "Browse Goals",
                                         goalTable, this);

      if ( browseGoalUI.isShowing() )
         browseGoalUI.toFront();
      else {
         String storeKey = AllNames.AGENT_TYPE_PREFIX + AllNames.AGENT;
         String[] agents = model.getData(storeKey);
         if ( agents == null ) {
            JOptionPane.showMessageDialog(Util.getJFrame(this),
                          "No task agents currently online");
         }
         else {
            browseGoalUI.display();
         }
      } */
   }

   public void StreamGoals(boolean mode)
	 {
      /* if ( !hubOK() ) return;

      String storeKey = AllNames.AGENT_TYPE_PREFIX + AllNames.AGENT;
      String[] agents = model.getAgents(); // getData(storeKey);

      if ( sa_goal == null )
         sa_goal = new SelectAgent(Util.getFrame(this),"Select Agents",
                                   "Add Agent:", agents);
      else {
         String[] chosen = sa_goal.getData();
         sa_goal.setListData(agents);
         sa_goal.setData(chosen);
      }

      String[] data  = sa_goal.selectedAgents();
      model.addReplyKey(sa_goal.getListData());
			//model.add(storeKey,sa_goal.getListData());

      if ( data != null && data.length > 0 )
        stream_all(mode,keys[GOAL_KEY],"stream-all-goals","log_goal",data); */
   }


   public void RequestGoals(String agent) {
      //stream_all(true,keys[GOAL_KEY],"stream-all-goals","log_goal",agent);
   }

   public void SuspendGoals(String agent, String[] ids) {
      //String content = Misc.concat("suspend goals",ids);
      //achieve("achieve-suspend-goals",content,agent);
   }

   public void ResumeGoals(String agent, String[] ids) {
      //String content = Misc.concat("resume goals",ids);
      //achieve("achieve-resume-goals",content,agent);
   }

   public void CancelGoals(String agent, String[] ids) {
      //String content = Misc.concat("cancel goals",ids);
      //achieve("achieve-cancel-goals",content,agent);
   }

   public void add(Goal g) {
      //achieve("achieve-do-goals", "do goal " + g, g.getDesiredBy());
   }

   public void add(Goal g, String ref) {
      //achieve("achieve-do-goals", "do enact-goal " + ref + " " + g, g.getDesiredBy());
   }

   private void KillAgents()
	 {
     System.out.println(myName + " sending kill command");
     String[] agents = model.getAgents(); // getData(AllNames.AGENTS);
     if (agents == null) return;

     for( int i = 0; i < agents.length; i++ )
       if (!agents[i].equals(myName))
       {
			   System.out.println("Killing ..." + agents[i]);
         request("kill_yourself", agents[i], "");
				 //achieve(context.newId(), "kill_yourself", agents[i]);
       }
     request("kill_yourself", myName, "");
	 }

   public void BrowseAgents() { }
   public void TuneAgent() { }

   public void BrowseFacts() {}
   public void AddFact() {}
   public void StreamFacts(boolean mode) {}

   public void BrowseTasks() {}
   public void AddTask() {}
   public void StreamTasks(boolean mode) {}

   public void BrowseRelations() {}
   public void AddRelation() {}
   public void StreamRelations(boolean mode) {}

   public void BrowseStrategies() {}
   public void AddStrategy() {}
   public void StreamStrategies(boolean mode) {}

   protected void removeRequestKeys() {
      //removeKey(keys[ADDRESS_KEY],"log_address");
      //removeKey(keys[GOAL_KEY],"log_goal");
   }

   protected void registerAgent(String name, String type) {
      /* if ( type.equals(AllNames.AGENT) ) {
         goalTable.add(name);
         if ( browseGoalUI != null && browseGoalUI.isShowing() )
            browseGoalUI.refresh();
      } */
   }

   protected boolean incomingMessage(Performative msg)
	 {
     /* String in_reply_to = msg.getInReplyTo();
     MContextItem mci = adhoc.lookUp(in_reply_to);

     String context = mci.getContext();
     String content = msg.getContent();

     if ( context.equals("achieve-do-goals")      ||
          context.equals("achieve-suspend-goals") ||
          context.equals("achieve-resume-goals")  ||
          context.equals("achieve-cancel-goals")  )
		 {
        msgContext.del(in_reply_to);
        adhoc.del(in_reply_to);
        return true;
     }
     else if ( context.equals("stream-all-goals") )
		 {
        Vector v = GoalSummary.getGoalSummaryList(content);
        for( int i = 0; i < v.size(); i++ )
        {
        	String s = ((GoalSummary)v.elementAt(i)).toString();
          goalTable.add(s);
				}
        if ( !v.isEmpty() && browseGoalUI != null && browseGoalUI.isShowing() )
           browseGoalUI.refresh();
        return true;
     } */
     return false;
   }


   public void showHelp()
   {
     HelpWindow helpWin;
     Point dispos = getLocation();
     helpWin = new HelpWindow(this, dispos, "visualiser", "Control Tool");
     helpWin.setSource(toolbar.helpBtn);
     helpWin.setSize(new Dimension(getWidth(), 440));
     helpWin.setLocation(dispos.x, dispos.y+getHeight());
     helpWin.validate();
   }

    /**
        a main method so the control tool can work by itself 
        */
   public static void main( String[] arg )
	 {
     AgentContext ac = new ZeusAgentContext("Test", "visualiser");
     ControlTool z = new ControlTool(ac, new VisualiserModel(ac));
   }
}
