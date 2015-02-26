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
* ColumnsTableModel.java
*
* The local model for the Tables Table
*****************************************************************************/

package zeus.ontology.database;

import java.util.*;
import java.sql.*;
import javax.swing.event.*;
import javax.swing.table.*;

import zeus.concepts.*;
import zeus.ontology.*;
import zeus.gui.editors.*;
import zeus.ext.DbConnector;


public class ColumnsTableModel extends AbstractTableModel
{
  protected String[]     columnNames = { "Column", "Type", "Zeus Equivalent" };
  protected String[][]   data = null;
  protected boolean[][]  validityInfo = null; // might use this for unsupported types
  protected DatabasePane DBPane = null;
  protected DbConnector  MYDB = null;

  public ColumnsTableModel(DatabasePane parent)
  {
    DBPane = parent;
    // nothing to refresh as no table is selected yet
  }

  void refresh() {
    // refresh data from source
    fireTableStructureChanged();
  }

  public int      getColumnCount()     { return columnNames.length; }
  public int      getRowCount()        { return (data != null) ? data.length : 0; }
  public String[] getRow(int row)      { return data[row]; }

  public boolean isCellEditable(int r, int c) { return false;}
  public String  getColumnName(int c)         { return columnNames[c]; }

  public boolean isValidEntry(int r, int c)   { return validityInfo[r][c]; }

  public Object  getValueAt(int r, int c)     { return data[r][c]; }


  public String mapTypeToOntology(int sqlType)
  {
    if (sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR ||
        sqlType == Types.CLOB || sqlType == Types.CHAR)
      return DBPane.ONTDB.STRING;
    else if (sqlType == Types.BIT || sqlType == Types.VARBINARY)
      return DBPane.ONTDB.BOOLEAN;
    else if (sqlType == Types.INTEGER || sqlType == Types.SMALLINT || sqlType == Types.TINYINT)
      return DBPane.ONTDB.INTEGER;
    else if (sqlType == Types.DECIMAL || sqlType == Types.DOUBLE || sqlType == Types.FLOAT)
      return DBPane.ONTDB.REAL;
    else if (sqlType == Types.DATE)
      return DBPane.ONTDB.DATE;
    else if (sqlType == Types.TIME || sqlType == Types.TIMESTAMP )
      return DBPane.ONTDB.TIME;
    return "Not supported";
  }

  public void setValues(ResultSet rs, int count)
  {
    try
    {
      //ResultSetMetaData rsmd = rs.getMetaData();
      data = new String[count][3];
      int i = 0;
      while(rs.next())
      {
        String cName = rs.getString(4);
        data[i][0] = cName;
        String cType = rs.getString(6);
        data[i][1] = cType;
        // determine most appropriate type
        String zType = mapTypeToOntology(rs.getInt(5));
        data[i][2] = zType;
        // System.out.println("> " + cName + " " + cType + " " + zType);
        i++;
      }
    }
    catch(SQLException e)
    {
      DBPane.messageArea.append("\nSQL Exception: " + e + "\n");
    }
  }

  public void refreshColumns(String dbTableName)
  {
    MYDB = DBPane.MYDB;
    // get list of columns from named table
    DBPane.messageArea.append("Getting columns of " + dbTableName);
    try
    {
      DatabaseMetaData dbmd = MYDB.getConnection().getMetaData();
      ResultSet rs = dbmd.getColumns(null, null, dbTableName, "%");

      int c = 0;
      while (rs.next()) c++;
      rs = dbmd.getColumns(null, null, dbTableName, "%");

      setValues(rs, c); // convert resultSet into data[][]
      MYDB.close();
      DBPane.messageArea.append(" .. OK\n");
    }
    catch(SQLException e)
    {
      DBPane.messageArea.append("\nSQL Exception: " + e + "\n");
    }
    refresh();
  }

  String[][] getRows(int[] input)
  {
    String[][] result = new String[input.length][columnNames.length];
    for(int i = 0; i < result.length; i++ )
      for(int j = 0; j < result[i].length; j++)
        result[i][j] = data[input[i]][j];
    return result;
  }
}
