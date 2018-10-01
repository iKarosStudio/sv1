package vidar.game.model.monster.ai;

import java.util.*;

import vidar.server.process_server.*;
import vidar.game.map.*;
import vidar.game.model.monster.*;

public class MonsterAiKernel extends TimerTask implements Runnable
{	
	//private static TimerPool SchedulePool;
	private static Random random = new Random (System.currentTimeMillis ());
	VidarMap map;
	MonsterInstance monster;
	
	public boolean isAiRunning = false;
	public int timeoutCounter = 0;
	public int deadTimeCounter = 0;
	
	public void run () {
		try {			
			/*
			 * 清空AI移除計時器
			 */
			timeoutCounter = 0;
			
			/*
			 * 死亡檢查
			 */
			
			if (!monster.isDead) {
				if (monster.hp < 1) {
					byte[] die = new ModelAction (ModelActionId.DIE, monster.uuid, monster.heading).getRaw () ;
					
					//轉移經驗值與道具
					monster.transferExp ();
					monster.transferItems ();
					
					monster.boardcastPcInsight (die);
					
					monster.isDead = true;
					monster.actionStatus = MonsterInstance.ACTION_DEAD;
					System.out.printf ("%s 死掉了\n", monster.name);
				}
			}
			
			/*
			 * 執行AI動作
			 */
			ai ();
			
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	public MonsterAiKernel (MonsterInstance _monster) {
		monster = _monster;
		map = monster.map;
	}
	
	public boolean ai () {
		try {			
			isAiRunning = true;
			
			if (monster.actionStatus == MonsterInstance.ACTION_STOP) {
				Thread.sleep (500);
				
			} else if (monster.actionStatus == MonsterInstance.ACTION_IDLE) {
				monster.moveToHeading (random.nextInt (8));
				Thread.sleep (monster.moveInterval);
				
				/* 0~3S隨機停頓  */
				Thread.sleep (random.nextInt (3000));
				
			} else if (monster.actionStatus == MonsterInstance.ACTION_ATTACK) {
				if (monster.targetPc == null) {
					if (monster.agro) {
						//find target
					} else {
						monster.actionStatus = MonsterInstance.ACTION_IDLE;
					}
				} else {
					monster.attackPc (monster.targetPc);
					Thread.sleep (monster.attackInterval);
				}
				
			} else if (monster.actionStatus == MonsterInstance.ACTION_DEAD) {
				//
				
			} else {
				System.out.printf ("UNKNOWN AI STATUS MOB:%s(%d)\n", monster.name, monster.uuid) ;
			}
			isAiRunning = false;
			
		} catch (Exception e) {
			isAiRunning = false;
			System.out.printf ("%s -> map:%d(%d,%d)\n", monster.name, monster.location.mapId, monster.location.point.x, monster.location.point.y) ;
			e.printStackTrace ();
			System.exit (999);
			
		}
		
		return true;
	}
}
