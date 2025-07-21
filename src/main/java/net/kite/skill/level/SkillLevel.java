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
	ONE(36),
	
	/**
	 * Level two
	 */
	TWO(32),
	
	/**
	 * Level three
	 */
	THREE(28),
	
	/**
	 * Level four
	 */
	FOUR(24),
	
	/**
	 * Level five
	 */
	FIVE(20),
	
	/**
	 * Level six
	 */
	SIX(16),
	
	/**
	 * Level seven
	 */
	SEVEN(12),
	
	/**
	 * Level eight
	 */
	EIGHT(8),
	
	/**
	 * Level nine
	 */
	NINE(4),
	
	/**
	 * Level ten (the strongest level; equivalent to perfect)
	 */
	TEN(0),
	
	/**
	 * The level of a perfect Connect Four player
	 */
	PERFECT(0);
	
	private final int maximalScoreLoss;
	
	SkillLevel(int maximalScoreLoss) {
		this.maximalScoreLoss = maximalScoreLoss;
	}
	
	/**
	 * Returns the maximum amount of score that
	 * a player of this skill level is allowed
	 * to lose for each move.
	 */
	public int getMaximalScoreLoss() {
		return maximalScoreLoss;
	}
	
}
