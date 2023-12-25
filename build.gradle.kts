plugins {
    kotlin("jvm") version "1.9.21"
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

dependencies {
    implementation("io.github.copper-leaf:kudzu-core:5.1.0")
    implementation("me.alllex.parsus:parsus-jvm:0.6.1")
    implementation("io.arrow-kt:arrow-optics:1.2.0")
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:1.2.0")
    implementation("io.arrow-kt:arrow-core:1.2.0")
    implementation("org.apache.commons:commons-math4-legacy:4.0-beta1")
    implementation("com.google.guava:guava:33.0.0-jre")
}