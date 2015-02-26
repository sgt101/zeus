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

public class ArcEvent extends Event {
   public static final long CREATE_MASK  = 1;
   public static final long DISPOSE_MASK = 2;
   public static final long FAIL_MASK    = 4;
   public static final long SUCCEED_MASK = 8;

   protected Node origin = null;
   protected Node destination = null;

   public ArcEvent(Object source, Arc object, Node node, long event_mask) {
      super(source,object,ARC_FIRST,ARC_LAST,event_mask);
      if ( (event_mask & CREATE_MASK) != 0 )
         origin = node;
      else if ( (event_mask & SUCCEED_MASK) != 0 )
         destination = node;
      else
         Core.ERROR(node != null, 1, this);
   }
   public Arc  getArc()         { return (Arc)object; }
   public Node getOrigin()      { return origin; }
   public Node getDestination() { return destination; }
}
