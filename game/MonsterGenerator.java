package vidar.game;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import vidar.config.*;
import vidar.types.*;
import vidar.server.database.*;
import vidar.server.utility.*;
import vidar.game.map.*;
import vidar.game.model.item.*;
import vidar.game.model.monster.*;
import vidar.game.template.*;

public class MonsterGenerator extends Thread implements Runnable {
	VidarMap vidarMap;
	
	static Random random = new Random (System.currentTimeMillis ());
	
	/* <K, V> = <spawnlist主鍵, 產生參數> */
	ConcurrentHashMap<Integer, MonsterSpawnList> spawnList = null;
	
	/* <K, V> = <怪物編號, 物品掉落清單> */
	ConcurrentHashMap<Integer, List<MonsterDropList>> dropList = null;
	
	public void run () {
		try {
			/*
			 * msl->monster spawn list
			 */
			spawnList.forEach ((Integer u, MonsterSpawnList msl)->{
				while (msl.monsters.size () < msl.count) {
				//if (msl.Mobs.size () < msl.Count) {
				//if (msl.Mobs.size () < 1) {
					NpcTemplate monsterTemplate = CacheData.npcs.get (msl.npcTemplateId);
					
					/* 定點生怪 */
					//Location SpawnLoc = new Location (msl.MapId, msl.LocX, msl.LocY, msl.Heading) ;
					
					/* 區域生怪 */
					int x = 0, y = 0;
					int dx = msl.locX2 - msl.locX1;
					int dy = msl.locY2 - msl.locY1;
					if (dx < 1) {
						dx = 1;
					}
					if (dy < 1) {
						dy = 1;
					}
					
					/* 確保怪物不會生在非行動區域 */
					do {
						x = msl.locX1 + random.nextInt (dx);
						y = msl.locY1 + random.nextInt (dy);
						if (x == 0 || y == 0) {
							x = msl.locX; y = msl.locY;
						}
					} while (!vidarMap.isNextTileAccessible (x, y, msl.heading));
					Location spawnLocation = new Location (msl.mapId, x, y); //new Location (msl.MapId, x, y, msl.Heading) ;
					
					/* 製作怪物實體 */
					MonsterInstance monster = new MonsterInstance (monsterTemplate, spawnLocation);
					monster.movementDistance = msl.movementDistance;
					monster.uuid = UuidGenerator.next ();//FIX
					
					//System.out.printf ("要求產生[%s]%d隻在地圖%d", msl.Location, (msl.Count-msl.Mobs.size()), msl.MapId) ;
					//System.out.printf (":0x%08X\n", Mob.Uuid) ;
					
					/* 產生怪物持有道具 (mdl->monster drop list) */
					List<MonsterDropList> mdl = dropList.get (msl.npcTemplateId);
					if (mdl != null) {
						for (MonsterDropList i : mdl) {		
							int dropRate = random.nextInt (1000000) * Configurations.RateDropItem;
							if (dropRate < i.posibility) {	
								ItemInstance dropItem = new vidar.game.model.item.ItemInstance (i.itemId, 0, 0, 0, 0, 0, 100, false, false);
								if (dropItem.isItem ()) {
									dropItem.count = i.min + random.nextInt (i.max);
									if (dropItem.id == 40308) {
										dropItem.count *= Configurations.RateDropGold;
									}
								} else {
									dropItem.count = 1;
								}
								
								monster.itemBag.put (dropItem.id, dropItem);
							} //end of item droped
						} //end of mdl
					}
					
					//vidarMap.monsters.put (monster.uuid, monster);
					vidarMap.addModel (monster);
					msl.monsters.add (monster) ;
					monster.actionStatus = MonsterInstance.ACTION_IDLE; /* 生怪後的初始狀態 */
				}
			});
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	public MonsterGenerator (VidarMap map) {
		vidarMap = map;
		spawnList = new ConcurrentHashMap<Integer, MonsterSpawnList> ();
		dropList  = new ConcurrentHashMap<Integer, List<MonsterDropList>> ();
		updateSpawnList ();
	}
	
	public void removeMonster (MonsterInstance m) {
		spawnList.forEach ((Integer u, MonsterSpawnList msl)->{
			msl.monsters.remove (m);
		});
	}
	
	public void updateSpawnList () {
		ResultSet rs = DatabaseCmds.mobSpawnList (vidarMap.mapId);
		try {
			while (rs.next ()) {
				MonsterSpawnList msl = new MonsterSpawnList (
					rs.getInt ("id"),
					rs.getString ("location"),
					rs.getInt ("count"),
					rs.getInt ("npc_templateid"),
					rs.getInt ("group_id"),
					rs.getInt ("locx"),
					rs.getInt ("locy"),
					rs.getInt ("randomx"), 
					rs.getInt ("randomy"),
					rs.getInt ("mapid"), 
					rs.getInt ("locx1"), 
					rs.getInt ("locy1"), 
					rs.getInt ("locx2"), 
					rs.getInt ("locy2"), 
					rs.getInt ("heading"), 
					rs.getInt ("min_respawn_delay"),
					rs.getInt ("max_respawn_delay"),
					rs.getInt ("respawn_screen"),
					rs.getInt ("movement_distance"),
					rs.getInt ("rest"),
					rs.getInt ("near_spawn")); //End of new MonsterSpawnList
				
				if (msl.movementDistance < 1) {
					msl.movementDistance = Configurations.DEFAULT_MOVEMENT_RANGE;
				}
				
				/*
				 * 確認生怪座標有在地圖範圍內
				 */	
				int[] MapSizeInfo = MapInfo.sizeTable.get (msl.mapId) ;
				if ((msl.locX < MapSizeInfo[0]) ||
					(msl.locX > MapSizeInfo[1]) ||
					(msl.locY < MapSizeInfo[2]) ||
					(msl.locY > MapSizeInfo[3])) {
					
					System.out.printf ("MSL ERR, CHECK->id:%d name:%s location(%d:%d,%d)\n",
						msl.listId,
						CacheData.npcs.get (msl.npcTemplateId).name,
						msl.mapId, msl.locX, msl.locY);
					continue;
				} 
				
				spawnList.put (msl.listId, msl);
				
				int monsterId = rs.getInt ("npc_templateid") ;
				ResultSet rsDrop = DatabaseCmds.mobDropList (monsterId);
				try {
					rsDrop.last ();
					int dropListSize = rsDrop.getRow ();
					
					if (dropListSize > 0 && !dropList.containsKey (monsterId)) {
						rsDrop.first ();
						List<MonsterDropList> dropTable= new ArrayList<MonsterDropList> ();
						
						while (rsDrop.next ()) {
							MonsterDropList mdl = new MonsterDropList (
								rsDrop.getInt ("mobId"),
								rsDrop.getInt ("itemId"),
								rsDrop.getInt ("min"),
								rsDrop.getInt ("max"),
								rsDrop.getInt ("chance"));
							dropTable.add (mdl);
						}
						dropList.putIfAbsent (monsterId, dropTable);
						
					}
				} catch (Exception e) {
					e.printStackTrace ();
				} finally {
					DatabaseUtil.close (rsDrop);
				}
			} //End of rs.next()
			
		} catch (Exception e) {
			e.printStackTrace ();
		} finally {
			DatabaseUtil.close (rs);
		}
	}
	
	/* 快取資料庫的spawnlist用 */
	class MonsterSpawnList {
		public int listId;
		public String location;
		public int count;
		public int npcTemplateId;
		public int groupId;
		public int locX, locY;
		public int randomX;
		public int randomY;
		public int mapId;
		public int locX1, locY1;
		public int locX2, locY2;
		public int heading;
		public int minRespawnDelay;
		public int maxRespawnDelay;
		public int respawnScreen;
		public int movementDistance;
		public int rest;
		public int nearSpawn;
		
		public List<MonsterInstance> monsters;
		
		public MonsterSpawnList (
			int _listId,
			String _location,
			int _count,
			int _npcTemplateId,
			int _groupId,
			int _locX,
			int _locY,
			int _randomX,
			int _randomY,
			int _mapId,
			int _locX1, int _locY1,
			int _locX2, int _locY2,
			int _heading,
			int _minRespawnDelay,
			int _maxRespawnDelay,
			int _respawnScreen,
			int _movementDistance,
			int _rest,
			int _nearRespawn
		) {
			listId = _listId;
			location = _location;
			count = _count;
			npcTemplateId = _npcTemplateId;
			groupId = _groupId;
			locX = _locX; locY = _locY;
			randomX = _randomX;
			randomY = _randomY;
			mapId = _mapId;
			locX1 = _locX1; locY1 = _locY1;
			locX2 = _locX2; locY2 = _locY2;
			heading = _heading;
			minRespawnDelay = _minRespawnDelay;
			if (minRespawnDelay < 60) {
				minRespawnDelay = 60;
			}
			maxRespawnDelay = _maxRespawnDelay;
			respawnScreen = _respawnScreen;
			movementDistance = _movementDistance;
			rest = _rest;
			nearSpawn = _nearRespawn;
			
			monsters = new ArrayList<MonsterInstance> ();
		}
	}
	
	class MonsterDropList {
		public int monsterId;
		public int itemId;
		public int min, max;
		public int posibility;
		
		public MonsterDropList (
			int _monsterId,
			int _itemId,
			int _min,
			int _max,
			int _posibility
		) {
			monsterId = _monsterId;
			itemId = _itemId;
			min = _min;
			max = _max;
			posibility = _posibility;
		}
	}
}
