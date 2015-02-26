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
 * @(#)ZeusAgent.java 1.00
 */

package zeus.agents;

import java.awt.*;
import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.actors.rtn.Engine;
import zeus.rete.ReteEngine;


/**
 * This class implements the core functionality of all Zeus Task Agents.
 * Every task agent implementation created by the Zeus toolkit includes a call
 * to the ZeusAgent constructor, which creates and initialises all the agent's
 * internal components and stores references to them in its {@link AgentContext}. <p>
 *
 * This class is of particular interest to developers because it provides
 * methods that allow the agent state to be interrogated and changed, as well
 * listing all the listener classes that can be added to monitor the agent's
 * state. <p>
 */

public class ZeusAgent extends BasicAgent
{
  /** Version 1.01 constructor - the componentmask parameter will prevent the
      creation of unnecessary agent components */
  public ZeusAgent(String name, String ontology_file, Vector nameservers,
                   int planner_width, int planner_length, 
                   boolean hasTasks, boolean hasRules)
  {
    super(SystemProps.getProperty("agent.names.agent"),name,nameservers);

    OntologyDb db = new OntologyDb(context.GenSym());
    context.set(db);

    int status = db.openFile(new File(ontology_file));
    if ( (status & OntologyDb.ERROR_MASK) != 0 ) {
       System.err.println("File I/O Error: " + db.getError());
      // System.exit(0);
    }
    else if ( (status & OntologyDb.WARNING_MASK) != 0 ) {
       System.err.println("Warning: " + db.getWarning());
    }

    // Create Agent Components
    new OrganisationDb(context);
    new ResourceDb(context);
    new Engine(context);
    new ProtocolDb(context);
    
    // changed by Jaron (v1.04) to fix uninitialised component bug
    new Planner(context,planner_width,planner_length);
    new TaskDb(context);
    new ExecutionMonitor(context);

    if (hasTasks)
    {
      // strictly speaking if there are no tasks, the Planner, TaskDb
      // and ExecutionMonitor don't need to exist. However, some internal
      // code does still reference these components whether the agent
      // has tasks or not - so this selective initialisation has been removed
    }

    if (hasRules)
    {
      // agent has rules - create rule engine
      ReteEngine rete = new ReteEngine(context);
    }
  }

  /** v1.00 Constructor, provided for backwards compatibility */
  public ZeusAgent(String name, String ont_file, Vector nameservers, int plan_width, int plan_length)
  {
    this(name, ont_file, nameservers, plan_width, plan_length, true, true);
  }


  public void addProtocol(Vector Protocols) {
    context.ProtocolDb().addProtocol(Protocols);
  }

  public void addProtocol(ProtocolInfo aProtocol) {
    context.ProtocolDb().addProtocol(aProtocol);
  }

  public void addTask(Vector Tasks) {
    for(int i = 0; i < Tasks.size(); i++ )
       addTask((AbstractTask)Tasks.elementAt(i));
  }

  public void addTask(AbstractTask aTask) {
    switch(aTask.getType()) {
       case AbstractTask.PRIMITIVE:
       case AbstractTask.SUMMARY:
            context.TaskDb().add((Task)aTask);
            break;
       case AbstractTask.BEHAVIOUR:
            context.ReteEngine().add((ReteKB)aTask);
            break;
       case AbstractTask.SCRIPT:
            break;
    }
  }

  public void addFact(Vector Facts) {
    context.ResourceDb().add(Facts);
  }

   /** Provides the agent with a new resource fact */
  public void addFact(Fact aFact) {
    context.ResourceDb().add(aFact);
  }

  public void addAbility(Vector AbilityItems) {
    context.OrganisationDb().add(AbilityItems);
  }

  public void addAbility(AbilityDbItem anAbilityItem ) {
    context.OrganisationDb().add(anAbilityItem);
  }

  public void addRelation(Vector Relations ) {
    context.OrganisationDb().addRelation(Relations);
  }

  public void addRelation(String agent, String aRelation ) {
    context.OrganisationDb().addRelation(agent,aRelation);
  }

  /** Adds an achievement goal to the agent's co-ordination engine, this
      will alter the agent's behaviour as it attempts to satisfy the goal */
  public void achieve(Goal g) {
    context.Engine().achieve(g);
  }

  /** Instructs the agent to achieve a specified goal */
  public void achieve(Goal g, String key) {
    context.Engine().achieve(g,key);
  }

  /** Instructs the agent to acquire the fact referred to in the goal */
  public void buy(Goal g) {
    context.Engine().buy(g);
  }

  /** Instructs the agent to sell the fact referred to in the goal */
  public void sell(Goal g) {
    context.Engine().sell(g);
  }

  /** Adds a new set of rules to the agent's rule base */
  public void addRulebase(ReteKB kb) {
    context.ReteEngine().add(kb);
  }

  public void addAbilityMonitor(AbilityMonitor monitor, long type) {
     context.OrganisationDb().addAbilityMonitor(monitor,type);
  }
  public void removeAbilityMonitor(AbilityMonitor monitor, long type) {
     context.OrganisationDb().removeAbilityMonitor(monitor,type);
  }
  public void addRelationMonitor(RelationMonitor monitor, long type) {
     context.OrganisationDb().addRelationMonitor(monitor,type);
  }
  public void removeRelationMonitor(RelationMonitor monitor, long type) {
     context.OrganisationDb().removeRelationMonitor(monitor,type);
  }
  public void addClockMonitor(ClockMonitor monitor, long type) {
     context.ExecutionMonitor().addClockMonitor(monitor,type);
  }
  public void removeClockMonitor(ClockMonitor monitor, long type) {
     context.ExecutionMonitor().removeClockMonitor(monitor,type);
  }
  public void addPlanningMonitor(PlanningMonitor monitor, long type) {
     context.Planner().addPlanningMonitor(monitor,type);
  }
  public void removePlanningMonitor(PlanningMonitor monitor, long type) {
     context.Planner().removePlanningMonitor(monitor,type);
  }
  public void addFactMonitor(FactMonitor monitor, long type) {
     context.ResourceDb().addFactMonitor(monitor,type);
  }
  public void removeFactMonitor(FactMonitor monitor, long type) {
     context.ResourceDb().removeFactMonitor(monitor,type);
  }
  public void addTaskMonitor(TaskMonitor monitor, long type) {
     context.TaskDb().addTaskMonitor(monitor,type);
  }
  public void removeTaskMonitor(TaskMonitor monitor, long type) {
     context.TaskDb().removeTaskMonitor(monitor,type);
  }
  public void addProtocolMonitor(ProtocolMonitor monitor, long type) {
     context.ProtocolDb().addProtocolMonitor(monitor,type);
  }
  public void removeProtocolMonitor(ProtocolMonitor monitor, long type) {
     context.ProtocolDb().removeProtocolMonitor(monitor,type);
  }
}
