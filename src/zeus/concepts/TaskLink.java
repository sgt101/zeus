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

public class TaskLink implements Reference {
   protected String leftNode;
   protected String rightNode;
   protected String leftArg;
   protected String rightArg;
   protected String leftGroup;
   protected String rightGroup;

   public TaskLink(String leftNode, String leftGroup, String leftArg,
                   String rightNode, String rightGroup, String rightArg) {

      Assert.notNull(leftNode);
      Assert.notNull(leftGroup);
      Assert.notNull(leftArg);
      Assert.notNull(rightNode);
      Assert.notNull(rightGroup);
      Assert.notNull(rightArg);

      this.leftNode = leftNode;
      this.leftGroup = leftGroup;
      this.leftArg = leftArg;
      this.rightNode = rightNode;
      this.rightGroup = rightGroup;
      this.rightArg = rightArg;
   }

   public TaskLink(TaskLink link) {
      leftNode = link.getLeftNode();
      rightNode = link.getRightNode();
      leftGroup = link.getLeftGroup();
      rightGroup = link.getRightGroup();
      leftArg = link.getLeftArg();
      rightArg = link.getRightArg();
   }

   public void setLeftNode(String leftNode) {
      Assert.notNull(leftNode);
      this.leftNode = leftNode;
   }
   public void setRightNode(String rightNode) {
      Assert.notNull(rightNode);
      this.rightNode = rightNode;
   }
   public void setLeftArg(String leftArg) {
      Assert.notNull(leftArg);
      this.leftArg = leftArg;
   }
   public void setRightArg(String rightArg) {
      Assert.notNull(rightArg);
      this.rightArg = rightArg;
   }
   public void setLeftGroup(String leftGroup) {
      Assert.notNull(leftGroup);
      this.leftGroup = leftGroup;
   }
   public void setRightGroup(String rightGroup) {
      Assert.notNull(rightGroup);
      this.rightGroup = rightGroup;
   }

   public String getLeftNode()   { return leftNode; }
   public String getRightNode()  { return rightNode; }
   public String getLeftGroup()  { return leftGroup; }
   public String getRightGroup() { return rightGroup; }
   public String getLeftArg()    { return leftArg; }
   public String getRightArg()   { return rightArg; }

   public boolean isFromBeginNode() { return leftNode.equals(TaskNode.BEGIN); }
   public boolean isToEndNode()     { return rightNode.equals(TaskNode.END); }

   public String getId() {
      return leftNode + ":" + leftGroup + ":" + leftArg +
             "==" +
	     rightNode + ":" + rightGroup + ":" + rightArg;
   }

   public boolean equals(TaskLink link) {
      return getId().equals(link.getId());
   }

   public String toString() {
      return( "(" +
               ":left_node " + leftNode + " " +
               ":left_group " + leftGroup + " " +
               ":left_arg " + leftArg + " " +
               ":right_node " + rightNode + " " +
               ":right_group " + rightGroup + " " +
               ":right_arg " + rightArg +
              ")"
            );
   }

   public String pprint() {
      return pprint(0);
   }
   public String pprint(int sp) {
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = "(:left_node " + leftNode + eol +
                  ":left_group " + leftGroup + eol +
                  ":left_arg " + leftArg + eol +
                  ":right_node " + rightNode + eol +
                  ":right_group " + rightGroup + eol +
                  ":right_arg " + rightArg + eol;
      return s.trim() + "\n" + tabs + ")";
   }

   public boolean referencesNode(String node) {
      return leftNode.equals(node) || rightNode.equals(node);
   }
   public boolean references(String id) {
      return leftArg.equals(id) || rightArg.equals(id);
   }

   public TaskLink duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public TaskLink duplicate(DuplicationTable table) {
      ValueFunction leftArgFn = new VarFn(leftArg);
      ValueFunction rightArgFn = new VarFn(rightArg);
      String _leftArg = (leftArgFn.duplicate(table)).toString();
      String _rightArg = (rightArgFn.duplicate(table)).toString();
      return new TaskLink(leftNode,leftGroup,_leftArg,
                          rightNode,rightGroup,_rightArg);
   }
}
