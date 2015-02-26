package zeus.ontology;

import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;

import java.util.List;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;

import zeus.util.SystemProps;
import zeus.util.Tree;
import zeus.util.TreeNode;

import zeus.concepts.FactDescription;

/**
 * Write a Zeus ontology as a DAML file. Uses Jena libraries.
 */
public class DAMLWriter {

  /**
   * The namespace of the file.
   */
  protected String namespace;

  /**
   * List of restricted types (<code>String[]</code>).
   */
  protected List restrictions;

  /**
   * List of facts (<code>TreeNode</code>s).
   */
  protected List facts;

  /**
   * Create a new <code>DAMLWriter</code> using the specified
   * namespace, restrictions and facts.
   */
  public DAMLWriter(String namespace, Enumeration restrictionsEnum,
		    Enumeration factsEnum) {

    this.namespace = namespace;

    restrictions = new Vector();
    while(restrictionsEnum.hasMoreElements()) {
      restrictions.add(restrictionsEnum.nextElement());
    }
    
    facts = new Vector();
    while(factsEnum.hasMoreElements()) {
      facts.add(factsEnum.nextElement());
    }
    
  }


  /**
   * Write DAML ontology to the specified file.
   * @param file <code>File</code> to write to.
   * @param version Version information to place in file.
   */
  public int write(File file, String version){

    try {

      DAMLModel document = new DAMLModelImpl();
      
      DAMLOntology ontology = document.createDAMLOntology("");
      ontology.prop_comment().addValue(version);

      //Facts
      for(Iterator i = facts.iterator() ; i.hasNext() ; ) {
	TreeNode node = (TreeNode)i.next();
	translateNode(node, document, facts);
      }

      String typeName = file.getAbsolutePath().replaceAll("\\.[^\\.]*$",
							  "Types.xsd");

      //Restrictions
      SchemaWriter res = new SchemaWriter();
      res.writeRestrictions(new File(typeName), restrictions);

      if(restrictions.size() > 0) {
	ontology.prop_comment().addValue("Types: " + typeName);
      }

      //Write to file
      Writer writer = new FileWriter(file);
      document.write(writer, "RDF/XML-ABBREV", namespace.replaceAll("#", ""));

      return 0;
    }
    catch(RDFException r) {
      return 2;
    }
    catch(IOException i) {
      return 2;
    }

  }

  /**
   * Translate a fact item and insert into the DAMLModel.
   */
  private DAMLClass translateNode(TreeNode node, DAMLModel document,
				  List facts)
    throws RDFException {

    FactDescription desc = (FactDescription)node.getValue();

    DAMLClass item = document.createDAMLClass(namespace + desc.getName());
	
    if(node.getParent() != null) {

      String parentName = ((FactDescription)node.getParent().getValue()).getName();
      Resource parent = null;

      //Try and find parent already in document
      parent = findResource(document, namespace + parentName);

      //If that failed, find the parent Zeus Fact
      if(parent == null) {
	for(Iterator i = facts.iterator() ; i.hasNext() ; ) {
	  TreeNode element = (TreeNode)i.next();
	  FactDescription parentDesc = (FactDescription)element.getValue();
	  if(parentDesc.getName().equals(parentName)) {
	    parent = translateNode(element, document, facts);
	    break;
	  }
	}

	//No parent found
	System.out.println("Parent fact \"" + parentName + "\" not found.");
      }
      
      item.prop_subClassOf().add(parent);
    }

    addAttributes(node, document);

    return item;
  }

  /**
   * Add the attributes of a fact to the DAMLModel as properties.
   */
  private void addAttributes(TreeNode node, DAMLModel document) {

    FactDescription desc = (FactDescription)node.getValue();
    String [][] attributes = desc.getAttributes();

    for(int index = 0 ; index < attributes.length ; index++) {
      String name = attributes[index][0];
      String type = attributes[index][1];
      String restriction = attributes[index][2];
      String defaultVal = attributes[index][3];

      DAMLProperty item = document.createDAMLProperty(desc.getName() +
						      "." + name);

      Resource domain = findResource(document, desc.getName());
      if(domain != null) {
	item.prop_domain().add(domain);
      }

      Resource range = lookupRange(document, type);
      if(range != null) {
	item.prop_range().add(range);
      }

      //Restriction
      if(restriction != null && restriction.length() > 0) {
	addRestriction(item, restriction);
      }
      else {
	//Push all attribute types out to schema file
	addRestriction(item, "");
      }

      //Default
      if(defaultVal != null && defaultVal.length() > 0) {
	addDefault(item, defaultVal);
      }
    }

  }

  /**
   * Find the DAML resource identified by the specified name.
   */
  private Resource findResource(DAMLModel document, String name) {

    for(Iterator i = document.listDAMLClasses() ; i.hasNext() ; ) {
      DAMLClass element = (DAMLClass)i.next();
      
      if(element.getURI() == null)
	continue;

      if(element.getURI().matches("(" + name + "|.*#" + name + ")")) {
	return element;
      }
    }

    for(Iterator i = document.listDAMLProperties() ; i.hasNext() ; ) {
      DAMLProperty element = (DAMLProperty)i.next();

      if(element.getURI() == null)
	continue;

      if(element.getURI().matches("(" + name + "|.*#" + name + ")")) {
	return element;
      }
    }

    return null;
  }

  /**
   * Translate the range of a property.
   */
  private Resource lookupRange(DAMLModel document, String type) {

    String xsd = "http://www.w3.org/2000/10/XMLSchema#";
    String daml = "http://www.daml.org/2001/03/daml+oil#";
    String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    String rdfs = "http://www.w3.org/2000/01/rdf-schema#";

    String types = "";
    if(namespace.lastIndexOf(".") > namespace.lastIndexOf("/")) {
      types = namespace.substring(0, namespace.lastIndexOf(".")) 
	+ "Types.xsd#";
    }

    if(isRestriction(document, type)) {
      return document.createDAMLDatatype(types + type);
    }

    if(findResource(document, type) != null) {
      return findResource(document, type);
    }

    if(type.equals("String")) {
      return document.createDAMLDatatype(xsd + "string");
    }
    else if(type.equals("Integer")) {
      return document.createDAMLDatatype(xsd + "integer");
    }
    else if(type.equals("Real")) {
      return document.createDAMLDatatype(xsd + "real");
    }
    else if(type.equals("Boolean")) {
      return document.createDAMLDatatype(xsd + "boolean");
    }
    else if(type.equals("Date")) {
      return document.createDAMLDatatype(xsd + "date");
    }
    else if(type.equals("Time")) {
      return document.createDAMLDatatype(xsd + "time");
    }
    else if(type.equals("List")) {
      return document.createDAMLDatatype(daml + "list");
    }
    else if(type.equals("JavaObject")) {
      return document.createDAMLDatatype(daml + "Thing");
    }

    return document.createDAMLDatatype(namespace + type);
  }

  private void addRestriction(DAMLProperty item, String value) {

    String name = item.getURI() + ".type";
    DAMLCommon baseRange = item.prop_range().getDAMLValue();
    String range = baseRange.getLocalName();
    item.prop_range().remove(baseRange);

    String[] restriction = new String[] {name, range, value};
    restrictions.add(restriction);

    item.prop_range().add(lookupRange(item.getDAMLModel(), name));
  }

  private void addDefault(DAMLProperty item, String defaultVal) {
    item.prop_comment().addValue("Default: \"" + defaultVal + "\"");
  }

  private boolean isRestriction(DAMLModel document, String name) {

    for(Iterator i = restrictions.iterator() ; i.hasNext() ; ) {
      String[] element = (String[])i.next();
      
      if(element[0].matches("(" + name + "|.*#" + name + ")")) {
	return true;
      }
    }

    return false;
  }

}
