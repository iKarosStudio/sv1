package vidar.game.skill.common_skill;

import vidar.server.*;
import vidar.game.model.*;
import vidar.game.template.*;

public class s002_Light
{
	SessionHandler handle;
	PcInstance pc;
	
	public s002_Light (SessionHandler _handle, SkillTemplate skillTemplate) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t使用%s\n", skillTemplate.name);
		
		pc.useSkill (pc.uuid, ModelActionId.NONE_TARGETED_SKILL, skillTemplate.gfx, pc.location.point.x, pc.location.point.y);
	}
}
