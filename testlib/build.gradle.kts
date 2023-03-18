plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":lib"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlin_version"]}")
    implementation("org.junit.jupiter:junit-jupiter-api:${rootProject.extra["junit_version"]}")
    implementation("org.junit.jupiter:junit-jupiter-params:${rootProject.extra["junit_version"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
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
