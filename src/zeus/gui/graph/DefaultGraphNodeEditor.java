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

public class DefaultGraphNodeEditor extends AbstractGraphNodeEditor {
   protected JTextField field = null;

   public DefaultGraphNodeEditor() {
      field = new JTextField(20);
      field.setBackground(Color.green);
      field.setOpaque(true);
      field.setSize(100,25);
      field.setMinimumSize(new Dimension(100,25));

      SymAction listener = new SymAction();
      field.addActionListener(listener);
      field.addFocusListener(listener);
   }

   public Component getNodeEditorComponent(Graph graph, GraphNode node) {
      this.graph = graph;
      this.node = node;
      field.setText(node.getUserObject().toString());
      return field;
   }
   private class SymAction implements ActionListener, FocusListener {
      public void actionPerformed(ActionEvent e) {
         if ( e.getSource() == field ) {
            fireEditAction(EDITING_STOPPED,node,field.getText());
         }
      }
      public void focusGained(FocusEvent e) {
      }
      public void focusLost(FocusEvent e) {
         if ( e.getSource() == field ) {
            fireEditAction(EDITING_STOPPED,node,field.getText());
         }
      }
   }
}