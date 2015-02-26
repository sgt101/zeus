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



package zeus.util;

import java.util.*;

public class Tree {
   protected TreeNode root = null;  

   public Tree(Object data) {
      root = new TreeNode(data);
   }

   public TreeNode getRoot() { return root; }

   public Enumeration values() {
      return root.values();
   }
   public Enumeration nodes() {
      return root.nodes();
   }


   public static void main(String[] args) {
      Tree tree = new Tree("ZeusFact");
      TreeNode root, node;

      root = tree.getRoot();

      TreeNode ZeusFact = tree.getRoot();
      TreeNode Animal = ZeusFact.addChild("Animal");
      TreeNode Fish = Animal.addChild("Fish");
      TreeNode Shark = Fish.addChild("Shark");
      TreeNode Cod = Fish.addChild("Cod");
      TreeNode Mammal = Animal.addChild("Mammal");
      TreeNode Human = Mammal.addChild("Human");
      TreeNode Cat = Mammal.addChild("Cat");
      TreeNode Dog = Mammal.addChild("Dog");
      TreeNode Amphibian = Animal.addChild("Amphibian");
      TreeNode Frog = Amphibian.addChild("Frog");
      TreeNode Toad = Amphibian.addChild("Toad");

      TreeNode TransportDevice = ZeusFact.addChild("TransportDevice");
      TreeNode Car = TransportDevice.addChild("Car");
      TreeNode Bus = TransportDevice.addChild("Bus");


      Enumeration enum = tree.nodes();
      while( enum.hasMoreElements() ) {
         node = (TreeNode)enum.nextElement();
         System.out.println(node);
      }
      
   }
}
