plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
    
    signing
    `maven-publish`
}

group = "io.github.tristan852"
version = "1.4.5"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.javadoc {
    exclude("net/kite/board/Board.java")
    exclude("net/kite/board/bit/**")
    exclude("net/kite/board/history/**")
    exclude("net/kite/board/score/**")
}

tasks.test {
    useJUnitPlatform()
}

signing {
    useGpgCmd()
}
