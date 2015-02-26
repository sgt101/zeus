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



/**************************************************************************
* ReportGraph.java
*
* Panel through which summary task nodes are entered
***************************************************************************/

package zeus.visualiser.report;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.PlanRecord;
import zeus.gui.graph.*;

public class ReportGraph extends JPanel {
  protected Graph       graph;
  protected ReportModel model;

  public ReportGraph(ReportModel model)  {
    this.model = model;
    graph = new Graph(Graph.HORIZONTAL_CHILD_PARENT,model,true,false);
    graph.setNodeRenderer(new ReportNodeRenderer());
    graph.setNodeEditor(new ReportNodeEditor());

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gridBagLayout);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    // Add the panel that will contain the task's nodes
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
    graph.setBackground(new Color(245,230,140));

    TitledBorder border = (BorderFactory.createTitledBorder("Task Graph"));
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

  Graph       getGraph() { return graph; }
  ReportModel getModel() { return model; }

  protected class ReportNodeRenderer implements GraphNodeRenderer {
     public Component getNodeRendererComponent(Graph g, GraphNode node) {
        ReportRec obj = (ReportRec)node.getUserObject();
        return new NodeIcon(obj);
     }

     class NodeIcon extends JPanel {
        public NodeIcon(ReportRec rec) {
           Color color = PlanRecord.color[rec.getState()];

           GridBagLayout gridBagLayout = new GridBagLayout();
           GridBagConstraints gbc = new GridBagConstraints();
           setLayout(gridBagLayout);
           setBackground(color);

           KeyLabel label;
           label = new KeyLabel("Agent: " + rec.getAgent());
           label.setBackground(color);
           gbc.gridwidth = GridBagConstraints.REMAINDER;
           gbc.anchor = GridBagConstraints.NORTHWEST;
           gbc.fill = GridBagConstraints.BOTH;
           gbc.weightx = gbc.weighty = 1;
           gbc.insets = new Insets(2,2,0,2);
           gridBagLayout.setConstraints(label,gbc);
           add(label);

           label = new KeyLabel("Task: " + rec.getTask());
           label.setBackground(color);
           gridBagLayout.setConstraints(label,gbc);
           add(label);

	   label = new KeyLabel("Goal: " + rec.getGoal());
           label.setBackground(color);
           gbc.insets = new Insets(2,2,2,2);
           gridBagLayout.setConstraints(label,gbc);
           add(label);
        }
     }

     class KeyLabel extends JLabel{
        public KeyLabel(String text) {
           super(text,JLabel.LEFT);
        }
        public void paint(Graphics g){
           Color c = g.getColor();
           g.setColor(getBackground());
           g.fillRect(0,0,getWidth(),getHeight()-1);
           g.setColor(c);
           super.paint(g);
        }
     }
  }

  class ReportNodeEditor extends AbstractGraphNodeEditor
                          implements ActionListener {

     protected JButton button = new JButton("Click to edit");
     protected GraphNode node;
     protected ReportDialog dialog = null;

     public ReportNodeEditor() {
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
           // Note: create dialog (using button) before canceling editing
           // which will remove the button from the visible component hierachy
           ReportRec rec = (ReportRec)node.getUserObject();
           dialog = new ReportDialog(rec);
	   dialog.setLocationRelativeTo(button);
           dialog.pack();
           dialog.setVisible(true);
           fireEditAction(EDITING_CANCELLED,this.node,null);
        }
     }
     public Component getNodeEditorComponent(Graph graph, GraphNode gnode) {
        this.node = gnode;
        return button;
     }

     class ReportDialog extends JDialog implements ActionListener {
       String[] attr = { 
         "Goal",
         "Task",
         "Agent",
         "State",
         "Owner",
         "Start-Time",
         "End-Time",
         "Cost"
       };
     
       protected JTextField[] textfield;
       protected JButton      okButton;
       
       public ReportDialog(ReportRec rec) {
         super((JFrame)SwingUtilities.getRoot(ReportGraph.this),
               rec.getGoal() + "/" + rec.getAgent(),false);
         
         JPanel p1 = new JPanel();
         p1.setLayout(new GridLayout(1,1,10,10));
         okButton = new JButton("OK");
         p1.add(okButton);
         
         GridBagLayout gb = new GridBagLayout();
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.insets = new Insets(10,0,10,0);
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.weightx = 1;
         
         JPanel p0 = new JPanel();
         JSeparator s1 = new JSeparator();
         p0.setLayout(gb);
         gb.setConstraints(s1,gbc);
         p0.add(s1);
         
         gbc.anchor = GridBagConstraints.CENTER;
         gbc.fill = GridBagConstraints.NONE;
         gbc.insets = new Insets(0,0,10,0);
         gb.setConstraints(p1,gbc);
         p0.add(p1);
         
         getContentPane().add("South",p0);
         
         JPanel panel = new JPanel();
         panel.setLayout(gb);
         
         JLabel[] label = new JLabel[attr.length];
         textfield = new JTextField[attr.length];
         
         gbc.anchor = GridBagConstraints.WEST;
         gbc.weightx = gbc.weighty = 0;
         for(int i = 0; i < attr.length; i++ ) {
           label[i] = new JLabel(attr[i],Label.LEFT);
           gbc.gridwidth = 1;
           gbc.fill = GridBagConstraints.NONE;
           gbc.weightx = 0;
           gbc.insets = new Insets(10,10,0,0);
           gb.setConstraints(label[i],gbc);
           panel.add(label[i]);
           
           textfield[i] = new JTextField();
           textfield[i].setEditable(false);
           gbc.gridwidth = GridBagConstraints.REMAINDER;
           gbc.fill = GridBagConstraints.HORIZONTAL;
           gbc.weightx = 1;
           gbc.insets = new Insets(10,10,0,10);
           gb.setConstraints(textfield[i],gbc);
           panel.add(textfield[i]);
         }
         getContentPane().add("Center",panel);
         
         okButton.addActionListener(this);
         this.addWindowListener( new WindowAdapter() {
               public void windowClosing(WindowEvent evt) {
                  setVisible(false);
               }
            }
         );
         update(rec);
       }
     
       protected void update(ReportRec report) {
         if ( report == null ) return;
         textfield[0].setText(report.getGoal());
         textfield[1].setText(report.getTask());
         textfield[2].setText(report.getAgent());
         textfield[3].setText(PlanRecord.state_string[report.getState()]);
         textfield[4].setText(report.getOwner());
         textfield[5].setText(Integer.toString(report.getStartTime()));
         textfield[6].setText(Integer.toString(report.getEndTime()));
         textfield[7].setText(Double.toString(report.getCost()));
       }
       
       public void actionPerformed(ActionEvent evt) {
         setVisible(false);
       }
     }
  }


  protected class NodesToolBar extends JToolBar implements ActionListener {
     protected JButton       selectBtn;
     protected JButton       selectAllBtn;
     protected JButton       hideBtn;
     protected JButton       showBtn;
     protected JButton       collapseBtn;
     protected JButton       expandBtn;
     protected JButton       deleteBtn;
     protected JButton       recomputeBtn;
     protected JButton       redrawBtn;

     public NodesToolBar() {
        setBackground(Color.lightGray);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setFloatable(false);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

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
     }

     public void actionPerformed(ActionEvent e)  {
        Object src = e.getSource();
        if ( src == recomputeBtn )
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
     }
  }
}
