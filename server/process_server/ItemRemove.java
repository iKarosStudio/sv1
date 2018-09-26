package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.item.*;

public class ItemRemove
{
	PacketBuilder packet = new PacketBuilder ();
	
	public ItemRemove (ItemInstance item) {
		packet.writeByte (ServerOpcodes.ITEM_REMOVE);
		packet.writeDoubleWord (item.uuid);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
