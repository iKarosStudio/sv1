package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

public class ReportSpMr
{
	PacketBuilder packet = new PacketBuilder () ;
	
	public ReportSpMr (SessionHandler handle) {
		PcInstance pc = handle.account.activePc;
		
		packet.writeByte (ServerOpcodes.MATK_MRST) ;
		packet.writeByte (pc.getSp ()); //sp
		packet.writeByte (pc.getMr ()); //mr
	}
	
	public byte[] getRaw () {
		return packet.getPacket () ;
	}
}
