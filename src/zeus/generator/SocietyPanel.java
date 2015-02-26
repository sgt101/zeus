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
* SocietyPanel.java
*
***************************************************************************/

package zeus.generator;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.io.File;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.util.*;
import zeus.generator.event.*;
import zeus.generator.agent.AcquaintanceModel;
import zeus.gui.graph.*;
import zeus.gui.help.*;

public class SocietyPanel extends JPanel {
  protected Graph            graph;
  protected GraphNode[]      clipboard;
  protected SocietyModel     model;
  protected GeneratorModel   genmodel;
  protected AgentGenerator   generator;

  public SocietyPanel(AgentGenerator generator, GeneratorModel genmodel)  {
    this.generator = generator;
    this.genmodel = genmodel;
    model = new SocietyModel(genmodel,true);
    graph = new Graph(Graph.VERTICAL_PARENT_CHILD,model,true,false);
    graph.setNodeRenderer(new SocietyNodeRenderer());
    graph.setNodeEditor(new SocietyNodeEditor());

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    JPanel lhsPanel = new JPanel();
    lhsPanel.setLayout(gridBagLayout);
    lhsPanel.setBackground(Color.lightGray);

    // Add the lhs panel
    SoceityControlPanel controlPane = new SoceityControlPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(8,8,8,8);
    gbc.weightx = gbc.weighty = 0;
    gridBagLayout.setConstraints(controlPane,gbc);
    lhsPanel.add(controlPane);
    lhsPanel.setBackground(Color.lightGray);


    gridBagLayout = new GridBagLayout();
    gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,0,0,0);
    gbc.weightx = gbc.weighty = 0;
    gridBagLayout.setConstraints(lhsPanel,gbc);
    add(lhsPanel);

    // Add the panel that will contain the nodes
    JPanel dataPanel = new JPanel();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(dataPanel,gbc);
    add(dataPanel);

    // Data panel info
    gridBagLayout = new GridBagLayout();
    dataPanel.setLayout(gridBagLayout);
    dataPanel.setBackground(Color.lightGray);

    JScrollPane scrollPane = new JScrollPane(graph);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(600,600));
    graph.setPreferredSize(new Dimension(2000,2000));
    graph.setBackground(Color.white);

    TitledBorder border = (BorderFactory.createTitledBorder("Agent Society"));
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(Color.blue);
    dataPanel.setBorder(border);

    JToolBar toolbar = new NodesToolBar();

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
  }

  protected class SocietyNodeRenderer implements GraphNodeRenderer {
     public Component getNodeRendererComponent(Graph g, GraphNode node) {
        AgentDescription obj = (AgentDescription)node.getUserObject();
        String agentId = obj.getName();
        String name = genmodel.getAgentName(agentId);
        String icon_file = genmodel.getAgentIcon(agentId);
        if ( icon_file == null )
	   return new JLabel(name,JLabel.CENTER);
        else {
           JLabel label = new JLabel(name,new ImageIcon(icon_file),JLabel.CENTER);
           label.setVerticalTextPosition(JLabel.BOTTOM);
           label.setHorizontalTextPosition(JLabel.CENTER);
           return label;
        }
     }
  }

  protected class NodesToolBar extends JToolBar implements ActionListener {
     protected HelpWindow    helpWin;
     protected JToggleButton helpBtn;
     protected JButton       newBtn;
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

     public NodesToolBar() {
        setBackground(Color.lightGray);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        // New Button
        newBtn = new JButton(new ImageIcon(path + "new1.gif"));
	newBtn.setMargin(new Insets(0,0,0,0));
        add(newBtn);
        newBtn.setToolTipText("New node");
        newBtn.addActionListener(this);

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
        if ( src == newBtn )
           model.addNewNode();
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
                 getLocation(), "generator", "Society Viewer");
              helpWin.setSource(helpBtn);
          }
          else
              helpWin.dispose();
        }
     }
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


  class SocietyNodeEditor extends AbstractGraphNodeEditor
                          implements ActionListener {

     protected JButton button = new JButton("Click to edit");
     protected GraphNode node;
     protected Graph graph;
     protected FileDialog dialog = null;
 
     public SocietyNodeEditor() {
        button.setBackground(Color.lightGray);
        button.setHorizontalAlignment(JButton.CENTER);
        button.setBorderPainted(true);
        button.addActionListener(this);
        button.setOpaque(true);
        button.setMinimumSize(new Dimension(120,30));
        button.setPreferredSize(new Dimension(120,30));
        button.setSize(120,30);
     }
 
     public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if ( src == button ) {
           // Note: create dialog (using button) before stopping editing
           // which will remove the button from the visible component hierachy
           if ( dialog == null )
              dialog = new FileDialog((Frame)SwingUtilities.getRoot(button),
  	       "Select Icon File",FileDialog.LOAD);
  
           AgentDescription agent = (AgentDescription)node.getUserObject();
           String icon_file = genmodel.getAgentIcon(agent.getName());
  
           if ( icon_file != null ) {
  	    File f1 = new File(icon_file);
              dialog.setFile(f1.getName());
              dialog.setDirectory(f1.getParent());
           }
           else
              dialog.setFile("*.gif");
  
           dialog.pack();
           dialog.setVisible(true);
  
           String path = (dialog.getFile() == null) ? null 
              : dialog.getDirectory() + File.separator + dialog.getFile();
           if ( path == null )
              fireEditAction(EDITING_CANCELLED,this.node,null);
           else
              fireEditAction(EDITING_STOPPED,this.node,path);
        }
     }
     public Component getNodeEditorComponent(Graph graph, GraphNode gnode) {
        this.graph = graph;
        this.node = gnode;
        return button;
     }
  }


  protected class SoceityControlPanel extends JPanel implements ItemListener {
     protected JCheckBox[]    checkbox;
     protected JRadioButton[] view;

     public SoceityControlPanel()  {
       String[] RELATIONS = Misc.stringArray(AcquaintanceModel.RELATIONS_LIST);

       checkbox = new JCheckBox[RELATIONS.length];
       view = new JRadioButton[RELATIONS.length];

       JPanel topPanel = new JPanel();
       TitledBorder border = (BorderFactory.createTitledBorder("View Style"));
       border.setTitlePosition(TitledBorder.TOP);
       border.setTitleJustification(TitledBorder.RIGHT);
       border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
       border.setTitleColor(Color.blue);
       topPanel.setBorder(border);
       topPanel.setBackground(Color.lightGray);

       GridBagLayout gb = new GridBagLayout();
       GridBagConstraints gbc = new GridBagConstraints();
       gbc.insets = new Insets(4,4,0,4);
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.anchor = GridBagConstraints.NORTHWEST;
       topPanel.setLayout(gb);

       ButtonGroup alignGroup = new ButtonGroup();
       for(int i = 0; i < view.length; i++ ) {
          if ( i == 0 )
             view[i] = new JRadioButton("vertical",model.getView() == i);
          else if ( i == 1 )
             view[i] = new JRadioButton("centered",model.getView() == i);
          else
             view[i] = new JRadioButton(RELATIONS[i],model.getView() == i);
          view[i].setBackground(Color.lightGray);
          alignGroup.add(view[i]);
          gb.setConstraints(view[i], gbc);
          topPanel.add(view[i]);
       }

       JPanel bottomPanel = new JPanel();
       border = (BorderFactory.createTitledBorder("Relational Links"));
       border.setTitlePosition(TitledBorder.TOP);
       border.setTitleJustification(TitledBorder.RIGHT);
       border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
       border.setTitleColor(Color.blue);
       bottomPanel.setBorder(border);
       bottomPanel.setBackground(Color.lightGray);

       gb = new GridBagLayout();
       gbc = new GridBagConstraints();
       bottomPanel.setLayout(gb);

       JPanel colorPanel;
       for(int i = 0; i < checkbox.length; i++ ) {
          checkbox[i] = new JCheckBox(RELATIONS[i]);
          checkbox[i].setBackground(Color.lightGray);
          colorPanel = new JPanel();
          colorPanel.setSize(10,10);
          colorPanel.setBackground(model.getColor(i));

          gbc.fill = GridBagConstraints.NONE;
          gbc.weightx = 0;
          gbc.anchor = GridBagConstraints.NORTHWEST;
          gbc.insets = new Insets(4,4,0,0);
          gbc.gridwidth = 1;
          gb.setConstraints(checkbox[i],gbc);
          bottomPanel.add(checkbox[i]);

          gbc.anchor = GridBagConstraints.EAST;
          gbc.insets = new Insets(4,0,0,4);
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gb.setConstraints(colorPanel,gbc);
          bottomPanel.add(colorPanel);
       }

       gb = new GridBagLayout();
       gbc = new GridBagConstraints();
       setLayout(gb);
       gbc.insets = new Insets(0,0,0,0);
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.anchor = GridBagConstraints.NORTHWEST;
       gbc.fill = GridBagConstraints.BOTH;
       gbc.weightx = gbc.weighty = 0;
       gb.setConstraints(topPanel,gbc);
       add(topPanel);

       gbc.weightx = gbc.weighty = 0;
       gb.setConstraints(bottomPanel,gbc);
       add(bottomPanel);

       for(int i = 0; i < checkbox.length; i++ ) {
          checkbox[i].setSelected(model.isLinkVisible(i));
          checkbox[i].addItemListener(this);
          view[i].addItemListener(this);
       }
     }

     public void itemStateChanged(ItemEvent evt) {
       Object source = evt.getSource();

       for(int i = 0; i < checkbox.length; i++ ) {
          if ( source == checkbox[i] ) {
             model.showLinks(i,checkbox[i].isSelected());
             graph.redraw();
             return;
          }
          if ( source == view[i] ) {
             switch( i ) {
                case 0:
                     graph.setViewMode(Graph.VERTICAL_PARENT_CHILD);
                     break;
                case 1:
                     graph.setViewMode(Graph.CENTRED);
                     break;
                default:
                     graph.setViewMode(Graph.CIRCLES);
                     break;
             }
             model.setView(i);
             graph.recompute();
             return;
          }
       }
     }
  }

}
