package vidar.game.model.npc;

import vidar.server.*;
import vidar.server.process_server.*;

public class NpcActionCodeHandler
{
	public NpcActionCodeHandler (SessionHandler handle, int npcId, String actionCode) {
		System.out.printf ("npc action code handler npcid(%d) action:%s\n", npcId, actionCode);
		ReportNpcShop shopResult;
		
		switch (actionCode) {
		case "buy" :
			shopResult = new ReportNpcShop (npcId);
			shopResult.buyList ();
			handle.sendPacket (shopResult.getRaw ());
			break;
			
		case "sell" :
			shopResult = new ReportNpcShop (npcId);
			shopResult.sellList (handle.getPc ()); 
			handle.sendPacket (shopResult.getRaw ());
			break;
			
		case "retrieve" :
			//倉庫
			System.out.println ("retrieve");
			break;
			
		case "retrieve-pledge" :
			//血盟倉庫
			System.out.println ("retrieve-pledge");
			break;
			
		default :
			System.out.printf ("unhandled action code:%s\n", actionCode) ;
			break;
		}
	}
}
