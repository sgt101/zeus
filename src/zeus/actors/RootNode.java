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



package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;


public class RootNode extends MsgNode {

   public RootNode () {
   ;
   }

   public RootNode(MsgHandler engine) {
      super(engine);
   }
   
   public void evaluate(String path, Performative input) {
      Assert.notFalse(path == null);
      propagate(path,input);
   }
   
   public void propagate(String path, Performative input) {
       StringBuffer err = new StringBuffer (40); 
      err.append ("\nMsgHandler Propagate: "); 
      err.append (path); 
      err.append (" RootNode"); 
      Core.DEBUG(4,err);
      Enumeration keys = successors.keys();
      MsgNode node;
      while( keys.hasMoreElements() ) {
         path = (String)keys.nextElement();
         node = (MsgNode)successors.get(path);
         err = new StringBuffer (40); 
         err.append ("\nRootNode about to evaluate: "); 
         err.append (path); 
        err.append (" ");
        err.append (node.toString()); 
        Core.DEBUG (4, err); //(4,"\nRootNode about to evaluate: " + path + " " + node);
     //   System.out.println("RootNote about to evaluate: " + path + " " + node); 
         node.evaluate(path,input);
      }
   }
   
   public String toString() {
      return "RootNode()";
   }
}
