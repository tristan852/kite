package net.kite.skill.level;

/**
 * Represents the skill level of
 * a Connect Four player.
 * Skill levels may range from level {@link SkillLevel#ONE}
 * (weakest) up to level {@link SkillLevel#TEN} (strongest).
 * <p>
 * Additionally, there exists three special
 * skill levels: {@link SkillLevel#RANDOM}, {@link SkillLevel#PERFECT}
 * and {@link SkillLevel#ADAPTIVE}.
 * {@link SkillLevel#RANDOM} and {@link SkillLevel#PERFECT} represent
 * skill levels of players that always play
 * random or optimal moves respectively.
 * {@link SkillLevel#ADAPTIVE} on the other hand
 * tries to match its own skill to that
 * of its opponent.
 */
public enum SkillLevel {
	
	/**
	 * The level of a Connect Four player
	 * that always plays uniformly sampled
	 * random moves.
	 */
	RANDOM(Integer.MAX_VALUE),
	
	/**
	 * Level one (the weakest level above random;
	 * can play any legal move but compared to
	 * {@link SkillLevel#RANDOM} the distribution is
	 * not uniform)
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
	 * Level ten (the strongest level; equivalent to {@link SkillLevel#PERFECT})
	 */
	TEN(0),
	
	/**
	 * The level of a perfect Connect Four player
	 */
	PERFECT(0),
	
	/**
	 * This skill level always tries to match
	 * its own playing strength to that of its
	 * opponent by attempting to equalize the
	 * position.
	 */
	ADAPTIVE(Integer.MAX_VALUE);
	
	private static final SkillLevel[] ORDERED_LEVELS = new SkillLevel[] {
			ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN
	};
	
	private final int maximalScoreLoss;
	
	SkillLevel(int maximalScoreLoss) {
		this.maximalScoreLoss = maximalScoreLoss;
	}
	
	/**
	 * Returns the maximum amount of score that
	 * a player of this skill level is allowed
	 * to lose for each move.
	 *
	 * @return maximal amount of score to lose
	 */
	public int getMaximalScoreLoss() {
		return maximalScoreLoss;
	}
	
	/**
	 * Returns a skill level from {@link SkillLevel#ONE}
	 * to {@link SkillLevel#TEN} given the requested
	 * level.
	 * {@code level(1)} returns {@link SkillLevel#ONE},
	 * {@code level(2)} returns {@link SkillLevel#TWO}
	 * and so on.
	 *
	 * @param level an integer from {@code 1} to {@code 10}
	 * @return corresponding skill level
	 */
	public static SkillLevel level(int level) {
		level--;
		
		return ORDERED_LEVELS[level];
	}
	
}
