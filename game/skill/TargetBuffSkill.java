package vidar.game.skill;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.model.*;
import vidar.game.skill.common_skill.*;
import vidar.game.template.*;
import static vidar.game.skill.SkillId.*;

public class TargetBuffSkill
{
	SessionHandler handle;
	PcInstance pc;
	
	public TargetBuffSkill (SessionHandler _handle, SkillTemplate skillTemplate, PacketReader packetReader) {
		handle = _handle;
		pc = handle.getPc ();
		int tid = 0;
		
		System.out.printf ("\tBuff target skill ->\n");
		
		switch (skillTemplate.skillId) {
		case LESSER_HEAL:
			tid = packetReader.readDoubleWord ();
			new s001_LesserHeal (handle, skillTemplate, tid);
			break;
		
		case HOLY_WEAPON:
			tid = packetReader.readDoubleWord ();
			new S008_HolyWeapon (handle, skillTemplate, tid);
			break;
			
		default:
			System.out.printf ("\t\t%s 還沒做的技能\n", skillTemplate.name);
		}
	}
}
