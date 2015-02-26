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

import java.util.*;
import javax.swing.event.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.event.*;
import zeus.generator.*;

/*****************************************************************************
* GenerationPlan.java
*
* Underlying Model of the Zeus Agent Generator
* Change Log
*-----------
* Simon made some variables public 
*****************************************************************************/
public class GenerationPlan
             implements AgentListener, TaskListener, ChangeListener {

  public static final String WINDOWS = "Windows";
  public static final String UNIX    = "Unix";
  public static final String ZSH = "zsh";
  public static final String NONE = "no shell"; 

  public static final String SAVE_NEEDED    = "Modified";
  public static final String NO_SAVE_NEEDED = "Saved";

  protected EventListenerList changeListeners = new EventListenerList();

  protected Hashtable agentTable = new Hashtable();
  protected Hashtable taskTable = new Hashtable();
  protected Hashtable nameserverTable = new Hashtable();
  protected Hashtable visualiserTable = new Hashtable();
  protected Hashtable facilitatorTable = new Hashtable();
  protected Hashtable dbProxyTable = new Hashtable();

  protected String directory = System.getProperty("user.dir");
  protected String platform = UNIX;
  protected String shell = NONE; 

  protected GeneratorModel genmodel;
  protected OntologyDb     ontology;

  public GenerationPlan(GeneratorModel genmodel, OntologyDb ontology) {
     this.genmodel = genmodel;
     this.ontology = ontology;
     genmodel.addAgentListener(this);
     genmodel.addTaskListener(this);
     ontology.addChangeListener(this);
     reset();
  }

// why are these synchronized? 
  public synchronized void setPlatform(String input) {
     if ( !input.equals(UNIX) && !input.equals(WINDOWS) ) {
        System.err.println("Unknown platform");
        return;
     }
     if ( platform != null && platform.equals(input) ) return;
     platform = input;
     recomputeOntologyFilePath();
     recomputeSavedState();
     fireChanged();
  }

   /** 
    set the shell selected by the user
    */
  public void setShell (String input) { 
    if (input.equals (ZSH))
        shell = input; 
        else 
        shell = NONE; 
  }
  
  
  /** 
    should we generate scripts for a shell, or not? Which one? 
    */
  public String getShell() { 
    return shell;
  }


  public synchronized String getPlatform() {
     return platform;
  }


  public synchronized void setDirectory(String input) {
     if ( directory != null && directory.equals(input) ) return;
     directory = input;
     recomputeOntologyFilePath();
     recomputeSavedState();
     fireChanged();
  }


  public synchronized String getDirectory() {
     return directory;
  }
  

  protected void recomputeSavedState() {
     Enumeration enum;
     AgentInfo agentInfo;
     TaskInfo taskInfo;
     enum = agentTable.elements();
     while( enum.hasMoreElements() ) {
        agentInfo = (AgentInfo)enum.nextElement();
        agentInfo.generate = true;
        agentInfo.status = SAVE_NEEDED;
     }
     enum = taskTable.elements();
     while( enum.hasMoreElements() ) {
        taskInfo = (TaskInfo)enum.nextElement();
        taskInfo.generate = true;
        taskInfo.status = SAVE_NEEDED;
     }
  }


  protected void recomputeOntologyFilePath() {
     Enumeration enum;
     AgentInfo agentInfo;
     FacilitatorInfo facilitatorInfo;
     VisualiserInfo visualiserInfo;
     String ontology_file = getOntologyFilename();
     enum = agentTable.elements();
     while( enum.hasMoreElements() ) {
        agentInfo = (AgentInfo)enum.nextElement();
        agentInfo.ontology_file = ontology_file;
     }
     enum = facilitatorTable.elements();
     while( enum.hasMoreElements() ) {
        facilitatorInfo = (FacilitatorInfo)enum.nextElement();
        facilitatorInfo.ontology_file = ontology_file;
     }
     enum = visualiserTable.elements();
     while( enum.hasMoreElements() ) {
        visualiserInfo = (VisualiserInfo)enum.nextElement();
        visualiserInfo.ontology_file = ontology_file;
     }
  }


  public synchronized AgentInfo[] getAgents() {
     AgentInfo[] out = new AgentInfo[agentTable.size()];
     Enumeration enum = agentTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = (AgentInfo)enum.nextElement();
     return out;
  }


  public synchronized void setAgent(AgentInfo info) {
     info.icon_file = genmodel.getAgentIcon(info.id);
     agentTable.put(info.id,info);
     fireChanged();
  }


  public synchronized void setAgentIcon(AgentInfo info) {
     genmodel.setAgentIcon(info.id,info.icon_file);
  }


  public synchronized TaskInfo[] getTasks() {
     TaskInfo[] out = new TaskInfo[taskTable.size()];
     Enumeration enum = taskTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = (TaskInfo)enum.nextElement();
     return out;
  }


  public synchronized void setTask(TaskInfo info) {
     taskTable.put(info.id,info);
     fireChanged();
  }


  public synchronized TaskInfo[] getSelectedTasks() {
     // return only tasks the are marked for generation;
     Enumeration enum = taskTable.elements();
     Vector results = new Vector();
     TaskInfo info;
     while( enum.hasMoreElements() ) {
        info = (TaskInfo)enum.nextElement();
	if ( info.generate ) results.addElement(info);
     }
     TaskInfo[] out = new TaskInfo[results.size()];
     for(int i = 0; i < out.length; i++ )
        out[i] = (TaskInfo)results.elementAt(i);
     return out;
  }
  
  
  public synchronized AgentInfo[] getSelectedAgents() {
     // return only tasks the are marked for generation;
     Enumeration enum = agentTable.elements();
     Vector results = new Vector();
     AgentInfo info;
     while( enum.hasMoreElements() ) {
        info = (AgentInfo)enum.nextElement();
	if ( info.generate ) results.addElement(info);
     }
     AgentInfo[] out = new AgentInfo[results.size()];
     for(int i = 0; i < out.length; i++ )
        out[i] = (AgentInfo)results.elementAt(i);
     return out;
  }


  public synchronized NameserverInfo[] getNameservers() {
     NameserverInfo[] out = new NameserverInfo[nameserverTable.size()];
     Enumeration enum = nameserverTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = (NameserverInfo)enum.nextElement();
     return out;
  }


  public synchronized void setNameserver(NameserverInfo info) {
     nameserverTable.put(info.id,info);
     fireChanged();
  }


  public synchronized void removeNameserver(String id) {
     nameserverTable.remove(id);
     fireChanged();
  }
  

  public synchronized void createNameserver() {
     NameserverInfo info = new NameserverInfo();
     nameserverTable.put(info.id,info);
     fireChanged();
  }
  

  public synchronized FacilitatorInfo[] getFacilitators() {
     FacilitatorInfo[] out = new FacilitatorInfo[facilitatorTable.size()];
     Enumeration enum = facilitatorTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = (FacilitatorInfo)enum.nextElement();
     return out;
  }
  

  public synchronized void setFacilitator(FacilitatorInfo info) {
     facilitatorTable.put(info.id,info);
     fireChanged();
  }


  public synchronized void removeFacilitator(String id) {
     facilitatorTable.remove(id);
     fireChanged();
  }


  public synchronized void createFacilitator() {
     FacilitatorInfo info = new FacilitatorInfo(getOntologyFilename());
     facilitatorTable.put(info.id,info);
     fireChanged();
  }


  public synchronized VisualiserInfo[] getVisualisers() {
     VisualiserInfo[] out = new VisualiserInfo[visualiserTable.size()];
     Enumeration enum = visualiserTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = (VisualiserInfo)enum.nextElement();
     return out;
  }


  public synchronized void setVisualiser(VisualiserInfo info) {
     visualiserTable.put(info.id,info);
     fireChanged();
  }


  public synchronized void removeVisualiser(String id) {
     visualiserTable.remove(id);
     fireChanged();
  }


  public synchronized void createVisualiser() {
     VisualiserInfo info = new VisualiserInfo(getOntologyFilename());
     visualiserTable.put(info.id,info);
     fireChanged();
  }


  public synchronized DbProxyInfo[] getDbProxys() {
     DbProxyInfo[] out = new DbProxyInfo[dbProxyTable.size()];
     Enumeration enum = dbProxyTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = (DbProxyInfo)enum.nextElement();
     return out;
  }


  public synchronized void setDbProxy(DbProxyInfo info) {
     dbProxyTable.put(info.id,info);
     fireChanged();
  }


  public synchronized void removeDbProxy(String id) {
     dbProxyTable.remove(id);
     fireChanged();
  }


  public synchronized void createDbProxy() {
     DbProxyInfo info = new DbProxyInfo();
     dbProxyTable.put(info.id,info);
     fireChanged();
  }


  public synchronized String[][] summarizeNameservers() {
     String[][] out = new String[nameserverTable.size()][4];
     Enumeration enum = nameserverTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = ((GenerationInfo)enum.nextElement()).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeVisualisers() {
     String[][] out = new String[visualiserTable.size()][4];
     Enumeration enum = visualiserTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = ((GenerationInfo)enum.nextElement()).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeFacilitators() {
     String[][] out = new String[facilitatorTable.size()][4];
     Enumeration enum = facilitatorTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = ((GenerationInfo)enum.nextElement()).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeDbProxys() {
     String[][] out = new String[dbProxyTable.size()][4];
     Enumeration enum = dbProxyTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = ((GenerationInfo)enum.nextElement()).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeSelectedTasks() {
     // show only tasks the are marked for generation;
     Enumeration enum = taskTable.elements();
     Vector results = new Vector();
     TaskInfo info;
     while( enum.hasMoreElements() ) {
        info = (TaskInfo)enum.nextElement();
	if ( info.generate ) results.addElement(info);
     }
     String[][] out = new String[results.size()][4];
     for(int i = 0; i < out.length; i++ )
        out[i] = ((TaskInfo)results.elementAt(i)).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeSelectedAgents() {
     // show only tasks the are marked for generation;
     Enumeration enum = agentTable.elements();
     Vector results = new Vector();
     AgentInfo info;
     while( enum.hasMoreElements() ) {
        info = (AgentInfo)enum.nextElement();
	if ( info.generate ) results.addElement(info);
     }
     String[][] out = new String[results.size()][4];
     for(int i = 0; i < out.length; i++ )
        out[i] = ((AgentInfo)results.elementAt(i)).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeTasks() {
     String[][] out = new String[taskTable.size()][4];
     Enumeration enum = taskTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = ((GenerationInfo)enum.nextElement()).summarize();
     return out;
  }
  
  
  public synchronized String[][] summarizeAgents() {
     String[][] out = new String[agentTable.size()][4];
     Enumeration enum = agentTable.elements();
     for(int i = 0; i < out.length; i++ )
        out[i] = ((GenerationInfo)enum.nextElement()).summarize();
     return out;
  }
  

  public synchronized void removeEntry(String type, String id) {
     if ( type.equals(GenerationInfo.NAMESERVER) ) {
        removeNameserver(id);
        return;
     }
     else if ( type.equals(GenerationInfo.VISUALISER) ) {
        removeVisualiser(id);
        return;
     }
     else if ( type.equals(GenerationInfo.FACILITATOR) ) {
        removeFacilitator(id);
        return;
     }
     else if ( type.equals(GenerationInfo.DBPROXY) ) {
        removeDbProxy(id);
        return;
     }
     else if ( type.equals(GenerationInfo.AGENT) ) {
        AgentInfo agentInfo = (AgentInfo)agentTable.get(id);
        agentInfo.generate = false;
        fireChanged();
     }
     else if ( type.equals(GenerationInfo.TASK) ) {
        TaskInfo taskInfo = (TaskInfo)taskTable.get(id);
        taskInfo.generate = false;
        fireChanged();
     }
  }


  public synchronized String getOntologyFilename() {
     String file = ontology.getFilename();
     file = Misc.relativePath(directory,file);
     if ( platform.equals(WINDOWS) )
        file = Writer.updateFilename(file,Writer.WINDOWS);
     else if ( platform.equals(UNIX) )
        file = Writer.updateFilename(file,Writer.UNIX);
     return file;
  }
  
  
  public synchronized void purge() {
     agentTable.clear();
     taskTable.clear();
     nameserverTable.clear();
     visualiserTable.clear();
     facilitatorTable.clear();
     dbProxyTable.clear();
     fireChanged();
  }


  public synchronized void reset() {
     purge();
     // create defaults: 1-nameserver, 1-facilitator & 1-visualiser
     createNameserver();
     createFacilitator();
     createVisualiser();

     String file = getOntologyFilename();
     AgentInfo agentInfo;
     AgentDescription[] agents = genmodel.getAgents();
     for(int i = 0; i < agents.length; i++ ) {
        agentInfo = new AgentInfo(agents[i].getName(),
           genmodel.getAgentName(agents[i].getName()),file);
        agentInfo.icon_file = genmodel.getAgentIcon(agents[i].getName());
        agentTable.put(agentInfo.id,agentInfo);
     }

     TaskInfo taskInfo;
     AbstractTask[] tasks = genmodel.getTasks();
     for(int i = 0; i < tasks.length; i++ ) {
        switch( tasks[i].getType() ) {
           case AbstractTask.PRIMITIVE:
           case AbstractTask.BEHAVIOUR:
                taskInfo = new TaskInfo(tasks[i].getName(),
                genmodel.getTaskName(tasks[i].getName()));
                taskTable.put(taskInfo.id,taskInfo);
                break;
        }
     }

     fireChanged();
  }
  

  public void stateChanged(ChangeEvent e) {
     if ( e.getSource() == ontology ) {
        recomputeOntologyFilePath();
        fireChanged();
     }
  }

// mabey there needs to be something about agent_external here?
  public void agentStateChanged(AgentChangeEvent e) {
     AgentDescription agent = e.getAgent();
     int mode = e.getEventType();
     AgentInfo agentInfo;
     switch(mode) {
        case AgentChangeEvent.ADD:
             agentInfo = new AgentInfo(agent.getName(),
                genmodel.getAgentName(agent.getName()),
		getOntologyFilename());
             agentInfo.icon_file = genmodel.getAgentIcon(agent.getName());
             agentTable.put(agentInfo.id,agentInfo);
             break;

        case AgentChangeEvent.MODIFY:
             agentInfo = (AgentInfo)agentTable.get(agent.getName());
             agentInfo.name = genmodel.getAgentName(agent.getName());
             agentInfo.icon_file = genmodel.getAgentIcon(agent.getName());
             agentInfo.status = SAVE_NEEDED;
             agentInfo.generate = true;
             break;

        case AgentChangeEvent.DELETE:
             agentTable.remove(agent.getName());
             break;
     }
     fireChanged();
  }


  public void taskStateChanged(TaskChangeEvent e) {
     AbstractTask task = e.getTask();
     if ( !task.isPrimitive() && !task.isBehaviour() ) return;

     int mode = e.getEventType();
     TaskInfo taskInfo;
     switch(mode) {
        case TaskChangeEvent.ADD:
             taskInfo = new TaskInfo(task.getName(),
                genmodel.getTaskName(task.getName()));
             taskTable.put(taskInfo.id,taskInfo);
             break;

        case TaskChangeEvent.MODIFY:
             taskInfo = (TaskInfo)taskTable.get(task.getName());
             taskInfo.name = genmodel.getTaskName(task.getName());
             taskInfo.status = SAVE_NEEDED;
             taskInfo.generate = true;
         
             AgentDescription[] agent = genmodel.getAgents();
             AgentInfo agentInfo;
             for(int i = 0; i < agent.length; i++ ) {
                if ( agent[i].containsTask(task.getName()) ) {
                   agentInfo = (AgentInfo)agentTable.get(agent[i].getName());
                   agentInfo.generate = true;
                   agentInfo.status = SAVE_NEEDED;
                }
             }
             break;

        case TaskChangeEvent.DELETE:
             taskTable.remove(task.getName());
             break;
     }
     fireChanged();
  }


  public void addChangeListener(ChangeListener x) {
    changeListeners.add(ChangeListener.class, x);
  }
  
  
  public void removeChangeListener(ChangeListener x) {
    changeListeners.remove(ChangeListener.class, x);
  }
  
  
  protected void fireChanged() {
    ChangeEvent c = new ChangeEvent(this);
    Object[] listeners = changeListeners.getListenerList();
    for(int i= listeners.length-2; i >= 0; i -=2) {
       if (listeners[i] == ChangeListener.class) {
          ChangeListener cl = (ChangeListener)listeners[i+1];
          cl.stateChanged(c);
       }
    }
  }
  
  
}
