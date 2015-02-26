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
public class  SellAction extends ReteAction{
    
    /**
        A rete action that gets the agent to sell. 
        */
    public void executeAction (Action a, Info info) { 
    ReteFact token = (ReteFact)a.head;
              OntologyDb ont = getOntologyDb(); 
              Fact f1 = ont.getFact(Fact.VARIABLE,token.getType());
              Enumeration enum = token.data.keys();
              while( enum.hasMoreElements() ) {
                 String attribute = (String)enum.nextElement();
                 ValueFunction value = (ValueFunction)token.data.get(attribute);
                 f1.setValue(attribute,value);
              }

              if ( !f1.resolve(info.getBindings()) ) {
                 Core.USER_ERROR("Improperly specified fact in Rete action achieve/sell/buy: " + token);
                 return;
              }
              ValueFunction end_time = (ValueFunction)a.table.get("end_time");
              if ( end_time == null || !end_time.isDeterminate() ) {
                 Core.USER_ERROR("End_time not specified in Rete action achieve/sell/buy: " + token);
                 return;
              }
              ValueFunction confirm_time = (ValueFunction)a.table.get("confirm_time");
              if ( confirm_time == null || !confirm_time.isDeterminate()  ) {
                 Core.USER_ERROR("Confirm_time not specified in Rete action achieve/sell/buy: " + token);
                 return;
              }

              if ( context == null ) {
                    Core.USER_ERROR("attempt to use goal orientated behaviour without agent"); 
                    return;}
              String gid = context.newId("goal");
              double now = context.now();
              int et = (int)(now + ((PrimitiveNumericFn)end_time).intValue());
              double ct = now + ((PrimitiveNumericFn)confirm_time).doubleValue();

              Goal g = new Goal(gid,f1,et,0,context.whoami(),ct);
              ValueFunction cost = (ValueFunction)a.table.get("cost");
              if ( cost != null )
                 g.setCost(((RealFn)cost).getValue());

              Core.DEBUG(2," ==> " + g);
              context.Engine().sell(g);
               
    }              
    
    
    
}