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
* SummaryTaskNodeEditorDialog.java
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
import zeus.generator.*;
import zeus.generator.util.*;


public class SummaryTaskNodeEditorDialog extends JDialog
                                   implements ActionListener, 
				              ChangeListener {

  protected JTabbedPane           tabbedPane;
  protected JButton               okBtn;
  protected JButton               cancelBtn;
  protected boolean               changed = false;
  protected SummaryTaskNodeEditor editor = null;

  protected TaskNodePanel nodePanel;
  protected TaskLinkPanel linkPanel;

  public SummaryTaskNodeEditorDialog(Frame parent, OntologyDb ontologyDb) {
    super(parent,"Summary Task Node Editor");

    JPanel pane = (JPanel)getContentPane();
    pane.setBackground(Color.lightGray);
    pane.setLayout(new BorderLayout());

    tabbedPane = new JTabbedPane();
    nodePanel = new TaskNodePanel(ontologyDb,this);
    linkPanel = new TaskLinkPanel(this,nodePanel,
       nodePanel.getPreconditionsManager(),nodePanel.getPostconditionsManager()
    );

    tabbedPane.addTab("Preconditions and Effects", nodePanel);
    tabbedPane.addTab("Links", linkPanel);

    tabbedPane.setSelectedIndex(0);
    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    pane.add(tabbedPane,BorderLayout.CENTER);

    JPanel controlpane = new JPanel();
    controlpane.setLayout(new GridLayout(1,2,10,10));
    pane.add(controlpane,BorderLayout.SOUTH);
    CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
                                            new EmptyBorder(5,5,5,5));
    controlpane.setBorder(cbr);

    okBtn = new JButton("OK");
    okBtn.addActionListener(this);
    cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(this);
    controlpane.add(okBtn);
    controlpane.add(cancelBtn);

    this.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent evt) { closeDown(false); }
       }
    );
    setModal(false);
    setVisible(false);
  }

  public void actionPerformed(ActionEvent e) {
     Object src = e.getSource();
     if ( src == okBtn )
        closeDown(changed);
     else if ( src == cancelBtn )
        closeDown(false);
  }

  protected void closeDown(boolean state) {
     if ( state ) {
        TaskNode node = nodePanel.getNode();
        Hashtable names = nodePanel.getNameTable();
        TaskLink[] links = linkPanel.getLinks();
        editor.editingStopped(node,links,names);
     }
     else {
        editor.editingCancelled();
     }
  }

  public void reset(SummaryTaskNodeEditor editor, TaskNode node,
                    TaskNode[] others, TaskLink[] links) {
     this.editor = editor;
     nodePanel.reset(node);
     linkPanel.reset(node,others,links);
     changed = false;
     tabbedPane.setSelectedIndex(0);
     pack();
  }

  public void stateChanged(ChangeEvent evt) {
     changed = true;
  }
}
