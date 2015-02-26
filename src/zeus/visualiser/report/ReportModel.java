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
* ReportModel.java
*
*****************************************************************************/

package zeus.visualiser.report;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.PlanRecord;
import zeus.gui.*;
import zeus.gui.graph.*;

public class ReportModel extends AbstractGraphModel {
  protected boolean           isNodeEditable    = true;
  protected boolean           auto_delete       = false;
  protected boolean           show_joint_graphs = false;
  protected Hashtable         nodeTable         = new Hashtable();
  protected Hashtable         agentTable        = new Hashtable();
  protected Hashtable         taskTable         = new Hashtable();
  protected DefaultListModel  taskListModel     = new DefaultListModel();
  protected DefaultListModel  agentListModel    = new DefaultListModel();
  protected EventListenerList listeners         = new EventListenerList();
  protected String            isShowingAgent    = null;
  protected String            isShowingTask     = null;

  public ReportModel() {
     reset();
  }

  public void reset() {
     agentTable.clear();
     taskTable.clear();
     nodeTable.clear();
     isShowingAgent = null;
     isShowingTask = null;
     fireChanged();
     fireGraphStructureChanged();
  }

  DefaultListModel getAgentListModel() { return agentListModel; }
  DefaultListModel getTaskListModel()  { return taskListModel; }

  String getCurrentAgent() { return isShowingAgent; }
  String getCurrentTask()  { return isShowingTask; }

  public Enumeration nodes() { return nodeTable.elements(); }

  public void setValue(GraphNode node, Object user_object) {
     Core.ERROR(null,1,this);
  }

  public boolean isNodeEditable(GraphNode node) {
     return isNodeEditable;
  }

  public boolean isLinkVisible(GraphNode from, GraphNode to) {
     ReportRec child = (ReportRec)from.getUserObject();
     ReportRec parent = (ReportRec)to.getUserObject();
     return child.hasParent(parent.getName());
  }

  public void setShowJointGraphs(boolean state) {
     show_joint_graphs = state;
     // resetGraphs();
  }
  public void    setAutoDelete(boolean state) { auto_delete = state; }
  public boolean getAutoDelete()              { return auto_delete; }
  public boolean getShowJointGraphs()         { return show_joint_graphs; }

  public synchronized GraphNode getNode(String name)  {
     return (GraphNode)nodeTable.get(name);
  }

  public synchronized void addAgent(String agent) {
     if ( agentTable.containsKey(agent) ) return;
     agentTable.put(agent,new HSet());
     agentListModel.addElement(agent);
     fireChanged();
  }

  public synchronized void removeAgent(String agent) {
     HSet taskList = (HSet)agentTable.remove(agent);
     agentListModel.removeElement(agent);
     removeTasks(taskList);
     fireChanged();
  }

  public void addAgents(Vector input) {
     for(int i = 0; i < input.size(); i++ )
        addAgent((String)input.elementAt(i));
  }

  public synchronized void addAgents(String[] input) {
     for(int i = 0; i < input.length; i++ )
        addAgent(input[i]);
  }

  public synchronized void removeAgents(Vector input) {
     for(int i = 0; i < input.size(); i++ )
        removeAgent((String)input.elementAt(i));
  }

  public synchronized void removeAgents(String[] input) {
     for(int i = 0; i < input.length; i++ )
        removeAgent(input[i]);
  }

  public synchronized String[] getAgents() {
     String[] output = new String[agentTable.size()];
     Enumeration enum = agentTable.keys();
     for(int i = 0; enum.hasMoreElements(); i++ )
        output[i] = (String)enum.nextElement();
     return output;
  }

  public synchronized String[] getTasks(String agent) {
     HSet db = (HSet)agentTable.get(agent);
     String[] output = new String[db.size()];
     Enumeration enum = db.elements();
     for(int i = 0; enum.hasMoreElements(); i++ )
        output[i] = (String)enum.nextElement();
     return output;
  }

  public synchronized void removeTasks(String agent, String[] tasks) {
     HSet taskList = (HSet)agentTable.get(agent);
     for(int i = 0; i < tasks.length; i++ )
        taskList.remove(tasks[i]);
     removeTasks(tasks);
     fireChanged();
  }

  public synchronized void removeTask(String agent, String task) {
     if ( agent == null ) agent = isShowingAgent;
     HSet taskList = (HSet)agentTable.get(agent);
     taskList.remove(task);
     removeTask(task);
     fireChanged();
  }

  public synchronized void removeTasks(String[] tasks) {
     for(int i = 0; i < tasks.length; i++ ) {
        taskTable.remove(tasks[i]);
        taskListModel.removeElement(tasks[i]);
        if ( nodeTable.containsKey(tasks[i]) ) {
           nodeTable.clear();
           fireGraphStructureChanged();
        }
     }
  }

  public synchronized void removeTasks(HSet tasks) {
     Enumeration enum = tasks.elements();
     String task;
     while( enum.hasMoreElements() ) {
        task = (String)enum.nextElement();
        taskTable.remove(task);
        taskListModel.removeElement(task);
        if ( nodeTable.containsKey(task) ) {
           nodeTable.clear();
           fireGraphStructureChanged();
        }
     }
  }

  public synchronized void removeTask(String task) {
     taskTable.remove(task);
     taskListModel.removeElement(task);
     if ( nodeTable.containsKey(task) ) {
        nodeTable.clear();
        fireGraphStructureChanged();
     }
  }

  public synchronized void addReport(ReportRec rec) {
     Core.DEBUG(3,"ReportModel adding report " + rec);

     String agent  = rec.getAgent();
     String rootId = rec.getRootId();

     if ( rec.isRoot() ) {
        HSet taskList = (HSet)agentTable.get(agent);
        if ( taskList == null ) {
           taskList = new HSet();
           agentTable.put(agent,taskList);
        }
        taskList.add(rec.getName());
     }

     Hashtable db = (Hashtable)taskTable.get(rootId);
     if ( db == null ) {
        db = new Hashtable();
        taskTable.put(rootId,db);
     }
     db.put(rec.getName(),rec);

     // check if graph containing rec is visible
     // if so - update graph node
     if ( nodeTable.containsKey(rootId) ) {
        GraphNode node = (GraphNode)nodeTable.get(rec.getName());
        if ( node == null ) {
           node = new GraphNode(rec);
           nodeTable.put(rec.getName(),node);
           doReportAdded(db,node,rec,true);
           fireGraphNodeAdded(node);
           fireGraphStructureChanged();
        }
        else {
           node.setUserObject(rec);
           doReportAdded(db,node,rec,true);
           fireGraphNodeStateChanged(node);
        }
     }

     if ( rec.isRoot() ) {
        if ( isShowingAgent == null )
	   showAgent(agent);

	if ( agent.equals(isShowingAgent) ) {
           if ( !taskListModel.contains(rec.getName()) )
              taskListModel.addElement(rec.getName());
           if ( isShowingTask == null )
              showTask(rec.getName());
           else if ( auto_delete && !isShowingTask.equals(rec.getName()) ) {
              GraphNode prev_root = (GraphNode)nodeTable.get(isShowingTask);
              ReportRec prev_rec = (ReportRec)prev_root.getUserObject();
              switch( prev_rec.getState() ) {
                 case PlanRecord.COMPLETED:
                 case PlanRecord.FAILED:
                 case PlanRecord.AGREEMENT:
                      removeTask(agent,isShowingTask);
                      showTask(rec.getName());
                      break;
                 default:
                      break;
              }
           }
        }
        fireChanged();
     }
  }

  public synchronized void showAgent(String agent) {
     Core.DEBUG(3,"Show agent: " + agent);
     String[] tasks =  getTasks(agent);
     taskListModel.removeAllElements();
     nodeTable.clear();
     fireGraphStructureChanged();
     for(int i = 0; i < tasks.length; i++ )
        taskListModel.addElement(tasks[i]);
     isShowingAgent = agent;
  }

  public synchronized void showTask(String task) {
     nodeTable.clear();
     Hashtable db = (Hashtable)taskTable.get(task);
     Enumeration enum = db.elements();
     GraphNode node;
     ReportRec rec;
     while( enum.hasMoreElements() ) {
        rec = (ReportRec)enum.nextElement();
        node = (GraphNode)nodeTable.get(rec.getName());
        if ( node == null ) {
           node = new GraphNode(rec);
           nodeTable.put(rec.getName(),node);
        }
        doReportAdded(db,node,rec,false);
     }
     isShowingTask = task;
     fireGraphStructureChanged();
  }

  protected void doReportAdded(Hashtable db, GraphNode node1,
                               ReportRec rec1, boolean notify) {
     ReportRec rec2;
     GraphNode node2;
     String[] parents = rec1.getParents();
     for(int i = 0; i < parents.length; i++ ) {
        rec2 = (ReportRec)db.get(parents[i]);
        if ( rec2 != null ) {
           node2 = (GraphNode)nodeTable.get(rec2.getName());
           if ( node2 == null ) {
              node2 = new GraphNode(rec2);
              nodeTable.put(rec2.getName(),node2);
           }
           node1.addParent(node2);
           node2.addChild(node1);
           if ( notify ) fireGraphNodeStateChanged(node2);
        }
        else
           ; // Core.ERROR(rec2,2,this);
     }

     String[] children = rec1.getChildren();
     for(int i = 0; i < children.length; i++ ) {
        rec2 = (ReportRec)db.get(children[i]);
        if ( rec2 != null ) {
           node2 = (GraphNode)nodeTable.get(rec2.getName());
           if ( node2 == null ) {
              node2 = new GraphNode(rec2);
              nodeTable.put(rec2.getName(),node2);
           }
           node1.addChild(node2);
           node2.addParent(node1);
           if ( notify ) fireGraphNodeStateChanged(node2);
        }
        else
           ; // Core.ERROR(rec2,3,this);
     }

     String[] siblings = rec1.getSiblings();
     for(int i = 0; i < siblings.length; i++ ) {
        rec2 = (ReportRec)db.get(siblings[i]);
        if ( rec2 != null ) {
           node2 = (GraphNode)nodeTable.get(rec2.getName());
           if ( node2 == null ) {
              node2 = new GraphNode(rec2);
              nodeTable.put(rec2.getName(),node2);
           }
           node1.addSibling(node2);
           node2.addSibling(node1);
           if ( notify ) fireGraphNodeStateChanged(node2);
        }
        else
           ; // Core.ERROR(rec2,4,this);
     }
  }

  public void addChangeListener(ChangeListener x) {
     listeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     listeners.remove(ChangeListener.class, x);
  }

  protected void fireChanged() {
     ChangeEvent c = new ChangeEvent(this);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == ChangeListener.class) {
           ChangeListener cl = (ChangeListener)list[i+1];
           cl.stateChanged(c);
        }
     }
  }
}
