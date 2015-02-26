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
import java.io.*;

public class Core {
  protected static PrintWriter out = new PrintWriter(System.out);

  public static boolean debug = SystemProps.getState("debug.on",false);
  public static int     level = SystemProps.getInt("debug.level",1);

  public static final void setDebuggerOutputFile(String dout) {
     try {
        if ( dout != null )
	   out = new PrintWriter(new FileWriter(dout));
     }
     catch(IOException e) {
        out = new PrintWriter(System.out);
     }
  }


  public static void FAIL(String s) {
    System.err.println(s);
    if ( debug ) Assert.notNull(null);
  }
  public static final void DEBUG(int level, Object any) {
     if ( debug && level <= Core.level ) debug(level,any);
  }
  protected static final void debug(int level, Object any) {
     if ( (any.getClass()).isArray() ) {
        Object[] array = (Object[])any;
        out.print("DEBUG " + level + " [");
        for(int i = 0; i < array.length; i++ ) {
           out.print(array[i]);
           if ( i < array.length-1 )
              out.print(",");
        }
        out.println("]");
     }
     else {
        out.println("DEBUG " + level + " " + any);
     }
     out.flush();
  }
  public static void ERROR(Object object, int num, Object source) {
     if ( object == null )
        error(num,source);
  }
  public static void ERROR(boolean state, int num, Object source) {
     if ( !state )
        error(num,source);
  }
  protected static void error(int num, Object source) {
     if ( source instanceof String )
        System.err.println("INTERNAL ERROR: " + source + "#" + num);
     else {
        String src = source.getClass().getName();
        System.err.println("INTERNAL ERROR in " + src + "#" + num);
     }
  }
  public static void USER_ERROR(String s) {
     System.err.println("USER ERROR: " + s);
     if ( debug ) Assert.notNull(null);
  }
}
