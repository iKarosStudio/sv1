package vidar.game.template;

import vidar.game.model.*;
import vidar.game.skill.*;

/*
 * 基本非玩家控制實體
 */
public class NpcTemplate extends ActiveModel
{
	public String nameId;
	public String family;
	public String impl;
	public String note;

	/* 每 n ms執行一次 */
	public int moveInterval;
	public int attackInterval;
	public int majorSkillInterval;
	public int minorSkillInterval;
	
	//主動怪物
	public boolean agro;
	
	public boolean isUndead;
	
	public NpcTemplate () {
		System.out.println ("警告 不該被呼叫") ;
	}
	
	public NpcTemplate (
			int _npcId, //Template id
			String _name,
			String _nameId,
			String _note,
			String _impl, //NPC Type
			int _gfx, //shape
			int _level, int _hp, int _mp, int _ac,
			int _str, int _con, int _dex, int _wis, int _intel,
			int _mr, int _exp, int _lawful,
			String _size,
			int _weakWater, int _weakWind, int _weakFire, int _weakEarth,
			int _ranged, boolean _isTamble,
			int _moveSpeed, int _attackSpeed, int _attackSkillSpeed, int _attackSubSkillSpeed,
			int _undead, int _poisonAttack, int _paralyseAttack,
			int _agro, //主動被動設定
			int _agrososc, //看穿隱身
			int _agrocoi, //看穿變身
			String _family,
			int _argofamily, int _pickUpItem, int _digestItem, int _braveSpeed,
			int _hprInterval, int _hpr, int _mprInterval, int _mpr, 
			int _teleport,
			int _randomLevel, int _randomHp, int _randomMp, int _randomAc, int _randomExp, int _randomLawful,
			int _dmgReduction, int _isHard, int _doppel, int _isTu, int _isEarse,
			int _bowActId, int _karma, int _transformId, int _lightSize, int _amountFixed, int _attackExSpeed,
			int _attStatus, int _bowUseId, int _hasCastle, int _board) 
	{
		uuid = _npcId;
		name = _name;
		nameId = _nameId;
		family = _family;
		impl = _impl;
		note = _note;
		gfx = _gfx;
		actId = 0;
		
		basicParameters = new AbilityParameter () ;
		basicParameters.str = _str; basicParameters.con = _con; basicParameters.dex = _dex;
		basicParameters.wis = _wis; basicParameters.cha = 0  ; basicParameters.intel = _intel;
		basicParameters.defWater = _weakWater; basicParameters.defWind = _weakWind;
		basicParameters.defEarth = _weakEarth; basicParameters.defFire = _weakFire;
		basicParameters.maxHp = _hp; basicParameters.maxMp = _mp;
		basicParameters.ac = _ac;
		
		this.level = _level;
		exp = _exp;
		if (_size.equalsIgnoreCase ("small") ) {
			size = 0;
		} else {
			size = 1;
		}
		
		moveInterval = _moveSpeed;
		attackInterval = _attackSpeed;
		majorSkillInterval = _attackSkillSpeed;
		minorSkillInterval = _attackSubSkillSpeed;
		
		agro = (_agro > 0) ? true:false;
		
		isUndead = (_undead > 0) ? true : false;
	}

	@Override
	public int getStr () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getCon () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getDex () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getWis () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getCha () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getIntel () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getSp () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getMr () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getAc () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getMaxHp () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getMaxMp () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getHpr () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getMpr () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getDmgReduction () {
		//Nothing to do
		return 0;
	}

	@Override
	public int getWeightReduction () {
		//Nothing to do
		return 0;
	}

	@Override
	public void updateModel () {
		//Nothing to do
	}

	@Override
	public boolean isParalyzed () {
		//Nothing to do
		return false;
	}

	@Override
	public boolean hasSkillEffect (int skillId) {
		//Nothing to do
		return false;
	}

	@Override
	public void giveItem () {
		//Nothing to do
	}

	@Override
	public void recvItem () {
		//Nothing to do
	}

	@Override
	public void pickItem (int uuid, int count, int x, int y) {
		//Nothing to do
	}

	@Override
	public void dropItem (int uuid, int count, int x, int y) {
		//Nothing to do
	}

	@Override
	public void damage (NormalAttack atk) {
		//Nothing to do
	}

	@Override
	public String getName () {
		return nameId;
	}
}
