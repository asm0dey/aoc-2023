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
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
}