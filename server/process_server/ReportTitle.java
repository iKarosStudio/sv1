package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class ReportTitle
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public ReportTitle (SessionHandler handle) {
		packet.writeByte (ServerOpcodes.CHAR_TITLE);
		packet.writeDoubleWord (handle.account.activePc.uuid);
		packet.writeString (handle.account.activePc.title);
	}
	
	public ReportTitle (int uuid, String title) {
		packet.writeByte (ServerOpcodes.CHAR_TITLE);
		packet.writeDoubleWord (uuid);
		packet.writeString (title);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
