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



package zeus.agentviewer.protocol;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.agentviewer.*;
import zeus.util.*;


public class ProtocolUI extends ZeusInternalFrame
   implements ListSelectionListener {

   final int TOP_PANE_MIN_HEIGHT = 43;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 100;
   final int BOTTOM_PANE_MIN_HEIGHT = 100;

   private JPanel  contentPane;
   private JTable  protocolTable, strategyTable, attributesTable;



   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;

   ProtocolModel protocolBuffer;
   StrategyModel strategyBuffer;
   AttributesModel attributesBuffer;


//------------------------------------------------------------------------------
    public ProtocolUI(InternalFramesPanel deskTop, ProtocolModel protocolBuffer,
                      StrategyModel strategyBuffer, AttributesModel attributesBuffer) 
    {
        super(" ",true,true,true,true);
        setTitle("Protocols & Strategies:" + (++NUMBER_DISPLAYED));
        String sep = System.getProperty("file.separator");
        String gifpath = SystemProps.getProperty("gif.dir") + "agentviewer" + sep;
        ImageIcon icon = new ImageIcon(gifpath + ViewerNames.PROTOCOL_IMG);
        setFrameIcon(icon);
        this.deskTop = deskTop;
        this.protocolBuffer =protocolBuffer;
        this.strategyBuffer = strategyBuffer;
        this.attributesBuffer = attributesBuffer;
        buildUI();
        deskTop.addInternalFrame(this);
        setVisible(true);
      }
//------------------------------------------------------------------------------
      private void buildUI(){
          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());
          contentPane.setBorder(BorderFactory.createEtchedBorder(Color.gray,Color.black));
          contentPane.add(BorderLayout.NORTH,getProtocolPanel());
          contentPane.add(BorderLayout.CENTER,getStrategyPanel());

          pack();

      }
//------------------------------------------------------------------------------
      JPanel getProtocolPanel(){
         JPanel north =  new JPanel(new BorderLayout());
         north.setBorder(makeBorder("Known Protocols"));

         TableColumnModel tm = new DefaultTableColumnModel();
         TableColumn column;
         column = new TableColumn(ProtocolModel.TYPE,24);
         column.setHeaderValue(protocolBuffer.getColumnName(ProtocolModel.TYPE));
         tm.addColumn(column);
         column = new TableColumn(ProtocolModel.PROTOCOL,24);
         column.setCellRenderer(new FriendlyRenderer());
         column.setHeaderValue(protocolBuffer.getColumnName(ProtocolModel.PROTOCOL));
         tm.addColumn(column);
         column = new TableColumn(ProtocolModel.STATE,8);
         column.setCellRenderer(new CheckBoxCellRenderer());
         column.setHeaderValue(protocolBuffer.getColumnName(ProtocolModel.STATE));
         tm.addColumn(column);

	 protocolTable = new JTable(protocolBuffer,tm);
         protocolTable.setPreferredScrollableViewportSize(
            new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT));
         protocolTable.getTableHeader().setReorderingAllowed(false);
         //protocolTable.setColumnSelectionAllowed(false);
         JScrollPane protocolTableSP = new JScrollPane(protocolTable);
         protocolTable.getSelectionModel().addListSelectionListener(this );
         protocolTable.getSelectionModel().setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
         north.add(BorderLayout.CENTER,protocolTableSP);

         TableColumn stateColumn = protocolTable.getColumnModel().getColumn(1);
         TableColumn protocolColumn = protocolTable.getColumnModel().getColumn(0);
         protocolColumn.setPreferredWidth(stateColumn.getPreferredWidth()*3);


         return north;
      }
//------------------------------------------------------------------------------
      JPanel getStrategyPanel(){
          JPanel center =  new JPanel(new GridLayout(2,1,2,6));
          center.setBorder(makeBorder("Coordination Strategies"));

          TableColumnModel tm = new DefaultTableColumnModel();
          TableColumn column = new TableColumn(StrategyModel.MODE,4);
          column.setCellRenderer(new CheckBoxCellRenderer());
          column.setHeaderValue(strategyBuffer.getColumnName(StrategyModel.MODE));
          tm.addColumn(column);

          column = new TableColumn(StrategyModel.AGENTS,24);
          column.setCellRenderer(new StringArrayCellRenderer());
          column.setHeaderValue(strategyBuffer.getColumnName(StrategyModel.AGENTS));
          tm.addColumn(column);

          column = new TableColumn(StrategyModel.RELATIONS,24);
          column.setCellRenderer(new StringArrayCellRenderer());
          column.setHeaderValue(strategyBuffer.getColumnName(StrategyModel.RELATIONS));
          tm.addColumn(column);

          column = new TableColumn(StrategyModel.STRATEGY,24);
          column.setCellRenderer(new FriendlyRenderer());
          column.setHeaderValue(strategyBuffer.getColumnName(StrategyModel.STRATEGY));
          tm.addColumn(column);

          column = new TableColumn(StrategyModel.FACT,12);
          column.setHeaderValue(strategyBuffer.getColumnName(StrategyModel.FACT));
          tm.addColumn(column);

          column = new TableColumn(StrategyModel.PARAMETERS,24);
          column.setCellRenderer(new HashtableCellRenderer());
          column.setHeaderValue(strategyBuffer.getColumnName(StrategyModel.PARAMETERS));
          tm.addColumn(column);


          strategyTable = new JTable(strategyBuffer,tm);
          strategyTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH/2, TOP_PANE_MIN_HEIGHT));

	  strategyTable.getTableHeader().setReorderingAllowed(false);
          //strategyTable.setColumnSelectionAllowed(false);
	  JScrollPane strategyTableSP = new JScrollPane(strategyTable);
          strategyTable.getSelectionModel().addListSelectionListener(this );
          strategyTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          strategyTable.setBorder(BorderFactory.createLineBorder(Color.black));
          center.add(strategyTableSP);


          attributesTable = new JTable(attributesBuffer);
          attributesTable.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH/2, TOP_PANE_MIN_HEIGHT));
          JScrollPane attributesTableSP = new JScrollPane(attributesTable);
          attributesTable.setBorder(BorderFactory.createLineBorder(Color.black));
          center.add(attributesTableSP);

          return center;
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
      public void valueChanged(ListSelectionEvent e) {
          int selectedRow;
          if (e.getSource() == protocolTable.getSelectionModel() ) {

               selectedRow = protocolTable.getSelectedRow();
               if (selectedRow >= 0  && selectedRow < protocolBuffer.getRowCount()) {
                    //System.out.println("p:row: " + selectedRow);
		    strategyBuffer.setStrategies(protocolBuffer.getStrategyInfos(selectedRow));
                    attributesBuffer.setFact(strategyBuffer.getFact(0));
               }
          }
          else if (e.getSource() == strategyTable.getSelectionModel() ) {
              selectedRow = strategyTable.getSelectedRow();
              //System.out.println("s:row: " + selectedRow);
              if (selectedRow >= 0  && selectedRow < strategyBuffer.getRowCount()) {
		   attributesBuffer.setFact(strategyBuffer.getFact(selectedRow));
               }
          }
      }

//--------------------------------------------------------------------------
     void reSize(){
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
     }
}
//--------------------------------------------------------------------------
class StringArrayCellRenderer extends DefaultTableCellRenderer {
     public void setValue(Object value) {
        String text = Misc.concat((String[])value);
        super.setValue(text);
     }
  }

//-------------------------------------------------------------------------
class HashtableCellRenderer extends DefaultTableCellRenderer {
     public void setValue(Object input) {
        if ( input == null )
           super.setValue(input);
        else {
           Hashtable table = (Hashtable)input;
           Enumeration enum = table.keys();
           String key, value;
           String result = "";
           while( enum.hasMoreElements() ) {
              key = (String)enum.nextElement();
              value = (String)table.get(key);
              result += key + "=" + value + " ";
           }
           super.setValue(result.trim());
        }
     }
}
//--------------------------------------------------------------------------
class FriendlyRenderer extends DefaultTableCellRenderer {
     public void setValue(Object value) {
        if ( value == null )
           super.setValue(value);
        else {
           String name = SystemProps.getProperty("friendly.name." + value, 
              (String)value);
           super.setValue(name);
        }
     }
}
//--------------------------------------------------------------------------
class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

     public CheckBoxCellRenderer() {
        setHorizontalAlignment(JCheckBox.CENTER);
     }

     public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        if ( value != null )
           this.setSelected(((Boolean)value).booleanValue());
        return this;
     }
}
