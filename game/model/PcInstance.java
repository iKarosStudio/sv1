package vidar.game.model;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import vidar.types.*;
import vidar.server.*;
import vidar.server.database.*;
import vidar.server.utility.*;
import vidar.server.process_server.*;
import vidar.game.*;
import vidar.game.map.*;
import vidar.game.model.item.*;

public class PcInstance extends Model
{
	public SessionHandler handle;
	
	public boolean isExit;
	
	public int sex;
	public int type;
	
	public int exp;
	public int expRes;
	
	public int clanId;
	public String clanName;
	
	public int satiation;
	public int pkCount;
	
	public ConcurrentHashMap<Integer, ItemInstance> itemBag = null;
	public Equipment equipment;
	public AbilityParameter equipParameter;
	
	public PcInstance () {
		//
	}
	
	public PcInstance (SessionHandler handle) {
		this.handle = handle;
		itemBag = new ConcurrentHashMap<Integer, ItemInstance> ();
		location = new Location ();
		equipment = new Equipment (handle);
	}
	
	public boolean load (String charName) {
		boolean result = false;
		
		Connection con = HikariCP.getConnection () ;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement ("SELECT * FROM characters WHERE char_name=?") ;
			ps.setString (1, charName) ;
			
			rs = ps.executeQuery () ;
			if (rs.next ()) {
				location.mapId = rs.getInt ("MapID") ;
				location.point.x = rs.getInt ("LocX") ;
				location.point.y = rs.getInt ("LocY") ;
				heading = rs.getInt ("Heading") ;
				
				if (location.mapId > MapLoader.MAPID_LIMIT) {
					location.mapId = 0;
				}
				
				uuid = rs.getInt ("objid") ;
				name = rs.getString ("char_name") ;
				title = rs.getString ("Title") ;
				clanId = rs.getInt ("ClanID") ;
				if (clanId > 0) {
					clanName = rs.getString ("Clanname") ;
				}
				
				level = rs.getInt ("level") ;
				exp = rs.getInt ("Exp") ;
				expRes = rs.getInt ("ExpRes") ;
				
				sex = rs.getInt ("Sex") ;
				type = rs.getInt ("Type") ;
				gfx = rs.getInt ("Class") ;
				gfxTemp = gfx;
				
				basicParameters = new AbilityParameter ();
				skillParameters = new AbilityParameter ();
				equipParameter  = new AbilityParameter ();
				
				basicParameters.str = rs.getInt ("Str") ;
				basicParameters.con = rs.getInt ("Con") ;
				basicParameters.dex = rs.getInt ("Dex") ;
				basicParameters.wis = rs.getInt ("Wis") ;
				basicParameters.cha = rs.getInt ("Cha") ;
				basicParameters.intel = rs.getInt ("Intel") ;
				
				//load bounes parameters
				basicParameters.ac = Utility.calcAcBonusFromDex (level, basicParameters.dex) ;
				basicParameters.mr = Utility.calcMr (type, level, basicParameters.wis) ;
				basicParameters.sp = Utility.calcSp (type, level, basicParameters.intel) ;
				
				
				satiation = rs.getInt ("Food") ;
				//BasicParameter.Ac = rs.getInt ("Ac") ;
				status = rs.getInt ("Status") ;
				status |= 4;
				
				hp = rs.getInt ("CurHp") ;
				mp = rs.getInt ("CurMp") ;
				basicParameters.maxHp = rs.getInt ("MaxHp") ;
				basicParameters.maxMp = rs.getInt ("MaxMp") ;
				
				pkCount = rs.getInt ("PKcount") ;
				
				//Tick = new SystemTick (this) ;
				//SkillTimer = new SkillEffectTimer (this) ;
				//RoutineTask = new PcRoutineTasks (this) ;
				//ExpKeeper = new ExpMonitor (this) ;		
				///HpKeeper = new HpMonitor (this) ;
				//MpKeeper = new MpMonitor (this) ;
				
				this.updateCurrentMap ();
				
				result = true;
			}		
		} catch (Exception e) {
			e.printStackTrace () ;
		} finally {
			DatabaseUtil.close (rs) ;
			DatabaseUtil.close (ps) ;
			DatabaseUtil.close (con) ;
		}
		
		return result;
	}
	
	public void loadItemBag () {
		itemBag = new ConcurrentHashMap<Integer, ItemInstance> ();
		ResultSet rs = null;
		try {
			rs = DatabaseCmds.loadPcItems (uuid);
			while (rs.next ()) {
				int itemId = rs.getInt ("item_id");
				int itemUuid = rs.getInt ("id") ;
				int itemOwnerUuid = rs.getInt ("char_id");
				int itemCount = rs.getInt ("count");
				int itemEnchant = rs.getInt ("enchantlvl");
				int itemDurability = rs.getInt ("durability");
				int itemChargeCount = rs.getInt ("charge_count");
				boolean itemIsUsing = rs.getBoolean ("is_equipped");
				boolean itemIsIdentified = rs.getBoolean ("is_id");
				
				//生成ItemInstace, WeaponInstance, ArmorInstance
				if (CacheData.item.containsKey (itemId)) {
					ItemInstance item = new ItemInstance (itemId, itemUuid, itemOwnerUuid, itemCount, itemDurability, itemChargeCount, itemIsUsing, itemIsIdentified);
					itemBag.put (item.uuid, item);
				} else if (CacheData.weapon.containsKey (itemId)) {
					WeaponInstance weapon = new WeaponInstance (itemId, itemUuid, itemOwnerUuid, itemDurability, itemEnchant, itemIsUsing, itemIsIdentified);
					itemBag.put (weapon.uuid, weapon);
				} else if (CacheData.armor.containsKey (itemId)) {
					ArmorInstance armor = new ArmorInstance (itemId, itemUuid, itemOwnerUuid, itemDurability, itemEnchant, itemIsUsing, itemIsIdentified);
					itemBag.put (armor.uuid, armor);
				}
			}
			
			handle.sendPacket (new ReportItemBag (itemBag).getRaw()); //report held item
			
			//itemBag.forEach ((Integer uuid, ItemInstance item)->{
				//套用裝備效果
			//});
		} catch (Exception e) {
			e.printStackTrace ();
		} finally {
			DatabaseUtil.close (rs);
		}
	}
	
	public void loadSkills () {
	}
	
	
	public ItemInstance findItemByUuid (int uuid) {
		return itemBag.get (uuid);
	}
	
	public List<ItemInstance> findItemByItemId (int itemId) {
		List<ItemInstance> result = new ArrayList<ItemInstance> ();
		
		itemBag.forEach ((Integer uuid, ItemInstance item)->{
			if (item.id == itemId) {
				result.add (item);
			}
		});
		
		return result;
	}
	
	public void setWeapon (int uuid) {
		WeaponInstance weapon = (WeaponInstance) findItemByUuid (uuid);
		if (weapon != null) {
			equipment.setWeapon (weapon);
		}
	}
	
	public void setArmor (int uuid) {
		ArmorInstance armor = (ArmorInstance) findItemByUuid (uuid);
		if (armor != null) {
			equipment.setArmor (armor);
		}
	}
	
	public boolean isRoyal () {
		return (type == 0);
	}
	
	public boolean isKnight () {
		return (type == 1);
	}
	
	public boolean isElf () {
		return (type == 2);
	}
	
	public boolean isWizard () {
		return (type == 3);
	}
	
	public boolean isDarkelf () {
		return (type == 4);
	}
	
	public int getWeaponGfx () {
		if (equipment.weapon == null) {
			return 0;
		} else {
			return equipment.weapon.gfx;
		}
	}
	
	public int getStr () {
		return basicParameters.str + skillParameters.str + equipParameter.str;
	}
	
	public int getCon () {
		return basicParameters.con + skillParameters.con + equipParameter.con;
	}
	
	public int getDex () {
		return basicParameters.dex + skillParameters.dex + equipParameter.dex;
	}
	
	public int getWis () {
		return basicParameters.wis + skillParameters.wis + equipParameter.wis;
	}
	
	public int getCha () {
		return basicParameters.cha + skillParameters.cha + equipParameter.cha;
	}
	
	public int getIntel () {
		return basicParameters.intel + skillParameters.intel + equipParameter.intel;
	}
	
	public int getSp () {
		return basicParameters.sp + skillParameters.sp + equipParameter.sp;
	}
	
	public int getMr () {
		return basicParameters.mr + skillParameters.mr + equipParameter.mr;
	}
	
	public int getMaxHp () {
		return basicParameters.maxHp + skillParameters.maxHp + equipParameter.maxHp;
	}

	public int getBaseMaxHp () {
		return basicParameters.maxHp;
	}
	
	public int getMaxMp () {
		return basicParameters.maxMp + skillParameters.maxMp + equipParameter.maxMp;
	}
	
	public int getBaseMaxMp () {
		return basicParameters.maxMp;
	}
	
	public int getAc () {
		return basicParameters.ac + skillParameters.ac + equipParameter.ac;
	}
	
	public int getBaseAc () {
		return basicParameters.ac;
	}
	
	public int getEquipAc () {
		return equipParameter.ac;
	}
	
	public int getWeight () {
		int totalWeight = 0;
		ArrayList<ItemInstance> allItemList = new ArrayList<ItemInstance> () ;
		itemBag.forEach ((Integer uuid, ItemInstance item)->{
			allItemList.add (item) ;
		});
		
		for (ItemInstance i : allItemList) {
			totalWeight += i.weight * i.count;
		}
		
		return totalWeight;
	}
	
	public int getMaxWeight () {
		int max_wieght = 1500 + (((getStr () + getCon () - 18) >> 1) * 150) ;
		//apply skill effect
		//apply equip effect
		//apply doll effect
		
		return max_wieght * 1000;
	}
	
	public int getWeightInScale30 () {
		return (getWeight() * 100) / (int) (getMaxWeight() * 3.4);
	}
	
	public void setHandler (SessionHandler handle) {
		this.handle = handle;
	}
		
	public SessionHandler getHandler () {
		return handle;
	}
	
	public void offline () {
		Vidar.getInstance (); //remove pc
		
		DatabaseCmds.savePc (this);		
	}
	
}
