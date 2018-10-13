package vidar.game.routine_task;

import java.util.*;
import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.threadpool.*;
import vidar.server.process_server.*;
import vidar.game.model.*;
import vidar.game.model.npc.*;
import vidar.game.model.monster.*;
import vidar.game.model.item.*;

public class SightUpdate implements Runnable
{
	ScheduledFuture<?> schedulor;
	private PcInstance pc;
	private SessionHandler handle;

	public SightUpdate (PcInstance pc) {
		this.pc = pc;
		handle = pc.getHandle () ;
	}
	
	public void run () {
		/* 更新視界各類物件 */
		updatePcs ();
		updateModels ();
	}
	
	private void updatePcs () {
		//
		// 加入/移出不在清單內卻在視距內的物件
		// 注意:不會自己將自身視為端點
		//
		List<PcInstance> pcs = pc.getCurrentMap ().getPcsInsight (pc.location.p);
		//pcs.forEach ((PcInstance eachPc)->{
		for (PcInstance eachPc : pcs) {
			if (!pc.pcsInsight.containsKey (eachPc.uuid) && (eachPc.uuid != pc.uuid)) {
				//pc.addPcInstance (eachPc);
				pc.pcsInsight.putIfAbsent (eachPc.uuid, eachPc);
				handle.sendPacket (new ModelPacket (eachPc).getRaw ());
			}
		}
		
		//
		//玩家必須額外注意是否已經離線, 檢查socket close or thread is Alive
		//
		pc.pcsInsight.forEach ((Integer u, PcInstance p)->{
			if (!pc.isInsight (p.location) || !pcs.contains (p)) {
				//pc.removePcInstance (u);
				pc.pcsInsight.remove (u);
				handle.sendPacket (new RemoveModel (u).getRaw());
			}
		});
	}
	
	private void updateModels () {
		List<MapModel> models = pc.getCurrentMap ().getModelInsight (pc.location.p);
		for (MapModel model : models) {
			if (!pc.modelsInsight.containsKey (model.uuid)) {
				pc.modelsInsight.putIfAbsent (model.uuid, model);
				handle.sendPacket (new ModelPacket (model).getRaw());
			}
		}
		models = null;
		
		pc.modelsInsight.forEach ((Integer uuid, MapModel model)->{
			if (!pc.isInsight (model.location)) {
				pc.modelsInsight.remove (model.uuid);
				handle.sendPacket (new RemoveModel (model.uuid).getRaw ());
			} else {
				if (model instanceof MonsterInstance) {
					((MonsterInstance) model).toggleAi ();
				}
			}
		});
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 200, 500);
	}
	
	public void stop () {
		schedulor.cancel (true);
	}
}
