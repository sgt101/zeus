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



package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;


public class EffectChain {
   public PlanRecord record = null;
   public int position = -1;
   public int amount = -1;
   public int start = -1;
   public boolean consumed = true;
   public String key = null;

   // meaningless init  to allow rearch
   public EffectChain () {
   ;
   }
   

   public EffectChain(PlanRecord record, int position, int amount,
                      int start, boolean consumed) {
      Assert.notNull(record);
      Assert.notFalse(position > -1);
      Assert.notFalse(amount >  0);
      Assert.notFalse(start >  0);
      this.record = record;
      this.position = position;
      this.amount = amount;
      this.start = start;
      this.consumed = consumed;
   }
   public EffectChain(String key, int amount, int start, boolean consumed) {
      Assert.notNull(key);
      Assert.notFalse(amount > 0);
      Assert.notFalse(start >  0);
      this.key = key;
      this.amount = amount;
      this.start = start;
      this.consumed = consumed;
   }
   public EffectChain(EffectChain ch) {
      if ( ch.isExternal() ) {
         this.key = ch.key;
         this.amount = ch.amount;
      }
      else {
         this.record = ch.record;
         this.position = ch.position;
         this.amount = ch.amount;
      }
      this.start = ch.start;
      this.consumed = ch.consumed;
   }
   public boolean isExternal() { return key != null; }
   public String toString() {
      String out = "EffectChain(";
      if ( isExternal() )
         out += key + "," + amount + "," + start + "," + consumed;
      else 
         out += record + "," + position + "," + amount + "," + start + "," +
                consumed;

      out += ")";
      return out;
   }
}
