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


public class Acquaintance extends Relationship {
  protected Vector abilities = new Vector(100);

  public Acquaintance () { 
  }

  public Acquaintance(Acquaintance child) {
    setName(child.getName());
    setRelation(child.getRelation());
    setAbilities(child.getAbilities());
  }

  public Acquaintance(String name, String relation) {
    super(name,relation);
  }

  public Acquaintance(String name, String relation, AbilitySpec[] abilities) {
    super(name,relation);
    setAbilities(abilities);
  }

  public Acquaintance(String name, String relation, Vector abilities) {
    super(name,relation);
    setAbilities(abilities);
  }

  public void setAbilities( Vector in ) {
     abilities.removeAllElements();
     for( int i = 0; in != null && i < in.size(); i++ )
        abilities.addElement( new AbilitySpec( (AbilitySpec)in.elementAt(i)) );
  }

  public void setAbilities( AbilitySpec[] in ) {
     abilities.removeAllElements();
     for( int i = 0; in != null && i < in.length; i++ )
        abilities.addElement( new AbilitySpec(in[i]) );
  }

  public AbilitySpec[] getAbilities() {
    AbilitySpec[] out = new AbilitySpec[abilities.size()];    
    for( int i = 0; i < abilities.size(); i++ )
       out[i] = (AbilitySpec)abilities.elementAt(i);
    return out;
  }
  
  public String toString() {
    String s = new String("(");
    
    s += ":name " + name + " ";
    s += ":relation " + relation + " ";
    
    if ( !abilities.isEmpty() ) {
      s += ":abilities (";
      for( int i = 0; i < abilities.size(); i++ )
	s += ((AbilitySpec)abilities.elementAt(i)).toString() + " ";
      s = s.trim() + ")";
    }
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
    
    if ( !abilities.isEmpty() ) {
      String prefix = ":abilities ";
      String suffix = Misc.spaces(1+ sp + prefix.length());
      s += prefix + "(";
      for( int i = 0; i < abilities.size(); i++ )
	s += ((AbilitySpec)
	      abilities.elementAt(i)).pprint(1+suffix.length());
      s = s.trim() + "\n" + suffix + ")" + eol;
    }
    return s.trim() + "\n" + tabs + ")";
  }
}
