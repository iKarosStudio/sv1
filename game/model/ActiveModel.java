package vidar.game.model;

import java.util.concurrent.*;

import vidar.game.skill.*;
import vidar.game.model.item.*;

public abstract class ActiveModel extends MapModel
{
	/* 有變身時的polyGfx */
	public int gfxTemp;
	
	public int braveSpeed;
	
	public volatile int hp;
	public volatile int mp;
	public boolean isDead = false;
	
	public ConcurrentHashMap<Integer, ItemInstance> itemBag;
	
	/* Buff/Debuff 效果計時 */
	public SkillEffectTimer skillBuffs = null;
	
	public AbilityParameter basicParameters;
	public AbilityParameter skillParameters;
	
	public void addSkillEffect (int skillId, int remainTime) {
		addSkillEffect (skillId, remainTime, 0);
	}
	
	public void addSkillEffect (int skillId, int remainTime, int polyId) {
		skillBuffs.addSkill (skillId, remainTime, polyId);
	}
	
	public void removeSkillEffect (int skillId) {
		skillBuffs.removeSkill (skillId);
	}

	public boolean hasSkillEffect (int skillId) {
		return skillBuffs.hasSkillEffect (skillId);
	}
	
	public int getBaseAc () {
		return basicParameters.ac;
	}
	
	public int getBaseMaxHp () {
		return basicParameters.maxHp;
	}
	
	public int getBaseMaxMp () {
		return basicParameters.maxMp;
	}
	
	public boolean isPoly () {
		return hasSkillEffect (SkillId.SHAPE_CHANGE);
	}
	
	//
	
	public abstract void pickItem (int uuid, int count, int x, int y);
	public abstract void dropItem (int uuid, int count, int x, int y);
	public abstract void giveItem ();
	public abstract void recvItem ();
	
	public abstract int getStr ();
	public abstract int getCon ();
	public abstract int getDex ();
	public abstract int getWis ();
	public abstract int getCha ();
	public abstract int getIntel ();
	public abstract int getSp ();
	public abstract int getMr ();
	public abstract int getAc ();
	public abstract int getMaxHp ();
	public abstract int getMaxMp ();
	public abstract int getHpr ();
	public abstract int getMpr ();
	public abstract int getDmgModify ();
	public abstract int getSpModify ();
	public abstract int getHitModify ();
	public abstract int getBowHitModify ();
	public abstract int getDmgReduction ();
	public abstract int getWeightReduction ();
}
