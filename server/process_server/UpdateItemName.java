package vidar.server.process_server;

import vidar.server.opcodes.*;
import vidar.server.packet.*;
import vidar.game.model.item.*;

public class UpdateItemName
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public UpdateItemName (ItemInstance item) {
		packet.writeByte (ServerOpcodes.ITEM_UPDATE_NAME);
		packet.writeDoubleWord (item.uuid);
		packet.writeString (item.getName ());
	}
	
	/*
	 * 更新為指定名稱
	 */
	public UpdateItemName (ItemInstance item, String name) {
		packet.writeByte (ServerOpcodes.ITEM_UPDATE_NAME);
		packet.writeDoubleWord (item.uuid);
		packet.writeString (name);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
