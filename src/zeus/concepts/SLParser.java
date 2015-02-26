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
* public limited company are Copyright 1996-2001. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/

package zeus.concepts;

import sl.*;
import java.util.*;
import java.io.*;
import JADE_SL.abs.*;
import JADE_SL.*;
import JADE_SL.lang.sl.*;


/**
        zeus.concepts.SLParser wraps the SLParsers in Zeus, the parsers themselves are from the team at
        the University of Parma and TILabs, so thanks for that guys! Please see the
        LGPL license that the parser classes in sl && JADE_SL are under.
        <p>
        To use this you need to call the parse method with the content that you are
        parsing
        */
public class SLParser {

        /** here is the method that you must call!
        */
    public static AbsContentElement  parse (String s) {
        return parsefull (s);
        }
    
 
    public static AbsContentElement parsefull(String s) {
	       SLCodec codec = new SLCodec ();
                try {
                	AbsContentElement content = codec.decode (SLOntology.getInstance(),s);
                	return content;
                } catch (Exception e) {
                	e.printStackTrace();
                }
                catch (Throwable tr) {
                	tr.printStackTrace();
                }
                return (null); 
        }


    public static List parse0 (String s) { 
      try {
        s= s.trim();
        SL0Parser parser = new SL0Parser (new ByteArrayInputStream(s.getBytes())); 
        
        
        List ret = parser.parse(s);  } 
        catch (Exception e) {
            e.printStackTrace(); 
        }
        return null;
    }
    
}