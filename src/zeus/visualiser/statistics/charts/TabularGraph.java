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



package zeus.visualiser.statistics.charts;

import java.util.*;
import java.awt.*;
import zeus.util.*;

public class TabularGraph implements DrawObject {
   protected static final double TINY = 1E-6;
   protected static final int    LEFT = 30;

   protected String title;
   protected Vector bars = new Vector();
   protected int x, y = 0;
   protected FontMetrics fm;
   protected Font font;
   protected int gap = 10;

   class Bar { String label; double[] values; }

   public TabularGraph() {
   }
   public TabularGraph(double[][] values, String[] labels, String title) {
      setData(values,labels,title);
   }
   public void setData(double[][] values, String[] labels, String title) {
      this.title = title;

      bars.removeAllElements();
      for( int i = 0; i < values.length; i++ ) {
         Bar bar = new Bar();
         bar.values = new double[values[i].length];
         for( int j = 0; j < bar.values.length; j++ )
            bar.values[j] = values[i][j];
         bar.label = labels[i];
         bars.addElement(bar);
      }
   }


   public void drawYourSelf(Graphics g) {
      font = new Font("Arial", Font.BOLD, 14);
      fm = g.getFontMetrics(font);
      g.setFont(font);
      int W  = fm.stringWidth(title);
      int H  = fm.getHeight();
      g.drawString(title,(x-W)/2,H);

      if ( bars.isEmpty() ) return;

      String str;
      Bar bar;
      int w = 0;
      font = new Font("Arial", Font.PLAIN, 12);
      fm = g.getFontMetrics(font);
      int h  = fm.getHeight() + gap;
      g.setFont(font);
      for( int i = 0; i < bars.size(); i++ ) {
         bar = (Bar) bars.elementAt(i);
         w = Math.max(w,fm.stringWidth(bar.label));
         for( int j = 0; j < bar.values.length; j++ ) {
            str = Misc.decimalPlaces(bar.values[j],2);
            w = Math.max(w,fm.stringWidth(str));
         }
      }
      w += gap;

      int x0, y0, x1, y1, x2, y2;
      g.drawLine(LEFT+w,3*H,LEFT+w,3*H+(bars.size()+1)*h);
      g.drawLine(LEFT,3*H+h,LEFT+(bars.size()+1)*w,3*H+h);

      x0 = LEFT+w+gap/2; y0 = 3*H + h - gap/2;
      y1 = y0;
      for( int i = 0; i < bars.size(); i++ ) {
         bar = (Bar) bars.elementAt(i);
         x1 = x0 + i*w;
         g.drawString(bar.label,x1,y1);
      }
      x0 = LEFT+gap/2; y0 = 3*H + h + h - gap/2;
      x1 = x0;
      for( int i = 0; i < bars.size(); i++ ) {
         bar = (Bar) bars.elementAt(i);
         y1 = y0 + i*h;
         g.drawString(bar.label,x1,y1);
         y2 = y1;
         for( int j = 0; j < bar.values.length; j++ ) {
            str = Misc.decimalPlaces(bar.values[j],2);
            x2 = (x1 + w) + j*w;
            g.drawString(str,x2,y2);
         }
      }
   }

   public void setXY(int xc, int yc) {
      x = xc; y = yc;
   }
}
