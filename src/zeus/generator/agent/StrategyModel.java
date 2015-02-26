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
* StrategyModel.java
*
* The underlying model for the Strategy Table
*****************************************************************************/

package zeus.generator.agent;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.*;
import zeus.generator.util.*;

public class StrategyModel extends AbstractTableModel
                       implements ChangeListener {

  static final int MODE       = 0;
  static final int TYPE       = 1;
  static final int AGENTS     = 2;
  static final int RELATIONS  = 3;
  static final int STRATEGY   = 4;
  static final int PARAMETERS = 5;
  static final int FACT       = 6;

  static final int      USE    = StrategyInfo.USE;
  static final int      NO_USE = StrategyInfo.NO_USE;
  static final String[] ALL    = new String[0];

  static int count  = 0;
  static Vector INITIATOR_STRATEGY_LIST = null;
  static Vector RESPONDENT_STRATEGY_LIST = null;

  static {
     String sep = SystemProps.getProperty("file.separator");
     String user1 = SystemProps.getProperty("user.strategy.initiator");
     String user2 = SystemProps.getProperty("user.strategy.respondent");
     String system1 = SystemProps.getProperty("system.strategy.initiator");
     String system2 = SystemProps.getProperty("system.strategy.respondent");

     StringTokenizer s1 = null;
     HSet List = new HSet();

     if ( system1 != null ) {
        s1 = new StringTokenizer(system1,sep);
        while( s1.hasMoreTokens() )
           List.add(s1.nextToken());
     }
     if ( user1 != null ) {
        s1 = new StringTokenizer(user1,sep);
        while( s1.hasMoreTokens() )
            List.add(s1.nextToken());
     }
     if ( StrategyInfo.DEFAULT_INITIATOR_STRATEGY != null )
        List.add(StrategyInfo.DEFAULT_INITIATOR_STRATEGY);

     INITIATOR_STRATEGY_LIST = List.toVector();

     List.clear();
     if ( system2 != null ) {
        s1 = new StringTokenizer(system2,sep);
        while( s1.hasMoreTokens() )
           List.add(s1.nextToken());
     }
     if ( user2 != null ) {
        s1 = new StringTokenizer(user2,sep);
        while( s1.hasMoreTokens() )
            List.add(s1.nextToken());
     }
     if ( StrategyInfo.DEFAULT_RESPONDENT_STRATEGY != null )
        List.add(StrategyInfo.DEFAULT_RESPONDENT_STRATEGY);

     RESPONDENT_STRATEGY_LIST = List.toVector();
  };


  protected EventListenerList changeListeners = new EventListenerList();
  protected String[]          columnNames     = {
     "Mode", "Fact Type", "Agents", "Relations", "Strategy", "Parameters",
  };
  protected Vector            data            = new Vector();
  protected int               selectedRow     = -1;
  protected AttributeModel    attributeModel;
  protected OntologyDb        ontologyDb;
  protected ProtocolInfo      protocol;
  protected GeneratorModel    genmodel;

  public StrategyModel(GeneratorModel genmodel, OntologyDb ontologyDb,
                       AttributeModel attributeModel) {

     this.genmodel = genmodel;
     this.ontologyDb = ontologyDb;
     this.attributeModel = attributeModel;
     ontologyDb.addChangeListener(this);
     genmodel.addChangeListener(this);
  }

  public void reset(ProtocolInfo input) {
     selectRow(-1); // side effect of saving fact attribute info;
     // save any modified information
     if ( protocol != null )
        protocol.setConstraints(data);
     int size = data.size();
     data.removeAllElements();
     if ( size != 0 )
        fireTableRowsDeleted(0,size-1);

     this.protocol = input;
     if ( protocol != null ) {
        StrategyInfo[] info = protocol.getConstraints();
        for(int i = 0; i < info.length; i++ )
           data.addElement(info[i]);
        fireTableRowsInserted(0,info.length-1);
     }
     fireTableStructureChanged(); // need to force redraw - bug in swing?
  }

  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        data.removeElementAt(rows[i]-i);
        fireTableRowsDeleted(rows[i]-i,rows[i]-i);
     }
     selectRow(-1);
     fireChanged();
  }

  public void selectRow(int row) {
     if ( selectedRow == row ) return;
     selectedRow = row;
     if ( attributeModel != null ) {
        if ( selectedRow >= 0 ) {
           StrategyInfo info = (StrategyInfo)data.elementAt(selectedRow);
           attributeModel.reset((Fact)info.getFact());
        }
        else
           attributeModel.reset(null);
     }
  }

  public void addNewRows(String[] names)  {
     if ( names == null || names.length == 0 ) return;
     Fact[] input = new Fact[names.length];
     for(int i = 0; i < names.length; i++ )
        input[i] = ontologyDb.getFact(Fact.VARIABLE,names[i]);
     addRows(input);
  }

  public void addRows(Fact[] input) {
     if ( input == null     ) return;
     if ( input.length == 0 ) return;

     Fact f1;
     StrategyInfo info;
     String strategy = null;
     if ( protocol.getType().equals(ProtocolInfo.RESPONDENT) )
        strategy = StrategyInfo.DEFAULT_RESPONDENT_STRATEGY;
     else
        strategy = StrategyInfo.DEFAULT_INITIATOR_STRATEGY;

     int size = data.size();
     for(int i = 0; i < input.length; i++ ) {
        f1 = new Fact(input[i]);
        // check for duplicate fact id and modify if found
	String id = f1.ID();
	while( contains(id) )
	   id += (count++);
        f1.setId(id);
        data.addElement(new StrategyInfo(f1,strategy));
     }
     selectRow(-1);
     fireTableRowsInserted(size-1,size+input.length-1);
     fireChanged();
  }

  protected boolean contains(String id) {
    // check that model does not already contain task
    StrategyInfo info;
    for(int i = 0; i < data.size(); i++ ) {
       info = (StrategyInfo)data.elementAt(i);
       if ( id.equals(info.getFact().ID()) )
          return true;
    }
    return false;
  }

  // ----------------------------------------------------------------------

  public int getColumnCount()           { return columnNames.length;  }
  public int getRowCount()              { return data.size(); }
  public String  getColumnName(int col) { return columnNames[col]; }

  public boolean isCellEditable(int row, int col) {
     StrategyInfo info = (StrategyInfo)data.elementAt(row);
     return col != TYPE && (col != STRATEGY || info.getType() == USE) &&
            (col != PARAMETERS || info.getType() == USE);
  }

  public Object getValueAt (int row, int column)  {
     StrategyInfo info = (StrategyInfo)data.elementAt(row);
     switch(column) {
        case MODE:
             return new Boolean(info.getType() == USE);

        case TYPE:
             return info.getFact().getType();

        case PARAMETERS:
             return (info.getType() == USE) ? info.getParameters() : null;

        case AGENTS:
             String[] agentIds = info.getAgents();
	     String[] agents = new String[agentIds.length];
	     for(int i = 0; i < agentIds.length; i++ )
	        agents[i] = genmodel.getAgentName(agentIds[i]);
	     return agents;
        
	case RELATIONS:
             return info.getRelations();

        case STRATEGY:
             return (info.getType() == USE) ? info.getStrategy() : null;

        case FACT:
             return info.getFact();
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column)  {
     String[] list;

     StrategyInfo info = (StrategyInfo)data.elementAt(row);
     switch(column) {
        case MODE:
             Boolean b = (Boolean)aValue;
             int mode = b.equals(Boolean.TRUE) ? USE : NO_USE;
             if ( mode != info.getType() ) {
                info.setType(mode);
                if ( mode == USE ) {
                   if ( protocol.getType().equals(ProtocolInfo.RESPONDENT) )
                      info.setStrategy(StrategyInfo.DEFAULT_RESPONDENT_STRATEGY);
                   else
                      info.setStrategy(StrategyInfo.DEFAULT_INITIATOR_STRATEGY);
                   info.clearParameters();
                }
                fireTableCellUpdated(row,STRATEGY);
                fireTableCellUpdated(row,PARAMETERS);
                fireTableCellUpdated(row,column);
             }
             break;

	case TYPE:
             Core.ERROR(null,1,this);
             break;

        case AGENTS:
             list = (String[])aValue;
	     String[] agentIds = new String[list.length];
  	     for(int i = 0; i < agentIds.length; i++ ) {
	        agentIds[i] = genmodel.reverseAgentNameLookup(list[i]);
                if ( agentIds[i] == null ) {
                   agentIds[i] = genmodel.createNewAgentId();
                   genmodel.createNewAgent(agentIds[i]);
                   genmodel.renameAgent(agentIds[i],list[i]);
                }
             }
             info.setAgents(agentIds);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;

        case RELATIONS:
             list = (String[])aValue;
             info.setRelations(list);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;

        case STRATEGY:
             Core.ERROR(info.getType() == USE,2,this);
             if ( info.getStrategy().equals(aValue) )
                return;
             info.setStrategy((String)aValue);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;

        case PARAMETERS:
             Core.ERROR(info.getType() == USE,3,this);
             if ( info.getParameters().equals(aValue) )
                return;
             info.setParameters((Hashtable)aValue);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;
     }
  }

  public void stateChanged(ChangeEvent e) {
     Object src = e.getSource();
     if ( src == ontologyDb ) {
        // REM revalidate against the ontology
     }
     else if ( src == genmodel ) {
        // refresh agent names, etc.
	fireTableDataChanged();
     }
  }

  public void addChangeListener(ChangeListener x) {
     changeListeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     changeListeners.remove(ChangeListener.class, x);
  }
  
  protected void fireChanged() {
     ChangeEvent c = new ChangeEvent(this);
     Object[] listeners = changeListeners.getListenerList();
     for(int i= listeners.length-2; i >= 0; i -=2) {
        if (listeners[i] == ChangeListener.class) {
           ChangeListener cl = (ChangeListener)listeners[i+1];
           cl.stateChanged(c);
        }
     }
  }
}
