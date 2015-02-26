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


/**
    ReteFact is used as an internal store for a fact for processing by the Rete engine. 
    */
public class ReteFact {
    
   /**
        type of fact - variable, fact? 
        This is needed to allow extensions of the language
        */
   String type = null;
   
   
   public Hashtable data = null;
   

   public ReteFact(String type) {
      this.type = type;
      data = new Hashtable();
   }
   
   
   public ReteFact(ReteFact t) {
      type = t.type;
      data = new Hashtable();
      String attribute;
      ValueFunction value;
      Enumeration enum = t.data.keys();
      while( enum.hasMoreElements() ) {
         attribute = (String)enum.nextElement();
         value = (ValueFunction)t.data.get(attribute);
         data.put(attribute,value.mirror());
      }
   }


   public void setValue(String attribute, ValueFunction value) {
      data.put(attribute,value);
   }


   ValueFunction getValue(String attribute) {
      return (ValueFunction)data.get(attribute);
   }
 
   
   ValueFunction[] listValues() {
      ValueFunction[] value = new ValueFunction[data.size()];
      Enumeration enum = data.elements();
      for(int i = 0; enum.hasMoreElements(); i++ )
         value[i] = (ValueFunction)enum.nextElement();
      return value;
   }


   String[] listAttributes() {
      String[] value = new String[data.size()];
      Enumeration enum = data.keys();
      for(int i = 0; enum.hasMoreElements(); i++ )
         value[i] = (String)enum.nextElement();
      return value;
   }


   ValueFunction[] variables() {
      Vector out = new Vector();
      Enumeration enum = data.elements();
      while( enum.hasMoreElements()) {
        out = Misc.union(out,((ValueFunction)enum.nextElement()).variables());}

      ValueFunction[] result = new ValueFunction[out.size()];
      for(int i = 0; i < result.length; i++ )
         result[i] = (ValueFunction)out.elementAt(i);
      return result;
   }


   public ReteFact duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   
   
   public ReteFact duplicate(DuplicationTable table) {
      ReteFact t = new ReteFact(type);
      String attribute;
      ValueFunction value;
      Enumeration enum = this.data.keys();
      while( enum.hasMoreElements() ) {
         attribute = (String)enum.nextElement();
         value = (ValueFunction)this.data.get(attribute);
         t.data.put(attribute,value.duplicate(table));
      }
      return t;
   }
   
   
   /** 
    provide get wrapper for type of fact 
    */
    public String getType () { 
        return type; 
    }
    
    
    /** 
    provide set wrapper for type of fact 
    */
    public void setType (String type) { 
        this.type = type; 
    }
    
   
   /** 
    this seems to be called when rete is trying to process 
    a rule
    */
   public boolean resolve(Bindings b) {
      String attribute;
      ValueFunction value;
      Enumeration enum = data.keys();
      while( enum.hasMoreElements() ) {
         attribute = (String)enum.nextElement();
         value = (ValueFunction)data.get(attribute);
         if ( (value = value.resolve(b)) == null )
            return false;
         data.put(attribute,value);
      }
      return true;
   }
   
   
   public String toString() {
      String s = "(" + type + " ";
      String attribute;
      ValueFunction value;
      Enumeration enum = data.keys();
      while( enum.hasMoreElements() ) {
         attribute = (String)enum.nextElement();
         value = (ValueFunction)data.get(attribute);
         s += "(" + attribute + " " + value + ") ";
      }
      return s.trim() + ")";
   }
   
   
   public String pprint() {
      return pprint(0);
   }
   
   
   public String pprint(int sp) {
      return Misc.spaces(sp) + toString();
   }
}
