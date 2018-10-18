package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;

public class Move {
	public Move (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data);
		PcInstance pc = handle.getPc ();
		
		int tmpX = packetReader.readWord () ; //pseudo
		int tmpY = packetReader.readWord () ; //pseudo
		int heading = packetReader.readByte () ;
		
		pc.move (tmpX, tmpY, heading);
	}
}
