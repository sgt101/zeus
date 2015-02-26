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
* TaskEditor.java
*
* Main Frame for the task editing panels
***************************************************************************/

package zeus.generator.task;

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
import zeus.generator.task.rulebase.RuleUI;

/**
 *TaskEditor is the panel that is used as an editor for the various types of task.
 *dependent on the task type being edited the panel will take on the right 
 *characteristics to get the information that is appropriate for that task
 *Change Log
 *Simon added an Information Panel to the Primative task editor to allow information 
 *for DAML-S service descriptions to be edited
 */
public class TaskEditor extends JFrame
                        implements Editor {

  protected JTabbedPane       tabbedPane;
  protected AgentGenerator    generator;
  protected GeneratorModel    genmodel;
  protected OntologyDb        ontologyDb;
  protected HelpWindow        helpWin;
  protected boolean           changed;
  protected EventListenerList changeListeners = new EventListenerList();
  protected AbstractTask      currentTask;

  // Panels used for primitive tasks
  protected ConditionsPanel   conditionsPanel;
  protected ConstraintsPanel  constraintsPanel;
  protected InformationPanel infoPanel;

  // Panels used for summary tasks and plan scripts
  protected NodesPanel        nodesPanel;

  // Panels used for rulebases
  protected RuleUI            rulebasePanel;

  static final String[] MESSAGE = {
     /* 0 */ "Save task?",
     /* 1 */ "Save needed",
     /* 2 */ "Task Editor: "
  };


  public TaskEditor(AgentGenerator generator, GeneratorModel genmodel,
                    OntologyDb ontologyDb, AbstractTask task) {

    this.generator = generator;
    this.genmodel = genmodel;
    this.currentTask = task;
    this.ontologyDb = ontologyDb;

    changed = false;
    genmodel.addChangeListener(this);

    getContentPane().setBackground(Color.lightGray);
    getContentPane().setLayout(new BorderLayout());

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "generator" + sep;
    ImageIcon icon = new ImageIcon(path + "edit.gif");
    setIconImage(icon.getImage());

    tabbedPane = new JTabbedPane();
    if ( task.isPrimitive() ) {
       conditionsPanel = new ConditionsPanel(
          generator,genmodel,ontologyDb,this,(PrimitiveTask)task);
       constraintsPanel = new ConstraintsPanel(
          generator,genmodel,ontologyDb,this,(Task)task,
          conditionsPanel.getPreconditionsModel(),
          conditionsPanel.getPostconditionsModel());
       infoPanel = new InformationPanel((Task)task); 
       
       tabbedPane.addTab("Preconditions and Effects", conditionsPanel);
       tabbedPane.addTab("Constraints", constraintsPanel);
       tabbedPane.addTab("Information", infoPanel); 
    
    }
    else if ( task.isSummary() || task.isScript() ) {
       nodesPanel = new NodesPanel(
          generator,genmodel,ontologyDb,this,(SummaryTask)task);
       constraintsPanel = new ConstraintsPanel(
          generator,genmodel,ontologyDb,this,(Task)task,
	  nodesPanel.getPreconditionsModel(),
	  nodesPanel.getPostconditionsModel());
       tabbedPane.addTab("Decomposition Graph", nodesPanel);
       tabbedPane.addTab("Constraints", constraintsPanel);
    }
    else if ( task.isBehaviour() ) {
       rulebasePanel = new RuleUI(ontologyDb,this,(ReteKB)task, new Vector());
       tabbedPane.addTab("Behaviour Rulebase", rulebasePanel);
    }

    tabbedPane.setSelectedIndex(0);
    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    getContentPane().add(tabbedPane,BorderLayout.CENTER);

    this.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent evt) { closeDown(); }
       }
    );
    String name = genmodel.getTaskName(task.getName());
    setTitle(task.getTypeName() + " " + MESSAGE[2] + name);
    pack();
    this.repaint();

    genmodel.addChangeListener(new SymChangeListener());
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
       Point pt = getLocation();
       helpWin = new HelpWindow(this, pt, "generator", "Task Specification");
       helpWin.setSource(helpBtn);
    }
    else
       helpWin.dispose();
  }

  public void save() {
    if ( currentTask.isPrimitive() ) {
       conditionsPanel.save();
       constraintsPanel.save();
    }
    else if ( currentTask.isSummary() || currentTask.isScript() ) {
       nodesPanel.save();
       constraintsPanel.save();
    }
    else if ( currentTask.isBehaviour() ) {
       rulebasePanel.save();
    }
    genmodel.updateTask(currentTask);
    changed = false;
    fireChanged();
  }

  public AbstractTask getCurrentTask() {
     return currentTask;
  }
  public String getObjectName() {
     return genmodel.getTaskName(currentTask.getName());
  }

  public boolean hasChanged() {
     return changed;
  }

  public void stateChanged(ChangeEvent evt) {
    if ( evt.getSource() != genmodel )
       changed = true;
    fireChanged();
  }

  public void closeDown() {
    if ( changed ) {
       int answer = JOptionPane.showConfirmDialog(this,MESSAGE[0],
          MESSAGE[1],JOptionPane.YES_NO_CANCEL_OPTION);
       if ( answer == JOptionPane.YES_OPTION )
          save();
       else if ( answer == JOptionPane.CANCEL_OPTION ) {
          setVisible(true);
          return;
       }
    }
    generator.taskEditorClosed(currentTask.getName());
    dispose();
  }

  protected class SymChangeListener implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
       String taskName = genmodel.getTaskName(currentTask.getName());
       setTitle(currentTask.getTypeName() + " " + MESSAGE[2] + taskName);
       fireChanged();
    }
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
