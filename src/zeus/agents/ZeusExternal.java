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



/*
 * @(#)ZeusExternal.java 1.00
 */

package zeus.agents;

import zeus.actors.AgentContext;

/**
 * This class provides a means to connect an agent to an application specific
 * external interface, (it is typically used to connect GUI front-ends to the
 * agent, but they do not need to be graphical in nature). External interfaces
 * are launched when the agent is started and may persist for the life-time of
 * the agent. <p>
 *
 * Instructions and an example of how to implement and link an external
 * interface to an agent are provided in Section 6 of the Zeus Application
 * Realisation Guide.
 */

public interface ZeusExternal {
   public void exec(AgentContext context);
}
