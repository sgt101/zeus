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


public class ReportRec {
   protected String  name = null;
   protected String  goal = null;
   protected String  task = null;
   protected String  agent = null;
   protected int     state;
   protected String  owner = null;
   protected String  root_id = null;
   protected int     start_time = 0;
   protected int     end_time = 0;
   protected double  cost = 0;
   protected String  parent = null;
   protected Vector  children = new Vector();
   protected Vector  siblings = new Vector();
   protected Vector  parents = new Vector();
   protected Vector  consumed = new Vector();
   protected Vector  produced = new Vector();

   ReportRec(String name, String goal) {
      Assert.notNull(name);
      Assert.notNull(goal);
      this.name = name;
      this.goal = goal;
   }
   public ReportRec(String name, String goal, String task, String agent,
                    int state, String owner, String root_id, String parent,
                    int s, int e, double c, Vector children, Vector siblings,
                    Vector parents, Fact[] consumed, Fact[] produced) {
      this(name,goal);
      setTask(task);
      setAgent(agent);
      setState(state);
      setOwner(owner);
      setRootId(root_id);
      setStartTime(s);
      setEndTime(e);
      setCost(c);
      setParent(parent);
      setChildren(children);
      setSiblings(siblings);
      setParents(parents);
      setPreconditions(consumed);
      setPostconditions(produced);
   }
   public ReportRec(ReportRec rec) {
      this(rec.getName(),rec.getGoal());
      setTask(rec.getTask());
      setAgent(rec.getAgent());
      setState(rec.getState());
      setOwner(rec.getOwner());
      setStartTime(rec.getStartTime());
      setEndTime(rec.getEndTime());
      setCost(rec.getCost());
      setRootId(rec.getRootId());
      setParent(rec.getParent());
      setChildren(rec.getChildren());
      setSiblings(rec.getSiblings());
      setParents(rec.getParents());
      setPreconditions(rec.getPreconditions());
      setPostconditions(rec.getPostconditions());
   }

   public boolean hasNoParents() {
      return parents.isEmpty();
   }
   public boolean hasOneParentOnly(String name) {
      Assert.notNull(name);
      return (parents.contains(name) && parents.size() == 1);
   }
   public void removeParent(String name) {
      Assert.notNull(name);
      parents.removeElement(name);
   }
   public void setTask(String task) {
      Assert.notNull(task);
      this.task = task;
   }
   public void setAgent(String agent) {
      Assert.notNull(agent);
      this.agent = agent;
   }
   public void setState(int state) {
      this.state = state;
   }
   public void setOwner(String owner) {
      Assert.notNull(owner);
      this.owner = owner;
   }
   public void setParent(String parent) {
      String prev = this.parent;
      this.parent = parent;
      if ( prev != null )
         parents.removeElement(prev);
      if ( parent != null )
         parents.addElement(parent);
   }
   public void setRootId(String root) {
      Assert.notNull(root);
      root_id = root;
   }
   public void setSiblings(Vector List) {
      siblings.removeAllElements();
      for(int i = 0; List != null && i < List.size(); i++ )
         siblings.addElement(List.elementAt(i));
   }
   public void setChildren(Vector List) {
      children.removeAllElements();
      for(int i = 0; List != null && i < List.size(); i++ )
         children.addElement(List.elementAt(i));
   }
   public void setParents(Vector List) {
      parents.removeAllElements();
      for(int i = 0; List != null && i < List.size(); i++ )
         parents.addElement(List.elementAt(i));
      if ( parent != null && !parents.contains(parent) )
         parents.addElement(parent);
   }
   public void setSiblings(String[] List) {
      siblings.removeAllElements();
      for(int i = 0; List != null && i < List.length; i++ )
         siblings.addElement(List[i]);
   }
   public void setChildren(String[] List) {
      children.removeAllElements();
      for(int i = 0; List != null && i < List.length; i++ )
         children.addElement(List[i]);
   }
   public void setParents(String[] List) {
      parents.removeAllElements();
      for(int i = 0; List != null && i < List.length; i++ )
         parents.addElement(List[i]);
      if ( parent != null && !parents.contains(parent) )
         parents.addElement(parent);
   }
   public void setPostconditions(Vector List) {
      produced.removeAllElements();
      for(int i = 0; List != null && i < List.size(); i++ )
         produced.addElement((Fact) List.elementAt(i));
   }
   public void setPostconditions(Fact[] List) {
      produced.removeAllElements();
      for(int i = 0; List != null && i < List.length; i++ )
         produced.addElement(List[i]);
   }
   public void setPreconditions(Vector List) {
      consumed.removeAllElements();
      for(int i = 0; List != null && i < List.size(); i++ )
         consumed.addElement((Fact) List.elementAt(i));
   }
   public void setPreconditions(Fact[] List) {
      consumed.removeAllElements();
      for(int i = 0; List != null && i < List.length; i++ )
         consumed.addElement(List[i]);
   }
   public void addChild(String child) {
      if ( !children.contains(child) )
         children.addElement(child);
   }
   public boolean hasChild(String child) {
      return children.contains(child);
   }
   public void addSibling(String sibling) {
      if ( !siblings.contains(sibling) )
         siblings.addElement(sibling);
   }
   public boolean hasSibling(String sibling) {
      return siblings.contains(sibling);
   }
   public void addParent(String name) {
      if ( !parents.contains(name) )
         parents.addElement(name);
   }
   public boolean hasParent(String name) {
      return parents.contains(name);
   }
   public void setEndTime(int time) {
      Assert.notFalse(time >= 0);
      end_time = time;
   }
   public void setStartTime(int time) {
      Assert.notFalse(time >= 0);
      start_time = time;
   }
   public void setCost(double cost) {
      Assert.notFalse(cost >= 0);
      this.cost = cost;
   }

   public String   getName()       { return name; }
   public String   getGoal()       { return goal; }
   public String   getTask()       { return task; }
   public String   getAgent()      { return agent; }
   public int      getState()      { return state; }
   public String   getOwner()      { return owner; }
   public String   getParent()     { return parent; }
   public String   getRootId()     { return root_id; }
   public boolean  isRoot()        { return name.equals(root_id); }
   public int      getStartTime()  { return start_time; }
   public int      getEndTime()    { return end_time; }
   public double   getCost()       { return cost; }
   
   public String[] getChildren() {
      String[] out = new String[children.size()];
      for(int i = 0; i < children.size(); i++ )
         out[i] = (String)children.elementAt(i);
      return out;

   }
   public String[] getSiblings() {
      String[] out = new String[siblings.size()];
      for(int i = 0; i < siblings.size(); i++ )
         out[i] = (String)siblings.elementAt(i);
      return out;

   }
   public String[] getParents()   {
      String[] out = new String[parents.size()];
      for(int i = 0; i < parents.size(); i++ )
         out[i] = (String)parents.elementAt(i);
      return out;

   }
   public Fact[]  getPreconditions() {
      Fact[] out = new Fact[consumed.size()];
      for(int i = 0; i < consumed.size(); i++ )
         out[i] = (Fact)consumed.elementAt(i);
      return out;
   }
   public Fact[] getPostconditions() {
      Fact[] out = new Fact[produced.size()];
      for(int i = 0; i < produced.size(); i++ )
         out[i] = (Fact)produced.elementAt(i);
      return out;
   }

   public String toString() {
      String s = new String("(");

      s += ":name " + name + " ";
      s += ":goal " + goal + " ";
      s += ":task " + task + " ";
      s += ":agent " + agent + " ";
      s += ":state " + state + " ";
      s += ":owner " + owner + " ";
      s += ":root_id " + root_id + " ";

      if ( parent != null )
         s += ":parent " + parent + " ";

      s += ":start_time " + start_time + " ";
      s += ":end_time " + end_time + " ";
      s += ":cost " + cost + " ";

      if ( !children.isEmpty() ) {
         s += ":children (";
         for(int i = 0; i < children.size(); i++ )
            s += (String)children.elementAt(i) + " ";
         s = s.trim() + ") ";
      }
      if ( !siblings.isEmpty() ) {
         s += ":siblings (";
         for(int i = 0; i < siblings.size(); i++ )
            s += (String)siblings.elementAt(i) + " ";
         s = s.trim() + ") ";
      }
      if ( !parents.isEmpty() ) {
         s += ":parents (";
         for(int i = 0; i < parents.size(); i++ )
            s += (String)parents.elementAt(i) + " ";
         s = s.trim() + ") ";
      }
      if ( !consumed.isEmpty() ) {
         s += ":consumed_facts (";
         for(int i = 0; i < consumed.size(); i++ )
            s += ((Fact) consumed.elementAt(i)).toString();
         s += ") ";
      }
      if ( !produced.isEmpty() ) {
         s += ":produced_facts (";
         for(int i = 0; i < produced.size(); i++ )
            s += ((Fact) produced.elementAt(i)).toString();
         s += ") ";
      }
      return s.trim() + ")";
   }

   public String pprint() {
      return pprint(0);
   }
   public String pprint(int sp) {
      String suffix, prefix;
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = new String("(");

      s += ":name " + name + eol;
      s += ":goal " + goal + eol;
      s += ":task " + task + eol;
      s += ":agent " + agent + eol;
      s += ":state " + state + eol;
      s += ":owner " + owner + eol;
      s += ":root_id " + root_id + eol;

      if ( parent != null )
         s += ":parent " + parent + eol;

      s += ":start_time " + start_time + eol;
      s += ":end_time " + end_time + eol;
      s += ":cost " + cost + eol;

      if ( !children.isEmpty() ) {
         prefix = ":children ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < children.size(); i++ )
            s += (String)children.elementAt(i) + "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }

      if ( !siblings.isEmpty() ) {
         prefix = ":siblings ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < siblings.size(); i++ )
            s += (String)siblings.elementAt(i) + "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }

      if ( !parents.isEmpty() ) {
         prefix = ":parents ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < parents.size(); i++ )
            s += (String)parents.elementAt(i) + "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      if ( !consumed.isEmpty() ) {
         prefix = ":consumed_facts ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < consumed.size(); i++ )
            s += ((Fact)consumed.elementAt(i)).pprint(1+suffix.length()) +
                 "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      if ( !produced.isEmpty() ) {
         prefix = ":produced_facts ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < produced.size(); i++ )
            s += ((Fact)produced.elementAt(i)).pprint(1+suffix.length()) +
                 "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      return s.trim() + "\n" + tabs + ")";
   }
}
