package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

public class DoorDetail
{
	PacketBuilder packet = new PacketBuilder ();
	
	public DoorDetail (DoorInstance door) {
		packet.writeByte (ServerOpcodes.DOOR_DETAIL);
		packet.writeWord (door.entrance.point.x);
		packet.writeWord (door.entrance.point.y);
		packet.writeByte (door.heading);
		packet.writeByte (!door.isOpened);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
