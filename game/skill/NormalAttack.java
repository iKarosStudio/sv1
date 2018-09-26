package vidar.game.skill;

import vidar.game.map.*;
import vidar.game.model.*;
import vidar.game.model.npc.*;
import vidar.game.model.monster.*;

public class NormalAttack
{
	public static final byte TYPE_PC = 0x01;
	public static final byte TYPE_MONSTER = 0x02;
	public static final byte TYPE_NPC = 0x03;
	public static final byte TYPE_PET = 0x04;
	
	byte srcType = 0;
	byte destType = 0;
	
	MonsterInstance targetMonster;
	NpcInstance targetNpc;
	PcInstance targetPc;
	
	boolean isValid = false;
	boolean checkWeaponMaterial = false;
	
	public NormalAttack (Model src, int _targetUuid, int _targetX, int _targetY) {
		
		if (src instanceof PcInstance) {
			System.out.println ("pc發起攻擊");
		}
		
		VidarMap map = src.map;
		Model targetModel = map.getModel (_targetUuid);
		
		if (targetModel instanceof MonsterInstance) {
			checkWeaponMaterial = true;
		} else if (targetModel instanceof PcInstance) {
		} else {
		}
		
		//所有參數都有效時isValid->true
		//construct attack
	}
	
	public NormalAttack (PcInstance src, MonsterInstance dest) {
		//
	}
	
	public NormalAttack (MonsterInstance src, PcInstance dest) {
		//
	}
	
	/*
	public NormalAttack (MonsterInstance src, PetInstance dest) {
	}
	*/
	
	public boolean isValid () {
		return isValid;
	}
	
	public boolean isHit () {
		
		return false;
	}
	
	public void commit () {
	}
	
	public void action () {	
	}
}
