package vidar.server.process_client;

import vidar.server.*;
import vidar.server.packet.*;
import vidar.server.process_server.*;
import vidar.game.map.*;
import vidar.game.model.*;

public class Move {
	public Move (SessionHandler handle, byte[] data) {
		PacketReader packetReader = new PacketReader (data);
		PcInstance pc = handle.getPc ();
		
		packetReader.readWord ();//int tmpX = packetReader.readWord () ; //pseudo
		packetReader.readWord ();//int tmpY = packetReader.readWord () ; //pseudo
		int heading = packetReader.readByte () ;
		
		VidarMap map = pc.map;
		
		/* Server config 參數為3的情況下, 使用腳色原本的座標操作, 封包的座標無效 */
		int x = pc.location.point.x;
		int y = pc.location.point.y;
		
		map.setAccessible (pc.location.point.x, pc.location.point.y, true) ;
		
		switch (heading) {
		case 0:
		case 73:
			heading = 0; y--;
			break;
			
		case 1:
		case 72:
			heading = 1; x++; y--;
			break;
			
		case 2:
		case 75:
			heading = 2; x++;
			break;
			
		case 3:
		case 74:
			heading = 3; x++; y++;
			break;
			
		case 4:
		case 77:
			heading = 4; y++;
			break;	
			
		case 5:
		case 76:
			heading = 5; x--; y++;
			break;
			
		case 6:
		case 79:
			heading = 6; x--;
			break;
			
		case 7:
		case 78:
			heading = 7; x--; y--;
			break;

		default : break;
		}
		
		/* 防穿檢查
		if (!Pc.CurrentMap.isPassable (x, y) ) {
			//System.out.printf ("next p(%5d, %5d) = 0x%02x is not passable\n", x, y, Pc.CurrentMap.getTile (x, y) ) ;
			//return ;
		}
		*/
		
		/*
		 * 廣播移動訊息
		 */
		pc.boardcastPcInsight (new ModelMove (pc.uuid, pc.location.point.x, pc.location.point.y, heading).getRaw ());
		
		/* 檢查是否需要傳送位置 */
		if (pc.map.isInTpLocation (x, y) ) {
			pc.skillBuffs.saveBuffs ();
			new Teleport (pc, pc.map.getTpDestination (x, y), false);
			return ;
		}
		
		/* 更新自身位置 */
		pc.location.point.x = x;
		pc.location.point.y = y;
		pc.heading = heading;
		map.setAccessible (pc.location.point.x, pc.location.point.y, false);
	}
}
