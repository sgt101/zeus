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
* CodeGenerator.java
*
* Sub-window that facilitates the code generation process
*****************************************************************************/

package zeus.generator.code;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.gui.help.*;
import zeus.generator.*;
import zeus.util.*;
import zeus.concepts.*;

public class CodeGenerator extends JFrame 
{
  static final String CODE_GENERATOR = "Code Generator";

  protected JTabbedPane     tabbedPane;
  protected GenerationPanel generationPanel;
  protected UtilityPanel    utilityPanel;
  protected AgentPanel      agentPanel;
  protected TaskPanel       taskPanel;
  protected GeneratorModel  genmodel;
  protected GenerationPlan  genplan;
  protected boolean         isSubFrame = false;
  protected AgentWriter     agentWriter = null;
  protected TaskWriter      taskWriter = null;
  protected ScriptWriter    scriptWriter = null;

  public CodeGenerator(GeneratorModel genmodel, GenerationPlan genplan,
                       boolean isSubFrame) {
     this(genmodel,genplan);
     this.isSubFrame = isSubFrame;
  }

  public CodeGenerator(GeneratorModel genmodel, GenerationPlan genplan) 
  {
    this.genmodel = genmodel;
    this.genplan = genplan;

    setTitle(CODE_GENERATOR);
    JPanel panel = (JPanel)getContentPane();
    panel.setBackground(Color.gray);

    String path = SystemProps.getProperty("gif.dir") + "generator" +
                  System.getProperty("file.separator");
    ImageIcon icon = new ImageIcon(path + "stripe.gif");
    setIconImage(icon.getImage());

    panel.setLayout(new BorderLayout());
    tabbedPane = new JTabbedPane();

    generationPanel = new GenerationPanel(genplan);
    utilityPanel    = new UtilityPanel(genplan);
    agentPanel      = new AgentPanel(genplan);
    taskPanel       = new TaskPanel(genplan);

    tabbedPane.addTab("Generation Plan", generationPanel);
    tabbedPane.addTab("Utility Agents", utilityPanel);
    tabbedPane.addTab("Task Agents", agentPanel);
    tabbedPane.addTab("Tasks", taskPanel);
    tabbedPane.setSelectedIndex(0);

    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
    panel.add(tabbedPane,BorderLayout.CENTER);
    panel.add(new ControlPanel(),BorderLayout.NORTH);
    panel.setPreferredSize(new Dimension(800,640));
    this.addWindowListener(
       new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
             cancel();
          }
       }
    );

    pack();
  }

  protected class ControlPanel extends JToolBar implements ActionListener {
     protected JButton       generateBtn;
     protected JButton       clearBtn;
     protected JButton       cancelBtn;
     protected JToggleButton helpBtn;
     protected HelpWindow    helpWin;

     public ControlPanel() {
        setBackground(Color.lightGray);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setVisible(true);

        String sep = System.getProperty("file.separator");
        String path = SystemProps.getProperty("gif.dir") + "generator" + sep;

        generateBtn = new JButton(new ImageIcon(path + "generate.gif"));
        generateBtn.addActionListener(this);
        generateBtn.setMargin(new Insets(0,0,0,0));
        generateBtn.setToolTipText("Generate source code");
        add(generateBtn);

        addSeparator();

        clearBtn = new JButton(new ImageIcon(path + "clear-big.gif"));
        clearBtn.addActionListener(this);
        clearBtn.setMargin(new Insets(0,0,0,0));
        clearBtn.setToolTipText("Clear code generation plan");
        add(clearBtn);

        addSeparator();

        cancelBtn = new JButton(new ImageIcon(path + "cancel.gif"));
        cancelBtn.addActionListener(this);
        cancelBtn.setMargin(new Insets(0,0,0,0));
        cancelBtn.setToolTipText("Dismiss window");
        add(cancelBtn);

        addSeparator();

        helpBtn = new JToggleButton(new ImageIcon(path + "info.gif"));
        helpBtn.addActionListener(this);
        helpBtn.setMargin(new Insets(0,0,0,0));
        helpBtn.setToolTipText("Help about code generator");
        add(helpBtn);

        validate();
     }

     public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if ( src == generateBtn )
            
           generateCode();
        else if ( src == clearBtn )
           genplan.reset();
        else if ( src == cancelBtn )
           cancel();
        else if ( src == helpBtn ) {
           if ( helpBtn.isSelected() ) {
              helpWin = new HelpWindow(SwingUtilities.getRoot(this),
                getLocation(), "generator", "Code Generator");
              helpWin.setSource(helpBtn);
           }
           else
              helpWin.dispose();
        }
     }
  }

  protected void cancel() {
      this.setVisible(false);
      if ( !isSubFrame ) {
         System.exit(0);
      }
  }

  protected String createTargetDirectory()  {
    String directory = genplan.getDirectory();
    if ( directory != null )
       directory = directory.trim();
    if ( directory == null || directory.equals("") )
       directory = System.getProperty("user.dir");

    File f = new File(directory);
    if ( f.exists() ) {
       if (f.isFile())
	  directory = f.getPath();
       else
	  directory = f.getAbsolutePath();
    }
    else if (!f.mkdirs()) {
       JOptionPane.showMessageDialog(this,"Cannot create directory\nCheck path name",
          "Error", JOptionPane.ERROR_MESSAGE);
       return null;
    }
    directory += File.separator;
    return directory;
  }

  protected void generateCode()
  {
    JTextArea textArea = generationPanel.getTextArea();
    textArea.setText("");

    // added by Jaron 1/11/00
    // to prevent problems where users have not saved their ontology
    String file = genmodel.ontology.getFilename();
    if (file == null) {
      textArea.append("You can not generate code until you have saved your ontology");
      return;
    }

    String directory = createTargetDirectory();
  
    if (directory == null || directory.equals("")) {
      textArea.append("You can not generate code until you have specified a target directory");
      return;
    }

    textArea.append("###### Code Generation Started ######\n\n");
    if ( agentWriter == null ) 
    {
      
      agentWriter = new AgentWriter(genplan,genmodel,directory,textArea);
      taskWriter = new TaskWriter(genplan,genmodel,directory,textArea);
      scriptWriter = new ScriptWriter(genplan,genmodel,directory,textArea);
    }
    
    scriptWriter.setDirectory (directory);
    agentWriter.setDirectory(directory);
    taskWriter.setDirectory(directory);
  
    scriptWriter.write();
    agentWriter.write();
    taskWriter.write();
    textArea.append("\n###### Code Generation Completed ######\n");
  }

  protected static void usage() {
    System.err.println("Usage: java CodeGenerator -f <file> [-h] [-v]");
    System.exit(0);
  }

  protected static void version() {
    System.err.println("Zeus Code Generator version: " +
                       SystemProps.getProperty("version.id"));
    System.exit(0);
  }

  public static void main(String[] arg) {
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

     if ( filename == null ) {
        System.err.println("Error: No filename specified");
        usage();
     }

     GenSym genSym = new GenSym("AgentGenerator");
     OntologyDb ontology = new OntologyDb(genSym);
     GeneratorModel genmodel = new GeneratorModel(ontology);
     GenerationPlan genplan = new GenerationPlan(genmodel,ontology);
     FileHandler filer = new FileHandler(ontology,genmodel,genplan);

     CodeGenerator generator = new CodeGenerator(genmodel,genplan);
     generator.setVisible(true);

     String dir = System.getProperty("user.dir") +
                  System.getProperty("file.separator");
     File f = new File(dir + filename);

     Frame frame = (Frame)SwingUtilities.getRoot(generator);
     Cursor lastCursor = frame.getCursor();
     frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
     int status = filer.openFile(f);
     frame.setCursor(lastCursor);
     if ( (status & FileHandler.ERROR_MASK) != 0 ) {
        JOptionPane.showMessageDialog(generator,filer.getError(),
           "Error",JOptionPane.ERROR_MESSAGE);
        System.exit(0);
     }
     else if ( (status & FileHandler.WARNING_MASK) != 0 ) {
        JOptionPane.showMessageDialog(generator,filer.getWarning(),
           "Warning",JOptionPane.WARNING_MESSAGE);
     }
  }
}
