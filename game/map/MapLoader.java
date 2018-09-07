package vidar.game.map;

import java.io.*;
import java.sql.*;

import vidar.types.*;
import vidar.server.database.*;
import vidar.game.*;

public class MapLoader
{
	public static final int OFFSET_MAPID = 0;
	public static final int OFFSET_START_X = 0;
	public static final int OFFSET_END_X = 1;
	public static final int OFFSET_START_Y = 2;
	public static final int OFFSET_END_Y = 3;
	
	public static final int MAPID_LIMIT = 3;
	
	public MapLoader (Vidar handle) {
		load (handle) ;		
	}
	
	public static void load (Vidar handle) {
		/*
		 * 載入伺服器地圖檔案
		 */
		System.out.printf ("Load map files...") ;
		
		long timeStarts = System.currentTimeMillis () ;

		MapInfo.sizeTable.forEach ((Integer mapId, int[] info)->{
			Connection con = HikariCP.getConnection () ;
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			try {
				if (mapId > MAPID_LIMIT) return ;
				
				String mapFilePath = String.format ("./maps/%d.txt", mapId) ;
				FileReader fileHandler = new FileReader (mapFilePath) ;
				BufferedReader mapFile = new BufferedReader (fileHandler) ;
				//System.out.printf ("\tLoad map->%s\n", Path) ;
				
				VidarMap map = new VidarMap (
						mapId,
						info[OFFSET_START_X],
						info[OFFSET_END_X],
						info[OFFSET_START_Y],
						info[OFFSET_END_Y]);
				
				String s = null;
				
				int y = 0;
				while ((s = mapFile.readLine () ) != null) {
					if (s.length () == 0) {
						continue;
					}
					
					String[] tiles = s.split (",") ;
					int x = 0;
					for (String tile : tiles) {
						map.setTile (x, y, Byte.valueOf (tile));
						x++;
					}
					y++;
				}
				
				/* 將地圖加入世界管理 */
				handle.addMap (map) ;
				fileHandler.close () ;
				
				/* 讀取地圖傳送點並新增 */
				ps = con.prepareStatement ("SELECT * FROM dungeon WHERE src_mapid=?;") ;
				ps.setInt (1, map.mapId) ;
				rs = ps.executeQuery () ;
				
				while (rs.next () ) {
					Location dest = new Location (rs.getInt ("new_mapid"), rs.getInt ("new_x"), rs.getInt ("new_y"));
					map.addTpLocation (rs.getInt ("src_x"), rs.getInt ("src_y"), dest) ;
				}
				
				//ValidCount++;
			} catch (Exception e) {
				e.printStackTrace () ;
				
			} finally {
				DatabaseUtil.close (rs);
				DatabaseUtil.close (ps);
				DatabaseUtil.close (con);
				
			}
		}); //end for mapid	
		long timeEnds = System.currentTimeMillis () ;
		
		float usedTime = (float) (timeEnds - timeStarts) / 1000;
		System.out.printf ("loaded in\t%.3f s\n", usedTime) ;
	}
}
