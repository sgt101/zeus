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
import zeus.concepts.*; 
import zeus.concepts.fn.*;
import zeus.util.*;
/** 
    
    @author Simon Thompson
    @since 1.1
    */
public class  RetractAction extends ReteAction {
    
    /**
        Action that retracts a fact from a zeus.concepts.ResourceDb base
        */
    public void executeAction (Action a, Info info) { 
                 for(int j = 0; j < a.items.size(); j++ ) {
                    ValueFunction var = (ValueFunction)a.items.elementAt(j);
                    ValueFunction type_var = var.resolve(info.getBindings());
                    // type == 3 and id = 1... I'm not sure what this test is for, but it always fails... 
                  System.out.println ("type_var.getID() == " + type_var.getID()); 
                    System.out.println ("ValueFunction.TYPE == " + ValueFunction.TYPE); 
                    Assert.notFalse(type_var.getID() == ValueFunction.TYPE);
                    boolean found = false;
                    for(int k = 0; ! found && k < info.getInput().size(); k++ ) {
                        Fact f1 = (Fact)info.getInput().elementAt(k);
                        found = (f1.functor()).equals(type_var);
                        if ( found ) {
                            Core.DEBUG(2," <== " + f1);
                            retract(f1);
                            }// end if 
                        } // end for
                     Assert.notFalse(found);
                }// end for 
    }
    
    
    
    private void retract(Fact f1) {
      if ( context == null )
         engine.update(Node.REMOVE,f1);
      else
         context.ResourceDb().del(f1);
   }
    
    
}