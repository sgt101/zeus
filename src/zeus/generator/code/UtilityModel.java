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



package zeus.generator.code;

import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;

import zeus.util.*;

public abstract class UtilityModel extends AbstractTableModel
                                   implements ChangeListener {

  protected boolean changed = false;

  public abstract void addNewRow();
  public abstract void removeRows(int[] rows);
  protected abstract void refresh();

  protected String updateString(String prev, Object aValue, boolean mode) {
     String value = updateString(prev,aValue);
     if ( value != null )
        return value;
     else {
        if ( mode )
	   return value;
	else {
	   changed = false;
	   return prev;
        }
     }
  }
  protected String updateString(String prev, Object aValue) {
     String value;
     changed = false;
     if ( prev == null ) {
        if ( aValue == null )
           return prev;
        else {
           value = ((String)aValue).trim();
           value = value.trim();
           if ( value.equals("") )
	      return null;
	   else {
	      changed = true;
	      return value;
           }
        }
     }
     else {
        if ( aValue == null ) {
           changed = true;
           return null;
        }
        else {
           value = ((String)aValue).trim();
           value = value.trim();
           if ( value.equals("") ) {
              changed = true;
	      return null;
           }
	   else if ( prev.equals(value) ) {
	      return prev;
           }
           else {
              changed = true;
              return value;
           }
        }
     }
  }

  protected boolean updateBoolean(boolean prev, Object aValue) {
     boolean curr = ((Boolean)aValue).booleanValue();
     changed = (prev != curr);
     return curr;
  }

  public void stateChanged(ChangeEvent e) {
    // generationPlan state changed recompute all entries;
    refresh();
  }
}
