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

import java.util.*;

public class SelectPanel extends JPanel
                         implements MouseListener, MouseMotionListener {

    // protected Dimension psize = new Dimension(400,400);

    protected Point anchor    = new Point(0,0);
    protected Point stretched = new Point(0,0);
    protected Point last      = new Point(0,0);
    protected Point end       = new Point(0,0);

    protected boolean   firstStretch = true;
    protected boolean   regionSelected = false;
    protected Rectangle region = new Rectangle(0,0,0,0);
    protected Vector    items = new Vector();

    public SelectPanel() {
       super(true);
       this.addMouseListener(this);
       this.addMouseMotionListener(this);
       // setSize(psize);
       // validate();
    }
    public SelectPanel(Dimension dim) {
       this();
       // psize = new Dimension(dim); setSize(dim);
       setMaximumSize(dim);
       // validate();
    }
    public SelectPanel(int w, int h) {
       this();
       //psize = new Dimension(w,h); setSize(w,h);
       //validate();
    }

    public void mouseClicked(MouseEvent evt) {}
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}
    public void mouseMoved(MouseEvent evt) {}

    boolean source_ok = false;
    public void mousePressed(MouseEvent evt) {
       source_ok = (evt.getSource() == this);

       if ( !source_ok ) return;
       if ( SwingUtilities.isLeftMouseButton(evt) )
          this.anchor(evt.getPoint());
    }
    public void mouseDragged(MouseEvent evt) {
       if ( !source_ok ) return;
       if ( SwingUtilities.isLeftMouseButton(evt) )
          this.stretch(evt.getPoint());
    }
    public void mouseReleased(MouseEvent evt) {
       if ( !source_ok ) return;
       if ( SwingUtilities.isLeftMouseButton(evt) )
          this.end(evt.getPoint());
       source_ok = false;
    }

    public void drawRegion(Graphics graphics) {
       graphics.drawRect(region.x, region.y, region.width, region.height);
    }

    public void clearRegion(Graphics graphics) {
       Color c = graphics.getColor();
       graphics.setColor(this.getBackground());
       graphics.drawRect(region.x, region.y, region.width, region.height);
       graphics.setColor(c);
    }
    public void drawLast(Graphics graphics) {
       Rectangle rect = lastBounds();
       graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
    }
    public void drawNext(Graphics graphics) {
       Rectangle rect = currentBounds();
       graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    public Point getAnchor   () { return anchor;    }
    public Point getStretched() { return stretched; }
    public Point getLast     () { return last;      }
    public Point getEnd      () { return end;       }

    public void anchor(Point p) {
       firstStretch = true;
       stretched.x = last.x = anchor.x = p.x;
       stretched.y = last.y = anchor.y = p.y;

       if ( regionSelected && !region.contains(anchor.x,anchor.y) ) {
          Graphics g = this.getGraphics();
          if (g != null) clearRegion(g);
          regionSelected = false;
       }
    }
    public void stretch(Point p) {
       last.x      = stretched.x;
       last.y      = stretched.y;
       stretched.x = p.x;
       stretched.y = p.y;

       Graphics g = this.getGraphics();
       if ( g != null && !regionSelected ) {
          g.setXORMode(this.getBackground());

          if ( firstStretch ) firstStretch = false;
          else                drawLast(g);
          drawNext(g);
       }
    }
    public void end(Point p) {
       last.x = end.x = p.x;
       last.y = end.y = p.y;

       Graphics g = this.getGraphics();
       if ( g != null && !regionSelected ) {
          g.setXORMode(this.getBackground());
          drawLast(g);
          region.setBounds( stretched.x < anchor.x ? stretched.x : anchor.x,
                            stretched.y < anchor.y ? stretched.y : anchor.y,
                            Math.abs(stretched.x - anchor.x),
                            Math.abs(stretched.y - anchor.y));
          if ( findItems() ) {
             regionSelected = true;
             drawRegion(g);
          }
       }
    }
    public Rectangle currentBounds() {
       return new Rectangle(stretched.x < anchor.x ? stretched.x : anchor.x,
                            stretched.y < anchor.y ? stretched.y : anchor.y,
                            Math.abs(stretched.x - anchor.x),
                            Math.abs(stretched.y - anchor.y));
    }

    public Rectangle lastBounds() {
       return new Rectangle(last.x < anchor.x ? last.x : anchor.x,
                            last.y < anchor.y ? last.y : anchor.y,
                            Math.abs(last.x - anchor.x),
                            Math.abs(last.y - anchor.y));
    }

    public Vector boundedItems() {
       if ( regionSelected ) return items;
       return new Vector();
    }

    protected boolean findItems() {
       Component[] all_items = getComponents();
       items.removeAllElements();

       for( int i = 0; i < all_items.length; i++ ) {
          Rectangle r = all_items[i].getBounds();
          if ( region.contains(r.x,r.y) &&
               region.contains(r.x+r.width,r.y+r.height) )
             items.addElement(all_items[i]);
       }
       return !items.isEmpty();
    }
}
