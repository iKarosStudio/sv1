package vidar.game.model.npc;

import vidar.types.*;

import java.util.List;

import vidar.game.model.*;
import vidar.game.template.*;
import vidar.game.skill.*;

public class NpcInstance extends ActiveModel implements BoardcastNode
{
	public String nameId;
	
	public NpcInstance (NpcTemplate _template) {
		uuid = _template.uuid;
		gfx = _template.gfx;
		name = _template.name;
		nameId = _template.nameId;
		level = _template.level;
		heading = _template.heading;
		
		location = new Location ();
		//updateCurrentMap ();
	}

	@Override
	public int getStr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCon () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDex () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWis () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCha () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntel () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSp () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAc () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxHp () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxMp () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHpr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMpr () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDmgModify () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpModify () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHitModify () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBowHitModify () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDmgReduction () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWeightReduction () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void giveItem () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recvItem () {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boardcastPcInsight (byte[] packet) {
		List<PcInstance> pcs = getCurrentMap ().getPcsInsight (location.p);
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
	public void pickItem (int uuid, int count, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropItem (int uuid, int count, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void damage (NormalAttack atk) {
		// TODO Auto-generated method stub
		
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
}
