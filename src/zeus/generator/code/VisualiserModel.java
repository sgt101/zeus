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
* VisualiserModel.java
*
* The underlying model for the Generator Table
*****************************************************************************/

package zeus.generator.code;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;


public class VisualiserModel extends UtilityModel {

  static final int NAME        = 0;
  static final int HOST        = 1;
  static final int SERVER_FILE = 2;
  static final int HAS_GUI     = 3;
  static final int EXTERNAL    = 4;

  protected static final String[] columnNames = {
     "Name", "Host", "DNS file", "Create GUI?", "External Program"
  };

  protected Vector data = new Vector();
  protected GenerationPlan  genplan;

  public VisualiserModel(GenerationPlan genplan) {
    this.genplan = genplan;
    genplan.addChangeListener(this);
    refresh();
  }

  public void addNewRow() {
     genplan.createVisualiser();
  }
  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        VisualiserInfo info = (VisualiserInfo)data.elementAt(rows[i]-i);
        genplan.removeVisualiser(info.id);
     }
  }

  protected void refresh()  {
    data.removeAllElements();
    VisualiserInfo[] info = genplan.getVisualisers();
    for(int i = 0; i < info.length; i++ )
       data.addElement(info[i]);
    fireTableDataChanged();
  }

  public int     getColumnCount()                 { return columnNames.length - 2; }
  public int     getRowCount()                    { return data.size(); }
  public String  getColumnName(int col)           { return columnNames[col]; }

  public boolean isCellEditable(int row, int col) { return true; }

  public Object getValueAt(int row, int column) {
     VisualiserInfo info = (VisualiserInfo)data.elementAt(row);
     switch(column) {
        case NAME:
             return info.name;

        case HOST:
             return info.host;

        case HAS_GUI:
             return new Boolean(info.has_gui);

        case EXTERNAL:
             return info.zeus_external;

        case SERVER_FILE:
             return info.dns_file;
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column) {
     VisualiserInfo info = (VisualiserInfo)data.elementAt(row);
     switch(column) {
        case NAME:
             info.name = updateString(info.name,aValue,false);
             break;

        case HOST:
             info.host = updateString(info.host,aValue);
             break;

        case HAS_GUI:
             info.has_gui = updateBoolean(info.has_gui,aValue);
             break;

	case EXTERNAL:
             info.zeus_external = updateString(info.zeus_external,aValue);
             break;

        case SERVER_FILE:
             info.dns_file = updateString(info.dns_file,aValue);
             break;
     }
     if ( changed ) genplan.setVisualiser(info);
  }
}