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



/***************************************************************************
* TaskNodePanel.java
*
* Panel through which task attributes are entered
***************************************************************************/

package zeus.generator.task;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;
import zeus.generator.*;
import zeus.generator.event.*;
import zeus.generator.util.*;
import zeus.gui.fields.*;
import zeus.gui.help.*;

public class GroupManager extends FactModel
                          implements ActionListener, FactModelListener {

     protected static int           group_count = 0;
     protected DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
     protected String               selectedGroup;
     protected Hashtable            table = new Hashtable();
     protected OrderedHashtable     backup = new OrderedHashtable();

     public GroupManager(OntologyDb ontologyDb, AttributeModel attributeModel,
                          boolean isVariable, int type, Fact[] input) {
        super(ontologyDb,attributeModel,isVariable,type,input);
     }

     public DefaultComboBoxModel getComboBoxModel() { return comboModel; }

     public void resetManager(Hashtable input, Fact[] backup_input) {
        resetManager(input);
        for(int i = 0; i < backup_input.length; i++ )
           backup.put(backup_input[i].getId(),backup_input[i]);
     }
     public void resetManager(Hashtable input) {
        backup.clear();
        selectedGroup = null;
        this.table = input;
        if ( comboModel.getSize() > 0 ) comboModel.removeAllElements();
        String group;
        Enumeration enum = table.keys();
        while( enum.hasMoreElements() ) {
           group = (String)enum.nextElement();
           comboModel.addElement(group);
           if ( selectedGroup == null )
              selectedGroup = group;
        }
        if ( selectedGroup != null ) {
           comboModel.setSelectedItem(selectedGroup);
        }
     }

     public Hashtable getManagerData() {
        // first do deselect operation to save current state
        if ( selectedGroup != null ) {
           Fact[] input = super.getData();
           table.put(selectedGroup,factVector(input));
        }
        return table;
     }

     public void actionPerformed(ActionEvent e) {
        String item = (String)comboModel.getSelectedItem();
        Fact[] input;
        Vector List;
        // first do deselect operation
        if ( selectedGroup != null ) {
           input = super.getData();
           table.put(selectedGroup,factVector(input));
        }
        // then do select op
        List = (Vector)table.get(item);
        if ( List == null && selectedGroup != null ) {
           // assume we are renaming
           List = (Vector)table.get(selectedGroup);
           table.remove(selectedGroup);
           table.put(item,List);
           String prevSelection = selectedGroup;
           selectedGroup = null;
           comboModel.removeElement(prevSelection);
           comboModel.addElement(item);
           comboModel.setSelectedItem(item);
           fireNameChanged(comboModel,prevSelection,item);
        }
        else if ( List != null )
           super.reset(factArray(List));
        selectedGroup = item;
     }

     public void factModelChanged(FactModelEvent e) {
        Fact f1 = e.getFact();
        Fact f2;
        Fact[] input;
        Vector List;
        String value, value1, prev, curr;
        String[] attribute;
        Enumeration enum;

        switch( e.getEventType() ) {
           case FactModelEvent.FACT_ADDED:
                enum = table.elements();
                while( enum.hasMoreElements() ) {
                   List = (Vector)enum.nextElement();
                   List.addElement(new Fact(f1));
                }
                backup.put(f1.getId(),f1);
                input = new Fact[1];
                input[0] = new Fact(f1);
                super.addRows(input);
                break;

           case FactModelEvent.FACT_REMOVED:
                enum = table.elements();
                while( enum.hasMoreElements() ) {
                   List = (Vector)enum.nextElement();
                   for(int i = 0; i < List.size(); i++ ) {
                      f2 = (Fact)List.elementAt(i);
                      if ( f1.getId().equals(f2.getId()) )
                         List.removeElementAt(i--);
                   }
                }
                backup.remove(f1.getId());
                input = new Fact[1];
                input[0] = f1;
                super.removeRows(input);
                break;

           case FactModelEvent.FACT_ID_CHANGED:
                prev = e.getPreviousId();
                curr = e.getCurrentId();

                enum = table.elements();
                while( enum.hasMoreElements() ) {
                   List = (Vector)enum.nextElement();
                   for(int i = 0; i < List.size(); i++ ) {
                      f2 = (Fact)List.elementAt(i);
                      if ( prev.equals(f2.getId()) )
                         f2.setId(f1.ID());
                      attribute = f2.listAttributes();
                      for(int j = 0; j < attribute.length; j++ ) {
                         value = f2.getValue(attribute[j]);
                         value1 = Misc.substitute(value,prev,curr);
                         if ( !value1.equals(value) )
                            f2.setValue(attribute[j],value1);
                      }
                   }
                }

                input = super.getData();
                for(int i = 0; i < input.length; i++ )  {
                   if ( input[i].getId().equals(prev) )
                      input[i].setId(f1.ID());
                      attribute = input[i].listAttributes();
                      for(int j = 0; j < attribute.length; j++ ) {
                         value = input[i].getValue(attribute[j]);
                         value1 = Misc.substitute(value,prev,curr);
                         if ( !value1.equals(value) )
                            input[i].setValue(attribute[j],value1);
                      }

                }
                backup.reKey(prev,curr,f1);
                super.reset(input);
                fireNameChanged(f1,prev,curr);
                break;
        }
     }

     protected Fact[] factArray(Vector input) {
        Fact[] output = new Fact[input.size()];
        for(int i = 0; i < output.length; i++ )
           output[i] = (Fact)input.elementAt(i);
        return output;
     }

     protected Vector factVector(Fact[] input) {
        Vector output = new Vector();
        for(int i = 0; i < input.length; i++ )
           output.addElement(input[i]);
        return output;
     }

     public void newGroup() {
        String name = "group" + (group_count++);
        while( table.containsKey(name) )
           name = "group" + (group_count++);
        Vector List = new Vector();
        Enumeration enum = backup.elements();
        while( enum.hasMoreElements() )
           List.addElement(new Fact((Fact)enum.nextElement()));
        table.put(name,List);
        comboModel.addElement(name);
        comboModel.setSelectedItem(name);
        fireChanged();
     }

     public void deleteGroup() {
        if ( selectedGroup == null ) return;
        if ( comboModel.getSize() <= 1 ) {
           JOptionPane.showMessageDialog(null,
              "Cannot delete group.\nAt least one group must exist.",
              "Error", JOptionPane.ERROR_MESSAGE);
           return;
        }

        String prevSelection = selectedGroup;
        selectedGroup = null;
        comboModel.removeElement(prevSelection);
        table.remove(prevSelection);
        comboModel.setSelectedItem(comboModel.getElementAt(0));
        fireChanged();
     }

     public void copyGroup() {
        if ( selectedGroup == null ) return;
        Fact[] input = super.getData();
        Vector ListCopy = new Vector();
        for(int i = 0; i < input.length; i++ )
           ListCopy.addElement(new Fact(input[i]));

	String name = "group" + (group_count++);
        while( table.containsKey(name) )
           name = "group" + (group_count++);
        table.put(name,ListCopy);

        comboModel.addElement(name);
        comboModel.setSelectedItem(name);
        fireChanged();
     }
}