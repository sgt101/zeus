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



package zeus.agentviewer;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.Engine;
import zeus.ontology.*;
import zeus.actors.*;
import zeus.gui.editors.*;
import zeus.gui.fields.*;


public class GoalDialog extends JDialog
                        implements ItemListener,
			           ActionListener,
                                   FactSelector {

   final int TOP_PANE_MIN_HEIGHT = 150;
   final int TOP_PANE_MIN_WIDTH = 400;    //155;

   public static final String[] FORMAT_TYPE = {
      "popup",
      "email",
      "phone",
      "voice"
   };

   public static final boolean BASIC = true;
   public static final boolean EXTENDED = false;

   protected JLabel gLabel, fLabel, aLabel, eLabel, rLabel,
                   sLabel, cLabel, tLabel, dLabel, pLabel,
                   nLabel, slaLabel, cmLabel;
   protected JTextField fTextF, gTextF, dTextF, slaTextF;
   protected WholeNumberField cTextF, eTextF, cmTextF, sTextF, nTextF, pTextF;
   protected JButton browseBtn, browseAgentBtn = null;
   protected JComboBox typeChooser, goalType;
   protected JList agentDialog = null;
   protected OntologyDb ontology = null;
   protected Engine engine = null;
   protected Fact fact = null;
   protected String[] agents = null;
   protected JButton okButton;
   protected JButton cancelButton;

   protected int              nFormat = FORMAT_TYPE.length;
   protected LocalCheckBox[]  _cb     = new LocalCheckBox[nFormat];
   protected JTextField[]     _cbTf   = new JTextField[nFormat];
   protected boolean          type    = BASIC;

   protected Vector sla = new Vector();
   protected Vector sla_data = new Vector();
   protected String II_key = null, IC_key = null;
   protected String WMP_key = null;
   JScrollPane topPane;
   JTable table;
   JPanel contentPane;
   FactTableModel gdModel;

   AgentContext context;


   public GoalDialog(Frame parent, boolean type,
                 AgentContext context) {

      super(parent," Enter Goal Dialog",false);
      this.context = context;
      set(context.Engine());
      set(context.OntologyDb());
      gdModel = new FactTableModel(ontology);

      contentPane = (JPanel) getContentPane();
      contentPane.setLayout(new BorderLayout());
      this.type = type;



      JPanel p1 = new JPanel();
      p1.setLayout(new GridLayout(1,2,10,10));
      okButton = new JButton("Ok");
      okButton.setForeground(Color.black);
      okButton.setFont(new  Font("Helvetica", Font.BOLD, 14));
      cancelButton = new JButton("Cancel");
      cancelButton.setForeground(Color.black);
      cancelButton.setFont(new  Font("Helvetica", Font.BOLD, 14));
      p1.add(okButton);
      p1.add(cancelButton);

      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(10,0,10,0);
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = 1;

      JPanel btnPanel = new JPanel();
      btnPanel.setLayout(gb);

      gbc.anchor = GridBagConstraints.CENTER;
      gbc.fill = GridBagConstraints.NONE;
      gbc.insets = new Insets(10,10,10,10);
      gb.setConstraints(p1,gbc);
      btnPanel.add(p1);



      JPanel outerBtn = new JPanel(new BorderLayout());
      outerBtn.add(BorderLayout.NORTH,new JSeparator(SwingConstants.HORIZONTAL));
      outerBtn.add(BorderLayout.CENTER,btnPanel);
      contentPane.add(BorderLayout.SOUTH,outerBtn);

      JPanel p2 = new JPanel();
      p2.setLayout(gb);


      gbc.insets = new Insets(10,10,0,0);
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.fill = GridBagConstraints.NONE;

      gLabel = new JLabel("Goal:");
      gbc.gridwidth = 1;
      gb.setConstraints(gLabel,gbc);
      p2.add(gLabel);

      gTextF = new JTextField(15);
      gTextF.setEditable(false);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = 1;
      gb.setConstraints(gTextF,gbc);
      p2.add(gTextF);

      gbc.weightx = 0;

/*
      tLabel = new JLabel("Type:");
      gbc.gridwidth = 1;
      gb.setConstraints(tLabel,gbc);
      p2.add(tLabel);

      String[] choiceTypes = {"discrete"} // ,"continuous"};
      typeChooser = new JComboBox(choiceTypes);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(typeChooser,gbc);
      p2.add(typeChooser);
*/

      fLabel = new JLabel("Fact:");
      gbc.gridwidth = 1;
      gb.setConstraints(fLabel,gbc);
      p2.add(fLabel);

      String[] goalTypes = {"achieve","sell","buy"}; //, "enact"};
      goalType = new JComboBox(goalTypes);
      gb.setConstraints(goalType,gbc);
      p2.add(goalType);

      fTextF = new JTextField(15);
      gbc.gridwidth = 1;
      gb.setConstraints(fTextF,gbc);
      p2.add(fTextF);
      fTextF.setEditable(false);

      browseBtn = new JButton("Browse...");
      gbc.anchor = GridBagConstraints.WEST;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(browseBtn,gbc);
      p2.add(browseBtn);

      aLabel = new JLabel("Attributes:");
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridwidth = 1;
      gb.setConstraints(aLabel,gbc);
      p2.add(aLabel);


//----------------- table

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     column = new TableColumn(FactTableModel.ATTRIBUTE,12);
     column.setHeaderValue(gdModel.getColumnName(FactTableModel.ATTRIBUTE));
     tm.addColumn(column);
     ExpressionCellEditor editor = new ExpressionCellEditor(gdModel);
     column = new TableColumn(FactTableModel.VALUE,24,
        new ValidatingCellRenderer(gdModel,FactTableModel.VALUE),editor);
     column.setHeaderValue(gdModel.getColumnName(FactTableModel.VALUE));
     tm.addColumn(column);

     table = new JTable(gdModel,tm);
     table.getTableHeader().setReorderingAllowed(false);
     table.setColumnSelectionAllowed(false);


      topPane = new JScrollPane(table);
      table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      topPane.setPreferredSize(new Dimension(TOP_PANE_MIN_WIDTH,
                                                                 TOP_PANE_MIN_HEIGHT));


      gbc.insets = new Insets(10,10,10,10);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(topPane,gbc);
      p2.add(topPane);

/*
      sLabel = new JLabel("Start Time:");
      gbc.insets = new Insets(0,10,0,0);
      gbc.gridwidth = 1;
      gb.setConstraints(sLabel,gbc);
      p2.add(sLabel);

      sTextF = new WholeNumberField();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(sTextF,gbc);
      p2.add(sTextF);
*/

      gbc.insets = new Insets(10,10,0,0);
      eLabel = new JLabel("End Time:");
      gbc.gridwidth = 1;
      gb.setConstraints(eLabel,gbc);
      p2.add(eLabel);

      eTextF = new WholeNumberField();
      eTextF.setPreferredSize(new Dimension(100,20));
      eTextF.setMinimumSize(new Dimension(100,20));
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(eTextF,gbc);
      p2.add(eTextF);

      gbc.insets = new Insets(10,10,0,0);
      cmLabel = new JLabel("Confirm Time:");
      gbc.gridwidth = 1;
      gb.setConstraints(cmLabel,gbc);
      p2.add(cmLabel);

      cmTextF = new WholeNumberField();
      cmTextF.setPreferredSize(new Dimension(100,20));
      cmTextF.setMinimumSize(new Dimension(100,20));
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(cmTextF,gbc);
      p2.add(cmTextF);

      cLabel = new JLabel("Cost:");
      gbc.gridwidth = 1;
      gb.setConstraints(cLabel,gbc);
      p2.add(cLabel);

      cTextF = new WholeNumberField();
      cTextF.setPreferredSize(new Dimension(100,20));
      cTextF.setMinimumSize(new Dimension(100,20));
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(cTextF,gbc);
      p2.add(cTextF);
/*
      pLabel = new JLabel("Priority:");
      gbc.gridwidth = 1;
      gb.setConstraints(pLabel,gbc);
      p2.add(pLabel);

      pTextF = new WholeNumberField(Goal.MIN_PRIORITY,Goal.MAX_PRIORITY);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(pTextF,gbc);
      p2.add(pTextF);

      nLabel = new JLabel("Invocations:");
      gbc.gridwidth = 1;
      gb.setConstraints(nLabel,gbc);
      p2.add(nLabel);

      nTextF = new WholeNumberField();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gb.setConstraints(nTextF,gbc);
      p2.add(nTextF);

      if ( type == BASIC ) {
         dLabel = new JLabel("Desired By:");
         gbc.gridwidth = 1;
         gb.setConstraints(dLabel,gbc);
         p2.add(dLabel);

         dTextF = new JTextField(15);
         dTextF.setEditable(false);
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.weighty = 1;
         gb.setConstraints(dTextF,gbc);
         p2.add(dTextF);
      }
      else {
         dLabel = new JLabel("Agent:");
         gbc.gridwidth = 1;
         gb.setConstraints(dLabel,gbc);
         p2.add(dLabel);

         dTextF = new JTextField(15);
         gb.setConstraints(dTextF,gbc);
         p2.add(dTextF);

         browseAgentBtn = new JButton("Browse...");
         gbc.anchor = GridBagConstraints.WEST;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.weighty = 1;
         gb.setConstraints(browseAgentBtn,gbc);
         p2.add(browseAgentBtn);
      }

      slaLabel = new JLabel("SLA Ref:");
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridwidth = 1;
      gb.setConstraints(slaLabel,gbc);
      p2.add(slaLabel);

      slaTextF = new JTextField(15);
      slaTextF.setEditable(false);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weighty = 1;
      gb.setConstraints(slaTextF,gbc);
      p2.add(slaTextF);

      rLabel = new JLabel("Result Format:");
      gbc.gridwidth = 1;
      gb.setConstraints(rLabel,gbc);
      p2.add(rLabel);

      JPanel p3 = new JPanel();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weighty = 1;
      gb.setConstraints(p3,gbc);
      p2.add(p3);

      p3.setLayout(gb);
      gbc.insets = new Insets(0,0,2,0);
      gbc.anchor = GridBagConstraints.WEST;
      gbc.weightx = gbc.weighty = 1;

      for( int i = 0; i < nFormat; i++ ) {
         _cb[i] = new LocalCheckBox(FORMAT_TYPE[i]);
         _cbTf[i] = new JTextField(10);

         gbc.gridwidth = 1;
         gbc.fill = GridBagConstraints.NONE;
         gb.setConstraints(_cb[i],gbc);
         p3.add(_cb[i]);

         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gb.setConstraints(_cbTf[i],gbc);
         p3.add(_cbTf[i]);
      }
*/
      contentPane.add(BorderLayout.CENTER,p2);

      // set initial state
//      typeChooser.addItemListener(this);
      goalType.addItemListener(this);
      browseBtn.addActionListener(this);
      okButton.addActionListener(this);
      cancelButton.addActionListener(this);
      this.addWindowListener(
         new WindowAdapter() {
            public void windowClosing(WindowEvent evt) { setVisible(false); }
         }
      );
/*
      for( int i = 0; i < nFormat; i++ )
         _cb[i].addItemListener(this);

      if ( type == EXTENDED )
         browseAgentBtn.addActionListener(this);
*/
      pack();
      setVisible(true);
      setResizable(false);
   }

   public void set(OntologyDb ontology) {
      Assert.notNull(ontology);
      this.ontology = ontology;
   }
   public void set(Engine engine) {
      Assert.notNull(engine);
      this.engine = engine;
   }
   public void set(String[] agents) {
      Assert.notFalse(type == EXTENDED);
      this.agents = agents;
   }
//------------------------------------------------------------------------------
    public void factSelected(String[] name){
       if ( name == null ) return;
        gdModel.setFact(name[0]);
        fact = gdModel.getFact();
        fTextF.setText(name[0]);
    }
//------------------------------------------------------------------------------
   public void itemStateChanged(ItemEvent evt) {
      Object src = evt.getSource();
/*
      if ( src == typeChooser ) {
         boolean state = typeChooser.getSelectedItem().equals("discrete");
         sLabel.setEnabled(!state);
         sTextF.setEnabled(!state);
         sTextF.setEditable(!state);
         nLabel.setEnabled(!state);
         nTextF.setEnabled(!state);
         nTextF.setEditable(!state);

         if ( !state ) { // continuous goal
            slaLabel.setEnabled(false);
            slaTextF.setEnabled(false);
            slaTextF.setEditable(false);
            if ( goalType.getSelectedItem().equals("enact") )
               goalType.setSelectedIndex(0);
         }
         else { // discrete goal
            boolean state1 = goalType.getSelectedItem().equals("enact");
            slaLabel.setEnabled(state1);
            slaTextF.setEnabled(state1);
            slaTextF.setEditable(state1);
         }
      }
      else if ( src == goalType ) {
         boolean state1 = goalType.getSelectedItem().equals("enact");
         slaLabel.setEnabled(state1);
         slaTextF.setEnabled(state1);
         slaTextF.setEditable(state1);
      }
      else {
         for( int i = 0; i < nFormat; i++ )
            if ( src == _cb[i] ) {
               _cbTf[i].setEnabled(_cb[i].getState());
               break;
            }
      }
*/
   }
//------------------------------------------------------------------------------
   public void actionPerformed(ActionEvent evt) {
      Object src = evt.getSource();
      if ( src == browseBtn && ontology != null)
         browseBtnFn();
//      else if ( browseAgentBtn != null && src == browseAgentBtn )
//         browseAgentBtnFn();
      if ( src == okButton ) {
         if ( okBtnFn() ) this.setVisible(false);
      }
      else if ( src == cancelButton )
         this.setVisible(false);
   }
//------------------------------------------------------------------------------
   protected void browseBtnFn() {
       AddFactDialog afd = new AddFactDialog(this,gdModel);
       afd.display(this);
       fact = gdModel.getFact();
       topPane.validate();
   }
//------------------------------------------------------------------------------
   protected void browseAgentBtnFn() {
      if ( agents != null ) {
         if ( agentDialog == null )

         agentDialog = new JList(agents);
         String text = dTextF.getText();
         /*
         if ( text != null && !text.equals("") )
            agentDialog.setSelection(text);
         */
         text = (String) agentDialog.getSelectedValue();
         if ( text != null && !text.equals("") )
            dTextF.setText(text);
      }
      return;
   }

//------------------------------------------------------------------------------
   public boolean okBtnFn() {
      String factName = fTextF.getText();
      String action = (String) goalType.getSelectedItem();

      if (factName == null || factName.equals("") ) {
         JOptionPane.showMessageDialog(null,"Fact Type is undefined","Error",
                                            JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if ( fact == null ) fact =  ontology.getFact(Fact.VARIABLE,factName);


      Long confirmTime, endTime, startTime, cost;
      Long priority, invocations;

/*
      boolean type = ((String)typeChooser.getSelectedItem()).equals("discrete")
                     ? Goal.DISCRETE : Goal.CONTINUOUS;
*/
      boolean type = Goal.DISCRETE;

      endTime = eTextF.getValue();
      confirmTime = cmTextF.getValue();
      cost = cTextF.getValue();
/*
      priority = pTextF.getValue(1);
      if ( type == Goal.CONTINUOUS ) {
         invocations = nTextF.getValue(1);
         startTime = sTextF.getValue(-1);
      }
*/

      if ( confirmTime == null || endTime == null || cost == null )
//           startTime == null || priority == null || invocations == null )
         return false;

      String goalId = gTextF.getText();
      String desiredBy = context.whoami();

/*
      String text;
      int count = 0;
      String[] format = null;
      for( int i = 0; i < nFormat; i++ ) {
         if ( _cb[i].getState() ) {
            if ( (text = _cbTf[i].getText()) == null || text.equals("") ) {
               String msg =  "Destination expected in " +
                             FORMAT_TYPE[i] + " text field";
               JOptionPane.showMessageDialog(null,msg,"Error",
                                            JOptionPane.ERROR_MESSAGE);
               return false;
            }
            else
               count++;
         }
      }
      if ( count != 0 ) {
         format = new String[count*2];
         int j = 0;
         for( int i = 0; i < nFormat; i++ ) {
            if ( _cb[i].getState() ) {
               format[j++] = FORMAT_TYPE[i];
               format[j++] = _cbTf[i].getText();
            }
         }
      }
*/


//      Goal g = (type == Goal.DISCRETE)
//         : new Goal(goalId,fact,startTime,endTime,cost,priority,
//                    invocations,desiredBy,null,new Time((double)confirmTime));
//      g.setTargetMedia(format);

        Goal g = new Goal(goalId,fact,endTime.intValue(),cost.intValue(),
	   Goal.MIN_PRIORITY,desiredBy,null,new Time(confirmTime.doubleValue()));

      if ( engine == null ) return true;
      if ( type == Goal.DISCRETE ) {
         if ( action.equals("achieve") )
            engine.achieve(g);
         else if ( action.equals("sell") )
            engine.sell(g);
         else if ( action.equals("buy") )
            engine.buy(g);
/*
         else if ( gtype.equals("enact") && !slaTextF.getText().equals("") )
            engine.add(g,slaTextF.getText());
*/
      }
      else {
/*
         engine.add(g);
         String ftype = g.getFact().getType();
         ftype = ftype + " {" + g.getId() + "}";
         sla.addElement(ftype);
         sla_data.addElement(g.getFact());
*/
      }
      return true;
   }
//------------------------------------------------------------------------------
   public void display(String[] agents) {
      set(agents);
      display();
   }
//------------------------------------------------------------------------------
   public void display() {
         // select discrete
/*
         typeChooser.setSelectedIndex(0);
         sLabel.setEnabled(false);
         sTextF.setEnabled(false);
         sTextF.setEditable(false);
         nLabel.setEnabled(false);
         nTextF.setEnabled(false);
         nTextF.setEditable(false);
         slaLabel.setEnabled(false);
         slaTextF.setEnabled(false);
         slaTextF.setEditable(false);
*/

         //select("achieve");
         goalType.setSelectedIndex(0);

         gTextF.setText( context.newId("goal") );

/*
         sTextF.setText("");
*/
         eTextF.setText("");
         cmTextF.setText("");
/*
         if ( type == BASIC )
            dTextF.setText( context.whoami() );
         else
            dTextF.setText("");
         pTextF.setText( Integer.toString(Goal.DEFAULT_PRIORITY) );
         nTextF.setText("");
*/
         cTextF.setText( Integer.toString(0) );
         fTextF.setText("");
/*
	 slaTextF.setText("");


         for( int i = 0; i < nFormat; i++ ) {
            _cb[i].setState(false);
            _cbTf[i].setText("");
            _cbTf[i].setEnabled(_cb[i].getState());
         }
*/
   }
//------------------------------------------------------------------------------
   class LocalCheckBox extends JCheckBox implements ItemListener {
             boolean state = false;
              public LocalCheckBox(String text){
                super(text);
                addItemListener(this);
              }

              public void itemStateChanged(ItemEvent e) {

                  if (e.getStateChange() == ItemEvent.SELECTED)
                     state = true;
                  else
                    state = false;
              }
              //------
              boolean getState(){
                return state;
              }
             void setState(boolean b){
                state = b;
                setSelected(b);
             }
          }


}
