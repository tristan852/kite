package net.kite.skill.level;

/**
 * Represents the skill level of
 * a Connect Four player.
 * Skill levels may range from level {@link SkillLevel#ONE}
 * (weakest) up to level {@link SkillLevel#TEN} (strongest).
 * Additionally, the special skill levels {@link SkillLevel#RANDOM}
 * and {@link SkillLevel#PERFECT} represent skill levels of players
 * that always play random or optimal moves respectively.
 */
public enum SkillLevel {
	
	/**
	 * The level of a Connect Four player
	 * that always plays random moves.
	 */
	RANDOM(Integer.MAX_VALUE),
	
	/**
	 * Level one (the weakest level above random)
	 */
	ONE(28),
	
	/**
	 * Level two
	 */
	TWO(25),
	
	/**
	 * Level three
	 */
	THREE(22),
	
	/**
	 * Level four
	 */
	FOUR(19),
	
	/**
	 * Level five
	 */
	FIVE(16),
	
	/**
	 * Level six
	 */
	SIX(13),
	
	/**
	 * Level seven
	 */
	SEVEN(10),
	
	/**
	 * Level eight
	 */
	EIGHT(7),
	
	/**
	 * Level nine
	 */
	NINE(4),
	
	/**
	 * Level ten (the strongest level below perfect)
	 */
	TEN(1),
	
	/**
	 * The level of a perfect Connect Four player
	 */
	PERFECT(0);
	
	private final int maximalScoreLose;
	
	SkillLevel(int maximalScoreLose) {
		this.maximalScoreLose = maximalScoreLose;
	}
	
	/**
	 * Returns the maximum amount of score that
	 * a player of this skill level is allowed
	 * to lose for each move.
	 */
	public int getMaximalScoreLose() {
		return maximalScoreLose;
	}
	
}
