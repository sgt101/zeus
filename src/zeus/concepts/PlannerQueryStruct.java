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



package zeus.concepts;

import java.util.*;
import zeus.util.*;

public class PlannerQueryStruct {
   public Vector             external = null;
   public Vector             goals = null;
   public Vector             internal = new Vector();
   public Bindings           bindings = new Bindings();
   public double             timeout = 0;
   public Hashtable          decompositions = new Hashtable();

   public PlannerQueryStruct(Goal g) {
      Vector goals = new Vector();
      goals.addElement(g);
      this.goals = goals;
      this.external = new Vector();
   }
   public PlannerQueryStruct(Goal g, Vector external) {
      Vector goals = new Vector();
      goals.addElement(g);
      this.goals = goals;
      this.external = external;
   }
   public PlannerQueryStruct(Vector goals, Vector external) {
      this.goals = goals;
      this.external = external;
   }
   public PlannerQueryStruct(Vector goals) {
      this.goals = goals;
      this.external = new Vector();
   }

   public String toString() {
      String output = "\n " +
                      "(goals " + goals + "\n " +
                      " internal " + internal + "\n " +
                      " external " + external + "\n " +
                      " timeout " + timeout + "\n " +
                      " bindings " + bindings + "\n " + 
                      " decompositions " + decompositions + "\n " +
                      ")";
      return output;
   }
}

