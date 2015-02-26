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

/****************************************************************************
* ConditionsPanel.java
*
* Panel through which task attributes are entered
***************************************************************************/

package zeus.generator.task;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.generator.*;
import zeus.generator.event.*;
import zeus.generator.util.*;
import zeus.gui.fields.*;

/****************************************************************************
* ConditionsPanel.java
*
* Panel through which task attributes are entered
***************************************************************************/
public class ConditionsPanel extends JPanel {
  protected PrimitiveTask  task;
  protected LargeTextField costfield;
  protected LargeTextField timefield;
  protected FactPanel      preconditionsPanel;
  protected FactPanel      postconditionsPanel;
  protected FactModel      postconditionsModel;
  protected FactModel      preconditionsModel;


  public ConditionsPanel(AgentGenerator generator,
                         GeneratorModel genmodel,
                         OntologyDb ontologyDb,
                         TaskEditor editor,
                         PrimitiveTask task)  {

    this.task = task;

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);    
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    
    // Add the control panel 
    ControlPanel controlPane =
       new ControlPanel(editor,"Task Preconditions and Effects",true,false);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(controlPane,gbc);
    add(controlPane);

    // Add the panel show the flow metaphor to this panel.
    JPanel flowPanel = new JPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(flowPanel,gbc);
    add(flowPanel);
    
    // Add the panel containing the task's facts to this panel.   
    JPanel factPanel = new JPanel();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gridBagLayout.setConstraints(factPanel,gbc);
    add(factPanel);

    // Add the panel containing the task's cost and time to the panel.
    JPanel costPanel = new JPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.SOUTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gbc.weightx = gbc.weighty = 0;
    gridBagLayout.setConstraints(costPanel,gbc);
    add(costPanel);

    // Cost Panel information
    TitledBorder border =
       BorderFactory.createTitledBorder("Task Cost and Time");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    costPanel.setBorder(border);

    gridBagLayout = new GridBagLayout();
    costPanel.setLayout(gridBagLayout);
    costPanel.setBackground(Color.lightGray);

    JLabel label = new JLabel("Cost:");
    label.setToolTipText("Default cost of performing this task");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,16,0,0);
    gridBagLayout.setConstraints(label,gbc);
    costPanel.add(label);

    costfield = new LargeTextField(2,15);
    costfield.setLineWrap(true);
    costfield.setText(task.getCostFn().toString());
    costfield.addChangeListener(editor);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(0,16,0,16);
    gridBagLayout.setConstraints(costfield,gbc);
    costPanel.add(costfield);

    label = new JLabel("Time:");
    label.setToolTipText("Default duration of this task");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(16,16,16,0);
    gridBagLayout.setConstraints(label,gbc);
    costPanel.add(label);

    timefield = new LargeTextField(2,15);
    timefield.setLineWrap(true);
    timefield.setText(task.getTimeFn().toString());
    timefield.addChangeListener(editor);

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(16,16,16,16);
    gridBagLayout.setConstraints(timefield,gbc);
    costPanel.add(timefield);

    // Flow panel info
    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

    gridBagLayout = new GridBagLayout();
    flowPanel.setLayout(gridBagLayout);
    flowPanel.setBackground(Color.lightGray);

    label = new JLabel(new ImageIcon(path + "uparrow.gif"));
    gbc = new GridBagConstraints();
    gbc.weightx = gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0,0,0,0);
    gridBagLayout.setConstraints(label,gbc);
    flowPanel.add(label);

    label = new JLabel(new ImageIcon(path + "arrow.gif"));
    gbc.insets = new Insets(0,0,0,0);
    gridBagLayout.setConstraints(label,gbc);
    flowPanel.add(label);

    label = new JLabel(new ImageIcon(path + "body.gif"));
    gbc.insets = new Insets(0,0,0,0);
    gridBagLayout.setConstraints(label,gbc);
    flowPanel.add(label);

    label = new JLabel(new ImageIcon(path + "arrow.gif"));
    gbc.insets = new Insets(0,0,0,0);
    gridBagLayout.setConstraints(label,gbc);
    flowPanel.add(label);

    label = new JLabel(new ImageIcon(path + "downarrow.gif"));
    gbc.insets = new Insets(0,0,0,0);
    gridBagLayout.setConstraints(label,gbc);
    flowPanel.add(label);

    // Add factPanel info
    factPanel.setLayout(new GridLayout(1,2,5,5));
    factPanel.setBackground(Color.lightGray);

    preconditionsPanel = new FactPanel(
       ontologyDb,editor,task.getPreconditions(),Fact.VARIABLE,
       FactPanel.PRECONDITION,"Task Inputs/Preconditions");
    postconditionsPanel = new FactPanel(
       ontologyDb,editor,task.getPostconditions(),Fact.VARIABLE,
       FactPanel.POSTCONDITION,"Task Effects/Outputs");

    preconditionsModel = preconditionsPanel.getFactModel();
    postconditionsModel = postconditionsPanel.getFactModel();

    AttributeModel preAttrModel = preconditionsPanel.getAttributeModel();
    AttributeModel postAttrModel = postconditionsPanel.getAttributeModel();

    AttributeTable preAttrTable = preconditionsPanel.getAttributeTable();
    AttributeTable postAttrTable = postconditionsPanel.getAttributeTable();

    // for supporting attribute tree creation
    preAttrTable.setFactModels(preconditionsModel,postconditionsModel);
    postAttrTable.setFactModels(preconditionsModel,postconditionsModel);

    // for uniqueness checking
    preconditionsModel.addRelatedModel(postconditionsModel);
    postconditionsModel.addRelatedModel(preconditionsModel);

    // for renaming cost/time expressions
    SymRenameAction rs = new SymRenameAction();
    preconditionsModel.addRenameListener(rs);
    postconditionsModel.addRenameListener(rs);

    // for pre/post attribute renaming
    preconditionsModel.addRenameListener(preAttrModel);
    preconditionsModel.addRenameListener(postAttrModel);

    postconditionsModel.addRenameListener(preAttrModel);
    postconditionsModel.addRenameListener(postAttrModel);

    // for validating cost/time expressions
    SymFocusAction fs = new SymFocusAction();
    costfield.addFocusListener(fs);
    timefield.addFocusListener(fs);

    // for supporting attribute tree creation for cost/time fields
    SymMouseAction ms  = new SymMouseAction();
    costfield.addMouseListener(ms);
    timefield.addMouseListener(ms);

    factPanel.add(preconditionsPanel);
    factPanel.add(postconditionsPanel);
  }

  class SymFocusAction implements FocusListener {
    public void focusLost(FocusEvent e) {
       LargeTextField field = (LargeTextField)e.getSource();
       validate(field);
    }
    public void focusGained(FocusEvent e) {
       LargeTextField field = (LargeTextField)e.getSource();
       field.setForeground(Color.black);
    }
    protected void validate(LargeTextField field) {
       String s = field.getText();
       ValueFunction fn = ZeusParser.Expression(s);
       if ( fn != null && (fn instanceof NumericFn || fn instanceof ElseFn) )
          field.setForeground(Color.black);
       else
          field.setForeground(Color.red);
    }
  }

  class SymMouseAction extends MouseAdapter implements AttributeSelector {
     protected JTextComponent field = null;
     protected AttributeDialog dialog = null;
     protected AttributeTreeModel attributeTreeModel = null;

     public SymMouseAction() {
        attributeTreeModel = new AttributeTreeModel();
        attributeTreeModel.setFactModels(preconditionsModel,
	                                 postconditionsModel);
     }
     public void mouseClicked(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
           field = (JTextComponent)e.getSource();
           if ( dialog == null )
              dialog = new AttributeDialog(
	         (Frame)SwingUtilities.getRoot(field),attributeTreeModel);
           field = (JTextComponent)e.getSource();
           dialog.setLocationRelativeTo(field);
           dialog.display(this);
        }
     }
     public void attributeSelected(String attribute) {
        try {
	   Document doc = field.getDocument();
           int length = doc.getLength();
           AttributeSet a = doc.getDefaultRootElement().getAttributes();
           doc.insertString(length,attribute,a);
	}
	catch(BadLocationException e) {
	}
     }
  }

  class SymRenameAction implements RenameListener {
    public void nameChanged(RenameEvent e) {
       String prev = (String)e.getOriginal();
       String curr = (String)e.getCurrent();

       String s;
       s = costfield.getText();
       costfield.setText(Misc.substitute(s,prev,curr));
       s = timefield.getText();
       timefield.setText(Misc.substitute(s,prev,curr));
    }
  }

  FactModel getPostconditionsModel() { return postconditionsModel; }
  FactModel getPreconditionsModel()  { return preconditionsModel; }

  void save() {
     task.setCostFn(costfield.getText());
     task.setTimeFn(timefield.getText());
     task.setPreconditions(preconditionsPanel.getData());
     task.setPostconditions(postconditionsPanel.getData());
  }
}
