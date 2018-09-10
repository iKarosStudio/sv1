package vidar.game;

import java.sql.*;
import java.util.*;

import vidar.server.database.*;
import vidar.game.template.*;
import vidar.game.map.*;
import vidar.game.model.npc.*;

public class NpcLoader
{	
	public NpcLoader (Vidar vidar) {
		System.out.printf ("Load npc datas...") ;
		
		List<String> errorList = new ArrayList <String> () ;
		long timeStarts = System.currentTimeMillis () ;
		
		/* 載入NPC產生清單 by Map */
		MapInfo.sizeTable.forEach ((Integer _mapId, int[] _info)->{
			if (_mapId > MapLoader.MAPID_LIMIT) {
				return;
			}
			
			Connection con = HikariCP.getConnection ();
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			try {
				ps = con.prepareStatement ("SELECT * FROM spawnlist_npc WHERE mapid=?;");
				ps.setInt (1, _mapId) ;
				ResultSet spawnlistRs = ps.executeQuery () ;
				
				while (spawnlistRs.next ()) {
					int uuid = spawnlistRs.getInt ("id") ;
					String locationName = spawnlistRs.getString ("location") ;
					int count = spawnlistRs.getInt ("count") ;
					int npcTemplateId = spawnlistRs.getInt ("npc_templateid") ;
					int posX = spawnlistRs.getInt ("locx") ;
					int posY = spawnlistRs.getInt ("locy") ;
					int mapId = spawnlistRs.getInt ("mapid") ;
					int heading = spawnlistRs.getInt ("heading") ;
					int randomX = spawnlistRs.getInt ("randomx") ;
					int randomY = spawnlistRs.getInt ("randomy") ;
					int respawnDelay = spawnlistRs.getInt ("respawn_delay") ;
					int movementDistance = spawnlistRs.getInt ("movement_distance") ;

					if (CacheData.npcs.containsKey (npcTemplateId) ) {
						NpcTemplate npcData = CacheData.npcs.get (npcTemplateId);
						NpcInstance npc = new NpcInstance (npcData);
						
						npc.location.point.x = posX;
						npc.location.point.y = posY;
						npc.location.mapId = mapId;
						npc.heading = heading;
						
						vidar.addNpc (npc);
						
					} else {
						String errorMessage = String.format ("\tNpc Template NOT EXIST, UUID:%d/Template:%d/Location:%s", 
							uuid,
							npcTemplateId,
							locationName
						) ;
						errorList.add (errorMessage) ;
					}
				}
			} catch (Exception e) {
				e.printStackTrace () ;
			} finally {
				DatabaseUtil.close (rs) ;
				DatabaseUtil.close (ps) ;
				DatabaseUtil.close (con) ;
			}
		}) ;
		
		long timeEnds = System.currentTimeMillis () ;
		float usedTime = (float) (timeEnds - timeStarts) / 1000;
		System.out.printf ("loaded in\t%.3f s\n", usedTime) ;
		
		if (errorList.size () > 0) {
			System.out.printf ("[***WARN!***] %d NPC LOAD FAIL:\n", errorList.size ());
			for (String s : errorList) {
				System.out.println (s) ;
			}
		}
	}
}
