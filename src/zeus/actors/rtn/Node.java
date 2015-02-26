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
 * @(#)Node.java 1.03b
 */

package zeus.actors.rtn;

import java.util.*;
import zeus.util.*;
import zeus.actors.AgentContext;

public class Node {
   public static final int NOT_READY  = 0;
   public static final int READY      = 1;
   public static final int WAITING    = 2;
   public static final int RUNNING    = 3;
   public static final int DONE       = 4;
   public static final int FAILED     = 5;

   protected static final int OK      = 1;
   protected static final int FAIL    = 2;
   protected static final int WAIT    = 3;
   protected static final int OK_WAIT = 4;

   private static int count = 0;

   protected String[]     arcs = null;
   protected String[]     nodes = null;
   protected Object       input = null;
   protected Object       output = null;
   protected Node         previous_node = null;
   protected Node[]       parents = null;
   protected Graph        graph = null;
   protected String       description = null;
   protected int          state = NOT_READY;
   protected int          current_arc = -1;
   protected double       timeout = 0;
   protected String       msg_wait_key = null;
   protected AgentContext context = null;
   protected Vector       parents_of_next_node =null;

   // LL 040500 1.03b
   public  String       getDesc() {return null;};

   public void finaliser () { 
    finals(); 
   }

   public void finals () { 
    if (arcs!=null) 
    for (int count = 0; count< arcs.length; count++) { 
        arcs[count] = null; 
    }
    arcs = null; 
    if (nodes != null) 
    for (int count = 0; count<nodes.length; count++) { 
           nodes [count] = null; 
    }
    nodes = null; 
    input = null; 
    output = null; 
    previous_node = null; 
    if (parents!=null) 
    for (int count = 0; count<parents.length; count++){ 
      //  parents[count].finals(); // recursive structure to unwind this from the memory
        parents[count] = null; }
        parents = null; 
   }


   public Node(String description) {
      this.description = description + "-" + (count++);
      Core.DEBUG(3,"New node " + this.description + " created");
   }

   public final String getDescription()  { return description; }
   public final double getTimeout()      { return timeout; }
   public final String getMsgWaitKey()   { return msg_wait_key; }
   public final Object getOutput()       { return output; }
   public final Node   getPrevious()     { return previous_node; }
   public final Node[] getParents()      { return parents; }
   public final int    getState()        { return state; }
   public final Graph  getGraph()        { return graph; }

   final void set(Graph graph, String[] arcs, String[] nodes, Node previous) {
      this.arcs = arcs;
      this.nodes = nodes;
      this.graph = graph;
      this.previous_node = previous;

      if ( previous != null ) {
         this.parents = new Node[1];
         this.parents[0] = previous;
      }
   }
   
   
   final void set(Graph graph, String[] arcs, String[] nodes, Node previous,
                  Vector parents) {
      this.arcs = arcs;
      this.nodes = nodes;
      this.graph = graph;
      this.previous_node = previous;

      this.parents = new Node[parents.size()];
      for(int i = 0; i < parents.size(); i++ )
         this.parents[i] = (Node)parents.elementAt(i);
   }
   
   
   final void run(Engine engine) {
      context = engine.getAgentContext();

      switch(state) {
         case READY:
         case WAITING:
            if ( !graph.allow_exec() )
               fail(engine,false,"Exec refused by graph");
            else {
               int result = (state == READY) ? exec() : continue_exec();
               switch( result ){
                  case OK:
                     setState(engine,RUNNING);
                     engine.add(this);
                     break;
                  case WAIT:
                     engine.waitForMsg(this);
                     setState(engine,WAITING);
                     break;
                  default:
                     fail(engine,false,"Node exec failed");
                     break;
               }
            }
            break;
         case RUNNING:
            if ( !graph.allow_exec() )
               fail(engine,true,"Exec refused by graph");
            else if ( arcs == null )
               done(engine,"terminal node reached");
            else if ( current_arc >= arcs.length )
               fail(engine,true,"All arcs traversed");
            else
               exec_arc(engine);
            break;
         default:
            Core.ERROR(null,2,this);
            break;
      }
      engine  = null; 
      context = null; 
   }
   
   
   private void done(Engine engine, String reason) {
      Core.DEBUG(3,description + " done: " + reason);
      setState(engine,DONE);
      graph.done(engine,this);
   }
   
   
   final void fail(Engine engine, boolean reset, String reason) {
      Thread.yield(); 
      Core.DEBUG(3,description + " failed: " + reason);
      setState(engine,FAILED);
      Thread.yield(); 
      if ( reset ) reset();
      graph.failed(engine,this);
      Thread.yield(); 
      if ( previous_node != null && graph.allow_backtrack(this) )
         previous_node.nextArc(engine);
    //  this.finals(); 
   }
   
   
   protected int exec() {
      // prepare output
      output = input;
      return OK;
   }
   protected int continue_exec() {
      // prepare output
      output = input;
      return OK;
   }
   protected void reset() {
      // reset any state changed by exec()
   }
   final void setInput(Engine engine, Object input) {
      Core.ERROR(state == NOT_READY, 1, this);
      this.input = input;
      current_arc = 0;
      setState(engine,READY);
   }
   protected final void exec_arc(Engine engine) {
      try {
            Class c = Class.forName(arcs[current_arc]);
            Arc arc = (Arc) c.newInstance();
            engine.notifyArcMonitors(arc,this,Engine.ARC_CREATE);
            arc.run1(engine,graph,this,output,nodes[current_arc]);
      }
      catch(Exception e) {
         Core.USER_ERROR("Arc " + arcs[current_arc] + " cannot be executed" +
                         "\nException " + e);
         nextArc(engine);
      }
   }
   final void nextArc(Engine engine) {
      switch(state) {
         // a done node  may be backtracked on when the node is the terminal
         // node of a sub-graph in which case arcs == null and we have to fail
         case DONE:
         case RUNNING:
            if ( !graph.allow_exec() )
               fail(engine,true,"Next arc disallowed by graph");
            else {
               if ( state == DONE && arcs == null )
                  fail(engine,true,"All arcs traversed");
               else {
                  setState(engine,RUNNING);
                  current_arc++;
                  engine.add(this);
               }
            }
            break;
         default:
            Core.ERROR(null,3,this);
            break;
      }
   }
   
   
   final void setState(Engine engine, int value) {
      state = value;
      engine.notifyNodeMonitors(this,Engine.NODE_STATE_CHANGE);
   }

   protected void finalize() throws Throwable {
      if ( context != null )
         context.Engine().notifyNodeMonitors(this,Engine.NODE_DISPOSE);
    arcs = null;
    nodes = null;
    input = null;
    output = null;
    previous_node = null;
    parents = null;
    graph = null;
    description = null;
    msg_wait_key = null;
    parents_of_next_node =null;
//    super.finalize();
   }

   protected Graph createGraph(String name) {
      try {
         Class c = Class.forName(name);
         Graph g = (Graph)c.newInstance();
         if ( parents_of_next_node == null )
	    parents_of_next_node = new Vector();
	 parents_of_next_node.addElement(g);
         context.Engine().notifyGraphMonitors(g,Engine.GRAPH_CREATE);
         return g;
      }
      catch(Exception e) {
         Core.USER_ERROR("Cannot create graph: " + name + "\n" + e);
         return null;
      }
   }

   Vector getParentsOfNextNode() {
      if ( parents_of_next_node == null || parents_of_next_node.isEmpty() )
         return null;

      Vector out = new Vector();
      Graph g;
      for(int i = 0; i < parents_of_next_node.size(); i++ ) {
         g = (Graph)parents_of_next_node.elementAt(i);
         out = Misc.union(out,g.getTerminalNodes());
      }
      return out.isEmpty() ? null : out;
   }

   protected Object createObject(String name) {
      try {
         Class c = Class.forName(name);
         Object g = c.newInstance();
         return g;
      }
      catch(Exception e) {
         Core.USER_ERROR("Cannot create object: " + name + "\n" + e);
         return null;
      }
   }
   final boolean hasChildGraph(Graph g) {
      Core.DEBUG(3,"hasChildGraph: " + g.getDescription() + " " + getDescription());
      boolean b = parents_of_next_node != null && parents_of_next_node.contains(g);
      Core.DEBUG(3,"\thasChildGraph = " + b);
      return b;
   }
   
   
   public String toString() { return description; }


    /**
        overwrite this method in a Node to get behaviour from
        a time out setting 
        */
   public boolean timeOut () { 
    return false; //why not?
   }
}
