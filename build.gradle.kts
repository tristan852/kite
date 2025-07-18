plugins {
    id("java")
    application
}

group = "net.kite"
version = "1.0.0"

application {
    mainClass.set("net.kite.Main")
}

repositories {
    mavenCentral()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}
