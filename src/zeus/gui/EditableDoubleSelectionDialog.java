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



package zeus.gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.gui.fields.*;


public class EditableDoubleSelectionDialog extends JDialog
                                   implements ActionListener,
                                              ListSelectionListener {

  protected JButton cancelButton;
  protected String OK = "OK";
  protected String CANCEL = "Cancel";
  protected JList lhsList, rhsList;
  protected DefaultListModel rhsListModel, lhsListModel;
  protected JButton okButton;
  protected NameField lhsTextfield;
  protected NameField rhsTextfield;
  protected Hashtable input = new Hashtable();
  protected Object[]  selection = null;

  public EditableDoubleSelectionDialog(Frame parent, String title,
                                       String leftLabel, String rightLabel,
                                       Hashtable data) {
    this(parent,title,leftLabel,rightLabel);
    setListData(input);
  }

  public EditableDoubleSelectionDialog(Frame parent, String title,
                                       String leftLabel, String rightLabel,
				       Hashtable input, Object leftItem,
                                       Object rightItem) {
    this(parent,title,leftLabel,rightLabel,input);
    lhsList.setSelectedValue(leftItem,true);
    rhsList.setSelectedValue(rightItem,true);
  }

  public EditableDoubleSelectionDialog(Frame parent, String title,
    String leftLabel, String rightLabel) {

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

    lhsListModel = new DefaultListModel();
    lhsList = new JList(lhsListModel);
    lhsList.setSelectionModel(new DefaultListSelectionModel());
    lhsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lhsList.addListSelectionListener(this);
    lhsList.setPreferredSize(new Dimension(60,100));
    JScrollPane lhsSP = new JScrollPane();
    lhsSP.getViewport().setView(lhsList);

    rhsListModel = new DefaultListModel();
    rhsList = new JList(rhsListModel);
    rhsList.setSelectionModel(new DefaultListSelectionModel());
    rhsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    rhsList.setPreferredSize(new Dimension(60,100));
    JScrollPane rhsSP = new JScrollPane();
    rhsSP.getViewport().setView(rhsList);

    JPanel p4 = new JPanel();
    p4.setLayout(gb);

    gbc.insets = new Insets(0,0,0,0);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;

    JLabel label = new JLabel(leftLabel);
    label.setToolTipText("Enter new data into text field and press <return> to add to list");
    gb.setConstraints(label,gbc);
    p4.add(label);

    gbc.insets = new Insets(0,10,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 1;

    lhsTextfield = new NameField(10);
    lhsTextfield.addActionListener(this);
    gb.setConstraints(lhsTextfield,gbc);
    p4.add(lhsTextfield);

    JPanel p5 = new JPanel();
    p5.setLayout(gb);

    gbc = new GridBagConstraints();
    gbc.insets = new Insets(0,0,0,0);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;

    label = new JLabel(rightLabel);
    label.setToolTipText("Enter new data into text field and press <return> to add to list");
    gb.setConstraints(label,gbc);
    p5.add(label);

    gbc.insets = new Insets(0,10,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 1;

    rhsTextfield = new NameField(10);
    rhsTextfield.addActionListener(this);
    gb.setConstraints(rhsTextfield,gbc);
    p5.add(rhsTextfield);

    JPanel p3 = new JPanel();
    p3.setLayout(gb);

    gbc.insets = new Insets(10,10,0,0);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 0;

    gb.setConstraints(p4,gbc);
    p3.add(p4);

    gbc.insets = new Insets(10,10,0,10);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 0;

    gb.setConstraints(p5,gbc);
    p3.add(p5);

    gbc.insets = new Insets(10,10,10,0);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;

    gb.setConstraints(lhsSP,gbc);
    p3.add(lhsSP);

    gbc.insets = new Insets(10,10,10,10);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;

    gb.setConstraints(rhsSP,gbc);
    p3.add(rhsSP);

    pane.add("South",p0);
    pane.add("Center",p3);

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

  public void valueChanged(ListSelectionEvent evt) {
     if ( evt.getValueIsAdjusting() ) return;
     JList list = (JList)evt.getSource();
     String value = (String)list.getSelectedValue();
     if ( value == null ) return;

     if ( list == lhsList ) {
        rhsList.clearSelection();
        HSet data = (HSet)input.get(value);
        Enumeration enum = data.elements();
        rhsListModel.removeAllElements();
        while( enum.hasMoreElements() )
           rhsListModel.addElement(enum.nextElement());
     }
  }

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();

    if ( source == okButton ) {
       int result;
       selection = new Object[2];
       selection[0] = lhsList.getSelectedValue();
       if ( selection[0] == null ) {
          result = JOptionPane.showConfirmDialog(this,
             "No value selected in left list\nContinue?", "Warning",
	     JOptionPane.YES_NO_OPTION);
          switch(result) {
	     case JOptionPane.YES_OPTION:
                  selection = null;
                  setVisible(false);
                  return;

             case JOptionPane.NO_OPTION:
                  return;
          }
       }
       selection[1] = rhsList.getSelectedValue();
       if ( selection[1] == null ) {
          result = JOptionPane.showConfirmDialog(this,
             "No value selected in right list\nContinue?", "Warning",
	     JOptionPane.YES_NO_OPTION);
          switch(result) {
	     case JOptionPane.YES_OPTION:
                  selection = null;
                  setVisible(false);
                  return;

             case JOptionPane.NO_OPTION:
                  return;
          }
       }
       this.setVisible(false);
    }
    else if ( source == cancelButton ) {
       this.setVisible(false);
    }
    else if ( source == lhsTextfield ) {
       String value = lhsTextfield.getText();
       if ( value == null ) return;
       value = value.trim();
       if ( value.equals("") ) return;

       if ( !lhsListModel.contains(value) ) {
          lhsListModel.addElement(value);
          input.put(value, new HSet());
       }
       lhsTextfield.setText("");
    }
    else if ( source == rhsTextfield ) {
       String value = rhsTextfield.getText();
       if ( value == null ) return;
       value = value.trim();
       if ( value.equals("") ) return;


       if ( !rhsListModel.contains(value) ) {
          String agent = (String)lhsList.getSelectedValue();
          if ( agent == null ) {
             JOptionPane.showMessageDialog(this,
                "Selection an item on the left list\nbefore typing <return>",
                "Error", JOptionPane.ERROR_MESSAGE);
             return;
          }
          rhsListModel.addElement(value);
          HSet inner = (HSet)input.get(agent);
          inner.add(value);
       }
       rhsTextfield.setText("");
    }
  }

  public Object[] getSelection() {
     selection = null;
     this.setVisible(true);
     return selection;
  }

  public Object getPriorSelection() {
     Object[] prior = new Object[2];
     prior[0] = lhsList.getSelectedValue();
     prior[1] = rhsList.getSelectedValue();
     return prior;
  }

  public void setListData(Hashtable input) {
     this.input.clear();
     lhsList.clearSelection();
     rhsList.clearSelection();
     lhsListModel.removeAllElements();
     rhsListModel.removeAllElements();
     Enumeration enum = input.keys();
     Object lvalue, rvalue;
     while( enum.hasMoreElements() ) {
        lvalue = enum.nextElement();
        rvalue = input.get(lvalue);
        lhsListModel.addElement(lvalue);
        this.input.put(lvalue,rvalue);
     }
  }

  public Hashtable getListData() {
     return input;
  }

  public void setSelection(Object leftValue, Object rightValue) {
     if ( leftValue != null  ) lhsList.setSelectedValue(leftValue,true);
     if ( rightValue != null ) rhsList.setSelectedValue(rightValue,true);
  }
}
