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
		int skill_id,
		String name,
		int skill_level,
		int skill_number,
		int mp_cost,
		int hp_cost,
		int item_cost_id,
		int item_cost_count,
		int delay_time,
		int remain_time,
		String target,
		int target_to,
		int damage_value,
		int damage_dice,
		int damage_dice_count,
		int properbility_value,
		int properbility_dice,
		int attr,
		int actid,
		int type,
		int lawful,
		int ranged,
		int area,
		int through,
		int id,
		String name_id,
		int cast_gfx,
		int msg_id_start,
		int msg_id_stop,
		int msg_id_fail,
		int arrow_type) {
		
		SkillId = skill_id;
		Name = name;
		SkillLv = skill_level;
		SkillNumber = skill_number;
		MpCost = mp_cost;
		HpCost = hp_cost;
		ItemCostId = item_cost_id;
		ItemCostAmount = item_cost_count;
		DelayTime = delay_time;
		RemainTime = remain_time;
		
		//Target
		if (target.contentEquals ("none")) {
			Target = 0;
		} else if (target.contentEquals ("attack")) {
			Target = 1;
		} else if (target.contentEquals ("buff") ) {
			Target = 2;
		}
		
		TargetTo = target_to;
		DamageBase = damage_value;
		DamageDice = damage_dice;
		DamageDiceCount = damage_dice_count;
		ProperbilityValue = properbility_value;
		ProperbilityDice = properbility_dice;
		Attr = attr;
		ActId = actid;
		Type = type;
		Lawful = lawful;
		Range = ranged;
		Area = area;
		//Through
		if (through > 0) {
			Through = true;
		} else {
			Through = false;
		}
		Id = id;
		NameId = name_id;
		GfxId = cast_gfx;
		MsgIdStart = msg_id_start;
		MsgIdStop = msg_id_stop;
		MsgIdFail = msg_id_fail;
		//Arrowtype
		if (arrow_type > 0) {
			ArrowType = true;
		} else {
			ArrowType = false;
		}
	}
}
