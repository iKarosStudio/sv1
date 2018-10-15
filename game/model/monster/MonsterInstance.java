package vidar.game.model.monster;

import java.util.*;
import java.util.concurrent.*;

import vidar.config.*;
import vidar.types.*;
import vidar.server.utility.*;
import vidar.server.process_server.*;
import vidar.game.*;
import vidar.game.template.*;
import vidar.game.map.VidarMap;
import vidar.game.model.*;
import vidar.game.model.item.*;
import vidar.game.model.monster.ai.*;
import vidar.game.skill.*;

public class MonsterInstance extends ActiveModel implements Fightable, Moveable, SkillCastable, BoardcastNode
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
	
	public boolean isUndead;
	public boolean isOrc;
	public boolean isWolf;
	
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
		location.p.x = loc.p.x;
		location.p.y = loc.p.y;
		heading = 0;
		//updateCurrentMap ();
		
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
	
	public void transferExp () {
		int MonsterExp = exp * Configurations.RateExp;
		
		System.out.printf ("%s 經驗值轉移:\n", name) ;
		
		hateList.forEach ((Integer pcUuid, Integer hit)->{
			PcInstance recv = getCurrentMap ().pcs.get (pcUuid);
			recv.exp += MonsterExp * hit / hateListSize;
			
			System.out.printf ("\tHateList->%s 分到 %d經驗值\n", recv.name, MonsterExp * hit / hateListSize) ;
			recv.getHandle().sendPacket (new UpdateExp (recv).getRaw ());
		}) ;
	}
	
	public void transferItems () {
		System.out.printf ("%s 道具轉移(Total:%d):\n", name, hateListSize) ;
		
		itemBag.forEach ((Integer itemId, ItemInstance dropItem)->{
			hateList.forEach ((Integer pcUuid, Integer hatePoint)->{
				PcInstance recv = getCurrentMap ().pcs.get (pcUuid);
				
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
	
	public void toggleHateList (MapModel pc, int dmg) {
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

	@Override
	public void boardcastToAll (byte[] packet) {
		// TODO Auto-generated method stub
		
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
		List<PcInstance> result = Vidar.getInstance ().getMap (location.mapId).getPcsInsight (location.p);
		return result;
	}

	@Override
	public void move (int tmpX, int tmpY, int heading) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public synchronized void moveToHeading (int heading) {
		int px = location.p.x;
		int py = location.p.y;
		
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
		
		if (getCurrentMap ().isNextTileAccessible (location.p.x, location.p.y, heading) ) {			
			getCurrentMap ().setAccessible (location.p.x, location.p.y, true);
			
			byte[] movePacket = new ModelMove (uuid, location.p.x, location.p.y, heading).getRaw () ;
			boardcastPcInsight (movePacket);
			
			//update loc
			getCurrentMap ().setAccessible (px, py, false);
			location.p.x = px;
			location.p.y = py;
			this.heading = heading;
		} else {
			//can't pass
			return ;
		}
	}

	@Override
	public void attack (MapModel target) {
		heading = getDirection (target.location.p.x, target.location.p.y);
		
		/* 嘗試接近攻擊目標 */
		if (getDistance (target.location.p.x, target.location.p.y) > 1) {
			moveToHeading (heading);
			return;
		}
		
		/* 顯示攻擊動作 */
		byte[] actionPacket = new ModelAction (ModelActionId.ATTACK, uuid, heading).getRaw ();
		boardcastPcInsight (actionPacket);
		
		/* 命中與傷害運算 */
		if (isInsight (target.location)) {
			//NormalAttack atk = new NormalAttack (this, target);
		}
	}

	@Override
	public void attack (int targetUuid, int targetX, int targetY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void damage (NormalAttack atk) {
		int dmg = atk.totalDmg;
		
		//減免調整
		atk.hitRate += (getAc () * 5);
		
		
		if (atk.isHit ()) {
			System.out.printf ("命中! %d\n", atk.hitRate);
			if (hp > dmg) {
				hp -= dmg;
				
				//挨打動作
				boardcastPcInsight (new ModelAction (ModelActionId.DAMAGE, uuid, heading).getRaw());
				
				toggleHateList (atk.attacker, dmg);
				
				//設定反擊
				if (targetPc == null) {
					aiKernel.cancel ();
					actionStatus = MonsterInstance.ACTION_ATTACK;
					targetPc = (PcInstance) atk.attacker;
				}
				
			} else {
				
				//一個毆打致死
				try {
					byte[] die = new ModelAction (ModelActionId.DIE, uuid, heading).getRaw ();
					boardcastPcInsight (die);
					
					toggleHateList (atk.attacker, dmg);
					
					//轉移經驗值與道具
					transferExp ();
					transferItems ();					
					
					hp = 0;
					isDead = true;
					actionStatus = MonsterInstance.ACTION_DEAD;
					System.out.printf ("%s 死掉了\n", name);
				} catch (Exception e) {
					e.printStackTrace ();
				}
			}
		} else {
			System.out.println ("沒有命中!");
		}
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
	public int getStr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCon () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDex () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWis () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCha () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntel () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSp () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAc () {
		return basicParameters.ac;
	}

	@Override
	public int getMaxHp () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxMp () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHpr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMpr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDmgReduction () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWeightReduction () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void boardcastPcInsight (byte[] packet) {
		VidarMap map = Vidar.getInstance ().getMap (location.mapId);
		List<PcInstance> pcs = map.getPcsInsight (location.p);
		for (PcInstance pc : pcs) {
			pc.getHandle ().sendPacket (packet);
		}
		pcs = null;
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

	@Override
	public boolean hasSkillEffect (int skillId) {
		// TODO Auto-generated method stub
		return false;
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
	public void pickItem (int uuid, int count, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropItem (int uuid, int count, int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
}
