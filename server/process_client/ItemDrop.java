package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;

public class ItemDrop
{
	public ItemDrop (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data) ;
		PcInstance pc = handle.account.activePc;
		
		int x = packetReader.readWord ();
		int y = packetReader.readWord ();
		int uuid = packetReader.readDoubleWord ();
		int count = packetReader.readDoubleWord ();
		
		pc.dropItem (uuid, count, x, y);
	}
}
