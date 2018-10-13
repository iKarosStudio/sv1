package vidar.game.skill.common_skill;

import vidar.server.*;
import vidar.game.model.*;
import vidar.game.template.*;

public class s004_EnergyBolt
{
	SessionHandler handle;
	PcInstance pc;
	
	public s004_EnergyBolt (SessionHandler _handle, SkillTemplate skillTemplate, int tid) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t%s 目標%d\n", skillTemplate.name, tid);
		
		MapModel target = pc.getCurrentMap ().getModel (tid);
		pc.useAttackSkill (tid, ModelActionId.TARGETED_SKILL, skillTemplate.gfx, target.location.p.x, target.location.p.y, true);
	}
}
