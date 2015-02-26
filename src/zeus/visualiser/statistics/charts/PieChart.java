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

/*
   Author :			Haydn R. Haynes
   Name	:			PieChart.java
   Inception Date :		18/07/1997
   Last Revision Date :		27/08/1997
   Revision Reason :		move slice percentage calcs into this class
   As Part of :			Statistics Tool for ZEUS Visualiser

   This is a Pie Chart drawing class.  It extends the DrawObject class which
   has been created to allow an interface which all drawing objects can use to
   allow their display within the Statistics Tool drawing area.
*/

import java.util.*;
import java.awt.*;
import zeus.gui.ColorManager;


public class PieChart implements DrawObject
{
   protected String title;
   protected Vector pieces = new Vector();
   protected int x,y = 0;
   protected FontMetrics fm;
   protected Font font;
   protected static double TINY = 1E-6;

/*
   This simple object is designed to hold slice information for the pie
   chart such as percentage (of all messages), the legend string associated
   with it and the colour that the slice will be draw on screen.  This will
   keep the colours consistent when the object is refreshed on screen.
*/
   class Slice { double value; String label; Color color; }

/*
   This is the default constructor for the Pie Chart object.  It takes as
   its parameters the title for the object as well as the percentage/legend
   information.  It dispenses of any 0.0 % values and constructs a vector of
   Slices to store the information for future refresh.
*/
   public PieChart() {
   }
   public PieChart(double[] values, String[] labels, String title) {
      setData(values,labels,title);
   }

   public void setData(double[] values, String[] labels, String title) {
      this.title = title;

      double sum = 0.0;
      int pos = 0;
      pieces.removeAllElements();
      for(int i=0;i<values.length;i++) sum += values[i];
      for(int j=0;j<values.length;j++) {
         if (Math.abs(values[j]-0.0) > TINY) {
             Slice slice = new Slice();
             slice.value = (100.0*values[j]/sum);
             slice.label = labels[j];
             slice.color = ColorManager.getColor(pos++);
             pieces.addElement(slice);
         }
      }
   }

/*
   This is the method responsible for drawing the object onto whatever
   Graphics object is passed to it.  It attempts to draw the object in as
   central a position as possible.
*/
   public void drawYourSelf(Graphics g) {
      int si = 0; int ly = y/5; int lx = x-(x/5); int rad = x/3;

      font = new Font("Arial", Font.BOLD, 14);
      fm = g.getFontMetrics(font);
      g.setFont(font);
      si = fm.stringWidth(title);
      g.drawString(title,(x-si)/2,y/13);

      if ( pieces.isEmpty() ) return;

      font = new Font("Arial", Font.PLAIN, 12);
      fm = g.getFontMetrics(font);
      g.setFont(font);
      g.drawString("Key",lx,ly);

      int sa = 90; ly += 25;
      Slice slice;
      for (int i=0;i<pieces.size();i++) {
         slice = (Slice)pieces.elementAt(i);
         int a = (int)(Math.round((360/100.0)*slice.value));
         g.setColor(slice.color);
	 g.fillArc(x/3,y/3,rad,rad,sa,a);
         doLegend(g,lx,ly,slice.label,Math.round(slice.value));
         sa += a; ly += 20;
      }
   }

/*
   Draw the legend information that corresponds to this pie chart.  It
   attempts to draw the legend on the right hand side of the screen.
*/
   protected void doLegend(Graphics g, int xc, int yc, String legend, long i) {
       g.fillRect(xc,yc,10,10);
       g.setColor(Color.black);
       if (i<10.0) { g.drawString("  " + i + " % :: " + legend,xc+20,yc+10); }
       else { g.drawString(i + " % :: " + legend,xc+20,yc+10); }
   }

/*
   This method sets the default height and width of the component that the
   Pie chart is to be drawn on.  It is necessary to allow the positioning
   of the pie chart, the header and the legend information.
*/
   public void setXY(int xc, int yc) {
      x = xc; y = yc;
   }

/*
   This is a method that allows the caller program to place miscellaneous
   text on the drawing pane.  This information is extra to the scale and
   legend information.  Note, there is no checking in the PieChart class
   to determine whether the text will overlap any on screen component.
*/
   public void userDraw(Graphics g, String text, int xc, int yc) {
      font = new Font("Arial", Font.PLAIN, 12);
      fm = g.getFontMetrics(font);
      g.setFont(font);
      g.drawString(text,xc,yc);
   }
}



