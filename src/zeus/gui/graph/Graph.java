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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.util.*;

public class Graph extends MovePanel
             implements GraphIconListener,
                        GraphModelListener,
                        GraphNodeEditorListener {

  public static final int VERTICAL_PARENT_CHILD   = 0;
  public static final int VERTICAL_CHILD_PARENT   = 1;
  public static final int HORIZONTAL_PARENT_CHILD = 2;
  public static final int HORIZONTAL_CHILD_PARENT = 3;
  public static final int CENTRED                 = 4;
  public static final int CIRCLES                 = 5;

  protected static final int ORG_DX      = 20;
  protected static final int ORG_DY      = 20;

  protected static final double HEIGHT_FACTOR = 2.0;

  protected static final int BETA	 = 30;
  protected static final int GAMMA	 = 20;

  protected static final int BOTTOM_EDGE = 1;
  protected static final int TOP_EDGE	 = 2;
  protected static final int LEFT_EDGE	 = 3;
  protected static final int RIGHT_EDGE	 = 4;

  protected Hashtable         ViewList = new Hashtable();
  protected GraphModel        model;
  protected GraphNodeRenderer nodeRenderer = null;
  protected GraphNodeEditor   nodeEditor = null;
  protected Component         nodeEditorComponent = null;
  protected JLayeredPane      pane = null;
  protected boolean           isNodeEditable;
  protected boolean           isLinkEditable;
  protected int               viewMode;

  private Point     startPoint = null;
  private Point     lastPoint = null;
  private Point     stretchedPoint= null;
  private GraphIcon sourceIcon = null;
  private GraphIcon destIcon = null;

  public Graph(int viewMode, GraphModel model, boolean isNodeEditable,
               boolean isLinkEditable) {
      super(2000,2000);
      this.setLayout(new BulletinLayout());
      this.model = model;
      this.isNodeEditable = isNodeEditable;
      this.isLinkEditable = isLinkEditable;
      this.viewMode = viewMode;
      model.addGraphModelListener(this);
  }
  
  
  public Graph(GraphModel model, boolean isNodeEditable,
               boolean isLinkEditable) {
     this(HORIZONTAL_CHILD_PARENT,model,isNodeEditable,isLinkEditable);
  }
  
  
  public Graph(GraphModel model) {
    this(HORIZONTAL_CHILD_PARENT,model,true,true);
  }
  
  


  public void setModel(GraphModel model) {
    this.model.removeGraphModelListener(this);
    this.model = model;
    this.model.addGraphModelListener(this);
    reset();
  }


  public GraphModel getModel() {  return model; }

  public void setViewMode(int mode) {
     switch( mode ) {
        case HORIZONTAL_PARENT_CHILD:
        case HORIZONTAL_CHILD_PARENT:
        case VERTICAL_PARENT_CHILD:
        case VERTICAL_CHILD_PARENT:
        case CIRCLES:
        case CENTRED:
             viewMode = mode;
             break;
        default:
             Core.USER_ERROR("Attempt to set an illegal view mode: " + mode);
             break;
     }
  }

  public GraphNodeRenderer getNodeRenderer() {
     return nodeRenderer;
  }
  
  
  public void setNodeRenderer(GraphNodeRenderer nodeRenderer) {
     this.nodeRenderer = nodeRenderer;
  }
  
  
  public GraphNodeEditor getNodeEditor() {
     return nodeEditor;
  }
  
  
  public void setNodeEditor(GraphNodeEditor nodeEditor) {
     if ( this.nodeEditor != null )
        this.nodeEditor.removeGraphNodeEditorListener(this);

     this.nodeEditor = nodeEditor;

     if ( this.nodeEditor != null )
        this.nodeEditor.addGraphNodeEditorListener(this);
  }
  

  public void addNotify() {
     super.addNotify();
     reset();
  }
  

  public boolean isVisible(GraphNode node) {
     GraphIcon icon = (GraphIcon)ViewList.get(node);
     return icon != null && icon.isVisible();
  }


  public Rectangle getBounds(GraphNode node) {
     GraphIcon icon = (GraphIcon)ViewList.get(node);
     if ( icon != null )
        return icon.getBounds();
     else {
        Core.USER_ERROR("getBounds called on a null node");
        return new Rectangle(0,0,0,0);
     }
  }

  protected void reset() {
    // first remove graph from icon listenerList
    GraphIcon icon;
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      icon.removeGraphIconListener(this);
    }
    // next clear ViewList
    ViewList.clear();
    this.removeAll();
    // recreate graph
    enum = model.nodes();
    while( enum.hasMoreElements() )
       addNode((GraphNode)enum.nextElement());
    recompute();
  }


  protected void addNode(GraphNode node) {
    GraphIcon icon;
    Rectangle rect = new Rectangle(0,0,0,0);
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      rect = rect.union(icon.getBounds());
    }
    icon = new GraphIcon(node,this);
    ViewList.put(node,icon);
    this.add(icon);
    icon.setLocation(new Point(rect.x+rect.width,rect.y));
    icon.addGraphIconListener(this);
  }
  
  
  protected void removeNode(GraphNode node) {
    GraphIcon icon = (GraphIcon)ViewList.remove(node);
    if ( icon != null ) {
       this.remove(icon);
       icon.removeGraphIconListener(this);
    }
  }
  protected void updateNode(GraphNode node) {
    GraphIcon icon = (GraphIcon)ViewList.get(node);
    if ( icon != null )
       icon.reset();
  }

  public void hide() {
    GraphIcon icon;
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      if ( icon.isSelected() ) icon.setVisible(false);
    }
    repaint();
  }

  public void show() {
    GraphIcon icon;
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      if ( !icon.isVisible() ) icon.setVisible(true);
    }
    repaint();
  }

  public void collapse() {
    GraphIcon icon;
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      if ( icon.isSelected() )
         collapseNode(icon.getGraphNode(), new Vector());
    }
    repaint();
  }

  public void expand() {
    GraphIcon icon;
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      if ( icon.isSelected() )
         expandNode(icon.getGraphNode(), new Vector());
    }
    repaint();
  }

  public GraphNode[] getSelectedNodes() {
    GraphIcon icon;
    Vector items = new Vector();
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      if ( icon.isSelected() )
         items.addElement(icon);
    }

    GraphNode[] out = new GraphNode[items.size()];
    for(int i = 0; i < out.length; i++ )
       out[i] = ((GraphIcon)items.elementAt(i)).getGraphNode();
    return out;
  }

  protected void collapseNode(GraphNode node, Vector doneList) {
     GraphNode[] children = node.getChildren();
     doneList.addElement(node);
     for(int i = 0; i < children.length; i++ ) {
        GraphIcon icon = (GraphIcon)ViewList.get(children[i]);
        if ( icon != null ) {
           icon.setVisible(false);
           icon.setSelected(false);
           if ( !doneList.contains(children[i]) )
              collapseNode(children[i],doneList);
        }
     }
  }

  protected void expandNode(GraphNode node, Vector doneList) {
     GraphNode[] children = node.getChildren();
     doneList.addElement(node);
     for(int i = 0; i < children.length; i++ ) {
        GraphIcon icon = (GraphIcon)ViewList.get(children[i]);
        if ( icon != null ) {
           icon.setVisible(true);
           if ( !doneList.contains(children[i]) )
              expandNode(children[i],doneList);
        }
     }
  }

  public void select() {
    Vector items;
    GraphIcon icon;
    if ( (items = this.boundedItems()) != null ) {
      boolean all_selected = true;
      for( int i = 0; i < items.size(); i++ ) {
	icon = (GraphIcon)items.elementAt(i);
	all_selected = all_selected && icon.isSelected();
	if ( !all_selected) break;
      }

      for( int i = 0; i < items.size(); i++ ) {
	icon = (GraphIcon)items.elementAt(i);
	if ( all_selected )
	  icon.setSelected(false);
	else
	  icon.setSelected(true);
      }
    }
  }

  public void selectAll() {
    GraphIcon icon;
    boolean all_selected = true;
    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      all_selected = all_selected && icon.isSelected();
      if ( !all_selected) break;
    }

    enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      if ( all_selected )
	icon.setSelected(false);
      else
	icon.setSelected(true);
    }
  }

  public void recompute() {
    Point p = new Point(0,0);
    GraphNode node;
    GraphIcon icon;
    Enumeration enum;

    Rectangle bb;
    Point pt;
    Rectangle rect;

    Vector doneList = new Vector();
    switch( viewMode ) {
       case VERTICAL_PARENT_CHILD:
       case VERTICAL_CHILD_PARENT:
            p.x = ORG_DX; p.y = ORG_DY;
            enum = ViewList.elements();
            while( enum.hasMoreElements() ) {
               icon = (GraphIcon)enum.nextElement();
               node = icon.getGraphNode();
               if ( icon.isVisible() && node.getNodeType() == GraphNode.PARENT )
  	          computeVerticalPosition(node,doneList,p);
            }

            enum = ViewList.elements();
            while( enum.hasMoreElements() ) {
               icon = (GraphIcon)enum.nextElement();
               node = icon.getGraphNode();
               if ( icon.isVisible() && node.getNodeType() == GraphNode.CHILD &&
	            !doneList.contains(node) )
	          computeVerticalPosition(node,doneList,p);
            }

            if ( viewMode == VERTICAL_PARENT_CHILD )
               break;

            rect = getDrawArea(doneList);
            int ym = rect.y + rect.height/2;
            enum = doneList.elements();
            while( enum.hasMoreElements() ) {
               node = (GraphNode)enum.nextElement();
               icon = (GraphIcon)ViewList.get(node);
               bb = icon.getBounds();
               pt = new Point(bb.x,bb.y+bb.height);
               // transform 
               pt = new Point(pt.x, 2*ym - pt.y);
               // set new Location
               icon.setLocation(pt);
            }
            break;

       case HORIZONTAL_PARENT_CHILD:
       case HORIZONTAL_CHILD_PARENT:
            enum = ViewList.elements();
            while( enum.hasMoreElements() ) {
               icon = (GraphIcon)enum.nextElement();
               node = icon.getGraphNode();
               if ( icon.isVisible() && node.getNodeType() == GraphNode.PARENT )
  	          computeHorizontalPosition(node,doneList,p);
            }

            enum = ViewList.elements();
            while( enum.hasMoreElements() ) {
               icon = (GraphIcon)enum.nextElement();
               node = icon.getGraphNode();
               if ( icon.isVisible() && node.getNodeType() == GraphNode.CHILD &&
	            !doneList.contains(node) )
	          computeHorizontalPosition(node,doneList,p);
            }

            rect = getDrawArea(doneList);

            enum = doneList.elements();
            while( enum.hasMoreElements() ) {
               node = (GraphNode)enum.nextElement();
               icon = (GraphIcon)ViewList.get(node);
               pt = icon.getLocation();
               icon.setLocation(pt.x-rect.x+ORG_DX,pt.y-rect.y+ORG_DY);
            }

            if ( viewMode == HORIZONTAL_CHILD_PARENT )
               break;

            rect = getDrawArea(doneList);
            int xm = rect.x + rect.width/2;
            enum = doneList.elements();
            while( enum.hasMoreElements() ) {
               node = (GraphNode)enum.nextElement();
               icon = (GraphIcon)ViewList.get(node);
               bb = icon.getBounds();
               pt = new Point(bb.x + bb.width, bb.y);
               // transform 
               pt = new Point(2*xm - pt.x + ORG_DX, pt.y - ORG_DY);
               icon.setLocation(pt);
            }
            break;


       case CENTRED:
            GraphNode[] nodes = getSelectedNodes();
            node = null;
            boolean found = false;
            if ( nodes.length > 0 ) {
               for(int i = 0; !found && i < nodes.length; i++ ) {
                  node = nodes[i];
                  icon = (GraphIcon)ViewList.get(node);
                  found = icon.isVisible();
               }
            }

            if ( !found && !ViewList.isEmpty() ) {
               enum = ViewList.keys();
               while( !found && enum.hasMoreElements() ) {
                  node = (GraphNode)enum.nextElement();
                  icon = (GraphIcon)ViewList.get(node);
                  found = icon.isVisible();
               }
            }
            if ( !found ) break;

            GraphNode middleNode = node;

            enum = ViewList.keys();
            while( enum.hasMoreElements() ) {
               node = (GraphNode)enum.nextElement();
               icon = (GraphIcon)ViewList.get(node);
               if ( icon.isVisible() )
                  doneList.addElement(node);
            }
            centerView(middleNode,doneList);
            break;

       case CIRCLES:
            p.x = ORG_DX; p.y = ORG_DY;
            enum = ViewList.elements();
            while( enum.hasMoreElements() ) {
               icon = (GraphIcon)enum.nextElement();
               node = icon.getGraphNode();
               if ( icon.isVisible() )
  	          computeCircularPosition(node,doneList,p);
            }
            break;
    }
    redraw();
  }

  protected Rectangle getDrawArea(Vector doneList) {
    Rectangle rect = new Rectangle(0,0,0,0);
    Enumeration enum = ViewList.elements();
    GraphIcon icon;
    GraphNode node;
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      node = icon.getGraphNode();
      if ( doneList.contains(node) )
         rect = rect.union(icon.getBounds());
    }
    return rect;
  }

  public void redraw() {
    invalidate();
    repaint();
  }

  public void paintComponent(Graphics g){
    Dimension d = this.getSize();
    super.paintComponent(g);
    drawLinks(g);
  }

  protected void drawLinks(Graphics graphics) {
    GraphNode node;
    GraphIcon icon;

    Enumeration enum = ViewList.elements();
    while( enum.hasMoreElements() ) {
      icon = (GraphIcon)enum.nextElement();
      node = icon.getGraphNode();
      if ( icon.isVisible() )
         drawLinks(node,graphics);
    }
    revalidate();
  }

  protected void getRelations(Vector doList, Vector doneList) {
    Vector items;
    GraphNode node;

    for(int i = 0; i < doList.size(); i++ ) {
       node = (GraphNode)doList.elementAt(i);
       if ( !doneList.contains(node) ) {
          doneList.addElement(node);
          items = model.getViewRelations(node);
          items = Misc.difference(items,doneList);
          getRelations(items,doneList);
       }
    }
  }

  protected boolean computeCircularPosition(GraphNode self, Vector doneList,
					    Point p) {

    if ( doneList.contains(self) ) return false;

    GraphIcon icon;
    GraphNode node;
    Dimension size;
    int Wm, Hm, Wt, Ht, R;
    double theta, phi;
    int xx, yy;
    Point pa = new Point(0,0);

    Vector aList = new Vector();
    aList.addElement(self);
    getRelations(model.getViewRelations(self),aList);

    // remove invisible items from aList;
    // and simultaneously add all items in aList to the doneList
    for(int i = 0; i < aList.size(); i++ ) {
       node = (GraphNode)aList.elementAt(i);
       icon = (GraphIcon)ViewList.get(node);
       doneList.addElement(node);
       if ( !icon.isVisible() )
          aList.removeElementAt(i--);
    }

    if ( aList.size() <= 2 ) {
       for(int i = 0; i < aList.size(); i++ ) {
	  node = (GraphNode)aList.elementAt(i);
          icon = (GraphIcon)ViewList.get(node);
	  size = icon.getSize();
	  pa.x = p.x+(BETA+size.width)/2;
	  pa.y = p.y+(3*GAMMA+size.height)/2;
	  icon.setLocation(new Point(pa.x - size.width/2, pa.y - size.height/2));
	  p.x += size.width+BETA;
       }
       return true;
    }

    Dimension[] wh = new Dimension[aList.size()];
    Wm = Hm = 0;
    for(int i = 0; i < aList.size(); i++ ) {
       node = (GraphNode)aList.elementAt(i);
       icon = (GraphIcon)ViewList.get(node);
       wh[i] = icon.getSize();
       Wm = Math.max(Wm,wh[i].width);
       Hm = Math.max(Hm,wh[i].height);
    }
    R = (int)(HEIGHT_FACTOR*Hm/Math.tan(Math.PI/aList.size()));
    Wt = (2*Wm+BETA+2*R);
    Ht = (2*Hm+GAMMA+2*R);
    xx = p.x + Wt/2;
    yy = p.y + Ht/2;

    p.x += Wt;

    theta = (2*Math.PI/aList.size());
    for(int i = 0; i < aList.size(); i++ ) {
       phi = i*theta;
       pa.x = (int)(xx + R*Math.cos(phi)) - wh[i].width/2;
       pa.y = (int)(yy + R*Math.sin(phi)) - wh[i].height/2;
       node = (GraphNode)aList.elementAt(i);
       icon = (GraphIcon)ViewList.get(node);
       icon.setLocation(pa);
    }
    return true;
  }

  protected boolean centerView(GraphNode self, Vector aList) {
    /**
       Centers everyone about self.
       Only visible nodes are passed to this method.
    */

    GraphIcon icon = (GraphIcon)ViewList.get(self);
    if ( icon == null )
       return false;

    Dimension ps;
    if ( (getParent()).getClass() == JViewport.class ) {
       // If the panel is in a scrollpane
       JViewport viewport = (JViewport)getParent();
       viewport.setViewPosition(new Point(0,0));
       ps = viewport.getSize();
    }
    else {
       // The panel is not in a scrollpane
       ps = getSize();
    }

    Dimension ms = icon.getSize();
    int R = Math.min(ps.width,ps.height)/2;

    icon.setLocation(new Point(ps.width/2 - ms.width/2,
                               ps.height/2 - ms.height/2));

    int sr = (ms.width+ms.height)/2;
    int n = 0;
    GraphNode node;
    for(int i = 0; i < aList.size(); i++ ) {
       node = (GraphNode)aList.elementAt(i);
       if ( node != self && (icon = (GraphIcon)ViewList.get(node)) != null ) {
          ms = icon.getSize();
	  sr += (ms.width+ms.height)/2;
          n++;
       }
    }

    if ( n != 0 )  {
       R -= sr/n;
       double phi;
       Point pa = new Point(0,0);
       double theta = (2*Math.PI/n);
       int j = 0;
       for(int i = 0; i < aList.size(); i++ ) {
          node = (GraphNode)aList.elementAt(i);
          if ( node != self && (icon = (GraphIcon)ViewList.get(node)) != null ) {
             phi = j*theta;
             pa.x = (int)(ps.width/2 + R*Math.cos(phi));
             pa.y = (int)(ps.height/2 + R*Math.sin(phi));
             ms = icon.getSize();
             icon.setLocation(new Point(pa.x - ms.width/2,pa.y - ms.height/2));
             j++;
          }
       }
    }
    return false;
  }

  public boolean computeHorizontalPosition(GraphNode self, Vector doneList,
                                           Point p) {
    GraphNode node;
    Dimension size;
    Point p0 = new Point(0,0);
    Point p1 = new Point(0,0);
    Point loc = new Point(0,0);

    if ( doneList.contains(self) )
       return false;

    doneList.addElement(self);
    GraphIcon icon = (GraphIcon)ViewList.get(self);
    if ( icon == null )
       return false;

    if ( !icon.isVisible() )
       return false;

    GraphNode[] children = self.getChildren();
    Vector Items = new Vector();
    for(int i = 0; i < children.length; i++ ) {
       if ( !doneList.contains(children[i]) )
          Items.addElement(children[i]);
    }

    size = icon.getSize();
    if ( Items.isEmpty() ) {
      loc.y = p.y+(3*GAMMA+size.height)/2 - size.height/2;
      loc.x = p.x-(BETA+size.width)/2 - size.width/2;

      icon.setLocation(loc);

      p.y += size.height+3*GAMMA;
    }
    else {
      p0.y = p.y;
      p0.x = p.x-(size.width+BETA);

      for(int i = 0; i < Items.size(); i++ ) {
         node = (GraphNode) Items.elementAt(i);
	 computeHorizontalPosition(node,doneList,p0);
      }
      loc.y = (p.y+p0.y)/2 - size.height/2;
      loc.x = p.x-(size.width+BETA)/2 - size.width/2;

      icon.setLocation(loc);

      p.y = p0.y;
    }

    GraphNode[] siblings = self.getSiblings();
    for(int i = 0; i < siblings.length; i++ ) {
       if ( !doneList.contains(siblings[i]) )
          computeHorizontalPosition(siblings[i],doneList,p);
    }
    return true;
  }

  public boolean computeVerticalPosition(GraphNode self, Vector doneList, Point p) {
    GraphNode node;
    Dimension size;
    Point p0 = new Point(0,0);
    Point p1 = new Point(0,0);
    Point loc = new Point(0,0);

    if ( doneList.contains(self) )
       return false;

    doneList.addElement(self);
    GraphIcon icon = (GraphIcon)ViewList.get(self);
    if ( icon == null )
       return false;

    if ( !icon.isVisible() )
       return false;

    GraphNode[] children = self.getChildren();
    Vector Items = new Vector();
    for(int i = 0; i < children.length; i++ ) {
       if ( !doneList.contains(children[i]) )
          Items.addElement(children[i]);
    }

    size = icon.getSize();
    if ( Items.isEmpty() ) {
       loc.x = p.x+(BETA+size.width)/2 - size.width/2;
       loc.y = p.y+(3*GAMMA+size.height)/2 - size.height/2;

       icon.setLocation(loc);
       p.x += size.width+BETA;
    }
    else {
       p0.x = p.x;
       p0.y = p.y+size.height+3*GAMMA;

       for(int i = 0; i < Items.size(); i++ ) {
          node = (GraphNode) Items.elementAt(i);
          computeVerticalPosition(node,doneList,p0);
       }
       loc.x = (p.x+p0.x)/2 - size.width/2;
       loc.y = p.y+(size.height+3*GAMMA)/2 - size.height/2;

       icon.setLocation(loc);

       p.x = p0.x;
    }

    GraphNode[] siblings = self.getSiblings();
    for(int i = 0; i < siblings.length; i++ ) {
       if ( !doneList.contains(siblings[i]) )
          computeVerticalPosition(siblings[i],doneList,p);
    }

    return true;
  }


  protected void drawLinks(GraphNode node, Graphics graphics) {
    GraphIcon icon = (GraphIcon)ViewList.get(node);

    Point p1 = new Point(0,0);
    Point p2 = new Point(0,0);

    Color color;
    GraphNode[] parent = node.getParents();
    for(int i = 0; i < parent.length; i++ )  {
       if ( model.isLinkVisible(node,parent[i]) &&
            getConnections(node,parent[i],p1,p2) ) {
          color = model.getLinkColor(node,parent[i]);
          drawEdge(color,p1,p2,graphics);
       }
    }

    GraphNode[] children = node.getChildren();
    for(int i = 0; i < children.length; i++ )  {
       if ( model.isLinkVisible(node,children[i]) &&
            getConnections(node,children[i],p1,p2) ) {
          color = model.getLinkColor(node,children[i]);
          drawEdge(color,p1,p2,graphics);
       }
    }
    
    GraphNode[] sibling = node.getSiblings();
    for(int i = 0; i < sibling.length; i++ ) {
       if ( model.isLinkVisible(node,sibling[i]) &&
            getConnections(node,sibling[i],p1,p2) ) {
          color = model.getLinkColor(node,sibling[i]);
          drawEdge(color,p1,p2,graphics);
       }
    }
  }

  protected void getConnection(Rectangle bb, int edge, Point p) {
    switch( edge ) {
    case BOTTOM_EDGE:
      p.x = bb.x + bb.width/2;
      p.y = bb.y;
      return;

    case TOP_EDGE:
      p.x = bb.x + bb.width/2;
      p.y = bb.y + bb.height;
      return;

    case LEFT_EDGE:
      p.x = bb.x;
      p.y = bb.y + bb.height/2;
      return;

    case RIGHT_EDGE:
      p.x = bb.x + bb.width;
      p.y = bb.y + bb.height/2;
      return;

    default:
      return;
    }
  }

  protected boolean getConnections(GraphNode node1, GraphNode node2,
                                   Point p1, Point p2) {

    Rectangle a, b;
    GraphIcon icon1 = (GraphIcon)ViewList.get(node1);
    GraphIcon icon2 = (GraphIcon)ViewList.get(node2);

    if ( icon1 == null || icon2 == null ||
         !icon1.isVisible() || !icon2.isVisible() )
       return false;


    a = icon1.getBounds();
    b = icon2.getBounds();

    switch( viewMode ) {
       case HORIZONTAL_CHILD_PARENT:
       case HORIZONTAL_PARENT_CHILD:
            if ( b.x > a.x+a.width ) {
               getConnection(a,RIGHT_EDGE,p1);
               getConnection(b,LEFT_EDGE,p2);
               return true;
            }
            else if ( b.x+b.width < a.x ) {
               getConnection(a,LEFT_EDGE,p1);
               getConnection(b,RIGHT_EDGE,p2);
               return true;
            }
            else if ( b.y > a.y+a.height ) {
               getConnection(a,TOP_EDGE,p1);
               getConnection(b,BOTTOM_EDGE,p2);
               return true;
            }
            else if ( b.y+b.height < a.y ) {
               getConnection(a,BOTTOM_EDGE,p1);
               getConnection(b,TOP_EDGE,p2);
               return true;
            }
            // in case of overlapping rects -- don't draw links
            return false;

       default:
            if ( b.y > a.y+a.height ) {
               getConnection(a,TOP_EDGE,p1);
               getConnection(b,BOTTOM_EDGE,p2);
               return true;
            }
            else if ( b.y+b.height < a.y ) {
               getConnection(a,BOTTOM_EDGE,p1);
               getConnection(b,TOP_EDGE,p2);
               return true;
            }
            else if ( b.x > a.x+a.width ) {
               getConnection(a,RIGHT_EDGE,p1);
               getConnection(b,LEFT_EDGE,p2);
               return true;
            }
            else if ( b.x+b.width < a.x ) {
               getConnection(a,LEFT_EDGE,p1);
               getConnection(b,RIGHT_EDGE,p2);
               return true;
            }
            return false;
    }
  }

  protected void drawEdge(Color color, Point p1, Point p2, Graphics g) {
    Point[] pts;

    Color c = g.getColor();
    g.setColor( color );
    g.setPaintMode();

    g.drawLine(p1.x,p1.y,p2.x,p2.y);
    pts = ArrowData.getPoints((double)p1.x, (double)p1.y,
			      (double)p2.x, (double)p2.y);
    for(int i = 0; i < 2; i++ )
      g.drawLine(pts[i].x,pts[i].y,pts[i+1].x,pts[i+1].y);
    g.setColor(c);
  }

  protected GraphIcon findIcon(Point pt) {
     GraphIcon icon;
     Rectangle region;
     Enumeration enum = ViewList.elements();
     while( enum.hasMoreElements() ) {
        icon = (GraphIcon)enum.nextElement();
        region = icon.getBounds();
        if ( region.contains(pt.x,pt.y) )
           return icon;
     }
     return null;
  }


  // GraphIcon Events
  public void locationChanged(GraphIconEvent evt) {
     redraw();
  }
  public void performLeftMouseDClickAction(GraphIconEvent evt) {
     // if appropriate, configure node editor
     if ( nodeEditorComponent == null && isNodeEditable ) {
        GraphIcon icon = (GraphIcon)evt.getSource();
        GraphNode node = icon.getGraphNode();
        if ( model.isNodeEditable(node) ) {
           if ( nodeEditor == null ) {
              nodeEditor = new DefaultGraphNodeEditor();
              nodeEditor.addGraphNodeEditorListener(this);
           }
           nodeEditorComponent = nodeEditor.getNodeEditorComponent(this,node);
           Component root = SwingUtilities.getRoot(this);
           if ( !(nodeEditorComponent instanceof Window) ) {
              if ( root instanceof JDialog )
                 pane = ((JDialog)root).getLayeredPane();
              else if ( root instanceof JFrame )
                 pane = ((JFrame)root).getLayeredPane();
              else
                 Core.ERROR(null,1,this);
              pane.add(nodeEditorComponent,JLayeredPane.PALETTE_LAYER);
              pane.moveToFront(nodeEditorComponent);
           }
           Point pt = SwingUtilities.convertPoint(icon,0,0,root);
           nodeEditorComponent.setLocation(pt);
           nodeEditorComponent.setVisible(true);
           if ( nodeEditorComponent instanceof JComponent )
	      nodeEditorComponent.requestFocus();
        }
     }

  }
  public void performLeftMouseAction(GraphIconEvent evt) {
  }
  public void performRightMouseAction(GraphIconEvent evt) {
    if ( !isLinkEditable ) return;

    Point[] pts;
    Graphics g = this.getGraphics();
    Color color = g.getColor();
    g.setColor(Color.black);
    if (evt.isRightMousePressed() ){
       if ( !(evt.getSource() instanceof GraphIcon) ) {
         sourceIcon = null;
         return;
       }
       sourceIcon = (GraphIcon)evt.getSource();
       lastPoint = stretchedPoint= startPoint = SwingUtilities.convertPoint(
          sourceIcon,evt.getPoint().x,evt.getPoint().y,this);
    }
    else if (evt.isRightMouseDragged() ){
      if ( sourceIcon == null ) return;
      lastPoint  = stretchedPoint;
      stretchedPoint =  SwingUtilities.convertPoint(
         sourceIcon,evt.getPoint().x,evt.getPoint().y,this);

      g.setXORMode(this.getBackground());
      g.drawLine(startPoint.x,startPoint.y,lastPoint.x,lastPoint.y);
      g.drawLine(startPoint.x,startPoint.y,stretchedPoint.x,stretchedPoint.y);
    }
    else if (evt.isRightMouseReleased() ){
      if ( sourceIcon == null ) return;
      lastPoint = stretchedPoint;
      stretchedPoint =  SwingUtilities.convertPoint(
         sourceIcon,evt.getPoint().x,evt.getPoint().y,this);

      g.setXORMode(this.getBackground());
      g.drawLine(startPoint.x,startPoint.y,lastPoint.x,lastPoint.y);
      destIcon = findIcon(stretchedPoint);
      if (destIcon == null) return;
      g.drawLine(startPoint.x,startPoint.y,stretchedPoint.x,stretchedPoint.y);
      pts = ArrowData.getPoints((double)startPoint.x, (double)startPoint.y,
			      (double)stretchedPoint.x, (double)stretchedPoint.y);
      for(int i = 0; i < 2; i++ )
         g.drawLine( pts[i].x, pts[i].y, pts[i+1].x, pts[i+1].y);
    }
    g.setColor(color);
  }

  public void performMiddleMouseAction(GraphIconEvent evt) {
  }

  // GraphNodeEditor events
  public void graphNodeEditingStopped(GraphNodeEditorEvent evt) {
     if ( nodeEditorComponent != null ) {
        nodeEditorComponent.setVisible(false);
        if ( !(nodeEditorComponent instanceof Window) )
	   pane.remove(nodeEditorComponent);
        nodeEditorComponent = null;
        model.setValue(evt.getNode(),evt.getValue());
     }
  }
  public void graphNodeEditingCancelled(GraphNodeEditorEvent evt) {
     if ( nodeEditorComponent != null ) {
        nodeEditorComponent.setVisible(false);
        if ( !(nodeEditorComponent instanceof Window) )
	   pane.remove(nodeEditorComponent);
        nodeEditorComponent = null;
     }
  }

  // GraphModel Events
  public void graphStructureChanged(GraphModelEvent evt) {
     if ( model == evt.getModel() )
        reset();
  }
  public void graphNodeAdded(GraphModelEvent evt) {
     if ( model == evt.getModel() ) {
        addNode(evt.getNode());
        redraw();
     }
  }
  public void graphNodeRemoved(GraphModelEvent evt) {
     if ( model == evt.getModel() ) {
        removeNode(evt.getNode());
        redraw();
     }
  }
  public void graphNodeStateChanged(GraphModelEvent evt) {
     if ( model == evt.getModel() ) {
        updateNode(evt.getNode());
        redraw();
     }
  }
}
