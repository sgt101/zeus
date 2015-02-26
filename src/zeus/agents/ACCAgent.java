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
* public limited company are Copyright 1996-2001. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



/*
 * @(#)AccAgent.java 1.00
 */

package zeus.agents;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.agents.*;
import zeus.actors.outtrays.*;
import zeus.actors.intrays.*;

/**
 The DFAgent is run to provide an IIOP ACC service for Zeus that uses FIPA addressing
 @see zeus.concepts.FIPA_AID_Address
 @see zeus.actors.fipa.FIPA_Postman
 @see zeus.actors.fipa.FIPA_Server
 @see zeus.actors.fipa.Zeus_ACC_Server
 @see zeus.actors.fipa.Zeus_ACC_Mailbox
 @author Simon Thompson
 */
public class ACCAgent extends BasicAgent
{
   private HSet[] eventMonitor = new HSet[4];

   public static final int CREATE  = 0;
   public static final int DEATH   = 1;
   public static final int SUSPEND = 2;
   public static final int RESUME  = 3;

   protected AgentContext context = null;

   public ACCAgent(String type, String name, Vector nameservers) {
     this(type, name, nameservers, null);
   }

   public ACCAgent(String type, String name,
                     Vector nameservers, Clock clock) {

     context = new ZeusAgentContext(name,type);
     context.set(this);
     context.setNameservers(nameservers);
     new MsgHandler(context);
     Zeus_ACC_MailBox mbox = new Zeus_ACC_MailBox(context); 
    
     context.set(mbox); 
   

     if ( clock != null )
        context.set(clock);

     for(int i = 0; i < eventMonitor.length; i++ )
        eventMonitor[i] = new HSet();

     long now, start = System.currentTimeMillis();

     while( context.Clock() == null ) {
       now = System.currentTimeMillis();
       if ( now - start > (long)(context.getRegistrationTimeout()*60000) ) {
          System.err.println("Cannot initialize " + name + " -- exiting");
          notifyMonitors(DEATH);
	        System.exit(0);
       }
       try {
          Thread.currentThread().sleep(1000);
       }
       catch(InterruptedException e) {
       }
     }
     notifyMonitors(CREATE);
   }

   public AgentContext getAgentContext() { return context; }


   /** Shortcut to add a MessageMonitor, used if your code needs to react to
       changes in the state of the mailbox */
   public void addMessageMonitor(MessageMonitor monitor, long event_type) {
      context.MailBox().addMessageMonitor(monitor,event_type);
   }

   public void removeMessageMonitor(MessageMonitor monitor, long event_type) {
      context.MailBox().removeMessageMonitor(monitor,event_type);
   }

   /** Add an AgentMonitor if your code needs to react to Agent-level changes in state */
   public void addAgentMonitor(AgentMonitor monitor, long event_type) {
      Assert.notNull(monitor);
      if ( (event_type & AgentEvent.CREATE_MASK) != 0 )
         eventMonitor[CREATE].add(monitor);
      if ( (event_type & AgentEvent.DEATH_MASK) != 0 )
         eventMonitor[DEATH].add(monitor);
      if ( (event_type & AgentEvent.SUSPEND_MASK) != 0 )
         eventMonitor[SUSPEND].add(monitor);
      if ( (event_type & AgentEvent.RESUME_MASK) != 0 )
         eventMonitor[RESUME].add(monitor);
   }
   public void removeAgentMonitor(AgentMonitor monitor, long event_type) {
      Assert.notNull(monitor);
      if ( (event_type & AgentEvent.CREATE_MASK) != 0 )
         eventMonitor[CREATE].remove(monitor);
      if ( (event_type & AgentEvent.DEATH_MASK) != 0 )
         eventMonitor[DEATH].remove(monitor);
      if ( (event_type & AgentEvent.SUSPEND_MASK) != 0 )
         eventMonitor[SUSPEND].remove(monitor);
      if ( (event_type & AgentEvent.RESUME_MASK) != 0 )
         eventMonitor[RESUME].remove(monitor);
   }
   public void notifyMonitors(int type) {
      if ( eventMonitor[type].isEmpty() ) return;

      AgentMonitor monitor;
      AgentEvent event;
      Enumeration enum = eventMonitor[type].elements();

      switch(type) {
         case CREATE:
              event = new AgentEvent(this,this,AgentEvent.CREATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AgentMonitor)enum.nextElement();
                 monitor.agentCreatedEvent(event);
              }
              break;
         case DEATH:
              event = new AgentEvent(this,this,AgentEvent.DEATH_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AgentMonitor)enum.nextElement();
                 monitor.agentDeathEvent(event);
              }
              break;
         case SUSPEND:
              event = new AgentEvent(this,this,AgentEvent.SUSPEND_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AgentMonitor)enum.nextElement();
                 monitor.agentSuspendedEvent(event);
              }
              break;
         case RESUME:
              event = new AgentEvent(this,this,AgentEvent.RESUME_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (AgentMonitor)enum.nextElement();
                 monitor.agentResumedEvent(event);
              }
              break;
      }
   }
}
