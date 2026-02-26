package net.kite.api.skill.level;

import java.util.Locale;

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
	 * <p>
	 * Elo rating: {@code 0}
	 */
	RANDOM(
			"Random",
			0,
			36,
			0,
			0,
			0
	),
	
	/**
	 * Level one (the weakest level above random;
	 * can play any legal move but compared to
	 * {@link SkillLevel#RANDOM} the distribution is
	 * not uniform)
	 * <p>
	 * Elo rating: {@code 600}
	 */
	BEGINNER(
			"Beginner",
			600,
			58,
			6,
			70,
			95
	),
	
	/**
	 * Level two
	 * <p>
	 * Elo rating: {@code 750}
	 */
	NOVICE(
			"Novice",
			750,
			39,
			6,
			70,
			95
	),
	
	/**
	 * Level three
	 * <p>
	 * Elo rating: {@code 900}
	 */
	AMATEUR(
			"Amateur",
			900,
			30,
			6,
			70,
			95
	),
	
	/**
	 * Level four
	 * <p>
	 * Elo rating: {@code 1050}
	 */
	INTERMEDIATE(
			"Intermediate",
			1050,
			21,
			6,
			80,
			96
	),
	
	/**
	 * Level five
	 * <p>
	 * Elo rating: {@code 1200}
	 */
	SKILLED(
			"Skilled",
			1200,
			15,
			7,
			80,
			96
	),
	
	/**
	 * Level six
	 * <p>
	 * Elo rating: {@code 1350}
	 */
	ADVANCED(
			"Advanced",
			1350,
			11,
			7,
			80,
			96
	),
	
	/**
	 * Level seven
	 * <p>
	 * Elo rating: {@code 1500}
	 */
	EXPERT(
			"Expert",
			1500,
			7,
			7,
			95,
			99
	),
	
	/**
	 * Level eight
	 * <p>
	 * Elo rating: {@code 1650}
	 */
	MASTER(
			"Master",
			1650,
			5,
			8,
			95,
			99
	),
	
	/**
	 * Level nine
	 * <p>
	 * Elo rating: {@code 1800}
	 */
	GRANDMASTER(
			"Grandmaster",
			1800,
			3,
			8,
			95,
			99
	),
	
	/**
	 * Level ten (the strongest level below perfect)
	 * <p>
	 * Elo rating: {@code 1950}
	 */
	SUPER_GRANDMASTER(
			"Super Grandmaster",
			1950,
			2,
			9,
			95,
			99
	),
	
	/**
	 * The level of a perfect Connect Four player
	 * <p>
	 * Elo rating: {@code 2000}
	 */
	PERFECT(
			"Perfect",
			2000,
			0,
			42,
			100,
			100
	),
	
	/**
	 * This skill level always tries to match
	 * its own playing strength to that of its
	 * opponent by attempting to equalize the
	 * position.
	 */
	ADAPTIVE(
			"Adaptive",
			-1,
			36,
			42,
			-1,
			-1
	);
	
	private static final SkillLevel[] ORDERED_LEVELS = new SkillLevel[] {
			BEGINNER, NOVICE, AMATEUR, INTERMEDIATE, SKILLED, ADVANCED, EXPERT, MASTER, GRANDMASTER, SUPER_GRANDMASTER
	};
	
	private final String name;
	private final String displayName;
	
	private final int approximateEloRating;
	
	private final int maximalEvaluationLoss;
	private final int openingKnowledgeDepth;
	
	private final int immediateWinNoticeProbability;
	private final int immediateLossNoticeProbability;
	
	SkillLevel(String displayName, int approximateEloRating, int maximalEvaluationLoss, int openingKnowledgeDepth, int immediateWinNoticeProbability, int immediateLossNoticeProbability) {
		this.name = name().toLowerCase(Locale.ROOT);
		this.displayName = displayName;
		this.approximateEloRating = approximateEloRating;
		this.maximalEvaluationLoss = maximalEvaluationLoss;
		this.openingKnowledgeDepth = openingKnowledgeDepth;
		this.immediateWinNoticeProbability = immediateWinNoticeProbability;
		this.immediateLossNoticeProbability = immediateLossNoticeProbability;
	}
	
	/**
	 * Returns the name of this skill
	 * level that should for example
	 * be displayed inside a CLI.
	 *
	 * @return name of this skill level
	 */
	public String getName() {
		return name;
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
	 * Returns the maximum amount of evaluation
	 * score that a player of this skill level
	 * is allowed to lose for each move.
	 *
	 * @return maximal amount of evaluation score to lose
	 */
	public int getMaximalEvaluationLoss() {
		return maximalEvaluationLoss;
	}
	
	/**
	 * Returns the maximal depth to which this
	 * skill level will play much more strongly
	 * due to simulated opening knowledge.
	 *
	 * @return maximal depth where opening knowledge applies
	 */
	public int getOpeningKnowledgeDepth() {
		return openingKnowledgeDepth;
	}
	
	/**
	 * Returns the probability bias of this skill
	 * level noticing that a certain move would
	 * immediately win the game.
	 * <p>
	 * The probability {@code p} is encoded as {@code 100 * p}.
	 *
	 * @return probability bias of noticing immediately winning moves
	 */
	public int getImmediateWinNoticeProbability() {
		return immediateWinNoticeProbability;
	}
	
	/**
	 * Returns the probability bias of this skill
	 * level noticing that a certain move would
	 * immediately lose the game on the very next move
	 * by the opponent.
	 * <p>
	 * The probability {@code p} is encoded as {@code 100 * p}.
	 *
	 * @return probability bias of noticing immediately losing moves
	 */
	public int getImmediateLossNoticeProbability() {
		return immediateLossNoticeProbability;
	}
	
	/**
	 * Returns a skill level from {@link SkillLevel#RANDOM}
	 * to {@link SkillLevel#ADAPTIVE} given the requested
	 * level name.
	 * {@code level("beginner")} returns {@link SkillLevel#BEGINNER},
	 * {@code level("BEGINNER")} returns {@link SkillLevel#BEGINNER},
	 * {@code level("super_grandmaster")} returns {@link SkillLevel#SUPER_GRANDMASTER},
	 * and so on.
	 *
	 * @param levelName the level name in snake case
	 * @return corresponding skill level
	 */
	public static SkillLevel level(String levelName) {
		levelName = levelName.toUpperCase(Locale.ROOT);
		
		return valueOf(levelName);
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
