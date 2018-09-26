package vidar.server.process_server;

import vidar.server.*;
import vidar.server.opcodes.*;
import vidar.server.packet.*;
import vidar.game.model.*;

/*
	 * 報告玩家角色各項數值
	 */
public class NodeStatus
{
	PacketBuilder packet = new PacketBuilder ();
	
	public NodeStatus (PcInstance pc) {
		ServerTime serverTime = ServerTime.getInstance ();
		try {
			packet.writeByte (ServerOpcodes.NODE_STATUS);
			packet.writeDoubleWord (pc.uuid); //id
			packet.writeByte (pc.level);
			packet.writeDoubleWord (pc.exp);
			packet.writeByte (pc.getStr ());
			packet.writeByte (pc.getIntel ());
			packet.writeByte (pc.getWis ());
			packet.writeByte (pc.getDex ());
			packet.writeByte (pc.getCon ());
			packet.writeByte (pc.getCha ());
			packet.writeWord (pc.hp);
			packet.writeWord (pc.getMaxHp ());
			packet.writeWord (pc.mp);
			packet.writeWord (pc.getMaxMp ());
			packet.writeByte (pc.getAc ());
			packet.writeDoubleWord (serverTime.getTime ()); //time
			packet.writeByte (pc.satiation);
			packet.writeByte (pc.getWeightInScale30 ());//weight 0~30 = 0~100%, 1:3.4
			packet.writeWord (pc.lawful);
			packet.writeByte (0);//fire
			packet.writeByte (0);//water
			packet.writeByte (0);//wind
			packet.writeByte (0);//earth
			
		} catch (Exception e) {
			e.printStackTrace () ;
		}
	}
	
	public byte[] getRaw () {
		return packet.getPacketNoPadding () ;
	}
}
