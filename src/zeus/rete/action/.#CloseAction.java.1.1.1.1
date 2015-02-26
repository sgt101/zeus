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
import zeus.concepts.fn.*; 
import zeus.concepts.*;
import zeus.util.*;
import java.util.*;
import java.io.*;

/** 
    
    @author Simon Thompson
    @since 1.1
    */
public class  CloseAction extends ReteAction{
    
    /**
        execute an action that closes a stream
        */
    public void executeAction (Action a, Info info) { 
              ValueFunction var = ((ValueFunction)a.head).resolve(info.getBindings());
              if ( var == null || var.getID() != ValueFunction.ID ) {
                 Core.USER_ERROR("Cannot resolve reader/writer '" + a.head + "' in action " + a);
                 return;
              }
              String name = ((IdFn)var).getValue();

	          PrintWriter out = (PrintWriter)conflictHandler.getWriters().remove(name);
              if ( out != null ) {
                 out.flush();
                 out.close();
                 return;
              }

              DataReader in = (DataReader)conflictHandler.getReaders().remove(name);
              if ( in != null ) {
                 in = null;
                 return; 
              }

              Core.USER_ERROR("Unknown reader/writer '" + name + "' in " + a);
    
    }
    
    
}