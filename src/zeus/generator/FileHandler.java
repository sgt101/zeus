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
* FileHandler.java
*
* Provides persistance for the Agent Generator
*****************************************************************************/

package zeus.generator;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.code.*;

public class FileHandler {

  static final String BEGIN_GENERATOR		= "BEGIN_GENERATOR";
  static final String BEGIN_PREAMBLE		= "BEGIN_PREAMBLE";
  static final String SYSTEM			= ":system";
  static final String SYSTEM_NAME               = "ZEUS-Agent-Generator";
  static final String VERSION			= ":version";
  static final String ONTOLOGY			= ":ontology";
  static final String END_PREAMBLE		= "END_PREAMBLE";
  static final String BEGIN_AGENT_NAME_INDEX	= "BEGIN_AGENT_NAME_INDEX";
  static final String END_AGENT_NAME_INDEX	= "END_AGENT_NAME_INDEX";
  static final String BEGIN_AGENT_ICON_INDEX	= "BEGIN_AGENT_ICON_INDEX";
  static final String END_AGENT_ICON_INDEX	= "END_AGENT_ICON_INDEX";
  static final String BEGIN_TASK_NAME_INDEX	= "BEGIN_TASK_NAME_INDEX";
  static final String END_TASK_NAME_INDEX	= "END_TASK_NAME_INDEX";
  static final String BEGIN_AGENT_LIST		= "BEGIN_AGENT_LIST";
  static final String END_AGENT_LIST		= "END_AGENT_LIST";
  static final String BEGIN_TASK_LIST		= "BEGIN_TASK_LIST";
  static final String END_TASK_LIST		= "END_TASK_LIST";
  static final String BEGIN_GENERATION_PLAN	= "BEGIN_GENERATION_PLAN";
  static final String END_GENERATION_PLAN	= "END_GENERATION_PLAN";
  static final String END_GENERATOR		= "END_GENERATOR";
  static final String PLATFORM                  = ":platform";
  static final String DIRECTORY                 = ":directory";
  static final String BEGIN_NAMESERVER_LIST     = "BEGIN_NAMESERVER_LIST";
  static final String END_NAMESERVER_LIST       = "END_NAMESERVER_LIST";
  static final String BEGIN_FACILITATOR_LIST    = "BEGIN_FACILITATOR_LIST";
  static final String END_FACILITATOR_LIST      = "END_FACILITATOR_LIST";
  static final String BEGIN_VISUALISER_LIST     = "BEGIN_VISUALISER_LIST";
  static final String END_VISUALISER_LIST       = "END_VISUALISER_LIST";
  static final String BEGIN_DBPROXY_LIST        = "BEGIN_DBPROXY_LIST";
  static final String END_DBPROXY_LIST          = "END_DBPROXY_LIST";


  protected static final String TFILE = "___file";
  protected int count = 0;

  public static final int WARNING_MASK = 1;
  public static final int ERROR_MASK   = 2;

  protected String error = null;
  protected String warning = null;

  protected GeneratorModel genmodel;
  protected GenerationPlan genplan;
  protected OntologyDb     ontology;


  public FileHandler(OntologyDb ontology, GeneratorModel genmodel,
                     GenerationPlan genplan) {
     this.genmodel = genmodel;
     this.genplan = genplan;
     this.ontology = ontology;
  }

  //---------------------------------------------------------------------------
  public int saveFile(File f) {
    error = null;
    warning = null;
    int status = 0;

    int num = 0;
    String dir = f.getParent();
    String name = (dir != null) ? dir + File.separator + TFILE : TFILE;
    File file = new File(name);

    char sys_char = File.separatorChar;
    char zeus_char = SystemProps.getProperty("file.separator").charAt(0);

    String ontology_file = ontology.getFilename();
    ontology_file = Misc.relativePath(dir,ontology_file);
    ontology_file = ontology_file.replace(sys_char,zeus_char);

    String gen_dir = genplan.getDirectory();
    gen_dir = Misc.relativePath(dir,gen_dir);
    gen_dir = gen_dir.replace(sys_char,zeus_char);

    while( file.exists() )
       file = new File(name + (num++));

    try {
      String[] identifiers, names;
      String   icon;

      PrettyPrintWriter out = new PrettyPrintWriter(file);

      out.pprint(0,BEGIN_GENERATOR);
      out.pprint(1,BEGIN_PREAMBLE);
      out.pprint(2,SYSTEM   + " \"" + SYSTEM_NAME + "\"");
      out.pprint(2,VERSION + " \"" + SystemProps.getProperty("version.id") + "\"");
      out.pprint(2,ONTOLOGY + " \"" + Misc.escape(ontology_file) + "\"");
      out.pprint(1,END_PREAMBLE);

      out.println();
      out.pprint(1,BEGIN_AGENT_NAME_INDEX);
      identifiers = genmodel.getAgentIds();
      names = genmodel.getAgentNames();
      for(int i = 0; i < names.length; i++ )
         out.pprint(2, identifiers[i] + " " + names[i]);
      out.pprint(1,END_AGENT_NAME_INDEX);

      out.println();
      out.pprint(1,BEGIN_AGENT_ICON_INDEX);
      for(int i = 0; i < identifiers.length; i++ ) {
         icon = genmodel.getAgentIcon(identifiers[i]);
         icon = Misc.relativePath(dir,icon);
         icon = icon.replace(sys_char,zeus_char);
         out.pprint(2, identifiers[i] + " \"" + Misc.escape(icon) + "\"");
      }
      out.pprint(1,END_AGENT_ICON_INDEX);

      out.println();
      out.pprint(1,BEGIN_TASK_NAME_INDEX);
      identifiers = genmodel.getTaskIds();
      names = genmodel.getTaskNames();
      for(int i = 0; i < names.length; i++ )
         out.pprint(2, identifiers[i] + " " + names[i]);
      out.pprint(1,END_TASK_NAME_INDEX);

      out.println();
      out.pprint(1,BEGIN_AGENT_LIST);
      AgentDescription[] agents = genmodel.getAgents();
      for(int i = 0; i < agents.length; i++ )
         out.println(agents[i].pprint(2*2));
      out.pprint(1,END_AGENT_LIST);

      out.println();
      out.pprint(1,BEGIN_TASK_LIST);
      AbstractTask[] tasks = genmodel.getTasks();
      for(int i = 0; i < tasks.length; i++ )
         out.println(tasks[i].pprint(2*2));
      out.pprint(1,END_TASK_LIST);

      out.println();
      out.pprint(1,BEGIN_GENERATION_PLAN);
      out.pprint(2,BEGIN_PREAMBLE);
      out.pprint(3,PLATFORM + " \"" + genplan.getPlatform() + "\"");
      out.pprint(3,DIRECTORY + " \"" + Misc.escape(gen_dir) + "\"");
      out.pprint(2,END_PREAMBLE);

      AgentInfo[] agentInfo = genplan.getAgents();
      if ( agentInfo.length > 0 ) {
         out.pprint(2,BEGIN_AGENT_LIST);
         for(int i = 0; i < agentInfo.length; i++ )
            out.println(agentInfo[i].pprint(3*2));
         out.pprint(2,END_AGENT_LIST);
      }

      TaskInfo[] taskInfo = genplan.getTasks();
      if ( taskInfo.length > 0 ) {
         out.pprint(2,BEGIN_TASK_LIST);
         for(int i = 0; i < taskInfo.length; i++ )
            out.println(taskInfo[i].pprint(3*2));
         out.pprint(2,END_TASK_LIST);
      }

      NameserverInfo[] nameserverInfo = genplan.getNameservers();
      if ( nameserverInfo.length > 0 ) {
         out.pprint(2,BEGIN_NAMESERVER_LIST);
         for(int i = 0; i < nameserverInfo.length; i++ )
            out.println(nameserverInfo[i].pprint(3*2));
         out.pprint(2,END_NAMESERVER_LIST);
      }

      FacilitatorInfo[] facilitatorInfo = genplan.getFacilitators();
      if ( facilitatorInfo.length > 0 ) {
         out.pprint(2,BEGIN_FACILITATOR_LIST);
         for(int i = 0; i < facilitatorInfo.length; i++ )
            out.println(facilitatorInfo[i].pprint(3*2));
         out.pprint(2,END_FACILITATOR_LIST);
      }

      VisualiserInfo[] visualiserInfo = genplan.getVisualisers();
      if ( visualiserInfo.length > 0 ) {
         out.pprint(2,BEGIN_VISUALISER_LIST);
         for(int i = 0; i < visualiserInfo.length; i++ )
            out.println(visualiserInfo[i].pprint(3*2));
         out.pprint(2,END_VISUALISER_LIST);
      }

      DbProxyInfo[] dbProxyInfo = genplan.getDbProxys();
      if ( dbProxyInfo.length > 0 ) {
         out.pprint(2,BEGIN_DBPROXY_LIST);
         for(int i = 0; i < dbProxyInfo.length; i++ )
            out.println(dbProxyInfo[i].pprint(3*2));
         out.pprint(2,END_DBPROXY_LIST);
      }
      out.pprint(1,END_GENERATION_PLAN);
      out.pprint(0,END_GENERATOR);
      out.flush();
      out.close();

      if ( f.exists() ) f.delete();
      file.renameTo(f);
    }
    catch(Exception e) {
      error = e.toString();
      status |= ERROR_MASK;
    }
    return status;
  }


  //------------------------------------------------------------------------
  public int openFile(File file) {
    Assert.notNull(file);
    int status = 0;
    genplan.purge();

    try {
       Parser parser = new Parser(new FileInputStream(file));
       String dir = null;
       try {
          dir = file.getParentFile().getCanonicalPath();
       }
       catch(IOException e) {
          dir = file.getParentFile().getAbsolutePath();
       }
       parser.generator(this, genmodel, genplan, ontology, dir);
    }
    catch(Exception e) {
      genmodel.clear();
      genplan.reset();
      ontology.clear();

      error = e.toString();
      status |= ERROR_MASK;
    }
    if ( warning != null )
       status |= WARNING_MASK;

    return status;
  }

  public void __setWarning(String info) {
     if ( warning == null )
        warning = info;
     else
        warning += "\n" + info;
  }
  public void __setError(String info) {
     if ( error == null )
        error = info;
     else
        error += "\n" + info;
  }

  public String getWarning() {
    return warning;
  }
  public String getError() {
    return error;
  }
}
