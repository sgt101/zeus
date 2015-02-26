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



package zeus.actors.rtn;

import java.util.*;


import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.actors.graphs.*;
import zeus.actors.rtn.util.*;

class NodeWaitTable extends Hashtable implements Runnable {
   protected zeus.util.Queue queue = null;
   protected Engine engine;
   protected Vector dont_add_list = new Vector();
   protected long sleep_until = Long.MAX_VALUE;

   public NodeWaitTable(Engine engine, Queue queue) {
      this.engine = engine;
      this.queue = queue;
   }


   public void run() {
     Thread.currentThread().setName ("NodeWaitTable"); 
      for(;;) {
	 try {
            Core.DEBUG(3,"Before Wakeup\n" + this);
            wakeup();
            long duration = sleep_period();
            Core.DEBUG(3,engine.getAgentContext().whoami() +
	                 " sleeping for " + duration);
            Core.DEBUG(3,"After Wakeup\n" + this);
            synchronized(this) {
               wait(duration);
            }
         }
         catch(InterruptedException e) {
         }
      }
   }

   protected synchronized long sleep_period() {
      long timeout = Long.MAX_VALUE;
      Node node;
      Vector list;
      double t;

      // compute timeout
      if ( !this.isEmpty() ) {
         t = Double.MAX_VALUE; 
         Enumeration enum = this.elements();
         while( enum.hasMoreElements() ) {
            list = (Vector)enum.nextElement();
            for(int i = 0; i < list.size(); i++ ) {
               node = (Node)list.elementAt(i);
               t = Math.min(t,node.getTimeout());
            }
         }
         t = t - engine.getAgentContext().now();
         timeout = (long)(t*engine.getAgentContext().getClockStep());
      }
      timeout = Math.max(timeout,0);

      sleep_until = (timeout == Long.MAX_VALUE) ? timeout :
                   System.currentTimeMillis() + timeout;
      return timeout;
   }

   public synchronized void wakeup() {
      Node node;
      Vector list;
      String key;

      double now = engine.getAgentContext().now();
      Core.DEBUG (4,"wakeup>> now == " + String.valueOf(now) ); 
      
      
      Enumeration enum = this.keys();
      while( enum.hasMoreElements() ) {
        Core.DEBUG(4, "in while - keys >= !"); 
         key = (String)enum.nextElement();
         list = (Vector)this.get(key);
         for(int i = 0; i < list.size(); i++ ) {
            node = (Node)list.elementAt(i);
            Core.DEBUG (4,"Timeout == " + node.getTimeout()); 
            if ( now >= node.getTimeout() ) {
               queue.enqueue(node);
               list.removeElementAt(i);
            }
         }
         if ( list.isEmpty() )
            this.remove(key);
      }
      notifyAll();
   }

   public synchronized void wakeup(String key) {
      Node node;
      Vector list;

      list = (Vector)this.remove(key);

      if ( list == null ) {
         /*
          special case: the results have been returned before
          we can store the query
         */
         dont_add_list.addElement(key);
         return;
      }

      for(int i = 0; i < list.size(); i++ ) {
         node = (Node)list.elementAt(i);
         queue.enqueue(node);
      }
      list = null; // GC
      notify();
   }

   public synchronized void add(Node node) {
      String key = node.getMsgWaitKey();
      Core.DEBUG(3,"Adding wait_node: " + node.getDescription() + "[" + key + "]");

      // check special case first;
      if ( dont_add_list.contains(key) ) {
         dont_add_list.removeElement(key);
         queue.enqueue(node);
         return;
      }

      Vector list = (Vector)this.get(key);
      if ( list == null ) {
         list = new Vector();
         this.put(key,list);
      }
      list.addElement(node);

      double t = node.getTimeout();
      t = t - engine.getAgentContext().now();
      long duration = (long)(t*engine.getAgentContext().getClockStep());
      if ( System.currentTimeMillis() + duration < sleep_until ) {
         Core.DEBUG(3,"Will notify... ");
         notify();
      }
      else
         Core.DEBUG(3,"No notify: sleeping for " + (sleep_until - System.currentTimeMillis()));

   }

   public String toString() {
      double now = engine.getAgentContext().now();
      String out = "NodeWaitTable: now = " + Misc.decimalPlaces(now,4) + "\n";

      synchronized(this) {
         Enumeration enum = this.keys();
         Vector List;
         Node node;
         String key;
         while( enum.hasMoreElements() ) {
            key = (String)enum.nextElement();
            List = (Vector)this.get(key);
            out += "\t" + key + "\n";
            for(int i = 0; i < List.size(); i++) {
               node = (Node)List.elementAt(i);
               out += "\t\t" + node +
	              "\t" + Misc.decimalPlaces(node.getTimeout(),4) + "\n";
            }
         }
      }
      return out;
   }
}
