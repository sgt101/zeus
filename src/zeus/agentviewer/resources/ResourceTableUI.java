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



package zeus.agentviewer.resources;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


import zeus.agentviewer.*;
import zeus.util.*;



public class ResourceTableUI extends ZeusInternalFrame implements ListSelectionListener,
                                                               ActionListener,
                                                               ChangeListener

{
   String fsep = System.getProperty("file.separator");
   String IMAGEPATH = SystemProps.getProperty("gif.dir") + "agentviewer" + fsep;
   private JPanel  contentPane;
   private JTable  table;
   private JTextArea bottomPane;
   private JScrollPane topPane;

   final int IMGw = 20;
   final int IMGh = 20;
   final int TOP_PANE_MIN_HEIGHT = 120;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_HEIGHT = 100;

   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;
   ResourceTableModel resourceBuffer;
   private FactAttributesTableModel attributesTableModel;
   JTable attributesTable;




//------------------------------------------------------------------------------
     public ResourceTableUI(InternalFramesPanel deskTop,
                            ResourceTableModel resourceBuffer)
     {
        super(" ",true,true,true,true);
        setTitle("Resource Database:" + (++NUMBER_DISPLAYED) );
        ImageIcon icon = new ImageIcon(IMAGEPATH + ViewerNames.RESDB_IMG);
        setFrameIcon(icon);
        this.deskTop = deskTop;
        this.resourceBuffer =resourceBuffer;
        attributesTableModel = new FactAttributesTableModel(resourceBuffer);
        buildUI();
        deskTop.addInternalFrame(this);
        setVisible(true);
     }

//------------------------------------------------------------------------------
     Icon getIcon(String imgFile, int w, int h){

       String  imgStr = new String(IMAGEPATH + imgFile);
       Image aImg = Toolkit.getDefaultToolkit().getImage(imgStr);
       aImg = aImg.getScaledInstance(w,h,Image.SCALE_SMOOTH);
       Icon aIcon = new ImageIcon(aImg);
       return  aIcon;
     }
//------------------------------------------------------------------------------
    public void stateChanged(ChangeEvent c){
      bottomPane.setText("  ");
    }
//------------------------------------------------------------------------------
     public void actionPerformed(ActionEvent e){
         String cmd = e.getActionCommand();
         int sRow;

         if (cmd.equals("delResBtn")) {
            sRow = table.getSelectedRow();
            if (sRow == -1) {
               JOptionPane.showMessageDialog(this," Select resource",
                  "Resource not selected",JOptionPane.ERROR_MESSAGE);
              return;
            }
            else {
              // resourceBuffer.deleteFact(sRow);
              // attributesTableModel.setFact(null);
              // bottomPane.setText("  ");
            }

         }
    }
//------------------------------------------------------------------------------
    JToolBar getToolBar(){

         JButton  delResBtn;
         String img;
         JToolBar toolBar = new JToolBar();

         delResBtn = new JButton(getIcon(ViewerNames.DELETE_IMG,IMGw,IMGh));
         delResBtn.setToolTipText("Delete resource");
         delResBtn.addActionListener(this);
         delResBtn.setActionCommand("delResBtn");
         toolBar.add(delResBtn);

         toolBar.setFloatable(false);

         return toolBar;
    }
//------------------------------------------------------------------------------
   private TitledBorder makeBorder(String title){
          TitledBorder border = (BorderFactory.createTitledBorder(title));
        	border.setTitlePosition(TitledBorder.TOP);
	        border.setTitleJustification(TitledBorder.RIGHT);
	        border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
	        border.setTitleColor(Color.black);
          return border;

   }
//------------------------------------------------------------------------------
   private void buildUI(){

          table = new JTable(resourceBuffer);
          table.setPreferredScrollableViewportSize(new Dimension(TOP_PANE_MIN_WIDTH,
                                                                 TOP_PANE_MIN_HEIGHT));
          topPane = new JScrollPane(table);
          table.getSelectionModel().addListSelectionListener(this );
          table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

          bottomPane = new JTextArea();
          bottomPane.setEditable(false);
          bottomPane.setLineWrap(true);
          bottomPane.setWrapStyleWord(true);
          bottomPane.setPreferredSize(new Dimension(BOTTOM_PANE_MIN_WIDTH,
                                                    BOTTOM_PANE_MIN_HEIGHT));
          JScrollPane bottomSP = new JScrollPane(bottomPane);
          bottomSP.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.gray));

          //attributes
          attributesTable = new JTable(attributesTableModel);
          attributesTable.setColumnSelectionAllowed(false);
          attributesTable = new JTable(attributesTableModel);
          attributesTable.setPreferredScrollableViewportSize(new Dimension(BOTTOM_PANE_MIN_WIDTH,
                                                               BOTTOM_PANE_MIN_HEIGHT));

          JPanel attributesPanel = new JPanel(new BorderLayout());
          attributesPanel.setBorder(makeBorder("Attributes"));
          attributesPanel.add(BorderLayout.CENTER,new JScrollPane(attributesTable));

          JPanel centerPanel = new JPanel(new BorderLayout());
          centerPanel.add(BorderLayout.NORTH,topPane);
          centerPanel.add(BorderLayout.CENTER,attributesPanel);
          //centerPanel.add(bottomSP);

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());
          //contentPane.add(BorderLayout.NORTH,getToolBar());
          contentPane.add(BorderLayout.CENTER,centerPanel);


          pack();

      }
//------------------------------------------------------------------------------
   void displayAttributes(){

     int  row = table.getSelectedRow();
     if (row >= 0  && row < resourceBuffer.getRowCount()) {
        attributesTableModel.setFact(resourceBuffer.getAttributesOf(row));
        reSize();
     }

   }
//------------------------------------------------------------------------------
      public void valueChanged(ListSelectionEvent e) {
          int row;
          if (e.getSource() == table.getSelectionModel() ) {

               row = table.getSelectedRow();
               if (row >= 0  && row < resourceBuffer.getRowCount()) {
                  //bottomPane.setText(resourceBuffer.getFactContent(row));
                  //bottomPane.validate();
                  displayAttributes();

               }
          }

      }

//------------------------------------------------------------------------------
     void reSize(){
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
     }

}
