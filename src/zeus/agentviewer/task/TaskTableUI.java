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



package zeus.agentviewer.task;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.agentviewer.*;
import zeus.util.*;
import zeus.gui.fields.*;


public class TaskTableUI extends ZeusInternalFrame
                         implements ListSelectionListener  {

   final int TOP_PANE_MIN_HEIGHT = 43;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 100;
   final int BOTTOM_PANE_MIN_HEIGHT = 100;

   private JPanel  contentPane;
   private JTable  taskTable, preCondTable,
                   preAttrTable,effectsTable, effectsAttrTable,
                   orderingTable, constraintsTable;



   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;

   TaskTableModel taskBuffer;
   ConditionsAttributeTableModel preAttrBuffer, effectsAttrBuffer;
   TaskConditionsTableModel effectsBuffer, preCondBuffer;
   ConstraintsModel constraintsBuffer;
   OrderingModel orderingBuffer;
   LargeTextField timefield, costfield;

//------------------------------------------------------------------------------
     public TaskTableUI(InternalFramesPanel deskTop,
                        TaskTableModel taskBuffer,
                        TaskConditionsTableModel preCondBuffer,
                        ConditionsAttributeTableModel preAttrBuffer,
                        TaskConditionsTableModel effectsBuffer,
                        ConditionsAttributeTableModel effectsAttrBuffer,
                        ConstraintsModel constraintsBuffer,
                        OrderingModel orderingBuffer)
     {
        super("Task Database",true,true,true,true);
        setTitle("Task Database:" + (++NUMBER_DISPLAYED));
        String sep = System.getProperty("file.separator");
        String gifpath = SystemProps.getProperty("gif.dir") + "agentviewer" + sep;
        ImageIcon icon = new ImageIcon(gifpath + ViewerNames.TASKPLAN_IMG);
        setFrameIcon(icon);
        this.deskTop = deskTop;
        this.taskBuffer =taskBuffer;
        this.preCondBuffer = preCondBuffer;
        this.preAttrBuffer = preAttrBuffer;
        this.effectsBuffer = effectsBuffer;
        this.effectsAttrBuffer =effectsAttrBuffer;
        this.constraintsBuffer = constraintsBuffer;
        this.orderingBuffer = orderingBuffer;

        buildUI();
        deskTop.addInternalFrame(this);
        setVisible(true);
      }
//------------------------------------------------------------------------------
      private void buildUI() {
          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());

          JPanel conditionsPanel = new JPanel(new GridLayout(1,2,10,10));
          conditionsPanel.setBorder(
             BorderFactory.createEtchedBorder(Color.gray,Color.black));
          conditionsPanel.add(getPreConditionsPanel());
          conditionsPanel.add(getEffectsPanel());

          JPanel centerPanel = new JPanel(new BorderLayout(5,15));

          JPanel taskPanel = getTaskPanel();
          JPanel costPanel = getCostPanel();

          GridBagLayout gridBagLayout = new GridBagLayout();
          GridBagConstraints gbc = new GridBagConstraints();
          centerPanel.setLayout(gridBagLayout);
          
          // Add the taskPanel 
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.anchor = GridBagConstraints.NORTHWEST;
          gbc.fill = GridBagConstraints.BOTH;
          gbc.insets = new Insets(8,8,0,8);
          gbc.weightx = gbc.weighty = 1;
          gridBagLayout.setConstraints(taskPanel,gbc);
          centerPanel.add(taskPanel);
      
          // Add the conditionsPanel
          JPanel flowPanel = new JPanel();
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.anchor = GridBagConstraints.WEST;
          gbc.fill = GridBagConstraints.BOTH;
          gbc.insets = new Insets(8,8,0,8);
          gbc.weightx = gbc.weighty = 1;
          gridBagLayout.setConstraints(conditionsPanel,gbc);
          centerPanel.add(conditionsPanel);
          
          // Add the panel containing the task's cost and time to the panel.
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.anchor = GridBagConstraints.SOUTHWEST;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          gbc.insets = new Insets(8,8,8,8);
          gbc.weightx = gbc.weighty = 0;
          gridBagLayout.setConstraints(costPanel,gbc);
          centerPanel.add(costPanel);

          JTabbedPane tabbedPane = new JTabbedPane();

          tabbedPane.addTab("Preconditions and Effects", centerPanel);
          tabbedPane.addTab("Constraints", getConstraintsPanel());
          tabbedPane.setSelectedIndex(0);

          tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
          contentPane.add(tabbedPane,BorderLayout.CENTER);

          pack();

      }
//------------------------------------------------------------------------------
      JPanel getConstraintsPanel() {
          JPanel panel = new JPanel(new BorderLayout());

          JPanel north = new JPanel(new BorderLayout());
          north.setBorder(makeBorder("PreConditions Ordering Constraints"));
          orderingTable = new JTable(orderingBuffer);
          orderingTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT));
          JScrollPane orderingTableSP = new JScrollPane(orderingTable);
          north.add(BorderLayout.CENTER,orderingTableSP);
          panel.add(BorderLayout.NORTH, north);

          JPanel south =  new JPanel(new BorderLayout());
          south.setBorder(makeBorder("Task Applicability Constraints"));
          constraintsTable = new JTable(constraintsBuffer);
          constraintsTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT));
          JScrollPane constraintsTableSP = new JScrollPane(constraintsTable);
          south.add(BorderLayout.CENTER,constraintsTableSP);
          panel.add(BorderLayout.CENTER, south);

          return panel;

      }
//------------------------------------------------------------------------------
      JPanel getTaskPanel() {
         JPanel north =  new JPanel(new BorderLayout());
         north.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.gray));
         taskTable = new JTable(taskBuffer);
         taskTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT));
         JScrollPane taskTableSP = new JScrollPane(taskTable);
         taskTable.getSelectionModel().addListSelectionListener(this );
         taskTable.getSelectionModel().setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
         north.add(BorderLayout.CENTER,taskTableSP);


         return north;
      }
//------------------------------------------------------------------------------
      JPanel getPreConditionsPanel() {
          JPanel centerLeft =  new JPanel(new GridLayout(2,1,2,6));
          Border redline = BorderFactory.createLineBorder(Color.red);
          centerLeft.setBorder(makeBorder("PreConditions"));

          preCondTable = new JTable(preCondBuffer);
          preCondTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH/2, TOP_PANE_MIN_HEIGHT));
          JScrollPane preCondTableSP = new JScrollPane(preCondTable);
          preCondTable.getSelectionModel().addListSelectionListener(this );
          preCondTable.getSelectionModel().setSelectionMode(
             ListSelectionModel.SINGLE_SELECTION);
          preCondTable.setBorder(BorderFactory.createLineBorder(Color.black));
          centerLeft.add(preCondTableSP);


          preAttrTable = new JTable(preAttrBuffer);
          preAttrTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH/2, TOP_PANE_MIN_HEIGHT));
          JScrollPane preAttrTableSP = new JScrollPane(preAttrTable);
          preAttrTable.getSelectionModel().addListSelectionListener(this );
          preAttrTable.getSelectionModel().setSelectionMode(
             ListSelectionModel.SINGLE_SELECTION);
          preAttrTable.setBorder(BorderFactory.createLineBorder(Color.black));
          centerLeft.add(preAttrTableSP);


          return centerLeft;
      }
//------------------------------------------------------------------------------
      JPanel getEffectsPanel() {
          JPanel centerRight =  new JPanel(new GridLayout(2,1,2,6));
          centerRight.setBorder(makeBorder("Effects"));
          effectsTable = new JTable(effectsBuffer);
          effectsTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH/2, TOP_PANE_MIN_HEIGHT));
          JScrollPane effectsTableSP = new JScrollPane(effectsTable);
          effectsTable.getSelectionModel().addListSelectionListener(this );
          effectsTable.getSelectionModel().setSelectionMode(
             ListSelectionModel.SINGLE_SELECTION);
          effectsTable.setBorder(BorderFactory.createLineBorder(Color.black));
          centerRight.add(effectsTableSP);


          effectsAttrTable = new JTable(effectsAttrBuffer);
          effectsAttrTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH/2, TOP_PANE_MIN_HEIGHT));
          JScrollPane effectsAttrTableSP = new JScrollPane(effectsAttrTable);
          effectsAttrTable.getSelectionModel().addListSelectionListener(this );
          effectsAttrTable.getSelectionModel().setSelectionMode(
             ListSelectionModel.SINGLE_SELECTION);
          effectsAttrTable.setBorder(BorderFactory.createLineBorder(Color.black));
          centerRight.add(effectsAttrTableSP);


          return centerRight;
      }
//------------------------------------------------------------------------------
      private TitledBorder makeBorder(String title) {
          TitledBorder border = (BorderFactory.createTitledBorder(title));
          border.setTitlePosition(TitledBorder.TOP);
	  border.setTitleJustification(TitledBorder.RIGHT);
	  border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
	  border.setTitleColor(Color.black);

          return border;

     }
//------------------------------------------------------------------------------
   JPanel getCostPanel() {

    JPanel costPanel = new JPanel(new BorderLayout());


    // Cost Panel information
    TitledBorder border =
       BorderFactory.createTitledBorder("Task Cost and Time");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border.setTitleColor(Color.black);
    costPanel.setBorder(border);

    JPanel labelPanel = new JPanel(new GridLayout(2,1,5,10));

    JLabel label = new JLabel("Cost:");
    label.setToolTipText("Default cost of performing this task");
    labelPanel.add(label);
    label = new JLabel("Time:");
    label.setToolTipText("Default duration of this task");
    labelPanel.add(label);
    costPanel.add(BorderLayout.WEST,labelPanel);


    JPanel fieldsPanel = new JPanel(new GridLayout(2,1,5,10));
    costfield = new LargeTextField(2,15);
    costfield.setLineWrap(true);
    costfield.setText("");
    costfield.setEditable(false);
    fieldsPanel.add(costfield);

    timefield = new LargeTextField(2,15);
    timefield.setLineWrap(true);
    timefield.setText("");
    timefield.setEditable(false);
    fieldsPanel.add(timefield);

    costPanel.add(BorderLayout.CENTER,fieldsPanel);

    return costPanel;

   }
//------------------------------------------------------------------------------
      public void valueChanged(ListSelectionEvent e) {
          int row;
          if (e.getSource() == taskTable.getSelectionModel() ) {

               row = taskTable.getSelectedRow();
               costfield.setText(taskBuffer.getCost(row));
               timefield.setText(taskBuffer.getTime(row));
               if (row >= 0  && row < taskBuffer.getRowCount()) {
                    preCondBuffer.setFacts(taskBuffer.getPreConditions(row));
                    preCondTable.validate();
                    effectsBuffer.setFacts(taskBuffer.getEffects(row));
                    effectsTable.validate();
               }
          }
          else if (e.getSource() == preCondTable.getSelectionModel() ) {
              row = preCondTable.getSelectedRow();
              if (row >= 0  && row < preCondBuffer.getRowCount()) {
                   preAttrBuffer.setFact(preCondBuffer.getFact(row));
                   preAttrTable.validate();
               }
          }
          else if (e.getSource() == effectsTable.getSelectionModel() ) {
              row = effectsTable.getSelectedRow();
              if (row >= 0  && row < effectsBuffer.getRowCount()) {
                  effectsAttrBuffer.setFact(effectsBuffer.getFact(row));
                  effectsAttrTable.validate();
               }
          }
      }

//------------------------------------------------------------------------------
     void reSize() {
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
     }
}
