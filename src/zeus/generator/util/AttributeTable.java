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
* AttributeTable.java
*
* The Container panel for the Attribute Table
*****************************************************************************/

package zeus.generator.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;

import zeus.gui.fields.*;
import zeus.gui.editors.*;
import zeus.util.Core;

public class AttributeTable extends JPanel {

  protected JTable               table;
  protected AttributeModel       model;
  protected AttributeDialog      dialog;
  protected AttributeTreeModel   attributeTreeModel;

  public AttributeTable(AttributeModel model) {
     this.model = model;
     attributeTreeModel = new AttributeTreeModel(model);

     TableColumnModel tm = new DefaultTableColumnModel();
     TableColumn column;
     column = new TableColumn(AttributeModel.ATTRIBUTE,12);
     column.setHeaderValue(model.getColumnName(AttributeModel.ATTRIBUTE));
     tm.addColumn(column);
     ExpressionCellEditor editor = new ExpressionCellEditor(model);
     editor.addMouseListener(new SymMouseAction());
     column = new TableColumn(AttributeModel.VALUE,24,
        new ValidatingCellRenderer(model,AttributeModel.VALUE),	editor);
     column.setHeaderValue(model.getColumnName(AttributeModel.VALUE));
     tm.addColumn(column);

     table = new JTable(model,tm);
     table.getTableHeader().setReorderingAllowed(false);
     table.setColumnSelectionAllowed(false);

     GridBagLayout gridBagLayout = new GridBagLayout();
     setLayout(gridBagLayout);
     setBackground(Color.lightGray);
     setBorder(new BevelBorder(BevelBorder.LOWERED));
     setPreferredSize(new Dimension(200,120));

     JScrollPane scrollPane = new JScrollPane(table);
     scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
     scrollPane.setPreferredSize(new Dimension(400,80));
     table.setBackground(Color.white);

     GridBagConstraints gbc = new GridBagConstraints();
     gbc.gridwidth = GridBagConstraints.REMAINDER;
     gbc.anchor = GridBagConstraints.WEST;
     gbc.fill = GridBagConstraints.BOTH;
     gbc.weightx = gbc.weighty = 1;
     gbc.insets = new Insets(4,4,4,4);
     gridBagLayout.setConstraints(scrollPane, gbc);
     add(scrollPane);
  }

  public void setFactModel(BasicFactModel factmodel) {
     attributeTreeModel.setFactModel(factmodel);
  }
  public void setFactModels(BasicFactModel preModel, BasicFactModel postModel) {
     attributeTreeModel.setFactModels(preModel,postModel);
  }

  class SymMouseAction extends MouseAdapter implements AttributeSelector {
     protected JTextComponent field = null;

     public void mouseClicked(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
           if ( dialog == null ) {
              Component comp = SwingUtilities.getRoot(table);
              if ( comp instanceof Frame )
                 dialog = new AttributeDialog((Frame)comp,attributeTreeModel);
              else if ( comp instanceof Dialog )
                 dialog = new AttributeDialog((Dialog)comp,attributeTreeModel);
              else
                 Core.ERROR(null,1,this);
           }
           field = (JTextComponent)e.getSource();
           dialog.setLocationRelativeTo(field);
           dialog.display(this);
        }
     }
     public void attributeSelected(String attribute) {
        try {
	   Document doc = field.getDocument();
           int length = doc.getLength();
           AttributeSet a = doc.getDefaultRootElement().getAttributes();
           doc.insertString(length,attribute,a);
	}
	catch(BadLocationException e) {
	}
     }
  }
}
