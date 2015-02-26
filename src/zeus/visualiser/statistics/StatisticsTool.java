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



package zeus.visualiser.statistics;

import java.io.File;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.help.*;
import zeus.actors.*;
import zeus.visualiser.VisualiserModel;
import zeus.visualiser.statistics.charts.*;
import zeus.visualiser.basic.*;


public class StatisticsTool extends VideoTool {
  private static int count = 0;

  static final int NIL = 0;
  static final int BAT = 1;
  static final int TVT = 2;
  static final int TVA = 4;
  static final int IAT = 8;
  static final int MPG = 16;
  static final int NEG = 32;
  static final int GCS = 64;
  static final int AEM = 128;
  static final int AMS = 256;
  static final int CRR = 512;
  static final int GLT = 1024;
  static final int TAL = 2048;

  static final int PIE = 1;
  static final int BAR = 2;
  static final int LIN = 4;
  static final int XYG = 8;
  static final int TAB = 16;

  static String[] STATS_MENU_ITEMS = {
      "Breakdown of agent types",
      "Traffic volume by type",
      "Traffic volume by agent",
      "Inter agent traffic volume",
      "Messages per goal",
      "Inter agent negotiation graphs",

      "Goal completion status",
      "Agent efficiency measure",
      "Agent monetary statement",
      "Plan/Resources ratio",
      "Average goal lapse times",
      "Task activity level"
  };


  protected StatisticsMenuBar menubar;
  protected StatisticsToolBar statisticsToolBar;

  protected EditableMultipleSelectionDialog message_dialog = null;
  protected DoubleSelectionDialog           neg_dialog = null;
  protected NumberDialog                    as_dialog = null;

  protected JScrollPane scrollpane;
  protected DrawCanvas canvas;

  private  int chart_type = PIE;
  private  int statistics_type = NIL;

  protected TrafficVolume  offlineQueue;
  protected TrafficVolume  onlineQueue;
  protected TrafficVolume  msgQueue = null;
  protected Grapher grapher = null;

  protected MultipleSelectionDialog ms_dialog = null;


  public StatisticsTool(AgentContext context, VisualiserModel model) {
    super(context, model);
    this.setTitle(context.whoami() + " - Statistics Tool:" + (count++));
    this.setBackground(Color.lightGray);
    ImageIcon icon = new ImageIcon(SystemProps.getProperty("gif.dir") +
       File.separator + "visualiser" +																	 File.separator + "stats-icon.gif");
    setIconImage(icon.getImage());

    offlineQueue = new TrafficVolume(context.OntologyDb());
    onlineQueue = new TrafficVolume(context.OntologyDb());

    getContentPane().setLayout(new BorderLayout());

    canvas = new DrawCanvas();
    canvas.setBackground(new Color(245,230,145));

    scrollpane = new JScrollPane(canvas);
    scrollpane.setPreferredSize(new Dimension(460,460));
    scrollpane.setMaximumSize(new Dimension(1600, 1600));
    canvas.setPreferredSize(new Dimension(1600,1600));

    // Create the statistics tool bar
    statisticsToolBar = new StatisticsToolBar();

    // Create a panel for the tool bars
    JPanel toolpane = new JPanel();
    toolpane.setLayout(new GridLayout(1,2));
    toolpane.add(statisticsToolBar);
    toolpane.add(videoToolbar);

    getContentPane().add("North",toolpane);
    getContentPane().add("Center",scrollpane);

    msgQueue = onlineQueue;
    grapher = new Grapher();

    // Create the frame's menu bar
    menubar = new StatisticsMenuBar();
    setJMenuBar(menubar);

    this.pack();
    this.setVisible(true);
    
    /* initialisation sequence */
    setMode(ONLINE);
  }

  public void Exit() {
     if ( grapher != null )
        grapher.terminate();
     grapher = null;
     super.Exit();
  }

  public Dimension getViewportSize() {
     return scrollpane.getViewport().getExtentSize();
  }

  public void SaveGoalTraffic(boolean set) {
     msgQueue.setUpdatingGoalTraffic(set);
  }
  public void ClearGoalTraffic() {
     msgQueue.clearGoalTraffic();
  }

  public void StreamMessages(boolean mode) {
      if ( !hubOK() ) return;
      if ( !stopPlayback() ) return;

      String[] agents = model.getAgents();
      if ( message_dialog == null ) {
         message_dialog = new EditableMultipleSelectionDialog(this,
            "Select Agents", agents);
         message_dialog.setLocationRelativeTo(this);
      }
      else {
         Object[] chosen = message_dialog.getPriorSelection();
         message_dialog.setListData(agents);
         message_dialog.setSelection(chosen);
      }

      Object[] data = message_dialog.getSelection();
      model.addAgents(Misc.stringArray(message_dialog.getListData()));
      subscribe(mode,model.keys[VisualiserModel.MESSAGE_KEY],
         Misc.stringArray(data),"log_message");
   }


   protected void setMode(int mode) {
     if ( mode == ONLINE ) {
        menubar.update(ONLINE);
        videoToolbar.setStatus(false);
        offlineQueue.clear();
        msgQueue = onlineQueue;
     }
     else {
        menubar.update(PLAYBACK);
        videoToolbar.setStatus(true);
        offlineQueue.clear();
        msgQueue = offlineQueue;
     }
   }

   protected void registerAgent(String name, String type) {
      //onlineQueue.addAgent(name,type);
   }

   protected void registerListOfPlaybackAgents(Vector List) {
      //offlineQueue.addAgents(List);
   }

   protected void  visualiseVideoData(int dir, Performative msg) {
      if ( state.mode == PLAYBACK && filterMsg(msg) )
         offlineQueue.update(msg);
   }

   public void log_message(Performative msg) {
try {
      Performative imsg = ZeusParser.performative(msg.getContent());
      if ( filterMsg(imsg) )
         onlineQueue.update(imsg);

      // save to persistent db
      record_item(imsg);
} catch(Exception e) {
}
   }

  public void AnimationSpeed() {
    if ( !hubOK() ) return;
    long speed = grapher.getSpeed();
    if ( as_dialog == null ) {
       as_dialog = new NumberDialog(this, "Set Animation Speed", "Enter speed:");
       as_dialog.setLocationRelativeTo(this);
    }
    as_dialog.setValue(speed);
    Long value = as_dialog.getValue();
    if ( value != null )
       grapher.setSpeed(value.longValue());
  }

  protected boolean statisticIsOneOf(int st) {
     if ( statistics_type != NIL && ((statistics_type & st) == 0) ) {
        JOptionPane.showMessageDialog(this, "Invalid chart type for current statistic");
        return false;
     }
     return true;
  }
  public void RedrawGraph() {
     if ( grapher != null )
        grapher.drawChart();
  }
  public void DrawPieGraph() {
     if ( !statisticIsOneOf(BAT|TVT) ) return;
     chart_type = PIE;
     if ( grapher != null )
        grapher.drawChart();
  }
  public void DrawBarGraph() {
     if ( !statisticIsOneOf(BAT|TVT|TVA|MPG) ) return;
     chart_type = BAR;
     if ( grapher != null )
        grapher.drawChart();
  }
  public void DrawLineGraph() {
     if ( !statisticIsOneOf(BAT|TVT|TVA|MPG) ) return;
     chart_type = LIN;
     if ( grapher != null )
        grapher.drawChart();
  }
  public void DrawXYGraph() {
     if ( !statisticIsOneOf(NEG) ) return;
     chart_type = XYG;
     if ( grapher != null )
        grapher.drawChart();
  }
  public void DrawTabularGraph() {
     if ( !statisticIsOneOf(IAT) ) return;
     chart_type = TAB;
     if ( grapher != null )
        grapher.drawChart();
  }

  public void setStatisticsType(int type) {
     if ( grapher == null ) return;

     Object[] user_choice;
     Object[] prior_goals;
     String[] current_goals;

     switch(type) {
        case NIL: // None
             break;
        case BAT: // Breakdown of Agent Types
             if ( (chart_type & (PIE|BAR|LIN)) == 0 )
                chart_type = PIE;
             statistics_type = type;
             grapher.drawChart();
             break;
        case TVT: // Traffic Volume by Type
             if ( (chart_type & (PIE|BAR|LIN)) == 0 )
                chart_type = BAR;
             statistics_type = type;
             grapher.drawChart();
             break;
        case TVA: // Traffic Volume by Agent
             if ( (chart_type & (BAR|LIN)) == 0 )
                chart_type = BAR;
             statistics_type = type;
             grapher.drawChart();
             break;
        case IAT: // Inter Agent Traffic Volume
             if ( (chart_type & (TAB)) == 0 )
                chart_type = TAB;
             statistics_type = type;
             grapher.drawChart();
             break;
        case MPG: // Messages per Goal
             current_goals = msgQueue.getCurrentGoals();
             if ( current_goals.length == 0 ) {
                JOptionPane.showMessageDialog(this,"No goals currently available");
                menubar.setStatisticsType(statistics_type);
                return;
             }

             if ( ms_dialog == null ) {
                ms_dialog = new MultipleSelectionDialog(this,"Select Required Goals");
                ms_dialog.setLocationRelativeTo(this);
             }

             prior_goals = ms_dialog.getPriorSelection();
             ms_dialog.setListData(current_goals);
             ms_dialog.setSelection(prior_goals);
             user_choice = ms_dialog.getSelection();

             if ( user_choice == null ) return;

             if ( (chart_type & (BAR|LIN)) == 0 )
                chart_type = BAR;
             statistics_type = type;

             grapher.setUserGoals(Misc.stringArray(user_choice));
             grapher.drawChart();
             break;

	case NEG: // Inter-agent negotiation graph
             current_goals = msgQueue.getCurrentGoals();
             if ( current_goals.length == 0 ) {
                JOptionPane.showMessageDialog(this,"No goals currently available");
                menubar.setStatisticsType(statistics_type);
                return;
             }

             Hashtable input = msgQueue.getNegotiationGoals();
             if ( neg_dialog == null ) {
                neg_dialog = new DoubleSelectionDialog(this,
	           "Goal & Negotiation Partners","Goal","Negotiation Partners");
                neg_dialog.setLocationRelativeTo(this);
             }

             prior_goals = neg_dialog.getPriorSelection();
             neg_dialog.setListData(input);
             neg_dialog.setSelection(prior_goals[0],prior_goals[1]);
             user_choice = neg_dialog.getSelection();

             if ( user_choice == null ) return;

             if ( (chart_type & XYG) == 0 )
                chart_type = XYG;
             statistics_type = type;

             grapher.setUserGoals(Misc.stringArray(user_choice));
             grapher.drawChart();
             break;

	case TAL: // Task Activity Level
        case GCS: // Goal Completion Status
        case AEM: // Agent Efficiency Measure
        case AMS: // Agent Monetary Statement
        case CRR: // Plan/Resources Ratio
        case GLT: // Average Goal Lapse Times
             statistics_type = type;
             grapher.drawChart();
             break;

        default:
           break;
     }
  }

  public void Help()   {
     Point pt = getLocation();
     HelpWindow helpWin = new HelpWindow(this, pt, "visualiser", "Statistics Tool");
     helpWin.setSize(new Dimension(getWidth(),440));
     helpWin.setLocation(pt.x+24, pt.y+24);
     helpWin.validate();
   }

class StatisticsMenuBar extends JMenuBar
                               implements ActionListener,
                                          ItemListener {

    protected static final int CHECKITEM = 0;
    protected static final int PLAINITEM = 1;
    protected static final int RADIOITEM = 2;

    protected JMenu     fileMenu, onLineMenu, replayMenu;
    protected JMenu     optionsMenu, viewMenu, helpMenu;
    protected JMenu     animationMenu;
    protected JMenu     doMenu;
    protected JMenuItem sessions, load, save, close, forward, rewind,
                      fforward, frewind, stop, forward_step, rewind_step,
                      forward_last, rewind_first, delete, purge;
    protected JMenuItem connect, disconnect, exit;
    protected JMenuItem cc, un_cc;
    protected JMenuItem filter, animation_speed, player_speed, clear_goal;
    protected JMenuItem redraw, line, bar, pie, xy, table;
    protected JMenuItem help, about;
    protected JCheckBoxMenuItem save_goal;
    protected JRadioButtonMenuItem[] statsRadioBox;

    public StatisticsMenuBar()	{
      super();
      add(fileMenu());
      add(onlineMenu());
      add(playbackMenu());
      add(optionsMenu());
      add(doMenu());
      add(viewMenu());
      add(helpMenu());
    }

    private JMenu fileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        connect = createMenuItem(menu,PLAINITEM,"Connect to namservers", 'C');
        disconnect=createMenuItem(menu,PLAINITEM,"Disconnect from nameservers",'D');
        exit = createMenuItem(menu, PLAINITEM, "Quit", 'Q');
        return menu;
    }

    private JMenu onlineMenu() {
        JMenu menu = new JMenu("Online");
        menu.setMnemonic('O');
        cc    = createMenuItem(menu, PLAINITEM, "Request messages...", 'R');
        un_cc = createMenuItem(menu, PLAINITEM, "Unrequest messages...", 'U');
        return menu;
    }

    private JMenu playbackMenu() {
        JMenu menu = new JMenu("Playback");
        menu.setMnemonic('P');
        sessions = createMenuItem(menu, PLAINITEM, "Request saved sessions", 0);
        delete = createMenuItem(menu, PLAINITEM, "Delete session", 0);
        purge = createMenuItem(menu, PLAINITEM, "Purge database", 0);
        menu.addSeparator();
        load = createMenuItem(menu, PLAINITEM, "Load session", 0);
        save = createMenuItem(menu, PLAINITEM, "Save session", 0);
        close = createMenuItem(menu, PLAINITEM, "Close session", 0);
        menu.addSeparator();
        forward = createMenuItem(menu, PLAINITEM, "Forward", 0);
        rewind = createMenuItem(menu, PLAINITEM, "Rewind", 0);
        fforward = createMenuItem(menu, PLAINITEM, "Fast forward", 0);
        frewind = createMenuItem(menu, PLAINITEM, "Fast rewind", 0);
        forward_step = createMenuItem(menu, PLAINITEM, "Step forward", 0);
        rewind_step = createMenuItem(menu, PLAINITEM, "Step backward", 0);
        forward_last = createMenuItem(menu, PLAINITEM, "Forward to end", 0);
        rewind_first = createMenuItem(menu, PLAINITEM, "Rewind to beginning", 0);
        stop = createMenuItem(menu, PLAINITEM, "Stop", 0);
        return menu;
    }

   private JMenu optionsMenu() {
        JMenu menu = new JMenu("Options");
        menu.setMnemonic('O');
        filter       = createMenuItem(menu, PLAINITEM, "Filter messages...", 0);
        player_speed = createMenuItem(menu, PLAINITEM, "Player speed...", 0);
        animation_speed = createMenuItem(menu, PLAINITEM, "Animation speed...", 0);
        save_goal  = (JCheckBoxMenuItem) createMenuItem(menu, CHECKITEM, "Save goal traffic data", 0);
        clear_goal = createMenuItem(menu, PLAINITEM, "Clear goal traffic data", 0);

        save_goal.setState(StatisticsTool.this.msgQueue.isUpdatingGoalTraffic());
        return menu;
    }

    private JMenu doMenu() {
        JMenu menu = new JMenu("Statistics");
        ButtonGroup group = new ButtonGroup();
        menu.setMnemonic('S');
        statsRadioBox = new JRadioButtonMenuItem[STATS_MENU_ITEMS.length];
        for(int i= 0; i < STATS_MENU_ITEMS.length; i++) {
           statsRadioBox[i] = new JRadioButtonMenuItem(STATS_MENU_ITEMS[i]);
           statsRadioBox[i].addActionListener(this);
           if ( i >= 6 ) statsRadioBox[i].setEnabled(false);
           menu.add(statsRadioBox[i]);
           group.add(statsRadioBox[i]);
       	}
        return menu;
    }

    private JMenu viewMenu() {
        JMenu menu = new JMenu("View");
        menu.setMnemonic('V');
        redraw = createMenuItem(menu, PLAINITEM, "Redraw current", 'R');
        pie    = createMenuItem(menu, PLAINITEM, "Pie chart", 'P');
        line   = createMenuItem(menu, PLAINITEM, "Line graph",  0);
        xy     = createMenuItem(menu, PLAINITEM, "XY graph", 'X');
        table  = createMenuItem(menu, PLAINITEM, "Table", 'T');
        bar    = createMenuItem(menu, PLAINITEM, "Bar chart", 'B');
        return menu;
    }

    private JMenu helpMenu() {
        JMenu menu = new JMenu("Help");
        menu.setMnemonic('H');
        help  = createMenuItem(menu, PLAINITEM, "Using the statistics tool", 'U');
        about = createMenuItem(menu, PLAINITEM, "About ZEUS...", 'A');
        return menu;
    }

    private JMenuItem createMenuItem(JMenu menu, int type, String text, int accelKey)	{
        JMenuItem item;
        switch(type) {
            case CHECKITEM:
                item = new JCheckBoxMenuItem(text);
                ((JCheckBoxMenuItem) item).setState(false);
                item.addItemListener(this);
                break;
            case RADIOITEM:
                item = new JRadioButtonMenuItem(text,false);
                item.addActionListener(this);
            default:
                item = new JMenuItem(text);
                item.addActionListener(this);
                break;
        }
        if (accelKey > 0)
           item.setMnemonic(accelKey);
        menu.add(item);
        return item;
    }



    public void actionPerformed(ActionEvent event) {
      Object src = event.getSource();

      if ( src == connect ) 		Connect(true);
      if ( src == disconnect ) 		Connect(false);
      else if ( src == exit )		Exit();

      else if ( src == cc)		StreamMessages(true);
      else if ( src == un_cc )		StreamMessages(false);

      else if ( src == sessions)	Sessions();
      else if ( src == delete)		Delete();
      else if ( src == purge)		Purge();
      else if ( src == load)		Load();
      else if ( src == save )		Record();
      else if ( src == close )		Close();
      else if ( src == forward )	Forward();
      else if ( src == rewind )		Rewind();
      else if ( src == fforward )	FForward();
      else if ( src == frewind )	FRewind();
      else if ( src == stop )		Stop();
      else if ( src == forward_step )	StepForward();
      else if ( src == forward_last )	ForwardEnd();
      else if ( src == rewind_step )	StepRewind();
      else if ( src == rewind_first )	RewindBegin();

      else if ( src == clear_goal ) 	ClearGoalTraffic();

      else if ( src == filter )		Filter();
      else if ( src == player_speed )	PlayerSpeed();
      else if ( src == animation_speed) AnimationSpeed();

      else if ( src == redraw )	RedrawGraph();
      else if ( src == xy )	DrawXYGraph();
      else if ( src == table )	DrawTabularGraph();
      else if ( src == line )	DrawLineGraph();
      else if ( src == pie )	DrawPieGraph();
      else if ( src == bar )  	DrawBarGraph();

      else if (src == help)	Help();
      else if (src == about)	About();

      else
      	for(int i = 0; i < STATS_MENU_ITEMS.length; i++ )
	   if ( statsRadioBox[i] == src )
              StatisticsTool.this.setStatisticsType((int)(Math.pow(2,(double)i) +0.51));
  }

   public void itemStateChanged(ItemEvent event) {
      Object src = event.getSource();

      if ( src == save_goal ) {
         boolean v = save_goal.getState();
         SaveGoalTraffic(v);
         return;
      }
   }

   public void setStatisticsType(int type) {
      if ( type == 0 ) {
         for(int j = 0; j < STATS_MENU_ITEMS.length; j++ )
            statsRadioBox[j].setSelected(false);
         return;
      }
      int n = (int)(Math.log((double)type)/Math.log(2) + 0.51);
      statsRadioBox[n].setSelected(true);
   }

   public void update(int mode) {
      boolean b = mode == StatisticsTool.this.PLAYBACK;
      cc.setEnabled(!b);

      forward.setEnabled(b);
      rewind.setEnabled(b);
      fforward.setEnabled(b);
      frewind.setEnabled(b);
      stop.setEnabled(b);
      forward_step.setEnabled(b);
      rewind_step.setEnabled(b);
      forward_last.setEnabled(b);
      rewind_first.setEnabled(b);
   }
}


protected class StatisticsToolBar extends JToolBar implements ActionListener {
  protected JButton  barBtn, pieBtn, lineBtn, xyBtn, tabularBtn;

  public StatisticsToolBar() {
    setFloatable(false);

    String path = SystemProps.getProperty("gif.dir") + File.separator +
                  "visualiser" + File.separator;
    addSeparator();

    // Bar Button
    barBtn = new JButton(new ImageIcon(path + "bar.gif"));
    add(barBtn);
    barBtn.setPreferredSize(new Dimension(24,24));
    barBtn.setToolTipText("Show as Bar Chart");
    barBtn.setMargin(new Insets(0,0,0,0));
    addSeparator();

    // Pie Button
    pieBtn = new JButton(new ImageIcon(path + "pie.gif"));
    add(pieBtn);
    pieBtn.setPreferredSize(new Dimension(24,24));
    pieBtn.setToolTipText("Show as Pie Chart");
    pieBtn.setMargin(new Insets(0,0,0,0));
    addSeparator();

    // Line Button
    lineBtn = new JButton(new ImageIcon(path + "line.gif"));
    add(lineBtn);
    lineBtn.setPreferredSize(new Dimension(24,24));
    lineBtn.setToolTipText("Show as Line Chart");
    lineBtn.setMargin(new Insets(0,0,0,0));
    addSeparator();

    // XY Button
    xyBtn = new JButton(new ImageIcon(path + "xy.gif"));
    add(xyBtn);
    xyBtn.setPreferredSize(new Dimension(24,24));
    xyBtn.setToolTipText("Show as XY Chart");
    xyBtn.setMargin(new Insets(0,0,0,0));
    addSeparator();

    // Tabluar Button
    tabularBtn = new JButton(new ImageIcon(path + "table.gif"));
    add(tabularBtn);
    tabularBtn.setPreferredSize(new Dimension(24,24));
    tabularBtn.setToolTipText("Show as Tabular Chart");
    tabularBtn.setMargin(new Insets(0,0,0,0));
    addSeparator();

    setPreferredSize(new Dimension(300,32));

    barBtn.addActionListener(this);
    pieBtn.addActionListener(this);
    lineBtn.addActionListener(this);
    xyBtn.addActionListener(this);
    tabularBtn.addActionListener(this);
  }

  public void actionPerformed(ActionEvent evt) {
     Object src = evt.getSource();
          if ( src == xyBtn      ) DrawXYGraph();
     else if ( src == tabularBtn ) DrawTabularGraph();
     else if ( src == barBtn     ) DrawBarGraph();
     else if ( src == pieBtn     ) DrawPieGraph();
     else if ( src == lineBtn    ) DrawLineGraph();
  }
}

class Grapher extends Thread {
   protected long speed = 3000;
   protected boolean running = true;
   protected PieChart pie = new PieChart();
   protected Histogram bar = new Histogram();
   protected LineGraph line = new LineGraph();
   protected XYGraph xy = new XYGraph();
   protected TabularGraph tabular = new TabularGraph();
   protected String[] user_goals;

   public String[] AGENT_TYPES = {
      SystemProps.getProperty("agent.names.nameserver"),
      SystemProps.getProperty("agent.names.facilitator"),
      SystemProps.getProperty("agent.names.visualiser"),
      SystemProps.getProperty("agent.names.dbProxy"),
      SystemProps.getProperty("agent.names.agent")
   };

   public Grapher()	 {
      this.setPriority(Thread.NORM_PRIORITY-3);
      this.start();
   }

   public void setSpeed(long speed) {
      this.speed = speed;
   }

   public long getSpeed() {
      return speed;
   }

   public void run() {
      long count = 0;
      while( running ) {
         synchronized(this) {
            try {
               while( !StatisticsTool.this.state.animating ||
	              chart_type == StatisticsTool.NIL ||
                      statistics_type == StatisticsTool.NIL )
                  wait(speed);
               drawChart();
               wait(speed);
            }
            catch(InterruptedException e) {
            }
         }
      }
   }

   public void terminate() {
      running = false;
   }

   public void wakeup() {
      notifyAll();
   }

   public void setUserGoals(String[] goals) {
      user_goals = goals;
   }
   public String[] getUserGoals() {
      return user_goals;
   }

   public void drawChart() {
      double[][] values = null;
      double[][] x_values = null;
      double[]   data = null;
      double[]   x_data = null;
      String[]   labels = null;
      String[]   keys = null;
      String     title = null;

      switch( statistics_type ) {
         case StatisticsTool.NIL: // None
            return;

         case StatisticsTool.BAT:
            title = "Breakdown of Agent Types";
            labels = AGENT_TYPES;
            data = new double[AGENT_TYPES.length];
            String[] s;
            for(int i = 0; i < data.length; i++ ) {
               s = StatisticsTool.this.model.getAgents(AGENT_TYPES[i]);
               data[i] = (s == null) ? 0.0 : s.length;
            }
            break;

         case StatisticsTool.TVT:
            title = "Traffic Volume: Distribution by Type";
            labels = msgQueue.getDistributionByTypeLabels();
            data = msgQueue.getDistributionByTypeData();
            break;

         case StatisticsTool.TVA:
            title = "Traffic Volume: Distribution by Agent";
            labels = msgQueue.getDistributionByAgentLabels();
            values = msgQueue.getDistributionByAgentData();
            keys = msgQueue.getDistributionByAgentKeys();
            if ( values == null ) return;
            break;

         case StatisticsTool.IAT: // Inter-Agent Traffic Volume
            title = "Inter-Agent Traffic Volume";
            labels = msgQueue.getInterAgentTrafficLabels();
            values = msgQueue.getInterAgentTrafficData();
            if ( values == null ) return;
            break;

         case StatisticsTool.MPG: // Messages per Goal
            title = "Traffic Volume for Selected Goals: Distribution by Agent";
            labels = msgQueue.getDistributionByGoalLabels(user_goals);
            values = msgQueue.getDistributionByGoalData(user_goals);
            keys = msgQueue.getDistributionByGoalKeys();
            if ( values == null ) return;
            break;

         case StatisticsTool.NEG: // Negotiation graphs
            title = "Negotiation Graph for " + user_goals[1];
            values = msgQueue.getDistributionByNegotiationDialogueData(user_goals);
            x_values = msgQueue.getDistributionByNegotiationDialogueXData(user_goals);
            keys = msgQueue.getDistributionByNegotiationDialogueKeys(user_goals);
            if ( x_values == null ) return;
            break;

         case StatisticsTool.TAL: // Task Activity Level
         case StatisticsTool.GCS: // Goal Completion Status
         case StatisticsTool.AEM: // Agent Efficiency Measure
         case StatisticsTool.AMS: // Agent Monetary Statement
         case StatisticsTool.CRR: // Plan/Resources Ratio
         case StatisticsTool.GLT: // Average Goal Lapse Times
            return;

         default:
            return;
      }

      Dimension d;
      switch( chart_type ) {
         case StatisticsTool.NIL:
            return;

         case  StatisticsTool.PIE:
            pie.setData(data,labels,title);
            d = getViewportSize();
            pie.setXY(d.width,d.height);
	    canvas.setDrawType(pie);
            canvas.repaint();
            return;

         case  StatisticsTool.BAR:
            if ( keys != null )
               bar.setData(values,labels,keys,title);
            else
               bar.setData(data,labels,title);
            d = getViewportSize();
            bar.setXY(d.width,d.height);
            canvas.setDrawType(bar);
            canvas.repaint();
            return;

         case  StatisticsTool.LIN:
            if ( keys != null )
               line.setData(values,labels,keys,title);
            else
               line.setData(data,labels,title);
            d = getViewportSize();
            line.setXY(d.width,d.height);
            canvas.setDrawType(line);
            canvas.repaint();
            return;

         case  StatisticsTool.TAB:
            tabular.setData(values,labels,title);
            d = getViewportSize();
            tabular.setXY(d.width,d.height);
            canvas.setDrawType(tabular);
            canvas.repaint();
            return;

         case  StatisticsTool.XYG:
            if ( keys != null )
               xy.setData(values,x_values,keys,title);
            else
               xy.setData(data,x_data,title);
            d = getViewportSize();
            xy.setXY(d.width,d.height);
            canvas.setDrawType(xy);
            canvas.repaint();
            return;

         default:
            return;
      }
   }
}

}
