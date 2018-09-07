package vidar.server;

import java.net.*;
import java.lang.Thread;
import java.lang.management.*;
import com.sun.management.OperatingSystemMXBean;

import vidar.config.*;
import vidar.game.*;

public class ManageService extends Thread
{
	private static ManageService instance;
	private ServerSocket ServiceSocket;
	
	Vidar vidar;
	
	ThreadMXBean mana = ManagementFactory.getThreadMXBean();
	OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean () ;
	
	/*
	 * 建立連接用session
	 */
	public void run () {		
		while (true) {
			vidar.cpuUsage = (float) osmb.getProcessCpuLoad () * 100;
			vidar.threadCount = mana.getThreadCount ();
			 //移出管理介面
			String t = String.format ("CPU:%2.1f%% 執行緒:%d 使用記憶體:%1.1f MB",
				osmb.getProcessCpuLoad () * 100,
				mana.getThreadCount (),
				(float) vidar.memUsage / (1024 * 1024)
			) ;
			try {
				Socket sock = ServiceSocket.accept ();
				//String ClientIp = Sock.getInetAddress ().getHostAddress ();
				
				/*
				 * 開始管理服務程式
				 */
				
			} catch (SocketTimeoutException e) {
				//it's ok
				
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}
	}
	
	public static ManageService getInstance () {
		if (instance == null) {
			instance = new ManageService ();
		}
		return instance;
	}
	
	public ManageService () {
		vidar = Vidar.getInstance ();
		
		/*
		 * 初始化端口監聽
		 */
		System.out.printf ("Management Port %d binding...", Configurations.MANAGE_PORT);
		try {
			ServiceSocket = new ServerSocket (Configurations.MANAGE_PORT);
			ServiceSocket.setSoTimeout (3000) ; //10s accept timeout
			System.out.printf ("success\n") ;
			
			this.setName ("MANAGEMENT SERVICE");
			
		} catch (Exception e) {
			System.out.printf ("fail\n") ;
			System.out.printf ("[!] TCP/IP Listening fault->%s\n", e.getMessage () ) ;
			e.printStackTrace () ;
			System.exit (666) ;
			
		}
	}
}
