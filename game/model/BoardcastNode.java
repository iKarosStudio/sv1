package vidar.game.model;

import java.util.*;

/* 封包廣播節點功能 */
public interface BoardcastNode
{
	//public void boardcastPcInsight (byte[] packet);
	//java 8後可用default關鍵字預設實作
	public void boardcastPcInsight (byte[] packet);
	public void boardcastToAll (byte[] packet);
	public void boardcastToMap (byte[] packet);
	
	public List<MapModel> getModelInsight ();
	public List<PcInstance> getPcInsight ();
}
