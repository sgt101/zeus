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



package zeus.agentviewer;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class InternalFramesPanel extends JPanel {
    private JScrollPane scrollPane;
    private JDesktopPane desktopPane;
    final int WIDTH =750;
    final int HEIGHT = 300;
    final int DELTAw = 25;
    final int DELTAh = 80;
    public JFrame frame;
    boolean TILE = false;

//------------------------------------------------------------------------------
    public InternalFramesPanel(){
        super();
        setLayout(new BorderLayout());
        desktopPane = new JDesktopPane() {
           public Dimension getMinimumSize() {
              return new Dimension(10, 10);
           }

           public Dimension getPreferredSize() {
              return new Dimension(4000, 4000);// 700, 250
           }
        };
        desktopPane.setOpaque(false);
        scrollPane = new JScrollPane(desktopPane,
                                     ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS ){
                     public boolean isOpaque() {
                       return true;

                     }

        };
        this.add(BorderLayout.CENTER,scrollPane);
        this.setBorder(BorderFactory.createEtchedBorder(Color.black,Color.gray));
        this.setSize(WIDTH,HEIGHT);
    }
//------------------------------------------------------------------------------
    public InternalFramesPanel(AgentViewer frame){
      this();
      this.frame = frame;
    }
//------------------------------------------------------------------------------
    public InternalFramesPanel(BasicAgentViewer bframe){
      this();
      this.frame = bframe;
    }

//------------------------------------------------------------------------------
    public void addInternalFrame(JInternalFrame iframe){
        desktopPane.add(iframe,JLayeredPane.PALETTE_LAYER);
        positionInternalFrame(iframe);
    }
//------------------------------------------------------------------------------
    private void positionInternalFrame(JInternalFrame iframe){
        try {
           iframe.setSelected(true);
        }
	catch(java.beans.PropertyVetoException e2) {
        }
        desktopPane.validate();
        JInternalFrame[] allFrames = desktopPane.getAllFrames();

        if (allFrames.length > 1)
          cascade();
        else {
            
           Dimension frameSize = new Dimension (600,300); 
           int width = (frameSize.width * 2) /4;
           int height = (frameSize.height * 2) / 4;
           iframe.setBounds(20,20, width,height );
           iframe.setSize (width,height); 
        }
        repaint(); 
    }


//------------------------------------------------------------------------------
    private Vector getDisplayedFrames(){
          Vector displayFrames = new Vector();
          JInternalFrame[] allFrames = desktopPane.getAllFrames();
          for (int i=0; i < allFrames.length; i++) {
            if  (allFrames[i].isIcon() == false )
             displayFrames.addElement(allFrames[i]);

          }
        return displayFrames;
    }
//------------------------------------------------------------------------------
    public void tile() {
          Vector displayFrames = getDisplayedFrames();
          int numFrames = displayFrames.size();

          if ( numFrames== 0)
            return;

          Dimension frameSize = desktopPane.getSize();
          int width = frameSize.width;
          int height = frameSize.height / numFrames;
          Dimension newSize = new Dimension(width, height);
          int currentY = 0;
          Enumeration enum = displayFrames.elements();
          while (enum.hasMoreElements()) {
              JInternalFrame internalFrame = (JInternalFrame) enum.nextElement();
              internalFrame.setSize(newSize);
              internalFrame.setLocation(new Point(0, currentY));
              currentY = currentY + height;
          }
          TILE = true;
    }
//------------------------------------------------------------------------------

    public void cascade(){

          Vector displayFrames = getDisplayedFrames();
          int numFrames = displayFrames.size();
          int width, height;

          if ( numFrames== 0)
            return;

          Dimension frameSize = this.getSize();

          if ( numFrames > 1 ) {
             width = (frameSize.width * 4) /5;
             height = (frameSize.height * 2) / 3;
          }

          else {
             width = frameSize.width-10;
             height = frameSize.height-10;
           }

          Dimension newSize = new Dimension(width, height);
          int currentX = 0;
          int currentY = 0;
          Enumeration enum = displayFrames.elements();
          while (enum.hasMoreElements()) {
              JInternalFrame internalFrame = (JInternalFrame) enum.nextElement();
              internalFrame.setSize(newSize);
              internalFrame.setLocation(new Point(currentX, currentY));
              currentX = currentX + (width / (numFrames + 1));
              currentY = currentY + (height / (numFrames + 1));
          }
          TILE = false;
    }
//------------------------------------------------------------------------------
    public boolean tileOn(){
         return TILE;
    }
}