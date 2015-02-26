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



/********************************************************************
 *  Visualiser.java - this creates and launches the Visualiser Hub  *
 ********************************************************************/

package zeus.visualiser;

import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.agents.*;


public class Visualiser extends BasicAgent {
  public Visualiser(String name, String filename, Vector nameservers,
                    boolean quickflag) {
    super(SystemProps.getProperty("agent.names.visualiser"),
          name, nameservers);

    context.set(new OntologyDb(context.GenSym()));
    context.MailBox().lowerStatus();
    context.MsgHandler().lowerStatus();

    if ( filename != null ) {
       OntologyDb db = context.OntologyDb();
       int status = db.openFile(new File(filename));
       if ( (status & OntologyDb.ERROR_MASK) != 0 ) {
          System.err.println("Ontology File I/O Error: " + db.getError());
          System.exit(0);
       }
       else if ( (status & OntologyDb.WARNING_MASK) != 0 )
         System.err.println("Warning: " + db.getWarning());
    }
    new VisualiserHub(context,quickflag);
  }

  protected static void version() {
    System.err.println("Visualiser version: " +
                       SystemProps.getProperty("version.id"));
    System.exit(0);
  }

  protected static void usage() {
    System.err.println("Usage: java Visualiser <name> -s <dns_file> " +
                       "[-o ontology_file] [-gui ViewerProg] " +
         "[-e ExternalProg] [-quick] [-debug] [-h] [-v]");
    System.exit(0);
  }

  public static void main(String[] arg){
        // debug classpath problems for lamers. 
    // added by simon 21/08/00
    try {
         Class c = Class.forName("java.lang.Object"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find java.lang.Object.\n This indicates that the rt.jar file is not in your classpath.\n Ensure that $java_install_dir\\jre\\rt.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}
    try {
        // obscure zeus class picked to try it 
         Class c = Class.forName("zeus.gui.help.HelpWindow"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find a zeus class.\n This indicates that the zeus.jar file is not in your classpath.\n Ensure that zeus_install_dir\\lib\\zeus.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}   
 /*   try {
         Class c = Class.forName("gnu.regexp.REException"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find a utility object.\n This indicates that the gnu-regexp.jar file is not in your classpath.\n Ensure that $zeus_install_dir\\lib\\gnu-regexp.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}*/
            
    Vector nameservers = null;
    String dns_file = null;
    String ontology_file = null;
    String gui = null;
    boolean quick = false;
    String external = null;

    if ( arg.length < 3 )  usage();
    else
      for( int i = 1; i < arg.length; i++ ) {
        if ( arg[i].equals("-s") && ++i < arg.length )
           dns_file = arg[i];
        else if ( arg[i].equals("-o") && ++i < arg.length )
           ontology_file = arg[i];
        else if ( arg[i].equals("-e") && ++i < arg.length )
           external = arg[i];
        else if ( arg[i].equals("-gui") && ++i < arg.length )
           gui = arg[i];
        else if ( arg[i].equals("-quick") )
           quick = true;
        else if ( arg[i].equals("-debug") ) {
	  Core.debug = true;
          Core.setDebuggerOutputFile(arg[0] + ".log");
        }
        else if ( arg[i].equals("-h") )
           usage();
        else if ( arg[i].equals("-v") )
           version();
        else
           usage();
      }

    try {
       nameservers = ZeusParser.addressList(new FileInputStream(dns_file));
       if ( nameservers == null || nameservers.isEmpty() )
          throw new IOException();

      Visualiser f = new Visualiser(arg[0],ontology_file,nameservers,quick);

      Class c;
      if ( gui != null ) {
         c = Class.forName(gui);
         BasicAgentUI ui = (BasicAgentUI) c.newInstance();
         ui.set(f.getAgentContext());
      }

      if ( external != null ) {
         c = Class.forName(external);
         ZeusExternal user_prog = (ZeusExternal)c.newInstance();
         user_prog.exec(f.getAgentContext());
      }

    }
    catch(Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}
