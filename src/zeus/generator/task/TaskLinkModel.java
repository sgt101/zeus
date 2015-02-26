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
* TaskLinkModel.java
*
* The underlying model for the Applicability TaskLink Table
*****************************************************************************/

package zeus.generator.task;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.generator.*;
import zeus.generator.event.*;
import zeus.generator.util.*;
import zeus.gui.editors.*;

public class TaskLinkModel extends AbstractTableModel
                              implements ChangeListener,
                                         RenameListener,
                                         LinkNodeSelectionListener {

  static final int LEFT = 0;
  static final int RIGHT = 1;

  static final String SEPARATOR = "::";

  protected static final String[] columnNames = {
     "From/Postconditions", "To/Preconditions"
  };

  protected GroupManager    leftGroupManager;
  protected GroupManager    rightGroupManager;

  protected EventListenerList changeListeners = new EventListenerList();
  protected Vector data     = new Vector();
  protected String nodeName = null;
  protected String leftNode = null, leftGroup = null,
                   leftType = null, leftArg = null;
  protected String rightNode = null, rightGroup = null,
                   rightType = null, rightArg = null;

  public TaskLinkModel(GroupManager leftGroupManager,
                       GroupManager rightGroupManager) {

     this.leftGroupManager = leftGroupManager;
     this.rightGroupManager = rightGroupManager;
     leftGroupManager.addChangeListener(this);
     rightGroupManager.addChangeListener(this);
     leftGroupManager.addRenameListener(this);
     rightGroupManager.addRenameListener(this);
  }

  public void reset(String nodeName, TaskLink[] input) {
     this.nodeName = nodeName;
     leftNode = rightNode = leftGroup = rightGroup =
        leftType = rightType = leftArg = rightArg = null;

     int r = data.size();
     data.removeAllElements();
     if ( r != 0 ) fireTableRowsDeleted(0,r-1);

     for(int i = 0; i < input.length; i++ ) {
        if ( input[i].referencesNode(nodeName) )
           data.addElement(new TaskLink(input[i]));
     }
     fireTableRowsInserted(0,data.size()-1);
     fireTableStructureChanged(); // swing bug? force redraw of table
  }

  public TaskLink[] getData() {
     TaskLink[] output = new TaskLink[data.size()];
     for(int i = 0; i < data.size(); i++ )
        output[i] = (TaskLink)data.elementAt(i);
     return output;
  }

  public void removeRows(int[] rows) {
     for(int i = 0; i < rows.length; i++ ) {
        data.removeElementAt(rows[i]-i);
        fireTableRowsDeleted(rows[i]-i,rows[i]-i);
     }
     fireTableStructureChanged(); // swing bug? force redraw of table
     fireChanged();
  }

  public void addNewRow()  {
     TaskLink link = getAddition();
     if ( link != null ) {
        data.addElement(link);
        int size = data.size();
        fireTableRowsInserted(size-1,size-1);
        fireTableStructureChanged(); // swing bug? force redraw of table
        fireChanged();
     }
  }

  protected TaskLink getAddition() {
     if ( leftNode == null || leftArg == null || leftGroup == null ||
          leftType == null || rightNode == null || rightArg == null ||
          rightGroup == null || rightType == null ) {
        JOptionPane.showMessageDialog(null,
           "Attempting to add improperly defined link","Error",
            JOptionPane.ERROR_MESSAGE);
        return null;
     }
     else if ( !rightType.equals(leftType) ) {
        JOptionPane.showMessageDialog(null,
           "Attempting to add improperly defined link\n" +
           "Left and right fact types must be the same","Error",
            JOptionPane.ERROR_MESSAGE);
        return null;
     }
     return new TaskLink(leftNode,leftGroup,leftArg,
                         rightNode,rightGroup,rightArg);
  }

  protected Vector getConditions() {
     Vector items = new Vector();
     Vector List;
     Fact f1;

     Hashtable input = leftGroupManager.getManagerData();
     Enumeration enum = input.elements();
     while( enum.hasMoreElements() ) {
        List = (Vector)enum.nextElement();
        for(int i = 0; i < List.size(); i++ ) {
           f1 = (Fact)List.elementAt(i);
           if ( !items.contains(f1.getId()) )
              items.addElement(f1.getId());
        }
     }

     input = rightGroupManager.getManagerData();
     enum = input.elements();
     while( enum.hasMoreElements() ) {
        List = (Vector)enum.nextElement();
        for(int i = 0; i < List.size(); i++ ) {
           f1 = (Fact)List.elementAt(i);
           if ( !items.contains(f1.getId()) )
              items.addElement(f1.getId());
        }
     }

     return items;
  }
  // ----------------------------------------------------------------------

  public int     getColumnCount()                 { return columnNames.length;}
  public boolean isCellEditable(int row, int col) { return false; }
  public int     getRowCount()                    { return data.size(); }
  public String  getColumnName(int col)           { return columnNames[col]; }

  public Object getValueAt (int row, int column)  {
     TaskLink link = (TaskLink)data.elementAt(row);
     switch(column) {
        case LEFT:
             return link.getLeftNode() + SEPARATOR + link.getLeftGroup() +
	            SEPARATOR + link.getLeftArg();
        case RIGHT:
             return link.getRightNode() + SEPARATOR + link.getRightGroup() +
	            SEPARATOR + link.getRightArg();
     }
     return null;
  }

  public void setValueAt(Object aValue, int row, int column)  {
     Core.ERROR(null,1,this);
  }

  public void linkNodeSelected(LinkNodeSelectionEvent e) {
    if ( e.getType().equals(TaskLinkBaseTreeModel.EFFECTS) ) {
       leftNode = e.getNodeName();
       leftGroup = e.getGroupName();
       leftArg = e.getFactId();
       leftType = e.getFactType();
       if ( rightNode != null && leftNode.equals(rightNode) )
          rightNode = rightGroup = rightArg = rightType = null;
    }
    else if ( e.getType().equals(TaskLinkBaseTreeModel.PRECONDITIONS) ) {
       rightNode = e.getNodeName();
       rightGroup = e.getGroupName();
       rightArg = e.getFactId();
       rightType = e.getFactType();
       if ( leftNode != null && rightNode.equals(leftNode) )
          leftNode = leftGroup = leftArg = leftType = null;
    }
  }

  public void stateChanged(ChangeEvent e) {
     // Preconditions/Postconditions have changed!
     // NEED to verify all links
     if ( e.getSource() == leftGroupManager ||
          e.getSource() == rightGroupManager ) {
        Vector items = getConditions();
        TaskLink link;
        for(int i = 0; i < data.size(); i++ ) {
           link = (TaskLink)data.elementAt(i);
           if ( link.getLeftNode().equals(nodeName) ) {
              if ( !items.contains(link.getLeftArg()) )
                 data.removeElementAt(i--);
           }
           else if ( link.getRightNode().equals(nodeName) ) {
             if ( !items.contains(link.getRightArg()) )
                 data.removeElementAt(i--);
           }
        }
        fireTableDataChanged();
     }
  }
  public void nameChanged(RenameEvent e) {
     // Preconditions/Postconditions ids have changed!
     // NEED to update all ids in link database

     TaskLink link;
     String prev = (String)e.getOriginal();
     String curr = (String)e.getCurrent();

     if ( prev.equals(curr) ) return;
     for(int i = 0; i < data.size(); i++ ) {
        link = (TaskLink)data.elementAt(i);
        if ( link.getLeftNode().equals(prev) )
           link.setLeftNode(curr);
        else if ( link.getLeftGroup().equals(prev) )
           link.setLeftGroup(curr);
        else if ( link.getLeftArg().equals(prev) )
           link.setLeftArg(curr);

        else if ( link.getRightNode().equals(prev) )
           link.setRightNode(curr);
        else if ( link.getRightGroup().equals(prev) )
           link.setRightGroup(curr);
        else if ( link.getRightArg().equals(prev) )
           link.setRightArg(curr);
     }
     if ( nodeName == null || nodeName.equals(prev) )     nodeName = curr;
     if ( leftNode != null && leftNode.equals(prev) )     leftNode = curr;
     if ( rightNode != null && rightNode.equals(prev) )   rightNode = curr;
     if ( leftGroup != null && leftGroup.equals(prev) )   leftGroup = curr;
     if ( rightGroup != null && rightGroup.equals(prev) ) rightGroup = curr;
     if ( leftArg != null && leftArg.equals(prev) )       leftArg = curr;
     if ( rightArg != null && rightArg.equals(prev) )     rightArg = curr;
     // Note: Fact types cannot be renamed
     fireTableDataChanged();
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
