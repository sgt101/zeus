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
import javax.swing.*;
import java.util.*;

public class MovePanel extends SelectPanel{
    protected Cursor lastCursor;

    public MovePanel() {
       super();
    }
    public MovePanel(Dimension d) {
       super(d);
    }
    public MovePanel(int w, int h) {
       super(w,h);
    }

    public void anchor(Point p) {
       super.anchor(p);

       // Moving
       if ( regionSelected && region.contains(anchor.x,anchor.y) ) {
          JFrame f = (JFrame)SwingUtilities.getRoot(this);
          lastCursor = f.getCursor();
          f.setCursor(new Cursor(Cursor.MOVE_CURSOR));
       }
    }
    public void stretch(Point p) {
       super.stretch(p);

       Graphics g = this.getGraphics();
       if ( g != null && regionSelected ) {
          g.setXORMode(this.getBackground());

          drawRegion(g);
          region.translate(stretched.x-last.x,stretched.y-last.y);
          drawRegion(g);
       }
    }
    public void end(Point p) {
       boolean prior = regionSelected; // hack since the super.end() method
                                       // determines the new state of
                                       // regionSelected
       super.end(p);

       Graphics g = this.getGraphics();
       if ( g != null && prior ) {
          g.setXORMode(this.getBackground());
          drawRegion(g);
          region.translate(stretched.x-last.x,stretched.y-last.y);
          moveItems();
          drawRegion(g);

          // reset cursor -- end of move
          JFrame f = (JFrame)SwingUtilities.getRoot(this);
          f.setCursor(lastCursor);
       }
    }

    protected void moveItems() {
       for(int i = 0; i < items.size(); i++ ) {
          Component comp = (Component)items.elementAt(i);
          Point p = comp.getLocation();
          comp.setLocation(p.x + end.x-anchor.x, p.y + end.y-anchor.y);
      }
   }
}
