package vidar.server.process_server; 

import vidar.server.packet.*;
import vidar.server.opcodes.*;

/*
 * 指定uuid物件面向Heading
 */
public class ModelHeading
{
	PacketBuilder packet = new PacketBuilder () ;
	public ModelHeading (int _uuid, int _heading) {
		
		packet.writeByte (ServerOpcodes.SET_HEADING);
		packet.writeDoubleWord (_uuid) ;
		packet.writeByte (_heading);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
