package net.kite.skill.level;

/**
 * Represents the skill level of
 * a Connect Four player.
 * Skill levels may range from the {@link SkillLevel#BEGINNER}
 * level (weakest) up to {@link SkillLevel#SUPER_GRANDMASTER} level
 * (strongest).
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
	RANDOM("Random", Integer.MAX_VALUE, 1120),
	
	/**
	 * Level one (the weakest level above random;
	 * can play any legal move but compared to
	 * {@link SkillLevel#RANDOM} the distribution is
	 * not uniform)
	 */
	BEGINNER("Beginner", 222, 1200),
	
	/**
	 * Level two
	 */
	NOVICE("Novice", 69, 1400),
	
	/**
	 * Level three
	 */
	AMATEUR("Amateur", 32, 1600),
	
	/**
	 * Level four
	 */
	INTERMEDIATE("Intermediate", 21, 1800),
	
	/**
	 * Level five
	 */
	SKILLED("Skilled", 13, 2000),
	
	/**
	 * Level six
	 */
	ADVANCED("Advanced", 8, 2200),
	
	/**
	 * Level seven
	 */
	EXPERT("Expert", 5, 2400),
	
	/**
	 * Level eight
	 */
	MASTER("Master", 3, 2600),
	
	/**
	 * Level nine
	 */
	GRANDMASTER("Grandmaster", 2, 2800),
	
	/**
	 * Level ten (the strongest level; equivalent to {@link SkillLevel#PERFECT})
	 */
	SUPER_GRANDMASTER("Super Grandmaster", 0, 3000),
	
	/**
	 * The level of a perfect Connect Four player
	 */
	PERFECT("Perfect", 0, 3000),
	
	/**
	 * This skill level always tries to match
	 * its own playing strength to that of its
	 * opponent by attempting to equalize the
	 * position.
	 */
	ADAPTIVE("Adaptive", Integer.MAX_VALUE, -1);
	
	private static final SkillLevel[] ORDERED_LEVELS = new SkillLevel[] {
			BEGINNER, NOVICE, AMATEUR, INTERMEDIATE, SKILLED, ADVANCED, EXPERT, MASTER, GRANDMASTER, SUPER_GRANDMASTER
	};
	
	private final String displayName;
	
	private final int maximalScoreLoss;
	
	private final int approximateEloRating;
	
	SkillLevel(String displayName, int maximalScoreLoss, int approximateEloRating) {
		this.displayName = displayName;
		this.maximalScoreLoss = maximalScoreLoss;
		this.approximateEloRating = approximateEloRating;
	}
	
	/**
	 * Returns the name of this skill
	 * level that should for example
	 * be displayed inside a user
	 * interface.
	 *
	 * @return display name of this skill level
	 */
	public String getDisplayName() {
		return displayName;
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
	 * Returns the approximate ELO rating
	 * of players of this skill level.
	 * For the adaptive skill level
	 * {@code -1} is returned instead.
	 * <p>
	 * Visit <a href="https://github.com/tristan852/kite?tab=readme-ov-file#%EF%B8%8F-skill-levels">the README file</a>
	 * for further details.
	 *
	 * @return approximate ELO rating
	 */
	public int getApproximateEloRating() {
		return approximateEloRating;
	}
	
	/**
	 * Returns a skill level from {@link SkillLevel#BEGINNER}
	 * to {@link SkillLevel#SUPER_GRANDMASTER} given the requested
	 * level.
	 * {@code level(1)} returns {@link SkillLevel#BEGINNER},
	 * {@code level(2)} returns {@link SkillLevel#NOVICE}
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
