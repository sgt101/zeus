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
import zeus.concepts.*;

/**
 * A list of all {@link StrategyEvaluator}s participating in the
 * contracting process for a goal. <p>
 * For example, assume a goal g0 decomposes into two external
 * subgoals g1 and g2. Now an attempt is made to contract out g1 to agents
 * B and C, and g2 to agent D, E, F. In this case, we will have 5 strategy
 * evaluators, one for each subgoal/agent combination. The shared strategy
 * evaluator list allows one evaluator to consider the status of other
 * evaluators when making bidding decisions. (Note: No implemented example
 * to date has utilised the cross-evaluator dialogue feature).
 *
 * TO DO:
 * This class should contain methods to retrieve strategy evaluators
 * depending on their variour properties, e.g. their state, goal, current
 * price, etc.
 */

public class StrategyEvaluatorList extends HSet {
}
