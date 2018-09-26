package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.model.*;
import vidar.game.model.item.*;
import vidar.game.model.item.potion.*;
import vidar.game.model.item.scroll.*;
import static vidar.game.template.ItemTypeTable.*;

public class ItemUse
{
	PcInstance pc;
	SessionHandler handle;
	
	public ItemUse (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data) ;
		
		this.handle = handle;
		pc = handle.getPc ();
		
		int itemUuid = packetReader.readDoubleWord () ;
		
		ItemInstance item = pc.findItemByUuid (itemUuid);
		if (item != null) {			
			/* 使用道具 */
			if (item.isItem ()) {
				System.out.printf ("使用道具-%s,major[%d],minor[%d],useType[%d]\n", item.name, item.majorType, item.minorType, item.useType);
				if (item.minorType == TYPE_ARROW) {
					//
				} else if (item.minorType == TYPE_WAND) {
					//
				} else if (item.minorType == TYPE_LIGHT) {
					//
				} else if (item.minorType == TYPE_GEM) {
					//
				} else if (item.minorType == TYPE_TOTEM) {
					//
				} else if (item.minorType == TYPE_FIRECRACKER) {
					//
				} else if (item.minorType == TYPE_POTION) {
					new UsePotion (pc, item) ;
					//Pc.removeItem (i.Uuid, 1) ;
				} else if (item.minorType == TYPE_FOOD) {
					//
				} else if (item.minorType == TYPE_SCROLL) {
					int targetUuid = packetReader.readDoubleWord () ;
					//new UseScroll (Pc, i, target_uuid) ;
				} else if (item.minorType == TYPE_QUEST_ITEM) {
					//
				} else if (item.minorType == TYPE_SPELL_BOOK) {
					//
				} else if (item.minorType == TYPE_PET_ITEM) {
					//
				} else if (item.minorType == TYPE_OTHER) {
					if (item.id == 40310) { //一般信件
						int mailCode = packetReader.readWord () ;
						String mailReciever = packetReader.readString () ;
						byte[] mailText = packetReader.readRaw () ;
						
						System.out.printf ("Mail code:%d\n", mailCode) ;
						System.out.printf ("To:%s\n", mailReciever) ;
						System.out.println (mailText.toString ());
					}
					
					if (item.id == 40311) { //血盟信件
					}
					
					if (item.id >= 40373 && item.id <= 40390) { //使用地圖
						//handle.sendPacket (new MapUse (item.uuid, item.id).getRaw () ) ;
					}
				} else if (item.minorType == TYPE_MATERIAL) {
					//
				} else if (item.minorType == TYPE_EVENT) {
					//
				} else if (item.minorType == TYPE_STING) {
					//
				} else { //未知道具 無法使用
					System.out.printf ("%s 使用未知種類道具%d(Type:%d)\n", pc.name, item.uuid, item.minorType) ;
				}
				
			/* 使用武器 */
			} else if (item.isWeapon ()) {
				pc.setWeapon (item.uuid);
				
				/* 更新腳色武器外型 */
				byte[] packet = new UpdateModelGfx (pc.uuid, pc.getWeaponGfx ()).getRaw ();
				handle.sendPacket (packet) ;
				pc.boardcastPcInsight (packet) ;
			
			/* 使用防具 */
			} else if (item.isArmor ()) {
				pc.setArmor (item.uuid);
				
			} else {
				System.out.printf ("%s 使用不明道具%d(Major type:%d)\n", pc.name, item.uuid, item.majorType) ;
			}
		}
	}
}
