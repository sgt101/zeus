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

public abstract class Graph extends Arc {
   public static final int READY   = 0;
   public static final int RUNNING = 1;
   public static final int DONE    = 2;
   public static final int FAILED  = 3;

   private static int count = 0;

   protected String[][] nodes = null;
   protected String     start_node = null;
   protected String     next_node = null;
   protected Node       previous_node = null;
   protected Node       begin_node = null;
   protected int        state = READY;
   protected String     node_wakeup_key = null;
   private   Node       last_node = null;

   public Graph(String description, String[][] nodes, String start_node) {
      this.start_node = start_node;
      this.nodes = nodes;
      this.description = description + "-" + (count++);
      Core.DEBUG(3,"New graph " + this.description + " created");
   }

   public final int getState() { return state; }

   void run1(Engine engine, Graph graph, Node previous_node,
             Object input, String next_node) {

      context = engine.getAgentContext();
      this.graph = graph;
      this.previous_node = previous_node;
      this.next_node = next_node;
      start(engine,input);
   }
   public void run(Engine engine, Object input) {
      start(engine,input);
   }
   public void run(Engine engine, Node previous_node, Object input,
                   String node_wakeup_key) {
      this.node_wakeup_key = node_wakeup_key;
      this.previous_node = previous_node;
      start(engine,input);
   }
   protected void start(Engine engine, Object input) {
      setState(engine,RUNNING);
      begin_node = newNode(engine,start_node,previous_node);
      if ( begin_node == null )
         fail(engine,"Start node " + start_node + " not found");
      else {
         begin_node.setInput(engine,input);
         engine.add(begin_node);
      }
   }
   void done(Engine engine, Node node) {
      Core.DEBUG(3,description + " done");
      setState(engine,DONE);

      if ( graph != null ) {
         Node next = graph.newNode(engine,next_node,node);
         if ( next == null ) {
            setState(engine,RUNNING);
            node.nextArc(engine);
         }
         else {
            Object local_input = node.getOutput();
            next.setInput(engine,local_input);
            engine.add(next);
         }
      }
      else if ( node_wakeup_key != null )
         engine.wakeup(node_wakeup_key);
   }
   void failed(Engine engine, Node node) {
      if ( node == begin_node ) {
         setState(engine,FAILED);
         if ( node_wakeup_key != null )
            engine.wakeup(node_wakeup_key);
      }
   }
   protected void fail(Engine engine, String reason) {
      Core.DEBUG(3,description + " failed: " + reason);
      setState(engine,FAILED);
      if ( node_wakeup_key != null )
         engine.wakeup(node_wakeup_key);
      if ( previous_node != null )
         previous_node.nextArc(engine);
   }

   Vector getTerminalNodes() {
      Vector output;
      if ( last_node != null ) {
         if ( (output = last_node.getParentsOfNextNode()) != null )
            return output;
         else {
            output = new Vector();
            output.addElement(last_node);
            return output;
         }
      }
      else
         return new Vector();
   }

   Node newNode(Engine engine, String name, Node previous) {
      Vector parents;
      if ( previous != null ) {
Core.DEBUG(3,"NewNode preamble\n\tname = " + name +
"\n\tprevious = " + previous +
"\n\thasChild = " + previous.hasChildGraph(this) +
"\n\tparents = " + previous.getParentsOfNextNode());
      }
      if ( previous != null && !previous.hasChildGraph(this) &&
           (parents = previous.getParentsOfNextNode()) != null )
         return newNode(engine,name,previous,parents);
      else
         return newNode(engine,name,previous,null);
   }

   Node newNode(Engine engine, String name, Node previous, Vector parents) {
      Core.DEBUG(3,"NewNode:\n\tname = " + name + "\n\tprev = " + previous +
                   "\n\tparents = " + parents);
      try {
         boolean found = false;
         String[] arcs = null, vertices = null;
         for(int i = 0; i < nodes.length; i++ ) {
            if ( nodes[i][0].equals(name) ) {
               found = true;
               if ( nodes[i].length%2 != 1 )
                  Core.USER_ERROR("Improperly specified graph description " +
		     description + "at node " + nodes[i][0]);
               int k = 0;
               for(int j = 1; j < nodes[i].length; k++ ) {
                  if ( j == 1 ) {
                     arcs = new String[(nodes[i].length-1)/2];
                     vertices = new String[(nodes[i].length-1)/2];
                  }
                  arcs[k] = nodes[i][j++];
                  vertices[k] = nodes[i][j++];
               }
               break;
            }
         }
         if ( !found ) {
            Core.USER_ERROR("Improperly specified graph description " +
		     description + ": No definintion for node " + name);
	    return null;
         }
         Class c = Class.forName(name);
         last_node = (Node)c.newInstance();

         if ( parents != null )
	    last_node.set(this,arcs,vertices,previous,parents);
         else
	    last_node.set(this,arcs,vertices,previous);
         engine.notifyNodeMonitors(last_node,Engine.NODE_CREATE);
         return last_node;
      }
      catch(Exception e) {
         Core.USER_ERROR("Error in graph specification " + description +
            "\nException: " + e);
         return null;
      }
   }
   boolean allow_exec() {
      return state != FAILED;
   }
   boolean allow_backtrack(Node node) {
      return ( node != begin_node || node_wakeup_key == null );
   }
   protected boolean exec() {
      Core.ERROR(null,1,this); // should never be called
      return false;
   }
   final void setState(Engine engine,int value) {
      state = value;
      engine.notifyGraphMonitors(this,Engine.GRAPH_STATE_CHANGE);
   }
   protected void finalize() {
      if ( context != null )
         context.Engine().notifyGraphMonitors(this,Engine.GRAPH_DISPOSE);
   }
}
