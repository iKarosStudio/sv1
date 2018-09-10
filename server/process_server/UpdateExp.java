package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

public class UpdateExp
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public UpdateExp (PcInstance pc) {
		packet.writeByte (ServerOpcodes.UPDATE_EXP) ;
		packet.writeByte (pc.level);
		packet.writeDoubleWord (pc.exp);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
