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
 * @(#)ZeusTask.java 1.03b
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

/**
 * Tasks are generated in the form of 'stub' files, skeleton implementations
 * that need to be realised by application specific program code - this code
 * is linked to the agents through the methods of this interface. This approach
 * enables new domain-specific functionality to be integrated with the
 * automated created agent-specific code, without needing to modify the latter. < p>
 *
 * The class variables include several arrays of {@link Fact}
 * objects, one of which passes information from agent to task, and another which
 * stores the result of performing the task (which can then be read back by the
 * agent when the task has completed running. <p>
 *
 * This is a crucially important class for developers who want to add their own
 * application-specific functionality, and so should be studied carefully.
 * Instructions and an example of how to write a task body are provided in
 * Section 6 of Zeus Application Realisation Guide.
 * TaskExternals have been available since Zeus 1.1 and allow customisation without
 * editing the stubs - which is useful in terms of preserving your edited code 
 * in a separate file.
 */


public abstract class ZeusTask extends Thread
{
  /** The information passed from the agent to the task, (a 2D array is used
      to enable multiple fact instances) */
  protected Fact[][]  inputArgs  = null;

  /** The information returned from the task to the agent */
  protected Fact[][]  outputArgs = null;

  /** The information the task expects to receive, (as specified when the task
      was defined) this can be used to validate the inputArgs */
  protected Fact[]    expInputArgs = null;

  /** The information the task expects to return, (as specified when the task
      was defined) this can be used to validate the outputArgs */
  protected Fact[]    expOutputArgs = null;


  protected String[]  media = null;
  private   boolean   isFinished = false;
  private   boolean   isRunning = false;
  private   boolean   isAborted = false;

  protected String   desired_by; // LL 030500 1.03b

  protected AgentContext context = null;


  protected ZeusTask() {
    this.setPriority(Thread.NORM_PRIORITY);
    this.setName("ZeusTask"); 
  }

  public void setMedia(String[] media) {
    this.media = media;
  }

  /** Used internally by the {@link ExecutionMonitor} to start the task,
      users should not call this themselves */
  public void run() {
    isRunning = true;
    exec();
    isRunning = false;
    isFinished = true;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public boolean isFinished() {
    return isFinished;
  }

  public boolean isAborted() {
    return isAborted;
  }

  /** Call this method in cases where the task terminates abnormally */
  public void abort() {
    isAborted = true;
   // this.stop(); - causes deadlock? 
  }

  public void setContext(AgentContext context) {
     this.context = context;
  }

  public void setInputArgs(Fact[][] t) {
    Assert.notNull(t);
    inputArgs = t;
  }

  public void setExpectedOutputArgs(Fact[] f) {
    Assert.notNull(f);
    expOutputArgs = f;
  }

  public void setExpectedInputArgs(Fact[] f) {
    expInputArgs = f;
  }
  
  
  public void setOutputArgs (Fact[] f) { 
 /*   System.out.println("outputArgs =") ;
    for (int count = 0; count< f.length; count++) { 
        System.out.println(": " + f[count].toString()); 
    }*/


    Assert.notNull(f); 
    if (outputArgs == null) 
    outputArgs = new Fact[f.length][0]; 
    outputArgs[0] = f; 
    }
    
    
  public void setOutputArgs (Fact[][] f) { 
 /*   System.out.println("outputArgs =") ;
    for (int count = 0; count< f.length; count++) { 
        System.out.println(": " + f[count].toString()); 
    }*/


    Assert.notNull(f); 
    outputArgs = f; 
    }  

  public Fact[][] getInputArgs()          { return inputArgs; }

  public Fact[]   getExpectedOutputArgs() { return expOutputArgs; }

  public Fact[]   getExpectedInputArgs()  { return expInputArgs; }

  public Fact[][] getOutputArgs() {
    if ( !isFinished ) return null;
    return outputArgs;
  }

   // LL 030500 1.03bB
   public void setDesiredBy(String desired_by) {
      Assert.notNull(desired_by);
      this.desired_by = desired_by;
   }
   public String  getDesiredBy()    { return desired_by; }
   // LL 030500 1.03bE

  /** This is the interface between the agent and the task; hence the body of
      the task should implement this method */
  protected abstract void exec();
  
  /** 
   *This is the method called to get the string that describes this task in the service 
   *registration 
   *The concrete implementation is included to promote the smooth running of legacy 
   *agents
   *@since 1.3
   *@author Simon Thompson
   */
  public  String getDescription () {
      return ("no description"); 
  }

  /**
   * Get the instance details from the task. Returns <code>null</code>
   * if not implemented by the sub class.
   */
  public String getInstanceDetails() {
    return null;
  }

}
