package vidar.game;

import java.lang.Thread;
import java.util.*;
import java.util.concurrent.*;

import vidar.config.*;
import vidar.types.*;
import vidar.server.*;
import vidar.server.threadpool.*;
import vidar.server.process_server.*;
import vidar.server.utility.*;
import vidar.game.map.*;
import vidar.game.model.*;

public class Vidar extends Thread
{
	private static Vidar instance;
	
	/* 管理的所有地圖 */
	private static ConcurrentHashMap<Integer, VidarMap> maps;
	
	public MemoryMonitor memMonitor = null;
	public BoardcastMessage sysMessage = null;
	
	/* 全局等級狀態 */
	private static int onlinePlayers = 0;
	public static ServerTime time = null;
	
	public int threadCount = 0;
	public float cpuUsage = 0;
	public float memUsage = 0;
	
	/* 世界天氣參數
	 * Bit[8] :
	 * 0:下雪
	 * 1:下雨 
	 * 
	 * Bit[1-0] : 程度控制
	 * 0:不下
	 * 1:小
	 * 2:中
	 * 3:大
	 *  */
	public int weather = 0x10 ;
	
	public void run () {
		/* 暫時沒有被使用, 預定做世界級監控調整 */
	}
	
	public static Vidar getInstance () {
		if (instance == null) {
			instance = new Vidar () ;
		}
		return instance;
	}
	
	public Vidar () {
		System.out.println ("Creating VIDAR game instance in " + this) ;
		maps = new ConcurrentHashMap<Integer, VidarMap> () ;
	}
	
	public void initialize () {
		try {
			/* 快取遊戲資料 */
			CacheData.getInstance () ;
			
			/* 取得DB中最後一個UUID */
			UuidGenerator.getInstance () ;
			
			/* Load maps */
			//MonsterAiQueue.getInstance () ;
			//MonsterAiExecutor.getInstance () ;
			MapInfo.getInstance ();
			new MapLoader (instance);
			
			/* Load npc */
			//new NpcLoader (instance) ;
			System.out.println ();
			
			/* Load Door */
			//DoorGenerator.getInstance () ;
			
			/* Generate monster */
			/*
			System.out.printf ("Monster generator initialize interval:%.1f Sec...", (float)Configurations.MONSTER_GENERATOR_UPDATE_RATE/1000) ;
			maps.forEach ((Integer map_id, VidarMap map)->{
				map.mobGenerator = new MonsterGenerator (map) ;
				KernelThreadPool.getInstance ().ScheduleAtFixedRate (map.mobGenerator, 1000, Configurations.MONSTER_GENERATOR_UPDATE_RATE) ;
			}) ;
			System.out.printf ("success\n") ;
			*/
			
			/* Generate Element Stone */
			if (maps.containsKey (4) ) {
				System.out.println ("元素石生產引擎...") ;
			}
			
			/* Start server time */
			time = ServerTime.getInstance () ;
			KernelThreadPool.getInstance ().ScheduleAtFixedRate (time, 0, 1000) ;
			
			//load boss
			
			//load special system
			
			/* Server memory usage monitor */
			memMonitor = MemoryMonitor.getInstance () ;
			KernelThreadPool.getInstance ().ScheduleAtFixedRate (memMonitor, 0, 5000) ;
			
			/* Game boardcast message */
			sysMessage = BoardcastMessage.getInstance () ;
			KernelThreadPool.getInstance ().ScheduleAtFixedRate (sysMessage, 10000, 30000) ;
		} catch (Exception e) {
			e.printStackTrace () ;
			
		}
	}
	
	public void updateWeather () {
		//boardcastToAll (new ReportWeather (weather).getRaw () ) ;
		System.out.printf ("Update Weather:0x%02x\n", weather) ;
	}
	
	public void addMap (VidarMap map) {
		try {
			maps.put (map.mapId, map) ;
		} catch (Exception e) {
			e.printStackTrace () ;
		}
	}
	
	public void addPc (PcInstance pc) {
		VidarMap map = getMap (pc.location.mapId);
		map.addPc (pc);
	}
	
	public void removePc (PcInstance pc) {
	}
	
	public VidarMap getMap (int id) {
		return maps.get (id) ;
	}
	
}
