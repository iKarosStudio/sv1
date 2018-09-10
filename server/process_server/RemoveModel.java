package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class RemoveModel
{
	private PacketBuilder packet = new PacketBuilder ();
	
	public RemoveModel (int _uuid) {
		packet.writeByte (ServerOpcodes.REMOVE_OBJECT);
		packet.writeDoubleWord (_uuid);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
