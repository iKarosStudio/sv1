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
import vidar.game.skill.*;
import vidar.game.model.item.*;
import vidar.game.model.npc.*;
import vidar.game.model.monster.*;
import vidar.game.template.*;
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
	
	/* 藥水延遲  <K, V> = <道具編號, 上一個時間戳記> */
	private ConcurrentHashMap<Integer, Long> itemDelay = null;
	
	/* 技能延遲  <K, V> = <技能編號, 上一個時間戳記> */
	private ConcurrentHashMap<Integer, Long> skillDelay = null;
	
	public ConcurrentHashMap<Integer, PcInstance> pcsInsight;
	public ConcurrentHashMap<Integer, NpcInstance> npcsInsight;
	public ConcurrentHashMap<Integer, MonsterInstance> monstersInsight;
	public ConcurrentHashMap<Integer, DoorInstance> doorsInsight;
	public ConcurrentHashMap<Integer, ItemInstance> itemsInsight;
	
	public SightUpdate sightUpdate;
	public PcRoutineTasks routineTasks;
	public ExpMonitor expMonitor;
	public HpMonitor hpMonitor;
	//public MpMonitor mpMonitor;
	
	public PcInstance () {
		basicParameters = new AbilityParameter ();
		location = new Location ();
	}
	
	public PcInstance (SessionHandler handle) {
		this.handle = handle;
		itemBag = new ConcurrentHashMap<Integer, ItemInstance> ();
		location = new Location ();
		equipment = new Equipment (handle);
		
		skillBuffs = new SkillEffectTimer (this);
		itemDelay = new ConcurrentHashMap<Integer, Long> ();
		skillDelay = new ConcurrentHashMap<Integer, Long> ();
		
		pcsInsight = new ConcurrentHashMap<Integer, PcInstance> ();
		npcsInsight = new ConcurrentHashMap<Integer, NpcInstance> ();
		monstersInsight = new ConcurrentHashMap<Integer, MonsterInstance> ();
		doorsInsight = new ConcurrentHashMap<Integer, DoorInstance> ();
		itemsInsight = new ConcurrentHashMap<Integer, ItemInstance> ();
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
				
				//load bonus parameters
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
				
				routineTasks = new PcRoutineTasks (this);
				expMonitor = new ExpMonitor (this);	
				hpMonitor = new HpMonitor (this);
				
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
	
	public void unload () {
		//未來登出使用
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
				ItemInstance item = new ItemInstance (itemId, itemUuid, itemOwnerUuid, itemEnchant, itemCount, itemDurability, itemChargeCount, itemIsUsing, itemIsIdentified);
				itemBag.put (item.uuid, item);
				
				if (item.isWeapon () && item.isUsing) {
					setWeapon (item.uuid);
				} else if (item.isArmor () && item.isUsing) {
					setArmor (item.uuid);
				} else if (item.isArrow ()) {
					setArrow (item.uuid);
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
		ResultSet rs = null;
		int[] skillValue = new int[25];
		HashMap<Integer, Integer> skillTable = new HashMap<Integer, Integer> ();
		try {
			rs = DatabaseCmds.loadPcSkills (uuid);
			while (rs.next ()) {
				int skill_id = rs.getInt ("skill_id");
				SkillTemplate skillTemplate = CacheData.skill.get (skill_id);
				skillValue[skillTemplate.skillLevel] |= skillTemplate.id;
			}
			
			for (int i = 1; i <= 24; i++) {
				skillTable.putIfAbsent (i, skillValue[i]);
			}
			
			handle.sendPacket (new SkillTable (type, skillTable).getRaw ());
		} catch (Exception e) {
			e.printStackTrace ();
		} finally {
			DatabaseUtil.close (rs);
		}
	}
	
	public void saveSkillEffect () {
		skillBuffs.saveBuffs ();
	}
	
	public void loadSkillEffect () {
		skillBuffs.loadBuffs ();
	}
	
	public synchronized void attack (int _targetUuid, int _targetX, int _targetY) {
		if (isParalyzed ()) {
			return;
		}
		
		heading = getDirection (_targetX, _targetY);
		battleCounter = 30;
		
		byte[] actionPacket;
		
		NormalAttack atk = new NormalAttack (this, _targetUuid, _targetX, _targetY);
		
		if (atk.isHit) {
			System.out.printf ("命中! ");
			if (atk.isRemoteAttack) {
				actionPacket = new ModelShootArrow (this, _targetUuid, _targetX, _targetY).getRaw ();
			} else {
				actionPacket = new ModelAction (ModelActionId.ATTACK, uuid, heading).getRaw ();
			}
		} else {
			System.out.printf ("未命中!\n");
			actionPacket = new ModelAction (ModelActionId.ATTACK, uuid, heading).getRaw ();
		}
		
		//表現攻擊動作
		handle.sendPacket (actionPacket);
		boardcastPcInsight (actionPacket);		
	}

	synchronized public void takeAttack (NormalAttack _atk) {
		battleCounter = 30; //戰鬥狀態秒數
	}
	
	synchronized public void takeDamage (int dmg) {
		if (hp > dmg) {
			hp -= dmg;
			byte[] actionDamage = new ModelAction (ModelActionId.DAMAGE, uuid, heading).getRaw();
			handle.sendPacket (actionDamage);
			boardcastPcInsight (actionDamage);
		} else {
			hp = 0;
			isDead = true;
			byte[] actionDie = new ModelAction (ModelActionId.DIE, uuid, heading).getRaw();
			handle.sendPacket (actionDie);
			boardcastPcInsight (actionDie);
		}
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
	
	public int getMoney () {
		int money = 0;
		List<ItemInstance> find = findItemByItemId (40308);
		if (find.size () > 0) {
			money = find.get (0).count;
		}
		return money;
	}
	
	public void addItem (ItemInstance item) {
		List<ItemInstance> foundItems = findItemByItemId (item.id);
		
		if (item.isStackable && (foundItems.size() > 0)) {
			ItemInstance i = itemBag.get (foundItems.get (0).uuid);
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
	
	public synchronized void removeItemByItemId (int _itemId, int _amount) {
		List<ItemInstance> fountItem = findItemByItemId (_itemId);
		if (fountItem.size () > 0) {
			ItemInstance item = fountItem.get (0);
			removeItem (item.uuid, _amount);
		}
	}
	
	public synchronized void removeItem (ItemInstance item) {
		removeItem (item.uuid, item.count);
	}
	
	public synchronized void removeItem (int _uuid, int _amount) {
		if (itemBag.containsKey (_uuid)) {
			ItemInstance item = itemBag.get (_uuid);
			
			if (item.count > _amount) {
				item.count -= _amount;
				DatabaseCmds.updateItem (item) ;
				handle.sendPacket (new UpdateItemAmount(item).getRaw ());
				handle.sendPacket (new UpdateItemName(item).getRaw ());
				handle.sendPacket (new UpdateItemStatus(item).getRaw ());
			} else {
				itemBag.remove (item.uuid);
				DatabaseCmds.deleteItem (item);
				handle.sendPacket (new ItemRemove (item).getRaw ());
			}
		}
	}
	
	public void pickItem (int _uuid, int count, int x, int y) {
		ItemInstance pick = map.items.get (_uuid);
		if (pick != null) {
			byte[] actionPacket = new ModelAction (ModelActionId.PICK_UP, uuid, heading).getRaw ();
			
			handle.sendPacket (actionPacket);
			boardcastPcInsight (actionPacket);
			
			pick.uuidOwner = uuid;
			pick.location.point.x = 0;
			pick.location.point.y = 0;
			pick.location.mapId = 0;
			addItem (pick);
			map.items.remove (pick.uuid);
		}
	}
	
	public void dropItem (int _uuid, int _amount, int x, int y) {
		if (itemBag.containsKey (_uuid)) {
			ItemInstance item = itemBag.get (_uuid);
			ItemInstance drop = null;
			
			if (item.count > _amount) { /* 丟出數量小於持有數量  */
				item.count -= _amount;
				drop = new ItemInstance (item.id, UuidGenerator.next (), 0, item.enchant, _amount, item.durability, item.chargeCount, false, false);
				
				DatabaseCmds.updateItem (item);
				handle.sendPacket (new UpdateItemAmount(item).getRaw ());
				handle.sendPacket (new UpdateItemName(item).getRaw ());
				handle.sendPacket (new UpdateItemStatus(item).getRaw ());
			} else {
				drop = new ItemInstance (item.id, item.uuid, 0, item.enchant, item.count, item.durability, item.chargeCount, false, false);
				
				DatabaseCmds.deleteItem (item);
				handle.sendPacket (new ItemRemove (item).getRaw ());

				itemBag.remove (item.uuid);
			}
			
			drop.location.point.x = x;
			drop.location.point.y = y;
			drop.location.mapId = location.mapId;
			
			map.items.put (drop.uuid, drop);
		} else {
			/* error */
		}
		
		/* 更新負重 */
		handle.sendPacket (new NodeStatus (this).getRaw ());
	}
	
	public void addSkillEffect (int skillId, int remainTime) {
		SkillEffect skillEffect = new SkillEffect (skillId, remainTime);
		skillBuffs.effects.put (skillId, skillEffect);
	}
	
	public void removeSkillEffect(int skillId) {
		if (skillBuffs.effects.containsKey (skillId)) {
			skillBuffs.effects.get(skillId).remainTime = 0;
		}
	}
	
	public long getItemDelay (int item_id, long now_time) {
		long res;
		if (itemDelay.containsKey (item_id)) {
			res = now_time - itemDelay.get (item_id);
		} else {
			res = Long.MAX_VALUE;
		}
		return res;
	}
	
	public void setItemDelay (int item_id, long now_time) {
		itemDelay.put (item_id, now_time);
	}
	
	public void setWeapon (int uuid) {
		ItemInstance weapon = findItemByUuid (uuid);
		if (weapon != null) {
			equipment.setWeapon (weapon);
			applyEquipmentEffects ();
		}
	}
	
	public void setArmor (int uuid) {
		ItemInstance armor = findItemByUuid (uuid);
		if (armor != null) {
			equipment.setEquipment (armor);
			applyEquipmentEffects ();
		}
	}
	
	public void setArrow (int uuid) {
		ItemInstance arrow = findItemByUuid (uuid);
		if (arrow != null) {
			equipment.setArrow (arrow);
		}
	}
	
	public void applyEquipmentEffects () {
		AbilityParameter temp = new AbilityParameter ();
		List<ItemInstance> armorList = equipment.getArmorList ();
		armorList.forEach ((ItemInstance armor)->{
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
	
	public int getHitModify () {
		return basicParameters.hitModify + skillParameters.hitModify + equipParameter.hitModify;
	}
	
	public int getBowHitModify () {
		return basicParameters.bowHitModify + skillParameters.bowHitModify + equipParameter.bowHitModify;
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
		ArrayList<ItemInstance> allItemList = new ArrayList<ItemInstance> ();
		itemBag.forEach ((Integer uuid, ItemInstance item)->{
			allItemList.add (item);
		});
		
		for (ItemInstance i : allItemList) {
			totalWeight += i.weight;
		}
		
		return totalWeight;
	}
	
	public int getMaxWeight () {
		int maxWeight = 1500 + (((getStr () + getCon () - 18) >> 1) * 150);
		//apply skill effect
		//apply equip effect
		//apply doll effect
		
		return maxWeight * 1000;
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
	
	public void addMonstersInsight (MonsterInstance _monster) {
		monstersInsight.put (_monster.uuid, _monster);
	}
	
	public void removeMonstersInsight (int _uuid) {
		monstersInsight.remove (_uuid);
	}
	
	public void addDoorsInsight (DoorInstance _door) {
		doorsInsight.put (_door.uuid, _door);
	}
	
	public void removeDoorsInsight (int _uuid) {
		doorsInsight.remove (_uuid);
	}
	
	public void addItemInsight (ItemInstance i) {
		itemsInsight.put (i.uuid, i);
	}
	
	public void removeItemInsight (int _uuid) {
		itemsInsight.remove (_uuid);
	}
	
	public void removeAllInsight () {
		pcsInsight.clear ();
		npcsInsight.clear ();
	}
	
	public void boardcastPcInsight (byte[] packet) {
		pcsInsight.forEach ((Integer u, PcInstance p)->{
			p.getHandle ().sendPacket (packet);
		});
	}
	
	public void setHandle (SessionHandler _handle) {
		handle = _handle;
	}
		
	public SessionHandler getHandle () {
		return handle;
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
		saveSkillEffect ();
		
		sightUpdate.stop ();
		routineTasks.stop ();
		skillBuffs.stop ();
		
		Vidar.getInstance ().removePc (this);

		sightUpdate = null;
		routineTasks = null;
		skillBuffs = null;
		
		
		DatabaseCmds.savePc (this);		
	}
	
}
