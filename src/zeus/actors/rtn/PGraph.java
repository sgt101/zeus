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

public class PGraph extends Graph {
   protected Vector  done_nodes = new Vector();
   protected Vector  failed_nodes = new Vector();
   protected Node[]  start_nodes = null;
   protected boolean first = true;
   protected int     min_done = -1;
   protected int     max_fail = 0;

   public PGraph(String description, String[][] nodes, String start_node) {
      super(description,nodes,start_node);
   }
   public PGraph(String description, String[][] nodes, String start_node,
                 int min_done) {
      super(description,nodes,start_node);
      this.min_done = min_done;
   }
   protected void start(Engine engine, Object input) {
      state = RUNNING;
      Object[] local_input = (Object[]) input;
      start_nodes = new Node[local_input.length];
      if ( min_done != -1 )
         max_fail = local_input.length - min_done;

      for(int i = 0; i < local_input.length; i++ ) {
         start_nodes[i] = newNode(engine,start_node,previous_node);
         if ( start_nodes[i] == null ) {
            fail(engine,"Start node not found");
            return;
         }
      }
      for(int i = 0; i < local_input.length; i++ ) {
         start_nodes[i].setInput(engine,local_input[i]);
         engine.add(start_nodes[i]);
      }
   }
   void done(Engine engine, Node node) {
      done_nodes.addElement(node);
      Core.DEBUG(3,description + " no: " + done_nodes.size() + " done");
      if ( done_nodes.size() + failed_nodes.size() == start_nodes.length )
         doSucceed(engine);
   }

   protected void doSucceed(Engine engine) {
      setState(engine,DONE);
      if ( graph != null ) {
	 Node next = graph.newNode(engine,next_node,previous_node,getTerminalNodes());
         if ( next == null ) {
            fail(engine,"next_node - " + next_node + " - not found");
            return;
         }
         Object[] local_input = new Object[done_nodes.size()];
         for(int i = 0; i < done_nodes.size(); i++ )
            local_input[i] = ((Node)done_nodes.elementAt(i)).getOutput();
         next.setInput(engine,local_input);
         engine.add(next);
      }
      else if ( node_wakeup_key != null )
         engine.wakeup(node_wakeup_key);
   }

   void failed(Engine engine, Node node) {
      if ( state != FAILED ) {
         for(int i = 0; i < start_nodes.length; i++ ) {
            if ( start_nodes[i] == node ) {
               failed_nodes.addElement(node);
               if ( failed_nodes.size() > max_fail )
	          setState(engine,FAILED);
               break;
            }
         }
         if ( state == FAILED ) {
            for(int i = 0; i < done_nodes.size(); i++ )
               ((Node)done_nodes.elementAt(i)).fail(engine,true,"Parallel branch failed");
         }
      }
      else {
         for(int i = 0; i < start_nodes.length; i++ ) {
            if ( start_nodes[i] == node ) {
               failed_nodes.addElement(node);
               break;
            }
         }
      }
      if ( first && failed_nodes.size() == start_nodes.length ) {
         first = false;
         fail(engine,"Parallel graph failed");
      }
      else if ( failed_nodes.size() + done_nodes.size() == start_nodes.length )
         doSucceed(engine);
   }
   Node newNode(Engine engine, String name, Node previous) {
      if ( state == FAILED ) return null;
      else return super.newNode(engine,name,previous);
   }

   Vector getTerminalNodes() {
      Vector out = new Vector();
      Node node;
      Vector local;

      Core.DEBUG(3,"PGraph: getTerminalNodes for: " + description);
      for(int i = 0; i < done_nodes.size(); i++ ) {
         node = (Node)done_nodes.elementAt(i);
         Core.DEBUG(3,"\tConsidering done_node: " + node);
         if ( (local = node.getParentsOfNextNode()) != null )
 	    out = Misc.union(out,local);
         else
            out.addElement(node);
         Core.DEBUG(3,"\tCurrent terminals: " + out);
      }
      for(int i = 0; i < failed_nodes.size(); i++ ) {
         node = (Node)failed_nodes.elementAt(i);
         Core.DEBUG(3,"\tConsidering failed_node: " + node);
         if ( (local = node.getParentsOfNextNode()) != null )
 	    out = Misc.union(out,local);
         else
            out.addElement(node);
         Core.DEBUG(3,"\tCurrent terminals: " + out);
      }
      Core.DEBUG(3,"\tFinal terminals: " + out);
      return out;
   }
   boolean allow_backtrack(Node node) {
      for(int i = 0; i < start_nodes.length; i++ )
         if ( start_nodes[i] == node )
            return false;
      return true;
   }
}
