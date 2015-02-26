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

import java.io.*;
import java.util.*;
import zeus.util.*;

public class FactSummary extends Summary {
   public static final String ALLOCATED = "Reserved";
   public static final String UNALLOCATED = "Free";

   protected Fact   fact;

   public FactSummary(Fact fact, String status) {
      setFact(fact);
      setStatus(status);
   }

   public FactSummary(FactSummary s) {
      setFact(s.getFact());
      setStatus(s.getStatus());
   }

   public String getType()     { return fact.getType(); }
   public String getId()       { return fact.getId(); }
   public Fact   getFact()     { return fact; }

   public String[] summarize() {
      String[] data = { fact.getId(), status };
      return data;
   }

   public void setStatus(String status) {
      Assert.notNull(status);
      Assert.notFalse( status.equals(ALLOCATED) || status.equals(UNALLOCATED) ||
                       status.equals(UNKNOWN) );
      this.status = status;
   }
   public void setFact(Fact fact) {
      Assert.notNull(fact);
      this.fact = fact;
   }

   public boolean equals(FactSummary s ) {
      return fact.equals(s.getFact()) &&
             status.equals(s.getStatus());
   }

   public String toString() {
      return( "(" +
               ":fact " + fact.toString() + " " + 
               ":status " + status +
              ")"
            );
   } 

   public String pprint() {
      return pprint(0);
   }
   public String pprint(int sp) {
      String prefix;
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      prefix = "(:fact ";
      String s = prefix + fact.pprint(sp+prefix.length()) + eol +
                 ":status " + status + eol;
      return s.trim() + "\n" + tabs + ")";
   }
}
