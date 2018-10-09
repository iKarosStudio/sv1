package vidar.server.igcp;

import vidar.server.*;
import vidar.server.process_server.*;
import vidar.types.*;
import vidar.game.model.*;

public class RdTeleport
{
	public RdTeleport (PcInstance rd, String cmd) {
		SessionHandler handle = rd.getHandle ();
		
		String[] tpTarget = cmd.split (" ");
		if (tpTarget.length == 4) {
			int destMapId = Integer.valueOf (tpTarget[1]);
			int destX     = Integer.valueOf (tpTarget[2]);
			int destY     = Integer.valueOf (tpTarget[3]);
			
			Location dest = new Location (destMapId, destX, destY);
			new Teleport (rd, dest, true);
			
		} else {
			handle.sendPacket (new SystemMessage ("無效命令 -> .tp mapid x y").getRaw ());
		}
	}
	
}
