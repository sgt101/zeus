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
* AgentModel.java
*
* The underlying model for the Agent Table
*****************************************************************************/

package zeus.generator.code;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;


public class AgentModel extends UtilityModel {
  
  static final int GENERATE    = 0;
  static final int STATUS      = 1;
  static final int NAME        = 2;
  static final int HOST        = 3;
  static final int DATABASE    = 4;
  static final int SERVER_FILE = 5;
  static final int HAS_GUI     = 6;
  static final int EXTERNAL    = 7;
  static final int ICON        = 8;

  protected static final String[] columnNames = {
     "Generate", "Status", "Name", "Host", "Database Extension",
     "DNS file", "Create GUI?", "External Program", "Icon"
  };

  protected Vector data = new Vector();
  protected GenerationPlan  genplan;

  public AgentModel(GenerationPlan genplan) {
    this.genplan = genplan;
    genplan.addChangeListener(this);
    refresh();
  }

  public void addNewRow() {
  }
  public void removeRows(int[] rows) {
  }
  protected void refresh()  {
    data.removeAllElements();
    AgentInfo[] info = genplan.getAgents();
    for(int i = 0; i < info.length; i++ )
       data.addElement(info[i]);
    fireTableDataChanged();
  }

  public int     getColumnCount()       { return columnNames.length; }
  public int     getRowCount()          { return data.size(); }
  public String  getColumnName(int col) { return columnNames[col]; }

  public boolean isCellEditable(int row, int col) {
     switch(col) {
        case GENERATE:
        case HOST:
        case DATABASE:
        case SERVER_FILE:
        case HAS_GUI:
        case EXTERNAL:
        case ICON:
             return true;

	case NAME:
        case STATUS:
             return false;
     }
     return false;
  }

  public Object getValueAt(int row, int column) {
     AgentInfo info = (AgentInfo)data.elementAt(row);
     switch(column) {
        case GENERATE:
             return new Boolean(info.generate);

	case STATUS:
             return info.status;

        case NAME:
             return info.name;

        case HOST:
             return info.host;

        case DATABASE:
             return info.database;

	    case SERVER_FILE:
             return info.dns_file;

        case HAS_GUI:
             return new Boolean(info.has_gui);

        case EXTERNAL:
             return info.zeus_external;

        case ICON:
             return info.icon_file;
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column) {
     AgentInfo info = (AgentInfo)data.elementAt(row);
     switch(column) {
        case GENERATE:
             info.generate = updateBoolean(info.generate,aValue);
             if ( changed ) genplan.setAgent(info);
             break;

	case STATUS:
             Core.ERROR(null,1,this);
             break;

        case NAME:
             Core.ERROR(null,2,this);
             break;

        case HOST:
             info.host = updateString(info.host,aValue);
             if ( changed ) genplan.setAgent(info);
             break;

        case DATABASE:
             info.database = updateString(info.database,aValue);
             if ( changed ) genplan.setAgent(info);
             break;

	case SERVER_FILE:
             info.dns_file = updateString(info.dns_file,aValue);
             if ( changed ) genplan.setAgent(info);
             break;

        case HAS_GUI:
             info.has_gui = updateBoolean(info.has_gui,aValue);
             if ( changed ) genplan.setAgent(info);
             break;

        case EXTERNAL:
             info.zeus_external = updateString(info.zeus_external,aValue);
             if ( changed ) genplan.setAgent(info);
             break;

        case ICON:
             info.icon_file = updateString(info.icon_file,aValue);
             genplan.setAgentIcon(info);
             break;
     }
  }
}
