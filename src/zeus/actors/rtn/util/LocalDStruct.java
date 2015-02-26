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
 * A very specific structure used to manage negotaion between two agents. The
 * relationship between {@link GraphStruct}, {@link DStruct}, {@link
 * LocalDStruct} and {@link DelegationStruct} is as follows: Consider that
 * an agentA is given a goal g0, that decomposes into subgoals g1 and g2 that
 * need to be achieved externally. <p>
 * <table>
 * <tr> <td> {@link GraphStruct} </td> <td> created at the beginning of the
 * coordination process for goal g0 </td> </tr>
 * <tr> <td> {@link DStruct} </td> <td> created at the start of contracting/delegation
 * of subgoals g1 and g2 </td> </tr>
 * <tr> <td> {@link LocalDStruct} </td> <td> created at the start of
 * negotiation to achieve each subgoal g1 and g2, will contain additional
 * information e.g. the negotation protocol/strategy in use </td> </tr>
 * <tr> <td> {@link DelegationStruct} </td> <td> contains communication
 * specific information about the contracts </td> <tr>
 * </table>
 *
 * @see GraphStruct, LocalDStruct, DelegationStruct, DStruct
 */
public class LocalDStruct {
   public Vector goal = null;
   public String key = null;
   public String agent = null;
   public GraphStruct gs = null;
   public DelegationStruct result = null;
   public Object any = null;

   public LocalDStruct(String agent, DStruct ds) {
      this.agent = agent;
      this.goal = Misc.copyVector(ds.goal);
      this.gs = ds.gs;
   }

   public String toString() {
      String output = "(goal " + goal + "\n " +
                      " key " + key + "\n " +
                      " agent " + agent + "\n " +
                      " gs <gs>\n" +
                      " any " + any + "\n " +
                      " result " + result + "\n " + ")";
      return output;
   }
}
