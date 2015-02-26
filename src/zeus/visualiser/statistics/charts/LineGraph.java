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
import zeus.gui.ColorManager;


public class LineGraph implements DrawObject {
   protected static final int    TYPE1 = 0;
   protected static final int    TYPE2 = 1;
   protected static final double TINY = 1E-6;
   protected static final int    LEFT = 100;
   protected static final int    STEP = 5;
   protected static final int    TICK_SIZE = 10;
   protected static final int    KEY_DEPTH = 20;

   protected String title;
   protected Vector bars = new Vector();
   protected int x, y = 0;
   protected FontMetrics fm;
   protected Font font;
   protected int    gap = 10;
   protected double max = 0.0, min = 0.0, sum = 0.0;
   protected int type = -1;
   protected boolean[] is_valid;
   protected String[] keys;

   class Bar { double value; String label; Color color; double[] values; }

   public LineGraph() {
   }
   public LineGraph(double[] values, String[] labels, String title) {
      setData(values,labels,title);
   }
   public LineGraph(double[][] values, String[] labels, String[] keys,
                    String title) {
      setData(values,labels,keys,title);
   }

   public void setData(double[] values, String[] labels, String title) {
      type = TYPE1;
      this.title = title;

      int pos = 0;
      bars.removeAllElements();
      max = min = sum = 0.0;
      for(int j=0;j<values.length;j++) {
         if (Math.abs(values[j]-0.0) > TINY) {
             max = Math.max(max,values[j]);
             min = Math.min(min,values[j]);
             Bar bar = new Bar();
             bar.value = values[j];
             bar.label = labels[j];
             bar.color = ColorManager.getColor(pos++);
             bars.addElement(bar);
         }
      }
   }
   public void setData(double[][] values, String[] labels,
                       String[] keys, String title) {
      type = TYPE2;
      this.title = title;

      bars.removeAllElements();
      max = min = sum = 0.0;
      is_valid = new boolean[keys.length];
      this.keys = new String[keys.length];
      for( int i = 0; i < keys.length; i++ ) {
         this.keys[i] = keys[i];
         is_valid[i] = false;
      }
      double s;
      for( int i = 0; i < values.length; i++ ) {
         s = 0.0;
         for( int j = 0; j < values[i].length; j++ ) {
            if (Math.abs(values[i][j]-0.0) > TINY) {
               is_valid[j] = true;
               s += values[i][j];
               max = Math.max(max,values[i][j]);
               min = Math.min(min,values[i][j]);
            }
         }
         if ( Math.abs(s-0.0) > TINY ) {
            Bar bar = new Bar();
            bar.values = new double[values[i].length];
            for( int k = 0; k < bar.values.length; k++ )
               bar.values[k] = values[i][k];
            bar.label = labels[i];
            bars.addElement(bar);
         }
      }
   }

   public void drawYourSelf(Graphics g) {
      font = new Font("Arial", Font.BOLD, 14);
      fm = g.getFontMetrics(font);
      g.setFont(font);
      int w  = fm.stringWidth(title);
      int h  = fm.getHeight();
      g.drawString(title,(x-w)/2,h);

      if ( bars.isEmpty() ) return;

      int mw = 0;
      Bar bar;
      font = new Font("Arial", Font.PLAIN, 12);
      fm = g.getFontMetrics(font);
      g.setFont(font);
      for( int i = 0; i < bars.size(); i++ ) {
         bar = (Bar) bars.elementAt(i);
         mw = Math.max(mw,fm.stringWidth(bar.label));
      }
      mw += 10;
      int length = (mw+gap)*bars.size();

      double max_h = min > 0.0 ? max : Math.abs(max-min);

      int x0 = 0, y0 = 0, x1, y1, x2, y2, xp, yp, px = 0, py = 0;
      switch( type ) {
         case TYPE1:
            g.drawLine(LEFT,3*h,LEFT,y-2*h);
            x0 = LEFT;
            y0 = (int)(3.0*h + (max/max_h)*(y-5.0*h));
            x1 = x0 + length;
            y1 = y0;
            g.drawLine(x0,y0,x1,y1);
            g.setColor(Color.black);
            for( int i = 0; i < bars.size(); i++ ) {
               bar = (Bar) bars.elementAt(i);
               x1 = x0 + (mw+gap)*i + (mw+gap)/2;
               y1 = (int)(y0 - (bar.value/max)*(y0-3.0*h));
               GraphicsSymbol.drawSymbol(g,0,10,x1,y1);
               if ( i != 0 ) g.drawLine(px,py,x1,y1);
               px = x1; py = y1;
               w = fm.stringWidth(bar.label);
               g.drawString(bar.label,x1-w/2,y0 + h);

            }
            break;
         case TYPE2:
            g.drawLine(LEFT,3*h,LEFT,y-2*h);
            x0 = LEFT;
            y0 = (int)(3.0*h + (max/max_h)*(y-5.0*h));
            x1 = x0 + length;
            y1 = y0;
            g.drawLine(x0,y0,x1,y1);
            for( int j = 0; j < keys.length; j++ ) {
               for( int i = 0; i < bars.size(); i++ ) {
                  bar = (Bar) bars.elementAt(i);
                  x1 = x0 + (mw+gap)*i + (mw+gap)/2;
                  y1 = (int)(y0 - (bar.values[j]/max)*(y0-3.0*h));
                  g.setColor(ColorManager.getColor(j));
                  GraphicsSymbol.drawSymbol(g,j,10,x1,y1);
                  if ( i != 0 ) g.drawLine(px,py,x1,y1);
                  px = x1; py = y1;
               }
            }
            for( int i = 0; i < bars.size(); i++ ) {
               bar = (Bar) bars.elementAt(i);
               x1 = x0 + (mw+gap)*i + (mw+gap)/2;
               w = fm.stringWidth(bar.label);
               g.setColor(Color.black);
               g.drawString(bar.label,x1-w/2,y0+h);
            }
            break;
         default:
            return;
      }
      // draw y-scale
      double v, dv;
      dv = (max/STEP);
      dv = ((int)(dv+0.51) == 0) ? dv : (double)((int)(dv+0.51));
      int step = (int)((y0-3.0*h)*dv/max);
      g.setColor(Color.black);
      v = 0.0;
      xp = x0; yp = y0;
      while( v-TINY <= max ) {
         String yval = Misc.decimalPlaces(v,2);
         g.drawLine(xp,yp,xp-TICK_SIZE,yp);
         w = fm.stringWidth(yval);
         g.drawString(yval,xp-TICK_SIZE-w-5,yp);
         v += dv;
         yp -= step;
      }
      if ( min < 0.0 ) {
         v = -dv;
         xp = x0; yp = y0 + step;
         while( v > min ) {
            String yval = Misc.decimalPlaces(v,2);
            g.drawLine(xp,yp,xp-TICK_SIZE,yp);
            w = fm.stringWidth(yval);
            g.drawString(yval,xp-TICK_SIZE-w-5,yp);
            v -= dv;
            yp += step;
         }
      }
      // add key
      if ( type == TYPE2 ) {
         x1 = x0 + length + LEFT; y1 = 3*h;
         h = fm.getHeight();
         int dh = Math.max(h+10,KEY_DEPTH);
         g.setColor(Color.black);
         g.drawString("Key",x1,y1);
         for( int i = 0; i < keys.length; i++ ) {
            if ( is_valid[i] ) {
               y1 += dh;
               g.setColor(ColorManager.getColor(i));
               GraphicsSymbol.drawSymbol(g,i,10,x1+(dh-3)/2,y1+(dh-3)/2);
               g.setColor(Color.black);
               g.drawString(keys[i],x1+dh,y1+(dh-3)/2);
            }
         }
      }
   }
   public void setXY(int xc, int yc) {
      x = xc; y = yc;
   }

}
