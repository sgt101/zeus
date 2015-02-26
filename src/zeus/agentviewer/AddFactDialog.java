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



/*****************************************************************************
*
*
* A pop-up dialog that prompts selection of a fact
*****************************************************************************/

package zeus.agentviewer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import zeus.ontology.facts.*;
import zeus.ontology.*;


public class AddFactDialog extends JDialog implements ActionListener{
  protected FactTreeUI     treeView;
  protected JButton        okBtn, cancelBtn;
  protected FactSelector   caller = null;

  public AddFactDialog(JDialog parent, FactTableModel gdModel) {
    super(parent, "Fact Selection Window");

     JPanel pane = (JPanel)getContentPane();
    pane.setBorder(new EmptyBorder(10,10,10,10));  
    pane.setBackground(Color.lightGray);
    pane.setLayout(new BorderLayout());

    treeView = new FactTreeUI(gdModel.ontologyDb);
    JScrollPane treePane = new JScrollPane();
    treePane.setPreferredSize(new Dimension(300,150));
    treePane.getViewport().add(treeView);
    pane.add(treePane,BorderLayout.CENTER);

    JPanel controlpane = new JPanel();
    controlpane.setLayout(new GridLayout(1,2,10,10));
    pane.add(controlpane,BorderLayout.SOUTH);

    okBtn = new JButton("OK");
    okBtn.addActionListener(this);
    cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(this);
    controlpane.add(okBtn);
    controlpane.add(cancelBtn);

    setModal(true);
    setVisible(false);
  }

  public void actionPerformed(ActionEvent evt) {
    if ( evt.getSource() == okBtn ) {
       setVisible(false);
       String[] names = treeView.getSelectedNodeNames();
       caller.factSelected(names);
    }
    else if ( evt.getSource() == cancelBtn ) {
       setVisible(false);
    }
  }

  public void display(FactSelector caller) {
    this.caller = caller;
    treeView.refresh();
    pack();
    setVisible(true);
  }
}
