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



package zeus.generator.task.rulebase;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.* ;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.util.*;
import zeus.rete.Action;
import zeus.gui.fields.*;


public class RuleUI  extends JPanel
                     implements  ListSelectionListener,
                                 FocusListener,
                                 ActionListener  {

   private JPanel  contentPane;
   private JTable  ruleTable;
   private JScrollPane ruleSP,lhsSP,rhsSP;
   private JTextArea lhsArea,rhsArea;
   private RuleModel ruleBuffer;
   private JComboBox predicateList, actionList, functionList;
   private JButton predicateBtn, actionBtn, functionBtn;
   protected FactPanel factPanel;
   protected boolean lhsFocus, rhsFocus;
   protected JMenuItem fsave, fexit;
   protected OntologyDb db = null;
   protected ReteKB kb = null;

   private final int WIDTH = 640;
   private final int HEIGHT = 480;
   private final int LWIDTH = 300;
   private final int LHEIGHT = 250;
   private  JFrame frame;

   protected String[] precedenceWds = zeus.rete.Action.types;
   protected String[] booleanWds = {"not","test"};
   protected Vector   methodValues;

   protected SimpleAttributeSet boolFmt, preFmt, methFmt, plainFmt;

//---------------------------------------------------------------------------
      public RuleUI(OntologyDb db, Editor editor,
                    ReteKB kb, Vector methodValues) {

         this.methodValues = methodValues;
         this.db = db;
         this.kb = kb;

         ruleBuffer = new RuleModel(kb,db);
         ruleBuffer.addChangeListener(editor);

         GridBagLayout gridBagLayout = new GridBagLayout();
         GridBagConstraints gbc = new GridBagConstraints();
         setLayout(gridBagLayout);
         setBackground(Color.lightGray);
         setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

	 // Add the control panel
         ControlPanel controlPane =
            new ControlPanel(editor,"Rulebase Editor",true,true);
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,8,8);
         gbc.weightx = gbc.weighty = 0;
         gridBagLayout.setConstraints(controlPane,gbc);
         add(controlPane);

         // Add the panel that will contain the task's nodes
         JPanel dataPanel = new JPanel();
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.BOTH;
         gbc.weightx = gbc.weighty = 1;
         gbc.insets = new Insets(8,8,8,8);
         gridBagLayout.setConstraints(dataPanel,gbc);
         add(dataPanel);

         gridBagLayout = new GridBagLayout();
         gbc = new GridBagConstraints();
         dataPanel.setLayout(gridBagLayout);
         dataPanel.setBackground(Color.lightGray);
//         dataPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

         JToolBar toolbar = new RuleToolBar();
	 gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.NONE;
         gbc.insets = new Insets(0,8,0,0);
         gbc.weightx = gbc.weighty = 0;
         gridBagLayout.setConstraints(toolbar,gbc);
         dataPanel.add(toolbar);

     // Create data info area     
         TableColumnModel tm = new DefaultTableColumnModel();     
         TableColumn column = new TableColumn(RuleModel.RULE,12);
         column.setHeaderValue(ruleBuffer.getColumnName(RuleModel.RULE));
         tm.addColumn(column);
         column = new TableColumn(RuleModel.PRIORITY,12,     
            new DefaultTableCellRenderer(),
            new DefaultCellEditor(new WholeNumberField(zeus.rete.Rule.MIN_SALIENCE,
               zeus.rete.Rule.MAX_SALIENCE)));
         column.setHeaderValue(ruleBuffer.getColumnName(RuleModel.PRIORITY));
         tm.addColumn(column);
    
         ruleTable = new JTable(ruleBuffer,tm);
         ruleTable.getTableHeader().setReorderingAllowed(false);
         ruleTable.setColumnSelectionAllowed(false);

         ruleSP = new JScrollPane(ruleTable);
         ruleSP.setPreferredSize(new Dimension(400,150));
         ruleTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         ruleTable.getSelectionModel().addListSelectionListener(this);

         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.weightx = gbc.weighty = 0;
         gbc.insets = new Insets(8,8,0,8);
         gridBagLayout.setConstraints(ruleSP,gbc);
         dataPanel.add(ruleSP);

         JPanel panel = getIfThenPanel();
	 gbc.gridwidth = 1;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.BOTH;
         gbc.weightx = gbc.weighty = 1;
         gbc.insets = new Insets(8,8,8,0);
         gridBagLayout.setConstraints(panel,gbc);
         dataPanel.add(panel);

         panel = getUtilitiesPanel();
	 gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.VERTICAL;
         gbc.weightx = gbc.weighty = 0;
         gbc.insets = new Insets(8,8,8,8);
         gridBagLayout.setConstraints(panel,gbc);
         dataPanel.add(panel);

	 lhsFocus = false;
         rhsFocus  = false;
//         setTextFormats();
//         setPreferredSize(new Dimension(WIDTH,HEIGHT));
      }

//---------------------------------------------------------------------------
      public void save() {
	 kb.setRules(ruleBuffer.getData());
      }
//---------------------------------------------------------------------------
      void setTextFormats(){
          boolFmt = new SimpleAttributeSet();
          StyleConstants.setForeground(boolFmt,Color.red);
          preFmt = new SimpleAttributeSet();
          StyleConstants.setForeground(preFmt,Color.blue);
          methFmt = new SimpleAttributeSet();
          StyleConstants.setForeground(methFmt,Color.green);
          plainFmt = new SimpleAttributeSet();
          StyleConstants.setForeground(plainFmt,Color.black);

      }
//---------------------------------------------------------------------------
      void createFrame(){
        frame = new JFrame("Rule Editor");
        frame.getContentPane().add(this);
        frame.addWindowListener(
           new WindowAdapter() {
              public void windowClosing(WindowEvent evt) { System.exit(0); }
           }
        );
        JMenuBar menuBar = new JMenuBar();
        JMenu fMenu = new JMenu("File");
        menuBar.add(fMenu);
        fsave = new JMenuItem("Save");
        fsave.addActionListener(this);
        fMenu.add(fsave);
        fexit = new JMenuItem("Exit");
        fexit.addActionListener(this);
        fMenu.add(fexit);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.show();
     }

//---------------------------------------------------------------------------
      class RuleToolBar extends JToolBar implements ActionListener{
        protected JButton       newBtn;
        protected JButton       deleteBtn;

        public RuleToolBar() {
          setBackground(java.awt.Color.lightGray);
          setBorder( new BevelBorder(BevelBorder.LOWERED ) );
          setFloatable(false);

          String sep = System.getProperty("file.separator");
          String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

          // New Button
          newBtn = new JButton(new ImageIcon(path + "new1.gif"));
      	  newBtn.setMargin(new Insets(0,0,0,0));
          add(newBtn);
          newBtn.setToolTipText("New rule");
          newBtn.addActionListener(this);

          // Delete Button
          deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	  deleteBtn.setMargin(new Insets(0,0,0,0));
          add(deleteBtn);
          deleteBtn.setToolTipText("Delete");
          deleteBtn.addActionListener(this);

         }
          public void actionPerformed(ActionEvent e)  {
            Object src = e.getSource();
            if ( src == newBtn ) {
               ruleBuffer.addRule();
            }
            else if ( src == deleteBtn ) {
              int row = ruleTable.getSelectedRow();
              if (row != -1 && row <= ruleBuffer.getRowCount())
                 ruleBuffer.deleteRule(row);
              ruleTable.clearSelection();
              lhsArea.setText("");
              rhsArea.setText("");
            }
          }

      }
//---------------------------------------------------------------------------
      JPanel getUtilitiesPanel() {
         JPanel panel = new JPanel();
         GridBagLayout gridBagLayout = new GridBagLayout();
         GridBagConstraints gbc = new GridBagConstraints();
         panel.setLayout(gridBagLayout);
         panel.setBackground(Color.lightGray);
//         panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

         functionBtn = new JButton("Insert Function");
         functionBtn.addActionListener(this);
         functionBtn.setToolTipText("Click to insert selected function");
	 gbc.gridwidth = 1;
         gbc.anchor = GridBagConstraints.WEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,0,0);
         gridBagLayout.setConstraints(functionBtn,gbc);
         panel.add(functionBtn);

         functionList = new JComboBox(methodValues) {
            public void contentsChanged(ListDataEvent e) {
               selectedItemReminder = null;
               super.contentsChanged(e);
            }
         };
         functionList.addActionListener(this);
         functionList.setForeground(Color.green);
	 gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.WEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,0,0);
         gridBagLayout.setConstraints(functionList,gbc);
         panel.add(functionList);

         predicateBtn = new JButton("Insert Predicate");
         predicateBtn.addActionListener(this);
         predicateBtn.setToolTipText("Click to insert selected predicate");
	 gbc.gridwidth = 1;
         gbc.anchor = GridBagConstraints.WEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,0,0);
         gridBagLayout.setConstraints(predicateBtn,gbc);
         panel.add(predicateBtn);

         predicateList = new JComboBox(booleanWds) {
            public void contentsChanged(ListDataEvent e) {
               selectedItemReminder = null;
               super.contentsChanged(e);
            }
         };
         predicateList.addActionListener(this);
         functionList.setForeground(Color.red);
	 gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.WEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,0,0);
         gridBagLayout.setConstraints(predicateList,gbc);
         panel.add(predicateList);

         actionBtn = new JButton("Insert Action");
         actionBtn.addActionListener(this);
         actionBtn.setToolTipText("Click to insert selected action");
	 gbc.gridwidth = 1;
         gbc.anchor = GridBagConstraints.WEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,0,0);
         gridBagLayout.setConstraints(actionBtn,gbc);
         panel.add(actionBtn);

         actionList = new JComboBox(precedenceWds) {
            public void contentsChanged(ListDataEvent e) {
               selectedItemReminder = null;
               super.contentsChanged(e);
            }
         };
         actionList.addActionListener(this);
         functionList.setForeground(Color.blue);
	 gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.WEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.insets = new Insets(8,8,0,0);
         gridBagLayout.setConstraints(actionList,gbc);
         panel.add(actionList);

	 factPanel= new FactPanel(this,db);
//         factPanel.setPreferredSize(new Dimension(LWIDTH,LHEIGHT+300));
         factPanel.setBorder(makeBorder("Ontology"));

	 gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.BOTH;
         gbc.weightx = gbc.weighty = 1;
         gbc.insets = new Insets(8,0,0,0);
         gridBagLayout.setConstraints(factPanel,gbc);
         panel.add(factPanel);

         return panel;
      }
//---------------------------------------------------------------------------
      JTextArea getLastTextAreaWithFocus() {
          if (lhsFocus)
            return lhsArea;
          else if (rhsFocus)
            return rhsArea;
          else
            return null;
      }
//---------------------------------------------------------------------------
      void setFocus(JTextArea textarea){

           if (textarea == lhsArea) {
             lhsFocus = true;
             rhsFocus = false;
           }
           else if (textarea == rhsArea) {
             rhsFocus = true;
             lhsFocus = false;
           }
      }
//---------------------------------------------------------------------------
      void appendTextTo(JTextArea textarea, String text, boolean keyword){
           int pos = textarea.getCaretPosition();
           Document doc = null;

           if (textarea == lhsArea) {
              doc = lhsArea.getDocument();
              insert(doc,text,pos);
           }
           else if (textarea == rhsArea) {
             doc = rhsArea.getDocument();
             insert(doc,text,pos);
           }

           if(keyword && !methodValues.contains(text))
             textarea.setCaretPosition(textarea.getCaretPosition()-1);
           else {
             textarea.setCaretPosition(textarea.getDocument().getLength());
             try {
               doc.insertString(textarea.getCaretPosition(),"\n",null);
             }
             catch (BadLocationException e) {
//               e.printStackTrace();
             }
           }
      }
//---------------------------------------------------------------------------
      void insert(Document doc, String text, int pos) {

          try {
           if (Misc.member(text,booleanWds)) {
              doc.insertString(pos,"(" + text + " )",null);
           }
           else if (Misc.member(text,precedenceWds)) {
              if ( text.equals(Action.types[Action.MESSAGE]) ) {
                 text += " ";
                 for(int i = 0; i < Performative.ATTRIBUTE_TYPES.length; i++ )
                    text += "(" + Performative.ATTRIBUTE_TYPES[i] + " " +
		             Fact.V_STR + db.GenSym().plainId("var") + ")";
                 doc.insertString(pos,"(" + text + ")",null);
              }
              else if ( text.equals(Action.types[Action.ACHIEVE]) ||
                        text.equals(Action.types[Action.BUY]) ||
                        text.equals(Action.types[Action.SELL]) ) {
                 text += " ";
                 for(int i = 0; i < OntologyDb.GOAL_ATTRIBUTES.length; i++ )
                    text += "(" + OntologyDb.GOAL_ATTRIBUTES[i] + " " +
		             Fact.V_STR + db.GenSym().plainId("var") + ")";
                 doc.insertString(pos,"(" + text + ")",null);
              }
              else if ( text.equals(Action.types[Action.IF]) )
                  doc.insertString(pos,"(" + text + "  \n then\n\n else\n\n)",null);
              else if ( text.equals(Action.types[Action.WHILE]) )
                  doc.insertString(pos,"(" + text + "  \n do\n\n)",null);
              else
                 doc.insertString(pos,"(" + text + " )",null);
           }
           else if (methodValues.contains(text))
              doc.insertString(pos,text,null);
           else {
              doc.insertString(pos,text,null);
           }
          }
          catch (BadLocationException e) {
          }
      }

//---------------------------------------------------------------------------
      void appendTextTo(String text){
         JTextArea textarea = getLastTextAreaWithFocus();
         if (textarea != null) {
             appendTextTo(textarea,text,false);
         }
      }
      public void actionPerformed(ActionEvent e){
         Object source = e.getSource();
         String value = null;
         JTextArea textarea;

         if (source == fsave) {
            writeRulesToFile();
         }
         else if (source == fexit) {
            System.exit(0);
         }
         else if ( source == predicateList || source == predicateBtn ||
	           source == functionList  || source == functionBtn  ||
		   source == actionList    || source == actionBtn ) {
            textarea = getLastTextAreaWithFocus();
            if (textarea != null) {
               if (source == predicateList || source == predicateBtn )
                  value = (String)predicateList.getSelectedItem();
               else if (source == actionList || source == actionBtn )
                  value = (String)actionList.getSelectedItem();
               else if (source == functionList || source == functionBtn )
                  value = (String)functionList.getSelectedItem();
               if ( value != null) {
                 if (Misc.member(value,precedenceWds) && textarea != rhsArea)
                    return;
                 appendTextTo(textarea,value,true);
              }
            }
         }
      }
//---------------------------------------------------------------------------
      public void focusGained(FocusEvent e) {
           JTextArea textarea = (JTextArea) e.getSource();
           setFocus(textarea);
      }
//---------------------------------------------------------------------------
      public void focusLost(FocusEvent e) {
      }
//---------------------------------------------------------------------------
      JPanel getIfThenPanel(){
         JPanel panel = new JPanel(new GridLayout(2,1,5,5));
         panel.setBackground(Color.lightGray);

         lhsArea = new JTextArea(new PlainDocument(),"",6,80);
         lhsSP = new JScrollPane(lhsArea);
         lhsSP.setBorder(makeBorder("Conditions"));
         lhsArea.addFocusListener(this);
         lhsSP.setPreferredSize(new Dimension(500,100));
         panel.add(lhsSP);

         rhsArea = new JTextArea(new PlainDocument(),"",6,80);
         rhsSP = new JScrollPane(rhsArea);
         rhsSP.setBorder(makeBorder("Actions"));
         rhsSP.setPreferredSize(new Dimension(500,100));
         rhsArea.addFocusListener(this);
         panel.add(rhsSP);

         lhsArea.setLineWrap(true);
         lhsArea.setWrapStyleWord(true);
         rhsArea.setLineWrap(true);
         rhsArea.setWrapStyleWord(true);

	 return panel;
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
     void writeRulesToFile(){
         Rule rule;
         String fname;
         File f1 = null;
         String fsep = System.getProperty("file.separator");
         String fdir = SystemProps.getProperty("zeus.dir") + "rete" + fsep + "clp" + fsep;
         String image = SystemProps.getProperty("gir.dir") + "generator" + "kb.gif";

         JFileChooser chooser = new JFileChooser(new File(fdir));
         //chooser.addChoosableFileType("Rule bases (*.kb)", "kb", new ImageIcon(image));
         int val = chooser.showSaveDialog(frame);
         if (val == JFileChooser.APPROVE_OPTION )
          f1 = chooser.getSelectedFile();

         if (f1 == null) {
            JOptionPane.showMessageDialog(null,"File hasn't been specified","Error",JOptionPane.ERROR_MESSAGE);
            return;
         }
         try {

	   PrintWriter out = new PrintWriter(new FileWriter(f1));
           Vector rules = ruleBuffer.getRules();
           for(int i=0;i < rules.size(); i++) {
              rule = (Rule) rules.elementAt(i);
              printRule(rule,out);
           }
           out.flush();
	   out.close();
         }
         catch(IOException e) {
	   e.printStackTrace();
         }
     }
//---------------------------------------------------------------------------
     void printRule(Rule rule, PrintWriter out) {
         out.println("   (" + rule.name );
         out.println();
         out.println(rule.getCondition().trim());
         out.println("=>");
         out.println(rule.getConclusion().trim());
         out.println();
         out.println("   )");
         out.println(); out.println();
     }
//---------------------------------------------------------------------------
      public void valueChanged(ListSelectionEvent e) {
          int row;
          if (e.getSource() == ruleTable.getSelectionModel() ) {
             row = ruleTable.getSelectedRow();
             if (row >= 0  && row < ruleBuffer.getRowCount()) {
              lhsArea.setDocument(ruleBuffer.getRule(row).getLHS());
              rhsArea.setDocument(ruleBuffer.getRule(row).getRHS());
             }
          }
      }
//---------------------------------------------------------------------------
}
