package vidar.server.process_server;

import vidar.types.*;
import vidar.server.*;
import vidar.game.*;
import vidar.game.map.*;
import vidar.game.model.*;

public class Teleport
{
	public Teleport (PcInstance pc, Location dest, boolean useVirtualEffect) {
		SessionHandler handle = pc.getHandle ();
		
		if (pc.location.mapId != dest.mapId) {
			pc.getCurrentMap ().removePc (pc);
			pc.location.mapId = dest.mapId;
			
			VidarMap map = Vidar.getInstance ().getMap (pc.location.mapId);
			map.addPc (pc);
			pc.saveSkillEffects ();
		}
		
		pc.location.p.x = dest.p.x;
		pc.location.p.y = dest.p.y;
		
		byte[] mapIdPacket = new MapId (pc.location.mapId).getRaw ();
		byte[] pcPacket = new ModelPacket (pc).getRaw ();
		
		if (useVirtualEffect) {
			byte[] effect_packet = new VisualEffect (pc.uuid, 169).getRaw ();
			handle.sendPacket (effect_packet);
			pc.boardcastPcInsight (effect_packet);
			
			try {
				Thread.sleep (700) ;
			} catch (Exception e) {
				e.printStackTrace (); 
			}
		}
		
		pc.pcsInsight.clear ();
		pc.modelsInsight.clear ();
		
		//update Skills
		pc.skillBuffs.updateSkillEffects ();
		
		handle.sendPacket (mapIdPacket);
		handle.sendPacket (pcPacket);
		handle.sendPacket (new UpdateModelGfx (pc.uuid, pc.getWeaponGfx ()).getRaw ());
		if (pc.location.mapId != dest.mapId) {
			pc.loadSkillEffects ();
		}
	
		pc.boardcastPcInsight (pcPacket) ;
	}
}
