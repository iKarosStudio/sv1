package vidar.game.template;

import vidar.game.model.*;

/*
 * 基本非玩家控制實體
 */
public class NpcTemplate extends Model
{
	public String nameId;
	public String family;
	public String impl;
	public String note;
	
	public int exp;
	public int size; //0:small 1:large
	/*
	 * 每 n ms執行一次
	 */
	public int MoveInterval;
	public int AttackInterval;
	public int MajorSkillInterval;
	public int MinorSkillInterval;
	
	//主動怪物
	public boolean agro;
	
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
		
		MoveInterval = _moveSpeed;
		AttackInterval = _attackSpeed;
		MajorSkillInterval = _attackSkillSpeed;
		MinorSkillInterval = _attackSubSkillSpeed;
		
		agro = (_agro > 0) ? true:false;
	}
}
