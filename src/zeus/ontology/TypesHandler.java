package zeus.ontology;

import zeus.concepts.OntologyDb;

import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import java.util.Iterator;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class TypesHandler extends DefaultHandler {

  private List types;

  private String name;
  private String type;
  private String value;

  private int unionTracker;

  public TypesHandler() {
    types = new Vector();
    unionTracker = 0;
  }

  /**
   * Add the derived restrictions to the db
   */
  public void addRestrictions(OntologyDb db) {
    List names = Arrays.asList(db.getAllRestrictionNames());

    for(Iterator i = types.iterator() ; i.hasNext() ; ) {
      String[] restriction = (String[])i.next();
      String itemName = restriction[0];
      
      if(names.contains(itemName)) {
	continue;
      }

      db.addRestrictions(new String[][] {restriction});
    }

    //Consider moving ".*\\.type" restrictions into the attributes
  }

  private void addRestriction() {

    if(name == null || name.equals("") || type == null || type.equals("") ||
       value == null || value.equals("")) {
      clear();
      return;
    }

    String[] restriction = new String[] {name, type, value};
    types.add(restriction);
    clear();
  }

  public void startElement(String namespace, String sName, String qName,
			   Attributes attributes) throws SAXException {

    String element = sName;
    if(element.equals("")) {
      element = qName;
    }

    if(element.matches("(?i).*:union")) {
      unionTracker++;
    }
    else if(element.matches("(?i).*:simpleType")) {
      //Get name from attributes
      for(int index = 0 ; index < attributes.getLength() ; index++) {

	if(attributes.getQName(index).equalsIgnoreCase("name")) {
	  name = attributes.getValue(index);
	  break;
	}
      }
    }
    else if(name != null && !name.equals("")) {
      if(element.matches("(?i).*:restriction")) {
	for(int index = 0 ; index < attributes.getLength() ; index++) {
	  if(attributes.getQName(index).equalsIgnoreCase("base")) {
	    parseType(attributes.getValue(index));
	    break;
	  }
	}
      }
      else if(type != null && !type.equals("")) {
	parseRestriction(element, attributes);
      }
      
    }
  }

  public void endElement(String namespace, String sName, String qName)
    throws SAXException {

    String element = sName;
    if(element.equals("")) {
      element = qName;
    }

    if(element.matches("(?i).*:simpletype") && unionTracker == 0) {
      addRestriction();
    }
    else if(element.matches("(?i).*:union")) {
      unionTracker--;
    }
  }

  public void characters(char[] ch, int start, int length)
    throws SAXException {

  }

  private void parseRestriction(String element, Attributes attributes) {
    if(element.matches("(?i).*:minInclusive")) {
      joinRestriction(">= " + attributes.getValue("value"));
    }
    else if(element.matches("(?i).*:maxInclusive")) {
      joinRestriction("<= " + attributes.getValue("value"));
    }
    else if(element.matches("(?i).*:minExclusive")) {
      joinRestriction("> " + attributes.getValue("value"));
    }
    else if(element.matches("(?i).*:maxExclusive")) {
      joinRestriction("< " + attributes.getValue("value"));
    }
    else if(element.matches("(?i).*:enumeration")) {
      disjoinRestriction(attributes.getValue("value"));
    }

  }

  private void joinRestriction(String expression) {
    if(unionTracker > 0) {
      disjoinRestriction(expression);
    }
    else {
      conjoinRestriction(expression);
    }
  }

  private void conjoinRestriction(String expression) {
    if(value == null || value.equals("")) {
      value = expression;
    }
    else {
      value = value + " & " + expression;
    }
  }

  private void disjoinRestriction(String expression) {
    if(value == null || value.equals("")) {
      value = expression;
    }
    else {
      value = value + " | " + expression;
    }
  }

  private void clear() {
    name = "";
    type = "";
    value = "";
  }

  private void parseType(String base) {

    if(base.matches(".*:.*")) {
      base = base.substring(base.indexOf(":") + 1);
    }

    if(base.toLowerCase().equals("string")) {
      type = "String";
    }
    else if(base.toLowerCase().equals("integer")) {
      type = "Integer";
    }
    else if(base.toLowerCase().equals("real")) {
      type = "Real";
    }
    else if(base.toLowerCase().equals("boolean")) {
      type = "Boolean";
    }
    else if(base.toLowerCase().equals("date")) {
      type = "Date";
    }
    else if(base.toLowerCase().equals("time")) {
      type = "Time";
    }
    else if(base.toLowerCase().equals("list")) {
      type = "List";
    }
    else 
      type = base;
  }
}
