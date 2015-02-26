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


public class SummaryTask extends Task {
   protected Vector  nodes = new Vector();
   protected Vector  links = new Vector();
   protected boolean autorun = false;

   public SummaryTask() {
      type = SUMMARY;

      TaskNode node;
      node = new TaskNode(TaskNode.BEGIN);
      nodes.addElement(node);

      node = new TaskNode(TaskNode.END);
      nodes.addElement(node);
   }

   public SummaryTask(String name, ValueFunction time, ValueFunction cost,
                      TaskNode[] nodes, TaskLink[] links,
                      LogicalFn[] constraints) {
      type = SUMMARY;
      setName(name);
      setTimeFn(time);
      setCostFn(cost);
      setNodes(nodes);
      setLinks(links);
      setConstraints(constraints);
   }
   public SummaryTask(String name, String time, String cost,
                      TaskNode[] nodes, TaskLink[] links,
                      LogicalFn[] constraints) {
      type = SUMMARY;
      setName(name);
      setTimeFn(time);
      setCostFn(cost);
      setNodes(nodes);
      setLinks(links);
      setConstraints(constraints);
   }
   public SummaryTask(String name, ValueFunction time, ValueFunction cost,
                      Vector nodes, Vector links, Vector constraints) {
      type = SUMMARY;
      setName(name);
      setTimeFn(time);
      setCostFn(cost);
      setNodes(nodes);
      setLinks(links);
      setConstraints(constraints);
   }
   public SummaryTask(String name, String time, String cost,
                      Vector nodes, Vector links, Vector constraints) {
      type = SUMMARY;
      setName(name);
      setTimeFn(time);
      setCostFn(cost);
      setNodes(nodes);
      setLinks(links);
      setConstraints(constraints);
   }

   public SummaryTask(SummaryTask task) {
      type = SUMMARY;
      name = task.getName();
      cost = task.getCostFn();
      time = task.getTimeFn();
      setNodes( task.getNodes() );
      setLinks( task.getLinks() );
      setConstraints( task.getConstraints() );
   }

   public Vector  links()       { return links; }
   public Vector  constraints() { return constraints; }

   // Note: for getPreconditions() and getPostconditions() only what
   // the user considered to be significant pre- and postconditions are
   // returned - by extracting then from the 'begin' and 'end' nodes

   public Fact[] getPostconditions() {
      TaskNode node = getNode(TaskNode.END);
      return node.getPreconditions();
   }
   public Fact[] getPreconditions() {
      TaskNode node = getNode(TaskNode.BEGIN);
      return node.getPostconditions();
   }

   public TaskNode getNode(String name) {
      TaskNode node = null;
      for(int j = 0; j < nodes.size(); j++ ) {
         node = (TaskNode)nodes.elementAt(j);
         if ( node.getName().equals(name) )
            return node;
      }
      return null;
   }

   public Fact[] allFacts() {
      TaskNode node;
      Fact[] out;
      Vector data = new Vector();
      for(int i = 0; i < nodes.size(); i++ ) {
         node = (TaskNode)nodes.elementAt(i);
         if ( !node.getName().equals(TaskNode.BEGIN) &&
              !node.getName().equals(TaskNode.END) ) {
            out = node.getPreconditions();
            for(int j = 0; j < out.length; j++ )
               data.addElement(out[j]);
            if ( !node.isConditionalNode() ) {
	       out = node.getPostconditions();
               for(int j = 0; j < out.length; j++ )
                  data.addElement(out[j]);
            }
         }
      }
      out = new Fact[data.size()];
      for(int i = 0; i < data.size(); i++ )
         out[i] = new Fact((Fact)data.elementAt(i));
      data = null; // GC
      return out;
   }

   public boolean applyConstraints(Bindings bindings) {
      Bindings b = new Bindings(bindings);
      if ( !super.applyConstraints(b) ) return false;

      TaskLink link;
      TaskNode node1, node2;
      String lhs, rhs;
      Fact f1, f2;
      for(int i = 0; i < links.size(); i++ ) {
         link = (TaskLink)links.elementAt(i);
         lhs = link.getLeftNode();
         rhs = link.getRightNode();
         if ( !lhs.equals(TaskNode.BEGIN) && !rhs.equals(TaskNode.END) ) {
            node1 = getNode(lhs);
            node2 = getNode(rhs);
            f1 = node1.getPostcondition(link.getLeftGroup(),link.getLeftArg());
            f2 = node2.getPrecondition(link.getRightGroup(),link.getRightArg());
            if ( !f1.unifiesWith(f2,b) ) return false;
         }
      }

      if ( resolve(b) ) {
         bindings.set(b);
         return true;
      }
      else
         return false;
   }

   public ResolutionContext getContext() {
      if ( resolution_context != null ) return resolution_context;

      resolution_context = new ResolutionContext();
      TaskNode node;
      for(int i = 0; i < nodes.size(); i++ ) {
         node = (TaskNode)nodes.elementAt(i);
         resolution_context.add(node.produced());
         resolution_context.add(node.consumed());
      }
      return resolution_context;
   }

   public boolean resolve(Bindings bindings) {
      ResolutionContext context = getContext();

      time = time.resolve(context,bindings);
      if ( time == null ) return false;

      cost = cost.resolve(context,bindings);
      if ( cost == null ) return false;

      TaskNode node;
      for(int i = 0; i < nodes.size(); i++ ) {
         node = (TaskNode)nodes.elementAt(i);
         if ( !node.resolve(context,bindings) )
            return false;
      }
      return true;
   }

   public TaskNode[] getNodes() {
      TaskNode[] out = new TaskNode[nodes.size()];
      for(int i = 0; i < nodes.size(); i++ )
         out[i] = new TaskNode((TaskNode)nodes.elementAt(i));
      return out;
   }

   public void setNodes(Vector List) {
      nodes.removeAllElements();
      for(int i = 0; i < List.size(); i++ )
         nodes.addElement(new TaskNode((TaskNode)List.elementAt(i)));
   }

   public void setNodes(TaskNode[] List) {
      nodes.removeAllElements();
      for(int i = 0; i < List.length; i++ )
         nodes.addElement(new TaskNode(List[i]));
   }

   public TaskLink[] getLinks() {
      TaskLink[] out = new TaskLink[links.size()];
      for(int i = 0; i < links.size(); i++ )
         out[i] = new TaskLink((TaskLink)links.elementAt(i));
      return out;
   }

   public void setLinks(Vector List) {
      links.removeAllElements();
      for(int i = 0; i < List.size(); i++ )
         links.addElement(new TaskLink((TaskLink)List.elementAt(i)));

   }

   public void setLinks(TaskLink[] List) {
      links.removeAllElements();
      for(int i = 0; i < List.length; i++ )
         links.addElement(new TaskLink(List[i]));
   }

   public boolean isValid() {
      return true;
   }

   public String toString() {
      String s = "(:" + TaskTypes[type] + " " + name + " ";

      s += ":is_autorun " + autorun + " ";
      s += ":time (" + time + ") ";
      s += ":cost (" + cost + ") ";

      if ( !nodes.isEmpty() ) {
         s += ":nodes (";
         for(int i = 0; i < nodes.size(); i++ )
            s += nodes.elementAt(i);
         s += ") ";
      }
      if ( !links.isEmpty() ) {
         s += ":links (";
         for(int i = 0; i < links.size(); i++ )
            s += links.elementAt(i);
         s += ") ";
      }
      if ( !constraints.isEmpty() ) {
         s += ":constraints (";
         for(int i = 0; i < constraints.size(); i++ )
            s += "(" + constraints.elementAt(i) + ")";
         s += ") ";
      }
      return s.trim() + ")";
   }


   public String pprint(int sp) {
      String suffix, prefix;
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = "(:" + TaskTypes[type] + " " + name + eol;

      s += ":is_autorun " + autorun + eol;
      s += ":time (" + time + ")" + eol;
      s += ":cost (" + cost + ")" + eol;

      if ( !nodes.isEmpty() ) {
         prefix = ":nodes ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < nodes.size(); i++ )
            s += ((TaskNode)nodes.elementAt(i)).pprint(1+suffix.length()) +
                 "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      if ( !links.isEmpty() ) {
         prefix = ":links ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
         for(int i = 0; i < links.size(); i++ )
            s += ((TaskLink)links.elementAt(i)).pprint(1+suffix.length()) +
                 "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      if ( !constraints.isEmpty() ) {
         prefix = ":constraints ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(" + "\n" + suffix + " ";
         for(int i = 0; i < constraints.size(); i++ )
            s += "(" + ((LogicalFn)constraints.elementAt(i)) + ")" +
                 "\n" + suffix + " ";
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      return tabs + s.trim() + "\n" + tabs + ")";
   }

   public AbstractTask duplicate(DuplicationTable table) {
      TaskNode[]  Xnodes = new TaskNode[nodes.size()];
      TaskLink[]  Xlinks = new TaskLink[links.size()];
      LogicalFn[] Xconstraints = new LogicalFn[constraints.size()];

      ValueFunction Xtime = time.duplicate(table);
      ValueFunction Xcost = cost.duplicate(table);

      for(int i = 0; i < nodes.size(); i++ )
         Xnodes[i] = ((TaskNode)nodes.elementAt(i)).duplicate(table);

      for(int i = 0; i < links.size(); i++ )
         Xlinks[i] = ((TaskLink)links.elementAt(i)).duplicate(table);

      for(int i = 0; i < constraints.size(); i++ )
         Xconstraints[i] = (LogicalFn)((LogicalFn)constraints.elementAt(i)).duplicate(table);

      return new SummaryTask(name,Xtime,Xcost,Xnodes,Xlinks,Xconstraints);
   }
}
