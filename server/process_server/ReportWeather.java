package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class ReportWeather
{
	PacketBuilder builder = new PacketBuilder () ;
	
	public ReportWeather (int weather) {
		builder.writeByte (ServerOpcodes.WEATHER) ;
		builder.writeByte (weather) ;
	}
	
	public byte[] getRaw () {
		return builder.getPacket () ;
	}
}
