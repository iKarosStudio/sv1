package vidar.game.routine_task;

import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.process_server.*;
import vidar.server.threadpool.*;
import vidar.game.model.*;

public class PcRoutineTasks implements Runnable
{
	ScheduledFuture<?> schedulor;
	private int taskInterval = 0;
	private SessionHandler handle;
	private PcInstance pc;
	
	private int hprTimer = 0, mprTimer = 0;
	
	public PcRoutineTasks (PcInstance _pc) {
		handle = _pc.getHandle () ;
		this.pc = _pc;
		taskInterval = 1000; //1S
	}
	
	public void run () {
		try {
			hpResume ();
			mpResume ();
			tick ();
			
			if (pc.battleCounter > 0) {
				pc.battleCounter--;
			}
			
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	
	private void hpResume () {		
		if (pc.battleCounter > 0) { //戰鬥狀態
			if (hprTimer < 16) {
				hprTimer++;
			} else {
				hprTimer = 0;
				if ((pc.hp + pc.basicParameters.hpR) > pc.getMaxHp ()) {
					pc.hp = pc.getMaxHp ();
				} else {
					pc.hp += pc.basicParameters.hpR;
				}
				handle.sendPacket (new UpdateHp (pc.hp, pc.getMaxHp ()).getRaw ());
			}
		} else { //一般狀態
			if (hprTimer < 4) {
				hprTimer++;
			} else {
				hprTimer = 0;
				if ((pc.hp + pc.basicParameters.hpR) > pc.getMaxHp ()) {
					pc.hp = pc.getMaxHp ();
				} else {
					pc.hp += pc.basicParameters.hpR;
				}
				
				handle.sendPacket (new UpdateHp (pc.hp, pc.getMaxHp ()).getRaw ());
			}
		}
	}
	
	private void mpResume () {
		if (pc.battleCounter > 0) {
			if (mprTimer < 16) {
				mprTimer++;
			} else {
				if ((pc.mp + pc.basicParameters.mpR) > pc.getMaxMp ()) {
					pc.mp = pc.getBaseMaxMp ();
				} else {
					pc.mp += pc.basicParameters.mpR;
				}
				mprTimer = 0;
			}
		} else {
			if (mprTimer < 4) {
				mprTimer++;
			} else {
				if ((pc.mp + pc.basicParameters.mpR) > pc.getMaxMp ()) {
					pc.mp = pc.getBaseMaxMp ();
				} else {
					pc.mp += pc.basicParameters.mpR;
				}
				mprTimer = 0;
			}
		}
	}
	
	private void tick () {
		handle.sendPacket (new GameTime().getRaw());
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 0, taskInterval);
	}
	
	public void stop () {
		schedulor.cancel (true);
	}
}
