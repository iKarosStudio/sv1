package vidar.game.skill.common_skill;

import vidar.server.*;
import vidar.game.model.*;
import vidar.game.template.*;

public class s001_LesserHeal
{
	SessionHandler handle;
	PcInstance pc;
	
	public s001_LesserHeal (SessionHandler _handle, SkillTemplate skillTemplate, int tid) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t%s 對目標:%d\n", skillTemplate.name, tid);
		
		Model target = pc.map.getModel (tid);
		pc.useSkill (tid, ModelActionId.NONE_TARGETED_SKILL, skillTemplate.gfx, target.location.point.x, target.location.point.y);
	}
}
