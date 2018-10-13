package vidar.game;

import java.sql.*;

import vidar.server.database.*;
import vidar.game.map.*;
import vidar.game.model.*;

public class DoorGenerator
{
	private static DoorGenerator instance;
	private static Vidar vidar;
	
	public static DoorGenerator getInstance () {
		if (instance == null) {
			instance = new DoorGenerator ();
		}
		return instance;
	}
	
	public DoorGenerator () {
		vidar = Vidar.getInstance () ;
		
		for (int mapId = 0; mapId < MapLoader.MAPID_LIMIT; mapId++) {
			VidarMap map = vidar.getMap (mapId) ;
			if (map != null) {
				ResultSet rs = DatabaseCmds.doorSpawnList (mapId);
				try {
					while (rs.next ()) {
						DoorInstance door = new DoorInstance (
							rs.getInt ("id"),
							rs.getString ("location"),
							rs.getInt ("gfxid"),
							rs.getInt ("locx"),
							rs.getInt ("locy"),
							rs.getInt ("mapid"),
							rs.getInt ("direction"),
							rs.getInt ("entrancex"),
							rs.getInt ("entrancey"),
							rs.getInt ("hp"),
							rs.getBoolean ("keeper"),
							rs.getInt ("key"),
							rs.getInt ("size"),
							rs.getInt ("castle"),
							rs.getInt ("order"));
						
						//map.doors.put (door.uuid, door);
						map.addModel (door);
						map.setAccessible (door.location.p.x, door.location.p.y, false);
					}
				} catch (Exception e) {
					e.printStackTrace ();
					
				} finally {
					DatabaseUtil.close (rs);
					
				}
			}
		}
		//DatabaseCmds.DoorSpawinlist (0) ;
	}
}
