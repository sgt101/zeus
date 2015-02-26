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

import zeus.util.*;
import zeus.gui.fields.*;


public class EditableSingleSelectionDialog extends JDialog
                                   implements ActionListener {

  protected JButton cancelButton;
  protected String OK = "OK";
  protected String CANCEL = "Cancel";
  protected JList list;
  protected JButton okButton;
  protected Object selection = null;
  protected String ENTER = "Enter new item:";
  protected NameField textfield;

  public EditableSingleSelectionDialog(Frame parent, String title, 
                                       Object[] data) {
    this(parent,title);
    list.setListData(data);
  }

  public EditableSingleSelectionDialog(Frame parent, String title,
                                       Object[] data, Object selectedData) {
    this(parent,title);
    list.setListData(data);
    list.setSelectedValue(selectedData,true);
  }

  public EditableSingleSelectionDialog(Frame parent, String title) {
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

    list = new JList();
    list.setSelectionModel(new DefaultListSelectionModel());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setPreferredSize(new Dimension(100,100));
    JScrollPane scrollpane = new JScrollPane();
    scrollpane.getViewport().setView(list);

    JPanel p4 = new JPanel();
    p4.setLayout(gb);

    gbc.insets = new Insets(0,0,0,0);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;

    JLabel label = new JLabel(ENTER);
    label.setToolTipText("Enter new data into text field and press <return> to add to list");
    gb.setConstraints(label,gbc);
    p4.add(label);

    gbc.insets = new Insets(0,10,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 1;

    textfield = new NameField(20);
    textfield.addActionListener(this);
    gb.setConstraints(textfield,gbc);
    p4.add(textfield);

    JPanel p3 = new JPanel();
    p3.setLayout(gb);

    gbc.insets = new Insets(10,10,0,10);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;

    gb.setConstraints(p4,gbc);
    p3.add(p4);

    gbc.insets = new Insets(10,10,10,10);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;

    gb.setConstraints(scrollpane,gbc);
    p3.add(scrollpane);

    pane.add("South",p0);
    pane.add("Center",p3);

    gb.setConstraints(scrollpane,gbc);
    p3.add(scrollpane);

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

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();

    if ( source == okButton ) {
       selection = list.getSelectedValue();
       if ( selection == null ) {
          int result = JOptionPane.showConfirmDialog(this,
             "No value selected\nContinue?", "Warning",
	     JOptionPane.YES_NO_OPTION);
          switch(result) {
	     case JOptionPane.YES_OPTION:
                  break;

             case JOptionPane.NO_OPTION:
                  return;
          }
       }
       this.setVisible(false);
    }
    else if ( source == cancelButton ) {
       this.setVisible(false);
    }
    else if ( source == textfield ) {
       String value = textfield.getText();
       if ( value == null ) return;
       value = value.trim();
       if ( value.equals("") ) return;

       ListModel model = list.getModel();
       Vector data = new Vector();
       for(int i = 0; i < model.getSize(); i++ )
          data.addElement(model.getElementAt(i));

       if ( !data.contains(value) ) {
          data.addElement(value);
          list.setListData(data);
       }
       textfield.setText("");
    }
  }

  public Object getSelection() {
     selection = null;
     this.setVisible(true);
     return selection;
  }

  public void setListData(Object[] data) {
     list.setListData(data);
  }

  public Object[] getListData() {
     ListModel model = list.getModel();
     Vector data = new Vector();
     for(int i = 0; i < model.getSize(); i++ )
        data.addElement(model.getElementAt(i));
     return data.toArray();
  }
  
  public void setSelection(Object value) {
     list.setSelectedValue(value,true);
  }

  public static void main(String arg[]) {
    JFrame f = new JFrame("Test");
    f.setSize(200,200);
    f.show();
    String[] agents = {"Dave","John","Henry","Alice","Albert"};
    EditableSingleSelectionDialog m = new EditableSingleSelectionDialog(f,"Select Servers");
    m.setListData(agents);
    Object data = m.getSelection();
    System.out.println(data);
  }
}
