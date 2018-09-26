package vidar.game.model.item.scroll;

import vidar.types.*;
import vidar.server.*;
import vidar.server.process_server.*;
import vidar.game.model.*;
import vidar.game.model.item.*;

public class UseScroll
{
	PcInstance pc;
	SessionHandler Handle;
	public UseScroll (PcInstance _pc, ItemInstance i, int _targetUuid) {
		pc = _pc;
		Handle = _pc.getHandle ();
			
		if (i.id == 40100) { //順捲
			while (!checkScrollDelay (i) ) {
				try {
					Thread.sleep (500);
				} catch (Exception e) {
					e.printStackTrace ();
				} 
			}
			TeleportScroll ();
			
		} else if (i.id== 40079) { //回捲 
			
		} else if (i.id == 40088) { //變形卷軸
			
		} else if (i.id == 40126) { //鑑定卷軸
			ItemInstance t = pc.findItemByUuid (_targetUuid) ;
			if (t != null) {
				//byte[] packet = new ItemIdentify (t).getRaw () ;
				//fix
				//Handle.SendPacket (packet) ;
			}

						
		} else {
			System.out.printf ("對%d使用卷軸:%s %d\n", _targetUuid, i.getName (), i.id) ;
		}
	}
	
	public boolean checkScrollDelay (ItemInstance i) {
		boolean res;
		long nowTime = System.currentTimeMillis ();
		
		if (pc.getItemDelay (i.id, nowTime) > i.delayTime) {
			pc.setItemDelay (i.id, nowTime);
			res = true;
		} else {
			res = false;
		}
		
		return res;
	}
	
	private void TeleportScroll () {
		Location dest;
		dest = pc.map.getRandomLocation ();		
		new Teleport (pc, dest, true);
		dest = null;
	}
}
