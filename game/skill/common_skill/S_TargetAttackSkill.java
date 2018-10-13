package vidar.game.skill.common_skill;

import vidar.game.model.*;
import vidar.game.template.*;
import vidar.server.*;

public class S_TargetAttackSkill
{
	SessionHandler handle;
	PcInstance pc;
	
	public S_TargetAttackSkill (SessionHandler _handle, SkillTemplate skillTemplate, int tid) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t%s 目標%d\n", skillTemplate.name, tid);
		
		MapModel target = pc.getCurrentMap ().getModel (tid);
		pc.useAttackSkill (tid, ModelActionId.TARGETED_SKILL, skillTemplate.gfx, target.location.p.x, target.location.p.y, true);
	}
}
