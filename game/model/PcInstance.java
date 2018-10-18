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
import vidar.game.template.*;
import vidar.game.routine_task.*;

public class PcInstance extends ActiveModel implements Fightable, Moveable, SkillCastable, BoardcastNode
{	
	private SessionHandler handle;
	public boolean isExit;
	
	public static final int TYPE_ROYAL = 0;
	public static final int TYPE_KNIGHT = 1;
	public static final int TYPE_ELF = 2;
	public static final int TYPE_WIZARD = 3;
	public static final int TYPE_DARKELF = 4;
	/* 角色職業類別 */
	public int type;
	
	public static final int SEX_MALE = 0;
	public static final int SEX_FEMALE = 1;
	/* 角色性別 */
	public int sex;
	
	public int satiation;
	public int pkCount;
	
	public int accessLevel = 0;
	public boolean isGm;
	public boolean isRd;
	
	public int battleCounter;
	
	/* 裝備與裝備的加成素質 */
	public Equipment equipment;
	public AbilityParameter equipParameter;
	
	/* 藥水延遲  <K, V> = <道具編號, 上一個時間戳記> */
	private ConcurrentHashMap<Integer, Long> itemDelay = null;
	
	/* 技能延遲  <K, V> = <技能編號, 上一個時間戳記> */
	private ConcurrentHashMap<Integer, Long> skillDelay = null;
	
	/* 視線內物件 <K, V> = <UUID, 物件實體> */
	public ConcurrentHashMap<Integer, PcInstance> pcsInsight = null;
	public ConcurrentHashMap<Integer, MapModel> modelsInsight = null;
	
	public SightUpdate sightUpdate;
	public PcRoutineTasks routineTasks;
	public ExpMonitor expMonitor;
	public HpMonitor hpMonitor;
	//public MpMonitor mpMonitor;
	
	public SessionHandler getHandle () {
		return handle;
	}
	
	public void setHandle (SessionHandler _handle) {
		handle = _handle;
	}
	
	public PcInstance () {
		basicParameters = new AbilityParameter ();
		location = new Location ();
	}
	
	public PcInstance (SessionHandler _handle) {
		this.handle = _handle;
		itemBag = new ConcurrentHashMap<Integer, ItemInstance> ();
		location = new Location ();
		equipment = new Equipment (handle);
		
		skillBuffs = new SkillEffectTimer (this);
		itemDelay = new ConcurrentHashMap<Integer, Long> ();
		skillDelay = new ConcurrentHashMap<Integer, Long> ();
		
		
		pcsInsight = new ConcurrentHashMap<Integer, PcInstance> ();
		modelsInsight = new ConcurrentHashMap<Integer, MapModel> ();
		//npcsInsight = new ConcurrentHashMap<Integer, NpcInstance> ();
		//monstersInsight = new ConcurrentHashMap<Integer, MonsterInstance> ();
		//doorsInsight = new ConcurrentHashMap<Integer, DoorInstance> ();
		//itemsInsight = new ConcurrentHashMap<Integer, ItemInstance> ();
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
				location.p.x = rs.getInt ("LocX");
				location.p.y = rs.getInt ("LocY");
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
				//expRes = rs.getInt ("ExpRes");
				
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
				status |= MapModel.STATUS_PC;
				
				
				hp = rs.getInt ("CurHp");
				mp = rs.getInt ("CurMp");
				basicParameters.maxHp = rs.getInt ("MaxHp");
				basicParameters.maxMp = rs.getInt ("MaxMp");
				
				pkCount = rs.getInt ("PKcount");
				
				routineTasks = new PcRoutineTasks (this);
				expMonitor = new ExpMonitor (this);	
				hpMonitor = new HpMonitor (this);
				
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
	
	public void saveSkillEffects () {
		skillBuffs.saveBuffs ();
	}

	public void loadSkillEffects () {
		skillBuffs.loadBuffs ();
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
	
	public void addMoney (int amount) {
		ItemInstance money = new ItemInstance (40308, UuidGenerator.next(), uuid, 0, amount, 0, 0, false, true);
		addItem (money);
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
	
	public void setWeapon (int uuid) {
		ItemInstance weapon = findItemByUuid (uuid);
		if (weapon != null) {
			equipment.setWeapon (weapon);
			applyEquipmentEffects ();
			actId = getWeaponGfx ();
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
		List<ItemInstance> equipmentList = equipment.getList ();
		
		equipmentList.forEach ((ItemInstance armor)->{
			if (armor != null) {
				temp.ac += armor.ac;
				temp.str += armor.str; temp.dex += armor.dex; temp.con += armor.con;
				temp.wis += armor.wis; temp.cha += armor.cha; temp.intel += armor.intel;
				temp.maxHp += armor.hp; temp.maxMp += armor.mp;
				temp.hpR += armor.hpr; temp.mpR += armor.mpr;
				temp.sp += armor.sp; temp.mr += armor.mr;
				temp.defWater += armor.defenseWater; temp.defWind += armor.defenseWind;
				temp.defFire += armor.defenseFire; temp.defEarth += armor.defenseEarth;
				//temp.bowHitModify += armor.bowHitModify;
				//temp.spModify += armor.magicDmgModify;
				temp.dmgReduction += armor.dmgReduction;
				temp.weightReduction += armor.weightReduction;
				
				//temp.hitModify += equipment.weapon.hitModify;
				//temp.dmgModify += equipment.weapon.dmgModify;
				//temp.spModify  += equipment.weapon.magicDmgModify;
			}
		});
		
		equipParameter = temp;
		if (isPoly ()) {
		} else {
			actId = getWeaponGfx ();
		}
		handle.sendPacket (new UpdateAc (this).getRaw());
	}
	
	public void setBrave () {
		braveSpeed = 1;
		status |= STATUS_BRAVE;
	}
	
	public void unsetBrave () {
		braveSpeed = 0;
		status &= ~STATUS_BRAVE;
	}
	
	public boolean isRoyal () {
		return (type == TYPE_ROYAL);
	}
	
	public boolean isKnight () {
		return (type == TYPE_KNIGHT);
	}
	
	public boolean isElf () {
		return (type == TYPE_ELF);
	}
	
	public boolean isWizard () {
		return (type == TYPE_WIZARD);
	}
	
	public boolean isDarkelf () {
		return (type == TYPE_DARKELF);
	}
	
	public int getWeaponGfx () {
		if (equipment.weapon == null) {
			return 0;
		} else {
			return equipment.weapon.actId;
		}
	}

	@Override
	public void boardcastToAll (byte[] packet) {
		Vidar.getInstance ().boardcastToAllPc (packet);
	}

	@Override
	public void boardcastToMap (byte[] packet) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<MapModel> getModelInsight () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PcInstance> getPcInsight () {
		// TODO
		return null;
	}

	@Override
	public void move (int tmpX, int tmpY, int newHeading) {
		/* Server config 參數為3的情況下, 使用腳色原本的座標操作, 封包的座標無效 */
		int x = location.p.x;
		int y = location.p.y;
		
		getCurrentMap ().setAccessible (location.p.x, location.p.y, true) ;
		
		switch (newHeading) {
		case 0:
		case 73:
			heading = 0; y--;
			break;
			
		case 1:
		case 72:
			heading = 1; x++; y--;
			break;
			
		case 2:
		case 75:
			heading = 2; x++;
			break;
			
		case 3:
		case 74:
			heading = 3; x++; y++;
			break;
			
		case 4:
		case 77:
			heading = 4; y++;
			break;	
			
		case 5:
		case 76:
			heading = 5; x--; y++;
			break;
			
		case 6:
		case 79:
			heading = 6; x--;
			break;
			
		case 7:
		case 78:
			heading = 7; x--; y--;
			break;

		default : break;
		}
		
		/* 防穿檢查
		if (!Pc.CurrentMap.isPassable (x, y) ) {
			//System.out.printf ("next p(%5d, %5d) = 0x%02x is not passable\n", x, y, Pc.CurrentMap.getTile (x, y) ) ;
			//return ;
		}
		*/
		
		/*
		 * 廣播移動訊息
		 */
		boardcastPcInsight (new ModelMove (uuid, location.p.x, location.p.y, newHeading).getRaw ());
		
		/* 檢查是否需要傳送位置 */
		if (getCurrentMap ().isInTpLocation (x, y) ) {
			skillBuffs.saveBuffs ();
			new Teleport (this, getCurrentMap ().getTpDestination (x, y), false);
			return ;
		}
		
		/* 更新自身位置 */
		location.p.x = x;
		location.p.y = y;
		heading = newHeading;
		getCurrentMap ().setAccessible (location.p.x, location.p.y, false);
	}

	@Override
	public void moveToHeading (int heading) {
		//
	}

	@Override
	public void attack (MapModel target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attack (int targetUuid, int targetX, int targetY) {
		// TODO Auto-generated method stub
		heading = getDirection (targetX, targetY);
		
		//建構攻擊原型
		NormalAttack atk = new NormalAttack (this, targetUuid, targetX, targetY);
		if (atk.target != null) {
			atk.target.damage (atk);
		}
		
		//表現攻擊動作
		int actionId = ModelActionId.ATTACK;
		handle.sendPacket (new ModelAction (actionId, uuid, heading).getRaw ());
	}

	@Override
	public void damage (NormalAttack atk) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void useSkill (int targetUuid, int actionId, int skillGfx, int targetX, int targetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useAttackSkill (int targetUuid, int actionId, int skillGfx, int tx, int ty, boolean isHit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pickItem (int _uuid, int count, int x, int y) {
		ItemInstance pick = (ItemInstance) getCurrentMap ().models.get (_uuid);
		if (pick != null) {
			byte[] actionPacket = new ModelAction (ModelActionId.PICK_UP, uuid, heading).getRaw ();
			
			handle.sendPacket (actionPacket);
			boardcastPcInsight (actionPacket);
			
			pick.uuidOwner = uuid;
			pick.location.p.x = 0;
			pick.location.p.y = 0;
			pick.location.mapId = 0;			
			getCurrentMap ().models.remove (pick.uuid);
			addItem (pick);
		}
	}

	@Override
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
			
			drop.location.p.x = x;
			drop.location.p.y = y;
			drop.location.mapId = location.mapId;
			drop.exp = drop.count;
			
			getCurrentMap ().models.put (drop.uuid, drop);
		} else {
			/* error */
		}
		
		/* 更新負重 */
		handle.sendPacket (new NodeStatus (this).getRaw ());
	}

	@Override
	public void giveItem () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recvItem () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStr () {
		return basicParameters.str + skillParameters.str + equipParameter.str;
	}

	@Override
	public int getCon () {
		return basicParameters.con + skillParameters.con + equipParameter.con;
	}

	@Override
	public int getDex () {
		return basicParameters.dex + skillParameters.dex + equipParameter.dex;
	}

	@Override
	public int getWis () {
		return basicParameters.wis + skillParameters.wis + equipParameter.wis;
	}

	@Override
	public int getCha () {
		return basicParameters.cha + skillParameters.cha + equipParameter.cha;
	}

	@Override
	public int getIntel () {
		return basicParameters.intel + skillParameters.intel + equipParameter.intel;
	}

	@Override
	public int getSp () {
		return basicParameters.sp + skillParameters.sp + equipParameter.sp;
	}

	@Override
	public int getMr () {
		return basicParameters.mr + skillParameters.mr + equipParameter.mr;
	}

	@Override
	public int getAc () {
		return basicParameters.ac + skillParameters.ac + equipParameter.ac;
	}

	@Override
	public int getMaxHp () {
		return basicParameters.maxHp + skillParameters.maxHp + equipParameter.maxHp;
	}

	@Override
	public int getMaxMp () {
		return basicParameters.maxMp + skillParameters.maxMp + equipParameter.maxMp;
	}

	@Override
	public int getHpr () {
		return basicParameters.hpR + skillParameters.hpR + equipParameter.hpR;
	}

	@Override
	public int getMpr () {
		return basicParameters.mpR + skillParameters.mpR + equipParameter.mpR;
	}

	@Override
	public int getDmgReduction () {
		return basicParameters.dmgReduction + skillParameters.dmgReduction + equipParameter.dmgReduction;
	}

	@Override
	public int getWeightReduction () {
		return basicParameters.weightReduction + skillParameters.weightReduction + equipParameter.weightReduction;
	}

	@Override
	public void boardcastPcInsight (byte[] packet) {
		pcsInsight.forEach ((Integer uuid, PcInstance pc)->{
			pc.getHandle ().sendPacket (packet);
		});
	}

	@Override
	public void updateModel () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isParalyzed () {
		// TODO Auto-generated method stub
		return false;
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
		saveSkillEffects ();
		
		sightUpdate.stop ();
		routineTasks.stop ();
		skillBuffs.stop ();
		
		Vidar.getInstance ().removePc (this);

		sightUpdate = null;
		routineTasks = null;
		skillBuffs = null;
		
		DatabaseCmds.savePc (this);		
	}

	@Override
	public String getName () {
		return name;
	}
}
