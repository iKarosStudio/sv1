package vidar.game.routine_task;

import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.process_server.*;
import vidar.server.threadpool.*;
import vidar.game.model.*;

public class MpMonitor implements Runnable
{
	//private final Timer t = new Timer () ;
	ScheduledFuture<?> schedulor;
	private int taskInterval = 0;
	private PcInstance pc;
	private SessionHandler handle;
	
	public MpMonitor (PcInstance _pc) {
		this.pc = _pc;
		handle = _pc.getHandle ();
		taskInterval = 1000;
	}
	
	public void run () {
		try {
			handle.sendPacket (new UpdateMp (pc.mp, pc.getMaxMp ()).getRaw ());
		} catch (Exception e) {
			System.out.printf ("MP Monitor %s\n", e.toString ());
		}
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 1000, taskInterval);
		//t.scheduleAtFixedRate (this, 1000, TaskInterval) ;
	}
	
	public void stop () {
		schedulor.cancel (true);
	}
}
