/*****************************************************************************
* orchardUI.java
*
* Interface between OrchardBot and its TraderFrontEnd GUI
*****************************************************************************/

import zeus.actors.*;
import zeus.agents.*;


public class orchardUI implements ZeusExternal
{
   public void exec(AgentContext agent)	 {
     TraderFrontEnd thiswin = new TraderFrontEnd(agent);
   }

   public void showMsg(String message) {
   }

   public static void main(String[] args) {
     orchardUI win = new orchardUI();
     win.exec(null);
   }
}