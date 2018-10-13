package vidar.game.model;

/* 可戰鬥物件功能 */
public interface Fightable
{
	public void attack (MapModel target);
	public void attack (int targetUuid, int targetX, int targetY);
}
