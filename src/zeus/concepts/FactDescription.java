package zeus.concepts;

import java.util.*;

import zeus.util.*;
import zeus.concepts.fn.*;

public class FactDescription {
  protected String name = null;
  protected OrderedHashtable attributes = null;

  public FactDescription(String name) {
    this.name = name;
    this.attributes = new OrderedHashtable();
  }

  public FactDescription(FactDescription desc) {
    this.name = desc.getName();
    this.attributes = new OrderedHashtable();
    String[][] attributes = desc.getAttributes();
    for(int i = 0; i < attributes.length; i++ )
       setAttributeEntry(attributes[i]);
  }

  public String getName()       { return name; }
  public int    numAttributes() { return attributes.size(); }

  public void setName(String name) {
     Assert.notNull(name);
     this.name = name;
  }

  public String[][] getAttributes() {
     String[][] data = new String[attributes.size()][4];
     String[] entry;
     Enumeration elements = attributes.elements();
     for(int i = 0; elements.hasMoreElements(); i++ ) {
        entry = (String[])elements.nextElement();
        data[i][0] = entry[0];
        data[i][1] = entry[1];
        data[i][2] = entry[2];
        data[i][3] = entry[3];
     }
     return data;
  }
  public void add(String aName, String type,
                  String restriction, String defaultValue) {

     Assert.notFalse( !attributes.containsKey(aName) );
     String[] entry = new String[4];
     entry[0] = aName;
     entry[1] = type;
     entry[2] = type;
     entry[3] = type;
     attributes.put(aName,entry);
  }

  public String[] getAttributeEntry(String aName) {
     String[] data = new String[4];
     String[] entry = (String[])attributes.get(aName);
     for(int i = 0; i < entry.length; i++ )
        data[i] = entry[i];
     return data;
  }

  public String[] removeAttributeEntry(String aName) {
     return (String[])attributes.remove(aName);
  }

  public void setAttributeEntry(String[] entry) {
     attributes.put(entry[0],entry);
  }

  public boolean containsAttribute(String aName) {
    return attributes.containsKey(aName);
  }
  public String toString() {
     return name;
  }
}
