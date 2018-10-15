package vidar.game.skill;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import vidar.server.*;
import vidar.server.database.*;
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
	private ConcurrentHashMap<Integer, SkillEffect> effects = null;
	
	
	public SkillEffectTimer (PcInstance pc) {
		effects = new ConcurrentHashMap<Integer, SkillEffect> () ;
		this.pc = pc;
		handle = pc.getHandle ();
	}
	
	public void run () {//每一秒定時檢查技能存在時間
		if (!effects.isEmpty ()) {
			effects.forEach ((Integer SkillId, SkillEffect Buff)->{
				if (Buff.remainTime == 0xFFFF) {
					return;
				} else if (Buff.remainTime > 0) {
					Buff.remainTime--;
				} else {
					//Stop buff
					removeSkill (SkillId);
					effects.remove (SkillId);
				}
			});
		}
	}
	
	public void updateSkillEffects () {
		effects.forEach ((Integer skillId, SkillEffect effects)->{
			if (skillId == SkillId.STATUS_HASTE) {
				byte[] data = new SkillHaste (pc.uuid, pc.moveSpeed, effects.remainTime).getRaw () ;
				handle.sendPacket (data);
				
			} else if (skillId == SkillId.STATUS_BRAVE) {
				byte[] data = new SkillBrave (pc.uuid, pc.braveSpeed, effects.remainTime).getRaw () ;
				handle.sendPacket (data);
			} else {
				//其他持續性技能
			}
		}) ;
	}
	
	//登入&換地圖時重新載入角色技能效果資訊
	public void loadBuffs () {		
		effects.forEach ((Integer skillId, SkillEffect effect)->{
			removeSkill (skillId);
		});
		effects.clear ();
		
		ResultSet rs = null;
		try {
			rs = DatabaseCmds.loadSkillEffects (pc.uuid);
			while (rs.next ()) {
				int skillId = rs.getInt ("skill_id");
				int remainTime = rs.getInt ("remaining_time");
				int polyGfx = rs.getInt ("poly_id");
				SkillEffect effect = new SkillEffect (skillId, remainTime, polyGfx);
				
				effects.put (skillId, effect);
				
				//重發更新技能封包
				skillPacket (skillId);
			}
		} catch (Exception e) {
			e.printStackTrace ();
			
		} finally {
			DatabaseUtil.close (rs);
		}
	}
	
	public void saveBuffs () {
		//清空全部紀錄
		DatabaseCmds.deleteSkillEffects (pc.uuid);
		
		//塞新的進去
		effects.forEach ((Integer skillId, SkillEffect effect)->{
			DatabaseCmds.insertSkillEffect (pc.uuid, skillId, effect.remainTime, effect.polyGfx);
		});
	}
	
	private void skillPacket (int skillId) {
		switch (skillId) {
		case SkillId.SHIELD:
			pc.skillParameters.ac -= 1;
			handle.sendPacket (new UpdateAc (pc).getRaw ());
			handle.sendPacket (new SkillShield (effects.get (skillId).remainTime, 0).getRaw ());
			break;
		
		case SkillId.STATUS_HASTE:
			handle.sendPacket (new SkillHaste (pc.uuid, 1, effects.get(skillId).remainTime).getRaw());
			break;
			
		case SkillId.STATUS_BRAVE:
			handle.sendPacket (new SkillBrave (pc.uuid, 1, effects.get(skillId).remainTime).getRaw());
			pc.status |= 0x10;
			break;
		
		default:
			break;
		}
	}
	
	public boolean hasSkillEffect (int skillId) {
		return effects.containsKey (skillId);
	}
	
	public void addSkill (int skillId, int remainTime, int polyId) { //skillid, remain time poly
		SkillEffect effect = new SkillEffect (skillId, remainTime, polyId);
		effects.put (skillId, effect);
		skillPacket (skillId);
	}
	
	public void removeSkill (int skillId) {
		switch (skillId) {
		case SkillId.SHIELD: //保護罩
			pc.skillParameters.ac += 1;
			handle.sendPacket (new UpdateAc (pc).getRaw ());
			handle.sendPacket (new SkillShield (0, 0).getRaw ());
			break;
		
		case SkillId.STATUS_HASTE: //加速
			pc.moveSpeed = 0;
			handle.sendPacket (new SkillHaste (pc.uuid, 0, 0).getRaw ());
			break;
			
		case SkillId.STATUS_BRAVE: //勇敢藥水
			pc.braveSpeed = 0;
			handle.sendPacket (new SkillBrave (pc.uuid, 0, 0).getRaw ());
			pc.status &= 0xEF;
			break;
		
		default:
			break;
		}
	}
	
	public ConcurrentHashMap<Integer, SkillEffect> getEffects () {
		return effects;
	}
	
	public void start () {
		timer.scheduleAtFixedRate (this, 0, 1000); //1S interval
	}
	
	public void stop () {
		timer.cancel ();
	}
}
