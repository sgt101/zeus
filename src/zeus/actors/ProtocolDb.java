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
 * @(#)ProtocolDb.java 1.00
 */

package zeus.actors;

import java.io.*;
import java.util.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.event.*;


/**
 * The Protocol Database is a simple storage component that holds references
 * to the social interaction protocols known by the agent. Note, although there
 * is a class constructor () here it should never be used .
 */
public class ProtocolDb extends Tree
{
  protected Hashtable factIndex;
  private HSet[] eventMonitor = new HSet[4];

  private static final int ADD    = 0;
  private static final int MODIFY = 1;
  private static final int DELETE = 2;
  private static final int ACCESS = 3;

  private static final int FAIL     = 0;
  private static final int ANY      = 1;
  private static final int RELATION = 2;
  private static final int AGENT    = 4;

  Hashtable protocolInfoList;
  AgentContext context;

  /**
  *this init really is for architectural purposes only. I think
   *instantiation without a parameter here could be bad!
  */
  public ProtocolDb () {
  super (new ProtocolDbNode ("dummy"));
  }

//----------------------------------------------------------------------------
  public ProtocolDb(OntologyDb model) {
     super(new ProtocolDbNode(model.getRoot().toString()));
     String rootName = model.getRoot().toString();
     factIndex = new Hashtable();
     factIndex.put(rootName, root);
     createTree(getRoot(),model.getRoot());

     for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();

     protocolInfoList = new Hashtable();
  }
//----------------------------------------------------------------------------
   public ProtocolDb(AgentContext context) {
     super(new ProtocolDbNode(context.OntologyDb().getRoot().toString()));
     this.context = context;
     context.set(this);
     String rootName = context.OntologyDb().getRoot().toString();
     factIndex = new Hashtable();
     factIndex.put(rootName, root);
     createTree(getRoot(),context.OntologyDb().getRoot());

     for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();

     protocolInfoList = new Hashtable();
  }
//----------------------------------------------------------------------------
  protected void createTree(TreeNode m_node,TreeNode a_node) {

    // takes contents of model and inserts them into the tree

    Vector children = a_node.getChildren();
    TreeNode b_node;
    TreeNode n_node;

    for(int i = 0; i < children.size(); i++ ) {
       b_node = (TreeNode)children.elementAt(i);
       n_node = new TreeNode( new ProtocolDbNode(b_node.toString()) );
       m_node.addChild(n_node);
       factIndex.put(b_node.toString(),n_node);
       createTree(n_node,b_node);
    }
  }
//----------------------------------------------------------------------------
  public void addProtocol(ProtocolInfo info) {
     addProtocol(info,false);
  }
  public void addProtocol(Vector info) {
    for(int i = 0; i < info.size(); i++ )
       addProtocol((ProtocolInfo)info.elementAt(i),false);
  }

//----------------------------------------------------------------------------
  private void addProtocol(ProtocolInfo info, boolean onlyLocal){
     String factType;
     ProtocolDbNode pDbNode;
     TreeNode a_node;

     Core.DEBUG(3,"addProtocol");
     Core.DEBUG(3,info);
     StrategyInfo[] strategies = info.getConstraints();
     for(int i = 0; i < strategies.length; i++ ){
        factType = strategies[i].getFact().getType();
        a_node = (TreeNode) factIndex.get(factType);
        Core.DEBUG(3,"a_node = " + a_node);
        pDbNode = (ProtocolDbNode) a_node.getValue();
        pDbNode.addProtocol(info.getName(),strategies[i]);
     }
     protocolInfoList.put(info.getName(),info);
     if (!onlyLocal) {
        notifyMonitors(info,ADD);
        notifyMonitors(info,ACCESS);
     }
  }
//----------------------------------------------------------------------------
  public void removeProtocol(ProtocolInfo info){
     removeProtocol(info, false);
  }
  public void removeProtocol(Vector info) {
    for(int i = 0; i < info.size(); i++ )
       removeProtocol((ProtocolInfo)info.elementAt(i),false);
  }
//----------------------------------------------------------------------------
  private void removeProtocol(ProtocolInfo info, boolean onlyLocal){
       String protocol = info.getName();
       Enumeration enum = values();
       ProtocolDbNode node;
       while( enum.hasMoreElements() ) {
           node = (ProtocolDbNode)enum.nextElement();
           node.deleteProtocol(protocol);
       }
       protocolInfoList.remove(protocol);
       if ( !onlyLocal ) {
          notifyMonitors(info,DELETE);
          notifyMonitors(info,ACCESS);
      }
  }
//----------------------------------------------------------------------------
// Refers to a protocol name, not a particular protocol info.
  public void modifyProtocol(ProtocolInfo info){
     removeProtocol(info,true);
     addProtocol(info,true);
     notifyMonitors(info,MODIFY);
     notifyMonitors(info,ACCESS);
  }

//----------------------------------------------------------------------------
  public Vector getProtocols(Fact fact, String[] agents, String type) {
      Core.DEBUG(3, "getProtocols");
      Core.DEBUG(3, fact);
      Core.DEBUG(3, agents);
      Core.DEBUG(3, type);
      TreeNode a_node = (TreeNode) factIndex.get(fact.getType());
      ProtocolDbNode node;
      Vector protocols;
      Vector strategies;
      StrategyInfo info;
      String protocol;
      ProtocolInfo protocolInfo;
      int value;
      Hashtable resultSet = new Hashtable();
      
      Core.DEBUG(3, "a_node0 = " + a_node);
      while (a_node != null) {
        Core.DEBUG(3, "a_node = " + a_node);
        node = (ProtocolDbNode)a_node.getValue();
        protocols = node.getProtocols();
        Core.DEBUG(3, protocols);
        for(int i = 0; i < protocols.size(); i++) {
           protocol = (String) protocols.elementAt(i);
           strategies = node.getStrategy(protocol);
           Core.DEBUG(3, strategies);
           for(int j = 0; j < strategies.size(); j++) {
              info = (StrategyInfo)strategies.elementAt(j);
              Core.DEBUG(3, info);
              for(int k = 0; k < agents.length; k++) {
                 protocolInfo = (ProtocolInfo)protocolInfoList.get(protocol);
                 if ( protocolInfo.getType().equals(type) &&
                      (value = constraintsOK(fact,agents[k],info)) != FAIL ) {
                    if (info.getType() == StrategyInfo.USE)
                       addToResultSet(new ProtocolDbResult(agents[k],
                          protocol,info.getStrategy(),info.getParameters()),
                          value,resultSet);
                    else
                       removeFromResultSet(agents[k],protocol,resultSet);
                 }
              }
           }
        }
        a_node = a_node.getParent();
      }
      return sortResultSet(resultSet);
  }
//----------------------------------------------------------------------------
  private int constraintsOK(Fact fact, String agent, StrategyInfo info) {
     Core.DEBUG(3, "constraintsOK");
     Core.DEBUG(3, fact);
     Core.DEBUG(3, agent);
     Core.DEBUG(3, info);

     OrganisationDb organisationDb = context.OrganisationDb();
     Bindings b;

     if ( context != null )
        b = new Bindings(context.whoami());
     else
        b = new Bindings();

     Fact f1 = info.getFact();
     if ( !f1.unifiesWithChild(fact,b) ) {
        Core.DEBUG(3, "unifiesWithChild failed");
        Core.DEBUG(3, f1);
        return FAIL;
     }

     int result = ANY;

     String[] agents = info.getAgents();
     if ( agents.length > 0 ) {
        if ( !Misc.member(agent,agents) )
           return FAIL;
        else
           result *= AGENT;
     }

     String[] relations = info.getRelations();
     if ( relations.length > 0 ) {
        boolean found = false;
        for(int i = 0; !found && i < relations.length; i++ )
  	   found |= organisationDb.hasRelation(agent,relations[i]);
        if ( !found )
           return FAIL;
        else
           result *= RELATION;
     }
     return result;
  }
//----------------------------------------------------------------------------
  private void removeFromResultSet(String agent, String protocol,
                                   Hashtable resultSet) {
     ProtocolDbResult item;
     Vector List;
     Enumeration enum = resultSet.elements();
     while( enum.hasMoreElements() ) {
        List = (Vector)enum.nextElement();
        for(int i = 0; i< List.size(); i++) {
           item = (ProtocolDbResult) List.elementAt(i);
           if ( item.agent.equals(agent) && item.protocol.equals(protocol) )
              List.removeElementAt(i--);
        }
     }
  }
  private void addToResultSet(ProtocolDbResult result, int grade,
                              Hashtable resultSet) {
     String Id = Integer.toString(grade);
     Vector List = (Vector)resultSet.get(Id);
     if ( List == null ) {
        List = new Vector();
        resultSet.put(Id,List);
     }
     List.addElement(result);
  }

  private Vector sortResultSet(Hashtable resultSet) {
     /* Possible value of result set keys are 1, 2, 4, 8 only,
        corresponding to ANY, RELATION, AGENT, RELATION & AGENT */

     String Id;
     Vector List;
     Vector output = new Vector();

     for(int i = 8; i > 0; i /= 2 ) {
        Id = Integer.toString(i);
        List = (Vector)resultSet.get(Id);
        if ( List != null )
        output = Misc.union(output,List);
     }
     return output;
   }


   public void addProtocolMonitor(ProtocolMonitor monitor, long event_type,
                                   boolean notify_previous)  {
      addProtocolMonitor(monitor,event_type);
      if ( !notify_previous ) return;

      Enumeration enum = protocolInfoList.elements();
      ProtocolInfo info;
      ProtocolEvent event;

      while( enum.hasMoreElements() ) {
        info = (ProtocolInfo)enum.nextElement();
        event = new ProtocolEvent(this,info,ProtocolEvent.ACCESS_MASK);
        monitor.protocolAccessedEvent(event);
        event = new ProtocolEvent(this,info,ProtocolEvent.ADD_MASK);
        monitor.protocolAddedEvent(event);
      }
   }

   /**
    * If your code needs to react to changes in the agent's stored protocols
    * use this method to add a ProtocolMonitor.
    */
   public void addProtocolMonitor(ProtocolMonitor monitor, long event_type) {
      if ( (event_type & ProtocolEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].add(monitor);
      if ( (event_type & ProtocolEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].add(monitor);
      if ( (event_type & ProtocolEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].add(monitor);
      if ( (event_type & ProtocolEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].add(monitor);
   }

   public void removeProtocolMonitor(ProtocolMonitor monitor,
                                     long event_type) {
      if ( (event_type & ProtocolEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].remove(monitor);
      if ( (event_type & ProtocolEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].remove(monitor);
      if ( (event_type & ProtocolEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].remove(monitor);
      if ( (event_type & ProtocolEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].remove(monitor);
   }

   private void notifyMonitors(ProtocolInfo info, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      ProtocolMonitor monitor;
      ProtocolEvent event;
      Enumeration enum = eventMonitor[type].elements();

      switch(type) {
         case ADD:
              event = new ProtocolEvent(this,info,ProtocolEvent.ADD_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ProtocolMonitor)enum.nextElement();
                 monitor.protocolAddedEvent(event);
              }
              break;
         case MODIFY:
              event = new ProtocolEvent(this,info,ProtocolEvent.MODIFY_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ProtocolMonitor)enum.nextElement();
                 monitor.protocolModifiedEvent(event);
              }
              break;
         case DELETE:
              event = new ProtocolEvent(this,info,ProtocolEvent.DELETE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ProtocolMonitor)enum.nextElement();
                 monitor.protocolDeletedEvent(event);
              }
              break;
         case ACCESS:
              event = new ProtocolEvent(this,info,ProtocolEvent.ACCESS_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ProtocolMonitor)enum.nextElement();
                 monitor.protocolAccessedEvent(event);
              }
              break;
      }
   }
}

//-------------------------------------------------------------------------
class  ProtocolDbNode {

    private String factType;
    protected OrderedHashtable protocolIndex;

    public ProtocolDbNode () {
    ;
    }


    public ProtocolDbNode(String factType){
        this.factType = factType;
        protocolIndex = new OrderedHashtable();
    }
//-------------------------------------------------------------------------
    public String getFactType() {  return factType; }
//-------------------------------------------------------------------------
    public Vector getProtocols() {
     Vector protocols = new Vector();
     Enumeration  enum = protocolIndex.keys();
     while (enum.hasMoreElements())
        protocols.addElement(enum.nextElement());

     return protocols;
    }
//-------------------------------------------------------------------------
    public void addProtocol(String protocol, StrategyInfo info) {
          if (protocolIndex.containsKey(protocol)) {
             Vector infos = (Vector) protocolIndex.get(protocol);
             infos.addElement(info);
          }
          else {
             Vector infos = new Vector();
             infos.addElement(info);
             protocolIndex.put(protocol,infos);
          }

    }
//-------------------------------------------------------------------------
    public Vector getStrategy(String protocol){
       return (Vector) protocolIndex.get(protocol);
    }
//-------------------------------------------------------------------------
    public boolean hasProtocol(String protocol){
       return protocolIndex.containsKey(protocol);
    }
//-------------------------------------------------------------------------
    public void deleteProtocol(String protocol){
       protocolIndex.remove(protocol);
    }
//-------------------------------------------------------------------------
    public String toString() {
       String s = "(:fact " + factType + " ";
       Enumeration enum = protocolIndex.keys();
       String protocol;
       Vector info;
       while( enum.hasMoreElements() ) {
          protocol = (String)enum.nextElement();
          info = (Vector)protocolIndex.get(protocol);
          s += ":protocol " + protocol + " " +
               ":strategy " + info + " ";
       }
       s = s.trim() + ")";
       return s;
    }
}


