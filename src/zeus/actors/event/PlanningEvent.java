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



package zeus.actors.event;

import zeus.util.*;
import zeus.concepts.*;

public class PlanningEvent extends Event {
   public static final int PLANNING   = 0;
   public static final int ENACTMENT  = 1;
   public static final int REPLANNING = 2;

   public static final long START_MASK   = 1;
   public static final long FAIL_MASK    = 2;
   public static final long SUCCEED_MASK = 4;

   protected int type;

   public PlanningEvent(Object source, Goal object, long event_mask, int type) {
      super(source,object,PLANNING_FIRST,PLANNING_LAST,event_mask);
      Assert.notFalse(type >= PLANNING && type <= REPLANNING);
      this.type = type;
   }
   public Goal getGoal() { return (Goal)object; }
   public int  getType() { return type; }
}
