package vidar.game.model.npc;

import vidar.server.*;
import vidar.server.process_server.*;

public class NpcActionCodeHandler
{
	public NpcActionCodeHandler (SessionHandler handle, int npcId, String actionCode) {
		
		//System.out.printf ("npc code:%s\n", ActionCode) ;
		
		switch (actionCode) {
		case "buy" :
			ReportNpcShop result = new ReportNpcShop (npcId) ;
			result.buyList ();
			handle.sendPacket (result.getRaw ());
			
			/*
			 * item id 取得方式參考IdFactory
			 */
			
			break;
		case "sell" :
			//
			break;
			
		case "retrieve" :
			//
			break;
			
		case "retrieve-pledge" :
			//
			break;
			
		default :
			System.out.printf ("unhandled action code:%s\n", actionCode) ;
			break;
		}
	}
}
