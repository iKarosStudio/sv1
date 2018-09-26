package vidar.server.process_server;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class SkillHaste
{
	PacketBuilder packet = new PacketBuilder ();
	
	public SkillHaste (int _uuid, int _moveSpeed, int _remainTime) {
		packet.writeByte (ServerOpcodes.SKILL_HASTE) ; 
		packet.writeDoubleWord (_uuid);
		packet.writeByte (_moveSpeed);
		
		/*
		 * Remain Time  0xFFFF -> 永久效果
		 */
		packet.writeWord (_remainTime) ;
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
