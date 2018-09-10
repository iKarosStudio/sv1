package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class UpdateMp
{
	PacketBuilder builder = new PacketBuilder () ;
	
	public UpdateMp (int mp, int max_mp) {
		builder.writeByte (ServerOpcodes.MP_UPDATE) ;
		builder.writeWord (mp) ;
		builder.writeWord (max_mp) ;
	}
	
	public byte[] getRaw () {
		return builder.getPacket () ;
	}
}
