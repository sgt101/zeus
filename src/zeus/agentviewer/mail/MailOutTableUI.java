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



package zeus.agentviewer.mail;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.* ;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.agentviewer.*;
import zeus.util.*;
import zeus.concepts.Performative;

public class MailOutTableUI extends ZeusInternalFrame 
   implements ListSelectionListener  {

   private JPanel  contentPane;
   private JTable  table;
   private JTextArea bottomPane;
   private JScrollPane topPane;
   private JSplitPane splitPane;

   final int TOP_PANE_MIN_HEIGHT = 120;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_HEIGHT = 100;


   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;
   MailOutTableModel mailOutBuffer;
   JTextArea  replyWithTF, inReplyToTF, receiveTimeTF,
              ontologyTF, languageTF, contentTF, sendTimeTF;

//---------------------------------------------------------------------------
      public MailOutTableUI(InternalFramesPanel deskTop,
                            MailOutTableModel mailOutBuffer) {

        super(" ",true,true,true,true);
        setTitle("Mail Out:" + (++NUMBER_DISPLAYED));
        String sep = System.getProperty("file.separator");
        String gifpath = SystemProps.getProperty("gif.dir") + "agentviewer" + sep;
        ImageIcon icon = new ImageIcon(gifpath + ViewerNames.MAILOUT_IMG);
        setFrameIcon(icon);
        this.deskTop = deskTop;
        this.mailOutBuffer =mailOutBuffer;
        buildUI();
        deskTop.addInternalFrame(this);
        setVisible(true);
      }

//---------------------------------------------------------------------------
      private void buildUI(){

          table = new JTable(mailOutBuffer);
          table.setPreferredScrollableViewportSize(new Dimension(TOP_PANE_MIN_WIDTH,
                                                                 TOP_PANE_MIN_HEIGHT));
          topPane = new JScrollPane(table);
          table.getSelectionModel().addListSelectionListener(this );
          table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          table.setShowGrid(false);
          TableColumn toColumn = table.getColumnModel().getColumn(0);
          TableColumn subjectColumn = table.getColumnModel().getColumn(1);
          subjectColumn.setPreferredWidth(toColumn.getPreferredWidth()*3);

          bottomPane = new JTextArea();
          bottomPane.setEditable(false);
          bottomPane.setLineWrap(true);
          bottomPane.setWrapStyleWord(true);
          bottomPane.setPreferredSize(new Dimension(BOTTOM_PANE_MIN_WIDTH,
                                                    BOTTOM_PANE_MIN_HEIGHT));
          //JScrollPane bottomSP = new JScrollPane(bottomPane);
          JScrollPane bottomSP = new JScrollPane(getBottomPanel());
          bottomSP.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.gray));

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
          contentPane.add(topPane);
          contentPane.add(bottomSP);

          pack();

      }
//---------------------------------------------------------------------------
      private TitledBorder makeBorder(String title){
          TitledBorder border = (BorderFactory.createTitledBorder(title));
          border.setTitlePosition(TitledBorder.TOP);
	  border.setTitleJustification(TitledBorder.RIGHT);
	  border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
	  border.setTitleColor(Color.black);

          return border;

     }
//---------------------------------------------------------------------------
      JPanel getBottomPanel(){
          JLabel label;
          JScrollPane sp;

          GridBagLayout gb = new GridBagLayout();
          GridBagConstraints gbc = new GridBagConstraints();

          JPanel panel = new JPanel();
          panel.setLayout(gb);

          label = new JLabel("Reply With ");
          gbc.anchor = GridBagConstraints.NORTHWEST;
          gbc.insets = new Insets(0,5,0,0);
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.fill = GridBagConstraints.NONE;
          gb.setConstraints(label,gbc);
          panel.add(label);

          replyWithTF = new JTextArea();
          replyWithTF.setEditable(false);
          replyWithTF.setLineWrap(true);
          replyWithTF.setWrapStyleWord(true);
          replyWithTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.insets = new Insets(0,5,0,0);
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          sp = new JScrollPane(replyWithTF);
          sp.setPreferredSize(new Dimension(10,35));
          gb.setConstraints(sp,gbc);
          panel.add(sp);

          label = new JLabel("In Reply To ");
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.fill = GridBagConstraints.NONE;
          gbc.insets = new Insets(5,5,0,0);
          gb.setConstraints(label,gbc);
          panel.add(label);

          inReplyToTF = new JTextArea();
          inReplyToTF.setEditable(false);
          inReplyToTF.setLineWrap(true);
          inReplyToTF.setWrapStyleWord(true);
          inReplyToTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.insets = new Insets(5,5,0,0);
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          sp = new JScrollPane(inReplyToTF);
          sp.setPreferredSize(new Dimension(10,35));
          gb.setConstraints(sp,gbc);
          panel.add(sp);

          label = new JLabel("Content ");
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.fill = GridBagConstraints.NONE;
          gbc.insets = new Insets(5,5,0,0);
          gb.setConstraints(label,gbc);
          panel.add(label);

          contentTF =  new JTextArea();
          contentTF.setEditable(false);
          contentTF.setLineWrap(true);
          contentTF.setWrapStyleWord(true);
          contentTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.BOTH;
          gbc.insets = new Insets(5,5,0,0);
          sp = new JScrollPane(contentTF);
          sp.setPreferredSize(new Dimension(10,70));
          gb.setConstraints(sp,gbc);
          panel.add(sp);

          label = new JLabel("Sent ");
          gbc.anchor = GridBagConstraints.NORTHWEST;
          gbc.insets = new Insets(5,5,0,0);
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.fill = GridBagConstraints.NONE;
          gb.setConstraints(label,gbc);
          panel.add(label);

          sendTimeTF = new JTextArea();
          sendTimeTF.setEditable(false);
          sendTimeTF.setLineWrap(true);
          sendTimeTF.setWrapStyleWord(true);
          sendTimeTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.insets = new Insets(5,5,0,0);
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          sp = new JScrollPane(sendTimeTF);
          sp.setPreferredSize(new Dimension(10,35));
          gb.setConstraints(sp,gbc);
          panel.add(sp);

          label = new JLabel("Recieved ");
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.insets = new Insets(5,5,0,0);
          gbc.fill = GridBagConstraints.NONE;
          gb.setConstraints(label,gbc);
          panel.add(label);

          receiveTimeTF = new JTextArea();
          receiveTimeTF.setEditable(false);
          receiveTimeTF.setLineWrap(true);
          receiveTimeTF.setWrapStyleWord(true);
          receiveTimeTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.insets = new Insets(5,5,0,0);
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          sp = new JScrollPane(receiveTimeTF);
          sp.setPreferredSize(new Dimension(10,35));
          gb.setConstraints(sp,gbc);
          panel.add(sp);

          label = new  JLabel("Ontology ");
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.fill = GridBagConstraints.NONE;
          gbc.insets = new Insets(5,5,0,0);
          gb.setConstraints(label,gbc);
          //panel.add(label);

          ontologyTF  = new JTextArea();
          ontologyTF.setEditable(false);
          ontologyTF.setLineWrap(true);
          ontologyTF.setWrapStyleWord(true);
          ontologyTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.insets = new Insets(5,5,0,0);
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          sp = new JScrollPane(ontologyTF);
          sp.setPreferredSize(new Dimension(10,35));
          gb.setConstraints(sp,gbc);
          //panel.add(sp);

          label = new JLabel("Language ");
          gbc.gridwidth = 1;
          gbc.weightx = 0;
          gbc.fill = GridBagConstraints.NONE;
          gbc.insets = new Insets(5,5,0,0);
          gb.setConstraints(label,gbc);
          //panel.add(label);

          languageTF = new JTextArea();
          languageTF.setEditable(false);
          languageTF.setLineWrap(true);
          languageTF.setWrapStyleWord(true);
          languageTF.setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.gray));
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.insets = new Insets(5,5,0,0);
          gbc.weightx = gbc.weighty= 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          sp = new JScrollPane(languageTF);
          sp.setPreferredSize(new Dimension(10,35));
          gb.setConstraints(sp,gbc);
          //panel.add(sp);

          return panel;
      }


//---------------------------------------------------------------------------
      void setMailFields(int row){
           Performative msg = mailOutBuffer.getMessage(row);

           replyWithTF.setText(msg.getReplyWith());
           inReplyToTF.setText(msg.getInReplyTo());
           contentTF.setText(msg.getContent());

           //ontologyTF.setText(msg.getOntology());
           //languageTF.setText(msg.getLanguage());

	   Object obj = msg.getSendTime();
           if ( obj != null )
	      sendTimeTF.setText(obj.toString());
           else
              sendTimeTF.setText(null);

	   obj = msg.getReceiveTime();
           if ( obj != null )
	      receiveTimeTF.setText(obj.toString());
           else
              receiveTimeTF.setText(null);
     }

//---------------------------------------------------------------------------
      public void valueChanged(ListSelectionEvent e) {
          int selectedRow;
          if (e.getSource() == table.getSelectionModel() ) {

               selectedRow = table.getSelectedRow();
               if (selectedRow >= 0  && selectedRow < mailOutBuffer.getRowCount()) {
                  //bottomPane.setText(mailOutBuffer.getMailContent(selectedRow));
                  setMailFields(selectedRow);
                  validate();

               }
          }

      }

//---------------------------------------------------------------------------
     void reSize(){
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
     }

}
