package zeus.ontology;

import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.*;
import com.hp.hpl.mesa.rdf.jena.model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.Reader;
import java.io.IOException;

import java.util.List;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;

import zeus.util.SystemProps;
import zeus.util.Tree;
import zeus.util.TreeNode;

import java.util.regex.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import zeus.concepts.FactDescription;
import zeus.concepts.OntologyDb;

public class DAMLReader {

  protected OntologyDb db;
  protected Hashtable factIndex;

  public DAMLReader(OntologyDb db) {
    this.db = db;
    factIndex = db.getFactIndex();
  }

  public void read(File file) {

    String namespace = SystemProps.getProperty("daml.namespace")
      + file.getName() + "#";

    try {
      DAMLModel document = new DAMLModelImpl();

      Reader reader = new FileReader(file);
      document.read(reader, namespace);

      List waitingList = new Vector();

      for(Iterator i = document.listDAMLClasses() ; i.hasNext() ; ) {
	DAMLClass item = (DAMLClass)i.next();
	translateClass(item, waitingList);
      }

      while(waitingList.size() > 0) {
	DAMLClass item = (DAMLClass)waitingList.remove(0);
	translateClass(item, waitingList);
      }

      for(Iterator i = document.listDAMLProperties() ; i.hasNext() ; ) {
	DAMLProperty item = (DAMLProperty)i.next();
	translateProperty(item);
      }

      //Ignore instances

      loadRestrictions(file);

    }
    catch(RDFException r) {
      debug(r.toString());
    }
    catch(IOException i) {
      debug(i.toString());
    }
    
  }

  private void translateClass(DAMLClass item, List waitingList)
    throws RDFException {

    String name = item.getLocalName();

    //Check if item is already in fact index
    if(factIndex.get(name) != null)
      return;

    DAMLCommon parent = item.prop_subClassOf().getDAMLValue();
    if(parent == null) {
      //Invalid item
      return;
    }
    String parentName = parent.getLocalName();

    if(factIndex.get(parentName) == null) {
      waitingList.add(item);
      return;
    }

    TreeNode node = new TreeNode(new FactDescription(name));
    factIndex.put(name, node);
 
    TreeNode parentNode = (TreeNode)factIndex.get(parentName);
    parentNode.addChild(node);
  }

  private void translateProperty(DAMLProperty item) throws RDFException {

    String fact = "";
    String name = item.getLocalName();
    String type = "";
    String restriction = "";
    String defaultValue = "";

    DAMLCommon domain = item.prop_domain().getDAMLValue();
    if(domain != null) {
      fact = domain.getLocalName();
    }

    Resource range = (Resource)item.prop_range().get();
    if(range != null) {
      type = translateRange(range.getLocalName());
    }

    for(NodeIterator i = item.prop_comment().getValues() ; i.hasNext() ; ) {
      String comment = i.next().toString();

      if(comment.matches("Restriction: \\\".*\\\"")) {
	restriction = comment.substring(comment.indexOf("\"") + 1,
					comment.lastIndexOf("\""));
      }
      else if(comment.matches("Default: \\\".*\\\"")) {
	defaultValue = comment.substring(comment.indexOf("\"") + 1,
					 comment.lastIndexOf("\""));
      }

    }

    TreeNode factNode = (TreeNode)factIndex.get(fact);

    if(factNode == null) {
      //Invalid item
      debug("Invalid property: " + fact);
      return;
    }

    if(name.matches(fact + "\\..*")) {
      name = name.substring(fact.length() + 1);
    }

    FactDescription desc = (FactDescription)factNode.getValue();
    desc.setAttributeEntry(new String[] {name, type, restriction,
					 defaultValue});
  }

  private String translateRange(String range) {

    if(range.toLowerCase().equals("string")) {
      return "String";
    }
    else if(range.toLowerCase().equals("integer")) {
      return "Integer";
    }
    else if(range.toLowerCase().equals("real")) {
      return "Real";
    }
    else if(range.toLowerCase().equals("boolean")) {
      return "Boolean";
    }
    else if(range.toLowerCase().equals("date")) {
      return "Date";
    }
    else if(range.toLowerCase().equals("time")) {
      return "Time";
    }
    else if(range.toLowerCase().equals("list")) {
      return "List";
    }
    else 
      return range;
  }

  private void debug(String entry) {
    //System.out.println(entry);
  }

  private void loadRestrictions(File ontology) {

    String content = readFile(ontology);

    //Try and retrieve types file name from ontology file
    if(content.matches("(?smi).*comment>types: .*")) {
      
      try {
	String lower = content.toLowerCase();
	int begin = lower.indexOf(":ontology");
	begin = lower.indexOf(":comment", begin);
	begin = lower.indexOf("types: ", begin) + "types: ".length();
	int end = lower.indexOf("<", begin);

	String name = content.substring(begin, end);
	
	File restrictions = new File(name);
	if(restrictions.exists()) {
	  readRestrictions(restrictions);
	  return;
	}
      }
      catch(StringIndexOutOfBoundsException s) {}
    }

    { //If that fails, try using the same filename with .xsd extension
      String name = ontology.getAbsolutePath().replaceAll("\\.[^\\.]*$",
							  ".xsd");

      File restrictions = new File(name);
      if(restrictions.exists()) {
	readRestrictions(restrictions);
      }
    }

  }

  private void readRestrictions(File types) {

    SAXParserFactory factory = SAXParserFactory.newInstance();
    TypesHandler handler = new TypesHandler();

    try {
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(types, handler);
    }
    catch(SAXException s) {
      s.printStackTrace();
    }
    catch(IOException i) {}
    catch(ParserConfigurationException p) {
      p.printStackTrace();
    }

    handler.addRestrictions(db);
  }

  private String readFile(File file) {

    if(file == null || !file.exists()) {
      return "";
    }

    try {
      FileInputStream reader = new FileInputStream(file);
      BufferedInputStream in = new BufferedInputStream(reader);
      byte[] out = new byte[in.available()];

      in.read(out, 0, out.length);

      reader.close();

      return new String(out);
    }
    catch(IOException i) {
    }
    return "";
  }
}
