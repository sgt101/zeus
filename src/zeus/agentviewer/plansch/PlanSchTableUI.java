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



package zeus.agentviewer.plansch;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


import zeus.agentviewer.*;
import zeus.util.*;
import zeus.gui.fields.*;
import zeus.actors.PlanRecord;
import zeus.concepts.*;


public class PlanSchTableUI extends ZeusInternalFrame
   implements ActionListener, MouseListener, ChangeListener  {

   private JPanel  contentPane;
   private JTable  table;
   private JTextArea textarea;
   private JScrollPane topPane;

   final int TOP_PANE_MIN_HEIGHT = 27;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_HEIGHT = 30;


   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;
   PlanSchModel planSchBuffer;
   WholeNumberField startTime;
   JButton setBtn,resetBtn;
   
   
   public PlanSchTableUI(InternalFramesPanel deskTop,
                           PlanSchModel planSchBuffer)
     {
        
        super(" ",true,true,true,true);
        try {
        setTitle("Planner & Scheduler:" + (++NUMBER_DISPLAYED));
        String sep = System.getProperty("file.separator");
        String gifpath = SystemProps.getProperty("gif.dir") + "agentviewer" + sep;
        ImageIcon icon = new ImageIcon(gifpath + ViewerNames.PLANSCH_IMG);
        setFrameIcon(icon);
        this.deskTop = deskTop;
        this.planSchBuffer = planSchBuffer;

        buildUI();
        startTime.setText(Integer.toString(planSchBuffer.getFrom()));
        deskTop.addInternalFrame(this);
        planSchBuffer.addChangeListener(this);
        planSchBuffer.setTable(table);
        setSize(500,350);
        setDoubleBuffered(true);
        setVisible(true);
        } catch (Exception e) { 
            e.printStackTrace(); }

      }


      class KeyLabel extends JPanel {
        
           public KeyLabel(String text, Color bColor) {
            try { 
              GridBagLayout gridBagLayout = new GridBagLayout();
              GridBagConstraints gbc = new GridBagConstraints();
              setLayout(gridBagLayout);
          
              // Add the colour panel
              JPanel colorPanel = new JPanel();
              colorPanel.setPreferredSize(new Dimension(10,10));
              colorPanel.setBackground(bColor);
              gbc.gridwidth = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.fill = GridBagConstraints.NONE;
              gbc.insets = new Insets(0,0,0,0);
              gbc.weightx = gbc.weighty = 0;
              gridBagLayout.setConstraints(colorPanel,gbc);
              add(colorPanel);

              // Add the text 
              JLabel label = new JLabel(text);
              gbc.gridwidth = GridBagConstraints.REMAINDER;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.fill = GridBagConstraints.HORIZONTAL;
              gbc.insets = new Insets(0,5,0,0);
              gbc.weightx = gbc.weighty = 1;
              gridBagLayout.setConstraints(label,gbc);
              add(label);
               } catch (Exception e) { 
            e.printStackTrace(); }

           }
      }
//-------------------------------------------------------------------------
      private JPanel getColorPanel() {
        try {
         KeyLabel lb;
         JPanel skeyPanel = new JPanel();
         skeyPanel.setLayout(new GridLayout(3,3,5,2));
         for(int i = 0; i < PlanRecord.state_string.length; i++ ) {
            lb = new KeyLabel(PlanRecord.state_string[i],PlanRecord.color[i]);
            skeyPanel.add(lb);
         }
   
         JPanel keyPanel = new JPanel();
         GridBagLayout gb = new GridBagLayout();
         GridBagConstraints gbc = new GridBagConstraints();
         keyPanel.setLayout(gb);
   
         gbc.insets = new Insets(10,5,0,0);
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.NONE;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         JLabel keyLb = new JLabel("Key",SwingConstants.LEFT);
         keyLb.setFont(new Font("Courier",Font.BOLD,14));
         keyLb.setForeground(Color.black);
         gb.setConstraints(keyLb,gbc);
         keyPanel.add(keyLb);
         gbc.insets = new Insets(5,5,5,5);
         gbc.weightx = gbc.weighty = 1;
         gb.setConstraints(skeyPanel,gbc);
         keyPanel.add(skeyPanel);
      

         return keyPanel;
            } catch (Exception e) { 
            e.printStackTrace(); }
            return null;
        
      }


      private void buildUI() {
        try {
          table = new JTable(planSchBuffer);
          planSchBuffer.setTable(table); 
          table.addMouseListener(this);
          table.getSelectionModel().setSelectionMode(
             ListSelectionModel.SINGLE_SELECTION);
          table.setCellSelectionEnabled(true);
          DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
             public void setValue(Object value) {
                if ( value == null ) {
                super.setValue("Free");
                   setBackground(PlanRecord.color[PlanRecord.FREE]);
                }
                else {
                   PrimitiveTask task = ((PlanRecord)value).getTask();
                   Goal g = ((PlanRecord)value).getGoal();
                   super.setValue(task.getName() + ": " + g.getFactType());
                   setBackground(PlanRecord.color[((PlanRecord)value).getState()]);
                }
       }
    };

          table.setDefaultRenderer(Object.class,renderer);

          topPane = new JScrollPane(table);
          topPane.getViewport().setBackground(Color.white);
//          topPane.setPreferredSize(new Dimension(TOP_PANE_MIN_WIDTH,
//             TOP_PANE_MIN_HEIGHT*planSchBuffer.getProcessors()));

          textarea = new JTextArea(3,40);

             textarea.setBorder(BorderFactory.createTitledBorder(
             BorderFactory.createLineBorder(Color.gray),
             "Diagnostic Information",
             TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION,
             new Font("Courier",Font.BOLD,14), Color.black)
          );

    textarea.setEditable(false);
          textarea.setLineWrap(true);
          textarea.setWrapStyleWord(true);

//          textarea.setPreferredSize(new Dimension(BOTTOM_PANE_MIN_WIDTH,
//                                                    BOTTOM_PANE_MIN_HEIGHT));
          JScrollPane bottomSP = new JScrollPane(textarea);

          JPanel centerPanel = new JPanel(new BorderLayout());
          centerPanel.add(BorderLayout.CENTER,topPane);
          centerPanel.add(BorderLayout.SOUTH, bottomSP);

          JPanel rangePanel = new JPanel();
          rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.X_AXIS));
          rangePanel.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.gray));
          rangePanel.add(Box.createRigidArea(new Dimension(15,10)));
          JLabel text = new JLabel("Display times from ");
          text.setAlignmentY(Component.CENTER_ALIGNMENT);
          text.setForeground(Color.black);
          rangePanel.add(text);
          //global
          startTime = new WholeNumberField();
          startTime.setPreferredSize(new Dimension(100,20));
          startTime.setMinimumSize(new Dimension(100,20));
          startTime.setAlignmentY(Component.CENTER_ALIGNMENT);
          rangePanel.add(startTime);

          setBtn = new JButton("Set");
          setBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
          setBtn.addActionListener(this);
          setBtn.setToolTipText("Click to set columns");
          setBtn.setForeground(Color.red);
          setBtn.setBorder(BorderFactory.createRaisedBevelBorder());

          resetBtn = new JButton("Reset");
          resetBtn.setBorder(BorderFactory.createRaisedBevelBorder());
          resetBtn.setForeground(Color.blue);
          resetBtn.setToolTipText("Click to reset columns");
          resetBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
          resetBtn.addActionListener(this);
          JPanel btnPanel = new JPanel(new GridLayout(1,2));
          btnPanel.add(setBtn);
          btnPanel.add(resetBtn);
          rangePanel.add(btnPanel);
          rangePanel.add(Box.createRigidArea(new Dimension(15,10)));

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout(10,10));
          contentPane.add(BorderLayout.CENTER,centerPanel);
          contentPane.add(BorderLayout.NORTH, rangePanel);
          contentPane.add(BorderLayout.SOUTH, getColorPanel());
          pack();
         } catch (Exception e) { 
            e.printStackTrace(); }

      }



        public void stateChanged(ChangeEvent c) {
            try { 
                startTime.setValue(planSchBuffer.getFrom());
                textarea.setText("  ");
                table.repaint(); 
              //  reSize();
              } catch (Exception e) { 
            e.printStackTrace(); }

        }


    public void mouseClicked(MouseEvent e) {
       int iMouseX = e.getX();
       int iMouseY = e.getY();

       int sCol = table.columnAtPoint(new Point(iMouseX,iMouseY));
       int sRow = table.rowAtPoint(new Point(iMouseX,iMouseY));
       PlanRecord pr = (PlanRecord) table.getValueAt(sRow,sCol);
       if (pr != null && pr.diagnostic() != null )
          textarea.setText(pr.diagnostic());
       else
          textarea.setText("");
    }
//-------------------------------------------------------------------------
    public void mouseEntered(MouseEvent e) {}
//-------------------------------------------------------------------------
    public void mousePressed(MouseEvent e) {}
//-------------------------------------------------------------------------
    public void mouseExited(MouseEvent e) {}
//-------------------------------------------------------------------------
    public void mouseReleased(MouseEvent e) {}
//-------------------------------------------------------------------------
     public void actionPerformed(ActionEvent event) {
        try {
          Object source = event.getSource();

          if ( source == setBtn) {
             Long value = startTime.getValue();
             if ( value != null ) planSchBuffer.setFrom(value.intValue());
          }
          else if (source == resetBtn) {
             planSchBuffer.reSetFrom();
          }
         } catch (Exception e) { 
            e.printStackTrace(); }

     }
//-------------------------------------------------------------------------
     void reSize() {
        try {
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
         } catch (Exception e) { 
            e.printStackTrace(); }

     }
//------------------------------------------------------------------------
}



