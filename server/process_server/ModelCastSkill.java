package vidar.server.process_server;

import java.util.concurrent.atomic.AtomicInteger;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

public class ModelCastSkill
{
	PacketBuilder packet = new PacketBuilder ();
	private static AtomicInteger sequentialNumber = new AtomicInteger(0);
	
	public ModelCastSkill (MapModel src, MapModel dest, int actionId, int skillGfx, int actGfx) {
		packet.writeByte (ServerOpcodes.MODEL_ACTION);
		packet.writeByte (actionId);
		
		//發動方, 接受方 UUID
		packet.writeDoubleWord (src.uuid);
		if (dest != null) {
			packet.writeDoubleWord (dest.uuid);
		} else {
			packet.writeDoubleWord (0);
		}
		
		//命中代碼(命中時的表現嗎?) actionId
		packet.writeByte (actGfx);
		
		//heading
		packet.writeByte (src.heading);
		
		//流水編號
		packet.writeDoubleWord (sequentialNumber.getAndIncrement ());
		
		//Skill GFX
		packet.writeWord (skillGfx);
		
		//unknown
		packet.writeByte (6);
		
		//發動方, 接受方座標
		packet.writeWord (src.location.p.x);
		packet.writeWord (src.location.p.y);
		if (dest != null) {
			packet.writeWord (dest.location.p.x);
			packet.writeWord (dest.location.p.y);
		} else {
			packet.writeWord (0);
			packet.writeWord (0);
		}
		
		packet.writeWord (0);
		packet.writeByte (0);
	}
}
