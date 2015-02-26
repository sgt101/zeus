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
* NodesPanel.java
*
* Panel through which summary task nodes are entered
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
import zeus.generator.util.*;
import zeus.generator.event.*;
import zeus.gui.graph.*;
import zeus.gui.fields.*;
import zeus.gui.help.*;

public class NodesPanel extends JPanel {
  protected SummaryTask      task;
  protected Graph            graph;
  protected GraphNode[]      clipboard;
  protected SummaryTaskModel model;
  protected JCheckBox        autorun;
  protected LargeTextField   costfield;
  protected LargeTextField   timefield;
  protected BasicFactModel   preconditionsModel;
  protected BasicFactModel   postconditionsModel;

  public NodesPanel(AgentGenerator generator, GeneratorModel genmodel,
                    OntologyDb ontologyDb, TaskEditor editor,
                    SummaryTask task)  {

    this.task = task;

    model = new SummaryTaskModel(ontologyDb,task.getNodes(),task.getLinks());
    model.addChangeListener(editor);
    graph = new Graph(model);
    graph.setNodeRenderer(new SummaryTaskNodeRenderer());
    graph.setNodeEditor(new SummaryTaskNodeEditor(ontologyDb,model));

    preconditionsModel =
       new SymBasicFactModel(SummaryTaskModel.PRECONDITION,model);
    postconditionsModel =
       new SymBasicFactModel(SummaryTaskModel.POSTCONDITION,model);

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    // Add the control panel
    ControlPanel controlPane =
       new ControlPanel(editor,"Task Decomposition Graph",true,false);
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

    // Add the panel that will contain the task's cost and time fields
    JPanel costPanel = new JPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,8,8);
    gbc.weightx = gbc.weighty = 0;
    gridBagLayout.setConstraints(costPanel,gbc);
    add(costPanel);

    // Cost/Time panel info

    String title = task.isScript() ? "Task Mode, Cost and Time" :
       "Task Cost and Time";

    TitledBorder border =
       BorderFactory.createTitledBorder(title);
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    costPanel.setBorder(border);

    gridBagLayout = new GridBagLayout();
    costPanel.setLayout(gridBagLayout);
    costPanel.setBackground(Color.lightGray);

    JLabel label;

    if ( task.isScript() ) {
       label = new JLabel("Autorun:");
       label.setToolTipText("Set autorun on if task is invoked automatically");
       gbc = new GridBagConstraints();
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.NONE;
       gbc.gridwidth = 1;
       gbc.insets = new Insets(0,8,8,0);
       gridBagLayout.setConstraints(label,gbc);
       costPanel.add(label);

       autorun = new JCheckBox();
       autorun.setBackground(Color.lightGray);
       autorun.setSelected(((PlanScript)task).isAutorun());
       autorun.setEnabled(false); // REM for now
       autorun.addChangeListener(editor);

       gbc = new GridBagConstraints();
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.NONE;
       gbc.insets = new Insets(0,8,8,8);
       gridBagLayout.setConstraints(autorun,gbc);
       costPanel.add(autorun);
    }


    label = new JLabel("Cost:");
    label.setToolTipText("Default cost of performing this task");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,8,0,0);
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
    gbc.insets = new Insets(0,8,0,8);
    gridBagLayout.setConstraints(costfield,gbc);
    costPanel.add(costfield);

    label = new JLabel("Time:");
    label.setToolTipText("Default duration of this task");
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(8,8,8,0);
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
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(timefield,gbc);
    costPanel.add(timefield);

    // Data panel info
    gridBagLayout = new GridBagLayout();
    dataPanel.setLayout(gridBagLayout);
    dataPanel.setBackground(Color.lightGray);

    JScrollPane scrollPane = new JScrollPane(graph);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(600,400));
    graph.setPreferredSize(new Dimension(2000,2000));
    graph.setBackground(Color.white);

    border = (BorderFactory.createTitledBorder("Task Decomposition Graph"));
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    dataPanel.setBorder(border);

    JToolBar toolbar = new NodesToolBar(task.isScript());

    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(toolbar,gbc);
    dataPanel.add(toolbar);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(scrollPane,gbc);
    dataPanel.add(scrollPane);

    // Initialisation Information
    // for renaming cost/time expressions
    SymRenameAction rs = new SymRenameAction();
    preconditionsModel.addRenameListener(rs);
    postconditionsModel.addRenameListener(rs);

    // for validating cost/time expressions
    SymFocusAction fs = new SymFocusAction();
    costfield.addFocusListener(fs);
    timefield.addFocusListener(fs);

    // for supporting attribute tree creation for cost and time fields
    SymMouseAction ms  = new SymMouseAction();
    costfield.addMouseListener(ms);
    timefield.addMouseListener(ms);

  }

  protected class SymFocusAction implements FocusListener {
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

  protected class SymMouseAction extends MouseAdapter 
                                 implements AttributeSelector {
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

  protected class SymRenameAction implements RenameListener {
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

  protected class SummaryTaskNodeRenderer implements GraphNodeRenderer {
     public Component getNodeRendererComponent(Graph g, GraphNode node) {
        TaskNode obj = (TaskNode)node.getUserObject();
        return new JLabel(obj.getName(),JLabel.CENTER);
     }
  }

  protected class NodesToolBar extends JToolBar implements ActionListener {
     protected HelpWindow    helpWin;
     protected JToggleButton helpBtn;
     protected JButton       nodeBtn;
     protected JButton       guardBtn;
     protected JButton       selectBtn;
     protected JButton       selectAllBtn;
     protected JButton       hideBtn;
     protected JButton       showBtn;
     protected JButton       collapseBtn;
     protected JButton       expandBtn;
     protected JButton       deleteBtn;
     protected JButton       cutBtn;
     protected JButton       copyBtn;
     protected JButton       pasteBtn;
     protected JButton       recomputeBtn;
     protected JButton       redrawBtn;

     public NodesToolBar(boolean is_script) {

        setBackground(Color.lightGray);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        // Node Buttons
        nodeBtn = new JButton("Node"); // new ImageIcon(path + "node.gif"));
	nodeBtn.setMargin(new Insets(0,0,0,0));
        add(nodeBtn);
        nodeBtn.setToolTipText("New task node");
        nodeBtn.addActionListener(this);

        if ( is_script ) {
           guardBtn = new JButton("Guard"); // new ImageIcon(path + "guard.gif"));
	   guardBtn.setMargin(new Insets(0,0,0,0));
           add(guardBtn);
           guardBtn.setToolTipText("New conditional node");
           guardBtn.addActionListener(this);
        }

        addSeparator();

        // Draw Buttons
        recomputeBtn = new JButton(new ImageIcon(path + "recompute.gif"));
	recomputeBtn.setMargin(new Insets(0,0,0,0));
        add(recomputeBtn);
        recomputeBtn.setToolTipText("Recompute node positions");
        recomputeBtn.addActionListener(this);

        redrawBtn = new JButton(new ImageIcon(path + "redraw.gif"));
	redrawBtn.setMargin(new Insets(0,0,0,0));
        add(redrawBtn);
        redrawBtn.setToolTipText("Redraw");
        redrawBtn.addActionListener(this);

	addSeparator();

        selectBtn = new JButton(new ImageIcon(path + "select.gif"));
	selectBtn.setMargin(new Insets(0,0,0,0));
        add(selectBtn);
        selectBtn.setToolTipText("Select nodes");
        selectBtn.addActionListener(this);

        selectAllBtn = new JButton(new ImageIcon(path + "selectAll.gif"));
	selectAllBtn.setMargin(new Insets(0,0,0,0));
        add(selectAllBtn);
        selectAllBtn.setToolTipText("Select all nodes");
        selectAllBtn.addActionListener(this);

        addSeparator();

        collapseBtn = new JButton(new ImageIcon(path + "collapse.gif"));
	collapseBtn.setMargin(new Insets(0,0,0,0));
        add(collapseBtn);
        collapseBtn.setToolTipText("Collapse nodes");
        collapseBtn.addActionListener(this);

        expandBtn = new JButton(new ImageIcon(path + "expand.gif"));
	expandBtn.setMargin(new Insets(0,0,0,0));
        add(expandBtn);
        expandBtn.setToolTipText("Expand nodes");
        expandBtn.addActionListener(this);

        addSeparator();

        hideBtn = new JButton(new ImageIcon(path + "hide.gif"));
	hideBtn.setMargin(new Insets(0,0,0,0));
        add(hideBtn);
        hideBtn.setToolTipText("Hide nodes");
        hideBtn.addActionListener(this);

        showBtn = new JButton(new ImageIcon(path + "show.gif"));
	showBtn.setMargin(new Insets(0,0,0,0));
        add(showBtn);
        showBtn.setToolTipText("Show nodes");
        showBtn.addActionListener(this);

        addSeparator();

        // Delete Button
        deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
	deleteBtn.setMargin(new Insets(0,0,0,0));
        add(deleteBtn);
        deleteBtn.setToolTipText("Delete");
        deleteBtn.addActionListener(this);

        addSeparator();

        // Cut Button
        cutBtn = new JButton(new ImageIcon(path + "cut.gif"));
	cutBtn.setMargin(new Insets(0,0,0,0));
        add(cutBtn);
        cutBtn.setToolTipText("Cut");
        cutBtn.addActionListener(this);

        // Copy Button
        copyBtn = new JButton(new ImageIcon(path + "copy.gif"));
	copyBtn.setMargin(new Insets(0,0,0,0));
        add(copyBtn);
        copyBtn.setToolTipText("Copy");
        copyBtn.addActionListener(this);

        // Paste Button
        pasteBtn = new JButton(new ImageIcon(path + "paste.gif"));
	pasteBtn.setMargin(new Insets(0,0,0,0));
        add(pasteBtn);
        pasteBtn.setToolTipText("Paste");
        pasteBtn.addActionListener(this);

        addSeparator();

        // Help Button
        helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
	helpBtn.setMargin(new Insets(0,0,0,0));
        add(helpBtn);
        helpBtn.setToolTipText("Help");
        helpBtn.addActionListener(this);
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == nodeBtn )
           model.addNewNode(SummaryTaskModel.BASIC_NODE);
        else if ( src == guardBtn )
           model.addNewNode(SummaryTaskModel.GUARD_NODE);
        else if ( src == recomputeBtn )
           graph.recompute();
        else if ( src == redrawBtn )
           graph.redraw();
        else if ( src == selectBtn )
           graph.select();
        else if ( src == selectAllBtn )
           graph.selectAll();
        else if ( src == collapseBtn )
           graph.collapse();
        else if ( src == expandBtn )
           graph.expand();
        else if ( src == hideBtn )
           graph.hide();
        else if ( src == showBtn )
           graph.show();
        else if ( src == deleteBtn )
           deleteSelectedNodes();
        else if ( src == copyBtn )
           clipboard = graph.getSelectedNodes();
        else if ( src == pasteBtn )
           model.addNodes(clipboard);
        else if ( src == cutBtn )
           clipboard = cutSelectedNodes();
        else if ( src == helpBtn ) {
          if ( helpBtn.isSelected() ) {
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 getLocation(),"generator","Summary Task Decomposition Graph");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
  }

  BasicFactModel getPreconditionsModel() {
     return preconditionsModel;
  }
  BasicFactModel getPostconditionsModel() {
     return postconditionsModel;
  }

  void save() {
     task.setNodes(model.getNodes());
     task.setLinks(model.getLinks());
     task.setCostFn(costfield.getText());
     task.setTimeFn(timefield.getText());

     if ( task.isScript() )
        ((PlanScript)task).setAutorun(autorun.isSelected());
  }

  protected void deleteSelectedNodes() {
     model.removeNodes(graph.getSelectedNodes());
     graph.redraw();
  }
  protected GraphNode[] cutSelectedNodes() {
     GraphNode[] out = graph.getSelectedNodes();
     model.removeNodes(out);
     graph.redraw();
     return out;
  }
}
