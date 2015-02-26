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
 *                    Video Tools for Zeus                         *
 *   Provides functionality to use record and playback feature     *
 *******************************************************************/

package zeus.visualiser.basic;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.actors.*;
import zeus.visualiser.*;


public abstract class VideoTool extends BasicTool {
  protected static final int PLAYBACK = 0;
  protected static final int ONLINE   = 1;

  protected static final int FORWARD  = 0;
  protected static final int BACKWARD = 1;

  private NumberDialog                    number_dialog = null;
  private EditableMultipleSelectionDialog dialog = null;
  private EditableDoubleSelectionDialog   proxy_dialog = null;

  protected VideoToolBar videoToolbar = null;
  protected StateInfo    state;

  public VideoTool(AgentContext context, VisualiserModel model) {
     super(context,model);
     state = new StateInfo();
     state.animating = true;
     state.mode = ONLINE;
     state.saving = false;
     videoToolbar = new VideoToolBar();
  }

  public void Exit() {
     this.setVisible(false);
     removeSubscriptions();
     if ( state.player != null ) {
        state.player.terminate();
        state.player = null;
     }
     this.dispose();
  }

  protected boolean stopPlayback() {
     if ( state.mode == PLAYBACK ) {
        int result = JOptionPane.showConfirmDialog(this,
           "There is an open database connection\nClose connection?",
           "Warning", JOptionPane.YES_NO_OPTION);
        if ( result == JOptionPane.YES_OPTION )
           Close();
     }
     return state.mode == ONLINE;
  }

  public void Sessions() {
     /**
       send a query to a DbProxy to return the names of
       saved sessions belonging to this agent
     */

     if ( !hubOK() ) return;

     String[] db_servers = model.getDbProxys();
     if ( dialog == null ) {
        dialog = new EditableMultipleSelectionDialog(this,"Select DbProxy");
        dialog.setLocationRelativeTo(this);
     }

     dialog.setListData(db_servers);
     Object[] selection = dialog.getSelection();

     if ( selection != null ) {
        model.addDbProxys(Misc.stringArray(dialog.getListData()));

        query("db_sessions " + getClass().getName(),
           Misc.stringArray(selection), "db_sessions");
     }
  }

  public void Delete() {
     /**
        send a request to a DbProxy to delete the specified
        saved session belonging to this agent
     */

     if ( !hubOK() ) return;

     String sessionType = this.getClass().getName();
     Hashtable input = model.getDbSessions(sessionType);
     if ( proxy_dialog == null ) {
        proxy_dialog = new EditableDoubleSelectionDialog(this,
	   "Select DbProxy & Session Name","DbProxy Agent","Session Name");
        proxy_dialog.setLocationRelativeTo(this);
     }

     proxy_dialog.setListData(input);
     Object[] selection = proxy_dialog.getSelection();

     if ( selection != null ) {
        Hashtable output = proxy_dialog.getListData();
	model.addDbSessions(sessionType,output);

        request("db_delete " + sessionType + " " + selection[1],
           (String)selection[0], "db_delete");
     }
  }

  public void Purge() {
     /**
        send a request to a DbProxy to purge all
        saved sessions belonging to this agent
     */

     if ( !hubOK() ) return;

     String[] db_servers = model.getDbProxys();
     if ( dialog == null ) {
        dialog = new EditableMultipleSelectionDialog(this,"Select DbProxy");
        dialog.setLocationRelativeTo(this);
     }

     dialog.setListData(db_servers);
     Object[] selection = dialog.getSelection();

     if ( selection != null ) {
        model.addDbProxys(Misc.stringArray(dialog.getListData()));

        request("db_purge " + getClass().getName(),
           Misc.stringArray(selection), "db_purge");
     }
  }

  public void Load() {
     if ( !hubOK() ) return;
     if ( !stopPlayback() ) return;

     if ( state.saving ) {
        int result = JOptionPane.showConfirmDialog(this,
            "There is an open database connection\n" +
            "and saving is in progress.\nClose current connection?",
	    "Warning", JOptionPane.YES_NO_OPTION);
        if ( result == JOptionPane.YES_OPTION)
           Close();
        else
           return;
     }

     String sessionType = this.getClass().getName();
     Hashtable input = model.getDbSessions(sessionType);
     if ( proxy_dialog == null ) {
        proxy_dialog = new EditableDoubleSelectionDialog(this,
	   "Select DbProxy & Session Name", "DbProxy Agent","Session Name");
        proxy_dialog.setLocationRelativeTo(this);
     }

     proxy_dialog.setListData(input);
     Object[] selection = proxy_dialog.getSelection();

     if ( selection != null ) {
        Hashtable output = proxy_dialog.getListData();
	model.addDbSessions(sessionType,output);

        // adjust buttons etc
        setMode(PLAYBACK);

        state.proxy = (String)selection[0];
        state.session = (String)selection[1];
        state.key = context.newId("LoadSessionKey");
        state.mode = PLAYBACK;
        state.player = new Player(this);

	request("db_open " + sessionType + " " + state.session +
	   " " + state.key, state.proxy, "db_open");
     }
  }

  public void Record() {
     if ( state.saving ) {
        int result = JOptionPane.showConfirmDialog(this,
            "There is an open database connection\n" +
            "and saving is in progress.\nClose current connection?",
	    "Warning", JOptionPane.YES_NO_OPTION);
        if ( result == JOptionPane.YES_OPTION)
           Close();
        else
           return;
     }

     String sessionType = this.getClass().getName();
     Hashtable input = model.getDbSessions(sessionType);
     if ( proxy_dialog == null ) {
        proxy_dialog = new EditableDoubleSelectionDialog(this,
	   "Select DbProxy & Session Name", "DbProxy Agent","Session Name");
        proxy_dialog.setLocationRelativeTo(this);
     }

     proxy_dialog.setListData(input);
     Object[] selection = proxy_dialog.getSelection();

     if ( selection != null ) {
        Hashtable output = proxy_dialog.getListData();
	model.addDbSessions(sessionType,output);

        state.proxy = (String)selection[0];
        state.session = (String)selection[1];
        state.key = context.newId("SaveSessionKey");
        state.errored = false;

	request("db_create " + sessionType + " " + state.session +
           " " + state.key, state.proxy, "db_create");
     }
  }

  public void Close() {
     if ( hubOK() && state.key != null ) {
        // close db connection
	request("db_close " + state.key, state.proxy, "db_close");
        state.key = null;
        // initialize view & reconstruct online society view
        if ( state.mode == PLAYBACK ) {
           if ( state.player != null ) {
              state.player.terminate();
              state.player = null;
           }
        }
     }
     setMode(ONLINE);
     state.mode = ONLINE;
     state.saving = false;
     state.errored = false;
  }

  public void Forward() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.forward();
  }

  public void Rewind() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.rewind();
  }

  public void FForward() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.fforward();
  }

  public void FRewind() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.frewind();
  }

  public void Stop() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.pause();
  }

  public void StepForward() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.forward_step();
  }

  public void ForwardEnd() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.last();
  }

  public void StepRewind() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.rewind_step();
  }

  public void RewindBegin() {
     if ( state.mode == PLAYBACK && !state.errored )
        state.player.first();
  }

  protected boolean checkErrorMsg(Performative msg, String message) {
     String type = msg.getType();
     if ( type.equals("failure") || type.equals("not-understood") ||
          type.equals("refuse") ) {
        JOptionPane.showMessageDialog(this, message, "Error",
	   JOptionPane.ERROR_MESSAGE);
        return true;
     }
     return false;
  }

  public void db_sessions(Performative msg) {
     if ( checkErrorMsg(msg,"Cannot list sessions") ) return;
     if ( msg.getType().equals("inform") ) {
        String sessionType = this.getClass().getName();
        String agent = msg.getSender();
        StringTokenizer st = new StringTokenizer(msg.getContent());
        while( st.hasMoreTokens() )
           model.addDbSession(sessionType,agent,st.nextToken());
     }
  }

  public void db_delete(Performative msg) {
     checkErrorMsg(msg,"Cannot delete database session");
  }

  public void db_purge(Performative msg) {
     checkErrorMsg(msg,"Cannot purge database");
  }

  public void db_close(Performative msg) {
     checkErrorMsg(msg,"Cannot close database");
  }

  public void db_save(Performative msg) {
     if ( checkErrorMsg(msg,"Cannot save item") )
        Close();
  }

  protected boolean record_item(Performative msg) {
     if ( state.mode != ONLINE || !state.saving || state.errored ) 
        return false;

     String s = msg.getSender();
     String r = msg.getReceiver();
     if ( (s.equals(context.whoami()) && r.equals(state.proxy)) ||
          (r.equals(context.whoami()) && s.equals(state.proxy))  )
        return false;

     request("db_save " + state.key + " " + msg, state.proxy, "db_save");
     return true;
  }


  public void db_create(Performative msg) {
     state.errored = checkErrorMsg(msg,
        "Cannot create database session.\nClosing link.");
     if ( state.errored ) {
        setMode(ONLINE);
        state.mode = ONLINE;
        state.saving = false;
        state.errored = false;
     }
     else
        state.saving = true;
  }

  public void db_prior(Performative msg) {
     if ( checkErrorMsg(msg,"End of file reached") )
        state.player.setCommand(Player.READY);
     else {
        Performative imsg = ZeusParser.performative(msg.getContent());
        visualiseVideoData(BACKWARD,imsg);
     }
  }
  public void db_next(Performative msg) {
     if ( checkErrorMsg(msg,"End of file reached") )
        state.player.setCommand(Player.READY);
     else {
        Performative imsg = ZeusParser.performative(msg.getContent());
        visualiseVideoData(FORWARD,imsg);
     }
  }

  public void db_first(Performative msg) {
     if ( checkErrorMsg(msg,"End of file reached") )
        state.player.setCommand(Player.READY);
  }

  public void db_last(Performative msg) {
     if ( checkErrorMsg(msg,"End of file reached") )
        state.player.setCommand(Player.READY);
  }

  public void db_open(Performative msg) {
     if ( checkErrorMsg(msg,"Cannot open database") ) {
        state.key = null;
        Close();
        return;
     }
     // db_open acknowledged; send LIST cmd
     state.player.setCommand(Player.LIST);
     query("db_list " + state.key, state.proxy, "db_list");
  }

  public void db_list(Performative msg) {
     if ( checkErrorMsg(msg,"Cannot obtain list of agents") ) {
        Close();
        return;
     }
     // db_list replied to; process list; cmd = COUNT
     StringTokenizer st = new StringTokenizer(msg.getContent());
     Vector info = new Vector();
     while( st.hasMoreTokens() )
        info.addElement(st.nextToken());
     registerListOfPlaybackAgents(info);
     state.player.setCommand(Player.COUNT);
     query("db_count " + state.key, state.proxy, "db_count");
  }

  public void db_count(Performative msg) {
     if ( checkErrorMsg(msg,"Cannot obtain count of itens") ) {
        Close();
        return;
     }
     // count replied to; intialize player; cmd = REDAY
     int max_count = Integer.parseInt(msg.getContent());
     state.player.setCount(max_count);
     state.player.setCommand(Player.READY);
  }

  void doPlayerCommand(String cmd) {
     query(cmd + " " + state.key, state.proxy, cmd);
  }

  public void PlayerSpeed() {
     long speed;

     speed = (state != null && state.player != null)
             ? state.player.getSpeed()
             : Player.getDefaultSpeed();
     if ( number_dialog == null ) {
        number_dialog = new NumberDialog(this,"Set Player Speed",
                                     "Enter speed:");
        number_dialog.setLocationRelativeTo(this);
     }

     number_dialog.setValue(speed);
     Long speedValue = number_dialog.getValue();
     if (speedValue != null) {
        if ( state != null && state.player != null)
           state.player.setSpeed(speedValue.longValue());
        else
           Player.setDefaultSpeed(speedValue.longValue());
     }
  }

  protected abstract void registerListOfPlaybackAgents(Vector info);
  protected abstract void visualiseVideoData(int dir, Performative msg);
  protected abstract void setMode(int mode);

  protected class VideoToolBar extends JToolBar implements ActionListener {
     protected JButton  firstBtn, fastRewindBtn;
     protected JButton  rewindBtn, stepRewindBtn;
     protected JButton  stopBtn, stepForwardBtn;
     protected JButton  forwardBtn, fastForwardBtn;
     protected JButton  lastBtn, recordBtn;
   
     public VideoToolBar() {
        setFloatable(false);
    
        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + sep +
           "visualiser" + sep;
    
        // Last Button
        firstBtn = new JButton(new ImageIcon(path + "first.gif"));
        add(firstBtn);
        firstBtn.setPreferredSize(new Dimension(24,24));
        firstBtn.setToolTipText("First");
        firstBtn.setMargin(new Insets(0,0,0,0));
    
        // Fast Rewind Button
        fastRewindBtn = new JButton(new ImageIcon(path + "fbwd.gif"));
        add(fastRewindBtn);
        fastRewindBtn.setPreferredSize(new Dimension(24,24));
        fastRewindBtn.setToolTipText("Fast Rewind");
        fastRewindBtn.setMargin(new Insets(0,0,0,0));
    
        // Rewind Button
        rewindBtn = new JButton(new ImageIcon(path + "bwd.gif"));
        add(rewindBtn);
        rewindBtn.setPreferredSize(new Dimension(24,24));
        rewindBtn.setToolTipText("Rewind");
        rewindBtn.setMargin(new Insets(0,0,0,0));
    
        // Step Rewind Button
        stepRewindBtn = new JButton(new ImageIcon(path + "prior.gif"));
        add(stepRewindBtn);
        stepRewindBtn.setPreferredSize(new Dimension(24,24));
        stepRewindBtn.setToolTipText("Prior");
        stepRewindBtn.setMargin(new Insets(0,0,0,0));
        addSeparator();
    
        // Stop Button
        stopBtn = new JButton(new ImageIcon(path + "stop.gif"));
        add(stopBtn);
        stopBtn.setPreferredSize(new Dimension(24,24));
        stopBtn.setToolTipText("Stop");
        stopBtn.setMargin(new Insets(0,0,0,0));
        addSeparator();
    
        // Step Forward Button
        stepForwardBtn = new JButton(new ImageIcon(path + "next.gif"));
        add(stepForwardBtn);
        stepForwardBtn.setPreferredSize(new Dimension(24,24));
        stepForwardBtn.setToolTipText("Next");
        stepForwardBtn.setMargin(new Insets(0,0,0,0));
    
        // Forward Button
        forwardBtn = new JButton(new ImageIcon(path + "fwd.gif"));
        add(forwardBtn);
        forwardBtn.setPreferredSize(new Dimension(24,24));
        forwardBtn.setToolTipText("Play");
        forwardBtn.setMargin(new Insets(0,0,0,0));
    
        // Fast Forward Button
        fastForwardBtn = new JButton(new ImageIcon(path + "ffwd.gif"));
        add(fastForwardBtn);
        fastForwardBtn.setPreferredSize(new Dimension(24,24));
        fastForwardBtn.setToolTipText("Fast Forward");
        fastForwardBtn.setMargin(new Insets(0,0,0,0));
    
        // Last Button
        lastBtn = new JButton(new ImageIcon(path + "last.gif"));
        add(lastBtn);
        lastBtn.setPreferredSize(new Dimension(24,24));
        lastBtn.setToolTipText("Last");
        lastBtn.setMargin(new Insets(0,0,0,0));
        addSeparator();
    
        // Record Button
        recordBtn = new JButton(new ImageIcon(path + "rec.gif"));
        add(recordBtn);
        recordBtn.setPreferredSize(new Dimension(24,24));
        recordBtn.setToolTipText("Record");
        recordBtn.setMargin(new Insets(0,0,0,0));
    
        firstBtn.addActionListener(this);
        fastRewindBtn.addActionListener(this);
        rewindBtn.addActionListener(this);
        stepRewindBtn.addActionListener(this);
        stopBtn.addActionListener(this);
        stepForwardBtn.addActionListener(this);
        forwardBtn.addActionListener(this);
        fastForwardBtn.addActionListener(this);
        lastBtn.addActionListener(this);
        recordBtn.addActionListener(this);
    
        setPreferredSize(new Dimension(300,32));
     }
   
     public void setStatus(boolean b)  {
        firstBtn.setEnabled(b);
        fastRewindBtn.setEnabled(b);
        rewindBtn.setEnabled(b);
        stepRewindBtn.setEnabled(b);
        stopBtn.setEnabled(b);
        stepForwardBtn.setEnabled(b);
        forwardBtn.setEnabled(b);
        fastForwardBtn.setEnabled(b);
        lastBtn.setEnabled(b);
        recordBtn.setEnabled(!b);
     }
   
     public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
    
        if ( src == firstBtn )
           RewindBegin();
        else if ( src == fastRewindBtn )
           FRewind();
        else if ( src == rewindBtn )
           Rewind();
        else if ( src == stepRewindBtn )
           StepRewind();
        else if ( src == stopBtn )
           Stop();
        else if ( src == stepForwardBtn )
           StepForward();
        else if ( src == forwardBtn )
           Forward();
        else if ( src == fastForwardBtn )
           FForward();
        else if ( src == lastBtn )
           ForwardEnd();
        else if ( src == recordBtn )
           Record();
     }
  }
}
