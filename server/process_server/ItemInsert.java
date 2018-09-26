package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.item.*;

public class ItemInsert
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public ItemInsert (ItemInstance item) {
		packet.writeByte (ServerOpcodes.ITEM_INSERT);
		packet.writeDoubleWord (item.uuid);
		packet.writeByte (item.useType);
		packet.writeByte (0) ;
		packet.writeWord (item.gfxInBag);
		packet.writeByte (item.bless);
		packet.writeDoubleWord (item.count);
		packet.writeByte (item.isIdentified);
		packet.writeString (item.getName ());
		if (item.isIdentified) {
			byte[] detail = item.getDetail ();
			packet.writeByte (detail.length);
			packet.writeByte (detail);
		} else {
			packet.writeByte (0);
		}
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
