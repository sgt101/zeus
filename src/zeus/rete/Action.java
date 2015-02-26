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

public class Action {
   public static final int ASSERT    = 0;
   public static final int RETRACT   = 1;
   public static final int MODIFY    = 2;
   public static final int PRINT     = 3;
   public static final int MESSAGE   = 4;
   public static final int ACHIEVE   = 5;
   public static final int BUY       = 6;
   public static final int SELL      = 7;
   public static final int EXECUTE   = 8;

   public static final int BIND      = 9;
   public static final int IF        = 10;
   public static final int WHILE     = 11;
   public static final int OPEN      = 12;
   public static final int CLOSE     = 13;
   public static final int READ      = 14;
   public static final int READLN    = 15;
   public static final int SYSTEM    = 16;
   public static final int CALL      = 17;
   public static final int PRINTLN   = 18;

   public static final String[] types = {
      "assert",
      "retract",
      "modify",
      "print",
      "send_message",
      "achieve",
      "buy",
      "sell",
      "execute",
      "bind",
      "if",
      "while",
      "open",
      "close",
      "read",
      "readln",
      "system",
      "call",
      "println"
   };

   public int       type         = -1;
   public Vector    items        = null;
   public Hashtable table        = null;
   public Object    head         = null;
   public Object    sub_head     = null;
   public Object    sub_sub_head = null;
   public Vector    sub_items    = null;

   public Action(String type) {
      this(Misc.whichPosition(type,types));
   }

   public Action(int type) {
      this.type = type;

      switch(type) {
         case ASSERT:
         case EXECUTE:
              break;

         case RETRACT:
              items = new Vector(10);
              break;

         case MODIFY:
         case MESSAGE:
         case ACHIEVE:
         case BUY:
         case SELL:
              table = new Hashtable();
              break;


         case IF:
              items = new Vector(10);
              sub_items = new Vector(10);
              break;

         case WHILE:
              items = new Vector(10);
              break;

         case PRINT:
         case PRINTLN:
	 case OPEN:
         case CLOSE:
         case SYSTEM:
              items = new Vector(10);
              break;

         case BIND:
	 case READ:
         case READLN:
              break;
         default:
              Core.USER_ERROR("Unknown action type " + type);
      }
   }
   
   public Action(Action a) {
      type = a.type;

      ValueFunction var;
      Action b;
      switch(type) {
         case ASSERT:
              head = new ReteFact((ReteFact)a.head);
              break;

         case CLOSE:
	 case EXECUTE:
              head = ((ValueFunction)a.head).mirror();
              break;

         case SYSTEM:
         case RETRACT:
              items = new Vector(10);
              for(int i = 0; i < a.items.size(); i++ ) {
                 var = (ValueFunction)a.items.elementAt(i);
                 items.addElement(var.mirror());
              }
              break;

         case MODIFY:
         case MESSAGE:
         case ACHIEVE:
         case BUY:
         case SELL:
 	      table = new Hashtable();
              Enumeration enum = a.table.keys();
              Object key;
              while( enum.hasMoreElements() ) {
                 key = enum.nextElement();
                 var = (ValueFunction)a.table.get(key);
                 table.put(key,var.mirror());
              }
              switch(type) {
                 case MODIFY:
                      head = ((ValueFunction)a.head).mirror();
                      break;
                 case ACHIEVE:
                 case BUY:
                 case SELL:
                      head = new ReteFact((ReteFact)a.head);
                      break;
              }
              break;

         case PRINTLN:
         case PRINT:
              head = ((ValueFunction)a.head).mirror();
              items = new Vector(10);
              for(int i = 0; i < a.items.size(); i++ ) {
                 var = (ValueFunction)a.items.elementAt(i);
                 items.addElement(var.mirror());
              }
              break;

         case IF:
      	      head = ((ValueFunction)a.head).mirror();
              items = new Vector(10);
              for(int i = 0; i < a.items.size(); i++ ) {
                 b = (Action)a.items.elementAt(i);
                 items.addElement(new Action(b));
              }
              sub_items = new Vector(10);
              for(int i = 0; i < a.sub_items.size(); i++ ) {
                 b = (Action)a.sub_items.elementAt(i);
                 sub_items.addElement(new Action(b));
              }
              break;

         case WHILE:
      	      head = ((ValueFunction)a.head).mirror();
              items = new Vector(10);
              for(int i = 0; i < a.items.size(); i++ ) {
                 b = (Action)a.items.elementAt(i);
                 items.addElement(new Action(b));
              }
              break;

	 case OPEN:
              head = ((ValueFunction)a.head).mirror();
              sub_head = ((ValueFunction)a.sub_head).mirror();
              sub_sub_head = ((ValueFunction)a.sub_sub_head).mirror();
              break;

         case BIND:
	 case READ:
	 case READLN:
              head = ((ValueFunction)a.head).mirror();
              sub_head = ((ValueFunction)a.sub_head).mirror();
              break;
      }
   }

   public Action duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public Action duplicate(DuplicationTable table) {
      Action a = new Action(type);

      ValueFunction var;
      Action b;
      switch(type) {
         case ASSERT:
              a.head = ((ReteFact)this.head).duplicate(table);
              break;

         case CLOSE:
         case EXECUTE:
              a.head = ((ValueFunction)this.head).duplicate(table);
              break;

         case SYSTEM:
	 case RETRACT:
              for(int i = 0; i < this.items.size(); i++ ) {
                 var = (ValueFunction)this.items.elementAt(i);
                 var = var.duplicate(table);
                 a.items.addElement(var);
              }
              break;

         case MODIFY:
         case MESSAGE:
         case ACHIEVE:
         case BUY:
         case SELL:
              Enumeration enum = this.table.keys();
              Object key;
              while( enum.hasMoreElements() ) {
                 key = enum.nextElement();
                 var = (ValueFunction)this.table.get(key);
                 var = var.duplicate(table);
                 a.table.put(key,var);
              }
              switch(type) {
                 case MODIFY:
                      a.head = ((ValueFunction)this.head).duplicate(table);
                      break;
                 case ACHIEVE:
                 case BUY:
                 case SELL:
                      a.head = ((ReteFact)this.head).duplicate(table);
                      break;
              }
              break;

         case PRINTLN:
         case PRINT:
              a.head = ((ValueFunction)this.head).duplicate(table);
              for(int i = 0; i < this.items.size(); i++ ) {
                 var = (ValueFunction)this.items.elementAt(i);
                 var = var.duplicate(table);
                 a.items.addElement(var);
              }
              break;

         case IF:
      	      a.head = ((ValueFunction)this.head).duplicate(table);
              a.items = new Vector(10);
              for(int i = 0; i < this.items.size(); i++ ) {
                 b = (Action)this.items.elementAt(i);
                 a.items.addElement(b.duplicate(table));
              }
              a.sub_items = new Vector(10);
              for(int i = 0; i < this.sub_items.size(); i++ ) {
                 b = (Action)this.sub_items.elementAt(i);
                 a.sub_items.addElement(b.duplicate(table));
              }
              break;

         case WHILE:
      	      a.head = ((ValueFunction)this.head).duplicate(table);
              a.items = new Vector(10);
              for(int i = 0; i < this.items.size(); i++ ) {
                 b = (Action)this.items.elementAt(i);
                 a.items.addElement(b.duplicate(table));
              }
              break;

	 case OPEN:
              a.head = ((ValueFunction)this.head).duplicate(table);
              a.sub_head = ((ValueFunction)this.sub_head).duplicate(table);
              a.sub_sub_head = ((ValueFunction)this.sub_sub_head).duplicate(table);
              break;

         case BIND:
	 case READ:
	 case READLN:
// System.err.println("Action dup-before: table = " + table);
              a.head = ((ValueFunction)this.head).duplicate(table);
              a.sub_head = ((ValueFunction)this.sub_head).duplicate(table);
// System.err.println("Action dup-after: table = " + table);
              break;

      }
      return a;
   }

   public boolean resolve(Bindings b) {
      ValueFunction var;
      Action a;

      switch(type) {
         case ASSERT:
              return ((ReteFact)head).resolve(b);

         case EXECUTE:
              head = ((ValueFunction)head).resolve(b);
              return head != null;

         case SYSTEM:
         case RETRACT:
              for(int i = 0; i < items.size(); i++ ) {
                 var = (ValueFunction)items.elementAt(i);
                 var = var.resolve(b);
                 if ( var == null ) return false;
                 items.setElementAt(var,i);
              }
              break;

         case MODIFY:
         case MESSAGE:
         case ACHIEVE:
         case BUY:
         case SELL:
              Enumeration enum = table.keys();
              Object key;
              while( enum.hasMoreElements() ) {
                 key = enum.nextElement();
                 var = (ValueFunction)table.get(key);
                 var = var.resolve(b);
                 if ( var == null ) return false;
                 table.put(key,var);
              }
              switch(type) {
                 case MODIFY:
                      head = ((ValueFunction)head).resolve(b);
                      if ( head == null ) return false;
                      break;
                 case ACHIEVE:
                 case BUY:
                 case SELL:
                      return ((ReteFact)head).resolve(b);
              }
              break;

         case PRINTLN:
         case PRINT:
              head = ((ValueFunction)head).resolve(b);
              if ( head == null ) return false;

              for(int i = 0; i < items.size(); i++ ) {
                 var = (ValueFunction)items.elementAt(i);
                 var = var.resolve(b);
                 if ( var == null ) return false;
                 items.setElementAt(var,i);
              }
              break;

         case IF:
      	      head = ((ValueFunction)head).resolve(b);
              if ( head == null ) return false;

              for(int i = 0; i < items.size(); i++ ) {
                 a = (Action)items.elementAt(i);
                 if ( !a.resolve(b) ) return false;
              }
              for(int i = 0; i < sub_items.size(); i++ ) {
                 a = (Action)sub_items.elementAt(i);
                 if ( !a.resolve(b) ) return false;
              }
              break;

         case WHILE:
      	      head = ((ValueFunction)head).resolve(b);
              if ( head == null ) return false;

              for(int i = 0; i < items.size(); i++ ) {
                 a = (Action)items.elementAt(i);
                 if ( !a.resolve(b) ) return false;
              }
              break;

	 case OPEN:
              head = ((ValueFunction)head).resolve(b);
              sub_head = ((ValueFunction)sub_head).resolve(b);
              sub_sub_head = ((ValueFunction)sub_sub_head).resolve(b);
              return head != null && sub_head != null && sub_sub_head != null;

         case BIND:
	 case READ:
	 case READLN:
              head = ((ValueFunction)head).resolve(b);
              sub_head = ((ValueFunction)sub_head).resolve(b);
              return head != null && sub_head != null;
      }
      return true;
   }
   
   public String toString() {
      ValueFunction var;
      Enumeration enum;
      String s = "(" + types[type] + " ";
      switch(type) {
         case ASSERT:
              s += head;
              break;

         case CLOSE:
	 case EXECUTE:
              s += head;
              break;

         case SYSTEM:
         case RETRACT:
              s += Misc.concat(items);
              break;

         case MODIFY:
	 case MESSAGE:
         case ACHIEVE:
         case BUY:
         case SELL:
              switch(type) {
                 case MODIFY:
                      s += head + " ";
                      break;
                 case ACHIEVE:
                 case BUY:
                 case SELL:
                      s += "(" + OntologyDb.GOAL_FACT + " " + head + ") ";
                      break;
              }
              enum = table.keys();
              Object key;
              while( enum.hasMoreElements() ) {
                 key = enum.nextElement();
                 var = (ValueFunction)table.get(key);
                 s += "(" + key + " " + var + ") ";
              }
              break;

         case PRINTLN:
         case PRINT:
              s += head + " " + Misc.concat(items);
              break;

         case IF:
      	      s += head + " then\n " + Misc.concat(items);
              if ( !sub_items.isEmpty() )
	         s += "\n else\n " + Misc.concat(sub_items);
              break;

         case WHILE:
      	      s += head + " do\n " + Misc.concat(items);
              break;

	 case OPEN:
              s += head + " " + sub_head + " " + sub_sub_head;
              break;

         case BIND:
	 case READ:
	 case READLN:
              s += head + " " + sub_head;
              break;

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
