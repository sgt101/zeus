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



package zeus.concepts;

import java.util.*;
import zeus.util.*;
import zeus.concepts.fn.*;
/**
    PlanScripts are (I think) a set of tasks chained together to form a meta task
    */
public class PlanScript extends SummaryTask {
    
   public PlanScript() {
      super();
      type = SCRIPT;
      autorun = true;
   }

   public PlanScript(String name, ValueFunction time, ValueFunction cost,
                     TaskNode[] nodes, TaskLink[] links,
		     LogicalFn[] constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      autorun = true;
   }
   
   
   public PlanScript(String name, String time, String cost,
                     TaskNode[] nodes, TaskLink[] links,
                     LogicalFn[] constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      autorun = true;
   }
   
   
   public PlanScript(String name, ValueFunction time, ValueFunction cost,
                     Vector nodes, Vector links, Vector constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      autorun = true;
   }
   
   
   public PlanScript(String name, String time, String cost,
                     Vector nodes, Vector links, Vector constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      autorun = true;
   }



   public PlanScript(String name, boolean autorun,
                     ValueFunction time, ValueFunction cost,
                     TaskNode[] nodes, TaskLink[] links,
		     LogicalFn[] constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      this.autorun = autorun;
   }
   public PlanScript(String name, boolean autorun,
                     String time, String cost,
                     TaskNode[] nodes, TaskLink[] links,
                     LogicalFn[] constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      this.autorun = autorun;
   }
   
   
   public PlanScript(String name, boolean autorun,
                     ValueFunction time, ValueFunction cost,
                     Vector nodes, Vector links, Vector constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      this.autorun = autorun;
   }
   
   
   public PlanScript(String name, boolean autorun,
                     String time, String cost,
                     Vector nodes, Vector links, Vector constraints) {
      super(name,time,cost,nodes,links,constraints);
      type = SCRIPT;
      this.autorun = autorun;
   }
   

   public PlanScript(PlanScript task) {
      type = SCRIPT;
      autorun = task.isAutorun();
      name = task.getName();
      cost = task.getCostFn();
      time = task.getTimeFn();
      setNodes( task.getNodes() );
      setLinks( task.getLinks() );
      setConstraints( task.getConstraints() );
   }


   public boolean isAutorun()                 { return autorun; }
   
   public void    setAutorun(boolean autorun) { this.autorun = autorun; }

   public AbstractTask duplicate(DuplicationTable table) {
      TaskNode[]  Xnodes = new TaskNode[nodes.size()];
      TaskLink[]  Xlinks = new TaskLink[links.size()];
      LogicalFn[] Xconstraints = new LogicalFn[constraints.size()];

      ValueFunction Xtime = time.duplicate(table);
      ValueFunction Xcost = cost.duplicate(table);

      for(int i = 0; i < nodes.size(); i++ )
         Xnodes[i] = ((TaskNode)nodes.elementAt(i)).duplicate(table);

      for(int i = 0; i < links.size(); i++ )
         Xlinks[i] = ((TaskLink)links.elementAt(i)).duplicate(table);

      for(int i = 0; i < constraints.size(); i++ )
         Xconstraints[i] = (LogicalFn)((LogicalFn)constraints.elementAt(i)).duplicate(table);

      return new PlanScript(name,autorun,Xtime,Xcost,Xnodes,Xlinks,Xconstraints);
   }
}
