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



/*****************************************************************************
* SocietyModelEntry.java
*
*****************************************************************************/

package zeus.visualiser.society;

import java.util.*;

import zeus.util.*;
import zeus.concepts.Relationship;

public class SocietyModelEntry {

  protected String  name = null;
  protected String  icon = null;
  protected Vector  relations = new Vector();

  SocietyModelEntry(String name, String icon) {
     Core.ERROR(name,1,this);
     this.name = name;
     this.icon = icon;
  }

  String getName()            { return name; }
  String getIcon()            { return icon; }
  void   setIcon(String icon) { this.icon = icon; }

  Relationship[] getRelations() {
     Relationship[] output = new Relationship[relations.size()];
     for(int i = 0; i < output.length; i++ )
        output[i] = (Relationship)relations.elementAt(i);
     return output;
  }

  void addRelations(Vector input) {
     Relationship relation;
     for(int i = 0; i < input.size(); i++ ) {
        relation = (Relationship)input.elementAt(i);
        if ( !relations.contains(relation) ) 
           relations.addElement(relation);
     }
  }
}
