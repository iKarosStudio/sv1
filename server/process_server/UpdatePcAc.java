package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

public class UpdatePcAc
{
	PacketBuilder packet = new PacketBuilder ();
	
	public UpdatePcAc (PcInstance pc) {
		packet.writeByte (ServerOpcodes.NODE_DEF) ;
		packet.writeByte (pc.getAc ()); //Ac
		packet.writeByte (0) ; //fire
		packet.writeByte (0) ; //water
		packet.writeByte (0) ; //wind
		packet.writeByte (0) ; //earth
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
