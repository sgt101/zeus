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

/**
    Change Log
    ----------
    07/06/01 () init added
 *  21/06/02 - DAML-S changes
 * 26/9/02 - DAML-s changes back - moved to a name so that a URL can be 
 *generated that is used in the service registration
 *
    */
public class AbilitySpec {
   protected Fact   fact;
   protected int    time;
   protected double cost;
   protected String name = "noname";
     
   public AbilitySpec () {
    
   }

   
 public AbilitySpec(String name, Fact fact, int time, double cost) {
      this.fact = new Fact(fact);
      this.time = time;
      this.cost = cost;
      this.name = name;
   }
   
   public AbilitySpec( Fact fact, int time, double cost) {
      this.fact = new Fact(fact);
      this.time = time;
      this.cost = cost;
   }

   
   public AbilitySpec(AbilitySpec ability) {
      fact = new Fact(ability.getFact());
      time = ability.getTime();
      cost = ability.getCost();
   }

   
   public AbilitySpec duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   
   
   public AbilitySpec duplicate(DuplicationTable table) {
      return new AbilitySpec(name,fact.duplicate(table), time, cost);
   }

   
   public String getType()     { return fact.getType(); }
   public String getId()       { return fact.getId(); }
   public Fact   getFact()     { return fact; }
   public int    getTime()     { return time; }
   public double getCost()     { return cost; }
   
   public String getName () { return name;} 

   public void setTime(int t)     { time = t; }
   public void setCost(double c)  { cost = c; }

   public void setName(String s) { name = s; }
   
   public boolean resolve(Bindings b) {
      return fact.resolve(b);
   }

   public boolean equals(AbilitySpec ability ) {
      return fact.equals(ability.getFact()) &&
             time == ability.getTime() &&
             Math.abs(cost - ability.getCost()) < 1.0e-12; // very small number!
             // this is because there are rounding errors that mean that sometimes things 
             // that should be 0 come out as 0.000001 ...
      
   }

   public String toString() {
    String front = ( "(" +":fact " + fact + " " + ":time " + time + " " +  ":cost " + cost + " " );
    if (name != null) { 
        front +=  ":name " + name +  ")";}
    else front += ":name NULL)"; //10-1-06 to fix parser error. 
    return front; 
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
                 ":time " + time + eol +
                 ":cost " + cost + eol +
      			 ":name NULL" + eol;
      return s.trim() + "\n" + tabs + ")";
   }
}
