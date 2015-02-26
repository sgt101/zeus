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



/*******************************************************************
 *          ZEUS Agent Society Viewer with Video Tools             *
 *        Allows the user to view the interaction of agents        *
 *         and use video tool features to examine sessions         *
 *******************************************************************/

package zeus.visualiser.society;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.graph.*;
import zeus.gui.help.*;
import zeus.actors.*;
import zeus.visualiser.VisualiserModel;
import zeus.visualiser.basic.*;


public class SocietyTool extends VideoTool {
   private static int count = 0;

   private EditableMultipleSelectionDialog update_dialog   = null;
   private EditableMultipleSelectionDialog message_dialog  = null;
   private NumberDialog                    number_dialog = null;

   private SocietyMenuBar menubar;

   protected SocietyModel     onlineModel, offlineModel;
   protected AnimationManager animationManager;
   protected Graph            graph;

   public SocietyTool(AgentContext context, VisualiserModel model) {
     super(context,model);
     setTitle(context.whoami() + " - SocietyTool:" + (count++));
     String path = SystemProps.getProperty("gif.dir") + File.separator +
                   "visualiser" + File.separator + "society-icon.gif";
     ImageIcon icon = new ImageIcon(path);
     setIconImage(icon.getImage());
     setBackground(Color.lightGray);

     onlineModel = new SocietyModel();
     offlineModel = new SocietyModel();

     SocietyPanel societyPanel = new SocietyPanel(onlineModel);
     graph = societyPanel.getGraph();
     animationManager = new AnimationManager(context,graph);

     // Create a panel to model the frame contents
     Container pane = getContentPane();
     pane.setLayout(new BorderLayout());
     pane.add(societyPanel, BorderLayout.CENTER);
     pane.add(videoToolbar,BorderLayout.NORTH);

     menubar = new SocietyMenuBar();
     setJMenuBar(menubar);

     pack();
     setVisible(true);
     setMode(ONLINE);
     initialRegistration();
   }

   public void Exit() {
     if ( animationManager != null )
        animationManager.terminate();
     super.Exit();
   }

   public void UpdateRelations() {
      if ( !hubOK() ) return;
      if ( !stopPlayback() ) return;
  
      String[] agents = model.getAgents();
      if ( update_dialog == null ) {
         update_dialog = new EditableMultipleSelectionDialog(this,
            "Select Agents", agents);
         update_dialog.setLocationRelativeTo(this);
      }
      else {
         Object[] chosen = update_dialog.getPriorSelection();
         update_dialog.setListData(agents);
         update_dialog.setSelection(chosen);
      }

      Object[] data = update_dialog.getSelection();
      model.addAgents(Misc.stringArray(update_dialog.getListData()));
      if ( data != null && data.length > 0 )
         query("your_relations",Misc.stringArray(data),"log_relations");
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
        graph.setModel(onlineModel);
     }
     else {
        menubar.update(PLAYBACK);
        videoToolbar.setStatus(true);
        graph.setModel(offlineModel);
     }
   }

   protected void initialRegistration() {
      String type = SystemProps.getProperty("agent.names.agent");
      String[] agent = model.getAgents(type);
      for(int i = 0; i < agent.length; i++ )
         registerAgent(agent[i],type);

      type = SystemProps.getProperty("agent.names.facilitator");
      agent = model.getAgents(type);
      for(int i = 0; i <  agent.length; i++ )
         registerAgent(agent[i],type);

      type = SystemProps.getProperty("agent.names.visualiser");
      agent = model.getAgents(type);
      for(int i = 0; i <  agent.length; i++ )
         registerAgent(agent[i],type);

      type = SystemProps.getProperty("agent.names.dbProxy");
      agent = model.getAgents(type);
      for(int i = 0; i <  agent.length; i++ )
         registerAgent(agent[i],type);

      type = SystemProps.getProperty("agent.names.nameserver");
      agent = model.getAgents(type);
      for(int i = 0; i <  agent.length; i++ )
         registerAgent(agent[i],type);
   }

   protected void registerAgent(String name, String type) {
      onlineModel.addAgent(name,type);
      debug ("added online name = " + name ); 
   }

   protected void registerListOfPlaybackAgents(Vector List) {
      offlineModel.addAgents(List);
   }

   protected void  visualiseVideoData(int dir, Performative msg) {
      if ( state.mode == PLAYBACK && state.animating && filterMsg(msg) ) {
         // check if going forwards or backwards
         if ( dir == BACKWARD ) {
            String sender = msg.getReceiver();
            msg.setReceiver(msg.getSender());
            msg.setSender(sender);
         }
         animationManager.animate(msg);
      }
   }

   public void log_relations(Performative msg) {
      if (!msg.getType().equals("inform") ) return;
      Vector List = ZeusParser.relationshipList(msg.getContent());
      String agent = msg.getSender();
      onlineModel.addRelations(agent,List);
   }

   public void log_message(Performative msg) {
    debug("log_message 1"); 
try {
      Performative imsg = ZeusParser.performative(msg.getContent());
         debug("log_message 2"); 
      if ( state.mode == ONLINE && state.animating && filterMsg(imsg) )
         animationManager.animate(imsg);
            debug("log_message 3"); 

      // save to persistent db
      record_item(imsg);
}
catch(Exception e) {
       e.printStackTrace();
}
   }

   void Animation(boolean set) {
      state.animating = set;
      if ( !set ) animationManager.flushAnimator();
   }

   void setAnimationMode(int mode) {
      animationManager.setMode(mode);
   }

   void AnimationSpeed() {
      long speed = animationManager.getSpeed();
      if ( number_dialog == null ) {
         number_dialog = new NumberDialog(this,"Set Animation Speed",
            "Enter speed:");
         number_dialog.setLocationRelativeTo(this);
      }
      number_dialog.setValue(speed);
      Long value = number_dialog.getValue();
      if (value != null)
         animationManager.setSpeed(value.longValue());
   }

   void Help() {
      Point pt = getLocation();
      HelpWindow helpWin = new HelpWindow(this, pt, "visualiser", "Society Viewer");
      helpWin.setSize(new Dimension(getWidth(), 500));
      helpWin.setLocation(pt.x+24, pt.y+24);
      helpWin.validate();
   }

   public class SocietyMenuBar extends JMenuBar
                               implements ActionListener, ItemListener {

     protected JMenuItem connect, disconnect, exit;
     protected JMenuItem sessions, load, save, close, forward, rewind,
                         fforward, frewind, stop, forward_step, rewind_step,
                         forward_last, rewind_first, delete, purge;

     protected JMenuItem cc, update, un_cc;
     protected JMenuItem filter, animation_speed, player_speed;
     protected JMenuItem collapse, expand, recompute, redraw;
     protected JMenuItem select, selectAll, hide, show;
     protected JMenuItem help, about;
     protected JMenuItem onOff;
     protected JRadioButtonMenuItem letterAnim, arrowAnim;
   
     protected static final int CHECK = 0;
     protected static final int PLAIN = 1;
     protected static final int RADIO = 2;

     public SocietyMenuBar() {
        add(fileMenu());
        add(onlineMenu());
        add(playbackMenu());
        add(optionsMenu());
        add(viewMenu());
        add(helpMenu());
     }
   
     private JMenu fileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        connect = createMenuItem(menu,PLAIN,"Connect to namservers", 'C');
        disconnect=createMenuItem(menu,PLAIN,"Disconnect from nameservers",'D');
        exit = createMenuItem(menu, PLAIN, "Quit", 'Q');
        return menu;
     }
   
       private JMenu onlineMenu() {
           JMenu menu = new JMenu("Online");
           menu.setMnemonic('O');
           update = createMenuItem(menu, PLAIN, "Update relations", 'U');
           cc = createMenuItem(menu, PLAIN, "Request messages", 'R');
           un_cc = createMenuItem(menu, PLAIN, "Un-request messages", 'N');
           return menu;
       }
   
       private JMenu playbackMenu() {
           JMenu menu = new JMenu("Playback");
           menu.setMnemonic('P');
           sessions = createMenuItem(menu, PLAIN, "Request saved sessions", 0);
           delete = createMenuItem(menu, PLAIN, "Delete session", 0);
           purge = createMenuItem(menu, PLAIN, "Purge database", 0);
           menu.addSeparator();
           load = createMenuItem(menu, PLAIN, "Load session", 0);
           save = createMenuItem(menu, PLAIN, "Save session", 0);
           close = createMenuItem(menu, PLAIN, "Close session", 0);
           menu.addSeparator();
           forward = createMenuItem(menu, PLAIN, "Forward", 0);
           rewind = createMenuItem(menu, PLAIN, "Rewind", 0);
           fforward = createMenuItem(menu, PLAIN, "Fast forward", 0);
           frewind = createMenuItem(menu, PLAIN, "Fast rewind", 0);
           forward_step = createMenuItem(menu, PLAIN, "Step forward", 0);
           rewind_step = createMenuItem(menu, PLAIN, "Step backward", 0);
           forward_last = createMenuItem(menu, PLAIN, "Forward to end", 0);
           rewind_first = createMenuItem(menu, PLAIN, "Rewind to beginning", 0);
           stop = createMenuItem(menu, PLAIN, "Stop", 0);
           return menu;
       }
   
       private JMenu optionsMenu() {
           JMenu menu = new JMenu("Options");
           menu.setMnemonic('T');
           filter = createMenuItem(menu, PLAIN, "Filter messages", 0);
           player_speed = createMenuItem(menu, PLAIN, "Player speed...", 0);
           animation_speed = createMenuItem(menu,PLAIN,"Animation speed...",0);
           JMenu animMenu = new JMenu("Animation mode");
           menu.add(animMenu);
           int mode = SocietyTool.this.animationManager.getMode();
           arrowAnim = new JRadioButtonMenuItem("Arrow",
              mode == AnimationQueue.ARROWS);
           arrowAnim.addActionListener(this);
           letterAnim = new JRadioButtonMenuItem("Letter",
              mode == AnimationQueue.LETTER);
           letterAnim.addActionListener(this);
           animMenu.add(letterAnim);
           animMenu.add(arrowAnim);
           ButtonGroup animGroup = new ButtonGroup();
           animGroup.add(arrowAnim);
           animGroup.add(letterAnim);
           onOff = createMenuItem(menu, CHECK, "Animation", 0);
           ((JCheckBoxMenuItem)onOff).setState(SocietyTool.this.state.animating);
           return menu;
       }
   
       private JMenu viewMenu() {
           JMenu menu = new JMenu("View");
           menu.setMnemonic('V');
           collapse = createMenuItem(menu, PLAIN, "Collapse", 0);
           expand = createMenuItem(menu, PLAIN, "Expand", 0);
           recompute = createMenuItem(menu, PLAIN, "Recompute", 0);
           redraw = createMenuItem(menu, PLAIN, "Redraw", 0);
           menu.addSeparator();
           select = createMenuItem(menu, PLAIN, "Select", 0);
           selectAll = createMenuItem(menu, PLAIN, "Select all", 0);
           menu.addSeparator();
           hide = createMenuItem(menu, PLAIN, "Hide", 0);
           show = createMenuItem(menu, PLAIN, "Show", 0);
           return menu;
       }
   
       private JMenu helpMenu() {
           JMenu menu = new JMenu("Help");
           menu.setMnemonic('H');
           help  = createMenuItem(menu, PLAIN, "Using the society tool", 'U');
           about = createMenuItem(menu, PLAIN, "About ZEUS...", 'A');
           return menu;
       }
   
       private JMenuItem createMenuItem(JMenu menu, int type,
          String text, int accelKey) {

           JMenuItem item;
           switch(type) {
               case CHECK :
                   item = new JCheckBoxMenuItem();
                   ((JCheckBoxMenuItem) item).setState(false);
                   item.addItemListener(this);
                   break;
               case RADIO :
                   item = new JRadioButtonMenuItem();
                   item.addActionListener(this);
               default :
                   item = new JMenuItem();
                   item.addActionListener(this);
                   break;
           }
           item.setText(text);
           if(accelKey > 0)
               item.setMnemonic(accelKey);
           menu.add(item);
           return item;
     }

     public void actionPerformed(ActionEvent event) {
       Object source = event.getSource();

       if ( source == connect )
         Connect(true);
       else if ( source == disconnect )
         Connect(false);
       else if ( source == exit )
         Exit();

       else if ( source == cc)
         StreamMessages(true);
       else if ( source == update )
         UpdateRelations();
       else if ( source == un_cc )
         StreamMessages(false);

       else if ( source == sessions)
         Sessions();
       else if ( source == delete)
         Delete();
       else if ( source == purge)
         Purge();
       else if ( source == load)
         Load();
       else if ( source == save )
         Record();
       else if ( source == close )
         Close();
       else if ( source == forward )
         Forward();
       else if ( source == rewind )
         Rewind();
       else if ( source == fforward )
         FForward();
       else if ( source == frewind )
         FRewind();
       else if ( source == stop )
         Stop();
       else if ( source == forward_step )
         StepForward();
       else if ( source == forward_last )
         ForwardEnd();
       else if ( source == rewind_step )
         StepRewind();
       else if ( source == rewind_first )
         RewindBegin();


       else if ( source == filter )
         Filter();
       else if ( source == player_speed )
         PlayerSpeed();
       else if ( source == animation_speed )
         AnimationSpeed();

       // The animation choice radio buttons
       else if( source == arrowAnim )
         setAnimationMode(AnimationQueue.ARROWS);
       else if( source == letterAnim )
         setAnimationMode(AnimationQueue.LETTER);
   
       else if ( source == collapse )
         graph.collapse();
       else if ( source == expand )
         graph.expand();
       else if ( source == recompute )
         graph.recompute();
       else if ( source == redraw )
         graph.redraw();
       else if ( source == select )
         graph.select();
       else if ( source == selectAll )
         graph.selectAll();
       else if ( source == hide )
         graph.hide();
       else if ( source == show )
         graph.show();
   
       else if ( source == help )
         Help();
       else if ( source == about )
         About();
     }

     public void itemStateChanged(ItemEvent event) {
       Object source = event.getSource();

       if ( source == onOff ) {
           boolean b = ((JCheckBoxMenuItem)onOff).getState();
           animation_speed.setEnabled(b);
           letterAnim.setEnabled(b);
           arrowAnim.setEnabled(b);
           Animation(b);
       }
     }

     public void update(int mode) {
       boolean b = mode == SocietyTool.this.PLAYBACK;

       cc.setEnabled(!b);
       update.setEnabled(!b);

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
    
    private void debug (String str) { 
          //  System.out.println("SocietyTool>> " + str); 
          }
}
