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



package zeus.util;

import java.util.*;


public class GenSym {
  protected static final String SEP = "_";

  protected String  name = "genSym";
  protected String  suffix = "";
  protected int     count = 0;

  public GenSym () {
    this.name = "Default"; 
    }

  public GenSym(String name) {
    Assert.notNull(name);
    this.name = name;
  }
  
  public GenSym(String name, boolean isRuntime) {
    this(name);
    if ( isRuntime )
       suffix = "_";
  }
  public synchronized void set(String name) {
    Assert.notNull(name);
    this.name = name;
  }

  public synchronized void set(int count) {
    if ( count > this.count )
    this.count = count;
  }
  public synchronized int getCount() {
    return count;
  }

  public synchronized String plainId(String prefix) {
     return prefix + suffix + (++count);
  }
  public synchronized String newId() {
     return name + (++count);
  }
  public synchronized String newId(String text) {
     return name + SEP + text + (++count);
  }
}
