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


public class PreconditionChain {
   public PlanRecord record = null;
   public int position = -1;
   public int amount = -1;
   public String key = null;


   public PreconditionChain () {
   ;
   }
   

   public PreconditionChain(PlanRecord record, int position, int amount) {
      Assert.notNull(record);
      Assert.notFalse(position > -1);
      Assert.notFalse(amount >  0);
      this.record = record;
      this.position = position;
      this.amount = amount;
   }
   public PreconditionChain(String key, int amount) {
      Assert.notNull(key);
      Assert.notFalse(amount > 0);
      this.key = key;
      this.amount = amount;
   }
   public PreconditionChain(PreconditionChain ch) {
      if ( ch.isExternal() ) {
         this.key = ch.key;
         this.amount = ch.amount;
      }
      else {
         this.record = ch.record;
         this.position = ch.position;
         this.amount = ch.amount;
      }
   }
   public boolean isExternal() { return key != null; }
   public String toString() {
      String out = "PreconditionChain(";
      if ( isExternal() )
         out += key + "," + amount;
      else
         out += record + "," + position + "," + amount;

      out += ")";
      return out;
   }
}
