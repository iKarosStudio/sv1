package vidar.game;

import java.util.concurrent.*;
import java.sql.*;
import org.w3c.dom.*;

import vidar.server.database.*;
import vidar.game.template.*;

public class CacheData
{
	private static CacheData instance;
	
	//public static ConcurrentHashMap<Integer, NpcTemplate> npcCache;
	//public static ConcurrentHashMap<String, Document> npcActionCache;
	//public static ConcurrentHashMap<Integer, NpcTalkData> npcTalkDataCache;
	//public static ConcurrentHashMap MonsterCache;
	//public static ConcurrentHashMap MerchantCache;
	//public static ConcurrentHashMap HousekeeperCache;
	public static ConcurrentHashMap<Integer, ItemTemplate> item;
	public static ConcurrentHashMap<Integer, WeaponTemplate> weapon;
	public static ConcurrentHashMap<Integer, ArmorTemplate> armor;
	
	//public static ConcurrentHashMap<Integer, SkillTemplate> skillCache;
	
	//public static ConcurrentHashMap<Integer, NpcShop> shopCache;
	
	public static CacheData getInstance () {
		if (instance == null) {
			instance = new CacheData () ;
		}
		return instance;
	}
	
	public CacheData () {
		System.out.print ("Load Game data...\n") ;
		
		System.out.print ("\t-> Load npc cache data...") ;
		//loadNpcCache () ;
		
		System.out.print ("\t-> Load npc action cache data...") ;
		//loadNpcActionCache () ;
		
		System.out.print ("\t-> Load npc talk cache data...") ;
		//loadNpcTalkDataCache () ;
		
		System.out.print ("\t-> Load items cache data...") ;
		loadItemCache () ;
		
		System.out.print ("\t-> Load skill cache data...") ;
		//loadSkillCache () ;
		
		System.out.print ("\t-> Load shop cache data...") ;
		//loadShopCache () ;
		
		System.out.print ("\t-> Load weapons cache data...") ;
		loadWeaponCache () ;
		
		System.out.print ("\t-> Load armor cache data...") ;
		loadArmorCache () ;
		
		System.out.println () ;
		//cache pets types
		//cache monster types
		//cache npc types
	}
	
	public void loadItemCache () {
		item = new ConcurrentHashMap<Integer, ItemTemplate> ();
		
		Connection con = HikariCP.getConnection ();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement ("SELECT * FROM etcitem;");
			rs = ps.executeQuery () ;
			
			int Counter = 0;
			long timeStarts = System.currentTimeMillis ();
			while (rs.next ()) {
				ItemTemplate i = new ItemTemplate (
					rs.getInt ("item_id"),
					rs.getString ("name"),
					rs.getString ("name_id"),
					rs.getString ("item_type"),
					rs.getString ("use_type"),
					rs.getString ("material"),
					rs.getInt ("weight"),
					rs.getInt ("invgfx"),
					rs.getInt ("grdgfx"),
					rs.getInt ("itemdesc_id"),
					rs.getBoolean ("stackable"),
					rs.getInt ("max_charge_count"),
					rs.getInt ("dmg_small"),
					rs.getInt ("dmg_large"),
					rs.getInt ("min_lvl"),
					rs.getInt ("max_lvl"),
					rs.getInt ("bless"),
					rs.getBoolean ("trade"),
					rs.getInt ("delay_id"),
					rs.getInt ("delay_time"),
					rs.getInt ("food_volume"),
					rs.getBoolean ("save_at_once"));
				
				item.put (rs.getInt ("item_id"), i) ;
				
				Counter ++;
			}
			long timeEnds = System.currentTimeMillis () ;
			float usedTime = (float) (timeEnds - timeStarts) / 1000;
			System.out.printf ("\t\t" + Counter + " items cached in\t%.3f s\n", usedTime) ;
		} catch (Exception e) {
			e.printStackTrace () ;
		} finally {
			DatabaseUtil.close (rs) ;
			DatabaseUtil.close (ps) ;
			DatabaseUtil.close (con) ;
		}
	}
	
	public void loadWeaponCache () {
		weapon = new ConcurrentHashMap<Integer, WeaponTemplate> ();
		
		Connection con = HikariCP.getConnection ();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement ("SELECT * FROM weapon;");
			rs = ps.executeQuery () ;
			
			int Counter = 0;
			long timeStarts = System.currentTimeMillis () ;
			while (rs.next () ) {
				int itemId = rs.getInt ("item_id") ;
				WeaponTemplate w = new WeaponTemplate (
					itemId,
					rs.getString ("name"),
					rs.getString ("name_id"),
					rs.getString ("type"),
					rs.getString ("material"),
					rs.getInt ("weight"),
					rs.getInt ("invgfx"),
					rs.getInt ("grdgfx"),
					rs.getInt ("itemdesc_id"),
					rs.getInt ("dmg_small"),
					rs.getInt ("dmg_large"),
					rs.getInt ("safenchant"),
					rs.getBoolean ("use_royal"),
					rs.getBoolean ("use_knight"),
					rs.getBoolean ("use_mage"),
					rs.getBoolean ("use_elf"),
					rs.getBoolean ("use_darkelf"),
					rs.getInt ("hitmodifier"),
					rs.getInt ("dmgmodifier"),
					rs.getInt ("add_str"),
					rs.getInt ("add_con"),
					rs.getInt ("add_dex"),
					rs.getInt ("add_int"),
					rs.getInt ("add_wis"),
					rs.getInt ("add_cha"),
					rs.getInt ("add_hp"),
					rs.getInt ("add_mp"),
					rs.getInt ("add_hpr"),
					rs.getInt ("add_mpr"),
					rs.getInt ("add_sp"),
					rs.getInt ("m_def"), //mr
					rs.getBoolean ("haste_item"),
					rs.getInt ("magicdmgmodifier"),
					rs.getBoolean ("canbedmg"),
					rs.getInt ("min_lvl"),
					rs.getInt ("max_lvl"),
					rs.getInt ("bless"),
					rs.getBoolean ("trade"),
					rs.getBoolean ("mana_item") ) ;
				
				weapon.putIfAbsent (itemId, w) ;
				Counter ++;
			}
			long timeEnds = System.currentTimeMillis ();
			float usedTime = (float) (timeEnds - timeStarts) / 1000;
			System.out.printf ("\t\t" + Counter + " weapons cached in\t%.3f s\n", usedTime) ;
		} catch (Exception e) {
			e.printStackTrace () ;
			
		} finally {
			DatabaseUtil.close (rs) ;
			DatabaseUtil.close (ps) ;
			DatabaseUtil.close (con) ;
			
		}
	}
	
	public void loadArmorCache () {
		armor = new ConcurrentHashMap<Integer, ArmorTemplate> () ;
		
		Connection con = HikariCP.getConnection () ;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement ("SELECT * FROM armor;") ;
			rs = ps.executeQuery () ;
			
			int Counter = 0;
			long timeStarts = System.currentTimeMillis () ;
			while (rs.next () ) {
				int itemId = rs.getInt ("item_id") ;
				
				ArmorTemplate a = new ArmorTemplate (
					itemId,
					rs.getString ("name"),
					rs.getString ("name_id"),
					rs.getString ("type"),
					rs.getString ("material"),
					rs.getInt ("weight"),
					rs.getInt ("invgfx"),
					rs.getInt ("grdgfx"),
					rs.getInt ("itemdesc_id"),
					rs.getInt ("ac"),
					rs.getInt ("safenchant"),
					rs.getBoolean ("use_royal"),
					rs.getBoolean ("use_knight"),
					rs.getBoolean ("use_mage"),
					rs.getBoolean ("use_elf"),
					rs.getBoolean ("use_darkelf"),
					rs.getInt ("add_str"),
					rs.getInt ("add_con"),
					rs.getInt ("add_dex"),
					rs.getInt ("add_int"),
					rs.getInt ("add_wis"),
					rs.getInt ("add_cha"),
					rs.getInt ("add_hp"),
					rs.getInt ("add_mp"),
					rs.getInt ("add_hpr"),
					rs.getInt ("add_mpr"),
					rs.getInt ("add_sp"),
					rs.getInt ("min_lvl"),
					rs.getInt ("max_lvl"),
					rs.getInt ("m_def"),
					rs.getBoolean ("haste_item"),
					rs.getInt ("damage_reduction"),
					rs.getInt ("weight_reduction"),
					rs.getInt ("bow_hit_rate"),
					rs.getInt ("bless"),
					rs.getBoolean ("trade"),
					rs.getInt ("defense_water"),
					rs.getInt ("defense_wind"),
					rs.getInt ("defense_fire"),
					rs.getInt ("defense_earth"),
					rs.getInt ("regist_stan"),
					rs.getInt ("regist_stone"),
					rs.getInt ("regist_sleep"),
					rs.getInt ("regist_freeze")	) ;
				armor.putIfAbsent (itemId, a) ;
				Counter ++;
			}
			long timeEnds = System.currentTimeMillis () ;
			float usedTime = (float) (timeEnds - timeStarts) / 1000;
			System.out.printf ("\t\t" + Counter + " armors cached in \t%.3f s\n", usedTime) ;
		} catch (Exception e) {
			e.printStackTrace ();
			
		} finally {
			DatabaseUtil.close (rs);
			DatabaseUtil.close (ps);
			DatabaseUtil.close (con);
			
		}
	}
}
