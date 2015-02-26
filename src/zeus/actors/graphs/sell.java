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

public class sell extends Graph {
   public static String[][] sell_entry;

   private static final String[][] entry = {
      {"zeus.actors.graphs.se0",
       "zeus.actors.graphs.a0", "zeus.actors.graphs.se1"},
      {"zeus.actors.graphs.se1",
       "zeus.actors.graphs.a0", "zeus.actors.graphs.se2"},
      {"zeus.actors.graphs.se2",
       "zeus.actors.graphs.a0", "zeus.actors.graphs.se3"},
      {"zeus.actors.graphs.se3"}
   };

   public sell() {
      super("sell",entry,"zeus.actors.graphs.se0");
   }
}
