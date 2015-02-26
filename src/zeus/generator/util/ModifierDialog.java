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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.concepts.Fact;


public class ModifierDialog extends JDialog 
                            implements ActionListener, ItemListener {

  protected static final String  CANCEL     = "Cancel";
  protected static final String  OK         = "Ok";

  protected static final String  VARIABLE   = "Is a variable";
  protected static final String  NEGATIVE   = "Not";
  protected static final String  READONLY   = "Strict Precondition (read only - not consumed)";
  protected static final String  LOCAL      = "Must be in local database";
  protected static final String  REPLACED   = "Is replaced after use";
  protected static final String  SIDEEFFECT = "Is a side-effect only";

  protected JButton okButton;
  protected JButton cancelButton;

  protected JCheckBox isVariable   = new JCheckBox(VARIABLE,false);
  protected JCheckBox isNegative   = new JCheckBox(NEGATIVE,false);
  protected JCheckBox isReadOnly   = new JCheckBox(READONLY,false);
  protected JCheckBox isLocal      = new JCheckBox(LOCAL,false);
  protected JCheckBox isReplaced   = new JCheckBox(REPLACED,false);
  protected JCheckBox isSideEffect = new JCheckBox(SIDEEFFECT,false);

  protected FactModifier caller = null;

  public ModifierDialog(Frame parent, String title) {
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

    JPanel p3 = new JPanel();
    p3.setBackground(Color.lightGray);
    CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
                                            new EmptyBorder(5,5,5,5));
    p3.setBorder(cbr);
    p3.setLayout(new GridLayout(6,1,5,5));

    p3.add(isVariable);
    p3.add(isNegative);
    p3.add(isReadOnly);
    p3.add(isLocal);
    p3.add(isReplaced);
    p3.add(isSideEffect);

    isVariable.setBackground(Color.lightGray);
    isNegative.setBackground(Color.lightGray);
    isReadOnly.setBackground(Color.lightGray);
    isLocal.setBackground(Color.lightGray);
    isReplaced.setBackground(Color.lightGray);
    isSideEffect.setBackground(Color.lightGray);

    pane.add("South",p0);
    pane.add("Center",p3);
    
    // Event handling
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);

    isVariable.addItemListener(this);
    isNegative.addItemListener(this);
    isReadOnly.addItemListener(this);
    isLocal.addItemListener(this);
    isReplaced.addItemListener(this);
    isSideEffect.addItemListener(this);

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
       if ( caller != null ) {
          int modifier = 0;
          modifier = Fact.setIsVariable(modifier,   isVariable.isSelected());
          modifier = Fact.setIsNegative(modifier,   isNegative.isEnabled() &&
                                                    isNegative.isSelected());
          modifier = Fact.setIsReadOnly(modifier,   isReadOnly.isEnabled() &&
                                                    isReadOnly.isSelected());
          modifier = Fact.setIsLocal(modifier,      isLocal.isEnabled() &&
                                                    isLocal.isSelected());
          modifier = Fact.setIsReplaced(modifier,   isReplaced.isEnabled() && 
                                                    isReplaced.isSelected());
          modifier = Fact.setIsSideEffect(modifier, isSideEffect.isEnabled() &&
                                                    isSideEffect.isSelected());

          caller.factModifiersChanged(modifier);
       }
    }
    else if ( source == cancelButton ) {
       this.setVisible(false);
    }
  }

  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    boolean state = (e.getStateChange() == ItemEvent.SELECTED);
    if ( src == isNegative ) {
       isReadOnly.setEnabled(!state);
       isLocal.setEnabled(!state);
       isReplaced.setEnabled(!state);
    }
    else if ( src == isReadOnly ) {
       isNegative.setEnabled(!state);
       isReplaced.setEnabled(!state);
    }
    else if ( src == isLocal ) {
       isNegative.setEnabled(!state);
    }
    else if ( src == isReplaced ) {
       isNegative.setEnabled(!state);
       isReadOnly.setEnabled(!state);
    }
  }

  public void display(FactModifier caller, int modifier, int type) {
     this.caller = caller;

     isVariable.setSelected(Fact.isVariable(modifier));
     isVariable.setEnabled(false);

     if ( type == FactPanel.PRECONDITION ) {
        isNegative.setEnabled(true);
        isReadOnly.setEnabled(true);
        isLocal.setEnabled(true);
        isReplaced.setEnabled(true);
        isSideEffect.setEnabled(false);

        isNegative.setSelected(Fact.isNegative(modifier));
        isReadOnly.setSelected(Fact.isReadOnly(modifier));
        isLocal.setSelected(Fact.isLocal(modifier));
        isReplaced.setSelected(Fact.isReplaced(modifier));
        isSideEffect.setSelected(false);
     }
     else {
        isNegative.setSelected(false);
        isReadOnly.setSelected(false);
        isLocal.setSelected(false);
        isReplaced.setSelected(false);
        isSideEffect.setSelected(Fact.isSideEffect(modifier));

        isNegative.setEnabled(false);
        isReadOnly.setEnabled(false);
        isLocal.setEnabled(false);
        isReplaced.setEnabled(false);
        isSideEffect.setEnabled(true);
     }
     setVisible(true);
     pack();
  }

}
