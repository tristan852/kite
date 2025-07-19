<a href="/" >
    <img src="assets/images/brand/banner.png" alt="Kite banner" />
</a>

# Kite

![GitHub License](https://img.shields.io/github/license/tristan852/kite)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/tristan852/kite)

Kite is a Connect Four solver that can solve any Connect Four position on any hardware in a reasonable amount of time.
This library may be used as part of a UI application or a Connect Four bot.

## How to set up

This library can easily be imported into any **Gradle** or **Maven** project using the Maven Central repository.

### Gradle (Kotlin DSL)

Add the following code snippet to your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.tristan852:kite:1.0.3")
}
```

### Gradle (Groovy DSL)

Add the following code snippet to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.tristan852:kite:1.0.3'
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
        <version>1.0.3</version>
    </dependency>
</dependencies>
```

## How to use

The Kite solver can be used by obtaining a reference to the singleton solver instance.
Note that the solver cannot be used by multiple threads in parallel.
The first time a reference to the Kite solver is obtained the solver is created and initialized first which may take a bit of time.

The following code snippet demonstrates how the Kite solver should ideally be used:

```java
// obtain access to the Kite solver
Kite kite = Kite.instance();

// if we haven't used the solver before
// then the board is still empty and it
// is red's turn

// red plays in the 4th column
// and yellow plays in the 6th column
kite.playMoves(4, 6);

// it is now red's turn, and they are going
// to win with their second to last stone
System.out.println(kite.evaluateBoard()); // = 2

// red plays in the 5th column
kite.playMove(5);

// it is now yellow's turn

// if yellow plays in the 6th column they
// are going to win with their last stone
System.out.println(kite.evaluateMove(6)); // = 1

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
System.out.println(kite.boardString());

// clear the board (i.e. go back to
// the starting game state)
kite.clearBoard();
```
