package zeus.ontology.service;

import zeus.actors.AgentContext;
import zeus.util.SystemProps;
import zeus.concepts.Performative;

import java.io.*;

/**
 * Class to receive and write out the various elements of a service
 * description.
 */
public class ServiceDescriptionReceiver {

  private AgentContext context;

  public ServiceDescriptionReceiver(AgentContext context) {
    
    this.context = context;
  }

  public void serviceProfileReceived(Performative msg) {
    
    String serviceDir = SystemProps.getProperty("http_root");
    
    String profile = msg.getContent();
    profile = profile.substring(profile.indexOf(":serviceProfile")
				+ ":serviceProfile".length()).trim();
    
    profile = profile.substring(profile.indexOf("<"),
				profile.lastIndexOf(">") + 1);
    
    int nameBegin =
      profile.toLowerCase().indexOf("service:serviceprofile ");
    
    nameBegin = profile.toLowerCase().indexOf("rdf:id", nameBegin);
    nameBegin = profile.indexOf("\"", nameBegin) + 1;

    int nameEnd = profile.indexOf("\"", nameBegin);

    String name = profile.substring(nameBegin, nameEnd);

    File profileDir = new File(serviceDir + "\\services\\classes\\" +name);
    File profileFile = new File(profileDir.getPath() +
				"\\" + name + "Profile.daml");

    profileDir.mkdirs();
    write(profileFile, profile);
    
    //Send service profile to DF
    msg.setReceiver("df");
    msg.setContent(msg.getContent());
    msg.setInReplyTo(zeus.agents.Facilitator.SERVICE_KEY);
    context.MailBox().sendMsg(msg);
  }

  public void serviceInstanceReceived(Performative msg) {

    String serviceDir = SystemProps.getProperty("http_root");

    String description = msg.getContent();
    description = description.substring(description.indexOf(":serviceInstance")
					+ ":serviceInstance".length()).trim();

    description = description.substring(description.indexOf("<"),
					description.lastIndexOf(">") + 1);

    int nameBegin = description.toLowerCase().indexOf("service:service ");
    nameBegin = description.toLowerCase().indexOf("rdf:id", nameBegin);
    nameBegin = description.indexOf("\"", nameBegin) + 1;
    
    int nameEnd = description.indexOf("\"", nameBegin);

    String name = description.substring(nameBegin, nameEnd);

    File instanceDir = new File(serviceDir + "\\services\\instances\\" +
				name);
    File instanceFile = new File(instanceDir.getPath() + "\\" +
				 name + "Instance.daml");

    instanceDir.mkdirs();

    write(instanceFile, description);
  }


  public void serviceRangeReceived(Performative msg) {

    String serviceDir = SystemProps.getProperty("http_root");

    String description = msg.getContent();
    description = description.substring(description.indexOf(":serviceRange")
					+ ":serviceRange".length()).trim();

    String task = description.substring(description.indexOf(":task") +
					":task".length(),
					description.indexOf(":content")
					).trim();

    description = description.substring(description.indexOf("<"),
					description.lastIndexOf(">") + 1);

    String name = msg.getSender() + "__" + task;

    File instanceDir = new File(serviceDir + "\\services\\instances\\" +
				name);
    File instanceFile = new File(instanceDir.getPath() + "\\" +
				 name + "Range.xsd");

    instanceDir.mkdirs();
    write(instanceFile, description);
  }

  private boolean write(File file, String content) {

    try {
      FileWriter writer = new FileWriter(file);
      writer.write(content);
      writer.flush();
      writer.close();
      return true;
    }
    catch(IOException i) {
      i.printStackTrace();
      return false;
    }
  }

  public void processModelReceived(Performative msg) {
    
    String serviceDir = SystemProps.getProperty("http_root");
    
    String process = msg.getContent();
    process = process.substring(process.indexOf(":processModel")
				+ ":processModel".length()).trim();
    
    process = process.substring(process.indexOf("<"),
				process.lastIndexOf(">") + 1);
    
    int nameBegin =
      process.toLowerCase().indexOf("process:processmodel ");
    
    nameBegin = process.toLowerCase().indexOf("rdf:id", nameBegin);
    nameBegin = process.indexOf("\"", nameBegin) + 1;

    int nameEnd = process.indexOf("_", nameBegin);

    String name = process.substring(nameBegin, nameEnd);

    File processDir = new File(serviceDir + "\\services\\classes\\" +name);
    File processFile = new File(processDir.getPath() +
				"\\" + name + "Process.daml");

    processDir.mkdirs();
    write(processFile, process);
  }

}
