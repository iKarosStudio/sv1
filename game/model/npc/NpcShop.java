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
	
	public boolean isExistItem (int itemId) {//<-需要優化效能; 重複利用過多
		boolean res = false;
		
		List<Integer> ids = new ArrayList<Integer> ();
		menu.forEach ((Integer order, NpcShopMenu m)->{
			ids.add (m.itemId);
		});
		
		if (ids.contains (itemId)) {
			res = true;
		} else {
			res = false;
		}
		
		return res;
	}
}
