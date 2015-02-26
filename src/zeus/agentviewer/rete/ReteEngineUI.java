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



package zeus.agentviewer.rete;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.agentviewer.*;
import zeus.util.*;

public class ReteEngineUI extends ZeusInternalFrame {
   final int TOP_PANE_MIN_HEIGHT = 120;
   final int TOP_PANE_MIN_WIDTH = 500;
   final int BOTTOM_PANE_MIN_WIDTH = 50;
   final int BOTTOM_PANE_MIN_HEIGHT = 50;

   static int NUMBER_DISPLAYED = 0;

   protected ReteEngineDataModel engineBuffer;
   protected JTextArea  textarea;

   public ReteEngineUI(InternalFramesPanel deskTop,
                       ReteEngineDataModel engineBuffer) 
   {
      super("Rule Engine",true,true,true,true);
      setTitle("Rule Engine:" + (++NUMBER_DISPLAYED));
      String sep = System.getProperty("file.separator");
      String gifpath = SystemProps.getProperty("gif.dir") + "agentviewer" + sep;
      ImageIcon icon = new ImageIcon(gifpath + ViewerNames.RETE_ENG_IMG);
      setFrameIcon(icon);

      this.engineBuffer = engineBuffer;
      buildUI();
      deskTop.addInternalFrame(this);
      setVisible(true);
   }

   private void buildUI(){
      textarea = new JTextArea(engineBuffer,null,40,80);
      JScrollPane scrollpane = new JScrollPane(textarea);
      scrollpane.setPreferredSize(
         new Dimension(TOP_PANE_MIN_WIDTH, TOP_PANE_MIN_HEIGHT)
      );
      textarea.setEditable(false);
      JPanel contentPane = (JPanel)getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(BorderLayout.CENTER,scrollpane);
      pack();
   }

   void reSize(){
      setSize(getWidth()-2,getHeight());
      setSize(getWidth()+2,getHeight());
   }
}
