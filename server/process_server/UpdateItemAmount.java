package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.item.*;

public class UpdateItemAmount
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public UpdateItemAmount (ItemInstance item) {
		packet.writeByte (ServerOpcodes.ITEM_UPDATE_AMOUNT);
		packet.writeDoubleWord (item.uuid);
		packet.writeDoubleWord (item.count);
		packet.writeByte (0);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
