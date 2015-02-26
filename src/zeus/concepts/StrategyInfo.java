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


public class StrategyInfo {
   public static final int USE    = 0;
   public static final int NO_USE = 1;

   public static final String DEFAULT_INITIATOR_STRATEGY =
      SystemProps.getProperty("system.strategy.initiator.default",
                              "NO_DEFAULT_STRATEGY_AVAILABLE");
   public static final String DEFAULT_RESPONDENT_STRATEGY =
      SystemProps.getProperty("system.strategy.respondent.default",
                              "NO_DEFAULT_STRATEGY_AVAILABLE");

   protected Fact      fact;
   protected String    strategy = null;
   protected Vector    agents = new Vector();
   protected Vector    relations = new Vector();
   protected int       type = USE;
   protected Hashtable parameters = new Hashtable();

   public StrategyInfo () { 
   }


   public StrategyInfo(Fact fact) {
      this.fact = new Fact(fact);
      this.type = NO_USE;
   }

   public StrategyInfo(Fact fact, String strategy) {
      this.fact = new Fact(fact);
      this.type = USE;
      this.strategy = strategy;
   }

   public StrategyInfo(Fact fact, String strategy,
                       String[] agents, String[] relations, Hashtable param) {
      this.fact = new Fact(fact);
      this.type = USE;
      this.strategy = strategy;
      setAgents(agents);
      setRelations(relations);
      setParameters(param);
   }

   public StrategyInfo(Fact fact, String[] agents, String[] relations) {
      this.fact = new Fact(fact);
      this.type = NO_USE;
      setAgents(agents);
      setRelations(relations);
   }

   public StrategyInfo(StrategyInfo info) {
      fact = new Fact(info.getFact());
      type = info.getType();
      if ( type == USE ) {
         strategy = info.getStrategy();
         setParameters(info.getParameters());
      }
      setAgents(info.getAgents());
      setRelations(info.getRelations());
   }

   public StrategyInfo duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public StrategyInfo duplicate(DuplicationTable table) {
      if ( type == USE )
         return new StrategyInfo(fact.duplicate(table), strategy,
	                         getAgents(), getRelations(), getParameters());
      else
         return new StrategyInfo(fact.duplicate(table),
	                         getAgents(), getRelations());
   }

   public void setType(int mode) {
      Assert.notFalse(mode == USE || mode == NO_USE);
      this.type = mode;
   }
   public int  getType() { return type; }
   public Fact getFact() { return fact; }

   public String getStrategy() {
      if ( type == NO_USE )
         Core.USER_ERROR("Use/No_Use restriction error");
      return strategy;
   }
   public Hashtable getParameters() {
      if ( type == NO_USE )
         Core.USER_ERROR("Use/No_Use restriction error");

      Hashtable output = new Hashtable();
      Enumeration enum = parameters.keys();
      Object key;
      while( enum.hasMoreElements() ) {
         key = enum.nextElement();
         output.put(key,parameters.get(key));
      }
      return output;
   }

   public String[] getAgents()    { return Misc.stringArray(agents); }
   public String[] getRelations() { return Misc.stringArray(relations); }

   public void setStrategy(String strategy) {
      if ( type == NO_USE )
         Core.USER_ERROR("Use/No_Use restriction error");
      this.strategy = strategy;
   }
   public void setParameters(Hashtable input) {
      if ( type == NO_USE )
         Core.USER_ERROR("Use/No_Use restriction error");
      parameters.clear();
      Enumeration enum = input.keys();
      Object key;
      while( enum.hasMoreElements() ) {
         key = enum.nextElement();
         parameters.put(key,input.get(key));
      }
   }

   public void clearParameters() {
      if ( type == NO_USE )
         Core.USER_ERROR("Use/No_Use restriction error");
      parameters.clear();
   }

   public void setAgents(String[] input) {
      agents = Misc.stringVector(input);
   }
   public void setAgents(Vector input) {
      agents = Misc.copyVector(input);
   }

   public void setRelations(String[] input) {
      relations = Misc.stringVector(input);
   }
   public void setRelations(Vector input) {
      relations = Misc.copyVector(input);
   }

   public boolean resolve(Bindings b) {
      return fact.resolve(b);
   }

   public String toString() {
      String s = "(:fact " + fact + " " + ":type " + type + " ";
      if ( type == USE ) {
         s += ":strategy \"" + strategy + "\" ";
         if ( !parameters.isEmpty() ) {
            Enumeration enum = parameters.keys();
            Object key;
            s += ":parameters (";
            while( enum.hasMoreElements() ) {
               key = enum.nextElement();
               s += "\"" + key + "\" \"" + parameters.get(key) + "\" ";
            }
            s = s.trim() + ")" + " ";
         }
      }
      if ( !agents.isEmpty() )
         s += ":agents (" + Misc.concat(agents) + ")" + " ";
      if ( !relations.isEmpty() )
         s += ":relations (" + Misc.concat(relations) + ")";
      return s.trim() + ")";
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
                 ":type " + type + eol;
      if ( type == USE ) {
         s += ":strategy \"" + strategy + "\"" + eol;
         if ( !parameters.isEmpty() ) {
            Enumeration enum = parameters.keys();
            Object key;
            s += ":parameters (";
            while( enum.hasMoreElements() ) {
               key = enum.nextElement();
               s += "\"" + key + "\" \"" + parameters.get(key) + "\" ";
            }
            s = s.trim() + ")" + eol;
         }
      }
      if ( !agents.isEmpty() )
         s += ":agents (" + Misc.concat(agents) + ")" + eol;
      if ( !relations.isEmpty() )
         s += ":relations (" + Misc.concat(relations) + ")" + eol;
      return s.trim() + "\n" + tabs + ")";
   }
}
