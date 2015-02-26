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



package zeus.agentviewer.task;

import javax.swing.table.*;
import java.util.*;
import zeus.concepts.PrimitiveTask;
import zeus.concepts.Fact;
import zeus.actors.event.*;
import zeus.concepts.*;
import zeus.actors.*;



public class TaskTableModel  extends AbstractTableModel implements TaskMonitor{

      private String header = "Task";
      private Vector   data ;
      private TaskDb taskDb;
//------------------------------------------------------------------------------
      public TaskTableModel(AgentContext context){
          data = new Vector();
          taskDb = context.TaskDb();
          taskDb.addTaskMonitor(this, TaskEvent.ADD_MASK | TaskEvent.DELETE_MASK,true);
      }
//------------------------------------------------------------------------------
       public int getRowCount() {
             return data.size();
       }
//------------------------------------------------------------------------------
       public int getColumnCount(){
           return 1;
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
           PrimitiveTask task = (PrimitiveTask) data.elementAt(row);

           switch (col){
             case 0: return  task.getName();
             default: return new String("Error in TaskTableModel getValue");
           }
       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  header;
       }
//------------------------------------------------------------------------------
       public void addTask(PrimitiveTask task){
           if ( data.contains(task) )
             return;

           data.addElement(task);
           fireTableDataChanged();

       }
//------------------------------------------------------------------------------
       public void removeTask(PrimitiveTask task){
          if ( !data.contains(task) )
             return;

           data.removeElement(task);
           fireTableDataChanged();
        }

//------------------------------------------------------------------------------
       public Fact[] getEffects(int row){
          PrimitiveTask task = (PrimitiveTask) data.elementAt(row);
          //return task.getProducedFacts();
          return task.getPostconditions();
       }
//------------------------------------------------------------------------------
       public Fact[] getPreConditions(int row){
          PrimitiveTask task = (PrimitiveTask) data.elementAt(row);
          //return task.getConsumedFacts();
          return task.getPreconditions();
       }
//------------------------------------------------------------------------------
       public void taskAddedEvent(TaskEvent event) {
         if ( !((Task) event.getTask()).isPrimitive() )
           return;
          addTask((PrimitiveTask) event.getTask());
       }
//------------------------------------------------------------------------------
       public void taskModifiedEvent(TaskEvent event) {}
//------------------------------------------------------------------------------
       public void taskDeletedEvent(TaskEvent event) {
          removeTask((PrimitiveTask) event.getTask());
       }
//------------------------------------------------------------------------------
       public void taskAccessedEvent(TaskEvent event) {}

//------------------------------------------------------------------------------
    public void removeZeusEventMonitors(){
      taskDb.removeTaskMonitor(this, TaskEvent.ADD_MASK | TaskEvent.DELETE_MASK);

    }
//------------------------------------------------------------------------------
    public String getCost(int row) {
        PrimitiveTask task = (PrimitiveTask) data.elementAt(row);
        return task.getCostFn().toString();
    }
//------------------------------------------------------------------------------
    public String getTime(int row){
        PrimitiveTask task = (PrimitiveTask) data.elementAt(row);
        return task.getTimeFn().toString();
    }


}
