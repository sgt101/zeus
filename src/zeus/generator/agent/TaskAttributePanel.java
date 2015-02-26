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

public class TaskAttributePanel extends JPanel {

  private java.util.List restrictions;

  public TaskAttributePanel(AgentGenerator generator,
			    GeneratorModel genmodel,
			    AgentEditor editor,
			    AgentDescription agent, boolean isVar,
			    OntologyDb ontologyDb, String label) {
    super();
    init(generator, genmodel, editor, agent, isVar, ontologyDb, label);
  }

  private void init(AgentGenerator generator,
		    GeneratorModel genmodel,
		    AgentEditor editor,
		    AgentDescription agent, boolean isVar,
		    OntologyDb ontologyDb, String label) {

    //Create layout
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    setBackground(java.awt.Color.lightGray);

    //Create border
    makeBorder(label);

    //Create task table
    String[] tasks = agent.getTasks();
    TaskModel taskModel = new TaskModel(tasks, genmodel);
    taskModel.addChangeListener(editor);
    taskModel.setReadOnly(TaskModel.TASK);
    JTable taskTable = makeTaskTable(taskModel);
    configureTable(taskTable);

    //Create fact table
    FactModel factModel = new FactModel(ontologyDb, new AttributeModel(),
					isVar, 0, new Fact[0]);
    factModel.setEditable(false);
    TableCellEditor instanceEditor =
      new FactPanel(ontologyDb, editor, new Fact[0],
		    isVar, label).newInstanceEditor();
    JTable factTable = makeFactTable(factModel, instanceEditor);
    configureTable(factTable);
    
    //Create attribute table
    AttributeModel attModel = factModel.getAttributeModel();
    attModel.enableRestrictions();
    attModel.setWriteable(AttributeModel.RESTRICTION);
    attModel.setReadOnly(AttributeModel.VALUE);
    JTable attTable = makeAttributeTable(attModel);
    configureTable(attTable);

    //Add elements to panel
    makeScrollPane(taskTable, gridBagLayout);
    makeScrollPane(factTable, gridBagLayout);
    makeScrollPane(attTable, gridBagLayout);

    //Setup listeners
    setupListeners(taskTable, taskModel, factTable, factModel,
		   attTable, attModel, genmodel, agent);

    attModel.addChangeListener(editor);
  }

  private void makeScrollPane(JTable table, GridBagLayout layout) {

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setMinimumSize(new Dimension(160, 80));
    scrollPane.setPreferredSize(new Dimension(200, 80));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);

    layout.setConstraints(scrollPane, gbc);
    add(scrollPane);
  }

  private void setupListeners(JTable taskTable, TaskModel taskModel,
			      JTable factTable, FactModel factModel,
			      JTable attTable, AttributeModel attModel,
			      GeneratorModel genModel, AgentDescription agent){

    ListSelectionListener listener =
      new TaskAttributeListener(taskTable, taskModel, factTable, factModel,
				attTable, attModel, genModel, agent);
    taskTable.getSelectionModel().addListSelectionListener(listener);
    factTable.getSelectionModel().addListSelectionListener(listener);
    attTable.getSelectionModel().addListSelectionListener(listener);

    restrictions = ((TaskAttributeListener)listener).getRestrictions();
  }

  private void configureTable(JTable table) {
    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void makeBorder(String label) {
    TitledBorder border = (BorderFactory.createTitledBorder(label));
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    border.setTitleColor(java.awt.Color.blue);
    setBorder(border);
  }

  private JTable makeTaskTable(TaskModel model) {

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;

    column = new TableColumn(TaskModel.TASK,12);
    column.setHeaderValue(model.getColumnName(TaskModel.TASK));
    tm.addColumn(column);

    column = new TableColumn(TaskModel.TYPE,24);
    column.setHeaderValue(model.getColumnName(TaskModel.TYPE));
    tm.addColumn(column);

    JTable taskTable = new JTable(model, tm);
    return taskTable;
  }

  private JTable makeFactTable(FactModel factModel, TableCellEditor editor) {

    TableColumnModel factTm = new DefaultTableColumnModel();

    TableColumn column = new TableColumn(FactModel.TYPE,12);
    column.setHeaderValue(factModel.getColumnName(FactModel.TYPE));
    factTm.addColumn(column);

    column = new TableColumn(FactModel.INSTANCE,24);
    column.setHeaderValue(factModel.getColumnName(FactModel.INSTANCE));
    factTm.addColumn(column);
    
    JTable factTable = new JTable(factModel, factTm);
    return factTable;
  }

  private JTable makeAttributeTable(AttributeModel model) {

    TableColumnModel attTm = new DefaultTableColumnModel();

    TableColumn column = new TableColumn(AttributeModel.ATTRIBUTE,12);
    column.setHeaderValue(model.getColumnName(AttributeModel.ATTRIBUTE));
    attTm.addColumn(column);

    column = new TableColumn(AttributeModel.VALUE, 24);
    column.setHeaderValue(model.getColumnName(AttributeModel.VALUE));
    attTm.addColumn(column);

    column = new TableColumn(AttributeModel.RESTRICTION, 12);
    column.setHeaderValue(model.getColumnName(AttributeModel.RESTRICTION));
    attTm.addColumn(column);

    JTable attTable = new JTable(model, attTm);
    return attTable;
  }

  public java.util.List getRestrictions() {
    return restrictions;
  }
}
