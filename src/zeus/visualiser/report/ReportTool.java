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



/********************************************************************
 *   ReportTool for the visualiser
 ********************************************************************/


package zeus.visualiser.report;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.help.*;
import zeus.gui.graph.*;
import zeus.actors.*;
import zeus.visualiser.*;
import zeus.visualiser.basic.*;

public class ReportTool extends VideoTool
                        implements ListSelectionListener {

  private static int count = 0;

  protected ReportMenuBar                   menubar;
  protected EditableMultipleSelectionDialog report_dialog = null;
  protected DeleteReportDialog              del_report = null;
  protected Graph                           graph = null;
  protected JList                           agentList, taskList;
  protected ReportModel                     onlineModel, offlineModel;


  public ReportTool(AgentContext context, VisualiserModel model) {
    super(context, model);
    this.setTitle(context.whoami() + " - Reports Tool:" + (count++));
    ImageIcon icon = new ImageIcon(SystemProps.getProperty("gif.dir") +
       File.separator + "visualiser" + File.separator + "report-icon.gif");
    setIconImage(icon.getImage());
    this.setBackground(Color.lightGray);

    onlineModel = new ReportModel();
    offlineModel = new ReportModel();
    ReportGraph graphPanel = new ReportGraph(onlineModel);
    graph = graphPanel.getGraph();

    // Create and Add Toolbars
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(videoToolbar,BorderLayout.NORTH);
    getContentPane().add(graphPanel,BorderLayout.CENTER);

    // Create Lists
    agentList = new JList(onlineModel.getAgentListModel());
    taskList = new JList(onlineModel.getTaskListModel());

    agentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    agentList.addListSelectionListener(this);
    taskList.addListSelectionListener(this);

    JPanel lhsPanel = new JPanel();
    lhsPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    lhsPanel.setLayout(gb);
    setBackground(Color.lightGray);


    JScrollPane scrollpane = new JScrollPane(agentList);
    scrollpane.setPreferredSize(new Dimension(80,180));

    TitledBorder border = BorderFactory.createTitledBorder("Agents");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border.setTitleColor(Color.blue);
    scrollpane.setBorder(border);

    gbc.insets = new Insets(4,4,0,4);
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = gbc.weighty = 1;
    gb.setConstraints(scrollpane,gbc);
    lhsPanel.add(scrollpane);

    scrollpane = new JScrollPane(taskList);
    scrollpane.setPreferredSize(new Dimension(80,180));

    border = BorderFactory.createTitledBorder("Current Tasks");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border.setTitleColor(Color.blue);
    scrollpane.setBorder(border);

    gbc.insets = new Insets(4,4,0,4);
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = gbc.weighty = 1;
    gb.setConstraints(scrollpane,gbc);
    lhsPanel.add(scrollpane);

    JPanel keyPanel = new JPanel();
    border = BorderFactory.createTitledBorder("Task Codes");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border.setTitleColor(Color.blue);
    keyPanel.setBorder(border);

    gbc.insets = new Insets(4,4,4,4);
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = gbc.weighty = 0;
    gb.setConstraints(keyPanel,gbc);
    lhsPanel.add(keyPanel);

    gb = new GridBagLayout();
    gbc = new GridBagConstraints();
    keyPanel.setBackground(Color.lightGray);
    keyPanel.setLayout(gb);

    JLabel taskLabel;
    JPanel taskCode;
    // first possible 3 states are highly unlikely to be shown
    for(int i = 3; i < PlanRecord.state_string.length; i++) {
       taskLabel = new JLabel(PlanRecord.state_string[i],JLabel.LEFT);
       gbc.insets = new Insets(4,4,0,0);
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.NONE;
       gbc.gridwidth = 1;
       gbc.weightx = gbc.weighty = 0;
       gb.setConstraints(taskLabel,gbc);
       keyPanel.add(taskLabel);

       taskCode = new JPanel();
       taskCode.setSize(10,10);
       taskCode.setBackground(PlanRecord.color[i]);
       gbc.insets = new Insets(4,4,0,4);
       gbc.anchor = GridBagConstraints.WEST;
       gbc.fill = GridBagConstraints.NONE;
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.weightx = gbc.weighty = 0;
       gb.setConstraints(taskCode,gbc);
       keyPanel.add(taskCode);
    }

    getContentPane().add(lhsPanel,BorderLayout.WEST);
    
    menubar = new ReportMenuBar();
    setJMenuBar(menubar);

    pack();
    setVisible(true);
    
    /* initialisation sequence */
    setMode(ONLINE);
  }

  public void valueChanged(ListSelectionEvent evt) {
     if ( evt.getValueIsAdjusting() ) return;
     JList list = (JList)evt.getSource();
     String value = (String)list.getSelectedValue();
     if ( value == null ) return;

     if ( list == agentList )
        ((ReportModel)graph.getModel()).showAgent(value);
     else if ( list == taskList )
        ((ReportModel)graph.getModel()).showTask(value);
  }

  public void StreamReports(boolean mode) {
      if ( !hubOK() ) return;
      if ( !stopPlayback() ) return;

      String type = SystemProps.getProperty("agent.names.agent");
      String[] agents = model.getAgents(type);
      if ( report_dialog == null ) {
         report_dialog = new EditableMultipleSelectionDialog(this,
            "Select Agents", agents);
         report_dialog.setLocationRelativeTo(this);
      }
      else {
         Object[] chosen = report_dialog.getPriorSelection();
         report_dialog.setListData(agents);
         report_dialog.setSelection(chosen);
      }

      Object[] data = report_dialog.getSelection();
      model.addAgents(Misc.stringArray(report_dialog.getListData()));
      agents = Misc.stringArray(data);
      subscribe(mode,model.keys[VisualiserModel.REPORT_KEY],agents,"log_report");

      if ( mode )
         onlineModel.addAgents(agents);
      else
         onlineModel.removeAgents(agents);
  }

  public void registerAgent(String name, String type) {
  }

  public void log_report(Performative msg) {
try {
     ReportRec rec = ZeusParser.reportRec(context.OntologyDb(),msg.getContent());
     Core.DEBUG(2,"log_report\n" + rec);
     if ( filterMsg(msg) ) {
        // for efficiency only
        Fact[] pc = null;
        rec.setPreconditions(pc);
        rec.setPostconditions(pc);
        onlineModel.addReport(rec);
     }

     // save to persistent db
     record_item(msg);
}
catch(Exception e) {
//   e.printStackTrace();
}
  }

  public void Help()  {
    Point pt = getLocation();
    HelpWindow helpWin = new HelpWindow(this, pt, "visualiser", "Report Tool");
    helpWin.setSize(new Dimension(getWidth(), 440));
    helpWin.setLocation(pt.x+24, pt.y+24);
    helpWin.validate();
  }

  public void DeleteReport() {
     String task = (String)taskList.getSelectedValue();
     String agent = (String)agentList.getSelectedValue();
     if ( task == null )
        JOptionPane.showMessageDialog(this,
	   "Select a task from the task list before calling this function",
	   "Error", JOptionPane.ERROR_MESSAGE);
     else
        ((ReportModel)graph.getModel()).removeTask(agent,task);
  }

  public void DeleteReports() {
     if ( del_report == null ) {
        del_report = new DeleteReportDialog(this, "Delete Reports");
        del_report.setLocationRelativeTo(this);
     }
     if ( del_report.isShowing() )
        del_report.toFront();
     else {
        del_report.display((ReportModel)graph.getModel());
     }
  }

  public void setAutoDeleteReport(boolean set) {
     onlineModel.setAutoDelete(set);
     offlineModel.setAutoDelete(set);
  }

  public void setShowJointGraphs(boolean set) {
     onlineModel.setShowJointGraphs(set);
     offlineModel.setShowJointGraphs(set);
  }

  protected void setMode(int mode) {
    agentList.clearSelection();
    taskList.clearSelection();

    if ( mode == ONLINE ) {
       menubar.update(ONLINE);
       videoToolbar.setStatus(false);
       graph.setModel(onlineModel);
       agentList.setModel(onlineModel.getAgentListModel());
       taskList.setModel(onlineModel.getTaskListModel());

       String agent = onlineModel.getCurrentAgent();
       String task = onlineModel.getCurrentTask();
       if ( agent != null ) agentList.setSelectedValue(agent,true);
       if ( task != null )  taskList.setSelectedValue(task,true);
 
    }
    else {
       menubar.update(PLAYBACK);
       videoToolbar.setStatus(true);
       offlineModel.reset();
       graph.setModel(offlineModel);
       agentList.setModel(offlineModel.getAgentListModel());
       taskList.setModel(offlineModel.getTaskListModel());

       String agent = onlineModel.getCurrentAgent();
       String task = onlineModel.getCurrentTask();
       if ( agent != null ) agentList.setSelectedValue(agent,true);
       if ( task != null )  taskList.setSelectedValue(task,true);
    }
  }

  protected void registerListOfPlaybackAgents(Vector List) {
     offlineModel.addAgents(List);
  }

  protected void  visualiseVideoData(int dir, Performative msg) {
     if ( state.mode == PLAYBACK ) {
        ReportRec rec;
        rec = ZeusParser.reportRec(context.OntologyDb(),msg.getContent());
        // System.out.println(rec.pprint());
        // for efficiency only
        Fact[] pc = new Fact[0];
        rec.setPreconditions(pc);
        rec.setPostconditions(pc);
        offlineModel.addReport(rec);
     }
  }

protected class ReportMenuBar extends JMenuBar
                              implements ActionListener, ItemListener {
  protected JMenu fileMenu;
  protected JMenu requestMenu;
  protected JMenu helpMenu;
  protected JMenu viewMenu;
  protected JMenu playMenu;
  protected JMenu optionsMenu;

  protected JMenuItem connect;
  protected JMenuItem disconnect;
  protected JMenuItem exit;
  protected JMenuItem cc;
  protected JMenuItem un_cc;
  protected JMenuItem delete_one;
  protected JMenuItem delete_all;
  protected JMenuItem help;
  protected JMenuItem about;

  protected JMenuItem filter;
  protected JMenuItem player_speed;
  protected JMenuItem collapse;
  protected JMenuItem expand;
  protected JMenuItem recompute;
  protected JMenuItem redraw;
  protected JMenuItem select;
  protected JMenuItem selectAll;
  protected JMenuItem hide;
  protected JMenuItem show;

  protected JMenuItem sessions;
  protected JMenuItem load;
  protected JMenuItem save;
  protected JMenuItem close;
  protected JMenuItem forward;
  protected JMenuItem rewind;
  protected JMenuItem fforward;
  protected JMenuItem frewind;
  protected JMenuItem stop;
  protected JMenuItem forward_step;
  protected JMenuItem rewind_step;
  protected JMenuItem forward_last;
  protected JMenuItem rewind_first;
  protected JMenuItem delete;
  protected JMenuItem purge;

  protected JCheckBoxMenuItem auto_delete;
  protected JCheckBoxMenuItem joint_graph;

  protected static final int CHECK = 0;
  protected static final int PLAIN = 1;
  protected static final int RADIO = 2;

  public ReportMenuBar() {
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    connect    = createMenuItem(fileMenu, PLAIN, "Connect to namservers", 'C');
    disconnect = createMenuItem(fileMenu, PLAIN, "Disconnect from nameservers",'D');
    exit       = createMenuItem(fileMenu, PLAIN, "Quit", 'Q');
    add(fileMenu);

    requestMenu = new JMenu("Reports");
    requestMenu.setMnemonic('R');
    cc = createMenuItem(requestMenu, PLAIN, "Request reports", 0);
    un_cc = createMenuItem(requestMenu, PLAIN, "Unrequest reports", 0);
    delete_one = createMenuItem(requestMenu, PLAIN, "Delete current report", 0);
    delete_all = createMenuItem(requestMenu, PLAIN, "Delete reports...", 0);
    auto_delete = (JCheckBoxMenuItem)createMenuItem(requestMenu, CHECK,
		               "Auto-delete reports", 0);
    joint_graph = (JCheckBoxMenuItem)createMenuItem(requestMenu, CHECK,
		               "Show connected reports", 0);

    auto_delete.setState(onlineModel.getAutoDelete());
    joint_graph.setState(onlineModel.getShowJointGraphs());
    add(requestMenu);

    JMenu playMenu = new JMenu("Playback");
    playMenu.setMnemonic('P');
    sessions = createMenuItem(playMenu, PLAIN, "Request saved sessions", 0);
    delete   = createMenuItem(playMenu, PLAIN, "Delete session", 0);
    purge    = createMenuItem(playMenu, PLAIN, "Purge database", 0);
    playMenu.addSeparator();
    load  = createMenuItem(playMenu, PLAIN, "Load session", 0);
    save  = createMenuItem(playMenu, PLAIN, "Record session", 0);
    close = createMenuItem(playMenu, PLAIN, "Close session", 0);
    playMenu.addSeparator();
    forward  = createMenuItem(playMenu, PLAIN, "Forward", 0);
    rewind   = createMenuItem(playMenu, PLAIN, "Rewind", 0);
    fforward = createMenuItem(playMenu, PLAIN, "Fast forward", 0);
    frewind  = createMenuItem(playMenu, PLAIN, "Fast rewind", 0);
    forward_step = createMenuItem(playMenu, PLAIN, "Step forward", 0);
    rewind_step  = createMenuItem(playMenu, PLAIN, "Step backward", 0);
    forward_last = createMenuItem(playMenu, PLAIN, "Forward to end", 0);
    rewind_first = createMenuItem(playMenu, PLAIN, "Rewind to beginning", 0);
    stop         = createMenuItem(playMenu, PLAIN, "Stop", 0);
    add(playMenu);

    optionsMenu = new JMenu("Options");
    optionsMenu.setMnemonic('O');
    filter       = createMenuItem(optionsMenu, PLAIN, "Filter messages...", 0);
    player_speed = createMenuItem(optionsMenu, PLAIN, "Player speed...", 0);
    add(optionsMenu);

    viewMenu = new JMenu("View");
    viewMenu.setMnemonic('V');
    collapse  = createMenuItem(viewMenu, PLAIN, "Collapse", 0);
    expand    = createMenuItem(viewMenu, PLAIN, "Expand", 0);
    recompute = createMenuItem(viewMenu, PLAIN, "Recompute", 0);
    redraw    = createMenuItem(viewMenu, PLAIN, "Redraw", 0);
    viewMenu.addSeparator();
    select    = createMenuItem(viewMenu, PLAIN, "Select", 0);
    selectAll = createMenuItem(viewMenu, PLAIN, "Select all", 0);
    viewMenu.addSeparator();
    hide      = createMenuItem(viewMenu, PLAIN, "Hide", 0);
    show      = createMenuItem(viewMenu, PLAIN, "Show", 0);

    helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    help  = createMenuItem(helpMenu, PLAIN, "Using the control tool", 'U');
    about = createMenuItem(helpMenu, PLAIN, "About ZEUS", 'A');
    add(helpMenu);
  }

  private JMenuItem createMenuItem(JMenu menu, int type, String text, int accelKey) {
    JMenuItem item;
    switch(type) {
      case CHECK:
        item = new JCheckBoxMenuItem(text);
        ((JCheckBoxMenuItem)item).setState(false);
        item.addItemListener(this);
        break;
      case RADIO:
        item = new JRadioButtonMenuItem(text);
        item.addActionListener(this);
      default:
        item = new JMenuItem(text);
        item.addActionListener(this);
        break;
    }
    if(accelKey > 0)
      item.setMnemonic(accelKey);
    menu.add(item);
    return item;
  }

  public void actionPerformed(ActionEvent actionevent)  {
        Object obj = actionevent.getSource();
        if(obj == connect)
            Connect(true);
        else if(obj == disconnect)
            Connect(false);
        else if(obj == exit)
            Exit();
        else if(obj == cc)
            StreamReports(true);
        else if(obj == un_cc)
            StreamReports(false);
        else if(obj == delete_one)
            DeleteReport();
        else if(obj == delete_all)
            DeleteReports();
        else if(obj == sessions)
            Sessions();
        else if(obj == delete)
            Delete();
        else if(obj == purge)
            Purge();
        else if(obj == load)
            Load();
        else if(obj == save)
            Record();
        else if(obj == close)
            Close();
        else if(obj == forward)
            Forward();
        else if(obj == rewind)
            Rewind();
        else if(obj == fforward)
            FForward();
        else if(obj == frewind)
            FRewind();
        else if(obj == stop)
            Stop();
        else if(obj == forward_step)
            StepForward();
        else if(obj == forward_last)
            ForwardEnd();
        else if(obj == rewind_step)
            StepRewind();
        else if(obj == rewind_first)
            RewindBegin();
        else if(obj == player_speed)
            PlayerSpeed();
       else if (obj == filter )
            Filter();


        else if(obj == collapse)
            graph.collapse();
        else if(obj == expand)
            graph.expand();
        else if(obj == recompute)
            graph.recompute();
        else if(obj == redraw)
            graph.redraw();
        else if(obj == select)
            graph.select();
        else if(obj == selectAll)
            graph.selectAll();
        else if(obj == hide)
            graph.hide();
        else if(obj == show)
            graph.show();

        else if(obj == help)
            Help();
        else if(obj == about)
            About();
  }

  public void itemStateChanged(ItemEvent itemevent)  {
    Object obj = itemevent.getSource();
    if(obj == auto_delete)
      setAutoDeleteReport(auto_delete.getState());
    else if(obj == joint_graph)
      setShowJointGraphs(joint_graph.getState());
  }

  public void update(int mode)  {
    boolean flag = mode == ReportTool.this.PLAYBACK;
    cc.setEnabled(!flag);
    forward.setEnabled(flag);
    rewind.setEnabled(flag);
    fforward.setEnabled(flag);
    frewind.setEnabled(flag);
    stop.setEnabled(flag);
    forward_step.setEnabled(flag);
    rewind_step.setEnabled(flag);
    forward_last.setEnabled(flag);
    rewind_first.setEnabled(flag);
  }
}

}
