/*****************************************************************************
* shopUI.java
*
* Interface between ShopBot and its TraderFrontEnd GUI
*****************************************************************************/

import zeus.actors.*;
import zeus.agents.*;


public class shopUI implements ZeusExternal
{
   public void exec(AgentContext agent)
	 {
     TraderFrontEnd thiswin = new TraderFrontEnd(agent);
   }

   public void showMsg(String message) {
   }

   public static void main(String[] args)
	 {
     shopUI win = new shopUI();
     win.exec(null);
   }
}