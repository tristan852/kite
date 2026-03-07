import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("org.graalvm.buildtools.native") version "0.11.4"
    
    id("war")
    id("org.teavm") version "0.13.1"
    
    signing
    `maven-publish`
}

group = "io.github.tristan852"
version = "1.19.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.fusesource.jansi:jansi:2.4.2")
    
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
    into("build/war-unpacked/assets")
}

tasks.processResources {
    doLast {
        val gitCommit = providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.getOrElse("unknown")
        
        val file = destinationDir.resolve("build.properties")
        if(file.exists()) {
            
            val text = file.readText().replace("\${gitCommit}", gitCommit)
            file.writeText(text)
        }
    }
}

val isSignedPublishEnabled: Boolean = project.hasProperty("enableSignedPublish")

tasks.withType<Sign>().configureEach {
    onlyIf { isSignedPublishEnabled }
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
        
        optimization = OptimizationLevel.AGGRESSIVE
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
            
            if(project.hasProperty("marchNative") && project.property("marchNative") == "true") {
                buildArgs.add("-march=native")
                
	            println("[native-image-plugin] Using -march=native: binary will be CPU-specific")
            }
            
            buildArgs.add("-O3")
            buildArgs.add("--future-defaults=all")
            
            buildArgs.add("--no-fallback")
            buildArgs.add("--no-server")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
    }
}
