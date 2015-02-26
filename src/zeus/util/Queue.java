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

public class Queue extends Thread {
   protected Vector[]   data = null;
   protected String name = null;

   /**
        Queues are all now named to make multithread debugging that little bit
        easier...
        */
   public Queue () {
     this(1);
     this.name = new String ("unnamed: default");
     this.start();
   }

   public Queue(String name) {
      this(1);
      this.name = name;
      this.start();

   }

   public Queue(int levels) {
      super(); 
      data = new Vector[levels];
      for(int i = 0; i < data.length; i++ )
         data[i] = new Vector(250);
   }


   public final synchronized Enumeration elements() {
      Vector out = new Vector();
      for(int i = 0; i < data.length; i++ )
         for(int j = 0; j < data[i].size(); j++ )
            out.add(data[i].get(j));
      return out.elements();
   }

   public final synchronized boolean remove(Object elem) {
      for(int i = 0; i < data.length; i++ ) {
         for(int j = 0; j < data[i].size(); j++ ) {
            if ( data[i].contains(elem) ) {
               data[i].remove(elem);
               return true;
            }
         }
      }
      return false;
   }

   public final synchronized boolean remove(Object elem, int level) {
      for(int j = 0; j < data[level].size(); j++ ) {
         if ( data[level].contains(elem) ) {
            data[level].remove(elem);
            return true;
         }
      }
      return false;
   }

   public void enqueue(Object elem) {
      enqueue(elem, 0);
   }


   public final synchronized  void enqueue(Object elem, int level) {
      data[level].add(elem);
      // System.err.flush();
      notify();
   }

   public final synchronized Object dequeue() {
  //    while(true) {
        while( isEmpty() ) {
            try {
                wait();
            }
            catch(InterruptedException e) {
            }
         }

         for(int i = 0; i < data.length; i++ ) {
            if ( !data[i].isEmpty() ) {
               Object elem = data[i].get(0);
               data[i].remove(elem);
               return elem;
            }
         }
    //  }
    return null; 
   }

   public final Object peek() {
      while( isEmpty() ) {
         try {
            wait();
         }
         catch(InterruptedException e) {
         }
      }
      for(int i = 0; i < data.length; i++ ) {
         if ( !data[i].isEmpty() )
            return data[i].get(0);
      }
      return null; // statement should never be reached
   }

   public final boolean isEmpty() {
      for(int i = 0; i < data.length; i++ )
         if ( !data[i].isEmpty() )
            return false;
      return true;
   }
   
   public final synchronized void clear() {
      for(int i = 0; i < data.length; i++ )
         data[i].clear();
   }


   /**
    * not synchronized because used for estimates, and not
    * critical
    */
   public int size () {
      return data[0].size();
    }


    /**
      * not synchronized
      */
   public int size (int level) {
      return data[level].size();
    }
}
