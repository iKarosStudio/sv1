package vidar.game.routine_task;

import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.threadpool.*;
import vidar.game.model.*;

public class PcRoutineTasks implements Runnable
{
	ScheduledFuture<?> schedulor;
	private int taskInterval = 0;
	private SessionHandler handle;
	private PcInstance pc;
	
	
	public PcRoutineTasks (PcInstance _pc) {
		handle = _pc.getHandler () ;
		this.pc = _pc;
		taskInterval = 500;
	}
	
	public void run () {
		try {
			//System.out.println ("pc routine tasks..") ;
		} catch (Exception e) {
			System.out.printf ("%s Routine tasks : %s\n", pc.name, e.toString ()) ;
		}
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 0, taskInterval);
	}
	
	public void stop () {
		schedulor.cancel (true) ;
	}
}
