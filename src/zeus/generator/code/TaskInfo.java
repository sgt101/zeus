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



package zeus.generator.code;

import zeus.util.*;

public class TaskInfo extends GenerationInfo {
    
   public boolean generate = true;
   public String status = GenerationPlan.SAVE_NEEDED; 
   public String task_external = "null"; 
   
   public TaskInfo(String id, String name) {
      super(id);
      this.name = name;
   }


   public String[] summarize() {
      String[] out = new String[4];
      out[NAME] = name;
      out[TYPE] = TASK;
      out[ID]   = id;

      out[COMMAND] = "";
      return out;
   }
   
   
   public String isValid() {
      return null;
   }


   public String toString() {
      String s = "";
      s += "(:id "   + id;
      s += " :generate " + generate;
      s += " :status " + status;
      s += " :external " + task_external; 
      s += ")";
      return s;
   }

   public String pprint(int sp) {
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs;
      String s = tabs;
      s += "(:id "   + id + eol;
      s += " :generate " + generate + eol;
      s += " :status " + status + eol;
      s += " :external " + Misc.escape(task_external) + eol; 
      s += ")";
      return s;
   }
}
