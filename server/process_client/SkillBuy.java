package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;

public class SkillBuy
{
	SessionHandler handle;
	PacketReader reader;
	
	public SkillBuy (SessionHandler _handle, byte[] data) {
		handle = _handle;
		reader = new PacketReader (data);
		int skill = reader.readDoubleWord ();
		
		new SkillBuyList (handle, handle.getPc().type);
	}
}
