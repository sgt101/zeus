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



package zeus.visualiser.basic;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.util.*;
import zeus.gui.*;
import zeus.gui.fields.*;

public class MsgFilterEditor extends JDialog
                             implements ActionListener, ItemListener {

  protected JButton okButton;
  protected JButton cancelButton;
  protected JLabel fromLabel  = new JLabel("From agent: ");
  protected JLabel toLabel    = new JLabel("To agent: ");
  protected JLabel aboutLabel = new JLabel("About topic: ");
  protected AgentList fromList, toList;

  protected JCheckBox  checkbox   = null;
  protected JTextField checkboxTf = null;
  protected MsgFilter filter = null;

  public MsgFilterEditor(JFrame parent, String title) {
    this(parent,title,null);
  }

  public MsgFilterEditor(JFrame parent, String title, String[] agents) {
    super(parent,title,true);
    getContentPane().setLayout(new BorderLayout());

    JPanel p1 = new JPanel();
    p1.setLayout(new GridLayout(1,2,10,10));
    okButton = new JButton("OK");
    cancelButton = new JButton("Cancel");
    p1.add(okButton);
    p1.add(cancelButton);

    getContentPane().add("South",p1);

    JPanel p2 = new JPanel();
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    p2.setLayout(gb);
    gbc.insets = new Insets(10,0,10,0);
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;

    gbc.insets = new Insets(10,10,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(fromLabel,gbc);
    p2.add(fromLabel);

    fromList = new AgentList(agents);
    gbc.insets = new Insets(0,0,0,10);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(fromList,gbc);
    p2.add(fromList);

    gbc.insets = new Insets(0,10,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(toLabel,gbc);
    p2.add(toLabel);

    toList = new AgentList(agents);
    gbc.insets = new Insets(0,0,0,10);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(toList,gbc);
    p2.add(toList);

    gbc.insets = new Insets(0,10,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(aboutLabel,gbc);
    p2.add(aboutLabel);

    checkbox = new JCheckBox("About");
    checkboxTf = new JTextField(20);

    gbc.gridwidth = 1;
    gbc.insets = new Insets(10,10,0,0);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = gbc.weighty = 0;
    gbc.fill = GridBagConstraints.NONE;
    gb.setConstraints(checkbox,gbc);
    p2.add(checkbox);

    gbc.insets = new Insets(10,10,0,10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(checkboxTf,gbc);
    p2.add(checkboxTf);

    JSeparator s1 = new JSeparator();
    gbc.insets = new Insets(10,0,10,0);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gb.setConstraints(s1,gbc);
    p2.add(s1);


    getContentPane().add("Center",p2);

    /* Event handling */
    okButton.addActionListener(this);
    cancelButton.addActionListener(this);
    checkbox.addItemListener(this);
    this.addWindowListener(
       new WindowAdapter() {
          public void windowClosing(WindowEvent evt) { setVisible(false); }
       }
    );

    /* Initialisation */
    checkbox.setSelected(false);
    checkboxTf.setText("");
    checkboxTf.setEnabled(checkbox.isSelected());
  }

  public void itemStateChanged(ItemEvent evt) {
    Object source = evt.getSource();
    if ( source == checkbox ) {
       checkboxTf.setEnabled(checkbox.isSelected());
    }
  }

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();

    if ( source == okButton ) {
       filter = new MsgFilter();
       filter.from = Misc.stringArray(fromList.getSelection());
       filter.to = Misc.stringArray(toList.getSelection());
       if ( checkbox.isSelected() )
          filter.about = checkboxTf.getText();
       this.setVisible(false);
    }
    else if ( source == cancelButton ) {
      this.setVisible(false);
    }
  }

  public void setListData(String[] agents) {
    fromList.setListData(agents);
    toList.setListData(agents);
  }

  public void setFilter(MsgFilter filter) {
    String[] from = new String[0], to = new String[0];
    String about = null;

    if ( filter != null ) {
       from = filter.from;
       to = filter.to;
       about = filter.about;
    }

    fromList.setSelection(from);
    toList.setSelection(to);

    checkbox.setSelected(false);
    checkboxTf.setEditable(checkbox.isSelected());

    if ( about != null ) {
       checkbox.setSelected(true);
       checkboxTf.setEditable(checkbox.isSelected());
       checkboxTf.setText(about);
    }
  }

  public synchronized MsgFilter getFilter() {
    filter = null;
    this.pack();
    this.setVisible(true);
    return filter;
  }

  protected class AgentList extends JPanel
                            implements ActionListener {

    protected JButton selectButton, clearButton, invertButton;
    protected String ENTER = "Add item:";
    protected String SELECT_ALL = "Select All";
    protected String CLEAR_ALL = "Clear All";
    protected String INVERT = "Invert Selection";
    protected NameField textfield;
    protected JList list;
    protected Object[] selection = null;
  
    public AgentList(Object[] data) {
      this();
      list.setListData(data);
    }
  
    public AgentList(Object[] data, Object[] selectedData) {
      this();
      list.setListData(data);
      setSelection(selectedData);
    }
  
    public AgentList() {
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
  
      JPanel p4 = new JPanel();
      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
  
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
      gbc.weightx = gbc.weighty = 0;
  
      textfield = new NameField(20);
      textfield.addActionListener(this);
      gb.setConstraints(textfield,gbc);
      p4.add(textfield);
  
      setLayout(gb);
  
      gbc.insets = new Insets(10,10,0,10);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.weightx = gbc.weighty = 0;
  
      gb.setConstraints(p4,gbc);
      add(p4);
  
      gbc.insets = new Insets(10,10,10,0);
      gbc.gridwidth = 1;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.weightx = gbc.weighty = 1;
  
      gb.setConstraints(scrollpane,gbc);
      add(scrollpane);
  
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(10,40,0,10);
      gbc.weightx = gbc.weighty = 0;
      gb.setConstraints(p2,gbc);
      add(p2);
  
      // Event handling
      selectButton.addActionListener(this);
      clearButton.addActionListener(this);
      invertButton.addActionListener(this);
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
  
    public Object[] getPriorSelection() {
       return list.getSelectedValues();
    }
  
    public Object[] getSelection() {
       selection = list.getSelectedValues();
       if ( selection.length == 0 ) return null;
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
  
    public void setSelection(Object[] selectedData) {
      ListModel model = list.getModel();
      int num = model.getSize();
      list.clearSelection();
  
      for(int i = 0; i < selectedData.length; i++ )
         for(int j = 0; j < num; j++ )
            if ( selectedData[i] == model.getElementAt(j) )
               list.addSelectionInterval(j,j);
    }
  }


  public static void main(String arg[]) {
    JFrame f = new JFrame("Test");
    f.setSize(200,200);
    f.show();
    String[] agents = {"Dave","John","Henry","Alice","Albert"};
    MsgFilterEditor m = new MsgFilterEditor(f,"Edit Filter",agents);
    MsgFilter data = m.getFilter();
    System.out.println(data);
    System.exit(0);
  }
}
