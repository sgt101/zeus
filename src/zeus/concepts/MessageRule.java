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
/**
    MessageRule is an interface that is implemented by MessageRuleImpl. 
    The idea is that this specifices that you have some generic pattern matching 
    and action functionality to be applied in a messageHandler. 
    *@author Simon Thompson
    *@since 1.3
    */
public interface MessageRule { 
    
    public String getName();
    public MessagePattern getPattern(); 
    public MessageAction getAction(); 
    public void setAction (MessageAction act); 
    public void setPattern (MessagePattern pat); 
    public String toString(); 
    
    
    
}