package vidar.game.model;

import java.util.*;

import vidar.types.*;
import vidar.game.*;
import vidar.game.map.*;

public class DoorInstance
{
	/*
	 * Direction用location.heading標示
	 */
	public static final int STATUS_DOOR_CLOSE = 29;
	public static final int STATUS_DOOR_OPEN = 28;
	
	public int uuid;
	public int gfx;
	
	public int actionCode = STATUS_DOOR_CLOSE;
	public boolean isOpened = false;
	public boolean isKeeper = false;
	public Location location = new Location ();
	public Location entrance = new Location ();
	public int heading;
	public int hp;
	public int size;
	public int keyId; /* 需要特定道具才可以打開 */
	public int castle;
	public int order;
	public String note;
	
	public boolean isVisible = true;
	
	public DoorInstance (int _uuid, String _note, int _gfx, int _x, int _y, int _mapId, int _direction, int _entranceX, int _entranceY, int _hp, boolean _keeper, int _keyId, int _size, int _castel, int _order) {
		uuid = _uuid;
		note = _note;
		gfx = _gfx;
		location.point.x = _x;
		location.point.y = _y;
		location.mapId = _mapId;
		//location.Heading = direction;
		heading = _direction;
		entrance.point.x = _entranceX;
		entrance.point.y = _entranceY;
		hp = _hp;
		isKeeper = _keeper;
		keyId = _keyId;
		size = _size;
		castle = _castel;
		order = _order;
	}
	
	public void boardcastPcInsight (byte[] packet) {
		VidarMap map = Vidar.getInstance ().getMap (location.mapId) ;
		List<PcInstance> pcs = map.getPcsInsight (location.point);
		for (PcInstance p : pcs) {
			p.getHandle ().sendPacket (packet);
		}
		pcs = null;
	}
	
	public void open () {
		isOpened = true;
		actionCode = STATUS_DOOR_OPEN;
	}
	
	public void close () {
		isOpened = false;
		actionCode = STATUS_DOOR_CLOSE;
	}
	
	public int getDistance (int x, int y) {
		int dx = Math.abs (x - location.point.x);
		int dy = Math.abs (y - location.point.y);
		
		return (int) Math.sqrt (Math.pow (dx, 2) + Math.pow (dy, 2) );		
	}
}
