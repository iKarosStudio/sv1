package vidar.server.process_client;

import java.util.*;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.server.database.*;
import vidar.game.*;
import vidar.game.model.*;

public class SkillBuyOrder
{
	PacketReader reader;
	
	public SkillBuyOrder (SessionHandler handle, byte[] data) {
		reader = new PacketReader (data);
		PcInstance pc = handle.getPc ();
		
		int OrderCount = reader.readWord ();
		int SkillItem[] = new int[OrderCount] ;
		int L1 = 0, L1Cost = 0;
		int L2 = 0, L2Cost = 0;
		int L3 = 0, L3Cost = 0;
		int price = 0;
		
		/* 讀取買入的選項 */
		for (int i = 0; i < OrderCount; i++) {
			SkillItem[i] = reader.readDoubleWord ();
			
			if (SkillItem[i] < 8) { //level 1
				L1 |= (1 << SkillItem[i]);
				L1Cost += 100;
			} else if (SkillItem[i] < 16) { //level 2
				L2 |= (1 << (SkillItem[i]-8));
				L2Cost += 400;
			} else if (SkillItem[i] < 24) { //level 3
				L3 |= (1 << (SkillItem[i]-16));
				L3Cost += 900;
			}
			
		}
		
		/* 根據職業及角色等級過濾還不能學的選項 */
		if (pc.isRoyal ()) {
			if (pc.level < 10) {
				L1 = 0; L1Cost = 0;
				L2 = 0; L2Cost = 0;
			} else if (pc.level < 20) {
				L2 = 0; L2Cost = 0;
			}
			L3 = 0; L3Cost = 0;
		}
		
		if (pc.isKnight ()) {
			if (pc.level < 50) {
				L1 = 0; L1Cost = 0;
			}
			L2 = 0; L2Cost = 0;
			L3 = 0; L3Cost = 0;
		}
		
		if (pc.isElf ()) {
			if (pc.level < 8) {
				L1 = 0; L1Cost = 0;
				L2 = 0; L2Cost = 0;
				L3 = 0; L3Cost = 0;
			} else if (pc.level < 16) {
				L2 = 0; L2Cost = 0;
				L3 = 0; L3Cost = 0;
			} else if (pc.level < 24) {
				L3 = 0; L3Cost = 0;
			}
		}
		
		if (pc.isWizard ()) {
			if (pc.level < 4) {
				L1 = 0; L1Cost = 0;
				L2 = 0; L2Cost = 0;
				L3 = 0; L3Cost = 0;
			} else if (pc.level < 8) {
				L2 = 0; L2Cost = 0;
				L3 = 0; L3Cost = 0;
			} else if (pc.level < 12) {
				L3 = 0; L3Cost = 0;
			}
		}
		
		if (pc.isDarkelf ()) {
			if (pc.level < 12) {
				L1 = 0; L1Cost = 0;
				L2 = 0; L2Cost = 0;
			} else if (pc.level < 24) {
				L2 = 0; L2Cost = 0;
			}
			L3 = 0; L3Cost = 0;
		}
		
		if ( (L1 | L2 | L3) == 0) {
			return;
		}
		
		price = L1Cost + L2Cost + L3Cost;
		if (pc.getMoney () < price) {
			/* 錢不夠 */
			handle.sendPacket (new ServerMessage (189).getRaw ());
		} else {
			HashMap<Integer, Integer> s = new HashMap<Integer, Integer> () ;
			for (int level = 1; level <= 24; level++) {
				if (level == 1) {
					s.put (level, L1) ;
				} else if (level == 2) {
					s.put (level, L2) ;
				} else if (level == 3) {
					s.put (level, L3) ;
				} else {
					s.put (level, 0) ;
				}
			}
			
			/* 檢查各等級沒學過的技能並寫入資料庫 */
			if (L1 != 0) {
				for (int b = 0; b < 7; b++) {
					if (((L1 >>> b) & 0x01) > 0) {
						if (!DatabaseCmds.checkSkill (pc.uuid, 1+b) ) {
							DatabaseCmds.saveSkills (pc.uuid, 1+b, CacheData.skill.get (1+b).Name);
						}
					}
				}
			}
			
			if (L2 != 0) {
				for (int b = 0; b < 7; b++) {
					if (((L2 >>> b) & 0x01) > 0) {
						if (!DatabaseCmds.checkSkill (pc.uuid, 9+b) ) {
							DatabaseCmds.saveSkills (pc.uuid, 9+b, CacheData.skill.get (9+b).Name) ;
						}
					}
				}
			}
			
			if (L3 != 0) {
				for (int b = 0; b < 7; b++) {
					if (((L3 >>> b) & 0x01) > 0) {
						if (!DatabaseCmds.checkSkill (pc.uuid, 17+b) ) {
							DatabaseCmds.saveSkills (pc.uuid, 17+b, CacheData.skill.get (17+b).Name) ;
						}
					}
				}
			}
			
			/* show virtual effect */
			handle.sendPacket (new VisualEffect (pc.uuid, 224).getRaw ());
			handle.sendPacket (new SkillTable (pc.type, s).getRaw ());
			
			/* 扣錢  */
			//Pc.removeItemByItemId (40308, Price) ;
		}
		
	}
}
