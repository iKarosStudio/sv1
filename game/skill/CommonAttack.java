package vidar.game.skill;

import java.util.*;

import vidar.server.process_server.*;
import vidar.game.model.*;
import vidar.game.model.item.*;
import vidar.game.model.monster.*;

import static vidar.game.template.ItemTypeTable.*;

/* 
 * 一般攻擊視為一個技能實作
 * src : 攻擊發動方
 * dest : 被攻擊方
 */
public class CommonAttack
{	
	Random random = new Random () ;
	
	/* 玩家打怪物 */
	public CommonAttack (PcInstance src, MonsterInstance dest) {
		int dmg = 0;
		
		if (dest.isDead) {
			return;
		}
		
		
		/*
		if (src.equipment.getWeapon () == null) {
			System.out.printf ("%s 使用%s對 %s(%d) 攻擊", src.Name, "空手", dest.Name, dest.Uuid) ;
			
		} else {
			System.out.printf ("%s 使用%s對 %s(%d) 攻擊", src.Name, src.equipment.getWeapon ().getName (), dest.Name, dest.Uuid) ;
		}
		*/
		
		/*
		 * 命中檢查
		 */
		if (isPc2NpcHit (src, dest)) {
			/*
			 * 傷害計算
			 */
			dmg = CalcPc2NpcDmg (src, dest);
			dest.toggleHateList (src, dmg);
			
			System.out.printf ("命中! 造成%3d傷害\n", dmg) ;
			dest.takeDamage (dmg);
			
			/*
			 * 設定反擊
			 */
			if (dest.targetPc == null) {
				dest.aiKernel.cancel ();
				dest.actionStatus = MonsterInstance.ACTION_ATTACK;
				dest.targetPc = src;
			}
			
			/*
			 * 挨打動作
			 */
			dest.boardcastPcInsight (new ModelAction (ModelActionId.DAMAGE, dest.uuid, dest.heading).getRaw ());
			
			if (dest.isDead) {
				byte[] die = new ModelAction (ModelActionId.DIE, dest.uuid, dest.heading).getRaw ();
				
				//轉移經驗值與道具
				dest.transferExp (dest.targetPc);
				dest.transferItems () ;
				
				dest.boardcastPcInsight (die);
				dest.isDead = true;
				dest.actionStatus = MonsterInstance.ACTION_DEAD;
			}
				
			
		} else {
			System.out.printf ("未命中!\n") ;
		}
	}
	
	/* 怪物打玩家 */
	public CommonAttack (MonsterInstance src, PcInstance dest) {
		int dmg = 0;
		
		if (dest.isDead) {
			return;
		}
		
		if (isNpc2PcHit (src, dest) ) {
			dmg = CalcNpc2PcDmg (src, dest) ;
			System.out.printf ("命中! 造成%d傷害\n", dmg) ;
			
			dest.takeDamage (dmg) ;
			byte[] action_code = new ModelAction (2, dest.uuid, dest.heading).getRaw () ;
			dest.getHandle ().sendPacket (action_code) ;
			dest.boardcastPcInsight (action_code) ;
		} else {
			System.out.printf ("未命中!\n") ;
		}
	}
	
	/* 怪物(寵物, 怪物)對怪物  */
	//public CommonAttack (PetInstance src, MonsterInstance dest) {
	//}
	
	/*
	 * 玩家對玩家
	 */
	public CommonAttack (PcInstance src, PcInstance dest) {
	}
	
	/*
	 * 算PC2NPC命中率
	 */
	public boolean isPc2NpcHit (PcInstance src, MonsterInstance dest) {
		
		int srcStr = src.getStr () ;
		int srcDex = src.getDex () ;
		int hitRate = src.level;
		
		int weaponEnchant = 0;
		int weaponType = 0;
		ItemInstance weapon = src.equipment.weapon;
		if (weapon != null) {
			weaponType = src.equipment.weapon.minorType;
			weaponEnchant = src.equipment.weapon.enchant;
		}
		
		if (srcStr > 39) {
			hitRate += STR_HIT_OFFSET[39];
		} else {
			hitRate += STR_HIT_OFFSET[srcStr];
		}
		
		if (srcDex > 39) {
			hitRate += DEX_HIT_OFFSET[39];
		} else {
			hitRate += DEX_HIT_OFFSET[srcDex];
		}
		
		if (weapon != null) {
			hitRate += weapon.hitModify;
			hitRate += (weaponEnchant >>> 1) ;
		}
		
		if ((weaponType != WEAPON_TYPE_BOW) && (weaponType != WEAPON_TYPE_GAUNTLET)) {
			hitRate += src.getHitModify ();
		} else {
			hitRate += src.getBowHitModify ();
		}
		
		hitRate *= 5;
		hitRate += (dest.basicParameters.ac * 5) ;
		
		if (hitRate > 95) {
			hitRate = 95;
		}
		
		if (hitRate < 5) {
			hitRate = 5;
		}
		
		int Rate = random.nextInt (100) + 1;
		return Rate < hitRate;
	}
	
	/*
	 * 算NPC2PC命中率
	 */
	public boolean isNpc2PcHit (MonsterInstance src, PcInstance dest) {
		int hitRate = 0;
		
		hitRate = src.level * 10; // * 2 * 5
		hitRate -= dest.getAc () * 5;
		
		/*
		 * 寵物命中修正*2
		 */
		
		/*
		 * Npc Hit rate修正
		 */
		
		/*
		 * 有暗影閃避(Uncanny dodge)命中-20
		 */
		
		if (hitRate < src.level) {
			hitRate = src.level;
		}
		
		if (hitRate < 5) {
			hitRate = 5;
		}
		
		if (hitRate > 95) {
			hitRate = 95;
		}
		
		int p = random.nextInt (100) + 1;
		System.out.printf ("Hit: %d/%d\n", hitRate, p) ;
		return p < hitRate;
	}
	
	public int CalcPc2NpcDmg (PcInstance src, MonsterInstance dest) {
		ItemInstance weapon = src.equipment.weapon;
		int weaponMaxDmg = 0;
		int weaponDmg = 0;
		int str = src.getStr () ;
		int dex = src.getDex () ;
		
		/* 有傷害無效化技能先行處理 return 0; */
		
		if (weapon != null) {
			if (dest.size == 0) { //小型怪
				weaponMaxDmg = weapon.dmgSmall;
			} else { //大型怪
				weaponMaxDmg = weapon.dmgLarge;
			}
			
			/* 套用力量/敏捷加乘效果 */
			if ((weapon.minorType == WEAPON_TYPE_ARROW) || (weapon.minorType == WEAPON_TYPE_GAUNTLET) ) {
				//遠程武器
				if (dex > 35) {
					weaponMaxDmg += DEX_DMG_OFFSET[35];
				} else {
					weaponMaxDmg += DEX_DMG_OFFSET[dex];
				}
				
				if (src.isElf ()) {
					weaponMaxDmg += src.level / 10;
				}
				
				if (src.isDarkelf ()) {
					//
				}
				
			} else {
				//近戰武器
				if (str > 50) {
					weaponMaxDmg += STR_DMG_OFFSET[50];
				} else {
					weaponMaxDmg += STR_DMG_OFFSET[str];
				}
				
				/* 套用職業加乘效果 */
				if (src.isKnight ()) {
					weaponMaxDmg += src.level / 10;
				}
				
				if (src.isDarkelf ()) {
					//
				}
			}
					
			/* 套用材質加乘效果 */
			
			/* 雙刀1/3機率打最大傷害 特效#3671 */
			
			/* 雙刀1/4機率打出雙倍傷害 特效#3398 */
			
			/* 雙刀/雙爪 有雙重破壞(Double Brake)時1/3機率打出雙倍傷害 */
			
			/* 有擬似魔法武器+2 */
			
			/* 有烈焰之魂(Soul of Flame)近戰武器 取最高傷害 */
			
			weaponDmg = 1 + random.nextInt (weaponMaxDmg) + weapon.enchant;
			
		} else {
			weaponDmg = 1;
		}
		
		return weaponDmg;
	}
	
	public int CalcNpc2PcDmg (MonsterInstance src, PcInstance dest) {
		int dmg = 5 * (src.level / 10) ;
		dmg += random.nextInt (src.level - dmg + 5) ;
		dmg += src.basicParameters.str;
		dmg = dmg >>> 1;
		dmg++;
		
		/*
		 * 職業傷害減免
		 */
		int ac = Math.min (0, dest.getAc () ) ;
		if (dest.isRoyal () ) {
			dmg += random.nextInt (ac / 3 + 1) ;
		} else if (dest.isKnight () ) {
			dmg += random.nextInt (ac / 2 + 1) ;
		} else if (dest.isElf () ) {
			dmg += random.nextInt (ac / 4 + 1) ;
		} else if (dest.isWizard ()) {
			dmg += random.nextInt (ac / 5 + 1) ;
		} else if (dest.isDarkelf ()) {
			dmg += random.nextInt (ac / 4 + 1) ;
		}
		
		/*
		 * 壞物 傷害/2
		 */
		
		/*
		 * 反擊屏障
		 */
		
		return dmg;
	}
	
	public int CalcPc2PcDmg (PcInstance src, PcInstance dest) {
		return 0;
	}
	
	/* 算迴避率(Evation Rate) */
	public int CalcER (int type, int level, int dex) {
		int d = 0;
		
		/* 不同職業的等級修正係數 */
		switch (type) {
		case 0: d = 8; break; //Royal
		case 1: d = 4; break; //Knight
		case 2: d = 6; break; //Elf
		case 3: d = 10;break; //Wizard
		case 4: d = 5; break; //Darkelf
		}
		
		return (level / d) + (dex >>> 2) - 4;
	}
	
	
	private static final int[] STR_HIT_OFFSET = {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, //0~9
			 0,  0,  1,  1,  2,  2,  3,  3,  4,  4, //10-19
			 4,  5,  5,  5,  6,  6,  6,  7,  7,  7, //20-29
			 8,  8,  8,  9,  9,  9, 10, 10, 10, 10, //30-39
			 10} ; //40

	private static final int[] DEX_HIT_OFFSET = {
			-1, -1, -1, -1, -1, -1, -1, -1, -1,  0, //0~9
			 0,  1,  1,  2,  2,  3,  3,  4,  4,  5, //10~19
			 6,  7,  8,  9, 10, 11, 12, 13, 14, 15, //20~29
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, //30~39
			26}; //40
	
	private static final int[] STR_DMG_OFFSET = {
			-2, -2, -2, -2, -2, -2, -2, -2, -2, -1, //0~9
			-1,  0,  0,  1,  1,  2,  2,  3,  3,  4, //10~19
			 4,  5,  5,  6,  6,  6,  7,  7,  7,  8, //20~29
			 8,  9,  9, 10, 11, 12, 12, 12, 12, 13, //30~39
			13, 13, 13, 14, 14, 14, 14, 15, 15, 16, //40~49
			17} ;
	
	private static final int[] DEX_DMG_OFFSET = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //0~9
			0, 0, 0, 0, 1, 2, 3, 4, 4, 4, //10~19
			4, 5, 5, 5, 6, 6, 6, 7, 7, 7, //20~29
			8, 8, 8, 9, 9, 10} ; //30~35
}
