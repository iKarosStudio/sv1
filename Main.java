package vidar;

import vidar.config.*;
import vidar.server.*;
import vidar.server.threadpool.*;
import vidar.server.database.*;
import vidar.game.*;
import vidar.gui.*;

public class Main
{
	static final String SLOG[] = {
		"---------------------------------------------------------------",
		"----  Wer mit Ungeheuern kämpft,                           ----",
		"----  mag zusehn, dass er nicht dabei zum Ungeheuer wird.  ----",
		"----  Und wenn du lange in einen Abgrund blickst,          ----",
		"----  blickt der Abgrund auch in dich hinein.              ----",
		"----                          <Jenseits von Gut und Böse>  ----",
		"----                                 Nietzsche, Friedrich  ----",
		"---------------------------------------------------------------",
		"                                                               ",
		"      Sponsor us with BTC :                                    ",
		"                         1GELjCWcxVZN48w5Ns9VeBzj9ZjL6PTtwR    ",
		"                                                               ",
		"---------------------------------------------------------------" 
	};

	public static void showSlog () {
		for (String s : SLOG) {
			System.out.println (s);
		}
	}
	
	public static void main (String[] parameters) throws InterruptedException {		
		//showSlog ();
		System.out.printf ("KERNEL AUTHOR:%s\n", Configurations.AUTHOR);
		System.out.printf ("SERVER OS:%s-%s\n", System.getProperty ("os.name"), System.getProperty ("os.arch"));
		
		/* 載入參數設定 */
		ConfigurationLoader.getInstance ();
		
		/* 建立GUI管理介面 */
		if (Configurations.USE_GUI) {
			GuiMain.getInstance ();
		}
		
		/* 建立資料庫連結 */
		HikariCP.getInstance ();
		
		/* 建立系統執行緒池 */
		KernelThreadPool.getInstance ();
		ServiceThreadPool.getInstance ();
		
		/* 建立遊戲世界 */
		Vidar vidar = Vidar.getInstance ();
		vidar.initialize ();
		
		/* 建立客戶用TCP/IP端口 */
		MainService gameService = MainService.getInstance ();
		gameService.start ();
		
		/* 建立管理用TCP/IP端口 */
		//ManageService manageService = ManageService.getInstance ();
		//manageService.start ();
		
		SystemMonitor systemMonitor = SystemMonitor.getInstance ();
		KernelThreadPool.getInstance ().ScheduleAtFixedRate (systemMonitor, 0, 1000);
		
		
		/* 實作賭場系統 */
		if (Configurations.CASINO) {
			//
		}
		
		/* Display GameRate setting */
		
		/* Server platform info */
		System.out.printf ("cpu core  :%d\n", SystemMonitor.getInstance ().cpuCount);
		System.out.printf ("system_pid:%s\n", SystemMonitor.getInstance ().pid);
		System.out.printf ("run on:%s\n", System.getProperty("java.vm.name"));
		
		Runtime.getRuntime().addShutdownHook (new Shutdown ());
		
		gameService.loginEnable ();
		
		/* 本地控制台 */
		while (true) {
			Thread.sleep (120000); //120S
		}
	}
}
