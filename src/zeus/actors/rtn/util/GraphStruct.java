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



package zeus.actors.rtn.util;

import java.util.*;
import zeus.util.*;
import zeus.concepts.Goal;

/** 
 * This is the primary coordination structure. One is created for each goal
 * at the start of a coordination process, and maintained until the end of
 * the planning/contracting process.
 */
public class GraphStruct implements Cloneable {
   /** 
    * A vector containing the root goal
    */
   public Vector    goal      = null;
   /** 
    *A vector containing subgoals of the root goal that the agent can
    * perform on its own.
    */
   public Vector    internal  = null;
   /**
    * A vector of subgoals of the root that need to be delegated/contracted
    * out.
    */
   public Vector    external  = null;

   /**
    * A vector containing the results of a delegation round
    */
   public Vector    d_results = null;

   /**
    * A vector of {@link DelegationStruct}s of from agents whose proposals
    * will be accepted.
    */
   public Vector    selection = new Vector();

   /**
    * If agentA contracts a task out to another agentB, then
    * gs.confirmed for agentB indicates whether agentA has confirmed the
    * contract or not.
    */
   public boolean   confirmed = false;

   /**
    * If agentA contracts a task out to another agentB, then
    * gs.agent for agentB will point to agentA.
    */
   public String    agent = null;

   /** 
    * The contract reference.
    */
   public String    key = null;

   /**
    * A temporary structure for holding miscellaneous information.
    */
   public Object    any = null;

   /**
    * The amount of time availabe for the contracting process.
    */
   public double    timeout = 0;

   /**
    * The list of agents to ignore, i.e. not to attempt contracting the goal
    * out to.
    */
   public Vector    ignore_agents = new Vector();

   /** 
    * If agentA contracts a task out to another agentB, then
    * gs.confirmed_goal for agentB will contain the final goal agreed to by
    * agentB and confirmed by agentA. This goal might contain additional
    * information such references to agents that will be provide resources to
    * agentB so that it can achieve the goal.
    */
   public Vector    confirmed_goal = null;

   /**
    * A structure containing summary task decomposition information that is
    * utilised during planning.
    */
   public Hashtable decompositions;

   /**
    * A list of all {@link StrategyEvaluator}s participating in the
    * contracting process for the goal. <p>
    * For example, assume a goal g0 decomposes into two external
    * subgoals g1 and g2. Now an attempt is made to contract out g1 to agents
    * B and C, and g2 to agent D, E, F. In this case, we will have 5 strategy
    * evaluators, one for each subgoal/agent combination. The shared strategy
    * evaluator list allows one evaluator to consider the status of other
    * evaluators when making bidding decisions. (Note: No implemented example
    * to date has utilised the cross-evaluator dialogue feature).
    */
   public StrategyEvaluatorList evaluators = new StrategyEvaluatorList();

   public GraphStruct() {
   }
   public GraphStruct(String agent, Goal g) {
      goal = new Vector();
      goal.addElement(g);
      this.agent = agent;
      this.key = g.getId();
   }
   public GraphStruct(String agent, Goal g, String key) {
      goal = new Vector();
      goal.addElement(g);
      this.agent = agent;
      this.key = key;
   }
   public GraphStruct(String agent, Goal g, String key,
                      Vector internal, Vector external) {
      goal = new Vector();
      goal.addElement(g);
      this.agent = agent;
      this.key = key;
      this.internal = internal;
      this.external = external;
   }
   public GraphStruct(String agent, Vector goal) {
      this.goal = goal;
      this.agent = agent;
      this.key = ((Goal)goal.elementAt(0)).getId();
   }
   public GraphStruct(String agent, String key, Vector goals) {
      this.goal = goals;
      this.key = key;
      this.agent = agent;
   }

   public String toString() {
      String output = "(goal " + goal + "\n" +
                      " internal " + internal + "\n" +
                      " external " + external + "\n" +
                      " d_results " + d_results + "\n" +
                      " selection " + selection + "\n" +
                      " confirmed " + confirmed + "\n" +
                      " agent " + agent + "\n" +
                      " key " + key + "\n" +
                      " any " + any + "\n" +
                      " timeout " + timeout + "\n" +
                      " ignore_agents " + ignore_agents + "\n" +
                      " confirmed_goal " + confirmed_goal + "\n" +
                      " evaluators " + evaluators + "\n" +
                      " decompositions " + decompositions + "\n" + ")";

      return output;
   }
   public GraphStruct duplicate() {
      GraphStruct gs    = new GraphStruct();
      gs.goal           = Misc.copyVector(goal);
      gs.internal       = Misc.copyVector(internal);
      gs.external       = Misc.copyVector(external);
      gs.d_results      = Misc.copyVector(d_results);
      gs.selection      = Misc.copyVector(selection);
      gs.confirmed_goal = Misc.copyVector(confirmed_goal);
      gs.confirmed = confirmed;
      gs.agent = agent;
      gs.key = key;
      gs.any = any;
      gs.timeout = timeout;
      gs.ignore_agents = Misc.copyVector(ignore_agents);
      gs.evaluators = evaluators;
      return gs;
   }
}
