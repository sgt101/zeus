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

public class RestrictionPanel extends JPanel {

  protected TaskAttributePanel taPanel;
  protected AgentDescription agent;

  public RestrictionPanel(AgentGenerator generator,
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
       new ControlPanel(editor,"Value Restriction Panel",false,true);
    
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(controlPane,gbc);
    add(controlPane);
    
    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;

    taPanel = new TaskAttributePanel(generator, genmodel, editor, agent,
				     Fact.FACT,ontologyDb,"Task Value Ranges");
    gridBagLayout.setConstraints(taPanel,gbc);
    add(taPanel);
  }

  public void save() {
    agent.setRestrictions(taPanel.getRestrictions());
  }
}
