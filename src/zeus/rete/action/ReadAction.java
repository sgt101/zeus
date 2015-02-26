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
public class  ReadAction extends ReteAction{
    
    /**
        A rete action that reads a token off the defined DataReader from the 
        conflictSet getReaders() struct. 
        @see java.io.DataReader
        */
    public void executeAction (Action a, Info info) { 
              Hashtable readers = conflictHandler.getReaders();
              ValueFunction var = ((ValueFunction)a.head).resolve(info.getBindings());
              if ( var == null || var.getID() != ValueFunction.ID ) {
                 Core.USER_ERROR("Cannot resolve logical reader name '" + a.head + "' in action " + a);
                 return;
              }
              ValueFunction value = ((ValueFunction)a.sub_head).resolve(info.getBindings());
              if ( value == null ) {
                 Core.USER_ERROR("Cannot resolve '" + a.sub_head + "' in action " + a);
                 return;
              }

              DataReader in = (DataReader)readers.get(((IdFn)var).getValue());
              if ( in == null ) {
                 Core.USER_ERROR("Unknown reader '" + var + "' in " + a);
                 return;
              }
                try {
                 String input = null;
                 input = in.nextToken();
                 Bindings bind = info.getBindings(); 
                    
                 if ( input == null )
                    Core.USER_ERROR("End of file reached while reading '" + var + "'");
                 else if ( Misc.isLong(input) )
                    bind.set(value,new IntFn(input));
                 else if ( Misc.isDouble(input) )
                    bind.set(value,new RealFn(input));
                 else
                    bind.set(value,new IdFn(input)); }
                    catch(IOException e) {
                 Core.USER_ERROR("IOException reading '" + var + "' in action " + a);
              }
    }
    
    
}