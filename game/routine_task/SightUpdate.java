package vidar.game.routine_task;

import java.util.*;
import java.util.concurrent.*;

import vidar.config.*;
import vidar.server.*;
import vidar.server.threadpool.*;
import vidar.server.process_server.*;
import vidar.game.model.*;
import vidar.game.model.npc.*;
import vidar.game.model.monster.*;
import vidar.game.map.*;

public class SightUpdate implements Runnable
{
	ScheduledFuture<?> schedulor;
	private PcInstance pc;
	private SessionHandler handle;

	public SightUpdate (PcInstance pc) {
		this.pc = pc;
		handle = pc.getHandler () ;
	}
	
	public void run () {
		/* 更新視界各類物件 */
		updatePcs ();
		updateNpcs ();
		//Monster () ;
		//Npc () ;
		//Item () ;
		//Door () ;
	}
	
	private void updatePcs () {
		//
		// 加入/移出不在清單內卻在視距內的物件
		// 注意:不會自己將自身視為端點
		//
		List<PcInstance> pcs = pc.map.getPcsInsight (pc.location.point);
		//pcs.forEach ((PcInstance eachPc)->{
		for (PcInstance eachPc : pcs) {
			if (!pc.pcsInsight.containsKey (eachPc.uuid) && (eachPc.uuid != pc.uuid)) {
				pc.addPcInstance (eachPc);
				handle.sendPacket (new ModelPacket (eachPc).getRaw ());
			}
		}
		
		//
		//玩家必須額外注意是否已經離線, 檢查socket close or thread is Alive
		//
		pc.pcsInsight.forEach ((Integer u, PcInstance p)->{
			if (!pc.isInsight (p.location) || !pcs.contains (p)) {
				pc.removePcInstance (u);
				handle.sendPacket (new RemoveModel (u).getRaw());
			}
		});
	}
	
	private void updateNpcs () {
		List<NpcInstance> npcs = pc.map.getNpcsInsight (pc.location.point);
		for (NpcInstance eachNpc : npcs) {
			if (!pc.npcsInsight.containsKey (eachNpc.uuid)) {
				pc.addNpcInstance (eachNpc);
				handle.sendPacket (new ModelPacket (eachNpc).getRaw());
			}
		}
	}
	
	/*
	public void Npc () {
		List<NpcInstance> Npcs = Pc.getNpcInsight () ;
		for (NpcInstance NpcNode : Npcs) {
			if (!Pc.NpcInsight.containsKey (NpcNode.Uuid) ) {
				Pc.addNpcInstance (NpcNode) ;
				Handle.sendPacket (new NodePacket (NpcNode).getRaw () ) ;
			}
		}
		Npcs = null;
		
		Pc.NpcInsight.forEach ((Integer u, NpcInstance n)->{
			if (!Pc.isInsight (n) ) {
				Pc.removeNpcInsight (n) ;
				Handle.sendPacket (new RemoveObject (n.Uuid).getRaw () ) ;
			}
		});
	}
	
	public void Monster () {
		List<MonsterInstance> Mobs = Pc.getMonsterInsight () ;
		for (MonsterInstance m : Mobs) {
			if (!Pc.MonsterInsight.containsKey (m.Uuid) ) {
				Pc.addMonsterInstance (m) ;
				Handle.sendPacket (new NodePacket (m).getRaw () );
			}
		}
		Mobs = null;
		
		Pc.MonsterInsight.forEach ((Integer u, MonsterInstance m)->{
			if (!Pc.isInsight (m) ) {
				Pc.removeMonsterInsight (m) ;
				Handle.sendPacket (new RemoveObject (m.Uuid).getRaw () ) ;
			} else {
				m.ToggleAi () ;
			}
		}) ;
	}
	
	public void Item () {
		List<ItemInstance> Items = Pc.getItemInsight () ;
		for (ItemInstance i : Items) {
			if (!Pc.GndItemInsight.containsKey (i.Uuid) ) {
				Pc.addGndItemInstance (i) ;
				Handle.sendPacket (new NodePacket (i).getRaw () ) ;
			}
		}
		Items = null;
		
		Pc.GndItemInsight.forEach ((Integer u, ItemInstance i)->{
			if (!Pc.isInsight (i) ) {
				Pc.removeGndItemInsight (i) ;
				Handle.sendPacket (new RemoveObject (i.Uuid).getRaw () ) ;
			}
		});
	}
	
	public void Door () {
		List<DoorInstance> Doors = Pc.getDoorInsight () ;
		for (DoorInstance d : Doors) {
			if (!Pc.DoorInsight.containsKey (d.Uuid) ) {
				Pc.addDoorInstance (d) ;
				Handle.sendPacket (new NodePacket (d).getRaw () ) ;
				Handle.sendPacket (new DoorDetail (d).getRaw () ) ;
			}
		}
		Doors = null;
		
		Pc.DoorInsight.forEach ((Integer u, DoorInstance d)->{
			if (!Pc.isInsight (d) ) {
				Pc.removeDoorInsight (d) ;
				Handle.sendPacket (new RemoveObject (d.Uuid).getRaw () ) ;
			}
		});
	}
	
	public void Pet () {
	}
	*/
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 200, 500);
	}
	
	public void stop () {
		schedulor.cancel (true);
	}
}
