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
* AgentEditor.java
*
* Main Frame for the agent editing panels
***************************************************************************/

package zeus.generator.agent;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.help.*;
import zeus.generator.*;
import zeus.generator.util.*;


public class AgentEditor extends JFrame
                         implements Editor {

  protected DefinitionPanel   definitionPanel;
  protected OrganisationPanel organisationPanel;
  protected CoordinationPanel coordinationPanel;
  protected RestrictionPanel  restrictionPanel;
  protected JTabbedPane       tabbedPane;
  
  protected AgentGenerator    generator;
  protected GeneratorModel    genmodel;
  protected OntologyDb        ontologyDb;
  protected HelpWindow        helpWin;
  
  protected boolean           changed;
  protected EventListenerList changeListeners = new EventListenerList();
  protected AgentDescription  currentAgent;
  
  static final String[] MESSAGE = {
     /* 0 */ "Save agent?",
     /* 1 */ "Save needed",
     /* 2 */ "Agent Editor: "

  };


  public AgentEditor(AgentGenerator generator, GeneratorModel genmodel,
                     OntologyDb ontologyDb, AgentDescription agent) {

    this.generator = generator;
    this.genmodel = genmodel;
    this.currentAgent = agent;
    this.ontologyDb = ontologyDb;

    changed = false;
    genmodel.addChangeListener(this);

    getContentPane().setBackground(java.awt.Color.gray);
    getContentPane().setLayout(new BorderLayout());

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "generator" + sep;
    ImageIcon icon = new ImageIcon(path + "edit.gif");
    setIconImage(icon.getImage());

    tabbedPane = new JTabbedPane();
    definitionPanel = new DefinitionPanel(generator,genmodel,ontologyDb,
                                          this,agent);
    organisationPanel = new OrganisationPanel(generator,genmodel,ontologyDb,
                                          this,agent);
    coordinationPanel = new CoordinationPanel(generator,genmodel,ontologyDb,
                                          this,agent);
    restrictionPanel = new RestrictionPanel(generator, genmodel, ontologyDb,
					    this, agent);

    
    tabbedPane.addTab("Agent Definition", definitionPanel);
    tabbedPane.addTab("Agent Organisation", organisationPanel);
    tabbedPane.addTab("Agent Coordination", coordinationPanel);
    tabbedPane.addTab("Value Restrictions", restrictionPanel);
    tabbedPane.setSelectedIndex(0);

    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    getContentPane().add(tabbedPane,BorderLayout.CENTER);

    this.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent evt) { closeDown(); }
       }
    );
    String name = genmodel.getAgentName(agent.getName());
    setTitle(MESSAGE[2] + name);
    
    validate();
    pack();
  }


  public void previous() {
    if ( (tabbedPane.getSelectedIndex() > 0) )
       tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
  }
  public void next() {
    if ( (tabbedPane.getSelectedIndex() + 1 < tabbedPane.getTabCount() ) )
       tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
  }
  public void help(AbstractButton helpBtn) {
    if ( helpBtn.isSelected() ) {
       Point dispos = getLocation();
       helpWin = new HelpWindow(this, dispos, "generator", "Agent Design Approach");
       helpWin.setSource(helpBtn);
    }
    else
       helpWin.dispose();
  }

  public void save() {
    definitionPanel.save();
    organisationPanel.save();
    coordinationPanel.save();
    restrictionPanel.save();
    genmodel.updateAgent(currentAgent);
    changed = false;
    fireChanged();
  }

  public AgentDescription getCurrentAgent() {
     return currentAgent;
  }
  public String getObjectName() {
     return genmodel.getAgentName(currentAgent.getName());
  }

  public boolean hasChanged() {
     return changed;
  }

  public void stateChanged(ChangeEvent evt) {
    if ( evt.getSource() != genmodel )
       changed = true;
    else if ( evt.getSource() == genmodel ) {
       String agentName = genmodel.getAgentName(currentAgent.getName());
       setTitle(MESSAGE[2] + agentName);
    }
    fireChanged();
  }

  public void closeDown() {
    if ( changed ) {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[0],
           MESSAGE[1],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION )
           save();
        else if ( answer == JOptionPane.CANCEL_OPTION )
           return;
    }
    generator.agentEditorClosed(currentAgent.getName());
    dispose();
  }

  // -- ChangeListener Methods
  public void addChangeListener(ChangeListener x) {
    changeListeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
    changeListeners.remove(ChangeListener.class, x);
  }
  
  protected void fireChanged() {
    ChangeEvent c = new ChangeEvent(this);
    Object[] listeners = changeListeners.getListenerList();
    for(int i = listeners.length-2; i >= 0; i -=2) {
       if (listeners[i] == ChangeListener.class) {
          ChangeListener cl = (ChangeListener)listeners[i+1];
          cl.stateChanged(c);
      }
    }
  }
}
