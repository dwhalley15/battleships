plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":lib"))
    testImplementation(project(":testlib"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlin_version"]}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${rootProject.extra["junit_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${rootProject.extra["junit_version"]}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${rootProject.extra["junit_version"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kotlin {
    target {
        compilations.configureEach {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}
