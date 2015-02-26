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

import zeus.actors.*; 
import zeus.concepts.*;
import zeus.agents.*;

public class HardRuleEngineTestExternal implements ZeusExternal {
    
    private int arraySize = 50; 
    private int numberTests = 10; 
    /** 
        this external asserts facts in the ResourceDb very quickly in order to see 
        that the appropriate rules are fired
        I think that 5050 rules should be fired
        */
    public void exec(AgentContext context ) { 
        ResourceDb rdb = context.getResourceDb(); 
        OntologyDb ont = context.getOntologyDb();
        Fact facts [] = new Fact [arraySize]; 
        int testNumber = 0; 
        while (testNumber<numberTests) { // run until stopped!
            testNumber++;
       //     System.out.println("Asserting "+ testNumber + " facts");  
            for (int count = 0; count < testNumber; count++ ) {
                for (int count2 = 0 ; count2 <arraySize; count2++) { 
                    Fact f = ont.getFact(Fact.FACT, "Entity"); 
                    facts[count2] = f; 
                }
                rdb.add(facts); }    
                
          }
    }
    
}