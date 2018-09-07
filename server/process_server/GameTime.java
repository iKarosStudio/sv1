package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class GameTime
{
	PacketBuilder packet = new PacketBuilder ();
	
	public GameTime () {
		ServerTime serverTime = ServerTime.getInstance ();
		
		packet.writeByte (ServerOpcodes.SYS_TICK) ;
		packet.writeDoubleWord (serverTime.getTime ()); //get sys tick time
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
