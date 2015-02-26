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

import zeus.actors.rtn.*;

public class g0 extends Graph {
   public static final String[][] entry = {
      {"zeus.actors.graphs.s0",
       "zeus.actors.graphs.a1", "zeus.actors.graphs.s1",
       "zeus.actors.graphs.a8", "zeus.actors.graphs.g0_b",
       "zeus.actors.graphs.a7", "zeus.actors.graphs.s7",
       "zeus.actors.graphs.a8", "zeus.actors.graphs.s8"},
      {"zeus.actors.graphs.g0_b",
       "zeus.actors.graphs.buy", "zeus.actors.graphs.n0"},
      {"zeus.actors.graphs.s1",
       "zeus.actors.graphs.a2", "zeus.actors.graphs.s2",
       "zeus.actors.graphs.a3", "zeus.actors.graphs.s6",
       "zeus.actors.graphs.a5", "zeus.actors.graphs.s3"},
      {"zeus.actors.graphs.s3",
       "zeus.actors.graphs.xb", "zeus.actors.graphs.s4"},
      {"zeus.actors.graphs.s4",
       "zeus.actors.graphs.a2", "zeus.actors.graphs.s5",
       "zeus.actors.graphs.a3", "zeus.actors.graphs.s6",
       "zeus.actors.graphs.a5", "zeus.actors.graphs.s3"},
      {"zeus.actors.graphs.s5",
       "zeus.actors.graphs.a0", "zeus.actors.graphs.s2"},
      {"zeus.actors.graphs.s6",
       "zeus.actors.graphs.a9", "zeus.actors.graphs.s9",
       "zeus.actors.graphs.a6", "zeus.actors.graphs.s5",
       "zeus.actors.graphs.a4", "zeus.actors.graphs.s2"},
      {"zeus.actors.graphs.s7"},
      {"zeus.actors.graphs.s8"},
      {"zeus.actors.graphs.s9"},
      {"zeus.actors.graphs.s2"},
      {"zeus.actors.graphs.n0"}
      // note system- and user-defined respondent-side negotiation protocols
      // define a subgraph in node s6
   };

   public g0() {
      super("g0",entry,"zeus.actors.graphs.s0");
   }
}
