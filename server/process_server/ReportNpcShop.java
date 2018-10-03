package vidar.server.process_server;

import java.util.*;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.*;
import vidar.game.model.*;
import vidar.game.model.npc.*;
import vidar.game.model.item.*;
import vidar.game.template.*;

public class ReportNpcShop
{
	PacketBuilder packet = new PacketBuilder () ;
	public int npcId;
	
	public ReportNpcShop (int _npcId) {
		npcId = _npcId;
	}
	
	public void buyList () {
		NpcShop shop = CacheData.npcShop.get (npcId);
		HashMap<Integer, NpcShopMenu> menu = shop.menu;
		
		packet.writeByte (ServerOpcodes.NPC_SELL_LIST);
		packet.writeDoubleWord (npcId);
		packet.writeWord (menu.size ());
		
		for (int i = 0; i < menu.size (); i++) {
			byte[] detail = null;
			int invGfx = 0;
			int invPrice = 0;
			String invName = null;
			/*
			 * 解析道具單項細節
			 */
			if (CacheData.item.containsKey (menu.get (i).itemId)) {
				ItemTemplate item = CacheData.item.get (menu.get (i).itemId);
				invGfx = item.gfxInBag;
				invPrice = menu.get (i).sellingPrice;
				invName = item.name;
				detail = item.ParseItemDetail ();
				
			} else if (CacheData.weapon.containsKey (menu.get (i).itemId)) {
				WeaponTemplate weapon = CacheData.weapon.get (menu.get (i).itemId);
				invGfx = weapon.gfxInBag;
				invPrice = menu.get (i).sellingPrice;
				invName = weapon.name;
				detail = weapon.ParseWeaponDetail ();
				
			} else if (CacheData.armor.containsKey (menu.get (i).itemId)) {
				ArmorTemplate armor = CacheData.armor.get (menu.get (i).itemId);
				invGfx = armor.gfxInBag;
				invPrice = menu.get (i).sellingPrice;
				invName = armor.name;
				detail = armor.ParseArmorDetail ();
				
			} else {
				System.out.printf ("unknow itemid : %d\n", menu.get (i).itemId);
			}
			
			/*
			 * 單項標頭
			 */
			packet.writeDoubleWord (i) ; //index
			packet.writeWord (invGfx) ;
			packet.writeDoubleWord (invPrice) ;
			if (menu.get (i).packCount > 1) {
				String n = String.format ("%s(%d)", invName, menu.get(i).packCount) ;
				packet.writeString (n) ;
			} else {
				packet.writeString (invName) ;
			}
			
			/*
			 * 寫入每個道具細節數值
			 */
			packet.writeByte (detail.length) ;
			if (detail.length > 0) {
				for (byte b : detail) {
					packet.writeByte (b) ;
				}
			}
		} //Each of menu
	}
	
	public void sellList (PcInstance pc) {
		NpcShop shop = CacheData.npcShop.get (npcId);
		if (shop == null) {
			return;
		}
		
		List<ItemInstance> sellList = new ArrayList<ItemInstance> ();
		
		pc.itemBag.forEach ((Integer uuid, ItemInstance item)->{
			if (shop.isExistItem (item.id)) { //可以賣掉的東西
				sellList.add (item);
			}
		});
		
		if (sellList.size() < 1) {
			packet.writeByte (new NpcNothingForSell (npcId).getRaw());
			
		} else {
			packet.writeByte (ServerOpcodes.NPC_BUY_LIST);
			packet.writeDoubleWord (npcId);
			packet.writeWord (sellList.size ());
			for (int index = 0; index < sellList.size (); index++) {
				packet.writeDoubleWord (sellList.get (index).uuid); //item
				packet.writeDoubleWord (50); //顯示售價
			}
		}
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
