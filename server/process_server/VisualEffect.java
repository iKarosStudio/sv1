package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class VisualEffect
{
	PacketBuilder packet = new PacketBuilder ();
	
	public VisualEffect (int _uuid, int _gfxId) {
		packet.writeByte (ServerOpcodes.VISUAL_EFFECT);
		packet.writeDoubleWord (_uuid);
		packet.writeWord (_gfxId);
		packet.writeWord (0);
		packet.writeDoubleWord (0);
 	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}	
}
