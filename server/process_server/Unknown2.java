package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class Unknown2
{
	public Unknown2 (SessionHandler Handle) 
	{
		PacketBuilder Builder = new PacketBuilder () ;
		Builder.writeByte (ServerOpcodes.UNKNOWN2) ;
		Builder.writeByte (0xFF) ;
		Builder.writeByte (0x7F) ;
		Builder.writeByte (0x03) ;
		Handle.sendPacket (Builder.getPacket () ) ;
	}
}
