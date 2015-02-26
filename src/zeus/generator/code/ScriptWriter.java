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



package zeus.generator.code;

import java.io.*;
import javax.swing.JTextArea;
import zeus.generator.GeneratorModel;
import zeus.util.SystemProps;

/** 
   * ScriptWriter is responsible for generating the files that are used to 
   * invoke a zeus agency from a command prompt
   * This class was altered somewhat for v1.2.2 so that zsh is supported. 
   */
public class ScriptWriter extends Writer {
    
   protected String language = SystemProps.getProperty("agent.language"); 
   protected int ZSHUNIX = 2; 
   protected int ZSHWINDOWS = 3; 
   
   
   protected static final String[][] ScriptName = {
      { "run1",     "run3",     "run2" },     // unix
      { "run1.bat", "run3.bat", "run2.bat" },  // windows
      { "all.scr","runAll"},
      { "all.scr","runAll.bat" } // zsh windows 
   };
   
   
   protected static final String[][] Preamble = {
      { "#!/usr/bin/csh\n#This script runs the root servers and should be run first",
        "#!/usr/bin/csh\n#This script runs the non-root servers and other utility agents",
        "#!/usr/bin/csh\n#This script runs the task agents"
      },
      { "REM This script runs the root servers and should be started first",
        "REM This script runs the non-root servers and other utility agents",
        "REM This script runs the task agents"
      }
      , 
      {""}
            
   };

   public ScriptWriter(GenerationPlan genplan, GeneratorModel genmodel,
                       String directory, JTextArea textArea) {
      super(genplan,genmodel,directory,textArea);
   }

   public void write() {
      String platform = genplan.getPlatform();
      int mode = UNIX;

      if ( platform.equals(GenerationPlan.UNIX) )
         mode = UNIX;
      else if ( platform.equals(GenerationPlan.WINDOWS) )
         mode = WINDOWS;
      else {
         textArea.append("Error: Unknown platform " + platform +
            "-- cannot write scripts");
         return;
      }
     String shell = genplan.getShell(); 
     if (shell.equals(GenerationPlan.NONE)) 
        normalScripts (mode); 
        else if (shell.equals (GenerationPlan.ZSH)) 
            zshScripts (mode); 
}

    /** 
      *  generate scripts for the Zsh shell - one script that runs the nameserver, then runs
      *  a instance of zsh and invokes the task agents in it, and the facilitator. 
      *  A runVis script is also written so that visualisation agents can be run easily
      *  This is quite a simple implementation, so it doesn't handle all the issues like 
      *  remote deployment that zeus normally does. 
      *@author Simon Thompson
      *@since 1.2.2
      *@param mode - unix(0) or widows (1) 
      *@returns nothing. 
        */
    public void zshScripts (int mode) { 
       try {
            NameserverInfo[] server = genplan.getNameservers();
            FacilitatorInfo[] facilitator = genplan.getFacilitators();
            VisualiserInfo[] visualiser = genplan.getVisualisers();
            DbProxyInfo[] proxy = genplan.getDbProxys();
            AgentInfo[] agent = genplan.getAgents();

            // write root servers into first script
            textArea.append("Writing zsh.zsh script " +
	                    ScriptName[ZSHUNIX][0] + "\n");

            PrintWriter out = createFile(ScriptName[mode+ZSHUNIX][0]);
	        out.println("ns"); 
    	     
            for(int i = 0; i < agent.length; i++ ) {
                String cmd = writeForScript(mode,agent[i],out);
                textArea.append(cmd + "\n"); 
            }
           
	        out.println("yp"); 
	        out.println("vis"); 
	        out.flush(); 
	        out.close(); 
	        
	        textArea.append ("Writing batch file: "+ ScriptName[mode+ZSHUNIX][1]) ; 
	        out = createFile (ScriptName[mode + ZSHUNIX][1]); 
	        String ontology = agent[0].ontology_file;
	        if (mode == UNIX) { 
	            out.println("java zsh.zsh " + ontology + " 0.2 all.scr &");
	            }
	              else
	             {
	                 out.println("start /min java zsh.zsh " + ontology + " 0.2 all.scr ");
	             }

	     out.flush(); 
	     out.close(); 

      }
      catch(IOException e) {
         textArea.append("I/O Error while attempting to create/write script");
      }
        
    }


    public void normalScripts(int mode) { 
      try {
         NameserverInfo[] server = genplan.getNameservers();
         FacilitatorInfo[] facilitator = genplan.getFacilitators();
         VisualiserInfo[] visualiser = genplan.getVisualisers();
         DbProxyInfo[] proxy = genplan.getDbProxys();
         AgentInfo[] agent = genplan.getAgents();

         // write root servers into first script
         textArea.append("Attempting to write script: " +
	                 ScriptName[mode][0] + "\n");

         PrintWriter out = createFile(ScriptName[mode][0]);
	 out.println(Preamble[mode][0]);

	 for(int i = 0; i < server.length; i++ ) {
            if ( server[i].is_root )
               writeCommand(mode,server[i],out);
         }
         out.flush();
         out.close();
         textArea.append("Script: " + ScriptName[mode][0] +
	                 " written successfully\n");

	 // write non-root servers, visualisers, facilitators & dbProxys
         // into second script
         textArea.append("Attempting to write script: " +
	                 ScriptName[mode][1] + "\n");

         out = createFile(ScriptName[mode][1]);
	 out.println(Preamble[mode][1]);

         for(int i = 0; i < server.length; i++ )
            if ( !server[i].is_root )
               writeCommand(mode,server[i],out);

         for(int i = 0; i < facilitator.length; i++ )
            writeCommand(mode,facilitator[i],out);

         for(int i = 0; i < visualiser.length; i++ )
            writeCommand(mode,visualiser[i],out);

         for(int i = 0; i < proxy.length; i++ )
            writeCommand(mode,proxy[i],out);

         out.flush();
         out.close();
         textArea.append("Script: " + ScriptName[mode][1] +
	                 " written successfully\n");

	 // write agents into third script
         textArea.append("Attempting to write script: " +
	                 ScriptName[mode][2] + "\n");

         out = createFile(ScriptName[mode][2]);
	 out.println(Preamble[mode][2]);

         for(int i = 0; i < agent.length; i++ )
            writeCommand(mode,agent[i],out);

         out.flush();
         out.close();
         textArea.append("Script: " + ScriptName[mode][2] +
	                 " written successfully\n");
         switch(mode) {
            case UNIX:
                 textArea.append("Setting script permissions\n");
                 Runtime runtime = Runtime.getRuntime();
                 Process proc = runtime.exec("chmod 777 " +
                                ScriptName[mode][0] + " " +
                                ScriptName[mode][1] + " " +
                                ScriptName[mode][2]);
/*
The permissions cannot be change for some reason
                 try {
                    int exitStatus = proc.waitFor();
                 }
                 catch(InterruptedException e) {
                    textArea.append("Warning: could not change script permissions\n");
                 }
*/
                 break;
         }

      }
      catch(IOException e) {
         textArea.append("I/O Error while attempting to create/write script");
      }
   }



    /** 
        very simple at the moment, could extend it to allow people 
        to specify the number of these to be run
        */
   protected String writeForScript (int mode, GenerationInfo info, 
                                    PrintWriter out ) { 
                                        
      String[] summary = info.summarize();
      String cmd = summary[GenerationInfo.COMMAND];                            
      out.println(cmd);        
      return cmd;
                                    }
                                    
    

   protected void writeCommand(int mode, GenerationInfo info,
                               PrintWriter out) {
      String error;

      if ( (error = info.isValid()) != null ) {
         textArea.append(error);
         return;
      }

      String[] summary = info.summarize();
      String cmd = language + " " + summary[GenerationInfo.COMMAND];
      String dir;

      switch(mode) {
         case UNIX:
              cmd += " & ";
              dir = updateFilename(directory,UNIX);
              if ( info.host == null || info.host.equals("127.0.0.1") ||
                   info.host.equals(GenerationInfo.LOCALHOST) )
                 out.println(cmd);
              else
                 out.println("rsh " + info.host + " \'cd " + dir +
		    "; " + cmd + "\' & ");
              break;

         case WINDOWS:
              cmd = "start /min " + cmd;
              dir = updateFilename(directory,WINDOWS);
              if ( info.host == null || info.host.equals("127.0.0.1") ||
                   info.host.equals(GenerationInfo.LOCALHOST) )
                 out.println(cmd);
              else
                 out.println("rsh " + info.host + " cd " + dir +
		    "; " + cmd);
              break;
      }
   }
}
