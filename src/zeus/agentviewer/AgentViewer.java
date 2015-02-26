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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


import zeus.util.*;
import zeus.concepts.*;
import zeus.ontology.OntologyEditor;
import zeus.gui.help.*;
import zeus.agents.ZeusAgentUI;
import zeus.agents.ZeusAgent;
import zeus.actors.*;
import zeus.agentviewer.mail.*;
import zeus.agentviewer.msghandler.*;
import zeus.agentviewer.resources.*;
import zeus.agentviewer.acquaintances.*;
import zeus.agentviewer.task.*;
import zeus.agentviewer.plansch.*;
import zeus.agentviewer.engine.*;
import zeus.agentviewer.protocol.*;
import zeus.agentviewer.rete.*;

/** 
    this is the main class for the viewer that lets you inspect the internals of Zeus 
    Agents. 
    
    Change log
    ----------
    12/04/01 - altered to allow toolbars to detach and set standard l&f to metal 
               shifted tool bar to top/north of frame 
    */
public class AgentViewer extends JFrame 
                         implements ActionListener,
                                    ComponentListener,
                                    ZeusAgentUI
                                    {

   JPanel contentPane;
   public JPanel topPanel;
   InternalFramesPanel bottomPanel;
   String fsep = System.getProperty("file.separator");
   String IMAGEPATH = SystemProps.getProperty("gif.dir") + "agentviewer" + fsep;
   final int ICONh  = 40;
   final int ICONw  = 40;
   final int HEADERw = 200;
   final int HEADERh = 43;
   final int WIDTH = 600;
   final int HEIGHT = 400;
   int startWidth = 600;
   int startHeight = 300; 


    private JMenuBar           menuBar;
    private JMenu              fileMenu, helpMenu, goalMenu;
    private JMenu              viewMenu, dbMenu;
    private JMenuItem          goal,options,exit;
    private JMenuItem          about, aboutZeus;
    private JMenuItem          tile, cascade;
    private JMenuItem          newTask,newResource,newAcquaintance;
    private JMenuItem          nativeLF, metalLF;
    private JToolBar           toolBar;

    /** 
	changed these to protected to allow subclassing of the viewer for 
	specialist agent architectures 
    */
    protected AgentButton mailInBtn, mailOutBtn, msgHandlerBtn,
                        reteEngineBtn, coordEngineBtn, acqDbBtn,
                        planSchBtn,taskPlanDbBtn,
                        resDbBtn, ontDbBtn,protocolBtn;

    AgentContext context = null;
    Thread agentViewerThread;
    MailInTableModel mailInBuffer;
    MailOutTableModel mailOutBuffer;
    MsgHandlerTableModel msgHandlerBuffer;
    ResourceTableModel resourceBuffer;
    RelationsTableModel relationsBuffer;
    AbilitiesTableModel abilitiesBuffer;
    AttributesTableModel attributesBuffer;
// Task
    TaskTableModel taskBuffer;
    TaskConditionsTableModel preCondBuffer;
    ConditionsAttributeTableModel preAttrBuffer;
    TaskConditionsTableModel effectBuffer;
    ConditionsAttributeTableModel effectsAttrBuffer;
    ConstraintsModel constraintsBuffer;
    OrderingModel orderingBuffer;

// Planner
    PlanSchModel planSchBuffer;
    EngineTableModel engineBuffer;
    ReteEngineDataModel reteEngineBuffer;
    ProtocolModel protocolBuffer;
    StrategyModel strategyBuffer;
    AttributesModel pAttributesBuffer;

//-------------------------------------------------------------------------
     public AgentViewer()
      {
          super("Agent Viewer");
          //getNativeUI();
          getMetalUI(); 
          ImageIcon icon = new ImageIcon(IMAGEPATH + ViewerNames.VIEWER_IMG);
          setIconImage(icon.getImage());
          createMenus();
          setJMenuBar(menuBar);
          setContentPane();

          addWindowListener(
           new WindowAdapter() {
              public void windowClosing(WindowEvent evt) { 
                exitBtnFn(); 
                System.exit(0);  }
             }
          );

          addComponentListener(this);
          setSize (new Dimension(startWidth, startHeight));
          setVisible(true);
      //    pack();

      }


     void setMailInBox() {
       mailInBuffer = new MailInTableModel(context);
     }


     void setMailOutBox() {
       mailOutBuffer = new MailOutTableModel(context);
     }


     void setMsgHandlerBox() {
       msgHandlerBuffer = new MsgHandlerTableModel(context);
     }


    void setResourceBuffer() {
      resourceBuffer = new ResourceTableModel(context);
    }


    void setAcquaintanceBuffers() {
      relationsBuffer = new RelationsTableModel(context);
      abilitiesBuffer = new AbilitiesTableModel(context);
      attributesBuffer = new AttributesTableModel(abilitiesBuffer);
    }


    void setTaskBuffers() {
      taskBuffer = new TaskTableModel(context);
      preCondBuffer = new TaskConditionsTableModel() ;
      preAttrBuffer = new ConditionsAttributeTableModel();
      effectBuffer = new TaskConditionsTableModel() ;
      effectsAttrBuffer = new ConditionsAttributeTableModel();
      constraintsBuffer = new ConstraintsModel();
      orderingBuffer = new OrderingModel() ;

    }


    void setPlanSchBuffer() {
       planSchBuffer = new PlanSchModel(context);
    }


    void setEngineBuffer() {
       engineBuffer = new EngineTableModel(context);
    }


    void setReteEngineBuffer() {
       reteEngineBuffer = new ReteEngineDataModel(context);
    }


    void setProtocolBuffers() {
       protocolBuffer = new ProtocolModel(context);
       strategyBuffer = new StrategyModel();
       pAttributesBuffer = new AttributesModel();
    }


    public void showMsg(String message) {
    }


    public void set(AgentContext context) 
    {
        Assert.notNull(context);

        this.context = context;
        this.setVisible(true);
        this.setTitle(context.whoami());

        setMailInBox();
        setMailOutBox();
        setMsgHandlerBox();
        setResourceBuffer();
        setAcquaintanceBuffers();
        setEngineBuffer();
        setProtocolBuffers();

        if (context.Planner() == null)
        {
          planSchBtn.setEnabled(false);
          taskPlanDbBtn.setEnabled(false);
        }
        else
        {
          setTaskBuffers();
          setPlanSchBuffer();
        }

        if (context.ReteEngine() == null)
          reteEngineBtn.setEnabled(false);
        else
          setReteEngineBuffer();
    }


    private void getNativeUI() {
       String pathsep = System.getProperty("path.separator");
       try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           SwingUtilities.updateComponentTreeUI(this);
       }
       catch (Exception e) {
        System.out.println("Error getting UI"); 
       }
    }
    
    
    private void getMetalUI() { 
           try {
               System.out.println("Metal look & feel chosen");
               UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
               SwingUtilities.updateComponentTreeUI(this);
            }
            catch (Exception exc) {
               System.out.println("Error getting UI");
            }
    }


    public void showHelp() {
      HelpWindow helpWin;
      Point dispos = getLocation();
      helpWin = new HelpWindow(this, dispos, "visualiser", "Agent Viewer");
      helpWin.setSize(new Dimension(600, getHeight()));
      helpWin.setLocation(dispos.x+24, dispos.y+24);
      helpWin.validate();
      helpWin.setSource(about);
    }


    public void About() {
      HelpWindow helpWin;
      Point dispos = getLocation();
      helpWin = new HelpWindow(this, dispos, "visualiser", "About");
      helpWin.setSize(new Dimension(440, 440));
      helpWin.setLocation(dispos.x+24, dispos.y+24);
      helpWin.setTitle("About ZEUS ...");
      helpWin.validate();
      helpWin.setSource(aboutZeus);
    }


     public void componentResized(ComponentEvent e) {

        if (bottomPanel.tileOn())
          bottomPanel.tile();
        else
          bottomPanel.cascade();

     }


      public void componentMoved(ComponentEvent e) {
       // System.out.println("component moved" + e.toString()); 
        
        }


      public void componentShown(ComponentEvent e) { }


      public void componentHidden(ComponentEvent e) { }


      private void setContentPane() {
          bottomPanel = new InternalFramesPanel(this);

          toolBar = new JToolBar(SwingConstants.HORIZONTAL);
          JPanel leftPanel = new JPanel(new GridLayout(1,1));
          leftPanel.add(toolBar);
	  //  leftPanel.setBorder(BorderFactory.createEtchedBorder(Color.red,Color.black));
	  
          addButtonsToToolBar();

          contentPane = (JPanel) getContentPane();
          contentPane.setLayout(new BorderLayout());
          contentPane.add(BorderLayout.NORTH,toolBar);
          contentPane.add(BorderLayout.CENTER,bottomPanel);
          
      }


      private void createMenus() {
          menuBar = new JMenuBar();

          fileMenu = new JMenu("File");
          options = new JMenuItem("Control Options");
          options.addActionListener(this);
          fileMenu.add(options);

          exit = new JMenuItem("Exit");
          exit.addActionListener(this);
          fileMenu.add(exit);
          menuBar.add(fileMenu);

          goalMenu = new JMenu("Goal");
          goal = new JMenuItem("New Goal");
          goalMenu.add(goal);
          goal.addActionListener(this);
          menuBar.add(goalMenu);


          dbMenu = new JMenu("Databases");
          newAcquaintance = new JMenuItem("Add Acquaintance");
          dbMenu.add(newAcquaintance);
          newAcquaintance.addActionListener(this);

          newResource = new JMenuItem("Add Resource");
          dbMenu.add(newResource);
          newResource.addActionListener(this);

          newTask = new JMenuItem("Add Task");
          dbMenu.add(newTask);
          newTask.addActionListener(this);
          menuBar.add(dbMenu);

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


      public void actionPerformed(ActionEvent evt)
      {
        Object source = evt.getSource();

        // --- look and feel
        if ( source == nativeLF ) {
          getNativeUI();
          return;
        }
        else if ( source == metalLF ) {
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
         else if (source == about)
           showHelp();
         else if (source == aboutZeus)
           About();

         // others
         if (context != null) {
            if ( source == exit ) exitBtnFn();
            else if ( source == tile      )
         bottomPanel.tile();
            else if ( source == cascade   )
         bottomPanel.cascade();
            else if ( source == options   )
               new ControlOptionsDialog(this,context);
            else if ( source == goal   ) {
             GoalDialog gd = new GoalDialog(this,GoalDialog.BASIC,context);
             gd.display();
            }
            else if ( source == newAcquaintance) {
                    System.out.println("not yet implemented");
            }
            else if ( source == newResource) {
                   System.out.println("not yet implemented");
                   // new AddFactDialog (

            }
            else if ( source == newTask) {
                    System.out.println("not yet implemented"); 
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
            else if (source == coordEngineBtn) {
                new EngineUI(bottomPanel,engineBuffer);
            }
            else if (source == reteEngineBtn) {
                new ReteEngineUI(bottomPanel,reteEngineBuffer);
            }
            else if (source == acqDbBtn) {
                new AcquaintanceUI(bottomPanel,relationsBuffer, abilitiesBuffer,
                                   attributesBuffer);
            }
            else if (source == planSchBtn) {
                new PlanSchTableUI(bottomPanel,planSchBuffer);
            }
            else if (source == taskPlanDbBtn) {
                new TaskTableUI(bottomPanel,taskBuffer,preCondBuffer,
                                preAttrBuffer,effectBuffer,effectsAttrBuffer,
                                constraintsBuffer,orderingBuffer);
            }
            else if (source == resDbBtn) {
                new ResourceTableUI(bottomPanel,resourceBuffer);
            }
            else if (source == ontDbBtn) {
                OntologyEditor editor = new OntologyEditor(context.OntologyDb(),true);
               // JInternalFrame editorPanel = editor.createInternalFrame(); 
             //   bottomPanel.addInternalFrame(editorPanel);
              //  editorPanel.setVisible(true); 
                
                }
            else if (source == protocolBtn) {
                new ProtocolUI(bottomPanel,protocolBuffer,strategyBuffer,
                               pAttributesBuffer);
            }
            else
            {
              System.out.println("Unrecognised event source");
            }
         }
         else {
           JOptionPane.showMessageDialog(this,"No associated agent","Error Message",
                                         JOptionPane.OK_OPTION);
         }
      }


      private void exitBtnFn() {
         if (context != null) {
            mailInBuffer.removeZeusEventMonitors();
            mailOutBuffer.removeZeusEventMonitors();
            msgHandlerBuffer.removeZeusEventMonitors();
            abilitiesBuffer.removeZeusEventMonitors();
            relationsBuffer.removeZeusEventMonitors();
            resourceBuffer.removeZeusEventMonitors();
            engineBuffer.removeZeusEventMonitors();
            protocolBuffer.removeZeusEventMonitors();
            if (context.Planner() != null)
            {
              taskBuffer.removeZeusEventMonitors();
              planSchBuffer.removeZeusEventMonitors();
            }
         }
         MailBox mbox = context.getMailBox(); 
         mbox.shutdown(); 
         try { 
            Thread.sleep(25); 
         } catch(Exception e) { 
            ;
         }
         System.exit(0);
      }



     Icon getIcon(String imgFile, int w, int h) {
       String  imgStr = new String(IMAGEPATH + imgFile);
       Image aImg = Toolkit.getDefaultToolkit().getImage(imgStr);
       aImg = aImg.getScaledInstance(w,h,Image.SCALE_SMOOTH);
       Icon aIcon = new ImageIcon(aImg);
       return aIcon;
     }


      private TitledBorder makeBorder(String title) {
          TitledBorder border = (BorderFactory.createTitledBorder(title));
          border.setTitlePosition(TitledBorder.TOP);
	  border.setTitleJustification(TitledBorder.RIGHT);
	  border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
	  border.setTitleColor(Color.black);

          return border;
     }


    private void addButtonsToToolBar()  
    {
     // JLabel header = new JLabel(getIcon(ViewerNames.HEADER_IMG, HEADERw, HEADERh));
      toolBar.setFloatable(false); 
      // toolBar.add(header);
      toolBar.setAutoscrolls(true); 

      JToolBar topBar = new JToolBar(SwingConstants.HORIZONTAL);
      //   topBar.setBorder(makeBorder("Processors"));
      topBar.setFloatable(true);
      toolBar.add(topBar);

      mailInBtn = new AgentButton(ViewerNames.MAIL_IN,
                                  getIcon(ViewerNames.MAILIN_IMG,ICONw,ICONh));
      mailInBtn.addActionListener(this);
      topBar.add(mailInBtn);

      mailOutBtn = new AgentButton(ViewerNames.MAIL_OUT,
                                   getIcon(ViewerNames.MAILOUT_IMG,ICONw,ICONh));
      mailOutBtn.addActionListener(this);
      topBar.add(mailOutBtn);

      msgHandlerBtn = new AgentButton(ViewerNames.MESSAGE_HANDLER,
                                      getIcon(ViewerNames.MSGHANDLER_IMG,ICONw,ICONh)) ;
      msgHandlerBtn.addActionListener(this);
      topBar.add(msgHandlerBtn);

      reteEngineBtn = new AgentButton(ViewerNames.RETE_ENGINE,
                                       getIcon(ViewerNames.RETE_ENG_IMG,ICONw,ICONh));
      reteEngineBtn.addActionListener(this);
      topBar.add(reteEngineBtn);

      coordEngineBtn = new AgentButton(ViewerNames.COORDINATION_ENGINE,
                                       getIcon(ViewerNames.COORDENG_IMG,ICONw,ICONh));
      coordEngineBtn.addActionListener(this);
      topBar.add(coordEngineBtn);

      planSchBtn = new AgentButton(ViewerNames.PLANNER_SCHEDULER,
                                   getIcon(ViewerNames.PLANSCH_IMG,ICONw,ICONh));
      planSchBtn.addActionListener(this);
      topBar.add(planSchBtn);

      JToolBar bottomBar = new JToolBar(SwingConstants.HORIZONTAL);
      // bottomBar.setBorder(makeBorder("Databases"));
      toolBar.add(bottomBar);

      acqDbBtn = new AgentButton(ViewerNames.ACQUAINTANCE_DATABASE,
                                 getIcon(ViewerNames.ACQDB_IMG,ICONw,ICONh));
      acqDbBtn.addActionListener(this);
      bottomBar.add(acqDbBtn);

      ontDbBtn =  new AgentButton(ViewerNames.ONTOLOGY_DATABASE,
                                  getIcon(ViewerNames.ONTDB_IMG,ICONw,ICONh));
      ontDbBtn.addActionListener(this);
      bottomBar.add(ontDbBtn);


      protocolBtn = new AgentButton(ViewerNames.PROTOCOL,
                                       getIcon(ViewerNames.PROTOCOL_IMG,ICONw,ICONh));
      protocolBtn.addActionListener(this);
      bottomBar.add(protocolBtn);


      resDbBtn = new AgentButton(ViewerNames.RESOURCE_DATABASE,
                                 getIcon(ViewerNames.RESDB_IMG,ICONw,ICONh));
      resDbBtn.addActionListener(this);
      bottomBar.add(resDbBtn);

      taskPlanDbBtn = new AgentButton(ViewerNames.TASK_PLAN_DATABASE,
                                      getIcon(ViewerNames.TASKPLAN_IMG,ICONw,ICONh));
      taskPlanDbBtn.addActionListener(this);
      bottomBar.add(taskPlanDbBtn);

    }
//-------------------------------------------------------------------------
}
