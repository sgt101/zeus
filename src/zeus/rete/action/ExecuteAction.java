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
import zeus.util.Core;

/** 
    ExecuteAction is intended to implement an Execute action part of a rule 
    which is to do with PlanScripts.. although no one knows what it is to do with 
    PlanScripts. Indeed, it seems that PlanScripts are entirely mythical beasts. 
    In any case this class does very little, so not to worry. 
    <p> 
    Still someone will probably write a handler for this sooner or later, why don't you
    be the one?
    @author Simon Thompson
    @since 1.1
    */
public class  ExecuteAction extends ReteAction{
    
    /**
        raises an user error 
        */
    public void executeAction (Action a, Info info) { 
                  Core.USER_ERROR("Execute " + a.head + " - PlanScript execution not yet defined");
    }
    
    
}