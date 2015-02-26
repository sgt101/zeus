/*
    Zsh -The Zeus Shell, a container for running more than one Zeus agent in a JVM, or for running
    large numbers of Zeus agents with similar names. 
    
Copyright (C) 2000 Chris Van Buskirk

This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The full license for this module can be found in the file lgpl.txt

This module was originally developed by Chris Van Buskirk.
Some additions by Simon Thompson.

* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/

package zsh;
import java.io.*;
import java.util.*;
/**
 Change Log
 ----------
 Simon 24/08/00 - removed the nameserver start from the main loop. 
 Simon 24/08/00 - added comments to provide Javadoc for users
 Simon 24/08/00 - changed agent invoker to provide following functionality. 
                  if an agent is invoked without options invoke a default option string, 
                  set in the bat file. 
                  if an agent is invoked with a number after it (agentName <args> <int bottom_range> 
                  <int top_range>)
                  then start top_range - bottom_range instances of the agent, and name them using 
                  the following convention agentNameXX where XX is a number
                  if an agent is invoked with a number after it (agentName <int bottom_range> 
                  <int top_range>)
                  then start top_range - bottom_range instances of the agent, and name them using 
                  the following convention agentNameXX where XX is a number
 
 
 Issues
 ------
 I have a strong feeling that this utiltiy will prove to be a stop-gap before we
 implement a console that allows the deployment and control of many agents from 
 one machine over a number of machines. At the moment you have to be on the console of the 
 machines that you have in your net in order to invoke agents on them. 
 
 But, having said that, it is a nice solution to a pressing problem : it sure ain't 
 broke yet!
 */

/** {@version 1.0}
    {@author Christopher P. van Buskirk}
    {@author Simon Thompson}
    {@param ontology file}
    <p>
    zsh (zeus shell) is a utility for deploying agents within one virtual machine. 
    Agents in a single virtual machine will run with a far smaller system overhead, 
    but are obviously more vunerable to system failure - one can bring the others down
    However, there has been substantial user pressure for this sort of facility in Zeus 
    and so Chris Van Buskirk wrote the zsh utility to support it. </p>
    <p> 
    To run Zsh from a DOS window type <i> zsh your_ontology.ont </i> 
    in the $zeus_install (where you installed Zeus)
    directory, or make sure  that $zeus_install is in your path. This will invoke zsh.bat, 
    which will invoke zsh.</p>
    <p> {@param ontology} is the name of the ontology file that you want to use for the 
    agency </p> 
    <p> 
    Your prompt should change to <b> 
    <code> Zeus 0> </code> 
    <p> 
    The commands are: <br> 
    <li> quit : exit Zsh and stop all the agents running in this shell
    <li> yp : start a Facilitator Agent (provides a yellow pages service for the Utility Agents)
    <li> help : provides a short help message 
    <li> viz  : run a Visualiser
    <li> vis : run a Visualiser
    <li> <i> AgentName <args> </i> :run a Utility Agent of type AgentName
    <li> <i> AgentName </i> :run a Utility Agent with the default arguement string set in the Zsh.bat file
    <li> <i> AgentName <args> - <bottom_range> <top_range> :run top_range-bottom_range copies of the 
    agent type <i> AgentName </i> each named according to the convention <i> AgentName_XX </i> where 
    XX is the number of this copy. 
    <li> <i> AgentName <bottom_range> -range <top_range> :run top_range copies of the 
    agent type <i> AgentName </i> each named according to the convention <i> AgentName_XX </i> where 
    XX is the number of this copy. Run the agents with the default parameters read from the Zsh.bat file
    <li> script : run a script containing commands according to the format given above.
    <p> After an agent is invoked you should see the number in the command prompt increase as 
    the shell counts the number of agents that it is running at the moment. 
*/  


public class zsh
{
  static int     numAgents = 0;
  static String  TIMESLICE = "0.5";
  static boolean manyAgents = false; 
  static String visualiser_ont = null; 
  static String facilitator_ont = null; 
  static String facilitator_time = null; 

/** 
    standard main function 
    */
  static public void main(String[] arg)
  {
    String           buffer;
    StringTokenizer  parser;
    String           keyword;
    String[]         params;
    BufferedReader   keyboard 
        = new BufferedReader (new InputStreamReader(System.in));
    
    // removed the nameserver auto start - zsh may run 
    // on many machines and starting the nameserver on each of them 
    // would be a mistake. 
   // ns();   // automatically start the name server
    zsh shell = new zsh(); 
    if (arg.length>0) {
        visualiser_ont = arg[0]; 
        facilitator_ont = arg[0];}
    if (arg.length>1) {
        facilitator_time = arg[1]; }
    while (true)
    {
        buffer  = shell.getCommand (keyboard);
        parser = new StringTokenizer(buffer); 
        keyword = shell.getKeyword (parser);
        params  = shell.getParams  (parser);
        if (!manyAgents) { 
            shell.doCommand(keyword,params);
            }
            else {
                shell.runManyAgents(buffer); 
            }
       manyAgents = false; 
    }
  } //method:  zsh::main()


   /**
    method called to run a clutch of agents
    */
  public void runManyAgents(String buffer) { 
     manyAgents = false; 
     StringTokenizer parser = new StringTokenizer (buffer);
     String keyword = this.getKeyword(parser); 
     String [] params = this.getParams(parser);
     System.out.println (params.length); 
     parser = new StringTokenizer (buffer); 
     String bottomRange = this.getBottomRange(parser); 
     String topRange = this.getTopRange(parser); 
     if (topRange == null) { 
        this.runAgents(keyword,params,bottomRange); }
        else {
           this.runAgents (keyword,params,bottomRange,topRange); 
        } 
   }


/**
    method called to run a number of agents named 0-> {@param numberAgents}
    */
    public void runAgents (String agentType, String[] params, String bottomRange, 
                                String topRange) { 
        int numberRequired = Integer.parseInt(topRange)-Integer.parseInt(bottomRange);
        int startCount = Integer.parseInt(bottomRange);
        for (int count = 0; count < numberRequired; count++) {                             
            String agentName = String.valueOf(count+startCount); 
            doCommand (agentType, agentName, params); }
    }


    /**
        method called to run a number of agents named 0-> {@param numberAgents}
    */
    public void runAgents (String agentType, String[] params, String numberAgents) { 
       int numberRequired = Integer.parseInt(numberAgents);
       System.out.println (params.length); 
       for (int count = 0; count < numberRequired; count++) {                             
            String agentName = String.valueOf(count); 
            doCommand (agentType, agentName, params); }
    }
    
    
    protected String getBottomRange (StringTokenizer parser) { 
        boolean counting = false; 
        manyAgents = false;
        int numberParams = parser.countTokens(); 
        while (parser.hasMoreTokens())
        {
            String temp = parser.nextToken(); 
        if (manyAgents == true) 
            return temp; 
        if (temp.equals ("-range")) {
            manyAgents = true; 
             }
        
        }
        return (null) ;
    }
    
    
    /**
        this method assumes that getBottom range has been called first and so 
        it will just return the next token on the stream
        */ 
    protected String getTopRange (StringTokenizer parser) {
        if (!parser.hasMoreTokens()) {
            return null; 
        }
        else {
            return (parser.nextToken()); 
        }}
    
    
  /** 
    get command reads a command from the specified input stream 
    */
  protected String getCommand(BufferedReader in)
  {
    String           buffer;

    System.out.print("ZEUS " +numAgents+ "> ");
    try
    {
      buffer = in.readLine(); 
      if (buffer == null)  {System.out.println();  return null;}
            return buffer;
    }
    catch (IOException e) {return null;}

  } //method: getComman()



   /**
    getKeyword parses out the command from the users input and 
    returns it as a String 
    */
  protected String getKeyword(StringTokenizer parser)
  {
    String output;

    if ((parser==null) || !parser.hasMoreTokens())  
    {
      return null;
    }  
    else
    {
      output = parser.nextToken();
      //System.out.println(output);
      return output;
    }
  } //method: getKeyword()
  

  /**
    getParams works out what the Zeus parameters are, and separates 
    them from any zsh specific parameters (ranges). 
    */ 
  protected String[] getParams(StringTokenizer parser)
  {
    
    String[] writeArray = new String [parser.countTokens()]; 
    int count = 0;  
    boolean counting = false; 
    int numberParams = writeArray.length; 
    for (int i=0;  i < numberParams;  i++)
    {
        String temp = parser.nextToken(); 
     
        if (temp.equals ("-range")) {
            manyAgents = true; 
             }
        if (manyAgents == true) 
            count ++; 
            writeArray[i] = temp;
    }
    if (count == 0) {
        return writeArray;
         }
        else { 
            String [] output = new String [numberParams - count];
            for (int copyCount = 0; copyCount<output.length; copyCount++) { 
                output[copyCount] = writeArray[copyCount]; 
            }
        return (output); 
        }
  }



 /**
    doCommand is the business part of the deal. 
    It's job is to run the command that the user has given. 
    */
 protected void doCommand(String command, String[] parameters)
  {
    if (command == null)           {System.out.println();   return;}
    if (command.equals("quit"))    {System.exit(0);                }
    if (command.equals("help"))    {help();                 return;}
    if (command.equals("yp"))      {yp();                   return;}
    if (command.equals("viz"))     {viz();                  return;}
    if (command.equals("vis"))     {viz();                  return;}
    if (command.equals("script"))  {script(parameters[0]);  return;}
    try
    {

      //////////////////////////////////////////////
      // ELSE: fire off the specified agent
      //////////////////////////////////////////////
      Class nextClass = Class.forName(command);
      java.lang.reflect.Method boot = nextClass.getMethod(
          "main", 
          new java.lang.Class[]  { java.lang.String[].class }
      );
      boot.invoke(
        nextClass.newInstance(), 
        new java.lang.Object[] {parameters}
      );
       try { 
          Thread.sleep(100); 
      }
      catch (Exception e) {;}
      numAgents++;
    }




    //////////////////////////////////////////////////
    // Exception Processing
    //////////////////////////////////////////////////
    catch (ClassNotFoundException e1) 
    {
      System.out.println("ERROR: could not load " +command+ " class");
    }
    catch (NoSuchMethodException  e2) 
    {
      System.out.println("ERROR: could not find " +command+ "::main()");
    }
    catch (java.lang.reflect.InvocationTargetException  e3) 
    {
      System.out.println("ERROR: invoking " +command+ "::main()");
      System.out.println(e3.toString());
    }
    catch (java.lang.InstantiationException  e4) 
    {
      System.out.println("ERROR: invoking " +command+ "::main()");
      System.out.println(e4.toString());
    }
    catch (java.lang.IllegalAccessException  e5) 
    {
      System.out.println("ERROR: invoking " +command+ "::main()");
      System.out.println(e5.toString());
    }


    System.out.println();
  } //method: doCommand()


 /**
    doCommand is the business part of the deal. 
    It's job is to run the command that the user has given. 
    */
 protected void doCommand(String command, String agentName, String[] parameters)
  {
    if (command == null)           {System.out.println();   return;}
    if (command.equals("quit"))    {System.exit(0);                }
    if (command.equals("help"))    {help();                 return;}
    if (command.equals("yp"))      {yp();                   return;}
    if (command.equals("viz"))     {viz();                  return;}
    if (command.equals("vis"))     {viz();                  return;}
    if (command.equals("script"))  {script(parameters[0]);  return;}
    
    try
    {
        System.out.println("in right doCommand"); 
        String newParams [] = new String [parameters.length +2]; 
        for (int count = 0; count < parameters.length; count ++) {
            System.out.println ("copying : " + parameters[count]); 
            newParams[count] = parameters[count]; 
        }
        newParams[parameters.length]= "-name";
        newParams[parameters.length +1] = agentName; 
      //////////////////////////////////////////////
      // ELSE: fire off the specified agent
      //////////////////////////////////////////////
      Class nextClass = Class.forName(command);
      java.lang.reflect.Method boot = nextClass.getMethod(
          "main", 
          new java.lang.Class[]  { java.lang.String[].class }
      );
      boot.invoke(
        nextClass.newInstance(),
        new java.lang.Object[] {newParams}
      );
      try { 
          Thread.sleep(100); 
      }
      catch (Exception e) {;}
      

      numAgents++;
    }



    //////////////////////////////////////////////////
    // Exception Processing
    //////////////////////////////////////////////////
    catch (ClassNotFoundException e1) 
    {
      System.out.println("ERROR: could not load " +command+ " class");
    }
    catch (NoSuchMethodException  e2) 
    {
      System.out.println("ERROR: could not find " +command+ "::main()");
    }
    catch (java.lang.reflect.InvocationTargetException  e3) 
    {
      System.out.println("ERROR: invoking " +command+ "::main()");
      System.out.println(e3.toString());
    }
    catch (java.lang.InstantiationException  e4) 
    {
      System.out.println("ERROR: invoking " +command+ "::main()");
      System.out.println(e4.toString());
    }
    catch (java.lang.IllegalAccessException  e5) 
    {
      System.out.println("ERROR: invoking " +command+ "::main()");
      System.out.println(e5.toString());
    }


    System.out.println();
  } //method: doCommand()


    /** 
    script is used to step through a file and execute the commands 
    found there in. Modified to support the initialisation of 
    multiple agents of a particular type. 
    */
  protected void script(String fname)
  {
    try
    {
      BufferedReader scriptFile = new BufferedReader(new FileReader(fname));
      StringTokenizer  parser;
      String           keyword;
      String[]         params;
      String           buffer;
      while ((buffer = getCommand (scriptFile)) != null)
      {
        parser = new StringTokenizer (buffer); 
        keyword = getKeyword (parser);
        params  = getParams  (parser);
        if (!manyAgents) { 
            this.doCommand(keyword,params);
            }
            else {
                this.runManyAgents(buffer); 
            }
       manyAgents = false; 
      }
    }
    catch (FileNotFoundException e)
    {
      System.out.println("ERROR: opening " +fname+ " script");
    }

  }



  /////////////////////////////////////////////////////////////////////////
  // Bootstrap the nameserver
  /////////////////////////////////////////////////////////////////////////
  static protected void ns()
  {
    String[] nsArgs = {
      "Nameserver1",
      "-t",  TIMESLICE,
      "-f",  "dns.db"
    };
    zeus.agents.ANServer.main(nsArgs);
  }



  /////////////////////////////////////////////////////////////////////////
  // Bootstrap the facilitator
  /////////////////////////////////////////////////////////////////////////
  static protected void yp()
  {
    String[] ypArgs = {
      "Facilitator1",
      "-o",  facilitator_ont,
      "-s",  "dns.db",
      "-t",  facilitator_time
    };
    zeus.agents.Facilitator.main(ypArgs);
    System.out.println();
  }



  /////////////////////////////////////////////////////////////////////////
  // Bootstrap the visualizer
  /////////////////////////////////////////////////////////////////////////
  static protected void viz()
  {
    String[] vizArgs = {
      "Visualiser1",
      "-o",  visualiser_ont,
      "-s",  "dns.db",
      "-quick"
    };
    zeus.visualiser.Visualiser.main(vizArgs);
    System.out.println();
  }



  /////////////////////////////////////////////////////////////////////////
  // Help message for the console operator
  /////////////////////////////////////////////////////////////////////////
  static protected void help()
  {
    System.out.println("USAGE:");
    System.out.println("======");
    System.out.println("  help");
    System.out.println("  yp");
    System.out.println("  viz");
    System.out.println("  agent  args");
    System.out.println("  script filename");
    System.out.println();
  } 



} //class: ZEUS shell



