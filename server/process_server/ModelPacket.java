package vidar.server.process_server;

import vidar.server.opcodes.*;
import vidar.server.packet.*;
import vidar.game.model.*;

/*
 * S_OwnCharPack對比
 * 報告給client畫面上一個物件(玩家, 怪物, 地上的道具, 特效等等)
 */
public class ModelPacket
{
	PacketBuilder packet = new PacketBuilder ();
	
	public static int STATUS_POISON = 0x01; //中毒
	public static int STATUS_INVISIBLE = 0x02;//隱身
	public static int STATUS_PC = 0x04; //一般玩家
	public static int STATUS_FROZEN = 0x08; //冷凍
	public static int STATUS_BRAVE = 0x10; //勇水
	public static int STATUS_ELF_BRAVE = 0x20; //精餅
	public static int STATUS_FASTMOVE = 0x40; //高速移動用
	public static int STATUS_GHOST = 0x80; //幽靈模式
	
	public ModelPacket (MapModel node) {
		packet.writeByte (ServerOpcodes.MODEL_PACK);
		packet.writeWord (node.location.p.x);
		packet.writeWord (node.location.p.y);
		packet.writeDoubleWord (node.uuid);
		packet.writeWord (node.gfx);
		packet.writeByte (node.getActId ());
		packet.writeByte (node.heading);
		packet.writeByte (node.lightRange);
		packet.writeByte (node.moveSpeed);
		packet.writeDoubleWord (node.exp);
		packet.writeWord (node.lawful);
		packet.writeString (node.name);
		packet.writeString (node.title);
		packet.writeByte (node.status);
		packet.writeDoubleWord (node.clanId);
		packet.writeString (node.clanName);
		packet.writeString (null);
		packet.writeByte (0x00);
		packet.writeByte (node.hpScale); //血條百分比
		packet.writeByte (0x00);
		packet.writeByte (node.levelScale);
		packet.writeByte (0x00);
		packet.writeByte (0xFF);
		packet.writeByte (0xFF);
	}
	
	/* 報告角色物件 */
	/*
	public ModelPacket (PcInstance pc) {
		int status = STATUS_PC;	
		
		if (pc.braveSpeed == 1) {
			status |= STATUS_BRAVE;
		}
		
		packet.writeByte (ServerOpcodes.MODEL_PACK);
		packet.writeWord (pc.location.p.x) ;
		packet.writeWord (pc.location.p.y) ;
		packet.writeDoubleWord (pc.uuid) ;
		
		packet.writeWord (pc.gfxTemp) ; //get gfx
		if (pc.isDead) {
			packet.writeByte (8) ;
		} else {
			packet.writeByte (pc.getWeaponGfx ()); //武器外型修正
		}
		packet.writeByte (pc.heading) ;
		packet.writeByte (10) ; //light
		
		packet.writeByte (pc.moveSpeed) ; //move speed;
		
		packet.writeDoubleWord (pc.exp) ;
		packet.writeWord (pc.lawful) ;
		packet.writeString (pc.name) ;
		packet.writeString (pc.title);
		
		pc.status = status;
		packet.writeByte (pc.status) ; //status
		packet.writeDoubleWord (pc.clanId) ;
		packet.writeString (pc.clanName) ;
		packet.writeString (null) ;
		packet.writeByte (0x00) ;
		
		packet.writeByte (0xFF) ; //血條百分比
		
		packet.writeByte (0x00) ;
		packet.writeByte (0x00) ;
		packet.writeByte (0x00) ;
		packet.writeByte (0xFF) ;
		packet.writeByte (0xFF) ;
	}*/
	
	/*
	 * 報告NPC物件
	 */
	/*
	public ModelPacket (NpcInstance npc) {
		packet.writeByte (ServerOpcodes.MODEL_PACK);
		packet.writeWord (npc.location.p.x) ;
		packet.writeWord (npc.location.p.y) ;
		packet.writeDoubleWord (npc.uuid) ;
		
		packet.writeWord (npc.gfx) ; //get gfx
		if (npc.isDead) {
			packet.writeByte (8) ;
		} else {
			packet.writeByte (0) ;//weapon 0:hand 4:sword
		}
		packet.writeByte (npc.heading) ;
		packet.writeByte (10) ; //light
		packet.writeByte (npc.moveSpeed) ; //move speed;
		
		packet.writeDoubleWord (0) ; //EXP
		packet.writeWord (0) ; //Lawful
		packet.writeString (npc.nameId) ;
		packet.writeString (null) ;//title
		packet.writeByte (npc.status) ; //status
		packet.writeDoubleWord (0) ;
		packet.writeString (null) ;
		packet.writeString (null) ;
		packet.writeByte (0x00) ;
		
		packet.writeByte (0xFF) ; //血條百分比
		
		packet.writeByte (0x00) ;
		packet.writeByte (npc.level) ;
		packet.writeByte (0x00) ;
		packet.writeByte (0xFF) ;
		packet.writeByte (0xFF) ;
	}*/
	
	/*
	 * 報告門物件
	 */
	/*
	public ModelPacket (DoorInstance d) {
		packet.writeByte (ServerOpcodes.MODEL_PACK) ;
		packet.writeWord (d.location.p.x) ;
		packet.writeWord (d.location.p.y) ;
		packet.writeDoubleWord (d.uuid) ;
		packet.writeWord (d.gfx) ;
		packet.writeByte (d.actionCode) ;
		packet.writeByte (0) ;
		packet.writeByte (0) ;
		packet.writeByte (0) ;
		packet.writeDoubleWord (0x01) ;
		packet.writeWord (0) ;
		packet.writeString (null) ;
		packet.writeString (null) ;
		if (d.isVisible) {
			packet.writeByte (0) ;
		} else {
			packet.writeByte (STATUS_INVISIBLE) ;
		}
		packet.writeDoubleWord (0);
		packet.writeString (null) ;
		packet.writeString (null) ;
		packet.writeByte (0) ;
		packet.writeByte (0xFF) ;
		packet.writeByte (0) ;
		packet.writeByte (0) ;
		packet.writeByte (0) ;
		packet.writeByte (0xFF) ;
		packet.writeByte (0xFF) ;
	}*/	
	
	/*
	 * 報告怪物物件
	 */
	/*
	public ModelPacket (MonsterInstance monster) {
		packet.writeByte (ServerOpcodes.MODEL_PACK);
		packet.writeWord (monster.location.p.x) ;
		packet.writeWord (monster.location.p.y) ;
		packet.writeDoubleWord (monster.uuid) ;
		
		packet.writeWord (monster.gfx) ; //get gfx
		if (monster.isDead) {
			packet.writeByte (8) ;
		} else {
			packet.writeByte (0) ;
		}
		packet.writeByte (monster.heading) ;
		packet.writeByte (10) ; //light
		packet.writeByte (monster.moveSpeed) ; //move speed;
		
		packet.writeDoubleWord (0) ; //EXP
		packet.writeWord (0) ; //Lawful
		packet.writeString (monster.nameId) ;
		packet.writeString (null) ;//title
		packet.writeByte (monster.status) ; //status
		packet.writeDoubleWord (0) ;
		packet.writeString (null) ;
		packet.writeString (null) ;
		packet.writeByte (0x00) ;
		
		packet.writeByte (0xFF) ; //血條百分比
		
		packet.writeByte (0x00) ;
		packet.writeByte (monster.level) ;
		packet.writeByte (0x00) ;
		packet.writeByte (0xFF) ;
		packet.writeByte (0xFF) ;
	}*/
	/*
	public ModelPacket (ItemInstance item) {
		packet.writeByte (ServerOpcodes.MODEL_PACK);
		packet.writeWord (item.location.p.x);
		packet.writeWord (item.location.p.y);
		packet.writeDoubleWord (item.uuid);
		packet.writeWord (item.gfxOnGround);
		packet.writeByte (0);
		packet.writeByte (0);
		packet.writeByte (1);
		packet.writeByte (0);
		packet.writeDoubleWord (item.count);
		packet.writeByte (0);
		packet.writeByte (0);
		if (item.count > 1) {
			packet.writeString (item.getName ());
		} else {
			packet.writeString (item.name);
		}
		packet.writeByte (0);
		packet.writeDoubleWord (0);
		packet.writeDoubleWord (0);
		packet.writeByte (0xFF);
		packet.writeByte (0);
		packet.writeByte (0);
		packet.writeByte (0);
		packet.writeWord (0xFFFF); 
		packet.writeDoubleWord (0);
		packet.writeByte (0);
		packet.writeByte (0);
		
	}*/
	
	public byte[] getRaw () {
		return packet.getPacketNoPadding () ;
	}
}
