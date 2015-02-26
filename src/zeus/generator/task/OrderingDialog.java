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



package zeus.generator.task;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.concepts.Fact;
import zeus.generator.util.BasicFactModel;

public class OrderingDialog extends JDialog implements ActionListener {

  protected static final String OK = "OK";
  protected static final String CANCEL = "Cancel";

  protected JList lhsList;
  protected JList rhsList;
  protected JButton okButton;
  protected JButton cancelButton;
  protected BasicFactModel model;
  protected OrderingSelector selector;

  public OrderingDialog(Frame parent, String title, BasicFactModel model) {
    super(parent,title,true);

    this.model = model;

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

    JPanel lhsPanel = new JPanel();
    TitledBorder border = BorderFactory.createTitledBorder("Before");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    lhsPanel.setBorder(border);

    lhsList = new JList();
    lhsList.setSelectionModel(new DefaultListSelectionModel());
    lhsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lhsList.setPreferredSize(new Dimension(100,100));
    JScrollPane scrollpane1 = new JScrollPane();
    scrollpane1.getViewport().setView(lhsList);
    lhsList.setBackground(Color.white);

    JPanel rhsPanel = new JPanel();
    border = BorderFactory.createTitledBorder("After");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    rhsPanel.setBorder(border);

    rhsList = new JList();
    rhsList.setSelectionModel(new DefaultListSelectionModel());
    rhsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    rhsList.setPreferredSize(new Dimension(100,100));
    JScrollPane scrollpane2 = new JScrollPane();
    scrollpane2.getViewport().setView(rhsList);
    rhsList.setBackground(Color.white);

    JPanel p3 = new JPanel();
    p3.setLayout(new GridLayout(1,2,5,5));
    lhsPanel.add(scrollpane1);
    rhsPanel.add(scrollpane2);
    p3.add(lhsPanel);
    p3.add(rhsPanel);

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
  }

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();

    if ( source == okButton ) {
       String lhsItem = (String)lhsList.getSelectedValue();
       Object[] data = rhsList.getSelectedValues();
       String[] rhsItem = new String[data.length];
       for(int i = 0; i < rhsItem.length; i++ )
          rhsItem[i] = (String)data[i];

       if ( selector != null && lhsItem != null && rhsItem.length != 0 ) {
          for(int i = 0; i < rhsItem.length; i++ ) {
	     if ( lhsItem.equals(rhsItem[i]) ) {
                JOptionPane.showMessageDialog(this,
	           "The left and right items must be different:\n" +
		   lhsItem + " < " + rhsItem[i],
                   "Error", JOptionPane.ERROR_MESSAGE);
	        return;
             }
          }
          this.setVisible(false);
	  selector.orderingSelected(lhsItem,rhsItem);
       }
       else
          this.setVisible(false);
    }
    else if ( source == cancelButton ) {
       this.setVisible(false);
    }
  }

  public void display(OrderingSelector selector) {
     this.selector = selector;
     Fact[] data = model.getData();
     String[] items = new String[data.length];
     for(int i = 0; i < data.length; i++ )
        items[i] = data[i].getId();

     lhsList.setListData(items);
     rhsList.setListData(items);

     pack();
     setVisible(true);
  }  
}
