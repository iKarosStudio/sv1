package vidar.game.model.npc;

import java.util.*;

public class NpcShop
{
	public int npcId;
	public HashMap<Integer, NpcShopMenu> menu = new HashMap <Integer, NpcShopMenu> () ;
	
	public NpcShop (int npc_id) {
		npcId = npc_id;
	}
	
	public void addMenuItem (NpcShopMenu item) {
		menu.put (item.orderId, item) ;
	}
}
