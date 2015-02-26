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



/*****************************************************************************
* AgentGenerator.java
*
* Main frame of the Zeus Agent Generator
*****************************************************************************/

package zeus.generator;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.help.*;
import zeus.ontology.*;
import zeus.generator.code.CodeGenerator;
import zeus.generator.code.GenerationPlan;
import zeus.generator.agent.*;
import zeus.generator.task.*;

public class AgentGenerator extends javax.swing.JFrame implements ChangeListener, ActionListener
{
  protected FileHandler       filer;
  protected CodeGenerator     codeUI = null;

  protected File              projectFile  = null;
  protected boolean           projectSaveNeeded = false;

  protected Hashtable         agentEditorList = new Hashtable();
  protected Hashtable         taskEditorList = new Hashtable();

  protected OntologyDb        ontology;
  protected GeneratorModel    genmodel;
  protected GenerationPlan    genplan;
  protected OntologyEditor    ontologyEditor = null;
  protected SocietyEditor     societyEditor  = null;

  protected JMenuBar  mainMenuBar;
  protected JMenu     menu1;
  protected JMenuItem miNew;
  protected JMenuItem miOpen;
  protected JMenuItem miSave;
  protected JMenuItem miSaveAs;
  protected JMenuItem miGen;
  protected JMenuItem miExit;
  protected JMenu     menu2;
  protected JMenuItem miNewOnt;
  protected JMenuItem miEditOnt;
  protected JMenuItem miLoadOnt;
  protected JMenuItem miSaveOnt;
  protected JMenu     menu3;
  protected JMenuItem miAbout;

  protected JLabel projectInfoLabel;
  protected JLabel ontologyInfoLabel;

  static final String PROJECT_FILE_EXT = "*.def";

  static final String[] MESSAGE = {
     /* 0 */ "Save file?",
     /* 1 */ "Save needed",
     /* 2 */ "File already exists.\nOverwrite saved file?",
     /* 3 */ "Save file as",
     /* 4 */ "Error",
     /* 5 */ "Warning",
     /* 6 */ "Agent is currently being edited.\nClose agent editor?",
     /* 7 */ "Remove agent",
     /* 8 */ "Are you sure?",
     /* 9 */ "FREE SLOT",

     /* 10 */ "Project: ",
     /* 11 */ "Using ontology: ",
     /* 12 */ "FREE SLOT",
     /* 13 */ "FREE SLOT",
     /* 14 */ "FREE SLOT",
     /* 15 */ "FREE SLOT",
     /* 16 */ "FREE SLOT",
     /* 17 */ "FREE SLOT",
     /* 18 */ "FREE SLOT",
     /* 19 */ "ZEUS Agent Generator v1.1\nOriginal by BT Labs 1999-2001\n 1.2.2,1.2.1,1.2,1.1,1.04, 1.03b developed in the Zeus Open Source Community & The Intelligent Systems Lab (BT Exact)\nConsult the ZEUS Realisation Guide for instructions\n For more info on Zeus Open Source effort see http://www.sourceforge.net/projects/zeusagent\n For more on the IS Lab see http://www.labs.bt.com/projects/agents.htm",

     /* 20 */ "About ZEUS",
     /* 21 */ "No project loaded",
     /* 22 */ "Task is currently being edited.\nClose task editor?",
     /* 23 */ "Remove task",
     /* 24 */ "Task is currently being edited.\nSave task?",
     /* 25 */ "Clone Task",
     /* 26 */ "Agent is currently being edited.\nSave agent?",
     /* 27 */ "Clone Agent",
     /* 28 */ " is currently being edited.\nSave agent?",
     /* 29 */ " is currently being edited.\nSave task?",
     /* 30 */ "New Project"
  };

  public AgentGenerator() {
    // debug classpath problems for lamers. 
    // added by simon 21/08/00
    try {
         Class c = Class.forName("java.lang.Object"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find java.lang.Object.\n This indicates that the rt.jar file is not in your classpath.\n Ensure that $java_install_dir\\jre\\rt.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}
    try {
         Class c = Class.forName("zeus.gui.help.HelpWindow"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find a zeus class.\n This indicates that the zeus.jar file is not in your classpath.\n Ensure that zeus_install_dir\\lib\\zeus.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}      
      

        
    GenSym genSym = new GenSym("AgentGenerator");
    ontology = new OntologyDb(genSym);
    genmodel = new GeneratorModel(ontology);
    genplan = new GenerationPlan(genmodel,ontology);
    filer = new FileHandler(ontology,genmodel,genplan);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "generator" + sep;
    ImageIcon icon = new ImageIcon(path + "tool.gif");
    setIconImage(icon.getImage());
    getContentPane().setBackground(Color.lightGray);
    getContentPane().setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    genmodel.addChangeListener(this);
    genplan.addChangeListener(this);

    mainPanel.setBackground(Color.lightGray);
    Border b1 = new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new EmptyBorder(15,15,15,15));
    mainPanel.setBorder(b1);

    GridBagLayout gridBagLayout = new GridBagLayout();
    mainPanel.setLayout(gridBagLayout);
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(0,8,8,8);

    JLabel header = new JLabel(new ImageIcon(path + "zeus.gif"));
    header.setBorder(new BevelBorder(BevelBorder.RAISED));

    gridBagLayout.setConstraints(header, gbc);
    mainPanel.add(header);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(8,8,0,8);

    JPanel projectPanel = new JPanel();
    gridBagLayout.setConstraints(projectPanel, gbc);
    mainPanel.add(projectPanel);

    JPanel ontologyPanel = new JPanel();
    gridBagLayout.setConstraints(ontologyPanel, gbc);
    mainPanel.add(ontologyPanel);

    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;

    JPanel agentPanel = new JPanel();
    gridBagLayout.setConstraints(agentPanel, gbc);
    mainPanel.add(agentPanel);

    JPanel taskPanel = new JPanel();
    gridBagLayout.setConstraints(taskPanel, gbc);
    mainPanel.add(taskPanel);

   // ----- Create Project Panel ----
    projectPanel.setBackground(Color.lightGray);
    TitledBorder b2 = (BorderFactory.createTitledBorder("Project Options"));
    b2.setTitlePosition(TitledBorder.TOP);
    b2.setTitleJustification(TitledBorder.RIGHT);
    b2.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    b2.setTitleColor(Color.blue);
    projectPanel.setBorder(b2);
   
    JToolBar toolbar = new ProjectToolBar();
    projectInfoLabel = new JLabel();
    projectInfoLabel.setFont(new Font("Helvetica", Font.BOLD, 12));
    projectInfoLabel.setText(MESSAGE[21]);

    gridBagLayout = new GridBagLayout();
    projectPanel.setLayout(gridBagLayout);
    gbc = new GridBagConstraints();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(toolbar, gbc);
    projectPanel.add(toolbar);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(4,8,4,8);
    gridBagLayout.setConstraints(projectInfoLabel, gbc);
    projectPanel.add(projectInfoLabel);

    // ---- Create Ontology Panel ----
    ontologyPanel.setBackground(Color.lightGray);
    b2 = (BorderFactory.createTitledBorder("Ontology Options"));
    b2.setTitlePosition(TitledBorder.TOP);
    b2.setTitleJustification(TitledBorder.RIGHT);
    b2.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    b2.setTitleColor(Color.blue);
    ontologyPanel.setBorder(b2);

    toolbar = new OntologyToolBar();
    ontologyInfoLabel = new JLabel();
    ontologyInfoLabel.setText(MESSAGE[16]);
    ontologyInfoLabel.setFont(new Font("Helvetica", Font.BOLD, 12));

    gridBagLayout = new GridBagLayout();
    ontologyPanel.setLayout(gridBagLayout);
    gbc = new GridBagConstraints();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(toolbar, gbc);
    ontologyPanel.add(toolbar);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(4,8,4,8);
    gridBagLayout.setConstraints(ontologyInfoLabel, gbc);
    ontologyPanel.add(ontologyInfoLabel);

    // ---- Create Agent Panel ----
    agentPanel.setBackground(Color.lightGray);
    b2 = (BorderFactory.createTitledBorder("Agent Options"));
    b2.setTitlePosition(TitledBorder.TOP);
    b2.setTitleJustification(TitledBorder.RIGHT);
    b2.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    b2.setTitleColor(Color.blue);
    agentPanel.setBorder(b2);

    AgentTableUI agentTable = new AgentTableUI(this,genmodel);
    toolbar = new AgentToolBar(agentTable);

    gridBagLayout = new GridBagLayout();
    agentPanel.setLayout(gridBagLayout);
    gbc = new GridBagConstraints();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,8,0);
    gridBagLayout.setConstraints(toolbar, gbc);
    agentPanel.add(toolbar);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(4,8,4,8);
    gridBagLayout.setConstraints(agentTable, gbc);
    agentPanel.add(agentTable);

    // ---- Create Task Panel ----
    taskPanel.setBackground(Color.lightGray);
    b2 = (BorderFactory.createTitledBorder("Task Options"));
    b2.setTitlePosition(TitledBorder.TOP);
    b2.setTitleJustification(TitledBorder.RIGHT);
    b2.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    b2.setTitleColor(Color.blue);
    taskPanel.setBorder(b2);
   
    TaskTableUI taskTable = new TaskTableUI(this,genmodel);
    toolbar = new TaskToolBar(taskTable);

    gridBagLayout = new GridBagLayout();
    taskPanel.setLayout(gridBagLayout);
    gbc = new GridBagConstraints();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,8,0);
    gridBagLayout.setConstraints(toolbar, gbc);
    taskPanel.add(toolbar);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(4,8,4,8);
    gridBagLayout.setConstraints(taskTable, gbc);
    taskPanel.add(taskTable);

    // ---- Create OntologyEditor ----
    ontologyEditor = new OntologyEditor(ontology, this, genmodel, ontologyInfoLabel);
    // now passes in generator model - Jaron 1/11/00

    // ---- Set Frame title/menus/etc ----
    setTitle("ZEUS Agent Generator " + SystemProps.getProperty("version.id"));
    initMenus();

    // ---- Register Listeners ----
    SymWindow aSymWindow = new SymWindow();
    this.addWindowListener(aSymWindow);

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setBackground(Color.lightGray);
    setVisible(true);
    pack();
    // force compiler to except ClassNotFoundException
   

  }

  protected void initMenus() {
     mainMenuBar = new JMenuBar();
     menu1 = new JMenu("Project");
     miNew = new JMenuItem("New Project");
     miNew.addActionListener(this);
     menu1.add(miNew);
     
     miOpen = new JMenuItem("Load Project...");
     miOpen.addActionListener(this);
     menu1.add(miOpen);
     
     miSave = new JMenuItem("Save Project...");
     miSave.addActionListener(this);
     menu1.add(miSave);
     
     miSaveAs = new JMenuItem("Save Project As...");
     miSaveAs.addActionListener(this);
     menu1.add(miSaveAs);
     menu1.addSeparator();
     
     miGen = new JMenuItem("Generate Code");
     miGen.addActionListener(this);
     menu1.add(miGen);
     menu1.addSeparator();
     
     miExit = new JMenuItem("Exit");
     miExit.addActionListener(this);
     menu1.add(miExit);
     mainMenuBar.add(menu1);
     
     menu2 = new JMenu("Ontology");
     miNewOnt = new JMenuItem("New Ontology");
     miNewOnt.addActionListener(this);
     menu2.add(miNewOnt);
     miLoadOnt = new JMenuItem("Load Ontology");
     miLoadOnt.addActionListener(this);
     menu2.add(miLoadOnt);
     miSaveOnt = new JMenuItem("Save Ontology");
     miSaveOnt.addActionListener(this);
     menu2.add(miSaveOnt);
     miEditOnt = new JMenuItem("Edit Ontology");
     miEditOnt.addActionListener(this);
     menu2.add(miEditOnt);

     mainMenuBar.add(menu2);
     menu3 = new JMenu("Help");
//     mainMenuBar.setHelpMenu(menu3);
     miAbout = new JMenuItem("About..");
     miAbout.addActionListener(this);
     menu3.add(miAbout);
     mainMenuBar.add(menu3);
     setJMenuBar(mainMenuBar);
       
  }

  
  // ---- Project Options ----  
  
  public boolean openFile(File f) {
    Assert.notNull(f);
    Frame frame = (Frame)SwingUtilities.getRoot(this);
    Cursor lastCursor = frame.getCursor();
    frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    int status = filer.openFile(f);
    frame.setCursor(lastCursor);
    if ( (status & FileHandler.ERROR_MASK) != 0 ) {
       JOptionPane.showMessageDialog(this,filer.getError(),
          MESSAGE[4],JOptionPane.ERROR_MESSAGE);
       projectSaveNeeded = false;
       return newProject();
    }
    else if ( (status & FileHandler.WARNING_MASK) != 0 ) {
      JOptionPane.showMessageDialog(this,filer.getWarning(),
         MESSAGE[5],JOptionPane.WARNING_MESSAGE);

         if (FileHandler.WARNING_MASK == 1)
           projectSaveNeeded = true; // autosave perhaps?
    }
    else
      projectSaveNeeded = false;
    projectFile = f;
    projectInfoLabel.setText(MESSAGE[10] + f.getName());
    ontologyInfoLabel.setText(MESSAGE[11] + ontology.getFilename());
    return true;
  }

  public boolean saveFile(File f) {
    Assert.notNull(f);
    Frame frame = (Frame)SwingUtilities.getRoot(this);
    Cursor lastCursor = frame.getCursor();

    frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if ( !ontologyEditor.Save() ) {
       frame.setCursor(lastCursor);
       return false;
    }
    int status = filer.saveFile(f);
    frame.setCursor(lastCursor);

    if ( (status & FileHandler.ERROR_MASK) != 0 ) {
       JOptionPane.showMessageDialog(this,filer.getError(),
          MESSAGE[4],JOptionPane.ERROR_MESSAGE);
       return false;
    }
    else if ( (status & FileHandler.WARNING_MASK) != 0 ) {
       JOptionPane.showMessageDialog(this,filer.getWarning(),
          MESSAGE[5],JOptionPane.WARNING_MESSAGE);
    }

    projectInfoLabel.setText(MESSAGE[10] + f.getName());
    projectSaveNeeded = false;
    projectFile = f;
    return true;
  }

  protected void resetProject() {
    genmodel.clear();
    genplan.reset();
    ontologyEditor.closeFile();
    projectFile = null;
    projectInfoLabel.setText(MESSAGE[21]);
    projectSaveNeeded = false;

    Enumeration enum = agentEditorList.elements();
    while (enum.hasMoreElements()) {
      JFrame frame = (JFrame)enum.nextElement();
      frame.dispose();
    }
    agentEditorList.clear();

    enum = taskEditorList.elements();
    while (enum.hasMoreElements()) {
      JFrame frame = (JFrame)enum.nextElement();
      frame.dispose();
    }
    taskEditorList.clear();
  }

  public void loadProject() {
    if ( !newProject() ) return;

    File f = getFile(FileDialog.LOAD,projectFile,PROJECT_FILE_EXT);
    if (f != null)
       openFile(f);
  }

  protected boolean editorsHaveChanged() {
    Enumeration enum = agentEditorList.elements();
    while( enum.hasMoreElements()) {
      AgentEditor editor = (AgentEditor)enum.nextElement();
      if ( editor.hasChanged() ) return true;
    }

    enum = taskEditorList.elements();
    while ( enum.hasMoreElements()) {
      TaskEditor editor = (TaskEditor)enum.nextElement();
      if ( editor.hasChanged() ) return true;
    }
    return false;
  }

  public boolean saveProject() {
    // store information from open editor windows
    Enumeration enum = agentEditorList.elements();
    while (enum.hasMoreElements()) {
      AgentEditor editor = (AgentEditor)enum.nextElement();
      if ( editor.hasChanged() ) {
         int answer = JOptionPane.showConfirmDialog(this, "Agent " +
            editor.getObjectName() + MESSAGE[28],
            MESSAGE[30],JOptionPane.YES_NO_CANCEL_OPTION);
         if ( answer == JOptionPane.YES_OPTION )
            editor.save();
         else if ( answer == JOptionPane.CANCEL_OPTION )
            return false;
      }
    }

    enum = taskEditorList.elements();
    while (enum.hasMoreElements()) {
      TaskEditor editor = (TaskEditor)enum.nextElement();
      if ( editor.hasChanged() ) {
         int answer = JOptionPane.showConfirmDialog(this, "Task " +
            editor.getObjectName() + MESSAGE[29],
            MESSAGE[30], JOptionPane.YES_NO_CANCEL_OPTION);
         if ( answer == JOptionPane.YES_OPTION )
            editor.save();
         else if ( answer == JOptionPane.CANCEL_OPTION )
            return false;
      }
    }

    if ( projectFile == null )
       return saveProjectAs();
    else
       return saveFile(projectFile);
  }

  protected boolean saveProjectAs() {
    File f = getFile(FileDialog.SAVE,projectFile,PROJECT_FILE_EXT);
    if ( f != null ) {
       if ( f.exists() && projectFile != null && !f.equals(projectFile) ) {
          int answer = JOptionPane.showConfirmDialog(this,MESSAGE[2],
                       MESSAGE[3],JOptionPane.YES_NO_OPTION);
          if ( answer == JOptionPane.NO_OPTION )
             return false;
       }
       return saveFile(f);
    }
    return false;
  }

  protected boolean newProject() {
    if ( projectSaveNeeded || editorsHaveChanged() ) {
       int answer = JOptionPane.showConfirmDialog(this,MESSAGE[0],
                       MESSAGE[1],JOptionPane.YES_NO_CANCEL_OPTION);

       if ( answer == JOptionPane.YES_OPTION && !saveProject() )
          return false;
       if ( answer == JOptionPane.CANCEL_OPTION )
          return false;
    }
    resetProject();
    return true;
  }

  public void generateCode() {
    if ( codeUI == null )
       codeUI = new CodeGenerator(genmodel,genplan,true);

    if ( codeUI.isShowing() )
       codeUI.toFront();
    else
       codeUI.setVisible(true);
  }

  public void showSociety() {
    if ( societyEditor == null )
       societyEditor = new SocietyEditor(this,genmodel);

    if ( societyEditor.isShowing() )
       societyEditor.toFront();
    else
       societyEditor.setVisible(true);
  }

  public void exitGenerator() {
     if ( !newProject() ) return;

     dispose();
     if ( codeUI != null ) codeUI.dispose();
     if ( societyEditor != null ) societyEditor.dispose();
     System.exit(0);
  }
  
  // ---- Ontology Options -----

  public boolean newOntology() {
     return ontologyEditor.Close();
  }


  public void loadOntology() {
     ontologyEditor.Open();
  }


  public boolean saveOntology() {
     return ontologyEditor.Save();
  }


  public void editOntology() {
    ontologyEditor.showFrame();
  }

  // ---- About Options -----

  public void about() {
      JOptionPane.showMessageDialog(this,MESSAGE[19],MESSAGE[20],
                                    JOptionPane.INFORMATION_MESSAGE);
  }

  // ---- Agent Options -----

  public void editAgent(String id) {
    if ( agentEditorList.containsKey(id) ) {
      AgentEditor editor = (AgentEditor)agentEditorList.get(id);
      editor.toFront();
    }
    else {
      AgentDescription agent = genmodel.getAgent(id);
      AgentEditor editor = new AgentEditor(this,genmodel,ontology,agent);
      agentEditorList.put(id,editor);
      editor.show();
    }
  }
  
  
  public void removeAgent(String id) {
     if ( agentEditorList.containsKey(id) ) {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[6],
                        MESSAGE[7],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION ) {
           AgentEditor editor = (AgentEditor)agentEditorList.get(id);
           editor.dispose();
           genmodel.removeAgent(id);
        }
        else
           return;
     }
     else {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[8],
                        MESSAGE[7],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION ) {
           genmodel.removeAgent(id);
        }
     }
  }


  public void cloneAgent(String id) {
     if ( agentEditorList.containsKey(id) ) {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[26],
                        MESSAGE[27],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION ) {
           AgentEditor editor = (AgentEditor)agentEditorList.get(id);
           editor.save();
        }
        else if ( answer == JOptionPane.CANCEL_OPTION ) {
           return;
        }
     }
     genmodel.cloneAgent(id);
  }


  public void agentEditorClosed(String name) {
    agentEditorList.remove(name);
  }

  // ---- Task Options -----

  public void editTask(String id) {
    if ( taskEditorList.containsKey(id) ) {
      TaskEditor editor = (TaskEditor)taskEditorList.get(id);
      editor.toFront();
    }
    else {
      AbstractTask task = genmodel.getTask(id);
      TaskEditor editor = new TaskEditor(this,genmodel,ontology,task);
      taskEditorList.put(id,editor);
      editor.show();
    }
  }


  public void removeTask(String id) {
     if ( taskEditorList.containsKey(id) ) {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[22],
                        MESSAGE[23],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION ) {
           TaskEditor editor = (TaskEditor)taskEditorList.get(id);
           editor.dispose();
           genmodel.removeTask(id);
        }
        else
           return;
     }
     else {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[8],
                        MESSAGE[23],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION ) {
           genmodel.removeTask(id);
        }
     }
  }
  
  
  public void cloneTask(String id) {
     if ( taskEditorList.containsKey(id) ) {
        int answer = JOptionPane.showConfirmDialog(this,MESSAGE[24],
                        MESSAGE[25],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION ) {
           TaskEditor editor = (TaskEditor)taskEditorList.get(id);
           editor.save();
        }
        else if ( answer == JOptionPane.CANCEL_OPTION ) {
           return;
        }
     }
     genmodel.cloneTask(id);
  }

  public void taskEditorClosed(String name) {
    taskEditorList.remove(name);
  }

  // ---- Utilities ----

  protected File getFile(int type, File f1, String filter) {
    FileDialog f = new FileDialog(this, "AgentGenerator: Select File", type);
    if (f1 != null ) {
       f.setFile(f1.getName());
       f.setDirectory(f1.getParent());
    }
    else
       f.setFile(filter);

    f.pack();
    f.setVisible(true);

    return  f.getFile()==null ? null : new File(f.getDirectory(),f.getFile());
  }
  
  

  // ---- Initialisation Methods ----
  
  protected static void usage() {
    System.err.println("Usage: java AgentGenerator [-f <file>] [-h] [-v]");
    System.exit(0);
  }
  
  protected static void version() {
    System.err.println("Zeus Agent Generator version: " +
                       SystemProps.getProperty("version.id"));
    System.exit(0);
  }

  public static void main(String arg[]) {
    String filename = null;

    for( int i = 0; i < arg.length; i++ ) {
      if ( arg[i].equals("-f") && ++i < arg.length )
        filename = arg[i];
      else if ( arg[i].equals("-h") )
        usage();
      else if ( arg[i].equals("-v") )
        version();
      else
        usage();
    }

    AgentGenerator generator = new AgentGenerator();

    if ( filename != null ) {
       String dir = System.getProperty("user.dir") +
                    System.getProperty("file.separator");
       generator.openFile(new File(dir + filename));
    }
  }
  
//---------------------------------------------------------------------------


  public void stateChanged(ChangeEvent evt) {
     projectSaveNeeded = true;
  }

  public void actionPerformed(ActionEvent event) {
     Object object = event.getSource();
     if (object == miNew)
       newProject();
     else if (object == miOpen)
       loadProject();
     else if (object == miSave)
       saveProject();
     else if (object == miSaveAs)
       saveProjectAs();
     else if (object == miGen)
       generateCode();
     else if (object == miExit)
       exitGenerator();

     else if (object == miNewOnt)
       newOntology();
     else if (object == miLoadOnt)
       loadOntology();
     else if (object == miSaveOnt)
       saveOntology();
     else if (object == miEditOnt)
       editOntology();

     else if (object == miAbout)
       about();

   repaint(); 

  }


  class SymWindow extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
       exitGenerator();
    }
  }

  class ProjectToolBar extends JToolBar implements ActionListener {
     protected HelpWindow    helpWin;
     protected JButton       newBtn;
     protected JButton       loadBtn;
     protected JButton       saveBtn;
     protected JButton       viewBtn;
     protected JButton       genBtn;
     protected JToggleButton helpBtn;

     public ProjectToolBar() {
       setBackground(Color.lightGray);
       setBorder( new BevelBorder(BevelBorder.LOWERED ) );
       setFloatable(false);

       String sep = System.getProperty("file.separator");
       String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

       newBtn = new JButton(new ImageIcon(path + "new.gif"));
       add(newBtn);
       newBtn.setToolTipText("Create New Project");
       newBtn.addActionListener(this);

       loadBtn = new JButton(new ImageIcon(path + "load.gif"));
       add(loadBtn);
       loadBtn.setToolTipText("Load Existing Project");
       loadBtn.addActionListener(this);

       saveBtn = new JButton(new ImageIcon(path + "savedisk.gif"));
       add(saveBtn);
       saveBtn.setToolTipText("Save current Project to disk");
       saveBtn.addActionListener(this);

       addSeparator();

       viewBtn = new JButton(new ImageIcon(path + "view.gif"));
       add(viewBtn);
       viewBtn.setToolTipText("View Agent Society");
       viewBtn.addActionListener(this);

       addSeparator();

       genBtn = new JButton(new ImageIcon(path + "code.gif"));
       add(genBtn);
       genBtn.setToolTipText("Generate Agent Code");
       genBtn.addActionListener(this);

       addSeparator();


       helpBtn = new JToggleButton(new ImageIcon(path + "info.gif"));
       helpBtn.setMargin(new Insets(0,0,0,0));
       add(helpBtn);
       helpBtn.setToolTipText("Help");
       helpBtn.addActionListener(this);
     }

     public void actionPerformed(ActionEvent e) {
       Object src = e.getSource();
       if ( src == newBtn )
          newProject();
       else if ( src == loadBtn )
         loadProject();
       else if ( src == saveBtn )
         saveProject();
       else if ( src == viewBtn )
         showSociety();
       else if ( src == genBtn )
         generateCode();
       else if ( src == helpBtn ) {
         if (helpBtn.isSelected()) {
           Point dispos = getLocation();
           helpWin = new HelpWindow(SwingUtilities.getRoot(this), dispos,
                                    "generator", "Project Options");
           helpWin.setSource(helpBtn);
         }
         else
           helpWin.dispose();
       }
     }
   }

   class OntologyToolBar extends JToolBar implements ActionListener {
     protected HelpWindow    helpWin;
     protected JButton       newBtn;
     protected JButton       loadBtn;
     protected JButton       editBtn;
     protected JButton       saveBtn;
     protected JToggleButton helpBtn;

     public OntologyToolBar() {
       setBackground(Color.lightGray);
       setBorder( new BevelBorder(BevelBorder.LOWERED ) );
       setFloatable(false);

       String sep = System.getProperty("file.separator");
       String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

       newBtn = new JButton(new ImageIcon(path + "new.gif"));
       add(newBtn);
       newBtn.setToolTipText("Create New Ontology");
       newBtn.addActionListener(this);

       loadBtn = new JButton(new ImageIcon(path + "load.gif"));
       add(loadBtn);
       loadBtn.setToolTipText("Load Existing Ontology");
       loadBtn.addActionListener(this);

       saveBtn = new JButton(new ImageIcon(path + "savedisk.gif"));
       add(saveBtn);
       saveBtn.setToolTipText("Save current Ontology to disk");
       saveBtn.addActionListener(this);

       addSeparator();

       editBtn = new JButton(new ImageIcon(path + "edit.gif"));
       add(editBtn);
       editBtn.setToolTipText("Edit Current Ontology");
       editBtn.addActionListener(this);

       addSeparator();

       helpBtn = new JToggleButton(new ImageIcon(path + "info.gif"));
       helpBtn.setMargin(new Insets(0,0,0,0));
       add(helpBtn);
       helpBtn.setToolTipText("Help");
       helpBtn.addActionListener(this);
     }
   
     public void actionPerformed(ActionEvent e) {
       Object src = e.getSource();
       if ( src == newBtn )
         newOntology();
       else if ( src == loadBtn )
         loadOntology();
       else if ( src == editBtn )
         editOntology();
       else if ( src == saveBtn )
         saveOntology();
       else if ( src == helpBtn ) {
         if (helpBtn.isSelected()) {
           Point dispos = getLocation();
           helpWin = new HelpWindow(SwingUtilities.getRoot(this), dispos,
                                    "generator", "Ontology Options");
           helpWin.setSource(helpBtn);
         }
         else
           helpWin.dispose();
       }
     }
   }


   class AgentToolBar extends JToolBar implements ActionListener {
     protected JButton        newBtn;
     protected JButton        editBtn;
     protected JButton        deleteBtn;
     protected JButton        cloneBtn;
     protected JButton        renameBtn;
     protected JToggleButton  helpBtn;
     protected HelpWindow     helpWin;
     protected AgentTableUI   agentTable;
     protected JPopupMenu     popup;
     protected JMenuItem      renameAgentMenuItem;
     protected JMenuItem      modifyTaskListMenuItem;

     public AgentToolBar(AgentTableUI agentTable) {
       this.agentTable = agentTable;
       setBackground(Color.lightGray);
       setBorder( new BevelBorder(BevelBorder.LOWERED ) );
       setFloatable(false);

       String sep = System.getProperty("file.separator");
       String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

       newBtn = new JButton(new ImageIcon(path + "new.gif"));
       add(newBtn);
       newBtn.setToolTipText("Create new agent");
       newBtn.addActionListener(this);

       renameBtn = new JButton(new ImageIcon(path + "rename.gif"));
       add(renameBtn);
       renameBtn.setToolTipText("Modify this agent's name or its task list");
       renameBtn.addActionListener(this);

       deleteBtn = new JButton(new ImageIcon(path + "delete.gif"));
       add(deleteBtn);
       deleteBtn.setToolTipText("Delete this agent");
       deleteBtn.addActionListener(this);

       cloneBtn = new JButton(new ImageIcon(path + "clone.gif"));
       add(cloneBtn);
       cloneBtn.setToolTipText("Clone this agent");
       cloneBtn.addActionListener(this);

       addSeparator();

       editBtn = new JButton(new ImageIcon(path + "edit.gif"));
       add(editBtn);
       editBtn.setToolTipText("Edit this agent");
       editBtn.addActionListener(this);

       addSeparator();

       helpBtn = new JToggleButton(new ImageIcon(path + "info.gif"));
       helpBtn.setMargin(new Insets(0,0,0,0));
       add(helpBtn);
       helpBtn.setToolTipText("Help");
       helpBtn.addActionListener(this);

       // ---- Popup Menu for Modifications ----
       popup = new JPopupMenu();
       popup.add(new JLabel ("Modify"));
       popup.addSeparator();

       renameAgentMenuItem = new JMenuItem("Rename agent");
       renameAgentMenuItem.addActionListener(this);
       popup.add(renameAgentMenuItem);

       modifyTaskListMenuItem  = new JMenuItem("Modify task list");
       modifyTaskListMenuItem.addActionListener(this);
       popup.add(modifyTaskListMenuItem);

       CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
                                               new EmptyBorder(5,5,5,5));
       popup.setBorder(cbr);

     }

     public void actionPerformed(ActionEvent e) {
       Object src = e.getSource();

       if ( src == newBtn )
          agentTable.addNewAgent();
       else if ( src == editBtn )
          agentTable.editAgent();
       else if ( src == deleteBtn )
          agentTable.removeAgent();
       else if ( src == cloneBtn )
          agentTable.cloneAgent();
       else if ( src == renameBtn ) {
          popup.pack();
          popup.show(renameBtn,0,0);
       }
       else if ( src == renameAgentMenuItem )
          agentTable.renameAgent();
       else if ( src == modifyTaskListMenuItem )
          agentTable.modifyTaskList();
       else if ( src == helpBtn ) {
         if ( helpBtn.isSelected() ) {
           Point dispos = getLocation();
           helpWin = new HelpWindow(SwingUtilities.getRoot(this),dispos,
                                    "generator", "Agent Options");
           helpWin.setSource(helpBtn);
         }
         else
           helpWin.dispose();
       }
     }
   }

   class TaskToolBar extends JToolBar implements ActionListener {
     protected JButton        newBtn;
     protected JButton        editBtn;
     protected JButton        cloneBtn;
     protected JButton        deleteBtn;
     protected JButton        renameBtn;
     protected JToggleButton  helpBtn;
     protected HelpWindow     helpWin;
     protected TaskTableUI    taskTable;
     protected JPopupMenu     popup;
     protected JMenuItem      primitiveMenuItem;
     protected JMenuItem      summaryMenuItem;
     protected JMenuItem      behaviourMenuItem;
     protected JMenuItem      scriptMenuItem;

     public TaskToolBar(TaskTableUI taskTable) {
       this.taskTable = taskTable;
       setBackground(Color.lightGray);
       setBorder( new BevelBorder(BevelBorder.LOWERED ) );
       setFloatable(false);

       String sep = System.getProperty("file.separator");
       String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

       newBtn = new JButton(new ImageIcon(path + "new.gif"));
       add(newBtn);
       newBtn.setToolTipText("Create new task");
       newBtn.addActionListener(this);

       renameBtn = new JButton(new ImageIcon(path + "rename.gif"));
       add(renameBtn);
       renameBtn.setToolTipText("Rename task");
       renameBtn.addActionListener(this);

       deleteBtn = new JButton(new ImageIcon(path + "delete.gif"));
       add(deleteBtn);
       deleteBtn.setToolTipText("Delete this task");
       deleteBtn.addActionListener(this);

       cloneBtn = new JButton(new ImageIcon(path + "clone.gif"));
       add(cloneBtn);
       cloneBtn.setToolTipText("Clone this task");
       cloneBtn.addActionListener(this);

       addSeparator();

       editBtn = new JButton(new ImageIcon(path + "edit.gif"));
       add(editBtn);
       editBtn.setToolTipText("Edit this task");
       editBtn.addActionListener(this);

       addSeparator();

       helpBtn = new JToggleButton(new ImageIcon(path + "info.gif"));
       helpBtn.setMargin(new Insets(0,0,0,0));
       add(helpBtn);
       helpBtn.setToolTipText("Help");
       helpBtn.addActionListener(this);

       // ---- Popup Menu for Task Types ----
       popup = new JPopupMenu();
       popup.add(new JLabel ("Task Type"));
       popup.addSeparator();

       primitiveMenuItem = new JMenuItem(
          AbstractTask.getTypeName(AbstractTask.PRIMITIVE));
       primitiveMenuItem.addActionListener(this);
       popup.add(primitiveMenuItem);

       summaryMenuItem  = new JMenuItem(
          AbstractTask.getTypeName(AbstractTask.SUMMARY));
       summaryMenuItem.addActionListener(this);
       popup.add(summaryMenuItem);

       behaviourMenuItem = new JMenuItem(
          AbstractTask.getTypeName(AbstractTask.BEHAVIOUR));
       behaviourMenuItem.addActionListener(this);
       popup.add(behaviourMenuItem);

       scriptMenuItem  = new JMenuItem(
          AbstractTask.getTypeName(AbstractTask.SCRIPT));
       scriptMenuItem.addActionListener(this);
       popup.add(scriptMenuItem);

       CompoundBorder cbr = new CompoundBorder(new EtchedBorder(),
                                               new EmptyBorder(5,5,5,5));
       popup.setBorder(cbr);
     }

     public void actionPerformed(ActionEvent e) {
       Object src = e.getSource();

       if ( src == newBtn ) {
          popup.pack();
          popup.show(newBtn,0,0);
       }
       else if ( src == primitiveMenuItem )
          taskTable.addNewTask(AbstractTask.PRIMITIVE);
       else if ( src == summaryMenuItem )
          taskTable.addNewTask(AbstractTask.SUMMARY);
       else if ( src == behaviourMenuItem )
          taskTable.addNewTask(AbstractTask.BEHAVIOUR);
       else if ( src == scriptMenuItem )
          taskTable.addNewTask(AbstractTask.SCRIPT);
       else if ( src == editBtn )
          taskTable.editTask();
       else if ( src == deleteBtn )
          taskTable.removeTask();
       else if ( src == cloneBtn )
          taskTable.cloneTask();
       else if ( src == renameBtn )
          taskTable.renameTask();
       else if ( src == helpBtn ) {
         if ( helpBtn.isSelected() ) {
           Point dispos = getLocation();
           helpWin = new HelpWindow(SwingUtilities.getRoot(this),dispos,
                                    "generator", "Task Options");
           helpWin.setSource(helpBtn);
         }
         else
           helpWin.dispose();
       }
     }
   }


}

