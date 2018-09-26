package vidar.game.skill;

import java.util.*;
import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.process_server.*;
import vidar.game.model.*;

/*
 * 所有技能共用的計時器
 * 新增技能效果要新增在HashMap Effect裡面
 */
public class SkillEffectTimer extends TimerTask implements Runnable
{
	private final Timer timer = new Timer ("SkillEffectTimer") ;
	private PcInstance pc;
	private SessionHandler handle;
	public ConcurrentHashMap<Integer, SkillEffect> effects = null;
	
	
	public SkillEffectTimer (PcInstance pc) {
		effects = new ConcurrentHashMap<Integer, SkillEffect> () ;
		this.pc = pc;
		handle = pc.getHandle ();
	}
	
	public void run () {
		if (!effects.isEmpty ()) {
			effects.forEach ((Integer SkillId, SkillEffect Buff)->{
				if (Buff.RemainTime == 0xFFFF) {
					return;
				} else if (Buff.RemainTime > 0) {
					Buff.RemainTime--;
				} else {
					//Stop buff
					stopSkill (SkillId);
					effects.remove (SkillId);
				}
			});
		}
	}
	
	public void updateSkillEffects () {
		effects.forEach ((Integer skillId, SkillEffect effects)->{
			if (skillId == SkillId.STATUS_HASTE) {
				byte[] data = new SkillHaste (pc.uuid, pc.moveSpeed, effects.RemainTime).getRaw () ;
				handle.sendPacket (data);
				
			} else if (skillId == SkillId.STATUS_BRAVE) {
				byte[] data = new SkillBrave (pc.uuid, pc.braveSpeed, effects.RemainTime).getRaw () ;
				handle.sendPacket (data);
				
			} else {
				//其他持續性技能
			}
		}) ;
	}
	
	public void loadBuffs () {
	}
	
	public void saveBuffs () {
	}
	
	public boolean hasSkillEffect (int skillId) {
		return effects.containsKey (skillId);
	}
	
	public void start () {
		timer.scheduleAtFixedRate (this, 0, 1000) ; //1S interval
	}
	
	public void stop () {
		timer.cancel () ;
	}
	
	public void stopSkill (int skillId) {
		if (skillId == SkillId.STATUS_HASTE) {
			pc.moveSpeed = 0;
			handle.sendPacket (new SkillHaste (pc.uuid, 0, 0).getRaw () ) ;
			
		} else if (skillId == SkillId.STATUS_BRAVE) {
			pc.braveSpeed = 0;
			handle.sendPacket (new SkillBrave (pc.uuid, 0, 0).getRaw () ) ;
			
		}
	}
}
