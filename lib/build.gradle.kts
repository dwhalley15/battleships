plugins {
    kotlin("jvm")
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api("io.github.pdvrieze.matrixlib:matrixlib:1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${rootProject.extra["junit_version"]}")
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
