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
* ExpressionCellEditor.java
*
* The large text field for editing long expressions
*****************************************************************************/

package zeus.gui.editors;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

import zeus.gui.fields.*;
import zeus.util.Core;


public class ExpressionCellEditor extends DefaultCellEditor
                            implements ActionListener {
  
    protected JButton button = new JButton("");
    protected int row, column;
    protected LargeTextField valuefield = new LargeTextField(10,40);
    protected boolean first = true;
    protected TableModel model;
  
    public ExpressionCellEditor(TableModel model) {
      super(new JTextField());

      this.model = model;
  
      setClickCountToStart(2);
  
      button.setBackground(Color.white);
      button.setBorderPainted(false);
      button.addActionListener(this);

      valuefield.setLineWrap(true);
      valuefield.addActionListener(this);
      valuefield.setBackground(Color.green);
      valuefield.setOpaque(true);
      valuefield.setSize(400,80);
      valuefield.setMinimumSize(new Dimension(400,80));
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ( src == button ) {
         button.setEnabled(false);
         Component root = SwingUtilities.getRoot(button);
         JLayeredPane pane = null;
         if ( root instanceof JDialog )
            pane = ((JDialog)root).getLayeredPane();
         else if ( root instanceof JFrame )
            pane = ((JFrame)root).getLayeredPane();
         else
            Core.ERROR(null,1,this);

         if ( first ) {
            pane.add(valuefield,JLayeredPane.PALETTE_LAYER);
            first = false;
         }
         Point pt = SwingUtilities.convertPoint(button,0,0,root);
         pane.moveToFront(valuefield);
         valuefield.setVisible(true);
         valuefield.setLocation(new Point(pt.x-2,pt.y-22));
         valuefield.grabFocus();
      }
      else if ( src == valuefield ) {
         fireEditingStopped();
         valuefield.setVisible(false);
         String value = valuefield.getText();
         model.setValueAt(value,row,column);
      }
    }

    public Document getDocument() {
       return valuefield.getDocument();
    }
    public void addMouseListener(MouseListener l) {
       valuefield.addMouseListener(l);
    }
    public void removeMouseListener(MouseListener l) {
       valuefield.removeMouseListener(l);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {  
      this.row = row;
      this.column = column;
      String s = "";
      if ( value != null ) s = (String)value;
      valuefield.setText(s);
      valuefield.selectAll();
      button.setEnabled(true);
      button.setText(s);
      return  button;
    }
}
