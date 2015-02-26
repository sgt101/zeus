/*****************************************************************************
* supplyUI.java
*
* Interface between SupplyBot and its TraderFrontEnd GUI
*****************************************************************************/

import zeus.actors.*;
import zeus.agents.*;


public class supplyUI implements ZeusExternal
{
   public void exec(AgentContext agent)
	 {
     TraderFrontEnd thiswin = new TraderFrontEnd(agent);
   }

   public void showMsg(String message) {
   }

   public static void main(String[] args)
	 {
     supplyUI win = new supplyUI();
     win.exec(null);
   }
}