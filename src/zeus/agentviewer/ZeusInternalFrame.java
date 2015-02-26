

package zeus.agentviewer;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
public class ZeusInternalFrame extends JInternalFrame implements InternalFrameListener, ComponentListener{


  public ZeusInternalFrame (String str, boolean b1, boolean b2 ,boolean b3, boolean b4) {
    super (str, b1, b2,b3,b4);
    addInternalFrameListener(this);
    addComponentListener(this);
     }


 public void componentHidden(ComponentEvent ce) {
  repaint();
  }

  public void componentMoved (ComponentEvent ce) {
    repaint();
    }

  public void componentResized (ComponentEvent ce) {
    repaint();
    }

  public void componentShown (ComponentEvent ce) {
    repaint ();
    }

  public void internalFrameActivated (InternalFrameEvent we) {
    repaint();
    }


  public void internalFrameClosed  (InternalFrameEvent we) {
    repaint ();
    }


  public void internalFrameClosing (InternalFrameEvent we) {
    repaint();
    }


  public void internalFrameDeactivated (InternalFrameEvent we) {
    repaint ();
    }


  public void internalFrameIconified (InternalFrameEvent we) {
  ;}


  public void internalFrameDeiconified (InternalFrameEvent we) {
    repaint();
    }


  public void internalFrameOpened (InternalFrameEvent we) {
    repaint();
    }


}