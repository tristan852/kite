plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("org.graalvm.buildtools.native") version "0.11.4"
    
    id("war")
    id("org.teavm") version "0.13.0"
    
    signing
    `maven-publish`
}

group = "io.github.tristan852"
version = "1.15.0"

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
    exclude("net/kite/internal/**")
}

tasks.test {
    useJUnitPlatform()
    
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
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

tasks.processResources {
    from("src/main/resources") {
        include("**/*")
    }
    
    into(layout.buildDirectory.dir("resources").get())
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

signing {
    useGpgCmd()
}

teavm {
    all {
        mainClass = "net.kite.internal.demo.Main"
    }
    
    wasmGC {
        addedToWebApp = true
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("kite")
            mainClass.set("net.kite.internal.cli.Main")
            
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(25))
            })
            
            buildArgs.add("-H:IncludeResources=.*")
            
            buildArgs.add("--no-fallback")
            buildArgs.add("--no-server")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
    }
}
