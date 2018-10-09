package vidar.game.skill;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;
import vidar.game.skill.common_skill.*;
import vidar.game.template.*;
import static vidar.game.skill.SkillId.*;

public class TargetNoneSkill
{
	SessionHandler handle;
	PcInstance pc;
	
	public TargetNoneSkill (SessionHandler _handle, SkillTemplate skillTemplate, PacketReader packetReader) {
		handle = _handle;
		pc = handle.getPc ();
		
		System.out.printf ("\tNone target skill ->\n");
		
		switch (skillTemplate.skillId) {
		case LIGHT:
			new s002_Light (handle, skillTemplate);
			break;
			
		case SHIELD:
			new s003_Shield (handle, skillTemplate);
			break;
		
		case TELEPORT:
			new s005_Teleport (handle, skillTemplate);
			break;
			
		default:
			System.out.printf ("\t\t%s 還沒做的技能\n", skillTemplate.name);
		}
		
		
	}
}
