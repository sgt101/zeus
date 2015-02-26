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


public class AttributeList extends Hashtable {
   AttributeList() {
   }

   AttributeList(AttributeList List) {
      this();
      String attribute;
      ValueFunction value;

      for(Enumeration enum = List.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = List.getFn(attribute);
         this.setValue(attribute,value.mirror());
      }
   }

  
  /** 
    getNames produces a list of the names of the attributes. 
    You can iterate through this list using the getValue call to find the 
    values of the attributes
    *@since 1.21
    *@author Simon Thompson
    */
  public String [] getNames () { 
    return this.listAttributes(); 
  }


   public String[] listAttributes() {
      String[] result = new String[size()];

      Enumeration enum = this.keys();
      for(int i = 0; enum.hasMoreElements(); i++ )
         result[i] = (String) enum.nextElement();
      return result;
   }




   ValueFunction[] listValues() {
      ValueFunction[] result = new ValueFunction[size()];

      Enumeration enum = this.elements();
      for(int i = 0; enum.hasMoreElements(); i++ )
         result[i] = (ValueFunction) enum.nextElement();
      return result;
   }
   
   
   Vector variables() {
      Vector data = new Vector(100);
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() )
        data = Misc.union(data,((ValueFunction)enum.nextElement()).variables());
      return data;
   }


   public void setValue(String attribute, ValueFunction value) {
      this.put(attribute,value);
   }


   public String getValue(String attribute) {
      ValueFunction fn = (ValueFunction)get(attribute);
      if ( fn != null ) return fn.toString();
      else return null;
   }


   public ValueFunction getFn(String attribute) {
      return (ValueFunction)get(attribute);
   }


   public String toString() {
      String results = new String("(");
      String attribute;
      ValueFunction value;
      for(Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = (ValueFunction) this.get(attribute);

         results += "(" + attribute + " " + value + ")";
      }
      return results.trim() + ")";
   }

        /**
                added by Simon on 20/02/02 - rough and ready implementation as an
                experiment
                */
   public String toSL() {
      String results = new String("");
      String attribute;
      ValueFunction value;
      for(Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = (ValueFunction) this.get(attribute);

         results += ":" + attribute + " " + value + " ";
      }
      return results.trim() ;
   }


   String pprint() {
      return pprint(0);
   }
   
   
   String pprint(int sp) {
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";
      String results = new String("(");
      String attribute;
      ValueFunction value;

      for(Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = (ValueFunction)this.get(attribute);

         results += "(" + attribute + " " + value +")" + eol;
      }
      return results.trim() + "\n" + tabs + ")";
   }


   boolean unifiesWith(AttributeList List, Bindings bindings) {
      String attribute;
      ValueFunction value, m_value;

      Bindings b = new Bindings(bindings);
      for( Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = List.getFn(attribute);
         m_value = getFn(attribute);

         Core.ERROR(value,1,this);
         Core.ERROR(m_value,2,this);

         if ( !attribute.equals(OntologyDb.NUMBER) ) {
            if ( m_value.unifiesWith(value,b) == null )
               return false;
         }
         else {
            // attribute = OntologyDb.NUMBER
            // unify only if one of them is not a Primitive Numeric Fn
            if ( !(m_value instanceof NumericFn) ||
                 !(value instanceof NumericFn) ) {
               if ( m_value.unifiesWith(value,b) == null )
                  return false;
            }
         }
      }
      bindings.set(b);
      return true;
   }


    /** 
        somewhat changed to allow less constrained matches
        @author Simon Thompson
        @since 1.2.2
        *//*
   public boolean equals(AttributeList List) {
      String attribute;
      ValueFunction value, m_value;
      
      if ( this.size() != List.size() ) return false;

     
      for(Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String)enum.nextElement();
         value = List.getFn(attribute);
         // test for nullness
         if ( (m_value = List.getFn(attribute)) == null )
            return false;
         else if ( !value.equals(m_value) )
            return false;
      }
      return true;
   }*/
   
   
   
   
   public boolean equals(AttributeList List) {
      String attribute;
      ValueFunction value, m_value;
      
      if ( this.size() != List.size() ) return false;

     
      for(Enumeration enum = List.keys(); enum.hasMoreElements(); ) {
         attribute = (String)enum.nextElement();
         value = List.getFn(attribute);
         if ( (m_value = this.getFn(attribute)) == null )
            return false;
         else if ( !value.equals(m_value) )
            return false;
      }
      return true;
   }
   
   
   /*


    public boolean equals (AttributeList list ) { 
        String attribute = null; 
        Enumeration allThis   
    }*/

   boolean isDeterminate() {
      ValueFunction value;
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         value = (ValueFunction) enum.nextElement();
         if ( !value.isDeterminate() ) return false;
      }
      return true;
   }
   
   
   boolean isDeterminate(String attribute) {
      ValueFunction value = this.getFn(attribute);
      return value.isDeterminate();
   }


   boolean resolve(ResolutionContext context, Bindings bindings) {
      String attribute;
      ValueFunction value;

      for(Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = getFn(attribute);
         value = value.resolve(context,bindings);
         if ( value == null ) return false;
         this.setValue(attribute,value);
      }
      return true;
   }
   
   
   void disjoin(AttributeList List) {
      String attribute;
      ValueFunction value, m_value;

      for( Enumeration enum = List.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         if ( !attribute.equals(OntologyDb.NUMBER) ) {
            value = List.getFn(attribute);
            m_value = getFn(attribute);
            if ( m_value == null || value.equals(m_value) )
               setValue(attribute,value);
            else
               setValue(attribute,new OrFn(m_value,value));
         }
      }
   }


   AttributeList duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   
   
   AttributeList duplicate(DuplicationTable table) {
      AttributeList attr = new AttributeList();
      String attribute;
      ValueFunction value, your_value;

      for( Enumeration enum = this.keys(); enum.hasMoreElements(); ) {
         attribute = (String) enum.nextElement();
         value = this.getFn(attribute);
            your_value = value.duplicate(table);
         attr.setValue(attribute,your_value);
      }
      return attr;
   }
}
