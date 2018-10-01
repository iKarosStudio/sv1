package vidar.game.model.monster;

import java.util.*;
import java.util.concurrent.*;

import vidar.config.*;
import vidar.types.*;
import vidar.server.utility.*;
import vidar.server.process_server.*;
import vidar.game.template.*;
import vidar.game.skill.*;
import vidar.game.model.*;
import vidar.game.model.item.*;
import vidar.game.model.monster.ai.*;

public class MonsterInstance extends Model
{
	public String nameId;
	
	public static final int ACTION_STOP=0;
	public static final int ACTION_IDLE=1;
	public static final int ACTION_ATTACK=2;
	public static final int ACTION_DEAD=3;
	public int actionStatus = 0;
	
	public int groupId;
	public int randomX;
	public int randomY;
	public int movementDistance;
	public int rest;
	
	public int moveInterval = 0;
	public int attackInterval = 0;
	public int majorSkillInterval = 0;
	public int minorSkillInterval = 0;
	
	public boolean agro;
	
	/* 怪物持有道具 <K, V> = <itemid, item> */
	public ConcurrentHashMap<Integer, ItemInstance> itemBag = null;
	
	/* 怪物經驗值 */
	public int exp;
	
	/* 仇恨清單 <K, V> = <仇恨對象UUID, 打擊次數> */
	private int hateListSize = 0;
	public ConcurrentHashMap<Integer, Integer> hateList;
	public PcInstance targetPc = null;
	
	/* AI動作核心 */
	public MonsterAiKernel aiKernel;
	
	public MonsterInstance (NpcTemplate template, Location loc) {
		uuid = template.uuid;
		gfx = template.gfx;
		gfxTemp = template.gfxTemp;
		name = template.name;
		nameId = template.nameId;
		
		level = template.level;
		exp = template.exp;
		size = template.size;
		
		location = new Location ();
		location.mapId = loc.mapId;
		location.point.x = loc.point.x;
		location.point.y = loc.point.y;
		heading = 0;
		updateCurrentMap ();
		
		basicParameters = new AbilityParameter ();
		basicParameters.maxHp = template.basicParameters.maxHp;
		basicParameters.maxMp = template.basicParameters.maxMp;
		hp = basicParameters.maxHp;
		mp = basicParameters.maxMp;
		basicParameters.ac = template.basicParameters.ac;
		
		moveInterval = template.moveInterval;
		attackInterval = template.attackInterval;
		majorSkillInterval = template.majorSkillInterval;
		minorSkillInterval = template.minorSkillInterval;
		
		agro = template.agro;
		
		isUndead = template.isUndead;
		if (template.family.equalsIgnoreCase ("wolf")) {
			isWolf = true;
		}
		
		if (template.family.equalsIgnoreCase ("orc")) {
			isOrc = true;
		}
		
		itemBag = new ConcurrentHashMap<Integer, ItemInstance> ();
	}
	
	public synchronized void moveToHeading (int heading) {
		
		int px = location.point.x;
		int py = location.point.y;
		
		switch (heading) {
		case 0: py--; break;
		case 1: px++; py--; break;
		case 2: px++; break;
		case 3: px++; py++; break;
		case 4: py++; break;
		case 5: px--; py++; break;
		case 6: px--; break;
		case 7: px--; py--; break;
		default: break;
		}
		//上段需要優化
		
		if (map.isNextTileAccessible (location.point.x, location.point.y, heading) ) {			
			map.setAccessible (location.point.x, location.point.y, true);
			
			byte[] movePacket = new ModelMove (uuid, location.point.x, location.point.y, heading).getRaw () ;
			boardcastPcInsight (movePacket);
			
			//update loc
			map.setAccessible (px, py, false);
			location.point.x = px;
			location.point.y = py;
			this.heading = heading;
		} else {
			//can't pass
			return ;
		}
	}
	
	public void attackPc (PcInstance pc) {
		heading = getDirection (pc.location.point.x, pc.location.point.y);
		
		/* 嘗試接近攻擊目標 */
		if (getDistance (pc.location.point.x, pc.location.point.y) > 1) {
			moveToHeading (heading);
			return;
		}
		
		/* 顯示攻擊動作 */
		byte[] actionPacket = new ModelAction (ModelActionId.ATTACK, uuid, heading).getRaw ();
		boardcastPcInsight (actionPacket);
		
		/* 命中與傷害運算 */
		if (isInsight (pc.location)) {
			NormalAttack atk = new NormalAttack (this, pc);
		}
	}
	
	public void attackPet () {
		//
	}
	
	synchronized public void takeDamage (int dmg) {		
		if (hp > dmg) {
			hp -= dmg;
			
			//挨打動作
			boardcastPcInsight (new ModelAction (ModelActionId.DAMAGE, uuid, heading).getRaw());
			
		} else {
			/*
			try {
				Aikernel.wait () ;
			} catch (Exception e) {e.printStackTrace () ; }
			*/
			hp = 0;
			//isDead = true;
			//actionStatus = ACTION_DEAD;
			
			//往生動作
			//boardcastPcInsight (new ModelAction (ModelActionId.DIE, uuid, heading).getRaw());
		}
		
		
	}
	
	public void transferExp () {
		int MonsterExp = exp * Configurations.RateExp;
		
		System.out.printf ("%s 經驗值轉移:\n", name) ;
		
		hateList.forEach ((Integer pcUuid, Integer hit)->{
			PcInstance recv = map.pcs.get (pcUuid);
			recv.exp += MonsterExp * hit / hateListSize;
			
			System.out.printf ("\tHateList->%s 分到 %d經驗值\n", recv.name, MonsterExp * hit / hateListSize) ;
			recv.getHandle().sendPacket (new UpdateExp (recv).getRaw ());
		}) ;
	}
	
	public void transferItems () {
		System.out.printf ("%s 道具轉移(Total:%d):\n", name, hateListSize) ;
		
		itemBag.forEach ((Integer itemId, ItemInstance dropItem)->{
			hateList.forEach ((Integer pcUuid, Integer hatePoint)->{
				PcInstance recv = map.pcs.get (pcUuid);
				
				System.out.printf ("\t%s hit:%d 分到 %s\n", recv.name, hatePoint, dropItem.getName ());
				
				/* 道具真實UUID */
				dropItem.uuid = UuidGenerator.next ();
				dropItem.uuidOwner = recv.uuid;
				recv.addItem (dropItem);
				
				String[] serverMessage = {name, dropItem.getName ()};
				byte[] msgPacket = new ServerMessage (143, serverMessage).getRaw ();
				//超過20E金幣丟msgid:166
				recv.getHandle ().sendPacket (msgPacket);
				
			}) ;
			
			itemBag.remove (itemId);
		});		
	}
	
	public synchronized void toggleAi () {
		if (aiKernel == null) {
			aiKernel = new MonsterAiKernel (this);
		} 
		//else {
			//Clear timeout counter			
			if (aiKernel.isAiRunning) {
				/*
				 * 同步問題, AI工作未睡眠結束就被執行
				 * 或視距內多個玩家觸發
				 */
				return;
				
			} else {
				/* AI工作加入Queue等候執行 */
				MonsterAiQueue.getInstance ().getQueue ().offer (aiKernel);
			}
		//}
	}
	
	public void toggleHateList (PcInstance pc, int dmg) {
		hateListSize ++;
		if (hateList == null) {
			hateList = new ConcurrentHashMap<Integer, Integer> ();
		}
		
		if (hateList.containsKey (pc.uuid)) {
			int hate = hateList.get (pc.uuid) + 1;
			hateList.put (pc.uuid, hate);
		} else {
			hateList.put (pc.uuid, 1) ;
		}
		
	}
	
	public void boardcastPcInsight (byte[] packet) {
		List<PcInstance> pcs = map.getPcsInsight (location.point);			
		for (PcInstance pc : pcs) {
			pc.getHandle ().sendPacket (packet);
		}
	}
}
