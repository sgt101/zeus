import java.sql.*;

public class PopulateDB  {


  public PopulateDB() {
    Connection con = null;
    Statement stmt = null;
    try {
       Class.forName("com.imaginary.sql.msql.MsqlDriver");
       String url = "jdbc:msql://132.146.209.181:1114/NMDB";
       con = DriverManager.getConnection(url," "," ");
       stmt = con.createStatement();
       stmt.executeUpdate("DROP TABLE NMTABLE ");
       stmt.executeUpdate("CREATE TABLE NMTABLE ("
                          + "node char(20),  nni_port char(20), vcid int, "
                          + "problem_status char(20), trunck_utilisation real)" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('ScoobyAgent','NNI',2002,'yellow',85 )" );

       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('BugsAgent','NNI',2002,'yellow',85 )" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('RenAgent','NNI',2002,'yellow',85 )" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('RenAgent','NNI',3021,'yellow',85 )" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('ShaggyAgent','NNI',3021,'yellow',85 )" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('BugsAgent','NNI',3021,'yellow',85 )" );

       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('RenAgent','NNI',3219,'yellow',85 )" );

       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('BugsAgent','NNI',3219,'yellow',85 )" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('ShaggyAgent','NNI',3219,'yellow',85 )" );
       stmt.executeUpdate("INSERT INTO NMTABLE "
                          + "(node, nni_port,  vcid, problem_status, trunck_utilisation)"
                          + "VALUES('ScoobyAgent','NNI',3219,'yellow',85 )" );


    }
    catch(Exception e ){
       e.printStackTrace();
    }
    finally {
      try{

      }
      catch(Exception e){
        try{
         con.close();
         stmt.close();
        }
        catch(Exception ie){
          ie.printStackTrace();
        }

      }


    }

  }

 public static void main(String[] ags){
   new PopulateDB();
 }


}