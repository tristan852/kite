<span align="center">
    <br>
    
[![The Kite logo](assets/images/brand/small_logo.png)](assets/images/brand/large_logo.png)
    
# Kite
    
![Java version](https://img.shields.io/badge/Java-17+-blue?style=for-the-badge)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/tristan852/kite?style=for-the-badge)
![GitHub license](https://img.shields.io/github/license/tristan852/kite?style=for-the-badge)
[![Java documentation](https://javadoc.io/badge2/io.github.tristan852/kite/javadoc.svg?style=for-the-badge)](https://javadoc.io/doc/io.github.tristan852/kite)
    
</span>

Kite is a lightweight, high-performance Connect Four solver capable of solving any board position blazingly fast — even on modest hardware. It can be used to power AI bots with adjustable playing strength — from deliberately weak to perfectly optimal, making only provably best moves. Kite is well-suited for integration into GUI applications, backend systems, or for programmatic position analysis.

Internally, Kite leverages **alpha-beta pruning**, **symmetry reduction**, **bitboards**, **position hashing** and **opening book lookups** to provide fast and accurate game tree evaluation.

---

## 🚀 Features

* **Alpha-Beta Pruning**: Reduces search space by skipping suboptimal branches early.
* **Symmetry Pruning**: Mirrored game states are considered equivalent and cached accordingly.
* **Move Ordering**: Uses heuristics that favor center columns and winning threats.
* **Bitboard Representation**: Game states use 64-bit integers for fast updates and operations.
* **Transposition Caching**: Hashes each position and stores scores in an efficient score cache.
* **Opening Book**: Stores lots of precomputed scores for early-game positions.
* **Claim-even**: Applies the claim-even strategy to solve certain special positions in *O(1)* time.
* *and much more...*

---

## 🕹️ Online Demo

Try the solver directly in your browser:

👉 **[Launch the Demo](https://tristan852.github.io/kite)**

You can set up and analyze positions, or play against AI opponents of varying strength — no installation required.

The demo runs natively in *WebAssembly* and is generally slower than the Java library, though still fast enough for typical use.

---

## 📊 Benchmark

The empty Connect Four board is considered the most challenging position to solve, as it represents the root of the entire game tree. Successfully evaluating this state is a significant achievement and serves as an excellent benchmark for testing the performance of a Connect Four solver.

Typically, an **opening book** is used to store precomputed evaluations of early-game positions — including the empty board — allowing such evaluations to be retrieved instantly via a simple table lookup. However, to properly assess the solver’s raw computational strength, the opening book was **turned off**, and the **transposition table was cleared** before evaluating the empty board.

Two hardware configurations were used to run this benchmark, representing different levels of processing power:

* **Setup 1**: Modern laptop with an *Intel i7-1165G7* processor (1.8 Mnodes/s per thread)
* **Setup 2**: Modern desktop PC with an *Intel i9-11900KF* processor (6.9 Mnodes/s per thread)

The benchmark results are as follows:

| Kite version | Node evaluations | Compute time (Setup 1)     | Compute time (Setup 2) |
|--------------|------------------|----------------------------|------------------------|
| 1.8.4        | `233,863,140`    | *2 minutes and 9 seconds*  | *34 seconds*           |
| 1.8.3        | `233,863,140`    | *2 minutes and 9 seconds*  | *34 seconds*           |
| 1.8.2        | `233,863,140`    | *2 minutes and 9 seconds*  | *34 seconds*           |
| 1.8.1        | `233,863,140`    | *2 minutes and 9 seconds*  | *34 seconds*           |
| 1.8.0        | `264,328,020`    | *2 minutes and 26 seconds* | *39 seconds*           |
| 1.7.10       | `282,023,140`    | *2 minutes and 35 seconds* | *41 seconds*           |
| 1.7.9        | `282,023,140`    | *2 minutes and 35 seconds* | *41 seconds*           |
| 1.7.8        | `298,565,585`    | *2 minutes and 45 seconds* | *44 seconds*           |
| 1.7.7        | `312,998,949`    | *2 minutes and 53 seconds* | *47 seconds*           |

**Note:** "Node evaluations" refers to the number of times the *negamax* function was invoked to evaluate different game states.

Some internal constants — such as the transposition table size and the minimum depth threshold for enhanced transposition table lookups — were tuned specifically for the task of evaluating the empty board. These settings differ from those optimized for use with an opening book.

Also note that Kite is a lightweight Java solver library designed to support running multiple solvers in parallel. However, each individual solver evaluates boards using a single thread only. As a result, compute times reflect single-threaded performance per solver.

---

## 📦 Installation

Kite is available via **Maven Central** and can be easily added to any **Gradle** or **Maven** project.

### Gradle (Kotlin DSL)

Add the following code snippet to your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.tristan852:kite:1.8.4")
}
```

### Gradle (Groovy DSL)

Add the following code snippet to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.tristan852:kite:1.8.4'
}
```

### Maven

Add the following code snippet to your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.tristan852</groupId>
        <artifactId>kite</artifactId>
        <version>1.8.4</version>
    </dependency>
</dependencies>
```

---

## 🚀 Getting Started

The Kite solver can be used by obtaining a newly created solver instance.
Note that each solver instance cannot be used by multiple threads in parallel.
If your project involves only a single game (even with multiple bots), a single solver instance is sufficient. However, if you're running multiple games in parallel, each game will need its own solver instance to avoid delays caused by mutual exclusion. In that case, the best approach is to recycle solver instances when possible and create new ones as needed. A single Connect Four game should use only one solver instance, as each maintains its own transposition table. Additionally, a solver instance should not alternate between different games, as this can pollute the table with irrelevant entries and negatively impact performance.

The first time a Kite solver instance is obtained, a warm-up and additional initialization is done, which may take a bit of time.

The following code snippet demonstrates how the Kite solver should ideally be used:

```java
// obtain access to a new Kite solver instance
Kite solver = Kite.createInstance();

// Newly created solver instances will
// have the empty board state set up.
// Playing new moves will therefore add
// them in sequence to the empty board.

// red plays in the 4th column
// and yellow plays in the 6th column
solver.playMoves(4, 6);

// it is now red's turn, and they are going
// to win with their second to last stone
System.out.println(solver.evaluateBoard()); // = 2

// red plays in the 5th column
solver.playMove(5);

// it is now yellow's turn

// if yellow plays in the 6th column they
// are going to win with their last stone
System.out.println(solver.evaluateMove(6)); // = 1

// print a string representation
// of the current game state

// .......
// .......
// .......
// .......
// .......
// ...XXO.
// 
// moves: 465
// move scores: -3, 0, -2, +2, 0, +1, -2
// outcome: UNDECIDED
System.out.println(solver.boardString());

// clear the board (i.e. go back to
// the starting game state)
solver.clearBoard();
```

Please keep in mind that Java classes are being loaded lazily.

```java
// a class that is not used during program startup
public class A {
	
	private static final Kite SOLVER = Kite.createInstance();
	
}
```

In the above setup, if class `A` is not loaded at program startup, but rather at some later point, the solver instance creation and initialization will also not happen at startup, but rather when you first use class `A`, which might introduce an unwanted delay before your first use of the solver instance.

In the following class, the method `onProgramStartup` is assumed to be called when your program is booting up. The method obtains a reference to a new solver instance, which ensures that the solver is already initialized and ready to go after your program has started.

```java
public class B {
	
	private static Kite solver;
	
	// a method that is called during program startup
	public void onProgramStartup() {
		solver = Kite.createInstance();
	}
	
}
```

---

## 🧪 Try It Out
Want to quickly try out and experiment with the Kite solver? Here's a simple demo class that pits you against the solver using a fixed skill level:

```java
import net.kite.board.outcome.BoardOutcome;
import net.kite.skill.level.SkillLevel;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	
	// opponent of advanced skill level
	private static final SkillLevel OPPONENT_SKILL_LEVEL = SkillLevel.ADVANCED;
	
	public static void main(String[] programArguments) {
		// initialize a new solver instance and a scanner
		Kite solver = Kite.createInstance();
		Scanner scanner = new Scanner(System.in);
		
		// randomly choose who goes first
		Random random = ThreadLocalRandom.current();
		if(random.nextBoolean()) solver.playMove(solver.skilledMove(OPPONENT_SKILL_LEVEL));
		
		System.out.println(solver.boardString());
		
		while(true) {
			
			System.out.println("Enter your move");
			int x = scanner.nextInt();
			
			if(!solver.moveLegal(x)) {
				
				System.err.println("Illegal move!");
				return;
			}
			
			solver.playMove(x);
			
			if(solver.gameOver()) {
				
				System.out.println(solver.boardString());
				System.out.println(solver.gameOutcome() == BoardOutcome.DRAW ? "You drew." : "You won!");
				return;
			}
			
			solver.playMove(solver.skilledMove(OPPONENT_SKILL_LEVEL));
			System.out.println(solver.boardString());
			
			if(solver.gameOver()) {
				
				System.out.println(solver.gameOutcome() == BoardOutcome.DRAW ? "You drew." : "You lost!");
				return;
			}
		}
	}
	
}
```

---

## 🧠 Evaluation Scale

Kite uses the following score metric to represent the value of a board or a move under perfect play:

| Evaluation score | Interpretation                                                                                                                                                |
|------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `0`              | The position is a guaranteed draw if both players play perfectly.                                                                                             |
| `n > 0`          | The current player will win, assuming perfect play, by placing their `n`th-to-last stone — the fastest possible win against perfect defense in this position. |
| `n < 0`          | The opponent will win, assuming perfect play, by placing their `-n`th-to-last stone — the fastest possible win against perfect defense in this position.      |

**Examples:**

* A score of `1` means the player to move can win, but only with their final stone.
* A score of `-2` means the opponent will win, and they will still have one stone remaining after the win.

---

## ⚔️ Skill levels

The Kite solver is capable of not only playing perfectly but also generating moves at fixed skill levels.
Its API supports thirteen distinct skill levels, ranging from `SkillLevel.RANDOM` up to `SkillLevel.PERFECT`, including intermediate levels like `SkillLevel.BEGINNER`, `SkillLevel.NOVICE`, ..., and `SkillLevel.SUPER_GRANDMASTER`.

These skill levels are ordered by increasing playing strength, with each level designed to be stronger than the previous one.
A special skill level, `SkillLevel.ADAPTIVE`, adjusts move selection to match the opponent's playing strength.
You can use all available skill levels or choose a subset that fits your project.

The `SkillLevel.SUPER_GRANDMASTER` skill level always plays perfectly and is therefore equivalent to `SkillLevel.PERFECT`.
`SkillLevel.BEGINNER` plays slightly better than the random bot.
The Elo ratings of all the different skill levels are given in the table below:

| Skill level       | Elo rating estimate |
|-------------------|---------------------|
| Random            | 1120                |
| Beginner          | 1200                |
| Novice            | 1400                |
| Amateur           | 1600                |
| Intermediate      | 1800                |
| Skilled           | 2000                |
| Advanced          | 2200                |
| Expert            | 2400                |
| Master            | 2600                |
| Grandmaster       | 2800                |
| Super Grandmaster | 3000                |
| Perfect           | 3000                |

An Elo rating difference of approximately *400* corresponds to a *91%* win rate for the higher-rated player. A difference in Elo of *200* corresponds to a *76%* win probability.

For reference, the Elo ratings have been normalized so that the `SkillLevel.PERFECT` bot has a rating of *3000*.
Since `SkillLevel.SUPER_GRANDMASTER` and `SkillLevel.PERFECT` represent the same level of play, they share the same rating estimate.

If you want to translate these Elo ratings to your own scale — or vice versa — try to identify a reference point by comparing one of these skill levels to a skill level in your system with a known Elo rating.

---

## 🔗 References

The following resources were instrumental in shaping the design and implementation of this solver:

1. **[Pascal Pons' Connect Four solver](http://blog.gamesolver.org/)** – A detailed breakdown of the core architecture behind an efficient alpha-beta solver.
2. **[Chris Steininger's Connect Four solver](https://github.com/ChristopheSteininger/c4)** – Offers additional optimizations, tips, and implementation insights.

---

## ⚖️ License

Kite is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html).

You are free to use, modify, and distribute this software under the terms of the GPL-3.0. However, if you distribute a modified version or derivative work, it must also be licensed under the GPL-3.0 and include the source code.

For full terms, see the [LICENSE](./LICENSE) file.
