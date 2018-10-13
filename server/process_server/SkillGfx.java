package vidar.server.process_server;

import java.util.concurrent.atomic.*;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

//表現技能特效
public class SkillGfx
{
	PacketBuilder packet = new PacketBuilder ();
	private static AtomicInteger sequentialNumber = new AtomicInteger(0);
	
	public SkillGfx (MapModel src, int tid, int actionId, int skillGfx, int x, int y, boolean isHit) {
		packet.writeByte (ServerOpcodes.MODEL_ACTION);
		packet.writeByte (actionId);
		
		//發動方, 接受方 UUID
		packet.writeDoubleWord (src.uuid);
		packet.writeDoubleWord (tid);
		
		//命中代碼(命中時的表現嗎?) //orig=6
		packet.writeByte (isHit? 6 : 0);
		
		//heading
		packet.writeByte (src.heading);
		
		//流水編號
		packet.writeDoubleWord (sequentialNumber.getAndIncrement ());
		
		//Skill GFX
		packet.writeWord (skillGfx);
		
		//unknown
		packet.writeByte (127);
		
		//發動方, 接受方座標
		packet.writeWord (src.location.p.x);
		packet.writeWord (src.location.p.y);
		packet.writeWord (x);
		packet.writeWord (y);
		
		packet.writeWord (0);
		packet.writeByte (0);
		//packet.writeByte (8);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}
