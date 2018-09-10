package vidar.game.model.npc;

import vidar.types.*;
import vidar.game.model.*;
import vidar.game.template.*;

public class NpcInstance extends Model
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
	}
}
