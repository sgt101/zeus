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

public class SummaryTable extends Hashtable {
   public SummaryTable() {
   }

   public void add(String agent) {
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         table = new Hashtable();
         put(agent,table);
      }
   }

   public void del(String agent) {
      this.remove(agent);
   }
   
   public String[] listAgents() {
      String[] agents = new String[size()];
      Enumeration enum = keys();
      for( int i = 0; enum.hasMoreElements(); i++ )
         agents[i] = (String)enum.nextElement();
      return agents;
   }

   public String[][] getData(String agent) {
      Hashtable table = (Hashtable)get(agent);
      if ( table == null || table.isEmpty() ) return null;
      String[][] data = new String[table.size()][];
      Enumeration enum = table.elements();
      Summary g;
      for( int i = 0; enum.hasMoreElements(); i++ ) {
         g = (Summary)enum.nextElement();
         data[i] = g.summarize();
      }
      return data;   
   }

   public Summary getData(String agent, String id) {
      Hashtable table = (Hashtable)get(agent);
      if ( table == null || table.isEmpty() ) return null;
      return (Summary) table.get(id);
   }

   public void add(String agent, Summary item) {
      String id = item.getId();
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         table = new Hashtable();
         this.put(agent,table);
      }
      table.put(id,item);
   }
   public void add(String agent, Vector items) {
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         table = new Hashtable();
         this.put(agent,table);
      }
      Summary item;
      String id;
      for(int i = 0; i < items.size(); i++ ) {
         item = (Summary)items.elementAt(i);
         id = item.getId(); 
         table.put(id,item);
      }
   }

   public void del(String agent, Summary item) {
      String id = item.getId(); 
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         Assert.notNull(null);
         return;
      }
      Assert.notNull( table.remove(id) );
   }
   public void del(String agent, Vector items) {
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         Assert.notNull(null);
         return;
      }
      Summary item;
      String id;
      for(int i = 0; i < items.size(); i++ ) {
         item = (Summary)items.elementAt(i);
         id = item.getId();
         Assert.notNull( table.remove(id) );
      }
   }

   public void modify(String agent, Summary item) {
      String id = item.getId();
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         Assert.notNull(null);
         return;
      }
      Assert.notNull( table.put(id,item) );
   }
   public void modify(String agent, Vector items) {
      Hashtable table = (Hashtable)get(agent);
      if ( table == null ) {
         Assert.notNull(null);
         return;
      }
      Summary item;
      String id;
      for(int i = 0; i < items.size(); i++ ) {
         item = (Summary)items.elementAt(i);
         id = item.getId();
         Assert.notNull( table.put(id,item) );
      }
   }
}
