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



package zeus.generator.event;

import javax.swing.event.*;
import zeus.concepts.Fact;

public class FactModelEvent extends ChangeEvent {
  public static final int FACT_ADDED      = 0;
  public static final int FACT_REMOVED    = 1;
  public static final int FACT_ID_CHANGED = 2;

  protected Fact   fact;
  protected int    type = -1;
  protected String original = null, current = null;

  public FactModelEvent(Object source, Fact fact, int type) {
     super(source);
     this.fact = fact;
     this.type = type;
  }
  public FactModelEvent(Object source, Fact fact, int type,
                        String original, String current) {
     super(source);
     this.original = original;
     this.current = current;
     this.fact = fact;
     this.type = type;
  }
  public Fact   getFact()       { return fact; }
  public String getPreviousId() { return original; }
  public String getCurrentId()  { return current; }
  public int    getEventType()  { return type; }
}
