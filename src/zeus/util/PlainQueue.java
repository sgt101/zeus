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

public class PlainQueue {
   protected Vector[] data = null;

   public PlainQueue() {
      this(1);
   }

   public PlainQueue(int levels) {
      Assert.notFalse(levels > 0);
      data = new Vector[levels];
      for(int i = 0; i < data.length; i++ )
         data[i] = new Vector();
   }

   public synchronized Enumeration elements() {
      Vector out = new Vector();
      for(int i = 0; i < data.length; i++ )
         for(int j = 0; j < data[i].size(); j++ )
            out.addElement(data[i].elementAt(j));
      return out.elements();
   }

   public synchronized boolean remove(Object elem) {
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; j < data[i].size(); j++ ) {
            if ( data[i].contains(elem) ) {
               data[i].removeElement(elem);
               return true;
            }
         }
      }
      return false;
   }

   public synchronized boolean remove(Object elem, int level) {
      for(int j = 0; j < data[level].size(); j++ ) {
         if ( data[level].contains(elem) ) {
            data[level].removeElement(elem);
            return true;
         }
      }
      return false;
   }

   public synchronized void enqueue(Object elem) {
      enqueue(elem, 0);
   }

   public synchronized void enqueue(Object elem, int level) {
      Assert.notFalse(level >= 0 && level < data.length);
      data[level].addElement(elem);
   }

   public synchronized Object dequeue() {
      for(int i = 0; i < data.length; i++ ) {
         if ( !data[i].isEmpty() ) {
            Object elem = data[i].firstElement();
            data[i].removeElement(elem);
            return elem;
         }
      }
      return null;
   }


   public synchronized Object peek() {
      for(int i = 0; i < data.length; i++ ) {
         if ( !data[i].isEmpty() )
            return data[i].firstElement();
      }
      return null;
   }

   public synchronized boolean isEmpty() {
      for(int i = 0; i < data.length; i++ )
         if ( !data[i].isEmpty() )
            return false;
      return true;
   }
   
   public synchronized void clear() {
      for(int i = 0; i < data.length; i++ )
         data[i].removeAllElements();
   }

}
