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
* OntologyEditor.java
*
* October 2000 - Updated by Jaron to import schemas via JDBC
*
* Main frame of the Ontology Editor
*****************************************************************************/

package zeus.ontology;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

import zeus.util.*;
import zeus.gui.*;
import zeus.concepts.*;
import zeus.ontology.attributes.*;
import zeus.ontology.database.*;
import zeus.ontology.facts.*;
import zeus.ontology.restrictions.*;
import zeus.generator.GeneratorModel;

public class OntologyEditor 
{
   static final String[] MESSAGE = {  /* sequence of (question,title) pairs */
      /* 0 */ "Save current ontology?",
      /* 1 */ "Ontology save needed",
      /* 2 */ "File already exists.\nOverwrite saved file?",
      /* 3 */ "Save ontology as",
      /* 4 */ "Error",            // singleton
      /* 5 */ "Warning",           // singleton
      /* 6 */ "About ZEUS Ontology Editor",
      /* 7 */ "About",
      /* 8 */ "Using ontology: ",
      /* 9 */ "No ontology loaded",
      /* 9 */ "Clear current ontology?"
   };

   static final String ONTOLOGY_FILE_EXT = "*.*";

   static final String FILE           = "File";
   static final String NEW            = "New";
   static final String LOAD           = "Load...";
   static final String SAVE           = "Save...";
   static final String SAVE_AS        = "Save as...";
   static final String SAVE_AS_XML    = "Save as DAML...";
   static final String EXIT           = "Exit";
   static final String ABOUT          = "About";
   static final String ABOUT_ZEUS     = "About ZEUS...";

   public static final String ONTOLOGY_EDITOR = "Ontology Editor";

   protected JFrame          frame = null;
   protected JInternalFrame  iframe = null;
   protected JTabbedPane     tabbedPane;
   protected JLabel          infoLabel= null;
   protected Component       root = null;
   protected boolean         AS_XML = false;

   protected OntologyDb     ontologyDb = null;
   protected GeneratorModel genmodel = null;

   protected JMenuItem miNew;
   protected JMenuItem miOpen;
   protected JMenuItem miSave;
   protected JMenuItem miSaveAs;
   protected JMenuItem miSaveAsXML;
   protected JMenuItem miExit;
   protected JMenuItem miAbout;


   private static int NUMBER_DISPLAYED = 0;

   protected void initMenus() 
   {
     JMenuBar mainMenuBar = new JMenuBar();
     JMenu menu1 = new JMenu(FILE);
     miNew = new JMenuItem(NEW);
     miOpen = new JMenuItem(LOAD);
     miSave = new JMenuItem(SAVE);
     miSaveAs = new JMenuItem(SAVE_AS);
     miSaveAsXML = new JMenuItem(SAVE_AS_XML);
     miExit = new JMenuItem(EXIT);
     menu1.add(miNew);
     menu1.add(miOpen);
     menu1.add(miSave);
     menu1.add(miSaveAs);
     menu1.add(miSaveAsXML);
     menu1.addSeparator();
     menu1.add(miExit);
     mainMenuBar.add(menu1);

     JMenu menu2 = new JMenu(ABOUT);
     miAbout = new JMenuItem(ABOUT_ZEUS);
     menu2.add(miAbout);

     if ( frame != null )
        frame.setJMenuBar(mainMenuBar);
     else if ( iframe != null )
        iframe.setJMenuBar(mainMenuBar);
   }

   /**
    * Constructor invoked from the Agent Generator tool
    */
   public OntologyEditor(OntologyDb ontologyDb, Component root, GeneratorModel genmodel, JLabel infoLabel)
   {
     this.ontologyDb = ontologyDb;
     this.genmodel = genmodel;
     this.infoLabel = infoLabel;
     this.root = root;
     setTitle("");
   }

   /**
    * Constructor to build standalone ontology editor
    */
   public OntologyEditor(OntologyDb ontologyDb, boolean isFrame) 
   {
     this.ontologyDb = ontologyDb;
     if ( isFrame )
       createFrame();
   }


//---------------------------------------------------------------------------
   JTabbedPane getTabbedPane() {
      JTabbedPane tabbedPane = new JTabbedPane();
      Component treepanel = new FactTreePane(this,ontologyDb);
      tabbedPane.addTab("Known Facts", null, treepanel,
                         "Shows the hierarchy of known facts");

      Component rpanel = new RestrictionTableUI(ontologyDb);
      tabbedPane.addTab("Restriction Definitions", null, rpanel,
                         "Shows a table containing known restrictions");

      Component dbpanel = new DatabasePane(ontologyDb, genmodel);
      tabbedPane.addTab("Database Import", null, dbpanel,
                        "Allows facts to be imported from relational database schemas");

      tabbedPane.setSelectedIndex(0);
      tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

      return tabbedPane;
   }

   ImageIcon getIcon() {
      String sep = System.getProperty("file.separator");
      String gifpath = SystemProps.getProperty("gif.dir") + "ontology" + sep;
      ImageIcon icon = new ImageIcon(gifpath + "cloudicon.gif");
      return icon;
   }

   void registerListeners() {
      SymAction lSymAction = new SymAction();
      miNew.addActionListener(lSymAction);
      miOpen.addActionListener(lSymAction);
      miSave.addActionListener(lSymAction);
      miSaveAs.addActionListener(lSymAction);
      miSaveAsXML.addActionListener(lSymAction);

      miExit.addActionListener(lSymAction);
      miAbout.addActionListener(lSymAction);
   }

   public JInternalFrame createInternalFrame() {
      iframe = new JInternalFrame("Ontology Database",true,true,true,true);
      iframe.setTitle("Ontology Database:" + (++NUMBER_DISPLAYED));
      iframe.setFrameIcon(getIcon());

      iframe.getContentPane().setBackground(Color.gray);
      iframe.getContentPane().setLayout(new BorderLayout());
      iframe.getContentPane().add(getTabbedPane(),BorderLayout.CENTER);

      iframe.setBackground(Color.lightGray);
      iframe.pack();
      return iframe;
   }

   public JFrame createFrame() 
   {
     frame = new JFrame();
     frame.setIconImage(getIcon().getImage());

     frame.getContentPane().setBackground(Color.gray);
     frame.getContentPane().setLayout(new BorderLayout());
     frame.getContentPane().add(getTabbedPane(),BorderLayout.CENTER);

     initMenus();

     // REGISTER_LISTENERS
     frame.addWindowListener(new SymWindow());
     registerListeners();

     frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
     frame.setBackground(Color.lightGray);
     frame.setSize(540,540);
     String title = ontologyDb.getFilename();
     if ( title == null ) title = "";
     setTitle(title);
     frame.pack();
     frame.setVisible(true);
     return frame;
   }

   protected void setTitle(String title) {
     if ( frame != null ) {
         if ( title.equals("") )
            frame.setTitle(ONTOLOGY_EDITOR);
         else
            frame.setTitle(ONTOLOGY_EDITOR + ": " + title);
     }
     else if ( iframe != null ) {
         if ( title.equals("") )
            iframe.setTitle(ONTOLOGY_EDITOR);
         else
            iframe.setTitle(ONTOLOGY_EDITOR + ": " + title);
     }
     if ( infoLabel != null ) {
         if ( title.equals("") )
            infoLabel.setText(MESSAGE[9]);
         else
            infoLabel.setText(MESSAGE[8] + " " + Misc.relativePath(title));
     }
   }

   protected Component getRoot() {
     return frame != null ? (Component)frame : (Component)root;
   }

   public void showFrame() {
     if ( frame == null )
        createFrame();
     else
        frame.setVisible(true);
   }

   class SymWindow extends WindowAdapter {
      public void windowClosing(WindowEvent event) {
         Exit();
      }
   }

   class SymAction implements ActionListener {
      public void actionPerformed(ActionEvent event) {
         Object object = event.getSource();
         if (object == miNew)
    	    Close();
         else if (object == miOpen)
    	    Open();
         else if (object == miSave)
    	    Save();
         else if (object == miSaveAs)
    	    SaveAs();
         else if (object == miSaveAsXML)
    	    SaveAsXML();
         else if (object == miExit)
    	    Exit();
         else if (object == miAbout)
    	    About();
      }
   }

   public boolean Close() {
     if ( ontologyDb.isSaveNeeded() ) {
        int answer = JOptionPane.showConfirmDialog(getRoot(),MESSAGE[0],
                     MESSAGE[1],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION && !Save() )
           return false;
        if ( answer == JOptionPane.CANCEL_OPTION )
           return false;
     }
     return closeFile();
   }

   public void Open() {
      File f = getFile(FileDialog.LOAD);
      String fname;

/*
      if (f != null) {
         fname = f.getName();
         if (fname.endsWith(".xml") || fname.endsWith(".dtd")) {
            Xml2Zeus xml_zeus = new Xml2Zeus(f);
	    f = xml_zeus.getOntFile();
         }
      }
*/
      if ( f != null && Close() )
         openFile(f);
   }

   public boolean Save() {
      if ( ontologyDb.getFilename() == null )
         return SaveAs();
      return saveFile(new File(ontologyDb.getFilename()));
   }

   boolean SaveAs() {
      File f = getFile(FileDialog.SAVE);
      if ( f != null ) {
         File f1 = null;
         String filename = ontologyDb.getFilename();
         if ( filename != null ) f1 = new File(filename);
         if ( f.exists() && f1 != null && !f.equals(f1) ) {
            int answer = JOptionPane.showConfirmDialog(getRoot(),MESSAGE[2],
                         MESSAGE[3],JOptionPane.YES_NO_OPTION);
            if ( answer == JOptionPane.NO_OPTION )
               return false;
        }
        return saveFile(f);
      }
      if (AS_XML)
        AS_XML = false;
      return false;
   }

   boolean SaveAsXML() {
      AS_XML = true;
      return SaveAs();
   }

   void Exit() {
     if ( root == null && ontologyDb.isSaveNeeded() ) {
        int answer = JOptionPane.showConfirmDialog(getRoot(),MESSAGE[0],
                     MESSAGE[1],JOptionPane.YES_NO_CANCEL_OPTION);
        if ( answer == JOptionPane.YES_OPTION && !Save() )
           return;
        if ( answer == JOptionPane.CANCEL_OPTION )
           return;
     }
     if ( root != null )
        frame.setVisible(false);
     else if ( frame != null ) {
        frame.dispose();
       // System.exit(0);
     }
     else if ( iframe != null ) {
        frame.dispose();
     }
   }

   void About() {
      JOptionPane.showMessageDialog(getRoot(),MESSAGE[6],MESSAGE[7],
                                    JOptionPane.INFORMATION_MESSAGE);
   }

   boolean saveFile( File f ) {
     Assert.notNull(f);
     int status;
     Frame frame = (Frame)SwingUtilities.getRoot(getRoot());
     Cursor lastCursor = frame.getCursor();
     frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

     if(AS_XML)
       status = ontologyDb.saveDAML(f);
     else
       status = ontologyDb.saveFile(f);

     frame.setCursor(lastCursor);
     if ( (status & OntologyDb.ERROR_MASK) != 0 ) {
        JOptionPane.showMessageDialog(getRoot(),ontologyDb.getError(),
           MESSAGE[4],JOptionPane.ERROR_MESSAGE);
        return false;
     }
     else if ( (status & OntologyDb.WARNING_MASK) != 0 ) {
        JOptionPane.showMessageDialog(getRoot(),ontologyDb.getWarning(),
           MESSAGE[5],JOptionPane.WARNING_MESSAGE);
     }
     if (!AS_XML)
      setTitle(f.getName());
     else
        AS_XML = false;
     return true;
   }


   boolean openFile(File f) {
     Assert.notNull(f);
     Frame frame = (Frame)SwingUtilities.getRoot(getRoot());
     Cursor lastCursor = frame.getCursor();
     frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
     int status = ontologyDb.openFile(f);
     frame.setCursor(lastCursor);
     if ( (status & OntologyDb.ERROR_MASK) != 0 ) {
        JOptionPane.showMessageDialog(getRoot(),ontologyDb.getError(),
           MESSAGE[4],JOptionPane.ERROR_MESSAGE);
       closeFile();
       return false;
     }
     else if ( (status & OntologyDb.WARNING_MASK) != 0 ) {
       JOptionPane.showMessageDialog(getRoot(),ontologyDb.getWarning(),
          MESSAGE[5],JOptionPane.WARNING_MESSAGE);
     }
     setTitle(f.getName());
     return true;
   }

   public boolean closeFile() {
     ontologyDb.clear();
     setTitle("");
     return true;
   }


   protected File getFile(int type) {
     FileDialog f = new FileDialog((Frame)SwingUtilities.getRoot(getRoot()),
                                   "Select File", type);

     File f1 = null;
     String filename = ontologyDb.getFilename();
     if ( filename != null ) f1 = new File(filename);
     if ( f1 != null ) {
        f.setFile(f1.getName());
        f.setDirectory(f1.getParent());
     }
     else
        f.setFile(ONTOLOGY_FILE_EXT);

     f.pack();
     f.setVisible(true);

     return f.getFile() == null ? null : new File(f.getDirectory(),f.getFile());
   }

   protected static void version() {
     System.err.println("OntologyEditor version: " +
                        SystemProps.getProperty("version.id"));
    // System.exit(0);
   }

   protected static void usage() {
     System.err.println("Usage: java OntologyEditor [-f <file>] [-h] [-v]");
     System.exit(0);
   }

   public static void main( String[] arg ) {
     String file = null;

     for( int i = 0; i < arg.length; i++ ) {
       if ( arg[i].equals("-f") && ++i < arg.length )
         file = arg[i];
       else if ( arg[i].equals("-h") )
         usage();
       else if ( arg[i].equals("-v") )
         version();
       else
         usage();
     }

     OntologyEditor z;
     z = new OntologyEditor(new OntologyDb(new GenSym("name")),true);

     if ( file != null ) {
        String dir = System.getProperty("user.dir") +
                     System.getProperty("file.separator");
        z.openFile( new File(dir + file) );
     }
   }
}
