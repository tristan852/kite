plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
    
    id("war")
    id("org.teavm") version "0.12.3"
    
    signing
    `maven-publish`
}

group = "io.github.tristan852"
version = "1.5.4"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.javadoc {
    exclude("net/kite/board/Board.java")
    exclude("net/kite/board/bit/**")
    exclude("net/kite/board/history/**")
    exclude("net/kite/board/score/**")
    exclude("net/kite/demo/**")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn(tasks.jar, tasks.javadoc)
}

tasks.named("build") {
    finalizedBy("copyDemoAssetFiles")
}

tasks.register<Copy>("copyDemoAssetFiles") {
    from("assets/demo/")
    into("build/war-unpacked")
}

signing {
    useGpgCmd()
}

teavm {
    all {
        mainClass = "net.kite.Main"
    }
    
    wasmGC {
        addedToWebApp = true
    }
}
