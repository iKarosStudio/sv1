package vidar.server.process_client;

import java.util.*;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.*;
import vidar.game.model.*;
import vidar.game.model.npc.*;
import vidar.game.model.item.*;

/*
 * type 
 * 0 : shop buy (contains player's shop)
 * 1 : shop sell
 * 2 : storage put in
 * 3 : storage take out
 * 4 : clan storage put in
 * 5 : clan storage take out
 */

public class NpcRequest
{
	int npcId = 0;
	int reqType = 0;
	int size = 0;
	int unknown = 0;
	
	PcInstance pc = null;
	PacketReader packetReader = null;
	
	public NpcRequest (SessionHandler handle, byte[] data) {
		pc = handle.getPc ();
		packetReader = new PacketReader (data);
		
		npcId = packetReader.readDoubleWord ();
		reqType = packetReader.readByte ();
		size = packetReader.readByte ();
		unknown = packetReader.readByte ();
		
		
		switch (reqType) {
		case 0: //Shop buy
			parseBuyList (handle, data);
			break;
			
		case 1: //Shop sell
			
			break;
			
		case 2: //storage put in
			break;
			
		case 3: //storage take out
			break;
			
		case 4: //clan storage put in
			break;
			
		case 5: //clan storage take out
			break;
			
		default:
			System.out.println ("unknown npc request!\n") ;
			break;
		}
		
	}
	
	void parseBuyList (SessionHandler handle, byte[] data) {
		HashMap<Integer, NpcShopMenu> menu = CacheData.npcShop.get (npcId).menu;
		
		List<ItemInstance> orderedItems = new ArrayList<ItemInstance> ();
		int[] orderId    = new int[size];
		int[] orderCount = new int[size];
		int totalPrice = 0;
		int totalWeight = 0;
		
		for (int index = 0; index < size; index++) {
			orderId[index] = packetReader.readDoubleWord () ;
			orderCount[index]   = packetReader.readDoubleWord () ;  
			NpcShopMenu i = menu.get (orderId[index]) ;
			
			int packCount = menu.get (orderId[index]).packCount;
			boolean isStackable = menu.get (orderId[index]).isStackable ();
			
			if (isStackable) {
				if (packCount < 1) {
					packCount = 1;
				}
				
				int TotalAmount = orderCount[index] * packCount;
				
				ItemInstance item = menu.get (orderId[index]).getItem ();
				item.uuidOwner = pc.uuid;
				item.count = TotalAmount;
				totalPrice += item.count * i.sellingPrice;
				totalWeight += item.count * item.weight;
				orderedItems.add (item);
				
			} else {
				if (orderCount[index] > 1) {
					for (int j = 0; j < orderCount[index]; j++) {
						ItemInstance item = menu.get (orderId[index]).getItem ();
						item.uuidOwner = pc.uuid;
						item.count = 1;
						totalPrice += item.count * i.sellingPrice;
						totalWeight += item.count * item.weight;
						orderedItems.add (item);
					}
				} else {
					ItemInstance item = menu.get (orderId[index]).getItem ();
					item.uuidOwner = pc.uuid;
					item.count = 1;
					totalPrice += item.count * i.sellingPrice;
					totalWeight += item.count * item.weight;
					orderedItems.add (item);
				}
			}
		} //for each order
		
		if ((pc.getWeight () + totalWeight) > pc.getMaxWeight ()) {
			handle.sendPacket (new ServerMessage (82).getRaw ()); //超過最大負重
			return;
		}
		/* 扣錢 */
		/*
		if (Pc.getMoney () < totalPrice) {
			Handle.SendPacket (new ServerMessage (189).getRaw () ) ; //金幣不足
			return;
		}
		*/
		//Pc.removeItemByItemId (40308, TotalPrice) ;
		
		for (int index = 0; index < orderedItems.size () ; index++) {
			pc.addItem (orderedItems.get (index)) ;
		}
		
		/* PAY TAX */
		
		/*
		 * 更新資料庫
		 */
		//pc.saveItem ();
		
		/*
		 * 更新角色狀況
		 */
		handle.sendPacket (new NodeStatus (pc).getRaw ());
	}
}
