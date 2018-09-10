package vidar.server.utility;


import java.util.Random;

import vidar.config.*;
import vidar.types.*;

/*
 * 雜項計算功能
 */
public class Utility
{
	private static Random random = new Random (System.currentTimeMillis ());
	
	/*
	 * 給定起點座標及方向
	 * 回傳該方向得座標
	 */
	public static Location getNextLocation (int x, int y, int heading) {
		int px = x;
		int py = y;
		
		switch (heading) {
		case 0: py--; break;
		case 1: px++; py--; break;
		case 2: px++; break;
		case 3: px++; py++; break;
		case 4: py++; break;
		case 5: px--; py++; break;
		case 6: px--; break;
		case 7: px--; py--; break;
		default: break;
		}
		
		Location nextLocation = new Location (0, px, py) ;
		return nextLocation;
	}
	
	/*
	 * 計算兩點間所有座標
	 */
	public static Point[] calcPointsOnPath (Point p1, Point p2) {
		return null;
	}
	
	/*
	 * 計算升級增加血量
	 */
	public static int calcIncreaseHp (int type, int hp, int maxHp, int con) {
		short randomHp = 0;
		
		if (con > 15) {
			randomHp = (short) (con - 15) ;
		}
		
		if (type == 0) {
			randomHp += (short) (5 + random.nextInt (6) ) ;
			if (hp + maxHp > Configurations.MAX_HP_ROYAL) {
				randomHp = (short) (Configurations.MAX_HP_ROYAL - maxHp) ;
			}
			
		} else if (type == 1) {
			randomHp += (short) (6 + random.nextInt (7) ) ;
			if (hp + maxHp > Configurations.MAX_HP_KNIGHT) {
				randomHp = (short) (Configurations.MAX_HP_KNIGHT - maxHp) ;
			}
			
		} else if (type == 2) {
			randomHp += (short) (5 + random.nextInt (6) ) ;
			if (hp + maxHp > Configurations.MAX_HP_ELF) {
				randomHp = (short) (Configurations.MAX_HP_ELF - maxHp) ;
			}
			
		} else if (type == 3) {
			randomHp += (short) (3 + random.nextInt (4) ) ;
			if (hp + maxHp > Configurations.MAX_HP_MAGE) {
				randomHp = (short) (Configurations.MAX_HP_MAGE - maxHp) ;
			}
			
		} else if (type == 4) {
			randomHp += (short) (5 + random.nextInt (6) ) ;
			if (hp + maxHp > Configurations.MAX_HP_DARKELF) {
				randomHp = (short) (Configurations.MAX_HP_DARKELF - maxHp) ;
			}
			
		} else {
			return 0;
		}
		return randomHp;
	}
	
	/*
	 * 計算升級增加魔量
	 */
	static final int SEED[] = {
			-2, -2, -2, -2, -2, -2, -2, -2, -2, -2, //0-9
			-1, -1,  0,  0,  0,  2,  2,  2,  3,  3, //10-19
			 4,  5,  5,  5,  6,  7,  9 //20-26
	} ;
	public static int calcIncreaseMp (int type, int mp, int maxMp, int wis) {
		int randomMp = 0;
		int seed = 0;
		
		if (wis > 26) {
			seed = SEED[26];
		} else {
			seed = SEED[wis];
		}
		
		/*
		randommp = 2 + rnd.nextInt(3 + seed % 2 + (seed / 6) * 2) + seed / 2
				- seed / 6;
		*/
		//fix
		//幹破你娘在算殺小
		//這組沒改掉我跟你姓
		randomMp = 2 + random.nextInt (3 + seed % 2 + (seed / 6) * 2) + seed / 2 - seed / 6;
		
		if (type == 0) {
			if (maxMp + randomMp > Configurations.MAX_MP_ROYAL) {
				randomMp = Configurations.MAX_MP_ROYAL - maxMp;
			}
			
		} else if (type == 1) {
			if (wis == 9) {
				randomMp --;
			} else {
				randomMp = (int) (1.0 * randomMp / 2 + 0.5) ;
			}
			
			if (maxMp + randomMp > Configurations.MAX_MP_KNIGHT) {
				randomMp = Configurations.MAX_MP_KNIGHT - maxMp;
			}
		} else if (type == 2) {
			randomMp = (int) (randomMp * 1.5) ;
			if (maxMp + randomMp > Configurations.MAX_MP_ELF) {
				randomMp = Configurations.MAX_MP_ELF - maxMp;
			}
			
		} else if (type == 3) {
			randomMp = (int) (randomMp * 2.0) ;
			if (maxMp + randomMp > Configurations.MAX_MP_MAGE) {
				randomMp = Configurations.MAX_MP_MAGE - maxMp;
			}
			
		} else if (type == 4) {
			randomMp = (int) (randomMp * 1.5) ;
			if (maxMp + randomMp > Configurations.MAX_MP_DARKELF) {
				randomMp = Configurations.MAX_MP_DARKELF - maxMp;
			}
			
		} else {
			return 0;
		}
		return randomMp;
	}
	
	private static final int MR_K[] = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, //0-9
			0, 0, 0, 0, 0, 3, 3, 6,10,15, //10-19
			21,28,37,47,50 //20-24
	} ;
	public static int calcMr (int type, int level, int wis) {
		int mr = 0;
		int k = 0;
		if (type == 0) {
			mr = 10;
		} else if (type == 1) {
			mr = 0;
		} else if (type == 2) {
			mr = 25;
		} else if (type == 3) {
			mr = 10;
		} else if (type == 4) {
			mr = 10;
		}
		
		if (wis > 24) {
			k = MR_K[24];
		} else {
			k = MR_K[wis];
		}
		
		mr += k + (level >> 1) ;
		
		return mr;
	}
	
	private static final int SP_K[] = {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, 0, //0-9
			 0,  0,  1,  1,  1,  2,  2,  2 //10-17
	} ;
	public static int calcSp (int type, int level, int intel) {
		int sp = 0;
		int k = 0;
		
		if (type == 0) {
			sp = level / 10;
		} else if (type == 1) {
			sp = level / 50;
		} else if (type == 2) {
			sp = level / 8;
		} else if (type == 3) {
			sp = level / 4;
		} else if (type == 4) {
			sp = level / 12;
		}
		
		if (intel > 17) {
			k = intel - 15;
		} else {
			k = SP_K[intel];
		}
		
		return sp + k;
	}
	
	private static final int DEX_K[] = {
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, //0~9 DEX
			7, 7, 7, 6, 6, 6, 5, 5, 4} ;  //10~18 DEX
	public static int calcAcBonusFromDex (int level, int dex) {
		int bonus = 10;
		int k = 0;
		if (dex > 18) {
			k = DEX_K[18];
		} else {
			k = DEX_K[dex];
		}
		
		bonus -= level / k;
		return bonus;
	}
}
