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



/****************************************************************************
* SummaryTaskNodeEditor.java
*
*
***************************************************************************/

package zeus.generator.task;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.graph.*;

public class SummaryTaskNodeEditor extends AbstractGraphNodeEditor {
   protected SummaryTaskNodeEditorDialog dialog = null;
   protected OntologyDb ontologyDb = null;
   protected SummaryTaskModel model = null;

   public SummaryTaskNodeEditor(OntologyDb ontologyDb, SummaryTaskModel model) {
      this.ontologyDb = ontologyDb;
      this.model = model;
   }

   public Component getNodeEditorComponent(Graph graph, GraphNode gnode) {
      this.graph = graph;
      this.node = gnode;

      if ( dialog == null )
         dialog = new SummaryTaskNodeEditorDialog(
	    (Frame)SwingUtilities.getRoot(graph),ontologyDb);

      TaskNode node = (TaskNode)gnode.getUserObject();
      dialog.reset(this, node, model.getNodes(), model.getLinks());
      return dialog;
   }

   public void editingStopped(TaskNode input, TaskLink[] links,
                             Hashtable names) {
      Vector data = new Vector();
      data.addElement(input);
      data.addElement(links);
      data.addElement(names);
      fireEditAction(EDITING_STOPPED,this.node,data);
   }

  public void editingCancelled() {
     fireEditAction(EDITING_CANCELLED,node,null);
  }
}
