package zeus.concepts;

public class Restriction {

  private String taskName;
  private String factName;
  private String attributeName;
  private String restriction;

  public Restriction(String task, String fact,
		     String attribute, String value) {

    setTaskName(task);
    setFactName(fact);
    setAttributeName(attribute);
    setRestriction(value);
  }

  public void setTaskName(String task) {
    this.taskName = task;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setFactName(String fact) {
    this.factName = fact;
  }

  public String getFactName() {
    return factName;
  }

  public void setAttributeName(String attribute) {
    this.attributeName = attribute;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setRestriction(String value) {
    if(value.trim().startsWith("\"") &&
       value.trim().endsWith("\"")) {

      value = value.trim();
      value = value.substring(1, value.length() - 1);
    }
    this.restriction = value;
  }

  public String getRestriction() {
    return restriction;
  }

  public boolean sameTarget(Restriction item) {
    if(item.getTaskName().equals(taskName) &&
       item.getFactName().equals(factName) &&
       item.getAttributeName().equals(attributeName)) {
      return true;
    }
    else {
      return false;
    }
  }

  public String toString() {
    return pprint();
  }

  public String pprint() {
    return pprint(0);
  }

  public String pprint(int spaces) {

    String tabs = zeus.util.Misc.spaces(spaces);
    String eol  = "\n" + tabs + " ";
    
    String s = new String("(");
    s += ":task " + taskName + eol;
    s += ":fact " + factName + eol;
    s += ":attribute " + attributeName + eol;
    s += ":value \"" + restriction + "\"" + eol;
    return s.trim() + "\n" + tabs + ")";

  }
}
