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



package zeus.agentviewer.plansch;

import javax.swing.JOptionPane;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import zeus.actors.*;
import zeus.actors.event.*;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class PlanSchModel extends DefaultTableModel implements PlanStepMonitor,
                                                                 ClockMonitor{
      static final int DEFAULT_SIZE = 10;
      private PlanRecord[][] data;
      private PlanRecord [][] newData = null;
      private int length;
      private int width;
      private int from;
      private int now;
      private ExecutionMonitor em;
      private Planner planner;
      protected EventListenerList changeListeners = new EventListenerList();
      private JTable table;


      public PlanSchModel(AgentContext context){
          em = context.ExecutionMonitor();
          planner = context.Planner();

          length = planner.getPlannerLength();
          width = planner.getPlannerWidth();

          now = (int)context.now();
          data = new PlanRecord[width][length];
          for(int i = 0; i < width; i++ )
             for(int j = 0; j < length; j++ )
                data[i][j] = null;

          planner.addPlanStepMonitor(this,PlanStepEvent.CREATE_MASK
                                          |PlanStepEvent.DISPOSE_MASK
                                          | PlanStepEvent.STATE_CHANGE_MASK);

          em.addClockMonitor(this, ClockEvent.TICK_MASK);

          from = now;
	  fireTableStructureChanged();
      }


       public int getRowCount() { return width; }


       public int getColumnCount() { return  DEFAULT_SIZE;} //DEFAULT SIZE


       public Object getValueAt(int row, int col) {
          if (row > width) return null; 
          if (col + from - now > length) return null; 
            return data[row][col+from-now];
       }


      /**
        this is altered to return column names that make sense
        when the table structure is not changing
        */
       public String getColumnName(int col) {
            int aCol = col + from;
            return ("+" + Integer.toString(col));
       }


       public synchronized void createPlanRecord(PlanRecord pr){
           int st = pr.getStartTime() - now;
           int et = pr.getEndTime() - now;
           int proc = pr.getProc();
           if ( st < 0 || et < 0 )        // length passed item to be removed
              return;
           for(int i = st; i < et; i++)
              data[proc][i] = pr;
           fireTableDataChanged();
        }


       /**
        */
       public synchronized void stateChangedPlanRecord(PlanRecord rec){
       
           int st = rec.getStartTime() - now;
           int et = rec.getEndTime() - now;
           int proc = rec.getProc();
           if ( st < 0 )        // length passed item to be removed
            return;
           // first clear previous
           for(int i = 0; i < width; i++ )
              for(int j = 0; j < length; j++ )
                 if ( data[i][j] == rec ) data[i][j] = null;
           // now reassert
           for(int i = st; i < et; i++)
              data[proc][i] = rec;
           fireTableDataChanged();
        
  }


       protected void setTable(JTable table) {
          this.table = table;
          }


       public synchronized void removePlanRecord(PlanRecord pr){
           int proc = pr.getProc();
           int st = pr.getStartTime() - now;
           int et = pr.getEndTime() - now;
      	   if ( st < 0 ) // length passed item to be removed
              return;
           for(int i = st; i < et; i++)
              data[proc][i] = null;
           fireTableDataChanged();
         }

    /**
      this is changed for 1.3 to get rid of the annoying
      swing synchronisation exception. This was caused by the
      swing threads attempting to access the table data structures
      while redrawing.
      To retunr to the old version fire a tableStructureChanged event

      */
      public synchronized void clockTicked(int now){
      if ( newData == null) {
          newData = new PlanRecord [width][length];}
      for(int i = 0; i < width; i++ ) {
         for(int j = 0; j < length - 1; j++ )
             newData[i][j] = data[i][j+1];
              // finally
              newData[i][length-1] = null;
               }
        data = newData;
        this.now = now;
        if ( now > from ) from = now;
        // next line changed from fireTableStructureChanged
        fireTableDataChanged();
      }


      public  void setFrom(int from){
         if ( from < now ) {
             reSetFrom();
         }
         else {
             this.from = from;
         }
         fireTableStructureChanged();
      }


     public  int getFrom() {
        return from;
     }


     public  void reSetFrom() {
         from = now;
         fireTableStructureChanged();
     }


     public Object[] getListeners(){
        Object[] listeners = listenerList.getListenerList();
        return listeners;
     }


     public  void clockTickEvent(ClockEvent event) {
         clockTicked(event.getValue());
         fireChanged();
     }


     public  void planStepCreatedEvent(PlanStepEvent event) {
         createPlanRecord(event.getPlanRecord());
         fireChanged();
     }


     public  void planStepDisposedEvent(PlanStepEvent event) {
         removePlanRecord( event.getPlanRecord());
         fireChanged();
     }


     public void planStepStateChangedEvent(PlanStepEvent event) {
         stateChangedPlanRecord( event.getPlanRecord());
         fireChanged();
     }


    public void addChangeListener(ChangeListener x) {
     changeListeners.add(ChangeListener.class, x);
    }


    public void removeChangeListener(ChangeListener x) {
     changeListeners.remove(ChangeListener.class, x);
    }


    protected  void fireChanged() {
        ChangeEvent c = new ChangeEvent(this);
        Object[] listeners = changeListeners.getListenerList();
        for (int i= listeners.length-2; i >= 0; i -=2) {
          if (listeners[i] == ChangeListener.class) {
            ChangeListener cl = (ChangeListener)listeners[i+1];
            cl.stateChanged(c);
          }
        }
     }


       public void removeZeusEventMonitors(){
         planner.removePlanStepMonitor(this,PlanStepEvent.CREATE_MASK
                                          |PlanStepEvent.DISPOSE_MASK
                                          | PlanStepEvent.STATE_CHANGE_MASK);

          em.removeClockMonitor(this, ClockEvent.TICK_MASK);
       }


       public  int getProcessors(){
          return width;
       }
}
