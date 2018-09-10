package vidar.server.process_server;

import java.util.concurrent.*;

import vidar.server.opcodes.*;
import vidar.server.packet.*;
import vidar.game.model.item.*;

public class ReportItemBag
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public ReportItemBag (ConcurrentHashMap<Integer, ItemInstance> itemBag) {
		
		packet.writeByte (ServerOpcodes.ITEM_LIST) ;
		packet.writeByte (itemBag.size ());
		
		itemBag.forEach ((Integer uuid, ItemInstance item)->{			
			packet.writeDoubleWord (uuid);
			packet.writeByte (item.useType);
			packet.writeByte (0);
			packet.writeWord (item.gfxInBag);
			packet.writeByte (item.bless);
			packet.writeDoubleWord (item.count);
			packet.writeByte (item.isIdentified);				
			packet.writeString (item.getName ());
			
			if (item.isIdentified) {
				byte[] itemDetail = null;
				Object i = item.getClass ();
				
				if (i instanceof WeaponInstance) {
					WeaponInstance w = (WeaponInstance) i;
					itemDetail = w.getDetail ();
				} else if (i instanceof ArmorInstance) {
					ArmorInstance a = (ArmorInstance) i;
					itemDetail = a.getDetail ();
				} else {
					itemDetail = item.getDetail ();
				}
				
				packet.writeByte (itemDetail.length);
				packet.writeByte (itemDetail);
			} else {
				packet.writeByte (0);
			}
		}) ;
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
