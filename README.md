<a href="/" >
    <img src="assets/images/brand/banner.png" alt="Kite banner" />
</a>

# Kite

![GitHub License](https://img.shields.io/github/license/tristan852/kite)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/tristan852/kite)

Kite is a high-performance Connect Four solver capable of solving any board position within practical time bounds, even on modest hardware. It can be used to power AI bots with adjustable playing strength — from deliberately weak to perfectly optimal, making only provably best moves. Kite is suitable for building AI opponents, integrating into GUI applications, or analyzing positions programmatically.

Even **without** an opening book, this solver can solve the empty board — the most difficult game state in Connect Four, as it represents the root of the game tree — in about 15 minutes on a modern laptop (using an *Intel i7-1165G7* processor) and around 4 minutes on a modern desktop PC (using an *Intel i9-11900KF* processor).

Internally, Kite leverages **bitboards**, **alpha-beta pruning**, **position hashing**, **symmetry reduction**, and **opening book lookups** to provide fast and accurate game tree evaluation.

---

## 🚀 Features

* **Bitboard Representation**: Game states use 64-bit integers for fast updates and operations.
* **Symmetry Pruning**: Mirrored game states are considered equivalent and cached accordingly.
* **Alpha-Beta Pruning**: Reduces search space by skipping suboptimal branches early.
* **Move Ordering**: Uses heuristics that favor center columns and winning threats.
* **Transposition Caching**: Hashes each position and stores scores in an efficient score cache.
* **Opening Book**: Stores lots of precomputed scores for early-game positions.
* *and much more...*

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
    implementation("io.github.tristan852:kite:1.0.11")
}
```

### Gradle (Groovy DSL)

Add the following code snippet to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.tristan852:kite:1.0.11'
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
        <version>1.0.11</version>
    </dependency>
</dependencies>
```

---

## 🚀 Getting Started

The Kite solver can be used by obtaining a reference to the singleton solver instance.
Note that the solver cannot be used by multiple threads in parallel.
The first time a reference to the Kite solver is obtained, the solver is created and initialized first, which may take a bit of time.

The following code snippet demonstrates how the Kite solver should ideally be used:

```java
// obtain access to the Kite solver
Kite solver = Kite.instance();

// if we haven't used the solver before
// then the board is still empty, and it
// is red's turn

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
System.out.println(solver.boardString());

// clear the board (i.e. go back to
// the starting game state)
solver.clearBoard();
```

Please keep in mind that Java classes are being loaded lazily.

```java
// a class that is not used during program startup
public class A {
	
	private static final Kite SOLVER = Kite.instance();
	
}
```

In the above setup, if class `A` is not loaded at program startup, but rather at some later point, the solver creation and initialization will also not happen at startup, but rather when you first use class `A`, which might introduce an unwanted delay before your first use of the solver.

In the following class, the method `onProgramStartup` is assumed to be called when your program is booting up. The method obtains a reference to the solver, which ensures that the solver is already initialized after your program has started.

```java
public class B {
	
	private static Kite solver;
	
	// a method that is called during program startup
	public void onProgramStartup() {
		solver = Kite.instance();
	}
	
}
```

---

## 🕹️ Try It Out
Want to quickly try out the Kite solver? Here's a simple demo class that pits you against the solver using a fixed skill level:

```java
import net.kite.board.outcome.BoardOutcome;
import net.kite.skill.level.SkillLevel;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	
	// opponent of skill level six
	private static final SkillLevel OPPONENT_SKILL_LEVEL = SkillLevel.SIX;
	
	public static void main(String[] programArguments) {
		// initialize solver and scanner
		Kite solver = Kite.instance();
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

| Score   | Meaning                                             |
|---------|-----------------------------------------------------|
| `0`     | Draw                                                |
| `n > 0` | We win by playing our `n`th to last stone           |
| `n < 0` | Opponent wins by playing their `-n`th to last stone |

For example, a score of `1` indicates that we are going to win, but only with our very last stone.
A score of `-2` means that our opponent will win, with one stone to spare.

---

## 📄 License

Kite is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html).

You are free to use, modify, and distribute this software under the terms of the GPL-3.0. However, if you distribute a modified version or derivative work, it must also be licensed under the GPL-3.0 and include the source code.

For full terms, see the [LICENSE](./LICENSE) file.
