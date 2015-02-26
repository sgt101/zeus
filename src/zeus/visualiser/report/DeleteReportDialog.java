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



package zeus.visualiser.report;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.gui.*;
import zeus.util.Assert;
import zeus.util.Misc;


public class DeleteReportDialog extends JDialog
             implements ActionListener,
	                ListSelectionListener,
			ChangeListener {

    protected DefaultListModel agentListModel, taskListModel;
    protected JList            agentList, taskList;
    protected JButton          cancelButton;
    protected JButton          delButton;
    protected JButton          selectButton;
    protected JButton          clearButton;
    protected JButton          invertButton;
    protected ReportModel      graphModel = null;

    public DeleteReportDialog(JFrame frame, String title) {
        super(frame, title, false);

	JLabel agentLabel = new JLabel("Agents");
        JLabel taskLabel = new JLabel("Tasks");
        JLabel blankLabel = new JLabel("");

        agentListModel = new DefaultListModel();
        taskListModel = new DefaultListModel();
        agentList = new JList(agentListModel);
        taskList = new JList(taskListModel);

        cancelButton = new JButton("Cancel");

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        Container pane = getContentPane();
        pane.setLayout(gb);

        gbc.insets = new Insets(10,10,0,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gb.setConstraints(agentLabel, gbc);
        pane.add(agentLabel);

        gbc.insets = new Insets(10,10,0,0);
        gb.setConstraints(taskLabel, gbc);
        pane.add(taskLabel);

        gbc.insets = new Insets(10,10,0,10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(blankLabel,gbc);
        pane.add(blankLabel);

        JScrollPane scrollpane = new JScrollPane(agentList);
        scrollpane.setPreferredSize(new Dimension(100,150));
        gbc.weightx = gbc.weighty = 1.0;
        gbc.insets = new Insets(5,10,10,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gb.setConstraints(scrollpane,gbc);
        pane.add(scrollpane);

        scrollpane = new JScrollPane(taskList);
        scrollpane.setPreferredSize(new Dimension(100,150));
        gbc.insets = new Insets(5,10,10,0);
        gb.setConstraints(scrollpane,gbc);
        pane.add(scrollpane);

        selectButton = new JButton("Select All");
        clearButton = new JButton("Clear All");
        invertButton = new JButton("Invert Selection");
        delButton = new JButton("Delete");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,2,2));
        panel.add(selectButton);
        panel.add(clearButton);
        panel.add(invertButton);
        panel.add(delButton);
        gbc.insets = new Insets(5,10,10,10);
        gbc.weightx = gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        gb.setConstraints(panel, gbc);
        pane.add(panel);

        JSeparator separator = new JSeparator();
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(separator,gbc);
        pane.add(separator);

        gbc.insets = new Insets(10,0,10,0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(cancelButton,gbc);
        pane.add(cancelButton);

	delButton.addActionListener(this);
        selectButton.addActionListener(this);
        clearButton.addActionListener(this);
        invertButton.addActionListener(this);
        cancelButton.addActionListener(this);
        agentList.addListSelectionListener(this);
    	taskList.addListSelectionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                cancelBtnFn();
            }
        });

        agentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        agentList.addListSelectionListener(this);
//        taskList.addListSelectionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      int num;

      if ( source == selectButton ) {
         num = taskListModel.getSize();
         taskList.setSelectionInterval(0,num-1);
      }
      else if ( source == clearButton )
         taskList.clearSelection();
      else if ( source == invertButton ) {
         num = taskListModel.getSize();
         int[] indices = taskList.getSelectedIndices();
         taskList.clearSelection();
         for(int i = 0; i < num; i++ ) {
            boolean status = false;
            for(int j = 0; !status && j < indices.length; j++ )
               status = (indices[j] == i);
            if ( !status )
               taskList.addSelectionInterval(i,i);
         }
      }
      else if ( source == delButton )
         deleteBtnFn();
      else if ( source == cancelButton )
         cancelBtnFn();
    }

    private boolean agentSelected()  {
      ListSelectionModel selmodel = agentList.getSelectionModel();
      if ( selmodel.isSelectionEmpty() ) {
         JOptionPane.showMessageDialog(this,
            "Select an agent before calling this operation", "Error",
	    JOptionPane.ERROR_MESSAGE);
         return false;
      }
      return true;
    }

    private boolean taskSelected() {
      ListSelectionModel selmodel = taskList.getSelectionModel();
      if( selmodel.isSelectionEmpty() ) {
        JOptionPane.showMessageDialog(this,
            "Select one or more task entries before\ncalling this operation",
	    "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    protected void cancelBtnFn() {
       setVisible(false);
       if ( graphModel != null )
          graphModel.removeChangeListener(this);
    }

    protected void deleteBtnFn() {
       if ( !agentSelected() || !taskSelected() )
          return;

       String agent = (String)agentList.getSelectedValue();
       Object obj[] = taskList.getSelectedValues();

       if ( graphModel != null )
          graphModel.removeTasks(agent,Misc.stringArray(obj));
    }

    public void display(ReportModel graphModel)  {
       this.graphModel = graphModel;
       reset();
       graphModel.addChangeListener(this);
       pack();
       setVisible(true);
    }

    public void valueChanged(ListSelectionEvent evt) {
       JList list = (JList)evt.getSource();
       String value = (String)list.getSelectedValue();
       if ( list == agentList && graphModel != null && value != null ) {
          String[] tasks = graphModel.getTasks(value);
          taskList.clearSelection();
          taskListModel.removeAllElements();
          for(int i = 0; i < tasks.length; i++ )
             taskListModel.addElement(tasks[i]);
       }
    }

    public void stateChanged(ChangeEvent evt) {
       if ( graphModel != null && evt.getSource() == graphModel ) {
          String selection = (String)agentList.getSelectedValue();
          Object[] tasks = taskList.getSelectedValues();
          agentList.clearSelection();
          agentListModel.removeAllElements();
          String[] agents = graphModel.getAgents();
          for(int i = 0; i < agents.length; i++ )
             agentListModel.addElement(agents[i]);
          if ( selection != null )
             agentList.setSelectedValue(selection,true);

          int num = taskListModel.getSize();
          for(int i = 0; i < tasks.length; i++ )
             for(int j = 0; j < num; j++ )
                if ( tasks[i] == taskListModel.getElementAt(j) )
                   taskList.addSelectionInterval(j,j);
       }
    }

    public void reset() {
       agentList.clearSelection();
       taskList.clearSelection();
       agentListModel.removeAllElements();
       if ( graphModel != null ) {
          String[] agents = graphModel.getAgents();
          for(int i = 0; i < agents.length; i++ )
             agentListModel.addElement(agents[i]);
       }
    }
}
