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
* FactModel.java
*
* The underlying model for the Fact Table
*****************************************************************************/

package zeus.generator.util;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.generator.event.*;

public class FactModel extends AbstractTableModel
                       implements BasicFactModel, ChangeListener {

  public static final int TYPE      = 0;
  public static final int INSTANCE  = 1;
  public static final int MODIFIERS = 2;
  public static final int FACT      = 3;


  protected static int count  = 0;
  protected static final boolean ERROR = true;
  protected static final boolean NO_ERROR = false;
  protected static final String[] columnNames     = {
     "Fact Type", "Instance", "Modifiers"
  };

  protected EventListenerList listeners = new EventListenerList();
  protected Vector            data            = new Vector();
  protected int               selectedRow     = -1;
  protected AttributeModel    attributeModel;
  protected OntologyDb        ontologyDb;
  protected boolean           isVariable;
  protected boolean           isEditable = true;
  protected int               type;
  protected Vector            relatedModels = new Vector();

  public FactModel(OntologyDb ontologyDb, AttributeModel attributeModel,
                   boolean isVariable, int type, Fact[] input) {

     this.ontologyDb = ontologyDb;
     this.attributeModel = attributeModel;
     this.isVariable = isVariable;
     this.type = type;
     ontologyDb.addChangeListener(this);
     reset(input);
  }

  public OntologyDb     getOntologyDb()     { return ontologyDb; }
  public AttributeModel getAttributeModel() { return attributeModel; }

  public void reset(Fact[] input) {
     int r = data.size();
     selectRow(-1);
     data.removeAllElements();
     if ( r != 0 ) fireTableRowsDeleted(0,r-1);

     for(int i = 0; i < input.length; i++ )
        data.addElement(input[i]);
     fireTableRowsInserted(0,input.length-1);
     fireTableStructureChanged(); // bug in swing?
  }

  public Fact[] getData() {
     Fact[] output = new Fact[data.size()];
     for(int i = 0; i < data.size(); i++ )
        output[i] = (Fact)data.elementAt(i);
     return output;
  }

  public void addRelatedModel(FactModel model) {
     if ( !relatedModels.contains(model) )
        relatedModels.addElement(model);
  }
  public void removeRelatedModel(FactModel model) {
     relatedModels.removeElement(model);
  }

  public void removeRows(int[] rows) {
     Fact f;
     for(int i = 0; i < rows.length; i++ ) {
        f = (Fact)data.elementAt(rows[i]-i);
        data.removeElementAt(rows[i]-i);
        fireTableRowsDeleted(rows[i]-i,rows[i]-i);
        fireFactEvent(f,FactModelEvent.FACT_REMOVED);
     }
     selectRow(-1);
     fireChanged();
  }

  public void selectRow(int row) {
     selectedRow = row;
     if ( attributeModel != null ) {
        if ( selectedRow >= 0 )
           attributeModel.reset((Fact)data.elementAt(selectedRow));
        else
           attributeModel.reset(null);
     }
  }

  public void addNewRows(String[] names)  {
     if ( names == null || names.length == 0 ) return;
     Fact[] input = new Fact[names.length];
     for(int i = 0; i < names.length; i++ )
        input[i] = ontologyDb.getFact(isVariable,names[i]);
     addRows(input);
  }

  public void addRows(Fact[] input) {
     if ( input == null || input.length == 0 ) return;

     Fact f1;
     String id;
     int size = data.size();
     for(int i = 0; i < input.length; i++ ) {
        f1 = new Fact(input[i]);
        id = f1.ID();
        while( contains(id,NO_ERROR) )
           id += (count++);
        f1.setId(id);
        data.addElement(f1);
        fireFactEvent(f1,FactModelEvent.FACT_ADDED);
     }
     fireTableRowsInserted(size,size+input.length-1);
     selectRow(-1);
     fireChanged();
  }
  public void removeRows(Fact[] input) {
     if ( input == null || input.length == 0 ) return;

     Fact f1;
     String id;
     for(int i = 0; i < input.length; i++ ) {
        id = input[i].ID();
        for(int j = 0; j < data.size(); j++ ) {
           f1 = (Fact)data.elementAt(j);
           if ( f1.ID().equals(id) ) {
              data.removeElementAt(j);
              fireTableRowsDeleted(j,j);
              fireFactEvent(f1,FactModelEvent.FACT_REMOVED);
              j--;
           }
        }
     }
     selectRow(-1);
     fireChanged();
  }

  // ----------------------------------------------------------------------

  public int getColumnCount() {
     if ( type == FactPanel.NONE )
        return columnNames.length - 1;
     else
        return columnNames.length;
  }
  public int     getRowCount()                    { return data.size(); }
  public String  getColumnName(int col)           { return columnNames[col]; }
  public boolean isCellEditable(int row, int col) {
     return isEditable && col != TYPE;
  }

  public void setEditable(boolean isEditable) {
     this.isEditable = isEditable;
  }

  public Object getValueAt (int row, int column)  {
     Fact f = (Fact)data.elementAt(row);
     switch(column) {
        case TYPE:
             return f.getType();
        case INSTANCE:
             return f.getId();
        case MODIFIERS:
             return new Integer(f.getModifiers());
        case FACT:
             return f;
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column)  {
    // prevents the table being accidently loaded with a null value
    // current table implementation needs this - possibly because of a bug
    if (aValue.toString().equals(""))
      return;

     Fact f = (Fact)data.elementAt(row);
     switch(column) {
        case TYPE:
             Core.ERROR(null,1,this);
             break;
        case INSTANCE:
             String newId = (String)aValue;
             String id = f.ID();
             if ( id.equals(newId) )
                return;
             else if ( contains(newId,ERROR) )
                return;
             else {
                String fid0 = f.getId();
                f.setId(newId);
                String fid1 = f.getId();
                fireTableCellUpdated(row,column);
                fireNameChanged(f,fid0,fid1);
                fireChanged();
             }
             break;
        case MODIFIERS:
             int modifiers = ((Integer)aValue).intValue();
             if ( modifiers == f.getModifiers()) return;
             f.setModifiers(modifiers);
             fireTableCellUpdated(row,column);
             fireChanged();
             break;
        case FACT:
             Core.ERROR(null,2,this);
             break;
     }
  }

  protected boolean contains(String id, boolean error) {
     return contains(null,id,error);
  }
  protected boolean contains(FactModel origin, String id, boolean error) {
    Fact f;
    for(int i = 0; i < data.size(); i++ ) {
       f = (Fact)data.elementAt(i);
       if ( id.equals(f.ID()) ) {
          if ( error )
             JOptionPane.showMessageDialog(null,
                "Attempting to rename fact to an already\nexisting name",
                "Error", JOptionPane.ERROR_MESSAGE);
          return true;
       }
    }

    FactModel model;
    for(int i = 0; i < relatedModels.size(); i++ ) {
       model = (FactModel)relatedModels.elementAt(i);
       if ( model != origin )
          if ( model.contains(this,id,error) ) return true;
    }
    return false;
  }

  public void stateChanged(ChangeEvent e) {
     // Underlying ontology has changed!!
     // NEED to verify all facts!!
  }
  public void addFactModelListener(FactModelListener x) {
     listeners.add(FactModelListener.class, x);
  }
  public void removeFactModelListener(FactModelListener x) {
     listeners.remove(FactModelListener.class, x);
  }
  public void addChangeListener(ChangeListener x) {
     listeners.add(ChangeListener.class, x);
  }
  public void removeChangeListener(ChangeListener x) {
     listeners.remove(ChangeListener.class, x);
  }
  public void addRenameListener(RenameListener x) {
     listeners.add(RenameListener.class, x);
  }
  public void removeRenameListener(RenameListener x) {
     listeners.remove(RenameListener.class, x);
  }

  protected void fireChanged() {
     ChangeEvent c = new ChangeEvent(this);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == ChangeListener.class) {
           ChangeListener cl = (ChangeListener)list[i+1];
           cl.stateChanged(c);
        }
     }
  }

  protected void fireNameChanged(Object object, Object previous,
                                 Object current) {
     RenameEvent c = new RenameEvent(this,object,previous,current);
     FactModelEvent e = new FactModelEvent(this,(Fact)object,
        FactModelEvent.FACT_ID_CHANGED,(String)previous,(String)current);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == FactModelListener.class) {
           FactModelListener l = (FactModelListener)list[i+1];
           l.factModelChanged(e);
        }
        else if (list[i] == RenameListener.class) {
           RenameListener cl = (RenameListener)list[i+1];
           cl.nameChanged(c);
        }
     }
  }

  protected void fireFactEvent(Fact f, int type) {
     FactModelEvent e = new FactModelEvent(this,f,type);
     Object[] list = listeners.getListenerList();
     for(int i= list.length-2; i >= 0; i -=2) {
        if (list[i] == FactModelListener.class) {
           FactModelListener l = (FactModelListener)list[i+1];
           l.factModelChanged(e);
        }
     }
  }

}
