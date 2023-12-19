plugins {
    kotlin("jvm") version "1.9.21"
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
    implementation("io.arrow-kt:arrow-core:1.2.0")
}