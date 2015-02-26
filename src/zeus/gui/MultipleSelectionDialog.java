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


public class MultipleSelectionDialog extends JDialog
                                    implements ActionListener {

  protected JButton cancelButton, selectButton, clearButton, invertButton;
  protected String SELECT_ALL = "Select All";
  protected String CLEAR_ALL = "Clear All";
  protected String INVERT = "Invert Selection";
  protected String OK = "OK";
  protected String CANCEL = "Cancel";
  protected JList list;
  protected JButton okButton;
  protected Object[] selection = null;

  public MultipleSelectionDialog(Frame parent, String title, Object[] data) {
    this(parent,title);
    list.setListData(data);
  }

  public MultipleSelectionDialog(Frame parent, String title,
                                 Object[] data, Object[] selectedData) {
    this(parent,title);
    list.setListData(data);
    setSelection(selectedData);
  }

  public MultipleSelectionDialog(Frame parent, String title) {
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
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    JScrollPane scrollpane = new JScrollPane();
    scrollpane.getViewport().setView(list);
    scrollpane.setPreferredSize(new Dimension(150,150));
    selectButton = new JButton(SELECT_ALL);
    clearButton = new JButton(CLEAR_ALL);
    invertButton = new JButton(INVERT);

    JPanel p2  = new JPanel();
    p2.setLayout(new GridLayout(3,1,2,2));
    p2.add(selectButton);
    p2.add(clearButton);
    p2.add(invertButton);

    JPanel p3 = new JPanel();
    p3.setLayout(gb);
    gbc.insets = new Insets(10,10,10,0);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;

    gb.setConstraints(scrollpane,gbc);
    p3.add(scrollpane);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(10,40,0,10);
    gbc.weightx = gbc.weighty = 0;
    gb.setConstraints(p2,gbc);
    p3.add(p2);

    pane.add("South",p0);
    pane.add("Center",p3);

    // Event handling
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);
    selectButton.addActionListener(this);
    clearButton.addActionListener(this);
    invertButton.addActionListener(this);
    this.addWindowListener(
       new WindowAdapter() {
          public void windowClosing(WindowEvent evt) { setVisible(false); }
       }
    );
    this.pack();
  }

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    int num = list.getModel().getSize();

    if ( source == selectButton ) {
       list.setSelectionInterval(0,num-1);
    }
    else if ( source == clearButton )
       list.clearSelection();
    else if ( source == invertButton ) {
       int[] indices = list.getSelectedIndices();
       list.clearSelection();
       for(int i = 0; i < num; i++ ) {
          boolean status = false;
          for(int j = 0; !status && j < indices.length; j++ )
             status = (indices[j] == i);
          if ( !status )
             list.addSelectionInterval(i,i);
       }
    }
    else if ( source == okButton ) {
       if ( !list.isSelectionEmpty() )
          selection = list.getSelectedValues();
       this.setVisible(false);
    }
    else if ( source == cancelButton ) {
       this.setVisible(false);
    }
  }

  public Object[] getPriorSelection() {
     return list.getSelectedValues();
  }

  public Object[] getSelection() {
     selection = null;
     this.setVisible(true);
     return selection;
  }

  public void setListData(Object[] data) {
     list.setListData(data);
  }

  public void setSelection(Object[] selectedData) {
    ListModel model = list.getModel();
    int num = model.getSize();
    list.clearSelection();

    for(int i = 0; i < selectedData.length; i++ )
       for(int j = 0; j < num; j++ )
          if ( selectedData[i] == model.getElementAt(j) )
             list.addSelectionInterval(j,j);
  }

  public Object[] getListData() {
     ListModel model = list.getModel();
     Vector data = new Vector();
     for(int i = 0; i < model.getSize(); i++ )
        data.addElement(model.getElementAt(i));
     return data.toArray();
  }

  public static void main(String arg[]) {
    JFrame f = new JFrame("Test");
    f.setSize(200,200);
    f.show();
    String[] agents = {"Dave","John","Henry","Alice","Albert"};
    MultipleSelectionDialog m = new MultipleSelectionDialog(f,"Select Servers");
    m.setListData(agents);
    Object[] data = m.getSelection();
    for(int i = 0; data != null && i < data.length; i++ )
       System.out.println(data[i]);
    System.out.println("DONE...");
  }
}
