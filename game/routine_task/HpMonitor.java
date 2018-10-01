package vidar.game.routine_task;

import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.threadpool.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

public class HpMonitor implements Runnable
{
	//private final Timer t = new Timer () ;
	ScheduledFuture<?> schedulor;
	private int taskInterval = 0;
	private PcInstance pc;
	private SessionHandler handle;
	private int prevHp;
	
	public HpMonitor (PcInstance _pc) {
		this.pc = _pc;
		handle = _pc.getHandle () ;
		prevHp = pc.hp;
		taskInterval = 33; //30hz updating
	}
	
	public void run () {
		
		//System.out.printf ("%s hp\n", Pc.Name) ;
		try {
			if (prevHp != pc.hp) {
				handle.sendPacket (new UpdateHp (pc.hp, pc.getMaxHp ()).getRaw ());
			}
			prevHp = pc.hp;
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 500, taskInterval);
		//t.scheduleAtFixedRate (this, 1000, TaskInterval) ;
	}
	
	public void stop () {
		schedulor.cancel (true);
	}
}
