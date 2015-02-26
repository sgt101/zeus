package zeus.generator.agent;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.help.*;
import zeus.gui.fields.*;
import zeus.generator.*;
import zeus.generator.util.*;
import zeus.gui.editors.*;

/**
 * Handle all events arising from the TaskAttributePanel, pricipally
 * selecting a different task or fact row.
 */
public class TaskAttributeListener implements ListSelectionListener {

  private JTable taskTable;
  private JTable factTable;
  private JTable attTable;
  private TaskModel taskModel;
  private FactModel factModel;
  private AttributeModel attModel;
  private GeneratorModel genModel;
  private java.util.List restrictions;

  private String currentTask;

  public TaskAttributeListener(JTable taskTable, TaskModel taskModel,
			       JTable factTable, FactModel factModel,
			       JTable attTable, AttributeModel attModel,
			       GeneratorModel genModel,AgentDescription agent){

    this.taskTable = taskTable;
    this.taskModel = taskModel;
    this.factTable = factTable;
    this.factModel = factModel;
    this.attTable = attTable;
    this.attModel = attModel;

    this.genModel = genModel;

    restrictions = agent.getRestrictions();
    if(restrictions == null) {
      restrictions = new Vector();
    }
  }

  /**
   * Event handling interface.
   */
  public void valueChanged(ListSelectionEvent e) {

    Object source = e.getSource();
    if(source == taskTable.getSelectionModel()) {
      taskSelected();
    }
    else if(source == factTable.getSelectionModel()) {
      factSelected();
    }
    else if(source == attTable.getSelectionModel()) {
    }
  }

  /**
   * Fired when a task row is selected. Saves the attribute
   * restrictions currently active, then loads the new fact table
   * associated with this task.
   */
  private void taskSelected() {

    if(attTable.getSelectedRow() >= 0) {
      saveAttributeRestrictions();
    }

    int row = taskTable.getSelectedRow();

    if(row < 0) {
      return;
    }

    String taskName = (String)taskModel.getValueAt(row, TaskModel.TASK);
    currentTask = taskName;
    String taskID = genModel.reverseTaskNameLookup(taskName);

    AbstractTask aTask = genModel.getTask(taskID);

    if(!Task.class.isAssignableFrom(aTask.getClass())) {
      //Rulebase tasks cannot be interpreted;
      factModel.reset(new Fact[0]);
      return;
    }

    Task task = (Task)aTask;
    
    Fact[] input = task.getPreconditions();
    Fact[] output = task.getPostconditions();

    Fact[] newFacts = new Fact[input.length + output.length];

    for(int index = 0 ; index < input.length ; index++) {
      newFacts[index] = input[index];
    }
    for(int index = input.length ; index < newFacts.length ; index++) {
      newFacts[index] = output[index - input.length];
    }

    factModel.reset(newFacts);
    attModel.reset(null);
  }

  /**
   * Fired when a fact row is selected. Saves current attribute
   * restrictions, loads the attribute table associated with the new
   * fact, and loads the restrictions into it.
   */
  private void factSelected() {

    if(attTable.getSelectedRow() >= 0) {
      saveAttributeRestrictions();
    }

    int row = factTable.getSelectedRow();
    if(row < 0) {
      return;
    }

    Fact fact = factModel.getData()[row];

    attModel.reset(fact);
    loadAttributeRestrictions();
  }

  /**
   * Save any attribute restrictions in the current attribute
   * table. Existing design of AttributeModel made it a serious
   * problem to save the restriction information in there, so it is
   * extracted and saved here.
   */
  private void saveAttributeRestrictions() {

    for(int row = 0 ; row < attModel.getRowCount() ; row++) {
      String value = (String)attModel.getValueAt(row,
						 AttributeModel.RESTRICTION);

      if(value != null) {
	String factInstance = attModel.getData().getId();

	String attName = (String)attModel.getValueAt(row,
						     AttributeModel.ATTRIBUTE);

	Restriction item = new Restriction(currentTask, factInstance,
					   attName, value);

	for(int index = 0 ; index < restrictions.size() ; index++) {
	  Restriction element = (Restriction)restrictions.get(index);
	  if(element.sameTarget(item)) {
	    item = (Restriction)restrictions.remove(index);
	    item.setRestriction(value);
	    break;
	  }
	}

	//If there is no restriction, don't insert restriction item
	if(item.getRestriction() == null ||
	   item.getRestriction().length() == 0) {
	  return;
	}

	restrictions.add(item);
      }
    }
  }

  /**
   * Restore and restrictions that are associated with the current
   * attribute table.
   */
  private void loadAttributeRestrictions() {
    int factRow = factTable.getSelectedRow();
    String factInstance = (String)factModel.getValueAt(factRow,
						       FactModel.INSTANCE);

    for(int row = 0 ; row < attModel.getRowCount() ; row++) {
      String attName = (String)attModel.getValueAt(row,
						   AttributeModel.ATTRIBUTE);

      Restriction item = new Restriction(currentTask, factInstance,
						 attName, "");

      for(Iterator i = restrictions.iterator() ; i.hasNext() ; ) {
	Restriction element = (Restriction)i.next();

	if(element.sameTarget(item)) {
	  attModel.setRestriction(element.getRestriction(), row);
	  break;
	}
      }
    }
  }

  /**
   * Retrieve the list of restrictions. Pricipally used for saving them.
   */
  public java.util.List getRestrictions() {
    return restrictions;
  }

}
