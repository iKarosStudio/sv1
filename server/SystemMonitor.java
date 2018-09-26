package vidar.server;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import com.sun.management.OperatingSystemMXBean;

import vidar.config.*;
import vidar.gui.*;
import vidar.game.Vidar;

public class SystemMonitor extends Thread implements Runnable
{
	private static SystemMonitor instance; 
	
	ThreadMXBean mana = ManagementFactory.getThreadMXBean();
	OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean ();
	
	public int threadCount;
	public float cpuUsage;
	public float memUsage;
	public String osName;
	public String cpuName = "unknown";
	public int cpuCount;
	public String pid;
	
	public void run () {
		Runtime processRuntime = Runtime.getRuntime ();
		memUsage = (processRuntime.totalMemory() - processRuntime.freeMemory()) / (1024 * 1024);
		cpuUsage = (float) osmb.getProcessCpuLoad () * 100;
		threadCount = mana.getThreadCount ();
		//System.out.printf ("cpu:%2.2f mem:%2.2f MB thread:%d\n", cpuUsage, memUsage, threadCount);
		
		if (Configurations.USE_GUI) {
			SystemTab.getInstance ().update ();
			ManagementTab.getInstance ().update ();
		}
	}
	
	//.maxMemory ()-> -Xmx
	//.totalMemory()-> -Xms
	public SystemMonitor () {
		//vidar = Vidar.getInstance ();
		
		osName = System.getProperty ("os.name") + System.getProperty ("os.arch");
		cpuCount = Runtime.getRuntime ().availableProcessors ();
		pid = ManagementFactory.getRuntimeMXBean().getName().split ("@")[0];
		
		if (osName.contains ("Windows")) {
			//
		}
		
		if (osName.contains ("Linux")) {
			//
		}
		
		//Set thread name
		this.setName ("SYSTEM MONITOR") ;
	}
	
	public static SystemMonitor getInstance () {
		if (instance == null) {
			instance = new SystemMonitor () ;
		}
		
		return instance;
	}
	
	public int getMaxMemory () {
		return (int) (Runtime.getRuntime ().maxMemory () >> 24);
	}
}
