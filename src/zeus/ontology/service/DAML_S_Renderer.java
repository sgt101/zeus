package zeus.ontology.service;

import zeus.actors.AgentContext;
import zeus.concepts.OntologyDb;
import zeus.concepts.Task;
import zeus.concepts.Fact;
import zeus.concepts.Restriction;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

public class DAML_S_Renderer
  implements InstanceRenderer, ProfileRenderer, ProcessRenderer {

  //Toggles whether to use subclass of Service methodology or schema
  //route.
  public final static boolean SUBCLASS = true;

  private HashMap namespaces;

  private final static String processPrefix = "procmod";
  private final static String profilePrefix = "servprof";
  private final static String instanceRangePrefix = "range";
  private final static String ontologyPrefix = "ont";

  public DAML_S_Renderer() {

    namespaces = new HashMap();

    namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns");
    namespaces.put("rdfs", "http://www.w3.org/2000/01/rdf-schema");
    namespaces.put("daml", "http://www.daml.org/2001/03/daml+oil");
    namespaces.put("process",
		   "http://www.daml.org/services/daml-s/0.7/Process");
    namespaces.put("service",
		   "http://www.daml.org/services/daml-s/0.7/Service");
    namespaces.put("profile",
		   "http://www.daml.org/services/daml-s/0.7/Profile");
    namespaces.put("xsd", "http://www.w3.org/2000/10/XMLSchema.xsd");
  }

  /**
   * Insert the XML header, including all the entity definitions.
   */
  private String makeHeader() {

    String output = "<?xml version='1.0' encoding='ISO-8859-1'?>\n" +
      "<!DOCTYPE uridef[\n";

    for(Iterator i = namespaces.keySet().iterator() ; i.hasNext() ; ) {
      String key = (String)i.next();
      output += "  <!ENTITY " + key + " \"" + namespaces.get(key) + "\" >\n";
    }

    output += "]>\n";

    return output;
  }

  /**
   * Begin the RDF item, including all the namespaces.
   */
  private String startRDF() {

    String output = "<rdf:RDF\n";

    for(Iterator i = namespaces.keySet().iterator() ; i.hasNext() ; ) {
      String key = (String)i.next();
      if(key.matches("(?i)default")) {
	output += "  xmlns =\"&default;#\"\n";
      }
      else {
	output += "  xmlns:" + key + "=\"&" + key + ";#\"\n";
      }
    }

    output += ">\n";

    return output;
  }

  /**
   * Render the ontology item, including the imports statements for
   * all referenced files.
   */
  private String makeOntology(String versionInfo) {

    String output = "<daml:ontology rdf:about=\"\" >\n";

    output += "  <daml:versionInfo>" + versionInfo + "</daml:versionInfo>\n";

    for(Iterator i = namespaces.keySet().iterator() ; i.hasNext() ; ) {
      String key = (String)i.next();
      output += "  <daml:imports rdf:resource=\"&" + key + ";\" />\n";
    }

    output += "</daml:ontology>\n\n";

    return output;
  }

  public String renderInstance(Task task, AgentContext context) {

    String instance = context.whoami() + "__" + task.getName();

    String serviceClass;
    String serviceInstance = "";

    if(SUBCLASS) {
      serviceClass = profilePrefix + ":" + task.getName() + "Service";
    }
    else {
      serviceClass = "service:Service";
    }

    namespaces.put(processPrefix, "http://" + context.whereAmI() +
		   "/services/classes/" + task.getName() + "/" +
		   task.getName() + "Process.daml");
    namespaces.put(profilePrefix, "http://" + context.whereAmI() +
		   "/services/classes/" + task.getName() + "/" +
		   task.getName() + "Profile.daml");
    namespaces.put(instanceRangePrefix, "http://" + context.whereAmI() +
		   "/services/instances/" + context.whoami() + "/" +
		   instance + "Range.xsd");
    namespaces.put("default", "http://" + context.whereAmI() +
		   "/services/instances/" + context.whoami() + "/" + instance +
		   "Instance.daml");

    serviceInstance += makeHeader() + startRDF() + makeOntology("");

    serviceInstance += "<" + serviceClass + " rdf:ID=\"" + instance + "\">\n";

    serviceInstance += "  <profile:providedBy>\n";

    String item;

    serviceInstance += "    <profile:ServiceProvider rdf:ID=\"" +
      context.whoami() + "\">\n";

    /* FIXME
    item = info.getProviderName();
    if(item != null && item.length() > 0) {
      serviceInstance += "      <profile:name>" + item
	+ "</profile:name>\n";
    }
    */

    item = task.getPhoneInfo();
    if(item != null && item.length() > 0) {
      serviceInstance += "      <profile:phone>" + item
	+ "</profile:phone>\n";
    }

    item = task.getFaxInfo();
    if(item != null && item.length() > 0) {
      serviceInstance += "      <profile:fax>" + item
	+ "</profile:fax>\n";
    }

    item = task.getEmailInfo();
    if(item != null && item.length() > 0) {
      serviceInstance += "      <profile:email>" + item
	+ "</profile:email>\n";
    }

    item = task.getPhysicalInfo();
    if(item != null && item.length() > 0) {
      serviceInstance += "      <profile:physicalAddress>" +
	item + "</profile:physicalAddress>\n";
    }

    serviceInstance += "      <profile:webURL>" + context.whereAmI()
      + "</profile:webURL>\n";

    serviceInstance += "    </profile:ServiceProvider>\n";
    serviceInstance += "  </profile:providedBy>\n";

    item = task.getGeoInfo();
    if(item != null && item.length() > 0) {
      serviceInstance += "  <profile:geographicRadius>" +
	item + "</profile:geographicRadius>\n";
    }

    serviceInstance += "  <service:presents rdf:resource=\"&" + profilePrefix +
      ";#" + task.getName() + "\" />\n";

    serviceInstance += "  <service:describedBy rdf:resource=\"&" +
      processPrefix + ";#" + task.getName() + "\" />\n";

    if(SUBCLASS) {
      //State instance properties here
      serviceInstance += generateInstanceValues(task);
    }

    serviceInstance += "</" + serviceClass + ">\n";

    if(!SUBCLASS) {
      //Pass restrictions off to range schema
      serviceInstance += generateFactRestrictions(task);
    }

    serviceInstance += "</rdf:RDF>\n";

    return serviceInstance;
  }

  /**
   * Generate the values of certain variables for this instance of the
   * Service subclass.
   */
  private String generateInstanceValues(Task task) {

    String output = "";

    List restrictions = task.getRestrictions();

    for(Iterator i = restrictions.iterator() ; i.hasNext() ; ) {
      Restriction item = (Restriction)i.next();

      output += generateRestriction(lookupFactId(item.getFactName(), task),
				    item.getAttributeName(),
				    item.getRestriction());
    }

    return output;
  }

  /**
   * Translate a single restriction
   */
  private String generateRestriction(String factName, String attributeName,
				     String restriction) {

    String output = "";

    if(restriction.matches(".*\\|.*")) {
      String[] pieces = restriction.split("\\|");
      output += "  <daml:unionOf rdf:parseType=\"daml:collection\">\n";
      for(int index = 0 ; index < pieces.length ; index++ ) {
	output += generateRestriction(factName, attributeName, pieces[index]);
      }
      output += "  </daml:unionOf>\n";
    }
    else if(restriction.matches(".*&.*")) {
      String[] pieces = restriction.split("&");
      for(int index = 0 ; index < pieces.length ; index++ ) {
	output += generateRestriction(factName, attributeName, pieces[index]);
      }
    }
    else if(restriction.matches("((.*<.*)|(.*>.*))")) {
      //Cannot deal with inequality
      return "";
    }
    else {
      restriction = restriction.replaceAll("=", "").trim();

      if(false) {
	output +=
	  "  <" + profilePrefix + ":" + factName + ">\n" +
	  "    <" + profilePrefix + ":" + attributeName + ">" + restriction +
	  "</" + profilePrefix + ":" + attributeName + ">\n" + 
	  "  </" + profilePrefix + ":" + factName + ">\n";
      }
      else {
	output +=
	  "  <" + profilePrefix + ":" + factName + " " + processPrefix + ":" +
	  attributeName + "=\"" + restriction + "\" />\n";
      }
    }

    return output;
  }

  /**
   * Lookup the type of fact behind a particular variable name.
   */
  private String lookupFactId(String factId, Task task) {

      Fact[] facts = task.getPreconditions();
      for(int index = 0 ; index < facts.length ; index++) {
	if(facts[index].getId().equals(factId)) {
	  return facts[index].getType();
	}
      }
 
      facts = task.getPostconditions();
      for(int index = 0 ; index < facts.length ; index++) {
	if(facts[index].getId().equals(factId)) {
	  return facts[index].getType();
	}
      }
 
      return "";
  }

  /**
   * Generate the restriction references to the range schema file.
   */
  private String generateFactRestrictions(Task task) {

    String output = "";

    Fact[] inputs = task.getPreconditions();
    Fact[] outputs = task.getPostconditions();

    for(int index = 0 ; index < inputs.length ; index++ ) {
      output += generateRestriction(inputs[index]);
    }

    for(int index = 0 ; index < outputs.length ; index++ ) {
      output += generateRestriction(outputs[index]);
    }

    return output;
  }

  /**
   * Generate the restriction references for a particular fact object.
   */
  private String generateRestriction(Fact fact) {

    String output = "";

    String[] attrs = fact.listAttributes();

    for(int index = 0 ; index < attrs.length ; index++) {

      output += "  <daml:Property rdf:about=\"&" + processPrefix + ";#" +
	fact.getType() + "." + attrs[index] + "\" >\n";
      output += "    <daml:range rdf:resource=\"&" + instanceRangePrefix +
	";#" + fact.getType() + "." + attrs[index] + ".type\" />\n";
      output += "  </daml:Property>\n";
    }

    return output;
  }

  /**
   * Produce a DAML-S service profile. Context is only used to
   * retrieve the address of the local platform, so the method could
   * easily be adapted to produce a profile at generation, rather than
   * runtime.
   */
  public String renderProfile(Task task, AgentContext context) {
    return renderProfile(task, context.whereAmI());
  }

  /**
   * Host is the IP address / domain name of the platform
   */
  public String renderProfile(Task task, String host) {

    String name = task.getName();

    namespaces.put(processPrefix, "http://" + host + "/services/classes/" +
		   name + "/" + name + "Process.daml");
    namespaces.put("default", "http://" + host + "/services/classes/" +
		   name + "/" + name + "Profile.daml");

    String serviceProfile = makeHeader() + startRDF() + makeOntology("");

    if(SUBCLASS) {
      serviceProfile += generateServiceClass(task) + "\n";
    }

    serviceProfile += "<service:serviceProfile rdf:ID=\"" + name + "\" >\n";

    serviceProfile += "  <profile:textDescription>\"" + task.getTextInfo() +
      "\"</profile:textDescription>\n";
   
    serviceProfile += "  <profile:has_process rdf:resource=\"" +
      "&" + processPrefix + ";#" + name + "\" />\n";

    serviceProfile += generateIOPEs(task);

    serviceProfile += "</service:serviceProfile>\n";

    serviceProfile += "</rdf:RDF>";

    return serviceProfile;
  }

  /**
   * Generate the collection of IOPEs for the profile
   */
  private String generateIOPEs(Task task) {

    String output = "";

    Fact[] inputs = task.getPreconditions();
    Fact[] outputs = task.getPostconditions();

    for(int index = 0 ; index < inputs.length ; index++) {
      if(inputs[index].isReadOnly()) {
	output += generateParameter(inputs[index], "precondition");
      }
      else {
	output += generateParameter(inputs[index], "input");
      }
    }

    for(int index = 0 ; index < outputs.length ; index++) {
      if(outputs[index].isReadOnly()) {
	output += generateParameter(outputs[index], "effect");
      }
      else {
	output += generateParameter(outputs[index], "output");
      }
    }

    return output;
  }

  /**
   * Generate a particular IOPE for the profile, of the type
   * specified.
   */
  private String generateParameter(Fact fact, String type) {

    String output = "";

    output += "  <profile:" + type + ">\n";

    output += "    <profile:ParameterDescription rdf:ID=\"" + fact.getType() +
      "IOPE\" >\n";
    output += "      <profile:parameterName>" +
      fact.getId().replaceAll("\\?", "") + "</profile:parameterName>\n";
    output += "      <profile:restrictedTo rdf:resource=\"&" + processPrefix +
      ";#" + fact.getType() + "\" />\n";
    output += "      <profile:refersTo rdf:resource=\"&" + processPrefix +
      ";#" + fact.getType() + "\" />\n";
    output += "    </profile:ParameterDescription>\n";

    output += "  </profile:" + type + ">\n";

    return output;
  }

  /**
   * Generate the subclass of Service that will allow instance
   * information to be recorded in the service declaration.
   */
  private String generateServiceClass(Task task) {

    String output = "";

    output += "<daml:Class rdf:ID=\"" + task.getName() + "Service\" >\n";

    output += "  <daml:subclassof rdf:resource=\"&service;#Service\" />\n";

    output += "</daml:Class>\n";

    output += generateServiceClassProperties(task);

    return output;
  }

  /**
   * Genreate the properties of the service subclass, and attribute
   * them to the class.
   */
  private String generateServiceClassProperties(Task task) {

    String output = "";

    Fact[] inputs = task.getPreconditions();
    Fact[] outputs = task.getPostconditions();

    for(int index = 0 ; index < inputs.length ; index++) {
      output += generateProperty(inputs[index], task);
    }

    for(int index = 0 ; index < outputs.length ; index++) {
      output += generateProperty(outputs[index], task);
    }

    return output;
  }

  /**
   * Generate an individual property of the service subclass.
   */
  private String generateProperty(Fact fact, Task task) {

    String output = "";

    output += "<daml:Property rdf:ID=\"" + fact.getType() + "\" >\n";
    output += "  <daml:domain rdf:resource=\"#" + task.getName() +
      "Service\" />\n";
    output += "  <daml:range rdf:resource=\"#" + fact.getType() +
      "IOPE\" />\n";
    output += "</daml:Property>\n";

    return output;
  }


  public String renderProcess(Task task, AgentContext context) {

    //FIXME: Need to determine name of ontology file
    String ontology = task.getName() + ".daml";

    namespaces.put(ontologyPrefix, "http://" + context.whereAmI() +
		   "/services/ontologies/" + ontology);

    String output = makeHeader() + startRDF() + makeOntology("");

    output += "<process:ProcessModel rdf:ID=\"" + task.getName() +
      "_Process\">\n";
    output += "  <process:hasProcess rdf:resource=\"" + task.getName() +
      "\">\n";
    output += "</process:ProcessModel>\n";

    output += "<daml:Class rdf:ID=\"" + task.getName() + "\" >\n";
    output += "  <daml:subclassOf rdf:resource=\"" +
      "&process;#AtomicProcess\" />\n";
    output += "<daml:Class>\n\n";

    //Refine classes into IOPEs
    output += makeIOPEs(task);

    output += "</rdf:RDF>\n";

    return output;
  }

  /**
   * Make IOPEs for the process
   */
  private String makeIOPEs(Task task) {

    String output = "";

    Fact[] inputs = task.getPreconditions();
    Fact[] outputs = task.getPostconditions();

    for(int index = 0 ; index < inputs.length ; index++) {
      if(inputs[index].isReadOnly()) {
	output += generateIOPE(inputs[index], task, "precondition");
      }
      else {
	output += generateIOPE(inputs[index], task, "input");
      }
    }

    for(int index = 0 ; index < outputs.length ; index++) {
      if(outputs[index].isReadOnly()) {
	output += generateIOPE(outputs[index], task, "effect");
      }
      else {
	output += generateIOPE(outputs[index], task, "output");
      }
    }

    return output;
  }

  /**
   * Generate an IOPE property for the process
   */
  private String generateIOPE(Fact fact, Task task, String type) {

    String output = "";

    output += "<daml:Property rdf:ID=\"" + fact.getType() + "\" >\n";
    output += "  <daml:subclassOf rdf:resource=\"&process;#" + type +"\" />\n";
    output += "  <daml:domain rdf:resource=\"" + task.getName() + "\" />\n";
    output += "  <daml:range rdf:resource=\"&" + ontologyPrefix + ";#" +
      fact.getType() + "\" />\n";
    output += "</daml:Property>\n";

    return output;
  }

}
