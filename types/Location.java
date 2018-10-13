package vidar.types;

/* 表示一個Model在世界中的位置 */
public class Location
{
	/* 所在地圖編號 */
	public int mapId;
	
	/* 所在地圖座標 */
	public Coordinate p;
	
	public Location () {
		mapId = 0;
		p = new Coordinate (0, 0) ;
	}
	
	public Location (int mapid, int x, int y) {
		mapId = mapid;
		p = new Coordinate (x, y);
	}
}
