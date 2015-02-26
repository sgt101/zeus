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
import zeus.actors.AgentContext;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.actors.event.*;
import zeus.actors.*;

public class ReteEngine {
   static final int RULE_ACTIVATED   = 0;
   static final int RULE_DEACTIVATED = 1;
   static final int RULE_ADDED       = 2;
   static final int RULE_REMOVED     = 3;
   static final int RULE_FIRED       = 4;

   protected Hashtable factDb = new Hashtable();
   protected static final String PATH = "path#";
   protected static final String JPATH = "path@";

   protected Hashtable roots = new Hashtable();
   protected Hashtable ids = new Hashtable();
   protected Vector singles = new Vector();
   protected Vector joins = new Vector();
   protected Vector tests = new Vector();

   protected int pathNo = 0;
   protected int joinPathNo = 0;
   protected ConflictSet conflictSet;
   protected Fact initial_fact = null;
   protected AgentContext context = null;
   protected OntologyDb ontologyDb = null;
   private   HSet[] eventMonitor = new HSet[5];
   private   HSet   localMonitor = new HSet();


   public ReteEngine () {
   ;
   }

   public ReteEngine(AgentContext context) {
      Assert.notNull(context);
      this.context = context;
      context.set(this);

      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();

      conflictSet = new ConflictSet(context);
      initial_fact = context.OntologyDb().getFact(Fact.FACT,Rule.INITIAL_FACT);
      ResourceDb database = context.ResourceDb();
      database.addFactMonitor(new SymFactAction(),
                              FactEvent.ADD_MASK|FactEvent.DELETE_MASK,true);
   }


   public ReteEngine(OntologyDb ontologyDb) {
      Assert.notNull(ontologyDb);
      this.ontologyDb = ontologyDb;

      for(int i = 0; i < eventMonitor.length; i++ )
         eventMonitor[i] = new HSet();

      conflictSet = new ConflictSet(this,ontologyDb);
      initial_fact = ontologyDb.getFact(Fact.FACT,Rule.INITIAL_FACT);
   }
   

   public AgentContext getAgentContext() {
      return context;
   }

   public void run() {
      update(Node.ADD,initial_fact);
      conflictSet.start();
   }
   

   public void reset() {
      conflictSet.reset();
      // reset all join-nodes
      JoinNode node;
      for(int i = 0; i < joins.size(); i++ ) {
         node = (JoinNode)joins.elementAt(i);
         node.reset();
      }

      // reassert initial facts
      Enumeration enum = factDb.elements();
      while( enum.hasMoreElements() )
         update(Node.ADD,(Fact)enum.nextElement());
   }
   

   ConflictSet getConfictSet() {
      return conflictSet;
   }


   Hashtable getFactDb() {
      return factDb;
   }


   public void update(int tag, ReteFact token) {
     // System.out.println("called update with retefact "+ token.type); 
      Fact f = ontology().getFact(Fact.FACT,token.type);
      String attribute;
      ValueFunction value;
      Enumeration enum = token.data.keys();
      while( enum.hasMoreElements() ) {
         attribute = (String)enum.nextElement();
         value = (ValueFunction)token.data.get(attribute);
         f.setValue(attribute,value);
      }
      update(tag,f);
   }


    /** 
      *  Update is the method which is called to decide if a rule should be fired or not 
      *  when a fact is added to the ResourceDb. 
      * Change Log
      * ----------
      * 26/06/01 - 1.3 - Simon Thompson added some code to check not only for rootnodes of 
      * the same type as the added fact, but also the ancestors of the fact to. 
      * However, note that only one rule will be fired! 
      */
   public  void update(int tag, Fact f) {
    // System.out.println("called update in reteengine " + f.getId() + f.toString()); 
      if ( context == null ) { // i.e. isLocal
         if ( tag == Node.ADD )
            factDb.put(f.getId(),f);
         else if ( tag == Node.REMOVE )
            factDb.remove(f.getId());
      }
      fireLocalFactMonitor(tag, f);
      Vector input = new Vector();
      input.addElement(f);
      /** 
        change */
        boolean fired = false; 
    
      TypeNode node = (TypeNode)roots.get(f.getType()); // originall
      if ( node != null ) {
        fired = true; 
             synchronized(conflictSet.queue) {
            node.evaluate(null, tag, Node.SINGLE, input, new Bindings());
             }
        
      }// original
      Iterator supers = ontology().allAncestors(f.getType()); //added
      while (supers.hasNext()&& !fired) {
      node = (TypeNode)roots.get((String)supers.next());
      if ( node != null ) {
           fired = true;
           synchronized(conflictSet.queue) {
                node.evaluate(null, tag, Node.SINGLE, input, new Bindings());
        }//added
      
      }// end change
      }
   }


   private OntologyDb ontology() {
      return ((ontologyDb != null) ? ontologyDb : context.OntologyDb());
   }


   protected class SymFactAction extends FactAdapter {
      public void factAddedEvent(FactEvent event) {
         Fact f = event.getFact();
         update(Node.ADD,f);
      }
      public void factDeletedEvent(FactEvent event) {
         Fact f = event.getFact();
         update(Node.REMOVE,f);
      }
   }

   public void add(ReteKB kb) {
      Rule[] rule = kb.getRules();
      for(int i = 0; i < rule.length; i++ ) {
         rule[i].setName(kb.getName() + "$" + rule[i].getName());
         add(rule[i]);
      }
   }

   public void add(Rule r) {
      Node node = null;
      Node[] term;
      ReteFact token;
      boolean found;
      String path;
      Pattern p;

      r = r.duplicate(Fact.VAR,ontology().GenSym());

      Core.DEBUG(2,"Compiling rule " + r.name);

      term = new Node[r.nTerminals()];
      int position = 0;
      for(int i = 0; i < r.patterns.size(); i++, position++ ) {
         p = (Pattern)r.patterns.elementAt(i);
         switch(p.tag) {
            case Pattern.TEST:
            case Pattern.CMD:
                 position--;
                 break;

            default:
                 path = PATH + (pathNo + position);
                 token = (ReteFact)p.data;

                 // create TypeNode if roots does not already contain an
                 // appropriate type node;
                 if ( roots.containsKey(token.type) ) {
                    term[position] = (Node)roots.get(token.type);
                    Core.DEBUG(4,"=t");
                    Core.DEBUG(5,"=t[" + term[position] + "]");
                 }
                 else {
                    term[position] = new TypeNode(this,token.type);
                    roots.put(token.type,term[position]);
                    Core.DEBUG(4,"+t");
                    Core.DEBUG(5,"+t[" + term[position] + "]");
                 }
                 term[position].use_count++;
                 node = term[position];

                 // if fact id is needed then create id node
                 // check if ids already contains node
                 if ( p.id != null ) {
                    if ( ids.containsKey(p.id) ) {
                       term[position] = (Node)ids.get(p.id);
                       Core.DEBUG(4,"=i");
                       Core.DEBUG(5,"=i[" + term[position] + "]");
                    }
                    else {
                       term[position] = new IdNode(this,p.id);
                       ids.put(p.id,term[position]);
                       Core.DEBUG(4,"+i");
                       Core.DEBUG(5,"+i[" + term[position] + "]");
                    }
                    term[position].use_count++;
                    node.addSuccessor(path,term[position],Node.SINGLE);
                    node = term[position];
                 }

                 // For each attribute value create attribute node
                 // check if 'singles' already contains node
                 ValueFunction[] value = token.listValues();
                 String[] attribute = token.listAttributes();

                 // For processing efficiency, do determinates first
                 Vector temp1 = new Vector();
                 String a;
                 ValueFunction v;

                 for(int k = 0; k < value.length; k++ ) {
                    if ( value[k].isDeterminate() ) {
                       term[position] =
                          new AttributeNode(this,attribute[k],value[k]);
                       found = false;
                       for(int j = 0; !found && j < singles.size(); j++ ) {
                          found = term[position].equals(singles.elementAt(j));
                          if (found)
                             term[position] = (Node)singles.elementAt(j);
                       }
                       if ( !found ) {
                          singles.addElement(term[position]);
                          Core.DEBUG(4,"+d");
                          Core.DEBUG(5,"+d[" + term[position] + "]");
                       }
                       else {
                          Core.DEBUG(4,"=d");
                          Core.DEBUG(5,"=d[" + term[position] + "]");
                       }
                       term[position].use_count++;
                       node.addSuccessor(path,term[position],Node.SINGLE);
                       node = term[position];
                    }
                    else {
                       temp1.addElement(attribute[k]);
                       temp1.addElement(value[k]);
                    }
                 }

                 // Repeat for indeterminates
                 for(int k = 0; k < temp1.size(); k += 2 ) {
                    a = (String)temp1.elementAt(k);
                    v = (ValueFunction)temp1.elementAt(k+1);
                    term[position] = new AttributeNode(this,a,v);
                    found = false;
                    for(int j = 0; !found && j < singles.size(); j++ ) {
                       found = term[position].equals(singles.elementAt(j));
                       if (found)
                          term[position] = (Node)singles.elementAt(j);
                    }
                    if ( !found ) {
                       singles.addElement(term[position]);
                       Core.DEBUG(4,"+n");
                       Core.DEBUG(5,"+n[" + term[position] + "]");
                    }
                    else {
                       Core.DEBUG(4,"=n");
                       Core.DEBUG(5,"=n[" + term[position] + "]");
                    }
                    term[position].use_count++;
                    node.addSuccessor(path,term[position],Node.SINGLE);
                    node = term[position];
                 }
                 break;
         }
      }

      // Now, the pattern nodes for each rule have been created
      // we create the join nodes

//System.out.println("Creating jn nodes for\n" + r);

      position = 0;
      JoinNode[] jn = new JoinNode[term.length-1];

      for(int i = 1; i < r.patterns.size(); i++, position++ ) {
         p = (Pattern)r.patterns.elementAt(i);

         switch( p.tag ) {
            case Pattern.TEST:
            case Pattern.CMD:
                 position--;
                 break;

            default:
                 token = (ReteFact)p.data;

                 jn[position] = (p.tag == Pattern.NONE)
                              ? new JoinNode(this) : new NotNode(this);

                 ValueFunction[] r_value = token.listValues();
                 String[] r_attribute = token.listAttributes();

                 ValueFunction[] vars = token.variables();

                 int q = -1;
                 for(int j = 0; j < i; j++) {
                    p = (Pattern)r.patterns.elementAt(j);
                    if ( p.tag == Pattern.NONE ) {
                       q++;
                       token = (ReteFact)p.data;

                       ValueFunction[] l_value = token.listValues();
                       String[] l_attribute = token.listAttributes();

                       boolean[][] local =
                          new boolean[l_value.length][r_value.length];

                       for(int m = 0; m < local.length; m++ )
                       for(int n = 0; n < local[m].length; n++ )
                          local[m][n] = false;

                       for(int k = 0; k < vars.length; k++ ) {
                          for(int m = 0; m < l_value.length; m++ ) {
                             if ( l_value[m].references(vars[k]) ) {
                                for(int n = 0; n < r_value.length; n++ ) {
                                   if ( !local[m][n] &&
                                        r_value[n].references(vars[k]) ) {
                                      local[m][n] = true;
                                      jn[position].add(q,l_attribute[m],l_value[m],
                                                       0,r_attribute[n],r_value[n]);
/*
NOT SURE why I used "jn[i-1]" before
                                      jn[i-1].add(q,l_attribute[m],l_value[m],
                                                  0,r_attribute[n],r_value[n]);
*/
                                   }
                                }
                             }
                          }
                       }
                    }
                 }
                 break;
         }
      }
      // all join nodes have been created,
      // now check for repetition (i.e. node equality) and create graph
      // in the process, create test & cmd nodes

      String rightPath;

      Node leftNode = term[0];
      String leftPath = PATH+pathNo;
      position = 0;
      for(int i = 1; i < r.patterns.size(); i++, position++ ) {
         p = (Pattern)r.patterns.elementAt(i);

         switch( p.tag ) {
            case Pattern.CMD:
                 position--;
                 break;

            case Pattern.TEST:
                 position--;
                 node = leftNode; // store previous
                 leftNode = new TestNode(this,(ValueFunction)p.data);
                 found = false;
                 for(int j = 0; !found && j < tests.size(); j++ ) {
                    found = leftNode.equals(tests.elementAt(j));
                    if (found) leftNode = (Node)tests.elementAt(j);
                 }
                 if ( !found ) {
                    tests.addElement(leftNode);
                    Core.DEBUG(4,"+c");
                    Core.DEBUG(5,"+c[" + leftNode + "]");
                 }
                 else {
                    Core.DEBUG(4,"=c");
                    Core.DEBUG(5,"=c[" + leftNode + "]");
                 }
                 leftNode.use_count++;
                 node.addSuccessor(leftPath,leftNode,Node.SINGLE);
                 break;

            default:
                 // Repetition check
                 found = false;
                 for(int j = 0; !found && j < joins.size(); j++ ) {
                    found = jn[position].equals(joins.elementAt(j));
                    if ( found ) jn[position] = (JoinNode)joins.elementAt(j);
                 }
                 if ( !found ) {
                    joins.addElement(jn[position]);
                    Core.DEBUG(4,"+j");
                    Core.DEBUG(5,"+j[" + jn[position] + "]");
                 }
                 else {
                    Core.DEBUG(4,"=j");
                    Core.DEBUG(5,"=j[" + jn[position] + "]");
                 }
                 jn[position].use_count++;
                 path = JPATH + (joinPathNo++);
                 rightPath = PATH+(pathNo+position+1);

                 jn[position].addPath(leftPath,rightPath,path);
                 leftNode.addSuccessor(leftPath,jn[position],Node.LEFT);
                 term[position+1].addSuccessor(rightPath,jn[position],Node.RIGHT);
                 leftNode = jn[position];
                 leftPath = path;
                 break;
         }
      }

      // now, all that is left is the action node
      // first determine the last node: which should be 'leftNode'

      ActionNode action = new ActionNode(this,r.name,r.salience,r.actions);
      action.use_count++;
      leftNode.addSuccessor(leftPath,action,Node.ACTION);
      Core.DEBUG(4,"+a");
      Core.DEBUG(5,"+a[" + action + "]");
      pathNo += term.length;

      String diagnostic = "ADD [" + action.rule_name + "]";
      fireEvent(RULE_ADDED,action,diagnostic);
   }


   public void remove(Rule r) {
      Core.USER_ERROR("Function ReteEngine.remove(Rule r) not yet implemented");
   }


   public void addLocalFactMonitor(LocalFactMonitor monitor) {
      localMonitor.add(monitor);
   }
   
   
   public void removeLocalFactMonitor(LocalFactMonitor monitor) {
      localMonitor.remove(monitor);
   }


   void fireLocalFactMonitor(int tag, Fact f) {
      LocalFactMonitor monitor;
      Enumeration enum = localMonitor.elements();
      while( enum.hasMoreElements() ) {
         monitor = (LocalFactMonitor)enum.nextElement();
         if ( tag == Node.ADD ) 
            monitor.reteFactAdded(f);
         else
            monitor.reteFactDeleted(f);
      }
   }


   public void addMonitor(ReteEngineMonitor monitor, long event_type) {
      if ( (event_type & ReteEngineEvent.ADD_MASK) != 0 )
         eventMonitor[RULE_ADDED].add(monitor);
      if ( (event_type & ReteEngineEvent.DELETE_MASK) != 0 )
         eventMonitor[RULE_REMOVED].add(monitor);
      if ( (event_type & ReteEngineEvent.ACTIVATE_MASK) != 0 )
         eventMonitor[RULE_ACTIVATED].add(monitor);
      if ( (event_type & ReteEngineEvent.DEACTIVATE_MASK) != 0 )
         eventMonitor[RULE_DEACTIVATED].add(monitor);
      if ( (event_type & ReteEngineEvent.FIRE_MASK) != 0 )
         eventMonitor[RULE_FIRED].add(monitor);
   }
   public void removeMonitor(ReteEngineMonitor monitor, long event_type) {
      if ( (event_type & ReteEngineEvent.ADD_MASK) != 0 )
         eventMonitor[RULE_ADDED].remove(monitor);
      if ( (event_type & ReteEngineEvent.DELETE_MASK) != 0 )
         eventMonitor[RULE_REMOVED].remove(monitor);
      if ( (event_type & ReteEngineEvent.ACTIVATE_MASK) != 0 )
         eventMonitor[RULE_ACTIVATED].remove(monitor);
      if ( (event_type & ReteEngineEvent.DEACTIVATE_MASK) != 0 )
         eventMonitor[RULE_DEACTIVATED].remove(monitor);
      if ( (event_type & ReteEngineEvent.FIRE_MASK) != 0 )
         eventMonitor[RULE_FIRED].remove(monitor);
   }


   void fireEvent(int type, ActionNode node, String diagnostic) {
      if ( eventMonitor[type].isEmpty() ) return;

      ReteEngineMonitor monitor;
      ReteEngineEvent event;
      Enumeration enum = eventMonitor[type].elements();
      switch(type) {
         case RULE_ADDED:
              event = new ReteEngineEvent(this,node.rule_name,node.salience,diagnostic,ReteEngineEvent.ADD_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ReteEngineMonitor)enum.nextElement();
                 monitor.reteRuleAddedEvent(event);
              }
              break;
         case RULE_REMOVED:
              event = new ReteEngineEvent(this,node.rule_name,node.salience,diagnostic,ReteEngineEvent.DELETE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ReteEngineMonitor)enum.nextElement();
                 monitor.reteRuleDeletedEvent(event);
              }
              break;
         case RULE_ACTIVATED:
              event = new ReteEngineEvent(this,node.rule_name,node.salience,diagnostic,ReteEngineEvent.ACTIVATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ReteEngineMonitor)enum.nextElement();
                 monitor.reteRuleActivatedEvent(event);
              }
              break;
         case RULE_DEACTIVATED:
              event = new ReteEngineEvent(this,node.rule_name,node.salience,diagnostic,ReteEngineEvent.DEACTIVATE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ReteEngineMonitor)enum.nextElement();
                 monitor.reteRuleDeactivatedEvent(event);
              }
              break;
         case RULE_FIRED:
              event = new ReteEngineEvent(this,node.rule_name,node.salience,diagnostic,ReteEngineEvent.FIRE_MASK);
              while( enum.hasMoreElements() ) {
                 monitor = (ReteEngineMonitor)enum.nextElement();
                 monitor.reteRuleFiredEvent(event);
              }
              break;
      }
   }

}
