package vidar.server.process_server; 

import vidar.server.packet.*;
import vidar.server.opcodes.*;

/*
 * 指定uuid物件面向Heading
 */
public class ModelHeading
{
	PacketBuilder Builder = new PacketBuilder () ;
	public ModelHeading (int Uuid, int Heading) {
		
		Builder.writeByte (ServerOpcodes.SET_HEADING) ;
		Builder.writeDoubleWord (Uuid) ;
		Builder.writeByte (Heading) ;
	}
	
	public byte[] getRaw () {
		return Builder.getPacket () ;
	}
}
