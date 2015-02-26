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

import java.awt.*;
import java.util.*;
import java.io.*;

/** 
  * Misc is used to do "things" to messages
  * Simon updated escape/unescape to allow serialised objects to be sent between agents
  * since 1.2.1
  */
public class Misc {
   static final String DELIMITERS = "\n\r\t !%^&*()-+={[]}~,.:;#|\\";
   public static final String OPAQUE_CHAR = "#";
   public static final String QUOTE = "\"";
   
   
   public static final String literalToString(String s) {
      if ( s.startsWith(QUOTE) && s.endsWith(QUOTE) )
         return unescape(s.substring(1,s.length()-1));
      else
         return unescape(s);
   }
   
   
   public static final String opaqueToString(String s) {
      return s.substring(1,s.length()-1);
   }


   public static final String escape(String str) {
      String retval = new String();
      char ch;
      for (int i = 0; i < str.length(); i++) {
    //    debug ("str.charAt("+ String.valueOf(i) +") = :" + str.charAt(i) + ":"); 
        switch (str.charAt(i)) {

           case 0 :
               debug ("0 !");
               retval+=((char) 0); 
              continue;
           case '\b':
              retval+=("\\b");
              continue;
           case '\t':
              retval+=("\\t");
              continue;
           case '\n':
              retval+=("\\n");
              continue;
           case '\f':
              retval+=("\\f");
              continue;
           case '\r':
              retval+=("\\r");
              continue;
           case '\"':
              retval+=("\\\"");
              continue;
           case '\'':
              retval+=("\\\'");
              continue;
           case '\\':
              retval+=("\\\\");
              continue;
          /* case ' ':
              retval.append (" "); 
              debug (" space" ); 
              continue;*/
           default:
              if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
                 String s =  new String();
                 s+=("0000");
                 s+=Integer.toString(ch, 16);
                 retval+=("\\u"); 
                 retval+=(s.substring(s.length() - 4, s.length()));
              } else {
                 retval+=(ch);
              }
              continue;
        }
      }
        return retval; 
   }

   public static final String unescape(String str) {
      String retval = new String();
      char ch;
      for(int i = 0; i < str.length(); ) {
         ch = str.charAt(i++);
         if ( ch != '\\' )
            retval+=(ch);
         else { 
           ch = str.charAt(i++);
           switch(ch) {
           case 0 :
              continue;
           case 'b':
              retval+=("\b");
              continue;
           case 't':
              retval+=("\t");
              continue;
           case 'n':
              retval+=("\n");
              continue;
           case 'f':
              retval+=("\f");
              continue;
           case 'r':
              retval+=("\r");
              continue;
           case '"':
              retval+=("\"");
              continue;
           case '\'':
              retval+=("\'");
              continue;
           case '\\':
              retval+=("\\");
              continue;
	   case ' ': 
              retval+= (" ");  // this clause readded to deal with FIPA-OS, but does object deserialisation get effected. 
              continue; 
           case 'u' : 
               String thisChar = str.substring (i,i+4); 
               debug ("thisChar = " + thisChar); 
               i = i +4; 
               int val = Integer.valueOf(thisChar,16).intValue(); 
               debug ("val = " + String.valueOf(val)); 
               char appender = (char) val; 
               debug ("appender = " + appender); 
               retval += (appender);
               
              // read the next four characters.
              // feed them to char to make a nice new one. 
              // Character forDigit (int , 16) 
              // Integer valueOf (str,16)
              continue; 
           default:
              // Don't know leave as is
              retval+=("\\");
              retval+=(ch);
              continue;
           }
         }
      }
      
      return retval; 
      
   }


   public static final String relativePath(String file) {
      if ( file == null ) return null;
      return relativePath(System.getProperty("user.dir"),file);
   }
   
   
   public static final String relativePath(String dir, String file) {
      if ( file == null ) return null;
      return relativePath(dir,new File(file));
   }
   
   
   public static final String relativePath(String dir, File file) {
      if ( file == null ) return null;

      if ( dir == null )
         dir = System.getProperty("user.dir");

      File d = new File(dir);
      try {
          dir = d.getCanonicalPath();
      }
      catch(IOException e) {
         dir = d.getAbsolutePath();
      }

      String filename;
      try {
         filename = file.getCanonicalPath();
      }
      catch(IOException e) {
         filename = file.getAbsolutePath();
      }
      File f;

      int count = 0;
      while( dir != null ) {
        if ( filename.startsWith(dir) && !dir.equals(File.separator) ) {
           String relpath = "";
           for(int i = 0; i < count; i++ ) {
              relpath += "..";
              if ( i+1 < count )
                 relpath += File.separator;
           }
           relpath += filename.substring(dir.length());
           if ( relpath.startsWith(File.separator) )
              return "." + relpath;
           else if ( relpath.equals("") )
              return ".";
           else
              return relpath;
        }
        count++;
        f = new File(dir);
        dir = f.getParent();
      }
      if ( filename.equals("") )
         return ".";
      else
         return filename;
   }

   public static final String spaces(int sp ) {
      String tabs = "";
      for( int i = 0; i < sp; i++ ) tabs += " ";
      return tabs;
   }
   
   
   public static final String concat(String prefix, int[] data) {
      for(int i = 0; data != null && i < data.length; i++ )
         prefix += " " + data[i];
      return prefix;
   }
   
   
   public static final String concat(int[] data) {
      String prefix = "";
      for( int i = 0; data != null && i < data.length; i++ )
         prefix += data[i] + " ";
      prefix = prefix.trim(); 
      return prefix;
   }
   
   
   public static final String concat(String prefix, double[] data) {
      for(int i = 0; data != null && i < data.length; i++ )
         prefix += " " + data[i];
      return prefix;
   }
   
   
   public static final String concat(double[] data) {
      String prefix = "";
      for( int i = 0; data != null && i < data.length; i++ )
         prefix += data[i] + " ";
      return prefix.trim();
   }


   public static final String concat(String prefix, Object[] data) {
      for(int i = 0; data != null && i < data.length; i++ )
         prefix += " " + data[i];
      return prefix;
   }
   
   
   public static final String concat(Object[] data) {
      String prefix = "";
      for( int i = 0; data != null && i < data.length; i++ )
         prefix += data[i] + " ";
      prefix = prefix.trim(); 
      return prefix;
   }
   
   
   public static final String concat(String prefix, Vector data) {
      for(int i = 0; data != null && i < data.size(); i++ )
         prefix += " " + data.elementAt(i);
      return prefix;
   }
   
   
   public static final String concat(Vector data) {
      String prefix = new String ();
      for( int i = 0; data != null && i < data.size(); i++ )
         prefix += data.elementAt(i) + " ";
      prefix = prefix.trim();
      return prefix; 
   }
   
   
   public static final String concat(HSet data) {
      String prefix = new String();
      Enumeration enum = data.elements();
      while( enum.hasMoreElements() )
         prefix += enum.nextElement() + " ";
      prefix= prefix.trim();
      return prefix; 
   }

   public static final String substitute(String in, String search, String replace) {
      StringTokenizer st = new StringTokenizer(in,DELIMITERS,true);
      String token;
      String s = "";
      while( st.hasMoreTokens() ) {
         token = st.nextToken();
         if ( token.equals(search) )
            s += replace;
         else
            s += token;
      }
      return s;
   }


   public static final boolean member(int item, int[] List) {
      if ( List == null ) return false;
      for(int j = 0; j < List.length; j++ )
         if ( item == List[j] ) return true;
      return false;
   }
   
   
   public static final boolean member(String item, String[] List) {
      if ( List == null ) return false;
      for(int j = 0; j < List.length; j++ )
         if ( item.equals(List[j]) ) return true;
      return false;
   }


   public static final Vector intersection(Vector left, Vector right) {
      Vector result = new Vector();
      Object item;
      for(int j = 0; j < left.size(); j++ ) {
         item = left.elementAt(j);
         if ( !result.contains(item) && right.contains(item) )
            result.addElement(item);
      }
      return result;
   }
   
   
   public static final Vector union(Vector left, Vector right) {
      Vector result = new Vector();      
      Object item;
      for(int j = 0; j < left.size(); j++ ) {
         item = left.elementAt(j);
         if ( !result.contains(item) )
            result.addElement(item);
      }
      for(int j = 0; j < right.size(); j++ ) {
         item = right.elementAt(j);
         if ( !result.contains(item) )
            result.addElement(item);
      }
      return result;
   }
   
   
   public static final Vector difference(Vector left, Vector right) {
      Vector result = new Vector();
      Object item;
      for(int j = 0; j < left.size(); j++ ) {
         item = left.elementAt(j);
         if ( !result.contains(item) && !right.contains(item) )
            result.addElement(item);
      }
      return result;
   }
   
   
   public static final boolean isSubset(Vector subset, Vector superset) {
      Object item;
      for(int j = 0; j < subset.size(); j++ ) {
         item = subset.elementAt(j);
         if ( !superset.contains(item) )
            return false;
      }
      return true;
   }

   public static final boolean sameVector(Vector left, Vector right) {
      if ( left.size() != right.size() ) return false;

      Vector diff = difference(left,right);
      if ( !diff.isEmpty() ) return false;

      diff = difference(right,left);
      return diff.isEmpty();
   }


   public static final boolean isNumber(String s) {
      return isLong(s) || isDouble(s);
   }

   
   public static final boolean isLong(String s) {
      try {
         long x = Long.parseLong(s);
         return true;
      }
      catch(NumberFormatException e) {
         return false;
      }
   }
   
   
   public static final boolean isDouble(String s) {
      try {
         double x = Double.parseDouble(s);
         return true;
      }
      catch(NumberFormatException e) {
         return false;
      }
   }


   public static final void sort(String[] data) {
      if ( data == null ) return;
      boolean swapped = true;
      while( swapped ) {
         swapped = false;
         for( int i = 0; i < data.length-1; i++ )
            if ( data[i].compareTo(data[i+1]) > 0 ) {
               String tmp = data[i];
               data[i] = data[i+1];
               data[i+1] = tmp;
               swapped = true;
            }
      }
   }


   public static final void sort(Vector data) {
      if ( data == null ) return;
      if ( data.isEmpty() ) return;
      if ( !( data.elementAt(0) instanceof String) ) return;

      boolean swapped = true;
      while( swapped ) {
         swapped = false;
         for( int i = 0; i < data.size()-1; i++ )
            if ( ((String)data.elementAt(i)).compareTo(
                 (String)data.elementAt(i+1)) > 0 ) {
               Object tmp = data.elementAt(i);
               data.setElementAt(data.elementAt(i+1),i);
               data.setElementAt(tmp,i+1);
               swapped = true;
            }
      }
   }


   public static final int whichPosition(String data, String[] items) {
      if ( data != null && items != null )
         for( int i = 0; i < items.length; i++ ) {
            if ( data.equals(items[i]) ) return i;
         }
      return -1;
   }
   
   
   public static final int whichPosition(String data, Vector items) {
      if ( data != null && items != null )
         for( int i = 0; i < items.size(); i++ ) {
            if ( data.equals((String)items.elementAt(i)) ) return i;
         }
      return -1;
   }


   public static final Vector flatten(Vector data) {
      Vector result = new Vector();
      if ( data == null || data.isEmpty() ) return result;
      Object obj;
      for( int i = 0; i < data.size(); i++ ) {
         obj = data.elementAt(i);
         if ( obj instanceof Vector ) {
            Vector v = flatten((Vector)obj);
            for( int j = 0; j < v.size(); j++ )
               result.addElement(v.elementAt(j));
         }
         else
            result.addElement(obj);
      }
      return result;
   }


   public static final Vector copyVector(Vector data) {
      if ( data == null ) return null;
      Vector result = new Vector();
      if ( data.isEmpty() ) return result;
      Object obj;
      for( int i = 0; i < data.size(); i++ ) {
         obj = data.elementAt(i);
         if ( obj instanceof Vector ) {
            Vector v = copyVector((Vector)obj);
            result.addElement(v);
         }
         else
            result.addElement(obj);
      }
      return result;
   }


   public static final String[] stringArray(String s) {
      StringTokenizer st = new StringTokenizer(s);
      Vector data = new Vector();
      while( st.hasMoreTokens() )
         data.addElement(st.nextToken());
      return stringArray(data);
   }
   
   
   public static final String[] stringArray(Object[] data) {
      if ( data == null ) return new String[0];
      String[] result = new String[data.length];
      for( int i = 0; i < data.length; i++ )
         result[i] = data[i].toString();
      return result;
   }
   
   
   public static final String[] stringArray(Vector data) {
      if ( data == null ) return new String[0];
      String[] result = new String[data.size()];
      for( int i = 0; i < data.size(); i++ )
         result[i] = (String)data.elementAt(i);
      return result;
   }
   
   
   public static final Vector stringVector(String[] data) {
      Vector result = new Vector();
      if ( data != null )
         for( int i = 0; i < data.length; i++ )
            result.addElement(data[i]);
      return result;
   }


   public static final String decimalPlaces(double x, int num) {
      return decimalPlaces(Double.toString(x),num);
   }
   
   
   public static final String decimalPlaces(String str,int num) {
      String exp = "";
      int index = str.indexOf("E");
      if ( index != -1 ) {
         exp = str.substring(index);
      }
      index = str.indexOf(".");
      if ( index == -1 ) {
         String cstr = str + ".";
         for( int i = 0; i < num; i++ )
            cstr += "0";
         return cstr+exp;
      }
      else {
         int len = str.length();
         if ( len > index + num )
            return str.substring(0,index+num+1)+exp;
         else {
            String cstr = str;
            for( int i = 0; i < num+index+1-len; i++ )
               cstr += "0";
            return cstr+exp;
         }
      }
   }
   
   
   /**
     * Serialize object to a String.
     * This can be used to transform an object into a string that can then be sent 
     * as content in a performative message. Note: the string must be turned back into 
     * an object using zeus.util.Misc.contentToObject(String)
     * modified by Simon. Static since 1.2.2
     * @author      John Shepherdson
     * @date        04/05/2001.
     * @param       The object to be serialised (must me serialisable!)
     * @return      String containing the agent.
     * @exception   IOException
     * @see         java.io.ByteArrayOutputStream, java.io.ObjectOutputStream
     * @since 1.2.1
     */
    public static final String objectToContent (Serializable serialised) throws java.io.IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream stream = new ObjectOutputStream(out);
      stream.writeObject(serialised);
      stream.flush();
      stream.close ();
      String retVal = out.toString(); 
      retVal = escape(retVal); 
      return retVal;
    }
    
    
    
    /**
     * De-serialize from a String that contains an escaped serialised object to a reference to 
     * the object. Note, you must have a copy of the class that this is an instance of in 
     * your path, or you will get an exception. 
     * Modified by Simon to return an Object
     *
     * @author      John Shepherdson
     * @date        04/05/2001.
     * @param       String containing the agent.
     * @return      Instance of Object
     * @exception   ClassNotFoundException, StreamCorruptedException, IOException
     * @see         java.io.ByteArrayInputStream, java.io.ObjectInputStream
     * @since 1.2.1
     */
    public static final Object contentToObject (String contentString)
      throws java.lang.ClassNotFoundException,
      java.io.StreamCorruptedException, java.io.IOException
      {
        contentString = unescape(contentString); 
        byte [] buf = contentString.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        ObjectInputStream stream = new ObjectInputStream(in);
        Object agent = stream.readObject();
        return agent;
    }
    
   
   public static final void debug (String str) { 
   // System.out.println("Misc>> " + str); 
   }


   public static final boolean isZero(double x) {
      return Math.abs(x) < 1.0E-12;
   }
}
