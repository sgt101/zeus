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
import java.awt.*;

public class Profile {
   protected Hashtable table = new Hashtable();
   protected static final double TINY = 1.0E-8;
  
   class Data { double cost = 0.0;
                int reject = 0;
                int accept = 0;
              };

   public Profile() {
   }
   public double p(double x) {
      String index = Integer.toString((int)x);
      Data data = (Data)table.get(index);
      if ( data == null ) {
         data = new Data();
         table.put(index,data);
      }
      return (data.reject + data.accept == 0) 
             ? 1.0 
             : 1.0*data.accept/((double)(data.reject + data.accept));
   }
   public double c(double x) {
      String index = Integer.toString((int)x);      
      Data data = (Data)table.get(index);
      if ( data == null ) {
         data = new Data();
         table.put(index,data);
      }
      return data.cost;
   }
   public double e(double x) {
      double p = p(x);
      return (p < TINY) ? Double.MAX_VALUE : c(x)/p;
   }
   public void update(double x, double c) {
      String index = Integer.toString((int)x);      
      Data data = (Data)table.get(index);
      data.cost = (data.accept*data.cost+c)/(data.accept+1);
      data.accept += 1;
   }
   public void update(double x) {
      String index = Integer.toString((int)x);      
      Data data = (Data)table.get(index);
      data.reject += 1;
   }
   public String toString() {
      Data data;
      String key;
      Enumeration keys = table.keys();
      String s = "";
      while( keys.hasMoreElements() ) {
        key = (String)keys.nextElement();
        data = (Data)table.get(key);
        if ( Math.abs(data.cost-0.0) > TINY )
           s += "Time:" + key + "\t" +
                "Accept:" + data.accept + "\t" +
                "Reject:" + data.reject + "\t" +
                "Cost:" + Misc.decimalPlaces(data.cost,2) + "\n\t\t";
      }
      return s;
   }

   public static void main(String[] args) {
      Profile pr = new Profile();
      double t = 4.2;
      double c = 125;
      double e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t,c);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      t = 4.8;
      c = 300;
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t,c);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      System.out.println("-----------");
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      System.out.println("-----------");
      pr = new Profile();
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t,c);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      pr.update(t,c);
      e = pr.e(t);
      System.out.println(e + " " + pr);
      
   }
}
