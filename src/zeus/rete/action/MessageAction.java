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
public class  MessageAction extends ReteAction{
    
    /**
        Send a message
        */
    public void executeAction (Action a, Info info) { 
            //  System.out.println("1"); 
              ValueFunction msg_type = (ValueFunction)a.table.get("type");
              if ( msg_type == null ) {
                 Core.USER_ERROR("Type not specified in Rete action send_message: " + a);
                 return; 
              }
              //System.out.println("2"); 
              
              ValueFunction receiver = (ValueFunction)a.table.get("receiver");
              if ( receiver == null ) {
                 Core.USER_ERROR("Receiver not specified in Rete action send_message: " + a);
                 return; 
              }
              //System.out.println("3"); 
              
              ValueFunction content = (ValueFunction)a.table.get("content");
              if ( receiver == null ) {
                 Core.USER_ERROR("Content not specified in Rete action send_message: " + a);
                 return; 
              }
              //System.out.println("4"); 
              

              msg_type = msg_type.resolve(info.getBindings());
              Performative msg = new Performative(msg_type.toString());
              Enumeration enum = a.table.keys();
              //System.out.println("5");
              
              while( enum.hasMoreElements() ) {
                 String attribute = (String)enum.nextElement();
                 ValueFunction value = (ValueFunction)a.table.get(attribute);

                 ValueFunction type_var = value.resolve(info.getBindings());
                 boolean found = false;
                 Fact f1 = null;
                 if (type_var.getID() == ValueFunction.TYPE) {
                    for(int k = 0; !found && k < info.getInput().size(); k++ ) {
                       f1 = (Fact)info.getInput().elementAt(k);
                       found = (f1.functor()).equals(type_var);
                    }
                 }
              //System.out.println("6"); 
                 
                 Object result = null; 
                 if (found) 
                        result = (Object) f1;
                    else 
                        result = (Object) type_var; 
              //System.out.println("7"); 
                        
                 /** 
                    old version 
                 Object result = found ? (Object)f1 : (Object)type_var;
                 */
                 if ( attribute.equals("content") )
		            msg.setAttribute(attribute,"data " + result);
                 else
		            msg.setAttribute(attribute,result.toString());
              }
              //System.out.println("8"); 
              
              Core.DEBUG(2," ==> " + msg);
              if ( context != null && context.MailBox() != null )
		        context.MailBox().sendMsg(msg);
              //System.out.println("9"); 
              //System.out.println(msg.toString());
              
		        
    }
    
    
}