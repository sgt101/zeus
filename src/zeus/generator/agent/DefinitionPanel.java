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
* DefinitionPanel.java
*
* Panel through which agent attributes are entered
***************************************************************************/

package zeus.generator.agent;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;
import zeus.generator.util.*;
import zeus.gui.fields.*;

public class DefinitionPanel extends JPanel {
  protected WholeNumberField planner_width;
  protected WholeNumberField planner_length;
  protected TaskPanel        taskPanel;
  protected FactPanel        factPanel;
  protected AgentDescription agent;

  public DefinitionPanel(AgentGenerator generator,
                         GeneratorModel genmodel,
                         OntologyDb ontologyDb,
                         AgentEditor editor,
                         AgentDescription agent)  {

    this.agent = agent;

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);    
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    
    // Add the control panel 
    ControlPanel controlPane =
       new ControlPanel(editor,"Agent Definition Panel",true,false);
    
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(controlPane,gbc);
    add(controlPane);
    
    // Add the panel containing planning parameters to this panel.
    JPanel planningPanel = new JPanel();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(planningPanel,gbc);
    add(planningPanel);

    // Add the panel containing the agent's tasks to the panel.
    String[] tasks = agent.getTasks();
    taskPanel = new TaskPanel(generator,genmodel,editor,tasks,
                              "Task Identification");
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(taskPanel,gbc);
    add(taskPanel);
    
    // Add the panel containing the agent's facts to this panel.
    Fact[] facts = agent.getInitialFacts();
    factPanel = new FactPanel(ontologyDb,editor,facts,Fact.FACT,
                              "Initial Agent Resources");
    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gridBagLayout.setConstraints(factPanel,gbc);
    add(factPanel);

    // create planning panel info
    planningPanel.setBackground(Color.lightGray);

    int min_width = SystemProps.getInt("planner.processors.min");
    int max_width = SystemProps.getInt("planner.processors.max");

    int min_length = SystemProps.getInt("planner.length.min");
    int max_length = SystemProps.getInt("planner.length.max");

    planner_width = new WholeNumberField(min_width,max_width);
    planner_width.setBackground(Color.white);
    planner_width.setValue(agent.getPlannerWidth());
    planner_width.addChangeListener(editor);

    planner_length = new WholeNumberField(min_length, max_length);
    planner_length.setBackground(Color.white);
    planner_length.setValue(agent.getPlannerLength());
    planner_length.addChangeListener(editor);

    planner_width.setPreferredSize(new Dimension(200,20));
    planner_length.setPreferredSize(new Dimension(200,20));

    TitledBorder border = BorderFactory.createTitledBorder("Planning Parameters");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    planningPanel.setBorder(border);

    GridBagLayout gb = new GridBagLayout();
    planningPanel.setLayout(gb);

    gbc = new GridBagConstraints();

    JLabel label1 = new JLabel("Maximum Number of Simultaneous Tasks");
    label1.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label1.setToolTipText("Minimum = " + min_width +
                          ", Maximum = " + max_width);

    gbc.insets = new Insets(4,8,4,0);
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(label1,gbc);
    planningPanel.add(label1);
  
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gb.setConstraints(planner_width,gbc);
    planningPanel.add(planner_width);

    JLabel label2 = new JLabel("Planner Length");
    label2.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label2.setToolTipText("Minimum = " + min_length +
                          ", Maximum = " + max_length);

    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,0);
    gb.setConstraints(label2,gbc);
    planningPanel.add(label2);
     
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gbc.weightx = 1;
    gb.setConstraints(planner_length,gbc);
    planningPanel.add(planner_length);
  }

  public void save() {
     Long value = planner_width.getValue();
     if ( value != null ) agent.setPlannerWidth(value.intValue());
     value = planner_length.getValue();
     if ( value != null ) agent.setPlannerLength(value.intValue());
     agent.setTasks(taskPanel.getData());
     agent.setInitialFacts(factPanel.getData());
  }
}
