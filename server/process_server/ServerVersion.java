package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class ServerVersion
{
	public ServerVersion (SessionHandler Handle) {
		PacketBuilder Builder = new PacketBuilder () ;
		ServerTime time = ServerTime.getInstance () ;
		
		Builder.writeByte (ServerOpcodes.SERVER_VERSION) ;
		Builder.writeByte (0x00) ;
		Builder.writeByte (0x02) ;
		Builder.writeDoubleWord (0x00009D7C) ;
		Builder.writeDoubleWord (0x0000791A) ;
		Builder.writeDoubleWord (0x0000791A) ;
		Builder.writeDoubleWord (0x00009DD1) ;
		Builder.writeDoubleWord (time.getTime () ) ; //time
		Builder.writeByte (0x00) ;
		Builder.writeByte (0x00) ;
		Builder.writeByte (0x03) ; //3:繁體中文
		
		Handle.sendPacket (Builder.getPacket () ) ;
	}
}
