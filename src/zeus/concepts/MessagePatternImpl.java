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
import gnu.regexp.*;



/** 
    MessagePattern is used to set attribute value pairs for the MessageRule class: 
    you specify as string array like so {"attribite1","reg_exp expression for a1", "attribute2", 
    "reg_exp expression for a2", ...., "attribute<i>n</i>", "reg_exp expression for a<i>n</i>"} 
    */
public class MessagePatternImpl implements MessagePattern {
   protected Hashtable constraints = new Hashtable();

   public MessagePatternImpl() {
   }
   
   
   public MessagePatternImpl(String[] input) {
      for(int i = 0; i < input.length; i += 2 )
         setConstraint(input[i],input[i+1]);
   }
   
   
   public String[] listAttributes() {
      String[] out = new String[constraints.size()];
      Enumeration enum = constraints.keys();
      for(int i = 0; enum.hasMoreElements(); i++ )
         out[i] = (String)enum.nextElement();
      return out;
   }
   
   
   public RE[] listValues() {
      RE[] out = new RE[constraints.size()];
      Enumeration enum = constraints.elements();
      for(int i = 0; enum.hasMoreElements(); i++ )
         out[i] = (RE)enum.nextElement();
      return out;
   }
   
   
   public boolean setConstraint(String attribute, String regular_expr) {
      try {
         constraints.put(attribute, new RE(regular_expr,RE.REG_DOT_NEWLINE)); 
         return true;
      }
      catch(REException rese) {
        rese.printStackTrace();
        return false;
     }
   }
   
   
   public RE getConstraint(String attribute) {
      return (RE)constraints.get(attribute);
   }
   
   
   public String toString() {
      String s = "(";
      RE regexp;
      String key;
      Enumeration enum = constraints.keys();
      while( enum.hasMoreElements() ) {
         key = (String)enum.nextElement();
         regexp = (RE)constraints.get(key);
         s += "(" + key + " " + regexp + ") ";
      }
      return s.trim() + ")";
   }
}
