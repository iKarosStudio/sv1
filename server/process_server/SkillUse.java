package vidar.server.process_server;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;
import vidar.game.skill.*;

public class SkillUse
{
	PacketReader packetReader;
	
	PcInstance pc;
	SessionHandler handle;
	
	public SkillUse (SessionHandler _handle, byte[] data) {
		packetReader = new PacketReader (data);
		handle = _handle;
		pc = handle.getPc ();
	}
}
