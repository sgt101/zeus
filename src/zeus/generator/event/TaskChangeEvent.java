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



package zeus.generator.event;

import javax.swing.event.*;

import zeus.concepts.AbstractTask;
import zeus.util.Core;

public class TaskChangeEvent extends java.util.EventObject {
  public static final int ADD    = 0;
  public static final int MODIFY = 1;
  public static final int DELETE = 2;

  protected AbstractTask task;
  protected int          mode;

  public TaskChangeEvent(Object source, AbstractTask task,
                         int mode) {
     super(source);
     this.task = task;
     this.mode = mode;
     Core.ERROR(mode == ADD || mode == MODIFY || mode == DELETE, 1, this);
  }
  public AbstractTask getTask()      { return task; }
  public int          getEventType() { return mode; }
}
