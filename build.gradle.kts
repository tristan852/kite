plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
    
    signing
    `maven-publish`
}

group = "io.github.tristan852"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.javadoc {
    exclude("net/kite/board/**")
}

tasks.test {
    useJUnitPlatform()
}

signing {
    useGpgCmd()
}
