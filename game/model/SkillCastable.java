package vidar.game.model;

public interface SkillCastable
{
	public void useSkill (int targetUuid, int actionId, int skillGfx, int targetX, int targetY);
	public void useAttackSkill (int targetUuid, int actionId, int skillGfx, int tx, int ty, boolean isHit);
}
