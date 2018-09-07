package vidar.game.model;

import vidar.config.*;
import vidar.types.*;
import vidar.game.*;
import vidar.game.map.*;

public class Model
{
	/* 通用辨識編號 */
	public int uuid;
	
	/* 世界位置 */
	public Location location;
	public int heading;
	public VidarMap map = null;
	
	/* 外型設定 */
	public int gfx;
	public int gfxTemp;
	public boolean isVisible = true;
	
	/* 狀態敘述 */
	public int status;
	public int moveSpeed;
	public int braveSpeed;
	public AbilityParameter basicParameters;
	public AbilityParameter skillParameters;
	
	public String name;
	public String title;
	public int lawful;
	
	public int level;
	
	public volatile int hp;
	public volatile int mp;
	public boolean isDead;
	
	public boolean isInsight (Location pos) {
		try {
			if (location.mapId != pos.mapId) {
				return false;
			}
			
			return getDistance (pos.point.x, pos.point.y) < Configurations.SIGHT_RAGNE;
		} catch (Exception e) {
			return false;
			
		}
	}
	
	public void updateCurrentMap () {
		this.map = Vidar.getInstance ().getMap (location.mapId);
	}
	
	/*
	 * 自身對p(x, y)的距離
	 */
	public int getDistance (int x, int y) {
		int dx = Math.abs (x - location.point.x) ;
		int dy = Math.abs (y - location.point.y) ;
		
		return (int) Math.sqrt (Math.pow (dx, 2) + Math.pow (dy, 2) );		
	}
	
	public int getDirection (int x, int y) {
		byte directionFace = 0;
		
		//int Dist = Utility.getDistance (PosX, PosY, x, y) ;
		
		//if (Dist < 2) {
			if (location.point.x == x && location.point.y == y) {
				return heading;
			} else {
				if ((x != location.point.x) && (y != location.point.y)) {
					directionFace |= 0x01;
				}
				
				if (((x > location.point.x) && !(y < location.point.y)) || ((x < location.point.x) && !(y > location.point.y)) ) {
					directionFace |= 0x02;
				}
				
				if (((x == location.point.x) && (y > location.point.y)) || (x < location.point.x)) {
					directionFace |= 0x04;
				}
			}
		//}
		return directionFace & 0x0FF;
	}
}
