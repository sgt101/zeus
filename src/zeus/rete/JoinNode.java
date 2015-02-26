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
import zeus.concepts.fn.*;


class JoinNode extends Node {
   static final int PLAIN = 0;
   static final int NOT   = 1;

   Vector constraints  = new Vector();
   Hashtable db = new Hashtable();
   Hashtable pathDb1 = new Hashtable();
   Hashtable pathDb2 = new Hashtable();
   Hashtable bindingsDb = new Hashtable();
   int type = PLAIN;

   JoinNode(ReteEngine engine) {
      super(engine);
   }
   void add(int l_position, String l_attribute, ValueFunction l_value,
            int r_position, String r_attribute, ValueFunction r_value) {

      JoinEntry e = new JoinEntry(l_position,l_attribute,l_value,
                                  r_position,r_attribute,r_value);
      constraints.addElement(e);
   }
   void addPath(String l_path, String r_path, String d_path) {
      Assert.notFalse(pathDb1.put(l_path,r_path) == null);
      Assert.notFalse(pathDb1.put(r_path,l_path) == null);
      Assert.notFalse(pathDb2.put(l_path,d_path) == null);
   }
   void reset() {
      db.clear();
      bindingsDb.clear();
   }
   
   
 void evaluate(String path, int tag, int type, Vector input, Bindings b) {
     // System.out.println("evaluating joinNode " + toString()); 
      Vector store;
      boolean found;
      Fact f1, f2;
      String l_path, r_path;
      Vector l_memory, r_memory;
      Vector l_bindings, r_bindings;
      Bindings b1, b2;

      switch(type) {
         case LEFT:
              l_path = path;
              r_path = (String)pathDb1.get(path);
              l_memory = (Vector)db.get(l_path);
              l_bindings = (Vector)bindingsDb.get(l_path);
              if ( l_memory == null ) {
                 l_memory = new Vector();
                 l_bindings = new Vector();
                 db.put(l_path,l_memory);
                 bindingsDb.put(l_path,l_bindings);
              }
              r_memory = (Vector)db.get(r_path);
              r_bindings = (Vector)bindingsDb.get(r_path);
              if ( r_memory == null ) {
                 r_memory = new Vector();
                 r_bindings = new Vector();
                 db.put(r_path,r_memory);
                 bindingsDb.put(r_path,r_bindings);
              }
             
              if ( tag == ADD ) {
                 l_memory.addElement(input);
                 l_bindings.addElement(new Bindings(b));
              }
              else {
                 found = false;
                 for(int i = 0; !found && i < l_memory.size(); i++ ) {
                    store = (Vector)l_memory.elementAt(i);
                    found = true;
                    for(int j = 0; found && j < input.size(); j++ ) {
                       f1 = (Fact)input.elementAt(j);
                       f2 = (Fact)store.elementAt(j);
                       found &= f1.equals(f2);
                    }
                    if ( found ) {
                       l_memory.removeElementAt(i);
                       l_bindings.removeElementAt(i--);
                    }
                 }
                 if ( !found ) return;
              }
              for(int i = 0; i < r_memory.size(); i++ ) {
                 store = (Vector)r_memory.elementAt(i);
                 b2 = (Bindings)r_bindings.elementAt(i);
                 b1 = new Bindings(b);
                 if ( b1.add(b2) )
                    evaluate(l_path,tag,input,store,b1);
              }
              break;

         case RIGHT:
              r_path = path;
              l_path = (String)pathDb1.get(path);
              l_memory = (Vector)db.get(l_path);
              l_bindings = (Vector)bindingsDb.get(l_path);
              if ( l_memory == null ) {
                 l_memory = new Vector();
                 l_bindings = new Vector();
                 db.put(l_path,l_memory);
                 bindingsDb.put(l_path,l_bindings);
              }
              r_memory = (Vector)db.get(r_path);
              r_bindings = (Vector)bindingsDb.get(r_path);
              if ( r_memory == null ) {
                 r_memory = new Vector();
                 r_bindings = new Vector();
                 db.put(r_path,r_memory);
                 bindingsDb.put(r_path,r_bindings);
              }

              if ( tag == ADD ) {
                 r_memory.addElement(input);
                 r_bindings.addElement(new Bindings(b));
              }
              else {
                 found = false;
                 for(int i = 0; !found && i < r_memory.size(); i++ ) {
                    store = (Vector)r_memory.elementAt(i);
                    found = true;
                    for(int j = 0; found && j < input.size(); j++ ) {
                       f1 = (Fact)input.elementAt(j);
                       f2 = (Fact)store.elementAt(j);
                       found &= f1.equals(f2);
                    }
                    if ( found ) {
                       r_memory.removeElementAt(i);
                       r_bindings.removeElementAt(i--);
                    }
                 }
                 if ( !found ) return;
              }
              for(int i = 0; i < l_memory.size(); i++ ) {
                 store = (Vector)l_memory.elementAt(i);
                 b1 = (Bindings)l_bindings.elementAt(i);
                 b2 = new Bindings(b);
                 if ( b2.add(b1) )
                    evaluate(l_path,tag,store,input,b2);
              }
              break;

         default:
            Assert.notNull(null);
      }
   }
   
   
   protected void evaluate(String path, int tag, Vector left, 
                           Vector right, Bindings b) {
      boolean status = true;
      JoinEntry e;
      Fact f1, f2;

      for(int i = 0; status && i < constraints.size(); i++ ) {
         e = (JoinEntry)constraints.elementAt(i);
         f1 = (Fact)left.elementAt(e.l_position);
         f2 = (Fact)right.elementAt(e.r_position);
         ValueFunction lv = f1.getFn(e.l_attribute);
         ValueFunction rv = f2.getFn(e.r_attribute);
         status &= lv != null && rv != null &&
                   e.l_value.unifiesWith(lv,b) != null &&
                   e.r_value.unifiesWith(rv,b) != null;
      }
      if ( status ) {
         Vector result = new Vector();
         for(int i = 0; i < left.size(); i++ )
            result.addElement(left.elementAt(i));
         for(int i = 0; i < right.size(); i++ )
            result.addElement(right.elementAt(i));

         path = (String)pathDb2.get(path);
         propagate(path,tag,result,b);
      }
   }
   public boolean equals(Object any) {
      if ( !(any instanceof JoinNode) ) return false;
      JoinNode node = (JoinNode)any;
      if ( node.type != PLAIN ) return false;
      if ( node.constraints.size() != constraints.size() ) return false;
      boolean result = true; 
      JoinEntry e1, e2;
      for(int i = 0; result && i < constraints.size(); i++ ) {
         e1 = (JoinEntry)constraints.elementAt(i);
         e2 = (JoinEntry)node.constraints.elementAt(i);
         result &= e1.equals(e2);
      }
      return result;
   }
   public String toString() {
      return "JoinNode(" + constraints + ")";
   }
}
