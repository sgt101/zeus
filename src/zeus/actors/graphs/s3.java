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
 * @(#)s3.java 1.03b
 */


package zeus.actors.graphs;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.rtn.*;
import zeus.actors.rtn.util.*;

public class s3 extends Node {
   public s3() {
      super("s3");
   }
    // ST 050500 1.03bB node description due to CVB
   private String node_desc = "do for each external precondition";
   
   
   public final String getDesc()
      {return node_desc;}
   
   
   public final void setDesc(String node_desc) 
      {this.node_desc = node_desc;}
   // ST 050500 1.03bE
   // memory useful for backtracking
   private DStruct[] data = null;

   protected int exec() {
      GraphStruct gs = (GraphStruct)input;
      Core.DEBUG(2,"s3 Input gs = " + gs);

      // Now create split node for next_lot
      data = new DStruct[gs.external.size()];
      for(int i = 0; i < data.length; i++ ) {
         data[i] = new DStruct();
         data[i].goal = new Vector();
         data[i].goal.addElement(gs.external.elementAt(i));
         data[i].ignore = gs.ignore_agents;
         data[i].gs = gs;
      }
      output = data;
      return OK;
   }
   protected void reset() {
      // reset any state changed by exec()
   }
}
