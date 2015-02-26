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
import zeus.concepts.fn.*; 
import zeus.concepts.*; 
import zeus.rete.*;
import zeus.actors.*;
/** 
    Another layer of abstraction for the re-architected rete engine action handler. 
    <p>
    To do : <br>
    Abstract (build interfaces for: OntologyDb, ConflictSet, ReteEngine... this will 
    make these actions more portable, and more plugable, I hope. This will also remove 
    the need for the imports of zeus.concepts.fn, zeus.concepts, zeus.rete and zeus.actors.<p> 
    @author Simon Thompson
    @since 1.1
    @see zeus.rete.actions.ReteAction
    */
public interface BasicAction {
    
     public void executeAction (Action a, Info info);
     public OntologyDb getOntologyDb() ;
     public void setActuators (ConflictSet conflictHandler, ReteEngine engine);
     public void setActuators (ConflictSet conflictHandler, AgentContext context);
     
     /** 
      *the action of a rule provides a service 
      *actions that can advertise a service need to have it defined here
      *other actions should return null
      **/
     public String getServiceDescription (String language);
     
}