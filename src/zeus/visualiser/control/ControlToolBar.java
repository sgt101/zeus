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



/****************************************************************************
* Toolbar for the Control Tool                                              *
*****************************************************************************/

package zeus.visualiser.control;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import zeus.util.SystemProps;


public class ControlToolBar extends JToolBar
{
  protected JButton  killBtn;
  protected JButton  addGoalBtn, addFactBtn;
  protected JButton  addTaskBtn, tuneAgentBtn;
  protected JButton  addRelBtn, addStratBtn;
  public    JToggleButton  helpBtn;


  public ControlToolBar(ActionListener caller)
  {
    // setBorder( new BevelBorder(BevelBorder.LOWERED ) );
    setFloatable(false);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + sep + "control" + sep;

    // Kill Agents Button
    killBtn = new JButton(new ImageIcon(path + "kill.gif"));
    add(killBtn);
    killBtn.setToolTipText("Closedown All Agents");
    killBtn.setMargin(new Insets(0, 0, 0, 0));
    killBtn.addActionListener(caller);
    killBtn.setActionCommand("Kill");
    addSeparator();

    addGoalBtn = new JButton(new ImageIcon(path + "addgoal.gif"));
    add(addGoalBtn);
    addGoalBtn.setToolTipText("Add a New Goal");
    addGoalBtn.setMargin(new Insets(0, 0, 0, 0));
    addGoalBtn.addActionListener(caller);
    addGoalBtn.setActionCommand("AddGoal");

    addFactBtn = new JButton(new ImageIcon(path + "addfact.gif"));
    add(addFactBtn);
    addFactBtn.setToolTipText("Add a New Fact");
    addFactBtn.setMargin(new Insets(0, 0, 0, 0));
    addFactBtn.addActionListener(caller);
    addFactBtn.setActionCommand("AddFact");

    addTaskBtn = new JButton(new ImageIcon(path + "addtask.gif"));
    add(addTaskBtn);
    addTaskBtn.setToolTipText("Add a New Task");
    addTaskBtn.setMargin(new Insets(0, 0, 0, 0));
    addTaskBtn.addActionListener(caller);
    addTaskBtn.setActionCommand("AddFact");

    tuneAgentBtn = new JButton(new ImageIcon(path + "tuneagent.gif"));
    add(tuneAgentBtn);
    tuneAgentBtn.setToolTipText("Modify Agent Attributes");
    tuneAgentBtn.setMargin(new Insets(0, 0, 0, 0));
    tuneAgentBtn.addActionListener(caller);
    tuneAgentBtn.setActionCommand("TuneAgent");

    addRelBtn = new JButton(new ImageIcon(path + "addrelation.gif"));
    add(addRelBtn);
    addRelBtn.setToolTipText("Add a new relationship");
    addRelBtn.setMargin(new Insets(0, 0, 0, 0));
    addRelBtn.addActionListener(caller);
    addRelBtn.setActionCommand("AddRelation");

    addStratBtn = new JButton(new ImageIcon(path + "addstrategy.gif"));
    add(addStratBtn);
    addStratBtn.setToolTipText("Add a new strategy");
    addStratBtn.setMargin(new Insets(0, 0, 0, 0));
    addStratBtn.addActionListener(caller);
    addStratBtn.setActionCommand("AddStrategy");
    addSeparator();

    helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
    add(helpBtn);
    helpBtn.setToolTipText("Help");
    helpBtn.setMargin(new Insets(0, 0, 0, 0));
    helpBtn.addActionListener(caller);
    helpBtn.setActionCommand("Help");
  }
}