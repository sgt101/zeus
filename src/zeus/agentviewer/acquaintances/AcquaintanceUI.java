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



package zeus.agentviewer.acquaintances;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import zeus.agentviewer.*;
import zeus.util.*;




public class AcquaintanceUI extends ZeusInternalFrame implements ListSelectionListener {
   String fsep = System.getProperty("file.separator");
   String IMAGEPATH = SystemProps.getProperty("gif.dir") + "agentviewer" + fsep;

   private JPanel  contentPane;
   private JTable  relationsTable,abilitiesTable, attributesTable;

   private AbilitiesTableModel abilitiesTableModel;
   private RelationsTableModel relationsTableModel;
   private AttributesTableModel attributesTableModel;
   private JScrollPane relationsSP, abilitiesSP, attributesSP;

   final int IMGw = 20;
   final int IMGh = 20;
   final int TABLE_HEIGHT = 10;
   final int TABLE_WIDTH = 200;
   final int FIRST_ROW =0;
   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;
   private TitledBorder border;




//------------------------------------------------------------------------------
   public AcquaintanceUI(InternalFramesPanel deskTop, RelationsTableModel relationsBuffer,
                                                      AbilitiesTableModel abilitiesBuffer,
                                                      AttributesTableModel attributesBuffer)
   {
         super(" ",true,true,true,true);
         setTitle("Aquaintance Database: " + (++NUMBER_DISPLAYED) );
         ImageIcon icon = new ImageIcon(IMAGEPATH + ViewerNames.ACQDB_IMG);
         setFrameIcon(icon);
         this.deskTop = deskTop;
         relationsTableModel = relationsBuffer;
         abilitiesTableModel = abilitiesBuffer;
         attributesTableModel = attributesBuffer;
         buildUI();

         deskTop.addInternalFrame(this);
         setVisible(true);
   }
//------------------------------------------------------------------------------
   private TitledBorder makeBorder(String title){
          TitledBorder border = (BorderFactory.createTitledBorder(title));
          border.setTitlePosition(TitledBorder.TOP);
          border.setTitleJustification(TitledBorder.RIGHT);
          border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
          border.setTitleColor(Color.blue);
          return border;

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
   private void buildUI(){

          // create tables
          relationsTable = new JTable(relationsTableModel);
          relationsTable.setColumnSelectionAllowed(false);
          relationsTable.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH,
                                                               TABLE_HEIGHT));
          relationsSP = new JScrollPane(relationsTable);
          relationsTable.setBackground(Color.white);
          relationsTable.getSelectionModel().addListSelectionListener(this );
          relationsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

          abilitiesTable = new JTable(abilitiesTableModel);
          abilitiesTable.setColumnSelectionAllowed(false);
          abilitiesTable.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH,
                                                               TABLE_HEIGHT*2));
          abilitiesSP = new JScrollPane(abilitiesTable);
          abilitiesTable.setBackground(Color.white);
          abilitiesTable.getSelectionModel().addListSelectionListener(this );
          abilitiesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

          //attributes
          attributesTable = new JTable(attributesTableModel);
          attributesTable.setColumnSelectionAllowed(false);
          attributesTable = new JTable(attributesTableModel);
          attributesTable.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH,
                                                               TABLE_HEIGHT*3));
          attributesSP = new JScrollPane(attributesTable);
          attributesTable.setBackground(Color.white);

        // create panels for tables
          JPanel centerPanel = new JPanel();
          centerPanel.setLayout(new GridLayout(3,1,10,15));

          JPanel relationsPanel = new JPanel(new BorderLayout());
          relationsPanel.setBorder(makeBorder("Known Relations"));
          relationsPanel.add(BorderLayout.CENTER,relationsSP);
          JPanel abilitiesPanel = new JPanel(new BorderLayout());
          abilitiesPanel.setBorder(makeBorder("Agent's Abilities"));
          abilitiesPanel.add(BorderLayout.CENTER,abilitiesSP);
          JPanel attributesPanel = new JPanel(new BorderLayout());
          attributesPanel.setBorder(makeBorder("Fact Attributes"));
          attributesPanel.add(BorderLayout.CENTER,attributesSP);

          centerPanel.add(relationsPanel);
          centerPanel.add(abilitiesPanel);
          centerPanel.add(attributesPanel);

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());
          contentPane.add(BorderLayout.CENTER,centerPanel);
          pack();
   }
//------------------------------------------------------------------------------
   void displayAbilities(){
       int selectedRow = relationsTable.getSelectedRow();
       if (selectedRow >= 0  && selectedRow < relationsTableModel.getRowCount()) {
           String name = relationsTableModel.getName(selectedRow);
           if (abilitiesTableModel.hasAbilities(name)== false){
              attributesTableModel.setFact(null);
              return;
           }
           abilitiesTableModel.setAbilitiesof(name);
           attributesTableModel.setFact(abilitiesTableModel.getAttributesof(FIRST_ROW));
           reSize();
       }
   }
//------------------------------------------------------------------------------
   void displayAttributes(){

     int  selectedRow = abilitiesTable.getSelectedRow();
     if (selectedRow >= 0  && selectedRow < abilitiesTableModel.getRowCount()) {
        attributesTableModel.setFact(abilitiesTableModel.getAttributesof(selectedRow));
        reSize();
     }
   }
//------------------------------------------------------------------------------
   public void valueChanged(ListSelectionEvent e) {

          String aValue;
          if (e.getSource() == relationsTable.getSelectionModel() ) {
             displayAbilities();
          }
          else if (e.getSource() == abilitiesTable.getSelectionModel() ) {
             displayAttributes();
          }
   }
//------------------------------------------------------------------------------
   void reSize(){
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
   }

}

