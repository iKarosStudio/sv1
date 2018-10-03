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
		
		System.out.printf ("npc request npcid:%d type:%d\n", npcId, reqType);
		
		switch (reqType) {
		case 0: //Shop buy
			parseBuyList (handle, data);
			break;
			
		case 1: //Shop sell
			parseSellList (npcId, size, data); //find C_Result.java:93
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
	
	void parseSellList (int npcId, int size, byte[] data) {
		String htmlId = null;
		
		if (npcId == 70523 || npcId == 70805) { // ???or ?∴??
			htmlId = "ladar2";
			System.out.printf ("特殊NPC處理 查NpcRequest.java:159\n");
			
		} else if (npcId == 70537 || npcId == 70807) { // ?橘??喉???or ?橘???
			htmlId = "farlin2";
			System.out.printf ("特殊NPC處理 查NpcRequest.java:163\n");
			
		} else if (npcId == 70525 || npcId == 70804) { // ???? or ?∴???
			htmlId = "lien2";
			System.out.printf ("特殊NPC處理 查NpcRequest.java:169\n");
			
		} else if (npcId == 50527 || npcId == 50505 || npcId == 50519
				|| npcId == 50545 || npcId == 50531 || npcId == 50529
				|| npcId == 50516 || npcId == 50538 || npcId == 50518
				|| npcId == 50509 || npcId == 50536 || npcId == 50520
				|| npcId == 50543 || npcId == 50526 || npcId == 50512
				|| npcId == 50510 || npcId == 50504 || npcId == 50525
				|| npcId == 50534 || npcId == 50540 || npcId == 50515
				|| npcId == 50513 || npcId == 50528 || npcId == 50533
				|| npcId == 50542 || npcId == 50511 || npcId == 50501
				|| npcId == 50503 || npcId == 50508 || npcId == 50514
				|| npcId == 50532 || npcId == 50544 || npcId == 50524
				|| npcId == 50535 || npcId == 50521 || npcId == 50517
				|| npcId == 50537 || npcId == 50539 || npcId == 50507
				|| npcId == 50530 || npcId == 50502 || npcId == 50506
				|| npcId == 50522 || npcId == 50541 || npcId == 50523
				|| npcId == 50620 || npcId == 50623 || npcId == 50619
				|| npcId == 50621 || npcId == 50622 || npcId == 50624
				|| npcId == 50617 || npcId == 50614 || npcId == 50618
				|| npcId == 50616 || npcId == 50615) { // ??梧NPC
			/* 賣盟屋處理
			String sellHouseMessage = sellHouse(pc, objid, npcId);
			if (sellHouseMessage != null) {
				htmlId = sellHouseMessage;
			}*/
			System.out.printf ("特殊NPC處理 查NpcRequest.java:171\n");
		} else {
			//一般商店處理
			for (int index = 0; index < size; index++) {
				int solduuid = packetReader.readDoubleWord ();
				int soldcount = packetReader.readDoubleWord ();
			}
			
			//賣掉換錢
			
			//pc.sendPackets(new S_ShopBuyList(objid, pc));
			pc.getHandle ().sendPacket (new NpcNothingForSell (npcId).getRaw());
		}
	}
}
