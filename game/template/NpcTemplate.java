package vidar.game.template;

import vidar.game.model.*;

/*
 * 基本非玩家控制實體
 */
public class NpcTemplate extends Model
{
	public int Gfx = 0;
	public String Impl;
	public String Name;
	public String NameId;
	public String Family;
	public String Note;
	public String NpcType;
	public int Size; /* 0:small 1:large */
	public int Exp;
	
	/*
	 * 每 n ms執行一次
	 */
	public int MoveInterval;
	public int AttackInterval;
	public int MajorSkillInterval;
	public int MinorSkillInterval;
	
	//主動怪物
	public boolean Agro;
	
	public NpcTemplate () {
		System.out.println ("警告 不該被呼叫") ;
	}
	
	public NpcTemplate (
			int npc_id, //Template id
			String name,
			String name_id,
			String note,
			String impl, //NPC Type
			int gfxid, //shape
			int level, int hp, int mp, int ac,
			int str, int con, int dex, int wis, int intel,
			int mr, int exp, int lawful,
			String size,
			int weak_water, int weak_wind, int weak_fire, int weak_earth,
			int ranged, boolean tamable,
			int passispeed, int atkspeed, int atk_magic_speed, int sub_magic_speed,
			int undead, int poison_atk, int paralysis_atk,
			int agro, //主動被動設定
			int agrososc, //看穿隱身
			int agrocoi, //看穿變身
			String family,
			int argofamily, int pickupitem, int digestitem, int bravespeed,
			int hprinterval, int hpr, int mprinterval, int mpr, 
			int teleport,
			int random_level, int random_hp, int random_mp, int random_ac, int random_exp, int random_lawful,
			int damage_reduction, int hard, int doppel, int is_tu, int is_erase,
			int bow_act_id, int karma, int transform_id, int light_size, int amount_fixed, int atkexspeed,
			int att_status, int bow_use_id, int has_castle, int broad) 
	{
		uuid = npc_id;
		Name = name;
		NameId = name_id;
		Family = family;
		Impl = impl;
		Note = note;
		NpcType = impl ;
		Gfx = gfxid;
		basicParameters = new AbilityParameter () ;
		basicParameters.str = str; basicParameters.con = con; basicParameters.dex = dex;
		basicParameters.wis = wis; basicParameters.cha = 0  ; basicParameters.intel = intel;
		basicParameters.defWater = weak_water; basicParameters.defWind = weak_wind;
		basicParameters.defEarth = weak_earth; basicParameters.defFire = weak_fire;
		basicParameters.maxHp = hp; basicParameters.maxMp = mp;
		basicParameters.ac = ac;
		
		this.level = level;
		Exp = exp;
		if (size.equalsIgnoreCase ("small") ) {
			Size = 0;
		} else {
			Size = 1;
		}
		
		MoveInterval = passispeed;
		AttackInterval = atkspeed;
		MajorSkillInterval = atk_magic_speed;
		MinorSkillInterval = sub_magic_speed;
		
		Agro = (agro > 0) ? true:false;
	}
}
