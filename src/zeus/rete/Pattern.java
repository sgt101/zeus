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


public class Pattern {
   static final String FACT_MARKER = "<-";
   static final String NEGATION    = "not";
   static final String TEST_NAME   = "test";

   public static final int NONE = 0;
   public static final int NOT  = 1;
   public static final int TEST = 2;
   public static final int CMD  = 3;

   VarFn  id = null;
   int    tag  = NONE;
   Object data = null;

   public Pattern(ReteFact t) {
      this.data = t;
   }
   
   
   public Pattern(int tag, ReteFact t) {
      Assert.notFalse(tag == NOT || tag == NONE);
      this.tag = tag;
      this.data = t;
   }
   
   
   public Pattern(int tag, ValueFunction constraint) {
      Assert.notFalse(tag == TEST);
      this.tag = tag;
      this.data = constraint;
   }
   
   
   public Pattern(Pattern p) {
      ValueFunction v;

      tag = p.tag;
      id = p.id;
      switch(p.tag) {
         case NONE:
         case NOT:
              data = new ReteFact((ReteFact)p.data);
              break;

         case TEST:
              data = ((ValueFunction)p.data).mirror();
              break;
      }
   }


   public void setId(VarFn id) { this.id = id; }


   Pattern duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   
   
   Pattern duplicate(DuplicationTable table) {
      ReteFact f;
      Pattern p;
      ValueFunction v;

      switch(tag) {
         case NONE:
         case NOT:
              f = (ReteFact)data;
              f = f.duplicate(table);
              p = new Pattern(tag,f);
              if ( id != null ) p.id = (VarFn)id.duplicate(table);
              return p;

         case TEST:
              v = (ValueFunction)data;
              v = v.duplicate(table);
              p = new Pattern(tag,v);
              return p;
      }
      return this;
   }
   
   
   public boolean resolve(Bindings b) {
      ReteFact f;
      ValueFunction v;

      if ( id != null ) {
         if ( (id = (VarFn)id.resolve(b)) == null )
            return false;
      }
     
      switch(tag) {
         case NONE:
         case NOT:
              f = (ReteFact)data;
              return f.resolve(b);

         case TEST:
              v = (ValueFunction)data;
              return ( (v = v.resolve(b)) != null );
      }
      return false;
   }
   
   
   public String toString() {
      switch(tag) {
         case NONE:
              if ( id == null ) 
                 return data.toString();
              else
                 return id.toString() + " " + 
                        FACT_MARKER + " " + data.toString();

         case NOT:
              return "(" + NEGATION + " " + data.toString() + ")";

         case TEST:
              return "(" + TEST_NAME + " " + data.toString() + ")";
      }
      Core.ERROR(null,1,this);
      return null;
   }
   
   
   public String pprint() {
      return pprint(0);
   }
   
   
   public String pprint(int sp) {
      switch(tag) {
         case NONE:
              return Misc.spaces(sp) + 
                     data.toString();

         case NOT:
              return Misc.spaces(sp) + 
                     "(" + NEGATION + " " + data.toString() + ")";

         case TEST:
              return Misc.spaces(sp) + 
                     "(" + TEST_NAME + " " + data.toString() + ")";
      }
      Core.ERROR(null,1,this);
      return null;
   }
}
