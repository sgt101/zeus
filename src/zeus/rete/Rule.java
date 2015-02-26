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

/** 
   * Rule is the class that defines what a Zeus rule actually looks like
   * @see zeus.concepts.ReteKB 
   * @see zeus.rete.ReteEngine
   * @see zeus.rete.ConflictSet
   * @see zeus.rete.TypeNode
   * @see zeus.rete.ActionNode
 * @author Divine Ndumu 
 *@author Simon Thompson
 * Changed to support rules as services 
    */
public class Rule {
   static final String IMPLIES = "=>";
   static final String INITIAL_FACT = OntologyDb.ROOT;
   static final ReteFact  initial_fact = new ReteFact(INITIAL_FACT);
    /** 
        rule salience is the priority measure for a rule in the 
        rulebase. 
        */
   public static final int MIN_SALIENCE  = 0;
   public static final int MAX_SALIENCE  = 9;
   public static final int NORM_SALIENCE = 5;

   String name = null;
   int salience = NORM_SALIENCE;
   // service indicates that this rule is a service
   boolean service = false; 
   Vector actions = new Vector();
   Vector patterns = new Vector();
   boolean can_add_patterns = true;

  
   /** 
    * filp the service status
    */
   public void setService () { 
       service = !service;
   }
   
   public boolean isService () { 
       return service;
   }
       
   
   
   public Rule(String name) {
      this.name = name;
   }
   
   
   protected Rule(String name, boolean state) {
      this.name = name;
      can_add_patterns = state;
   }
   
   
   public Rule(String name, int salience) {
      this.name = name;
      setSalience(salience);
   }
   
   
   protected Rule(String name, int salience, boolean state) {
      this.name = name;
      can_add_patterns = state;
      setSalience(salience);
   }
   
   
   public Rule(Rule r) {
      name = r.name;
      salience = r.salience;
      can_add_patterns = r.can_add_patterns;

      Pattern p;
      Action a;
      for(int i = 0; i < r.patterns.size(); i++ ) {
         p = (Pattern)r.patterns.elementAt(i);
         patterns.addElement(new Pattern(p));
      }
      for(int i = 0; i < r.actions.size(); i++ ) {
         a = (Action)r.actions.elementAt(i);
         actions.addElement(new Action(a));
      }
   }
   
   
   public Rule duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   
   
   public Rule duplicate(DuplicationTable table) {
      Rule r = new Rule(name,salience,can_add_patterns);
      Pattern p;
      Action a;
      for(int i = 0; i < patterns.size(); i++ ) {
         p = (Pattern)patterns.elementAt(i);
         p = p.duplicate(table);
         r.patterns.addElement(p);
      }
      for(int i = 0; i < actions.size(); i++ ) {
         a = (Action)actions.elementAt(i);
         a = a.duplicate(table);
         r.actions.addElement(a);
      }
      return r;
   }
   
   
   public void addPattern(Pattern p) {
      Assert.notFalse( can_add_patterns );

      if ( patterns.isEmpty() ) {
         switch(p.tag) {
            case Pattern.NOT:
            case Pattern.TEST:
                 patterns.addElement(new Pattern(initial_fact));
                 break;

            default:
                 break;
         }
      }
      patterns.addElement(p);
   }
   
   
   public void addAction(Action a) {
      can_add_patterns = false;
      if ( patterns.isEmpty() )
         patterns.addElement(new Pattern(initial_fact));
      actions.addElement(a);
   }
   
   
   int nTerminals() {
      Pattern p;
      int count = 0;
      for(int i = 0; i < patterns.size(); i++ ) {
         p = (Pattern)patterns.elementAt(i);
         switch( p.tag ) {
            case Pattern.NONE:
            case Pattern.NOT:
                 count++;
                 break;
            default:
                 break;
         }
      }
      return count;
   }
   
   
   public String getName() {
      return name;
   }
   
   
   public int getSalience() {
      return salience;
   }
   
   
   public void setName(String name) {
      Core.ERROR(name,1,this);
      this.name = name;
   }
   
   
   public Vector getPatterns(){
      return patterns;
   }



   public void setSalience(int salience) {
      if ( salience < MIN_SALIENCE || salience > MAX_SALIENCE ) {
         Core.USER_ERROR("Invalid salience level: " + salience);
         return;
      }
      this.salience = salience;
   }



   public Vector getActions(){
      return actions;
   }
   

   public boolean resolve(Bindings b) {
      boolean status = true;
      Pattern p;
      Action a;
      for(int i = 0; status && i < patterns.size(); i++ ) {
         p = (Pattern)patterns.elementAt(i);
         status &= p.resolve(b);
      }
      for(int i = 0; status && i < actions.size(); i++ ) {
         a = (Action)actions.elementAt(i);
         status &= a.resolve(b);
      }
      return status;
   }
   
   
   public String toString() {
      Pattern p;
      Action a;

      String s = "(" + name + " ";
      if ( salience != 0 )
         s += salience + " ";
      for(int i = 0; i < patterns.size(); i++ ) {
         p = (Pattern)patterns.elementAt(i);
         s += Misc.spaces(3) + p.toString();
      }
      s += " " + Misc.spaces(3) + IMPLIES + " ";
      for(int i = 0; i < actions.size(); i++ ) {
         a = (Action)actions.elementAt(i);
         s += Misc.spaces(3) + a.toString();
      }
      s += ")";
      return s;
   }
   
   
   public String pprint() {
      return pprint(0);
   }
   
   
   public String pprint(int sp) {
      Pattern p;
      Action a;

      String s = Misc.spaces(sp) + "(" + name;
      if ( salience != NORM_SALIENCE )
         s += " " + salience;
      for(int i = 0; i < patterns.size(); i++ ) {
         p = (Pattern)patterns.elementAt(i);
         s += "\n" + Misc.spaces(3+sp) + p.toString();
      }
      s += "\n" + Misc.spaces(3+sp) + IMPLIES;
      for(int i = 0; i < actions.size(); i++ ) {
         a = (Action)actions.elementAt(i);
         s += "\n" + Misc.spaces(3+sp) + a.toString();
      }
      s += "\n" + Misc.spaces(sp) + ")";
      return s;
   }
}
