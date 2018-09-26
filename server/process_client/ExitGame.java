package vidar.server.process_client;

import vidar.server.*;
import vidar.game.model.*;

public class ExitGame
{
	public ExitGame (SessionHandler handle, byte[] data) {
		if (handle.account != null && 
			handle.account.activePc != null) {
			PcInstance pc = handle.account.activePc;
			try {
				if (handle.account != null && handle.account.activePc != null) {
					pc.isExit = true;
					pc.offline ();
					
					/* 20180411
					 * 正常離開遊戲, 要多加一個旗標判斷
					 * 否則在sessionhandler exception捕捉斷線會kick()重複執行
					 */
					
					handle.disconnect ();
				}
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}	
	}
}
