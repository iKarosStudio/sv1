package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

public class DoorTouch
{
	PcInstance pc;
	
	public DoorTouch (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data);
		pc = handle.getPc ();
		packetReader.readWord ();//int x = packetReader.readWord ();
		packetReader.readWord ();//int y = packetReader.readWord ();
		int uuid = packetReader.readDoubleWord ();
		
		DoorInstance door = pc.doorsInsight.get (uuid);
		
		if (door.keyId == 0) {
			if (door.isOpened) {
				door.close ();
			} else {
				door.open ();
			}
			byte[] detail = new DoorDetail (door).getRaw ();
			byte[] action = new ModelAction (door.actionCode, door.uuid, door.heading).getRaw ();
			
			/* 以門為點做廣播 */
			door.boardcastPcInsight (detail);
			door.boardcastPcInsight (action);
		}
	}
}
