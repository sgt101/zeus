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



package zeus.visualiser.society;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.graph.*;

public class AnimationQueue extends Thread {
   static final int  LETTER = 0;
   static final int  ARROWS = 1;
   /** 
    if we attempt to add more than FLUSH_QUEUE_LIMIT items to the queue it gets 
    emptied - to stop it overflowing. 
    */
   static int FLUSH_QUEUE_LIMIT = 100; 

   static int   count = 0;

   long  speed  = 80;
   int   length = 10;
   int   mode   = LETTER;
   Graph graph;

   protected Vector queue = new Vector();
   protected boolean running = true;

   public AnimationQueue(Graph graph) {
      this.graph = graph;
      this.setPriority(Thread.NORM_PRIORITY+2);
      this.start();
   }

   synchronized void flush()          { queue.removeAllElements(); }
   void              setSpeed(long s) { speed = s; }
   long              getSpeed()       { return speed; }
   int               getMode()        { return mode; }
   int               getLength()      { return length; }

   void setMode(int s) {
      switch(s) {
         case LETTER:
         case ARROWS:
              mode = s;
              break;
         default:
              Core.USER_ERROR("Illegal animation type " + s);
      }
   }

   void setLength(int L) {
      Core.ERROR(L > 0,1,this);
      length = L;
   }

   void terminate() {
      running = false;
   }

   public synchronized void add(Performative msg, GraphNode sender,
                                GraphNode receiver, Color color) {
      Core.ERROR(msg,1,this);
      Core.ERROR(sender,2,this);
      Core.ERROR(receiver,3,this);
      Core.ERROR(color,4,this);
       if (queue.size()>FLUSH_QUEUE_LIMIT) queue.removeAllElements();
      AnimationQueueItem item =
         new AnimationQueueItem(msg,sender,receiver,color);

      if ( queue.isEmpty() ) {
         queue.addElement(item);
         debug ("in add empty - notifying"); 
         notify();
         return;
      }

      double s = msg.getSendTime().getTime();

      for(int i = 0; i < queue.size(); i++ ) {
         AnimationQueueItem v = (AnimationQueueItem)queue.elementAt(i);
         String id = v.id;
         double s1 = v.sendTime;
         double e1 = v.receiveTime;
         if ( s >= e1 ) {
            AnimationQueueConstraint constraint =
               new AnimationQueueConstraint(id,AnimationQueueConstraint.FINISH);
            item.addConstraint(constraint);
         }
         else if ( s < e1 && s > s1 ) {
            AnimationQueueConstraint constraint =
               new AnimationQueueConstraint(id,AnimationQueueConstraint.START);
            item.addConstraint(constraint);
         }
      }
      queue.addElement(item);
      debug ("in add more than one - notifying"); 
      notify();
   }


   private void debug (String str) { 
      //  System.out.println("AnimationQueue >> " + str); 
   }

   public void run() {
      AnimationQueueItem item;

      while( running ) {
         debug("in animation queue"); 
         synchronized(this) {
            while ( queue.isEmpty() ) {
               try {
                  wait();
                  debug ("notified animation thread - waking"); 
               }
               catch(InterruptedException e) {
               }
            }
            for(int i = 0; i < queue.size(); i++ ) {
               item = (AnimationQueueItem)queue.elementAt(i);
               if ( item != null ) {
                  if ( item.isDone() )
                     queue.removeElementAt(i--);
                  else if ( item.isRunning() )
                     item.next();
                  else if ( !item.isRunning() && item.evalConstraints(queue) )
                     item.start();
               }
            }
         }
         try {
            sleep(speed);
         }
         catch(InterruptedException e) {
         }
         System.out.println("stopped sleeping"); 
      }
   }

   class AnimationQueueConstraint {
      static final int FINISH = 1;
      static final int START = 0;

      String id;
      int    constraint;

      AnimationQueueConstraint(String id, int constraint) {
         this.id = id;
         this.constraint = constraint;
      }
   }

   class AnimationQueueItem {
      Performative msg;
      GraphNode    sender, receiver;
      String       id;
      Vector       constraints = new Vector();
      Animation    duke = null;
      double       sendTime, receiveTime;
      Color        color;

      AnimationQueueItem(Performative msg, GraphNode sender,
                         GraphNode receiver, Color color) {
         this.msg = msg;
         this.sendTime = msg.getSendTime().getTime();
         this.receiveTime = msg.getReceiveTime().getTime();
         this.sender = sender;
         this.receiver = receiver;
         this.color = color;
         this.id = new String("AnimationQueueItemId-" + (++count));
      }

      void addConstraint(AnimationQueueConstraint constr) {
         constraints.addElement(constr);
      }

      boolean isDone()    { return duke != null && duke.isDone(); }
      boolean isRunning() { return duke != null && duke.isRunning(); }

      void next() {
         if ( duke != null )
            duke.next();
      }

      boolean evalConstraints(Vector queue) {
         boolean eval = true;
         AnimationQueueConstraint c;
         AnimationQueueItem item;

         for(int i = 0; i < constraints.size(); i++ ) {
            c = (AnimationQueueConstraint) constraints.elementAt(i);
            for(int j = 0; j < queue.size(); j++ ) {
               item = (AnimationQueueItem) queue.elementAt(j);
               if ( item.id.equals(c.id) ) {
                  if ( c.constraint == AnimationQueueConstraint.START )
                     eval = eval && item.isRunning();
                  else if ( c.constraint == AnimationQueueConstraint.FINISH )
                     eval = eval && item.isDone();
                  break;
               }
            }
            if ( !eval ) return eval;
         }
         return eval;
      }

      void start() {
         duke = new Animation(sender,receiver,color,msg);
      }
   }


   class ImageLabel extends JPanel{
       Color color;
       JLabel label;
       public  ImageLabel(Color color, Image image, String msg){
          setLayout(new GridLayout(1,1));
	  label = new JLabel(msg,new ImageIcon(image),JLabel.CENTER);
          label.setOpaque(true);
          label.setBackground(color);
          label.setFont(new Font("Helvetica", Font.PLAIN, 10));
          label.repaint();
	  this.add(label);
          Image img =  Toolkit.getDefaultToolkit().getImage(
            SystemProps.getProperty("gif.dir") + "visualiser" +
            File.separator + "border.gif");
          this.setBorder(BorderFactory.createMatteBorder(2,4,2,4,
             new ImageIcon(img)));
          this.setPreferredSize(new Dimension(70,25));
       }
       public void setLetterColor(Color col){
         label.setBackground(col);
         label.repaint();
       }
   }


   public class Animation {

      int  NUM_COUNT = 1;
      int  LETTTER_WIDTH = 40;

      private GraphNode        source, target;
      private Color            color;
      private Point            p2, p1;
      private boolean          done = false;
      private boolean          running = false;
      private int              count = 0;
      private Image            image = null;
      ImageLabel  imageLabel;

      public Animation(GraphNode source, GraphNode target, 
                       Color color,Performative msg) {

         Core.ERROR(source,1,this);
         Core.ERROR(target,2,this);
         Core.ERROR(color,3,this);

         this.source = source;
         this.target = target;
         this.color  = color;

         Rectangle a = graph.getBounds(source);
         Rectangle b = graph.getBounds(target);
         p1 = new Point(a.x+a.width/2,a.y+a.height/2);
         p2 = new Point(b.x+b.width/2,b.y+b.height/2);

         image = Toolkit.getDefaultToolkit().getImage(
            SystemProps.getProperty("gif.dir") + "visualiser" +
            File.separator + "anim.gif");

         imageLabel = new ImageLabel(color,image,msg.getType());
         imageLabel.setVisible(false);
         graph.add(imageLabel);
         graph.validate();

         running = true;
         switch( mode ) {
            case LETTER:
                 imageLabel.setVisible(true);
		 drawLetter(p1,imageLabel);
                 break;
            case ARROWS:
                 drawArrows(p1,p2);
                 break;
         }
      }

      boolean isDone()    { return done;    }
      boolean isRunning() { return running; }

      void next() {
         Rectangle a, b;
         double phi, dist, len;

         if ( !done && (mode == LETTER) ) {
            if ( !(imageLabel.isVisible()) )
               imageLabel.setVisible(true);

	    b = graph.getBounds(target);
            p2 = new Point(b.x+b.width/2,b.y+b.height/2);
            dist  = Math.sqrt(Math.pow((p2.x-p1.x),2) +
                    Math.pow((p2.y-p1.y),2));
            phi = ArrowData.GetAngle((double)p1.x, (double)p1.y,
                                     (double)p2.x, (double)p2.y);
            len = dist > length ? length : dist;
            p1.x += (int) (len*Math.cos(phi));
            p1.y += (int) (len*Math.sin(phi));

            drawLetter(p1,imageLabel);
            done = b.contains(p1);
         }
         else if ( !done && mode == ARROWS ) {
            imageLabel.setVisible(false);
            graph.repaint();
	    drawArrows(p1,p2);
            a = graph.getBounds(source);
            b = graph.getBounds(target);
            p1 = new Point(a.x+a.width/2,a.y+a.height/2);
            p2 = new Point(b.x+b.width/2,b.y+b.height/2);
            drawArrows(p1,p2);
            done = ((++count) == NUM_COUNT);
         }

         if ( done ) {
            graph.remove(imageLabel);
	    running = false;
            if (mode == ARROWS) drawArrows(p1,p2);

         }
      }

      protected void drawArrows(Point p1, Point p2) {
         Graphics g = graph.getGraphics();
         if ( g != null ) {
            double phi, dist, len;

            g.setXORMode(graph.getBackground());
            Color col = g.getColor();
            g.setColor( this.color );

            drawThickLine(g,p1,p2);

            Point x1 = new Point(p1.x,p1.y);
            Point x2 = new Point(0,0);

            dist  = Math.sqrt(Math.pow((p2.x-x1.x),2) +
                              Math.pow((p2.y-x1.y),2));
            phi = ArrowData.GetAngle((double)x1.x, (double)x1.y,
                                     (double)p2.x, (double)p2.y);
            len = dist > 1.5*length ? 1.5*length : dist;
            x2.x = x1.x + (int) (len*Math.cos(phi));
            x2.y = x1.y + (int) (len*Math.sin(phi));
            drawArrow(g,x1,x2);
            while( len >= 1.5*length ) {
               x1.x = x2.x; x1.y = x2.y;
               dist  = Math.sqrt(Math.pow((p2.x-x1.x),2) +
                                 Math.pow((p2.y-x1.y),2));
               phi = ArrowData.GetAngle((double)x1.x, (double)x1.y,
                                        (double)p2.x, (double)p2.y);
               len = dist > 1.5*length ? 1.5*length : dist;
               x2.x = x1.x + (int) (len*Math.cos(phi));
               x2.y = x1.y + (int) (len*Math.sin(phi));
               drawArrow(g,x1,x2);
            }
            g.setColor( col );
         }
      }

      protected void drawThickLine(Graphics g, Point p1, Point p2) {
         if ( p1 != null && p2 != null && g != null ) {
            int w = 2;
            double theta = ArrowData.GetAngle((double)p1.x,(double)p1.y,
                                              (double)p2.x,(double)p2.y);
            double alpha = theta + Math.PI/2.0;
            double beta  = theta - Math.PI/2.0;
            int x, y;
            Polygon pg = new Polygon();

            x = (int)((double)p1.x + (double)(w*Math.cos(alpha)));
            y = (int)((double)p1.y + (double)(w*Math.sin(alpha)));
            pg.addPoint(x,y);

            x = (int)((double)p1.x + (double)(w*Math.cos(beta)));
            y = (int)((double)p1.y + (double)(w*Math.sin(beta)));
            pg.addPoint(x,y);

            x = (int)((double)p2.x + (double)(w*Math.cos(beta)));
            y = (int)((double)p2.y + (double)(w*Math.sin(beta)));
            pg.addPoint(x,y);

            x = (int)((double)p2.x + (double)(w*Math.cos(alpha)));
            y = (int)((double)p2.y + (double)(w*Math.sin(alpha)));
            pg.addPoint(x,y);

            g.fillPolygon(pg);
         }
      }


      protected void drawArrow(Graphics g, Point p1, Point p2) {
          Point[] pts;

          g.drawLine( p1.x, p1.y, p2.x, p2.y );
          pts = ArrowData.getPoints((double)p1.x, (double)p1.y,
                                    (double)p2.x, (double)p2.y);
          for( int i = 0; i < 2; i++ )
             g.drawLine( pts[i].x, pts[i].y, pts[i+1].x, pts[i+1].y);
      }

     protected void drawLetter(Point c, ImageLabel label) {
         label.setLocation(c);
         graph.repaint();
     }

   }
}
