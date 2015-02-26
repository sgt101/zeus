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



package zeus.util;

import java.util.*;

public class ReferenceTable extends Hashtable {
   protected String defaultReference = null;

   public ReferenceTable() {
   }
   public ReferenceTable(String defaultReference) {
      Assert.notNull(defaultReference);
      this.defaultReference = defaultReference;
   }
   public void add(String name, String reference) {
      this.put(name,reference);
   }
   public void replace(String name, String reference) {
      this.put(name,reference);
   }
   public void del(String name) {
      this.remove(name);
   }
   public String lookup(String name) {
      Assert.notNull(name);
      String reference = (String)this.get(name);
      if ( reference == null ) {
         if ( defaultReference != null ) {
            reference = defaultReference;
            add(name,reference);
         }
      }
      return reference;
   }
   public String lookup(String name, String otherwise) {
      Assert.notNull(otherwise);
      if ( name == null ) return otherwise;

      String reference = (String)this.get(name);
      if ( reference == null ) {
         reference = otherwise;
         add(name,reference);
      }
      return reference;
   }
   public void set(Vector data) {
      clear();
      String name, reference;
      for( int i = 0; data != null && i < data.size(); i += 2 ) {
         name = (String)data.elementAt(i);
         reference = (String)data.elementAt(i+1);
         add(name,reference);
      }
   }
   public String toString() {
    Enumeration enum = this.keys();
    String name, reference;
    String s = new String();
   
    while( enum.hasMoreElements() ) {
      name = (String) enum.nextElement();
      reference = lookup(name);
      s += name + " " + reference + " ";
    }
    s = s.trim();
    return s;
  }

  public String pprint() {
    return pprint(0);
  }

  public String pprint(int sp) {
    String tabs = Misc.spaces(sp);
    String eol  = "\n" + tabs;

    String s = new String();
    Enumeration enum = this.keys();
    String name, reference;
   
    while( enum.hasMoreElements() ) {
      name = (String) enum.nextElement();
      reference = lookup(name);
      s += name + " " + reference + eol;
    }
    return s.trim();
  }

  public static void main(String[] args) {
     ReferenceTable t = new ReferenceTable("--DefaultName--");
     t.add("divine","aaaaa");
     t.add("john","bbbbb");
     t.add("george","bbbbb");
     String s = t.lookup("adam");
     System.out.println(s);
     System.out.println(t.pprint());
  }
     
}
