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



package zeus.generator.task.rulebase;

import javax.swing.table.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.event.*;
import zeus.concepts.*;


public class RuleModel  extends AbstractTableModel implements ChangeListener{

      static final int RULE     = 0;
      static final int PRIORITY = 1;
      static final String[]  header = { "Rule", "Priority"};

      Vector rules;
      OntologyDb ontology;
      static int RULE_COUNT = 0;
      protected EventListenerList changeListeners = new EventListenerList();
//---------------------------------------------------------------------------
      public RuleModel(ReteKB kb, OntologyDb db) {
         rules = new Vector();
         ontology = db;
	 zeus.rete.Rule[] reteRules = kb.getRules();
         for(int i = 0; i< reteRules.length; i++)
            addRule(reteRules[i]);
      }

//---------------------------------------------------------------------------
       public int     getRowCount()                    { return rules.size(); }
       public int     getColumnCount()                 { return header.length; }
       public String  getColumnName(int col)           { return header[col]; }
       public boolean isCellEditable(int row, int col) { return true; }

//----------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
          Rule rule = (Rule) rules.elementAt(row);
          switch(col) {
             case RULE:
                  return rule.getName();
             case PRIORITY:
                  return new Integer(rule.getPriority());
          }
          return null;
       }
//----------------------------------------------------------------------------
       public void setValueAt(Object aValue, int row, int column) {
           Rule rule = (Rule) rules.elementAt(row);
          switch(column) {
             case RULE:
                  String name =(String)aValue;
                  if ( name.equals(rule.getName()) ) return;
                  rule.setName(name);
                  break;
             case PRIORITY:
                  int p = Integer.parseInt((String)aValue);
                  if ( p == rule.getPriority() ) return;
                  rule.setPriority(p);
                  break;
          }
          fireChanged();
       }
//----------------------------------------------------------------------------
       public void addRule(zeus.rete.Rule reteRule) {
           String name = reteRule.getName();
           int priority = reteRule.getSalience();
           Rule rule = new Rule(name,priority);
           rule.setPatterns(reteRule.getPatterns());
           rule.setActions(reteRule.getActions());
           rule.addChangeListener(this);
           rules.addElement(rule);
           fireTableDataChanged();
           fireChanged();
       }
//--------------------------------------------------------------------------
       public zeus.rete.Rule[] getData() {
          zeus.rete.Rule reteRule = null;
          Rule rule = null;
          Vector actions = null;
          Vector patterns = null;
          zeus.rete.Rule[]reteRules = new zeus.rete.Rule[rules.size()];

          for(int i = 0; i< rules.size(); i++ ) {
             rule = (Rule) rules.elementAt(i);
             reteRule = new zeus.rete.Rule(rule.getName(),rule.getPriority());
             try {
                patterns = ZeusParser.retePatternList(ontology,rule.getPatterns());
                addPatterns(reteRule,patterns);
                actions = ZeusParser.reteActionList(ontology,rule.getActions());
                addActions(reteRule,actions);
             }
             catch(Exception e) {
                JOptionPane.showMessageDialog(null,"Syntax error parsing rule \'" +
                   rule.getName() + "\'", "Syntax Error", JOptionPane.ERROR_MESSAGE);
             }
             reteRules[i] = reteRule;
          }
         return reteRules;
       }
//--------------------------------------------------------------------------
       private void addActions(zeus.rete.Rule reteRule, Vector actions) {
           zeus.rete.Action action = null;

           for(int i=0; i<actions.size();i++) {
              action = (zeus.rete.Action) actions.elementAt(i);
	      reteRule.addAction(action);
           }
       }
//--------------------------------------------------------------------------
       private void addPatterns(zeus.rete.Rule reteRule, Vector patterns) {
           zeus.rete.Pattern pattern = null;

           for(int i=0; i<patterns.size();i++) {
              pattern = (zeus.rete.Pattern) patterns.elementAt(i);
	      reteRule.addPattern(pattern);
           }
       }
//--------------------------------------------------------------------------
       public void addRule() {
           String name = "Rule" + (RULE_COUNT++);
           while( existRuleName(name) )
              name = "Rule" + (RULE_COUNT++);

           Rule rule = new Rule(name,zeus.rete.Rule.NORM_SALIENCE);
           rule.addChangeListener(this);
           rules.addElement(rule);
           fireTableDataChanged();
           fireChanged();
//           System.out.println("added " + RULE_COUNT);

       }
//----------------------------------------------------------------------------
       public void deleteRule(int row) {
          Rule rule = (Rule) rules.elementAt(row);
          rules.removeElementAt(row);
          rule.removeChangeListener(this);
          fireTableDataChanged();
          fireChanged();
       }
//---------------------------------------------------------------------------
       private boolean existRuleName(String name) {
          Rule rule;

          for(int i = 0; i < rules.size(); i++) {
             rule = (Rule) rules.elementAt(i);
             if (rule.getName().equals(name))
                return true;
          }
         return false;
       }
//---------------------------------------------------------------------------
       public Rule getRule(int row) {
            Rule rule = (Rule) rules.elementAt(row);
//            System.out.println("getRule " + row);
            return rule;
       }
//---------------------------------------------------------------------------
       public Vector getRules() {
         return rules;
       }
//----------------------------------------------------------------------------
    public void addChangeListener(ChangeListener x) {
      changeListeners.add(ChangeListener.class, x);
    }
//----------------------------------------------------------------------------
    public void removeChangeListener(ChangeListener x) {
      changeListeners.remove(ChangeListener.class, x);
    }
//----------------------------------------------------------------------------
     protected void fireChanged() {
      ChangeEvent c = new ChangeEvent(this);
      Object[] listeners = changeListeners.getListenerList();
      for(int i= listeners.length-2; i >= 0; i -=2) {
         if (listeners[i] == ChangeListener.class) {
            ChangeListener cl = (ChangeListener)listeners[i+1];
            cl.stateChanged(c);
         }
      }
     }
//---------------------------------------------------------------------------
     public void stateChanged(ChangeEvent e) {
         fireChanged();
     }
}
