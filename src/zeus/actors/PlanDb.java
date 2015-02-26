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
 * @(#)PlanDb.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

/**
 * The Plan Database is a simple storage component that holds the plan
 * descriptions known by the owning agent.  This information is used by
 * the agent's {@link Planner} component.
 */

public class PlanDb extends Hashtable {

   public void add(PlanRecord rec) {
     Assert.notNull(rec);
     Core.DEBUG(3,"Adding " + rec);
     Goal goal = rec.getGoal();
     String goalId = goal.getId();
     Object obj = this.put(goalId,rec);
     Core.ERROR(obj == null,1,this);
   }

   public void del(PlanRecord rec) {
     Assert.notNull(rec);
     Goal goal = rec.getGoal();
     String goalId = goal.getId();

     PlanRecord rec1 = (PlanRecord) this.remove(goalId);
     Assert.notFalse( rec1 == rec );
   }

   public void del(Vector List) {
      for( int i = 0; i < List.size(); i++ )
	 this.del((PlanRecord) List.elementAt(i) );
   }

   public void add(Vector List) {
      for( int i = 0; i < List.size(); i++ )
         this.add((PlanRecord) List.elementAt(i) );
   }

   public PlanRecord lookUp(Goal goal) {
      return (PlanRecord)this.get(goal.getId());
   }

   public PlanRecord lookUp(String goalId) {
      return (PlanRecord)this.get(goalId);
   }
   public boolean containsRecord(PlanRecord rec) {
      return this.get(rec.getGoal().getId()) != null;
   }
}
