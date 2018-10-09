package vidar.game.skill;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;
import vidar.game.skill.common_skill.*;
import vidar.game.template.*;
import static vidar.game.skill.SkillId.*;

public class TargetAttackSkill
{
	SessionHandler handle;
	PcInstance pc;
	
	public TargetAttackSkill (SessionHandler _handle, SkillTemplate skillTemplate, PacketReader packetReader) {
		handle = _handle;
		pc = handle.getPc ();
		int tid = 0; //target id
		
		System.out.printf ("\tAttack target skill ->\n");
		
		switch (skillTemplate.skillId) {
		case ENERGY_BOLT: //光箭
		case ICE_DAGGER: //冰箭
		case WIND_SHURIKEN: //風刃
		case FIRE_ARROW: //火箭
		case STALAC: //地獄之牙
		case EARTH_JAIL: //岩牢
		case CONE_OF_COLD: //冰椎
		case CALL_LIGHTNING: //極道落雷
		case ERUPTION: //地裂
		case SUNBURST: //烈焰
		case METEOR_STRIKE: //流星雨
		case DESTROY: //究極光裂
			tid = packetReader.readDoubleWord ();
			new S_TargetAttackSkill (handle, skillTemplate, tid);
			break;	
			
		default:
			System.out.printf ("\t\t%s 還沒做的技能\n", skillTemplate.name);
		}
	}
}
