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



package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class se3 extends Node {
   public se3() {
      super("se3");
   }

   // memory useful for backtracking

   protected int exec() {
      Engine engine = context.Engine();
      AuditTable audit = engine.getAuditTable();

      // first perform join
      GraphStruct gs = (GraphStruct)input;
      Core.DEBUG(2,"se3 Input gs " + gs);

      // Now prepare for post-contract phase
      Goal g = (Goal)gs.goal.elementAt(0);

      audit.add(g,gs.key,g.getCost(),false,false,context.whoami(),
                gs.agent,g.getEndTime());

      Core.DEBUG(2,"*** SE3 AuditTable State ***");
      Core.DEBUG(2,audit);

      // remove reserved resource and forward to auditable
      DataRec record = (DataRec)gs.any;
      Fact[] fact = record.getData();
      Vector data = new Vector();
      data.addElement(fact[0]);
      context.ResourceDb().consume(record);
      DelegationStruct ds = new DelegationStruct(context.whoami(),"result",
         g.getId(),data);
      audit.goodsReceived(ds);

      output = input;
      return OK;
   }
   protected void reset() {
   }
}
