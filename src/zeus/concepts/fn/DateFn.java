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



package zeus.concepts.fn;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

public class DateFn extends ValueFunction implements PrimitiveFn {
   protected String arg = null;
   protected Calendar calendar = null;

   public DateFn(String arg) {
      super(DATE,10);
      this.arg = arg;

      int index1 = arg.indexOf('-');
      if ( index1 == -1 )
         index1 = arg.indexOf('/');
      if ( index1 == 1 )
         this.arg = "0" + this.arg;
   }
   public ValueFunction mirror() {
      return new DateFn(arg);
   }
   public String toString() {
      return arg;
   }
   Object getArg(int position) {
      if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return arg;
   }
   public boolean isDeterminate() {
      return true;
   }
   public boolean equals(Object any) {
      if ( !(any instanceof DateFn) ) return false;
      DateFn fn = (DateFn)any;
      Calendar c1 = getCalendar();
      Calendar c2 = fn.getCalendar();
      return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) &&
             c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
             c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
   }
   public boolean less(Object any) {
      DateFn fn = (DateFn)any;
      Calendar c1 = getCalendar();
      Calendar c2 = fn.getCalendar();
      return c1.before(c2);
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      return null;
   }
   public boolean references(ValueFunction var) {
      return false;
   }

   Calendar getCalendar() {
      if ( calendar != null ) return calendar;

      StringTokenizer st = new StringTokenizer(arg,"-/");
      int day = Integer.parseInt(st.nextToken());
      int month = Integer.parseInt(st.nextToken());
      int year = Integer.parseInt(st.nextToken());
      calendar = new GregorianCalendar(year,month-1,day);
      return calendar;
   }

   /*
      List of methods associated with this ValueFunction type
   */

   static final String[] METHOD_LIST = {
      "day", "0",
      "dayOfWeek", "0",
      "month", "0",
      "nameOfMonth", "0",
      "year", "0",
      "interval", "1"
   };

   static final int DAY            = 0;
   static final int DAY_OF_WEEK    = 2;
   static final int MONTH          = 4;
   static final int NAME_OF_MONTH  = 6;
   static final int YEAR           = 8;
   static final int INTERVAL       = 10;

   protected static final String[] DAY_LIST = {
      "Sunday", "Monday", "Tuesday", "Wednesday",
      "Thursday", "Friday", "Saturday"
   };

   protected static final String[] MONTH_LIST = {
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
   };

   ValueFunction invokeMethod(String method, Vector args) {
      int position = Misc.whichPosition(method,METHOD_LIST);

      if ( position == -1 )
         return super.invokeMethod(method,args);

      int arity = Integer.parseInt(METHOD_LIST[position+1]);
      if ( args.size() != arity )
         throw new IllegalArgumentException(
            "Wrong number of arguments in method \'" + method + "/" +
             arity + "\'.");

       try {
         getCalendar();
         switch( position ) {
            case DAY:
                 return new IntFn(calendar.get(Calendar.DAY_OF_MONTH));
   
            case MONTH:
                 return new IntFn(calendar.get(Calendar.MONTH)+1);
   
            case YEAR:
                 return new IntFn(calendar.get(Calendar.YEAR));
   
            case DAY_OF_WEEK:
                 return new IdFn(DAY_LIST[calendar.get(Calendar.DAY_OF_WEEK)-1]);
   
            case NAME_OF_MONTH:
                 return new IdFn(MONTH_LIST[calendar.get(Calendar.MONTH)]);
   
            case INTERVAL:
                 DateFn date = (DateFn)args.elementAt(0);
                 Calendar cal = date.getCalendar();
                 int x = calendar.get(Calendar.DAY_OF_YEAR);
                 int y = cal.get(Calendar.DAY_OF_YEAR);
                 return new IntFn(Math.abs(x-y));
         }
      }
      catch(ClassCastException e) {
         throw new IllegalArgumentException(
            "Type mismatch in method \'" + this + Fact.A_CHR + method +
            "(...)\'");

      }
      Core.ERROR(null,1,this); // should never get here
      return null;
   }

}
