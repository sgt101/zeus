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



package zeus.generator.util;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ParameterDialog extends JDialog 
  implements ActionListener {

  protected static final String  CANCEL     = "Cancel";
  protected static final String  OK         = "Ok";

  protected JButton okButton;
  protected JButton cancelButton;


  protected ParameterChooser caller = null;
  protected HashtableModel model;

  public ParameterDialog(Frame parent, String title) {
    super(parent,title,true);

    JPanel pane = (JPanel)getContentPane();
    pane.setLayout( new BorderLayout() );

    
    JPanel p1 = new JPanel();
    p1.setLayout(new GridLayout(1,2,10,10));
    okButton = new JButton(OK);
    cancelButton = new JButton(CANCEL);
    p1.add(okButton);
    p1.add(cancelButton);
    
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10,0,10,0);
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 1;
    
    JPanel p0 = new JPanel();
    JSeparator s1 = new JSeparator();
    p0.setLayout(gb);
    gb.setConstraints(s1,gbc);
    p0.add(s1);
    
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,0,10,0);
    gb.setConstraints(p1,gbc);
    p0.add(p1);

    model = new HashtableModel();
    JPanel panel = new HashtablePanel(model);
    panel.setBackground(Color.lightGray);

    pane.add("South",p0);
    pane.add("Center",panel);
    
    // Event handling
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);

    this.addWindowListener(
       new WindowAdapter() {
          public void windowClosing(WindowEvent evt) { setVisible(false); }
       }
    );
    this.pack();
  }
  
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if ( source == okButton ) {
       this.setVisible(false);
       if ( caller != null && model.hasChanged() ){
          caller.parametersChanged(model.getData());
          repaint();}
    }
    else if ( source == cancelButton ) {
       this.setVisible(false);
    }
  }

  public void display(ParameterChooser caller, Hashtable input) {
     this.caller = caller;
     model.reset(input);
     setVisible(true);
     pack();
  }

}
