/**
 * This class is the interface between the agent and its underlying database
 * Ultimately its role will be to encapsulate the EJB server
 */

package zeus.ext;

import java.sql.*;
import java.util.*;

import zeus.concepts.*;


public class DbConnector implements java.io.Serializable
{
	// The JDBC connection
	protected Connection dbConn = null;

  // The JDBC properties to use
  protected String username = null;
  protected String password = null;
  protected String JDBCdriverName = null;
  protected String JDBCconnectionName = null;


  public DbConnector(String user, String pw, String driver, String conn)
  {
    username = user;
    password = pw;
    JDBCdriverName = driver;
    JDBCconnectionName = conn;
  }


	/**
	 * Connect to the db
	 * @return true if connection is successful, false otherwise
	 */

	public boolean connect() throws SQLException
  {
		try
    {
	    java.util.Properties props = new java.util.Properties();
      if (username != null) props.put("user", username);
		  if (password != null) props.put("password", password);
      if (JDBCdriverName == null || JDBCconnectionName == null)
        throw new SQLException("JDBC connection attempted without driver and connection being specified");
      Class.forName(JDBCdriverName).newInstance();
    	dbConn = DriverManager.getConnection(JDBCconnectionName, props);
		}
    catch(Exception e)
    {
		  e.printStackTrace();
		  throw new SQLException("Cannot create database driver... exiting");
		}
		return true;
	}

	/**
	 * Get the connection object for the database
	 * This is used to initiate calleable statements for PL/SQL calls
	 * @return the connection object to the db
	 */

	public Connection getConnection() throws SQLException {
		if(dbConn == null || dbConn.isClosed())
			connect();
		return dbConn;
	}


	/**
	 * Get a statement associated with the connection
	 * @return a statement object or null if none can be created
	 */

	public Statement getStatement() throws SQLException {
		if(dbConn == null || dbConn.isClosed())
			connect();
		return dbConn.createStatement();
	}

	/**
	 * Check to see if an active connection exists
	 * @return true if a connection is live, false otherwise
	 */
	 
	public boolean activeConnection() throws SQLException {
		if(dbConn == null || dbConn.isClosed())
			return false;
		else
			return true;
	}
	
	/**
	 * Close the connection to the db
	 */
	 
	public void close() {
		try {
			if(dbConn != null && !dbConn.isClosed())
				dbConn.close();
		} catch(SQLException e) {
			System.out.println("SQL exception thrown while trying to close a db connection");
		}
	}
	
	/**
	 * Execute an sql query
	 * @param query the sql query to execute
	 * @return the ResultSet from the query
	 */
	 
	public ResultSet sqlQuery(String query) throws SQLException {
		if(!activeConnection())
			connect();
		Statement stmt = dbConn.createStatement();
		return stmt.executeQuery(query);
	}
	
	/**
	 * Execute an SQL command
	 * @param command the sql command to execute
	 */
	 
	public void sqlCommand(String command) throws SQLException {
		if(!activeConnection())
			connect();
		Statement stmt = dbConn.createStatement();
		stmt.execute(command);
	}

  // probably need a utility method that can convert a SQL resultSet into a fact

  /** The CRUD methods ***************************************************/

  /*public boolean create(Fact f)
  {
    // given an agent-created fact, create an equivalent in the database
    return false;
  }

  public Fact read(String factKey, String factTable)
  {
    // given the key and table of some data convert it back to a Fact
    return null;
  }

  public boolean update(Fact f)
  {
    // given a fact, write it to the database
    return false;
  }

  public boolean destroy(Fact f)
  {
    // given a fact, delete it from the database
    return false;
  } */


  public static void main(String[] Args)
  {
    System.out.println("Starting DbTest");
   //riverManager.getConnection("jdbc:oracle:oci8:@(description=(address=(host=tb-toledo)(protocol=tcp)(port=1521))(connect_data=(sid=visitor)))",
//      "database", "tiger");
 // DbConnector dbc = new DbConnector("jaron", null, "org.gjt.mm.mysql.Driver",
                                 //     "jdbc:mysql://steerpike:3306/test");

    DbConnector dbc = new DbConnector ("database","tiger", "oracle.jdbc.driver.OracleDriver",
                    "jdbc:oracle:oci8:@(description=(address=(host=tb-toledo)(protocol=tcp)(port=1521))(connect_data=(sid=visitor)))"); 
    try
    {
      ResultSet rs = dbc.sqlQuery("select count(*) from table1");
      while(rs.next() != false)
      {
        int val = rs.getInt("count(*)");
        System.out.println("result = " + val);
      }
    }
    catch(SQLException e)
    {
      System.out.println("Ooops: " + e);
    }
  }
}
