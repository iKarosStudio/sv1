package vidar.game.template;

public class SkillTemplate
{
	public int SkillLv;
	public int SkillId;
	public int SkillNumber;
	public String Name;
	public String NameId;
	public int Id;
	public int GfxId;
	
	public int MpCost;
	public int HpCost;
	public int ItemCostId;
	public int ItemCostAmount;
	
	public int DelayTime;
	public int RemainTime;
	
	/*
	 *  0:none(自身) 1:attack(攻擊) 2:buff(輔助)
	 */
	public int Target;
	
	/*
	 * 0:自己
	 * 1:PC(未確認)
	 * 2:NPC
	 * 4:血盟成員
	 * 8:團隊成員
	 * 16:寵物
	 * 32:場所
	 */
	public int TargetTo;
	
	public int DamageBase;
	public int DamageDice;
	public int DamageDiceCount;
	
	public int ProperbilityValue;
	public int ProperbilityDice;
	
	/* Element 0:none 1:Earth 2:Fire 4:Water 8:Wind */
	public int Attr;
	public int ActId;
	
	public int Type;
	public int Lawful;
	public int Range;
	public int Area;
	
	public int MsgIdStart;
	public int MsgIdStop;
	public int MsgIdFail;
	
	public boolean Through;
	public boolean ArrowType;
	
	public SkillTemplate (
		int _skillId,
		String _name,
		int _skillLevel,
		int _skillNumber,
		int _mpCost,
		int _hpCost,
		int _itemCostId,
		int _itemCostCount,
		int _delayTime,
		int _remainTime,
		String _target,
		int _targetTo,
		int _damageValue,
		int _damageDice,
		int _damageDiceCount,
		int _properbilityValue,
		int _properbilityDice,
		int _attr,
		int _actId,
		int _type,
		int _lawful,
		int _ranged,
		int _area,
		int _through,
		int _id,
		String _nameId,
		int _castGfx,
		int _msgIdStart,
		int _msgIdStop,
		int _msgIdFail,
		int _arrowType) {
		
		SkillId = _skillId;
		Name = _name;
		SkillLv = _skillLevel;
		SkillNumber = _skillNumber;
		MpCost = _mpCost;
		HpCost = _hpCost;
		ItemCostId = _itemCostId;
		ItemCostAmount = _itemCostCount;
		DelayTime = _delayTime;
		RemainTime = _remainTime;
		
		//Target
		if (_target.contentEquals ("none")) {
			Target = 0;
		} else if (_target.contentEquals ("attack")) {
			Target = 1;
		} else if (_target.contentEquals ("buff") ) {
			Target = 2;
		}
		
		TargetTo = _targetTo;
		DamageBase = _damageValue;
		DamageDice = _damageDice;
		DamageDiceCount = _damageDiceCount;
		ProperbilityValue = _properbilityValue;
		ProperbilityDice = _properbilityDice;
		Attr = _attr;
		ActId = _actId;
		Type = _type;
		Lawful = _lawful;
		Range = _ranged;
		Area = _area;
		//Through
		if (_through > 0) {
			Through = true;
		} else {
			Through = false;
		}
		Id = _id;
		NameId = _nameId;
		GfxId = _castGfx;
		MsgIdStart = _msgIdStart;
		MsgIdStop = _msgIdStop;
		MsgIdFail = _msgIdFail;
		//Arrowtype
		if (_arrowType > 0) {
			ArrowType = true;
		} else {
			ArrowType = false;
		}
	}
}
