package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class UpdateHp
{
	PacketBuilder builder = new PacketBuilder () ;
	
	public UpdateHp (int hp, int max_hp) {
		builder.writeByte (ServerOpcodes.HP_UPDATE) ;
		builder.writeWord (hp) ;
		builder.writeWord (max_hp) ;
	}
	
	public byte[] getRaw () {
		return builder.getPacket ();
	}
}
