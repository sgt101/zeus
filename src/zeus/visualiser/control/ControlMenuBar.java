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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import zeus.util.Assert;


public class ControlMenuBar extends JMenuBar implements ActionListener
{
  protected JMenu fileMenu, agentMenu, goalMenu, orgMenu, coordMenu,
	                societyMenu, helpMenu;

  protected JMenuItem connect, disconnect, exit, load;
  protected JMenuItem browse_agent, tune_agent, command,
                      cc_fact, uncc_fact, browse_fact, add_fact,
                      cc_task, uncc_task, browse_task, add_task;
  protected JMenuItem cc_rel, uncc_rel, browse_rel, add_rel,
                      cc_sgy, uncc_sgy, browse_sgy, add_sgy;
  protected JMenuItem browse_goal, add_goal, cc_goal, uncc_goal;
  protected JMenuItem help, about;

  protected ControlTool view;

  protected static final int CHECK = 0;
  protected static final int PLAIN = 1;
  protected static final int RADIO = 2;


  public ControlMenuBar(ControlTool view)
	{
    super();
    Assert.notNull(view);
    this.view = view;

    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    connect    = createMenuItem(fileMenu, PLAIN, "Connect to namservers", 'C');
    disconnect = createMenuItem(fileMenu, PLAIN, "Disconnect from nameservers",'D');
    command    = createMenuItem(fileMenu, PLAIN, "Send commands...", 'S');
    command.setEnabled(false);
		load       = createMenuItem(fileMenu, PLAIN, "Load Ontology...", 'O');
    exit       = createMenuItem(fileMenu, PLAIN, "Quit", 'Q');
    add(fileMenu);

    agentMenu = new JMenu("Agents");
    agentMenu.setMnemonic('A');
    tune_agent  = createMenuItem(agentMenu, PLAIN, "Tune Agent...", 'T');
    cc_fact     = createMenuItem(agentMenu, PLAIN, "Request a Resource...", 'R');
    uncc_fact   = createMenuItem(agentMenu, PLAIN, "Unrequest Resource...", 'U');;
    browse_fact = createMenuItem(agentMenu, PLAIN, "Browse Resources...", 'B');
    add_fact    = createMenuItem(agentMenu, PLAIN, "Add a Resource...", 'A');
    agentMenu.addSeparator();
    cc_task     = createMenuItem(agentMenu, PLAIN, "Request Tasks...", 0);
    uncc_task   = createMenuItem(agentMenu, PLAIN, "Unrequest Tasks...", 0);
    browse_task = createMenuItem(agentMenu, PLAIN, "Browse Tasks...", 0);
    add_task    = createMenuItem(agentMenu, PLAIN, "Add a Task...", 0);
    add(agentMenu);
    agentMenu.setEnabled(false);

    goalMenu = new JMenu("Goals");
    goalMenu.setMnemonic('G');
    cc_goal     = createMenuItem(agentMenu, PLAIN, "Request Goals...", 'R');
    uncc_goal   = createMenuItem(agentMenu, PLAIN, "Unrequest Goals...", 'U');;
    browse_goal = createMenuItem(agentMenu, PLAIN, "Browse Goals...", 'B');
    add_goal    = createMenuItem(agentMenu, PLAIN, "Add a Goal...", 'A');;
    add(goalMenu);
    goalMenu.setEnabled(false);

    orgMenu = new JMenu("Organisation");
    orgMenu.setMnemonic('O');
    cc_rel     = createMenuItem(agentMenu, PLAIN, "Request Relations...", 'R');
    uncc_rel   = createMenuItem(agentMenu, PLAIN, "Unrequest Relations...", 'U');
    browse_rel = createMenuItem(agentMenu, PLAIN, "Browse Relations...", 'B');
    add_rel    = createMenuItem(agentMenu, PLAIN, "Add a Relation...", 'A');
    add(orgMenu);
    orgMenu.setEnabled(false);

		societyMenu = new JMenu("Society");
    societyMenu.setMnemonic('S');
    browse_agent = createMenuItem(agentMenu, PLAIN, "Browse Agents...", 'B');
    add(societyMenu);
    societyMenu.setEnabled(false);

    coordMenu = new JMenu("Co-ordination");
    coordMenu.setMnemonic('C');
		cc_sgy     = createMenuItem(agentMenu, PLAIN, "Request Strategies...", 'R');
    uncc_sgy   = createMenuItem(agentMenu, PLAIN, "Unrequest Strategies...", 'U');
    browse_sgy = createMenuItem(agentMenu, PLAIN, "Browse Strategies...", 'B');
    add_sgy    = createMenuItem(agentMenu, PLAIN, "Add a Strategy...", 'A');
    add(coordMenu);
    coordMenu.setEnabled(false);

    helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    help  = createMenuItem(helpMenu, PLAIN, "Using the Control Tool", 'U');
    about = createMenuItem(helpMenu, PLAIN, "About ZEUS", 'A');
    add(helpMenu);
  }


  private JMenuItem createMenuItem(JMenu menu, int type, String text, int accelKey)
	{
     JMenuItem item;
     switch(type)
		 {
       /* case CHECK :
         item = new JCheckBoxMenuItem();
         ((JCheckBoxMenuItem) item).setState(false);
         item.addItemListener(this);
         break; */
       case RADIO :
         item = new JRadioButtonMenuItem();
         item.addActionListener(this);
       default :
         item = new JMenuItem();
         item.addActionListener(this);
         break;
     }
     item.setText(text);
     if(accelKey > 0)
       item.setMnemonic(accelKey);
     menu.add(item);
     return item;
   }



  public void actionPerformed(ActionEvent event)
	{
    Object src = event.getSource();

    if      ( src == connect       ) view.Connect(true);
    else if ( src == disconnect    ) view.Connect(false);
    else if ( src == load          ) view.LoadOntology();
    else if ( src == command       ) view.SendCommand();
    else if ( src == exit          ) view.Exit();

    /* else if ( src == browse_agent  ) view.BrowseAgents();

    else if ( src == tune_agent    ) view.TuneAgent();

    else if ( src == browse_fact   ) view.BrowseFacts();
    else if ( src == add_fact      ) view.AddFact();
    else if ( src == cc_fact       ) view.StreamFacts(true);
    else if ( src == uncc_fact     ) view.StreamFacts(false);

    else if ( src == browse_task   ) view.BrowseTasks();
    else if ( src == add_task      ) view.AddTask();
    else if ( src == cc_task       ) view.StreamTasks(true);
    else if ( src == uncc_task     ) view.StreamTasks(false);

    else if ( src == browse_goal    ) view.BrowseGoals();
    else if ( src == add_goal       ) view.AddGoal();
    else if ( src == cc_goal        ) view.StreamGoals(true);
    else if ( src == uncc_goal      ) view.StreamGoals(false);

    else if ( src == browse_rel    ) view.BrowseRelations();
    else if ( src == add_rel       ) view.AddRelation();
    else if ( src == cc_rel        ) view.StreamRelations(true);
    else if ( src == uncc_rel      ) view.StreamRelations(false);

    else if ( src == browse_sgy    ) view.BrowseStrategies();
    else if ( src == add_sgy       ) view.AddStrategy();
    else if ( src == cc_sgy        ) view.StreamStrategies(true);
    else if ( src == uncc_sgy      ) view.StreamStrategies(false); */

    else if ( src == help          ) view.showHelp();
    else if ( src == about         ) view.About();
  }
}
