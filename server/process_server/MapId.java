package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class MapId
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public MapId (int mapId) {
		packet.writeByte (ServerOpcodes.MAP_ID) ;
		packet.writeWord (mapId);
		packet.writeByte (0x00); //1->underwater
		packet.writeByte (0x00);
		packet.writeWord (0x0000);
		packet.writeByte (0x00);
		packet.writeDoubleWord (0x00000000);
	}
	
	public byte[] getRaw () {
		return packet.getPacket () ;
	}
}
