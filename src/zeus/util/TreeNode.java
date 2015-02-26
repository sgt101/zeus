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

public class TreeNode {
   protected static final int NODES  = 0;
   protected static final int VALUES = 1;

   protected Object data = null;
   protected Vector children = null;
   protected TreeNode parent = null;

   public TreeNode(TreeNode parent, Object data) {
      children = new Vector();
      setParent(parent);
      setValue(data);
   }
   public TreeNode(Object data) {
      children = new Vector();
      setValue(data);
   }

   class PreorderEnumerator implements Enumeration {
      TreeNode current = null;
      TreeNode root = null;
      int type;

      public PreorderEnumerator(TreeNode node, int type) {
         root = current = node;
         this.type = type;
      }
      public boolean hasMoreElements() {
         return current != null;
      }
      public Object nextElement() {
         if ( current == null ) throw new NoSuchElementException();

         TreeNode token = current;

         current = token.firstChild();
         if ( current == null && token != root )
            current = token.nextSibling();
         if ( current == null ) {
            TreeNode parent = (token == root) ? null : token.getParent();
            while( parent != null && parent != root && 
                   (current = parent.nextSibling()) == null )
               parent = (parent == root) ? null : parent.getParent();
         }
         
         if (type == NODES )
            return token;
         else if ( token != null )
            return token.getValue();
         else
            return null;
      }
   }

   public Enumeration values() {
      return new PreorderEnumerator(this, VALUES);
   }
   public Enumeration nodes() {
      return new PreorderEnumerator(this, NODES);
   }

   public boolean  isRoot()      { return parent == null; }
   public boolean  isTerminal()  { return children.isEmpty(); }
   public boolean  hasChildren() { return !children.isEmpty(); }
   public TreeNode getParent()   { return parent; }
   public Object   getValue()    { return data; }

   public void setValue(Object value) {
      Assert.notNull(value);
      data = value;
   }

   public Vector getChildren() {
      Vector result = new Vector();
      for(int i = 0; i < children.size(); i++ ) 
         result.addElement(children.elementAt(i));
      return result;
   }
   public boolean hasChild(TreeNode child) {
      return children.contains(child);
   }
   public void setParent(TreeNode parent) {
      this.parent = parent;
   }
   public boolean containsValue(Object data) {
      if ( this.data.equals(data) ) return true;
      boolean status = false;
      TreeNode node;
      for(int i = 0; !status && i < children.size(); i++ ) {
         node = (TreeNode)children.elementAt(i);
         status |= node.containsValue(data);
      }
      return status;
   }
   public TreeNode addChild(TreeNode node) {
      node.setParent(this);
      children.addElement(node);
      return node;
   }
   public TreeNode addChild(Object data) {
      TreeNode node = new TreeNode(this,data);
      children.addElement(node);
      return node;
   }
   public TreeNode addChild(TreeNode node, int position) {
      Assert.notFalse(position >= 0 && position <= children.size());
      node.setParent(this);
      children.insertElementAt(node,position);
      return node;
   }
   public TreeNode addChild(Object data, int position) {
      Assert.notFalse(position >= 0 && position <= children.size());
      TreeNode node = new TreeNode(this,data);
      children.insertElementAt(node,position);
      return node;
   }
   public void removeChild(TreeNode node) {
      children.removeElement(node);
   }
   public void removeChild(int position) {
      Assert.notFalse(position >= 0 && position < children.size());
      children.removeElementAt(position);
   }
   public TreeNode addBefore(Object data) {
      Assert.notNull(parent);
      TreeNode node = new TreeNode(parent,data);
      parent.addBefore(node,this);
      return node;
   }
   public TreeNode addBefore(TreeNode node) {
      Assert.notNull(parent);
      node.setParent(parent);
      parent.addBefore(node,this);
      return node;
   }
   public TreeNode addAfter(Object data) {
      Assert.notNull(parent);
      TreeNode node = new TreeNode(parent,data);
      parent.addAfter(node,this);
      return node;
   }
   public TreeNode addAfter(TreeNode node) {
      Assert.notNull(parent);
      node.setParent(parent);
      parent.addAfter(node,this);
      return node;
   }
   protected void addBefore(TreeNode node, TreeNode me) {
      for(int i = 0; i < children.size(); i++ ) {
         if ( children.elementAt(i).equals(me) ) {
            children.insertElementAt(node,i);
            return;
         }
      }
      Assert.notNull(null);
   }
   
   protected void addAfter(TreeNode node, TreeNode me) {
      for(int i = 0; i < children.size(); i++ ) {
         if ( children.elementAt(i).equals(me) ) {
            children.insertElementAt(node,i+1);
            return;
         }
      }
      Assert.notNull(null);
   }
   public TreeNode nextSibling() {
      return parent != null ? parent.nextSibling(this) : null;
   }
   public TreeNode previousSibling() {
      return parent != null ? parent.previousSibling(this) : null;
   }
   protected TreeNode nextSibling(TreeNode me) {
      for(int i = 0; i < children.size(); i++ ) {
         if ( children.elementAt(i).equals(me) ) {
            if ( i+1 < children.size() )
               return (TreeNode)children.elementAt(i+1);
            else 
               return null;
         }
      }
      Assert.notNull(null); // I am not in my dad's list of children
      return null;
   }
   protected TreeNode previousSibling(TreeNode me) {
      for(int i = 0; i < children.size(); i++ ) {
         if ( children.elementAt(i).equals(me) ) {
            if ( i-1 >= 0  )
               return (TreeNode)children.elementAt(i-1);
            else 
               return null;
         }
      }
      Assert.notNull(null); // I am not in my dad's list of children
      return null;
   }
   public TreeNode firstChild() {
      return children.isEmpty() ? null : (TreeNode)children.elementAt(0);
   }
   public String toString() {
      return data.toString();
   }
}
