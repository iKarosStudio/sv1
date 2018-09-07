package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

public class UpdateHeading
{
	public UpdateHeading (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data) ;
		PcInstance pc = handle.getPc ();
		int heading = packetReader.readByte ();
		
		pc.heading = heading;
		
		//Pc.BoardcastPcInsightExceptSelf (new NodeHeading (Pc.Uuid, Pc.location.Heading).getRaw () ) ;
		pc.boardcastPcInsight (new ModelHeading (pc.uuid, pc.heading).getRaw ());
	}
}
