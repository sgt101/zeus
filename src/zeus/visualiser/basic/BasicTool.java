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



/**********************************************************************
* BasicTool.java - implements the core Visualiser functionality       *
*                - i.e. communication                                 *
**********************************************************************/

package zeus.visualiser.basic;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.help.*;
import zeus.visualiser.*;
import zeus.actors.*;
import gnu.regexp.*;

public abstract class BasicTool extends JFrame {
  protected AgentContext    context = null;
  protected VisualiserModel model   = null;
  protected MsgFilter       filter  = null;

  private EditableMultipleSelectionDialog connect_dialog = null;
  private MsgFilterEditor                 editor = null;

  public BasicTool(AgentContext context, VisualiserModel model)  {
     this.context = context;
     this.model = model;

     this.addWindowListener(
        new WindowAdapter() {
           public void windowClosing(WindowEvent evt) { Exit(); }
        }
     );
  }

  public AgentContext    getAgentContext() { return context; }
  public VisualiserModel getModel()        { return model; }

  public void Exit()  {
     this.setVisible(false);
     removeSubscriptions();
     this.dispose();
  }

  public void Connect(boolean mode) {
    if ( !hubOK() ) return;

    String[] servers = model.getNameservers();
    if ( connect_dialog == null ) {
       connect_dialog = new EditableMultipleSelectionDialog(this,
          "Select Servers", servers);
       connect_dialog.setLocationRelativeTo(this);
    }
    else {
      Object[] chosen = connect_dialog.getPriorSelection();
      connect_dialog.setListData(servers);
      connect_dialog.setSelection(chosen);
    }

    Object[] data = connect_dialog.getSelection();
    model.addNameservers(Misc.stringArray(connect_dialog.getListData()));
    if ( data != null && data.length > 0 )
       subscribe(mode,model.keys[VisualiserModel.ADDRESS_KEY],
                 Misc.stringArray(data),"log_address");
  }

  public void quickConnect() {
    String[] servers = model.getNameservers();
    if (servers != null && servers.length > 0 ) {
       subscribe(true,model.keys[VisualiserModel.ADDRESS_KEY],servers,
                 "log_address");
    }
  }

  protected void subscribe(boolean mode, String key, String agent,
                           String method) {
  /**
     Note: for subscribe messages we do not want to ask the
     agent twice to send the same data. Hence if 'isAlreadySubscribed()'
     then do not send message. In the case of a query msg this check is
     not made since the user might want to reissue a query.
  */
     Performative msg;
     String ruleId = null;
     String content = model.getSubscriptionContent(key);
     if ( mode ) {
        if ( (ruleId = model.getMessageRule(key,agent,this,method)) == null ) {
           ruleId = context.newId("Rule");
           String[] pattern = { "type", "inform", "in-reply-to", key, "sender", agent };
           context.MsgHandler().addRule(
              new MessageRuleImpl(ruleId,pattern,this,method)
           );
           model.addMessageRule(key,agent,this,method,ruleId);
        }
        if ( !model.isAlreadySubscribed(key,agent,ruleId) ) {
           msg = new Performative("subscribe");
           msg.setReceiver(agent);
           msg.setReplyWith(key);
           msg.setContent(content);
           context.MailBox().sendMsg(msg);
           model.subscribe(key,agent,ruleId);
        }
     }
     else {
        ruleId = model.removeMessageRule(key,agent,this,method);
        context.MsgHandler().removeRule(ruleId);
        switch( model.unsubscribe(key,agent,ruleId) ) {
           case VisualiserModel.CANCEL_SUBSCRIPTION:
                msg = new Performative("cancel");
                msg.setReceiver(agent);
                msg.setReplyWith(key);
                msg.setContent(content);
                context.MailBox().sendMsg(msg);
                break;
           default:
                break;
        }
     }
  }

  protected void subscribe(boolean mode, String key, String[] agent,
                           String method) {
     for(int i = 0; i < agent.length; i++ )
        subscribe(mode,key,agent[i],method);
  }

  protected void query(String content, String agent, String method) {
     String ruleId = context.newId("Rule");
     String[] pattern = { "in-reply-to", ruleId, "sender", agent };
     context.MsgHandler().addRule(
        new MessageRuleImpl(ruleId,pattern,MessageActionImpl.EXECUTE_ONCE,this,method)
     );
     Performative msg = new Performative("query-ref");
     msg.setReceiver(agent);
     msg.setReplyWith(ruleId);
     msg.setContent(content);
     context.MailBox().sendMsg(msg);
  }

  protected void query(String content, String[] agent, String method) {
     for(int i = 0; i < agent.length; i++ )
        query(content,agent[i],method);
  }

  protected void request(String content, String agent, String method) {
     String ruleId = context.newId("Rule");
     String[] pattern = { "in-reply-to", ruleId, "sender", agent };
     context.MsgHandler().addRule(
        new MessageRuleImpl(ruleId,pattern,MessageActionImpl.EXECUTE_ONCE,this,method)
     );
     Performative msg = new Performative("request");
     msg.setReceiver(agent);
     msg.setReplyWith(ruleId);
     msg.setContent(content);
     context.MailBox().sendMsg(msg);
  }

  protected void request(String content, String[] agent, String method) {
     for(int i = 0; i < agent.length; i++ )
        request(content,agent[i],method);
  }

  protected boolean hubOK() {
     if ( context == null || context.MailBox() == null ||
          context.MsgHandler() == null || model == null ) {
        JOptionPane.showMessageDialog(this, "Error",
           "Not connected to a visualiser hub", JOptionPane.ERROR_MESSAGE);
        return false;
     }
     return true;
  }

  public void log_address(Performative msg) {
    Vector List = ZeusParser.addressList(msg.getContent());
    context.MailBox().add(List);
    for(int i = 0; i < List.size(); i++ ) {
       Address a = (Address)List.elementAt(i);
       String name = a.getName();
       String type = a.getType();
       model.addAgent(name, type);
       registerAgent(name,type);
    }
  }

  public void Filter() {
    if ( !hubOK() ) return;

    String[] agents = model.getAgents();
    if ( editor == null ) {
       editor = new MsgFilterEditor(this,"Edit Message Filter");
       editor.setLocationRelativeTo(this);
    }

    editor.setListData(agents);
    editor.setFilter(filter);
    filter = editor.getFilter();

    if ( filter != null ) {
       if (filter.from != null)
          model.addAgents(filter.from);
       if (filter.to != null)
          model.addAgents(filter.to);
       if ( filter.about != null ) {
          try {
             filter.regexp = new RE(filter.about,RE.REG_DOT_NEWLINE);// was REG_DOT_NEWLINE

          }
          catch(REException e) { // was REException
             Core.USER_ERROR("Illegal 'about' filter: " + filter.about);
             filter.about = null;
             filter.regexp = null;
          }
       }
    }
  }

  public void About()  {
    Point pt = getLocation();
    HelpWindow helpWin = new HelpWindow(this, pt, "visualiser", "About");
    helpWin.setSize(new Dimension(440, 440));
    helpWin.setTitle("About ZEUS ...");
    helpWin.validate();
  }

  protected boolean filterMsg(Performative msg) {
     if ( filter == null ) return true;
     String from = msg.getSender();
     String to = msg.getReceiver();
     if ( filter.from != null && !Misc.member(from,filter.from) )
        return false;
     if ( filter.to != null && !Misc.member(to,filter.to) )
        return false;
     if ( filter.regexp != null && !filter.regexp.isMatch(msg.getContent()) ) // was isMatch
        return false;
     return true;
  }

  protected void removeSubscriptions() {
     MessageInfo info;
     String ruleId;
     Performative msg;
     Hashtable input = model.removeAllMessageRulesTo(this);
     Enumeration enum = input.keys();
     while( enum.hasMoreElements() ) {
        info = (MessageInfo)enum.nextElement();
        ruleId = (String)input.get(info);
        context.MsgHandler().removeRule(ruleId);
        switch( model.unsubscribe(info.key,info.agent,ruleId) ) {
           case VisualiserModel.CANCEL_SUBSCRIPTION:
                msg = new Performative("cancel");
                msg.setReceiver(info.agent);
                msg.setReplyWith(info.key);
                msg.setContent(model.getSubscriptionContent(info.key));
                context.MailBox().sendMsg(msg);
                break;
           default:
                break;
        }
     }
  }

  protected abstract void registerAgent(String agent, String type);
}
