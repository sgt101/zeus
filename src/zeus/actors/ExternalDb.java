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
 * @(#)ExternalDb.java 1.00
 */

package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

/** 
 * If an agent needs a resource it will first examine its local resource
 * database, and if the necessary {@link Fact} is not present it then will consult its
 * external resource database (if it has one).  These external resource databases 
 * are linked to Zeus agents through this interface class, allowing the
 * developer to encapsulate an external system like a database in a way that 
 * can still be accessed by the agent. <p>
 * Details on how to use this interface are provided in Section 6 of the
 * Zeus Application Realisation Guide.
 <EM> ISSUES </EM> We are likely to want to connect more than one external interface
 to each agent - this interface doesn't provide for that so we need to do something 
 about it .....
 */

public interface ExternalDb
{
   /** A configuration method, used to associate the external resource with
       its owner agent. */
   public abstract void        set(AgentContext context);


   /** Implements a membership	operation, returning true if the fact parameter
       currently exists within the external resource */
   public abstract boolean     contains(Fact f1);


   /** Implements an insertion operation, returning true if the fact parameter
       was successfully inserted. Whether duplicates are permitted is at the
       discretion of the external resource that implements this service. */
   public abstract boolean     put(Fact f1);


   /** Implements a retrieval operation that returns the fact matching the 
       the parameter; this is assumed to be destructive */
   public abstract Fact        remove(Fact f1);


   /** Implements a query operation that should return an enumeration of all
       facts that match the parameter; this is not assumed to be destructive */
   public abstract Enumeration all(Fact f1);
}
