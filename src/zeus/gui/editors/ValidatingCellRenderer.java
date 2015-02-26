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
* ValidatingCellRenderer.java
*
*****************************************************************************/

package zeus.gui.editors;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


public class ValidatingCellRenderer extends DefaultTableCellRenderer {
     protected int[] columns = null;
     protected ValidatingModel model;

     public ValidatingCellRenderer(ValidatingModel model) {
        this.model = model;
     }
     public ValidatingCellRenderer(ValidatingModel model, int column) {
        this(model);
        columns = new int[1];
        columns[0] = column;
     }
     public ValidatingCellRenderer(ValidatingModel model, int[] input) {
        this(model);
        columns = new int[input.length];
        for(int i = 0; i < input.length; i++ )
           columns[i] = input[i];
     }

     protected boolean checkColumn(int c) {
        if ( columns == null || columns.length == 0 ) return true;

        for(int i = 0; i < columns.length; i++ )
           if ( columns[i] == c ) return true;

        return false;
     }

     public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        if ( checkColumn(column) ) {
           if ( model.isValidEntry(row,column) )
              setForeground(Color.black);
           else
              setForeground(Color.red);
        }
        else
           setForeground(Color.black);

        return super.getTableCellRendererComponent(table, value,
           isSelected, hasFocus, row, column);
     }
}