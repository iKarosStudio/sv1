package vidar.game.model;

/* 可移動的物件功能 */
public interface Moveable
{
	public void move (int tmpX, int tmpY, int heading);
	public void moveToHeading (int heading);
}
