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



package zeus.concepts;

import java.util.*;
import zeus.util.*;


public class Relationship {
  protected String relation;
  protected String name;

  protected Relationship() {
  }

  public Relationship(Relationship data) {
    setName(data.getName());
    setRelation(data.getRelation());
  }

  public Relationship(String name, String relation) {
    setName(name);
    setRelation(relation);
  }

  public void setRelation( String name ) {
    Assert.notNull(name);
    relation = name;
  }

  public void setName(String name) {
     Assert.notNull(name);
     this.name = name;
  }

  public String getRelation()  { return relation; }
  public String getName()      { return name; }

  public String toString() {
    String s = new String("(");

    s += ":name " + name + " ";
    s += ":relation " + relation + " ";

    s = s.trim() + ")";
    return s;
  }

  public String pprint() {
    return pprint(0);
  }

  public String pprint(int sp) {
    String tabs = Misc.spaces(sp);
    String eol  = "\n" + tabs + " ";

    String s = new String("(");
    s += ":name " + name + eol;
    s += ":relation " + relation + eol;

    return s.trim() + "\n" + tabs + ")";
  }
}
