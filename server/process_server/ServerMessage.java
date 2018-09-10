package vidar.server.process_server;

import vidar.server.opcodes.*;
import vidar.server.packet.*;

public class ServerMessage
{
	PacketBuilder packet = new PacketBuilder ();
	
	public ServerMessage (int _messageId) {
		this (_messageId, null);
	}
	
	public ServerMessage (int _messageId, String[] _messageArgs) {
		packet.writeByte (ServerOpcodes.SERVER_MSG) ;
		packet.writeWord (_messageId);
		
		if (_messageArgs == null) {
			packet.writeByte (0) ;
		} else {
			packet.writeByte (_messageArgs.length) ;
			for (int index = 0; index < _messageArgs.length; index++) {
				packet.writeString (_messageArgs[index]);
			}
		}
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
