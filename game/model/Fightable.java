package vidar.game.model;

public interface Fightable
{
	public void attack ();
	public void damage ();
	
	public void useSkill ();
	public void useAttackSkill ();
	public void addSkillEffect ();
	public void removeSkillEffect ();
	public boolean hasSkillEffect ();
}
