import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.agents.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.gui.*;
import zeus.concepts.fn.*;

public class WorldSim extends JFrame implements ZeusExternal {

   static final String[] names = {
      "North", "East", "West", "South"
   };

   protected AgentContext context = null;
   protected int[][] WorldXY, ValidXY;
   protected int[][][] RegionXY;
   protected JButton nextBtn;
   protected DrawPanel drawpane;
   protected Image     image;
   protected int       imageWidth, imageHeight;
   protected int       SIZE = 20;

   public WorldSim() {
      nextBtn = new JButton("Next");
      nextBtn.addActionListener(new SymAction());

      JPanel contentPane = (JPanel) getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(nextBtn,BorderLayout.SOUTH);

      drawpane = new DrawPanel();
      drawpane.setBackground(Color.white);
      contentPane.add(drawpane,BorderLayout.CENTER);

      setVisible(false);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });

      ImageIcon icon = new ImageIcon("gifs" + File.separator + "ukmap.gif");
      image = icon.getImage();
      imageWidth = image.getWidth(this);
      imageHeight = image.getHeight(this);

      WorldXY = new int[imageWidth/SIZE][imageHeight/SIZE];
      ValidXY = new int[imageWidth/SIZE][imageHeight/SIZE];
      RegionXY = new int[4][imageWidth/SIZE][imageHeight/SIZE];

      for(int i = 0; i < WorldXY.length; i++ )
      for(int j = 0; j < WorldXY[i].length; j++ )
         WorldXY[i][j] = ValidXY[i][j] = 0;

      for(int i = 0; i < RegionXY.length; i++ )
      for(int j = 0; j < RegionXY[i].length; j++ )
      for(int k = 0; k < RegionXY[i][j].length; k++ )
         RegionXY[i][j][k] = 0;


      // Read data
      try {
         BufferedReader in = new BufferedReader(new FileReader("coords.dat"));
         String s = "", line;

         line = in.readLine();
         while( line != null ) {
            s += line;
            line = in.readLine();
         }

         StringTokenizer st = new StringTokenizer(s);

         for(int i = 0; i < RegionXY.length; i++ )
         for(int j = 0; j < RegionXY[i].length; j++ )
         for(int k = 0; k < RegionXY[i][j].length; k++ )
            RegionXY[i][j][k] = Integer.parseInt(st.nextToken());

	 in.close();
      }
      catch(Exception e) {
         System.err.println("Error in coordinates data file");
         System.exit(0);
      }

      for(int i = 0; i < ValidXY.length; i++ )
      for(int j = 0; j < ValidXY[i].length; j++ ) {
         ValidXY[i][j] = 0;
	 for(int k = 0; k < RegionXY.length; k++ )
	    ValidXY[i][j] += RegionXY[k][i][j];
      }
   }

   public void exec(AgentContext context) {
      this.context = context;
      context.ResourceDb().addFactMonitor(new SymFactMonitor(),
         FactEvent.ADD_MASK|FactEvent.DELETE_MASK);
      drawpane.setPreferredSize(new Dimension(imageWidth+10,imageHeight+10));
      pack();
      setVisible(true);
      repaint();
  }

   protected class DrawPanel extends JPanel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         if ( g == null ) return;

         Dimension size = drawpane.getSize();
         g.clearRect(0,0,size.width,size.height);
         g.drawImage(image,5,5,this);

         Point origin = new Point(5,5);
         // draw gridlines
         Color c = g.getColor();
         g.setColor(Color.black);

         for(int i = 0; i < WorldXY.length; i++ ) {
	    g.drawLine(origin.x + SIZE*i, origin.y, 
                       origin.x + SIZE*i, origin.y + SIZE*WorldXY[i].length);
            for(int j = 0; j < WorldXY[i].length; j++ )
               g.drawLine(origin.x, origin.y + SIZE*j, 
                          origin.x+SIZE*WorldXY.length, origin.y + SIZE*j);
	 }
         g.drawLine(origin.x, origin.y + SIZE*WorldXY[0].length, 
                    origin.x + SIZE*WorldXY.length, 
                    origin.y + SIZE*WorldXY[0].length);
         g.drawLine(origin.x + SIZE*WorldXY.length, 
                    origin.y, origin.x + SIZE*WorldXY.length, 
                    origin.y + SIZE*WorldXY[0].length);

         for(int i = 0; i < WorldXY.length; i++ )
         for(int j = 0; j < WorldXY[i].length; j++ ) {
            if ( WorldXY[i][j] != 0 ) {
               g.setColor(ColorManager.getColor(WorldXY[i][j]-3));
               g.fillRect(origin.x + SIZE*i, origin.y + SIZE*j, SIZE, SIZE);
            }
         }
         g.setColor(c);
      }
   }

   protected class SymFactMonitor extends FactAdapter {
      public void factAddedEvent(FactEvent e) {
         Fact f1 = e.getFact();
         if ( f1.getType().equals("Fault") ) {
            int x = f1.getInt("x_location");
            int y = f1.getInt("y_location");
            WorldXY[x][y] = f1.getInt("type");
            repaint();
         }
      }
      public void factDeletedEvent(FactEvent e) {
         Fact f1 = e.getFact();
         if ( f1.getType().equals("Fault") ) {
            int x = f1.getInt("x_location");
            int y = f1.getInt("y_location");
            WorldXY[x][y] = 0;
            repaint();
         }
      }
   }

   protected String getOwner(int x, int y) {
      for(int i = 0; i < RegionXY.length; i++ )
         if ( RegionXY[i][x][y] != 0 )
            return names[i];
      return null;
   }

   protected class SymAction implements ActionListener {
      Random rand = new Random();
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if ( src == nextBtn ) {

            boolean can_do = false;

            for(int i = 0; i < WorldXY.length; i++ )
            for(int j = 0; j < WorldXY[i].length; j++ )
               can_do = can_do || (WorldXY[i][j] == 0 && ValidXY[i][j] != 0);

            if ( !can_do ) {
               JOptionPane.showMessageDialog(null,
                  "No fault can be created\nat current time",
                  "Information",JOptionPane.INFORMATION_MESSAGE);
               return;
            }

            boolean done = false;
            while( !done ) {
               int x = rand.nextInt(WorldXY.length);
               int y = rand.nextInt(WorldXY[0].length);

               if ( WorldXY[x][y] == 0 && ValidXY[x][y] != 0 ) {
                  int type = 3 + rand.nextInt(2);
                  String owner = getOwner(x,y);
  	          Fact fault = context.OntologyDb().getFact(Fact.FACT,"Fault");
                  fault.setValue("id",context.GenSym().plainId("fault"));
                  fault.setValue("priority","normal");
                  fault.setValue("x_location",x);
                  fault.setValue("y_location",y);
                  fault.setValue("type",type);
                  fault.setValue("owner",owner);
                  context.ResourceDb().add(fault);
                  done = true;
               }
            }
         }
      }
   }
}
