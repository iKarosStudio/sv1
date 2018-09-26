package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

/* 攻擊驗算參考 model/L1Attack.java */
public class Attack
{
	public Attack (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data);
		PcInstance pc = handle.getPc ();
		
		int targetUuid = packetReader.readDoubleWord ();
		int targetX = packetReader.readWord ();
		int targetY = packetReader.readWord ();
		
		if (pc.getWeightInScale30 () > 24) {
			handle.sendPacket (new ServerMessage (110).getRaw ());
			return;
		}
		
		/* 傷害判定, 表現攻擊動作 */		
		pc.attack (targetUuid, targetX, targetY);
	}
}
