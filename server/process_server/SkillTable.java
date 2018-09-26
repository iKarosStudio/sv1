package vidar.server.process_server;

import java.util.*;

import vidar.server.packet.*;
import vidar.server.opcodes.*;

public class SkillTable
{
	PacketBuilder packet = new PacketBuilder ();

	public SkillTable (int _pcType, HashMap<Integer, Integer> skillTable) {		
		packet.writeByte (ServerOpcodes.SKILL_TABLE);
		
		int check_5_8 = 0;
		int check_9_10 = 0;
		
		for (int i = 5; i <= 10; i++) {
			if (i < 9) {
				check_5_8 += skillTable.get (i);
			} else {
				check_9_10 += skillTable.get (i);
			}
		}
		
		if (check_5_8 > 0 && check_9_10 == 0) {
			packet.writeByte (50) ;
		} else if (check_9_10 > 0) {
			packet.writeByte (100) ;
		} else {
			packet.writeByte (22);
		}
		
		for (int i = 1; i <= 24; i++) {
			packet.writeByte (skillTable.get (i));
		}
		packet.writeDoubleWord (0) ;
		packet.writeDoubleWord (0) ;
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
