package vidar.game;

public class MemoryMonitor extends Thread implements Runnable
{
	private static MemoryMonitor instance; 
	private static Vidar vidar;
	
	public MemoryMonitor () {
		vidar = Vidar.getInstance () ;
		this.setName ("System Monitor") ;
	}
	
	public void run () {
		Runtime r = Runtime.getRuntime () ;
		long memUsage = r.totalMemory () - r.freeMemory () ;

		//System.out.printf ("mem : %2.2f MB\n", (float) mmm / (1024 * 1024) ) ;
		vidar.memUsage = memUsage;
	}
	
	public static MemoryMonitor getInstance () {
		if (instance == null) {
			instance = new MemoryMonitor () ;
		}
		
		return instance;
	}
}
