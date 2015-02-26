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
public class  OpenAction extends ReteAction{
    
    /**
   
        */
    public void executeAction (Action a, Info info) { 
              ValueFunction var = ((ValueFunction)a.head).resolve(info.getBindings());
              if ( var == null || var.getID() != ValueFunction.ID ) {
                 Core.USER_ERROR("Cannot resolve '" + a.head + "' in action " + a);
                 return;
              }
              ValueFunction value = ((ValueFunction)a.sub_head).resolve(info.getBindings());
              if ( value == null || value.getID() != ValueFunction.ID ) {
                 Core.USER_ERROR("Cannot resolve '" + a.sub_head + "' in action " + a);
                 return;
              }
              ValueFunction value1 = ((ValueFunction)a.sub_sub_head).resolve(info.getBindings());
              if ( value == null || value.getID() != ValueFunction.ID ) {
                 Core.USER_ERROR("Cannot resolve '" + a.sub_sub_head + "' in action " + a);
                 return;
              }

              String logicalName = ((IdFn)var).getValue();
              String filename = ((IdFn)value).getValue();
              String mode = ((IdFn)value1).getValue();

              if ( mode.equalsIgnoreCase("r") )
                 conflictHandler.createReader(logicalName,filename);
              else if ( mode.equalsIgnoreCase("w") )
                 conflictHandler.createWriter(logicalName,filename,false);
              else if ( mode.equalsIgnoreCase("wa") || mode.equalsIgnoreCase("a") )
                 conflictHandler.createWriter(logicalName,filename,true);
              else
                 Core.USER_ERROR("Unknown mode '" + mode + "' in action " + a);
    }
    
    
}