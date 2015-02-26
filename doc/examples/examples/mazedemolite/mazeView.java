/**
 * mazeView.java
 * Implements the responsibilities of the Environment agent's View role
 * Notice that no Zeus code is imported, as this is not an agent-level component
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;


public class mazeView extends JPanel implements ComponentListener, MouseListener, MouseMotionListener
{
  private int lastX = 0;
  private int lastY = 0;
  private boolean listeningForUpdates = false;
  private boolean agentUpdate = true;
  private boolean mazeUpdate = true;
  private mazeModel mazeInUse = null;


  public  void mouseClicked (MouseEvent me)
  {
    int xMouse = me.getX();
    int yMouse = me.getY();
    Dimension thisPanelSize = getSize();
    int xGrid = xMouse / (thisPanelSize.width / mazeInUse.mazeWidth);
    int yGrid = yMouse / (thisPanelSize.height / mazeInUse.mazeHeight);
    if (me.getModifiers()==me.BUTTON1_MASK){
        mazeInUse.changeStatus(xGrid, yGrid);}
    if (me.getModifiers()== me.BUTTON3_MASK)  {
        mazeInUse.setTarget(xGrid, yGrid); }

    repaint();
  }

  private boolean thisSquareLastChanged(int thisX, int thisY)
  {
    if ((thisX == lastX) &&(thisY == lastY)) return true;
    else return false;
  }

  public void mouseMoved(MouseEvent me) {;}

  public void mouseDragged (MouseEvent me)
  {
    int xMouse = me.getX();
    int yMouse = me.getY();
    Dimension thisPanelSize = getSize();
    int xGrid = xMouse / (thisPanelSize.width / mazeInUse.mazeWidth);
    int yGrid = yMouse / (thisPanelSize.height / mazeInUse.mazeHeight);
    if (me.getModifiers() == MouseEvent.BUTTON1_MASK &&
                             !thisSquareLastChanged(xGrid,yGrid))
    {
      mazeInUse.changeStatus(xGrid, yGrid);
      lastX = xGrid;
      lastY = yGrid;
    }
    repaint();
  }

  public void mousePressed (MouseEvent me)  { repaint(); }
  public void mouseReleased (MouseEvent me) {}
  public void mouseEntered (MouseEvent me)  {;}
  public void mouseExited (MouseEvent me)   {;}

  public void componentResized (ComponentEvent ev) { repaint(); }
  public void componentMoved (ComponentEvent ev)   { repaint(); }
  public void componentShown (ComponentEvent ev)   { repaint(); }
  public void componentHidden (ComponentEvent ev)  {;}


  public mazeView (mazeModel mazeInUse)
  {
    super();
    this.mazeInUse = mazeInUse;
  }

  private void drawMaze(Graphics g)
  {
    Dimension thisPanelSize = getSize();
    int noPixGridX = thisPanelSize.width / mazeInUse.mazeWidth;
    int noPixGridY = thisPanelSize.height / mazeInUse.mazeHeight;
    // first rub the maze and contents out
    g.setColor(Color.white);
    g.fillRect(0,0,thisPanelSize.width, thisPanelSize.height);
    //now redraw it
    int row = 0;
    int col = 0;

    for (col = 0; col < mazeInUse.mazeHeight; col++)
    {

      for (row = 0; row < mazeInUse.mazeWidth;row++)
      {
        if (mazeInUse.thisMaze[row][col]) {
          g.setColor(Color.blue);
          g.fillRect(col *noPixGridX, row * noPixGridY, noPixGridX, noPixGridY);
        }

        if (mazeInUse.isTarget(row,col)) {
          g.setColor(Color.yellow);
          g.fillRect(col * noPixGridX, row * noPixGridY, noPixGridX, noPixGridY);
        }

        g.setColor(Color.black);
        g.drawRect(col *noPixGridX, row * noPixGridY, noPixGridX, noPixGridY);
      }
    }
    mazeUpdate = false;
    // ensure that the agents are added
    agentUpdate = true;
  }



  private void drawAgent(Graphics g)
  {
    Dimension thisPanelSize = getSize();
    int noPixGridX = thisPanelSize.width / mazeInUse.mazeWidth;
    int noPixGridY = thisPanelSize.height / mazeInUse.mazeHeight;
    Dimension currentAgentPos = null;
    Enumeration allAgents = mazeInUse.agents.elements();
    int row = 0;
    int col = 0;

    g.setColor(Color.red);
    while (allAgents.hasMoreElements())
    {
      currentAgentPos = (Dimension) allAgents.nextElement();
      row = currentAgentPos.height * noPixGridY;
      col = currentAgentPos.width * noPixGridX;
      g.fillOval(col+noPixGridX/4,row+noPixGridY/4, noPixGridX/2, noPixGridY/2);
    }
    agentUpdate = false; // only do this when necessary
  }



  public void paint (Graphics g)
  {
    drawMaze(g);
    drawAgent(g);
  }
}
