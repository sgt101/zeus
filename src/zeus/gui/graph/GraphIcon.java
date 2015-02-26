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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import zeus.util.*;

public class GraphIcon extends JPanel implements MouseListener,
                                                 MouseMotionListener {

  protected static final int LOCATION_CHANGED     = 0;
  protected static final int DOUBLE_CLICK_ACTION  = 1;
  protected static final int LEFT_MOUSE_ACTION    = 2;
  protected static final int RIGHT_MOUSE_PRESSED  = 3;
  protected static final int RIGHT_MOUSE_DRAGGED  = 4;
  protected static final int RIGHT_MOUSE_RELEASED = 5;
  protected static final int MIDDLE_MOUSE_ACTION  = 6;

  protected static final int thickness = 3;

  protected Point     anchor;
  protected Point     graphPoint;
  protected Graph     graph;
  protected Rectangle rect = null;
  protected Cursor    lastCursor;
  protected Component rendererComp;
  protected GraphNode node;

  protected EventListenerList iconListeners = new EventListenerList();
  protected boolean dragging     = false;
  protected boolean isSelected   = false;
  protected boolean isSelectable = true;
  protected boolean isMoveable   = true;



  public GraphIcon(GraphNode node, Graph graph,
                   boolean isSelectable, boolean isMoveable) {
    this(node,graph);
    this.isSelectable = isSelectable;
    this.isMoveable = isMoveable;
  }

  public GraphIcon(GraphNode node, Graph graph) {
    this.node = node;
    this.graph = graph;
    this.setBackground(Color.lightGray);
    this.setLayout(new GridBagLayout());
    reset();
    anchor = getLocation();
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }

  public void reset() {
    removeAll();
    GridBagLayout gb = (GridBagLayout)this.getLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    GraphNodeRenderer renderer = graph.getNodeRenderer();
    if ( renderer == null )
       renderer = new DefaultGraphNodeRenderer();

    rendererComp = renderer.getNodeRendererComponent(graph,node);
    rendererComp.addMouseListener(this);
    rendererComp.addMouseMotionListener(this);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(thickness,thickness,thickness,thickness);
    gb.setConstraints(rendererComp,gbc);
    this.add(rendererComp);
    setBorder(new BevelBorder(BevelBorder.RAISED));
    isSelected = false;
  }

  public void addNotify() {
    super.addNotify();
    setSize(getPreferredSize());
    doLayout();
  }

  public void setGraphNode(GraphNode node) {
     this.node = node;
     reset();
     setSize(getPreferredSize());
     doLayout();
  }

  public GraphNode getGraphNode() {
     return node;
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  public void setMoveable(boolean set) {
    this.isMoveable = set;
  }

  public void setSelectable(boolean set) {
    this.isSelectable = set;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean mode) 	{
    if ( isSelectable )	{
       isSelected = mode;
       if ( mode )
          setBorder(new BevelBorder(BevelBorder.LOWERED));
       else
          setBorder(new BevelBorder(BevelBorder.RAISED));
    }
  }

  public void setLocation(int x, int y) {
    super.setLocation(x,y);
    if ( isMoveable )
       fireAction(LOCATION_CHANGED);
  }

  public void mouseClicked(MouseEvent evt) {}
  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt)  {}
  public void mouseMoved(MouseEvent evt)   {}

  public void mousePressed(MouseEvent evt) {
    graphPoint = evt.getPoint();
    if ( evt.getClickCount() == 2 ) {
       if ( SwingUtilities.isLeftMouseButton(evt) )
          fireAction(DOUBLE_CLICK_ACTION);
    }
    else if ( evt.getClickCount() == 1 ){
       if ( SwingUtilities.isLeftMouseButton(evt) )
          fireAction(LEFT_MOUSE_ACTION);
       else if ( SwingUtilities.isRightMouseButton(evt) ){
	  fireAction(RIGHT_MOUSE_PRESSED);
       }
       else if ( SwingUtilities.isMiddleMouseButton(evt) )
          fireAction(MIDDLE_MOUSE_ACTION);
    }

    if ( isMoveable ) {
       dragging = false;
       anchor = evt.getPoint();
       rect = getBounds();
       Graphics g = graph.getGraphics();
       if ( g != null && rect != null ) {
            g.setXORMode(graph.getBackground());
            g.drawRect(rect.x,rect.y,rect.width,rect.height);
       }
    }
  }

  public void mouseReleased(MouseEvent evt) {
    graphPoint = evt.getPoint();
    if ( SwingUtilities.isRightMouseButton(evt) ){
       fireAction(RIGHT_MOUSE_RELEASED);
    }
    else {
      if ( isMoveable ) {
       int x = evt.getX();
       int y = evt.getY();

       int xx = getLocation().x + x-anchor.x;
       int yy = getLocation().y + y-anchor.y;
       xx = ( xx > 0 ) ? xx : 0;
       yy = ( yy > 0 ) ? yy : 0;
       Graphics g = graph.getGraphics();
       if ( g != null && rect != null ) {
	  g.setXORMode(graph.getBackground());
	  g.drawRect(rect.x,rect.y,rect.width,rect.height);
       }
       this.setLocation(xx,yy);
      }

      if ( isSelectable && !dragging & SwingUtilities.isLeftMouseButton(evt) )
       setSelected(!isSelected);
      else if ( isMoveable && dragging ) {
      // reset cursor -- end of move
       JFrame f = (JFrame)SwingUtilities.getRoot(this);
       f.setCursor(lastCursor);
      }
    }
  }

  public void mouseDragged(MouseEvent evt) {
    graphPoint = evt.getPoint();
    if ( SwingUtilities.isRightMouseButton(evt) ){
       fireAction(RIGHT_MOUSE_DRAGGED);
    }
    else{
      if ( isMoveable ) {
       if ( !dragging ) {
          Frame f = (JFrame)SwingUtilities.getRoot(this);
          lastCursor = f.getCursor();
          f.setCursor(new Cursor(Cursor.MOVE_CURSOR));
          dragging = true;
        }
        else {
          int x = evt.getX(); int y = evt.getY();
          Graphics g = graph.getGraphics();
          if ( g != null && rect != null ) {
	     g.setXORMode(graph.getBackground());
	     g.drawRect(rect.x,rect.y,rect.width,rect.height);
	     rect.x = getLocation().x + x-anchor.x;
	     rect.y = getLocation().y + y-anchor.y;
	     g.drawRect(rect.x,rect.y,rect.width,rect.height);
          }
        }
      }
    }
  }

  public void addGraphIconListener(GraphIconListener x) {
    iconListeners.add(GraphIconListener.class, x);
  }
  public void removeGraphIconListener(GraphIconListener x) {
    iconListeners.remove(GraphIconListener.class, x);
  }
  protected void fireAction(int type) {
    GraphIconEvent evt = new GraphIconEvent(this);
    evt.setPoint( graphPoint);
    Object[] listeners = iconListeners.getListenerList();
    for(int i = listeners.length-2; i >= 0; i -= 2) {
       if (listeners[i] == GraphIconListener.class) {
          GraphIconListener cl = (GraphIconListener)listeners[i+1];
          switch(type) {
             case LOCATION_CHANGED:
                  cl.locationChanged(evt);
                  break;
             case DOUBLE_CLICK_ACTION:
                  cl.performLeftMouseDClickAction(evt);
                  break;
             case LEFT_MOUSE_ACTION:
                  cl.performLeftMouseAction(evt);
                  break;
             case RIGHT_MOUSE_PRESSED:
                  evt.setRightMousePressed();
                  cl.performRightMouseAction(evt);
                  break;
             case RIGHT_MOUSE_DRAGGED:
                  evt.setRightMouseDragged();
		  cl.performRightMouseAction(evt);
                  break;
             case RIGHT_MOUSE_RELEASED:
                  evt.setRightMouseReleased();
                  cl.performRightMouseAction(evt);
                  break;
             case MIDDLE_MOUSE_ACTION:
                  cl.performMiddleMouseAction(evt);
                  break;
          }
       }
    }
  }

}
