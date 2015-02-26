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

/** 
    bug fixed 20/06/01 - call to init should initialise data to new string when no 
    tokens are found
    */

public class MsgContentHandler {
  protected String data = null;
  protected String tag = null;

  public MsgContentHandler(String s ) {
    /**
      Split the input string into tokens delimited by spaces,
      i.e. "\n\t\r "
    */

    Assert.notNull(s);
    
    StringTokenizer st = new StringTokenizer(s," \t\n\r",true);
    
    tag = st.nextToken();
    while( tag.equals(" ")  || tag.equals("\t") || 
	   tag.equals("\n") || tag.equals("\r") )
      tag = st.nextToken();
    
    if ( tag != null ) tag = tag.trim();
    
    if ( st.hasMoreTokens() )
      data = new String();
    
    while( st.hasMoreTokens() ) 
      data += st.nextToken();
    
    if ( data != null ) data = data.trim();
    // fix below... 
    if (data == null ) data = new String(); 
  }

  public String tag()  {
     /**
        return first token
     */
     return tag;
  }
  public String data() {
     /** 
        return the remainder of the input (i.e. input -tag)
     */
     return data;
  }

  public String data(int position) {
     /** 
         return the token at position pos
         where position zero refers to the first token after the tag.
         For example: given input = "x y z, u, v w"

         tag()   returns "x"
         data(0) returns "y"
         data(1) returns "z"
         rest(0) returns "z, u, v w"
         rest(1) returns "u, v w"

     */
     try {
        StringTokenizer st = new StringTokenizer(data," \t\n\r");
        for(int i = 0; i < position && st.hasMoreTokens(); i++ )
           st.nextToken();
        return st.nextToken();
     }
     catch(NoSuchElementException e) {
        return null;
     }
  }
  
  
  public String rest(int position) {
     try {
        StringTokenizer st = new StringTokenizer(data," \t\n\r");
        for(int i = 0; i <= position && st.hasMoreTokens(); i++ )
           st.nextToken();
          
        String result = st.nextToken();
        while( st.hasMoreTokens() )
           result += " " + st.nextToken();
        return result;
     }
     catch(NoSuchElementException e) {
        return null;
     }
  }
  
  
  public static void main(String[] args) {
     String input = "x y z, u, v w";
     MsgContentHandler hd = new MsgContentHandler(input);
     System.err.println("Input = " + input);
     System.err.println("Tag = " + hd.tag());
     System.err.println("data(0) = " + hd.data(0));
     System.err.println("data(1) = " + hd.data(1));
     System.err.println("data(2) = " + hd.data(2));
     System.err.println("rest(0) = " + hd.rest(0));
     System.err.println("rest(1) = " + hd.rest(1));
     System.err.println("rest(2) = " + hd.rest(2));
  }
}
