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



package zeus.concepts;

import java.util.*;
import zeus.util.*;
import zeus.concepts.fn.*;

public class ConditionalNode extends TaskNode {
   public ConditionalNode(String name) {
      super(name);
      isConditionalNode = true;
   }

   public ConditionalNode(String name, Fact[] consumed) {
      super(name);
      isConditionalNode = true;
      setPreconditions(consumed);
      setPostconditions(consumed);
   }

   public ConditionalNode(String name, Vector consumed) {
      super(name);
      isConditionalNode = true;
      setPreconditions(consumed);
      setPostconditions(consumed);
   }

   public ConditionalNode(ConditionalNode node) {
      super(node.getName());
      isConditionalNode = true;

      String[] group;
      group = node.getPreconditionGroups();
      for(int i = 0; i < group.length; i++ )
         setPreconditions(group[i],node.getPreconditions(group[i]));
      group = node.getPostconditionGroups();
      for(int i = 0; i < group.length; i++ )
         setPostconditions(group[i],node.getPostconditions(group[i]));
   }

   public TaskNode duplicate(DuplicationTable table) {
      Enumeration enum;
      String group;
      Vector data;
      Fact[] xdata;

      ConditionalNode xnode = new ConditionalNode(name);

      enum = consumed.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         data = (Vector)consumed.get(group);
         xdata = new Fact[data.size()];
         for(int i = 0; i < data.size(); i++ )
            xdata[i] = ((Fact)data.elementAt(i)).duplicate(table);
         xnode.setPreconditions(group,xdata);
      }

      enum = produced.keys();
      while( enum.hasMoreElements() ) {
         group = (String)enum.nextElement();
         data = (Vector)produced.get(group);
         xdata = new Fact[data.size()];
         for(int i = 0; i < data.size(); i++ )
            xdata[i] = ((Fact)data.elementAt(i)).duplicate(table);
         xnode.setPostconditions(group,xdata);
      }
      return xnode;
   }
}