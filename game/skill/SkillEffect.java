package vidar.game.skill;

public class SkillEffect
{
	public int skillId;
	public int remainTime; /* Sesond */
	public int polyGfx = 0;
	
	public SkillEffect (int _skillId, int _remainTime) {
		skillId = _skillId;
		remainTime = _remainTime;
	}
	
	public SkillEffect (int _skillId, int _remainTime, int _polyGfx) {
		skillId = _skillId;
		remainTime = _remainTime;
		polyGfx = _polyGfx;
	}
}
