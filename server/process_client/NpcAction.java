package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.npc.*;

public class NpcAction
{
	public NpcAction (SessionHandler handle, byte[] data) {
		PacketReader reader = new PacketReader (data);
		
		int npcId = reader.readDoubleWord ();
		String actionCode = reader.readString ();
		
		//NpcActionCodeHandler actionCodeHandler = new NpcActionCodeHandler (handle, npcId, actionCode);
		new NpcActionCodeHandler (handle, npcId, actionCode);
	}
}
