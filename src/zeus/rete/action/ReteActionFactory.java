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

package zeus.rete.action;
import zeus.rete.*;

/** 
    this class was written a an attempted response to the difficulties of 
    understanding what is going on in the rete engine, and also in an 
    attempt to provide a mechanism that can be used to easily extend the 
    language without having to edit, like, 1000 files
    @author Simon Thompson
    @see zeus.rete.action.ActionFactory
    @see zeus.rete.action.AbstractActionFactory
    @see zeus.rete.ReteEngine
    @since 1.1
    */
public class ReteActionFactory implements ActionFactory {
   
    
   public static final int ASSERT    = 0;
   public static final int RETRACT   = 1;
   public static final int MODIFY    = 2;
   public static final int PRINT     = 3;
   public static final int MESSAGE   = 4;
   public static final int ACHIEVE   = 5;
   public static final int BUY       = 6;
   public static final int SELL      = 7;
   public static final int EXECUTE   = 8;

   public static final int BIND      = 9;
   public static final int IF        = 10;
   public static final int WHILE     = 11;
   public static final int OPEN      = 12;
   public static final int CLOSE     = 13;
   public static final int READ      = 14;
   public static final int READLN    = 15;
   public static final int SYSTEM    = 16;
   public static final int CALL      = 17;
   public static final int PRINTLN   = 18;

    
    /** 
        return an action of the appropriate type, if the type is not supported then 
        throw a NoSuchActionException and hope it gets handled!
        */
    public BasicAction getAction (int type) throws NoSuchActionException { 
        switch (type) { 
            case ASSERT: 
            return (BasicAction) new AssertAction(); 
            case RETRACT: 
            return (BasicAction) new RetractAction(); 
            case MODIFY: 
            return (BasicAction) new ModifyAction(); 
            case PRINT: 
            return (BasicAction) new PrintAction(); 
            case MESSAGE:
            return (BasicAction) new MessageAction(); 
            case ACHIEVE: 
            return (BasicAction) new AchieveAction(); 
            case BUY: 
            return (BasicAction) new BuyAction(); 
            case SELL: 
            return (BasicAction) new SellAction(); 
            case EXECUTE: 
            return (BasicAction) new ExecuteAction(); 
            case BIND: 
            return (BasicAction) new BindAction(); 
            case IF : 
            return (BasicAction) new IfAction(); 
            case WHILE:
            return (BasicAction) new WhileAction(); 
            case OPEN:  
            return (BasicAction) new OpenAction(); 
            case CLOSE: 
            return (BasicAction) new CloseAction(); 
            case READ:
            return (BasicAction) new ReadAction();
            case READLN: 
            return (BasicAction) new ReadlnAction(); 
            case SYSTEM: 
            return (BasicAction) new SystemAction(); 
            case CALL:
            return (BasicAction) new CallAction (); 
            case PRINTLN:
            return (BasicAction) new PrintlnAction ();
            default :
                    throw new NoSuchActionException("ReteActionFactory does not support that type of action"); 
        }
            
    
}
}