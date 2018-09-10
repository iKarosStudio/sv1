package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.item.*; 

public class UpdateItemBless
{
	PacketBuilder builder = new PacketBuilder () ;
	
	public UpdateItemBless (ItemInstance item) {
		builder.writeByte (ServerOpcodes.ITEM_UPDATE_BLESS);
		builder.writeDoubleWord (item.uuid);
		builder.writeByte (item.bless);
	}

	public byte[] getRaw () {
		return builder.getPacket () ;
	}
}
