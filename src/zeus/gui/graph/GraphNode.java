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



package zeus.gui.graph;

import java.util.*;
import zeus.util.*;

public class GraphNode {
  public static final int PARENT = 0;
  public static final int CHILD  = 1;

  protected Vector children = new Vector();
  protected Vector parents = new Vector();
  protected Vector siblings = new Vector();
  protected int    nodeType = PARENT;
  protected Object user_object;

  public GraphNode(Object user_object) {
    this.user_object = user_object;
  }

  public void setUserObject(Object user_object)	{
    this.user_object = user_object;
  }

  public Object getUserObject() {
    return user_object;
  }

  public void setNodeType(int type) {
    Assert.notFalse( type == PARENT || type == CHILD );
    nodeType = type;
  }

  public int getNodeType()  {
    return nodeType;
  }

  public void initialize() {
    children.removeAllElements();
    parents.removeAllElements();
    siblings.removeAllElements();
    nodeType = PARENT;
  }

  protected boolean addItem(GraphNode node, Vector nodeList) {
    if ( nodeList.contains(node) )
       return false;
    nodeList.addElement(node);
    return true;
  }

  public void addSibling(GraphNode node) {
    addItem(node,siblings);
  }
  public void addChild(GraphNode node) {
    addItem(node,children);
  }
  public void addParent(GraphNode node) {
    if ( addItem(node,parents) )
       nodeType = CHILD;
  }
  public boolean hasSibling(GraphNode node) {
    return siblings.contains(node);
  }
  public boolean hasChild(GraphNode node) {
    return children.contains(node);
  }
  public boolean hasParent(GraphNode node) {
    return parents.contains(node);
  }
  public void removeSibling(GraphNode node) {
    siblings.removeElement(node);
  }
  public void removeChild(GraphNode node) {
    children.removeElement(node);
  }
  public void removeParent(GraphNode node) {
    parents.removeElement(node);
    if ( parents.isEmpty() )
       nodeType = PARENT;
  }
  public GraphNode[] getChildren() {
    GraphNode[] out = new GraphNode[children.size()];
    for(int i = 0; i < children.size(); i++ )
       out[i] = (GraphNode)children.elementAt(i);
    return out;
  }
  public GraphNode[] getSiblings() {
    GraphNode[] out = new GraphNode[siblings.size()];
    for(int i = 0; i < siblings.size(); i++ )
       out[i] = (GraphNode)siblings.elementAt(i);
    return out;
  }
  public GraphNode[] getParents() {
    GraphNode[] out = new GraphNode[parents.size()];
    for(int i = 0; i < parents.size(); i++ )
       out[i] = (GraphNode)parents.elementAt(i);
    return out;
  }
}
