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
* SymBasicFactModel.java
*
* A basic fact model simulator
*****************************************************************************/

package zeus.generator.task;

import java.util.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.event.*;
import zeus.generator.util.*;

public class SymBasicFactModel implements BasicFactModel, 
                               ChangeListener, RenameListener {

  protected EventListenerList listeners = new EventListenerList();
  protected int               type;
  protected SummaryTaskModel  taskModel;

  public SymBasicFactModel(int type, SummaryTaskModel taskModel) {
     this.taskModel = taskModel;
     this.type = type;
     taskModel.addChangeListener(this);
     taskModel.addRenameListener(this);
  }

  public Fact[] getData() {
     return taskModel.getConditions(type);
  }

  public void stateChanged(ChangeEvent e) {
     fireChanged();
  }

 public void nameChanged(RenameEvent e) {
    TaskNode node = (TaskNode)e.getObject();
    if ( type == SummaryTaskModel.PRECONDITION &&
         node.getName().equals(TaskNode.BEGIN) )
       fireNameChanged(node,e.getOriginal(),e.getCurrent());
    else if ( type == SummaryTaskModel.POSTCONDITION &&
         node.getName().equals(TaskNode.END) )
       fireNameChanged(node,e.getOriginal(),e.getCurrent());
  }

  public void addChangeListener(ChangeListener x) {
     listeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     listeners.remove(ChangeListener.class, x);
  }
  public void addRenameListener(RenameListener x) {
     listeners.add(RenameListener.class, x);
  }
  public void removeRenameListener(RenameListener x) {
     listeners.remove(RenameListener.class, x);
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

  protected void fireNameChanged(Object object, Object previous,
                                 Object current) {
     RenameEvent c = new RenameEvent(this,object,previous,current);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == RenameListener.class) {
           RenameListener cl = (RenameListener)list[i+1];
           cl.nameChanged(c);
        }
     }
  }
}
