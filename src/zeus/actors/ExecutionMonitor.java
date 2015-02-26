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
 * @(#)ExecutionMonitor.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.Engine;
import zeus.actors.event.*;

/**
 * This component controls the invocation of the domain functions that
 * implement the agent's task abilities. Domain functions are typically
 * external program code or legacy systems that implement the methods of the 
 * {@link ZeusTask} interface. A domain function is invoked with the task's 
 * preconditions as its input arguments, and upon completion its declared 
 * effects will be passed back to the agent through this component. <p>
 *
 * Another important role of the Execution Monitor is to detect failure during
 * the scheduled execution of a plan. This can occur for a number of reasons:
 * <ol>
 * <li> a resource reserved by an task might have been deleted,
 * <li> an operator might begin execution but fail to complete for some reason,
 * <li> an operator might successfully complete execution but return the wrong
 *      or incomplete results, or
 * <li> some promised resource from another agent might not arrive on time
 * </ol> <p>
 * Should any of the above occur, the Execution Monitor will trigger remedial
 * action, which may involve replanning.
 */

public class ExecutionMonitor extends Thread {
   public  static final int    TICK = 0;
   public  static final double UPDATE_FRACTION = 0.25;

   private   HSet[]       eventMonitor = new HSet[1];
   protected boolean      monitoring;
   protected AgentContext context = null;

  

   // meaningless init  to allow rearch
   public ExecutionMonitor () {
   ;
   }

   public ExecutionMonitor(AgentContext context) {
      Assert.notNull(context);
      this.context = context;
      context.set(this);

      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();
      this.setName("ExecutionMonitor"); 
      this.start();
   }

   public AgentContext getAgentContext() { return context; }
   public void         stopMonitoring()  { monitoring = false; }
   
   public void run() {
      double now;
      long   timeout;
      long   incr  = context.getClockStep();
      int    prev  = (int) context.now();

      monitoring = true;
      this.setPriority(Thread.NORM_PRIORITY);
      while( monitoring ) {
         Planner table = context.Planner();
         // continually update Plan Table
         now  = context.now();
         timeout =  now < prev + 1 && now + UPDATE_FRACTION > prev + 1 ?
                    (long) ((prev+1-now)*incr) :
                    (long) (UPDATE_FRACTION*incr);
         if ( ((int) now) == prev + 1 ) {
            prev = (int) now;
            notifyMonitors(prev,TICK);
            //1.3 deadlock preventer 
            Engine engine = context.getEngine(); 
            if ( table != null ) engine.callShuffle();
         }
         if ( table != null ) {
            Engine engine = context.getEngine (); 
            engine.callCheckRecords();}
         try { 

            if (timeout < 100) timeout = 10; 
                sleep(timeout);
         }
         catch(InterruptedException e) {
            e.printStackTrace();
         }
         yield(); 
      }
   }
   
   
   public void addClockMonitor(ClockMonitor monitor, long type) {
      Assert.notNull(monitor);
      if ( (type & ClockEvent.TICK_MASK) != 0 )
         eventMonitor[TICK].add(monitor);
   }
   
   
   public void removeClockMonitor(ClockMonitor monitor, long type) {
      Assert.notNull(monitor);
      if ( (type & ClockEvent.TICK_MASK) != 0 )
         eventMonitor[TICK].remove(monitor);
   }
   
   
   protected void notifyMonitors(int tick, int event_type) {
      if ( eventMonitor[event_type].isEmpty() ) return;

      Enumeration enum = eventMonitor[event_type].elements();
      ClockMonitor monitor;
      ClockEvent event;
      switch(event_type) {
         case TICK:
              event = new ClockEvent(this,tick,ClockEvent.TICK_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ClockMonitor)enum.nextElement();
                 monitor.clockTickEvent(event);
              }
              break;
      }
   }
}
