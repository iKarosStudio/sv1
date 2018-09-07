package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class Unknown1
{
	public Unknown1 (SessionHandler Handle) {
		PacketBuilder Builder = new PacketBuilder () ;
		Builder.writeByte (ServerOpcodes.UNKNOWN1) ;
		Builder.writeByte (0x03) ;
		Builder.writeByte (0x00) ;
		Builder.writeByte (0xF7) ;
		Builder.writeByte (0xAD) ;
		Builder.writeByte (0x74) ;
		Builder.writeByte (0x00) ;
		Builder.writeByte (0xE5) ;
		Handle.sendPacket (Builder.getPacket () ) ;
	}
}
