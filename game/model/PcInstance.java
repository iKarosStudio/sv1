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
import vidar.game.model.npc.*;
import vidar.game.routine_task.*;

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
	
	public int accessLevel = 0;
	public boolean isGm;
	public boolean isRd;
	
	public ConcurrentHashMap<Integer, ItemInstance> itemBag = null;
	public Equipment equipment;
	public AbilityParameter equipParameter;
	
	/* Buff/Debuff 效果計時 */
	//public SkillEffectTimer SkillTimer = null;
	
	/* 藥水延遲  <K, V> = <道具編號, 上一個時間戳記> */
	private ConcurrentHashMap<Integer, Long> itemDelay = null;
	
	/* 技能延遲  <K, V> = <技能編號, 上一個時間戳記> */
	private ConcurrentHashMap<Integer, Long> skillCastDelay = null;
	
	public ConcurrentHashMap<Integer, PcInstance> pcsInsight;
	public ConcurrentHashMap<Integer, NpcInstance> npcsInsight;
	
	public SightUpdate sightUpdate;
	public PcRoutineTasks routineTasks;
	//public SystemTick tick;
	public ExpMonitor expMonitor;
	public HpMonitor hpMonitor;
	public MpMonitor mpMonitor;
	
	public PcInstance () {
		//
	}
	
	public PcInstance (SessionHandler handle) {
		this.handle = handle;
		itemBag = new ConcurrentHashMap<Integer, ItemInstance> ();
		location = new Location ();
		equipment = new Equipment (handle);
		
		itemDelay = new ConcurrentHashMap<Integer, Long> ();
		skillCastDelay = new ConcurrentHashMap<Integer, Long> ();
		
		pcsInsight = new ConcurrentHashMap<Integer, PcInstance> ();
		npcsInsight = new ConcurrentHashMap<Integer, NpcInstance> ();
	}
	
	public boolean load (String charName) {
		boolean result = false;
		
		Connection con = HikariCP.getConnection ();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = con.prepareStatement ("SELECT * FROM characters WHERE char_name=?");
			ps.setString (1, charName);
			
			rs = ps.executeQuery ();
			if (rs.next ()) {
				location.mapId = rs.getInt ("MapID");
				location.point.x = rs.getInt ("LocX");
				location.point.y = rs.getInt ("LocY");
				heading = rs.getInt ("Heading");
				
				if (location.mapId > MapLoader.MAPID_LIMIT) {
					location.mapId = 0;
				}
				
				uuid = rs.getInt ("objid");
				name = rs.getString ("char_name");
				title = rs.getString ("Title");
				clanId = rs.getInt ("ClanID");
				if (clanId > 0) {
					clanName = rs.getString ("Clanname");
				}
				
				level = rs.getInt ("level");
				exp = rs.getInt ("Exp");
				expRes = rs.getInt ("ExpRes");
				
				sex = rs.getInt ("Sex");
				type = rs.getInt ("Type");
				gfx = rs.getInt ("Class");
				gfxTemp = gfx;
				
				basicParameters = new AbilityParameter ();
				skillParameters = new AbilityParameter ();
				equipParameter  = new AbilityParameter ();
				
				basicParameters.str = rs.getInt ("Str");
				basicParameters.con = rs.getInt ("Con");
				basicParameters.dex = rs.getInt ("Dex");
				basicParameters.wis = rs.getInt ("Wis");
				basicParameters.cha = rs.getInt ("Cha");
				basicParameters.intel = rs.getInt ("Intel");
				
				//load bounes parameters
				basicParameters.ac = Utility.calcAcBonusFromDex (level, basicParameters.dex);
				basicParameters.mr = Utility.calcMr (type, level, basicParameters.wis);
				basicParameters.sp = Utility.calcSp (type, level, basicParameters.intel);
				
				
				satiation = rs.getInt ("Food");
				//BasicParameter.Ac = rs.getInt ("Ac") ;
				status = rs.getInt ("Status");
				status |= 4;
				
				hp = rs.getInt ("CurHp");
				mp = rs.getInt ("CurMp");
				basicParameters.maxHp = rs.getInt ("MaxHp");
				basicParameters.maxMp = rs.getInt ("MaxMp");
				
				pkCount = rs.getInt ("PKcount");
				
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
			e.printStackTrace ();
		} finally {
			DatabaseUtil.close (rs);
			DatabaseUtil.close (ps);
			DatabaseUtil.close (con);
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
					setWeapon (weapon.uuid);
					
				} else if (CacheData.armor.containsKey (itemId)) {
					ArmorInstance armor = new ArmorInstance (itemId, itemUuid, itemOwnerUuid, itemDurability, itemEnchant, itemIsUsing, itemIsIdentified);
					
					itemBag.put (armor.uuid, armor);
					setArmor (armor.uuid);
				}
			}
			
			handle.sendPacket (new ReportItemBag (itemBag).getRaw()); //report held item
			applyEquipmentEffects ();
			
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
	
	public void addItem (ItemInstance item) {
		List<ItemInstance> foundItems = findItemByItemId (item.id);
		
		if (item.isStackable && (foundItems.size() > 0)) {
			ItemInstance i = itemBag.get (foundItems.get (0).uuid) ;
			i.count += item.count;
			DatabaseCmds.updateItem (i);
			
			handle.sendPacket (new UpdateItemAmount(i).getRaw ());
			handle.sendPacket (new UpdateItemName(i).getRaw ());
			handle.sendPacket (new UpdateItemStatus(i).getRaw ());
		} else {
			itemBag.put (item.uuid, item);
			DatabaseCmds.insertItem (item);
			
			handle.sendPacket (new ItemInsert (item).getRaw ());
		}
	}
	
	public void removeItem (ItemInstance i) {
	}
	
	public void deleteItem (ItemInstance i) {
	}
	
	public void dropItem (ItemInstance i) {
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
			equipment.setEquipment (armor);
			applyEquipmentEffects ();
		}
	}
	
	public void applyEquipmentEffects () {
		AbilityParameter temp = new AbilityParameter ();
		List<ArmorInstance> armorList = equipment.getArmorList ();
		armorList.forEach ((ArmorInstance armor)->{
			if (armor != null) {
				temp.ac += armor.ac;
				temp.str += armor.str; temp.dex += armor.dex; temp.con += armor.con;
				temp.wis += armor.wis; temp.cha += armor.cha; temp.intel += armor.intel;
				temp.maxHp += armor.hp; temp.maxMp += armor.mp;
				temp.hpR += armor.hpr; temp.mpR += armor.mpr;
				temp.sp += armor.sp; temp.mr += armor.mr;
				temp.defWater += armor.defenseWater; temp.defWind += armor.defenseWind;
				temp.defFire += armor.defenseFire; temp.defEarth += armor.defenseEarth;
				temp.bowHitModify += armor.bowHitModify;
				temp.spModify += armor.magicDmgModify;
				temp.dmgReduction += armor.dmgReduction;
				temp.weightReduction += armor.weightReduction;
			}
		});
		if (equipment.weapon != null) {
			temp.hitModify += equipment.weapon.hitModify;
			temp.dmgModify += equipment.weapon.dmgModify;
			temp.spModify  += equipment.weapon.magicDmgModify;
		}
		
		equipParameter = temp;
		handle.sendPacket (new UpdatePcAc (this).getRaw());
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
	
	public void addPcInstance (PcInstance _pc) {
		pcsInsight.put (_pc.uuid, _pc);
	}
	
	public void removePcInstance (int _uuid) {
		pcsInsight.remove (_uuid);
	}
	
	public void addNpcInstance (NpcInstance _npc) {
		npcsInsight.put (_npc.uuid, _npc);
	}
	
	public void removeNpcInstance (int _uuid) {
		npcsInsight.remove (_uuid);
	}
	
	public void removeAllInsight () {
		pcsInsight.clear ();
		npcsInsight.clear ();
	}
	
	public void boardcastPcInsight (byte[] packet) {
		pcsInsight.forEach ((Integer u, PcInstance p)->{
			p.getHandler ().sendPacket (packet);
		});
	}
	
	public void setHandler (SessionHandler handle) {
		this.handle = handle;
	}
		
	public SessionHandler getHandler () {
		return handle;
	}
	
	public void saveItem () {
		//
	}
	
	public void updateOnlineStatus (boolean isOnline) {
		Connection con = HikariCP.getConnection ();
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement ("UPDATE characters SET OnlineStatus=? WHERE objid=?;") ;
			ps.setInt (1, (isOnline) ? 1:0);
			ps.setInt (2, uuid);
			
			ps.execute ();	
		} catch (Exception e) {
			e.printStackTrace ();
		} finally {
			DatabaseUtil.close (ps) ;
			DatabaseUtil.close (con) ;
		}
	}
	
	public void offline () {
		Vidar.getInstance ().removePc (this);
		
		sightUpdate.stop ();
		
		sightUpdate = null;
		
		DatabaseCmds.savePc (this);		
	}
	
}
