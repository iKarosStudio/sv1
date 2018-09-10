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
	private int TaskInterval = 0;
	private PcInstance pc;
	private SessionHandler Handle;
	
	private int status = 0;
	
	public HpMonitor (PcInstance _pc) {
		this.pc = _pc;
		Handle = _pc.getHandler () ;
		TaskInterval = 1000;
	}
	
	public void run () {
		
		int div10 = 0;
		
		//System.out.printf ("%s hp\n", Pc.Name) ;
		try {
			//if (Pc.getHp < Pc.getMaxHp () ) {
				//Pc.Hp++;
				//Handle.SendPacket (new NodeStatus (Pc).getRaw () ) ;
				Handle.sendPacket (new UpdateHp (pc.hp, pc.getMaxHp ()).getRaw ());
			//}
		} catch (Exception e) {
			//s.cancel (true) ;
			//e.printStackTrace () ;
			//e.printStackTrace () ;
			//System.out.printf ("HP Monitor %s\n", e.toString () ) ;
		}
	}
	
	public void start () {
		schedulor = KernelThreadPool.getInstance ().ScheduleAtFixedRate (this, 1000, TaskInterval);
		//t.scheduleAtFixedRate (this, 1000, TaskInterval) ;
	}
	
	public void stop () {
		schedulor.cancel (true);
	}
}
