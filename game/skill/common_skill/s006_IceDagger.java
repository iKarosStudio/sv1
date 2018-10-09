package vidar.game.skill.common_skill;

import vidar.server.*;
import vidar.game.model.*;
import vidar.game.template.*;

public class s006_IceDagger
{
	SessionHandler handle;
	PcInstance pc;
	
	public s006_IceDagger (SessionHandler _handle, SkillTemplate skillTemplate, int tid) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t%s 目標%d\n", skillTemplate.name, tid);
		
		Model target = pc.map.getModel (tid);
		pc.useAttackSkill (tid, ModelActionId.TARGETED_SKILL, skillTemplate.gfx, target.location.point.x, target.location.point.y, true);
	}
}
