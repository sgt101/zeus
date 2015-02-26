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



package zeus.actors.event;

import zeus.util.*;
import zeus.actors.rtn.*;

public class NodeEvent extends Event {
   public static final long CREATE_MASK       = 1;
   public static final long DISPOSE_MASK      = 2;
   public static final long STATE_CHANGE_MASK = 4;

   public NodeEvent(Object source, Node object, long event_mask) {
      super(source,object,NODE_FIRST,NODE_LAST,event_mask);
   }

   public String getNodeName()      { return getNode().getDescription();   }
   public Node   getNode()          { return (Node)object; }
   public Node   getLogicalParent() { return ((Node)object).getPrevious(); }
   public Node[] getParents()       { return ((Node)object).getParents(); }
   public int    getState()         { return ((Node)object).getState(); }
   
   public String getLogicalParentName() { 
      return getLogicalParent().getDescription();
   }

   public String[] getParentNames() {
      Node[] parents = getParents();
      if ( parents == null ) return null;

      String[] output = new String[parents.length];
      for(int i = 0; i < parents.length; i++ )
         output[i] = parents[i].getDescription();
      return output;
   }
}