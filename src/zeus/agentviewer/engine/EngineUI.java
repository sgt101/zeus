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

/*
 * @(#)EngineUI.java 1.03b
 */


package zeus.agentviewer.engine;


import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.agentviewer.*;
import zeus.util.*;
import zeus.gui.graph.*;
import zeus.gui.help.*;


public class EngineUI extends ZeusInternalFrame
   implements ListSelectionListener {

   private JPanel  contentPane;
   private JTable  table;
   private JScrollPane tablePanel;

   final int TOP_PANE_MIN_HEIGHT = 120;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 50;
   final int BOTTOM_PANE_MIN_HEIGHT = 50;

   private static int NUMBER_DISPLAYED = 0;
   private InternalFramesPanel deskTop;
   EngineTableModel engineBuffer;
   private JScrollPane treeSP;
   zeus.gui.graph.Graph graph = null;



//--------------------------------------------------------------------------
   public EngineUI(InternalFramesPanel deskTop,EngineTableModel engineBuffer) 
   {
        super("Coordination Engine",true,true,true,true);
        setTitle("Coordination Engine:" + (++NUMBER_DISPLAYED));
        String sep = System.getProperty("file.separator");
        String gifpath = SystemProps.getProperty("gif.dir") + "agentviewer" + sep;
        ImageIcon icon = new ImageIcon(gifpath + ViewerNames.COORDENG_IMG);
        setFrameIcon(icon);
        this.deskTop = deskTop;
        this.engineBuffer = engineBuffer;
        buildUI();
        deskTop.addInternalFrame(this);
        setVisible(true);
   }
//--------------------------------------------------------------------------
      class KeyLabel extends JPanel {
           public KeyLabel(String text, Color bColor) {
              GridBagLayout gridBagLayout = new GridBagLayout();
              GridBagConstraints gbc = new GridBagConstraints();
              setLayout(gridBagLayout);
          
              // Add the colour panel
              JPanel colorPanel = new JPanel();
              colorPanel.setPreferredSize(new Dimension(10,10));
              colorPanel.setBackground(bColor);
              gbc.gridwidth = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.fill = GridBagConstraints.NONE;
              gbc.insets = new Insets(0,0,0,0);
              gbc.weightx = gbc.weighty = 0;
              gridBagLayout.setConstraints(colorPanel,gbc);
              add(colorPanel);

              // Add the text 
              JLabel label = new JLabel(text);
              gbc.gridwidth = GridBagConstraints.REMAINDER;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.fill = GridBagConstraints.HORIZONTAL;
              gbc.insets = new Insets(0,5,0,0);
              gbc.weightx = gbc.weighty = 1;
              gridBagLayout.setConstraints(label,gbc);
              add(label);
           }
      }
//--------------------------------------------------------------------------
      private JPanel getColorPanel() {

         KeyLabel lb;
         JPanel skeyPanel = new JPanel();
         skeyPanel.setLayout(new GridLayout(2,3,5,2));
         for( int i = 0; i < EngineNodeRenderer.state_string.length; i++ ) {
            lb = new KeyLabel(EngineNodeRenderer.state_string[i],
                              EngineNodeRenderer.color[i]) ;
     skeyPanel.add(lb);
         }
   
         JPanel keyPanel = new JPanel();
         GridBagLayout gb = new GridBagLayout();
         GridBagConstraints gbc = new GridBagConstraints();
         keyPanel.setLayout(gb);
   
         gbc.insets = new Insets(10,5,0,0);
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.NONE;
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         JLabel keyLb = new JLabel("Key",SwingConstants.LEFT);
         keyLb.setFont(new Font("Courier",Font.BOLD,14));
         keyLb.setForeground(Color.black);
         gb.setConstraints(keyLb,gbc);
         keyPanel.add(keyLb);
         gbc.insets = new Insets(5,5,5,5);
         gbc.weightx = gbc.weighty = 1;
         gb.setConstraints(skeyPanel,gbc);
         keyPanel.add(skeyPanel);
   
         return keyPanel;
   }

//--------------------------------------------------------------------------
      private void buildUI() {
          table = new JTable(engineBuffer);
          table.setPreferredScrollableViewportSize(
             new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT));
          tablePanel = new JScrollPane(table);
          tablePanel.setPreferredSize(
             new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT));
          table.getSelectionModel().addListSelectionListener(this );
          table.getSelectionModel().setSelectionMode(
             ListSelectionModel.SINGLE_SELECTION);
          table.setShowGrid(false);

          treeSP = new JScrollPane(graph);
          treeSP.setBackground(Color.white);

          treeSP.setPreferredSize(
             new Dimension(BOTTOM_PANE_MIN_WIDTH, BOTTOM_PANE_MIN_HEIGHT));

          treeSP.setBorder(new BevelBorder(BevelBorder.LOWERED));

          JPanel centerPanel = new JPanel(new BorderLayout());
          centerPanel.add(BorderLayout.NORTH, new NodesToolBar());
          centerPanel.add(BorderLayout.CENTER, treeSP);

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());
          contentPane.add(BorderLayout.NORTH,tablePanel);
          contentPane.add(BorderLayout.CENTER,centerPanel);
          contentPane.add(BorderLayout.SOUTH,getColorPanel());
          pack();
      }

//--------------------------------------------------------------------------
      public void valueChanged(ListSelectionEvent e) {
          int row;

          if (e.getSource() == table.getSelectionModel() ) {

               row = table.getSelectedRow();
               if (row >= 0  && row < engineBuffer.getRowCount()) {
                  if (treeSP.getViewport().getComponentCount() > 0)
                     treeSP.getViewport().removeAll();
                  EngineGraphModel engineGraphModel = engineBuffer.getGraph(row);

                  graph = new Graph(Graph.HORIZONTAL_PARENT_CHILD,
                   engineGraphModel,false,false);
                  graph.setNodeRenderer(new TreeNodeRenderer());
                  graph.setBackground(Color.white);
                  graph.setPreferredSize(new Dimension(2000,2000));
                  treeSP.getViewport().add(graph);
                  treeSP.validate();
               }
          }

      }
//--------------------------------------------------------------------------
     void reSize() {
        setSize(getWidth()-2,getHeight());
        setSize(getWidth()+2,getHeight());
     }
//--------------------------------------------------------------------------
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

   // Help Button
          helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
    helpBtn.setMargin(new Insets(0,0,0,0));
          add(helpBtn);
          helpBtn.setToolTipText("Help");
          helpBtn.addActionListener(this);
         }

         public void actionPerformed(ActionEvent e)  {
           Object src = e.getSource();

           if (graph == null ) return;

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
           else if ( src == helpBtn ) {
            if ( helpBtn.isSelected() ) {
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                 getLocation(), "generator", "Coordination Engine");
              helpWin.setSource(helpBtn);
            }
            else
              helpWin.dispose();
           }
         }
     }

//--------------------------------------------------------------------------
}



//--------------------------------------------------------------------------
class TreeNodeRenderer implements GraphNodeRenderer {
     public Component getNodeRendererComponent(Graph g, GraphNode node) {
        zeus.actors.rtn.Node obj = (zeus.actors.rtn.Node)node.getUserObject();
        return new EngineNodeRenderer(obj);
     }
  }


class EngineNodeRenderer extends JLabel  {

      zeus.actors.rtn.Node node;
      public static final Color[] color = {
       Color.blue,
       Color.yellow,
       Color.orange,
       Color.green,
       Color.cyan,
       Color.red
     };

     public static final String[] state_string = {
      "NOT_READY",
      "READY",
      "WAITING",
      "RUNNING",
      "DONE",
      "FAILED"
   };

//--------------------------------------------------------------------------
   public EngineNodeRenderer(zeus.actors.rtn.Node node) {
        super(node.getDescription(), JLabel.CENTER);
        // LL 040500 1.03b
        if ( node.getDesc() != null ) setToolTipText(node.getDesc());
        this.node =  node;
        repaint();
   }

 //-------------------------------------------------------------------------
   public void paint(Graphics g) {
         String text = node.getDescription();
         Color bColor = color[node.getState()];

   if (bColor == Color.black)
            setForeground(Color.white);
         else
          setForeground(Color.black);
         setText(text);
         g.setColor(bColor);
         g.fillRect(0,0,getWidth(),getHeight()-1);
         super.paint(g);
   }
}
