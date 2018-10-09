package vidar.game.skill.common_skill;

import vidar.types.*;
import vidar.game.model.*;
import vidar.game.template.*;
import vidar.server.*;
import vidar.server.process_server.*;

public class s005_Teleport
{
	SessionHandler handle;
	PcInstance pc;
	
	public s005_Teleport (SessionHandler _handle, SkillTemplate skillTemplate) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\t\t使用%s\n", skillTemplate.name);
		
		//pc.useSkill (pc.uuid, ModelActionId.NONE_TARGETED_SKILL, skillTemplate.gfx, pc.location.point.x, pc.location.point.y);
		Location dest = pc.map.getRandomLocation ();
		new vidar.server.process_server.Teleport (pc, dest, true);
	}
}
