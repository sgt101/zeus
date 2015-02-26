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
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


import zeus.util.*;
import zeus.concepts.*;
import zeus.agents.ZeusAgentUI;
import zeus.gui.help.*;
import zeus.agents.ZeusAgent;
import zeus.actors.event.*;
import zeus.actors.*;
import zeus.agentviewer.mail.*;
import zeus.agentviewer.msghandler.*;



public class BasicAgentViewer extends JFrame implements ActionListener,
                                            ComponentListener,
                                            ZeusAgentUI
{


   JPanel contentPane;
   public JPanel topPanel;
   InternalFramesPanel bottomPanel;
   String fsep = System.getProperty("file.separator");
   String IMAGEPATH = SystemProps.getProperty("gif.dir") + "agentviewer" + fsep;
   final int ICONh  = 40;
   final int ICONw =35;
   final int HEADERw = 165;
   final int HEADERh = 43;



    private JMenuBar           menuBar;
    private JMenu              fileMenu, helpMenu;
    private JMenu              viewMenu;
    private JMenuItem          exit;
    private JMenuItem          about, aboutZeus;
    private JMenuItem          tile, cascade;
    private JMenuItem          nativeLF, metalLF;
    private JToolBar toolBar;

    private AgentButton mailInBtn,mailOutBtn, msgHandlerBtn;
    AgentContext context = null;
    Thread agentViewerThread;
    MailInTableModel mailInBuffer;
    MailOutTableModel mailOutBuffer;
    MsgHandlerTableModel msgHandlerBuffer;


//------------------------------------------------------------------------------
      public BasicAgentViewer() {

          super("Basic Zeus Agent Viewer");


          createMenus();
          ImageIcon icon = new ImageIcon(IMAGEPATH + ViewerNames.VIEWER_IMG);
          setIconImage(icon.getImage());
          setJMenuBar(menuBar);
          setContentPane();
          getNativeUI();
          addWindowListener(
           new WindowAdapter() {
              public void windowClosing(WindowEvent evt) { exitBtnFn(); }
             }
          );

          addComponentListener(this);

          setVisible(true);
          setSize(600,500);
          pack();

      }
//------------------------------------------------------------------------------

     public void showHelp()
     {
        HelpWindow helpWin;
        Point dispos = getLocation();
        helpWin = new HelpWindow(this, dispos, "visualiser", "Agent Viewer");
        helpWin.setSize(new Dimension(600, getHeight()));
        helpWin.setLocation(dispos.x+24, dispos.y+24);
        helpWin.validate();
     }
//------------------------------------------------------------------------------
     public void About()
     {
       HelpWindow helpWin;
       Point dispos = getLocation();
       helpWin = new HelpWindow(this, dispos, "visualiser", "About");
       helpWin.setSize(new Dimension(440, 440));
       helpWin.setLocation(dispos.x+24, dispos.y+24);
       helpWin.setTitle("About ZEUS ...");
		   helpWin.validate();
     }


//------------------------------------------------------------------------------
     void setMailInBox(){
       mailInBuffer = new MailInTableModel(context);
     }
//------------------------------------------------------------------------------
     void setMailOutBox(){
       mailOutBuffer = new MailOutTableModel(context);
     }
//------------------------------------------------------------------------------
     void setMsgHandlerBox(){
       msgHandlerBuffer = new MsgHandlerTableModel(context);
     }
//------------------------------------------------------------------------------
       public void showMsg(String message) {
       }
//------------------------------------------------------------------------------
      public void set(AgentContext context) {
        Assert.notNull(context);
        this.context = context;
        this.setTitle(context.whoami());
        setMailInBox();
        setMailOutBox();
        setMsgHandlerBox();
      }
//------------------------------------------------------------------------------
    private void getNativeUI(){

         String pathsep = System.getProperty("path.separator");
         System.out.println("Choosing native look & feel ....");
         try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           SwingUtilities.updateComponentTreeUI(this);
         }
         catch (Exception exc) {
           System.out.println("Error getting UI");
         }

     }
//------------------------------------------------------------------------------
     public void componentResized(ComponentEvent e) {

        if (bottomPanel.tileOn())
          bottomPanel.tile();
        else
          bottomPanel.cascade();

     }
//------------------------------------------------------------------------------
      public void componentMoved(ComponentEvent e) { }
//------------------------------------------------------------------------------
      public void componentShown(ComponentEvent e) { }
//------------------------------------------------------------------------------
      public void componentHidden(ComponentEvent e) { }
//------------------------------------------------------------------------------
      private void setContentPane(){

          bottomPanel = new InternalFramesPanel(this);

          toolBar = new JToolBar(SwingConstants.VERTICAL);
          toolBar.setFloatable(false);
          JPanel leftPanel = new JPanel(new GridLayout(1,1));
          leftPanel.add(toolBar);
          leftPanel.setBorder(BorderFactory.createEtchedBorder(Color.red,Color.black));
          addButtonsToToolBar();

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());
          contentPane.add(BorderLayout.WEST,leftPanel);
          contentPane.add(BorderLayout.CENTER,bottomPanel);


      }
//------------------------------------------------------------------------------
      private void createMenus(){

          menuBar = new JMenuBar();

          fileMenu = new JMenu("File");

          exit = new JMenuItem("Exit");
          exit.addActionListener(this);
          fileMenu.add(exit);
          menuBar.add(fileMenu);


          viewMenu = new JMenu("View");
          tile = new JMenuItem("Tile");
          tile.addActionListener(this);
          viewMenu.add(tile);
          cascade = new JMenuItem("Cascade");
          cascade.addActionListener(this);
          viewMenu.add(cascade);
          viewMenu.addSeparator();
          nativeLF = new JMenuItem("Native Look & Feel");
          nativeLF.addActionListener(this);
          viewMenu.add(nativeLF);
          metalLF = new JMenuItem("Metal Look & Feel");
          metalLF.addActionListener(this);
          viewMenu.add(metalLF);
          menuBar.add(viewMenu);

          menuBar.add(Box.createHorizontalGlue());

          helpMenu = new JMenu("Help");
          about = new JMenuItem("Using the Agent Viewer");
          about.addActionListener(this);
          helpMenu.add(about);
          aboutZeus = new JMenuItem("About Zeus");
          aboutZeus.addActionListener(this);
          helpMenu.add(aboutZeus);
          menuBar.add(helpMenu);
      }
//------------------------------------------------------------------------------
      public void actionPerformed(ActionEvent evt) {
         Object source = evt.getSource();

         // --- look and feel
         if ( source == nativeLF   ) {
           getNativeUI();
           return;
         }
         else if ( source == metalLF   ) {
            try {
               System.out.println("Metal look & feel chosen");
               UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
               SwingUtilities.updateComponentTreeUI(this);
            }
            catch (Exception exc) {
               System.out.println("Error getting UI");
            }
           return;
         }

         // others
         if (context != null) {
            if ( source == exit ) exitBtnFn();
            else if ( source == tile   ) bottomPanel.tile();
            else if ( source == cascade   ) bottomPanel.cascade();
            else if ( source == about   ) {
            }
            else if (source == mailInBtn) {
                new MailInTableUI(bottomPanel,mailInBuffer);
            }
            else if (source == mailOutBtn) {
               new MailOutTableUI(bottomPanel,mailOutBuffer);
            }
            else if (source == msgHandlerBtn) {
                new MsgHandlerTableUI(bottomPanel,msgHandlerBuffer);
            }
         }
         else {
           JOptionPane.showMessageDialog(this,"No associated agent","Error Message",
                                         JOptionPane.OK_OPTION);
         }


      }

//------------------------------------------------------------------------------
      private void exitBtnFn() { System.exit(0); }


//------------------------------------------------------------------------------
     Icon getIcon(String imgFile, int w, int h){

       String  imgStr = new String(IMAGEPATH + imgFile);
       Image aImg = Toolkit.getDefaultToolkit().getImage(imgStr);
       aImg = aImg.getScaledInstance(w,h,Image.SCALE_SMOOTH);
       Icon aIcon = new ImageIcon(aImg);

       return aIcon;
     }

//------------------------------------------------------------------------------
    private void addButtonsToToolBar(){

      JLabel header = new JLabel(getIcon(ViewerNames.HEADER_IMG, HEADERw, HEADERh));
      toolBar.add(header);

      toolBar.add(Box.createRigidArea(new Dimension(10,30)));
      mailInBtn = new AgentButton(ViewerNames.MAIL_IN,
                                  getIcon(ViewerNames.MAILIN_IMG,ICONw,ICONh));
      mailInBtn.addActionListener(this);
      toolBar.add(mailInBtn);


      mailOutBtn = new AgentButton(ViewerNames.MAIL_OUT,
                                   getIcon(ViewerNames.MAILOUT_IMG,ICONw,ICONh));
      mailOutBtn.addActionListener(this);
      toolBar.add(mailOutBtn);

     }
//------------------------------------------------------------------------------
      public static void main(String[] args) {
           new BasicAgentViewer();
      }

}
