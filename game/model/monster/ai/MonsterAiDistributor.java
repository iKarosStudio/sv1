package vidar.game.model.monster.ai;

import java.util.concurrent.*;

import vidar.server.threadpool.*;
import vidar.server.process_server.*;
import vidar.game.map.*;
import vidar.game.model.monster.*;

/*
 * 抓地圖中的怪物AI執行需求塞到Queue
 * 20180913:只負責清除AI跟回收地面屍體, 塞Queue由視界觸發處理
 */
public class MonsterAiDistributor implements Runnable
{
	private VidarMap map;
	private ScheduledFuture<?> schedulor;
	//private static Queue<Runnable> queue;
	
	public void run () {
		map.monsters.forEach ((Integer u, MonsterInstance m)->{
			//主動模式
			/*
			if (!queue.contains (m.Aikernel) ) {
				queue.offer (m.Aikernel) ;
			}*/
			
			//被動模式
			if (m.aiKernel != null) {
				/*
				 * 太久沒有被玩家觸發, 主動停止並清除AI核心節省系統資源
				 */
				if (m.aiKernel.timeoutCounter < 20) { //500ms * 20 = 10s
					m.aiKernel.timeoutCounter++;
				} else {
					m.aiKernel.cancel () ;
					m.aiKernel = null;
				}
				
			}
			
			if (m.isDead) {
				if (m.aiKernel.deadTimeCounter < 10) { //500ms * 20 = 10s
					m.aiKernel.deadTimeCounter++;
				} else {
					//System.out.printf ("清除%s(%d)屍體\n", m.Name, m.Uuid) ;
					m.boardcastPcInsight (new RemoveModel (m.uuid).getRaw ());
					
					m.aiKernel.cancel ();
					m.aiKernel = null;
					
					//map.remo (m) ;
					map.monsters.remove (m.uuid);
					map.monsterGenerator.removeMonster (m);
				}
			}
		});
	}
	
	public MonsterAiDistributor (VidarMap map) {
		this.map = map;
		//queue = MonsterAiQueue.getInstance ().getQueue ();
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 100, 500);
	}
	
	public void stop () {
		schedulor.cancel (false) ;
	}
}
