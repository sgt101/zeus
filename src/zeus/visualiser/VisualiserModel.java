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



package zeus.visualiser;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;

public class VisualiserModel {
  public static final int ADDRESS_KEY   = 0;
  public static final int MESSAGE_KEY   = 1;
  public static final int REPORT_KEY    = 2;

  public static final int CANCEL_SUBSCRIPTION = 0;
  public static final int DO_NOTHING          = 1;

  protected Hashtable sessionList         = new Hashtable();
  protected Hashtable agentList           = new Hashtable();
  protected Hashtable messageRuleDb       = new Hashtable();
  protected Hashtable subscriptionDb      = new Hashtable();
  protected Hashtable subscriptionContent = new Hashtable();

  protected AgentContext context = null;
  public    String[]     keys = new String[3];

  public VisualiserModel(AgentContext context) {
     this.context = context;

     for(int i = 0; i < keys.length; i++ )
        keys[i] = context.newId("VisualiserModel");

     subscriptionContent.put(keys[ADDRESS_KEY],"log_address");
     subscriptionContent.put(keys[MESSAGE_KEY],"log_message");
     subscriptionContent.put(keys[REPORT_KEY],"log_report");
  }

  public String getSubscriptionContent(String key) {
     return (String)subscriptionContent.get(key);
  }


  public void addAgent(String name, String type) {
    if (name == null || type == null) return;
    agentList.put(name,type);
  }
  

  public void addAgents(String[] agent) {
    if (agent == null) return;
    String type = SystemProps.getProperty("agent.names.agent");
    for(int i = 0; i < agent.length; i++ )
       if ( !agentList.containsKey(agent[i]) )
          agentList.put(agent[i],type);
  }

  public String[] getAgents() {
     Enumeration keys = agentList.keys();
     String[] data = new String[agentList.size()];
     for(int i = 0; keys.hasMoreElements(); i++)
        data[i] = (String)keys.nextElement();
     return data;
  }

  public String[] getAgents(String agent_type) {
      Enumeration keys = agentList.keys();
      Vector List = new Vector();
      while(keys.hasMoreElements()) {
        String agent = (String)keys.nextElement();
        String type = (String)agentList.get(agent);
        if (type.equals(agent_type))
           List.addElement(agent);
      }
      return Misc.stringArray(List);
  }


  //-------------------------------------------------------
  // Agent Name Server List Methods

  public void addNameservers(String[] agent) {
    if (agent == null) return;
    String type = SystemProps.getProperty("agent.names.nameserver");
    for(int i = 0; i < agent.length; i++ ) {
       agentList.put(agent[i],type);
    }
  }

  public void addNameserver(String agent) {
     if (agent == null) return;
     String type = SystemProps.getProperty("agent.names.nameserver");
     agentList.put(agent,type);
  }

  public String[] getNameservers() {
    return getAgents(SystemProps.getProperty("agent.names.nameserver"));
  }

  //-------------------------------------------------------
  // Database Proxy List Methods

  public void addDbProxy(String agent) {
    if (agent == null) return;
    String type = SystemProps.getProperty("agent.names.dbProxy");
    agentList.put(agent,type);
  }

  public void addDbProxys(String[] agent) {
    if (agent == null) return;
    String type = SystemProps.getProperty("agent.names.dbProxy");
    for(int i = 0; i < agent.length; i++ )
       agentList.put(agent[i],type);
  }

  public String[] getDbProxys() {
    return getAgents(SystemProps.getProperty("agent.names.dbProxy"));
  }
  //-------------------------------------------------------

  public void addDbSession(String type, String agent, String sessionId) {
    Hashtable outer = (Hashtable)sessionList.get(type);
    if ( outer == null ) {
       outer = new Hashtable();
       sessionList.put(type,outer);
    }
    HSet inner = (HSet)outer.get(agent);
    if ( inner == null ) {
       inner = new HSet();
       outer.put(agent,inner);
    }
    inner.add(sessionId);
  }

  public void addDbSessions(String type, Hashtable input) {
    Hashtable outer = (Hashtable)sessionList.get(type);
    if ( outer == null ) {
       outer = new Hashtable();
       sessionList.put(type,outer);
    }

    String agent;
    HSet input_inner, inner;
    Enumeration keys = input.keys();
    while( keys.hasMoreElements() ) {
       agent = (String)keys.nextElement();
       input_inner = (HSet)input.get(agent);

       inner = (HSet)outer.get(agent);
       if ( inner == null ) {
          inner = new HSet();
          outer.put(agent,inner);
       }
       inner.add(input_inner);
    }
  }

  public Hashtable getDbSessions(String type) {
    // return value
    Hashtable outer = (Hashtable)sessionList.get(type);
    if ( outer == null ) {
       outer = new Hashtable();
       sessionList.put(type,outer);
    }
    // Now, make sure all dbProxys have an entry of <type>
    String[] agent = getDbProxys();
    for(int i = 0; i < agent.length; i++ )
       if ( !outer.containsKey(agent[i]) )
          outer.put(agent[i],new HSet());
    return outer;
  }

  //-------------------------------------------------------
  // Message management

  public String getMessageRule(String key, String agent,
                               Object target, String method) {
     MessageInfo info = new MessageInfo(key,agent,target,method);
     return (String)messageRuleDb.get(info);
  }
  
  
  public String removeMessageRule(String key, String agent,
                                  Object target, String method) {
     MessageInfo info = new MessageInfo(key,agent,target,method);
     return (String)messageRuleDb.remove(info);
  }
  
  
  public void addMessageRule(String key, String agent,
                             Object target, String method, String ruleId) {
     MessageInfo info = new MessageInfo(key,agent,target,method);
     Core.ERROR(messageRuleDb.put(info,ruleId) == null, 1, this);
  }
  

  public Hashtable removeAllMessageRulesTo(Object target) {
     String ruleId;
     MessageInfo info;
     Hashtable output = new Hashtable();
     Enumeration keys = messageRuleDb.keys();
     while( keys.hasMoreElements() ) {
        info = (MessageInfo)keys.nextElement();
        if ( info.target == target ) {
           ruleId = (String)messageRuleDb.remove(info);
           output.put(info,ruleId);
        }
     }
     return output;
  }



  public boolean isAlreadySubscribed(String key, String agent, String ruleId) {
     SubscriptionInfo info = new SubscriptionInfo(key,agent);
     Vector List = (Vector)subscriptionDb.get(info);
     return List != null && List.contains(ruleId);
  }
  
  

  public void subscribe(String key, String agent, String ruleId) {
     SubscriptionInfo info = new SubscriptionInfo(key,agent);
     Vector List = (Vector)subscriptionDb.get(info);
     if ( List == null ) {
        List = new Vector();
        subscriptionDb.put(info,List);
     }
     Core.ERROR(!List.contains(ruleId),2,this);
     List.addElement(ruleId);
  }


  public int unsubscribe(String key, String agent, String ruleId) {
     SubscriptionInfo info = new SubscriptionInfo(key,agent);
     Vector List = (Vector)subscriptionDb.get(info);
     Core.ERROR(List,3,this);
     Core.ERROR(List.removeElement(ruleId),4,this);
     if ( List.isEmpty() ) {
        subscriptionDb.remove(info);
        return CANCEL_SUBSCRIPTION;
     }
     return DO_NOTHING;
  }
}
