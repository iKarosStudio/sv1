package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class SkillBrave
{
	PacketBuilder packet = new PacketBuilder ();
	
	public SkillBrave (int _uuid, int _braveSpeed, int _remainTime) {
		packet.writeByte (ServerOpcodes.SKILL_BRAVE); 
		packet.writeDoubleWord (_uuid);
		packet.writeByte (_braveSpeed);
		
		/*
		 * Remain Time  0xFFFF -> 永久效果
		 */
		packet.writeWord (_remainTime);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
