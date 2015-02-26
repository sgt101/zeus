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

import java.awt.Color;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

public abstract class AbstractGraphModel implements GraphModel {
   private static final int STRUCTURE_CHANGED  = 0;
   private static final int NODE_ADDED         = 1;
   private static final int NODE_REMOVED       = 2;
   private static final int NODE_STATE_CHANGED = 3;

   protected EventListenerList graphModelListeners = new EventListenerList();


   public abstract Enumeration nodes();
   public abstract void        setValue(GraphNode node, Object user_object);
   public abstract boolean     isNodeEditable(GraphNode node);

   public Color getLinkColor(GraphNode from, GraphNode to) {
      return Color.black;
   }
   public boolean isLinkVisible(GraphNode from, GraphNode to) {
      return true;
   }
   public Vector getViewRelations(GraphNode node) {
      return new Vector(10);
   }
   protected void fireGraphStructureChanged() {
      fireGraphAction(STRUCTURE_CHANGED,null);
   }
   protected void fireGraphNodeAdded(GraphNode node) {
      fireGraphAction(NODE_ADDED,node);
   }
   protected void fireGraphNodeRemoved(GraphNode node) {
      fireGraphAction(NODE_REMOVED,node);
   }
   protected void fireGraphNodeStateChanged(GraphNode node) {
      fireGraphAction(NODE_STATE_CHANGED,node);
   }
   public void addGraphModelListener(GraphModelListener x) {
      graphModelListeners.add(GraphModelListener.class, x);
   }
   public void removeGraphModelListener(GraphModelListener x) {
      graphModelListeners.remove(GraphModelListener.class, x);
   }
   private void fireGraphAction(int type, GraphNode node) {
      GraphModelEvent evt = new GraphModelEvent(this,node);
      Object[] listeners = graphModelListeners.getListenerList();
      for(int i = listeners.length-2; i >= 0; i -= 2) {
         if (listeners[i] == GraphModelListener.class) {
            GraphModelListener cl = (GraphModelListener)listeners[i+1];
            switch(type) {
               case STRUCTURE_CHANGED:
                    cl.graphStructureChanged(evt);
                    break;
               case NODE_ADDED:
                    cl.graphNodeAdded(evt);
                    break;
               case NODE_REMOVED:
                    cl.graphNodeRemoved(evt);
                    break;
               case NODE_STATE_CHANGED:
                    cl.graphNodeStateChanged(evt);
                    break;
            }
         }
      }
   }

}
