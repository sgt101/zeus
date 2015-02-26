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
import zeus.util.*;

/** 
    
    @author Simon Thompson
    @since 1.1
    */
public class  IfAction extends ReteAction{
    
    /**
        if action rete RHS
        */
    public void executeAction (Action a, Info info) { 
              ValueFunction var = ((ValueFunction)a.head).resolve(info.getBindings());
             /* if ( var == null || var.getID() != ValueFunction.BOOL ) {
                 Core.USER_ERROR("Cannot resolve '" + a.head + "' in action " + a);
                return;
              }*/
              if ( ((BoolFn)var).getValue() ) {
                 for(int i = 0; i < a.items.size(); i++ ) {
                        AbstractActionFactory actionFact = new AbstractActionFactory(); 
                        ActionFactory factory  = actionFact.getActionFactory (); 
                        try {
                            Action b = (Action)a.items.elementAt(i);
                            System.out.println("generating action");
                            BasicAction action = factory.getAction(b.type);
                            action.setActuators(this.conflictHandler,context);
                            System.out.println("Action was : " + action.toString()); 
                            action.executeAction (b,info); }
                            catch (Exception e) { 
                                e.printStackTrace(); }
                        //Action b = (Action)a.items.elementAt(i);
                        //executeAction(b,info);
                 }
              }
              else {
                 for(int i = 0; i < a.sub_items.size(); i++ ) {
                    AbstractActionFactory actionFact = new AbstractActionFactory(); 
                        ActionFactory factory  = actionFact.getActionFactory (); 
                        try {
                            Action b = (Action)a.sub_items.elementAt(i);
                            System.out.println("generating action");
                            BasicAction action = factory.getAction(b.type);
                            action.setActuators(this.conflictHandler,context);
                            System.out.println("Action was : " + action.toString()); 
                            action.executeAction (b,info); }
                            catch (Exception e) { 
                                e.printStackTrace(); }
                    //Action b = (Action)a.sub_items.elementAt(i);
                   //executeAction(b,info);
                 }
              }
    }
    
    
}