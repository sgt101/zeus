package zeus.ontology.service;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import zeus.actors.AgentContext;
import zeus.concepts.Task;
import zeus.concepts.Fact;
import zeus.concepts.Restriction;
import zeus.ontology.SchemaWriter;
import zeus.util.SystemProps;

/**
 * Render XML Schema files, such as instance type restrictions
 */
public class SchemaRenderer implements RangeRenderer {

  /**
   * Create an XML Schema that restricts the range of values accepted
   * by a service. Doesn't actually use context, could be removed.
   */
  public String renderRange(Task task, AgentContext context) {

    List restrictions = task.getRestrictions();

    Fact[] input = task.getPreconditions();
    Fact[] output = task.getPostconditions();

    Fact[] facts = new Fact[input.length + output.length];

    for(int index = 0 ; index < input.length ; index++) {
      facts[index] = input[index];
    }
    for(int index = input.length ; index < facts.length ; index++) {
      facts[index] = output[index - input.length];
    }

    String ontologyFile = SystemProps.getProperty("service_platform") +
      "/services/classes/" + task.getName() + "/" + task.getName() +
      "Types.xsd";

    SchemaWriter writer = new SchemaWriter();
    writer.addNamespace("ont", ontologyFile);
    writer.setDefaultNamespace("ont");
    writer.enableOverrideNamespaces();

    return writer.makeSchema(generateRestrictions(restrictions, facts));
  }
  
  /**
   * Make the list of restriction items to pass to the SchemaWriter
   */
  private List generateRestrictions(List restrictions, Fact[] facts) {

    List output = new Vector();

    for(int index = 0 ; index < facts.length ; index++) {
      if(facts[index] == null) {
	continue;
      }

      String[] attr = facts[index].listAttributes();

      for(int count = 0 ; count < attr.length ; count++) {

	//Change types of attributes to 'Fact.Attribute.type'
	String name = facts[index].getType() + "." + attr[count] + ".type";

	String restriction = "";

	for(Iterator i = restrictions.iterator() ; i.hasNext() ; ) {
	  Restriction element = (Restriction)i.next();

	  //Taskname is a given
	  if(element.getFactName().equals(facts[index].getId()) &&
	     element.getAttributeName().equals(attr[count])) {
	    restriction = element.getRestriction();
	    break;
	  }
	  
	}

	String[] item = new String[] { name, name, restriction};
	output.add(item);
      }

    }

    return output;
  }
}
