package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.game.*;
import vidar.game.model.*;
import vidar.game.skill.*;
import vidar.game.template.*;

public class SkillUse
{
	PacketReader packetReader;
	
	PcInstance pc;
	SessionHandler handle;
	
	public SkillUse (SessionHandler _handle, byte[] data) {
		packetReader = new PacketReader (data);
		handle = _handle;
		pc = handle.getPc ();
		
		int row = packetReader.readByte ();
		int column = packetReader.readByte ();
		
		int skillId = ((row << 3) | column) + 1;
		
		System.out.printf ("%s use skillId(%d)-%s targetTo:0x%02X type:0x%02X ->\n", 
				pc.name,
				skillId,
				CacheData.skill.get (skillId).name,
				CacheData.skill.get (skillId).targetTo,
				CacheData.skill.get (skillId).type);
		
		SkillTemplate skill = CacheData.skill.get (skillId);
		
		switch (skill.target) {
		case SkillId.SKILL_TARGET_NONE:
			new TargetNoneSkill (handle, skill, packetReader);
			break;
			
		case SkillId.SKILL_TARGET_ATTACK:
			new TargetAttackSkill (handle, skill, packetReader);
			break;
			
		case SkillId.SKILL_TARGET_BUFF:
			new TargetBuffSkill (handle, skill, packetReader);
			break;
			
		default:
			break;
		}
		
	}
}
