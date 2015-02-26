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



package zeus.util;

import java.util.*;

public class Clock {
   private long start = 0;
   private long increment = 1;

   public Clock () {
   ;
   } 

   public Clock(long begin, long incr) {
      start = begin;
      increment = incr;
   }

   public final Time currentTime() {
      return new Time((double)(System.currentTimeMillis()-start)/increment);
   }

   public final Time time(long ctm) {
      return new Time((double)(ctm-start)/increment);
   }

   public final String initData() {
      return "" + start + " " + increment;
   }

   public final long getIncrement() {
      return increment;
   }
}
      
