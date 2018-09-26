package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;
import vidar.game.model.item.*;

public class ItemDelete
{
	public ItemDelete (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data);
		PcInstance pc = handle.getPc ();
		
		int itemUuid = packetReader.readDoubleWord ();
		
		ItemInstance item = pc.findItemByUuid (itemUuid);
		if (item != null) {
			pc.removeItem (item);
		}
	}
}
