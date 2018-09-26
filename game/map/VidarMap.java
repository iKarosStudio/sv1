package vidar.game.map;

import java.util.*;
import java.util.concurrent.*;

import vidar.config.*;
import vidar.types.*;
import vidar.game.*;
import vidar.game.model.*;
import vidar.game.model.item.*;
import vidar.game.model.npc.*;
import vidar.game.model.monster.*;
import vidar.game.model.monster.ai.*;

/*
 * 讀取的地圖實例
 * 參照 MapTileBitStruct 
 */
public class VidarMap
{
	private byte[][] tile;
	public  final int mapId;
	private final int startX;
	private final int endX;
	private final int startY;
	private final int endY;
	
	public final int sizeX;
	public final int sizeY;
	
	private Random random = new Random (System.currentTimeMillis () ) ;
	
	/* 生怪控制器, 數量邏輯控制 */
	public MonsterGenerator monsterGenerator = null;
	
	/* 傳送點列表 */
	public ConcurrentHashMap<Integer, Location> tpLocation;
	
	/* 線上使用者實體 */
	public ConcurrentHashMap<Integer, PcInstance> pcs;
	
	/* NPC實體 */
	public ConcurrentHashMap<Integer, NpcInstance> npcs;
	
	/* 地面道具實體 */
	public ConcurrentHashMap<Integer, ItemInstance> items; //Items on ground
	
	/* 門的實體 */
	public ConcurrentHashMap<Integer, DoorInstance> doors; //Doors
	
	/* 怪物實體 */
	public ConcurrentHashMap<Integer, MonsterInstance> monsters;
	public MonsterAiDistributor aiDistributor;
	
	/* 寵物, 召喚怪物實體  */
	//public ConcurrentHashMap<Integer, PetInstance> pets;
	
	public VidarMap (int _mapId, int _startX, int _endX, int _startY, int _endY) {
		mapId = _mapId;
		startX = _startX;
		endX = _endX;
		startY = _startY;
		endY = _endY;
		
		sizeX = endX - startX + 1;
		sizeY = endY - startY + 1;
		tile = new byte[sizeX][sizeY];
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				tile[x][y] = 0;
			}
		}
		
		tpLocation = new ConcurrentHashMap<Integer, Location> () ;
		
		pcs = new ConcurrentHashMap<Integer, PcInstance> ();
		npcs = new ConcurrentHashMap<Integer, NpcInstance> ();
		items = new ConcurrentHashMap<Integer, ItemInstance> ();
		doors = new ConcurrentHashMap<Integer, DoorInstance> ();
		monsters = new ConcurrentHashMap<Integer, MonsterInstance> ();
		
		aiDistributor = new MonsterAiDistributor (this);
		aiDistributor.start ();
	}
	
	public int spawnMonster () {
		return 0;
	}
	
	public void setAccessible (int x, int y, boolean passable) {
		if (passable) {
			tile[x-startX][y-startY] &= 0x7F;
		} else {
			tile[x-startX][y-startY] |= 0x80;
		}
	}
	
	public boolean isAccessible (int x, int y) {
		byte t = getTile (x, y) ;
		return (t & 0x80) > 0;
	}
	
	/*
	 * 檢查p(x, y)->heading p'(x', y')是否可通過
	 */
	public boolean isNextTileAccessible (int x, int y, int heading) {		
		byte nextTile = getHeadingTile (x, y, heading) ;
		
		/*
		 * 檢查動態物件佔有
		 */
		if ((nextTile & 0x80) > 0) {
			return false;
		}
		
		if ((nextTile & 0x03) > 0) {	
			/*
			 * 檢查方向單位是否可通行
			 */
			if (heading == 0) {
				return isYAxisAccessible (nextTile) ;
			} else if (heading == 2) { //
				return isXAxisAccessible (nextTile) ;
			} else if (heading == 4) {
				return isYAxisAccessible (nextTile) ;
			} else if (heading == 6) {
				return isXAxisAccessible (nextTile) ;
				
			} else if (heading == 1) {
				byte[] side = getHeadingSideTile (x, y, heading) ;
				return isYAxisAccessible (side[0]) || isXAxisAccessible (side[1]) ;
				
			} else if (heading == 3) { //
				byte[] side = getHeadingSideTile (x, y, heading) ;
				return isYAxisAccessible (side[0]) || isXAxisAccessible (side[1]) ;
	
			} else if (heading == 5) {
				byte[] side = getHeadingSideTile (x, y, heading) ;
				return isYAxisAccessible (side[0]) || isXAxisAccessible (side[1]) ;
	
			} else if (heading == 7) {
				byte[] side = getHeadingSideTile (x, y, heading) ;
				return isYAxisAccessible (side[0]) || isXAxisAccessible (side[1]) ;
	
			} else {
				//return false;
			}
		}
		return false;
	}
	
	public boolean isNormalZone (int x, int y) {
		return ((tile[x - startX][y - startY] & 0x30) == 0x00) ;
	}
	
	public boolean isSafeZone (int x, int y) {
		return ((tile[x - startX][y - startY] & 0x30) == 0x10) ;
	}
	 
	public boolean isCombatZone (int x, int y) {
		return ((tile[x - startX][y - startY] & 0x30) == 0x20) ;
	}
	
	public void setTile (int x, int y, byte _tile) {
		tile[x][y] = _tile;
	}
	
	public byte getTile (int x, int y) {
		byte tmpTile = 0;
		try {
			tmpTile = tile[x - startX][y - startY];
		} catch (Exception e) {
			System.out.printf ("(%d, %d) : %s\n", x, y, e.toString () ) ;
			tmpTile = 0;
		}
		
		return tmpTile;
	}
	
	public byte getHeadingTile (int x, int y, int heading) {
		if (heading == 0) {
			if (y == startY) {
				return 0;
			}
			return getTile (x, y-1) ;
		} else if (heading == 1) {
			if ((y == startY) || (x == endX)) {
				return 0;
			}
			return getTile (x+1, y-1) ;
		} else if (heading == 2) {
			if (x == endX) {
				return 0;
			}
			return getTile (x+1, y) ;
		} else if (heading == 3) {
			if ((x == endX) || (y == endX)) {
				return 0;
			}
			return getTile (x+1, y+1) ;
		} else if (heading == 4) {
			if (y == endX) {
				return 0;
			}
			return getTile (x, y+1) ;
		} else if (heading == 5) {
			if ((y == endY) || (x == startX)) {
				return 0;
			}
			return getTile (x-1, y+1) ;
		} else if (heading == 6) {
			if (x == startX) {
				return 0;
			}
			return getTile (x-1, y) ;
		} else if (heading == 7) {
			if ((x == startX) || (y == startY)) {
				return 0;
			}
			return getTile (x-1, y-1) ;
		} else {
			return 0;
		}
	}
	
	public byte[] getHeadingSideTile (int x, int y, int heading) {
		byte[] res = new byte[2];
		
		if (heading == 0) {
			res[0] = getHeadingTile (x, y, 7) ;
			res[1] = getHeadingTile (x, y, 1) ;
		} else if (heading == 1) {
			res[0] = getHeadingTile (x, y, 0) ;
			res[1] = getHeadingTile (x, y, 2) ;
		} else if (heading == 2) {
			res[0] = getHeadingTile (x, y, 1) ;
			res[1] = getHeadingTile (x, y, 3) ;
		} else if (heading == 3) {
			res[0] = getHeadingTile (x, y, 2) ;
			res[1] = getHeadingTile (x, y, 4) ;
		} else if (heading == 4) {
			res[0] = getHeadingTile (x, y, 3) ;
			res[1] = getHeadingTile (x, y, 5) ;
		} else if (heading == 5) {
			res[0] = getHeadingTile (x, y, 4) ;
			res[1] = getHeadingTile (x, y, 6) ;
		} else if (heading == 6) {
			res[0] = getHeadingTile (x, y, 5) ;
			res[1] = getHeadingTile (x, y, 7) ;
		} else if (heading == 7) {
			res[0] = getHeadingTile (x, y, 6) ;
			res[1] = getHeadingTile (x, y, 0) ;
		} else {
			res[0] = 0;
			res[1] = 0;
		}
		
		return res;
	}
	
	public boolean isXAxisAccessible (byte t) {
		return (t & 0x01) > 0;
	}
	
	public boolean isYAxisAccessible (byte t) {
		return (t & 0x02) > 0;
	}
	
	public Location getRandomLocation () {
		Location dest = new Location (mapId, 0, 0);
		
		do {
			dest.point.x = startX + random.nextInt (sizeX) ;
			dest.point.y = startY + random.nextInt (sizeY) ;
		} while (getTile (dest.point.x, dest.point.y) == 0) ;
		
		return dest;
	}
	
	/*
	 * 未來考慮取得視線內MonsterInstance物件改由MonsterSpawnList內登記的Mobs取得所有怪物物件的參考
	 */
	
	public boolean isInTpLocation (int x, int y) {
		int pos = (x << 16) | y;
		
		return tpLocation.containsKey (pos)	;	
	}
	
	public Location getTpDestination (int src_x, int src_y) {
		int src = (src_x << 16) | src_y;
		return tpLocation.get (src) ;
	}
	
	public void addTpLocation (int src_x, int src_y, Location dest) {
		int src = (src_x << 16) | src_y;
		tpLocation.put (src, dest);
	}
	
	public List<PcInstance> getPcsInDistance (Coordinate _point, int _range) {
		List<PcInstance> result = new ArrayList<PcInstance> ();
		try {
			pcs.forEach ((Integer uuid, PcInstance pc)->{
				if (pc.getDistance (_point.x, _point.y) < _range) {
					result.add (pc);
				}
			});
		} catch (Exception e) {
			e.printStackTrace ();
		}
		return result;
	}
	
	public List<PcInstance> getPcsInsight (Coordinate _point) {
		return getPcsInDistance (_point, Configurations.SIGHT_RAGNE);
	}
	
	public void addPc (PcInstance pc) {
		pcs.putIfAbsent (pc.uuid, pc);
	}
	
	public void removePc (PcInstance pc) {
		pcs.remove (pc.uuid);
	}

	
	public List<NpcInstance> getNpcsInDistance (Coordinate _point, int _range) {
		List<NpcInstance> result = new ArrayList<NpcInstance> ();
		try {
			npcs.forEach ((Integer uuid, NpcInstance npc)->{
				if (npc.getDistance (_point.x, _point.y) < _range) {
					result.add (npc);
				}
			});
		} catch (Exception e) {
			e.printStackTrace ();
		}
		return result;
	}
	
	public List<NpcInstance> getNpcsInsight (Coordinate _point) {
		return getNpcsInDistance (_point, Configurations.SIGHT_RAGNE) ;
	}
	
	public void addNpc (NpcInstance npc) {
		npcs.putIfAbsent (npc.uuid, npc);
	}
	
	public void removeNpc (NpcInstance npc) {
		npcs.remove (npc.uuid);
	}
	
	public List<DoorInstance> getDoorsInDistance (Coordinate _point, int _range) {
		List<DoorInstance> result = new ArrayList<DoorInstance> ();
		try {
			doors.forEach ((Integer uuid, DoorInstance door)->{
				if (door.getDistance (_point.x, _point.y) < _range) {
					result.add (door);
				}
			});
		} catch (Exception e) {
			e.printStackTrace ();
		}
		return result;
	}
	
	public List<DoorInstance> getDoorsInsight (Coordinate _point) {
		return getDoorsInDistance (_point, Configurations.SIGHT_RAGNE);
	}
	
	
	public List<MonsterInstance> getMonstersInDistance (Coordinate _point, int _range) {
		List<MonsterInstance> result = new ArrayList<MonsterInstance> ();
		try {
			monsters.forEach ((Integer uuid, MonsterInstance monster)->{
				if (monster.getDistance (_point.x, _point.y) < _range) {
					result.add (monster);
				}
			});
		} catch (Exception e) {
			e.printStackTrace ();
		}
		return result;
	}
	
	public List<MonsterInstance> getMonstersInsight (Coordinate _point) {
		return getMonstersInDistance (_point, Configurations.SIGHT_RAGNE);
	}
	
	
	public Model getModel (int uuid) {
		Model result;
		
		if (pcs.containsKey (uuid)) {
			result = pcs.get (uuid);
		} else if (monsters.containsKey (uuid)) {
			result = monsters.get (uuid);
		} else if (npcs.containsKey (uuid)) {
			result = npcs.get (uuid);
		} else {
			result = null;
		}	
		
		return result;
	}
}
