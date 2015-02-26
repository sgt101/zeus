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
* DatabasePane.java
*
* The Viewer/Controller for the Database Import Pane
*****************************************************************************/

package zeus.ontology.database;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.ontology.*;
import zeus.generator.GeneratorModel;
import zeus.gui.help.*;
import zeus.gui.editors.*;
import zeus.gui.fields.*;

import zeus.ext.DbConnector;
import java.sql.*;


public class DatabasePane extends JPanel implements ActionListener
{
  static final String[] ERROR_MESSAGE = {
     "Please select a row before\ncalling this operation"
  };

  public DbConnector    MYDB     = null;
  public OntologyDb     ONTDB    = null;
  public GeneratorModel genModel = null;

  protected TablesTableModel  tablesModel;
  protected ColumnsTableModel columnsModel;
  protected ColumnsTableUI    columnsUI;
  protected TablesTableUI     tablesUI;
  protected Vector            tablesList = new Vector();

  protected JTextField       dbNameField;
  protected JTextField       dbDriverField;
  protected JTextField       dbUsernameField;
  protected JTextField       dbPasswordField;
  protected JButton          connBtn;

  public JButton   importBtn;
  public JTextArea messageArea;


  public DatabasePane(OntologyDb ontologyDb, GeneratorModel genmodel)
  {
    setLayout(new BorderLayout());
    ONTDB = ontologyDb;
    genModel = genmodel;

    JPanel dbParamsPanel = new JPanel();
    TitledBorder border = BorderFactory.createTitledBorder("Database Parameters");
    border.setTitlePosition(TitledBorder.TOP);
    border.setTitleJustification(TitledBorder.RIGHT);
    border.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border.setTitleColor(Color.blue);
    dbParamsPanel.setBorder(border);
    add(BorderLayout.NORTH, dbParamsPanel);

    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    dbParamsPanel.setLayout(gb);
    gbc = new GridBagConstraints();

    dbNameField = new JTextField(20);
    JLabel label1 = new JLabel("Database");
    label1.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label1.setToolTipText("Enter the path to your database, consult your JDBC driver documentation for syntax");
    gbc.insets = new Insets(4,8,4,0);
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(label1,gbc);
    dbParamsPanel.add(label1);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gb.setConstraints(dbNameField,gbc);
    dbParamsPanel.add(dbNameField);

    dbDriverField = new JTextField(20);
    JLabel label2 = new JLabel("JDBC Driver");
    label2.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label2.setToolTipText("Enter the full classname of the JDBC driver used to access your database");
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(4,8,4,0);
    gb.setConstraints(label2,gbc);
    dbParamsPanel.add(label2);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gbc.weightx = 1;
    gb.setConstraints(dbDriverField,gbc);
    dbParamsPanel.add(dbDriverField);

    dbUsernameField = new JTextField(20);
    JLabel label3 = new JLabel("Username");
    label3.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label3.setToolTipText("Provide this if you need to be authenticated when connecting to your database");
    gbc.gridwidth = 1;
    gbc.insets = new Insets(4,8,4,0);
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(label3,gbc);
    dbParamsPanel.add(label3);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gbc.weightx = 1;
    gb.setConstraints(dbUsernameField,gbc);
    dbParamsPanel.add(dbUsernameField);

    dbPasswordField = new JTextField(20);
    JLabel label4 = new JLabel("Password");
    label4.setFont(new Font("Helvetica",Font.PLAIN, 12));
    label4.setToolTipText("Provide this if you need to be authenticated when connecting to your database");
    gbc.gridwidth = 1;
    gbc.insets = new Insets(4,8,4,0);
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(label4,gbc);
    dbParamsPanel.add(label4);

    gbc.gridwidth = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(4,8,4,8);
    gbc.weightx = 1;
    gb.setConstraints(dbPasswordField,gbc);
    dbParamsPanel.add(dbPasswordField);

    connBtn = new JButton("Connect");
    connBtn.addActionListener(this);
    gbc.insets = new Insets(4,8,4,0);
    gbc.fill = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.EAST;
    gb.setConstraints(connBtn,gbc);
    dbParamsPanel.add(connBtn);

    // The Database Tables Panel - shows all the tables and columns in the connected database
    JPanel dbTablesPanel = new JPanel();
    dbTablesPanel.setLayout(new BorderLayout());

    columnsUI = new ColumnsTableUI(this);
    columnsModel = columnsUI.getModel();
    tablesUI = new TablesTableUI(columnsModel);
    tablesModel = tablesUI.getModel();

    dbTablesPanel.add(BorderLayout.WEST, tablesUI);
    dbTablesPanel.add(BorderLayout.EAST, columnsUI);

    importBtn = new JButton("Import Selected");
    importBtn.addActionListener(this);
    if (tablesList.size() == 0)
      importBtn.setEnabled(false);

    dbTablesPanel.add(new JLabel());
    dbTablesPanel.add(BorderLayout.SOUTH, importBtn);

    TitledBorder border2 = BorderFactory.createTitledBorder("Database Schema");
    border2.setTitlePosition(TitledBorder.TOP);
    border2.setTitleJustification(TitledBorder.RIGHT);
    border2.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border2.setTitleColor(Color.blue);
    dbTablesPanel.setBorder(border2);
    add(BorderLayout.CENTER, dbTablesPanel);

    // The Database Messages Panel - prints results of JBDC operations
    JPanel dbMessagesPanel = new JPanel();
    TitledBorder border3 = BorderFactory.createTitledBorder("Messages");
    border3.setTitlePosition(TitledBorder.TOP);
    border3.setTitleJustification(TitledBorder.RIGHT);
    border3.setTitleFont(new Font("Helvetica", Font.BOLD, 12));
    border3.setTitleColor(Color.blue);
    dbMessagesPanel.setBorder(border3);

    dbMessagesPanel.setLayout(new GridLayout(1,1));

    messageArea = new JTextArea(8,200);
    messageArea.setEditable(false);
    messageArea.setLineWrap(true);
    messageArea.setBackground(Color.white);

    JScrollPane scrollPane = new JScrollPane(messageArea);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(300,160));
    dbMessagesPanel.add(scrollPane);
    add(BorderLayout.SOUTH, dbMessagesPanel);

    initParams();
  }

  private void initParams()
  {
    dbNameField.setText("jdbc:mysql://steerpike:3306/test");
    dbDriverField.setText("org.gjt.mm.mysql.Driver");
  }

  private void errorMsg(int tag) {
    JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                  "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void actionPerformed(ActionEvent e) 
  {
    Object src = e.getSource();
    if (src == connBtn)
      makeDbConnection();
    else if (src == importBtn)
      importTable();
  }


  private void makeDbConnection()
  {
    String dbName     = null;
    String dbDriver   = null;
    String dbUsername = null;
    String dbPassword = null;

    dbName     = dbNameField.getText();
    dbDriver   = dbDriverField.getText();
    dbUsername = dbUsernameField.getText();
    dbPassword = dbPasswordField.getText();

    // store JDBC parameters - need to write them into the jdbc.data file
    if (genModel != null)
    {
      genModel.dbName     = dbName;
      genModel.dbDriver   = dbDriver;
      genModel.dbUsername = dbUsername;
      genModel.dbPassword = dbPassword;
    }
    MYDB = new DbConnector(dbUsername, dbPassword, dbDriver, dbName);

    messageArea.append("** Reading table information from " + dbName);
    try
    {
      //  populate the known tables table
      DatabaseMetaData dmeta = MYDB.getConnection().getMetaData();
      String[] types = {"TABLE"};
      ResultSet rsTables = dmeta.getTables(null,null,null,types);
      while(rsTables.next())
      {
        String strTable = rsTables.getString(3);
        //System.out.println("> " + strTable);
        tablesList.add(strTable);
      }
      tablesModel.setValues(tablesList);
      MYDB.close();
      messageArea.append(" OK \n");
    }
    catch(SQLException e)
    {
      messageArea.append("\nSQL Exception: " + e + "\n");
    }
  }


  private void importTable()
  {
    if (columnsUI.getTable().getSelectedRow() == -1 )
    {
       messageArea.append("Error - no selected columns to import\n");
       return;
    }
    String[][] selectedrows = columnsUI.getSelectedRows();

    String importEntityName = tablesUI.getSelectedRow();
    TreeNode parent = ONTDB.getRoot();
    boolean ok = ONTDB.addNamedChildFact(parent, importEntityName);
    if (ok)
      messageArea.append("Created new entity: " + importEntityName + "\n");
    else
      messageArea.append("Entity: " + importEntityName + " already exists\n");

    messageArea.append("Importing: " + selectedrows.length + " attribute(s) \n");

    // create a new attribute for each imported column
    for(int i = 0; i < selectedrows.length; i++ )
    {
      String cName = selectedrows[i][0];
      String cType = selectedrows[i][1];
      String zType = selectedrows[i][2];
     // System.out.println("> " + cName + " " + cType + " " + zType);
      ONTDB.addNewAttribute(importEntityName, cName, zType);
    }

    messageArea.append("Import successful. \n");
    messageArea.append("The imported fact and its attributes are now in the fact tree \n\n");
  }
}
