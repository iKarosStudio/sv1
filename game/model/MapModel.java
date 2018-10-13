package vidar.game.model;

import vidar.config.*;
import vidar.types.*;
import vidar.game.*;
import vidar.game.map.*;
import vidar.game.skill.*;

/* 地圖上一個顯示物件的抽象表示 */
public abstract class MapModel
{
	/* 通用唯一辨識編號 */
	public int uuid;
	
	/* 位置敘述 */
	public Location location;
	public int heading;
	
	/* 外型敘述 */
	public int gfx;

	/* 0:small 1:large */
	public int size;
	
	/* 照明範圍 */
	public int lightRange;
	
	/* 閒置動作 */
	public int actId;
	
	/* 顯示名稱 */
	public String name;
	public int lawful;
	
	/* 顯示稱號 */
	public String title;
	
	/* 血盟敘述 */
	public int clanId;
	public String clanName;
	
	/* 額外狀態標示 */
	public int status;
	public int moveSpeed;
	
	/* 外顯等級, hp */
	public int level;
	public int levelScale;
	public int hpScale = 0xFF;
	
	/* exp or amount? */
	public int exp;
	
	public static final int MODEL_TYPE_MONSTER = 0;
	public static final int MODEL_TYPE_NPC = 1;
	public static final int MODEL_TYPE_DOOR = 2;
	public static final int MODEL_TYPE_PC = 3;
	public static final int MODEL_TYPE_ITEM = 4;
	
	/* 模組種類辨識(取代java instanceof 效能) */
	public int modelType;
	
	//共用功能
	public VidarMap getCurrentMap () {
		return Vidar.getInstance ().getMap (location.mapId);
	}

	public boolean isInsight (Location pos) {
		try {
			if (location.mapId != pos.mapId) {
				return false;
			} else {
				return getDistance (pos.p.x, pos.p.y) < Configurations.SIGHT_RAGNE;
			}
		} catch (Exception e) {
			return false;	
		}
	}
	
	public int getDistance (int x, int y) {
		int dx = Math.abs (x - location.p.x);
		int dy = Math.abs (y - location.p.y);
		
		return (int) Math.sqrt (Math.pow (dx, 2) + Math.pow (dy, 2) );		
	}
	
	public int getDirection (int x, int y) {
		byte directionFace = 0;

		if (location.p.x == x && location.p.y == y) {
			return heading;
		} else {
			if ((x != location.p.x) && (y != location.p.y)) {
				directionFace |= 0x01;
			}
			
			if (((x > location.p.x) && !(y < location.p.y)) || ((x < location.p.x) && !(y > location.p.y))) {
				directionFace |= 0x02;
			}
			
			if (((x == location.p.x) && (y > location.p.y)) || (x < location.p.x)) {
				directionFace |= 0x04;
			}
		}
		
		return directionFace & 0x0FF;
	}
	
	public static final int STATUS_POISON = 0x01; //中毒
	public static final int STATUS_INVISIBLE = 0x02;//隱身
	public static final int STATUS_PC = 0x04; //一般玩家
	public static final int STATUS_FROZEN = 0x08; //冷凍
	public static final int STATUS_BRAVE = 0x10; //勇水
	public static final int STATUS_ELF_BRAVE = 0x20; //精餅
	public static final int STATUS_FASTMOVE = 0x40; //高速移動用
	public static final int STATUS_GHOST = 0x80; //幽靈模式
	
	public boolean isPoison () {
		return (status & STATUS_POISON) > 0;
	}
	
	public boolean isInvisible () {
		return (status & STATUS_INVISIBLE) > 0;
	}
	
	public boolean isPc () {
		return (status & STATUS_PC) > 0;
	}
	
	public boolean isFreeze () {
		return (status & STATUS_FROZEN) > 0;
	}
	
	public boolean isBraveSpeed () { //x1.33
		return (status & STATUS_BRAVE) > 0;
	}
	
	public boolean isElfBraveSpeed () { //x1.15
		return (status & STATUS_ELF_BRAVE) > 0;
	}
	
	public boolean isFastMove () {
		return (status & STATUS_FASTMOVE) > 0;
	}
	
	public boolean isGhost () {
		return (status & STATUS_GHOST) > 0;
	}
	
	public int getActId () {
		return actId;
	}
	
	//抽象功能
	public abstract void damage (NormalAttack atk);
	public abstract void updateModel ();
	public abstract boolean isParalyzed ();//{
		//return hasSkillEffect (STATUS_POISON_PARALYZED) || hasSkillEffect (STATUS_CURSE_PARALYZED);
	//}
	public abstract boolean hasSkillEffect (int skillId);// {
		//return skillBuffs.hasSkillEffect (skillId);
	//}
}
