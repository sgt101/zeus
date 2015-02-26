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



/*
 * @(#)ANServer.java 1.00
 */

package zeus.agents;

import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.Address;
import zeus.concepts.ZeusParser;
import zeus.actors.*;

/**
 * The implementation of the Zeus Agent Name Server (ANS). An agent society
 * must possess at least one ANS, in order to maintain a registry of known agents,
 * enabling agent identities to be mapped to their logical network location.
 * This is necessary because to ensure location independence agents only know
 * the names of their acquaintances and not their locations. <p>
 *
 * It is unlikely that users will need to change or call directly any of the
 * methods of this class.
 */

public class ANServer extends BasicAgent
{

  public ANServer(String name, Vector nameservers, Clock clock) {
    super(SystemProps.getProperty("agent.names.nameserver"),
          name,nameservers,clock);
  }

  protected static void version() {
    System.err.println("ANServer version: " +
                       SystemProps.getProperty("version.id"));
    System.exit(0);
  }

  protected static void usage() {
    System.err.println("Usage: java ANServer <name> " +
           "[-s <dns_file>] [-t time_grain] " +
           "[-f <addressoutfile>] [-gui ViewerProg] " +
                       "[-e ExternalProg] [-debug] [-h] [-v]");
    System.exit(0);
  }

  public static void main(String[] arg) {
    // debug classpath problems for lamers. 
    // added by simon 21/08/00
    try {
         Class c = Class.forName("java.lang.Object"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find java.lang.Object.\n This indicates that the rt.jar file is not in your classpath.\n Ensure that $java_install_dir\\jre\\rt.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}
 /*   try {
        // obscure zeus class picked to try it 
         Class c = Class.forName("zeus.gui.help.HelpWindow"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find a zeus class.\n This indicates that the zeus.jar file is not in your classpath.\n Ensure that zeus_install_dir\\lib\\zeus.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}   */
 /*   try {
         Class c = Class.forName("gnu.regexp.REException"); 
    }
    catch (ClassNotFoundException cnfe) { 
       System.out.println("Java cannot find a utility object.\n This indicates that the gnu-regexp.jar file is not in your classpath.\n Ensure that $zeus_install_dir\\lib\\gnu-regexp.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}*/
    Vector nameservers = new Vector();
    String dns_file = null, dout = null;
    String time_grain = null;
    String gui = null;
    String external = null;

    if ( arg.length < 1 )  usage();
    else
      for( int i = 1; i < arg.length; i++ ) {
        if ( arg[i].equals("-s") && ++i < arg.length )
          dns_file = arg[i];
        else if ( arg[i].equals("-t") && ++i < arg.length )
          time_grain = arg[i];
        else if ( arg[i].equals("-e") && ++i < arg.length )
          external = arg[i];
        else if ( arg[i].equals("-f") && ++i < arg.length )
          dout = arg[i];
        else if ( arg[i].equals("-gui") && ++i < arg.length )
          gui = arg[i];
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
      if ( dns_file != null ) {
         nameservers = ZeusParser.addressList(new FileInputStream(dns_file));
      if ( nameservers == null || nameservers.isEmpty() )
      throw new IOException();
      }

      Clock clock = null;
      if ( time_grain != null ) {
        double time_incr = (Double.valueOf(time_grain)).doubleValue();
        long t = System.currentTimeMillis();
        clock = new Clock(t, (long)(60000*time_incr));
      }
      ANServer ans = new ANServer(arg[0],nameservers,clock);

      if ( dout != null ) {
        PrintWriter out = new PrintWriter(new FileWriter(dout));
        out.println(ans.getAgentContext().MailBox().getAddress());
        out.flush();
        out.close();
      }

      Class c;
      AgentContext context = ans.getAgentContext();

      if ( gui != null ) {
         c = Class.forName(gui);
         BasicAgentUI ui = (BasicAgentUI) c.newInstance();
         context.set(ui);
         ui.set(context);
      }
      if ( external != null ) {
         c = Class.forName(external);
         ZeusExternal user_prog = (ZeusExternal)c.newInstance();
         context.set(user_prog);
         user_prog.exec(ans.getAgentContext());
      }
      
 

    }
    catch(Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}
