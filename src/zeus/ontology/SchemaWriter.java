package zeus.ontology;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;

import java.util.List;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

public class SchemaWriter {

  private HashMap namespaces;

  private String defaultNamespace;

  private boolean override;

  public SchemaWriter() {
    namespaces = new HashMap();
    namespaces.put("xsd", "http://www.w3.org/2001/XMLSchema");
    setDefaultNamespace("xsd");
  }

  public void writeRestrictions(File types, List restrictions) {

    String output = makeSchema(restrictions);

    try {
      Writer writer = new FileWriter(types);
      writer.write(output);
      writer.close();
    }
    catch(IOException i) {
      System.out.println(i);
    }

  }

  /**
   * Also used by service renderers, be aware when modifying.
   */
  public String makeSchema(List restrictions) {

    String output = "<?xml version='1.0' encoding='ISO-8859-1'?>\n\n";

    output += "<xsd:schema ";

    output += getNamespaces();

    output += ">\n";

    for(Iterator i = restrictions.iterator() ; i.hasNext() ; ) {
      String[] item = (String[])i.next();
      output += translateType(item[0], item[1], item[2]);
    }

    output += "</xsd:schema>\n";

    //Pretty print
    output = zeus.util.XMLFormatter.formatXML(output);

    return output;
  }

  private String translateType(String typeName, String base, String value) {

    String output = "";

    output += "<xsd:simpleType";
    if(typeName != null && !typeName.equals("")) {
      output += " name=\"" + typeName + "\" ";
    }
    output += ">\n";

    output += translateRestriction(base, value);

    output += "</xsd:simpleType>\n";

    return output;
  }

  private String translateRestriction(String type, String restriction) {
    String output = "";
    
    if(restriction.matches("((.*[<>].*\\|.*)|(.*\\|.*[<>].*))")) {
      return union(type, restriction);
    }

    output += "<xsd:restriction";

    if(type != null && getType(type) != null) {
      output += " base=\"" + getType(type) + "\" ";
    }

    output += ">\n";

    output += parseRestriction(restriction);

    output += "</xsd:restriction>\n";

    return output;
  }

  private String getType(String type) {

    if(override) {
      return defaultNamespace + ":" + type;
    }

    if(type.toLowerCase().equals("string")) {
      return "xsd:string";
    }
    else if(type.toLowerCase().equals("integer")) {
      return "xsd:integer";
    }
    else if(type.toLowerCase().equals("real")) {
      return "xsd:real";
    }
    else if(type.toLowerCase().equals("boolean")) {
      return "xsd:boolean";
    }
    else if(type.toLowerCase().equals("date")) {
      return "xsd:date";
    }
    else if(type.toLowerCase().equals("time")) {
      return "xsd:time";
    }
    else {
      return defaultNamespace + ":" + type.toLowerCase();
    }
  }

  private String parseRestriction(String value) {

    if(value == null || value.equals("")) {
      return "";
    }
    value = value.trim();

    if(value.matches(".*\\|.*")) {
      String pt1 = parseRestriction(getLHS(value, "|"));
      String pt2 = parseRestriction(getRHS(value, "|"));
      return pt1 + pt2;
    }
    else if(value.matches(".*&.*")) {
      String pt1 = parseRestriction(getLHS(value, "&"));
      String pt2 = parseRestriction(getRHS(value, "&"));
      return pt1 + pt2;
    }
    else if(value.matches(".*>=.*")) {
      return "<xsd:minInclusive value=\"" + getRHS(value, ">=") + "\" />\n";
    }
    else if(value.matches(".*<=.*")) {
      return "<xsd:maxInclusive value=\"" + getRHS(value, "<=") + "\" />\n";
    }
    else if(value.matches(".*>.*")) {
      return "<xsd:minExclusive value=\"" + getRHS(value, ">") + "\" />\n";
    }
    else if(value.matches(".*<.*")) {
      return "<xsd:maxExclusive value=\"" + getRHS(value, "<") + "\" />\n";
    }
    else if(value.matches(".*=.*")) {
      return "<xsd:enumeration value=\"" + getRHS(value, "=") + "\" />\n";
    }
    else if(value.matches("..*")) {
      return "<xsd:enumeration value=\"" + value + "\" />\n";
    }

    return "";
  }

  private String getLHS(String target, String search) {
    return target.substring(0, target.indexOf(search)).trim();
  }

  private String getRHS(String target, String search) {
    return target.substring(target.indexOf(search) + search.length()).trim();
  }

  private String union(String type, String value) {

    //Pattern is: disjunction with an inequality on one side or the
    //other
    if(!value.matches("((.*[<>].*\\|.*)|(.*\\|.*[<>].*))")) {
      return parseRestriction(value);
    }

    String output = "";

    output += "<xsd:union>\n";

    output += translateType(null, type, getLHS(value, "|"));
    output += translateType(null, type, getRHS(value, "|"));

    output += "</xsd:union>\n";

    return output;
  }

  private String getNamespaces() {
    String output = "";

    for(Iterator i = namespaces.keySet().iterator() ; i.hasNext() ; ) {
      String key = (String)i.next();
      output += "xmlns:" + key + "=\"" + namespaces.get(key) + "\"\n";
    }

    return output.trim();
  }

  public void setDefaultNamespace(String key) {
    if(namespaces.keySet().contains(key)) {
      defaultNamespace = key;
    }
  }

  public void addNamespace(String key, String namespace) {
    if(key != null && namespace != null && !key.equals("xsd")) {
      namespaces.put(key, namespace);
    }
  }

  public void enableOverrideNamespaces() {
    override = true;
  }

  public void disableOverrideNamespaces() {
    override = false;
  }

}
