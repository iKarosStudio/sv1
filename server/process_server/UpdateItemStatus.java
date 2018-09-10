package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.item.*;

public class UpdateItemStatus
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public UpdateItemStatus (ItemInstance item) {
		packet.writeByte (ServerOpcodes.ITEM_UPDATE_STATUS);
		packet.writeDoubleWord (item.uuid);
		packet.writeString (item.getName ());
		packet.writeDoubleWord (item.count);
		
		if (item.isIdentified) {
			byte[] detail = item.getDetail ();
			packet.writeByte (detail.length);
			for (byte b : detail) {
				packet.writeByte (b);
			}
		} else {
			packet.writeByte (0);
		}
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
