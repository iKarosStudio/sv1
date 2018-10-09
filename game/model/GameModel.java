package vidar.game.model;

import vidar.game.map.VidarMap;
import vidar.game.skill.SkillEffectTimer;
import vidar.types.Location;

public abstract class GameModel
{
	/* 通用辨識編號 */
	public int uuid;
	
	/* 世界位置 */
	public Location location;
	public int heading;
	public VidarMap map = null;
	
	/* 外型設定 */
	public int size; //0:small 1:large
	public int gfx;
	public int gfxTemp;
	public int actId;
	
	/* 狀態敘述 */
	public int status;
	public int battleCounter;
	
	public int moveSpeed;
	public int braveSpeed;
	public AbilityParameter basicParameters;
	public AbilityParameter skillParameters;
	
	/* Buff/Debuff 效果計時 */
	public SkillEffectTimer skillBuffs = null;
	
	public String name;
	public String title;
	public int clanId;
	public String clanName;
	public int lawful;
	
	public int level;
	public int levelInOthers;
	
	public int hpBar;
	public volatile int hp;
	public volatile int mp;
	public boolean isDead;
	
	//可以共用的method在這邊直接寫!
	
	//不可以共用的method在這邊用abstract method先做宣告; 繼承class記得一定要全部實作
	
}
