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
import zeus.actors.AgentContext;

public class Arc {
   private  static int count = 0;

   protected String       description = null;
   protected Object       output = null;
   protected Object       input = null;
   protected AgentContext context = null;
   protected Graph        graph = null;

   protected Arc() {
   }
   public Arc(String description) {
      Assert.notNull(description);
      this.description = description + "-" + (count++);
      Core.DEBUG(3,"New arc " + this.description + " created");
   }

   public final String getDescription() { return description; }
   public final Graph  getGraph()       { return graph; }

   void run1(Engine engine, Graph graph, Node previous_node,
             Object input, String next_node) {

      this.input = input;
      this.graph = graph;
      context = engine.getAgentContext();

      Node node = null;
      if ( exec() && (node = graph.newNode(engine,next_node,previous_node)) != null ) {
         node.setInput(engine,output);
         engine.notifyArcMonitors(this,node,Engine.ARC_SUCCEED);
         engine.add(node);
      }
      else {
         Core.DEBUG(3,"Arc " + description + " FAILED");
         engine.notifyArcMonitors(this,null,Engine.ARC_FAIL);
         previous_node.nextArc(engine);
      }
   }
   protected boolean exec() {
      output = input;
      return true;
   }
   protected void finalize() throws Throwable {
      if ( context != null )
         context.Engine().notifyArcMonitors(this,null,Engine.ARC_DISPOSE);
      super.finalize();
   }
   public String toString() { return getDescription(); }
}
