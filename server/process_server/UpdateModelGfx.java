package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class UpdateModelGfx
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public UpdateModelGfx (int uuid, int gfx) {
		packet.writeByte (ServerOpcodes.UPDATE_PC_GFX) ;
		packet.writeDoubleWord (uuid);
		packet.writeByte (gfx);
	}
	
	public byte[] getRaw () {
		return packet.getPacket () ;
	}
}
