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
 * @(#)OrganisationDb.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.event.*;

/**
 * The Organisation Database stores the agent's beliefs about its organisational
 * relationships with other agents. Each relationship will be of one of the
 * following types:
 * <ul>
 * <li> Peer	- which is the default relationship which entails no restrictions
 *              or assumptions about agent interaction
 * <li> Superior - here the acquaintance is believed to possess higher authority
 *                 than this agent, and can issue orders that this agent must obey
 * <li> Subordinate	- the acquaintance is believed to have less authority than
 *                    this agent, and can be issued orders that it must obey
 * <li> Co-worker	- the acquaintance belongs to the same 'community' as this
 *                  agent, and will be asked before peers when any resources are required
 * </ul>
 */


public class OrganisationDb extends AbilityDb {
  protected HSet[]  eventMonitor = new HSet[4];

  private static final int ADD    = 0;
  private static final int MODIFY = 1;
  private static final int DELETE = 2;
  private static final int ACCESS = 3;

  protected Hashtable relations = new Hashtable();
  protected String agentName = "null"; 

// meaningless init  to allow rearch
  public OrganisationDb () {
   super(); 
   for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();
  }

  public OrganisationDb(AgentContext context) {
     super(context);
     agentName = context.whoami();
     context.set(this);

     for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();
  }

  public void addRelation(Vector List) {
    for(int i = 0; i < List.size(); i++ ) {
      String agent = (String) List.elementAt(i++);
      String relation = (String) List.elementAt(i);
      this.addRelation(agent,relation);
    }
  }

  
  public void modifyAgentRelation(String agent, String oldRelation,
                                  String newRelation) {
      removeRelation(agent,oldRelation);
      addRelation(agent,newRelation);
  }

  
  public Vector allRelations() {
    Vector result = new Vector();

    Vector peers = getPeers();
    String agent;
    for(int i = 0; i < peers.size(); i++ ) {
       agent = (String)peers.elementAt(i);
       result.addElement(new Relationship(agent,"peer"));
    }

    Enumeration enum = relations.keys();
    String relation;
    Vector List;
    while( enum.hasMoreElements() ) {
      relation = (String)enum.nextElement();
      List = (Vector)relations.get(relation);

      for(int i = 0; i < List.size(); i++ ) {
         agent = (String)List.elementAt(i);
         notifyMonitors(agent,relation,ACCESS);
         result.addElement(new Relationship(agent,relation));
      }
    }
    return result;
  }


  public void addRelation(String agent, String relation) {
    addItem(agent,knownAgents);
    Vector List = (Vector)relations.get(relation);
    if ( List == null ) {
       List = new Vector();
       relations.put(relation,List);
    }
    if ( !List.contains(agent) ) {
       List.addElement(agent);
       notifyMonitors(agent,relation,ADD);
    }
  }

  
  public void removeRelation(String agent, String relation) {
    Vector List = (Vector)relations.get(relation);
    if ( List == null ) return;
    if ( List.contains(agent) ) {
       List.removeElement(agent);
       notifyMonitors(agent,relation,DELETE);
    }
  }
  
  
  public boolean hasRelation(String agent, String relation) {
    boolean result = false;
    if ( relation.equals("peer") ) {
       Vector peers = getPeers();
       result = peers.contains(agent);
       notifyMonitors(agent,"peer",ACCESS);
    }
    else {
       Vector List = (Vector)relations.get(relation);
       if ( List == null ) return false;
       result = List.contains(agent);
       notifyMonitors(agent,"relation",ACCESS);
    }
    return result;
  }
  

  public boolean hasRelation(String relation) {
    boolean result = false;
    if ( relation.equals("peer") ) {
       Vector peers = getPeers();
       result = !peers.isEmpty();
    }
    else {
       Vector List = (Vector)relations.get(relation);
       result = List != null && !List.isEmpty();
    }
    return result;
  }

  
  public Vector anyAgent(Goal goal, Vector ignore) {
    Core.DEBUG(4,"anyAgent() = " + goal );

    if ( ignore == null )
       ignore = new Vector();

    Vector agents = Misc.difference(knownAgents,ignore);
    agents.removeElement(agentName);
    return _anyAgent(agents,goal);
  }

  
  private Vector getPeers() {
    Vector peers;
    Vector others = new Vector();
    Enumeration enum = relations.elements();
    while( enum.hasMoreElements() )
       others = Misc.union(others,(Vector)enum.nextElement());
    peers = Misc.difference(knownAgents,others);
    peers.removeElement(agentName);
    return peers;
  }

  
  protected  Vector _anyAgent(Vector agents, Goal goal) {
    AbilitySpec ability;
    Vector can_do = null;
    String name;
    AbilityDbItem item;
    String key = null;
    Vector result = new Vector();

    ability = goal.getAbility();
    if ( (can_do = findAll(ability)) != null ) {
       Core.DEBUG(4,"anyAgent: " + ability + "\nwith\n" + can_do);
       for( int j = 0; j < can_do.size(); j++ ) {
          item = (AbilityDbItem)can_do.elementAt(j);
          name = item.getAgent();
          Core.DEBUG(4,"anyAgent considering: " + name);
          if ( agents.contains(name) && !result.contains(name) ) {
             result.addElement(name);
             Core.DEBUG(4,"t" + name + " selected");
          }
          else {
            Core.DEBUG(4,"t" + name + " not selected");
          }
       }
    }
    Core.DEBUG(4,"anyAgent result = " + result);
    return result;
  }


  /**
   * If your code needs to react to changes in the agent's organisational beliefs
   * use this method to add a RelationMonitor to this component.
   */
  public void addRelationMonitor(RelationMonitor monitor, long event_type,
                                 boolean notify_previous)  {
      addRelationMonitor(monitor,event_type);
      if ( !notify_previous ) return;

      RelationEvent event;
      Relationship data;
      Vector peers = getPeers();
      String agent;
      for(int i = 0; i < peers.size(); i++ ) {
         agent = (String)peers.elementAt(i);
         data = new Relationship(agent,"peer");
         event = new RelationEvent(this,data,RelationEvent.ACCESS_MASK);
         monitor.relationAccessedEvent(event);
         event = new RelationEvent(this,data,RelationEvent.ADD_MASK);
         monitor.relationAddedEvent(event);
      }

      Enumeration enum = relations.keys();
      Vector List;
      String relation;
      while( enum.hasMoreElements() ) {
         relation = (String)enum.nextElement();
         List = (Vector)relations.get(relation);
         for(int i = 0; i < List.size(); i++ ) {
            agent = (String)List.elementAt(i);
            data = new Relationship(agent,relation);
            event = new RelationEvent(this,data,RelationEvent.ACCESS_MASK);
            monitor.relationAccessedEvent(event);
            event = new RelationEvent(this,data,RelationEvent.ADD_MASK);
            monitor.relationAddedEvent(event);
         }
      }
   }

   public void addRelationMonitor(RelationMonitor monitor, long type) {
      if ( (type & RelationEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].add(monitor);
      if ( (type & RelationEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].add(monitor);
      if ( (type & RelationEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].add(monitor);
      if ( (type & RelationEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].add(monitor);
   }

   public void removeRelationMonitor(RelationMonitor monitor, long type) {
      if ( (type & RelationEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].remove(monitor);
      if ( (type & RelationEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].remove(monitor);
      if ( (type & RelationEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].remove(monitor);
      if ( (type & RelationEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].remove(monitor);
   }

   private void notifyMonitors(String agent, String relation, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      Enumeration enum = eventMonitor[type].elements();
      Relationship data = new Relationship(agent,relation);
      RelationMonitor monitor;
      RelationEvent event;
      switch(type) {
         case ADD:
              event = new RelationEvent(this,data,RelationEvent.ADD_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (RelationMonitor)enum.nextElement();
                 monitor.relationAddedEvent(event);
              }
              break;
         case MODIFY:
              event = new RelationEvent(this,data,RelationEvent.MODIFY_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (RelationMonitor)enum.nextElement();
                 monitor.relationModifiedEvent(event);
              }
              break;
         case DELETE:
              event = new RelationEvent(this,data,RelationEvent.DELETE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (RelationMonitor)enum.nextElement();
                 monitor.relationDeletedEvent(event);
              }
              break;
         case ACCESS:
              event = new RelationEvent(this,data,RelationEvent.ACCESS_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (RelationMonitor)enum.nextElement();
                 monitor.relationAccessedEvent(event);
              }
              break;
      }
   }
}
