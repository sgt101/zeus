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

public class AgentDescription {

  protected String name = SystemProps.getProperty("agent.default.name");
  protected String xClass = SystemProps.getProperty("agent.default.class");
  protected int    planner_width = SystemProps.getInt("agent.default.planner.processors");
  protected int    planner_length  = SystemProps.getInt("agent.default.planner.length");
  protected int    doublebook_fraction = SystemProps.getInt("agent.default.planner.doublebooking");
  protected Vector tasks = new Vector();
  protected Vector initialFacts = new Vector();
  protected Vector protocols = new Vector();
  protected Vector acquaintances = new Vector();
  protected List   restrictions = new Vector();

  public AgentDescription() {}

  public AgentDescription( AgentDescription agent ) {
    name = agent.getName();
    xClass = agent.getAgentClass();
    planner_width = agent.getPlannerWidth();
    planner_length = agent.getPlannerLength();
    doublebook_fraction = agent.getDoublebookFraction();

    setTasks( agent.getTasks() );
    setInitialFacts( agent.getInitialFacts() );
    setProtocols( agent.getProtocols() );
    setAcquaintances( agent.getAcquaintances() );
    setRestrictions(agent.getRestrictions());
  }

  public void setName( String s ) {
    Assert.notNull(s);
    name = s;
  }

  public void setAgentClass( String s ) {
    Assert.notNull(s);
    xClass = s;
  }

  public void setPlannerWidth( int t ) {
    Assert.notFalse( t >= SystemProps.getInt("planner.processors.min") &&
                     t <= SystemProps.getInt("planner.processors.max") );
    planner_width = t;
  }

  public void setPlannerLength( int t ) {
    Assert.notFalse( t >= SystemProps.getInt("planner.length.min") &&
                     t <= SystemProps.getInt("planner.length.max") );
    planner_length = t;
   }

  public void setDoublebookFraction( int t ) {
    Assert.notFalse( t >= SystemProps.getInt("planner.doublebooking.min") &&
                     t <= SystemProps.getInt("planner.doublebooking.max") );
    doublebook_fraction = t;
  }

  public void setTasks( Vector v ) {
    tasks.removeAllElements();
    if ( v == null ) return;
    for( int i = 0; i < v.size(); i++ )
       tasks.addElement(v.elementAt(i));
  }

  public void setTasks( String[] v ) {
    tasks.removeAllElements();
    if ( v == null ) return;
    for( int i = 0; i < v.length; i++ )
       tasks.addElement(v[i]);
  }

  public boolean removeTask(String id) {
     return tasks.removeElement(id);
  }
  public boolean containsTask(String id) {
     return tasks.contains(id);
  }

  public void setInitialFacts( Vector v ) {
    initialFacts.removeAllElements();
    if ( v == null ) return;
    for(int i = 0; i < v.size(); i++ )
       initialFacts.addElement(new Fact((Fact)v.elementAt(i)));
  }

  public void setInitialFacts( Fact[] v ) {
    initialFacts.removeAllElements();
    if ( v == null ) return;
    for(int i = 0; i < v.length; i++ )
       initialFacts.addElement(new Fact(v[i]));
  }

  public void setProtocols( Vector v ) {
    protocols.removeAllElements();
    if ( v == null ) return;
    for(int i = 0; i < v.size(); i++ )
       protocols.addElement(v.elementAt(i));
  }

  public void setProtocols( ProtocolInfo[] v ) {
    protocols.removeAllElements();
    if ( v == null ) return;
    for(int i = 0; i < v.length; i++ )
       protocols.addElement(v[i]);
  }

  public void setAcquaintances( Vector v ) {
    acquaintances.removeAllElements();
    if ( v == null ) return;
    for(int i = 0; i < v.size(); i++ )
       acquaintances.addElement(new Acquaintance((Acquaintance)v.elementAt(i)));
  }

  public void setAcquaintances( Acquaintance[] v ) {
    acquaintances.removeAllElements();
    if ( v == null ) return;
    for( int i = 0; i < v.length; i++ )
      acquaintances.addElement(new Acquaintance(v[i]));
  }

  public void setRestrictions(List restrictions) {
    if(restrictions != null) {
      this.restrictions = restrictions;
    }
  }

  public List getRestrictions() {
    return restrictions;
  }

  public String getName()                { return name; }
  public String getAgentClass()          { return xClass; }
  public int    getPlannerWidth()        { return planner_width; }
  public int    getPlannerLength()       { return planner_length; }
  public int    getDoublebookFraction()  { return doublebook_fraction; }

  public String[] getTasks() {
    String[] data = new String[tasks.size()];
    for( int i = 0; i < tasks.size(); i++ )
      data[i] = (String)tasks.elementAt(i);
    return data;
  }

  public ProtocolInfo[] getProtocols() {
    ProtocolInfo[] data = new ProtocolInfo[protocols.size()];
    for( int i = 0; i < protocols.size(); i++ )
      data[i] = (ProtocolInfo)protocols.elementAt(i);
    return data;
  }

  public Fact[] getInitialFacts() {
    Fact[] data = new Fact[initialFacts.size()];
    for( int i = 0; i < initialFacts.size(); i++ )
      data[i] = new Fact( (Fact)initialFacts.elementAt(i) );
    return data;
  }

  public Acquaintance[] getAcquaintances() {
    Acquaintance[] data = new Acquaintance[acquaintances.size()];
    for( int i = 0; i < acquaintances.size(); i++ )
      data[i] = new Acquaintance((Acquaintance)acquaintances.elementAt(i) );
    return data;
  }

  public String toString() {
    String s = "(:name " + name + " ";
    if ( xClass != null && !((xClass.trim()).equals("")) )
      s += ":class " + xClass + " ";
    s += ":planner_width " + planner_width + " ";
    s += ":planner_length " + planner_length + " ";
    s += ":doublebook_fraction " + doublebook_fraction + " ";

    if ( !tasks.isEmpty() ) {
      s += ":tasks (";
      for( int i = 0; i < tasks.size(); i++ )
        s += (String)tasks.elementAt(i) + " ";
      s = s.trim() + ") ";
    }
    if ( !initialFacts.isEmpty() ) {
      s += ":initial_facts (";
      for( int i = 0; i < initialFacts.size(); i++ )
        s += ((Fact) initialFacts.elementAt(i)).toString();
      s += ") ";
    }
    if ( !protocols.isEmpty() ) {
      s += ":protocols (";
      for( int i = 0; i < protocols.size(); i++ )
        s += protocols.elementAt(i) + " ";
      s = s.trim() + ") ";
    }
    if ( !acquaintances.isEmpty() ) {
      s += ":acquaintances (";
      for( int i = 0; i < acquaintances.size(); i++ )
        s += ((Acquaintance) acquaintances.elementAt(i)).toString();
      s += ") ";
    }
    if( !restrictions.isEmpty() ) {
      s += ":restrictions (";
      for( int i = 0 ; i < restrictions.size() ; i++) {
	s += ((Restriction)restrictions.get(i)).toString();
      }
      s += ") ";
    }
    return s.trim() + ")";
  }

  public String pprint() {
    return pprint(0);
  }

  public String pprint(int sp) {
    String suffix, prefix;
    String tabs = Misc.spaces(sp);
    String eol  = "\n" + tabs + " ";

    String s = "(:name " + name + eol;
    if ( xClass != null && !((xClass.trim()).equals("")) )
      s += ":class " + xClass + eol;
    s += ":planner_width " + planner_width + eol;
    s += ":planner_length " + planner_length + eol;
    s += ":doublebook_fraction " + doublebook_fraction + eol;

    if ( !tasks.isEmpty() ) {
      prefix = ":tasks ";
      suffix = Misc.spaces(1+ sp + prefix.length());
      s += prefix + "(";
      for( int i = 0; i < tasks.size(); i++ )
        s += ((String)tasks.elementAt(i)) + "\n" + suffix + " ";
      s = s.trim() + "\n" + suffix + ")" + eol;
    }
    if ( !initialFacts.isEmpty() ) {
      prefix = ":initial_facts ";
      suffix = Misc.spaces(1+ sp + prefix.length());
      s += prefix + "(";
      for( int i = 0; i < initialFacts.size(); i++ )
        s += ((Fact)initialFacts.elementAt(i)).pprint(1+suffix.length()) +
	  "\n" + suffix + " ";
      s = s.trim() + "\n" + suffix + ")" + eol;
      }
    if ( !protocols.isEmpty() ) {
      prefix = ":protocols ";
      suffix = Misc.spaces(1+ sp + prefix.length());
      s += prefix + "(";
      for( int i = 0; i < protocols.size(); i++ )
        s += ((ProtocolInfo)protocols.elementAt(i)).pprint(1+suffix.length()) +
           "\n" + suffix + " ";
      s = s.trim() + "\n" + suffix + ")" + eol;
    }
    if ( !acquaintances.isEmpty() ) {
      prefix = ":acquaintances ";
      suffix = Misc.spaces(1+ sp + prefix.length());
      s += prefix + "(";
      for( int i = 0; i < acquaintances.size(); i++ )
	s += ((Acquaintance)
              acquaintances.elementAt(i)).pprint(1+suffix.length()) +
	  "\n" + suffix + " ";
      s = s.trim() + "\n" + suffix + ")" + eol;
    }
    if( !restrictions.isEmpty() ) {
      prefix = ":restrictions ";
      suffix = Misc.spaces(1+ sp + prefix.length());
      s += prefix + "(";
      for( int i = 0 ; i < restrictions.size() ; i++) {
	s += ((Restriction)restrictions.get(i)).pprint(1 + suffix.length())
	  + "\n" + suffix + " ";
      }
      s = s.trim() + "\n" + suffix + ")" + eol;
    }
    return tabs + s.trim() + "\n" + tabs + ")";
  }
}
