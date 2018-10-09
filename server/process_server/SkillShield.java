package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class SkillShield
{
PacketBuilder packet = new PacketBuilder ();
	
	public SkillShield (int remainTime, int type) {
		packet.writeByte (ServerOpcodes.SKILL_SHIELD); 
		/*
		 * Remain Time  0xFFFF -> 永久效果
		 */
		packet.writeWord (remainTime);
		packet.writeByte (type);
		packet.writeDoubleWord (0);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
