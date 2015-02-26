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

public class HSet {
   private static final String NONE = "NONE";
   protected Hashtable table;

   public HSet() {
      table = new Hashtable();
   }
   public HSet(int capacity) {
      table = new Hashtable(capacity);
   }
   public HSet(int capacity, float loadFactor) {
      table = new Hashtable(capacity, loadFactor);
   }

   public synchronized Enumeration elements() {
      return table.keys();
   }
   public synchronized void add(HSet input) {
      Enumeration enum = input.elements();
      while( enum.hasMoreElements() )
         table.put(enum.nextElement(),NONE);
   }
   public synchronized void add(Object data) {
      table.put(data,NONE);
   }

   public synchronized void remove(Object data) {
      table.remove(data);
   }

   public synchronized void clear() {
      table.clear();
   }
   public synchronized Object clone() {
      return null;
   }
   public synchronized int size() {
      return table.size();
   }
   public synchronized boolean isEmpty() {
      return table.isEmpty();
   }
   public synchronized boolean contains(Object data) {
      return table.containsKey(data);
   }
   public synchronized Vector toVector() {
      Vector output = new Vector();
      Enumeration enum = table.keys();
      while( enum.hasMoreElements() )
         output.addElement(enum.nextElement());
      return output;
   }
}
