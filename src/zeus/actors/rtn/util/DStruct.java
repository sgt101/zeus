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

/**
 * This class holds information about the contract status of individual goals of
 * a collection of goals. For example, an agent might want to contract out the
 * following three goals {g1,g2,g3} which might be held in the external slot of
 * the {@link GraphStruct} structure (i.e. gs.external = {g1,g2,g3}).<p>
 * During contracting, a {@link DStruct} will be created for each subgoal g1, ... g3,
 * and the subgoals contracted out in parallel.
 */

public class DStruct {
   /**
    * A vector containing a single goal being contracted out
    */
   public Vector      goal = null;

   /**
    * The list of agents that our agent will send a call for proposals to.
    */
   public Vector      agents = null;

   /**
    * A reference to the original coordination structure containing the root goal
    */
   public GraphStruct gs = null;

   /**
    * The list of agent to avoid sending call for proposals to. For example,
    * during replanning, this list will contain agents that have already failed
    * to achieve the goal.
    */
   public Vector      ignore = null;

   /**
    * The results of the delegation process. This vector will typically
    * comprise {@link DelegationStruct} objects.
    */
   public Vector      results = new Vector();

   public String toString() {
      return  "(goal " + goal + "\n " +
              " agents " + agents + "\n " +
              " gs <gs>\n " +
              " results " + results + "\n " +
              " ignore " + ignore + "\n " + ")";
   }
}
