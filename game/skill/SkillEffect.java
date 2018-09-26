package vidar.game.skill;

public class SkillEffect
{
	public int SkillId;
	public int RemainTime; /* Sesond */
	public int polyGfx = 0;
	
	public SkillEffect (int _skillId, int _remainTime) {
		SkillId = _skillId;
		RemainTime = _remainTime;
	}
	
	public SkillEffect (int _skillId, int _remainTime, int _polyGfx) {
		SkillId = _skillId;
		RemainTime = _remainTime;
		polyGfx = _polyGfx;
	}
}
