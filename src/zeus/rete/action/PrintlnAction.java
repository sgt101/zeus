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


package zeus.rete.action;
import zeus.rete.*;
import zeus.util.*; 
import zeus.concepts.*;
import zeus.concepts.fn.*;
import java.util.*;
import java.io.*;
/** 
    
    @author Simon Thompson
    @since 1.1
    */
public class  PrintlnAction extends ReteAction{
    
    /**
   
        */
    public void executeAction (Action a, Info info) { 
              ValueFunction var = ((ValueFunction)a.head).resolve(info.getBindings());
              if ( var.getID() != ValueFunction.ID ) {
                 Core.USER_ERROR("Unresolved writer destination '" + var + "' in action " + a);
                 return;
              }
              Hashtable writers = conflictHandler.getWriters();
              PrintWriter out = (PrintWriter)writers.get(((IdFn)var).getValue());
              if ( out == null ) {
                 Core.USER_ERROR("Unknown writer \'" + var + "' in " + a);
                 return;
              }

              String output = new String("");
              String str = new String(); 
              for(int j = 0; j < a.items.size(); j++ ) {
                 var = (ValueFunction)a.items.elementAt(j);
                 var = var.resolve(info.getBindings());
                 str = var.toString();
                 str = Misc.literalToString(str);
                 output += str;
              }
		         out.println(output);
         
    }
    
    
}