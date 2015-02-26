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



package zeus.generator.event;

import java.util.*;

public class LinkNodeSelectionEvent extends EventObject {
  protected String type, fact, factId, node, group;

  public LinkNodeSelectionEvent(Object source, String node, String group,
                                String type, String fact, String factId) {
     super(source);
     this.type = type;
     this.fact = fact;
     this.factId = factId;
     this.node = node;
     this.group = group;
  }
  public String getNodeName()  { return node; }
  public String getGroupName() { return group; }
  public String getType()      { return type; }
  public String getFactType()  { return fact; }
  public String getFactId()    { return factId; }
}
