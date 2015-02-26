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



package zeus.agentviewer.rete;

import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import zeus.util.*;
import zeus.actors.AgentContext;
import zeus.actors.event.*;
import zeus.rete.ReteEngine;

public class ReteEngineDataModel extends PlainDocument implements ReteEngineMonitor {
   protected Vector data;
   protected ReteEngine engine;

   public ReteEngineDataModel(AgentContext context){
      data = new Vector();
      ReteEngine engine = context.ReteEngine();
      engine.addMonitor(this,
         ReteEngineEvent.ADD_MASK | ReteEngineEvent.DELETE_MASK |
	 ReteEngineEvent.ACTIVATE_MASK | ReteEngineEvent.DEACTIVATE_MASK |
         ReteEngineEvent.FIRE_MASK
      );
   }

   public void removeZeusEventMonitors() {
      engine.removeMonitor(this,
         ReteEngineEvent.ADD_MASK | ReteEngineEvent.DELETE_MASK |
	 ReteEngineEvent.ACTIVATE_MASK | ReteEngineEvent.DEACTIVATE_MASK |
         ReteEngineEvent.FIRE_MASK
      );
   }

   public void reteRuleAddedEvent(ReteEngineEvent event) {
      try {
         insertString(getLength(),event.getDiagnostic() + "\n", null);
      }
      catch(BadLocationException e) {
      }
   }
   public void reteRuleDeletedEvent(ReteEngineEvent event) {
      try {
         insertString(getLength(),event.getDiagnostic() + "\n", null);
      }
      catch(BadLocationException e) {
      }
   }
   public void reteRuleActivatedEvent(ReteEngineEvent event) {
      try {
         insertString(getLength(),event.getDiagnostic() + "\n", null);
      }
      catch(BadLocationException e) {
      }
   }
   public void reteRuleDeactivatedEvent(ReteEngineEvent event) {
      try {
         insertString(getLength(),event.getDiagnostic() + "\n", null);
      }
      catch(BadLocationException e) {
      }
   }
   public void reteRuleFiredEvent(ReteEngineEvent event) {
      try {
         insertString(getLength(),event.getDiagnostic() + "\n", null);
      }
      catch(BadLocationException e) {
      }
   }
}