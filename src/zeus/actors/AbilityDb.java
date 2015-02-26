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
 * @(#)AbilityDb.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.event.*;

/**
 * This class implements the Ability Database component, which stores the
 * acquaintances of the owning agent. Each acquaintance consists of an agent
 * identity and the abilities that agent is believed to possess (these are
 * stored as {@link AbilityDbItem} objects). <p>
 *
 * Every Zeus agent has an AbilityDb instance, a reference to which is stored
 * in its {@link AgentContext} object. <p>
 * Change log
 * ----------
 * 12-06-01 Added agentName and genSym to facilitate intialisation from extended types. 
 * also altered init functions. 
 */

public class AbilityDb extends Hashtable {
   private HSet[]  eventMonitor = new HSet[4];

   private static final int ADD    = 0;
   private static final int MODIFY = 1;
   private static final int DELETE = 2;
   private static final int ACCESS = 3;

   protected Vector knownAgents = new Vector(100);
   protected AgentContext context = null;
    
   protected GenSym gensym = null; 
   protected String agentName = null; 

  //meaningless init to permit rearchitecting
   public AbilityDb() {
    for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();
   }


   public AbilityDb(AgentContext context) {
      Assert.notNull(context);
      this.context = context;
      gensym = context.getGenSym(); 
      agentName = context.whoami(); 
      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();
   }

   public AgentContext getAgentContext() {
       return context;
   }

   protected boolean addItem(String name, Vector List ) {
      if ( !List.contains(name) ) {
         List.addElement(name);
         return true;
      }
      return false;
   }

   private boolean member(AbilityDbItem item, Vector List) {
      AbilityDbItem elem;
      for( int i = 0; i < List.size(); i++ ) {
         elem = (AbilityDbItem)List.elementAt(i);
         notifyMonitors(elem,ACCESS);
         if ( item.equals(elem) ) return true;
      }
      return false;
   }

   public void add(String type, AbilityDbItem ability_item) {
      AbilityDbItem item = ability_item.duplicate(Fact.VAR,gensym);
      Vector obj;

      addItem(item.getAgent(),knownAgents);
      if ( (obj = (Vector)this.get(type)) == null ) {
         obj = new Vector(10);
         this.put(type,obj);
      }
      if ( !member(item,obj) ) {
         obj.addElement(item);
         notifyMonitors(item,ADD);
      }
   }

   public void add(String agent, AbilitySpec ability) {
      String type = ability.getType();
      AbilityDbItem item = new AbilityDbItem(agent,ability);
      add(type,item);
   }

   public void add(String item, Vector List) {
      if ( List.isEmpty() ) return;
      Object object = List.elementAt(0);

      if ( object instanceof AbilityDbItem ) {
         for( int i = 0; i < List.size(); i++ )
            this.add(item, (AbilityDbItem)List.elementAt(i) );
      }
      else if ( object instanceof AbilitySpec ) {
         for( int i = 0; i < List.size(); i++ )
            this.add(item, (AbilitySpec)List.elementAt(i) );
      }
   }

   public void add(Vector List) {
      for( int i = 0; i < List.size(); i++ ) {
         AbilityDbItem item = (AbilityDbItem)List.elementAt(i);
         String type = item.getAbility().getType();
         this.add(type,item);
      }
   }

   public void add(AbilityDbItem item) {
      String type = item.getAbility().getType();
      this.add(type,item);
   }

   public void modify(AbilityDbItem previousItem, AbilityDbItem newItem) {
       del(previousItem.getAbility().getType(),previousItem);
       add(newItem);
   }

   public void del(String type, AbilityDbItem item) {
      Vector obj;
      AbilityDbItem a;

      if ( (obj = (Vector)this.get(type)) == null ) return;

      for( int i = 0; i < obj.size(); i++ ) {
         a = (AbilityDbItem)obj.elementAt(i);
         if ( a.equals(item) ) {
            obj.removeElementAt(i--);
            notifyMonitors(a,DELETE);
         }
      }
      if ( obj.isEmpty() ) this.remove(type);
   }

   public void del(String agent, AbilitySpec ability) {
      String type = ability.getType();
      AbilityDbItem item = new AbilityDbItem(agent,ability);
      del(type,item);
   }

   public void del(String item, Vector List) {
      if ( List.isEmpty() ) return;
      Object object = List.elementAt(0);

      if ( object instanceof AbilityDbItem ) {
         for( int i = 0; i < List.size(); i++ )
            this.del(item, (AbilityDbItem) List.elementAt(i) );
      }
      else if ( object instanceof AbilitySpec ) {
         for( int i = 0; i < List.size(); i++ )
            this.del(item, (AbilitySpec) List.elementAt(i) );
      }
   }


   public AbilityDbItem findOne(AbilitySpec ability) {
      Vector obj;
      Vector reduced;
      AbilityDbItem item = null;

      if ( (obj = (Vector)this.get(ability.getType())) == null )
         return null;

      if ( (reduced = this.reduce(obj,ability)) == null )
         return null;

      if ( reduced.size() > 0 ) {
         int pos = (int) (Math.random()*reduced.size());
         item = (AbilityDbItem)reduced.elementAt(pos);
         notifyMonitors(item,ACCESS);
      }
      reduced = null;
      return item;
   }

   public Vector findAll(AbilitySpec ability) {
      Vector obj;
      Vector reduced;
      
      if ( (obj = (Vector)this.get(ability.getType())) == null )
         return new Vector(100);
      
      Core.DEBUG(3,"AbilityDb findAll: " + ability + "\n" + obj);
      reduced = this.reduce(obj,ability);
      return reduced;
   }

   public Vector abilitiesOf(String person) {
      Vector List = new Vector(100);
      Vector items = null;
      AbilityDbItem item;
      Enumeration enum = this.keys();
      String type;

      while ( enum.hasMoreElements() ) {
         type = (String) enum.nextElement();
         items = (Vector) this.get(type);
         for( int i = 0; i < items.size(); i++ ) {
            item = (AbilityDbItem) items.elementAt(i);
            notifyMonitors(item,ACCESS);
            if ( item.getAgent().equals(person) )
               List.addElement( new AbilitySpec(item.getAbility()) );
         }
      }
      return List;
   }

   protected Vector reduce(Vector List, AbilitySpec ability) {
      Vector ReducedList = new Vector(100);
      AbilityDbItem item;
      AbilitySpec   ab;
      Bindings b = new Bindings(agentName);
      Fact f2, f1 = ability.getFact();
      int  t2, t1 = ability.getTime();
      double c2, c1 = ability.getCost();

      for( int i = 0; i < List.size(); i++, b.clear() ) {
         item = (AbilityDbItem) List.elementAt(i);
         notifyMonitors(item,ACCESS);
         ab = item.getAbility();
         f2 = ab.getFact();
         t2 = ab.getTime();
         c2 = ab.getCost();
         if ( (t1 == 0 || t2 <= t1) && (c1 == 0 || c2 <= c1) &&
              f2.unifiesWith(f1,b) )
            ReducedList.addElement(item);
         else {
            Core.DEBUG(3,"Cannot unify abilities:\n" + ability + "\n" + item );
         }
      }
      return ReducedList;
   }

   public void addAbilityMonitor(AbilityMonitor monitor, long event_type,
                                 boolean notify_previous) {

      addAbilityMonitor(monitor,event_type);
      if ( !notify_previous ) return;

      Enumeration enum = elements();
      Vector List;
      AbilityDbItem item;
      AbilityEvent event;

      while( enum.hasMoreElements() ) {
         List = (Vector)enum.nextElement();
         for(int i = 0; i < List.size(); i++ ) {
            item = (AbilityDbItem) List.elementAt(i);
            event = new AbilityEvent(this,item,AbilityEvent.ACCESS_MASK);
            monitor.abilityAccessedEvent(event);
            event = new AbilityEvent(this,item,AbilityEvent.ADD_MASK);
            monitor.abilityAddedEvent(event);
         }
      }
   }

   public void addAbilityMonitor(AbilityMonitor monitor, long type) {
      Assert.notNull(monitor);
      if ( (type & AbilityEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].add(monitor);
      if ( (type & AbilityEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].add(monitor);
      if ( (type & AbilityEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].add(monitor);
      if ( (type & AbilityEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].add(monitor);
   }
   public void removeAbilityMonitor(AbilityMonitor monitor, long type) {
      Assert.notNull(monitor);
      if ( (type & AbilityEvent.ADD_MASK) != 0 )
         eventMonitor[ADD].remove(monitor);
      if ( (type & AbilityEvent.MODIFY_MASK) != 0 )
         eventMonitor[MODIFY].remove(monitor);
      if ( (type & AbilityEvent.DELETE_MASK) != 0 )
         eventMonitor[DELETE].remove(monitor);
      if ( (type & AbilityEvent.ACCESS_MASK) != 0 )
         eventMonitor[ACCESS].remove(monitor);
   }
   private void notifyMonitors(AbilityDbItem ability, int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      Enumeration enum = eventMonitor[type].elements();
      AbilityMonitor monitor;
      AbilityEvent event;
      switch(type) {
         case ADD:
              event = new AbilityEvent(this,ability,AbilityEvent.ADD_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AbilityMonitor)enum.nextElement();
                 monitor.abilityAddedEvent(event);
              }
              break;
         case MODIFY:
              event = new AbilityEvent(this,ability,AbilityEvent.MODIFY_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AbilityMonitor)enum.nextElement();
                 monitor.abilityModifiedEvent(event);
              }
              break;
         case DELETE:
              event = new AbilityEvent(this,ability,AbilityEvent.DELETE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AbilityMonitor)enum.nextElement();
                 monitor.abilityDeletedEvent(event);
              }
              break;
         case ACCESS:
              event = new AbilityEvent(this,ability,AbilityEvent.ACCESS_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AbilityMonitor)enum.nextElement();
                 monitor.abilityAccessedEvent(event);
              }
              break;
      }
   }
}
