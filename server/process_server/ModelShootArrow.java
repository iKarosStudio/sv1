package vidar.server.process_server;

import java.util.concurrent.atomic.AtomicInteger;

import vidar.server.packet.*;
import vidar.server.opcodes.*;
import vidar.game.model.*;

public class ModelShootArrow
{
	PacketBuilder packet = new PacketBuilder ();
	private static AtomicInteger sequentialNumber = new AtomicInteger(0);
	
	public ModelShootArrow (Model src, int targetUuid, int targetX, int targetY) {
		packet.writeByte (ServerOpcodes.MODEL_ACTION);
		packet.writeByte (ModelActionId.ATTACK);
		
		//發動方, 接受方 UUID
		packet.writeDoubleWord (src.uuid);
		packet.writeDoubleWord (targetUuid);
		
		//命中代碼(命中時的表現嗎?) actionId
		packet.writeByte (6);
		
		//heading
		packet.writeByte (src.heading);
		
		//流水編號
		packet.writeDoubleWord (sequentialNumber.getAndIncrement ());
		
		//Skill GFX
		packet.writeWord (167); //66
		
		//unknown
		packet.writeByte (127);
		
		//發動方, 接受方座標
		packet.writeWord (src.location.point.x);
		packet.writeWord (src.location.point.y);
		packet.writeWord (targetX);
		packet.writeWord (targetY);
		
		packet.writeWord (0);
		packet.writeByte (0);
		packet.writeByte (8);
	}
	
	public byte[] getRaw () {
		return packet.getPacket ();
	}
}