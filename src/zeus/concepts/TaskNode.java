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


public class TaskNode {
   public static final String  BEGIN = "begin";
   public static final String  END = "end";
   public static final String  DEFAULT_GROUP = "default";

   protected boolean   isConditionalNode = false;
   protected String    name;
   protected Hashtable consumed = new Hashtable();
   protected Hashtable produced = new Hashtable();

   public TaskNode(String name) {
      setName(name);
      consumed.put(DEFAULT_GROUP, new Vector());
      produced.put(DEFAULT_GROUP, new Vector());
   }

   public TaskNode(String name, Fact[] consumed, Fact[] produced) {
      setName(name);
      setPostconditions(produced);
      setPreconditions(consumed);
   }
   public TaskNode(String name, Vector consumed, Vector produced) {
      setName(name);
      setPostconditions(produced);
      setPreconditions(consumed);
   }

   public TaskNode(TaskNode node) {
      name = node.getName();
      setPostconditions(node.getPostconditions());
      setPreconditions(node.getPreconditions());
   }

   public final boolean isConditionalNode() { return isConditionalNode; }
   public final boolean isBeginNode()       { return name.equals(BEGIN); }
   public final boolean isEndNode()         { return name.equals(END); }
   public final String  getName()           { return name;   }

   public final void setName(String name)  {
      Assert.notNull(name);
      Assert.notFalse( !name.equals("") );
      this.name = name;
   }

   public String[] getPreconditionGroups() {
      String[] output = new String[consumed.size()];
      Enumeration enum = consumed.keys();
      for(int i = 0; enum.hasMoreElements(); i++ )
         output[i] = (String)enum.nextElement();
      return output;
   }

   public String[] getPostconditionGroups() {
      String[] output = new String[produced.size()];
      Enumeration enum = produced.keys();
      for(int i = 0; enum.hasMoreElements(); i++ )
         output[i] = (String)enum.nextElement();
      return output;
   }

   public void setPostconditions(Hashtable input) {
      produced.clear();
      String group;
      Vector data, List;
      Enumeration enum = input.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         List = (Vector)input.get(group);
         data = new Vector();
         for(int i = 0; i < List.size(); i++ )
            data.addElement(new Fact((Fact)List.elementAt(i)));
         produced.put(group,data);
      }
   }

   public void setPreconditions(Hashtable input) {
      consumed.clear();
      String group;
      Vector data, List;
      Enumeration enum = input.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         List = (Vector)input.get(group);
         data = new Vector();
         for(int i = 0; i < List.size(); i++ )
            data.addElement(new Fact((Fact)List.elementAt(i)));
         consumed.put(group,data);
      }
   }

   public Hashtable getAllPostconditions() {
      Hashtable output = new Hashtable();
      String group;
      Vector data, List;
      Enumeration enum = produced.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         List = (Vector)produced.get(group);
         data = new Vector();
         for(int i = 0; i < List.size(); i++ )
            data.addElement(new Fact((Fact)List.elementAt(i)));
         output.put(group,data);
      }
      return output;
   }

   public Hashtable getAllPreconditions() {
      Hashtable output = new Hashtable();
      String group;
      Vector data, List;
      Enumeration enum = consumed.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         List = (Vector)consumed.get(group);
         data = new Vector();
         for(int i = 0; i < List.size(); i++ )
            data.addElement(new Fact((Fact)List.elementAt(i)));
         output.put(group,data);
      }
      return output;
   }

   public Vector produced() { return getGroup(produced,DEFAULT_GROUP); }
   public Vector consumed() { return getGroup(consumed,DEFAULT_GROUP); }

   public Vector produced(String group) { return getGroup(produced,group); }
   public Vector consumed(String group) { return getGroup(consumed,group); }

   public void setPostconditions(Vector List) {
      setPostconditions(DEFAULT_GROUP,List);
   }
   public void setPostconditions(Fact[] List) {
      setPostconditions(DEFAULT_GROUP,List);
   }
   public void setPreconditions(Vector List) {
      setPreconditions(DEFAULT_GROUP,List);
   }
   public void setPreconditions(Fact[] List) {
      setPreconditions(DEFAULT_GROUP,List);
   }

   protected Vector getGroup(Hashtable table, String group) {
      Vector data = (Vector)table.get(group);
      if ( data == null ) {
         data = new Vector();
         table.put(group,data);
      }
      return data;
   }

   public void setPostconditions(String group, Vector List) {
      Vector data = getGroup(produced,group);
      data.removeAllElements();
      for(int i = 0; i < List.size(); i++ )
         data.addElement(new Fact((Fact)List.elementAt(i)));
   }

   public void setPostconditions(String group, Fact[] List) {
      Vector data = getGroup(produced,group);
      data.removeAllElements();
      for(int i = 0; i < List.length; i++ )
         data.addElement(new Fact(List[i]));
   }

   public void setPreconditions(String group, Vector List) {
      Vector data = getGroup(consumed,group);
      data.removeAllElements();
      for(int i = 0; i < List.size(); i++ )
         data.addElement(new Fact((Fact)List.elementAt(i)));
   }

   public void setPreconditions(String group, Fact[] List) {
      Vector data = getGroup(consumed,group);
      data.removeAllElements();
      for(int i = 0; i < List.length; i++ )
         data.addElement(new Fact(List[i]));
   }

   public Fact[] getPostconditions() {
      return getPostconditions(DEFAULT_GROUP);
   }
   public Fact[] getPreconditions() {
      return getPreconditions(DEFAULT_GROUP);
   }

   public Fact[] getPostconditions(String group) {
      Vector data = getGroup(produced,group);
      Fact[] out = new Fact[data.size()];
      for(int i = 0; i < data.size(); i++)
         out[i] = new Fact((Fact)data.elementAt(i));
      return out;
   }

   public Fact[] getPreconditions(String group) {
      Vector data = getGroup(consumed,group);
      Fact[] out = new Fact[data.size()];
      for(int i = 0; i < data.size(); i++)
         out[i] = new Fact((Fact)data.elementAt(i));
      return out;
   }

   public int countPreconditions() {
      return countPreconditions(DEFAULT_GROUP);
   }
   public int countPostconditions() {
      return countPostconditions(DEFAULT_GROUP);
   }
   public int[] numPreconditions() {
      return numPreconditions(DEFAULT_GROUP);
   }
   public int[] numPostconditions() {
      return numPostconditions(DEFAULT_GROUP);
   }

   public int countPreconditions(String group) {
      return getGroup(consumed,group).size();
   }
   public int countPostconditions(String group) {
      return getGroup(produced,group).size();
   }

   public int[] numPreconditions(String group) {
      Vector data = getGroup(consumed,group);
      int[] array = new int[data.size()];
      for(int i = 0; i < data.size(); i++)
         array[i] = ((Fact)data.elementAt(i)).getNumber();
      return array;
   }
   public int[] numPostconditions(String group) {
      Vector data = getGroup(produced,group);
      int[] array = new int[data.size()];
      for(int i = 0; i < data.size(); i++)
         array[i] = ((Fact)data.elementAt(i)).getNumber();
      return array;
   }

   public Fact getPrecondition(String fid) {
      return getPrecondition(DEFAULT_GROUP,fid);
   }
   public Fact getPostcondition(String fid) {
      return getPostcondition(DEFAULT_GROUP,fid);
   }
   public Fact getPrecondition(int pos) {
      return getPrecondition(DEFAULT_GROUP,pos);
   }
   public Fact getPostcondition(int pos) {
      return getPostcondition(DEFAULT_GROUP,pos);
   }
   public int getConsumedPos(Fact fact) {
      return getConsumedPos(DEFAULT_GROUP,fact);
   }
   public int getProducedPos(Fact fact) {
      return getProducedPos(DEFAULT_GROUP,fact);
   }

   public Fact getPrecondition(String group, String fid) {
      Vector data = getGroup(consumed,group);
      Fact f;
      for(int i = 0; i < data.size(); i++) {
        f = (Fact)data.elementAt(i);
        if ( fid.equals(f.getId()))
           return f;
      }
      return null;
   }
   public Fact getPostcondition(String group, String fid) {
      Vector data = getGroup(produced,group);
      Fact f;
      for(int i = 0; i < data.size(); i++) {
        f = (Fact)data.elementAt(i);
        if ( fid.equals(f.getId()))
           return f;
      }
      return null;
   }
   public Fact getPrecondition(String group, int pos) {
      Vector data = getGroup(consumed,group);
      return (Fact)data.elementAt(pos);
   }
   public Fact getPostcondition(String group, int pos) {
      Vector data = getGroup(produced,group);
      return (Fact)data.elementAt(pos);
   }

   public int getConsumedPos(String group, Fact fact) {
      Vector data = getGroup(consumed,group);
      String fid = fact.getId();
      Fact f;
      for(int i = 0; i < data.size(); i++) {
        f = (Fact)data.elementAt(i);
        if ( fid.equals(f.getId()))
           return i;
      }
      Assert.notNull(null);
      return -1;
   }
   public int getProducedPos(String group, Fact fact) {
      Vector data = getGroup(produced,group);
      String fid = fact.getId();
      Fact f;
      for(int i = 0; i < data.size(); i++) {
        f = (Fact)data.elementAt(i);
         if ( fid.equals(f.getId()))
           return i;
      }
      Assert.notNull(null);
      return -1;
   }

   public void relaxNumberFields() {
      Fact f1;
      ValueFunction fn;
      Vector data;
      Enumeration enum = consumed.elements();
      while( enum.hasMoreElements() ) {
         data = (Vector)enum.nextElement();
         for(int i = 0; i < data.size(); i++ ) {
            f1 = (Fact)data.elementAt(i);
            if ( f1.isa(OntologyDb.ENTITY) ) {
               fn = f1.getFn(OntologyDb.NUMBER);
               if ( fn.getID() == ValueFunction.INT )
                  f1.setValue(OntologyDb.NUMBER,f1.newVar());
            }
         }
      }
      enum = produced.elements();
      while( enum.hasMoreElements() ) {
         data = (Vector)enum.nextElement();
         for(int i = 0; i < data.size(); i++ ) {
            f1 = (Fact)data.elementAt(i);
            if ( f1.isa(OntologyDb.ENTITY) ) {
               fn = f1.getFn(OntologyDb.NUMBER);
               if ( fn.getID() == ValueFunction.INT )
                  f1.setValue(OntologyDb.NUMBER,f1.newVar());
            }
         }
      }
   }

   public boolean resolve(Bindings bindings) {
      Core.DEBUG(3,"TaskNode resolve: b = " + bindings);
      return resolve(new ResolutionContext(),bindings);
   }
   public boolean resolve(ResolutionContext context, Bindings bindings) {
   /**
      Note: All precondition groups (actually there is only one -- the
      default group) must be successfully resolved. However, for postcondition
      groups, any group that does not successfully resolve is removed. If all
      postcondition groups are eventually removed, then the resolution is
      deemed to have failed.
   */
      boolean status = true;
      Vector data;
      Enumeration enum = consumed.elements();
      while( enum.hasMoreElements() ) {
         data = (Vector)enum.nextElement();
         for(int i = 0; status && i < data.size(); i++ )
            status &= ((Fact)data.elementAt(i)).resolve(context,bindings);
      }

      if ( !status ) return status;

      String group;
      Bindings b;
      enum = produced.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         data = (Vector)produced.get(group);

         boolean local_status = true;
         b = new Bindings(bindings);
         for(int i = 0; local_status && i < data.size(); i++ )
            local_status &= ((Fact)data.elementAt(i)).resolve(context,b);

         if ( local_status )
	    bindings.add(b);
         else
            produced.remove(group);
      }

      if ( produced.isEmpty() ) return false;
      return status;
   }

   public String toString() {
      String s = new String("(");

      s += ":name " + name + " ";
      s += ":is_conditional " + isConditionalNode + " ";

      Enumeration enum;
      String group;
      Vector data;

      if ( !consumed.isEmpty() ) {
         s += ":consumed_facts (";
         enum = consumed.keys();
         while( enum.hasMoreElements() ) {
            group = (String)enum.nextElement();
            data = (Vector)consumed.get(group);
            s += "(:group " + group + " (";
            for(int i = 0; i < data.size(); i++ )
               s += data.elementAt(i);
            s += "))";
         }
         s += ") ";
      }
      if ( !produced.isEmpty() ) {
         s += ":produced_facts (";
         enum = produced.keys();
         while( enum.hasMoreElements() ) {
            group = (String)enum.nextElement();
            data = (Vector)produced.get(group);
            s += "(:group " + group + " (";
            for(int i = 0; i < data.size(); i++ )
               s += data.elementAt(i);
            s += "))";
         }
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
      s += ":is_conditional " + isConditionalNode + eol;

      Enumeration enum;
      String group;
      Vector data;

      if ( !consumed.isEmpty() ) {
         prefix = ":consumed_facts ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
	 enum = consumed.keys();
         while( enum.hasMoreElements() ) {
            group = (String)enum.nextElement();
            data = (Vector)consumed.get(group);
            s += "(:group " + group + " (";
            for(int i = 0; i < data.size(); i++ )
               s += ((Fact)data.elementAt(i)).pprint(1+suffix.length()) +
                    "\n" + suffix + " ";
            s = s.trim() + "\n" + suffix + "))" + eol;
         }
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      if ( !produced.isEmpty() ) {
         prefix = ":produced_facts ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + "(";
	 enum = produced.keys();
         while( enum.hasMoreElements() ) {
            group = (String)enum.nextElement();
            data = (Vector)produced.get(group);
            s += "(:group " + group + " (";
            for(int i = 0; i < data.size(); i++ )
               s += ((Fact)data.elementAt(i)).pprint(1+suffix.length()) +
                    "\n" + suffix + " ";
            s = s.trim() + "\n" + suffix + "))" + eol;
         }
         s = s.trim() + "\n" + suffix + ")" + eol;
      }
      return s.trim() + "\n" + tabs + ")";
   }

   public TaskNode duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public TaskNode duplicate(DuplicationTable table) {
      Enumeration enum;
      String group;
      Vector data;
      Fact[] xdata;

      TaskNode xnode = new TaskNode(name);

      enum = consumed.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         data = (Vector)consumed.get(group);
         xdata = new Fact[data.size()];
         for(int i = 0; i < data.size(); i++ )
            xdata[i] = ((Fact)data.elementAt(i)).duplicate(table);
         xnode.setPreconditions(group,xdata);
      }

      enum = produced.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         data = (Vector)produced.get(group);
         xdata = new Fact[data.size()];
         for(int i = 0; i < data.size(); i++ )
            xdata[i] = ((Fact)data.elementAt(i)).duplicate(table);
         xnode.setPostconditions(group,xdata);
      }
      return xnode;
   }
}
