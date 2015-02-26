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



package zeus.gui.graph;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public abstract class AbstractGraphNodeEditor implements GraphNodeEditor {
   protected static final int EDITING_STOPPED = 0;
   protected static final int EDITING_CANCELLED = 1;

   protected EventListenerList editListeners = new EventListenerList();

   protected Graph     graph = null;
   protected GraphNode node = null;
   
   public void addGraphNodeEditorListener(GraphNodeEditorListener x) {
      editListeners.add(GraphNodeEditorListener.class, x);
   }
   public void removeGraphNodeEditorListener(GraphNodeEditorListener x) {
      editListeners.remove(GraphNodeEditorListener.class, x);
   }

   protected void fireEditAction(int type, GraphNode node, Object value) {
      GraphNodeEditorEvent evt = new GraphNodeEditorEvent(this,node,value);
      Object[] listeners = editListeners.getListenerList();
      for(int i = listeners.length-2; i >= 0; i -= 2) {
         if (listeners[i] == GraphNodeEditorListener.class) {
            GraphNodeEditorListener cl = (GraphNodeEditorListener)listeners[i+1];
            switch(type) {
	       case EDITING_STOPPED:
                    cl.graphNodeEditingStopped(evt);
                    break;
               case EDITING_CANCELLED:
                    cl.graphNodeEditingCancelled(evt);
                    break;
            }
         }
      }
   }
}
