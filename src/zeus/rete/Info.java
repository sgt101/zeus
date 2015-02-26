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



package zeus.rete;
import java.util.*;
import zeus.concepts.Bindings;

/** 
    simple storage class - used to shuttle information between various nodes
    on various graphs. Was a package protected local class in ConflicSet, but 
    I decided that it was best exposed and refactored before I went mad.
    @author Simon Thompson
    @since 1.1
    
    */
public class Info {
      String path;
      Vector input;
      Bindings bindings;
      ActionNode node;
      
      
      public void setPath (String path) { 
        this.path = path; }
        
        
      public String getPath () { 
        return path; 
      }
      
      
      public void setInput (Vector vect) { 
        this.input = vect; 
      }
      
      
      public Vector getInput () { 
        return input; 
      }
      
      
      public void setBindings (Bindings bindings) { 
        this.bindings = bindings; 
      }
      
      
      public Bindings getBindings () { 
        return bindings;
      }
      
      
      public void setActionNode (ActionNode ac) { 
        this.node = ac; 
      }
      
      
      public ActionNode getActionNode () { 
        return node; 
      }
      



      
      
   }