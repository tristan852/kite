plugins {
    application
    signing
    
    `java-library`
    `maven-publish`
}

group = "net.kite"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.javadoc {
    exclude("net/kite/board/**")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            
            pom {
                name.set("Kite")
                description.set("A connect four solver")
                url.set("https://github.com/tristan852/kite")
                
                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/license/gpl-3-0")
                    }
                }
                
                developers {
                    developer {
                        id.set("tristan852")
                        name.set("Tristan")
                        email.set("tristan84502@gmail.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/tristan852/kite.git")
                    developerConnection.set("scm:git:ssh://github.com:tristan852/kite.git")
                    url.set("https://github.com/tristan852/kite")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("ossrhUsername") as String
                password = findProperty("ossrhPassword") as String
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}
