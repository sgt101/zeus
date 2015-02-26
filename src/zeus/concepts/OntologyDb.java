/*
* The contents of this file are subject to the BT "ZEUS" Open Source 
* Licence (L77741), Version 1.0 (the "Licence"); you may not use this file 
* except in compliance with the Licence. You may obtain a copy of the Licence
* from $ZEUS_INSTALL/licence.html or alternatively from
* http://www.labs.bt.com/projects/agents/zeus/licence.htm
*
* Except as stated in Clause 7 of the Licence, software distributed under the
* Licence is distributed WITHOUT WARRANTY OF ANY KIND, either express or 
* implied. See the Licence for the specific language governing rights and 
* limitations under the Licence.
* 
* The Original Code is within the package zeus.*.
* The Initial Developer of the Original Code is British Telecommunications
* public limited company, whose registered office is at 81 Newgate Street, 
* London, EC1A 7AJ, England. Portions created by British Telecommunications 
* public limited company are Copyright 1996-9. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



/*
 * @(#)OntologyDb.java 1.0
 */

package zeus.concepts;

import java.io.*;
import java.util.*;
import javax.swing.event.*;

import zeus.util.*;
import zeus.concepts.fn.*;
import zeus.ontology.DAMLWriter;
import zeus.ontology.DAMLReader;

/**
 * This implements the Ontology Database component, which has two main roles:
 * <ul>
 * <li> database - to store the conceptual descriptions that make up an ontology
 * <li> factory - to create new {@link Fact} objects from the stored fact descriptions
 * </ul> <p>
 *
 * Aside from the fact creation and query methods, developers are unlikely to
 * need the other methods of this class.
 * Change Log
 * 26/06/01 introduced allAncestors method which returns an interator containing all 
 * ancestors of a type - Simon Thompson
 */


public class OntologyDb extends Tree {
  /** OntologyDbChangeEvent types */

  public static final int RELOAD               = 0;
  public static final int FACT_ADDED           = 1;
  public static final int FACT_REMOVED         = 2;
  public static final int FACT_RENAMED         = 3;
  public static final int ATTRIBUTES_CHANGED   = 4;
  public static final int RESTRICTION_ADDED    = 5;
  public static final int RESTRICTION_REMOVED  = 6;
  public static final int RESTRICTION_CHANGED  = 7;

  /*
    Note: methods prefixed with __ are used by the parser
  */

  public static final String ROOT     = "ZeusFact";
  public static final String ABSTRACT = "Abstract";
  public static final String ENTITY   = "Entity";
  public static final String MESSAGE  = "Message";
  public static final String MONEY    = "Money";

  public static final String AMOUNT    = "amount";
  public static final String NUMBER    = "number";
  public static final String COST      = "unit_cost";
  public static final String GOAL_FACT = "fact";

  public static final int WARNING_MASK = 1;
  public static final int ERROR_MASK   = 2;

  static final String BEGIN_ONTOLOGY         = "BEGIN_ONTOLOGY";
  static final String BEGIN_PREAMBLE         = "BEGIN_PREAMBLE";
  static final String BEGIN_RESTRICTIONS     = "BEGIN_RESTRICTIONS";
  static final String BEGIN_RESTRICTION_ITEM = "BEGIN_RESTRICTION_ITEM";
  static final String BEGIN_FACTS            = "BEGIN_FACTS";
  static final String BEGIN_FACT_ITEM        = "BEGIN_FACT_ITEM";
  static final String BEGIN_ATTRIBUTE_LIST   = "BEGIN_ATTRIBUTE_LIST";
  static final String BEGIN_ATTRIBUTE_ITEM   = "BEGIN_ATTRIBUTE_ITEM";

  static final String END_PREAMBLE           = "END_PREAMBLE";
  static final String END_RESTRICTION_ITEM   = "END_RESTRICTION_ITEM";
  static final String END_RESTRICTIONS       = "END_RESTRICTIONS";
  static final String END_ATTRIBUTE_ITEM     = "END_ATTRIBUTE_ITEM";
  static final String END_ATTRIBUTE_LIST     = "END_ATTRIBUTE_LIST";
  static final String END_FACT_ITEM          = "END_FACT_ITEM";
  static final String END_FACTS              = "END_FACTS";
  static final String END_ONTOLOGY           = "END_ONTOLOGY";

  static final String SYSTEM_NAME            = "ZEUS-Ontology-Editor";

  static final String SYSTEM                 = ":system";
  static final String VERSION                = ":version";
  static final String NAME_TAG               = ":name";
  static final String PARENT_TAG             = ":parent";
  static final String TYPE_TAG               = ":type";
  static final String VALUE_TAG              = ":value";
  static final String REST_TAG               = ":restriction";
  static final String DEFA_TAG               = ":default";
  static final String QUOTE                  = "\"";

  static final int NAME        = 0;
  static final int TYPE        = 1;
  static final int RESTRICTION = 2;
  static final int DEFAULT     = 3;

  protected static final String FACT_STR = "Fact";
  protected static final String NAME_STR = "name";
  protected static final String CSEP = "$";
  protected static final String TFILE = "oetf22";

  public static final String STRING  = "String";
  public static final String INTEGER = "Integer";
  public static final String REAL    = "Real";
  public static final String BOOLEAN = "Boolean";
  public static final String DATE    = "Date";
  public static final String TIME    = "Time";
  public static final String LIST    = "List";

  public static final String OBJECT_TYPE = "JavaObject";

  public static final String[] BASIC_TYPES = {
     STRING, INTEGER, REAL, BOOLEAN, DATE, TIME, LIST
  };

  static final String[] PREDEFINED_FACTS = {
     ROOT, MONEY, ABSTRACT, MESSAGE, ENTITY
  };

  public static final String[] GOAL_ATTRIBUTES = {
     GOAL_FACT, "end_time", "cost", "confirm_time"
  };


  protected EventListenerList changeListeners = new EventListenerList();
  protected Hashtable factIndex;
  protected OrderedHashtable restrictions;
  protected int editableLimit;

  protected boolean save_needed = false;
  protected String  error = null;
  protected String  warning = null;
  protected String  filename = null;
  protected String ontologyName = null; 
  protected GenSym  genSym = null;

  public OntologyDb () {
   super (new FactDescription (ROOT));
  }

  public OntologyDb(GenSym genSym) {
     super(new FactDescription(ROOT));

     Assert.notNull(genSym);
     this.genSym = genSym;

     factIndex = new Hashtable();
     restrictions = new OrderedHashtable();
     clear();
  }

  public GenSym GenSym() { return genSym; }
  
  
  /**
   *return the actual user defined name of the ontology
   */
  public String getOntologyName () {
    return ontologyName;
  }
  


  public void clear() {
     factIndex.clear();
     restrictions.clear();

     root = new TreeNode(new FactDescription(ROOT));
     factIndex.put(ROOT,root);

     TreeNode node, leaf;
     node = root.addChild(new FactDescription(ABSTRACT));
     factIndex.put(ABSTRACT,node);

     leaf = node.addChild(new FactDescription(MONEY));
     factIndex.put(MONEY,leaf);
     __addNewAttributeRow(MONEY, AMOUNT, REAL, "", "0");

     leaf = node.addChild(new FactDescription(MESSAGE));
     factIndex.put(MESSAGE,leaf);
     for(int i = 0; i < Performative.ATTRIBUTE_TYPES.length; i++ )
     __addNewAttributeRow(MESSAGE, Performative.ATTRIBUTE_TYPES[i], STRING, "", "");

     node = root.addChild(new FactDescription(ENTITY));
     factIndex.put(ENTITY,node);
     __addNewAttributeRow(ENTITY, NUMBER, INTEGER, "", "1");
     __addNewAttributeRow(ENTITY, COST, REAL, "", "0");

     editableLimit = 0;

     error = null;
     warning = null;
     filename = null;
     fireChanged(RELOAD,null);
     save_needed = false;
  }

  // -- FILE MANAGEMENT OPERATIONS ---------------------------------

  public boolean isSaveNeeded() { return save_needed; }
  public String  getError()     { return error; }
  public String  getWarning()   { return warning; }
  public String  getFilename()  { return filename;  }

  public void __setWarning(String info) {
     if ( warning == null )
        warning = info;
     else
        warning += "\n" + info;
  }

  public int saveDAML(File file) {

    if(file == null) {
      return ERROR_MASK;
    }

    setFilename(file);

    String namespace = SystemProps.getProperty("daml.namespace")
      + file.getName() + "#";

    String version = "Created by " + SYSTEM_NAME + " " +
      SystemProps.getProperty("version.id");

    DAMLWriter writer = new DAMLWriter(namespace, restrictions.elements(),
				       this.nodes());
    return writer.write(file, version);
  }


//----------------------------G.Owusu 18.6.99-------------------------------
  static final String HEADER = "<?xml version=\"1.0\">";
  static final String ELEMENT_TAG = "!ELEMENT";
  static final String ATTRIBUTE_TAG = "!ATTLIST";
  static final String LT = "<";
  static final String GT = ">";
  static final String SPACE = " ";
  static final String OPEN_BRACKET = "(";
  static final String CLOSE_BRACKET = ")";
  static final String COMMA = ",";
  static final String PCDATA = "#PCDATA";
  static final String CDATA = "CDATA";

  public int saveXML(File file) 
  {
    Assert.notNull(file);
    int status = 0;
    int num = 0;
    error = null;
    String dir, temp, fsep;
    Enumeration enum;
    FactDescription desc;
    TreeNode node;
    String name, output, default_info, restriction_info;
    String[][] attributes;
    Vector elementNodes;

    try {
      dir = file.getParent();
      temp = (dir != null) ? dir + File.separator + TFILE : TFILE;
      File f1 = new File(temp);

      while( f1.exists() )
        f1 = new File(temp + (num++));

      String filename = file.getName();
      int index = filename.indexOf('.');
      String documentName = (index == -1) ? filename : filename.substring(0,index);

      PrettyPrintWriter out = new PrettyPrintWriter(f1);
      out.pprint(0,HEADER);
      out.pprint(0,"<!DOCTYPE" + SPACE + documentName + SPACE + "[");

      enum = this.nodes();
      while( enum.hasMoreElements() ) {
         node = (TreeNode)enum.nextElement();
         desc = (FactDescription)node.getValue();
         name = desc.getName();
         if ( !Misc.member(name,PREDEFINED_FACTS) ) {
            attributes = desc.getAttributes();

            /* First, write ELEMENT info */
            output = LT + ELEMENT_TAG + SPACE + name + SPACE + OPEN_BRACKET;

            elementNodes = getElementNodes(desc);

            if ( !elementNodes.isEmpty() ) {
               for(int i = 0; i < elementNodes.size(); i++ ) {
                  output += elementNodes.elementAt(i);
                  if ( i < elementNodes.size() - 1 )
                     output += COMMA;
               }
            }
            else {
               output += PCDATA;
            }
            output += CLOSE_BRACKET + GT;
            out.pprint(2,output);

            /* Next, write ATTLIST info */
            if ( hasBasicAttributes(desc) ) {
               out.pprint(2, LT + ATTRIBUTE_TAG + SPACE + name);
               for(int i = 0; i < attributes.length; i++ ) {
                  if ( !elementNodes.contains(attributes[i][NAME]) ) {
                   
                     restriction_info = default_info = "";
                     if (attributes[i][RESTRICTION] != null )
                        restriction_info = attributes[i][RESTRICTION].trim();
   
               if (attributes[i][DEFAULT] != null )
                         default_info = attributes[i][DEFAULT].trim();
   
                     if ( restriction_info.indexOf('|') == -1 ||
                          restriction_info.indexOf('(') != -1 )
                  output = SPACE + CDATA;
                     else
                        output = SPACE + OPEN_BRACKET + restriction_info + CLOSE_BRACKET;
                 
                     if ( default_info.length() > 0 )
                        output += SPACE + "\"" + default_info + "\"";
   
                     out.pprint(4, attributes[i][NAME] + output);
                  }
               }
               out.pprint(2,GT);
            }
            out.println();
         }
      }
      out.pprint(0,"]>");

      out.flush();
      out.close();

      if ( file.exists() ) file.delete();
      f1.renameTo(file);
    }
    catch(Exception e) {
      error = e.toString();
      status |= ERROR_MASK;
    }
    return status;

  }

  protected Vector getElementNodes(FactDescription desc) {
    String[][] attributes = desc.getAttributes();
    Vector output = new Vector();
    for(int i = 0; i < attributes.length; i++ ) {
       if ( factIndex.containsKey(attributes[i][TYPE]) )
          output.addElement(attributes[i][NAME]);
    }
    return output;
  }

  protected boolean hasBasicAttributes(FactDescription desc) {
    String[][] attributes = desc.getAttributes();
    Vector output = new Vector();
    for(int i = 0; i < attributes.length; i++ ) {
       if ( Misc.member(attributes[i][TYPE],BASIC_TYPES) )
          return true;
    }
    return false;
  }



//--------------------------------------------------------------------------
  public int saveFile(File file)
  {
    Assert.notNull(file);

    String dir, temp, fsep;
    int status = 0;
    error = null;
    warning = null;
    int num = 0;

    dir = file.getParent();
    temp = (dir != null) ? dir + File.separator + TFILE : TFILE;
    File f1 = new File(temp);

    while( f1.exists() )
       f1 = new File(temp + (num++));

    try 
    {
      String[][] attributes;
      String[] entry;
      String value;
      Enumeration enum;
      TreeNode node, parent;
      FactDescription desc, parent_desc;
      PrettyPrintWriter out = new PrettyPrintWriter(f1);

      out.pprint(0,BEGIN_ONTOLOGY);
      out.pprint(1,BEGIN_PREAMBLE);
      out.pprint(2,SYSTEM  + " " + SYSTEM_NAME);
      out.pprint(2,VERSION + " \"" + SystemProps.getProperty("version.id") + "\"");
      out.pprint(1,END_PREAMBLE);

      enum = restrictions.elements();
      if ( enum.hasMoreElements() ) 
      {
         out.pprint(1,BEGIN_RESTRICTIONS);
         while( enum.hasMoreElements() ) 
         {
            out.pprint(2,BEGIN_RESTRICTION_ITEM);
            entry = (String[])enum.nextElement();
            value = (entry[2] != null) ? entry[2] : "";
            out.pprint(3,NAME_TAG  + " " + entry[0]);
            out.pprint(3,TYPE_TAG  + " " + entry[1]);
            out.pprint(3,VALUE_TAG + " " + QUOTE + value + QUOTE);
            out.pprint(2,END_RESTRICTION_ITEM);
         }
         out.pprint(1,END_RESTRICTIONS);
      }

      enum = this.nodes();
      if ( enum.hasMoreElements() ) 
      {
        out.pprint(1,BEGIN_FACTS);
        while( enum.hasMoreElements() )
        {
          node = (TreeNode)enum.nextElement();
          desc = (FactDescription)node.getValue();

          if ( !Misc.member(desc.getName(),PREDEFINED_FACTS) )
          {
            out.pprint(2,BEGIN_FACT_ITEM);
            out.pprint(3,NAME_TAG + " " + desc.getName());
            parent = node.getParent();
            parent_desc = (FactDescription)parent.getValue();
            out.pprint(3,PARENT_TAG + " " + parent_desc.getName());

            attributes = desc.getAttributes();
            if ( attributes.length > 0 )
            {
              out.pprint(3,BEGIN_ATTRIBUTE_LIST);
              for(int i = 0; i < attributes.length; i++ )
              {
                for(int j = 0; j < attributes[i].length; j++ )
                  if ( attributes[i][j] == null ) attributes[i][j] = "";

                attributes[i][RESTRICTION] = QUOTE + attributes[i][RESTRICTION] + QUOTE;
                attributes[i][DEFAULT] = QUOTE + attributes[i][DEFAULT] + QUOTE;

                out.pprint(4,BEGIN_ATTRIBUTE_ITEM);
                out.pprint(5,NAME_TAG + " " + attributes[i][NAME]);
                out.pprint(5,TYPE_TAG + " " + attributes[i][TYPE]);
                out.pprint(5,REST_TAG + " " + attributes[i][RESTRICTION]);
                out.pprint(5,DEFA_TAG + " " + attributes[i][DEFAULT]);
                out.pprint(4,END_ATTRIBUTE_ITEM);
              }
              out.pprint(3,END_ATTRIBUTE_LIST);
            }
            out.pprint(2,END_FACT_ITEM);
          }
        }
        out.pprint(1,END_FACTS);
      }

      out.pprint(0,END_ONTOLOGY);
      out.flush();
      out.close();

      if ( file.exists() ) file.delete();
      f1.renameTo(file);
      save_needed = false;
    }
    catch(Exception e) {
      error = e.toString();
      status |= ERROR_MASK;
    }
    setFilename(file);
    return status;
  }

  public int openFile(File file) {

    Assert.notNull(file);
    int status = 0;
    clear();

    try {
       OntologyParser parser = new OntologyParser(new FileInputStream(file));
       parser.parse(this);
    }
    catch(TokenMgrError t) {
      //If it's not in the Zeus format, hope it's DAML
      DAMLReader reader = new DAMLReader(this);
      reader.read(file);
    }
    catch(Exception e) {
      clear();
      error = e.toString();
      status |= ERROR_MASK;
    }
    if ( warning != null ) status |= WARNING_MASK;
    setFilename(file);
    fireChanged(RELOAD,file.getName());
    save_needed = false;
    return status;
  }

  private void setFilename(File file) {
    try {
       ontologyName = file.getName(); 
       filename = file.getCanonicalPath();
    }
    catch(IOException e) {
       ontologyName = file.getName(); 
       filename = file.getAbsolutePath();
    }
  }

  // -- FACT QUERY METHODS -----------------------------------------
  public boolean isAncestorOf(String name, String parent) {
     FactDescription fd;
     TreeNode node = (TreeNode)factIndex.get(name);
     while( node != null ) {
        fd = (FactDescription)node.getValue();
        if ( fd.getName().equals(parent) ) return true;
        node = node.getParent();
     }
     return false;
  }
  
  
  /** 
    *  get a list of the ancestors of a type 
    *@since 1.2.2
    *@author Simon Thompson
    */
  public Iterator allAncestors (String name) { 
     ArrayList list = new ArrayList ();       
     FactDescription fd;
     TreeNode node = (TreeNode)factIndex.get(name);
     while( node != null ) {
        fd = (FactDescription)node.getValue();
        list.add(fd.getName()); 
        node = node.getParent();
     }
     return list.iterator(); 
  }
  

  public TreeNode addChildFact(TreeNode parent) {
     String name = genSym.plainId(FACT_STR);
     while( factIndex.containsKey(name) )
        name = genSym.plainId(FACT_STR);
     TreeNode node =  parent.addChild(new FactDescription(name));
     factIndex.put(name, node);
     fireChanged(FACT_ADDED,name);
     return node;
  }

  /**
   * New method added by Jaron to allow named facts to be added through an
   * API call rather than the FactTable GUI
   */
  public boolean addNamedChildFact(TreeNode parent, String name)
  {
    if (factIndex.containsKey(name))
      return false;
    TreeNode node = parent.addChild(new FactDescription(name));
    factIndex.put(name, node);
    fireChanged(OntologyDb.RELOAD, name);
    return true;
  }

  public void __addChildFact(String parent, String name) {
     TreeNode parentNode = (TreeNode)factIndex.get(parent);
     TreeNode node =  parentNode.addChild(new FactDescription(name));
     factIndex.put(name, node);
  }

  public Object renameFact(String previous, String current) {
     if ( factIndex.containsKey(current) ) return null;
     if ( Misc.member(previous,PREDEFINED_FACTS) ) return null;

     TreeNode node = (TreeNode)factIndex.remove(previous);
     Assert.notNull(node);
     FactDescription desc = (FactDescription)node.getValue();
     desc.setName(current);
     factIndex.put(current,node);
     fireChanged(FACT_RENAMED,current);
     // REM propagate changes to all other facts (embedded attributes) ?
     return node;
  }

  public boolean isFactEditable(String name) {
     return !Misc.member(name,PREDEFINED_FACTS);
  }

  public void removeFact(TreeNode node) {
     // excise branch from tree
     TreeNode parent = node.getParent();
     parent.removeChild(node);

     // remove entries from factIndex table
     FactDescription desc;
     Enumeration enum = node.values();
     while(enum.hasMoreElements()) {
        desc = (FactDescription)enum.nextElement();
        factIndex.remove(desc.getName());
     }
     desc = (FactDescription)node.getValue();
     fireChanged(FACT_REMOVED,desc.getName());
  }

  public TreeNode copyFactTree(TreeNode node) {
     FactDescription desc1 = (FactDescription)node.getValue();
     FactDescription desc2 = new FactDescription(desc1);
     TreeNode top_node = new TreeNode(desc2);
     copyFactTree1(top_node,node);
     return top_node;
  }

  protected void copyFactTree1(TreeNode top_node, TreeNode node) {
     FactDescription desc1, desc2;
     Vector children = node.getChildren();
     TreeNode lhs_node, rhs_node;
     for(int i = 0; i < children.size(); i++ ) {
        rhs_node = (TreeNode)children.elementAt(i);
        desc1 = (FactDescription)rhs_node.getValue();
        desc2 = new FactDescription(desc1);
        lhs_node = new TreeNode(desc2);
        top_node.addChild(lhs_node);
        copyFactTree1(lhs_node,rhs_node);
     }
  }

  public TreeNode pasteFactTree(TreeNode parent, TreeNode node) {
     TreeNode copy = copyFactTree(node);
     TreeNode a_node;
     FactDescription desc;
     String name;
     Enumeration enum = copy.nodes();
     while(enum.hasMoreElements()) {
        a_node = (TreeNode)enum.nextElement();
        desc = (FactDescription)a_node.getValue();
        name = desc.getName();
        while( factIndex.containsKey(name) )
           name = genSym.plainId(desc.getName() + CSEP);
        desc.setName(name);
        factIndex.put(name,a_node);
     }
     parent.addChild(copy);
     desc = (FactDescription)copy.getValue();
     fireChanged(FACT_ADDED,desc.getName());
     return copy;
  }

  public boolean hasFact(String fact) { return factIndex.containsKey(fact); }


  /**
   * This method is used to create a new Fact from its ontology description
   */
  public Fact getFact(boolean is_variable, String type) {
     if ( !factIndex.containsKey(type) ) {
        Core.USER_ERROR("Fact type: " + type +
                        " does not exist in current ontology");
        return null;
     }
     ValueFunction fn;
     Fact f1 = new Fact(is_variable,type,this,genSym);
     AttributeList attrList = new AttributeList();
     String[][] entry = getNetAttributeEntriesFor(type);
     String value;
     for(int i = 0; i < entry.length; i++ ) {
        value = entry[i][1];
        if (  is_variable || value == null || value.equals("") )
           value = Fact.newVar(genSym);
        fn = ZeusParser.Expression(this,value);
        attrList.put(entry[i][0],fn);
     }
     f1.setAttributeList(attrList);
     return f1;
  }

  // -- ATTRIBUTE QUERY METHODS --------------------------------------

  public String[][] getAttributeEntriesFor(String fact) {
     if ( fact == null || fact.equals("") ) return new String[0][4];

     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     String[][] result = desc.getAttributes();

     editableLimit = Misc.member(fact,PREDEFINED_FACTS)
                     ? result.length : 0;

     return result;
  }
  public String[][] getAllAttributeEntriesFor(String fact) {
     if ( fact == null || fact.equals("") ) return new String[0][4];

     // first collect all attributes: node --> parent --> grandparent --> etc
     // and while doing so, count attributes

     FactDescription desc;
     Vector temp = new Vector();
     int sum = 0;
     String[][] attributes;
     TreeNode node = (TreeNode)factIndex.get(fact);
     while( node != null ) {
        desc = (FactDescription)node.getValue();
        attributes = desc.getAttributes();
        sum += attributes.length;
        temp.addElement(attributes);
        node = node.getParent();
     }
     // next concatenate
     String[][] result = new String[sum][4];
     int k = 0;
     for(int i = temp.size() - 1; i >= 0; i-- ) {
        attributes = (String[][])temp.elementAt(i);
        for(int j = 0; j < attributes.length; j++ )
           result[k++] = attributes[j];
     }

     if ( Misc.member(fact,PREDEFINED_FACTS) )
        editableLimit = result.length;
     else
        editableLimit = result.length -
                        ((String[][])temp.elementAt(0)).length;

     return result;
  }
  public int getEditableLimit() {
     return editableLimit;
  }

  String[][] getNetAttributeEntriesFor(String fact) {
     if ( fact == null || fact.equals("") ) return new String[0][2];

     //  collect all attributes: node --> parent --> grandparent --> etc
     FactDescription desc;
     Hashtable temp = new Hashtable();
     TreeNode node = (TreeNode)factIndex.get(fact);
     String[][] attributes;
     String default_value;
     while( node != null ) {
        desc = (FactDescription)node.getValue();
        attributes = desc.getAttributes();
        for(int j = 0; j < attributes.length; j++ ) {
           if ( attributes[j][DEFAULT] == null )
              attributes[j][DEFAULT] = "";
           default_value = (String)temp.get(attributes[j][NAME]);
           if ( default_value == null || default_value.equals("") )
              default_value = attributes[j][DEFAULT];
           temp.put(attributes[j][NAME],default_value);
        }
        node = node.getParent();
     }

     String[][] result = new String[temp.size()][2];
     Enumeration enum = temp.keys();
     for(int i = 0; enum.hasMoreElements(); i++ ) {
        result[i][0] = (String)enum.nextElement();
        result[i][1] = (String)temp.get(result[i][0]);
     }
     return result;
  }

  String[] getNetAttributesOnlyFor(String fact) {
     if ( fact == null || fact.equals("") ) return new String[0];

     //  collect all attributes: node --> parent --> grandparent --> etc
     FactDescription desc;
     HSet temp = new HSet();
     TreeNode node = (TreeNode)factIndex.get(fact);
     String[][] attributes;
     while( node != null ) {
        desc = (FactDescription)node.getValue();
        attributes = desc.getAttributes();
        for(int j = 0; j < attributes.length; j++ )
           temp.add(attributes[j][NAME]);
        node = node.getParent();
     }

     String[] result = new String[temp.size()];
     Enumeration enum = temp.elements();
     for(int i = 0; enum.hasMoreElements(); i++ )
        result[i] = (String)enum.nextElement();
     return result;
  }

  public String setAttribute(String fact, String attribute,
                             int column, String value) {
     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     String[] entry = desc.removeAttributeEntry(attribute);
     switch(column) {
        case NAME:
             String value1 = value;
             while( desc.containsAttribute(value1) )
                value1 = genSym.plainId(value + CSEP);
             entry[NAME] = value1;
             desc.setAttributeEntry(entry);
             break;

        case TYPE:
        case RESTRICTION:
        case DEFAULT:
             entry[column] = value;
             desc.setAttributeEntry(entry);
             break;

        default:
             Core.FAIL("Unknown type in OntologyDb.setAttribute()");
     }
     fireChanged(ATTRIBUTES_CHANGED,fact);
     return value;
  }

  public void addNewAttributeRow(String fact) 
  {
    TreeNode node = (TreeNode)factIndex.get(fact);
    FactDescription desc = (FactDescription)node.getValue();

    String name = genSym.plainId(NAME_STR);
    while( desc.containsAttribute(name) )
      name = genSym.plainId(NAME_STR);

    String[] entry = { name, STRING, "", "" };
    desc.setAttributeEntry(entry);
    fireChanged(ATTRIBUTES_CHANGED,fact);
  }

  public void addNewAttribute(String fact, String name, String type)
  {
    TreeNode node = (TreeNode)factIndex.get(fact);
    FactDescription desc = (FactDescription)node.getValue();
    String[] entry = { name, type, "", "" };
    desc.setAttributeEntry(entry);
  }


  public void __addNewAttributeRow(String fact, String name, String type,
                            String restriction, String default_value) {
     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     String[] entry = { name, type, restriction, default_value };
     desc.setAttributeEntry(entry);
  }

  public void deleteAttributes(String fact, String[] attributes) {
     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     for(int i = 0; i < attributes.length; i++ )
        desc.removeAttributeEntry((String)attributes[i]);
     fireChanged(ATTRIBUTES_CHANGED,fact);
  }

  public void addAttributeRows(String fact, String[][] input) {
     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     for(int i = 0; i < input.length; i++) {
        String name = input[i][NAME];
        while( desc.containsAttribute(name) )
           name = genSym.plainId(input[i][NAME] + CSEP);
        input[i][NAME] = name;
        desc.setAttributeEntry(input[i]);
     }
     fireChanged(ATTRIBUTES_CHANGED,fact);
  }

  public TreeNode createAttributeTree(String fact, String name) {
     Core.ERROR(factIndex.containsKey(fact),1,this);
     TreeNode node = new TreeNode(name);
     createAttributeTreeNodes(fact,node);
     return node;
  }

  protected void createAttributeTreeNodes(String fact, TreeNode base) {
     // first collect all attributes: node --> parent --> grandparent --> etc
     FactDescription desc;
     Vector temp = new Vector();
     TreeNode node = (TreeNode)factIndex.get(fact);
     String[][] attributes;
     while( node != null ) {
        desc = (FactDescription)node.getValue();
        attributes = desc.getAttributes();
        temp.addElement(attributes);
        node = node.getParent();
     }

     // next concatenate
     OrderedHashtable temp2 = new OrderedHashtable();
     for(int i = 0; i < temp.size(); i++ ) {
        attributes = (String[][])temp.elementAt(i);
        for(int j = 0; j < attributes.length; j++ )
     temp2.put(attributes[j][NAME],attributes[j][TYPE]);
     }

     String name, type;
     TreeNode child;
     Enumeration enum = temp2.keys();
     for(int i = 0; enum.hasMoreElements(); i++ ) {
        name = (String)enum.nextElement();
        child = base.addChild(name);

        type = (String)temp2.get(name);
        if ( factIndex.containsKey(type) )
           createAttributeTreeNodes(type,child);
     }
  }

  // -- RESTRICTIONS QUERY METHODS -----------------------------------

  public String[][] getAllRestrictions() {
     String[][] data = new String[restrictions.size()][3];
     Enumeration elements = restrictions.elements();
     for(int i = 0; i < data.length; i++ )
        data[i] = (String[])elements.nextElement();
     return data;
  }
  public String[] getAllRestrictionNames() {
     String[] data = new String[restrictions.size()];
     String[] temp;
     Enumeration elements = restrictions.elements();
     for(int i = 0; i < data.length; i++ ) {
        temp = (String[])elements.nextElement();
        data[i] = temp[NAME];
     }
     return data;
  }
  public String setRestrictionData(String name, int column, Object aValue) {
     String[] entry = null;
     String value = (String)aValue;
     switch(column) {
        case NAME:
             while( restrictions.containsKey(value) )
                value = genSym.plainId((String)aValue + CSEP);
             entry = (String[])restrictions.get(name);
             String previousKey = entry[NAME];
             entry[column] = value;
             restrictions.reKey(previousKey,entry[NAME],entry);
             // REM propagate changes to all other restrictions and all facts?
             break;

        case TYPE:
        case RESTRICTION:
             entry = (String[])restrictions.get(name);
             entry[column] = value;
             break;
     }
     fireChanged(RESTRICTION_CHANGED,entry[NAME]);
     return entry[column];
  }
  public void addNewRestriction() {
     String name = genSym.plainId(NAME_STR);
     while( restrictions.containsKey(name) )
        name = genSym.plainId(NAME_STR);
     String[] entry = { name, STRING, "" };
     restrictions.put(entry[NAME],entry);
     fireChanged(RESTRICTION_ADDED,name);
  }
  public void deleteRestrictions(String[] names) {
     for(int i = 0; i < names.length; i++ ) {
        restrictions.remove(names[i]);
        fireChanged(RESTRICTION_REMOVED,names);
     }
  }
  public void addRestrictions(String[][] input) {
     for(int i = 0; i < input.length; i++) {
        String name = input[i][NAME];
        while( restrictions.containsKey(name) )
           name = genSym.plainId(input[i][NAME] + CSEP);
        input[i][NAME] = name;
        restrictions.put(input[i][NAME],input[i]);
        fireChanged(RESTRICTION_ADDED,input[i][NAME]);
     }
  }
  public void __addRestriction(String name, String type, String value) {
     String[] input = { name, type, value };
     restrictions.put(input[NAME],input);
  }

  // --------- Validity Checking ----------------------------------

  public boolean[] validateFact(Fact f1) {
     String[] attributes    = f1.listAttributes();
     ValueFunction[] values = f1.listValues();
     boolean[] result = new boolean[values.length];

     for(int i = 0; i < result.length; i++ )
        result[i] = validate(f1.getType(),attributes[i],values[i]);
     return result;
  }

  boolean validate(String fact, String attribute, ValueFunction value) {
// System.err.println("validate " + fact + " " + attribute + " " + value);
     TreeNode node = (TreeNode)factIndex.get(fact);
     if ( node == null )  {
// System.err.println("Return false 1");
        return false;
     }

     FactDescription desc = (FactDescription)node.getValue();
     while( !desc.containsAttribute(attribute) && node != null &&
            (node = node.getParent()) != null ) {
        desc = (FactDescription)node.getValue();
     }

     if ( node == null ) {
// System.err.println("Return false 2");
        return false;
     }

     String[] entry = desc.getAttributeEntry(attribute);
     String type = entry[TYPE];
     String b_type = getBasicType(type);
     ValueFunction v0 = null;

     String restriction = entry[RESTRICTION];
     if ( Misc.member(b_type,BASIC_TYPES) ) {
        v0 = checkRestrictionEntry(b_type,restriction);
        if ( v0 != null && !Misc.member(type,BASIC_TYPES) )
           v0 = checkRestriction(v0,type);
// System.err.println("v0 = " + v0);
        if ( v0 == null ) {
// System.err.println("Return false 4");
           return false;
        }
     }
     else {
        if ( !restriction.equals("") )  {
// System.err.println("Return false 5");
           return false;
        }
        v0 = new VarFn(Fact.newVar(genSym));
// System.err.println("v0 = " + v0);
     }

     value = value.unifiesWith(v0,new Bindings());
// System.err.println("value = " + value);
// System.err.println();

     return (value != null);
  }

  public boolean[][] getAllRestrictionValidityInfo() {
     boolean[][] output = new boolean[restrictions.size()][3];
     String name;
     Enumeration keys = restrictions.keys();
     for(int i = 0; i < output.length; i++ ) {
        name = (String)keys.nextElement();
        output[i] = isRestrictionValid(name);
     }
     return output;
  }
  public boolean[] isRestrictionValid(String name) {
     boolean[] output = new boolean[3];
     for(int i = 0; i < output.length; i++ )
        output[i] = true;

     if ( !restrictions.containsKey(name) )
        return output; // swing bug?

     output[RESTRICTION] = checkRestriction(null,name) != null;
     return output;
  }

  protected ValueFunction checkRestriction(ValueFunction child, String name) {
     String[] entry = (String[])restrictions.get(name);
     String basic_type = getRestrictionBasicType(name);
     ValueFunction v0 = checkRestrictionEntry(basic_type,entry[RESTRICTION]);

     if ( v0 != null && child != null )
        v0 = v0.unifiesWith(child,new Bindings());

     if ( basic_type.equals(entry[TYPE]) || (v0 == null) )
        return v0;

     return checkRestriction(v0,entry[TYPE]);
  }

  protected ValueFunction checkDefaultValue(String type, String value) {
     value = value.trim();
     if ( value.equals("") )
        return new VarFn(Fact.newVar(genSym));

     try {
        RestrictionParser parser;
        parser = new RestrictionParser(
           new ByteArrayInputStream(value.getBytes())
        );

        switch( Misc.whichPosition(type,BASIC_TYPES) ) {
           case 0: // STRING
              return parser.StringLiteral();
           case 1: // INTEGER
              return parser.IntegerExpression();
           case 2: // REAL
              return parser.RealExpression();
           case 3: // BOOLEAN
              return parser.BooleanLiteral();
           case 4: // DATE
              return parser.DateLiteral();
           case 5: // TIME
              return parser.TimeLiteral();
           case 6: // LIST
              return parser.VectorLiteral();
           default:
              return null;
        }
     }
     catch(Exception e) {
        return null;
     }
  }
  protected ValueFunction checkRestrictionEntry(String type, String value) {
     value = value.trim();
     if ( value.equals("") )
        return new VarFn(Fact.newVar(genSym));

     try {
        RestrictionParser parser;
        parser = new RestrictionParser(
           new ByteArrayInputStream(value.getBytes())
        );

        switch( Misc.whichPosition(type,BASIC_TYPES) ) {
           case 0: // STRING
              return parser.StringExpression();
           case 1: // INTEGER
              return parser.IntegerExpression();
           case 2: // REAL
              return parser.RealExpression();
           case 3: // BOOLEAN
              return parser.BooleanLiteral();
           case 4: // DATE
              return parser.DateExpression();
           case 5: // TIME
              return parser.TimeExpression();
           case 6: // LIST
              return parser.VectorLiteral();
           default:
              return null;
        }
     }
     catch(Exception e) {
        return null;
     }
  }

  protected String getRestrictionBasicType(String restriction) {
     String[] entry = (String[])restrictions.get(restriction);
     String type = entry[TYPE];
     if ( Misc.member(type,BASIC_TYPES) )
        return type;
     else
        return getRestrictionBasicType(type);
  }

  protected String getBasicType(String type) {
     if ( Misc.member(type,BASIC_TYPES) )
        return type;
     else if ( type.equals(OBJECT_TYPE) )
        return type;
     else if ( restrictions.containsKey(type) )
        return getRestrictionBasicType(type);
     else // isa fact
        return type;
  }

  public boolean[] isAttributeValid(String fact, String name) {
     boolean output[] = new boolean[4];
     for(int i = 0; i < output.length; i++ )
        output[i] = true;

     if ( fact == null || fact.equals("") ) return output;


     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     String[] entry = desc.getAttributeEntry(name);
     String type = entry[TYPE];
     String inherited_type = getInheritedTypeFor(fact,name);

     String b_type = getBasicType(type);
     String b_inherited_type = getBasicType(inherited_type);

     ValueFunction v0 = null;
     ValueFunction v1 = null;

     // TYPE check
     if ( !b_type.equals(b_inherited_type) && !hasFact(b_type) &&
          !isAncestorOf(type,b_inherited_type) )
       output[TYPE] = false;

     String restriction = entry[RESTRICTION];
     if ( Misc.member(b_type,BASIC_TYPES) ) {
          v0 = checkRestrictionEntry(b_type,restriction);
          if ( v0 != null && !Misc.member(type,BASIC_TYPES) )
             v0 = checkRestriction(v0,type);
          output[RESTRICTION] = (v0 != null);
     }
     else {
        output[RESTRICTION] = restriction.equals("");
     }

     String value = entry[DEFAULT];
     if ( Misc.member(b_type,BASIC_TYPES) ) {
        v1 = checkDefaultValue(b_type,value);
        if ( v1 != null && v0 != null )
           v1 = v1.unifiesWith(v0,new Bindings());
        output[DEFAULT] = (v1 != null);
     }
     else {
        output[DEFAULT] = value.equals("");
     }

     return output;
  }

  protected String getInheritedTypeFor(String fact, String name) 
  {
    FactDescription desc;
    String type = null;
    String[] entry;
    TreeNode node = (TreeNode)factIndex.get(fact);
    while( node != null )
    {
      desc = (FactDescription)node.getValue();
      if ( !desc.containsAttribute(name) )
        return type;
      entry = desc.getAttributeEntry(name);
      type = entry[TYPE];
      node = node.getParent();
    }
    return type;
  }

  public boolean[][] getValidityInfoFor(String fact) {
     if ( fact == null || fact.equals("") ) return new boolean[0][4];

     TreeNode node = (TreeNode)factIndex.get(fact);
     FactDescription desc = (FactDescription)node.getValue();
     String[][] data = desc.getAttributes();
     boolean[][] output = new boolean[data.length][];
     for(int i = 0; i < output.length; i++ )
        output[i] = isAttributeValid(fact,data[i][NAME]);
     return output;
  }

  public boolean[][] getAllValidityInfoFor(String fact) {
     if ( fact == null || fact.equals("") ) return new boolean[0][4];

     // first collect all attributes: node --> parent --> grandparent --> etc
     // and while doing so, count attributes

     FactDescription desc;
     Vector temp = new Vector();
     int sum = 0;
     boolean[][] attributes;
     String name;
     TreeNode node = (TreeNode)factIndex.get(fact);
     while( node != null ) {
        desc = (FactDescription)node.getValue();
        name = desc.getName();
        attributes = getValidityInfoFor(name);
        sum += attributes.length;
        temp.addElement(attributes);
        node = node.getParent();
     }
     // next concatenate
     boolean[][] result = new boolean[sum][4];
     int k = 0;
     for(int i = temp.size() - 1; i >= 0; i-- ) {
        attributes = (boolean[][])temp.elementAt(i);
        for(int j = 0; j < attributes.length; j++ )
           result[k++] = attributes[j];
     }
     return result;
  }

  // -- EVENTLISTENER METHODS ---------------------------------------

  public void addChangeListener(ChangeListener x) {
    changeListeners.add(ChangeListener.class, x);
  }

  public void removeChangeListener(ChangeListener x) {
    changeListeners.remove(ChangeListener.class, x);
  }

  // -- FIRE EVENT METHODS ------------------------------------------

  protected void fireChanged(int type, Object data) {
    save_needed = true;
    OntologyDbChangeEvent c = new OntologyDbChangeEvent(this,type,data);
    Object[] listeners = changeListeners.getListenerList();
    for (int i= listeners.length-2; i >= 0; i -=2) {
      if (listeners[i] == ChangeListener.class) {
        ChangeListener cl = (ChangeListener)listeners[i+1];
        cl.stateChanged(c);
      }
    }
  }

   public static void main(String[] arg) {
     String file = null;

     OntologyDb z = new OntologyDb(new GenSym("sym"));

     String dir = System.getProperty("user.dir") +
                  System.getProperty("file.separator");
     z.openFile(new File(dir + arg[0]));
   }

  public Hashtable getFactIndex() {
    return factIndex;
  }

}
