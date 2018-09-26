package vidar.server.igcp;

import vidar.types.*;
import vidar.server.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

public class InGameCommandParser
{
	SessionHandler handle;
	PcInstance pc;
	String text;
	
	public InGameCommandParser (SessionHandler _handle, String _text) {
		handle = _handle;
		pc = handle.getPc ();
		text = _text;
	}
	
	public void setText (String _text) {
		text = _text;
	}
	
	public boolean parse () {
		boolean isValid = false;
		
		/*
		if (talkText.startsWith (".help") ) {
			byte[] packet = new SystemMessage (String.format ("RD開發資訊")).getRaw ();
			handle.sendPacket (packet);
			return;
		}
		
		if (talkText.startsWith (".tile") ) {
			String[] splitText = talkText.split (" ") ;
			if (splitText.length == 3) {
				int t = pc.map.getTile (Integer.valueOf (splitText[1]), Integer.valueOf (splitText[2]) ) ;
				String msg = String.format ("Tile(%s,%s):0x%02X", splitText[1], splitText[2], t) ; 
				handle.sendPacket (new SystemMessage (msg).getRaw () ) ;
			} else {
				String msg = String.format ("Tile(%d:%5d, %5d):0x%02X", pc.map.mapId, pc.location.point.x, pc.location.point.y, pc.map.getTile (pc.location.point.x, pc.location.point.y) ) ;
				handle.sendPacket (new SystemMessage (msg).getRaw () ) ;
			}
			return ;
		}
		
		if (talkText.startsWith (".tp") ) {
			String[] tpLocation = talkText.split (" ") ;
			if (tpLocation.length == 4) {
				int destMapId = Integer.valueOf (tpLocation[1]) ;
				int destX = Integer.valueOf (tpLocation[2]) ;
				int destY = Integer.valueOf (tpLocation[3]) ;
				
				
				String msg = String.format ("Teleport to location(%d:%5d,%5d)\n", destMapId, destX, destY) ;
				byte[] packet = new SystemMessage (msg).getRaw () ;
				handle.sendPacket (packet) ;
				
				//Location Dest = new Location (DestMapId, DestX, DestY, Pc.heading) ;
				Location dest = new Location (destMapId, destX, destY);
				new Teleport (pc, dest, true) ;
				
				return ;
			}
		}
		*/
		return isValid;
	}
}
