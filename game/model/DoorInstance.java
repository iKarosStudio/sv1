package vidar.game.model;

import java.util.*;

import vidar.types.*;
import vidar.game.*;
import vidar.game.map.*;
import vidar.game.skill.*;

public class DoorInstance extends MapModel implements BoardcastNode
{
	public static final int STATUS_DOOR_CLOSE = ModelActionId.OFF_CLOSE_WEST;
	public static final int STATUS_DOOR_OPEN = ModelActionId.ON_OPEN_SOUTH;
	
	public boolean isOpened = false;
	public boolean isKeeper = false;
	//public Location location = new Location ();
	public Location entrance = new Location ();
	
	public int hp;
	public int size;
	public int keyId; //要求特定itemId才可以打開
	public int castle;
	public int order;
	public String note;
	
	public boolean isVisible = true;
	
	public DoorInstance (int _uuid, String _note, int _gfx, int _x, int _y, int _mapId, int _direction, int _entranceX, int _entranceY, int _hp, boolean _keeper, int _keyId, int _size, int _castel, int _order) {
		uuid = _uuid;
		note = _note;
		gfx = _gfx;
		location = new Location ();
		location.p.x = _x;
		location.p.y = _y;
		location.mapId = _mapId;
		//location.Heading = direction;
		heading = _direction;
		entrance.p.x = _entranceX;
		entrance.p.y = _entranceY;
		hp = _hp;
		isKeeper = _keeper;
		keyId = _keyId;
		size = _size;
		castle = _castel;
		order = _order;
		
		actId = STATUS_DOOR_CLOSE; //default door close
	}
	
	public void open () {
		isOpened = true;
		actId = STATUS_DOOR_OPEN;
	}
	
	public void close () {
		isOpened = false;
		actId = STATUS_DOOR_CLOSE;
	}

	@Override
	public void boardcastPcInsight (byte[] packet) {
		VidarMap map = Vidar.getInstance ().getMap (location.mapId);
		List<PcInstance> pcs = map.getPcsInsight (location.p);
		for (PcInstance pc : pcs) {
			pc.getHandle ().sendPacket (packet);
		}
		pcs = null;
	}

	@Override
	public void updateModel () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isParalyzed () {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSkillEffect (int skillId) {
		return false;
	}

	@Override
	public void damage (NormalAttack atk) {
		//
	}

	@Override
	public void boardcastToAll (byte[] packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boardcastToMap (byte[] packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MapModel> getModelInsight () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PcInstance> getPcInsight () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName () {
		return null;
	}
}
