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
* GeneratorModel.java
*
* Underlying Model of the Zeus Agent Generator
*****************************************************************************/

package zeus.generator;

import java.util.*;
import java.io.File;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.event.*;


public class GeneratorModel implements ChangeListener
{
  private int agentCount = 0;
  private int agentIdCount = 0;
  private int taskCount = 0;
  private int taskIdCount = 0;

  protected EventListenerList eventListeners = new EventListenerList();

  protected Hashtable agentTable;
  protected Hashtable taskTable;
  protected Hashtable agentNames;
  protected Hashtable taskNames;
  protected Hashtable iconTable;

  public OntologyDb ontology;

  // JDBC connection parameters - added by Jaron
  public String dbName     = "";
  public String dbDriver   = "";
  public String dbUsername = "";
  public String dbPassword = "";


  public GeneratorModel(OntologyDb ontology)
  {
    this.ontology = ontology;
    ontology.addChangeListener(this);

    agentTable = new Hashtable();
    taskTable = new Hashtable();
    agentNames = new Hashtable();
    taskNames = new Hashtable();
    iconTable = new Hashtable();
  }

  public void clear() {
     Enumeration enum;
     AgentDescription agent;
     AbstractTask task;

     enum = agentTable.elements();
     while( enum.hasMoreElements() ) {
        agent = (AgentDescription)enum.nextElement();
        fireAgentChanged(agent,AgentChangeEvent.DELETE);
     }
     enum = taskTable.elements();
     while( enum.hasMoreElements() ) {
        task = (AbstractTask)enum.nextElement();
        fireTaskChanged(task,TaskChangeEvent.DELETE);
     }
     agentTable.clear();
     taskTable.clear();
     agentNames.clear();
     taskNames.clear();
     iconTable.clear();
  }


  //---------------------------------------------------------------------------
  // Agent Definition Storage Methods
  //---------------------------------------------------------------------------

  public String getAgentIcon(String agentId) {
     Core.ERROR(agentNames.containsKey(agentId),255,this);
     String icon = (String)iconTable.get(agentId);
     if ( icon == null ) {
        icon = SystemProps.getProperty("gif.dir") + File.separator + "agent.gif";
        iconTable.put(agentId,icon);
     }
     return icon;
  }

  public void setAgentIcon(String agentId, String icon) {
     Core.ERROR(agentNames.containsKey(agentId),255,this);
     String oldIcon = (String)iconTable.get(agentId);
     boolean changed = (oldIcon == null && icon != null) ||
                       (oldIcon != null && icon == null) ||
                       (oldIcon != null && icon != null && !icon.equals(oldIcon));
     if ( icon == null )
        icon = SystemProps.getProperty("gif.dir") + File.separator + "agent.gif";
     iconTable.put(agentId,icon);
     // Note guard: agentTable.containsKey() used to prevent exeception
     // when loading from file
     if ( changed && agentTable.containsKey(agentId) )
        fireAgentChanged((AgentDescription)agentTable.get(agentId), AgentChangeEvent.MODIFY);
  }

  public AgentDescription[] getAgents() {
    AgentDescription[] List = new AgentDescription[agentTable.size()];
    Enumeration elem = agentTable.elements();
    for(int i = 0; elem.hasMoreElements(); i++ )
      List[i] = (AgentDescription) elem.nextElement();
    return List;
  }

  public Object[][] getAgentData() {
    Object[][] data = new Object[agentTable.size()][3];
    Enumeration elem = agentTable.elements();
    for(int i = 0; elem.hasMoreElements(); i++ ) {
      AgentDescription a = (AgentDescription) elem.nextElement();
      data[i][0] = getAgentName(a.getName());
      String[] tasks = a.getTasks();
      for(int j = 0; j < tasks.length; j++ )
         tasks[j] = getTaskName(tasks[j]);
      data[i][1] = tasks;
      // this is not shown in the tables, but used to index agent entries
      data[i][2] = a.getName();
    }
    return data;
  }

  public void updateAgent(AgentDescription agent) {
    String id = agent.getName();
    Object ob = agentTable.put(id,agent);
    Core.ERROR(ob != null,7,this);
    fireAgentChanged(agent,AgentChangeEvent.MODIFY);
  }

  public void addAgent(AgentDescription agent) {
    String id = agent.getName();
    Core.ERROR(!agentTable.containsKey(id),1,this);
    agentTable.put(id, new AgentDescription(agent));
    if ( agentNames.get(id) == null ) {
       String name = "Agent" + (agentCount++);
       while( reverseAgentNameLookup(name) != null )
          name = "Agent" + (agentCount++);
       agentNames.put(id, name);
    }
    fireAgentChanged(agent,AgentChangeEvent.ADD);
  }

  public void modifyAgentTasks(String id, String[] tasks) {
     String taskId[] = new String[tasks.length];
     for(int i = 0; i < tasks.length; i++ )
        taskId[i] = reverseTaskNameLookup(tasks[i]);
     AgentDescription agent = (AgentDescription) agentTable.get(id);
     agent.setTasks(taskId);
     fireAgentChanged(agent,AgentChangeEvent.MODIFY);
  }


  public void addAgent(Vector v) {
    for(int i = 0; v != null && i < v.size(); i++ )
       addAgent((AgentDescription)v.elementAt(i));
  }


  public void removeAgent(String id) {
    AgentDescription agent = (AgentDescription)agentTable.get(id);
    agentTable.remove(id);
    agentNames.remove(id);
    iconTable.remove(id);
    fireAgentChanged(agent,AgentChangeEvent.DELETE);

  }


  public AgentDescription getAgent(String id) {
    AgentDescription ob = (AgentDescription)agentTable.get(id);
    Assert.notNull(ob);
    return new AgentDescription(ob);
  }


  //---------------------------------------------------------------------------
  // Agent Naming Methods
  //---------------------------------------------------------------------------

  public void addAgentName(String id, String name) {
    agentNames.put(id, name);
  }

  public String createNewAgentId() {
    String id = "AgentId" + (agentIdCount++);
    while( agentNames.containsKey(id) )
       id = "AgentId" + (agentIdCount++);
    return id;
  }

  public void createNewAgent() {
    createNewAgent(createNewAgentId());
  }

  public void createNewAgent(String id) {
    Core.ERROR(agentNames.get(id) == null, 6, this);
    String name = "Agent" + (agentCount++);
    while( reverseAgentNameLookup(name) != null )
       name = "Agent" + (agentCount++);

    agentNames.put(id, name);
    AgentDescription agent = new AgentDescription();
    agent.setName(id);
    addAgent(agent);
  }

  public void cloneAgent(String id) {
    String name = (String)agentNames.get(id);
    Core.ERROR(name,11,this);
    AgentDescription agent = (AgentDescription)agentTable.get(id);

    String newName = name + "$" + (agentCount++);
    while( reverseAgentNameLookup(newName) != null )
       newName = name + "$" + (agentCount++);

    String newId = createNewAgentId();

    agentNames.put(newId,newName);
    AgentDescription newAgent = new AgentDescription(agent);
    newAgent.setName(newId);
    agentTable.put(newId,newAgent);
    fireAgentChanged(newAgent,AgentChangeEvent.ADD);
  }


  public String getAgentName(String id) {
    return (String)agentNames.get(id);
  }

  public String[] getAgentIds() {
    String[] data = new String[agentNames.size()];
    Enumeration enum = agentNames.keys();
    for(int i = 0; i < data.length; i++ )
       data[i] = (String)enum.nextElement();
    return data;
  }

  public String[] getAgentNames() {
    String[] data = new String[agentNames.size()];
    Enumeration enum = agentNames.elements();
    for(int i = 0; i < data.length; i++ )
       data[i] = (String)enum.nextElement();
    return data;
  }

  public String reverseAgentNameLookup(String name) {
    Enumeration keys = agentNames.keys();
    while( keys.hasMoreElements() ) {
      String k = (String)keys.nextElement();
      String s = (String)agentNames.get(k);
      if (s.equals(name))
         return k;
    }
    return null;
  }

  public void renameAgent(String id, String newname) {
    String name = newname;
    while( reverseAgentNameLookup(newname) != null )
       name = newname + (agentIdCount++);
    agentNames.put(id,name);
    AgentDescription agent = (AgentDescription)agentTable.get(id);
    fireAgentChanged(agent,AgentChangeEvent.MODIFY);
  }

  public boolean containsAgent(String id) {
    Assert.notNull(id);
    return agentNames.containsKey(id);
  }

  //---------------------------------------------------------------------------
  // Task Definition Storage Methods
  //---------------------------------------------------------------------------
  public void addTaskName(String id, String name) {
    taskNames.put(id, name);
  }

  public void addTask(AbstractTask task) {
    // the try/catch block makes it more robust anyway....
  
    String id = task.getName();
    Core.ERROR(!taskTable.containsKey(id),2,this);
    String name = getTaskName(id);
    Core.ERROR(name,3,this);
    taskNames.put(id,name);
    switch( task.getType() ) {
       case AbstractTask.PRIMITIVE:
            taskTable.put(id,new PrimitiveTask((PrimitiveTask)task));
            break;
       case AbstractTask.SUMMARY:
            taskTable.put(id,new SummaryTask((SummaryTask)task));
            break;
       case AbstractTask.BEHAVIOUR:
            taskTable.put(id,new ReteKB((ReteKB)task));
            break;
       case AbstractTask.SCRIPT:
            taskTable.put(id,new PlanScript((PlanScript)task));
            break;
    }
    fireTaskChanged(task,TaskChangeEvent.ADD);
  }

  public void updateTask(AbstractTask task) {
    String id = task.getName();
    Object ob = taskTable.put(id, task);
    Core.ERROR(ob != null,4,this);
    fireTaskChanged(task,TaskChangeEvent.MODIFY);
  }

  public void addTask(Vector v) {
    for(int i = 0; v != null && i < v.size(); i++ )
       addTask((AbstractTask)v.elementAt(i));
  }

  public void removeTask(String id) {
    AbstractTask task = (AbstractTask)taskTable.get(id);
    taskTable.remove(id);
    taskNames.remove(id);
    Enumeration enum = agentTable.elements();
    AgentDescription a;
    while( enum.hasMoreElements() ) {
       a = (AgentDescription)enum.nextElement();
       if ( a.removeTask(id) )
          fireAgentChanged(a,AgentChangeEvent.MODIFY);
    }
    fireTaskChanged(task,TaskChangeEvent.DELETE);
  }

  public AbstractTask getTask(String id) {
    return (AbstractTask)taskTable.get(id);
  }

  public boolean containsTask(String id, int type) {
    AbstractTask t = (AbstractTask)taskTable.get(id);
    return (t != null && t.getType() == type);
  }

  public int getTaskCount(int type) {
    Enumeration elem = taskTable.elements();
    int count = 0;
    AbstractTask t;
    while( elem.hasMoreElements() ) {
      t = (AbstractTask) elem.nextElement();
      if ( t.getType() == type ) count++;
    }
    return count;
  }

  public AbstractTask[] getTasks() {
    AbstractTask[] List = new AbstractTask[taskTable.size()];
    Enumeration elem = taskTable.elements();
    for(int i = 0; elem.hasMoreElements(); i++ )
      List[i] = (AbstractTask) elem.nextElement();
    return List;
  }

  public AbstractTask[] getTasks(int type) {
    AbstractTask[] List = new AbstractTask[getTaskCount(type)];
    Enumeration elem = taskTable.elements();
    int i = 0;
    AbstractTask t;
    while( elem.hasMoreElements() ) {
      t = (AbstractTask) elem.nextElement();
      if ( t.getType() == type )
      List[i++] = t;
    }
    return List;
  }

  public String[] getTaskNames(int type) {
    String[] List = new String[getTaskCount(type)];
    Enumeration elem = taskTable.elements();
    int i = 0;
    AbstractTask t;
    while( elem.hasMoreElements() ) {
      t = (AbstractTask) elem.nextElement();
      if ( t.getType() == type )
      List[i++] = (String)taskNames.get(t.getName());
    }
    return List;
  }

  public String getTaskType(String id) {
     AbstractTask t = (AbstractTask)taskTable.get(id);
     return t.getTypeName();
  }

  public String createNewTaskId() {
    String id = "AbstractTaskId" + (taskIdCount++);
    while( taskNames.containsKey(id) )
       id = "AbstractTaskId" + (taskIdCount++);
    return id;
  }

  public void createNewTask(String type) {
    createNewTask(createNewTaskId(),type);
  }

  public void createNewTask(String id, String type) {
    Core.ERROR(taskNames.get(id) == null, 5, this);
    String name = "Task" + (taskCount++);
    while( reverseTaskNameLookup(name) != null )
       name = "Task" + (taskCount++);

    taskNames.put(id,name);
    AbstractTask task = null;
    switch( AbstractTask.getType(type) ) {
       case AbstractTask.PRIMITIVE:
            task = new PrimitiveTask();
            break;
       case AbstractTask.SUMMARY:
            task = new SummaryTask();
            break;
       case AbstractTask.BEHAVIOUR:
            task = new ReteKB();
            break;
       case AbstractTask.SCRIPT:
            task = new PlanScript();
            break;
    }
    task.setName(id);
    taskTable.put(id,task);
    fireTaskChanged(task,TaskChangeEvent.ADD);
  }

  public void cloneTask(String id) {
    String name = (String)taskNames.get(id);
    Core.ERROR(name,10,this);
    AbstractTask task = (AbstractTask)taskTable.get(id);

    String newName = name + "$" + (taskCount++);
    while( reverseTaskNameLookup(newName) != null )
       newName = name + "$" + (taskCount++);

    String newId = createNewTaskId();

    taskNames.put(newId,newName);
    AbstractTask newTask = null;

    switch( task.getType() ) {
       case AbstractTask.PRIMITIVE:
            newTask = new PrimitiveTask((PrimitiveTask)task);
            break;
       case AbstractTask.SUMMARY:
            newTask = new SummaryTask((SummaryTask)task);
            break;
       case AbstractTask.BEHAVIOUR:
            newTask = new ReteKB((ReteKB)task);
            break;
       case AbstractTask.SCRIPT:
            newTask = new PlanScript((PlanScript)task);
            break;
    }
    newTask.setName(newId);
    taskTable.put(newId,newTask);
    fireTaskChanged(newTask,TaskChangeEvent.ADD);
  }

  public Object[][] getTaskData() {
    Object[][] data = new Object[taskTable.size()][3];
    Enumeration elem = taskTable.elements();
    for(int i = 0; elem.hasMoreElements(); i++ ) {
      AbstractTask t = (AbstractTask) elem.nextElement();
      data[i][0] = getTaskName(t.getName());
      data[i][1] = t.getTypeName();
      // this is not shown in the tables, but used to index task entries
      data[i][2] = t.getName();
    }
    return data;
  }

  public String getTaskName(String id) {
    return (String)taskNames.get(id);
  }

  public String[] getTaskIds() {
    String[] data = new String[taskNames.size()];
    Enumeration enum = taskNames.keys();
    for(int i = 0; i < data.length; i++ )
       data[i] = (String)enum.nextElement();
    return data;

  }
  public String[] getTaskNames() {
    String[] data = new String[taskNames.size()];
    Enumeration enum = taskNames.elements();
    for(int i = 0; i < data.length; i++ )
       data[i] = (String)enum.nextElement();
    return data;
  }

  public String reverseTaskNameLookup(String name) {
    Enumeration keys = taskNames.keys();
    while( keys.hasMoreElements() ) {
      String k = (String)keys.nextElement();
      String s = (String)taskNames.get(k);
      if (s.equals(name))
         return k;
    }
    return null;
  }

  public void renameTask(String id, String newname) {
    String name = newname;
    while ( reverseTaskNameLookup(newname) != null )
       name = newname + (taskIdCount++);
    taskNames.put(id,name);
    AbstractTask task = (AbstractTask)taskTable.get(id);
    fireTaskChanged(task,TaskChangeEvent.MODIFY);
  }

  public boolean containsTask(String id) {
    return taskNames.containsKey(id);
  }

  //---------------------------------------------------------------------------
  // Event Methods
  //---------------------------------------------------------------------------

  public void stateChanged(ChangeEvent evt) {
     // ontologyDb has changed
     // validate agents and tasks
     fireChanged();
  }

  public void addChangeListener(ChangeListener x) {
    eventListeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
    eventListeners.remove(ChangeListener.class, x);
  }

  public void addAgentListener(AgentListener x) {
    eventListeners.add(AgentListener.class, x);
  }
  public void removeAgentListener(AgentListener x) {
    eventListeners.remove(AgentListener.class, x);
  }

  public void addTaskListener(TaskListener x) {
    eventListeners.add(TaskListener.class, x);
  }
  public void removeTaskListener(TaskListener x) {
    eventListeners.remove(TaskListener.class, x);
  }

  protected void fireChanged() {
    ChangeEvent c = new ChangeEvent(this);
    Object[] listeners = eventListeners.getListenerList();
    for(int i= listeners.length-2; i >= 0; i -=2) {
       if (listeners[i] == ChangeListener.class) {
          ChangeListener cl = (ChangeListener)listeners[i+1];
          cl.stateChanged(c);
       }
    }
  }
  protected void fireAgentChanged(AgentDescription agent, int mode) {
    AgentChangeEvent c = new AgentChangeEvent(this,agent,mode);
    Object[] listeners = eventListeners.getListenerList();
    for(int i= listeners.length-2; i >= 0; i -=2) {
       if (listeners[i] == AgentListener.class) {
          AgentListener cl = (AgentListener)listeners[i+1];
          cl.agentStateChanged(c);
       }
    }
    fireChanged();
  }
  protected void fireTaskChanged(AbstractTask task, int mode) {
    TaskChangeEvent c = new TaskChangeEvent(this,task,mode);
    Object[] listeners = eventListeners.getListenerList();
    for(int i= listeners.length-2; i >= 0; i -=2) {
       if (listeners[i] == TaskListener.class) {
          TaskListener cl = (TaskListener)listeners[i+1];
          cl.taskStateChanged(c);
       }
    }
    fireChanged();
  }
}
