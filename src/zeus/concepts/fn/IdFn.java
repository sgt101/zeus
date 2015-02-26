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

public class IdFn extends ValueFunction implements PrimitiveFn {
   protected String arg = null;

   public IdFn(String arg) {
      super(ID,10);
      this.arg = arg;
   }
   public ValueFunction mirror() {
      return new IdFn(arg);
   }
   public String toString() {
      char[] array = arg.toCharArray();
      for(int i = 0; i < array.length; i++ ) {
         if ( !Character.isLetterOrDigit(array[i]) &&
              array[i] != '_' && array[i] != '$' )
            return "\"" + arg + "\"";
      }
      return arg;
   }
   Object getArg(int position) {
       if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return Misc.literalToString(arg);
   }
   public boolean isDeterminate() {
      return true;
   }
   public String getValue() {
      return Misc.literalToString(arg);
   }

   public boolean equals(Object any) {
      if ( !(any instanceof IdFn) ) return false;
      IdFn fn = (IdFn)any;
      return arg.equals(fn.getArg());
   }
   public boolean less(Object any) {
      if ( !(any instanceof IdFn) ) return false;
      IdFn fn = (IdFn)any;
      return ( arg.compareTo((String)fn.getArg()) < 0);
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      return null;
   }
   public boolean references(ValueFunction var) {
      return false;
   }

   /* 
      List of methods associated with this ValueFunction type
   */

   static final String[] METHOD_LIST = {
      /* char    */ "charAt",              "1",   /* (int index) */
      /* int     */ "compareTo",           "1",   /* (String anotherString) */ 
      /* int     */ "compareToIgnoreCase", "1",   /* (String str) */ 
      /* String  */ "concat",              "1",   /* (String str) */ 
      /* boolean */ "endsWith",            "1",   /* (String suffix) */ 
      /* boolean */ "equals",              "1",   /* (Object anObject) */ 
      /* boolean */ "equalsIgnoreCase",    "1",   /* (String anotherString) */ 
      /* int     */ "indexOf",             "1,2", /* (int str); (int str, int fromIndex);
      /* int     */ "lastIndexOf",         "1,2", /* (int str); (String str, int fromIndex) */ 
      /* int     */ "length",              "0",   /* () */ 
      /* boolean */ "regionMatches",       "4,5", /* (boolean ignoreCase, int toffset, String other, int ooffset, int len) */ 
                                                  /* (int toffset, String other, int ooffset, int len) */ 
      /* String  */ "replace",             "2",   /* (char oldChar, char newChar) */ 
      /* boolean */ "startsWith",          "1,2", /* (String prefix); (String prefix, int toffset) */ 
      /* String  */ "substring",           "1,2", /* (int beginIndex); (int beginIndex, int endIndex) */ 
      /* String  */ "toLowerCase",         "0",   /* () */ 
      /* String  */ "toUpperCase",         "0",   /* () */ 
      /* String  */ "trim",                "0"    /* () */ 
   };

   static final int CHAR_AT 			= 0;
   static final int COMPARE_TO 			= 2;
   static final int COMPARE_TO_IGNORE_CASE	= 4;
   static final int CONCAT 			= 6;
   static final int ENDS_WITH 			= 8;
   static final int EQUALS 			= 10;
   static final int EQUALS_IGNORE_CASE 		= 12;
   static final int INDEX_OF 			= 14;
   static final int LAST_INDEX_OF 		= 16;
   static final int LENGTH 			= 18;
   static final int REGION_MATCHES 		= 20;
   static final int REPLACE 			= 22;
   static final int STARTS_WITH 		= 24;
   static final int SUBSTRING 			= 26;
   static final int TO_LOWER_CASE 		= 28;
   static final int TO_UPPER_CASE 		= 30;
   static final int TRIM 			= 32;

   ValueFunction invokeMethod(String method, Vector arguments) {
      int position = Misc.whichPosition(method,METHOD_LIST);

      if ( position == -1 )
         return super.invokeMethod(method,arguments);

      StringTokenizer st = new StringTokenizer(METHOD_LIST[position+1],",");
      boolean num_args_ok = false;
      int arity = -1;
      while( !num_args_ok && st.hasMoreTokens() ) {
         arity = Integer.parseInt(st.nextToken());
         num_args_ok = (arguments.size() == arity);
      }

      if ( !num_args_ok )
         throw new IllegalArgumentException(
            "Wrong number of arguments in method IdFn.\'" + method + "/" +
             arity + "\'.");

       try {

         IntFn index1, index2, index3;
         ValueFunction value;
         IdFn str1, str2;
         BoolFn state1;
         String string;

         switch( position ) {
            case CHAR_AT:
                 index1 = (IntFn)arguments.elementAt(0); 
                 char ch = arg.charAt(index1.intValue());
                 return new IdFn(String.valueOf(ch));

            case COMPARE_TO:
                 value = (ValueFunction)arguments.elementAt(0);
                 string =  Misc.literalToString(value.toString());
                 return new IntFn(arg.compareTo(string));

            case COMPARE_TO_IGNORE_CASE:
                 value = (ValueFunction)arguments.elementAt(0);
                 string =  Misc.literalToString(value.toString());
                 return new IntFn(arg.compareToIgnoreCase(string));

            case CONCAT:
                 value = (ValueFunction)arguments.elementAt(0);
                 string =  Misc.literalToString(value.toString());
/*
                 String xx  = arg.concat(value.toString());
System.err.println("XX = " + xx );
                 return new IdFn( xx );
*/
                 return new IdFn(arg.concat(string));

            case ENDS_WITH:
                 str1 = (IdFn)arguments.elementAt(0);
                 return BoolFn.newBoolFn( arg.endsWith(str1.getValue()) );

            case EQUALS:
                 str1 = (IdFn)arguments.elementAt(0);
                 return arg.equals(str1.getValue()) ? BoolFn.trueFn : BoolFn.falseFn;

            case EQUALS_IGNORE_CASE:
                 str1 = (IdFn)arguments.elementAt(0);
                 return arg.equalsIgnoreCase(str1.getValue()) ? BoolFn.trueFn : BoolFn.falseFn;

            case INDEX_OF:
                 switch( arity ) {
                    case 1:
                       str1 = (IdFn)arguments.elementAt(0);
                       return new IntFn(arg.indexOf(str1.getValue()));

                    case 2:
                       str1 = (IdFn)arguments.elementAt(0);
                       index1 = (IntFn)arguments.elementAt(1);
                       return new IntFn(arg.indexOf(str1.getValue(),index1.intValue()));
                 }
                 break;

            case LAST_INDEX_OF:
                 switch( arity ) {
                    case 1:
                       str1 = (IdFn)arguments.elementAt(0);
                       return new IntFn(arg.lastIndexOf(str1.getValue()));

                    case 2:
                       str1 = (IdFn)arguments.elementAt(0);
                       index1 = (IntFn)arguments.elementAt(1);
                       return new IntFn(arg.lastIndexOf(str1.getValue(),index1.intValue()));
                 }
                 break;

            case LENGTH:
                 return new IntFn(arg.length());

            case REGION_MATCHES:
                 switch( arity ) {
                    case 4:
                       index1 = (IntFn)arguments.elementAt(0);
                       str1 = (IdFn)arguments.elementAt(1);
                       index2 = (IntFn)arguments.elementAt(2);
                       index3 = (IntFn)arguments.elementAt(3);
                       return BoolFn.newBoolFn( arg.regionMatches(index1.intValue(),
                                                                  str1.getValue(),
                                                                  index2.intValue(),
                                                                  index3.intValue()) 
                                              );

                    case 5:
                       state1 = (BoolFn)arguments.elementAt(0);
                       index1 = (IntFn)arguments.elementAt(1);
                       str1 = (IdFn)arguments.elementAt(2);
                       index2 = (IntFn)arguments.elementAt(3);
                       index3 = (IntFn)arguments.elementAt(4);
                       return BoolFn.newBoolFn( arg.regionMatches(state1.getValue(),
                                                                  index1.intValue(),
                                                                  str1.getValue(),
                                                                  index2.intValue(),
                                                                  index3.intValue()) 
                                              );
                 }
                 break;

            case REPLACE:
                 str1 = (IdFn)arguments.elementAt(0);
                 str2 = (IdFn)arguments.elementAt(1);
                 return new IdFn(arg.replace((str1.getValue()).charAt(0),(str2.getValue()).charAt(0)));

            case STARTS_WITH:
                 switch( arity ) {
                    case 1:
                       str1 = (IdFn)arguments.elementAt(0);
                       return BoolFn.newBoolFn(arg.startsWith(str1.getValue()));

                    case 2:
                       str1 = (IdFn)arguments.elementAt(0);
                       index1 = (IntFn)arguments.elementAt(1);
                       return BoolFn.newBoolFn(arg.startsWith(str1.getValue(),index1.intValue()));
                 }
                 break;

            case SUBSTRING:
                 switch( arity ) {
                    case 1:
                       index1 = (IntFn)arguments.elementAt(0);
                       return new IdFn(arg.substring(index1.intValue()));

                    case 2:
                       index1 = (IntFn)arguments.elementAt(0);
                       index2 = (IntFn)arguments.elementAt(1);
                       return new IdFn(arg.substring(index1.intValue(),index2.intValue()));
                 }
                 break;

            case TO_LOWER_CASE:
                 return new IdFn(arg.toLowerCase());

            case TO_UPPER_CASE:
                 return new IdFn(arg.toUpperCase());

            case TRIM:
                 return new IdFn(arg.trim());
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
