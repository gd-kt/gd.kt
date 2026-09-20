plugins {
    kotlin("jvm") version "2.4.0"
    id("org.jetbrains.dokka") version "2.2.0"
    `maven-publish`
}

group = "fr.geming400.gddotkt"
version = "1.0.2"
val samplesDir = "src/samples/kotlin"

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation("com.squareup.okhttp3:okhttp:5.4.0")
    implementation("commons-codec:commons-codec:1.22.1")
    implementation("org.apache.commons:commons-lang3:3.20.0")
}

kotlin {
    jvmToolchain(22)

    sourceSets {
        kotlin.sourceSets["main"].kotlin {
            srcDir(samplesDir)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name = "gd.kt"
                description = "A lightweight and simple library centered about geometry dash"

                developers {
                    developer {
                        id = "geming400"
                        name = "Geming400"
                    }
                }
            }

            groupId = group as String
            artifactId = "gddotkt"
            version = version

            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://github.com/gd-kt/gd.kt")
            name = "gd.kt"
        }
    }
}

dokka {
    dokkaSourceSets.main {
        samples.from(samplesDir)
    }
}

tasks.test {
    useJUnitPlatform()
}