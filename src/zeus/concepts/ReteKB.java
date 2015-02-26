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
import zeus.concepts.fn.*;
import zeus.rete.Rule;

/**
     this is the definition of a kb in Zeus for the Rulebase tasks. 
     @see zeus.rete.Rule
     @see zeus.rete.ReteEngine
     */
public class ReteKB extends AbstractTask {
   protected Vector rules = new Vector();

   public ReteKB() {
      type = BEHAVIOUR;
   }

   public ReteKB(String name) {
      type = BEHAVIOUR;
      setName(name);
   }
   public ReteKB(String name, Vector rules) {
      type = BEHAVIOUR;
      setName(name);
      setRules(rules);
   }
   public ReteKB(String name, Rule[] rules) {
      type = BEHAVIOUR;
      setName(name);
      setRules(rules);
   }
   public ReteKB(ReteKB kb) {
      type = BEHAVIOUR;
      name = kb.getName();
      setRules(kb.getRules());
   }

   public Rule getRule(String rulename) {
      Rule rule = null;
      for(int j = 0; j < rules.size(); j++ ) {
         rule = (Rule)rules.elementAt(j);
         if ( rule.getName().equals(rulename) )
            return rule;
      }
      return null;
   }

   public Rule getRule(int position) {
      return (Rule)rules.elementAt(position);
   }

   public Rule[] getRules() {
      Rule[] out = new Rule[rules.size()];
      for(int j = 0; j < rules.size(); j++ )
         out[j] = new Rule((Rule)rules.elementAt(j));
      return out;
   }

   public Rule removeRule(String rulename) {
      Rule rule = null;
      for(int j = 0; j < rules.size(); j++ ) {
         rule = (Rule)rules.elementAt(j);
         if ( rule.getName().equals(rulename) ) {
            rules.removeElementAt(j--);
            return rule;
         }
      }
      return null;
   }

   public Rule removeRule(int position) {
      Rule rule = (Rule)rules.elementAt(position);
      rules.removeElementAt(position);
      return rule;
   }

   public void addRule(Rule rule) {
      rules.addElement(new Rule(rule));
   }

   public void setRules(Vector List) {
      rules.removeAllElements();
      for(int i = 0; i < List.size(); i++ )
         rules.addElement(new Rule((Rule)List.elementAt(i)));
   }
   public void setRules(Rule[] List) {
      rules.removeAllElements();
      for(int i = 0; i < List.length; i++ )
         rules.addElement(new Rule(List[i]));
   }

   public boolean resolve(Bindings bindings) {
      Rule rule;
      for(int i = 0; i < rules.size(); i++ ) {
         rule = (Rule)rules.elementAt(i);
         if ( !rule.resolve(bindings) )
            return false;
      }
      return true;
   }

   public boolean isValid() {
      return true;
   }

   public String toString() {
      String s = "(:" + TaskTypes[type] + " " + name + " ";

      for(int i = 0; i < rules.size(); i++ )
         s += rules.elementAt(i) + " ";
      return s.trim() + ")";
   }

   public String pprint(int sp) {
      String suffix, prefix;
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = "(:" + TaskTypes[type] + " " + name + "\n";
      for(int i = 0; i < rules.size(); i++ )
         s += ((Rule)rules.elementAt(i)).pprint(sp+3) + "\n";
      return tabs + s + tabs + ")";
   }

   public AbstractTask duplicate(DuplicationTable table) {
      Rule[]  Xrules = new Rule[rules.size()];
      for(int i = 0; i < rules.size(); i++ )
         Xrules[i] = ((Rule)rules.elementAt(i)).duplicate(table);
      return new ReteKB(name,Xrules);
   }
}
