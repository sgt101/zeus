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


class NotNode extends JoinNode {
   Hashtable countDb = new Hashtable();

   NotNode(ReteEngine engine) {
      super(engine);
      type = NOT;
   }
   void reset() {
      super.reset();
      countDb.clear();
   }
   void evaluate(String path, int tag, int type, Vector input, Bindings b) {
    //  System.out.println("evaluating notNode "+ toString()); 
      Vector store;
      boolean found;
      Fact f1, f2;
      String l_path, r_path, d_path;
      Vector l_memory, r_memory, counter, l_bindings, r_bindings;
      Integer obj;
      int x;
      Bindings b1, b2;

      switch(type) {
         case LEFT:
              l_path = path;
              r_path = (String)pathDb1.get(path);
              d_path = (String)pathDb2.get(l_path);
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
              counter = (Vector)countDb.get(l_path);
              if ( counter == null ) {
                 counter = new Vector();
                 countDb.put(l_path,counter);
              }
             
              if ( tag == ADD ) {
                 l_memory.addElement(input);
                 l_bindings.addElement(new Bindings(b));
                 x = 0;
                 for(int i = 0; i < r_memory.size(); i++ ) {
                    store = (Vector)r_memory.elementAt(i);
                    b2 = (Bindings)r_bindings.elementAt(i);
                    b1 = new Bindings(b);
                    if ( b1.add(b2) && match(input,store,b1) )
                       x += 1;
                 }
                 counter.addElement(new Integer(x));
                 if ( x == 0 ) propagate(d_path,tag,input,b);

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
                    if ( found )  {
                       l_memory.removeElementAt(i);
                       l_bindings.removeElementAt(i);
                       obj = (Integer)counter.elementAt(i);
                       counter.removeElementAt(i--);
                       x = obj.intValue();
                       if ( x == 0 ) propagate(d_path,tag,input,b);
                    }
                 }
              }
              break;

         case RIGHT:
              r_path = path;
              l_path = (String)pathDb1.get(path);
              d_path = (String)pathDb2.get(l_path);
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
              counter = (Vector)countDb.get(l_path);
              if ( counter == null ) {
                 counter = new Vector();
                 countDb.put(l_path,counter);
              }

              if ( tag == ADD ) {
                 r_memory.addElement(input);
                 r_bindings.addElement(new Bindings(b));
                 for(int i = 0; i < l_memory.size(); i++ ) {
                    store = (Vector)l_memory.elementAt(i);
                    b1 = (Bindings)l_bindings.elementAt(i);
                    b2 = new Bindings(b);
                    if ( b2.add(b1) && match(store,input,b2) ) {
                       obj = (Integer)counter.elementAt(i);
                       x = obj.intValue() + 1;
                       counter.setElementAt(new Integer(x),i);
                       if ( x == 1 )
                          propagate(d_path,REMOVE,store,b1);
                    }
                 }
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

                       for(int k = 0; k < l_memory.size(); k++ ) {
                          store = (Vector)l_memory.elementAt(k);
                          b1 = (Bindings)l_bindings.elementAt(k);
                          b2 = new Bindings(b);
                          if ( b2.add(b1) && match(store,input,b2) ) {
                             obj = (Integer)counter.elementAt(k);
                             x = obj.intValue() - 1;
                             Assert.notFalse(x >= 0);
                             counter.setElementAt(new Integer(x),k);
                             if ( x == 0 ) propagate(d_path,ADD,store,b1);
                          }
                       }
                    }
                 }
              }
              break;

         default:
            Assert.notNull(null);
      }
   }

   protected void evaluate(String path, int tag, Vector left,
                           Vector right, Bindings b) {
      Assert.notNull(null);
   }

   protected boolean match(Vector left, Vector right, Bindings bindings) {
      boolean status = true;
      JoinEntry e;
      Fact f1, f2;
      Bindings b = new Bindings(bindings);

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
      return status;
   }
   public boolean equals(Object any) {
      if ( !(any instanceof NotNode) ) return false;
      NotNode node = (NotNode)any;
      if ( node.type != NOT ) return false;
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
      return "NotNode(" + constraints + ")";
   }
}
