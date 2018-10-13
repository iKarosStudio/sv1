package vidar.game.skill.common_skill;

import vidar.server.*;
import vidar.game.model.*;
import vidar.game.template.*;

public class S008_HolyWeapon
{
	SessionHandler handle;
	PcInstance pc;
	
	public S008_HolyWeapon (SessionHandler _handle, SkillTemplate skillTemplate, int tid) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t%s 對目標:%d\n", skillTemplate.name, tid);
		
		MapModel target = pc.getCurrentMap ().getModel (tid);
		pc.useSkill (tid, ModelActionId.NONE_TARGETED_SKILL, skillTemplate.gfx, target.location.p.x, target.location.p.y);
		
		if (tid == pc.uuid) {
			pc.addSkillEffect (skillTemplate.skillId, skillTemplate.remainTime);
		} else {
			if (pc.getCurrentMap ().pcs.contains (tid)) {
				pc.getCurrentMap ().pcs.get (tid).addSkillEffect (skillTemplate.skillId, skillTemplate.remainTime);
			}
		}
	}
}
