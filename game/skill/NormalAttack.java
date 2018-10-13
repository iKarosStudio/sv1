package vidar.game.skill;

import java.util.*;

import vidar.game.model.item.*;
import vidar.game.model.*;
import vidar.game.model.monster.*;

import static vidar.game.skill.AttackOffsetTable.*;
import static vidar.game.template.ItemTypeTable.*;

public class NormalAttack
{	
	Random random = new Random ();
	
	public MapModel attacker;
	public MapModel target;
	public boolean isRemoteAttack = false;
	public ItemInstance weapon = null;
	public ItemInstance arrow = null;
	public int fixedDmg = 0;
	public int randomDmg = 0;
	public int hitRate = 0;
	
	/* 由玩家發動的攻擊 */
	public NormalAttack (PcInstance pc) {
		//calcHitRate (pc);
		//calcDmg (pc);
	}
	
	public NormalAttack (PcInstance src, int _targetUuid, int _targetX, int _targetY) {
		attacker = src;
		
		target = attacker.getCurrentMap ().getModel (_targetUuid);
		if (target != null) {
			//
		}
		
		if (src.equipment.weapon != null) {
			weapon = src.equipment.weapon;
			if (weapon.isRemoteWeapon ()) {
				isRemoteAttack = true;
				arrow = src.equipment.arrow;
				
				if (target.size == 0) {
					randomDmg += (weapon.dmgSmall + arrow.dmgSmall);
				} else {
					randomDmg += (weapon.dmgLarge + arrow.dmgLarge);
				}
			} else {
				
				if (target.size == 0) {
					randomDmg += weapon.dmgSmall;
				} else {
					randomDmg += weapon.dmgLarge;
				}
				
			}
		}
		
	
		
		//find target in map
		
		/*
		VidarMap map = src.map;
		
		//attacking a monster
		if (map.monsters.containsKey (_targetUuid)) {
			MonsterInstance dest = map.monsters.get (_targetUuid);
			
			if (dest.isDead) {
				return;
			}
			
			isHit = isPc2NpcHit (src, dest);
			if (isHit) {
				dmg = calcPc2NpcDamage (src, dest);
				dest.toggleHateList (src, dmg);
				dest.takeDamage (dmg);
				
				//
				// 設定反擊
				//
				if (dest.targetPc == null) {
					dest.aiKernel.cancel ();
					dest.actionStatus = MonsterInstance.ACTION_ATTACK;
					dest.targetPc = src;
				}
			} else {
				dmg = 0;
			}
		
		//attacking a player
		} else if (map.pcs.containsKey (_targetUuid)) {
			//
		}
		*/
	}
	
	public NormalAttack (MonsterInstance src, PcInstance dest) {
		/*
		if (dest.isDead) {
			return;
		}
		
		System.out.printf ("%s 嘗試攻擊 %s :", src.name, dest.name);
		isHit = isNpc2PcHit (src, dest);
		if (isHit) {
			dmg = calcNpc2PcDamage (src, dest);
			System.out.printf ("命中, 造成%d傷害\n", dmg);
			dest.takeDamage (dmg);
		} else {
			System.out.printf ("沒有命中\n");
			dmg = 0;
		}
		*/
	}
	
	
	private boolean isPc2NpcHit (PcInstance src, MonsterInstance dest) {
		int srcStr = src.getStr ();
		int srcDex = src.getDex ();
		int hitRate = src.level;
		
		int weaponEnchant = 0;
		if (src.equipment.weapon != null) {
			weaponEnchant = src.equipment.weapon.enchant;
			
			if (src.equipment.weapon.isRemoteWeapon ()) {
				isRemoteAttack = true;
				
				//箭的相關操作 (要修正鐵手甲的飛刀種類(sting)
				if (src.equipment.arrow == null) { //沒有箭 嘗試找箭
					src.itemBag.forEach ((Integer uuid, ItemInstance item)->{
						if (item.isArrow ()) {
							src.equipment.setArrow (item);
						}
					});
					
					if (src.equipment.arrow == null) { //找不到箭可以射
						return false;
					}
				}
				
				//箭的消耗機制
				if (src.equipment.arrow.count > 0) {
					src.removeItem (src.equipment.arrow.uuid, 1);
				} else {
					src.equipment.arrow = null;
				}
				
				hitRate += src.getBowHitModify ();
			} else {
				hitRate += src.getHitModify ();
			}
			
			hitRate += (weaponEnchant >>> 1);
		}
		
		if (srcStr > 39) {
			hitRate += STR_HIT_OFFSET[39];
		} else {
			hitRate += STR_HIT_OFFSET[srcStr];
		}
		
		if (srcDex > 39) {
			hitRate += DEX_HIT_OFFSET[39];
		} else {
			hitRate =+ DEX_HIT_OFFSET[srcDex];
		}
		
		hitRate *= 5;
		hitRate += (dest.basicParameters.ac * 5);
		
		if (hitRate > 95) {
			hitRate = 95;
		} 
		
		if (hitRate < 5) {
			hitRate = 5;
		}
		
		int rate = random.nextInt (100) + 1;
		return rate < hitRate;
	}
	
	private boolean isNpc2PcHit (ActiveModel src, PcInstance dest) {
		int hitRate = 0;
		
		hitRate = src.level * 10;
		hitRate -= (dest.getAc () * 5);
		
		/* 寵物命中修正 x2 */
		/* npc hitrate 修正 */
		/* 玩家有暗影閃避 命中-20 */
		
		if (hitRate < src.level) {
			hitRate = src.level;
		}
		
		if (hitRate > 95) {
			hitRate = 95;
		}
		
		if (hitRate < 5) {
			hitRate = 5;
		} 
		
		int rate = random.nextInt (100) + 1;
		return rate < hitRate;
	}
	
	private int calcPc2NpcDamage (PcInstance src, MonsterInstance dest) {
		ItemInstance weapon = src.equipment.weapon;
		ItemInstance arrow = null;
		int weaponMaxDmg = 0;
		int weaponDmg = 0;
		int str = src.getStr ();
		int dex = src.getDex ();
		
		/* 有傷害無效技能優先處理 return 0 */
		
		if (weapon != null) {
			if (weapon.isRemoteWeapon ()) { //遠程武器
				arrow = src.equipment.arrow;
				
				if (dest.size == 0) { //小型怪
					weaponMaxDmg = weapon.dmgSmall;
					weaponMaxDmg += arrow.dmgSmall;
				} else { //大型怪
					weaponMaxDmg = weapon.dmgLarge;
					weaponMaxDmg = arrow.dmgLarge;
				}
				
				src.removeItem (arrow.uuid, 1);
				
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
				
				if (dest.isUndead || dest.isWolf || dest.isOrc) {
					if (arrow.isSilver ()) {
						weaponMaxDmg += 4;
					} else if (arrow.isMithril ()) {
						weaponMaxDmg += 6;
					} else if (arrow.isOriharukon ()) {
						weaponMaxDmg += 10;
					}
				}
				
			} else { //近戰武器
				if (dest.size == 0) { //小型怪
					weaponMaxDmg = weapon.dmgSmall;
				} else { //大型怪
					weaponMaxDmg = weapon.dmgLarge;
				}
				
				if (str > 50) {
					weaponMaxDmg += STR_DMG_OFFSET[50];
				} else {
					weaponMaxDmg += STR_DMG_OFFSET[str];
				}
				
				if (src.isKnight ()) {
					weaponMaxDmg += src.level / 10;
				}
				
				if (src.isDarkelf ()) {
					//雙刀1/3機率打出最大傷害 特效3671
					//雙刀1/4機率打出雙倍傷害 特效3398
					//雙刀雙爪有雙重破壞時1/3機率打出雙倍傷害
				}
				
				if (dest.isUndead || dest.isWolf || dest.isOrc) {
					if (weapon.isSilver ()) {
						weaponMaxDmg += 4;
					} else if (weapon.isMithril ()) {
						weaponMaxDmg += 6;
					} else if (weapon.isOriharukon ()) {
						weaponMaxDmg += 10;
					}
				}
				
			}
			
			//有擬似魔法武器+2
			//有烈焰之魂固定打最高傷害
			
			weaponDmg = 1 + random.nextInt (weaponMaxDmg) + weapon.enchant;
		} else {
			weaponDmg = 1;
		}
		
		return weaponDmg;
	}
	
	private int calcNpc2PcDamage (MonsterInstance src, PcInstance dest) {
		int dmg = 5 * (src.level / 10);
		dmg += random.nextInt (src.level - dmg + 5);
		dmg += src.basicParameters.str;
		dmg = dmg >>> 1;
		dmg++;
		
		int ac = Math.min (0, dest.getAc ());
		switch (dest.type) {
		case 0:
			dmg += random.nextInt (ac / 3 + 1);
			break;
		case 1:
			dmg += random.nextInt (ac / 2 + 1);
			break;
		case 2:
			dmg += random.nextInt (ac / 4 + 1);
			break;
		case 3:
			dmg += random.nextInt (ac / 5 + 1);
			break;
		case 4:
			dmg += random.nextInt (ac / 4 + 1);
			break;
		}
		
		//壞物傷害/2
		//有反擊屏障需要處理
		
		return dmg;
	}
	
	
	
	//new!
	private void calcDmg (PcInstance pc) {
	}
	
	//new!
	private void calcDmg (ActiveModel npc) {
	}
	
	//new!
	private void calcHitRate (PcInstance pc) {
	}
	
	//new!
	private void calcHitRate (ActiveModel npc) {
	}

	
	private int calcEr (int type, int level, int dex) {
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
}
