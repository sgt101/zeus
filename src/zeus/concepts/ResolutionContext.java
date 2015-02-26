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

public class ResolutionContext extends Hashtable {

   public ResolutionContext() {
      super();
   }
   public ResolutionContext(Hashtable c) {
      this();
      add(c);
   }
   public ResolutionContext(ResolutionContext c) {
      this();
      add(c);
   }
   public void add(ResolutionContext c) {
      add((Hashtable)c);
   }
   public void add(Hashtable c) {
      String key;
      Enumeration enum = c.keys();
      while( enum.hasMoreElements() ) {
         key = (String)enum.nextElement();
         Assert.notFalse(this.put(key,c.get(key)) == null);
      }
   }
   public void add(Vector c) {
      for(int i = 0; i < c.size(); i++ )
         add((Fact)c.elementAt(i));
   }
   public void add(Fact f1) {
      Assert.notFalse(this.put(f1.getId(),f1) == null);
   }
   public Fact lookUp(String fid) {
      return (Fact)this.get(fid);
   }
   public String toString() {
      String out = "";
      Enumeration enum = this.elements();
      Object data;
      while( enum.hasMoreElements() ) {
         data = enum.nextElement();
         out += data;
      }
      return out;
   }
   public ResolutionContext duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public ResolutionContext duplicate(DuplicationTable table) {
      Fact f1;
      ResolutionContext context = new ResolutionContext();

      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         f1 = (Fact)enum.nextElement();
         context.add(f1.duplicate(table));
      }
      return context;
   }
}
