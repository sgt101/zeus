import java.sql.*;
import java.util.*;

import zeus.agents.*;
import zeus.concepts.*;
import zeus.actors.*;

public class NE_ExternalDbInterface implements ZeusExternal, Runnable {
  AgentContext context;
  String agentName;
  Thread thread;
  java.sql.Connection con;

  public NE_ExternalDbInterface() { }

  public void open(){
    try {
       Class.forName("com.imaginary.sql.msql.MsqlDriver");
       String url = "jdbc:msql://132.146.209.181:1114/NMDB";
       con = DriverManager.getConnection(url," "," ");
    }
    catch(Exception e ){
       e.printStackTrace();
    }
  }

  public void close(){
    try {
     con.close();
     thread = null;
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public void exec(AgentContext context){
    this.context = context;
    agentName = context.whoami();
    thread = new Thread(this);
    thread.start();
  }

  public void run() {
    Vector entries = null;
    while (true) {
      try {

        entries = getEntries(agentName);
	addToResourceDb(entries);
        thread.sleep(60*1000);
      }
      catch(InterruptedException ie){
        ie.printStackTrace();
        return;
      }
    }//  end while loop

  }

  protected Vector getEntries(String agentName){
     String query = "SELECT * FROM NMTABLE WHERE node = " + "'" +agentName + "'";
     String FACT_NAME = "NetworkElementStatus";
     Vector nesList = new Vector();
     Statement stmt = null;
     ResultSet rs = null;
     String nni_port, problem_status;
     int vcid;
     float trunk_utilisation;
     Fact nesFact;

     try {
       stmt = con.createStatement();

       rs = stmt.executeQuery(query);

       while( rs.next() ) {
         nni_port = rs.getString("nni_port");
         problem_status = rs.getString("problem_status");
         vcid = rs.getInt("vcid");
         trunk_utilisation = rs.getFloat("trunk_utilisation");
         nesFact = context.OntologyDb().getFact(Fact.FACT, FACT_NAME);
         nesFact.setValue("node",agentName);
         nesFact.setValue("nni_port",nni_port);
         nesFact.setValue("vcid",vcid);
         nesFact.setValue("problem_status",problem_status);
         nesFact.setValue("trunk_utilisation",trunk_utilisation);
         nesList.addElement(nesFact);
       }
     }
     catch(Exception e){
       e.printStackTrace();
     }
     finally {
      try {
       stmt.close();
       rs.close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return nesList;
     }
  }

  public void addToResourceDb(Vector entries){
    context.ResourceDb().add(entries);
  }




}