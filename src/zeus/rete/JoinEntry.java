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



package zeus.rete;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;


class JoinEntry {
   int           l_position, r_position;
   String        l_attribute, r_attribute;
   ValueFunction l_value, r_value;

   JoinEntry(int l_position, String l_attribute, ValueFunction l_value,
             int r_position, String r_attribute, ValueFunction r_value) {

      this.l_position  = l_position;
      this.r_position  = r_position;
      this.l_attribute = l_attribute;
      this.r_attribute = r_attribute;
      this.l_value     = l_value;
      this.r_value     = r_value;
   }
   public String toString() {
      return "JoinEntry("+l_position + "," + l_attribute + "," + l_value + "," +
                          r_position + "," + r_attribute + "," + r_value + ")";
   }
   public boolean equals(Object obj) {
      if ( !(obj instanceof JoinEntry) ) return false;
      JoinEntry e = (JoinEntry)obj;
      return e.l_position == l_position &&
             e.r_position == r_position &&
             e.l_attribute.equals(l_attribute) &&
             e.r_attribute.equals(r_attribute) &&
             e.l_value.equals(l_value) && e.r_value.equals(r_value);
   }

}
