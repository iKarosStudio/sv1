package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;

public class ItemPick
{
	public ItemPick (SessionHandler handle, byte[] data) {
		PacketReader reader = new PacketReader (data);
		PcInstance pc = handle.getPc ();
		
		int x = reader.readWord ();
		int y = reader.readWord ();
		int uuid = reader.readDoubleWord ();
		int count = reader.readDoubleWord ();

		pc.pickItem (uuid, count, x, y);
	}
}
